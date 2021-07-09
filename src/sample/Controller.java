package sample;

import java.awt.Desktop;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Line;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;
import java.util.Objects;

public class Controller {

    @FXML
    private Label sampleNumberLabel;

    @FXML
    private Label sampleFrequencyLabel;

    private Label nextPreviewTransformation;

    private Stage stage;
    private Scene scene;
    private Parent root;


    public void navToAudioProgram(ActionEvent event) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("audioProgramMain.fxml")));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void navToImageProgram(ActionEvent event) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("imageProgramMain.fxml")));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void navToLaunch(ActionEvent event) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("launchScreen.fxml")));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    final FileChooser fileChooser = new FileChooser();
    private File file;

    @FXML
    private LineChart channelOneChart;

    @FXML
    private LineChart channelTwoChart;


    private int totalFramesRead = 0;
    XYChart.Series channelOneSeries = new XYChart.Series();
    XYChart.Series channelTwoSeries = new XYChart.Series();

    @FXML
    private Button fadeInOutButton;


    public void extractAudioData(File file, LineChart lineChartOne, LineChart lineChartTwo,
                                 XYChart.Series seriesOne, XYChart.Series seriesTwo ) {

        seriesOne.getData().clear();
        seriesTwo.getData().clear();

        lineChartOne.getData().clear();
        lineChartTwo.getData().clear();

        try {

            AudioInputStream audioInputStream =
                    AudioSystem.getAudioInputStream(file);
            int bytesPerFrame =
                    audioInputStream.getFormat().getFrameSize();
            if (bytesPerFrame == AudioSystem.NOT_SPECIFIED) {
                // some audio formats may have unspecified frame size
                // in that case we may read any amount of bytes
                bytesPerFrame = 1;
            }
            int numChannels = audioInputStream.getFormat().getChannels();
            int numBytes = (int) file.length();
            byte[] audioBytes = new byte[numBytes];
            try {
                int numBytesRead = 0;
                int numFramesRead = 0;
                int x = 0;
                // Try to read numBytes bytes from the file.
                while (x<numBytes) {
                    numBytesRead = audioInputStream.read(audioBytes);
                    // Calculate the number of frames actually read.
                    numFramesRead = numBytesRead / bytesPerFrame;
                    totalFramesRead += numFramesRead;

                    if (numChannels==1) {
                        if (x%5 == 0) {
                            ByteBuffer bb = ByteBuffer.wrap(audioBytes, x, 2);
                            bb.order(ByteOrder.LITTLE_ENDIAN);
        //                    System.out.println(bb.getShort() + ", (bb get short) " + "\n");
                            short audioAmplitude = bb.getShort();
                            float modulatedAmplitude = normalizeAudio(audioAmplitude);
        //                    System.out.println(x + ", " + audioAmplitude+ "\n");
                            seriesOne.getData().add(new XYChart.Data((x/2), modulatedAmplitude));
                        }
                        x+=2;
                    } else if (numChannels==2) {
                        if (x%10 == 0) {
                            ByteBuffer bb = ByteBuffer.wrap(audioBytes, x, 4);
                            bb.order(ByteOrder.LITTLE_ENDIAN);
                            short audioAmplitude = bb.getShort();
                            float modulatedAmplitude = normalizeAudio(audioAmplitude);

                            seriesOne.getData().add(new XYChart.Data((x/4), modulatedAmplitude));

                            bb = ByteBuffer.wrap(audioBytes, x+2, 2);
                            bb.order(ByteOrder.LITTLE_ENDIAN);
                            audioAmplitude = bb.getShort();
                            modulatedAmplitude = normalizeAudio(audioAmplitude);
                            seriesTwo.getData().add(new XYChart.Data((x/4), modulatedAmplitude));
                        }
                        x+=4;
                    }
                }
//                System.out.println(series.getData().);
//                System.out.println(Arrays.toString(audioBytes));
//                System.out.println("bytesPerFrame" + bytesPerFrame);
//                System.out.println("totalFramesRead" + totalFramesRead);

                lineChartOne.setCreateSymbols(false);
                lineChartOne.setHorizontalGridLinesVisible(false);
                lineChartOne.setVerticalGridLinesVisible(false);

                lineChartTwo.setCreateSymbols(false);
                lineChartTwo.setHorizontalGridLinesVisible(false);
                lineChartTwo.setVerticalGridLinesVisible(false);

                lineChartOne.getData().add(seriesOne);
                lineChartTwo.getData().add(seriesTwo);

                sampleNumberLabel.setText(totalFramesRead + " Samples");
                sampleFrequencyLabel.setText(audioInputStream.getFormat().getSampleRate() + " Hz");



            } catch (Exception ex) {
                System.out.println("no1");
            }
        } catch (Exception e) {
            System.out.println("no2");
        }
    }

    public void fadeInOutHandler(ActionEvent event) throws IOException {
        fadeInOut(channelOneChart, channelTwoChart, channelOneSeries, channelTwoSeries);
    }

    public void fadeInOut(LineChart lineChartOne, LineChart lineChartTwo,
                          XYChart.Series seriesOne, XYChart.Series seriesTwo) {

        lineChartOne.getData().clear();
        lineChartTwo.getData().clear();


        float amplification;
        float floatTotalFramesRead =  (float) totalFramesRead;
        modVolume(seriesOne, floatTotalFramesRead);
        modVolume(seriesTwo, floatTotalFramesRead);

//
        lineChartOne.getData().add(seriesOne);
        lineChartTwo.getData().add(seriesTwo);
//        System.out.println(seriesOne.getData());
//        System.out.println(seriesTwo.getData());
//        seriesOne.getData().clear();
//        seriesTwo.getData().clear();
    }

    public void modVolume(XYChart.Series series, float floatTotalFramesRead) {
        float amplification;
        float halfSize = (float) (series.getData().size())/2;
        for (int i = 0; i < (series.getData().size())/2; i++) {
            amplification = (float) Math.pow(10, ((((20/halfSize)*i)-20)/20));
            System.out.println(i + " with " + amplification);
            XYChart.Data data = (XYChart.Data) series.getData().get(i);
            float oldValue = (float) data.getYValue();
            float newAmplitude = oldValue * amplification;
            data.setYValue(newAmplitude);
        }

        int j = (series.getData().size())/2;
        for (int i = (series.getData().size())/2; i < series.getData().size(); i++) {
            amplification = (float) Math.pow(10, ((((20/halfSize)*j)-20)/20));
            System.out.println(i + " with2 " + amplification);
            XYChart.Data data = (XYChart.Data) series.getData().get(i);
            float oldValue = (float) data.getYValue();
            float newAmplitude = oldValue * amplification;
            data.setYValue(newAmplitude);
            j--;
        }
    }

    //}
    public float normalizeAudio (short audioAmplitude) {
        int normalizeFactor;
        float modulatedAmplitude;
        if (audioAmplitude < 0) {
            normalizeFactor = 32768;
        } else {
            normalizeFactor = 32767;
        }
        modulatedAmplitude = (float) audioAmplitude/normalizeFactor;
        return modulatedAmplitude;
    }

    public void openFileAudio(ActionEvent event) throws IOException{
        file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            extractAudioData(file, channelOneChart, channelTwoChart, channelOneSeries, channelTwoSeries);
        }
    }

    public void openFileImage(ActionEvent event) throws IOException{
        File file = fileChooser.showOpenDialog(stage);
//        if (file != null) {
//            openFile(file);
//        }
    }

    private Desktop desktop = Desktop.getDesktop();

    private void openFile(File file) {
        try {
            desktop.open(file);
        } catch (IOException ex) {
            System.out.println("no");
        }
    }


//    openFileAudio.setOnAction()
//    FileChooser fileChooser = new FileChooser();
//    fileChooser.setTitle("Open Resource File");
//    fileChooser.showOpenDialog(stage);

}
