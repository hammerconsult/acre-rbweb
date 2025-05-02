package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.FiltroHistoricoProcessoLicitatorio;
import br.com.webpublico.entidadesauxiliares.ItemFormularioCompraVO;
import br.com.webpublico.entidadesauxiliares.LoteFormularioCompraVO;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.FormularioCotacaoFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.TabChangeEvent;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 17/04/15
 * Time: 09:14
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean(name = "formularioCotacaoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoFormularioCotacao", pattern = "/formulario-cotacao/novo/", viewId = "/faces/administrativo/licitacao/formulariocotacao/edita.xhtml"),
    @URLMapping(id = "editarFormularioCotacao", pattern = "/formulario-cotacao/editar/#{formularioCotacaoControlador.id}/", viewId = "/faces/administrativo/licitacao/formulariocotacao/edita.xhtml"),
    @URLMapping(id = "verFormularioCotacao", pattern = "/formulario-cotacao/ver/#{formularioCotacaoControlador.id}/", viewId = "/faces/administrativo/licitacao/formulariocotacao/visualizar.xhtml"),
    @URLMapping(id = "listarFormularioCotacao", pattern = "/formulario-cotacao/listar/", viewId = "/faces/administrativo/licitacao/formulariocotacao/lista.xhtml"),
    @URLMapping(id = "formularioCotacaoReposicaoEstoque", pattern = "/formulario-cotacao/reposicao-de-estoque/", viewId = "/faces/administrativo/licitacao/formulariocotacao/edita.xhtml"),

    @URLMapping(id = "duplicarFormularioCotacao", pattern = "/formulario-cotacao/duplicar/novo/", viewId = "/faces/administrativo/licitacao/formulariocotacao/edita.xhtml"),


})
public class FormularioCotacaoControlador extends PrettyControlador<FormularioCotacao> implements Serializable, CRUD {

    @EJB
    private FormularioCotacaoFacade facade;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private Boolean formularioIrp;
    private SolicitacaoReposicaoEstoque solicitacaoReposicaoEstoque;
    private FiltroHistoricoProcessoLicitatorio filtroHistoricoProcesso;
    private Boolean hasCotacao;
    private List<LoteFormularioCompraVO> lotesVO;

