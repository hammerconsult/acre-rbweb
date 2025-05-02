/*
 * Codigo gerado automaticamente em Fri Feb 24 09:22:07 BRST 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.RequisicaoMaterialFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.EntidadeMetaData;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.context.RequestContext;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static br.com.webpublico.util.Util.adicionarObjetoEmLista;
import static br.com.webpublico.util.Util.clonarObjeto;

@ManagedBean(name = "requisicaoMaterialControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaRequisicaoMaterial", pattern = "/requisicao-de-material/novo/", viewId = "/faces/administrativo/materiais/requisicaomaterial/edita.xhtml"),
    @URLMapping(id = "editarRequisicaoMaterial", pattern = "/requisicao-de-material/editar/#{requisicaoMaterialControlador.id}/", viewId = "/faces/administrativo/materiais/requisicaomaterial/edita.xhtml"),
    @URLMapping(id = "listarRequisicaoMaterial", pattern = "/requisicao-de-material/listar/", viewId = "/faces/administrativo/materiais/requisicaomaterial/lista.xhtml"),
    @URLMapping(id = "verRequisicaoMaterial", pattern = "/requisicao-de-material/ver/#{requisicaoMaterialControlador.id}/", viewId = "/faces/administrativo/materiais/requisicaomaterial/visualizar.xhtml")
})
public class RequisicaoMaterialControlador extends PrettyControlador<RequisicaoMaterial> implements Serializable, CRUD {

    @EJB
    private RequisicaoMaterialFacade facade;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private ItemRequisicaoMaterial itemRequisicaoMaterialSelecionado;

    public RequisicaoMaterialControlador() {
        super(RequisicaoMaterial.class);
    }

    @Override
    public void salvar() {
        try {
            validarRegrasNegocio();
            selecionado = facade.salvarRetornando(selecionado);
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            redireciona();
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    @Override
    public void redireciona() {
        if (isOperacaoNovo() && selecionado.getId() != null) {
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId());
            return;
        }
        super.redireciona();
    }

    @Override
    public void excluir() {
        if (podeExcluirRequisicao()) {
            super.excluir();
        }
    }

    @Override
    public String getCaminhoPadrao() {
        return "/requisicao-de-material/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    @URLAction(mappingId = "novaRequisicaoMaterial", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        criarParametrosIniciaisGenericos();
    }

    @Override
    @URLAction(mappingId = "editarRequisicaoMaterial", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        rotinaParaEditar();
        if (selecionado.getIntegrada()) {
            FacesUtil.addOperacaoNaoPermitida("Não é permitido editar uma requisição integrada com as guias de distribuição.");
            redirecionarParaVerOrEditar(selecionado.getId(), "ver");
        }
        carregarListasDependentesSelecionado();
        setHierarquiaOrganizacional(getHierarquiaDaUnidade());
        Collections.sort(selecionado.getItensRequisitados());

    }

    private void rotinaParaEditar() {
        operacao = Operacoes.EDITAR;
        novoMetaDataRequisicaoMaterial();
        selecionado = (RequisicaoMaterial) Web.pegaDaSessao(metadata.getEntidade());
        if (selecionado == null) {
            selecionado = (RequisicaoMaterial) getFacede().recuperar(getId());
        }
    }

    @Override
    @URLAction(mappingId = "verRequisicaoMaterial", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    private HierarquiaOrganizacional getHierarquiaDaUnidade() {
        return facade.getHierarquiaOrganizacionalFacade().getHierarquiaDaUnidade(
            TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(),
            selecionado.getUnidadeRequerente(),
            facade.getSistemaFacade().getDataOperacao());
    }

    public void adicionarUnidade() {
        if (hierarquiaOrganizacional != null) {
            selecionado.setUnidadeRequerente(hierarquiaOrganizacional.getSubordinada());
        }
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    public ItemRequisicaoMaterial getItemRequisicaoMaterialSelecionado() {
        return itemRequisicaoMaterialSelecionado;
    }

    public void setItemRequisicaoMaterialSelecionado(ItemRequisicaoMaterial itemRequisicaoMaterialSelecionado) {
        this.itemRequisicaoMaterialSelecionado = itemRequisicaoMaterialSelecionado;
    }

    public void cancelarMaterial() {
        itemRequisicaoMaterialSelecionado = null;
        FacesUtil.executaJavaScript("setaFoco('Formulario:btnNovoMaterial')");
    }

    public void novoItemRequisicaoMaterial() {
        try {

            validarNovoItemRequisicao();
            itemRequisicaoMaterialSelecionado = new ItemRequisicaoMaterial();
            itemRequisicaoMaterialSelecionado.setQuantidadeAutorizada(BigDecimal.ZERO);
            itemRequisicaoMaterialSelecionado.setQuantidadeAtendida(BigDecimal.ZERO);
            RequestContext.getCurrentInstance().execute("setaFoco('Formulario:material_input')");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private void validarNovoItemRequisicao() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getTipoRequisicao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Requisição deve ser informado.");
        }
        if (selecionado.getUnidadeRequerente() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Unidade Administrativa Requerente deve ser informado.");
        }
        ve.lancarException();
    }

    private void criarParametrosIniciaisGenericos() {
        selecionado.setRequerenteEAutorizador(facade.getSistemaFacade().getUsuarioCorrente());
        selecionado.setDataRequisicao(facade.getSistemaFacade().getDataOperacao());
        hierarquiaOrganizacional = new HierarquiaOrganizacional();
        cancelarMaterial();
    }

    private void novoMetaDataRequisicaoMaterial() {
        metadata = new EntidadeMetaData(RequisicaoMaterial.class);
    }

    public void alterarItemRequisicao(ItemRequisicaoMaterial itemRequisicaoMaterial) {
        itemRequisicaoMaterialSelecionado = (ItemRequisicaoMaterial) clonarObjeto(itemRequisicaoMaterial);
    }

    public void removerItemRequisicao(ItemRequisicaoMaterial itemRequisicaoMaterial) {
        selecionado.getItensRequisitados().remove(itemRequisicaoMaterial);
    }

    public void listenerMaterial() {
        FacesUtil.executaJavaScript("setaFoco('Formulario:quantidade')");
    }

    public void adicionarItemRequisicaoMaterial() {
        try {
            validarCamposObrigatoriosItemRequisicaoMaterial();
            itemRequisicaoMaterialSelecionado.setRequisicaoMaterial(selecionado);
            selecionado.setItensRequisitados(adicionarObjetoEmLista(selecionado.getItensRequisitados(), this.itemRequisicaoMaterialSelecionado));
            cancelarMaterial();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private void carregarListasDependentesSelecionado() {
        carregarListaItensRequisitadosSelecionado();
    }

    private void carregarListaItensRequisitadosSelecionado() {
        selecionado.setItensRequisitados(facade.getRequisicaoMaterialFacade().buscarItensRequisicao(selecionado));
    }


    private void validarCamposObrigatoriosItemRequisicaoMaterial() {
        ValidacaoException ve = new ValidacaoException();
        if (itemRequisicaoMaterialSelecionado.getMaterialRequisitado() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo material deve ser informado.");
        }
        if (itemRequisicaoMaterialSelecionado.getQuantidade() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo quantidade deve ser informado.");
        } else {
            if (itemRequisicaoMaterialSelecionado.getQuantidade().compareTo(BigDecimal.ZERO) <= 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O campo quantidade deve ser maior que zero(0).");
            }
        }
        if (itemRequisicaoMaterialSelecionadoJaAdicionado()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O material selecionado já foi adicionado à requisação.");
        }
        ve.lancarException();
    }

    private void validarRegrasNegocio() {
        ValidacaoException ve = new ValidacaoException();
        selecionado.realizarValidacoes();
        if (selecionado.getItensRequisitados() == null || selecionado.getItensRequisitados().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Por favor, informe os materiais para esta requisição.");
        }
        ve.lancarException();
        validarRegrasTransferencia(ve);
        validaVinculoDoMaterialComLocalEstoque(ve);

        if (selecionado.isRequisicaoConsumo() && !selecionado.getLocalEstoqueOrigem().isDoTipo(TipoEstoque.MATERIAL_CONSUMO_PRINCIPAL_ALMOXARIFADO)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O local de estoque " + selecionado.getLocalEstoqueOrigem().getDescricao() + " não é do tipo almoxarifado.");
        }
        ve.lancarException();
    }

    private Boolean itemRequisicaoMaterialSelecionadoJaAdicionado() {
        try {
            for (ItemRequisicaoMaterial itemRequisicaoMaterial : selecionado.getItensRequisitados()) {
                if (itemRequisicaoMaterialSelecionado.getMaterialRequisitado().equals(itemRequisicaoMaterial.getMaterialRequisitado()) && !itemRequisicaoMaterialSelecionado.equals(itemRequisicaoMaterial)) {
                    return true;
                }
            }
        } catch (Exception e) {
        }
        return false;
    }

    private void validarRegrasTransferencia(ValidacaoException ve) {
        if (selecionado.isRequisicaoTransferencia()) {
            if (selecionado.getLocalEstoqueDestino() ==null){
                ve.adicionarMensagemDeCampoObrigatorio("O campo local de estoque deve ser informado.");
            }
            else if (selecionado.getLocalEstoqueOrigem().equals(selecionado.getLocalEstoqueDestino())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Destino e origem iguais. O local de estoque de destino não pode ser igual ao local de estoque de origem.");
            }
            else if (!facade.getLocalEstoqueFacade().isMovimentacaoPermitida(selecionado.getLocalEstoqueOrigem(), selecionado.getLocalEstoqueDestino())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Não são permitidas movimentações entre os locais de estoque de origem e destino selecionados. Verifique seus tipos.");
            }
        }
        ve.lancarException();
    }

    public Boolean estaEmEdicao() {
        return Operacoes.EDITAR.equals(operacao);
    }

    public Boolean podeExcluirRequisicao() {
        if (requisicaoJaFoiAvaliada()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Requisição já avaliada.", "Esta requisição já foi avaliada."));
            return false;
        }

        return true;
    }

    public Boolean requisicaoJaFoiAvaliada() {
        return facade.getRequisicaoMaterialFacade().requisicaoJaFoiAvaliada(selecionado);
    }

    private void validaVinculoDoMaterialComLocalEstoque(ValidacaoException ve) {
        for (ItemRequisicaoMaterial itemRequisicaoMaterial : selecionado.getItensRequisitados()) {
            if (selecionado.isRequisicaoConsumo()) {
                if (!facade.getLocalEstoqueFacade().materialPossuiVinculoComHierarquiaDoLocalEstoque(itemRequisicaoMaterial.getMaterialRequisitado(), selecionado.getLocalEstoqueOrigem())) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida(exibiMensagemDeErroVinculoDeMaterialELocalEstoque(itemRequisicaoMaterial.getMaterialRequisitado().toStringAutoComplete(), selecionado.getLocalEstoqueOrigem().getDescricao()));
                }
            }
            if (selecionado.isRequisicaoTransferencia()) {
                if (!facade.getLocalEstoqueFacade().materialPossuiVinculoComHierarquiaDoLocalEstoque(itemRequisicaoMaterial.getMaterialRequisitado(), selecionado.getLocalEstoqueDestino())) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida(exibiMensagemDeErroVinculoDeMaterialELocalEstoque(itemRequisicaoMaterial.getMaterialRequisitado().toStringAutoComplete(), selecionado.getLocalEstoqueDestino().getDescricao()));
                }
            }
        }
        ve.lancarException();
    }

    public String exibiMensagemDeErroVinculoDeMaterialELocalEstoque(String descricaoMaterial, String descricaoLocalEstoque) {
        return "Não é permitido realizar movimentações do material " + descricaoMaterial.toUpperCase() + " no local de estoque " + descricaoLocalEstoque.toUpperCase() + ".";
    }

    public List<HierarquiaOrganizacional> completarUnidadeAdiministrativa(String parte) {
        return facade.getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalAdministrativaOndeUsuarioEhGestorMaterial(
            parte.trim(), null, facade.getSistemaFacade().getDataOperacao(), facade.getSistemaFacade().getUsuarioCorrente());
    }

    public List<SelectItem> getTipoRequisicaoSelectItem() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.addAll(Util.getListSelectItem(Arrays.asList(TipoRequisicaoMaterial.CONSUMO, TipoRequisicaoMaterial.TRANSFERENCIA)));
        return retorno;
    }

    public List<RequisicaoMaterial> completaRequisicaoMaterial(String parte) {
        return facade.getRequisicaoMaterialFacade().buscarRequisicoesNaoAvaliada(parte, facade.getSistemaFacade().getUsuarioCorrente());
    }

    public List<LocalEstoque> completarLocaisEstoquePrimeiroNivelDaUnidade(String parte) {
        return facade.getLocalEstoqueFacade().completarLocaisEstoquePrimeiroNivel(parte);
    }

    public void gerarRelatorioRequisicao(RequisicaoMaterial requisicaoMaterial) {
        selecionado = requisicaoMaterial;
        gerarRelatorio();
    }

    public void gerarRelatorio() {
        try {
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("USUARIO", facade.getSistemaFacade().getUsuarioCorrente().getNome());
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("NOMERELATORIO", "Requisição de Materiais");
            dto.adicionarParametro("SECRETARIA", getHierarquiaDaUnidade().getDescricao());
            dto.adicionarParametro("MODULO", "Materiais");
            dto.adicionarParametro("SITUACAO", selecionado.getTipoSituacaoRequisicao().getDescricao());
            dto.adicionarParametro("idRequisicao", selecionado.getId());
            dto.setNomeRelatorio("REQUISIÇÃO-DE-MATERIAIS-NR-" + selecionado.getNumero());
            dto.setApi("administrativo/requisicao-de-material/");
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

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public void limparCampoLocalEstoque() {
        selecionado.setUnidadeRequerente(null);
        selecionado.setLocalEstoqueOrigem(null);
        selecionado.setLocalEstoqueDestino(null);
    }

    public List<Material> completarMateriais(String parte) {
        if (selecionado.getLocalEstoqueOrigem() != null) {
            return facade.getMaterialFacade().buscarMateriaisPorLocalDeEstoqueECodigoOuDescricao(parte.trim(), selecionado.getLocalEstoqueOrigem(), StatusMaterial.getSituacoesDeferidoInativo());
        }
        return facade.getMaterialFacade().buscarMateriaisPorCodigoOuDescricao(parte.trim(), StatusMaterial.getSituacoesDeferidoInativo());
    }
}
