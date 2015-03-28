/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stegcrypt;
import java.io.*;
import java.awt.image.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import javax.imageio.*;
import javax.crypto.*;
import javax.crypto.spec.DESKeySpec;
import javax.swing.JOptionPane;


/**
 *
 * @author Nikhil
 */
public class StegCrypt 
{
  
    private String password;
    private final static int INT_LENGTH = 4;
    private final static int DATA_SIZE = 8;
    
    public void setpassword(String x)
    {
         
        if ((x == null) || (x.length()<8) )    
            password = "password";     //defaultpassword
        else if (x.length() > 8)
             password = x.substring(0,8);
             
    }
    public boolean hide(String textfnm, String imfnm)
    {
        byte[] msgbytes = readmsgbytes(textfnm);
        if(msgbytes == null)
        {
            return false;
        }
        
        String password = getpassword();
        byte[] passbytes = password.getBytes();
        
        byte[] encryptedmsgbytes = encryptmsgbytes(msgbytes,password);
        if (encryptedmsgbytes == null)
            return false;
        byte[] stego = buildstego(passbytes,encryptedmsgbytes);
        
        BufferedImage im = loadimage(imfnm);
        if (im == null)
            return false;
        byte imbytes[] = accessbytes(im);
        if (!dohide(imbytes,stego))
            return false;
        String fnm = getfilename(imfnm);
        return writeimagetofile(fnm+"msg.png", im);
    }
    
    private static byte[] readmsgbytes(String fnm)
    {
        String inputtext = readtextfile(fnm);
        if((inputtext == null) || (inputtext.length() == 0))
            return null;
        return inputtext.getBytes();
    }
    
    private static String readtextfile(String fnm)
    {
        BufferedReader br = null;
        StringBuffer sb = new StringBuffer();
        
        try
        {
            br = new BufferedReader(new FileReader(new File(fnm)));
            String text = null;
            while ((text = br.readLine()) != null)
                sb.append(text+ "\n");
        }
        catch (Exception e)
        {
            System.out.println("Could not read : " + fnm);
        }
        finally
        {
            try
            {
                if (br != null)
                    br.close();
            }
            catch(IOException e)
            {
                System.out.println("Cannot close " + fnm);
                return null;
            }    
        }
        System.out.println("Reading text file" + fnm);
        return sb.toString();
    }
    
    private String getpassword()
    {
            return password;
        
    }
    
    private static byte[] encryptmsgbytes(byte[] msgbytes, String password)
    {
        byte[] encryptedtext = null;
        
        try
        {
            byte key[] = password.getBytes();
            DESKeySpec deskeyspec = new DESKeySpec(key);//InvalidKeyException
            SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("DES"); //NoSuchAlgorithmException
            SecretKey secretkey = keyfactory.generateSecret(deskeyspec); //InvalidKeySpec Exception
        
            Cipher des = Cipher.getInstance("DES/ECB/PKCS5Padding"); //NoSuchAlgorithmException
            des.init(Cipher.ENCRYPT_MODE, secretkey); //InvalidKeyException
            encryptedtext = des.doFinal(msgbytes); //IllegalBlockSizeException
            
        }
        catch (InvalidKeyException e)
        {
            System.err.println("Error in encryptmsgbytes() : Invalid key : " + e.getMessage());
        }
        catch (BadPaddingException e)
        {
            System.err.println("Error in encryptmsgbytes() : Bad Padding : " + e.getMessage());
        }
        catch (IllegalBlockSizeException e)
        {
            System.err.println("Error in encryptmsgbytes() : Illegal block size : " + e.getMessage());
            
        }
        catch (InvalidKeySpecException e)
        {
            System.err.println("Error in encryptmsgbytes() : Invalid key Spec : " + e.getMessage());
        }
        catch (NoSuchAlgorithmException e)
        {
            System.err.println("Error in encryptmsgbytes() : No such algorithm : " + e.getMessage());
        }
        
        catch (NoSuchPaddingException e)
        {
            System.err.println("Error in encryptmsgbytes() : No such padding : " + e.getMessage());
        }
        
        
    return encryptedtext;
    }
    
