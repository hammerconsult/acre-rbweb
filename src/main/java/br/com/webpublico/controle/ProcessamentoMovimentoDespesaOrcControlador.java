package br.com.webpublico.controle;

import br.com.webpublico.entidades.MovimentoDespesaORC;
import br.com.webpublico.entidadesauxiliares.contabil.SaldoFonteDespesaORCVO;
import br.com.webpublico.enums.OperacaoORC;
import br.com.webpublico.enums.TipoOperacaoORC;
import br.com.webpublico.negocios.AberturaFechamentoExercicioFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.SaldoFonteDespesaORCFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: RenatoRomanini
 * Date: 19/01/15
 * Time: 14:21
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
@URLMapping(id = "processamento-movimento-despesaorc", pattern = "/processamento-movimento-despesaorc/", viewId = "/faces/financeiro/abertura-fechamento-exercicio/processamento-movimento-despesaorc.xhtml")
public class ProcessamentoMovimentoDespesaOrcControlador implements Serializable {

    @EJB
    private AberturaFechamentoExercicioFacade facade;
    @EJB
    private SaldoFonteDespesaORCFacade saldoFonteDespesaORCFacade;
    private MovimentoDespesaORC selecionado;
    private TipoOperacaoORC tipoOperacaoORC;
    private OperacaoORC operacaoORC;
    private List<MovimentoDespesaORC> movimentos;

    public ProcessamentoMovimentoDespesaOrcControlador() {

    }

    private SistemaControlador getSistemaControlador() {
        return (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }

    @URLAction(mappingId = "processamento-movimento-despesaorc", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        SistemaControlador sistemaControlador = getSistemaControlador();
        selecionado = new MovimentoDespesaORC();
        selecionado.setDataMovimento(sistemaControlador.getDataOperacao());
        selecionado.setUnidadeOrganizacional(sistemaControlador.getUnidadeOrganizacionalOrcamentoCorrente());
        tipoOperacaoORC = TipoOperacaoORC.NORMAL;
        operacaoORC = OperacaoORC.DOTACAO;
    }

    public void recuperar() {
        movimentos = facade.recuperarMovimentosDespesaOrc(tipoOperacaoORC, operacaoORC);
    }

    public void gerar() {
        try {
            for (MovimentoDespesaORC movimento : movimentos) {
                SaldoFonteDespesaORCVO vo = new SaldoFonteDespesaORCVO(
                    movimento.getFonteDespesaORC(),
                    movimento.getOperacaoORC(),
                    movimento.getTipoOperacaoORC(),
                    movimento.getValor(),
                    movimento.getDataMovimento(),
                    movimento.getUnidadeOrganizacional(),
                    movimento.getId().toString(),
                    movimento.getClass().getSimpleName(),
                    movimento.getNumeroMovimento(),
                    movimento.getHistorico());
                saldoFonteDespesaORCFacade.gerarSaldoOrcamentario(vo);
            }
            FacesUtil.addOperacaoRealizada("Processo realizado com sucesso.");
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    public List<SelectItem> getOperacoes() {
        return Util.getListSelectItem(OperacaoORC.values());
    }

    public List<SelectItem> getTipoOperacoes() {
        return Util.getListSelectItem(TipoOperacaoORC.values());
    }

    public List<MovimentoDespesaORC> getMovimentos() {
        return movimentos;
    }

    public void setMovimentos(List<MovimentoDespesaORC> movimentos) {
        this.movimentos = movimentos;
    }

    public TipoOperacaoORC getTipoOperacaoORC() {
        return tipoOperacaoORC;
    }

    public void setTipoOperacaoORC(TipoOperacaoORC tipoOperacaoORC) {
        this.tipoOperacaoORC = tipoOperacaoORC;
    }

    public OperacaoORC getOperacaoORC() {
        return operacaoORC;
    }

    public void setOperacaoORC(OperacaoORC operacaoORC) {
        this.operacaoORC = operacaoORC;
    }
}
