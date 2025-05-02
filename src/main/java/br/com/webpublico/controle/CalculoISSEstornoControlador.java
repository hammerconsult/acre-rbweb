package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLActions;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ManagedBean(name = "calculoISSEstornoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "listarEstornoIssFixo", pattern = "/tributario/estorno-de-iss-fixo/listar/", viewId = "/faces/tributario/issqn/calculoestornofixo/lista.xhtml"),
    @URLMapping(id = "novoEstornoIssFixo", pattern = "/tributario/estorno-de-iss-fixo/novo/", viewId = "/faces/tributario/issqn/calculoestornofixo/editar.xhtml"),
    @URLMapping(id = "verEstornoIssFixo", pattern = "/tributario/estorno-de-iss-fixo/ver/#{calculoISSEstornoControlador.id}/", viewId = "/faces/tributario/issqn/calculoestornofixo/visualizar.xhtml"),

    @URLMapping(id = "listarEstornoIssMensal", pattern = "/tributario/estorno-de-iss-mensal/listar/", viewId = "/faces/tributario/issqn/calculohomologadoestorno/lista.xhtml"),
    @URLMapping(id = "novoEstornoIssMensal", pattern = "/tributario/estorno-de-iss-mensal/novo/", viewId = "/faces/tributario/issqn/calculohomologadoestorno/editar.xhtml"),
    @URLMapping(id = "verEstornoIssMensal", pattern = "/tributario/estorno-de-iss-mensal/ver/#{calculoISSEstornoControlador.id}/", viewId = "/faces/tributario/issqn/calculohomologadoestorno/visualizar.xhtml"),

    @URLMapping(id = "listarEstornoIssEstimado", pattern = "/tributario/estorno-de-iss-estimado/listar/", viewId = "/faces/tributario/issqn/calculoestornoestimado/lista.xhtml"),
    @URLMapping(id = "novoEstornoIssEstimado", pattern = "/tributario/estorno-de-iss-estimado/novo/", viewId = "/faces/tributario/issqn/calculoestornoestimado/editar.xhtml"),
    @URLMapping(id = "verEstornoIssEstimado", pattern = "/tributario/estorno-de-iss-estimado/ver/#{calculoISSEstornoControlador.id}/", viewId = "/faces/tributario/issqn/calculoestornoestimado/visualizar.xhtml")
})

public class CalculoISSEstornoControlador extends PrettyControlador<CalculoISSEstorno> implements Serializable, CRUD {

    protected static final Logger logger = LoggerFactory.getLogger(CalculoISSEstornoControlador.class);
    @EJB
    private CadastroEconomicoFacade cadastroEconomicoFacade;
    @EJB
    private CalculaISSFacade calculaISSFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ConsultaDebitoFacade consultaDebitoFacade;
    private List<CalculoISS> calculosParaEstorno;
    private List<CalculoISS> calculosSelecionadosParaEstorno;
    private CadastroEconomico cadastroEconomicoParaPesquisa;

