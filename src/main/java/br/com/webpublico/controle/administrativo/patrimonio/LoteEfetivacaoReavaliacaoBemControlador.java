package br.com.webpublico.controle.administrativo.patrimonio;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.administrativo.patrimonio.EfetivacaoReavaliacaoBem;
import br.com.webpublico.entidades.administrativo.patrimonio.LoteEfetivacaoReavaliacaoBem;
import br.com.webpublico.entidades.administrativo.patrimonio.LoteReavaliacaoBem;
import br.com.webpublico.entidades.administrativo.patrimonio.ReavaliacaoBem;
import br.com.webpublico.enums.SituacaoDaSolicitacao;
import br.com.webpublico.enums.TipoBem;
import br.com.webpublico.enums.TipoOperacaoReavaliacaoBens;
import br.com.webpublico.enums.administrativo.OperacaoMovimentacaoBem;
import br.com.webpublico.exception.MovimentacaoBemException;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.administrativo.patrimonio.LoteEfetivacaoReavaliacaoBemFacade;
import br.com.webpublico.util.AssistenteMovimentacaoBens;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created by William on 22/10/2015.
 */

@ManagedBean(name = "loteEfetivacaoReavaliacaoBemControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaEfetivacaoReavaliacaoMovel", pattern = "/efetivacao-de-reavaliacao-de-bem-movel/novo/", viewId = "/faces/administrativo/patrimonio/efetivacaoreavaliacaobem/movel/edita.xhtml"),
    @URLMapping(id = "editarEfetivacaoReavaliacaoMovel", pattern = "/efetivacao-de-reavaliacao-de-bem-movel/editar/#{loteEfetivacaoReavaliacaoBemControlador.id}/", viewId = "/faces/administrativo/patrimonio/efetivacaoreavaliacaobem/movel/edita.xhtml"),
    @URLMapping(id = "listarEfetivacaoReavaliacaoMovel", pattern = "/efetivacao-de-reavaliacao-de-bem-movel/listar/", viewId = "/faces/administrativo/patrimonio/efetivacaoreavaliacaobem/movel/lista.xhtml"),
    @URLMapping(id = "verEfetivacaoReavaliacaoMovel", pattern = "/efetivacao-de-reavaliacao-de-bem-movel/ver/#{loteEfetivacaoReavaliacaoBemControlador.id}/", viewId = "/faces/administrativo/patrimonio/efetivacaoreavaliacaobem/movel/visualizar.xhtml"),

    @URLMapping(id = "novaEfetivacaoReavaliacaoImovel", pattern = "/efetivacao-de-reavaliacao-de-bem-imovel/novo/", viewId = "/faces/administrativo/patrimonio/efetivacaoreavaliacaobem/imovel/edita.xhtml"),
    @URLMapping(id = "editarEfetivacaoReavaliacaoImovel", pattern = "/efetivacao-de-reavaliacao-de-bem-imovel/editar/#{loteEfetivacaoReavaliacaoBemControlador.id}/", viewId = "/faces/administrativo/patrimonio/efetivacaoreavaliacaobem/imovel/edita.xhtml"),
    @URLMapping(id = "listarEfetivacaoReavaliacaoImovel", pattern = "/efetivacao-de-reavaliacao-de-bem-imovel/listar/", viewId = "/faces/administrativo/patrimonio/efetivacaoreavaliacaobem/imovel/lista.xhtml"),
    @URLMapping(id = "verEfetivacaoReavaliacaoImovel", pattern = "/efetivacao-de-reavaliacao-de-bem-imovel/ver/#{loteEfetivacaoReavaliacaoBemControlador.id}/", viewId = "/faces/administrativo/patrimonio/efetivacaoreavaliacaobem/imovel/visualizar.xhtml")
})
public class LoteEfetivacaoReavaliacaoBemControlador extends PrettyControlador<LoteEfetivacaoReavaliacaoBem> implements Serializable, CRUD {

    @EJB
    private LoteEfetivacaoReavaliacaoBemFacade facade;
    private PessoaFisica responsavel;
    private List<LoteReavaliacaoBem> lotesEncontrados;
    private List<LoteReavaliacaoBem> lotesSelecionados;
    private List<LoteReavaliacaoBem> lotesSelecionadosRejeitados;
    private List<EfetivacaoReavaliacaoBem> efetivacoesReavaliacao;
    private LoteReavaliacaoBem loteSelecionado;
    private HashMap<ReavaliacaoBem, UnidadeOrganizacional> atribuirUnidadeOrcamentaria;
    private AssistenteMovimentacaoBens assistenteMovimentacao;

