/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stegcrypt;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.File;
import java.security.*;
import javax.crypto.*;
import java.io.*;
import java.util.ArrayList;
import javax.swing.border.EtchedBorder;
import javax.swing.border.MatteBorder;
import javax.swing.UIManager;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
/**
 *
 * @author Nikhil
 */
public class Main extends JFrame implements ActionListener
{
    private JRadioButton e,d;
    private ButtonGroup grp;
    private JButton submit;
    private JButton openfile,openimage,encryptedimage ;
    private String textfnm,imfnm,encryptedfnm;
    private JTextField pwd;
    
    
    public Main()
    {
        try {
                UIManager.setLookAndFeel(new NimbusLookAndFeel());
            }
        catch(Exception e) 
        {
            throw new RuntimeException(e);
        }
        
        
        this.setTitle("Secret Messenger!");
        this.setSize(750, 600);
        this.setLayout(null);
        
        e=new JRadioButton("Encryption");
        e.setBounds(40, 40, 90, 40);
        e.setSelected(true);
        this.add(e);
        
        d = new JRadioButton("Decryption");
        d.setBounds(40,250,90,40);
        this.add(d);
        
        grp = new ButtonGroup();
        grp.add(d);
        grp.add(e);
        
        submit = new JButton("submit");
        submit.setBounds(300,500,100,50);
        submit.addActionListener(this);
        this.add(submit);
        
        openfile = new JButton("Open text file");
        openfile.setBounds(50,120,150,60);
        openfile.addActionListener(this);
        this.add(openfile);
        
        openimage = new JButton("Open image");
        openimage.setBounds(250,120,150,60);
        openimage.addActionListener(this);
        this.add(openimage);
        
        encryptedimage = new JButton("Open Stego Image");
        encryptedimage.setBounds(50,330,150,60);
        encryptedimage.addActionListener(this);
        this.add(encryptedimage);
        
        textfnm = null;
        imfnm = null;
        encryptedfnm = null;
        
        pwd = new JTextField("password [Optional]");
        pwd.setBounds(450,120,200,60);
        this.add(pwd);
        
        
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
       
        
        
    }
    

    
    public static void main(String[] args)
    {
        Main m = new Main();
    }

    @Override
    public void actionPerformed(ActionEvent ae) 
    {
        Object o = ae.getSource();
        if(o instanceof JButton)
        {
            JButton btn = (JButton) o;
            
            if(btn.equals(openfile))
            {   
                JFileChooser fc = new JFileChooser();
                fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fc.setDialogTitle("Open a text file.");
                fc.setFileFilter(new txtfilter());
                fc.setAcceptAllFileFilterUsed(false);
                int result = fc.showOpenDialog(this); 
                if (result == JFileChooser.APPROVE_OPTION)
                {
                    File f = fc.getSelectedFile();
                    textfnm = f.getAbsolutePath();
                }
                
            }
            if (btn.equals(openimage))
            {
                JFileChooser fc = new JFileChooser();
                fc.setDialogTitle("Open a PNG image.");
                fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fc.setFileFilter(new pngfilter());
                fc.setAcceptAllFileFilterUsed(false);
                int result = fc.showOpenDialog(this);
                if (result == JFileChooser.APPROVE_OPTION)
                {
                    
                    File f = fc.getSelectedFile();
                    imfnm = f.getAbsolutePath();
                }
                
            }
            
            if(btn.equals(encryptedimage))
            {
                JFileChooser fc = new JFileChooser();
                fc.setDialogTitle("Open a PNG image.");
                fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fc.setFileFilter(new pngfilter());
                fc.setAcceptAllFileFilterUsed(false);
                int result = fc.showOpenDialog(this);
                if (result == JFileChooser.APPROVE_OPTION)
                {
                    
                    File f = fc.getSelectedFile();
                    encryptedfnm = f.getAbsolutePath();
                }
                
            }
            if (btn.equals(submit))
            {
                ArrayList errorlist = new ArrayList();
                if(e.isSelected())
                {
                    if(textfnm == null)
                        errorlist.add("\nPlease select a text file!");
                    if (imfnm == null)
                        errorlist.add("\nPlease select a PNG image!");
                    
                    StegCrypt stegcrypt = new StegCrypt();
                    stegcrypt.setpassword(pwd.getText());
                    stegcrypt.hide(textfnm, imfnm);
                }
                
                else //if (d.isSelected())
                {
                    if (encryptedfnm == null)
                        errorlist.add("\n Please select a PNG image!");
                    StegCrypt stegcrypt = new StegCrypt();
                    stegcrypt.reveal(encryptedfnm);
                    
                   
                }
                
                if (!errorlist.isEmpty())
                {
                    JOptionPane.showMessageDialog(null,errorlist,"Error!", JOptionPane.ERROR_MESSAGE);
                }
                else
                {
                    JOptionPane.showMessageDialog(null,"Encryption/Decryption completed succesfully!","Success!",JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }
       
    }
    
}