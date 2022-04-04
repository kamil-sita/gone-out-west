package pl.sita.goneoutwest;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class FightScreen implements Screen {

	private final long length;
	private static final AtomicInteger idGenerator = new AtomicInteger();
	private final int id = idGenerator.getAndIncrement();

	float introTime = 0;
	private final Sound shootingSound = Gdx.audio.newSound(
			Gdx.files.internal("shoot.wav")
	);
	private final Sound tickSound = Gdx.audio.newSound(
			Gdx.files.internal("tick.wav")
	);
	private final Sound hurtSound = Gdx.audio.newSound(
			Gdx.files.internal("hurt.wav")
	);
	private final SpriteBatch spriteBatch = new SpriteBatch();
	private final BitmapFont nandakaFont = new BitmapFont(
			Gdx.files.internal("nandaka_western.fnt")
	);

	ShaderProgram shaderProgram;

	List<FightEnemy> fightEnemyList = new ArrayList<>();
	List<FightBullet> bullets = new ArrayList<>();
	private MapScreen mapScreen;

	public FightScreen(MapScreen mapScreen, long difficulty, long length) {
		this.mapScreen = mapScreen;
		this.length = length;
		shaderProgram = new ShaderProgram(
				Gdx.files.internal("shader_vertex.glsl"),
				Gdx.files.internal("shader_fragment_saturation.glsl")
		);

		for (int i = 0; i < difficulty; i++) {
			FightEnemy fightEnemy = new FightEnemy();
			if ( i == 0) {
				fightEnemy.yPos = 90;
			} else {
				fightEnemy.yPos = Constants.HEIGHT - 90;
			}
			fightEnemy.delay = MathUtils.random(50, 90);
			fightEnemy.speed = (float) MathUtils.random(1.3f, 3.6f);
			fightEnemyList.add(fightEnemy);
		}
		GameState.ammo -= clip;

	}

	boolean ignoreDamageAll = false;
	boolean hasShot = false;

	@Override
	public void show() {
		// Prepare your screen here.
	}

	float time = 0;

	float playerX = Constants.WIDTH / 2;
	float playerY = Constants.HEIGHT / 2;
	float cameraX = 0;

	int tileCountSand = 4;

	int tileCountRock = 6;

	int clip = Math.min(GameState.ammo, 6);

	float playerReload = 0;
	float timeOnRocks = 0;

	HumanAnim playerAnim = HumanAnim.DEFAULT;
	float animlength = 0;

	int tilePseudoRandom(int x, int y, int by) {

		return (5 * id + x * 17 + y * 3 + (int) (5 * Math.sin(19 * x - 61 * y + 3 * id)))%by;

	}

	int delayBufferSize = 100;

	float[] xBuffer = new float[delayBufferSize];
	int i = 0;
	float accX = 0;
	float accY = 0;
	boolean accXLock = false;
	boolean accYLock = false;

	float addMoneyIt = 0;

	boolean hasWon = false;
	boolean hasLost = false;

	float playerOnRocksTimerAnim = 0;

	float fadeToBlackAlpha = 1f;

	@Override
	public void render(float delta) {
		if (!hasWon) {
			fadeToBlackAlpha -= delta;
			if (fadeToBlackAlpha < 0) {
				fadeToBlackAlpha = 0;
			}
		} else {
			fadeToBlackAlpha += delta;
			if (fadeToBlackAlpha > 1) {
				fadeToBlackAlpha = 1;
			}
		}
		spriteBatch.setShader(shaderProgram);
		spriteBatch.begin();

		boolean drawTip = false;

		if (GameState.firstFight) {
			ignoreDamageAll = true;
			drawTip = true;
			if (Gdx.input.justTouched()) {

				ignoreDamageAll = false;
				GameState.firstFight = false;
			}
		}

		playerOnRocksTimerAnim -= delta;
		animlength -= delta;
		if (animlength < 0) {
			animlength = 0;
			playerAnim = HumanAnim.DEFAULT;
			hasShot = false;
		}
		addMoneyIt += delta;

		if (addMoneyIt > 0.05) {
			addMoneyIt = 0;
			if (GameState.realMoney > GameState.animMoney) {
				GameState.animMoney++;
			}
			if (GameState.realMoney < GameState.animMoney) {
				GameState.animMoney--;
			}
		}

		playerReload -= delta;
		if (clip == 0 && playerReload < 0) {
			clip = Math.min(GameState.ammo, 6);
			GameState.ammo -= clip;
		}

		if (!accXLock) {

			if (accX < 0) {
				accX += delta * 3;
				if (accX > 0) {
					accX = 0;
				}
			} else if (accX > 0) {
				accX -= delta * 3;
				if (accX < 0) {
					accX = 0;
				}
			}
		}
		if (!accYLock) {

			if (accY < 0) {
				accY += delta * 3;
				if (accY > 0) {
					accY = 0;
				}
			} else if (accY > 0) {
				accY -= delta * 3;
				if (accY < 0) {
					accY = 0;
				}
			}
		}
		accXLock = false;
		accYLock = false;


		xBuffer[i] = playerX;
		i++;
		i %= delayBufferSize;
		shaderProgram.setUniformf("saturation", (1.0f * GameState.herHp / Constants.HER_MAX_HP));
		time += delta;
		introTime += delta;

		if (!ignoreDamageAll) {
			cameraX += delta * 180 * 4;
		}

		int mouseX = Gdx.input.getX();
		int mouseY = Gdx.input.getY();

		// not intro

		Sprite[] sandSprites = new Sprite[tileCountSand];
		for (int i = 0; i < tileCountSand; i++) {
			sandSprites[i] = new Sprite(TextureMemory.getTexture("sand_tile_" + i + ".png"));
		}

		Sprite[] rockSprites = new Sprite[tileCountRock];
		for (int i = 0; i < tileCountRock; i++) {
			rockSprites[i] = new Sprite(TextureMemory.getTexture("rocks_" + i + ".png"));
		}

		long aliveEnemies = fightEnemyList.stream().filter(en -> en.isDead != true).count();

		if ((cameraX > length * sandSprites[0].getWidth() || aliveEnemies == 0) && !hasWon) {
			hasWon = true;
			ignoreDamageAll = true;
			GameState.lastVictoryType = aliveEnemies == 0 ? FightVictoryType.KILLED : FightVictoryType.REACHED_CITY;
			GameState.ammo += clip;
			victoryReasonFade = 0;
		}

		if (GameState.heroHp <= 0) {
			hasLost = true;
			ignoreDamageAll = true;
		}

		for (int i = 0; i < length + 50; i++) {
			for (int j = 0; j < 5; j++) {
				int id = tilePseudoRandom(i, j, tileCountSand);
				sandSprites[id].setPosition(i * sandSprites[id].getWidth() - cameraX, j * sandSprites[id].getHeight());
				sandSprites[id].draw(spriteBatch);
			}
		}

		for (int i = 0; i < length + 51; i++) {
			for (int j = 0; j < 6; j++) {
				int id = tilePseudoRandom(i, j, tileCountSand);
				float myWidth = sandSprites[id].getWidth();
				float myHeight =  sandSprites[id].getHeight();
				sandSprites[id].setPosition((i-0.5f) * myWidth - cameraX, (j - 0.5f) * myHeight);
				sandSprites[id].draw(spriteBatch, 0.5f);
			}
		}

		boolean playerOnRocks = false;


		for (int i = 10; i < length + 50; i++) {
			for (int j = 0; j <= 5; j++) {
				int checkTileByI = i % 2 == 0 ? 1 : -1;
				if (isGenTile(i, j) && !(isGenTile(i, j + checkTileByI))) {
					int id = tilePseudoRandom(i, j, tileCountRock);
					float rockX = i * rockSprites[id].getWidth() - cameraX;
					float rockY = j * rockSprites[id].getHeight() - 30;
					rockSprites[id].setPosition(rockX, j * rockSprites[id].getHeight() - 30);
					rockSprites[id].draw(spriteBatch);

					if (new Vector2(playerX - rockX - 90, playerY - Constants.flipY(rockY + 150)).len() < 92) {
						playerOnRocks = true;
					}
				}
			}
		}

		if (playerOnRocks && !ignoreDamageAll) {
			playerOnRocksTimerAnim = 1;
			timeOnRocks += delta;
			if (timeOnRocks > 0.7) {
				GameState.heroHp--;
				timeOnRocks = 0;
			}
		}


		boolean movingFast = false;

		if (!ignoreDamageAll) {

			if (Gdx.input.isKeyPressed(Input.Keys.S) && !Gdx.input.isKeyPressed(Input.Keys.W)) {
				accYLock = true;
				accY += delta * 4;
				if (accY > 2) {
					accY = 2;
				}
			}
			if (Gdx.input.isKeyPressed(Input.Keys.W) && !Gdx.input.isKeyPressed(Input.Keys.S)) {
				accYLock = true;
				accY -= delta * 4;
				if (accY < -2) {
					accY = -2;
				}
			}
			if (Gdx.input.isKeyPressed(Input.Keys.D) && !Gdx.input.isKeyPressed(Input.Keys.A)) {
				accXLock = true;
				accX += delta * 10;
				if (accX > 2) {
					accX = 2;
				}
			}
			if (Gdx.input.isKeyPressed(Input.Keys.A) && !Gdx.input.isKeyPressed(Input.Keys.D)) {
				accXLock = true;
				accX -= delta * 10;
				if (accX < -2) {
					accX = -2;
				}
			}
			if (new Vector2(accX, accY).len() > 0.8) {
				movingFast = true;
			}

			float playerXBefore = playerX;
			float playerYBefore = playerY;
			if (accXLock) {
				playerX += accX;
			}
			if (accYLock) {
				playerY += accY;
			}
			playerX += accX;
			playerY += accY;

			if (playerX < 180 || playerX > Constants.WIDTH - 180)  {
				playerX = playerXBefore;
			}
			if (playerY < 180 || playerY > Constants.HEIGHT - 180) {
				playerY = playerYBefore;
			}

			for (FightEnemy fightEnemy : fightEnemyList) {
				if (fightEnemy.isDead) {
					fightEnemy.xPos -= 3;
				} else {
					int readFrame = (i - fightEnemy.delay + delayBufferSize) % delayBufferSize;
					if (fightEnemy.xPos < xBuffer[readFrame]) {
						fightEnemy.xPos += fightEnemy.speed;
						if (fightEnemy.xPos >  xBuffer[readFrame]) {
							fightEnemy.xPos =  xBuffer[readFrame];
						}
					}
					if (fightEnemy.xPos >  xBuffer[readFrame]) {
						fightEnemy.xPos -= fightEnemy.speed;
						if (fightEnemy.xPos <  xBuffer[readFrame]) {
							fightEnemy.xPos =  xBuffer[readFrame];
						}
					}
				}

			}
		}




		float movingFastAnimMultiplier = movingFast ? 6 : 1.5f;
		float movingFastAnimMultiplier2 = movingFast ? 1.4f : 1f;

		int horseId = 1 + (int) ( ((5 * time) % 4));

		Sprite horse = new Sprite(TextureMemory.getTexture("horsey_" + horseId + ".png"));
		horse.setSize(130 * 1.5f, 108 * 1.5f);
		Sprite enemyHorse = new Sprite(TextureMemory.getTexture("en_horsey_" + horseId + ".png"));
		enemyHorse.setSize(130 * 1.5f, 108 * 1.5f);

		horse.setPosition(playerX - horse.getWidth() /2f, Constants.flipY(horse.getHeight()/2f + playerY + (float) (movingFastAnimMultiplier * 2 * Math.sin(movingFastAnimMultiplier2 * 8 * time))));
		horse.draw(spriteBatch);

		Sprite heroSprite = new Sprite(TextureMemory.getTexture(playerAnim.getTextureName()));
		heroSprite.setSize(130 * 1.5f, 108 * 1.5f);
		Sprite enemySprite = new Sprite(TextureMemory.getTexture("hero_side.png"));
		enemySprite.setSize(130 * 1.5f, 108 * 1.5f);
		heroSprite.setPosition(playerX - heroSprite.getWidth()/2f, Constants.flipY(horse.getHeight()/2 +playerY + (float) (movingFastAnimMultiplier * 3 * Math.sin(movingFastAnimMultiplier2 * 8 * time))));
		heroSprite.draw(spriteBatch);

		for (FightEnemy fightEnemy : fightEnemyList) {
			fightEnemy.invincibility -= delta;
			fightEnemy.animLength -= delta;
			if (fightEnemy.animLength < 0) {
				fightEnemy.animLength = 0;
				fightEnemy.humanAnim = HumanAnim.DEFAULT;
			}
			enemySprite = new Sprite(TextureMemory.getTexture(fightEnemy.humanAnim.getTextureNameEnemy()));
			enemySprite.setSize(130 * 1.5f, 108 * 1.5f);

			enemyHorse.setPosition(fightEnemy.xPos - horse.getWidth() /2f, Constants.flipY(horse.getHeight()/2f + fightEnemy.yPos + (float) (1.5f * 2 * Math.sin(1 * 8 * time))));
			enemyHorse.draw(spriteBatch);
			if (!fightEnemy.isDead) {
				enemySprite.setPosition(fightEnemy.xPos - heroSprite.getWidth()/2f, Constants.flipY(horse.getHeight()/2 +fightEnemy.yPos + (float) (1.5f * 3 * Math.sin(1 * 8 * time))));
				if (fightEnemy.invincibility > 0) {
					enemySprite.draw(spriteBatch, 0.5f + 0.5f * (float) Math.cos(15 * time));
				} else {
					enemySprite.draw(spriteBatch);
				}
			} else {
				enemySprite.rotate(90);
				enemySprite.setPosition(fightEnemy.deadCameraPos - heroSprite.getWidth()/2f - cameraX, Constants.flipY(horse.getHeight()/2 + fightEnemy.yPos - 20));
				enemySprite.draw(spriteBatch);
				enemySprite.rotate(0);
			}

			if (!fightEnemy.isDead && !ignoreDamageAll) {

				if (Math.abs(playerX - fightEnemy.xPos) < 600) {
					if (fightEnemy.ammoInClip > 0 && Math.random() > 0.98) {
						if (fightEnemy.fireCoolDown < 0) {
							fightEnemy.fireCoolDown = fightEnemy.shootingSpeed;
							int baseBulletSpeed = 500;

							float fixedEnemyX = fightEnemy.xPos;
							float fixedEnemyY = fightEnemy.yPos;

							float xDistance = playerX - fixedEnemyX;
							float yDistance = playerY - fixedEnemyY;

							xDistance *= 2.5f;

							float divBy = new Vector2(
									xDistance,
									yDistance
							).len();

							float xSpeed = xDistance / divBy * baseBulletSpeed;
							float ySpeed = yDistance / divBy * baseBulletSpeed;

							bullets.add(
									new FightBullet(false, fixedEnemyX, fixedEnemyY, xSpeed, ySpeed)
							);

							shootingSound.play();
							if (ySpeed < 0) {
								fightEnemy.humanAnim = HumanAnim.SHOOT_TOP;
								fightEnemy.animLength = 1;
							} else {
								fightEnemy.humanAnim = HumanAnim.SHOOT_BOTTOM;
								fightEnemy.animLength = 1;
							}
						}
					}
				}

				if (fightEnemy.ammoInClip == 0) {
					fightEnemy.reloadCoolDown -= delta;
				}
				if (fightEnemy.reloadCoolDown <= 0) {
					fightEnemy.reloadCoolDown = FightEnemy.RELOAD_COOLDOWN;
					fightEnemy.ammoInClip = FightEnemy.AMMO_IN_CLIP;
				}
				fightEnemy.fireCoolDown -= delta;
			}

			if (fightEnemy.humanAnim != HumanAnim.DEFAULT && fightEnemy.animLength > 0.5) {
				Sprite gunFlash = new Sprite(TextureMemory.getTexture("gunflash.png"));

				gunFlash.setSize(30, 30);

				if (fightEnemy.humanAnim == HumanAnim.SHOOT_TOP) {

					gunFlash.setPosition(
							enemySprite.getX() + 85, enemySprite.getY() + 110
					);
				} else {

					gunFlash.setPosition(
							enemySprite.getX() + 80, enemySprite.getY() + 70
					);
				}

				gunFlash.setAlpha(fightEnemy.animLength);
				gunFlash.rotate(time * 70);
				gunFlash.draw(spriteBatch);
			}

		}

		if (Gdx.input.justTouched() && !ignoreDamageAll && !drawTip) {
			float yDistance = mouseY - playerY;
			if (clip > 0) {


				int baseBulletSpeed = 500;


				float xDistance = mouseX - playerX;

				float divBy = new Vector2(
						xDistance,
						yDistance
				).len();

				float xSpeed = xDistance / divBy * baseBulletSpeed;
				float ySpeed = yDistance / divBy * baseBulletSpeed;

				bullets.add(
						new FightBullet(true, playerX, playerY, xSpeed, ySpeed)
				);

				shootingSound.play();
				GameState.bulletsFired ++;

				clip--;
				if (clip == 0) {
					playerReload = Math.min(GameState.ammo, 6)/6f;
				}
				hasShot = true;

			} else {
				tickSound.play();
				hasShot = false;
			}

			animlength = 1;
			if (yDistance < 0) {
				playerAnim = HumanAnim.SHOOT_TOP;
			} else {
				playerAnim = HumanAnim.SHOOT_BOTTOM;
			}
		}


		Sprite gunFlash = new Sprite(TextureMemory.getTexture("gunflash.png"));

		gunFlash.setSize(30, 30);

		if (playerAnim == HumanAnim.SHOOT_TOP) {
			gunFlash.setPosition(
					heroSprite.getX() + 85, heroSprite.getY() + 110
			);
		} else {

			gunFlash.setPosition(
					heroSprite.getX() + 80, heroSprite.getY() + 70
			);
		}
		gunFlash.setAlpha(animlength);
		gunFlash.rotate(time * 70);
		if (hasShot) {

			gunFlash.draw(spriteBatch);
		}

		Sprite bulletSprite = new Sprite(
				TextureMemory.getTexture("bullet.png")
		);

		bulletSprite.setSize(15, 15);

		List<FightBullet> bulletsToRemove = new ArrayList<>();

		for (FightBullet bullet : bullets) {
			bulletSprite.setPosition(bullet.x - bulletSprite.getWidth(),
					Constants.flipY(bullet.y - bulletSprite.getHeight())
					);
			bulletSprite.draw(spriteBatch);

			if (bullet.isFriendly) {
				for (FightEnemy fightEnemy : fightEnemyList) {
					float distance = new Vector2(
							fightEnemy.xPos - bullet.x,
							fightEnemy.yPos - bullet.y
					).len();

					if (distance < 18 && !ignoreDamageAll && fightEnemy.invincibility <= 0) {
						fightEnemy.hp--;
						bulletsToRemove.add(bullet);
						hurtSound.play();
						if (fightEnemy.hp == 0) {
							fightEnemy.isDead = true;
							fightEnemy.deadCameraPos = cameraX + fightEnemy.xPos;
							GameState.realMoney += 30;
							GameState.earned += 30;
							GameState.enemiesKilled++;
						}
						fightEnemy.invincibility = 1;
					}
				}
			} else {
				float distance = new Vector2(
						playerX - bullet.x,
						playerY - bullet.y
				).len();

				if (distance < 15 && !ignoreDamageAll) {
					GameState.heroHp--;
					bulletsToRemove.add(bullet);
					hurtSound.play();
				}
			}


			if (bullet.y < -300 || bullet.y > 2100) {
				bulletsToRemove.add(bullet);
			} else {
				bullet.y += delta * bullet.ySpeed;
				bullet.x += delta * bullet.xSpeed;
			}


		}

		bullets.removeAll(bulletsToRemove);

		//pre-UI

		if (playerOnRocksTimerAnim > 0) {
			Sprite rocks = new Sprite(TextureMemory.getTexture("rocks_ov.png"));

			rocks.setAlpha(
					playerOnRocksTimerAnim
			);
			rocks.draw(spriteBatch);
		}

		if (GameState.heroHp < Constants.HERO_MAX_HP) {
			Sprite blood = new Sprite(TextureMemory.getTexture("bloody_screen.png"));
			float percentLostHealth = ( (1.0f * Constants.HERO_MAX_HP - GameState.heroHp) / Constants.HERO_MAX_HP);

			blood.setAlpha(
					((float) Math.cos(time * 4 + 5 * time * percentLostHealth) * 0.5f + 0.5f)
					*
							percentLostHealth
			);
			blood.draw(spriteBatch);
		}

		// UI

		Sprite heartSprite = new Sprite(TextureMemory.getTexture("heart.png"));
		heartSprite.setScale(4);

		Sprite heroHeadUi = new Sprite(TextureMemory.getTexture("hero_with_clothes.png"));
		heroHeadUi.setScale(6);
		heroHeadUi.setPosition(50, Constants.flipY(50));
		heroHeadUi.draw(spriteBatch);

		for (int i = 0; i < GameState.heroHp; i++) {
			heartSprite.setPosition(120 + i * 60, Constants.flipY(50));
			heartSprite.draw(spriteBatch);
		}


		Sprite moneyUi = new Sprite(TextureMemory.getTexture("money.png"));
		moneyUi.setScale(6);
		moneyUi.setPosition(50, Constants.flipY(150));
		moneyUi.draw(spriteBatch);

		nandakaFont.draw(spriteBatch, GameState.animMoney + "", 100, Constants.flipY(100));


		Sprite clipUi = new Sprite(TextureMemory.getTexture("clip_" + clip + ".png"));
		clipUi.setScale(6);
		clipUi.setPosition(50, Constants.flipY(250));
		clipUi.draw(spriteBatch);

		nandakaFont.draw(spriteBatch, GameState.ammo + "", 100, Constants.flipY(200));


		//intro

		if (drawTip) {
			Sprite tip = new Sprite(TextureMemory.getTexture("tip1_fight.png"));
			tip.draw(spriteBatch);
		}

		if (hasLost) {
			defeatFade += delta;
			if (defeatFade > 1) {
				defeatFade = 1;
			}
			Sprite black = new Sprite(TextureMemory.getTexture("black.png"));
			black.draw(spriteBatch, defeatFade);
		}

		Sprite black = new Sprite(TextureMemory.getTexture("black.png"));
		black.draw(spriteBatch, fadeToBlackAlpha);


		if (hasWon) {
			Sprite wonType;
			switch (GameState.lastVictoryType) {
				case KILLED:
					wonType = new Sprite(TextureMemory.getTexture("banditsKilled.png"));
					break;
				case REACHED_CITY:
					wonType = new Sprite(TextureMemory.getTexture("youveEscaped.png"));
					break;
				default:
					throw new RuntimeException();
			}

			wonType.draw(spriteBatch, fadeToBlackAlpha);
			victoryReasonFade += delta;
		}

		spriteBatch.end();

		if (hasWon && fadeToBlackAlpha >= 1 && victoryReasonFade >= 2) {
			Core.nextScreen = mapScreen;
		}

		if (hasLost && defeatFade >= 1) {
			mapScreen.dispose();
			Core.nextScreen = new DefeatScreen(true);
		}
	}

	float victoryReasonFade = 2;
	float defeatFade = 0;

	private boolean isGenTile(int i, int j) {
		return tilePseudoRandom(i, j, 50) % 10 < 7;
	}

	@Override
	public void resize(int width, int height) {
		// Resize your screen here. The parameters represent the new window size.
	}

	@Override
	public void pause() {
		// Invoked when your application is paused.
	}

	@Override
	public void resume() {
		// Invoked when your application is resumed after pause.
	}

	@Override
	public void hide() {
		// This method is called when another screen replaces this one.
	}

	@Override
	public void dispose() {
		spriteBatch.dispose();
		shaderProgram.dispose();
		// Destroy screen's assets here.
		hurtSound.dispose();
		shootingSound.dispose();
		tickSound.dispose();
	}
}