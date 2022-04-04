package pl.sita.goneoutwest;

import com.badlogic.gdx.math.MathUtils;

public enum ColorType {
    BROWN("brown"),
    DARKBLUE("darkblue"),
    GREEN("green"),
    LIGHT_BLUE("lightblue"),
    PURPLE("purple"),
    RED("red"),
    WHITE("white"),
    YELLOW("yellow");

    private final String postfix;

    ColorType(String postfix) {
        this.postfix = postfix;
    }

    public String getLetterFileName() {
        return "letter_" + postfix + ".png";
    }

    public String getTownFileName() {
        return "town_" + postfix + ".png";
    }

    public static ColorType random() {
        int id = MathUtils.random(0, ColorType.values().length - 1);

        return ColorType.values()[id];
    }

    public static ColorType randomExclude(ColorType exclude) {
        int id = MathUtils.random(0, ColorType.values().length - 2);

        ColorType generated = ColorType.values()[id];

        if (generated == exclude) {
            return ColorType.values()[ColorType.values().length - 1];
        }

        return generated;
    }
}
