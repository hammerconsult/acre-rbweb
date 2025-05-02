package br.com.webpublico.entidadesauxiliares;

/**
 * Created with IntelliJ IDEA.
 * User: André Gustavo
 * Date: 20/03/14
 * Time: 17:56
 */

// Decompiled by DJ v3.12.12.96 Copyright 2011 Atanas Neshkov  Date: 20/03/2014 17:35:03
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3)
// Source File Name:   Criptografia.java

import java.util.GregorianCalendar;

import java.util.Date;
import java.util.GregorianCalendar;

public class Criptografia {

    public Criptografia(String usuario, String chave) {
        sbox = new int[256];
        Key = new int[256];
        pUsuario = "";
        pSenha = "";
        rChavePublica = "";
        rSenhaCripto = "";
        setPUsuario(usuario);
        setRChavePublica(chave);
    }

    public Criptografia(String usuario) {
        sbox = new int[256];
        Key = new int[256];
        pUsuario = "";
        pSenha = "";
        rChavePublica = "";
        rSenhaCripto = "";
        setPUsuario(usuario);
    }

    private void Criptografa() {
        String sEncrypted = "";
        String chave = "";
        String Usuario = "";
        String Senha = "";
        String SenhaCripto = "";
        String ChavePublica = "";
        long Auxiliar = 0L;
        if (rChavePublica == null || rChavePublica.equals("")) {
            Auxiliar = ((new GregorianCalendar()).getTime().getDay() + 1) * ((new GregorianCalendar()).getTime().getHours() + 1) * ((new GregorianCalendar()).getTime().getMinutes() + 1) * ((new GregorianCalendar()).getTime().getSeconds() + 1);
            ChavePublica = String.valueOf(Auxiliar).trim();
        } else {
            ChavePublica = rChavePublica;
        }
        rSenhaCripto = "";
        Usuario = pUsuario.concat(completaEspacos("", 10)).substring(0, 10);
        Senha = pSenha.concat(completaEspacos("", 10)).substring(0, 10);
        for (int contador = Usuario.length() - 1; contador >= 0; contador--) {
            chave = chave.concat(Integer.toString(Usuario.charAt(contador) + Usuario.charAt(contador)));
            if (contador < ChavePublica.length())
                chave = chave.concat(String.valueOf(ChavePublica.charAt(contador)));
        }

        sEncrypted = EnDeCrypt(Senha, chave);
        SenhaCripto = ToHexa(sEncrypted);
        rSenhaCripto = SenhaCripto;
        rChavePublica = ChavePublica;
    }

    private void Decriptografa() {
        String chave = "";
        String Usuario = completaEspacos("", 10);
        String Senha = completaEspacos("", 10);
        String SenhaCripto = completaEspacos("", 20);
        String ChavePublica = completaEspacos("", 8);
        Usuario = pUsuario.concat(completaEspacos("", 10)).substring(0, 10);
        SenhaCripto = getRSenhaCripto();
        ChavePublica = completaEspacos(getRChavePublica(), 8);
        for (int contador = Usuario.length() - 1; contador >= 0; contador--) {
            chave = chave.concat(String.valueOf(Usuario.charAt(contador))).concat(String.valueOf(Usuario.charAt(contador)).trim());
            if (contador < ChavePublica.length())
                chave = chave.concat(String.valueOf(ChavePublica.charAt(contador)));
        }

        String sEncrypted = EnDeCrypt(ToDEC(SenhaCripto), chave);
        pSenha = sEncrypted;
    }

    private String completaEspacos(String texto, int tamanho) {
        for (; texto.length() < tamanho; texto = texto.concat(" ")) ;
        return texto;
    }

    private String EnDeCrypt(String plaintxt, String psw) {
        int temp = 0;
        int i = 0;
        int j = 0;
        int k = 0;
        int cipherby = 0;
        String cipher = "";
        RC4Initialize(psw);
        for (int a = 0; a < plaintxt.length(); a++) {
            i = (i + 1) % 256;
            j = (j + sbox[i]) % 256;
            temp = sbox[i];
            sbox[i] = sbox[j];
            sbox[j] = temp;
            k = sbox[(sbox[i] + sbox[j]) % 256];
            cipherby = plaintxt.charAt(a) ^ k;
            cipher = cipher.concat(String.valueOf((char) cipherby));
        }

        return cipher;
    }

