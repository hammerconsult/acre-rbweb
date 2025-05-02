/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.util;

/**
 *
 * @author fabio
 */

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;

public class Endecrypt {

    private byte[] md5(String strSrc) {
        byte[] returnByte = null;
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            returnByte = md5.digest(strSrc.getBytes("GBK"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnByte;
    }

    private byte[] getEnKey(String spKey) {
        byte[] desKey = null;
        try {
            byte[] desKey1 = md5(spKey);
            desKey = new byte[24];
            int i = 0;
            while (i < desKey1.length && i < 24) {
                desKey[i] = desKey1[i];
                i++;
            }
            if (i < 24) {
                desKey[i] = 0;
                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return desKey;
    }

    public byte[] Encrypt(byte[] src, byte[] enKey) {
        byte[] encryptedData = null;
        try {
            DESedeKeySpec dks = new DESedeKeySpec(enKey);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
            SecretKey key = keyFactory.generateSecret(dks);
            Cipher cipher = Cipher.getInstance("DESede");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            encryptedData = cipher.doFinal(src);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encryptedData;
    }

    public String getBase64Encode(byte[] src) {
        String requestValue = "";
        try {
            Base64 base64 = new Base64();
            requestValue = base64.encodeAsString(src);
            //BASE64Encoder base64en = new BASE64Encoder();
            //requestValue = base64en.encode(src);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return requestValue;
    }

    private String filter(String str) {
        String output = null;
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < str.length(); i++) {
            int asc = str.charAt(i);
            if (asc != 10 && asc != 13) {
                sb.append(str.subSequence(i, i + 1));
            }
        }
        output = new String(sb);
        return output;
    }

    public String getURLEncode(String src) {
        String requestValue = "";
        try {

            requestValue = URLEncoder.encode(src);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return requestValue;
    }

    public String get3DESEncrypt(String src, String spkey) {
        String requestValue = "";
        try {

            // µÃµ½3-DESµÄÃÜÔ¿³×
            byte[] enKey = getEnKey(spkey);
            // Òª½øÐÐ3-DES¼ÓÃÜµÄÄÚÈÝÔÚ½øÐÐ\"UTF-16LE\"È¡×Ö½Ú
            byte[] src2 = src.getBytes("UTF-16LE");
            // ½øÐÐ3-DES¼ÓÃÜºóµÄÄÚÈÝµÄ×Ö½Ú
            byte[] encryptedData = Encrypt(src2, enKey);

            // ½øÐÐ3-DES¼ÓÃÜºóµÄÄÚÈÝ½øÐÐBASE64±àÂë
            String base64String = getBase64Encode(encryptedData);
            // BASE64±àÂëÈ¥³ý»»ÐÐ·ûºó
            String base64Encrypt = filter(base64String);

            // ¶ÔBASE64±àÂëÖÐµÄHTML¿ØÖÆÂë½øÐÐ×ªÒåµÄ¹ý³Ì
            requestValue = getURLEncode(base64Encrypt);
            // //System.out.println(requestValue);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return requestValue;
    }

    public String getURLDecoderdecode(String src) {
        String requestValue = "";
        try {

            requestValue = URLDecoder.decode(src);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return requestValue;
    }

    public String deCrypt(byte[] debase64, String spKey) {
        String strDe = null;
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("DESede");
            byte[] key = getEnKey(spKey);
            DESedeKeySpec dks = new DESedeKeySpec(key);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
            SecretKey sKey = keyFactory.generateSecret(dks);
            cipher.init(Cipher.DECRYPT_MODE, sKey);
            byte ciphertext[] = cipher.doFinal(debase64);
            strDe = new String(ciphertext, "UTF-16LE");
        } catch (Exception ex) {
            strDe = "";
            ex.printStackTrace();
        }
        return strDe;
    }

    public String get3DESDecrypt(String src, String spkey) {
        String requestValue = "";
        try {

            // µÃµ½3-DESµÄÃÜÔ¿³×

            // URLDecoder.decodeTML¿ØÖÆÂë½øÐÐ×ªÒåµÄ¹ý³Ì
            String URLValue = getURLDecoderdecode(src);

            // ½øÐÐ3-DES¼ÓÃÜºóµÄÄÚÈÝ½øÐÐBASE64±àÂë

            //BASE64Decoder base64Decode = new BASE64Decoder();
            //byte[] base64DValue = base64Decode.decodeBuffer(URLValue);
            Base64 base64 = new Base64();
            byte[] base64DValue = base64.decode(URLValue);

            // Òª½øÐÐ3-DES¼ÓÃÜµÄÄÚÈÝÔÚ½øÐÐ\"UTF-16LE\"È¡×Ö½Ú

            requestValue = deCrypt(base64DValue, spkey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return requestValue;
    }

    public static void main(String[] args) {
        Endecrypt test = new Endecrypt();
        String oldString = "!@#! ÄãºÃariz  ona";

        String SPKEY = "1234";
        //System.out.println("1¡£·ÖÅäµÄSPKEYÎª:  " + SPKEY);
        //System.out.println("2¡£µÄÄÚÈÝÎª:  " + oldString);
        String reValue = test.get3DESEncrypt(oldString, SPKEY);
        // reValue = reValue.trim().intern();
        //System.out.println("½øÐÐ3-DES¼ÓÃÜºóµÄÄÚÈÝ: " + reValue);
        String reValue2 = test.get3DESDecrypt(reValue, SPKEY);
        //System.out.println("½øÐÐ3-DES½âÃÜºóµÄÄÚÈÝ: " + reValue2);

    }
}
