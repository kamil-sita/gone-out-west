package pl.sita.goneoutwest;

public class Connection {

    private final Town town1;
    private final Town town2;

    private float danger = 0;

    public Connection(Town town1, Town town2, int danger) {
        this.town1 = town1;
        this.town2 = town2;
        this.danger = danger;
    }

    public Town getTown1() {
        return town1;
    }

    public Town getTown2() {
        return town2;
    }

    public boolean contains(Town town) {
        return getTown1() == town || getTown2() == town;
    }

    public float calcDanger(float seed, int time) {
        if (time >= 12 && time <= 16 ) {
            return 0;
        }
        float mySeed = (town1.getId() * 3 + town2.getId() * 5 + town1.getId() + town2.getId());
        return 33 * (float) Math.cos(mySeed + seed * 0.25) + 33 + danger;
    }

}
