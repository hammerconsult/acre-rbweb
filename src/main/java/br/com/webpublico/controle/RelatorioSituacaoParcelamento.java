/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.negocios.SistemaFacade;
import net.sf.jasperreports.engine.JRException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.IOException;
import java.util.HashMap;

/**
 * @author magraowar
 */
@ManagedBean
@SessionScoped
public class RelatorioSituacaoParcelamento extends AbstractReport {

    private static final Logger logger = LoggerFactory.getLogger(RelatorioSituacaoParcelamento.class);

    private String where = "where 1=1";

    public void imprimeRelatorio() {
        HashMap parametro = new HashMap();
        parametro.put("BRASAO", getCaminhoImagem());
        parametro.put("USUARIO", SistemaFacade.obtemLogin());
        parametro.put("WHERE", where);
        try {
            gerarRelatorio("SituacaodosParcelamentos.jasper", parametro);
        } catch (JRException ex) {
            logger.error("Erro: ", ex);
        } catch (IOException ex) {
            logger.error("Erro: ", ex);
        }

    }
}
