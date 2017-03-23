import com.petebevin.markdown.MarkdownProcessor;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by Wu on 2016/12/6.
 */
public class Editor extends JScrollPane{
    private JTextPane editor;
    private MarkdownProcessor processor;
    private JScrollBar scrollBar;
    DataOutputStream outputToServer;
    private int isSocket;
    private Structure structure;

    public Editor(Player player,Structure structure,DataOutputStream outputToServer){
        this.editor = new JTextPane();
        this.processor = new MarkdownProcessor();
        this.setBounds(10,20,380,530);
        this.getViewport().add(editor);
        this.scrollBar = this.getVerticalScrollBar();
        this.outputToServer = outputToServer;
        this.structure = structure;
        isSocket = 0;

        this.editor.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                setHtml(player);
                updateStruct(structure);
                try {
                    outputToServer.writeUTF("1."+getContent());
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {
                setHtml(player);
                updateStruct(structure);
                try {
                    outputToServer.writeUTF("1."+getContent());
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                setHtml(player);
                updateStruct(structure);
                try {
                    outputToServer.writeUTF("1."+getContent());
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
//        this.editor.getDocument().addDocumentListener(new DocumentListener() {
//            @Override
//            public void insertUpdate(DocumentEvent e) {
//                setHtml(player);
//                updateStruct(structure);
//                if(isSocket==0){
//                    try {
//                        outputToServer.writeUTF("1."+getContent());
//                    } catch (IOException e1) {
//                        e1.printStackTrace();
//                    }
//                }
//                else if(isSocket>0){
//                    isSocket--;
//                }
//            }
//
//            @Override
//            public void removeUpdate(DocumentEvent e) {
//                setHtml(player);
//                updateStruct(structure);
//                if(isSocket==0){
//                    try {
//                        outputToServer.writeUTF("1."+getContent());
//                    } catch (IOException e1) {
//                        e1.printStackTrace();
//                    }
//                }
//                else if(isSocket>0){
//                    isSocket--;
//                }
//            }
//
//            @Override
//            public void changedUpdate(DocumentEvent e) {
//                setHtml(player);
//                updateStruct(structure);
//                if(isSocket==0){
//                    try {
//                        outputToServer.writeUTF("1."+getContent());
//                    } catch (IOException e1) {
//                        e1.printStackTrace();
//                    }
//                }
//                else if(isSocket>0){
//                    isSocket--;
//                }
//            }
//        });
        structure.getMyList().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                int index = structure.getMyList().getSelectedIndex();
                if(index <0) return;
                scrollBar.setValue((Integer)structure.getMyPlaces().get(index)*16);
                try {
                    outputToServer.writeUTF("3."+index);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    public void setHtml(Player player){
        player.setContent(this.processor.markdown(this.editor.getText()));
    }
    public String getContent(){ return this.editor.getText();}
    public void setContent(String content){ this.editor.setText(content);}
    public void updateStruct(Structure structure){structure.refreshStructure(this.editor.getText());}
    public void setSocket(){this.isSocket = 3;}
    public void setScrollBar(int value){scrollBar.setValue((Integer)this.structure.getMyPlaces().get(value)*16);}
}