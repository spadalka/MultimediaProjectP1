package sample;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class Main extends Application {

//    Stage window;
//    Scene launchScene, audioProgram;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
//        window = primaryStage;

        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("launchScreen.fxml")));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 490, 100));
        primaryStage.show();
    }


}
