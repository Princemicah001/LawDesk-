package com.example.data

import org.json.JSONObject
import java.util.Calendar

data class CustomReminder(val title: String, val body: String)

object NotificationPayloads {
    const val payloadsJson = """
{
  "article_of_the_day": [
    {"title": "Article of the Day: Article 1", "body": "Sovereignty of the People: All sovereign power belongs to the people of Kenya to be exercised in accordance with this Constitution."},
    {"title": "Article of the Day: Article 2", "body": "Supremacy of this Constitution: Any law, acting, or custom that is inconsistent with this Constitution is void."},
    {"title": "Article of the Day: Article 10", "body": "National Values & Principles: Governance must respect patriotism, unity, and the rule of law."},
    {"title": "Article of the Day: Article 19", "body": "Bill of Rights: Human rights belong to all individuals and form the base of social-economic policy."},
    {"title": "Article of the Day: Article 22", "body": "Enforcement of Rights: Any citizen has the right to institute court proceedings to secure basic constitutional rights."},
    {"title": "Article of the Day: Article 26", "body": "Right to Life: Every person has an inherent right to life; it shall not be deprived arbitrarily or unlawfully."},
    {"title": "Article of the Day: Article 27", "body": "Equality & Discrimination: No state organ or person may discriminate directly or indirectly against any individual."},
    {"title": "Article of the Day: Article 28", "body": "Inherent Human Dignity: Explore your constitutional right to have your human dignity respected and protected."},
    {"title": "Article of the Day: Article 31", "body": "Right to Privacy: Protecting citizens against unlawful visual, physical, or electronic data searches and intrusions."},
    {"title": "Article of the Day: Article 33", "body": "Freedom of Expression: Includes freedom to seek and receive information, but excludes incitement and hate speech."},
    {"title": "Article of the Day: Article 35", "body": "Access to Information: Every citizen has the right of access to any critical information held by the State."},
    {"title": "Article of the Day: Article 37", "body": "Assembly & Protest: Right to demonstrate peaceably and unarmed, and to present petitions to public authorities."},
    {"title": "Article of the Day: Article 40", "body": "Protection of Property: Constitutional guarantee against arbitrary deprivation of land, home, or physical property units."},
    {"title": "Article of the Day: Article 43", "body": "Economic & Social Rights: Guarantees your right to the highest attainable standard of health, food, and education."},
    {"title": "Article of the Day: Article 47", "body": "Fair Administrative Action: Entitles everyone to expeditious, efficient, lawful and procedurally fair actions."},
    {"title": "Article of the Day: Article 48", "body": "Access to Justice: The State's mandate to ensure justice remains accessible without prohibitive filing fees."},
    {"title": "Article of the Day: Article 49", "body": "Rights of Arrested Persons: Highlights the absolute right to be brought to court within twenty-four hours of arrest."},
    {"title": "Article of the Day: Article 50", "body": "Right to a Fair Trial: Every accused person has the constitutional guarantee to a public trial before an impartial court."},
    {"title": "Article of the Day: Article 159", "body": "Judicial Authority: Justice must be dispensed to all without delay, promoting local alternative dispute resolution."},
    {"title": "Article of the Day: Article 232", "body": "Values of Public Service: Chapter 13 demands high standards of professional ethics, integrity, and meritocracy."}
  ],
  "case_law": [
    {"title": "Case Law: Mwangi v Republic", "body": "Landmark ruling establishing strict burdens of proof on prosecution in circumstantial criminal allegations."},
    {"title": "Case Law: Muruatetu Case", "body": "The Supreme Court declared the mandatory death sentence unconstitutional, preserving judicial sentencing discretion."},
    {"title": "Case Law: Omtatah Finance Petitions", "body": "Affirmed that genuine public participation is a mandatory constitutional condition before enacting major tax legislations."},
    {"title": "Case Law: Peter K. Waweru Court Suit", "body": "Held that the constitutional right to life extends directly to a clean, safe, and healthy environment."},
    {"title": "Case Law: David Ngige Landmark Verdict", "body": "Clarified that self-defense actions must be reasonable, equal, and proportional to the active immediate threat."},
    {"title": "Case Law: Gachura v Gachura", "body": "An essential precedent reinforcing equitable distribution of matrimonial property upon dissolution of marriage."},
    {"title": "Case Law: Rose Wangui Judicial Decree", "body": "Affirmed that oral wills must be made before two competent witnesses to retain valid standing under estate laws."},
    {"title": "Case Law: Kinuthia adverse possession", "body": "Clarified requirements of open, continuous, and peaceful adverse occupancy of land for at least 12 years."},
    {"title": "Case Law: Wambua Customary Verdict", "body": "Explored the legal compatibility check of local customary family systems with written human rights statutes."},
    {"title": "Case Law: Nyaboke Terminations", "body": "Ruled that employers can never terminate contracts without offering physical hearings and explicit valid justification."},
    {"title": "Case Law: Kamau v Standard News", "body": "A critical precedent differentiating standard libel prints from verbal slanders in defamation damage claims."},
    {"title": "Case Law: Juma Judicial Review", "body": "Explored conditions under which courts issue Certiorari orders to quash biased decisions made by administrative bodies."},
    {"title": "Case Law: Kariuki Tax Rulings", "body": "Set the precedent that the State cannot collect any duties without explicit backing from an Act of Parliament."},
    {"title": "Case Law: Otieno Soil Dispute", "body": "Determined the interplay between customary burial wishes and the statutory rights of the surviving nuclear family."},
    {"title": "Case Law: Kenya Airports Authority case", "body": "Defined limitations of government immunity when public actions lead to active damage to private property units."},
    {"title": "Case Law: Barasa Arrest Petitions", "body": "Affirmed that confessions obtained under police coercion should be excluded as unconstitutional and inadmissible evidence."},
    {"title": "Case Law: Maina Kiai Ruling", "body": "Held that presidential election results declared at the constituency tier are final and cannot be modified."},
    {"title": "Case Law: Coalition for Reform case", "body": "Clarified boundaries of national security laws when they conflict with fundamental press and speech freedoms."},
    {"title": "Case Law: Martin Nyaga Civil Suits", "body": "Established standard rules governing locus standi, opening paths for public interest litigation without direct personal losses."},
    {"title": "Case Law: Mumo Contract Disputes", "body": "Set standards for calculating breach damages, aiming to return the injured party to their original financial standing."}
  ],
  "legal_companion": [
    {"title": "Legal Companion: AI Guidance", "body": "Draft comprehensive corporate contracts using structured elements like offer, acceptance, consideration, and capacity."},
    {"title": "Legal Companion: Precedent Check", "body": "Chat with LawDesk AI to analyze any land dispute, title validation, or tenant rights under current laws."},
    {"title": "Legal Companion: Document Reviews", "body": "Let LawDesk AI assist in breaking down dense legal jargon into clear, professional terms for your active clients."},
    {"title": "Legal Companion: Quiz Practice", "body": "Ready to test your knowledge? Try an interactive constitutional law quiz with the AI tutor companion today!"},
    {"title": "Legal Companion: Tort Assistance", "body": "Ask LawDesk AI to outline the essential elements of a negligence claim, including duty, breach, and causation."},
    {"title": "Legal Companion: Advocate Studies", "body": "Learn about the professional ethics, rolls, and standards required for admission into the bar under current statutes."},
    {"title": "Legal Companion: Succession Tool", "body": "Understand the process of intestate estate sharing and the role of the public trustee in family distributions."},
    {"title": "Legal Companion: Labor Adviser", "body": "Analyze wrongful dismissals, notice metrics, and fair hearing protocols using the LawDesk AI smart portal."},
    {"title": "Legal Companion: Penal Code Aid", "body": "Explore legal definitions of malicious intent, manslaughter, and ownership theft with instant AI feedback."},
    {"title": "Legal Companion: Bill of Rights Check", "body": "Ask your companion how specific articles like Article 43 on social-economic rights can be litigated in court."},
    {"title": "Legal Companion: Alternative ADR", "body": "Learn to utilize professional mediation and arbitration techniques to bypass lengthy civil court litigations."},
    {"title": "Legal Companion: County Insights", "body": "Get advice on how devolution, public funds, and local county assemblies function under the current framework."},
    {"title": "Legal Companion: Fair Hear Checker", "body": "Confirm if the rights of an arrested person have been respected using our interactive constitutional checklist."},
    {"title": "Legal Companion: Contract Drafting", "body": "Let the AI outline vital protection terms, indemnities, and termination triggers for lease agreements."},
    {"title": "Legal Companion: Public Procurement", "body": "Consult the AI on compliance requirements for government tenders to avoid unlawful disqualifications."},
    {"title": "Legal Companion: Leadership Ethics", "body": "Review the standards of integrity, public trusts, and dual-citizenship limitations in Chapter 6."},
    {"title": "Legal Companion: Env Law Support", "body": "Analyze the legal backing of environmental lawsuits and polluter-pays liability with the AI's assist."},
    {"title": "Legal Companion: Marriage Queries", "body": "Learn about registration processes, valid ages, and legal grounds under the unified Marriage framework."},
    {"title": "Legal Companion: Public Petitions", "body": "Review how to draft and file a public interest petition to address community grievances or rights violations."},
    {"title": "Legal Companion: Interactive Study", "body": "Discuss any complex legislative article or supreme court precedent directly with your smart AI companion today."}
  ]
}
"""

    fun getPayloadForId(alarmId: Int, dayOfYearOffset: Int): CustomReminder {
        return try {
            val root = JSONObject(payloadsJson)
            val key = when (alarmId) {
                1 -> "article_of_the_day"
                2 -> "case_law"
                else -> "legal_companion"
            }
            val array = root.getJSONArray(key)
            val index = (dayOfYearOffset) % 20
            val item = array.getJSONObject(index)
            CustomReminder(item.getString("title"), item.getString("body"))
        } catch (e: Exception) {
            // High-quality fallback if parsing ever encounters an issue
            when (alarmId) {
                1 -> CustomReminder("Article of the Day", "Explore daily constitutional principles on LawDesk.")
                2 -> CustomReminder("Case Law Precedent", "Review the latest leading case precedents on LawDesk.")
                else -> CustomReminder("Legal Companion Insight", "Discuss legal doctrines directly with LawDesk AI.")
            }
        }
    }
}
