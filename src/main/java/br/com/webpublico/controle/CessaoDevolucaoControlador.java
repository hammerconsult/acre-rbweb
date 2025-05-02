package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.administrativo.OperacaoMovimentacaoBem;
import br.com.webpublico.exception.MovimentacaoBemException;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.CessaoDevolucaoFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.AssistenteMovimentacaoBens;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.*;

@ManagedBean(name = "cessaoDevolucaoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoDevolucaoCessao", pattern = "/devolucao-cessao/novo/", viewId = "/faces/administrativo/patrimonio/cessaodevolucao/edita.xhtml"),
    @URLMapping(id = "editarDevolucaoCessao", pattern = "/devolucao-cessao/editar/#{cessaoDevolucaoControlador.id}/", viewId = "/faces/administrativo/patrimonio/cessaodevolucao/edita.xhtml"),
    @URLMapping(id = "listarDevolucaoCessao", pattern = "/devolucao-cessao/listar/", viewId = "/faces/administrativo/patrimonio/cessaodevolucao/lista.xhtml"),
    @URLMapping(id = "verDevolucaoCessao", pattern = "/devolucao-cessao/ver/#{cessaoDevolucaoControlador.id}/", viewId = "/faces/administrativo/patrimonio/cessaodevolucao/visualizar.xhtml")
})
public class CessaoDevolucaoControlador extends PrettyControlador<LoteCessaoDevolucao> implements Serializable, CRUD {

    @EJB
    private CessaoDevolucaoFacade facade;
    private Cessao[] cessaoSelecionados;
    private LoteCessao loteCessao;
    private List<CessaoDevolucao> cessoesDevolucaoDevolvidas;
    private List<Cessao> cessoesParaDevolver;
    private ConfigMovimentacaoBem configMovimentacaoBem;
    private AssistenteMovimentacaoBens assistenteMovimentacao;

