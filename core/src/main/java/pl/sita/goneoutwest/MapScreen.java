package pl.sita.goneoutwest;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MapScreen implements Screen {

public boolean currentRouteFought = false;

	float introTime = 0;
	private final SpriteBatch spriteBatch = new SpriteBatch();
	private final ShapeRenderer shapeRenderer = new ShapeRenderer();
	private final BitmapFont nandakaFont = new BitmapFont(
			Gdx.files.internal("nandaka_western.fnt")
	);
	private final BitmapFont nandakaSmallFont = new BitmapFont(
			Gdx.files.internal("nandaka_western_s.fnt")
	);
	private final BitmapFont nandakaMidFont = new BitmapFont(
			Gdx.files.internal("nandaka_western_m.fnt")
	);

	private final Sound reject = Gdx.audio.newSound(
			Gdx.files.internal("reject.wav")
	);
	private final Sound success = Gdx.audio.newSound(
			Gdx.files.internal("success.wav")
	);

	private final MapHero mapHero;


	private List<Town> towns = new ArrayList<>();
	private List<Connection> connections = new ArrayList<>();

	private List<OnClick> onClicks = new ArrayList<>();

	int timeOfTheDayAnim = 2;
	int timeOfTheDayAnimTarget = 2;
	int dayTimeBehindUs = 0;

	ShaderProgram shaderProgram;

	float fadeToBlackAlpha = 0;

	public MapScreen() {
		shaderProgram = new ShaderProgram(
				Gdx.files.internal("shader_vertex.glsl"),
				Gdx.files.internal("shader_fragment_saturation.glsl")
		);


		Town amCicano = new Town(275, 764, "Am Cicano", false, false, false, ColorType.WHITE, 4);
		Town batory = new Town(50, 537, "Batory", false, false, false, ColorType.DARKBLUE, 1);
		Town codysTown = new Town(485, 533, "Cody's Town", false, false, false, ColorType.LIGHT_BLUE, 1);
		Town desperado = new Town(239, 328, "Desperado", true, false, false, ColorType.GREEN, 4);
		Town eaglesRock = new Town(494, 225, "Eagle's Rock", false, true, false, ColorType.RED, 3);
		Town fanSirado = new Town(691, 409, "Fan Sirado", false, false, false, ColorType.DARKBLUE, 2);
		Town gravy = new Town(972, 345, "Gravy", false, false, false, ColorType.YELLOW, 3);
		Town holster = new Town(884, 200, "Holster", false, false, false, ColorType.RED, 2);

		Town ivy = new Town(846, 601, "Ivy", false, false, false, ColorType.YELLOW, 1);
		Town joshua = new Town(1051, 464, "Joshua", false, false, false, ColorType.LIGHT_BLUE, 1);
		Town keybreak = new Town(1322, 565, "Keybreak", false, false, false,  ColorType.RED, 1);
		Town losDuos = new Town(1226, 357, "Los Duos", false, true, false, ColorType.YELLOW, 2);
		Town morningsEnd = new Town(1165, 781, "Morning's End", true, false, false, ColorType.PURPLE, 4);
		Town nightsDawn = new Town(1500, 469, "Night's Dawn", false, false, false, ColorType.BROWN, 4);
		Town oatmeal = new Town(1111, 131, "Oatmeal", false, false, false, ColorType.DARKBLUE, 3);
		Town skyValley = new Town(1366, 179, "Sky Valley", false, false, false, ColorType.LIGHT_BLUE, 3);

		towns.add(amCicano);
		towns.add(batory);
		towns.add(codysTown);
		towns.add(desperado);
		towns.add(eaglesRock);
		towns.add(fanSirado);
		towns.add(gravy);
		towns.add(holster);

		towns.add(ivy);
		towns.add(joshua);
		towns.add(keybreak);
		towns.add(losDuos);
		towns.add(morningsEnd);
		towns.add(nightsDawn);
		towns.add(oatmeal);
		towns.add(skyValley);

		connections.add(new Connection(amCicano, batory, 999));
		connections.add(new Connection(amCicano, codysTown, 5));
		connections.add(new Connection(batory, codysTown, 5));
		connections.add(new Connection(batory, desperado, 5));
		connections.add(new Connection(desperado, eaglesRock, 5));
		connections.add(new Connection(desperado, codysTown, 5));
		connections.add(new Connection(fanSirado, codysTown, 5));
		connections.add(new Connection(fanSirado, eaglesRock, 20));
		connections.add(new Connection(holster, eaglesRock, 5));
		connections.add(new Connection(fanSirado, gravy, 25));
		connections.add(new Connection(holster, gravy, 5));


		connections.add(new Connection(ivy, joshua, 5));
		connections.add(new Connection(ivy, morningsEnd, -25));
		connections.add(new Connection(morningsEnd, keybreak, -25));
		connections.add(new Connection(keybreak, joshua, 5));
		connections.add(new Connection(joshua, losDuos, 5));
		connections.add(new Connection(losDuos, nightsDawn, 15));
		connections.add(new Connection(nightsDawn, skyValley, 15));
		connections.add(new Connection(losDuos, oatmeal, 5));
		connections.add(new Connection(losDuos, skyValley, 10));
		connections.add(new Connection(skyValley, oatmeal, 10));

		connections.add(new Connection(fanSirado, ivy, 5));
		connections.add(new Connection(holster, oatmeal, 15));

		mapHero = new MapHero(morningsEnd);
	}

	@Override
	public void show() {
		// Prepare your screen here.
	}

	boolean isPlayingIntro = Constants.PLAY_INTRO;

	int introStep = 0;

	float addMoneyIt = 0;

	float lastHerHpScale = 1;
	float time = 0;

	boolean defeatFade = false;
	float defeatFadeProgress = 0;

	@Override
	public void render(float delta) {
		GameState.timeSurvived = dayTimeBehindUs + GameState.herHpCounter + (GameState.herHp - 1) * Constants.HER_HP_COUNTER;

		if (GameState.herHp == 0) {
			defeatFade = true;
		}

		if (defeatFade) {
			defeatFadeProgress += delta;
		}

		float saturation = (1.0f * GameState.herHp / Constants.HER_MAX_HP);
		shaderProgram.setUniformf("saturation", saturation);
		addMoneyIt += delta;
		time += delta;

		if (addMoneyIt > 0.05) {
			addMoneyIt = 0;
			if (GameState.realMoney > GameState.animMoney) {
				GameState.animMoney++;
			}
			if (GameState.realMoney < GameState.animMoney) {
				GameState.animMoney--;
			}
		}

		if (GameState.herHp < GameState.herAnimHp) {
			lastHerHpScale = lastHerHpScale * 0.9f;
			if (lastHerHpScale < 0.3f) {
				GameState.herAnimHp--;
			}
		}
		if (GameState.herHp == GameState.herAnimHp) {
			lastHerHpScale = 1;
		}
		if (GameState.herHp > GameState.herAnimHp) {
			GameState.herAnimHp = GameState.herHp;
			lastHerHpScale = 1;
		}

		if (!isPlayingIntro) {
			introStep = -1000;
		}

		int x = Gdx.input.getX();
		int y = Gdx.input.getY();

		boolean hoveringOverTown = false;

		for (Town town : towns) {
			Circle townCircle = new Circle(town.getX(), town.getY(), Constants.TOWN_RADIUS + town.getTownSize() * 10);

			if (townCircle.contains(x, y)) {
				hoveringOverTown = true;
			}
		}

		onClicks.clear();

		introTime += delta;

		spriteBatch.setShader(shaderProgram);
		spriteBatch.begin();
		// not intro


		String textName = String.format("%04d", timeOfTheDayAnim);

		Sprite prevSprite = new Sprite(TextureMemory.getTexture(textName + ".png"));
		prevSprite.draw(spriteBatch);

		spriteBatch.end();
		spriteBatch.begin();

		String textName2 = String.format("%04d", timeOfTheDayAnimTarget);

		Sprite mapSprite = new Sprite(TextureMemory.getTexture(textName2 + ".png"));
		mapSprite.setAlpha(mapHero.getProgress());
		mapSprite.draw(spriteBatch, mapHero.getProgress());

		spriteBatch.end();
		spriteBatch.setShader(shaderProgram);

		shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

		Gdx.gl.glEnable(GL30.GL_BLEND);
		Gdx.gl.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);

		if (hoveringOverTown) {
			for (Town town : towns) {
				Gdx.gl.glLineWidth(4);
				shapeRenderer.setColor(0, 0, 0, 0.5f);
				shapeRenderer.circle(town.getX(), Constants.flipY(town.getY()), Constants.TOWN_RADIUS + town.getTownSize() * 10);

			}
		}

		shapeRenderer.flush();


		if (hoveringOverTown) {
			Gdx.gl.glLineWidth(12);
			for (Connection connection : connections) {
				shapeRenderer.setColor(0, 0, 0, 0.5f);
				shapeRenderer.line(
						connection.getTown1().getX(),
						Constants.flipY(connection.getTown1().getY()),
						connection.getTown2().getX(),
						Constants.flipY(connection.getTown2().getY())
				);

			}
		}
		shapeRenderer.flush();

		Set<Town> adjacentTowns = new HashSet<>();

		for (Connection connection : connections) {
			if (connection.contains(mapHero.getCurrentTown())) {
				adjacentTowns.add(connection.getTown1());
				adjacentTowns.add(connection.getTown2());
			}
		}

		shapeRenderer.end();
		shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

		Gdx.gl.glLineWidth(4);
		for (Connection connection : connections) {
			for (Town town : adjacentTowns) {
				if (connection.contains(town)) {

					float alpha = 0.6f;

					if (connection.contains(mapHero.getCurrentTown())) {
						alpha = 1;
					}
					
					float connectionDanger = connection.calcDanger(dayTimeBehindUs, timeOfTheDayAnim);

					ConnectionDanger enConnDang = getConnectionDanger(connectionDanger);


					switch (enConnDang) {
						case SAFE:
							Color color = new Color(0, 1, 0, alpha);
							Color desat = new Color(.33f, .33f, .33f, alpha);
							shapeRenderer.setColor(color.lerp(desat, 1 - saturation));
							break;
						case MEDIUM:
							Color color2 = new Color(1, 1, 0, alpha);
							Color desat2 = new Color(.66f, .66f, .66f, alpha);
							shapeRenderer.setColor(color2.lerp(desat2, 1 - saturation));
							break;
						case HARD:
							Color color3 = new Color(1, 0, 0, alpha);
							Color desat3 = new Color(.33f, .33f, .33f, alpha);
							shapeRenderer.setColor(color3.lerp(desat3, 1 - saturation));
							break;
					}

					shapeRenderer.line(
							connection.getTown1().getX(),
							Constants.flipY(connection.getTown1().getY()),
							connection.getTown2().getX(),
							Constants.flipY(connection.getTown2().getY())
					);
				}
			}

		}


		shapeRenderer.end();
		Gdx.gl.glDisable(GL30.GL_BLEND);
		spriteBatch.begin();

		for (Town town : towns) {
			float radius = (Constants.TOWN_RADIUS + town.getTownSize() * 10);
			float size = 2 * radius;

			nandakaSmallFont.draw(spriteBatch, town.getName() + ", " + town.getLetterCount(), town.getX() -
					town.getName().length() * 5, Constants.flipY(town.getY() + radius));

			if (hoveringOverTown) {
				Sprite townColorSprite = new Sprite(TextureMemory.getTexture(town.getTownColor().getTownFileName()));
				townColorSprite.setSize(size, size);
				townColorSprite.setPosition(town.getX() - size/2, Constants.flipY(town.getY() + size/2));
				townColorSprite.draw(spriteBatch);
			}
		}

		Sprite heroSprite = new Sprite(TextureMemory.getTexture("hero.png"));
		heroSprite.setScale(8);
		heroSprite.setPosition(
				mapHero.getX(),
				Constants.flipY(mapHero.getY())
		);
		heroSprite.draw(spriteBatch);

		if (!mapHero.isOnTheMove()) {


			Sprite postOffice = new Sprite(TextureMemory.getTexture("postoffice.png"));

			postOffice.setSize(200, 200);

			postOffice.setPosition(Constants.WIDTH / 2 - postOffice.getWidth() / 2 + 100, Constants.flipY(Constants.HEIGHT));

			postOffice.draw(spriteBatch);


			Sprite satchelSprite = new Sprite(TextureMemory.getTexture("satchel.png"));

			satchelSprite.setSize(200, 200);

			satchelSprite.setPosition(Constants.WIDTH / 2 - satchelSprite.getWidth() / 2 - 100 - 58, Constants.flipY(Constants.HEIGHT));

			satchelSprite.draw(spriteBatch);

			List<ColorType> lettersInTown = mapHero.getCurrentTown().getLetters();
			int i = 0;
			for (ColorType letterColor : lettersInTown) {
				Sprite sprite = new Sprite(TextureMemory.getTexture(letterColor.getLetterFileName()));

				sprite.setSize(
						120,
						90
				);

				sprite.setPosition(Constants.WIDTH / 2 - sprite.getWidth() / 2 + i + 100 + 35, Constants.flipY(Constants.HEIGHT - sprite.getWidth()/2));

				sprite.draw(spriteBatch);

				int cost = DistanceCalculator.letterWorth(mapHero.getCurrentTown(), connections, letterColor);

				nandakaSmallFont.draw(spriteBatch, cost + "", sprite.getX() + sprite.getWidth() - 8, sprite.getY() + sprite.getHeight() - 8);

				Rectangle rectangle = new Rectangle(
						sprite.getX(), Constants.flipY(sprite.getY() + sprite.getHeight()), sprite.getWidth(), sprite.getHeight()
				);

				onClicks.add(new OnClick() {
					@Override
					public Rectangle getRectangle() {
						return rectangle;
					}

					@Override
					public void onClick() {
						if (mapHero.getLetters().size() < 6) {
							lettersInTown.remove(letterColor);
							mapHero.getLetters().add(new Letter(cost, letterColor));
						}
					}
				});

				i -= 45;
			}


			List<Letter> playerLetters = mapHero.getLetters();

			int letterI = 0;
			int letterJ = 0;
			for (Letter letter : playerLetters) {

				Sprite letterSprite = new Sprite(TextureMemory.getTexture(letter.getLetterColor().getLetterFileName()));

				letterSprite.setSize(
						120,
						90
				);

				letterSprite.setPosition(Constants.WIDTH / 2 - letterSprite.getWidth() / 2 + letterI - 100 + 25, Constants.flipY(45 - letterJ + Constants.HEIGHT - letterSprite.getWidth()/2));

				letterSprite.draw(spriteBatch);

				nandakaSmallFont.draw(spriteBatch, letter.getCost() + "", letterSprite.getX() + letterSprite.getWidth() - 11, letterSprite.getY() + letterSprite.getHeight() - 8);

				letterI -= 45;
				if (letterI < -91) {
					letterI = 0;
					letterJ = 90;
				}
			}


			Sprite satchelSpriteTop = new Sprite(TextureMemory.getTexture("satchel_top.png"));

			satchelSpriteTop.setSize(200, 200);

			satchelSpriteTop.setPosition(Constants.WIDTH / 2 - satchelSpriteTop.getWidth() / 2 - 100 - 58, Constants.flipY(Constants.HEIGHT));

			satchelSpriteTop.draw(spriteBatch);

		}

		if (mapHero.getCurrentTown() != mapHero.getTargetTown()) {
			if (mapHero.getProgress() < 1) {
				mapHero.setProgress(mapHero.getProgress() + delta * Constants.TRAVELING_MAP_SPEED / mapHero.getTownDistance() * 333);
				Connection connection = getConnection(mapHero.getCurrentTown(), mapHero.getTargetTown());
				float connectionDanger = connection.calcDanger(dayTimeBehindUs, timeOfTheDayAnim);

				ConnectionDanger enConnDang = getConnectionDanger(connectionDanger);

				if (enConnDang != ConnectionDanger.SAFE) {
					fadeToBlackAlpha = (1 - Math.abs(0.5f - mapHero.getProgress()) * 2);
				}


				if (mapHero.getProgress() > 0.5 && !currentRouteFought) {
					switch (enConnDang) {
						case SAFE:
							currentRouteFought = true;
							break;
						case MEDIUM:
							Core.nextScreen = new FightScreen(this, 1, 100);
							Core.disposeThis = false;
							currentRouteFought = true;
							break;
						case HARD:
							Core.nextScreen = new FightScreen(this, 2, 250);
							Core.disposeThis = false;
							currentRouteFought = true;
							break;
					}
				}
			} else {
				mapHero.setProgress(1);
				GameState.herHpCounter--;
				if (GameState.herHpCounter < 0) {
					GameState.herHp--;
					GameState.herHpCounter = Constants.HER_HP_COUNTER;
				}
				mapHero.setCurrentTown(mapHero.getTargetTown());

				if (isMorningEnd(mapHero.getCurrentTown())) {
					GameState.visited++;
				}

				timeOfTheDayAnim++;
				dayTimeBehindUs++;
				if (timeOfTheDayAnim > 16) {
					timeOfTheDayAnim = 2;
				}
			}
		}

		//workaround
		if (mapHero.getCurrentTown() == mapHero.getTargetTown()) {
			mapHero.setProgress(1);
		}

		if (mapHero.isOnTheMove()) { //todo or skip day
			for (Town town : towns) {
				town.update(delta * Constants.TRAVELING_MAP_SPEED / 0.4f);
			}
		}

		for (Town town : towns) {
			town.updateAnim(delta);
		}

		//pre-UI

		if (GameState.heroHp < Constants.HERO_MAX_HP) {
			Sprite blood = new Sprite(TextureMemory.getTexture("bloody_screen.png"));
			float percentLostHealth = ( (1.0f * Constants.HERO_MAX_HP - GameState.heroHp) / Constants.HERO_MAX_HP);

			blood.setAlpha(
					((float) Math.cos(time * 1 + 1 * time * percentLostHealth) * 0.5f + 0.5f)
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
		heartSprite.setScale(4);

		Sprite herHeadUi = new Sprite(TextureMemory.getTexture("her.png"));
		herHeadUi.setScale(6);
		herHeadUi.setPosition(50, Constants.flipY(150));
		herHeadUi.draw(spriteBatch);

		for (int i = 0; i < GameState.herAnimHp; i++) {
			heartSprite.setPosition(120 + i * 60, Constants.flipY(150));
			if (i + 1 == GameState.herAnimHp) {
				heartSprite.setScale(lastHerHpScale * 4);
			}
			heartSprite.draw(spriteBatch);
		}

		Sprite moneyUi = new Sprite(TextureMemory.getTexture("money.png"));
		moneyUi.setScale(6);
		moneyUi.setPosition(50, Constants.flipY(250));
		moneyUi.draw(spriteBatch);

		nandakaFont.draw(spriteBatch, GameState.animMoney + "", 100, Constants.flipY(200));

		Sprite clipUi = new Sprite(TextureMemory.getTexture("clip_" + 6 + ".png"));
		clipUi.setScale(6);
		clipUi.setPosition(50, Constants.flipY(350));
		clipUi.draw(spriteBatch);

		nandakaFont.draw(spriteBatch, GameState.ammo + "", 100, Constants.flipY(300));

		for (Letter letter : new ArrayList<>(mapHero.getLetters())) {
			if (mapHero.getCurrentTown().getTownColor() == letter.getLetterColor()) {
				mapHero.getLetters().remove(letter);
				GameState.realMoney += letter.getCost();
				GameState.earned += letter.getCost();
				GameState.lettersDelivered++;
			}
		}

		if (!mapHero.isOnTheMove()) {
			Town town = mapHero.getCurrentTown();

			int options = 0;

			if (town.isHasHospital()) {
				int costSelf = (Constants.HERO_MAX_HP - GameState.heroHp) * 5;
				drawButton(options++, "Fully Heal Self - " + costSelf, () -> {
					if (GameState.realMoney >= costSelf) {
						GameState.realMoney -= costSelf;
						GameState.heroHp = Constants.HERO_MAX_HP;
						success.play();
					} else {
						reject.play();
					}
				});
				if (GameState.heroHp < Constants.HERO_MAX_HP) {
					drawButton(options++, "Heal Self - " + 5, () -> {
						if (GameState.realMoney >= 5) {
							GameState.realMoney -= 5;
							GameState.heroHp += 1;
							success.play();
						} else {
							reject.play();
						}
					});
				}
				boolean isMorningEnd = isMorningEnd(town);
				int baseHealingCostHer = (10 + dayTimeBehindUs / 10);
				if (isMorningEnd) {
					int costHer = (Constants.HER_MAX_HP - GameState.herHp) * baseHealingCostHer;
					drawButton(options++, "Fully Heal Her - " + costHer, () -> {
						if (GameState.realMoney >= costHer) {
							GameState.realMoney -= costHer;
							GameState.herHp = Constants.HER_MAX_HP;
							success.play();
						} else {
							reject.play();
						}
					});
					if (GameState.herHp < Constants.HER_MAX_HP) {
						drawButton(options++, "Heal Her - " + baseHealingCostHer, () -> {
							if (GameState.realMoney >= baseHealingCostHer) {
								GameState.realMoney -= baseHealingCostHer;
								GameState.herHp += 1;
								success.play();
							} else {
								reject.play();
							}
						});
					}
				} else {
					int costHer = 5 + (Constants.HER_MAX_HP - GameState.herHp) * baseHealingCostHer;
					if (GameState.herHp < Constants.HER_MAX_HP) {
						drawButton(options++, "Send Meds To Her - " + costHer, () -> {
							if (GameState.realMoney >= costHer) {
								GameState.realMoney -= costHer;
								GameState.herHp += 1;
								success.play();
							} else {
								reject.play();
							}
						});
					}
				}
			}

			if (town.isSellsAmmo()) {
				int costAmmoFull = (Constants.MAX_AMMO - GameState.ammo) * 1;
				drawButton(options++, "Buy Max Ammo - " + costAmmoFull, () -> {
					if (GameState.realMoney >= costAmmoFull) {
						GameState.realMoney -= costAmmoFull;
						GameState.ammo = Constants.MAX_AMMO;
						success.play();
					} else {
						reject.play();
					}
				});
				int costAmmo = 1;
				drawButton(options++, "Buy Ammo - " + costAmmo, () -> {
					if (GameState.realMoney >= costAmmo && GameState.ammo < Constants.MAX_AMMO) {
						GameState.realMoney -= costAmmo;
						GameState.ammo += 1;
						success.play();
					} else {
						reject.play();
					}
				});
			}

			drawButton(options++, "Wait here", () -> {
				success.play();
				for (Town townToUpdate : towns) {
					townToUpdate.update(delta * Constants.TRAVELING_MAP_SPEED / 0.4f);
				}

				timeOfTheDayAnimTarget++;
				timeOfTheDayAnim++;
				dayTimeBehindUs++;
				if (timeOfTheDayAnim > 16) {
					timeOfTheDayAnim = 2;
					timeOfTheDayAnimTarget =2;
				}
				GameState.herHpCounter--;
				if (GameState.herHpCounter < 0) {
					GameState.herHp--;
					GameState.herHpCounter = Constants.HER_HP_COUNTER;
				}
				if (isMorningEnd(mapHero.getCurrentTown())) {
					GameState.visited++;
				}
			});
		}


		//intro

		if (introTime > 0.5 && introStep == 0) {
			Sprite sprite = new Sprite(TextureMemory.getTexture("tip1.png"));
			sprite.draw(spriteBatch);
			if (Gdx.input.justTouched()) {
				introTime = 0;
				introStep = 1;
			}
		}

		if (introTime > 0.5 && introStep == 1) {
			Sprite sprite = new Sprite(TextureMemory.getTexture("tip2.png"));
			sprite.draw(spriteBatch);
			if (Gdx.input.justTouched()) {
				introTime = 0;
				introStep = 2;
			}
		}

		if (introTime > 0.5 && introStep == 2) {
			Sprite sprite = new Sprite(TextureMemory.getTexture("tip3.png"));
			sprite.draw(spriteBatch);
			if (Gdx.input.justTouched()) {
				introTime = 0;
				introStep = 3;
			}
		}

		if (introTime > 0.5 && introStep == 3) {
			Sprite sprite = new Sprite(TextureMemory.getTexture("tip5.png"));
			sprite.draw(spriteBatch);
			if (Gdx.input.justTouched()) {
				introTime = 0;
				introStep = 4;
			}
		}

		if (introTime > 0.5 && introStep == 4) {
			Sprite sprite = new Sprite(TextureMemory.getTexture("tip6.png"));
			sprite.draw(spriteBatch);
			if (Gdx.input.justTouched()) {
				introTime = 0;
				introStep = 5;
			}
		}

		if (introTime > 0.5 && introStep == 5) {
			Sprite sprite = new Sprite(TextureMemory.getTexture("tip4.png"));
			sprite.draw(spriteBatch);
			if (Gdx.input.justTouched()) {
				isPlayingIntro = false;
				introStep = 6;
			}
		}





		if (Gdx.input.justTouched() && !mapHero.isOnTheMove() && !isPlayingIntro && !defeatFade) {

			boolean anyTouched = false;

			for (Town town : towns) {
				Circle townCircle = new Circle(town.getX(), town.getY(), Constants.TOWN_RADIUS + town.getTownSize() * 10);

				if (townCircle.contains(x, y)) {
					if (town.isConnected(mapHero.getCurrentTown(), connections)) {
						if (town != mapHero.getCurrentTown()) {
							mapHero.setTargetTown(town);
							mapHero.setProgress(0);
							currentRouteFought = false;
							anyTouched = true;
							timeOfTheDayAnimTarget++;
							if (timeOfTheDayAnimTarget > 16) {
								timeOfTheDayAnimTarget = 2;
							}
						}
					}
				}
			}

			if (!anyTouched) {
				for (int i = onClicks.size() - 1; i >= 0; i--) {
					OnClick onClick = onClicks.get(i);
					if (onClick.getRectangle().contains(x, y)) {
						anyTouched = true;
						onClick.onClick();
						break;
					}

				}
			}

		}
		if (mapHero.getCurrentTown() == mapHero.getTargetTown() || mapHero.getProgress() > 0.99) {
			fadeToBlackAlpha = 0;
		}

		Sprite black = new Sprite(TextureMemory.getTexture("black.png"));
		black.draw(spriteBatch, fadeToBlackAlpha + defeatFadeProgress);

		if (defeatFadeProgress >= 1) {
			Core.nextScreen = new DefeatScreen(false);
		}

		spriteBatch.end();
	}

	private boolean isMorningEnd(Town town) {
		return town.getName().equals("Morning's End");
	}

	private Connection getConnection(Town currentTown, Town targetTown) {

		for (Connection connection : connections) {
			if (connection.contains(currentTown) && connection.contains(targetTown)) {
				return connection;
			}
		}

		return null;

	}

	private ConnectionDanger getConnectionDanger(float connectionDanger) {
		ConnectionDanger enConnDang;
		if (connectionDanger < 40) {
			enConnDang = ConnectionDanger.SAFE;
		} else if (connectionDanger < 80) {
			enConnDang = ConnectionDanger.MEDIUM;
		} else {
			enConnDang = ConnectionDanger.HARD;
		}
		return enConnDang;
	}

	private void drawButton(int options, String s, Runnable action) {
		Sprite plankSprite = new Sprite(TextureMemory.getTexture("plank.png"));
		plankSprite.setPosition(Constants.WIDTH - 355, Constants.flipY(Constants.HEIGHT - 160 + (options - 1) * 50));
		plankSprite.draw(spriteBatch);
		nandakaMidFont.draw(spriteBatch, s, Constants.WIDTH - 350, Constants.flipY(Constants.HEIGHT - 200 + (options - 1) * 50));
		onClicks.add(
				new OnClick() {
					@Override
					public Rectangle getRectangle() {
						return new Rectangle(
								Constants.WIDTH - 350, Constants.HEIGHT - 160 + ((options-2) * 50), 333, 51
						);
					}

					@Override
					public void onClick() {
						action.run();
					}
				}
		);
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
		shapeRenderer.dispose();
		spriteBatch.dispose();
		shaderProgram.dispose();
		// Destroy screen's assets here.
		nandakaSmallFont.dispose();
		nandakaMidFont.dispose();
		nandakaFont.dispose();
		reject.dispose();
		success.dispose();
	}
}