package Application;

import com.fazecast.jSerialComm.SerialPort;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class PortChoiceController implements Initializable {

    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML private ChoiceBox<SerialPort> portChoice;
    @FXML private Button confirmButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        portChoice.getItems().addAll(SerialPort.getCommPorts());
    }

    public void confirm(ActionEvent event) throws IOException {
        if(portChoice.getValue() != null){

            FXMLLoader loader = new FXMLLoader(getClass().getResource("App.fxml"));
            root = loader.load();

            AppController appController = loader.getController();
            appController.setPortLabel(portChoice.getSelectionModel().getSelectedIndex());

            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            String css = this.getClass().getResource("index.css").toExternalForm();
            scene.getStylesheets().add(css);
            stage.setScene(scene);
            stage.setMinHeight(730);
            stage.setMinWidth(800);
            stage.setResizable(true);
            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    System.exit(0);
                }
            });

            stage.show();
        }
    }
}
