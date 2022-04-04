package pl.sita.goneoutwest;

public class FightBullet {

    public final boolean isFriendly;
    public float x;
    public float y;
    public float ySpeed;
    public float xSpeed;

    public FightBullet(boolean isFriendy, float x, float y, float xSpeed, float ySpeed) {
        this.isFriendly = isFriendy;
        this.x = x;
        this.y = y;
        this.ySpeed = ySpeed;
        this.xSpeed = xSpeed;
    }
}
