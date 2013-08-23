package com.theora.sudoku;
import com.theora.M.Mutils;
import android.content.Context;
import android.content.res.Resources;
import android.media.MediaPlayer;
/*------------------------------------------------------------*/
public class Music {
	private static MediaPlayer musicMp = null;
	private MediaPlayer echoesMp = null;
	private Context context;
	private Mutils mutils;
	/*------------------------------------------------------------*/
	public Music(Context context) {
		this.context = context;
		this.mutils = new Mutils(context);
	}
	/*------------------------------------------------------------*/
	public void startPlaying(Context ctx, int resId) { 
		stopPlaying();
		if ( ! Prefs.getMusic(ctx))
			return;
		musicMp = MediaPlayer.create(ctx, resId);
		musicMp.setLooping(true);
		musicMp.start();
	}
	/*------------------------------*/
	public void stopPlaying() { 
		try {
			if (musicMp != null) {
				musicMp.stop();
				musicMp.release();
				musicMp = null;
			}
		} catch (Exception e) {
			mutils.logError();
		}
	}
	/*------------------------------------------------------------*/
	public void playEcho(int resId) {
		try {
			if (!Prefs.getEchoes(context))
				return;
			if (echoesMp != null) {
				echoesMp.stop();
				echoesMp.release();
			}
			echoesMp = MediaPlayer.create(context, resId);
			if (echoesMp != null)
				echoesMp.start();
		} catch (Exception e) {
			mutils.logError();
		}
	}
	/*------------------------------*/
	public void piano(String note) {
		try {
			Resources resources = context.getResources();
			String packageName = context.getPackageName();
			String resourceName = "piano_" + note;
			int resId = resources.getIdentifier(resourceName, "raw",
					packageName);
			if (resId == 0) {
				mutils.logError(String.format(
						"Mutil:piano: resource %s (%s.mp3) not found in %s",
						resourceName, resourceName, packageName));
				return;
			}
			playEcho(resId);
		} catch (Exception e) {
			mutils.logError();
		}
	}
	/*------------------------------------------------------------*/
}
/*------------------------------------------------------------*/
