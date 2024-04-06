package ca.yorku.eecs.groupr.tilttiktok;

import android.app.Activity;
import android.media.AudioAttributes;
import android.media.SoundPool;

public class SoundEffect {
    private SoundPool soundPool;
    private int correctSound, incorrectSound;

    public SoundEffect(Activity ctx) {
        AudioAttributes audioAttributes = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_GAME).setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build();

        soundPool = new SoundPool.Builder().setMaxStreams(2).setAudioAttributes(audioAttributes).build();
        correctSound = soundPool.load(ctx, R.raw.correct, 1);
        incorrectSound = soundPool.load(ctx, R.raw.incorrect, 1);
    }

    public void correct() {
        soundPool.play(correctSound, 1.0f, 1.0f, 0, 0, 1.0f);
    }

    public void incorrect() {
        soundPool.play(incorrectSound, 1.0f, 1.0f, 0, 0, 1.0f);
    }

    @Override
    protected void finalize() throws Throwable {
        soundPool.release();
        super.finalize();
    }
}
