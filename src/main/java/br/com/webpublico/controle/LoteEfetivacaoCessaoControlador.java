package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SituacaoLoteCessao;
import br.com.webpublico.enums.administrativo.OperacaoMovimentacaoBem;
import br.com.webpublico.exception.MovimentacaoBemException;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.LoteEfetivacaoCessaoFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.AssistenteMovimentacaoBens;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.hibernate.LazyInitializationException;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Desenvolvimento
 * Date: 15/05/14
 * Time: 16:21
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean(name = "loteEfetivacaoCessaoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaEfetivacaoCessao", pattern = "/efetivacao-cessao/novo/", viewId = "/faces/administrativo/patrimonio/efetivacaocessao/edita.xhtml"),
    @URLMapping(id = "editarEfetivacaoCessao", pattern = "/efetivacao-cessao/editar/#{loteEfetivacaoCessaoControlador.id}/", viewId = "/faces/administrativo/patrimonio/efetivacaocessao/edita.xhtml"),
    @URLMapping(id = "listarEfetivacaoCessao", pattern = "/efetivacao-cessao/listar/", viewId = "/faces/administrativo/patrimonio/efetivacaocessao/lista.xhtml"),
    @URLMapping(id = "verEfetivacaoCessao", pattern = "/efetivacao-cessao/ver/#{loteEfetivacaoCessaoControlador.id}/", viewId = "/faces/administrativo/patrimonio/efetivacaocessao/visualizar.xhtml")
})
public class LoteEfetivacaoCessaoControlador extends PrettyControlador<LoteEfetivacaoCessao> implements Serializable, CRUD {

    @EJB
    private LoteEfetivacaoCessaoFacade facade;
    private LoteCessao loteSelecionado;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private List<LoteCessao> lotesEncontrados;
    private List<LoteCessao> lotesSelecionados;
    private AssistenteMovimentacaoBens assistenteMovimentacao;
    private ConfigMovimentacaoBem configMovimentacaoBem;

    public LoteEfetivacaoCessaoControlador() {
        super(LoteEfetivacaoCessao.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }


    @Override
    public void salvar() {
        try {
            selecionado.setUnidadeOrganizacional(hierarquiaOrganizacional != null ? hierarquiaOrganizacional.getSubordinada() : null);
            preencherLotesSelecionados();
            validarNegocio();
            validarDataLancamentoAndDataOperacaoBem();
            selecionado = facade.salvarGerandoEfetivacoes(selecionado, lotesSelecionados, configMovimentacaoBem);
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            desbloquearMovimentoSingleton();
            redirecionaParaVer();
        } catch (MovimentacaoBemException ex) {
            FacesUtil.executaJavaScript("aguarde.hide()");
            if (facade.getSingletonBloqueioPatrimonio().verificarMovimentoBloqueadoPorUnidade(LoteEfetivacaoCessao.class, selecionado.getUnidadeOrganizacional())) {
                redireciona();
            }
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (ValidacaoException ex) {
            desbloquearMovimentoSingleton();
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            desbloquearMovimentoSingleton();
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        } catch (Exception e) {
            desbloquearMovimentoSingleton();
            descobrirETratarException(e);
        }
    }

    private void desbloquearMovimentoSingleton() {
        facade.getSingletonBloqueioPatrimonio().desbloquearMovimentoPorUnidade(LoteEfetivacaoCessao.class, selecionado.getUnidadeOrganizacional());
        FacesUtil.executaJavaScript("aguarde.hide()");
    }

    public void redirecionaParaVer() {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
    }

    private void validarNegocio() {
        ValidacaoException ve = new ValidacaoException();
        Util.validarCampos(selecionado);
        if (lotesSelecionados.isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Marque pelo menos uma Cessão com a opção \"Aceita\" ou \"Recusada\" para concluir a operação.");
        } else {
            for (LoteCessao ltb : lotesSelecionados) {
                if (ltb.foiRecusado() && (ltb.getMotivoRecusa() == null || ltb.getMotivoRecusa().isEmpty())) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("A Cessão " + ltb.getCodigo() + " foi recusada, portanto o motivo da recusa deve ser informado.");
                }
            }
        }
        ve.lancarException();
    }

