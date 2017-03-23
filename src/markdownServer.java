import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Wu on 2016/12/8.
 */
public class markdownServer {
    public static void main(String[] args) {
        new markdownServer();
    }
    public markdownServer(){
        try {
            ServerSocket serverSocket = new ServerSocket(8080);
            Socket socket1 = serverSocket.accept();
            System.out.println("1 connect");
            Socket socket2 = serverSocket.accept();
            System.out.println("2 connect");
            HandleAClient task1_2 = new HandleAClient(socket1,socket2);
            HandleAClient task2_1 = new HandleAClient(socket2,socket1);
            new Thread(task1_2).start();
            new Thread(task2_1).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
class HandleAClient implements Runnable{
    private Socket socket1;
    private Socket socket2;

    public HandleAClient(Socket socket1,Socket socket2){
        this.socket1 = socket1;
        this.socket2 = socket2;
    }

    @Override
    public void run() {
        try {

            DataInputStream inputFromClient = new DataInputStream(socket1.getInputStream());
            DataOutputStream outputToClient = new DataOutputStream(socket2.getOutputStream());
            while (true) {
                String str = inputFromClient.readUTF();
                outputToClient.writeUTF(str);
            }
        } catch (IOException e) {
                e.printStackTrace();
            }
        }
}