    private void RC4Initialize(String strPwd) {
        int tempSwap = 0;
        int B = 0;
        int intLength = strPwd.length();
        for (int a = 0; a <= 255; a++) {
            Key[a] = strPwd.charAt(a % intLength);
            sbox[a] = a;
        }

        for (int a = 0; a <= 255; a++) {
            B = (B + sbox[a] + Key[a]) % 256;
            tempSwap = sbox[a];
            sbox[a] = sbox[B];
            sbox[B] = tempSwap;
        }

    }

    private String ToHexa(String Valor) {
        String HValor = "";
        int letra = 0;
        int primeiro = 0;
        int segundo = 0;
        String hprimeiro = "";
        String hsegundo = "";
        for (int contador = 0; contador < Valor.length(); contador++) {
            letra = Valor.charAt(contador);
            primeiro = letra / 16;
            segundo = letra % 16;
            switch (primeiro) {
                case 10: // '\n'
                    hprimeiro = "A";
                    break;

                case 11: // '\013'
                    hprimeiro = "B";
                    break;

                case 12: // '\f'
                    hprimeiro = "C";
                    break;

                case 13: // '\r'
                    hprimeiro = "D";
                    break;

                case 14: // '\016'
                    hprimeiro = "E";
                    break;

                case 15: // '\017'
                    hprimeiro = "F";
                    break;

                default:
                    hprimeiro = String.valueOf(primeiro).trim();
                    break;
            }
            switch (segundo) {
                case 10: // '\n'
                    hsegundo = "A";
                    break;

                case 11: // '\013'
                    hsegundo = "B";
                    break;

                case 12: // '\f'
                    hsegundo = "C";
                    break;

                case 13: // '\r'
                    hsegundo = "D";
                    break;

                case 14: // '\016'
                    hsegundo = "E";
                    break;

                case 15: // '\017'
                    hsegundo = "F";
                    break;

                default:
                    hsegundo = String.valueOf(segundo).trim();
                    break;
            }
            HValor = HValor.concat(hprimeiro).concat(hsegundo);
        }

        return HValor;
    }

    private String ToDEC(String Valor) {
        String HValor = "";
        String letra = " ";
        String letra2 = " ";
        int primeiro = 0;
        int segundo = 0;
        int hprimeiro = 0;
        int hsegundo = 0;
        for (int contador = 0; contador < Valor.length(); contador += 2) {
            letra = String.valueOf(Valor.charAt(contador));
            letra2 = String.valueOf(Valor.charAt(contador + 1));
            switch (letra.charAt(0)) {
                case 65: // 'A'
                    hprimeiro = 10;
                    break;

                case 66: // 'B'
                    hprimeiro = 11;
                    break;

                case 67: // 'C'
                    hprimeiro = 12;
                    break;

                case 68: // 'D'
                    hprimeiro = 13;
                    break;

                case 69: // 'E'
                    hprimeiro = 14;
                    break;

                case 70: // 'F'
                    hprimeiro = 15;
                    break;

                default:
                    hprimeiro = Integer.parseInt(letra);
                    break;
            }
            switch (letra2.charAt(0)) {
                case 65: // 'A'
                    hsegundo = 10;
                    break;

                case 66: // 'B'
                    hsegundo = 11;
                    break;

                case 67: // 'C'
                    hsegundo = 12;
                    break;

                case 68: // 'D'
                    hsegundo = 13;
                    break;

                case 69: // 'E'
                    hsegundo = 14;
                    break;

                case 70: // 'F'
                    hsegundo = 15;
                    break;

                default:
                    hsegundo = Integer.parseInt(letra2);
                    break;
            }
            HValor = HValor.concat(String.valueOf((char) (hprimeiro * 16 + hsegundo)));
        }

        return HValor;
    }

    public void setRChavePublica(String chavePublica) {
        rChavePublica = chavePublica;
    }

    public void setRSenhaCripto(String senhaCripto) {
        rSenhaCripto = senhaCripto;
        Decriptografa();
    }

    public void setPSenha(String senha) {
        pSenha = senha;
        Criptografa();
    }

    private void setPUsuario(String usuario) {
        pUsuario = usuario;
    }

    public String getRChavePublica() {
        return rChavePublica;
    }

    public String getRSenhaCripto() {
        return rSenhaCripto;
    }

    public String getPSenha() {
        return pSenha;
    }

    public String getPUsuario() {
        return pUsuario;
    }

    private int sbox[];
    private int Key[];
    private String pUsuario;
    private String pSenha;
    private String rChavePublica;
    private String rSenhaCripto;
}

