package domain.polish;

public enum PolishTag {

    RZECZOWNIK("subst", "rzeczownik"),
    RZECZOWNIK_DEPRECJATYWNY("depr", "rzeczownik deprecjatywny"),
    PRZYMIOTNIK("adj", "przymiotnik"),
    PRZYMIOTNIK_PRZYPRZYMIOTNIKOWY("adja", "przymiotnik przyprzymiotnikowy"),
    PRZYMIOTNIK_POPRZYIMKOWY("adjp", "przymiotnik poprzyimkowy"),
    PRZYSLOWEK("adv", "przyslowek"),
    LICZEBNIK("num", "liczebnik"),
    ZAIMEK_NIETRZECIOOSOBOWY("ppron12", "zaimek nietrzecioosobowy"),
    ZAIMEK_TRZECIOOSOBOWY("ppron3", "zaimek trzeciosobowy"),
    ZAIMEK_SIEBIE("siebie", "zaimek siebie"),
    CZASOWNIK_NIEPRZESZLY("fin", "czasownik nieprzeszly"),
    CZASOWNIK_BYC_PRZYSZLY("bedzie", "czasownik byc przyszly"),
    CZASOWNIK_BYC_AGLUTYWNANT("aglt", "czasownik byc aglutynant"),
    CZASOWNIK_PSEUDOIMIESLOW("praet", "czasownik pseudoimieslow"),
    CZASOWNIK_ROZKAZNIK("impt", "czasownik rozkaznik"),
    CZASOWNIK_BEZOSOBNIK("imps", "czasownik bezosobnik"),
    CZASOWNIK_BEZOKOLICZNIK("inf", "czasownik bezokolicznik"),
    CZASOWNIK_IMIESLOW_PRZYSLOWKOWY_WSPOLCZESNY("pcon", "czasownik imieslow przyslowkowy wspolczesny"),
    CZASOWNIK_IMIESLOW_PRZYSLOWKOWY_UPRZEDNI("pant", "czasownik imieslow przyslowkowy uprzedni"),
    CZASOWNIK_ODSLOWNIK("ger", "czasownik odslownik"),
    CZASOWNIK_IMIESLOW_PRZYMIOTNIKOWY_CZYNNY("pact", "czasownik imieslow przymiotnikowy czynny"),
    CZASOWNIK_IMIESLOW_PRZYMIOTNIKOWY_BIEDNY("ppas", "czasownik imieslow przymiotnikowy bierny"),
    CZASOWNIK_WINIEN("winien", "czasownik winien"),
    PREDYKATYW("pred", "predykatyw"),
    PRZYIMEK("prep", "przyimek"),
    SPOJNIK("conj", "spojnik"),
    KUBLIK("qub", "kublik"),
    CIALO_OBCE_NOMINALNE("xxs", "cialo obce nominalne"),
    CIALO_OBCE_LUZNE("xxx", "cialo obce luzne"),

    LICZBA_POJEDYNCZA("sg", "liczba pojedyncza"),
    LICZBA_MNOGA("pl", "liczba mnoga"),
    PRZYPADEK_MIANOWNIK("nom", "mianownik"),
    PRZYPADEK_DOPELNIACZ("gen", "dopelniacz"),
    PRZYPADEK_CELOWNIK("dat", "celownik"),
    PRZYPADEK_BIERNIK("acc", "biernik"),
    PRZYPADEK_NARZEDNIK("inst", "narzednik"),
    PRZYPADEK_MIEJSCOWNIK("loc", "miejscownik"),
    PRZYPADEK_WOLACZ("voc", "wolacz"),
    RODZAJ_MESKI_OSOBOWY("m1", "rodzaj meski osobowy"),
    RODZAJ_MESKI_ZWIERZECY("m2", "rodzaj meski zwierzecy"),
    RODZAJ_MESKI_RZECZOWY("m3", "rodzaj meski rzeczowy"),
    RODZAJ_ZENSKI("f", "rodzaj zenski"),
    RODZAJ_NIJAKI_ZBIOROWY("n1", "rodzaj nijaki zbiorowy"),
    RODZAJ_NIJAKI_ZWYKLY("n2", "rodzaj nijaki zwykly"),
    RODZAJ_PRZYMNOGI_OSOBOWY("p1", "rodzaj przymnogi osobowy"),
    RODZAJ_PRZYMNOGI_ZWYKLY("p2", "rodzaj przymnogi zwykly"),
    RODZAJ_PRZYMNOGI_OPISOWY("p3", "rodzaj przymnogi opisowy"),
    OSOBA_PIERWSZA("pri", "osoba pierwsza"),
    OSOBA_DRUGA("sec", "osoba druga"),
    OSOBA_TRZECIA("ter", "osoba trzecia"),
    STOPIEN_ROWNY("pos", "stopien rowny"),
    STOPIEN_WYZSZY("comp", "stopien wyzszy"),
    STOPIEN_NAJWYZSZY("sup", "stopien najwyzszy"),
    ASPEKT_NIEDOKONANY("imperf", "aspekt niedokonany"),
    ASPEKY_DOKONANY("perf", "aspekt dokonany"),
    NIEZANEGOWANIE("aff", "niezanegowanie"),
    ZANEGOWANIE("neg", "zanegowanie"),
    AKCENTOWANIE("akc", "akcentowanie"),
    NIEAKCENTOWANIE("nakc", "nieakcentowanie"),
    PRZYIMKOWOSC("praep", "przyimkowosc"),
    NIEPOPRYIMKOWOSC("npraep", "nieprzyimkowosc"),
    AKOMODAYCJNOSC_UZGADNIAJACA("congr", "akomodacyjnosc uzgadniajaca"),
    AKOMODACYJNOSC_RZADZACA("rec", "akomodacyjnosc rzadzaca"),
    AGLUTYNACYJNOSC("agl", "aglutynacyjnosc"),
    NIEAGLUTYNACYJNOSC("nagl", "nieaglutynacyjnosc"),
    WOKALICZNOSC("wok", "wokalicznosc"),
    NIEWOKALICZNOSC("nwok", "niewokalicznosc"),

    PARTYKULA("part", "partykula"),

    NIEZNANY("?", "nierozpoznany");

    private String abbreviation;
    private String name;

    PolishTag(String abbreviation, String name) {
        this.abbreviation = abbreviation;
        this.name = name;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public String getName() {
        return name;
    }

    public static PolishTag fromAbbreviation(String abbreviation) {
        for (PolishTag tag: PolishTag.values()) {
            if (tag.abbreviation.equals(abbreviation)) {
                return tag;
            }
        }

        return PolishTag.NIEZNANY;
    }
}
