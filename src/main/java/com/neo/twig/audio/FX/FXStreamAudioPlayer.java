package com.neo.twig.audio.FX;

import com.neo.twig.audio.AudioPlayer;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.net.URI;

public class FXStreamAudioPlayer extends AudioPlayer {
    protected MediaPlayer mediaPlayer;

    public FXStreamAudioPlayer(URI resource) {
        super(resource);

        Media media = new Media(resource.toString());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setOnReady(onReadyCallback);
    }

    @Override
    public void play() {
        mediaPlayer.play();
    }

    @Override
    public void pause() {
        mediaPlayer.pause();
    }

    @Override
    public void stop() {
        mediaPlayer.stop();
    }

    @Override
    public void release() {
        super.release();
        mediaPlayer.dispose();
    }

    //TODO: Looping incurs a short delay, it may be best to roll a custom audio system
    @Override
    public void setLooping(boolean loop) {
        super.setLooping(loop);

        if (loop) {
            mediaPlayer.setOnEndOfMedia(() -> {
                mediaPlayer.seek(Duration.ZERO);
                mediaPlayer.play();
            });
        } else {
            mediaPlayer.setOnEndOfMedia(null);
        }
    }

    @Override
    public void setOnReadyCallback(Runnable callback) {
        super.setOnReadyCallback(callback);
        mediaPlayer.setOnReady(callback);

        if (mediaPlayer.getStatus() == MediaPlayer.Status.READY) {
            callback.run();
        }
    }
}
