package domain.english;


import java.util.ArrayList;
import java.util.List;

public enum  Number {

    SINGULAR,
    PLURAL;

    public static List<Number> fromNounType(String nounType) {

        List<Number> numbersToReturn = new ArrayList<>();

        if (nounType.contains("S")) {
            numbersToReturn.add(SINGULAR);
        }
        if (nounType.contains("P")) {
            numbersToReturn.add(PLURAL);
        }

        return numbersToReturn;
    }
}
