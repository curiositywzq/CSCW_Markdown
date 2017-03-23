import com.petebevin.markdown.MarkdownProcessor;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

/**
 * Created by Wu on 2016/12/6.
 */
public class Structure extends JScrollPane{
    private DefaultListModel myModel;
    private JList myList;
    private ArrayList<Integer> places;
    private MarkdownProcessor processor;

    public Structure(){
        myModel = new DefaultListModel();
        myList = new JList();
        myList.setModel(myModel);
        this.setBounds(10,20,380,530);
        this.getViewport().add(myList);
        places = new ArrayList<Integer>();
        processor = new MarkdownProcessor();
    }
    public void refreshStructure(String content){
        int index =0;
        this.myModel.removeAllElements();
        this.places.clear();
        BufferedReader in = new BufferedReader(new StringReader(content));
        String line;
        int row =0;
        try {
            while ((line = in.readLine())!=null){
                line = processor.markdown(line);
                if(line.startsWith("<h1>")){
                    this.places.add(row);
                    this.myModel.add(index++,line.substring(line.indexOf("<h1>")+4,line.indexOf("</h1>")));
                }
                else if(line.startsWith("<h2>")){
                    this.places.add(row);
                    this.myModel.add(index++,"  "+line.substring(line.indexOf("<h2>")+4,line.indexOf("</h2>")));
                }
                else if(line.startsWith("<h3>")){
                    this.places.add(row);
                    this.myModel.add(index++,"    "+line.substring(line.indexOf("<h3>")+4,line.indexOf("</h3>")));
                }
                else if(line.startsWith("<h4>")){
                    this.places.add(row);
                    this.myModel.add(index++,"      "+line.substring(line.indexOf("<h4>")+4,line.indexOf("</h4>")));
                }
                else if(line.startsWith("<h5>")){
                    this.places.add(row);
                    this.myModel.add(index++,"        "+line.substring(line.indexOf("<h5>")+4,line.indexOf("</h5>")));
                }
                row++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public JList getMyList(){return this.myList;}
    public ArrayList getMyPlaces(){return places;}
}