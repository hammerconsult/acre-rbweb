/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.util;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author terminal1
 */
public class TestarString {

    static int profundidadeRelacionamento;
    static HashMap entidadeCampo = new HashMap();
    static HashMap campoTipo = new HashMap();
    static List<String> entidadesTemporaria = new ArrayList<String>();
    static Connection conn;

    public static void main(String args[]) {
        String s = "01.001.001.000";
        String ss[] = s.split("\\.");
        //System.out.println("0"+ss[0]);
        //System.out.println("1"+ss[1].length());
        //System.out.println("1"+ss[1].substring(1, 3));
        //System.out.println("2"+ss[2]);
    }
}
