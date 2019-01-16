package domain.english;


import java.util.ArrayList;
import java.util.List;

public enum  Destination {

    FOR_MAN,
    FOR_WOMAN,
    FOR_THING;

    public static List<Destination> fromNounType(String nounType) {

        List<Destination> destinationsToReturn = new ArrayList<>();

        if (nounType.contains("M")) {
            destinationsToReturn.add(FOR_MAN);
        }
        if (nounType.contains("W")) {
            destinationsToReturn.add(FOR_WOMAN);
        }
        if (nounType.contains("T")) {
            destinationsToReturn.add(FOR_THING);
        }

        return destinationsToReturn;
    }
}
