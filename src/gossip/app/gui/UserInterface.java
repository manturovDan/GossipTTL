package gossip.app.gui;

import gossip.app.Runner;
import gossip.app.frame.GossipFrame;
import gossip.app.frame.GossipFrameMeaning;
import gossip.app.sender.Sender;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class UserInterface extends Application implements Runnable {
    @FXML
    ScrollPane macPane;

    @FXML
    Button sendBtn;

    @FXML
    TextField gossip;

    @FXML
    TextField ttl;

    @FXML
    Text myMac;

    private static Runner runner;

    public static void main(String[] args) {
        Application.launch();
    }

    public static void setRunner(Runner runner) {
        UserInterface.runner = runner;
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

    @FXML
    private void updClick(ActionEvent event) {
        macPane.setContent(new Text(runner.getMacTableInString()));
        myMac.setText("My Mac: " + runner.getMyMac());
    }

    @FXML
    private void send(ActionEvent send) {
        String gossipText = gossip.getText();
        int ttlVal = Integer.parseInt(ttl.getText());

        System.out.println("Sending gossip: " + gossipText + "(" + ttlVal + ")");

        GossipFrameMeaning frame = new GossipFrameMeaning(runner.getMyMac(), ttlVal, gossipText.length(), gossipText);
        Sender sender = new Sender(frame, runner);

        runner.sendRequest(sender);

    }
}
