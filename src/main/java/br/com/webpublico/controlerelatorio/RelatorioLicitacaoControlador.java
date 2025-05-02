package br.com.webpublico.controlerelatorio;

import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Limpavel;
import net.sf.jasperreports.engine.JRException;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: JoãoPaulo
 * Date: 24/02/14
 * Time: 16:15
 * To change this template use File | Settings | File Templates.
 */
@ViewScoped
@ManagedBean
public class RelatorioLicitacaoControlador extends AbstractReport implements Serializable {

    @EJB
    private SistemaFacade sistemaFacade;

    public RelatorioLicitacaoControlador() {
    }

    public void gerarRelatorio(Integer numero) throws JRException, IOException{
        String arquivoJasper = "Relatorio_Licitacao.jasper";
        HashMap parameters = new HashMap();
        String sql = montarSql(numero);
        parameters.put("MUNICIPIO", "PREFEITURA MUNICIPAL DE RIO BRANCO ");
        parameters.put("UNIDADELOGADO", recuperaUnidadeLogado());
        parameters.put("SQL", sql);
        parameters.put("IMAGEM", getCaminhoImagem());
        gerarRelatorio(arquivoJasper, parameters);
    }

    private String recuperaUnidadeLogado() {
        if (sistemaFacade.getUnidadeOrganizacionalAdministrativaCorrente() != null) {
            return sistemaFacade.getUnidadeOrganizacionalAdministrativaCorrente().getDescricao();
        } else {
            return sistemaFacade.getUnidadeOrganizacionalOrcamentoCorrente().getDescricao();
        }
    }

    private String montarSql(Integer numero) {
        StringBuffer stb = new StringBuffer();

        stb.append(" L.NUMEROLICITACAO =  ").append(numero);

        return stb.toString();
    }
}
