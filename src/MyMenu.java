import org.docx4j.convert.in.xhtml.XHTMLImporterImpl;
import org.docx4j.model.structure.PageSizePaper;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.*;

import static javafx.scene.input.KeyCode.K;
import static javafx.scene.input.KeyCode.O;
import static javafx.scene.input.KeyCode.Q;

/**
 * Created by Wu on 2016/12/6.
 */
public class MyMenu extends JMenuBar{
    private JFileChooser jfc_open,jfc_save, jfc_saveAs;
    private DataOutputStream outputToServer;

    public MyMenu(Editor editor , Player player,DataOutputStream outputToServer,Structure structure){
        this.outputToServer = outputToServer;
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic('F');
        this.add(fileMenu);

        this.jfc_open = new JFileChooser(new File("."));
        this.jfc_save = new JFileChooser(new File("."));
        this.jfc_saveAs = new JFileChooser(new File("."));

        jfc_open.addChoosableFileFilter(new MyFilter(".md"));
        jfc_open.addChoosableFileFilter(new MyFilter(".css"));

        jfc_save.setAcceptAllFileFilterUsed(false);
        jfc_save.addChoosableFileFilter(new MyFilter(".md"));

        jfc_saveAs.setAcceptAllFileFilterUsed(false);
        jfc_saveAs.addChoosableFileFilter(new MyFilter(".docx"));

        JMenuItem jmiOpen = new JMenuItem("Open");
        JMenuItem jmiSave = new JMenuItem("Save");
        JMenuItem jmiSaveAs = new JMenuItem("Export");

        jmiOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
        jmiOpen.setMnemonic('O');

        jmiSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
        jmiSave.setMnemonic('S');

        jmiSaveAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK));
        jmiSaveAs.setMnemonic('X');

        fileMenu.add(jmiOpen);
        fileMenu.add(jmiSave);
        fileMenu.add(jmiSaveAs);

        jmiOpen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(jfc_open.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
                    File file = jfc_open.getSelectedFile();
                    if(file.isFile()){
                        if(!file.getName().endsWith(".md")&&!file.getName().endsWith(".css")){
                            JOptionPane.showMessageDialog(null,
                                    "请选择.md或者.css文件", "系统信息", JOptionPane.ERROR_MESSAGE);
                        }
                        try {
                            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                            String line;
                            String content = "";
                            while((line = in.readLine())!=null){
                                content = content + line + "\n";
                            }
                            if(file.getName().endsWith(".md")){
                                editor.setContent(content);
                                editor.setHtml(player);
                                editor.updateStruct(structure);
                                outputToServer.writeUTF("4."+content);
                            }

                            else{
                                player.addCss(content);
                                editor.setHtml(player);
                                outputToServer.writeUTF("2."+content);
                            }
                        } catch (FileNotFoundException e1) {
                            e1.printStackTrace();
                        } catch (IOException e2) {
                            e2.printStackTrace();
                        }
                    }
                }
            }
        });
        jmiSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                File file ;
                jfc_save.setDialogTitle("保存为 .md 文件");
                jfc_save.setSelectedFile(new File("output.md"));
                int returnValue = jfc_save.showSaveDialog(null);
                if(returnValue == JFileChooser.APPROVE_OPTION){
                    file = jfc_save.getSelectedFile();
                    MyFilter filter = (MyFilter)jfc_save.getFileFilter();
                    String end = filter.getEnds();
                    File newFile;
                    if(file.getAbsolutePath().toLowerCase().endsWith(end.toLowerCase()))
                        newFile = file;
                    else
                        newFile = new File(file.getAbsolutePath()+end);

                    try {
                        FileWriter out = new FileWriter(newFile);
                        String content = editor.getContent();
                        out.write(content);
                        out.close();

                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
        jmiSaveAs.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                File file ;
                jfc_saveAs.setDialogTitle("导出为 .docx 文件");
                jfc_saveAs.setSelectedFile(new File("output.docx"));
                int returnValue = jfc_saveAs.showSaveDialog(null);
                if(returnValue == JFileChooser.APPROVE_OPTION){
                    file = jfc_saveAs.getSelectedFile();
                    MyFilter filter = (MyFilter)jfc_saveAs.getFileFilter();
                    String end = filter.getEnds();
                    File newFile;
                    if(file.getAbsolutePath().toLowerCase().endsWith(end.toLowerCase()))
                        newFile = file;
                    else
                        newFile = new File(file.getAbsolutePath()+end);
                    if(newFile.exists()){
                        JOptionPane.showMessageDialog(null, "已经有重名文件", "导出失败", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }
                    try {
                        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.createPackage(PageSizePaper.A4, true);
                        XHTMLImporterImpl xhtmlImporter = new XHTMLImporterImpl(wordMLPackage);
                        wordMLPackage.getMainDocumentPart().getContent().addAll(xhtmlImporter.convert(player.getXHTML(), null));
                        wordMLPackage.save(newFile);
                        JOptionPane.showMessageDialog(null, "已成功导出为 .docx 文件!", "导出成功", JOptionPane.INFORMATION_MESSAGE);
                    } catch (InvalidFormatException e1) {
                        e1.printStackTrace();
                    } catch (Docx4JException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
    }

}
class MyFilter extends javax.swing.filechooser.FileFilter {
    private String ext;
    public MyFilter(String extString){
        ext = extString;
    }

    @Override
    public boolean accept(File f) {
        if(f.isDirectory()) return true;
        String fileName=f.getName();
        return fileName.toLowerCase().endsWith(this.ext);
    }

    @Override
    public String getDescription() {
        return this.ext;
    }
    public String getEnds() {
        return this.ext;
    }
}