    public FormularioCotacaoControlador() {
        super(FormularioCotacao.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @URLAction(mappingId = "novoFormularioCotacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setExercicio(facade.getSistemaFacade().getExercicioCorrente());
        selecionado.setDataFormulario(facade.getSistemaFacade().getDataOperacao());
        selecionado.setUnidadeOrganizacional(facade.getSistemaFacade().getUnidadeOrganizacionalAdministrativaCorrente());
        selecionado.setUsuarioCriacao(facade.getSistemaFacade().getUsuarioCorrente());
        lotesVO = Lists.newArrayList();
        hasCotacao = false;
    }

    private void atribuirHierarquiaDaUnidade() {
        setHierarquiaOrganizacional(facade.getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalPorUnidade(
            selecionado.getDataFormulario(), selecionado.getUnidadeOrganizacional(), TipoHierarquiaOrganizacional.ADMINISTRATIVA));
    }

    @URLAction(mappingId = "verFormularioCotacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        operacao = Operacoes.VER;
        recuperarObjeto();
        atribuirHierarquiaDaUnidade();
        popularFormularioCompraVO();
    }

    @URLAction(mappingId = "editarFormularioCotacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        try {
            super.editar();
            lotesVO = Lists.newArrayList();
            hasCotacao = facade.hasFormularioUtilizadoEmCotacao(selecionado);
            popularFormularioCompraVO();
            formularioIrp = selecionado.getIntencaoRegistroPreco() != null;
            atribuirHierarquiaDaUnidade();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
            redirecionarParaVer();
        }
    }

    public void redirecionaDuplicar() {
        Web.poeNaSessao("formularioCotacaoDuplicar", selecionado);
        FacesUtil.redirecionamentoInterno("/formulario-cotacao/duplicar/novo/");
    }

    @URLAction(mappingId = "duplicarFormularioCotacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void duplicar() {
        operacao = Operacoes.NOVO;
        FormularioCotacao formulario = (FormularioCotacao) Web.pegaDaSessao("formularioCotacaoDuplicar");
        if (formulario == null) {
            FacesUtil.addOperacaoNaoPermitida("Erro ao recuperar o formulário de origem para duplicar o formulário de cotação.");
            FacesUtil.redirecionamentoInterno("/formulario-cotacao/ver/" + getId());
            return;
        }
        selecionado = formulario;
        if (!formulario.getFormularioIrp()) {
            selecionado.setId(null);
            selecionado.setNumero(null);
            selecionado.setDataFormulario(facade.getSistemaFacade().getDataOperacao());
            selecionado.setUsuarioCriacao(facade.getSistemaFacade().getUsuarioCorrente());
            selecionado.setExercicio(facade.getExercicioFacade().getExercicioCorrente());
            selecionado.setUnidadeOrganizacional(formulario.getUnidadeOrganizacional());
            selecionado.setTipoSolicitacao(formulario.getTipoSolicitacao());
            selecionado.setObjeto(formulario.getObjeto());
            selecionado.setTipoApuracaoLicitacao(formulario.getTipoApuracaoLicitacao());
            selecionado.setObservacao(formulario.getObservacao());
            selecionado.setLotesFormularioCotacao(formulario.getLotesFormulario());
            formularioIrp = false;
            atribuirHierarquiaDaUnidade();
        } else {
            FacesUtil.addOperacaoNaoPermitida("Não é permitido duplicar um formuário de cotação originado de um irp, pois um irp só pode estar em um formulário.");
            FacesUtil.redirecionamentoInterno("/formulario-cotacao/ver/" + selecionado.getId());
        }
    }

    @URLAction(mappingId = "formularioCotacaoReposicaoEstoque", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoFormularioReposicaoEstoque() {
        super.novo();
        this.solicitacaoReposicaoEstoque = (SolicitacaoReposicaoEstoque) Web.pegaDaSessao(SolicitacaoReposicaoEstoque.class);
        if (solicitacaoReposicaoEstoque != null) {
            selecionado.criarItemLoteFormularioCotacaoComBaseNosItensReposicao(solicitacaoReposicaoEstoque);
            selecionado.setUnidadeOrganizacional(solicitacaoReposicaoEstoque.getUnidadeOrganizacional());
            selecionado.setObjeto(solicitacaoReposicaoEstoque.toString());
            selecionado.setObservacao(solicitacaoReposicaoEstoque.toString());
            setHierarquiaOrganizacional(facade.getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalPorUnidade(solicitacaoReposicaoEstoque.getDataSolicitacao(), solicitacaoReposicaoEstoque.getUnidadeOrganizacional(), TipoHierarquiaOrganizacional.ADMINISTRATIVA));
        }
        selecionado.setExercicio(facade.getSistemaFacade().getExercicioCorrente());
        selecionado.setTipoSolicitacao(TipoSolicitacao.COMPRA_SERVICO);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/formulario-cotacao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public void salvar() {
        try {
            validarRegrasEspecificas();
            selecionado = facade.salvarFormulario(selecionado, solicitacaoReposicaoEstoque);
            redirecionarParaVer();
            FacesUtil.addOperacaoRealizada("Registro salvo com sucesso. Nº do Formulário: " + selecionado.getNumero());
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    private void popularFormularioCompraVO() {
        setLotesVO(LoteFormularioCompraVO.popularFormularioCompraVOFromFormulario(selecionado.getLotesFormulario()));
    }

    @Override
    public void excluir() {
        try {
            if (!selecionadoFoiGeradoPorSolicitacaoDeReposicao()) {
                validarFormularioUtilizadoEmCotacao("Exclusão não permitida.");
                super.excluir();
            }
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        }
    }

    private void validarRegrasEspecificas() {
        ValidacaoException ve = new ValidacaoException();
        Util.validarCamposObrigatorios(selecionado, ve);

        if (!hasLotes()) {
            ve.adicionarMensagemDeCampoObrigatorio("Nenhum lote informado para o formulário de cotação.");
        }
        ve.lancarException();

        lotesVO.stream()
            .filter(lote -> !lote.hasItens())
            .map(lote -> "O lote '" + lote.getDescricao() + "' encontra-se itens.")
            .forEach(ve::adicionarMensagemDeCampoObrigatorio);
        ve.lancarException();

        if (lotesVO.stream()
            .flatMap(lote -> lote.getItens().stream())
            .anyMatch(item -> item.getObjetoCompra().isObjetoValorReferencia())) {
            FacesUtil.addAtencao("Esse formulário de cotação contém Grupo de Objeto de Compra do tipo \"Referência\", no qual não será possível informar valor por fornecedor para ele na Cotação.");
        }
    }

    private void redirecionarParaVer() {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId());
    }

    public List<SelectItem> getTiposApuracao() {
        return Util.getListSelectItem(TipoApuracaoLicitacao.values());
    }

    public List<IntencaoRegistroPreco> completarIRP(String filtro) {
        return facade.getIntencaoRegistroPrecoFacade().buscarIRPPorSituacao(filtro.trim(), SituacaoIRP.CONCLUIDA);
    }

    public List<SelectItem> getTiposObjeto() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoSolicitacao object : TipoSolicitacao.values()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getTiposObjetoCompra() {
        if (selecionado.getTipoSolicitacao() != null && selecionado.getTipoSolicitacao().equals(TipoSolicitacao.COMPRA_SERVICO)) {
            return Util.getListSelectItem(Arrays.asList(TipoObjetoCompra.values()));
        }
        if (selecionado.getTipoSolicitacao() != null && selecionado.getTipoSolicitacao().equals(TipoSolicitacao.OBRA_SERVICO_DE_ENGENHARIA)) {
            return Util.getListSelectItem(TipoObjetoCompra.getTiposObraEServicoEngenharia());
        }
        return null;
    }

    public boolean hasLotes() {
        return !Util.isListNullOrEmpty(lotesVO);
    }

    public boolean isDesabilitaEdicaoDadosGeraisSeLoteAdicionado() {
        return (!getFormularioIrp() && hasLotes()) || hasLotes() || hasCotacao;
    }

    private boolean selecionadoFoiGeradoPorSolicitacaoDeReposicao() {
        SolicitacaoReposicaoEstoque sre = facade.recuperarSolicitacaoReposicaoVinculada(selecionado);

        if (sre != null) {
            exibirMensagemSolicitacaoMaterialVincualdaASolicitacaoReposicao(sre);
            return true;
        } else {
            return false;
        }
    }

    private void exibirMensagemSolicitacaoMaterialVincualdaASolicitacaoReposicao(SolicitacaoReposicaoEstoque sre) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
            "Impossível remover!", "O formulario de cotação " + selecionado.toString().toUpperCase()
            + " foi gerado a partir da solicitação de reposição de estoque Nro. " + sre.getNumero()
            + ", portanto pode ser excluída somente por seu local de origem."));
    }

    public List<FormularioCotacao> buscarFormularioCotacaoPorAnoOrNumeroOndeUsuarioEhGestorLicitacao(String filter) {
        return facade.buscarFormularioCotacaoPorAnoOrNumeroOndeUsuarioEhGestorLicitacao(filter, facade.getSistemaFacade().getUsuarioCorrente());
    }

    public void preencherFormularioCotacaoIRP() {
        try {
            lotesVO = Lists.newArrayList();
            selecionado.setLotesFormularioCotacao(Lists.newArrayList());
            IntencaoRegistroPreco irp = facade.getIntencaoRegistroPrecoFacade().recuperar(selecionado.getIntencaoRegistroPreco().getId());

            for (LoteIntencaoRegistroPreco loteIrp : irp.getLotes()) {
                LoteFormularioCompraVO novoLoteVO = LoteFormularioCompraVO.fromIrp(loteIrp);
                LoteFormularioCotacao loteFormulario = LoteFormularioCotacao.fromVO(novoLoteVO, selecionado);

                for (ItemIntencaoRegistroPreco itemIrp : loteIrp.getItens()) {
                    ItemFormularioCompraVO novoItemVO = ItemFormularioCompraVO.fromIRP(itemIrp, novoLoteVO);
                    Util.adicionarObjetoEmLista(novoLoteVO.getItens(), novoItemVO);

                    ItemLoteFormularioCotacao itemFormulario = ItemLoteFormularioCotacao.fromVO(novoItemVO, loteFormulario);
                    Util.adicionarObjetoEmLista(loteFormulario.getItensLoteFormularioCotacao(), itemFormulario);
                }
                Util.adicionarObjetoEmLista(selecionado.getLotesFormulario(), loteFormulario);
                Util.adicionarObjetoEmLista(lotesVO, novoLoteVO);
            }
            setHierarquiaOrganizacional(irp.getUnidadeGerenciadora());
            selecionado.setObjeto(irp.getDescricao());
            selecionado.setTipoSolicitacao(irp.getTipoObjeto());
            selecionado.setTipoApuracaoLicitacao(irp.getTipoApuracaoLicitacao());
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        }
    }

    public void limparDadosIRP() {
        selecionado.setIntencaoRegistroPreco(null);
        selecionado.setLotesFormularioCotacao(Lists.<LoteFormularioCotacao>newArrayList());
        selecionado.setObjeto(null);
    }

    public void limparDadosGeradosIrp() {
        if (!getFormularioIrp()) {
            novo();
        }
        selecionado.setIntencaoRegistroPreco(null);
        FacesUtil.atualizarComponente("Formulario");
    }

    public void gerarRelatorio(FormularioCotacao formulario) {
        if (formulario != null) {
            selecionado = formulario;
            gerarRelatorio("PDF");
        }
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USUARIO", facade.getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("dataOperacao", DataUtil.getDataFormatada(facade.getSistemaFacade().getDataOperacao()));
            dto.adicionarParametro("idFormularioCotacao", selecionado.getId());
            dto.setNomeRelatorio("Formulario de Cotação de Preço");
            dto.setApi("administrativo/formulario-cotacao/");
            ReportService.getInstance().gerarRelatorio(facade.getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
        }
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        if (hierarquiaOrganizacional != null) {
            selecionado.setUnidadeOrganizacional(hierarquiaOrganizacional.getSubordinada());
        }
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public Boolean getFormularioIrp() {
        return formularioIrp != null ? formularioIrp : false;
    }

    public void setFormularioIrp(Boolean formularioIrp) {
        this.formularioIrp = formularioIrp;
    }

    private void validarFormularioUtilizadoEmCotacao(String sumaryMsg) {
        ValidacaoException ve = new ValidacaoException();
        if (facade.hasFormularioUtilizadoEmCotacao(selecionado)) {
            ve.adicionarMensagemWarn(sumaryMsg, "O Formulário de Cotação/Planilha Orçamentária já está sendo utilizado em uma Cotação.");
        }
        ve.lancarException();
    }

    private void novoFiltroHistoricoProcesso() {
        filtroHistoricoProcesso = new FiltroHistoricoProcessoLicitatorio(selecionado.getId(), TipoMovimentoProcessoLicitatorio.FORMULARIO_COTACAO);
    }

    public void onTabChange(TabChangeEvent event) {
        String tab = event.getTab().getId();
        if ("tab-historico".equals(tab)) {
            novoFiltroHistoricoProcesso();
        }
    }

    public FiltroHistoricoProcessoLicitatorio getFiltroHistoricoProcesso() {
        return filtroHistoricoProcesso;
    }

    public void setFiltroHistoricoProcesso(FiltroHistoricoProcessoLicitatorio filtroHistoricoProcesso) {
        this.filtroHistoricoProcesso = filtroHistoricoProcesso;
    }

    public Boolean getHasCotacao() {
        return hasCotacao;
    }

    public void setHasCotacao(Boolean hasCotacao) {
        this.hasCotacao = hasCotacao;
    }

    public List<LoteFormularioCompraVO> getLotesVO() {
        return lotesVO;
    }

    public void setLotesVO(List<LoteFormularioCompraVO> lotesVO) {
        this.lotesVO = lotesVO;
    }
}
