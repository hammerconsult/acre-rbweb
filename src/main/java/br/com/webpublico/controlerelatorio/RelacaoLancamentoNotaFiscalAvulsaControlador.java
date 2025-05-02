package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controle.AbstractRelacaoLancamentoTributarioControlador;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidadesauxiliares.*;
import br.com.webpublico.negocios.AbstractRelacaoLancamentoTributarioFacade;
import br.com.webpublico.negocios.RelacaoLancamentoNotaFiscalFacade;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.Comparator;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 28/04/15
 * Time: 10:29
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaRelacaoLancamentoNotaFiscalAvulsa", pattern = "/tributario/nota-fiscal-avulsa/relacao-lancamento-nota-fiscal-avulsa/", viewId = "/faces/tributario/notafiscalavulsa/relatorios/relacaolancamentonotafiscalavulsa.xhtml")})
public class RelacaoLancamentoNotaFiscalAvulsaControlador extends AbstractRelacaoLancamentoTributarioControlador<VOConsultaNotaFiscal> {

    private static final Logger logger = LoggerFactory.getLogger(RelacaoLancamentoNotaFiscalAvulsaControlador.class);
    private FiltroRelacaoLancamentoNotaFiscal filtroRelacaoLancamentoNotaFiscal;
    @EJB
    private RelacaoLancamentoNotaFiscalFacade relacaoLancamentoNotaFiscalFacade;

    @Override
    protected Logger getLogger() {
        return logger;
    }

    @Override
    public AbstractFiltroRelacaoLancamentoDebito getFiltroLancamento() {
        return filtroRelacaoLancamentoNotaFiscal;
    }

    @Override
    protected AbstractRelacaoLancamentoTributarioFacade getFacade() {
        return relacaoLancamentoNotaFiscalFacade;
    }

    @Override
    protected Comparator getComparator() {
        return new VOConsultaNotaFiscalComparator();
    }

    @Override
    protected String getNomeRelatorio() {
        if (filtroRelacaoLancamentoNotaFiscal.getTipoRelatorio().equals(AbstractFiltroRelacaoLancamentoDebito.TipoRelatorio.RESUMIDO)) {
            return "RelacaoLancamentoNotaFiscalAvulsaResumido";
        }
        return "RelacaoLancamentoNotaFiscalAvulsaDetalhado";
    }

    @Override
    protected RelatorioRelacaoLancamentoTributario.RelacaoLancamentoTributario getRelacaoLancamentoTributario() {
        return RelatorioRelacaoLancamentoTributario.RelacaoLancamentoTributario.NOTA_FISCAL_AVULA;
    }

    @Override
    protected void instanciarFiltroLancamento() {
        filtroRelacaoLancamentoNotaFiscal = new FiltroRelacaoLancamentoNotaFiscal();
        Exercicio exercicioVigente = relacaoLancamentoNotaFiscalFacade.getExercicioFacade().getExercicioCorrente();
        filtroRelacaoLancamentoNotaFiscal.setExercicioDividaInicial(exercicioVigente.getAno());
        filtroRelacaoLancamentoNotaFiscal.setExercicioDividaFinal(exercicioVigente.getAno());
    }

    @URLAction(mappingId = "novaRelacaoLancamentoNotaFiscalAvulsa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
    }


}
