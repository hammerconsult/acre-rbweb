package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.OperacaoRelatorio;
import br.com.webpublico.relatoriofacade.AbstractRelatorioConciliacaoBancariaFacade;
import br.com.webpublico.relatoriofacade.RelatorioConciliacaoBancariaPorIdentificadorFacade;
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
 * Created by mateus on 04/08/17.
 */
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-conciliacao-bancaria-por-identificador", pattern = "/relatorio/conciliacao-bancaria-por-identificador", viewId = "/faces/financeiro/relatorio/relatorioconciliacaobancariaporidentificador.xhtml")
})
@ManagedBean
public class RelatorioConciliacaoBancariaPorIdentificadorControlador extends AbstractRelatorioConciliacaoBancariaControlador implements Serializable {

    @EJB
    private RelatorioConciliacaoBancariaPorIdentificadorFacade facade;
    @URLAction(mappingId = "relatorio-conciliacao-bancaria-por-identificador", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        limparCamposGeral();
        limparCamposRelatorio();
    }

    @Override
    protected void parametrosParaConsulta(List<ParametrosRelatorios> parametros) {
        parametros.add(new ParametrosRelatorios(null, ":DATAREFERENCIA", null, OperacaoRelatorio.IGUAL, getDataReferenciaFormatada(), null, 0, false));
        parametros.add(new ParametrosRelatorios(null, ":EXERCICIO_ID", null, null, getExercicioDaDataReferencia().getId(), null, 5, false));
        parametros.add(new ParametrosRelatorios(null, ":CBE_ID", null, OperacaoRelatorio.IGUAL, contaBancariaEntidade.getId(), null, 0, false));
    }

    @Override
    public AbstractRelatorioConciliacaoBancariaFacade getRelatorioFacade() {
        return facade;
    }

    @Override
    public TipoRelatorio getTipoRelatorio() {
        return TipoRelatorio.CONCILIACAO_BANCARIA_POR_IDENTIFICADOR;
    }

    @Override
    public String getNomeRelatorio() {
        return "CBC2-" + toStringConciliacaoBancaria() + "-" + DataUtil.getAno(dataReferencia) + "-" + StringUtil.preencheString(String.valueOf(DataUtil.getMes(dataReferencia)), 2, '0') + "-" + StringUtil.preencheString(String.valueOf(DataUtil.getDia(dataReferencia)), 2, '0');
    }
}