    public CessaoDevolucaoControlador() {
        super(LoteCessaoDevolucao.class);
        cessoesDevolucaoDevolvidas = new ArrayList<>();
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @URLAction(mappingId = "novoDevolucaoCessao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        try {
            super.novo();
            selecionado.setData(getDataOperacao());
            selecionado.setUsuario(facade.getSistemaFacade().getUsuarioCorrente());
            recuperarConfiguracaoMovimentacaoBem();
            novoAssistente();
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    @URLAction(mappingId = "verDevolucaoCessao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        inicializarRegistros(selecionado.getLoteCessao());
    }

    @URLAction(mappingId = "editarDevolucaoCessao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        inicializarRegistros(selecionado.getLoteCessao());
        recuperarConfiguracaoMovimentacaoBem();
        novoAssistente();
    }

    public void inicializarRegistros(LoteCessao lote) {
        loteCessao = facade.getLoteCessaoFacade().recuperar(lote.getId());
        selecionado.setLoteCessao(loteCessao);
        cessoesDevolucaoDevolvidas = facade.buscarCessoesDevolvidas(loteCessao);
        cessoesParaDevolver = facade.getLoteCessaoFacade().buscarCessaoParaDevolucao(loteCessao);
        cessaoSelecionados = selecionado.cessaoSelecionadas();
        selecionado.atribuirConservacaoTransient(cessoesParaDevolver);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/devolucao-cessao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public void salvar() {
        try {
            validarRegras();
            validarDataLancamentoAndDataOperacaoBem();
            verificarDisponibilidadeBemParaMovimentacao();
            novoAssistente();
            bloquearMovimento();
            selecionado = facade.salvar(selecionado, cessaoSelecionados);
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            desbloquearMovimento();
            redirecionarParaVer();
        } catch (MovimentacaoBemException ex) {
            verificarMovimentoBloqueado();
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (ValidacaoException ex) {
            desbloquearMovimento();
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            desbloquearMovimento();
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        } catch (Exception e) {
            desbloquearMovimento();
            descobrirETratarException(e);
        }
    }

    private void verificarMovimentoBloqueado() {
        for (UnidadeOrganizacional unidade : assistenteMovimentacao.getUnidades()) {
            if (facade.getSingletonBloqueioPatrimonio().verificarMovimentoBloqueadoPorUnidade(CessaoDevolucao.class, unidade)) {
                redireciona();
                break;
            }
        }
    }

    private void desbloquearMovimento() {
        if (assistenteMovimentacao.getUnidades() !=null && !assistenteMovimentacao.getUnidades().isEmpty()) {
            for (UnidadeOrganizacional unidade : assistenteMovimentacao.getUnidades()) {
                facade.getSingletonBloqueioPatrimonio().desbloquearMovimentoPorUnidade(CessaoDevolucao.class, unidade);
            }
        }
    }

    private void bloquearMovimento() {
        for (UnidadeOrganizacional unidade : assistenteMovimentacao.getUnidades()) {
            facade.getSingletonBloqueioPatrimonio().bloquearMovimentoPorUnidade(CessaoDevolucao.class, unidade, assistenteMovimentacao);
        }
    }

    public void concluir() {
        try {
            validarRegras();
            recuperarConfiguracaoMovimentacaoBem();
            verificarDisponibilidadeBemParaMovimentacao();
            novoAssistente();
            bloquearMovimento();
            selecionado = facade.concluir(selecionado, cessaoSelecionados, configMovimentacaoBem);
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            desbloquearMovimento();
            redirecionarParaVer();

        } catch (MovimentacaoBemException ex) {
            FacesUtil.executaJavaScript("aguarde.hide()");
            verificarMovimentoBloqueado();
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (ValidacaoException ex) {
            FacesUtil.executaJavaScript("aguarde.hide()");
            desbloquearMovimento();
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            desbloquearMovimento();
            FacesUtil.executaJavaScript("aguarde.hide()");
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        } catch (Exception e) {
            desbloquearMovimento();
            FacesUtil.executaJavaScript("aguarde.hide()");
            descobrirETratarException(e);
        }
    }

    private void recuperarConfiguracaoMovimentacaoBem() {
        ConfigMovimentacaoBem configMovimentacaoBem = facade.getConfigMovimentacaoBemFacade().buscarConfiguracaoMovimentacaoBem(selecionado.getData(), OperacaoMovimentacaoBem.DEVOLUCAO_CESSAO_BEM);
        if (configMovimentacaoBem != null) {
            this.configMovimentacaoBem = configMovimentacaoBem;
        }
    }

    private void validarDataLancamentoAndDataOperacaoBem() {
        recuperarConfiguracaoMovimentacaoBem();
        configMovimentacaoBem.validarDatasMovimentacao(selecionado.getData(), getDataOperacao(), operacao);
    }

    private Date getDataOperacao() {
        return facade.getSistemaFacade().getDataOperacao();
    }

    private void verificarDisponibilidadeBemParaMovimentacao() {
        novoAssistente();
        List<Bem> bensSelecionados = Lists.newArrayList();
        for (Cessao cessao : cessaoSelecionados) {
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

    private void novoAssistente() {
        recuperarConfiguracaoMovimentacaoBem();
        assistenteMovimentacao = new AssistenteMovimentacaoBens(selecionado.getData(), operacao);
        assistenteMovimentacao.setConfigMovimentacaoBem(configMovimentacaoBem);
        Set<UnidadeOrganizacional> unidades = new HashSet<>();
        if (cessaoSelecionados !=null && cessaoSelecionados.length > 0)
        for (Cessao cessaoSelecionado : cessaoSelecionados) {
            unidades.add(cessaoSelecionado.getUnidadeAdministrativaInicial());
        }
        assistenteMovimentacao.getUnidades().addAll(unidades);
    }

    private void redirecionarParaVer() {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
    }

    public void validarRegras() {
        Util.validarCampos(selecionado);
        ValidacaoException ve = new ValidacaoException();
        if (cessaoSelecionados.length == 0) {
            ve.adicionarMensagemDeCampoObrigatorio("Selecione ao menos um bem para devolução.");
        } else {
            for (Cessao cessaoSelecionado : cessaoSelecionados) {
                if (cessaoSelecionado.getConservacaoBem() == null || cessaoSelecionado.getConservacaoBem().isEmpty()) {
                    ve.adicionarMensagemDeCampoObrigatorio("Informe a conservação do bem " + cessaoSelecionado.getBem() + ".");
                }
            }
        }
        if (selecionado.getLoteCessao().getUltimoPrazoCessao().getInicioDoPrazo().after(selecionado.getDataDevolucao())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A data de devolução não pode ser anterior a data de início da cessão: " + DataUtil.getDataFormatada(selecionado.getLoteCessao().getUltimoPrazoCessao().getInicioDoPrazo()) + ".");
        }
        ve.lancarException();
    }

    public List<LoteCessao> completarLoteCessao(String filtro) {
        return facade.getLoteCessaoFacade().buscarLoteCessaoQueAindaTemBemParaDevolver(filtro);
    }

    public Cessao[] getCessaoSelecionados() {
        return cessaoSelecionados;
    }

    public void setCessaoSelecionados(Cessao[] cessaoSelecionados) {
        this.cessaoSelecionados = cessaoSelecionados;
    }

    public LoteCessao getLoteCessao() {
        return loteCessao;
    }

    public void setLoteCessao(LoteCessao loteCessao) {
        if (loteCessao != null) {
            inicializarRegistros(loteCessao);
        }
    }

    public Boolean hasCessaoDevolucao(Cessao cessao) {
        return facade.getLoteCessaoFacade().hasCessaoDevolucao(cessao);
    }

    public List<CessaoDevolucao> getCessoesDevolucaoDevolvidas() {
        return cessoesDevolucaoDevolvidas;
    }

    public void setCessoesDevolucaoDevolvidas(List<CessaoDevolucao> cessoesDevolucaoDevolvidas) {
        this.cessoesDevolucaoDevolvidas = cessoesDevolucaoDevolvidas;
    }

    public List<Cessao> getCessoesParaDevolver() {
        return cessoesParaDevolver;
    }

    public void setCessoesParaDevolver(List<Cessao> cessoesParaDevolver) {
        this.cessoesParaDevolver = cessoesParaDevolver;
    }

    public void imprimirTermoDevolucaoCessao() {
        try {
            String nomeRelatorio = "TERMO DE DEVOLUÇÃO DA CESSÃO DE USO";
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("USUARIO", facade.getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("NOMERELATORIO", nomeRelatorio);
            dto.adicionarParametro("MODULO", "PATRIMÔNIO");
            dto.adicionarParametro("idLoteCessaoDevolucao", selecionado.getId());
            dto.adicionarParametro("isInterno", selecionado.getLoteCessao().isInterno());
            dto.setNomeRelatorio(nomeRelatorio);
            dto.setApi("administrativo/termo-devolucao-cessao/");
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

    public void limparDadosCessao() {
        loteCessao = null;
        cessaoSelecionados = null;
        cessoesParaDevolver = null;
        cessoesDevolucaoDevolvidas = null;
    }

    public AssistenteMovimentacaoBens getAssistenteMovimentacao() {
        return assistenteMovimentacao;
    }

    public void setAssistenteMovimentacao(AssistenteMovimentacaoBens assistenteMovimentacao) {
        this.assistenteMovimentacao = assistenteMovimentacao;
    }
}
