package gossip.app.gui;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class UserInterface extends Application implements Runnable {
    @FXML
    ScrollPane infoPane;

    @FXML
    Button sendBtn;

    public static void main(String[] args) {
        Application.launch();
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader();

        loader.setLocation(getClass().getResource("window.fxml"));
        Parent root = loader.load();

        primaryStage.setTitle("GossipTTL");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    @Override
    public void run() {
        Application.launch();
    }

    public void setTextToInfoPane() {
        setTextToInfoPaneCalled(null);
    }

    @FXML
    private void setTextToInfoPaneCalled(ActionEvent event) {
        System.out.println("call action");
        sendBtn.setText("Hello");
    }

    @FXML
    private void click(ActionEvent event) {
        infoPane.setContent(new Text("hello"));
    }
}
