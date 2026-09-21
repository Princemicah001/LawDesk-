import pdfplumber
import re
import json

PDF_FILE = "Constitution-of-Kenya-2010.pdf"
OUTPUT_FILE = "constitution.json"


# -----------------------------------------
# TEXT CLEANUP
# -----------------------------------------

def clean_text(text):
    replacements = {
        "–": "-",
        "—": "-",
        "“": '"',
        "”": '"',
        "‘": "'",
        "’": "'",
        "￾": "",
        "\u00a0": " ",
    }

    for old, new in replacements.items():
        text = text.replace(old, new)

    return text.strip()


# -----------------------------------------
# REGEX PATTERNS
# -----------------------------------------

chapter_re = re.compile(
    r"^CHAPTER\s+([A-Z0-9]+)\s*-\s*(.+)$"
)

part_re = re.compile(
    r"^PART\s+([A-Z0-9]+)\s*-\s*(.+)$"
)

article_re = re.compile(
    r"^(\d+)\.\s+(.+)$"
)

clause_re = re.compile(
    r"^\((\d+)\)\s+(.*)"
)

paragraph_re = re.compile(
    r"^\(([a-z])\)\s+(.*)"
)


# -----------------------------------------
# STORAGE
# -----------------------------------------

constitution = []

current_chapter = None
current_part = None
current_article = None
current_clause = None


# -----------------------------------------
# PDF EXTRACTION
# -----------------------------------------

with pdfplumber.open(PDF_FILE) as pdf:

    for page_num, page in enumerate(pdf.pages):

        # Skip cover pages + table of contents
        if page_num < 13:
            continue

        text = page.extract_text()

        if not text:
            continue

        lines = text.split("\n")

        for raw_line in lines:

            line = clean_text(raw_line)

            if not line:
                continue

            # ---------------------------------
            # REMOVE PAGE HEADERS / FOOTERS
            # ---------------------------------

            if (
                "Constitution of Kenya" in line
                or "Const2010" in line
                or "[Rev." in line
                or "[Issue" in line
                or "C35-" in line
            ):
                continue

            # ---------------------------------
            # CHAPTER
            # ---------------------------------

            match = chapter_re.match(line)

            if match:
                current_chapter = {
                    "number": match.group(1),
                    "title": match.group(2).strip()
                }
                continue

            # ---------------------------------
            # PART
            # ---------------------------------

            match = part_re.match(line)

            if match:
                current_part = {
                    "number": match.group(1),
                    "title": match.group(2).strip()
                }
                continue

            # ---------------------------------
            # ARTICLE
            # ---------------------------------

            match = article_re.match(line)

            if match:

                if current_article:
                    constitution.append(current_article)

                current_article = {
                    "chapter": current_chapter,
                    "part": current_part,
                    "article_number": int(match.group(1)),
                    "title": match.group(2).strip(),
                    "clauses": []
                }

                current_clause = None
                continue

            # ---------------------------------
            # CLAUSE (1)
            # ---------------------------------

            match = clause_re.match(line)

            if match and current_article:

                current_clause = {
                    "number": int(match.group(1)),
                    "text": match.group(2).strip(),
                    "paragraphs": []
                }

                current_article["clauses"].append(
                    current_clause
                )

                continue

            # ---------------------------------
            # PARAGRAPH (a)
            # ---------------------------------

            match = paragraph_re.match(line)

            if match and current_clause:

                current_clause["paragraphs"].append({
                    "letter": match.group(1),
                    "text": match.group(2).strip()
                })

                continue

            # ---------------------------------
            # CONTINUATION TEXT
            # ---------------------------------

            if current_clause:

                if current_clause["paragraphs"]:

                    current_clause["paragraphs"][-1]["text"] += (
                        " " + line
                    )

                else:

                    current_clause["text"] += (
                        " " + line
                    )


# -----------------------------------------
# SAVE LAST ARTICLE
# -----------------------------------------

if current_article:
    constitution.append(current_article)


# -----------------------------------------
# SAVE JSON
# -----------------------------------------

with open(
    OUTPUT_FILE,
    "w",
    encoding="utf-8"
) as f:

    json.dump(
        constitution,
        f,
        ensure_ascii=False,
        indent=2
    )

print(f"Extracted {len(constitution)} articles")
print(f"Saved to {OUTPUT_FILE}")
