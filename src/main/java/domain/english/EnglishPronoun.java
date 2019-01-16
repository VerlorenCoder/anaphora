package domain.english;

import static domain.english.Destination.*;
import static domain.english.Number.*;

public enum EnglishPronoun {

    I("I", new Number[]{SINGULAR}, new Destination[]{FOR_MAN, FOR_WOMAN}),
    YOU("you", new Number[]{SINGULAR, PLURAL}, new Destination[]{FOR_MAN, FOR_WOMAN}),
    HE("he", new Number[]{SINGULAR}, new Destination[]{FOR_MAN}),
    SHE("she", new Number[]{SINGULAR}, new Destination[]{FOR_WOMAN}),
    IT("it", new Number[]{SINGULAR}, new Destination[]{FOR_THING}),
    WE("we", new Number[]{PLURAL}, new Destination[]{FOR_MAN, FOR_WOMAN}),
    THEY("they", new Number[]{PLURAL}, new Destination[]{FOR_MAN, FOR_WOMAN, FOR_THING}),

    HIS("his", new Number[]{SINGULAR}, new Destination[]{FOR_MAN}),

    UNKNOWN("?", new Number[]{SINGULAR, PLURAL}, new Destination[]{FOR_MAN, FOR_WOMAN, FOR_THING});


    private String value;
    private Number[] numbers;
    private Destination[] destinations;


    EnglishPronoun(String abbreviation, Number[] numbers, Destination[] destinations) {
        this.value = abbreviation;
        this.numbers = numbers;
        this.destinations = destinations;
    }

    public Number[] getNumbers() {
        return numbers;
    }

    public Destination[] getDestinations() {
        return destinations;
    }

    public static EnglishPronoun fromValue(String value) {
        for (EnglishPronoun type: EnglishPronoun.values()) {
            if (type.value.toLowerCase().equals(value.toLowerCase())) {
                return type;
            }
        }
        return UNKNOWN;
    }
}
