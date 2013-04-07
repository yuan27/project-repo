import javax.swing.*; 
import java.util.*; 
import java.io.*; 
import java.awt.*;
import java.awt.event.*; 
import javax.swing.border.*;


public class integrityVerifier extends JFrame implements ActionListener{
    // The preferred size of the editor
    final int PREFERRED_WIDTH = 1000;
    final int PREFERRED_HEIGHT = 700;
    
    int mode=1;
    
    //Display Panels
    JPanel _settings=new JPanel();
    
    JScrollPane _rightTextPane=new JScrollPane();
    JTextArea _rightTextarea;
    
    JScrollPane _upTextPane=new JScrollPane();
    JTextArea _upTextarea;
    
    /*Edit Panel*/
    JPanel upper=new JPanel();
    
    JPanel editor=new JPanel();
    //buttons
    JLabel proj=new JLabel("Projects:");
    JButton part1a=new JButton("Part1a");
    JButton part1b=new JButton("Part1b");
    JButton part2=new JButton("Part2");
    //JButton part2=new JButton("Part-2");
    JButton help=new JButton("Help");
    
    
    /*Setting Panel*/
    JLabel currentTree=new JLabel("Part1a");
    JLabel dummy=new JLabel("");
    
    JPanel box1=new JPanel();
    TitledBorder border1=BorderFactory.createTitledBorder("Tree Construction");
    
    JLabel msgFileLabel=new JLabel("Message File:");
    JTextField msgFileTextField=new JTextField(30);
    JButton msgFileButton=new JButton("Open");
    
    JLabel const_keyFileLabel=new JLabel("Key File:");
    JTextField const_keyFileTextField=new JTextField(30);
    JButton const_keyFileButton=new JButton("Open");
    
    JLabel const_outFileLabel=new JLabel("Output File:");
    JTextField const_outFileTextField=new JTextField(30);

    JButton saveButton1=new JButton("Construct");
    
    JPanel box2=new JPanel();
    TitledBorder border2=BorderFactory.createTitledBorder("Tree Verification");
    
    JLabel treeFileLabel=new JLabel("Tree File:");
    JTextField treeFileTextField=new JTextField(30);
    JButton treeFileButton=new JButton("Open");
    
    JLabel ver_keyFileLabel=new JLabel("Key File:");
    JTextField ver_keyFileTextField=new JTextField(30);
    JButton ver_keyFileButton=new JButton("Open");
    
    JLabel ver_outFileLabel=new JLabel("Output File:");
    JTextField ver_outFileTextField=new JTextField(30);
    
    
    JButton saveButton2=new JButton("Verify");
    
