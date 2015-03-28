/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stegcrypt;

import java.io.*;
import javax.swing.filechooser.*;

/**
 *
 * @author Nikhil
 */
public class pngfilter extends javax.swing.filechooser.FileFilter
{

    @Override
    public boolean accept(File file) 
    {
       if (file.isDirectory())
       {
           return true;
       }
       String name = file.getName();
       if(name.matches(".*\\.png"))
       {
           return true;
       }
       else 
           return  false;
    }

    @Override
    public String getDescription() 
    {
        return "PNG files (*.png)";
    }
    
}