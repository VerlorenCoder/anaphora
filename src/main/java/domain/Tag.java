package domain;

public enum Tag {

    COORDINATING_CONJUNCTION("CC", "spójnik"),
    CARDINAL_NUMBER("CD", "liczba"),
    DETERMINER("DT", "oznacznik(?)"),
    EXISTENSIAL_THERE("EX", "(there)"),
    FOREIGN_WORD("FW", "słowo zagraniczne"),
    PREPOSITION_OR_SUBORDINATING_CONJUNCTION("IN", "przyimek"),
    ADJECTIVE("JJ", "przymiotnik w stopniu równym"),
    ADJECTIVE_COMPARATIVE("JJR", "przymiotnik w stopniu wyższym"),
    ADJECTIVE_SUPERLATIVE("JJS", "przymiotnik w stopniu najwyższym"),
    LIST_ITEM_MARKER("LS", "(list item marker)"),
    MODAL("MD", "czasownik modalny"),
    NOUN_SINGULAR_OR_GROUP("NN", "rzeczownik w liczbie pojedynczej lub dla grupy"),
    NOUN_PLURAL("NNS", "rzeczownik w liczbie mnogiej"),
    PROPER_NOUN_SINGULAR("NNP", "nazwa własna w liczbie pojedynczej"),
    PROPER_NOUN_PLURAL("NNPS", "nazwa własna w liczbie mnogiej"),
    PREDETERMINER("PDT", "(predeterminer)"),
    POSSESSIVE_ENDING("POS", "(possessive ending)"),
    PERSONAL_PRONOUN("PRP", "zaimek"),
    POSSESSIVE_PRONOUN("PRP$", "zaimek dzierżawczy"),
    ADVERB("RB", "przysłówek"),
    ADVERB_COMPARATIVE("RBR", "przysłowke w stopniu wyższym"),
    ADVERB_SUPERLATIVE("RBS", "przysłowke w stopniu najwyższym"),
    PARTICLE("RP", "(particle)"),
    SYMBOL("SYM", "(symbol)"),
    TO("TO", "(to)"),
    INTERJECTION("UH", "(interjection)"),
    VERB_BASE_FORM("VB", "czasownik w czasie teraźniejszym"),
    VERB_PAST_FORM("VBD", "czasownik w czasie przeszłym"),
    VERB_OR_GERUND_OR_PRESENT_PARTICIPLE("VBG", "czasownik - rzeczownik odczasownikowy lub imiesłów teraźniejszy"),
    VERB_PAST_PARTICIPLE("VBN", "czasownik - imiesłów przymiotnikowy bierny w 3 formie"),
    VERB_NON_3RD_PERSON_SINGULAR_PRESENT("VBP", "czasownik w czasie teraźniejszym w formie podstawowej poza 3 os. l.p."),
    WH_DETERMINER("WDT", "(wh determiner)"),
    WH_PRONOUN("WP", "(wh pronoun)"),
    POSSESSIVE_WH_PRONOUN("WP$", "(possessive wh pronoun)"),
    WH_ADVERB("WRB", "(wh adverb)"),
    UNKNOWN("?", "nierozpoznany");

    private String abbreviation;
    private String polishName;

    Tag(String abbreviation, String polishName) {
        this.abbreviation = abbreviation;
        this.polishName = polishName;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public String getPolishName() {
        return polishName;
    }

    public Tag fromAbbreviation(String abbreviation) {
        for (Tag tag: Tag.values()) {
            if (tag.abbreviation.equals(abbreviation)) {
                return tag;
            }
        }
        return Tag.UNKNOWN;
    }
}