    /*Huffman Section*/
    String const_msgFile;
    String const_keyFile;
    String const_outFile="";
    String ver_treeFile;
    String ver_keyFile;
    String ver_outFile="";
    
    
    public static void main(String[] args) { 
        integrityVerifier intver = new integrityVerifier(); 
        intver.setVisible(true);
    }  
    
    
    public integrityVerifier(){
        //set frame properties
        setTitle("Integrity Verifier");
        setSize(PREFERRED_WIDTH, PREFERRED_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// Close when closed.For reals.
        
        /*Setting Panel*/
        _settings.setPreferredSize(new Dimension(400, 300));
        _settings.add(currentTree);
        _settings.add(box1);
        _settings.add(saveButton1);
        _settings.add(box2);
        _settings.add(saveButton2);
        
        
        box1.setBorder(border1);
        box1.setLayout(new SpringLayout());
        box1.setPreferredSize(new Dimension(370, 140));
        
        
        box2.setBorder(border2);
        box2.setLayout(new SpringLayout());
        box2.setPreferredSize(new Dimension(370, 140));
        
        box1.add(msgFileLabel);
        box1.add(msgFileTextField);
        box1.add(msgFileButton);
        box1.add(const_keyFileLabel);
        box1.add(const_keyFileTextField);
        box1.add(const_keyFileButton);
        box1.add(const_outFileLabel);
        box1.add(const_outFileTextField);
        box1.add(dummy);
        SpringUtilities.makeCompactGrid(box1, 3, 3, //rows, cols
                                        6, 6,        //initX, initY
                                        6, 6);       //xPad, yPad
        
        box2.add(treeFileLabel);
        box2.add(treeFileTextField);
        box2.add(treeFileButton);
        box2.add(ver_keyFileLabel);
        box2.add(ver_keyFileTextField);
        box2.add(ver_keyFileButton);
        box2.add(ver_outFileLabel);
        box2.add(ver_outFileTextField);
        box2.add(dummy);
        SpringUtilities.makeCompactGrid(box2, 3, 3, //rows, cols
                                        6, 6,        //initX, initY
                                        6, 6);       //xPad, yPad
        
        /*Text area*/
        _rightTextarea=new JTextArea("", 20, 20);
        _rightTextarea.setEditable(false);
        _rightTextPane.getViewport().add(_rightTextarea);
        
        _upTextarea=new JTextArea("Welcome to Integrity Verifier!\nPlease start by selecting a project.", 4, 20);
        _upTextarea.setEditable(false);
        _upTextPane.getViewport().add(_upTextarea);
        _upTextPane.setPreferredSize(new Dimension(700, 68));
        
        //add components to frame
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(upper,BorderLayout.NORTH);
        getContentPane().add(_settings,BorderLayout.WEST);
        getContentPane().add(_rightTextPane,BorderLayout.CENTER);
        
        //add actionlistener to the menuitems
        part1a.addActionListener(this);
        part1b.addActionListener(this);
        part2.addActionListener(this);
        help.addActionListener(this);
        msgFileButton.addActionListener(this);
        const_keyFileButton.addActionListener(this);
        treeFileButton.addActionListener(this);
        ver_keyFileButton.addActionListener(this);
        saveButton1.addActionListener(this);
        saveButton2.addActionListener(this);
        
        //add menuitems to menus
        editor.add(proj);
        editor.add(part1a);
        editor.add(part1b);
        editor.add(part2);
        editor.add(help);
        
        
        //set up the upper part
        upper.add(editor);
        upper.add(_upTextPane);
        upper.setPreferredSize(new Dimension(700, 133));
    }
    
    public void actionPerformed(ActionEvent e){
        Object source=e.getSource();
        if(source==msgFileButton){
            String prompt=String.format("Please choose %s file.",(mode==1||mode==3)?"data":"message");
            String res=chooseFile(prompt);
            msgFileTextField.setText(res);
        }else if(source==const_keyFileButton){
            String res=chooseFile("Please choose key file.");
            const_keyFileTextField.setText(res);
        }else if(source==treeFileButton){
            String res=chooseFile("Please choose tree file.");
            treeFileTextField.setText(res);
        }else if(source==ver_keyFileButton){
            String res=chooseFile("Please choose key file.");
            ver_keyFileTextField.setText(res);
        }else if(source==saveButton1){
            const_msgFile=msgFileTextField.getText();
            const_keyFile=const_keyFileTextField.getText();
            if(mode==1){
                BSTree bst=new BSTree(false, const_msgFile, const_keyFile);
                String res=bst.getBFSTree();
                _rightTextarea.setText(res);
                
                const_outFile=const_outFileTextField.getText();
                if(const_outFile.length()!=0){
                    printStringToFile(res,const_outFile);
                    _upTextarea.setText("Tree is successfully constructed.\nPlease look at the output file and textfield below to see the result.");
                }else{
                    _upTextarea.setText("Tree is successfully constructed.\nPlease look at the textfield below to see the result.\n");
                }
            }else if(mode==2){
                HCTree hct=new HCTree(const_msgFile, const_keyFile);
                String res=hct.getBFSTree();
                _rightTextarea.setText(res);
                
                const_outFile=const_outFileTextField.getText();
                if(const_outFile.length()!=0){
                    printStringToFile(res,const_outFile);
                    _upTextarea.setText("Tree is successfully constructed.\nPlease look at the output file and textfield below to see the result.");
                }else{
                    _upTextarea.setText("Tree is successfully constructed.\nPlease look at the textfield below to see the result.\n");
                }
            }else if(mode==3){
                BSTree bst=new BSTree(true, const_msgFile, const_keyFile);
                String res=bst.getBFSTree();
                _rightTextarea.setText(res);
                
                const_outFile=const_outFileTextField.getText();
                if(const_outFile.length()!=0){
                    printStringToFile(res,const_outFile);
                    _upTextarea.setText("Tree is successfully constructed.\nPlease look at the output file and textfield below to see the result.");
                }else{
                    _upTextarea.setText("Tree is successfully constructed.\nPlease look at the textfield below to see the result.\n");
                }
            }
            
        }else if(source==saveButton2){
            ver_treeFile=treeFileTextField.getText();
            ver_keyFile=ver_keyFileTextField.getText();
            if(mode==1){
                BSTree bst=new BSTree(ver_treeFile, ver_keyFile, false);
                String res=bst.getResult(false);
                _rightTextarea.setText(res);
                
                ver_outFile=ver_outFileTextField.getText();
                if(ver_outFile.length()!=0){
                    printStringToFile(res,ver_outFile);
                    _upTextarea.setText("Tree is successfully verified.\nPlease look at the output file and textfield below to see the result.");
                }else{
                    _upTextarea.setText("Tree is successfully verified.\nPlease look at the textfield below to see the result.\n");
                }
            }else if(mode==2){
                HCTree hct=new HCTree(ver_treeFile, ver_keyFile,true);
                String res=hct.getResult();
                _rightTextarea.setText(res);
                
                ver_outFile=ver_outFileTextField.getText();
                if(ver_outFile.length()!=0){
                    printStringToFile(res,ver_outFile);
                    _upTextarea.setText("Tree is successfully verified.\nPlease look at the output file and textfield below to see the result.");
                }else{
                    _upTextarea.setText("Tree is successfully verified.\nPlease look at the textfield below to see the result.\n");
                }
            }else if(mode==3){
                BSTree bst=new BSTree(ver_treeFile, ver_keyFile, true);
                String res=bst.getResult(true);
                _rightTextarea.setText(res);
                
                ver_outFile=ver_outFileTextField.getText();
                if(ver_outFile.length()!=0){
                    printStringToFile(res,ver_outFile);
                    _upTextarea.setText("Tree is successfully verified.\nPlease look at the output file and textfield below to see the result.");
                }else{
                    _upTextarea.setText("Tree is successfully verified.\nPlease look at the textfield below to see the result.\n");
                }
                
            }
        }else if(source==part1a){
            changeTo1A();
        }else if(source==part1b){
            changeTo1B();
        }else if(source==part2){
            changeTo2();
        }else if(source==help){
            changeToHelp();
        }
        
    }
    
    public void changeTo1A(){
        mode=1;
        _upTextarea.setText("Part1a.");
        currentTree.setText("Part1a");
        msgFileLabel.setText("Data File:");
        setInputable(true);  
        
    }
    
    public void changeTo1B(){
        mode=2;
        _upTextarea.setText("Part1b.");
        currentTree.setText("Part1b");
        msgFileLabel.setText("Message File:");
        setInputable(true);       
        
    }
    
    public void changeTo2(){
        mode=3;
        _upTextarea.setText("Part2.");
        currentTree.setText("Part2");
        msgFileLabel.setText("Data File:");
        setInputable(true);      
        
    }
    
    public void changeToHelp(){
        mode=4;
        _upTextarea.setText("Help.");
        currentTree.setText("Help");
        _rightTextarea.setText("Help:\n1. To get a tree, click on the part button above first.\n"+
              "\tThen input the filenames in 'Tree Construction' box.\n\n"+"2. To test a tree, click on the part button above first.\n"+
              "\tThen input the filenames in 'Tree Verification' box.\n\n"+"3. If you still need help, email us.\n");
        setInputable(false);
    }
    
    public void setInputable(boolean val){
        msgFileButton.setEnabled(val);
        const_keyFileButton.setEnabled(val);
        treeFileButton.setEnabled(val);
        ver_keyFileButton.setEnabled(val);
        saveButton1.setEnabled(val);
        saveButton2.setEnabled(val);
        
        msgFileTextField.setEditable(val);
        const_keyFileTextField.setEditable(val);
        treeFileTextField.setEditable(val);
        ver_keyFileTextField.setEditable(val);
        const_outFileTextField.setEditable(val);
        ver_outFileTextField.setEditable(val);
    }
    
    /*********************************Helper functions*********************************/ 
    public String chooseFile(String prompt){
        String currdir=System.getProperty("user.dir");
        JFileChooser fc=new JFileChooser(currdir);
        fc.setDialogTitle(prompt);
        fc.setDragEnabled(false);
        
        int val=fc.showOpenDialog(this);
        if(val==JFileChooser.CANCEL_OPTION || val==JFileChooser.ERROR_OPTION){
            return "";
        }
        
        File file = fc.getSelectedFile();
        String path=file.getPath();
        return path.substring(currdir.length()+1);
    }
    
    public boolean printStringToFile(String str, String filename){
        if(filename.length()==0)
            return false;
        try{
            PrintWriter out = new PrintWriter(filename);
            out.print(str);
            out.close();
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
}