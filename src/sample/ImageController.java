package sample;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class ImageController {

    final FileChooser fileChooser = new FileChooser();
    private Stage stage;
    private Parent root;
    private Scene scene;

    @FXML
    private ImageView img1;

    @FXML
    private Label nextPreviewTransformation;

    @FXML
    private Button nextButton;

    @FXML
    private StackPane pane;

    private int globalStepCounter = 1;

    private File file;


    public void navToLaunch(ActionEvent event) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("launchScreen.fxml")));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);

        stage.setScene(scene);
        stage.show();
    }

    static BufferedImage deepCopy(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    } // this is straight outta stackoverflow https://stackoverflow.com/questions/3514158/how-do-you-clone-a-bufferedimage

    public void transitionScene(ActionEvent event) {
        if (globalStepCounter == 1){
            normalToGrayScale(file);
        }
    }

    public void normalToGrayScale (File file) {

        BufferedImage originalImage = null;
        try {
            originalImage = ImageIO.read(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert originalImage != null;
        BufferedImage modifiableImage = deepCopy(originalImage);

        int width = modifiableImage.getWidth();
        int height = modifiableImage.getHeight();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color colorAtPixel = new Color(modifiableImage.getRGB(x, y));
                int redValue = (int)(colorAtPixel.getRed()*0.299);
                int greenValue = (int)(colorAtPixel.getGreen()*0.587);
                int blueValue = (int)(colorAtPixel.getBlue()*0.114);
                int grayScaleValue = redValue + greenValue + blueValue;
                Color grayScaleForPixel = new Color(grayScaleValue, grayScaleValue, grayScaleValue);
                modifiableImage.setRGB(x, y, grayScaleForPixel.getRGB());
            }
        }

            Image image = SwingFXUtils.toFXImage(modifiableImage, null);
            ImageView imageView = new ImageView(image);
            pane.getChildren().set(0, imageView);
            StackPane.setAlignment(imageView, Pos.CENTER);


        File output = new File("grayscale.bmp");
        try {
            ImageIO.write(modifiableImage, "bmp", output);
        } catch (IOException e) {
            e.printStackTrace();
        }

//
//
//
        nextPreviewTransformation.setText("GrayScale to Dither");
//        return;
    }


    public void openFileImage(ActionEvent event) throws IOException {
        file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            Image image = new Image(file.toURI().toString());
            ImageView imageView = new ImageView(image);
            pane.getChildren().set(0, imageView);
            StackPane.setAlignment(imageView, Pos.CENTER);




//


//            nextButton.setOnAction(
//                    e -> {
//                        for (int y = 0; y < height; y++) {
//                            for (int x = 0; x < width; x++) {
//                                Color colorAtPixel = new Color(modifiableImage.getRGB(x, y));
//                                int redValue = (int)(colorAtPixel.getRed()*0.2126);
//                                int greenValue = (int)(colorAtPixel.getGreen()*0.7152);
//                                int blueValue = (int)(colorAtPixel.getBlue()*0.0722);
//                                int grayScaleValue = redValue + greenValue + blueValue;
//                                Color grayScaleForPixel = new Color(grayScaleValue, grayScaleValue, grayScaleValue);
//                                modifiableImage.setRGB(x, y, grayScaleForPixel.getRGB());
//                            }
//                        }
//
//                        img1.setImage(SwingFXUtils.toFXImage(modifiableImage, null));
//
//                    }
//            );









        }
    }


}
