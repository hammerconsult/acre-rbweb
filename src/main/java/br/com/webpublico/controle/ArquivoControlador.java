/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import com.google.common.base.Strings;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

@ManagedBean
@ViewScoped
public class ArquivoControlador implements Serializable {

    public String buscarExtensoesPermitidas(String extensoesPermitidas) {
        if (Strings.isNullOrEmpty(extensoesPermitidas)) return "";
        return "/(\\.|\\/)(" + extensoesPermitidas.replace(".", "").replace(",", "|").replace(" ", "") + ")$/";
    }

    public String buscarMensagemExtensoesPermitidas(String extensoesPermitidas) {
        if (Strings.isNullOrEmpty(extensoesPermitidas)) return "";
        return "Extensão invalida, apenas as extenções (" + extensoesPermitidas + ") são permitidas.";
    }
}
