/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.util;

import br.com.webpublico.util.AtributoMetadata;
import br.com.webpublico.util.EntidadeMetaData;
import br.com.webpublico.controle.SistemaControlador;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * @author Renato
 */
public class RelatorioDinamico {

    public void geraRelatorio(List lista, EntidadeMetaData metadata) {

        String conteudo = "<?xml version=\"1.0\" encoding=\"iso-8859-1\" ?>"
                + " <!DOCTYPE HTML PUBLIC \"HTML 4.01 Transitional//PT\" \"http://www.w3.org/TR/html4/loose.dtd\">"
                + " <html>"
                + " <head>"
                + " <style type=\"text/css\">@page{size: A4 portrait;}</style>"
                + " <style type=\"text/css\">"
                + ".igualDataTable{\n"
                + "    border-collapse: collapse; /* CSS2 */\n"
                + "    border: 1px solid #aaaaaa; width: 100%;\n"
                + "    ;\n"
                + "}"
                + ".igualDataTable th{\n"
                + "    border: 0px solid #aaaaaa; \n"
                + "    height: 20px;\n"
                + "    background: #ebebeb 50% 50% repeat-x;\n"
                + "}\n"
                + "\n"
                + ".igualDataTable td{\n"
                + "    padding: 2px;\n"
                + "    border: 0px; \n"
                + "    height: 20px;\n"
                + "\n"
                + "}\n"
                + "\n"
                + ".igualDataTable thead td{\n"
                + "    border: 1px solid #aaaaaa; \n"
                + "    background: #6E95A6 repeat-x scroll 50% 50%; \n"
                + "    border: 0px; \n"
                + "    text-align: center; \n"
                + "    height: 20px;\n"
                + "}"
                + "body{"
                + "font-size: 8pt;"
                + "}"
                + ""
                + "</style>"
                + " <title>"
                + " < META HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; charset=iso-8859-1\">"
                + " </title>"
                + " </head>"
                + " <body>"
                + "" + adicionaCabecalho()
                + "<br/><br/><center><h1>Relatório de " + metadata.getNomeEntidade() + "</h1></center>"
                + "<hr/>"
                + "<br/><br/>"
                + "<table border=\"1\" style=\"width:100%; text-align:center\" class=\"igualDataTable\">"
                + "<tr>";
        for (AtributoMetadata atributo : metadata.getAtributosTabelaveis()) {
            conteudo += "<th> " + atributo.getEtiqueta() + " </th>";
        }
        for (Object objeto : lista) {
            conteudo += "<tr style=\"background: " + (lista.indexOf(objeto) % 2 == 0 ? "#d7e3e9" : "whitesmoke") + "\"> ";
            for (AtributoMetadata atributo : metadata.getAtributosTabelaveis()) {
                conteudo += "<td> " + atributo.getString(objeto) + " </td>";
            }
            conteudo += "</tr> ";
        }

        conteudo += "</tr>"
                + "</table>"
                + "<br/><br/>"
                + "<hr/>"
                + "<table border=\"0\" style=\"width:100%;\">"
                + "<tr>"
                + "<td style=\"text-align:left\">Usúario: " + getUsuarioLogado() + "</td>"
                + "<td style=\"text-align:right\">Data: " + Util.dateHourToString(new Date()) + "</td>"
                + "</tr>"
                + "</table>"
                + "<hr/>"
                + "<br/><br/>"
                + " </body>"
                + " </html>";
        try {
            Util.geraPDF("Relatorio de " + metadata.getNomeEntidade(), conteudo, FacesContext.getCurrentInstance());
        } catch (Exception e) {
        }
    }

    public static String geraUrlImagemDir() {
        FacesContext fc = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
        final StringBuffer url = request.getRequestURL();
        String test = url.toString();
        String[] quebrado = test.split("/");
        StringBuilder b = new StringBuilder();
        b.append(quebrado[0]);
        b.append("/").append(quebrado[1]);
        b.append("/").append(quebrado[2]);
        b.append("/").append(quebrado[3]).append("/");
        return b.toString();
    }

    private static String adicionaCabecalho() {
        String caminhoDaImagem = geraUrlImagemDir() + "img/Brasao_de_Rio_Branco.gif";

        String conteudo =
                "<table width=\"100%\">"
                        + " <tbody>"
                        + "  <tr>"
                        + "   <td style=\"text-align: left;\" align=\"right\">"
                        + "    <img src=\"" + caminhoDaImagem + "\" alt=\"PREFEITURA DO MUNICÍPIO DE RIO BRANCO\"></img>"
                        + "   </td>"
                        + "   <td><h1>PREFEITURA DO MUNICÍPIO DE RIO BRANCO"
                        + "        <br>SECRETARIA MUNICIPAL DE FINANÇAS"
                        + "        </h1>"
                        + "   </td>"
                        + "  </tr>"
                        + " </tbody>"
                        + " </table>";
        return conteudo;
    }

    public String getUsuarioLogado() {
        SistemaControlador controladorPeloNome = (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
        try {
            return controladorPeloNome.getUsuarioCorrente().getPessoaFisica().getNome();
        } catch (Exception e) {
            return controladorPeloNome.getUsuarioCorrente().getLogin();
        }
    }
}
