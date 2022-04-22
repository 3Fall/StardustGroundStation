package Application;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.io.*;

import java.util.Base64;


public class AppController{

    @FXML Label portLabel;
    @FXML Label labelTelemetry;

    @FXML ListView<String> console;

    @FXML Button buttonLaunch;
    @FXML Button buttonArmParachute;
    @FXML Button buttonEnableTVC;
    @FXML Button buttonSuspendMission;
    @FXML Button buttonChangePort;

    Package pack = new Package();

    ByteArrayOutputStream packet = new ByteArrayOutputStream();

    SerialPort serial = null;

    public void setPortLabel(int port) {

        //System.out.println(Mem.layoutString(Package.class));
        portLabel.setText("Port: " + SerialPort.getCommPorts()[port].toString());
        serial = SerialPort.getCommPorts()[port];
        serial.openPort();
        serial.addDataListener(new SerialPortDataListener() {
            @Override
            public int getListeningEvents() {
                return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
            }

            @Override
            public void serialEvent(SerialPortEvent event) {
                int size = event.getSerialPort().bytesAvailable();
                byte[] buffer = new byte[size];
                event.getSerialPort().readBytes(buffer, size);
                for(byte b : buffer){
                    addToConsole(b);
                }
            }
        });
    }

    public void addToConsole(byte item){
        if((char)item == '\n'){
            if(packet.size() > 0) {
                byte[] decoded = Base64.getDecoder().decode(packet.toByteArray());
                pack = new Package();
                ObjectDeserializer deserializer = new ObjectDeserializer();

                try {
                    deserializer.deserialize(decoded, pack);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

                System.out.println(pack.system_state);
                Platform.runLater(() -> pack.resetLabels(labelTelemetry));
            }
            packet = new ByteArrayOutputStream();
        }else{
            packet.write(item);
        }
    }

    public void launch(ActionEvent event){
        //yeaa
    }

    public void armParachute(ActionEvent event){

    }

    public void enableTVC(ActionEvent event){

    }

    public void testTVC(ActionEvent event){

    }

    public void suspendMission(ActionEvent event){

    }

    public void changePort(ActionEvent event){
        //TODO program this shit
    }

    private void sendData(byte[] arr, int datasize){
        serial.writeBytes(arr, datasize);
        byte[] endl = new byte[1];
        endl[0] = 10;
        serial.writeBytes(endl, 1);

    }
}
