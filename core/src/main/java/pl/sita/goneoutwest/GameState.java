package pl.sita.goneoutwest;

public class GameState {

    public static int heroHp = Constants.HERO_MAX_HP;
    public static int herHp = Constants.HER_MAX_HP;
    public static int herAnimHp = 4;
    public static int herHpCounter = Constants.HER_HP_COUNTER;
    public static int ammo = Constants.MAX_AMMO;

    public static int enemiesKilled = 0;

    public static int bulletsFired = 0;

    public static int animMoney = 0;
    public static int realMoney = 30;
    public static int earned = 0;
    public static int lettersDelivered = 0;

    public static boolean firstFight = true;

    public static FightVictoryType lastVictoryType = null;
    public static int visited = 0;
    public static int timeSurvived;

    public static void reset() {
        timeSurvived = 0;
        visited = 0;
        earned = 0;
        lettersDelivered = 0;
        heroHp = Constants.HERO_MAX_HP;
        herHp =  Constants.HER_MAX_HP;
        herAnimHp =  Constants.HER_MAX_HP;
        herHpCounter = Constants.HER_HP_COUNTER;
        ammo = Constants.MAX_AMMO;

        animMoney = 0;
        realMoney = 30;

        enemiesKilled = 0;
        bulletsFired = 0;
    }

}
