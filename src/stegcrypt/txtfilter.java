/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stegcrypt;

import java.io.File;
import javax.swing.filechooser.*;

/**
 *
 * @author Nikhil
 */
public class txtfilter extends javax.swing.filechooser.FileFilter
{

    @Override
    public boolean accept(File file) 
    {
       if (file.isDirectory())
       {
           return true;
       }
       String name = file.getName();
       if(name.matches(".*\\.txt"))
       {
           return true;
       }
       else 
           return  false;
    }

    @Override
    public String getDescription() 
    {
        return "Text Files (*.txt)";
    }

    
    
}
