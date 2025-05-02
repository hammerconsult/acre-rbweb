/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import net.sf.jasperreports.engine.JRException;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.IOException;
import java.util.HashMap;

/**
 * @author Heinz
 */
@ManagedBean
@SessionScoped
public class CertidaoBaixaDeAtividade extends AbstractReport {

    public void montaCertidao(String contribuinte, String atividade, String agente) throws IOException, JRException {
        String arquivoJasper = "CertificadoBaixaDeAtividade.jasper";

        HashMap parametros = new HashMap();
        parametros.put("CONTRIBUINTE", contribuinte);
        parametros.put("ATIVIDADE", atividade);
        parametros.put("AGENTE", agente);
        gerarRelatorio(arquivoJasper, parametros);
    }
}