    private static byte[] buildstego(byte[] passbytes, byte[] encryptedmsgbytes)
    {
        byte[] len = inttobytes(encryptedmsgbytes.length);
        int totallen = passbytes.length + len.length + encryptedmsgbytes.length;
        byte[] stego = new byte [totallen];
        
        int destpos=0;
        System.arraycopy(passbytes,0,stego,destpos,passbytes.length);
        destpos+=passbytes.length;
        
        System.arraycopy(len,0,stego,destpos,len.length );
        destpos+=len.length;
        
        System.arraycopy(encryptedmsgbytes,0,stego,destpos,encryptedmsgbytes.length);
    
        return stego;
    }
    
    
    private static byte[] inttobytes(int i)
    {
        byte[] intbytes = new byte[INT_LENGTH];    //an integer in java has size 4 bytes!!!
        intbytes[0] = (byte) ((i >>> 24) & 0xFF);
        intbytes[1] = (byte) ((i >>> 16) & 0xFF);
        intbytes[2] = (byte) ((i >>> 8) & 0xFF);
        intbytes[3] = (byte) (i & 0xFF);
        return intbytes;
        
    }
    
    private static BufferedImage loadimage(String imfnm)
    {
        BufferedImage im = null;
        try
        {
            im = ImageIO.read(new File(imfnm));
            System.out.println("reading image " + imfnm);
        }
        catch (IOException e)
        {
            System.out.println("could not load image..." + e.getMessage());
        }
        return im;
        
    }
    
    private static byte[] accessbytes(BufferedImage image)
    {
        WritableRaster raster = image.getRaster();
        DataBufferByte buffer = (DataBufferByte) raster.getDataBuffer();
        return buffer.getData();
    }
    
    private static boolean dohide(byte[] imbytes, byte[] stego)
    {
        int imlen = imbytes.length;
        int totallen = stego.length;
        if (totallen*DATA_SIZE > imlen)
        {
            System.out.println("image not big enough");
            return false;
        
        }
        process(imbytes,stego,0);
        return true;
    }
    
    private static void process(byte[] imbytes, byte[] stego, int offset)
    {
        for (int i = 0; i < stego.length; i++)
        {
            int byteval = stego[i];
            for (int j=7; j>=0; j--)
            {
                int bitval = (byteval >>> j) & 1 ;
                imbytes[offset] = (byte)((imbytes[offset] & 0xFE) | bitval);
                offset++;
            }
        }
    }
    
    private static String getfilename(String fnm)
    {
        int extposn = fnm.lastIndexOf('.');
        if (extposn == -1)
        {
            System.out.println("no extension found for " + fnm);
            return fnm;
        }
        return fnm.substring(0,extposn);
    }
    
    private static boolean writeimagetofile(String outfnm, BufferedImage im)
    {
        if (!canoverwrite(outfnm))
            return false;
        try
        {
            ImageIO.write(im, "png", new File(outfnm));
            System.out.println("Image written to PNG file : " + outfnm);
            return true;
        }
        catch (IOException e)
        {
            System.out.println("Could not write image to : " + outfnm);
            return false;
        }
    }
    
    private static boolean canoverwrite(String fnm)
    {
        File f = new File(fnm);
        int n;
        if (!f.exists())
            return true;
        else
        {   
            n = JOptionPane.showConfirmDialog(null,"File "+fnm+" already exits.Do you want to overwrite?","File exists!", JOptionPane.YES_NO_OPTION);
            if (n == JOptionPane.YES_OPTION)
            {
                return true;
            }
            else
                return false;

        }
    }
    
    
    
