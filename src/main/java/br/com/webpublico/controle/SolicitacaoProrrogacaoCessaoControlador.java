package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TipoCessao;
import br.com.webpublico.enums.administrativo.OperacaoMovimentacaoBem;
import br.com.webpublico.exception.MovimentacaoBemException;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.SolicitacaoProrrogacaoCessaoFacade;
import br.com.webpublico.util.AssistenteMovimentacaoBens;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Desenvolvimento
 * Date: 11/06/14
 * Time: 16:42
 * To change this template use File | Settings | File Templates.
 */

@ManagedBean(name = "solicitacaoProrrogacaoCessaoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoSolicitacaoProrrogacao", pattern = "/solicitacao-prorrogacao-cessao/novo/", viewId = "/faces/administrativo/patrimonio/solicitacaoprorrogacaocessao/edita.xhtml"),
    @URLMapping(id = "editarSolicitacaoProrrogacao", pattern = "/solicitacao-prorrogacao-cessao/editar/#{solicitacaoProrrogacaoCessaoControlador.id}/", viewId = "/faces/administrativo/patrimonio/solicitacaoprorrogacaocessao/edita.xhtml"),
    @URLMapping(id = "listarSolicitacaoProrrogacao", pattern = "/solicitacao-prorrogacao-cessao/listar/", viewId = "/faces/administrativo/patrimonio/solicitacaoprorrogacaocessao/lista.xhtml"),
    @URLMapping(id = "verSolicitacaoProrrogacao", pattern = "/solicitacao-prorrogacao-cessao/ver/#{solicitacaoProrrogacaoCessaoControlador.id}/", viewId = "/faces/administrativo/patrimonio/solicitacaoprorrogacaocessao/visualizar.xhtml")
})
public class SolicitacaoProrrogacaoCessaoControlador extends PrettyControlador<SolicitacaoProrrogacaoCessao> implements Serializable, CRUD {

    @EJB
    private SolicitacaoProrrogacaoCessaoFacade facade;
    private List<Cessao> listaCessoes;
    private Boolean podeExcluir;
    private AvaliacaoSolicitacaoProrrogacaoCessao avaliacaoSolicitacaoProrrogacaoCessao;
    private AssistenteMovimentacaoBens assistenteMovimentacao;

