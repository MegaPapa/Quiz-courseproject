package servercp;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.Random;
import com.mysql.jdbc.Driver;
import java.util.Arrays;

public class clientThread extends Thread{
    
    private static Socket cSocket;
    private static messageThread messanger;
    private static String USERNAME;
    private int PORT,port,gameScore = 0;
    private static DataInputStream fromClient;
    private static DataOutputStream toClient;
    private byte[] data = new byte[1024];
    private int lastID;
    private static Connection connection;
    private static Statement statement;
    private static ResultSet resultSet;
    private static boolean isAll = false;
    
    clientThread(messageThread messanger,Socket cSocket,int PORT){
        this.messanger = messanger;
        this.cSocket = cSocket;
        this.PORT = PORT;
        
        try{
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/game?zeroDateTimeBehavior=convertToNull", "root", "");
        }
        catch (Exception e){ System.out.println(e); }
    }
    
    private static boolean checkUsers() throws IOException{
        String name,password;
        name = fromClient.readUTF();
        password = fromClient.readUTF();
        try{
            statement = connection.createStatement();
            resultSet = statement.executeQuery("select name,password from users");
            while (resultSet.next()) {
                String comparedName = resultSet.getString(1);
                String comparedPassword = resultSet.getString(2);
                if ((comparedName.equals(name)) && (comparedPassword.equals(password))){
                    USERNAME = comparedName;
                    return true;
                }
            }
        }
        catch (Exception e) {System.out.println(e);}
        return false;
    }
    
    private static boolean checkName(String name) throws IOException,Exception{
        statement = connection.createStatement();
        resultSet = statement.executeQuery("select name from users");
        while (resultSet.next()) {
            String comparedName = resultSet.getString(1);
            if (comparedName.equals(name)){
                return false;
            }
        }
        return true;
    }
    
    private static void addNewUser() throws Exception{
        String name,password;
        name = fromClient.readUTF();
        password = fromClient.readUTF();
        if (checkName(name)){
            PreparedStatement preparedStmt = connection.prepareStatement("insert into users (name,password,score) value (?,?,?)");
            preparedStmt.setString (1, name);
            preparedStmt.setString (2, password);
            preparedStmt.setInt(3, 0);
            preparedStmt.execute();
            System.out.println("REGISTRATION_THREAD : added new user " + name);
            toClient.writeInt(1);
        }
        else{
            toClient.writeInt(0);
            
        }
    }
    
    private static void signIn() throws IOException,Exception{
        boolean signing = false;
        final int REG_COM = 1,SIGN_COM = 0,ACCEPTCODE = 0,RESETCODE = 1;
        int INSTRUCTION = -1;
        String name,pass;
        while (!signing) {
            INSTRUCTION = fromClient.readInt();
            System.out.println(INSTRUCTION);
            if (INSTRUCTION == SIGN_COM){
                if (checkUsers()){
                    signing = true;
                    messanger.addClientSocket(cSocket);
                    toClient.writeInt(ACCEPTCODE);
                    InetAddress address = InetAddress.getLocalHost();
                    String ip = address.getHostAddress();
                    int port = cSocket.getPort();
                }
                else
                    toClient.writeInt(RESETCODE);
            }
            else if (INSTRUCTION == REG_COM){
                try{
                    addNewUser();
                }
                catch (Exception e) {System.out.println(e);}
            }
                
        }
    }
    
    private int generateRandom(int border){
        Random rand = new Random();
        return (rand.nextInt(border) + 1);
    }
    
    private int getDBCount() throws Exception{
        statement = connection.createStatement();
        resultSet = statement.executeQuery("select * from questions");
        resultSet.last();
        return resultSet.getRow();
    }
    
    private void packQuestion(String question,String first,String second,String third,String four,byte answer){
        byte[] tmpString = new byte[128];
        byte nill = 0;
        Arrays.fill(data, nill);
        data[1] = answer;
        
        tmpString = question.getBytes();
        System.arraycopy(tmpString,0,data,5,tmpString.length);
        
        tmpString = first.getBytes();
        System.arraycopy(tmpString,0,data,133,tmpString.length);
        
        tmpString = second.getBytes();
        System.arraycopy(tmpString,0,data,261,tmpString.length);
        
        tmpString = third.getBytes();
        System.arraycopy(tmpString,0,data,389,tmpString.length);
        
        tmpString = four.getBytes();
        System.arraycopy(tmpString,0,data,517,tmpString.length);
        
    }
    
    //data[0] - комманда
    //data[1] - номер ответа
    //data[2] - заработанные баллы
    //data[3] - байт подтверждения
    //data[4] - rezerv
    
    private void sendQuestion() throws IOException,Exception{
        int rand_id;
        byte answer = 0;
        String question = "",firstansw = "",secondansw = "",thirdansw = "",fouransw = "";
        rand_id = generateRandom(getDBCount());
        lastID = rand_id;
        statement = connection.createStatement();
        resultSet = statement.executeQuery("select question,firstansw,secondansw,thirdansw,fouransw,answer from questions where id=" + rand_id);
        while (resultSet.next()) {
            question = resultSet.getString(1);
            firstansw = resultSet.getString(2);
            secondansw = resultSet.getString(3);
            thirdansw = resultSet.getString(4);
            fouransw = resultSet.getString(5);
            answer = resultSet.getByte(6);
        }
        
        packQuestion(question,firstansw,secondansw,thirdansw,fouransw,answer);
        toClient.write(data);
    }
    
    private void checkAnswer() throws IOException,Exception{
        int answer = -1;
        statement = connection.createStatement();
        resultSet = statement.executeQuery("select answer from questions where id=" + lastID);
        while (resultSet.next()) {
            answer = resultSet.getByte(1);
        }
        if (answer == data[1]){
            data[3] = 1;
            toClient.write(data);
            gameScore = gameScore + 5 + lastID;
        }
        else{
            data[3] = -1;
            toClient.write(data);
            gameScore = gameScore - 10;
        }
    }
    
    private void gameOver() throws IOException,Exception{
        int score = 0;
        statement = connection.createStatement();
        resultSet = statement.executeQuery("select score from users where name='"+USERNAME+"'");
        while (resultSet.next()) {
            score = resultSet.getInt(1);
        }
        score = score + gameScore;
        PreparedStatement preparedStmt = connection.prepareStatement("update users set score = ? where name=?");
        preparedStmt.setInt(1,score);
        preparedStmt.setString(2,USERNAME);
        preparedStmt.execute();
        data[2] = (byte)gameScore;
        toClient.write(data);
        gameScore = 0;
        
    }
    
    public void run(){
        try{
            byte nill = 0;
            
            fromClient = new DataInputStream(cSocket.getInputStream());
            toClient = new DataOutputStream(cSocket.getOutputStream());
            signIn();
            while (true){
                Arrays.fill(data, nill);
                fromClient.read(data);
                switch (data[0]){
                    case 1:
                        System.out.println("Client send command " + data[0]);
                        sendQuestion();
                        break;
                    case 2:
                        System.out.println("Client send command " + data[0]);
                        checkAnswer();
                        break;
                    case 3:
                        System.out.println("Client send command " + data[0]);
                        gameOver();
                        break;
                }
            }
        }
        catch (IOException e){System.out.println(e);}
        catch (Exception e){System.out.println(e);}
    }
    
}
