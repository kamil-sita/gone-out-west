package pl.sita.goneoutwest;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;

import static pl.sita.goneoutwest.Constants.PLAY_INTRO;

public class Core extends Game {

	public static Screen nextScreen = null;
	public static boolean disposeThis = true;

	public static Music music;

	@Override
	public void create() {
		music = Gdx.audio.newMusic(Gdx.files.internal("GoneOutWestTheme.wav"));
		music.setLooping(true);
		music.play();

		if (PLAY_INTRO) {
			setScreen(new IntroScreen());
		} else {
			setScreen(new StartScreen());
		}
	}

	@Override
	public void render () {
		if (screen != null) {
			screen.render(Gdx.graphics.getDeltaTime());
		}
		if (nextScreen != null) {
			if (disposeThis) {
				getScreen().dispose();
			}
			disposeThis = true;
			setScreen(nextScreen);
			nextScreen = null;
		}
	}

}