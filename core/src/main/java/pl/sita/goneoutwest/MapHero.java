package pl.sita.goneoutwest;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

public class MapHero {

    private Town currentTown;
    private Town targetTown;
    private List<Letter> letters = new ArrayList<>();

    private float progress = 1;

    public MapHero(Town currentTown) {
        this.currentTown = currentTown;
        this.targetTown = currentTown;
    }

    public float getX() {
        return MathUtils.lerp(
                currentTown.getX(),
                targetTown.getX(),
                progress
        );
    }

    public float getY() {
        return MathUtils.lerp(
                currentTown.getY(),
                targetTown.getY(),
                progress
        );
    }

    public float getTownDistance() {
        return new Vector2(
                currentTown.getX() - targetTown.getX(),
                currentTown.getY() - targetTown.getY()
        ).len();
    }

    public boolean isOnTheMove() {
        return progress != 1;
    }

    public List<Letter> getLetters() {
        return letters;
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
    }

    public Town getCurrentTown() {
        return currentTown;
    }

    public void setCurrentTown(Town currentTown) {
        this.currentTown = currentTown;
    }

    public Town getTargetTown() {
        return targetTown;
    }

    public void setTargetTown(Town targetTown) {
        this.targetTown = targetTown;
    }
}