    public SolicitacaoProrrogacaoCessaoControlador() {
        super(SolicitacaoProrrogacaoCessao.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @URLAction(mappingId = "novoSolicitacaoProrrogacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        try {
            super.novo();
            selecionado.setDataDaSolicitacao(getDataOperacao());
            selecionado.setSolicitante(facade.getSistemaFacade().getUsuarioCorrente());
            listaCessoes = Lists.newArrayList();
            avaliacaoSolicitacaoProrrogacaoCessao = new AvaliacaoSolicitacaoProrrogacaoCessao();
            iniciarAssistente();
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    @URLAction(mappingId = "verSolicitacaoProrrogacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperarAvaliacaoSolicitacaoProrrogacaoCessao();
    }

    @URLAction(mappingId = "editarSolicitacaoProrrogacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        podeExcluir = facade.getAvaliacaoProrrogacaoCessaoFacade().recuperarAvaliacaoDaSolicitacao(selecionado) == null;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/solicitacao-prorrogacao-cessao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public void salvar() {
        try {
            validarSelecionado();
            iniciarAssistente();
            validarDataLancamentoAndDataOperacaoBem();
            verificarDisponibilidadeBemParaMovimentacao();
            validarResponsaveisUnidades();
            facade.getSingletonBloqueioPatrimonio().bloquearMovimentoPorUnidade(SolicitacaoProrrogacaoCessao.class, assistenteMovimentacao.getUnidadeOrganizacional(), assistenteMovimentacao);
            selecionado = facade.salvarRetornando(selecionado);
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            desbloquearMovimentoPorUnidade();
            redirecionaParaVer();
        } catch (MovimentacaoBemException ex) {
            FacesUtil.executaJavaScript("aguarde.hide()");
            if (facade.getSingletonBloqueioPatrimonio().verificarMovimentoBloqueadoPorUnidade(SolicitacaoProrrogacaoCessao.class, assistenteMovimentacao.getUnidadeOrganizacional())) {
                redireciona();
            }
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (ValidacaoException ex) {
            desbloquearMovimentoPorUnidade();
            FacesUtil.executaJavaScript("aguarde.hide()");
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            desbloquearMovimentoPorUnidade();
            FacesUtil.executaJavaScript("aguarde.hide()");
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        } catch (Exception e) {
            desbloquearMovimentoPorUnidade();
            FacesUtil.executaJavaScript("aguarde.hide()");
            descobrirETratarException(e);
        }
    }

    private void desbloquearMovimentoPorUnidade() {
        facade.getSingletonBloqueioPatrimonio().desbloquearMovimentoPorUnidade(SolicitacaoProrrogacaoCessao.class, assistenteMovimentacao.getUnidadeOrganizacional());
    }

    private void redirecionaParaVer() {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
    }

    private void recuperarAvaliacaoSolicitacaoProrrogacaoCessao() {
        avaliacaoSolicitacaoProrrogacaoCessao = facade.recuperarAvaliacaoSolicitacaoProrrogacaoCessao(selecionado);
    }

    private ConfigMovimentacaoBem recuperarConfiguracaoMovimentacaoBem() {
        ConfigMovimentacaoBem configMovimentacaoBem = facade.getConfigMovimentacaoBemFacade().buscarConfiguracaoMovimentacaoBem(selecionado.getDataDaSolicitacao(), OperacaoMovimentacaoBem.SOLICITACAO_PRORROGACAO_CESSAO_BEM);
        if (configMovimentacaoBem != null) {
            return configMovimentacaoBem;
        }
        return null;
    }

    private void validarDataLancamentoAndDataOperacaoBem() {
        recuperarConfiguracaoMovimentacaoBem();
        assistenteMovimentacao.getConfigMovimentacaoBem().validarDatasMovimentacao(selecionado.getDataDaSolicitacao(), getDataOperacao(), operacao);
    }

    private Date getDataOperacao() {
        return facade.getSistemaFacade().getDataOperacao();
    }

    public void recarregarCessao() {
        try {
            selecionado.setResponsavelOrigem(null);
            selecionado.setResponsavelDestino(null);

            if (selecionado.getLoteCessao() != null) {
                selecionado.setLoteCessao(facade.getLoteCessaoFacade().recuperar(selecionado.getLoteCessao().getId()));
                listaCessoes = facade.getLoteCessaoFacade().buscarCessaoParaDevolucao(selecionado.getLoteCessao());

                selecionado.setResponsavelOrigem(facade.getParametroPatrimonioFacade().
                    recuperarResponsavelVigente(selecionado.getLoteCessao().getUnidadeOrigem(), getDataOperacao()).getResponsavel());

                if (selecionado.getLoteCessao().isInterno()) {
                    selecionado.setResponsavelDestino(facade.getParametroPatrimonioFacade().
                        recuperarResponsavelVigente(selecionado.getLoteCessao().getUnidadeDestino(), getDataOperacao()).getResponsavel());
                }
            }
        } catch (Exception ex) {
            logger.error("Ocorreu um erro ao tentar recuperar o responsável da unidade. " + ex.getMessage());
            FacesUtil.addError("", ex.getMessage());
        }
    }

    public void validarResponsaveisUnidades() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getResponsavelOrigem() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A unidade de origem deve ter um responsável vigente.");
        }
        if (selecionado.getResponsavelDestino() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A unidade de destino deve ter um responsável vigente.");
        }
        ve.lancarException();
    }

    private void verificarDisponibilidadeBemParaMovimentacao() {
        iniciarAssistente();
        List<Bem> bensSelecionados = Lists.newArrayList();
        for (Cessao cessao : listaCessoes) {
            bensSelecionados.add(cessao.getBem());
        }
        facade.getConfigMovimentacaoBemFacade().validarAndRetornarBensDisponiveisParaMovimentacaoComDataRetroativa(bensSelecionados, assistenteMovimentacao);
        if (assistenteMovimentacao.getMensagens() != null && !assistenteMovimentacao.getMensagens().isEmpty()) {
            ValidacaoException ve = new ValidacaoException();
            for (String msg : assistenteMovimentacao.getMensagens()) {
                ve.adicionarMensagemDeCampoObrigatorio(msg);
            }
            ve.lancarException();
        }
    }

    private void iniciarAssistente() {
        ConfigMovimentacaoBem configMovimentacaoBem = recuperarConfiguracaoMovimentacaoBem();
        assistenteMovimentacao = new AssistenteMovimentacaoBens(selecionado.getDataDaSolicitacao(), operacao);
        assistenteMovimentacao.setConfigMovimentacaoBem(configMovimentacaoBem);
        if (selecionado.getLoteCessao() != null) {
            UnidadeOrganizacional unidadeSolicitacao = selecionado.getLoteCessao().getUnidadeOrigem();
            assistenteMovimentacao.setUnidadeOrganizacional(unidadeSolicitacao);
        }
    }

    public void validarSelecionado() {
        ValidacaoException ve = new ValidacaoException();
        selecionado.realizarValidacoes();
        if (selecionado.getNovaDataFinal() != null) {
            if (selecionado.getNovaDataFinal().compareTo(getDataOperacao()) <= 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A nova data final deve ser igual ou posterior a data atual.");
            }
            recarregarCessao();
            if (selecionado.getNovaDataFinal().compareTo(selecionado.getLoteCessao().getUltimoPrazoCessao().getFimDoPrazo()) <= 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A nova data final deve ser posterior a última data final.");
            }
        }
        ve.lancarException();
    }

    public List<SelectItem> getTiposCessao() {
        return Util.getListSelectItemSemCampoVazio(TipoCessao.values());
    }

    public Boolean podeExcluir() {
        return this.podeExcluir;
    }

    public List<LoteCessao> completarLoteCessaoQueNaoPossuiSolicitacaoProrrogacaoParaAvaliar(String filtro) {
        return facade.getLoteCessaoFacade().completarLoteCessaoQueNaoPossuiSolicitacaoProrrogacaoParaAvaliar(filtro, selecionado.getTipoCessao());
    }

    public AvaliacaoSolicitacaoProrrogacaoCessao getAvaliacaoSolicitacaoProrrogacaoCessao() {
        return avaliacaoSolicitacaoProrrogacaoCessao;
    }

    public void setAvaliacaoSolicitacaoProrrogacaoCessao(AvaliacaoSolicitacaoProrrogacaoCessao
                                                             avaliacaoSolicitacaoProrrogacaoCessao) {
        this.avaliacaoSolicitacaoProrrogacaoCessao = avaliacaoSolicitacaoProrrogacaoCessao;
    }

    public void limparCampos() {
        selecionado.setLoteCessao(null);
        selecionado.setResponsavelDestino(null);
        selecionado.setResponsavelOrigem(null);
        if (listaCessoes != null) {
            listaCessoes.clear();
        }
    }

    public List<Cessao> getListaCessoes() {
        return listaCessoes;
    }

    public void setListaCessoes(List<Cessao> listaCessoes) {
        this.listaCessoes = listaCessoes;
    }
}