    private void recuperarConfiguracaoMovimentacaoBem() {
        if (configMovimentacaoBem == null) {
            this.configMovimentacaoBem = facade.getConfigMovimentacaoBemFacade().buscarConfiguracaoMovimentacaoBem(selecionado.getDataEfetivacao(), OperacaoMovimentacaoBem.EFETIVACAO_CESSAO_BEM);
        }
    }

    private void validarDataLancamentoAndDataOperacaoBem() {
        recuperarConfiguracaoMovimentacaoBem();
        configMovimentacaoBem.validarDatasMovimentacao(selecionado.getDataEfetivacao(), getDataOperacao(), operacao);
    }

    private Date getDataOperacao() {
        return facade.getSistemaFacade().getDataOperacao();
    }

    public void confirmarRecusa() {
        if (loteSelecionado.getMotivoRecusa() == null || loteSelecionado.getMotivoRecusa().isEmpty()) {
            FacesUtil.addCampoObrigatorio("O campo motivo da recusa deve ser informado.");
            return;
        }
        FacesUtil.executaJavaScript("dlgRecusa.hide()");
    }

    public void cancelarRecusa() {
        loteSelecionado.setMotivoRecusa(null);
        FacesUtil.executaJavaScript("dlgRecusa.hide()");
    }

    private void preencherLotesSelecionados() {
        lotesSelecionados.clear();
        for (LoteCessao lote : lotesEncontrados) {
            if (!lote.getSituacaoLoteCessao().equals(SituacaoLoteCessao.AGUARDANDO_EFETIVACAO)) {
                lotesSelecionados.add(lote);
            }
        }
        facade.getSingletonBloqueioPatrimonio().bloquearMovimentoPorUnidade(LoteEfetivacaoCessao.class, selecionado.getUnidadeOrganizacional(), assistenteMovimentacao);
    }

