import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by Wu on 2016/12/8.
 */
public class MyFrame extends JFrame {
    public static void main(String[] args) {
        MyFrame myFrame = new MyFrame();
    }
    public MyFrame(){
        this.setName("MyMarkdownEditor");
        Player player = new Player();
        Structure structure = new Structure();

        try {
            Socket socket = new Socket(InetAddress.getByName(null),8080);
            DataInputStream inputFromServer = new DataInputStream(socket.getInputStream());
            DataOutputStream outputToServer = new DataOutputStream(socket.getOutputStream());
            Editor editor = new Editor(player,structure,outputToServer);
            MyMenu menu = new MyMenu(editor,player,outputToServer,structure);
            new Thread(new ConnectServer(socket,editor,inputFromServer,player,structure)).start();

            this.setJMenuBar(menu);
            this.setLayout(new GridLayout(1,3));

            JPanel left_editor = new JPanel();
            JPanel center_player = new JPanel();
            JPanel right_structure = new JPanel();
            left_editor.setLayout(null);
            center_player.setLayout(null);
            right_structure.setLayout(null);

            left_editor.setBorder(BorderFactory.createTitledBorder("Editor"));
            center_player.setBorder(BorderFactory.createTitledBorder("Player"));
            right_structure.setBorder(BorderFactory.createTitledBorder("Structure"));

            left_editor.add(editor);
            center_player.add(player);
            right_structure.add(structure);

            this.add(left_editor);
            this.add(center_player);
            this.add(right_structure);

            this.setSize(1200,600);
            this.setLocationRelativeTo(null);
            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            this.setVisible(true);
            this.setResizable(false);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
class ConnectServer implements Runnable{
    private Socket socket;
    Editor editor;
    DataInputStream inputFromServer;
    Player player;
    Structure structure;
    public ConnectServer(Socket socket,Editor editor,DataInputStream inputFromServer,Player player,Structure structure){
        this.editor = editor;
        this.socket = socket;
        this.inputFromServer = inputFromServer;
        this.player = player;
        this.structure = structure;
    }

    @Override
    public void run() {
        try {
            while(true){
                String newContent = inputFromServer.readUTF();
                if(newContent.startsWith("1.")){//对方键盘事件
                    editor.setSocket();
                    editor.setContent(newContent.substring(2));
                    editor.setHtml(player);
                    structure.refreshStructure(this.editor.getContent());
                }
                else if(newContent.startsWith("2.")){//对方导入css文件
                    player.addCss(newContent.substring(2));
                    editor.setHtml(player);
                }
                else if(newContent.startsWith("3.")){
                    editor.setScrollBar(Integer.valueOf(newContent.substring(2)).intValue());
                }
                else if(newContent.startsWith("4.")){//对方打开文件
                    editor.setContent(newContent.substring(2));
                    editor.setHtml(player);
                    structure.refreshStructure(this.editor.getContent());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}