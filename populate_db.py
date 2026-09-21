import json
import sqlite3
import os

# Paths
CONSTITUTION_JSON_PATH = "app/src/main/assets/constitution.json"
TARGET_ROOT_DB_PATH = "LawApp.db"
TARGET_ASSET_DB_PATH = "app/src/main/assets/LawApp.db"

def run():
    print("Starting pre-population of LawApp.db...")
    
    # 1. Load constitution.json if available
    constitution_articles = []
    if os.path.exists(CONSTITUTION_JSON_PATH):
        with open(CONSTITUTION_JSON_PATH, "r", encoding="utf-8") as f:
            constitution_articles = json.load(f)
        print(f"Loaded {len(constitution_articles)} raw articles from {CONSTITUTION_JSON_PATH}")
    else:
        print(f"Warning: {CONSTITUTION_JSON_PATH} not found!")

    # 2. Build list of pre-written overriding statutes (from SeedingData)
    prewritten_statutes = [
        {
            "id": "const_27",
            "category": "CONSTITUTION",
            "actName": "Constitution of Kenya 2010",
            "sectionNumber": "Article 27",
            "title": "Equality and Freedom from Discrimination",
            "originalText": "Every person is equal before the law and has the right to equal protection and equal benefit of the law. Equality includes the full and equal enjoyment of all rights and fundamental freedoms. Women and men have the right to equal treatment, including the right to equal opportunities in political, economic, cultural and social spheres.",
            "aiInterpretation": "This article prevents any form of unfair or prejudicial treatment based on race, sex, pregnancy, marital status, health status, ethnic or social origin, color, age, disability, religion, conscience, belief, culture, dress, language or birth.",
            "practicalExample": "If a public or private company rejects your job application solely because of your gender or tribe, Article 27 is directly violated.",
            "whyItMatters": "It establishes the legal foundation for anti-discrimination, gender parity policies, and affirmative action programs across Kenya's public and private sectors.",
            "examTip": "Remember: Article 27 represents SUBSTANTIVE equality (achieving true equity through corrective measures) rather than just FORMAL equality (treating everyone identically regardless of structural barriers).",
            "relevantCases": "CREAW v AG, Katiba Institute v Attorney General",
            "isBookmarked": 0,
            "isRead": 0
        },
        {
            "id": "const_43",
            "category": "CONSTITUTION",
            "actName": "Constitution of Kenya 2010",
            "sectionNumber": "Article 43",
            "title": "Economic and Social Rights",
            "originalText": "Every person has the right to the highest attainable standard of health, which includes the right to health care services, including reproductive health care; to accessible and adequate housing, and to reasonable standards of sanitation; to be free from hunger, and to have adequate food of acceptable quality; to clean and safe water in adequate quantities; to social security; and to education.",
            "aiInterpretation": "Guarantees socio-economic welfare. Citizens can demand that the government progressively fulfill their rights to medical care, clean environment, food, water, housing, and education.",
            "practicalExample": "A public school denying admission to a student due to an inability to pay, or a public hospital turning away an emergency patient, constitutes an Article 43 infringement.",
            "whyItMatters": "Shifts basic human needs from charitable favors to legally enforceable rights that the state is mandated to prioritize under budget resource allocations.",
            "examTip": "A critical argument: In case law (e.g., Mitu-Bell), the Supreme Court clarified that socio-economic rights are justiciable and the government must provide structured realization plans.",
            "relevantCases": "Mitu-Bell Welfare Society v Attorney General, Sabyasachi v County Government",
            "isBookmarked": 0,
            "isRead": 0
        },
        {
            "id": "const_50",
            "category": "CONSTITUTION",
            "actName": "Constitution of Kenya 2010",
            "sectionNumber": "Article 50",
            "title": "Right to a Fair Hearing",
            "originalText": "Every accused person has the right to a fair trial, which includes the right to be presumed innocent until the contrary is proved; to be informed of the charge, with sufficient detail to answer it; to have adequate time and facilities to prepare a defence; and to a public trial before an ordinary court.",
            "aiInterpretation": "Protects citizens from arbitrary or biased prosecution. Ensures judicial trials are open, neutral, and adhere strictly to procedural justice.",
            "practicalExample": "If you are arrested and held in custody beyond 24 hours without being formally charged or brought before a judicial officer, your Article 50 right is compromised.",
            "whyItMatters": "The cornerstone of criminal justice. It maintains the delicate balance between state policing power and individual liberties.",
            "examTip": "Differentiate Article 50(1) general fair hear rights from Article 50(2) specific accused rights. Note that Article 25(c) designates the right to a fair trial as non-derogable.",
            "relevantCases": "Richard Muasya v Republic, Republic v Karisa",
            "isBookmarked": 0,
            "isRead": 0
        },
        {
            "id": "const_40",
            "category": "CONSTITUTION",
            "actName": "Constitution of Kenya 2010",
            "sectionNumber": "Article 40",
            "title": "Protection of Right to Property",
            "originalText": "Subject to Article 65, every person has the right, either individually or in association with others, to acquire and own property of any description and in any part of Kenya. Parliament shall not pass any law that arbitrarily deprives a person of property.",
            "aiInterpretation": "Guarantees private ownership of land, intellectual property, and belongings. Restricts the state from forcefully taking private property without compensating the owner fairly.",
            "practicalExample": "If the government demolishes commercial buildings on private land to clear space for heavy transport roads without prior notice and adequate market-value compensation, Article 40 is infringed.",
            "whyItMatters": "Essential for security of tenure, financial investments, and business growth in Kenya, making land title deeds and patents highly sacred.",
            "examTip": "Focus on parameters of compulsory acquisition: must be for public utility/interest, paid promptly and in full, and is fully contestable in the Environment and Land Court.",
            "relevantCases": "In Matter of the National Land Commission [2015], Kenwood Enterprises v KURA",
            "isBookmarked": 0,
            "isRead": 0
        },
        {
            "id": "const_24",
            "category": "CONSTITUTION",
            "actName": "Constitution of Kenya 2010",
            "sectionNumber": "Article 24",
            "title": "Limitation of Rights",
            "originalText": "A right or fundamental freedom in the Bill of Rights shall not be limited except by law, and then only to the extent that the limitation is reasonable and justifiable in an open and democratic society based on human dignity, equality and freedom.",
            "aiInterpretation": "Specifies that most rights are not absolute. They can be restricted if highly necessary for national security, public health, safety, or protecting others.",
            "practicalExample": "During a pandemic, emergency public health mandates might limit freedom of assembly and movement to curb viral spreads.",
            "whyItMatters": "Maintains state stability by ensuring that one citizen's exercise of liberty doesn't destroy general public order or infringe others' human rights.",
            "examTip": "Three-part test for valid limitations: (1) Prescribed by law; (2) Pursues legitimate public interest; (3) Proportionality (least restrictive means strictly used).",
            "relevantCases": "Coalition for Reform and Democracy (CORD) v AG, Ooko v Inspector General",
            "isBookmarked": 0,
            "isRead": 0
        },
        {
            "id": "const_25",
            "category": "CONSTITUTION",
            "actName": "Constitution of Kenya 2010",
            "sectionNumber": "Article 25",
            "title": "Absolute Rights (Non-Derogable)",
            "originalText": "Notwithstanding any other provision in this Constitution, there shall be no limitation on the following rights and fundamental freedoms: freedom from torture and cruel, inhuman or degrading treatment; freedom from slavery or servitude; the right to a fair trial; and the right to habeas corpus.",
            "aiInterpretation": "Defines absolute rights that CANNOT be taken away under any circumstances—even during emergency states, war, or security crises.",
            "practicalExample": "No amount of security emergency or military conflict can justify the state torturing a suspect or keeping them locked away in secret detention indefinitely (violating habeas corpus).",
            "whyItMatters": "The absolute moral ceiling of our legal system. It prevents the state from slipping into arbitrary tyranny.",
            "examTip": "Commit these four absolute rights to memory: Torture exclusion, Slavery omission, Fair Trial, and Habeas Corpus. A common practitioner trap lists 'Right to Life'—which is NOT in Article 25 (it has exceptions).",
            "relevantCases": "Wycliffe v Attorney General, Kaimenyi v Republic",
            "isBookmarked": 0,
            "isRead": 0
        },
        {
            "id": "penal_36",
            "category": "CRIMINAL",
            "actName": "Penal Code (Cap 63)",
            "sectionNumber": "Section 26",
            "title": "Kinds of Punishments",
            "originalText": "The following punishments may be inflicted by a court: Death; Imprisonment; Corporal punishment (historical); Fine; Forfeiture; Payment of compensation; Finding security to keep the peace.",
            "aiInterpretation": "Lists legal forms of penalties. Some historical punishments (like corporal stroke floggings) are now unconstitutional under human rights protections.",
            "practicalExample": "A magistrate ordering a fraud convict to fully pay back the stolen amount to the victim alongside standard state imprisonment.",
            "whyItMatters": "Defines boundaries of how the state may punish crime, ensuring a legal check on penalties.",
            "examTip": "Note that while the Penal Code still references the death penalty, the landmark Francis Karioko Muruatetu case ruled mandatory death sentences unconstitutional.",
            "relevantCases": "Francis Karioko Muruatetu v Republic",
            "isBookmarked": 0,
            "isRead": 0
        },
        {
            "id": "evidence_3",
            "category": "EVIDENCE",
            "actName": "Evidence Act (Cap 80)",
            "sectionNumber": "Section 3",
            "title": "Admissibility of Evidence",
            "originalText": "Subject to the provisions of this Act, evidence may be given in any suit or proceeding of the existence or non-existence of every fact in issue, and of such other facts as are hereinafter declared to be relevant, and of no others.",
            "aiInterpretation": "Only relevant facts and those declared by the Evidence Act are allowed in any court hearing. No random hearsay or unverified material is permitted.",
            "practicalExample": "An eye-witness account of the actual accident is admissible, but a neighbor's gossip about what they heard third-hand is excluded under the hearsay rule.",
            "whyItMatters": "Guarantees court decisions are grounded on high-quality, verified factual evidence rather than speculation.",
            "examTip": "Distinguish relevance from admissibility. A fact may be highly relevant but excluded if it was obtained unlawfully (Article 50(4) of the Constitution).",
            "relevantCases": "Republic v Philip Kipkoech",
            "isBookmarked": 0,
            "isRead": 0
        },
        {
            "id": "admin_4",
            "category": "ADMINISTRATIVE",
            "actName": "Fair Administrative Action Act 2015",
            "sectionNumber": "Section 4",
            "title": "Right to administrative action",
            "originalText": "Every person has the right to administrative action which is expeditious, efficient, lawful, reasonable and procedurally fair. Every person affected has the right to be given written reasons for any administrative action.",
            "aiInterpretation": "When government officials make regulatory or licensing decisions affecting you, they must follow fair procedures, hear your side, and give you written explanations.",
            "practicalExample": "A county governor arbitrarily revoking a business's trading license without notice or an opportunity to respond is in breach of Section 4.",
            "whyItMatters": "Crucial bulwark against civil servant high-handedness and public service corruption.",
            "examTip": "Essential statutory companion to Article 47 of the Constitution. Enables judicial review applications in High Court for ultra vires actions.",
            "relevantCases": "Republic v Cabinet Secretary for Transport ex-parte Fly540",
            "isBookmarked": 0,
            "isRead": 0
        },
        {
            "id": "employ_35",
            "category": "EMPLOYMENT",
            "actName": "Employment Act 2007",
            "sectionNumber": "Section 35",
            "title": "Termination of Employment",
            "originalText": "Every employee is entitled to reasonable notice or payment of wages in lieu of notice upon termination. In case of oral contract, if monthly, at least twenty-eight days' written notice.",
            "aiInterpretation": "Saves workers from sudden loss of livelihood by standardizing fair notice lengths or compensation when leaving a business.",
            "practicalExample": "Getting sacked instantly with zero notice or salary compensation because your manager wanted to hire their relative.",
            "whyItMatters": "Establishes standard industrial boundaries for labor relations and worker dignity.",
            "examTip": "Be mindful: Under Kenya's employment jurisprudence, fair notice is only procedural; the employer must also prove a standard substantive reason (e.g. misconduct) under Section 43.",
            "relevantCases": "Kenya Airways v Kenya Airline Pilots Association",
            "isBookmarked": 0,
            "isRead": 0
        }
    ]

    prewritten_map = {item["sectionNumber"]: item for item in prewritten_statutes}

    # 3. Parse and accumulate final list of statutes
    final_statutes = []
    
    # Process constitution.json structure
    for art_obj in constitution_articles:
        # Extract fields
        art_num = art_obj.get("article_number", 0)
        if art_num == 0:
            continue
        
        title = art_obj.get("title", "")
        
        chapter_obj = art_obj.get("chapter") or {}
        chapter_num = chapter_obj.get("number", "")
        chapter_title = chapter_obj.get("title", "")
        
        part_obj = art_obj.get("part") or {}
        part_num = part_obj.get("number", "")
        part_title = part_obj.get("title", "")
        
        # Skip transitional schedules/etc.
        if (chapter_num.lower() == "eighteen" or 
            "transitional" in chapter_title.lower() or 
            "schedule" in chapter_title.lower()):
            continue
            
        # Compile clauses text
        clauses_arr = art_obj.get("clauses", [])
        text_builder = []
        if clauses_arr and len(clauses_arr) > 0:
            for clause in clauses_arr:
                cl_no = clause.get("number", 0)
                cl_text = clause.get("text", "")
                
                if cl_no > 0:
                    text_builder.append(f"({cl_no}) {cl_text}")
                else:
                    text_builder.append(cl_text)
                
                paragraphs = clause.get("paragraphs", [])
                for p in paragraphs:
                    p_let = p.get("letter", "")
                    p_text = p.get("text", "")
                    text_builder.append(f"    ({p_let}) {p_text}")
        else:
            text_builder.append(f"Refer to the full text of Article {art_num} of the Constitution of Kenya.")
            
        original_text = "\n".join(text_builder).strip()
        location_desc = f"Chapter {chapter_num} ({chapter_title}, Part {part_title})" if part_title else f"Chapter {chapter_num} ({chapter_title})"
        
        # Default AI values
        ai_interpretation = f"Article {art_num} is a foundational constitutional provision within {location_desc}. It details and delineates the official legal parameters regarding: {title}."
        practical_example = f"In commercial or civil litigation involving the state or individuals, Article {art_num} provides the official legal authority governing {title} under {location_desc}."
        why_it_matters = f"Maintains constitutional supremacy by safeguarding the processes and public values defined under Chapter {chapter_num} of the Constitution of Kenya 2010."
        exam_tip = f"Memorize the core elements of Article {art_num}. Understand how this relates to democratic principles in Chapter {chapter_num} and potential limitation tests in Article 24."
        relevant_cases = ""
        
        section_number = f"Article {art_num}"
        
        # Override if prewritten exists
        if section_number in prewritten_map:
            match = prewritten_map[section_number]
            ai_interpretation = match["aiInterpretation"]
            practical_example = match["practicalExample"]
            why_it_matters = match["whyItMatters"]
            exam_tip = match["examTip"]
            relevant_cases = match["relevantCases"]
            
        final_statutes.append({
            "id": f"const_{art_num}",
            "category": "CONSTITUTION",
            "actName": "Constitution of Kenya 2010",
            "sectionNumber": section_number,
            "title": title,
            "originalText": original_text,
            "aiInterpretation": ai_interpretation,
            "practicalExample": practical_example,
            "whyItMatters": why_it_matters,
            "examTip": exam_tip,
            "relevantCases": relevant_cases,
            "isBookmarked": 0,
            "isRead": 0
        })

    # Add other non-constitutional statutes from statutes.json
    STATUTES_JSON_PATH = "app/src/main/assets/statutes.json"
    loaded_offline = False
    if os.path.exists(STATUTES_JSON_PATH):
        try:
            with open(STATUTES_JSON_PATH, "r", encoding="utf-8") as f:
                offline_statutes = json.load(f)
            print(f"Loaded {len(offline_statutes)} offline statutes from {STATUTES_JSON_PATH}")
            for stat in offline_statutes:
                if "isBookmarked" not in stat:
                    stat["isBookmarked"] = 0
                if "isRead" not in stat:
                    stat["isRead"] = 0
                final_statutes.append(stat)
            loaded_offline = True
        except Exception as e:
            print(f"Warning: Could not parse {STATUTES_JSON_PATH}: {e}")

    if not loaded_offline:
        print(f"Using prewritten non-constitutional statutes fallback.")
        for pre in prewritten_statutes:
            if pre["category"] != "CONSTITUTION":
                final_statutes.append(pre)

    print(f"Compiled {len(final_statutes)} final statutes")

    # 4. Court Cases
    court_cases = [
        {
            "id": "case_creaw",
            "title": "CREAW v Attorney General",
            "citation": "[2016] eKLR",
            "articleNumber": "Article 27",
            "facts": "The government made several high-ranking public appointments to state boards and security committees that failed to include the constitutional two-thirds gender representative balance.",
            "issues": "Whether the state is under a binding immediate or progressive obligation to implement equality and gender guidelines in critical public offices.",
            "holding": "The High Court ruled in favor of CREAW, emphasizing that Article 27 equality provisions are binding and public institutions must actively implement affirmative action protocols.",
            "whyItMatters": "Cemented the legal rule that gender diversity quotas are formal constitutional rights that the judiciary will enforce against government bodies.",
            "examTip": "Cite this case as a major authority on gender equality representation under Article 27(8) of the Constitution.",
            "isRead": 0
        },
        {
            "id": "case_muruatetu",
            "title": "Francis Karioko Muruatetu v Republic",
            "citation": "[2017] eKLR",
            "articleNumber": "Section 26",
            "facts": "Muruatetu and others were convicted of murder. Due to the Penal Code's mandatory penalty clause, they were sentenced to death automatically without any judicial mitigation or sentencing hearings.",
            "issues": "Does a mandatory death sentence violate the right to a fair trial (Article 50) and abstract judicial discretion, and constitute cruel, inhuman or degrading treatment?",
            "holding": "The Supreme Court held that the mandatory nature of the death sentence is unconstitutional, as it strips judges of their discretion to issue custom sentences based on offender culpability.",
            "whyItMatters": "Completely overhauled capital crimes jurisprudence in Kenya. Courts now conduct comprehensive mitigation hearings prior to pronouncing any sentence.",
            "examTip": "Do not claim Muruatetu abolished the death penalty! It only abolished the *mandatory* death sentence—meaning a judge *can* still issue a death sentence, but after full discretion.",
            "isRead": 0
        },
        {
            "id": "case_mitubell",
            "title": "Mitu-Bell Welfare Society v Attorney General",
            "citation": "[2021] eKLR",
            "articleNumber": "Article 43",
            "facts": "Over 3,000 residents of an informal settlement near Wilson Airport were violently evicted and their homes demolished by the government without alternative shelter or notice.",
            "issues": "Are socio-economic rights under Article 43 justiciable, and can courts issue structural interdicts (demanding the government report back on progress)?",
            "holding": "The Supreme Court affirmed that socioeconomic rights are fully justiciable and enforceable. It ruled that arbitrary evictions violating human dignity are illegal and courts can supervise administrative remedies.",
            "whyItMatters": "The definitive landmark case on socioeconomic rights. It empowers the poor to claim rights to housing, water, and sanitation directly through the courts.",
            "examTip": "Use this to explain how courts enforce 'progressive realization' by demanding standard structural plans from government departments.",
            "isRead": 0
        }
    ]

    # 5. Legal News
    legal_news = [
        {
            "id": "news_lsk",
            "title": "LSK Issues Advisory on Constitutional Compliance of New Tax Bill",
            "sourceName": "LSK",
            "sourceUrl": "https://lsk.or.ke",
            "aiSummary": "The Law Society of Kenya has published a comprehensive legal critique of the proposed Finance Bill, highlighting potential violations of public participation guidelines (Article 201) and equitable tax principles (Article 118). LSK advises Parliament to amend several clauses or face litigation.",
            "whyLawyersCare": "Signals upcoming constitutional court petitions and defines major tax litigation risk parameters for commercial corporate clients.",
            "datePublished": "2026-06-17"
        },
        {
            "id": "news_judiciary",
            "title": "Chief Justice Laureate Announces Launch of Digital Legal Aid Desks",
            "sourceName": "Judiciary",
            "sourceUrl": "https://judiciary.go.ke",
            "aiSummary": "The Judiciary has rolled out integrated legal aid portals in 14 marginalized counties. Armed with solar-powered terminals and human advisors, they are designed to let rural citizens register land, civil, and domestic disputes easily without steep attorney fees.",
            "whyLawyersCare": "Saves administrative filing times and expands access to justice, representing a huge step for grassroots legal practitioner practices.",
            "datePublished": "2026-06-15"
        },
        {
            "id": "news_gazzette",
            "title": "New Mediation Act Enacted by Parliament to Settle Commercial Cases",
            "sourceName": "Kenya Law",
            "sourceUrl": "http://kenyalaw.org",
            "aiSummary": "The President has signed the Mediation Act into law, standardizing Alternative Dispute Resolution (ADR) procedures in Kenya. The law makes court-annexed mediation agreements fully binding, with the same legal weight as high court decree judgments.",
            "whyLawyersCare": "Directly impacts court litigation timelines. Corporate attorneys must now prioritize structured mediation clauses in all business agreements.",
            "datePublished": "2026-06-12"
        }
    ]

    # 6. Helper function to create DB and structures
    def create_and_populate(db_path):
        # Remove existing if any
        if os.path.exists(db_path):
            try:
                os.remove(db_path)
            except Exception as e:
                print(f"Could not remove old db {db_path}: {e}")
                
        conn = sqlite3.connect(db_path)
        cur = conn.cursor()
        
        # Create Tables
        cur.execute("""
            CREATE TABLE IF NOT EXISTS `statutes` (
                `id` TEXT NOT NULL, 
                `category` TEXT NOT NULL, 
                `actName` TEXT NOT NULL, 
                `sectionNumber` TEXT NOT NULL, 
                `title` TEXT NOT NULL, 
                `originalText` TEXT NOT NULL, 
                `aiInterpretation` TEXT NOT NULL, 
                `practicalExample` TEXT NOT NULL, 
                `whyItMatters` TEXT NOT NULL, 
                `examTip` TEXT NOT NULL, 
                `relevantCases` TEXT NOT NULL, 
                `isBookmarked` INTEGER NOT NULL, 
                `isRead` INTEGER NOT NULL, 
                PRIMARY KEY(`id`)
            )
        """)
        
        cur.execute("CREATE INDEX IF NOT EXISTS idx_statutes_category ON statutes(category);")
        cur.execute("CREATE INDEX IF NOT EXISTS idx_statutes_actName ON statutes(actName);")
        cur.execute("CREATE INDEX IF NOT EXISTS idx_statutes_title ON statutes(title);")
        cur.execute("CREATE INDEX IF NOT EXISTS idx_statutes_section ON statutes(sectionNumber);")
        
        cur.execute("""
            CREATE TABLE IF NOT EXISTS `court_cases` (
                `id` TEXT NOT NULL, 
                `title` TEXT NOT NULL, 
                `citation` TEXT NOT NULL, 
                `articleNumber` TEXT NOT NULL, 
                `facts` TEXT NOT NULL, 
                `issues` TEXT NOT NULL, 
                `holding` TEXT NOT NULL, 
                `whyItMatters` TEXT NOT NULL, 
                `examTip` TEXT NOT NULL, 
                `isRead` INTEGER NOT NULL, 
                PRIMARY KEY(`id`)
            )
        """)
        
        cur.execute("""
            CREATE TABLE IF NOT EXISTS `legal_news` (
                `id` TEXT NOT NULL, 
                `title` TEXT NOT NULL, 
                `sourceName` TEXT NOT NULL, 
                `sourceUrl` TEXT NOT NULL, 
                `aiSummary` TEXT NOT NULL, 
                `whyLawyersCare` TEXT NOT NULL, 
                `datePublished` TEXT NOT NULL, 
                PRIMARY KEY(`id`)
            )
        """)
        
        cur.execute("""
            CREATE TABLE IF NOT EXISTS `user_stats` (
                `userId` TEXT NOT NULL, 
                `name` TEXT NOT NULL, 
                `streak` INTEGER NOT NULL, 
                `xp` INTEGER NOT NULL, 
                `lastActiveDate` TEXT NOT NULL, 
                `rank` TEXT NOT NULL, 
                PRIMARY KEY(`userId`)
            )
        """)
        
        cur.execute("""
            CREATE TABLE IF NOT EXISTS `ai_messages` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                `role` TEXT NOT NULL, 
                `content` TEXT NOT NULL, 
                `timestamp` INTEGER NOT NULL, 
                `sessionId` TEXT NOT NULL
            )
        """)
        
        cur.execute("""
            CREATE TABLE IF NOT EXISTS room_master_table (
                id INTEGER PRIMARY KEY,
                identity_hash TEXT
            )
        """)
        
        # Insert Room Master Table identity hash
        cur.execute("INSERT OR REPLACE INTO room_master_table (id, identity_hash) VALUES(42, '7a2e7659367251ca04d9da162bcccf83')")
        
        # Insert default user_stats
        cur.execute("""
            INSERT INTO user_stats (userId, name, streak, xp, lastActiveDate, rank) 
            VALUES ('local_user', 'Guest', 0, 0, '', 'Beginner')
        """)
        
        # Insert statutes
        for stat in final_statutes:
            cur.execute("""
                INSERT OR REPLACE INTO statutes (
                    id, category, actName, sectionNumber, title, originalText, 
                    aiInterpretation, practicalExample, whyItMatters, examTip, 
                    relevantCases, isBookmarked, isRead
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """, (
                stat["id"], stat["category"], stat["actName"], stat["sectionNumber"], stat["title"],
                stat["originalText"], stat["aiInterpretation"], stat["practicalExample"],
                stat["whyItMatters"], stat["examTip"], stat["relevantCases"], stat["isBookmarked"], stat["isRead"]
            ))
            
        # Insert court cases
        for case in court_cases:
            cur.execute("""
                INSERT OR REPLACE INTO court_cases (
                    id, title, citation, articleNumber, facts, issues, holding, 
                    whyItMatters, examTip, isRead
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """, (
                case["id"], case["title"], case["citation"], case["articleNumber"], case["facts"],
                case["issues"], case["holding"], case["whyItMatters"], case["examTip"], case["isRead"]
            ))
            
        # Insert legal news
        for news in legal_news:
            cur.execute("""
                INSERT OR REPLACE INTO legal_news (
                    id, title, sourceName, sourceUrl, aiSummary, whyLawyersCare, datePublished
                ) VALUES (?, ?, ?, ?, ?, ?, ?)
            """, (
                news["id"], news["title"], news["sourceName"], news["sourceUrl"], news["aiSummary"],
                news["whyLawyersCare"], news["datePublished"]
            ))
            
        conn.commit()
        conn.close()
        print(f"Successfully generated and seeded {db_path} database.")

    # Generate both files
    create_and_populate(TARGET_ROOT_DB_PATH)
    create_and_populate(TARGET_ASSET_DB_PATH)
    print("Database seeding completed fully!")

if __name__ == "__main__":
    run()
