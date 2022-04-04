package pl.sita.goneoutwest;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class Town {

    private static final AtomicInteger idGenerator = new AtomicInteger();

    private final int x;
    private final int y;
    private final int id;
    private final String name;
    private final boolean hasHospital;
    private final boolean sellsAmmo;
    private final boolean curesHorses;
    private final ColorType townColor;
    private final int townSize;

    private float letterGenerationProgress = 0;
    private float animProgress = 0;
    private List<ColorType> letters = new ArrayList<>();

    public Town(int x, int y, String name, boolean hasHospital, boolean sellsAmmo, boolean curesHorses, ColorType townColor, int townSize) {
        this.x = x;
        this.y = y;
        this.name = name;
        this.hasHospital = hasHospital;
        this.sellsAmmo = sellsAmmo;
        this.curesHorses = curesHorses;
        this.townColor = townColor;
        this.townSize = townSize;
        id = idGenerator.getAndIncrement();
        if (townSize >= 3) {
            letters.add(ColorType.randomExclude(townColor));
        }
        //pseudoRandom start value
        letterGenerationProgress = name.length() / 20.0f + townSize/5.0f;
        animProgress = letterGenerationProgress * 150;
    }

    public int getX() {
        return x;
    }

    public float getY() {
        return y + (float) (2f * Math.sin(animProgress));
    }

    public String getName() {
        return name;
    }

    public boolean isConnected(Town town, List<Connection> connections) {
        for (Connection connection : connections) {
            if (connection.getTown1() == this || connection.getTown2() == this) {
                if (connection.getTown1() == town || connection.getTown2() == town) {
                    return true;
                }
            }
        }

        return false;
    }

    public void update(float dt) {
        letterGenerationProgress += dt * townSize * (1/70f); //todo balance

        if (letterGenerationProgress >= 1) {
            if (letters.size() < townSize) {
                letters.add(ColorType.randomExclude(townColor));
                letterGenerationProgress = 0;
            }
            if (letterGenerationProgress >= 5) {
                letters.remove(0);
                letterGenerationProgress = 0;
            }
        }
    }

    public void updateAnim(float dt) {
        animProgress += dt * Math.sqrt(townSize);
    }

    public int getId() {
        return id;
    }

    public boolean isHasHospital() {
        return hasHospital;
    }

    public boolean isSellsAmmo() {
        return sellsAmmo;
    }

    public boolean isCuresHorses() {
        return curesHorses;
    }

    public ColorType getTownColor() {
        return townColor;
    }

    public int getTownSize() {
        return townSize;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Town town = (Town) o;
        return id == town.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public int getLetterCount() {
        return letters.size();
    }

    public List<ColorType> getLetters() {
        return letters;
    }
}
