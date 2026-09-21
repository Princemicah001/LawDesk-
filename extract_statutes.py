import os
import re
import zlib
import json
import sqlite3
import unicodedata

def clean_extracted_text(s):
    """Clean PDF string escapes and remove control/unprintable characters."""
    if not s:
        return ""
    # Clean PDF string escapes
    s = s.replace(r'\(', '(').replace(r'\)', ')').replace(r'\\', '\\')
    def replace_octal(m):
        try:
            return chr(int(m.group(1), 8))
        except Exception:
            return m.group(0)
    s = re.sub(r'\\([0-7]{1,3})', replace_octal, s)
    # Remove unicode control characters except newline and tab
    s = ''.join(c for c in s if (unicodedata.category(c)[0] != 'C' or c in '\n\t') and ord(c) < 65534)
    # Remove null bytes and non-printable control characters
    s = re.sub(r'[\x00-\x08\x0b\x0c\x0e-\x1f\x7f-\x9f]', ' ', s)
    s = re.sub(r'[ \t]+', ' ', s)
    return s.strip()

def is_meaningful_text(text):
    """Verify that the string contains legible English/legal text."""
    if not text or len(text) < 10:
        return False
    words = re.findall(r'[A-Za-z]{2,}', text)
    if len(words) < 2:
        return False
    total_letters = sum(len(w) for w in words)
    if total_letters == 0:
        return False
    vowels = 'aeiouAEIOU'
    vowel_count = sum(sum(1 for c in w if c in vowels) for w in words)
    vowel_ratio = vowel_count / total_letters
    return 0.18 <= vowel_ratio <= 0.70

def fast_extract_split(pdf_path):
    """Fast PDF text extraction using stream splitting with text sanitization."""
    try:
        with open(pdf_path, 'rb') as f:
            content = f.read()
    except Exception as e:
        print(f"Error reading {pdf_path}: {e}")
        return ""
    
    lines = []
    pos = 0
    while True:
        s_idx = content.find(b'stream', pos)
        if s_idx == -1:
            break
        nl = content.find(b'\n', s_idx)
        if nl == -1:
            break
        e_idx = content.find(b'endstream', nl)
        if e_idx == -1:
            break
            
        stream_bytes = content[nl+1:e_idx].rstrip(b'\r\n')
        pos = e_idx + 9
        
        try:
            decomp = zlib.decompress(stream_bytes)
        except Exception:
            decomp = stream_bytes

        parts = decomp.split(b'(')
        for p in parts[1:]:
            c_idx = p.find(b')')
            if c_idx > 0:
                snip = p[:c_idx]
                try:
                    raw_str = snip.decode('latin-1', errors='ignore').strip()
                    txt = clean_extracted_text(raw_str)
                    if len(txt) > 1 and is_meaningful_text(txt):
                        lines.append(txt)
                except Exception:
                    pass
    return '\n'.join(lines)

def determine_category(act_name):
    """Map Act name to standardized legal category."""
    name_upper = act_name.upper()
    if any(k in name_upper for k in ["CRIMINAL", "PENAL", "EVIDENCE", "TORTURE", "PRISONS", "VICTIM", "WITNESS", "PROBATION"]):
        return "CRIMINAL"
    elif any(k in name_upper for k in ["INSOLVENCY", "SALE OF GOODS", "PARTNERSHIP", "RESTRAIN", "UNCOLLECTED", "BUSINESS", "CONSUMER", "CAPITAL"]):
        return "COMMERCIAL"
    elif any(k in name_upper for k in ["JUDICIAL", "JUDICATURE", "KADHIS", "LEGAL EDUCATION", "MUTUAL LEGAL"]):
        return "JUDICIAL"
    elif any(k in name_upper for k in ["LAND", "PROPERTY", "ENVIRONMENT"]):
        return "LAND"
    elif any(k in name_upper for k in ["EMPLOYMENT", "LABOUR", "WORK"]):
        return "EMPLOYMENT"
    elif "CONSTITUTION" in name_upper:
        return "CONSTITUTION"
    else:
        return "CIVIL & ADMINISTRATIVE"

