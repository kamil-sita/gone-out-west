package pl.sita.goneoutwest;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

public class DefeatScreen implements Screen {

	private final boolean youHaveDiedFirst;

	private final SpriteBatch spriteBatch = new SpriteBatch();
	private final BitmapFont nandakaFont = new BitmapFont(
			Gdx.files.internal("nandaka_western.fnt")
	);
	private final BitmapFont nandakaSmallFont = new BitmapFont(
			Gdx.files.internal("nandaka_western_s.fnt")
	);
	private final BitmapFont nandakaMidFont = new BitmapFont(
			Gdx.files.internal("nandaka_western_m.fnt")
	);

	float fadeToBlackAlpha = 1;

	public DefeatScreen(boolean youHaveDiedFirst) {
		this.youHaveDiedFirst = youHaveDiedFirst;
	}


	@Override
	public void show() {
		// Prepare your screen here.
	}
	float time = 0;

	boolean isOver = false;

	@Override
	public void render(float delta) {

		time += delta;

		ScreenUtils.clear(new Color((104.0f/255.0f), (33.0f/255.0f), (33.0f/255.0f), 1f));

		spriteBatch.begin();

		if (!isOver) {
			fadeToBlackAlpha -= delta;
			fadeToBlackAlpha = Math.max(0, fadeToBlackAlpha);
		} else {
			fadeToBlackAlpha += delta;
		}


		if (youHaveDiedFirst) {
			nandakaFont.draw(spriteBatch, "You have died.", 100, Constants.flipY(100));
			if (time > 4) {
				nandakaMidFont.draw(spriteBatch, "She died alone several days later.", 100, Constants.flipY(200));
			}
		} else {
			nandakaFont.draw(spriteBatch, "She has died.", 100, Constants.flipY(100));
			if (time > 4) {
				nandakaMidFont.draw(spriteBatch, "So it was time for you to join her.", 100, Constants.flipY(200));
			}
		}
		if (time > 6) {
			nandakaMidFont.draw(spriteBatch, "Earned: " + GameState.earned + "$", 100, Constants.flipY(300));
		}
		if (time > 6.5) {
			nandakaMidFont.draw(spriteBatch, "Killed: " + GameState.enemiesKilled, 100, Constants.flipY(400));
		}
		if (time > 7) {
			nandakaMidFont.draw(spriteBatch, "Shot: " + GameState.bulletsFired, 100, Constants.flipY(500));
		}
		if (time > 7.5) {
			nandakaMidFont.draw(spriteBatch, "Delivered: " + GameState.lettersDelivered, 100, Constants.flipY(600));
		}
		if (time > 8) {
			nandakaMidFont.draw(spriteBatch, "Visited: " + GameState.visited, 100, Constants.flipY(700));
		}

		if (time > 8.5) {
			if (time < 9.5) {

				nandakaMidFont.draw(spriteBatch, "She has lived for another " + GameState.timeSurvived + " hours", 100, Constants.flipY(800));
			} else {
				nandakaMidFont.draw(spriteBatch, "She has lived for another " + GameState.timeSurvived + " hours. Thanks to you, Hero", 100, Constants.flipY(800));
			}
		}

		if (time > 11) {
			nandakaSmallFont.draw(spriteBatch, "Touch the screen to restart the game ", 1100, Constants.flipY(100));
			if (Gdx.input.justTouched()) {
				isOver = true;
			}
		}


		Sprite black = new Sprite(TextureMemory.getTexture("black.png"));
		black.draw(spriteBatch, fadeToBlackAlpha);

		if (isOver && fadeToBlackAlpha >= 1) {
			Core.nextScreen = new IntroScreen();
		}

		spriteBatch.end();
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
		// Destroy screen's assets here.
		nandakaSmallFont.dispose();
		nandakaMidFont.dispose();
		nandakaFont.dispose();
	}
}