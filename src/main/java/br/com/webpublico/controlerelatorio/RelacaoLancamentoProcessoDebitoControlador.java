package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controle.AbstractRelacaoLancamentoTributarioControlador;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidadesauxiliares.*;
import br.com.webpublico.enums.TipoProcessoDebito;
import br.com.webpublico.negocios.AbstractRelacaoLancamentoTributarioFacade;
import br.com.webpublico.negocios.RelacaoLancamentoProcessoDebitoFacade;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.util.Comparator;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 11/05/17
 * Time: 15:15
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaRelacaoLancamentoProcessoDebito",
        pattern = "/tributario/processo-debito/relacao-lancamento-processo-debito/",
        viewId = "/faces/tributario/contacorrente/relatorio/relacaolancamentoprocessodebito.xhtml")})
public class RelacaoLancamentoProcessoDebitoControlador extends AbstractRelacaoLancamentoTributarioControlador<VOConsultaProcessoDebito> {

    private static Logger logger = LoggerFactory.getLogger(RelacaoLancamentoProcessoDebitoControlador.class);
    private FiltroRelacaoLancamentoProcessoDebito filtroRelacaoLancamentoProcessoDebito;
    @EJB
    private RelacaoLancamentoProcessoDebitoFacade relacaoLancamentoProcessoDebitoFacade;
    private List<SelectItem> tiposProcessoDebito;

    @Override
    protected Logger getLogger() {
        return logger;
    }

    @Override
    public AbstractFiltroRelacaoLancamentoDebito getFiltroLancamento() {
        return filtroRelacaoLancamentoProcessoDebito;
    }

    @Override
    protected AbstractRelacaoLancamentoTributarioFacade getFacade() {
        return relacaoLancamentoProcessoDebitoFacade;
    }

    @Override
    protected Comparator getComparator() {
        return new VOConsultaProcessoDebitoComparator();
    }

    @Override
    protected String getNomeRelatorio() {
        if (filtroRelacaoLancamentoProcessoDebito.getTipoRelatorio().equals(AbstractFiltroRelacaoLancamentoDebito.TipoRelatorio.RESUMIDO)) {
            return "RelacaoLancamentoProcessoDebitoResumido";
        }
        return "RelacaoLancamentoProcessoDebitoDetalhado";
    }

    @Override
    protected RelatorioRelacaoLancamentoTributario.RelacaoLancamentoTributario getRelacaoLancamentoTributario() {
        return RelatorioRelacaoLancamentoTributario.RelacaoLancamentoTributario.PROCESSO_DEBITO;
    }

    @Override
    protected void instanciarFiltroLancamento() {
        filtroRelacaoLancamentoProcessoDebito = new FiltroRelacaoLancamentoProcessoDebito();
        Exercicio exercicioVigente = relacaoLancamentoProcessoDebitoFacade.getExercicioFacade().getExercicioCorrente();
        filtroRelacaoLancamentoProcessoDebito.setExercicioDividaInicial(exercicioVigente.getAno());
        filtroRelacaoLancamentoProcessoDebito.setExercicioDividaFinal(exercicioVigente.getAno());
    }

    @Override
    @URLAction(mappingId = "novaRelacaoLancamentoProcessoDebito", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
    }

    public List<SelectItem> getTiposProcessoDebito() {
        if (tiposProcessoDebito == null) {
            tiposProcessoDebito = Util.getListSelectItem(TipoProcessoDebito.values());
        }
        return tiposProcessoDebito;
    }
}
