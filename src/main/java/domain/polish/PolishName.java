package domain.polish;

public enum PolishName {

    NAZWA_WLASNA("nazwa własna"),
    NAZWA_GEOGRAFICZNA("nazwa geograficzna"),
    NAZWA_POSPOLITA("nazwa pospolita"),
    NAZWISKO("nazwisko"),
    NIEZNANY("???");

    private String name;

    PolishName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
