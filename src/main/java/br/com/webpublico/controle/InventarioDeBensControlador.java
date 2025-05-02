package br.com.webpublico.controle;

import br.com.webpublico.entidades.ConfigMovimentacaoBem;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.InventarioBens;
import br.com.webpublico.entidades.ItemInventarioBens;
import br.com.webpublico.entidadesauxiliares.BemVo;
import br.com.webpublico.entidadesauxiliares.FiltroPesquisaBem;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.TipoBem;
import br.com.webpublico.enums.TipoEventoBem;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.enums.administrativo.OperacaoMovimentacaoBem;
import br.com.webpublico.exception.MovimentacaoBemException;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.InventarioBensMoveisFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.AssistenteMovimentacaoBens;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoInventarioMovel", pattern = "/inventario-bens-moveis/novo/", viewId = "/faces/administrativo/patrimonio/inventario/inventario-de-bens-moveis/edita.xhtml"),
    @URLMapping(id = "editarInventarioMovel", pattern = "/inventario-bens-moveis/editar/#{inventarioDeBensControlador.id}/", viewId = "/faces/administrativo/patrimonio/inventario/inventario-de-bens-moveis/edita.xhtml"),
    @URLMapping(id = "listarInventarioMovel", pattern = "/inventario-bens-moveis/listar/", viewId = "/faces/administrativo/patrimonio/inventario/inventario-de-bens-moveis/lista.xhtml"),
    @URLMapping(id = "verInventarioMovel", pattern = "/inventario-bens-moveis/ver/#{inventarioDeBensControlador.id}/", viewId = "/faces/administrativo/patrimonio/inventario/inventario-de-bens-moveis/visualizar.xhtml")
})
public class InventarioDeBensControlador extends PrettyControlador<InventarioBens> implements Serializable, CRUD {

    @EJB
    private InventarioBensMoveisFacade facade;
    private LazyDataModel<ItemInventarioBens> model;
    private List<HierarquiaOrganizacional> unidadesOrcamentarias;
    private HierarquiaOrganizacional hoAdministrativa;
    private HierarquiaOrganizacional hoOrcamentaria;
    private AssistenteMovimentacaoBens assistenteMovimentacao;

    public InventarioDeBensControlador() {
        super(InventarioBens.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }


    @URLAction(mappingId = "novoInventarioMovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        try {
            super.novo();
            this.selecionado.setProgramadoEm(facade.getSistemaFacade().getDataOperacao());
            this.selecionado.setResponsavel(facade.getSistemaFacade().getUsuarioCorrente());
            novoAssistente();
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        }
    }

    @URLAction(mappingId = "verInventarioMovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        operacao = Operacoes.VER;
        recuperarObjeto();
        atribuirHierarquiaDaUnidadeAdm();
        atribuirHierarquiaDaUnidadeOrc();
        carregarPrimeiraPagina();
    }

    @URLAction(mappingId = "editarInventarioMovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        atribuirHierarquiaDaUnidadeAdm();
        atribuirHierarquiaDaUnidadeOrc();
        carregarPrimeiraPagina();
        buscarUnidadesOrcamentarias();
        novoAssistente();
        AssistenteMovimentacaoBens assistente = (AssistenteMovimentacaoBens) Web.pegaDaSessao("ASSISTENTE");
        if (assistente != null) {
            this.assistenteMovimentacao.setMensagens(assistente.getMensagens());
        }
    }

