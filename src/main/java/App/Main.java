package App;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class Main extends Application {
    public static void main(String args[]){
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        URL fileUrl = App.Main.class.getResource("fxml/main.fxml");
        System.out.println(fileUrl);
        Parent root = FXMLLoader.load(fileUrl);
        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.show();
    }
}