    @URLAction(mappingId = "novaEfetivacaoCessao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        try {
            super.novo();
            loteSelecionado = new LoteCessao();
            selecionado.setDataEfetivacao(getDataOperacao());
            selecionado.setResponsavel(facade.getSistemaFacade().getUsuarioCorrente());
            lotesSelecionados = new ArrayList<>();
            lotesEncontrados = new ArrayList<>();
            iniciarAssistenteMovimentacao();
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    @URLAction(mappingId = "verEfetivacaoCessao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        iniciarlizarAtributosOperacaoVerEdita();
    }

    private void iniciarlizarAtributosOperacaoVerEdita() {
        lotesEncontrados = facade.recuperarLotesEfetivados(selecionado);
        loteSelecionado = new LoteCessao();
    }

    @URLAction(mappingId = "editarEfetivacaoCessao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/efetivacao-cessao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public LoteCessao getLoteSelecionado() {
        return loteSelecionado;
    }

    public void setLoteSelecionado(LoteCessao loteSelecionado) {
        this.loteSelecionado = loteSelecionado;
    }

    public void pesquisar() {
        try {
            if (verificarBensBloqueadoSingletonAoPesquisar()) return;
            validarPesquisarBens();
            iniciarAssistenteMovimentacao();
            validarDataLancamentoAndDataOperacaoBem();
            lotesEncontrados = facade.getLoteCessaoFacade().getLotesNaoAvaliadosPorUnidadeAndResponsavel(hierarquiaOrganizacional.getSubordinada(), selecionado.getTipoCessao());
            validarMovimentacaoBensComDataRetroativa();
            if (lotesEncontrados.isEmpty()) {
                FacesUtil.addAtencao("Nenhuma cessão encontrada para a unidade: " + hierarquiaOrganizacional + ".");
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    private boolean verificarBensBloqueadoSingletonAoPesquisar() {
        if (isOperacaoNovo() && assistenteMovimentacao.getBensBloqueados() != null && !assistenteMovimentacao.getBensBloqueados().isEmpty()) {
            for (Bem bensSelecionado : assistenteMovimentacao.getBensBloqueados()) {
                if (facade.getSingletonBloqueioPatrimonio().verificarBloqueioBem(bensSelecionado, assistenteMovimentacao)) {
                    FacesUtil.addOperacaoNaoPermitida(assistenteMovimentacao.getMensagemBensBloqueadoSingleton());
                    return true;
                }
            }
        }
        return false;
    }

    private void iniciarAssistenteMovimentacao() {
        assistenteMovimentacao = new AssistenteMovimentacaoBens(selecionado.getDataEfetivacao(), operacao);
        recuperarConfiguracaoMovimentacaoBem();
        assistenteMovimentacao.setConfigMovimentacaoBem(configMovimentacaoBem);
    }

    private void validarMovimentacaoBensComDataRetroativa() {
        List<Bem> bensPesquisados = Lists.newArrayList();
        for (LoteCessao lote : lotesEncontrados) {
            LoteCessao loteRecuperado = facade.getLoteCessaoFacade().recuperar(lote.getId());
            for (Cessao cessao : loteRecuperado.getListaDeCessoes()) {
                bensPesquisados.add(cessao.getBem());
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

    private void validarPesquisarBens() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getTipoCessao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Cessão deve ser informado.");
        }
        if (hierarquiaOrganizacional == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Unidade Admnistrativa " + getDescricaoComplementarUnidade() + "  deve ser informado.");
        } else {
            if (!facade.getBemFacade().usuarioIsGestorUnidade(
                facade.getSistemaFacade().getUsuarioCorrente(),
                hierarquiaOrganizacional.getSubordinada(),
                getDataOperacao())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Você " + facade.getSistemaFacade().getUsuarioCorrente().getNome() + " não é gestor pela unidade organizacional " + hierarquiaOrganizacional + " e portanto não tem autorizacão para efetivação da transferência.");
            }
        }
        ve.lancarException();
    }

    public List<SelectItem> getListSelectItemSituacao() {
        return Util.getListSelectItem(Arrays.asList(SituacaoLoteCessao.ACEITA, SituacaoLoteCessao.AGUARDANDO_EFETIVACAO, SituacaoLoteCessao.RECUSADA));
    }

    public List<LoteCessao> getLotesSelecionados() {
        return lotesSelecionados;
    }

    public void setLotesSelecionados(List<LoteCessao> lotesSelecionados) {
        this.lotesSelecionados = lotesSelecionados;
    }

    public List<LoteCessao> getLotesEncontrados() {
        return lotesEncontrados;
    }

    public void setLotesEncontrados(List<LoteCessao> lotesEncontrados) {
        this.lotesEncontrados = lotesEncontrados;
    }

    public void atribuirLoteSelecionado(LoteCessao lote) {
        this.loteSelecionado = facade.getLoteCessaoFacade().recuperar(lote.getId());
        FacesUtil.executaJavaScript("dlgInfoCessao.show()");
    }

    public void atribuirLoteSelecionadoMotivo(LoteCessao lote) {
        try {
            if (lote.getListaDeCessoes() == null || lote.getListaDeCessoes().isEmpty()) {
                lote.setListaDeCessoes(facade.getLoteCessaoFacade().getListaDeCessaoPorLote(lote));
            }
        } catch (LazyInitializationException lazy) {
            lote.setListaDeCessoes(facade.getLoteCessaoFacade().getListaDeCessaoPorLote(lote));
        }
        this.loteSelecionado = lote;
    }

    public void imprimirTermoDeCessao(LoteCessao loteCessao) {
        try {
            String nomeRelatorio = "TERMO DE CESSÃO DE USO";
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("USUARIO", facade.getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("NOMERELATORIO", nomeRelatorio);
            dto.adicionarParametro("MODULO", "PATRIMÔNIO");
            dto.adicionarParametro("idLoteCessao", loteCessao.getId());
            dto.setNomeRelatorio(nomeRelatorio);
            dto.setApi("administrativo/termo-de-cessao/");
            ReportService.getInstance().gerarRelatorio(facade.getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            logger.error("Erro ao gerar relatório: ", ex);
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public void limparCampos() {
        lotesEncontrados.clear();
    }

    public String getDescricaoComplementarUnidade() {
        return selecionado.isInterno() ? "Destino" : "Origem";
    }

    public AssistenteMovimentacaoBens getAssistenteMovimentacao() {
        return assistenteMovimentacao;
    }

    public void setAssistenteMovimentacao(AssistenteMovimentacaoBens assistenteMovimentacao) {
        this.assistenteMovimentacao = assistenteMovimentacao;
    }
}