    private void atribuirHierarquiaDaUnidadeOrc() {
        if (selecionado.getUnidadeOrcamentaria() != null) {
            setHoOrcamentaria(facade.getHierarquiaOrganizacionalFacade().getHierarquiaDaUnidade(
                TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), selecionado.getUnidadeOrcamentaria(), facade.getSistemaFacade().getDataOperacao()));
        }
    }

    private void atribuirHierarquiaDaUnidadeAdm() {
        setHoAdministrativa(facade.getHierarquiaOrganizacionalFacade().getHierarquiaDaUnidade(
            TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), selecionado.getUnidadeAdministrativa(), facade.getSistemaFacade().getDataOperacao()));
    }

    @Override
    public String getCaminhoPadrao() {
        return "/inventario-bens-moveis/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public LazyDataModel<ItemInventarioBens> getModel() {
        return model;
    }

    public void setModel(LazyDataModel<ItemInventarioBens> model) {
        this.model = model;
    }

    public void carregarPrimeiraPagina() {
        model = new LazyDataModel<ItemInventarioBens>() {
            @Override
            public List<ItemInventarioBens> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
                setRowCount(facade.countItensInventario(selecionado));
                return facade.recuperarItensInventario(selecionado, pageSize, first);
            }
        };
    }

    public List<SelectItem> getUnidadesOrcamentarias() {
        List<SelectItem> toReturn = new ArrayList<>();
        if (!Util.isListNullOrEmpty(unidadesOrcamentarias)) {
            toReturn.add(new SelectItem(null, ""));
            for (HierarquiaOrganizacional obj : unidadesOrcamentarias) {
                toReturn.add(new SelectItem(obj, obj.toString()));
            }
        }
        return toReturn;
    }

    public void buscarUnidadesOrcamentarias() {
        if (hoAdministrativa != null) {
            unidadesOrcamentarias = facade.getHierarquiaOrganizacionalFacade().retornaHierarquiaOrcamentariaPorUnidadeAdministrativa(hoAdministrativa.getSubordinada(), facade.getSistemaFacade().getDataOperacao());
        }
    }

    public void fechamento() {
        selecionado.setDataFechamento(new Date());
        FacesUtil.executaJavaScript("dlgFechamento.show()");
    }

    public void cancelarFechamento() {
        selecionado.setDataFechamento(null);
        FacesUtil.executaJavaScript("dlgFechamento.hide()");
    }

    @Override
    public void redireciona() {
        if (selecionado != null && selecionado.getId() != null && isOperacaoNovo()) {
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "editar/" + selecionado.getId() + "/");
        } else if (isOperacaoEditar()) {
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
        } else {
            super.redireciona();
        }
    }

    @Override
    public void salvar() {
        try {
            selecionado.realizarValidacoes();
            facade.hasInventarioAbertoDiferente(selecionado);
            if (isOperacaoNovo()) {
                bloquearMovimentacaoBens();
                FiltroPesquisaBem filtro = novoFiltroPesquisaBem();
                assistenteMovimentacao.setFutureSalvar(facade.aberturaInventario(assistenteMovimentacao, filtro));
                FacesUtil.executaJavaScript("iniciaFuture()");
            } else {
                assistenteMovimentacao.setFutureSalvar(facade.fechamentoInventario(assistenteMovimentacao));
                FacesUtil.executaJavaScript("dlgFechamento.hide()");
                FacesUtil.executaJavaScript("iniciaFuture()");
            }
        } catch (MovimentacaoBemException ex) {
            blocoCatchMovimentacaoBemException(ex);
        } catch (ValidacaoException ex) {
            blocoCatchValidacaoException(ex);
        } catch (ExcecaoNegocioGenerica ex) {
            blocoCatchExcecaoNegocioGenerica(ex);
        } catch (Exception e) {
            blocoCatchException(e);
            assistenteMovimentacao.descobrirETratarException(e);
        }
    }

    @Override
    public void excluir() {
        try {
            novoAssistente();
            facade.remover(selecionado, assistenteMovimentacao);
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoExcluir());
            redireciona();
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }

    private FiltroPesquisaBem novoFiltroPesquisaBem() {
        FiltroPesquisaBem filtro = new FiltroPesquisaBem();
        filtro.setHierarquiaAdministrativa(hoAdministrativa);
        filtro.setHierarquiaOrcamentaria(hoOrcamentaria);
        filtro.setTipoBem(TipoBem.MOVEIS);
        return filtro;
    }

    private void novoAssistente() {
        assistenteMovimentacao = new AssistenteMovimentacaoBens(selecionado.getProgramadoEm(), operacao);
        assistenteMovimentacao.setSelecionado(selecionado);
        assistenteMovimentacao.setTipoEventoBem(TipoEventoBem.ITEMINVENTARIOBENS);
        recuperarConfiguracaoMovimentacaoBem();
    }

    public String getDescricaoProcesso(){
        return selecionado.getDataFechamento() == null
            ? "Salvando Abertura do Inventário"
            : "Salvando Fechamento do Inventário";
    }

    private void recuperarConfiguracaoMovimentacaoBem() {
        ConfigMovimentacaoBem configMovimentacaoBem = facade.getConfigMovimentacaoBemFacade().buscarConfiguracaoMovimentacaoBem(selecionado.getProgramadoEm(), OperacaoMovimentacaoBem.ABERTURA_INVENTARIO);
        if (configMovimentacaoBem != null) {
            assistenteMovimentacao.setConfigMovimentacaoBem(configMovimentacaoBem);
        }
    }

    private void desbloquearMovimentacaoSingleton() {
        facade.getSingletonConcorrenciaPatrimonio().desbloquearMovimentacaoSingleton(assistenteMovimentacao, InventarioBens.class);
    }

    private void blocoCatchException(Exception e) {
        desbloquearMovimentacaoSingleton();
        FacesUtil.executaJavaScript("aguarde.hide()");
    }

    private void blocoCatchValidacaoException(ValidacaoException ex) {
        desbloquearMovimentacaoSingleton();
        FacesUtil.executaJavaScript("aguarde.hide()");
        FacesUtil.printAllFacesMessages(ex.getMensagens());
    }

    private void blocoCatchMovimentacaoBemException(MovimentacaoBemException ex) {
        if (!isOperacaoNovo()) {
            redireciona();
        }
        FacesUtil.printAllFacesMessages(ex.getMensagens());
    }

    private void blocoCatchExcecaoNegocioGenerica(ExcecaoNegocioGenerica ex) {
        desbloquearMovimentacaoSingleton();
        FacesUtil.executaJavaScript("aguarde.hide()");
        FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
    }

    private void bloquearMovimentacaoBens() {
        if (!isOperacaoNovo()) {
            facade.getSingletonConcorrenciaPatrimonio().bloquearMovimentoPorUnidade(InventarioBens.class, hoAdministrativa.getSubordinada(), assistenteMovimentacao);
        }
        if (facade.getSingletonConcorrenciaPatrimonio().verificarBensBloqueadoSingletonAoPesquisar(assistenteMovimentacao)) {
            FacesUtil.executaJavaScript("aguarde.hide()");
            FacesUtil.atualizarComponente("Formulario");
            assistenteMovimentacao.lancarMensagemBensBloqueioSingleton();
        }
        if (isOperacaoNovo() && !assistenteMovimentacao.hasInconsistencias()) {
            facade.getConfigMovimentacaoBemFacade().verificarBensSelecionadosSeDisponivelConfiguracaoBloqueio(assistenteMovimentacao);
            FacesUtil.executaJavaScript("aguarde.hide()");
            FacesUtil.atualizarComponente("Formulario");
            assistenteMovimentacao.lancarMensagemBensBloqueioSingleton();
        }
        if (!isOperacaoVer()) {
            for (BemVo bemVo : assistenteMovimentacao.getBensSelecionadosVo()) {
                facade.getSingletonConcorrenciaPatrimonio().bloquearBem(bemVo.getBem(), assistenteMovimentacao);
            }
        }
    }

    public void finalizarFutureSalvar() {
        assistenteMovimentacao.setFutureSalvar(null);
        redireciona();
        FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
    }

    public void consultarFutureSalvar() {
        if (assistenteMovimentacao.getFutureSalvar() != null && assistenteMovimentacao.getFutureSalvar().isDone()) {
            try {
                AssistenteMovimentacaoBens assistente = assistenteMovimentacao.getFutureSalvar().get();
                if (assistente.getSelecionado() != null) {
                    selecionado = (InventarioBens) assistente.getSelecionado();
                    desbloquearMovimentacaoSingleton();
                    carregarPrimeiraPagina();
                    Web.poeNaSessao("ASSISTENTE", assistenteMovimentacao);
                    FacesUtil.executaJavaScript("finalizaFuture()");
                }
            } catch (Exception ex) {
                assistenteMovimentacao.descobrirETratarException(ex);
                assistenteMovimentacao.setBloquearAcoesTela(true);
                FacesUtil.executaJavaScript("clearInterval(timerSalvar)");
                FacesUtil.executaJavaScript("closeDialog(dlgProcesso)");
                assistenteMovimentacao.setFutureSalvar(null);
                desbloquearMovimentacaoSingleton();
            }
        }
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            String nomeRelatorio = "RELATÓRIO DE BENS MÓVEIS INVENTARIADOS";
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USUARIO", facade.getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("MODULO", "PATRIMÔNIO");
            dto.adicionarParametro("NOMERELATORIO", nomeRelatorio);
            dto.adicionarParametro("ID_INVENTARIO", selecionado.getId());
            dto.adicionarParametro("DATA_OPERACAO", DataUtil.getDataFormatada(facade.getSistemaFacade().getDataOperacao()));
            dto.setNomeRelatorio(nomeRelatorio);
            dto.setApi("administrativo/inventario-bens/");
            ReportService.getInstance().gerarRelatorio(facade.getSistemaFacade().getUsuarioCorrente(), dto);
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            FacesUtil.addErroAoGerarRelatorio("Ocorreu um erro ao gerar o relatório: " + ex.getMessage());
        }
    }

    public void mudarItemEncontraro(ItemInventarioBens item) {
        item.setNaoEncontrado(!item.getNaoEncontrado());
        facade.salvarItem(item);
    }

    public void mudarItemTransferir(ItemInventarioBens item) {
        item.setTransferir(!item.getTransferir());
        facade.salvarItem(item);
    }

    @Override
    public void cancelar() {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "listar/");
    }

    public AssistenteMovimentacaoBens getAssistenteMovimentacao() {
        return assistenteMovimentacao;
    }

    public void setAssistenteMovimentacao(AssistenteMovimentacaoBens assistenteMovimentacao) {
        this.assistenteMovimentacao = assistenteMovimentacao;
    }

    public HierarquiaOrganizacional getHoAdministrativa() {
        return hoAdministrativa;
    }

    public void setHoAdministrativa(HierarquiaOrganizacional hoAdministrativa) {
        if (hoAdministrativa != null) {
            selecionado.setUnidadeAdministrativa(hoAdministrativa.getSubordinada());
        }
        this.hoAdministrativa = hoAdministrativa;
    }

    public HierarquiaOrganizacional getHoOrcamentaria() {
        return hoOrcamentaria;
    }

    public void setHoOrcamentaria(HierarquiaOrganizacional hoOrcamentaria) {
        if (hoOrcamentaria != null) {
            selecionado.setUnidadeOrcamentaria(hoOrcamentaria.getSubordinada());
        }
        this.hoOrcamentaria = hoOrcamentaria;
    }
}
