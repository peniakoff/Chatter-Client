package sample;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ResourceBundle;

@ClientEndpoint
public class Controller implements Initializable {

    @FXML
    private Button sendMessage;

    @FXML
    private TextArea textMessage;

    @FXML
    private TextArea textArea;

    private Session session;
    private WebSocketContainer container;

    public Controller() {

    }

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        System.out.println("Nawiązano połączenie z serwerem.");
    }

    public void sendMessage(String message) {
        try {
            session.getBasicRemote().sendBinary(ByteBuffer.wrap(message.getBytes())); //basicRemote otwiera wątek przesyłania binarnego, w którym przesyłamy "zwrapowane" bajty wiadomości
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getTextFromField() {
        String text = textMessage.getText();
        textMessage.clear();
        return text;
    }

    @OnMessage //odbieranie danych ze streamu (z JEE)
    public void onMessage(Session session, ByteBuffer byteBuffer) {
        textArea.appendText(
                new String(byteBuffer.array()) + "\n");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        URI uri = URI.create("ws://localhost:8080/chat"); //WebSocket's address
//        URI uri = URI.create("ws://5.135.218.27:8081/chat");
        container = ContainerProvider.getWebSocketContainer(); //przypisany silnik implementujący narzędzie dla WebSocketu
        try {
            container.connectToServer(this, uri);
        } catch (DeploymentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        sendMessage.setOnMouseClicked(e -> sendMessage(getTextFromField()));

        /*
        Methods for sending the message and clear the message text area.
         */
        textMessage.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                sendMessage(textMessage.getText());
            }
        });

        textMessage.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                textMessage.clear();
            }
        });

    }



}
