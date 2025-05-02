package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.RelatorioConcialicaoBancariaControlador;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.LancamentoConciliacaoBancaria;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Roma
 * Date: 30/05/14
 * Time: 09:39
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean(name = "movimentoBancarioConciliacaoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-movimento-bancario-concilicao", pattern = "/conciliar/", viewId = "/faces/financeiro/conciliacao/movimentobancario/edita.xhtml")
})
public class MovimentoBancarioConciliacaoControlador extends ConciliacaoBancariaSuperControlador {

    @URLAction(mappingId = "novo-movimento-bancario-concilicao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
    }

    @Override
    public void buscarPendencias() {
        if (conciliacao.getContaBancaria() != null) {
            pendencias = getFacade().buscarLancamentosPorContaBancariaConciliacao(filtroPendencia);
        }
    }

    @Override
    public void preencherFiltrosPendencia() {
        super.preencherFiltrosPendencia();
        filtroPendencia.setDataInicial(DataUtil.getPrimeiroDiaAno(sistemaControlador.getExercicioCorrente().getAno()));
        filtroPendencia.setDataFinal(conciliacao.getData());
    }

    public void gerarRelatorioConciliacao(Boolean diferenteZero) {
        try {
            validarRelatorioConciliacao(diferenteZero);
            RelatorioConcialicaoBancariaControlador relatorioConcialicaoBancariaControlador = (RelatorioConcialicaoBancariaControlador) Util.getControladorPeloNome("relatorioConcialicaoBancariaControlador");
            relatorioConcialicaoBancariaControlador.setFiltros("");
            relatorioConcialicaoBancariaControlador.setContaBancariaEntidade(saldoConstContaBancaria.getContaBancariaEntidade());
            relatorioConcialicaoBancariaControlador.setDataReferencia(lancamentoConciliacaoBancaria.getData());
            relatorioConcialicaoBancariaControlador.setListaUnidades(new ArrayList<HierarquiaOrganizacional>());
            relatorioConcialicaoBancariaControlador.setDiferenteZero(diferenteZero);
            relatorioConcialicaoBancariaControlador.gerarRelatorio();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public void baixarPendencia(LancamentoConciliacaoBancaria lancamentoConciliacaoBancaria) {
        try {
            validarDataConciliacao(lancamentoConciliacaoBancaria);
            lancamentoConciliacaoBancaria.setDataConciliacao(conciliacao.getData());
            getFacade().salvar(lancamentoConciliacaoBancaria);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarDataConciliacao(LancamentoConciliacaoBancaria lancamentoConciliacaoBancaria) {
        ValidacaoException ve = new ValidacaoException();
        if (DataUtil.getAno(lancamentoConciliacaoBancaria.getData()).compareTo(DataUtil.getAno(conciliacao.getData())) != 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A data de conciliação está com o exercício diferente da data do registro.");
        }
        ve.lancarException();
    }

    public void removerPendencia(LancamentoConciliacaoBancaria lancamentoConciliacaoBancaria) {
        if (!getFacade().getFaseFacade().temBloqueioFaseParaRecurso("/financeiro/conciliacao/movimentobancario/edita.xhtml", lancamentoConciliacaoBancaria.getData(), lancamentoConciliacaoBancaria.getUnidadeOrganizacional(), sistemaControlador.getExercicioCorrente())) {
            getFacade().remover(lancamentoConciliacaoBancaria);
            buscarPendencias();
        } else {
            FacesUtil.addOperacaoNaoPermitida("A data de lançamento está fora do período fase!");
        }
    }

    public void alterarPendencia(LancamentoConciliacaoBancaria lancamentoConciliacaoBancaria) {
        this.lancamentoConciliacaoBancaria = lancamentoConciliacaoBancaria;
        definirUnidadeDaContaFinancieraNaPendencia();
    }

    public String getCaminhoOrigem() {
        return "/conciliar/";
    }

    public void navegarParaConciliacaoManual() {
        Web.navegacao(getCaminhoOrigem(), redirecionarConciliacaoManual(), conciliacao);
    }

    public void navegarParaGerarReceita() {
        Web.navegacao(getCaminhoOrigem(), "/receita-realizada/novo/", conciliacao);
    }
}
