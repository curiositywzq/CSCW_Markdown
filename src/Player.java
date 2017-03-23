import org.jsoup.Jsoup;

import javax.swing.*;
import javax.swing.text.html.HTMLEditorKit;

/**
 * Created by Wu on 2016/12/6.
 */
public class Player extends JScrollPane{
    private JEditorPane playerPane;
    private HTMLEditorKit kit;
    public Player(){
        this.kit = new HTMLEditorKit();
        this.playerPane = new JEditorPane();
        this.playerPane.setContentType("text/html");
        this.playerPane.setEditorKit(this.kit);
        this.playerPane.setEditable(false);
        this.setBounds(10,20,380,530);
        this.getViewport().add(playerPane);
    }
    public void setContent(String word){
        this.playerPane.setText(word);
    }
    public void addCss(String css){this.kit.getStyleSheet().addRule(css);}
    public String getXHTML(){return Jsoup.parse(this.playerPane.getText()).html();}
}
