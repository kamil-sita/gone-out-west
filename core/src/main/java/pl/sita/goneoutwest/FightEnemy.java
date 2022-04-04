package pl.sita.goneoutwest;

import com.badlogic.gdx.math.MathUtils;

public class FightEnemy {
    public float shootingSpeed = MathUtils.random(0.9f, 1.2f);

    public float xPos = Constants.WIDTH * 1.2f;
    public float yPos;
    public int hp = 3;
    public float invincibility = 0;
    public float speed = 1;
    public int delay = 2;
    public boolean isDead = false;
    public float deadCameraPos;

    public HumanAnim humanAnim = HumanAnim.DEFAULT;
    public float animLength = 0;

    public int ammoInClip = AMMO_IN_CLIP;
    public float fireCoolDown = shootingSpeed;
    public float reloadCoolDown = RELOAD_COOLDOWN;


    public static final float RELOAD_COOLDOWN = 5f;
    public static final int AMMO_IN_CLIP = 3;

}
