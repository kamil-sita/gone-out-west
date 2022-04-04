package pl.sita.goneoutwest;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

import static pl.sita.goneoutwest.Constants.INTRO_SPEED;

public class IntroScreen implements Screen {

	float time = 0;
	private final SpriteBatch spriteBatch = new SpriteBatch();


	@Override
	public void show() {
		// Prepare your screen here.
	}

	int intro =  0;

	@Override
	public void render(float delta) {
		time += delta;
		ScreenUtils.clear(new Color((104.0f/255.0f), (33.0f/255.0f), (33.0f/255.0f), 1f));
		spriteBatch.begin();

		if (time > 3.5 && Gdx.input.justTouched()) {
			intro++;
		}

		if (time > 0.5 * INTRO_SPEED && time < 3 * INTRO_SPEED) {
			Sprite sprite = new Sprite(TextureMemory.getTexture("logo.png"));
			sprite.setPosition(Constants.WIDTH /2-sprite.getWidth()/2, Constants.HEIGHT /2);
			sprite.draw(spriteBatch);
		}

		if (time > 3.5 * INTRO_SPEED && intro < 3) {
			Sprite sprite = new Sprite(TextureMemory.getTexture("text_1.png"));
			sprite.setPosition(Constants.WIDTH /2-sprite.getWidth()/2, Constants.HEIGHT /2 + Constants.HEIGHT /3);
			sprite.draw(spriteBatch);
		}

		if (intro >= 1 && intro < 3) {
			Sprite sprite = new Sprite(TextureMemory.getTexture("text_2.png"));
			sprite.setPosition(Constants.WIDTH /2-sprite.getWidth()/2, Constants.HEIGHT /2);
			sprite.draw(spriteBatch);
		}

		if (intro >= 2 && intro < 3) {
			Sprite sprite = new Sprite(TextureMemory.getTexture("text_3.png"));
			sprite.setPosition(Constants.WIDTH /2-sprite.getWidth()/2, Constants.HEIGHT /2 - Constants.HEIGHT /3);
			sprite.draw(spriteBatch);
		}

		if (intro >= 3 && intro < 4) {
			Sprite sprite = new Sprite(TextureMemory.getTexture("text_4.png"));
			sprite.setPosition(Constants.WIDTH /2-sprite.getWidth()/2, Constants.HEIGHT /2);
			sprite.draw(spriteBatch);
		}

		if (intro == 4) {
			Core.nextScreen = new StartScreen();
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
	}
}