package sample;

import java.awt.Desktop;

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
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Objects;

public class Controller {

    private Label sampleNumberLabel;
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


    private int totalFramesRead = 0;
    XYChart.Series series = new XYChart.Series();


    public void extractAudioData(File file, LineChart lineChartOne, XYChart.Series series ) {
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
            // Set an arbitrary buffer size of 1024 frames.
            int numBytes = (int) file.length();
            byte[] audioBytes = new byte[numBytes];
            try {
                int numBytesRead = 0;
                int numFramesRead = 0;
                int x = 0;
                // Try to read numBytes bytes from the file.
                while (x<numBytes) {
                        numBytesRead =
                        audioInputStream.read(audioBytes) ;
                    // Calculate the number of frames actually read.
                    numFramesRead = numBytesRead / bytesPerFrame;
                    totalFramesRead += numFramesRead;
//                    System.out.println("length" + audioBytes.length); //2048
//                    System.out.println("numBytesRead" + numBytesRead);
//                    System.out.println("numFramesRead" + numFramesRead);
                    // Here, do something useful with the audio data that's
                    // now in the audioBytes array...
//                    byte[] bytePair = {audioBytes[x+1], audioBytes[x]};
//                    System.out.println(bytePair[0] + ", (byte) " + bytePair[1]+ "\n");

                    ByteBuffer bb = ByteBuffer.wrap(audioBytes, x, 2);
                    bb.order(ByteOrder.LITTLE_ENDIAN);
//                    System.out.println(bb.getShort() + ", (bb get short) " + "\n");

                    short audioAmplitude = bb.getShort();
//                    System.out.println(x + ", " + audioAmplitude+ "\n");
                    series.getData().add(new XYChart.Data((x/2), (int) audioAmplitude));
                    x+=2;
                }
//                System.out.println(series.getData().);
//                System.out.println(Arrays.toString(audioBytes));
//                System.out.println("bytesPerFrame" + bytesPerFrame);
//                System.out.println("totalFramesRead" + totalFramesRead);


                lineChartOne.getData().add(series);


            } catch (Exception ex) {
                System.out.println("no1");
            }
        } catch (Exception e) {
            System.out.println("no2");
        }
    }

    public void openFileAudio(ActionEvent event) throws IOException{
        file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            extractAudioData(file, channelOneChart, series);
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
