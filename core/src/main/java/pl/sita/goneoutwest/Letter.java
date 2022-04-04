package pl.sita.goneoutwest;

public class Letter {
    private int cost;
    private ColorType letterColor;

    public Letter(int cost, ColorType letterColor) {
        this.cost = cost;
        this.letterColor = letterColor;
    }

    public int getCost() {
        return cost;
    }

    public ColorType getLetterColor() {
        return letterColor;
    }
}