    public boolean reveal(String imfnm)
    {
       BufferedImage im = loadimage(imfnm);
       if (im == null)
       {
           return false;
       }
       byte[] imbytes = accessbytes(im);
       int imlen = imbytes.length;
       String msg = extractmsg(imbytes,0);
       if (msg != null)
       {
           String fnm = getfilename(imfnm);
           return writestringtofile(fnm+".txt",msg);
       }
       else
       {
           System.out.println("No message found!");
           return false;
       }
    }
    
    
    private static String extractmsg(byte[] imbytes, int offset)
    {
        String pwd = extractpassword(imbytes,offset);
        if (pwd == null)
            return null;
        offset += 8*DATA_SIZE;   // password has 8 characters
        int msglen = getmsglength(imbytes, offset);
        if (msglen == -1)
                return null;
        offset += INT_LENGTH*DATA_SIZE;  
        return getmessage(imbytes, msglen,pwd,offset);
    }
    
    private static String extractpassword(byte[] imbytes, int offset)
    {
        byte[] passbytes = extracthiddenbytes(imbytes,DATA_SIZE,offset);
        if (passbytes == null)
            return null;
        String password = new String(passbytes);
        System.out.println("password is : "+ password);
        return password;
    }  
    
    private static int getmsglength(byte[] imbytes, int offset)
    {
        byte[] lenbytes = extracthiddenbytes(imbytes,INT_LENGTH,offset);
        if (lenbytes == null)
            return -1;
        int msglen = ((lenbytes[0] & 0xff) << 24) | 
                 ((lenbytes[1] & 0xff) << 16) | 
                 ((lenbytes[2] & 0xff) << 8) | 
                  (lenbytes[3] & 0xff);
        System.out.println("Message length: " + msglen);
        if ((msglen <= 0 ) || (msglen > imbytes.length))
            return -1;
        else
            return msglen;
    }
    
    
    private static String getmessage(byte[] imbytes, int msglen, String password,int offset)
    {
        byte[] enmsgbytes = extracthiddenbytes(imbytes, msglen, offset);
        if (enmsgbytes == null)
            return null;
        
        byte[] msgbytes = null;
        byte key[] = password.getBytes();
        try
        {
            DESKeySpec deskeyspec = new DESKeySpec(key);
            SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("DES");
            SecretKey secretkey = keyfactory.generateSecret(deskeyspec);
            
            Cipher des = Cipher.getInstance("DES/ECB/PKCS5Padding");
            des.init(Cipher.DECRYPT_MODE, secretkey);
            
            msgbytes = des.doFinal(enmsgbytes);
        }
         catch (InvalidKeyException e)
        {
            System.err.println("Error in getmessage() : Invalid key : " + e.getMessage());
        }
        catch (BadPaddingException e)
        {
            System.err.println("Error in getmessage() : Bad Padding : " + e.getMessage());
        }
        catch (IllegalBlockSizeException e)
        {
            System.err.println("Error in getmessage() : Illegal block size : " + e.getMessage());
            
        }
        catch (InvalidKeySpecException e)
        {
            System.err.println("Error in getmessage() : Invalid key Spec : " + e.getMessage());
        }
        catch (NoSuchAlgorithmException e)
        {
            System.err.println("Error in getmessage() : No such algorithm : " + e.getMessage());
        }
        
        catch (NoSuchPaddingException e)
        {
            System.err.println("Error in getmessage() : No such padding : " + e.getMessage());
        }
        
       String msg = new String(msgbytes);
       System.out.println("Decrypted message : " + msg);
       
       return msg;
    }
    
    private static byte[] extracthiddenbytes(byte[] imbytes, int size, int offset)
    {
        int finalposn = offset + (size*DATA_SIZE);
        if (finalposn > imbytes.length)
            return null;
        byte[] hiddenbytes = new byte[size];
        for (int j = 0; j<size; j++)
        {
            for (int i=0; i<8; i++)
            {
                hiddenbytes[j] = (byte) ((hiddenbytes[j] << 1) | (imbytes[offset] & 1));
                offset++;
            }
        }
        
        return hiddenbytes;
    }
    
    
    private static boolean writestringtofile(String outfnm, String msgstr)
    {
        if (!canoverwrite(outfnm))
            return false;
        try
        {
            FileWriter out = new FileWriter(new File(outfnm));
            out.write(msgstr);
            out.close();
            System.out.println("Message written to " + outfnm);
            return true;
        }
        catch(IOException e)
        {
            System.out.println("Could not write message to " +outfnm);
            return false;
        }
    }
}
