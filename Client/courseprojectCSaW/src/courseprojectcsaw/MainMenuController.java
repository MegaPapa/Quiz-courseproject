package courseprojectcsaw;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import com.mysql.jdbc.Driver;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.sql.DriverManager;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javax.swing.JOptionPane;


public class MainMenuController implements Initializable {

    @FXML Button sendButton,createServer,connectButton;
    @FXML TextField messageField,fieldIP,fieldPort;
    @FXML TitledPane usersPanel;
    @FXML protected TextArea allMessagesField;
    @FXML Label labelHello,labelScore,labelWaiting;
    private static TextArea tmpArea;
    //@FXML protected TextArea tmpField;
    private static Socket sSocket;
    private static Parent rootGameField;
    private static ServerSocket csSocket;
    private static Thread waitining;
    private boolean newGame = false;
    private static DataInputStream fromServer;
    private static DataOutputStream toServer;
    private static int userPort;
    private static boolean isServered = false;
    private static byte[] dataPack = new byte[1024]; 
    //public static String str;
    
    private static Connection connection;
    private static Statement statement;
    private static ResultSet resultSet;
            
    @FXML
    public void sendMessage(ActionEvent event) throws IOException{
        toServer.writeInt(1);
        toServer.writeUTF(messageField.getText());
        //allMessagesField.appendText(str+"\n");
        
    }
    
    @FXML
    private void exitFromGame() throws IOException{
        sSocket.close();
        System.exit(1);
    }
    
    
    
    
    @FXML
    private void peerConnect() throws IOException{
        String ip = fieldIP.getText();
        int port = Integer.parseInt(fieldPort.getText());
        //тут бы неплохо проверку
        Socket peerSocket = new Socket(ip,port);
        //отображение новой формы
        FXMLDocumentController("Client");
    }
    
 
//Метод запуска формы 2 для дальнейшего диалога
    protected void FXMLDocumentController(String whoConnecting) throws IOException {
        //Загрузили ресурс файла
        Stage stage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("gameField.fxml"));
        Scene scene = new Scene(root);
        stage.setTitle("Game starting " + whoConnecting);
        stage.initStyle(StageStyle.UTILITY);
        stage.setScene(scene);
        stage.show();
    }
    
    @FXML
    private void startNewGame() throws IOException{
        FXMLDocumentController("");
    }
 
    @FXML
    private void watchConfig(){
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Configuration");
        alert.setHeaderText("Your personal configuration");
        alert.setContentText("Your IP:PORT adress: " + sSocket.getInetAddress() + ":" + userPort
        + "\nUse this configuration for the game. Good game!");
        alert.showAndWait();
    }
    
    @FXML
    private void showAbout(){
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("About information");
        alert.setContentText("'Quiz game' - This course project created by Maxim Petrusevich (CSaN 2016)");
        alert.showAndWait();
    }
    
    
    @FXML
    private void updateResultTable() throws Exception{
        String name;
        int score,userScore = 0;
        rootGameField = FXMLLoader.load(getClass().getResource("gameField.fxml"));
        allMessagesField.setText("");
        labelHello.setText("Hello, " + CourseprojectCSaW.USERNAME + "!");
        statement = connection.createStatement();
        resultSet = statement.executeQuery("select name,score from users");
        while (resultSet.next()) {
            name = resultSet.getString(1);
            score = resultSet.getInt(2);
            allMessagesField.appendText(name + " - " + score + " points" + "\n");
        }
        resultSet = statement.executeQuery("select score from users where name="+"'"+ CourseprojectCSaW.USERNAME + "'");
        while (resultSet.next()) {
            userScore = resultSet.getInt(1);
        }
        labelScore.setText("Your score: " + userScore);
    }
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        sSocket = CourseprojectCSaW.sSocket;
        //tmpArea = allMessagesField;
        try {
            try{
                Class.forName("com.mysql.jdbc.Driver");
                connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/game?zeroDateTimeBehavior=convertToNull", "root", "");
                updateResultTable();
        }
        catch (Exception e) {System.out.println(e);}
            fromServer = new DataInputStream(sSocket.getInputStream());
            toServer = new DataOutputStream(sSocket.getOutputStream());
            //toServer.writeInt(1488);
            //new serverListener(sSocket,allMessagesField).start();
        } catch (IOException ex) {
            Logger.getLogger(MainMenuController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    
    
}
