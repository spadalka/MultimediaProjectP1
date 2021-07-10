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
import java.util.Arrays;
import java.util.Objects;

public class ImageController {

    final FileChooser fileChooser = new FileChooser();
    BufferedImage originalImage;
    BufferedImage modifiableImage;
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

    static BufferedImage deepCopy(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    } // this is straight outta stackoverflow https://stackoverflow.com/questions/3514158/how-do-you-clone-a-bufferedimage

    private static int findMin(int[] arr) {
        int minIndex = 255;
        for (int i = 254; i >= 0; i--) {
            if (i < minIndex && arr[i] > 0) {
                minIndex = i;
            }
        }
        return minIndex;
    }

    public static int findMax(int[] arr) {
        int maxIndex = 0;
        for (int i = 1; i <= 255; i++) {
            if (i > maxIndex && arr[i] > 0) {
                maxIndex = i;
            }
        }
        return maxIndex;
    }

    public void navToLaunch(ActionEvent event) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("launchScreen.fxml")));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);

        stage.setScene(scene);
        stage.show();
    }

    public void quit(ActionEvent event) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("launchScreen.fxml")));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);

        stage.setScene(scene);
        stage.show();
    }

    public void transitionScene(ActionEvent event) throws IOException {
        if (globalStepCounter == 1) {
            normalToGrayScale(file);
        } else if (globalStepCounter == 2) {
            grayScaleToDither(modifiableImage);
        } else if (globalStepCounter == 3) {
            autoLevel(originalImage);
        } else if (globalStepCounter == 4) {
            openFileImage(event);
        }
    }

    private void autoLevel(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int threshold = 30;
        int[] redCount = new int[256];
        int[] greenCount = new int[256];
        int[] blueCount = new int[256];
        Arrays.fill(redCount, 0);
        Arrays.fill(greenCount, 0);
        Arrays.fill(blueCount, 0);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color colorAtPixel = new Color(image.getRGB(x, y));
                int redValue = colorAtPixel.getRed();
                redCount[redValue]++;
                int greenValue = colorAtPixel.getGreen();
                greenCount[greenValue]++;
                int blueValue = colorAtPixel.getBlue();
                blueCount[blueValue]++;
            }
        }

        int redMax = findMax(redCount);

        for (int i : redCount) {
            if (i < threshold) {
                i = 0;
            }
        }

        int redMin = findMin(redCount);

        int greenMax = findMax(greenCount);

        for (int i : greenCount) {
            if (i < threshold) {
                i = 0;
            }
        }

        int greenMin = findMin(greenCount);

        int blueMax = findMax(blueCount);

        for (int i : blueCount) {
            if (i < threshold) {
                i = 0;
            }
        }

        int blueMin = findMin(blueCount);


        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color colorAtPixel = new Color(image.getRGB(x, y));
                int redValue = (int) (((colorAtPixel.getRed() - redMin) / (float) (redMax - redMin)) * 255);
                int greenValue = (int) (((colorAtPixel.getGreen() - greenMin) / (float) (greenMax - greenMin)) * 255);
                int blueValue = (int) (((colorAtPixel.getBlue() - blueMin) / (float) (blueMax - blueMin)) * 255);
                Color newColorAtPixel = new Color(redValue, greenValue, blueValue);
                image.setRGB(x, y, newColorAtPixel.getRGB());
            }
        }

        Image displayImage = SwingFXUtils.toFXImage(image, null);
        ImageView imageView = new ImageView(displayImage);
        pane.getChildren().set(0, imageView);
        StackPane.setAlignment(imageView, Pos.CENTER);
        nextPreviewTransformation.setText("Start Over");
        globalStepCounter++;

//        File output = new File("algoauto.bmp");
//        try {
//            ImageIO.write(image, "bmp", output);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }

    public void grayScaleToDither(BufferedImage image) {
        int[][] matrix4 = {{0, 2}, {3, 1}};
        int[][] matrix16 = {{0, 8, 2, 10}, {12, 4, 14, 6}, {3, 11, 1, 9}, {15, 7, 13, 5}};
        int[][] matrix64 = {{0, 32, 8, 40, 2, 34, 10, 42},
                {48, 16, 56, 24, 50, 18, 58, 26},
                {12, 44, 4, 36, 14, 46, 6, 38},
                {60, 28, 52, 20, 62, 30, 54, 22},
                {3, 35, 11, 43, 1, 33, 9, 41},
                {51, 19, 59, 27, 49, 17, 57, 25},
                {15, 47, 7, 39, 13, 45, 5, 37},
                {63, 31, 55, 23, 61, 29, 53, 21}};

        int width = image.getWidth();
        int height = image.getHeight();
        int n = 2;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int i = x % n;
                int j = y % n;
                Color colorAtPixel = new Color(image.getRGB(x, y));
                int reMapped = colorAtPixel.getRed() / (256 / (n * n + 1));
                Color newValue;
                if (reMapped > matrix4[i][j]) {
                    newValue = new Color(255, 255, 255);
                } else {
                    newValue = new Color(0, 0, 0);
                }
                image.setRGB(x, y, newValue.getRGB());
            }
        }
        Image displayImage = SwingFXUtils.toFXImage(image, null);
        ImageView imageView = new ImageView(displayImage);
        pane.getChildren().set(0, imageView);
        StackPane.setAlignment(imageView, Pos.CENTER);
        nextPreviewTransformation.setText("Original Autoleveled");
        globalStepCounter++;

//        File output = new File("dithered.bmp");
//        try {
//            ImageIO.write(modifiableImage, "bmp", output);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }

    public void normalToGrayScale(File file) {

        originalImage = null;
        try {
            originalImage = ImageIO.read(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert originalImage != null;
        modifiableImage = deepCopy(originalImage);

        int width = modifiableImage.getWidth();
        int height = modifiableImage.getHeight();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color colorAtPixel = new Color(modifiableImage.getRGB(x, y));
                int redValue = (int) (colorAtPixel.getRed() * 0.299);
                int greenValue = (int) (colorAtPixel.getGreen() * 0.587);
                int blueValue = (int) (colorAtPixel.getBlue() * 0.114);
                int grayScaleValue = redValue + greenValue + blueValue;
                Color grayScaleForPixel = new Color(grayScaleValue, grayScaleValue, grayScaleValue);
                modifiableImage.setRGB(x, y, grayScaleForPixel.getRGB());
            }
        }

        Image displayImage = SwingFXUtils.toFXImage(modifiableImage, null);
        ImageView imageView = new ImageView(displayImage);
        pane.getChildren().set(0, imageView);
        StackPane.setAlignment(imageView, Pos.CENTER);


//        File output = new File("grayscale.bmp");
//        try {
//            ImageIO.write(modifiableImage, "bmp", output);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        nextPreviewTransformation.setText("GrayScale to Dither");
        globalStepCounter++;
    }

    public void openFileImage(ActionEvent event) throws IOException {
        globalStepCounter = 1;
        file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            Image image = new Image(file.toURI().toString());
            ImageView imageView = new ImageView(image);
            pane.getChildren().set(0, imageView);
            StackPane.setAlignment(imageView, Pos.CENTER);

        }
    }


}
