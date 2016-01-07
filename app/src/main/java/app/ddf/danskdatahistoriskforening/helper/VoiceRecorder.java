package app.ddf.danskdatahistoriskforening.helper;

import android.media.MediaRecorder;
import com.coremedia.iso.boxes.Container;
import com.googlecode.mp4parser.FileDataSourceImpl;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.util.List;

/**
 * Mighty credits to Sebastian Annies (sannies @ github)!
 */
public class VoiceRecorder {
    private boolean isRecording;
    private MediaRecorder mRecorder;
    private File tempFile;

    public void execute() {
        if (!isRecording)
            startRecord();
        else
            stopRecord();
    }

    private void startRecord() {
        isRecording = true;
        String mFileName = LocalMediaStorage.getOutputMediaFileUri(3).getPath();
        tempFile = new File(mFileName);
        if (tempFile.exists()) {
            tempFile.delete();
            mFileName = LocalMediaStorage.getOutputMediaFileUri(3).getPath();
        }
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mRecorder.setAudioSamplingRate(16000);
        mRecorder.setAudioChannels(1);
        mRecorder.setOutputFile(mFileName);
        try {
            mRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mRecorder.start();
    }

    private void stopRecord() {
        if(mRecorder==null)
            return;
        mRecorder.stop();     // stop recording
        mRecorder.reset();    // set state to idle
        mRecorder.release();  // release resources back to the system
        mRecorder = null;
        isRecording = false;
        createAudioFile1();
    }

    private void createAudioFile1() {
        try {
            File mainFile = new File(LocalMediaStorage.getOutputMediaFileUri(2).getPath());
            if (!mainFile.exists()) {
                tempFile.renameTo(new File(LocalMediaStorage.getOutputMediaFileUri(2).getPath()));
                return;
            }
            final Movie movieA = MovieCreator.build(new FileDataSourceImpl(mainFile));
            final Movie movieB = MovieCreator.build(new FileDataSourceImpl(tempFile));
            final Movie finalMovie = new Movie();
            final List<Track> movieOneTracks = movieA.getTracks();
            final List<Track> movieTwoTracks = movieB.getTracks();
            for (int i = 0; i < movieOneTracks.size() || i < movieTwoTracks.size(); ++i) {
                finalMovie.addTrack(new AppendTrack(movieTwoTracks.get(i),
                        movieOneTracks.get(i)));
            }
            final Container container = new DefaultMp4Builder().build(finalMovie);
            File newFile = new File(LocalMediaStorage.getOutputMediaFileUri(4).getPath());
            if (newFile.exists()) {
                newFile.delete();
            }
            final FileOutputStream fos = new FileOutputStream(new File(String.format(newFile.getPath())));
            final WritableByteChannel bb = Channels.newChannel(fos);
            container.writeContainer(bb);
            fos.close();
            mainFile.delete();
            tempFile.delete();
            newFile.renameTo(new File(LocalMediaStorage.getOutputMediaFileUri(2).getPath()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void requestStop() {
        if (isRecording)
            stopRecord();
    }
}