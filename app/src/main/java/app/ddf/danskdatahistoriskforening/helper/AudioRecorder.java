package app.ddf.danskdatahistoriskforening.helper;

import android.media.MediaRecorder;
import android.net.Uri;

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
import java.util.ArrayList;
import java.util.List;

/**
 * Mighty credits to Sebastian Annies (sannies @ github)!
 */
public class AudioRecorder  {
    private boolean isRecording;
    private MediaRecorder mr;

    public boolean isRecording() {
        return isRecording;
    }

    public void startRecording() throws IOException {
        isRecording = true;
        mr = new MediaRecorder();
        String mFileName = LocalMediaStorage.getOutputMediaFileUri(null, LocalMediaStorage.MEDIA_TYPE_AUDIO_RECORD_TEMP).getPath();
        File tempFile = new File(mFileName);
        if (tempFile.exists()) {
            tempFile.delete();
            mFileName = LocalMediaStorage.getOutputMediaFileUri(null,LocalMediaStorage.MEDIA_TYPE_AUDIO_RECORD_TEMP).getPath();
        }
        mr.setAudioSource(MediaRecorder.AudioSource.MIC);
        mr.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mr.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mr.setAudioSamplingRate(16000);
        mr.setAudioChannels(1);
        mr.setOutputFile(mFileName);
        mr.prepare();
        mr.start();
    }

    public boolean stopRecording() throws IOException {
        isRecording = false;
        if (mr == null)
            return false;
        try{
            mr.stop();     // stop recording
        }catch(RuntimeException stopException){
            return false;
        }

        mr.reset();    // set state to idle
        mr.release();  // release resources back to the system
        File recordedFile = new File(LocalMediaStorage.getOutputMediaFileUri(null, LocalMediaStorage.MEDIA_TYPE_AUDIO_RECORD).getPath());
        File recordedFileTemp = new File(LocalMediaStorage.getOutputMediaFileUri(null, LocalMediaStorage.MEDIA_TYPE_AUDIO_RECORD_TEMP).getPath());
        mergeAudioFile(recordedFile, recordedFileTemp);
        isRecording = false;
        return true;
    }

    private void mergeAudioFile(File recordedFile, File recordedFileTemp) throws IOException {
        if (!recordedFile.exists()) {
            recordedFileTemp.renameTo(new File(LocalMediaStorage.getOutputMediaFileUri(null, LocalMediaStorage.MEDIA_TYPE_AUDIO_RECORD).getPath()));
            return;
        }
        final Movie movieA = MovieCreator.build(new FileDataSourceImpl(recordedFileTemp));
        final Movie movieB = MovieCreator.build(new FileDataSourceImpl(recordedFile));
        final Movie finalMovie = new Movie();
        final List<Track> movieOneTracks = movieA.getTracks();
        final List<Track> movieTwoTracks = movieB.getTracks();
        //for (int i = 0; i < movieOneTracks.size() || i < movieTwoTracks.size(); ++i) {
            finalMovie.addTrack(new AppendTrack(movieTwoTracks.get(0), movieOneTracks.get(0)));
        //}
        final Container container = new DefaultMp4Builder().build(finalMovie);
        File recordedFileMerged = new File(LocalMediaStorage.getOutputMediaFileUri(null, LocalMediaStorage.MEDIA_TYPE_AUDIO_RECORD_MERGED).getPath());
        if (recordedFileMerged.exists()) {
            recordedFileMerged.delete();
        }
        final FileOutputStream fos = new FileOutputStream(new File(String.format(recordedFileMerged.getPath())));
        final WritableByteChannel bb = Channels.newChannel(fos);
        container.writeContainer(bb);
        fos.close();
        recordedFile.delete();
        recordedFileTemp.delete();
        recordedFileMerged.renameTo(new File(LocalMediaStorage.getOutputMediaFileUri(null, LocalMediaStorage.MEDIA_TYPE_AUDIO_RECORD).getPath()));
    }

    public static MADATA mergeMultipleAudioFile(List<Uri> audioUris) throws IOException {
        if (audioUris == null)
            throw new IOException("AudioUris is null");
        Movie finalMovie = new Movie();
        List<Track> tracksList = new ArrayList<Track>();
        List<Integer> durationList = new ArrayList<Integer>();
        for (Uri uri : audioUris) {
            File file = new File(LocalMediaStorage.getOutputMediaFileUri(uri.getPath(), LocalMediaStorage.MEDIA_TYPE_AUDIO).getPath());
            Movie movie = MovieCreator.build(new FileDataSourceImpl(file));
            List<Track> movieOneTracks = movie.getTracks();
            tracksList.add(movieOneTracks.get(0));
            durationList.add((int) movieOneTracks.get(0).getDuration());
        }
        Track[] tracks = (Track[]) tracksList.toArray();
        finalMovie.addTrack(new AppendTrack(tracks));

        final Container container = new DefaultMp4Builder().build(finalMovie);
        File recordedFileMerged = new File(LocalMediaStorage.getOutputMediaFileUri(null, LocalMediaStorage.MEDIA_TYPE_AUDIO_RECORD_MERGED).getPath());
        if (recordedFileMerged.exists()) {
            recordedFileMerged.delete();
        }
        final FileOutputStream fos = new FileOutputStream(new File(String.format(recordedFileMerged.getPath())));
        final WritableByteChannel bb = Channels.newChannel(fos);
        container.writeContainer(bb);
        fos.close();
        return new MADATA(recordedFileMerged.toURI(), durationList);
    }

}