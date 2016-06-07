package courseprojectcsaw;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;


public class GameFieldController implements Initializable {

    private static DataInputStream fromServer;
    private static DataOutputStream toServer;
    private static Socket sSocket;
    private int health = 3;
    private final byte nill = 0;
    private static byte[] dataPack = new byte[1024]; 
    @FXML Button first,second,third,four;
    @FXML Label question;
    @FXML ProgressBar progress;
    
    private void parsePack(){
        byte[] tmpArray = new byte[128];
        String tmpString;
        System.arraycopy(dataPack, 5, tmpArray, 0, 128);
        tmpString = new String(tmpArray);
        question.setText(tmpString);
        
        System.arraycopy(dataPack, 133, tmpArray, 0, 128);
        tmpString = new String(tmpArray);
        first.setText(tmpString);
        
        System.arraycopy(dataPack, 261, tmpArray, 0, 128);
        tmpString = new String(tmpArray);
        second.setText(tmpString);
        
        System.arraycopy(dataPack, 389, tmpArray, 0, 128);
        tmpString = new String(tmpArray);
        third.setText(tmpString);
        
        System.arraycopy(dataPack, 517, tmpArray, 0, 128);
        tmpString = new String(tmpArray);
        four.setText(tmpString);
    }
    
    
    private void newGame() throws IOException{
        dataPack[0] = 1;
        toServer.write(dataPack);
        fromServer.read(dataPack);
        parsePack();
    }
    
    private void endGame() throws IOException{
        dataPack[0] = 3;
        toServer.write(dataPack);
        fromServer.read(dataPack);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game over!");
        alert.setHeaderText("Game results!");
        alert.setContentText("Your result: " + dataPack[2] + " points");
        alert.showAndWait();
        Stage stage = (Stage) first.getScene().getWindow();
        stage.close();
    }
    
    private void showErrorMessage(){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("False answer!");
        //alert.setHeaderText();
        alert.setContentText("It is not a true answer");
        alert.showAndWait();
    }
    
    private void clickAction(int buttonNum) throws IOException{
        
        
        Arrays.fill(dataPack ,nill);
        dataPack[0] = 2;
        dataPack[1] = (byte)buttonNum;
        toServer.write(dataPack);
        fromServer.read(dataPack);
        
        if ((dataPack[3] != 1)){
            health--;
            progress.setProgress(progress.getProgress() - 0.33);
            if (health != 0)
                showErrorMessage();
        }
        if (health == 0)
            endGame();
        newGame();
        
    }
    
    @FXML
    private void firstClick() throws IOException{
        clickAction(1);
    }
    
    @FXML
    private void secondClick() throws IOException{
        clickAction(2);
    }
    
    @FXML
    private void thirdClick() throws IOException{
        clickAction(3);
    }
    
    @FXML
    private void fourClick() throws IOException{
        clickAction(4);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try{
            sSocket = CourseprojectCSaW.sSocket;
            fromServer = new DataInputStream(sSocket.getInputStream());
            toServer = new DataOutputStream(sSocket.getOutputStream());
            progress.setProgress(1);
            newGame();
            
        }
        catch (IOException e) {System.out.println(e);}
    }    
    
}
