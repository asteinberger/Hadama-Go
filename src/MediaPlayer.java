import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import javax.media.Manager;
import javax.media.NoPlayerException;
import javax.media.Player;

public class MediaPlayer {

	private Player player;
	private File file;
	private boolean isPlaying = true;

	public MediaPlayer(String f) throws NoPlayerException,
			MalformedURLException, IOException {
		this.file = new File(f);
		if (this.file != null) {
			if (this.player != null)
				this.player.close();
			this.player = Manager.createPlayer(this.file.toURL());
			this.player.start(); // start player
		} // end if
	} // end constructor

	public void pause() {
		if (this.isPlaying) {
			this.player.stop();
		} else {
			try {
				this.player = Manager.createPlayer(this.file.toURL());
			} catch (NoPlayerException e) {
				e.printStackTrace();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} // end try
			this.player.start();
		} // end if
	} // end pause()

	public void toggle() {
		if (this.isPlaying)
			this.isPlaying = false;
		else
			this.isPlaying = true;
	} // end toggle()

} // end class