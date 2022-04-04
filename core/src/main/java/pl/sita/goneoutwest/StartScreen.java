package pl.sita.goneoutwest;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

import static pl.sita.goneoutwest.Constants.INTRO_SPEED;

public class StartScreen implements Screen {


	float time = 0;
	private final SpriteBatch spriteBatch = new SpriteBatch();


	@Override
	public void show() {
		// Prepare your screen here.
	}

	@Override
	public void render(float delta) {
		time += delta;
		ScreenUtils.clear(new Color((104.0f/255.0f), (33.0f/255.0f), (33.0f/255.0f), 1f));
		spriteBatch.begin();

		{
			Sprite sprite = new Sprite(TextureMemory.getTexture("logo.png"));
			sprite.setPosition(Constants.WIDTH /2-sprite.getWidth()/2, Constants.HEIGHT /2);
			sprite.draw(spriteBatch);
		}

		if (time % 2 < 1){
			Sprite sprite = new Sprite(TextureMemory.getTexture("text_5.png"));
			sprite.setPosition(Constants.WIDTH /2-sprite.getWidth()/2, Constants.HEIGHT /2 - Constants.HEIGHT/6);
			sprite.draw(spriteBatch);
		}

		if (time > 0.5 * INTRO_SPEED) {

			if (Gdx.input.isTouched()) {
				//Core.nextScreen = new FightScreen(2, 250);
				Core.nextScreen = new MapScreen();
				//Core.nextScreen = new DefeatScreen(true);
				GameState.reset();
			}
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