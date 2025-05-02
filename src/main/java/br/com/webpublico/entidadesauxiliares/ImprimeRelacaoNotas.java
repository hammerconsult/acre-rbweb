/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.util.FacesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

/**
 * @author MGA
 */
public class ImprimeRelacaoNotas extends AbstractReport {

    private static final Logger logger = LoggerFactory.getLogger(ImprimeRelacaoNotas.class);

    public String condicao;
    public String filtros;
    public String usuario;

    public void imprime() {
        geraNoDialog = true;
        String caminhoBrasao = getCaminhoImagem();
        String arquivoJasper = "RelacaonotaFiscalAcaoFiscal.jasper";
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("WHERE", condicao);
        parameters.put("BRASAO", caminhoBrasao);
        parameters.put("FILTROS", filtros);
        parameters.put("MODULO", "Tributário");
        parameters.put("SECRETARIA", "Secretaria de Finanças");
        parameters.put("NOMERELATORIO", "Demonstrativo de Lançamento de Notas Fiscais");
        parameters.put("USUARIO", usuario);
        try {
            gerarRelatorio(arquivoJasper, parameters);
        } catch (Exception ex) {
            logger.error("Erro: ", ex);
            FacesUtil.addError("Atenção!", "O Relatório não pode ser gerado por um problema interno do servidor");
        }
    }
}
