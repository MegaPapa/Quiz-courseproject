package courseprojectcsaw;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javax.swing.JOptionPane;
import java.sql.*;


public class CourseprojectCSaW extends Application{
    protected static Socket sSocket;
    private static DataInputStream fromServer;
    private static DataOutputStream toServer;
    private static Stage tmpStage;
    private static Parent root;
    protected static String USERNAME;
    private static boolean isSigning = false;
    
    @FXML
    private Button signButton;
    @FXML
    private Button registerButton;
    @FXML
    private TextField nameField;
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private void registerNewUser(ActionEvent event) throws IOException{
        int CODE = -1;
        if ((nameField.getText().equals("")) || (passwordField.getText().equals("")))
            JOptionPane.showMessageDialog(null, "Error");
        else{
            toServer.writeInt(1);
            toServer.writeUTF(nameField.getText());
            toServer.writeUTF(passwordField.getText());
            if (fromServer.readInt() == 1)
                showMessage("Hello!", "Hello, " + nameField.getText() + "!", "Thanks for registration in our game!");
            else
                showMessage("Error", "Registration error", "Invalid username. Please change username and repeat.");
            //System.out.println("neverno");
        }
    }
    
    public static void showMessage(String title,String header,String text){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(text);
        alert.showAndWait();
    }
    
    private void ClosePanel() throws IOException{
        root = FXMLLoader.load(getClass().getResource("mainMenu.fxml"));
        Scene scene = new Scene(root);
        tmpStage.setTitle("Game");
        tmpStage.setScene(scene);
        tmpStage.show();
        
    }
    
    @FXML
    public void signIn(ActionEvent event) throws IOException {
        int CODE = 1;
            if ((nameField.getText().equals("")) || (passwordField.getText().equals("")))
                JOptionPane.showMessageDialog(null, "Error");
            else{
                toServer.writeInt(0);
                toServer.writeUTF(nameField.getText());
                toServer.writeUTF(passwordField.getText());
                CODE = fromServer.readInt();
            }
            if (CODE == 0){
                System.out.println("OK");
                USERNAME = nameField.getText();
                ClosePanel();
            }
            else{
                System.out.println("RESET");
                showMessage("Sign error", "Error", "Invalid password or username!");
            }
    }
    
    @Override
    public void start(Stage primaryStage) throws IOException {
        tmpStage = primaryStage;
        sSocket = new Socket("localhost",60000);
        fromServer = new DataInputStream(sSocket.getInputStream());
        toServer = new DataOutputStream(sSocket.getOutputStream());
        root = FXMLLoader.load(getClass().getResource("mainForm.fxml"));
        Scene scene = new Scene(root);
        primaryStage.initStyle(StageStyle.UTILITY);
        tmpStage.setTitle("Sign in");
        tmpStage.setResizable(false);
        tmpStage.setScene(scene);
        tmpStage.show();
    }

   
    public static void main(String[] args) {
        launch(args);
    }
    
}