def parse_statute_sections(pdf_file, raw_text):
    """Parse statutory sections from extracted text."""
    act_name = os.path.splitext(pdf_file)[0]
    act_name_clean = re.sub(r'\s*\(\d+\)$', '', act_name).strip()
    category = determine_category(act_name_clean)
    
    lines = [l.strip() for l in raw_text.split('\n') if l.strip() and is_meaningful_text(l)]
    sections = []
    
    current_sec_num = None
    current_sec_title = ""
    current_sec_text = []

    sec_pattern1 = re.compile(r'^(?:Section|SECTION|Sec\.|Article|ARTICLE)\s+(\d+[A-Z]?)\.?\s*(.*)', re.IGNORECASE)
    sec_pattern2 = re.compile(r'^(\d+[A-Z]?)\.\s+([A-Z][A-Za-z0-9\s,\-\'\"\(\)]{2,80})$')

    for line in lines:
        m1 = sec_pattern1.match(line)
        m2 = sec_pattern2.match(line)
        
        if m1:
            if current_sec_num and current_sec_text:
                sections.append((current_sec_num, current_sec_title, "\n".join(current_sec_text)))
            current_sec_num = f"Section {m1.group(1)}"
            current_sec_title = clean_extracted_text(m1.group(2).strip()) or f"{act_name_clean} {current_sec_num}"
            current_sec_text = [line]
        elif m2 and not line.startswith("Cap.") and not line.startswith("Rev."):
            if current_sec_num and current_sec_text:
                sections.append((current_sec_num, current_sec_title, "\n".join(current_sec_text)))
            current_sec_num = f"Section {m2.group(1)}"
            current_sec_title = clean_extracted_text(m2.group(2).strip())
            current_sec_text = [line]
        else:
            if current_sec_num:
                current_sec_text.append(line)

    if current_sec_num and current_sec_text:
        sections.append((current_sec_num, current_sec_title, "\n".join(current_sec_text)))

    # Fallback: Slice into chunks if no explicit section headers match
    if not sections and raw_text:
        chunk_size = 1000
        total_len = len(raw_text)
        if total_len > 0:
            count = 1
            for start in range(0, total_len, chunk_size):
                chunk = raw_text[start:start+chunk_size]
                first_line = chunk.strip().split('\n')[0]
                first_line_clean = clean_extracted_text(first_line)
                title = first_line_clean[:60] if len(first_line_clean) > 5 else f"{act_name_clean} Part {count}"
                sections.append((f"Section {count}", title, chunk))
                count += 1

    parsed_entries = []
    act_slug = re.sub(r'[^a-z0-9]', '_', act_name_clean.lower()).strip('_')

    for sec_num, sec_title, sec_body in sections:
        clean_body = clean_extracted_text(sec_body[:3000])
        sec_clean = sec_num.replace("Section ", "").replace("Article ", "").strip()
        entry_id = f"statute_{act_slug}_{sec_clean}"

        if not sec_title or sec_title == sec_num or not is_meaningful_text(sec_title):
            sec_title = f"{act_name_clean} - {sec_num}"

        if not is_meaningful_text(clean_body) or len(clean_body) < 15:
            continue

        ai_interp = f"{sec_num} of the {act_name_clean} defines operative legal rules and statutory standards regarding {sec_title.lower()} in Kenya."
        prac_ex = f"Applied in legal disputes and commercial/civil proceedings under the {act_name_clean} concerning {sec_title.lower()}."
        why_matters = f"Provides key statutory guidance and judicial standards under the {act_name_clean}."
        exam_tip = f"Review the essential statutory conditions and procedural rules of {sec_num} in the {act_name_clean}."

        parsed_entries.append({
            "id": entry_id,
            "category": category,
            "actName": act_name_clean,
            "sectionNumber": sec_num,
            "title": sec_title,
            "originalText": clean_body if clean_body else f"Refer to full text of {sec_num} of {act_name_clean}.",
            "aiInterpretation": ai_interp,
            "practicalExample": prac_ex,
            "whyItMatters": why_matters,
            "examTip": exam_tip,
            "relevantCases": f"Republic v {act_name_clean} Cases",
            "isBookmarked": False,
            "isRead": False
        })

    return parsed_entries

def run_extraction():
    print("=== Starting Fast PDF Statutes Extraction & Indexing ===")
    statutes_dir = "statutes"
    extracted_statutes = []

    if os.path.exists(statutes_dir):
        files = sorted([f for f in os.listdir(statutes_dir) if f.endswith(".pdf")])
        print(f"Processing {len(files)} statute PDFs in {statutes_dir}/...")
        
        for f in files:
            path = os.path.join(statutes_dir, f)
            raw_text = fast_extract_split(path)
            entries = parse_statute_sections(f, raw_text)
            print(f"  [+] {f}: Extracted {len(entries)} section entries ({len(raw_text)} chars)")
            extracted_statutes.extend(entries)

    # Deduplicate entries by ID
    all_map = {}
    for item in extracted_statutes:
        all_map[item["id"]] = item

    final_statutes = list(all_map.values())
    print(f"Total compiled statute entries: {len(final_statutes)}")

    # Save to app/src/main/assets/statutes.json
    output_path = "app/src/main/assets/statutes.json"
    os.makedirs(os.path.dirname(output_path), exist_ok=True)

    with open(output_path, "w", encoding="utf-8") as f:
        json.dump(final_statutes, f, indent=2, ensure_ascii=False)

    print(f"Successfully saved {len(final_statutes)} statute entries to {output_path}")

    # Run populate_db.py to compile SQLite database
    print("Generating SQLite LawApp.db database with full-text search index...")
    import populate_db
    populate_db.run()
    print("=== Complete! LawApp.db and statutes.json successfully updated! ===")

if __name__ == "__main__":
    run_extraction()
