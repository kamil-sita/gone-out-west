package pl.sita.goneoutwest;

public class Constants {

    public static final int WIDTH = 1600;
    public static final int HEIGHT = 900;

    public static final boolean isDebug = false;


    public static final float INTRO_SPEED = 1f;
    public static final float TRAVELING_MAP_SPEED = isDebug ? 0.8f : 0.4f;

    public static final float TOWN_RADIUS = 25;

    public static final boolean PLAY_INTRO = !isDebug;
    public static final int HER_HP_COUNTER = isDebug ? 20 : 20;
    public static final int MAX_AMMO = 60;
    public static final int HERO_MAX_HP = 10;
    public static final int HER_MAX_HP = 5;

    public static int flipY(int y) {
        return HEIGHT - y;
    }

    public static float flipY(float y) {
        return HEIGHT - y;
    }
}
