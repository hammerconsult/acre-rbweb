package br.com.webpublico.util;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author Munif
 */
public class Seguranca {

    public static BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    public static String md5(String senha) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(senha.getBytes());
            BigInteger hash = new BigInteger(1, md.digest());
            String retornaSenha = hash.toString(16);
            return retornaSenha;
        } catch (NoSuchAlgorithmException ns) {
            ns.printStackTrace();
        }
        return null;
    }
}