    public LoteEfetivacaoReavaliacaoBemControlador() {
        super(LoteEfetivacaoReavaliacaoBem.class);
    }

    @URLAction(mappingId = "novaEfetivacaoReavaliacaoMovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novaEfetivacaoMovel() {
        try {
            novo();
            selecionado.setTipoBem(TipoBem.MOVEIS);
            recuperarConfiguracaoMovimentacaoBem();
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    @URLAction(mappingId = "novaEfetivacaoReavaliacaoImovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novaEfetivacaoImovel() {
        novo();
        selecionado.setTipoBem(TipoBem.IMOVEIS);
    }

    @Override
    public void novo() {
        super.novo();
        iniciarlizarAtributosOperacaoNovo();
    }

    @URLAction(mappingId = "verEfetivacaoReavaliacaoMovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verEfetivacaoMovel() {
        ver();
    }

    @URLAction(mappingId = "verEfetivacaoReavaliacaoImovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verEfetivacaoImovel() {
        ver();
    }

    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarEfetivacaoReavaliacaoMovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editarEfetivacaoMovel() {
        editar();
    }

    @URLAction(mappingId = "editarEfetivacaoReavaliacaoImovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editarEfetivacaoImovel() {
        editar();
    }

    @Override
    public void editar() {
        super.editar();
        iniciarlizarAtributosOperacaoVerEdita();
    }

    @Override
    public String getCaminhoPadrao() {
        switch (selecionado.getTipoBem()) {
            case IMOVEIS:
                return "/efetivacao-de-reavaliacao-de-bem-imovel/";
            case MOVEIS:
                return "/efetivacao-de-reavaliacao-de-bem-movel/";
            default:
                return "";
        }
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public void salvar() {
        try {
            novoAssistenteMovimentacao();
            validarSalvar();
            preencherLotesSelecionados();
            bloquearMovimentoSingleton();
            selecionado = facade.salvarGerandoReavaliacao(selecionado, lotesSelecionados, assistenteMovimentacao.getConfigMovimentacaoBem());
            facade.efetivarLotesRejeitados(selecionado, lotesSelecionadosRejeitados, assistenteMovimentacao.getConfigMovimentacaoBem());
            FacesUtil.addOperacaoRealizada("A efetivação de reavaliação código: " + selecionado.getCodigo() + " foi realizada com sucesso!");
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
            desbloquearMovimentoSingleton();
        } catch (MovimentacaoBemException ex) {
            redireciona();
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (ValidacaoException ex) {
            desbloquearMovimentoSingleton();
            FacesUtil.executaJavaScript("aguarde.hide()");
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            desbloquearMovimentoSingleton();
            FacesUtil.executaJavaScript("aguarde.hide()");
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        } catch (Exception e) {
            descobrirETratarException(e);
            FacesUtil.executaJavaScript("aguarde.hide()");
            desbloquearMovimentoSingleton();
        }
    }

    private void bloquearMovimentoSingleton() {
        facade.getSingletonBloqueioPatrimonio().bloquearMovimentoPorUnidade(LoteEfetivacaoReavaliacaoBem.class, selecionado.getUnidadeOrganizacional(), assistenteMovimentacao);
    }

    private void desbloquearMovimentoSingleton() {
        if (selecionado.getUnidadeOrganizacional() != null) {
            facade.getSingletonBloqueioPatrimonio().desbloquearMovimentoPorUnidade(LoteEfetivacaoReavaliacaoBem.class, selecionado.getUnidadeOrganizacional());
        }
    }

    private ConfigMovimentacaoBem recuperarConfiguracaoMovimentacaoBem() {
        return facade.getConfigMovimentacaoBemFacade().buscarConfiguracaoMovimentacaoBem(selecionado.getDataEfetivacao(), OperacaoMovimentacaoBem.EFETIVACAO_REAVALIACAO_BEM);
    }

    private void validarDataLancamentoAndDataOperacaoBem() {
        ConfigMovimentacaoBem configMovimentacaoBem = assistenteMovimentacao.getConfigMovimentacaoBem();
        if (configMovimentacaoBem != null) {
            configMovimentacaoBem.validarDatasMovimentacao(selecionado.getDataEfetivacao(), getDataOperacao(), operacao);
        }
    }

    private Date getDataOperacao() {
        return facade.getSistemaFacade().getDataOperacao();
    }

    private void validarSalvar() {
        selecionado.realizarValidacoes();
        ValidacaoException ve = new ValidacaoException();
        if (lotesEncontrados.isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Nenhum lote encontrado para continuar a operação.");

        } else {
            for (LoteReavaliacaoBem ltb : lotesEncontrados) {
                if (!ltb.foiAceito() && !ltb.foiRecusado()) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Marque pelo menos uma reavaliação com a opção \"Aceita\" ou \"Recusada\" para concluir a operação.");
                }
                if (ltb.foiRecusado() && (ltb.getMotivoRecusa() == null || ltb.getMotivoRecusa().trim().isEmpty())) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("A reavaliação " + ltb.getCodigo() + " foi recusada, portanto o motivo da recusa deve ser informado.");
                }
            }
        }
        validarDataLancamentoAndDataOperacaoBem();
        ve.lancarException();
    }

    private void preencherLotesSelecionados() {
        lotesSelecionados.clear();
        for (LoteReavaliacaoBem lote : lotesEncontrados) {
            if (lote.getSituacaoReavaliacaoBem().equals(SituacaoDaSolicitacao.ACEITA)) {
                lotesSelecionados.add(lote);
            }
        }
        for (LoteReavaliacaoBem lote : lotesEncontrados) {
            if (lote.getSituacaoReavaliacaoBem().equals(SituacaoDaSolicitacao.RECUSADA)) {
                lotesSelecionadosRejeitados.add(lote);
            }
        }
    }

    private void iniciarlizarAtributosOperacaoNovo() {
        selecionado.setDataEfetivacao(getDataOperacao());
        selecionado.setUsuarioSistema(facade.getSistemaFacade().getUsuarioCorrente());
        responsavel = facade.getSistemaFacade().getUsuarioCorrente().getPessoaFisica();
        lotesSelecionadosRejeitados = new ArrayList<>();
        lotesSelecionados = new ArrayList<>();
        loteSelecionado = new LoteReavaliacaoBem();
        atribuirUnidadeOrcamentaria = new HashMap<>();
    }

    private void iniciarlizarAtributosOperacaoVerEdita() {
        selecionado = facade.recuperar(selecionado.getId());
        responsavel = selecionado.getUsuarioSistema().getPessoaFisica();
        lotesEncontrados = facade.buscarLotesEfetivados(selecionado);
        loteSelecionado = new LoteReavaliacaoBem();
    }

    public void pesquisar() {
        try {
            novoAssistenteMovimentacao();
            validarPesquisaBens();
            lotesEncontrados = new ArrayList<>();
            List<LoteReavaliacaoBem> lotesPesquisados = facade.buscarLotesNaoAvaliadosPorUnidadeEResponsavel(selecionado.getUnidadeOrganizacional(), responsavel, selecionado.getTipoBem(), selecionado.getTipoOperacaoBem());
            if (lotesPesquisados == null || lotesPesquisados.isEmpty()) {
                FacesUtil.addOperacaoNaoRealizada("Nenhum lote encontrato para a unidade " + selecionado.getHierarquiaOrganizacional() + " e responsável " + responsavel + ".");
                return;
            }
            for (LoteReavaliacaoBem lote : lotesPesquisados) {
                lotesEncontrados.add(facade.getLoteReavaliacaoBemFacade().recuperar(lote.getId()));
            }
            validarMovimentacaoBensComDataRetroativa();
            validarResponsavelPatrimonio();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    private void validarResponsavelPatrimonio() {
        ResponsavelPatrimonio responsavelPatrimonio = null;
        try {
            responsavelPatrimonio = facade.getParametroPatrimonioFacade().recuperarResponsavelVigente(selecionado.getUnidadeOrganizacional(), getDataOperacao());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
        if (responsavelPatrimonio == null || !responsavel.equals(responsavelPatrimonio.getResponsavel())) {
            FacesUtil.addAtencao("Você " + responsavel.getNome() + " não é resposável pela unidade organizacional " + selecionado.getHierarquiaOrganizacional() + " e portanto não tem autorizacão para efetivação da reavaliação");
        }
    }

    private void validarPesquisaBens() {
        selecionado.realizarValidacoes();
        validarDataLancamentoAndDataOperacaoBem();
    }

    private void validarMovimentacaoBensComDataRetroativa() {
        novoAssistenteMovimentacao();
        List<Bem> bensPesquisados = Lists.newArrayList();
        for (LoteReavaliacaoBem lote : lotesEncontrados) {
            for (ReavaliacaoBem reavaliacaoBem : lote.getReavaliacaoBens()) {
                bensPesquisados.add(reavaliacaoBem.getBem());
            }
            facade.getConfigMovimentacaoBemFacade().validarAndRetornarBensDisponiveisParaMovimentacaoComDataRetroativa(bensPesquisados, assistenteMovimentacao);
        }
        if (assistenteMovimentacao.getMensagens() != null && !assistenteMovimentacao.getMensagens().isEmpty()) {
            for (String mensagem : assistenteMovimentacao.getMensagens()) {
                FacesUtil.addOperacaoNaoPermitida(mensagem);
            }
            lotesEncontrados = Lists.newArrayList();
        }
    }

    private void novoAssistenteMovimentacao() {
        assistenteMovimentacao = new AssistenteMovimentacaoBens(selecionado.getDataEfetivacao(), operacao);
        ConfigMovimentacaoBem configMovimentacaoBem = recuperarConfiguracaoMovimentacaoBem();
        assistenteMovimentacao.setConfigMovimentacaoBem(configMovimentacaoBem);
    }

    public void atribuirLoteSelecionado(LoteReavaliacaoBem lote) {
        if (lote != null) {
            this.loteSelecionado = lote;
            if (isOperacaoVer()) {
                efetivacoesReavaliacao = facade.buscarEfetivacoesReavaliacaoPorLoteReavaliacao(lote);
            }
        }
        FacesUtil.executaJavaScript("aguarde.hide()");
    }

    public List<SelectItem> getListSelectItemOpcaoAcaoReavaliacao() {
        return Util.getListSelectItem(Arrays.asList(SituacaoDaSolicitacao.AGUARDANDO_EFETIVACAO, SituacaoDaSolicitacao.ACEITA, SituacaoDaSolicitacao.RECUSADA));
    }

    public PessoaFisica getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(PessoaFisica responsavel) {
        if (responsavel != null)
            this.responsavel = responsavel;
    }

    public List<LoteReavaliacaoBem> getLotesEncontrados() {
        return lotesEncontrados;
    }

    public void setLotesEncontrados(List<LoteReavaliacaoBem> lotesEncontrados) {
        if (lotesEncontrados != null)
            this.lotesEncontrados = lotesEncontrados;
    }

    public List<LoteReavaliacaoBem> getLotesSelecionados() {
        return lotesSelecionados;
    }

    public void setLotesSelecionados(List<LoteReavaliacaoBem> lotesSelecionados) {
        if (lotesSelecionados != null)
            this.lotesSelecionados = lotesSelecionados;
    }

    public LoteReavaliacaoBem getLoteSelecionado() {
        return loteSelecionado;
    }

    public void setLoteSelecionado(LoteReavaliacaoBem loteSelecionado) {
        this.loteSelecionado = loteSelecionado;
    }

    public HashMap<ReavaliacaoBem, UnidadeOrganizacional> getAtribuirUnidadeOrcamentaria() {
        return atribuirUnidadeOrcamentaria;
    }

    public void limparTabela() {
        lotesEncontrados = Lists.newArrayList();
    }

    private void validarMotivoRecusa() {
        ValidacaoException ve = new ValidacaoException();
        if (Strings.isNullOrEmpty(loteSelecionado.getMotivoRecusa())) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Motivo deve ser informado.");
        }
        ve.lancarException();
    }

    public void confirmarMotivoRecusa() {
        try {
            validarMotivoRecusa();
            FacesUtil.executaJavaScript("dialogmotivorecusa.hide()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public void cancelarMotivoRecusa(LoteReavaliacaoBem lote) {
        lote.setMotivoRecusa(null);
    }

    public void cancelarMotivoRecusa() {
        loteSelecionado.setMotivoRecusa(null);
        FacesUtil.executaJavaScript("dialogmotivorecusa.hide()");
    }

    public List<SelectItem> getTipoOperacaoBens() {
        if (selecionado.getTipoBem().equals(TipoBem.MOVEIS)) {
            return Util.getListSelectItem(TipoOperacaoReavaliacaoBens.getOperacoesBemMovel());
        } else {
            return Util.getListSelectItem(TipoOperacaoReavaliacaoBens.getOperacoesBemImovel());
        }
    }

    public BigDecimal calcularValorReavaliacao(ReavaliacaoBem reavaliacaoBem) {
        if (selecionado.isReavaliacaoBemAumentativa()) {
            return reavaliacaoBem.getValor().add(reavaliacaoBem.getEstadoResultante().getValorOriginal());
        }
        return reavaliacaoBem.getEstadoResultante().getValorOriginal().subtract(reavaliacaoBem.getValor());
    }

    public List<EfetivacaoReavaliacaoBem> getEfetivacoesReavaliacao() {
        return efetivacoesReavaliacao;
    }

    public void setEfetivacoesReavaliacao(List<EfetivacaoReavaliacaoBem> efetivacoesReavaliacao) {
        this.efetivacoesReavaliacao = efetivacoesReavaliacao;
    }
}