    public CalculoISSEstornoControlador() {
        super(CalculoISSEstorno.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return calculaISSFacade;
    }

    public List<CalculoISS> getCalculosParaEstorno() {
        return calculosParaEstorno;
    }

    public void setCalculosParaEstorno(List<CalculoISS> calculosParaEstorno) {
        this.calculosParaEstorno = calculosParaEstorno;
    }

    public List<CalculoISS> getCalculosSelecionadosParaEstorno() {
        return calculosSelecionadosParaEstorno;
    }

    public void setCalculosSelecionadosParaEstorno(List<CalculoISS> calculosSelecionadosParaEstorno) {
        this.calculosSelecionadosParaEstorno = calculosSelecionadosParaEstorno;
    }

    public Mes retornaMes(Integer mesReferencia) {
        if (mesReferencia > 0) {
            return Mes.getMesToInt(mesReferencia);
        }
        return Mes.JANEIRO;
    }

    private SistemaControlador getSistemaControlador() {
        return Util.getSistemaControlador();
    }

    public CadastroEconomico getCadastroEconomicoParaPesquisa() {
        return cadastroEconomicoParaPesquisa;
    }

    public void setCadastroEconomicoParaPesquisa(CadastroEconomico cadastroEconomicoParaPesquisa) {
        this.cadastroEconomicoParaPesquisa = cadastroEconomicoParaPesquisa;
    }


    @Override
    public String getCaminhoPadrao() {
        return "/tributario/estorno-de-iss-" + selecionado.getTipoCalculoISS().getDescricao().toLowerCase() + "/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    @URLActions(actions = {
        @URLAction(mappingId = "verEstornoIssFixo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false),
        @URLAction(mappingId = "verEstornoIssMensal", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false),
        @URLAction(mappingId = "verEstornoIssEstimado", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    })
    public void editar() {
        selecionado = calculaISSFacade.recuperarEstornoPorId(getId());
        setId(selecionado.getId());
    }

    private void novoEstorno() {
        super.novo();
        selecionado.setUsuarioEstorno(getSistemaControlador().getUsuarioCorrente());
        selecionado.setDataEstorno(new Date());
        calculosParaEstorno = Lists.newArrayList();
        calculosSelecionadosParaEstorno = Lists.newArrayList();
        cadastroEconomicoParaPesquisa = null;
    }

    @URLAction(mappingId = "novoEstornoIssMensal", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoEstornoIssMensal() {
        novoEstorno();
        selecionado.setTipoCalculoISS(TipoCalculoISS.MENSAL);
    }

    @URLAction(mappingId = "novoEstornoIssFixo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoEstornoIssFixo() {
        novoEstorno();
        selecionado.setTipoCalculoISS(TipoCalculoISS.FIXO);
    }

    @URLAction(mappingId = "novoEstornoIssEstimado", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoEstornoIssEstimado() {
        novoEstorno();
        selecionado.setTipoCalculoISS(TipoCalculoISS.ESTIMADO);
    }

    private void validarEstorno() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getMotivoEstorno() == null || selecionado.getMotivoEstorno().trim().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o motivo do estorno!");
        }
        if (calculosSelecionadosParaEstorno.isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Ao menos um cálculo de ISS " + selecionado.getTipoCalculoISS().getDescricao() + " deve ser selecionado!");
        }
        ve.lancarException();
    }

    @Override
    public void salvar() {
        try {
            validarEstorno();
            estornarCalculoIss();
            selecionado = calculaISSFacade.salvarEstorno(selecionado);

            FacesUtil.addInfo("Estornado com sucesso!", "Lançamento estornado com sucesso!");
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarParcelasEmAberto() {
        ValidacaoException ve = new ValidacaoException();
        for (CalculoISS calculo : calculosSelecionadosParaEstorno) {
            List<ParcelaValorDivida> recuperaParcelasCalculo = calculaISSFacade.buscarParcelasCalculo(calculo);
            for (ParcelaValorDivida parcela : recuperaParcelasCalculo) {
                if (!parcela.getSituacaoAtual().getSituacaoParcela().equals(SituacaoParcela.EM_ABERTO)
                    && !parcela.getSituacaoAtual().getSituacaoParcela().equals(SituacaoParcela.CANCELAMENTO)
                    && !parcela.getSituacaoAtual().getSituacaoParcela().equals(SituacaoParcela.ESTORNADO)) {
                    String referencia = (!TipoCalculoISS.FIXO.equals(selecionado.getTipoCalculoISS()) && calculo.getProcessoCalculoISS().getMesReferencia() != null ?
                        calculo.getProcessoCalculoISS().getMesReferencia() + "/" : "") + calculo.getProcessoCalculoISS().getExercicio();
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O lançamento de <b>" + referencia + "</b> possui débito(s) com parcela(s) que não estão 'Em Aberto'!");
                    break;
                }
            }
        }
        ve.lancarException();
    }

    public void estornarCalculoIss() throws ValidacaoException {
        try {
            validarParcelasEmAberto();
            for (CalculoISS calculo : calculosSelecionadosParaEstorno) {
                calculo.setTipoSituacaoCalculoISS(TipoSituacaoCalculoISS.ESTORNADO);
                calculo.setCalculoISSEstorno(selecionado);
                alterarSituacaoParcelas(calculo);
                selecionado.getCalculos().add(calculo);
            }
        } catch (ValidacaoException ve) {
            throw ve;
        }
    }

    private void alterarSituacaoParcelas(Calculo calculo) {
        try {
            List<ParcelaValorDivida> parcelasDoCalculo = calculaISSFacade.buscarParcelasCalculo(calculo);
            for (ParcelaValorDivida parcela : parcelasDoCalculo) {
                SituacaoParcelaValorDivida ultimaSituacao = parcela.getUltimaSituacao();
                if (!ultimaSituacao.getSituacaoParcela().isPago()) {
                    SituacaoParcelaValorDivida novaSituacao = new SituacaoParcelaValorDivida(SituacaoParcela.ESTORNADO, parcela, ultimaSituacao.getSaldo());
                    calculaISSFacade.salvarSituacaoParcelaValorDivida(novaSituacao);
                }
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
    }

    public List<SituacaoCadastralCadastroEconomico> getSituacoesDisponiveis() {
        List<SituacaoCadastralCadastroEconomico> situacoes = new ArrayList<>();
        situacoes.add(SituacaoCadastralCadastroEconomico.ATIVA_REGULAR);
        situacoes.add(SituacaoCadastralCadastroEconomico.ATIVA_NAO_REGULAR);
        return situacoes;
    }

    private void validarPesquisa() {
        ValidacaoException ve = new ValidacaoException();
        if (cadastroEconomicoParaPesquisa == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Cadastro Econômico para pesquisar!");
        }
        ve.lancarException();
    }

    public void pesquisarIssParaEstorno() {
        try {
            calculosSelecionadosParaEstorno = Lists.newArrayList();
            validarPesquisa();
            calculosParaEstorno = calculaISSFacade.buscarCalculosParaEstorno(selecionado.getTipoCalculoISS(), cadastroEconomicoParaPesquisa);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void adicionarLancamentoParaMulta(CalculoISS calculo) {
        if (calculosSelecionadosParaEstorno == null) {
            calculosSelecionadosParaEstorno = Lists.newArrayList();
        }
        calculosSelecionadosParaEstorno.add(calculo);
    }

    public void removerLancamentoParaMulta(CalculoISS calculo) {
        calculosSelecionadosParaEstorno.remove(calculo);
    }

    public boolean containsLancamentoParaMulta(CalculoISS calculo) {
        return calculosSelecionadosParaEstorno.contains(calculo);
    }

    public void adicionarTodosLancamentoParaMulta() {
        for (CalculoISS calculo : calculosParaEstorno) {
            if (!calculosSelecionadosParaEstorno.contains(calculo)) {
                calculosSelecionadosParaEstorno.add(calculo);
            }
        }
    }

    public void removerTodosLancamentoParaMulta() {
        calculosSelecionadosParaEstorno.clear();
    }

    public boolean containsTodosLancamentoParaMulta() {
        boolean todos = true;
        for (CalculoISS calculo : calculosParaEstorno) {
            if (!calculosSelecionadosParaEstorno.contains(calculo)) {
                todos = false;
                break;
            }
        }
        return todos;
    }

}
