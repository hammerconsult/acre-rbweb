/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.OperacaoRelatorio;
import br.com.webpublico.negocios.RelatorioConciliacaoBancariaFacade;
import br.com.webpublico.relatoriofacade.AbstractRelatorioConciliacaoBancariaFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.StringUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

/**
 * @author juggernaut
 */
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-conciliacao-bancaria", pattern = "/relatorio/conciliacao-bancaria", viewId = "/faces/financeiro/relatorio/relatorioconciliacaobancaria.xhtml")
})
@ManagedBean
public class RelatorioConcialicaoBancariaControlador extends AbstractRelatorioConciliacaoBancariaControlador implements Serializable {

    @EJB
    private RelatorioConciliacaoBancariaFacade facade;

    @URLAction(mappingId = "relatorio-conciliacao-bancaria", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        limparCamposGeral();
        limparCamposRelatorio();
    }

    @Override
    public String getNomeRelatorio() {
        return "CBC1-" + toStringConciliacaoBancaria() + "-" + DataUtil.getAno(dataReferencia) + "-" + StringUtil.preencheString(String.valueOf(DataUtil.getMes(dataReferencia)), 2, '0') + "-" + StringUtil.preencheString(String.valueOf(DataUtil.getDia(dataReferencia)), 2, '0');
    }
    @Override
    protected void montarFiltroSql(List<ParametrosRelatorios> parametros) {
        super.montarFiltroSql(parametros);
        if (contaFinanceira != null && this.contaFinanceira.getId() != null) {
            parametros.add(new ParametrosRelatorios(" SUB.ID ", ":idSubConta", null, OperacaoRelatorio.IGUAL, contaFinanceira.getId(), null, 1, false));
        }
    }

    @Override
    public AbstractRelatorioConciliacaoBancariaFacade getRelatorioFacade() {
        return facade;
    }

    @Override
    public TipoRelatorio getTipoRelatorio() {
        return TipoRelatorio.CONCILIACAO_BANCARIA;
    }
}
