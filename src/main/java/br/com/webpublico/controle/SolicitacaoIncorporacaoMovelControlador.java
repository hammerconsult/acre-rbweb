package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.AssistenteDetentorArquivoComposicao;
import br.com.webpublico.enums.*;
import br.com.webpublico.enums.administrativo.SituacaoAprovacao;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.SolicitacaoIncorporacaoMovelFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Desenvolvimento on 29/01/2016.
 */

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaSolicitacaoIncorporacaoMovel", pattern = "/solicitacao-incorporacao-movel/novo/", viewId = "/faces/administrativo/patrimonio/incorporacaobem/movel/solicitacao/edita.xhtml"),
    @URLMapping(id = "editarSolicitacaoIncorporacaoMovel", pattern = "/solicitacao-incorporacao-movel/editar/#{solicitacaoIncorporacaoMovelControlador.id}/", viewId = "/faces/administrativo/patrimonio/incorporacaobem/movel/solicitacao/edita.xhtml"),
    @URLMapping(id = "listarSolicitacaoIncorporacaoMovel", pattern = "/solicitacao-incorporacao-movel/listar/", viewId = "/faces/administrativo/patrimonio/incorporacaobem/movel/solicitacao/lista.xhtml"),
    @URLMapping(id = "verSolicitacaoIncorporacaoMovel", pattern = "/solicitacao-incorporacao-movel/ver/#{solicitacaoIncorporacaoMovelControlador.id}/", viewId = "/faces/administrativo/patrimonio/incorporacaobem/movel/solicitacao/visualizar.xhtml")
})
public class SolicitacaoIncorporacaoMovelControlador extends PrettyControlador<SolicitacaoIncorporacaoMovel> implements Serializable, CRUD {

    @EJB
    private SolicitacaoIncorporacaoMovelFacade facade;
    private HierarquiaOrganizacional hierarquiaAdministrativa;
    private HierarquiaOrganizacional hierarquiaOrcamentaria;
    private OrigemRecursoBem origemRecursoBemSelecionada;
    private AssistenteDetentorArquivoComposicao assistenteDetentorArquivoComposicao;
    private DocumetoComprobatorioIncorporacaoMovel novoDocumetoComprobatorio;
    private DocumetoComprobatorioIncorporacaoMovel documetoComprobatorioSelecionado;
    private HashMap<DocumetoComprobatorioIncorporacaoMovel, AssistenteDetentorArquivoComposicao> mapAssintente;
    private ItemSolicitacaoIncorporacaoMovel itemSolicitacaoIncorporacaoMovelSelecionado;
    private Boolean segurado = Boolean.FALSE;
    private Boolean garantido = Boolean.FALSE;
    private AprovacaoSolicitacaoIncorporacaoMovel aprovacao;


    public SolicitacaoIncorporacaoMovelControlador() {
        super(SolicitacaoIncorporacaoMovel.class);
    }

    public AprovacaoSolicitacaoIncorporacaoMovel getAprovacao() {
        return aprovacao;
    }

    public void setAprovacao(AprovacaoSolicitacaoIncorporacaoMovel aprovacao) {
        this.aprovacao = aprovacao;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/solicitacao-incorporacao-movel/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @URLAction(mappingId = "novaSolicitacaoIncorporacaoMovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        inicializarNovo();
    }

    @URLAction(mappingId = "verSolicitacaoIncorporacaoMovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        if (verificarSeSolicitacaoFoiRecusada()) {
            buscarMotivoRecusaSolicitacao();
        }
    }

    @URLAction(mappingId = "editarSolicitacaoIncorporacaoMovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        inicializarEdita();
        if (verificarSeSolicitacaoFoiRecusada()) {
            buscarMotivoRecusaSolicitacao();
        }
    }

    private boolean verificarSeSolicitacaoFoiRecusada() {
        return SituacaoEventoBem.RECUSADO.equals(selecionado.getSituacao());
    }

    private void buscarMotivoRecusaSolicitacao() {
        aprovacao = facade.buscarAprovavaoSolicitacaoDaSolicitacaoDeIncorporacao(selecionado);
    }

    @Override
    public void salvar() {
        try {
            selecionado.realizarValidacoes();
            selecionado.validarNegocio(facade.getSistemaFacade().getDataOperacao());
            selecionado = facade.salvarRetornando(selecionado);
            redirecionarParaVer();
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    private void recuperarDocumentosComprobatorios() {
        novoDocumetoComprobatorio = new DocumetoComprobatorioIncorporacaoMovel();
        documetoComprobatorioSelecionado = new DocumetoComprobatorioIncorporacaoMovel();
        mapAssintente = new HashMap<>();
        for (DocumetoComprobatorioIncorporacaoMovel doc : selecionado.getDocumetosComprobatorio()) {
            mapAssintente.put(doc, new AssistenteDetentorArquivoComposicao(doc, facade.getSistemaFacade().getDataOperacao()));
        }
    }

    private void inicializarNovo() {
        selecionado.setDataSolicitacao(facade.getSistemaFacade().getDataOperacao());
        selecionado.setResponsavel(facade.getSistemaFacade().getUsuarioCorrente());
        origemRecursoBemSelecionada = new OrigemRecursoBem();
        novoDocumetoComprobatorio = new DocumetoComprobatorioIncorporacaoMovel(selecionado);
        documetoComprobatorioSelecionado = new DocumetoComprobatorioIncorporacaoMovel(selecionado);
        mapAssintente = new HashMap<>();
    }

    private void inicializarEdita() {
        hierarquiaAdministrativa = facade.getHierarquiaOrganizacionalFacade().getHierarquiaDaUnidade(TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), this.selecionado.getUnidadeAdministrativa(), facade.getSistemaFacade().getDataOperacao());
        hierarquiaOrcamentaria = facade.getHierarquiaOrganizacionalFacade().getHierarquiaDaUnidade(TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), this.selecionado.getUnidadeOrcamentaria(), facade.getSistemaFacade().getDataOperacao());
        origemRecursoBemSelecionada = new OrigemRecursoBem();
        novoDocumetoComprobatorio = new DocumetoComprobatorioIncorporacaoMovel(selecionado);
        documetoComprobatorioSelecionado = new DocumetoComprobatorioIncorporacaoMovel(selecionado);
        mapAssintente = new HashMap<>();
    }

    public boolean verificaObrigatoriedadeObservacao() {
        return TipoAquisicaoBem.DOACAO.equals(selecionado.getTipoAquisicaoBem());
    }

    public void recuperarAssociacaoComGrupoBem() {
        try {
            GrupoObjetoCompraGrupoBem grupoObjetoCompraGrupoBem;
            if (itemSolicitacaoIncorporacaoMovelSelecionado.getItem() != null) {
                grupoObjetoCompraGrupoBem = facade.getGrupoObjetoCompraGrupoBemFacade().recuperarAssociacaoDoGrupoObjetoCompra(itemSolicitacaoIncorporacaoMovelSelecionado.getItem().getGrupoObjetoCompra(), facade.getSistemaFacade().getDataOperacao());
                if (grupoObjetoCompraGrupoBem != null) {
                    itemSolicitacaoIncorporacaoMovelSelecionado.setGrupoBem(grupoObjetoCompraGrupoBem.getGrupoBem());
                } else {
                    if (itemSolicitacaoIncorporacaoMovelSelecionado.getItem() != null && itemSolicitacaoIncorporacaoMovelSelecionado.getItem().getGrupoObjetoCompra() != null) {
                        FacesUtil.addAtencao("Não foi possível encontrar uma associação de Grupo Objeto de Compra " + itemSolicitacaoIncorporacaoMovelSelecionado.getItem().getGrupoObjetoCompra().getDescricao() + " com um Grupo Patrimonial. Informe está associação no 'Cadastro de Grupo de Objeto de Compra com Grupo Patrimonial'");
                    }
                    itemSolicitacaoIncorporacaoMovelSelecionado.setGrupoBem(null);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            FacesUtil.addError(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), ex.getMessage());
        }
    }

    public void adicionarOrigemRecurso() {
        try {
            selecionado.getDetentorOrigemRecurso().adicionarOrigemRecurso(origemRecursoBemSelecionada);
            origemRecursoBemSelecionada = new OrigemRecursoBem();
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (ExcecaoNegocioGenerica en) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), en.getMessage());
        } catch (Exception e) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), e.getMessage());
        }
    }

    public List<SelectItem> getListaSelectItemEstadosDeConservacao() {
        return Util.getListSelectItem(Arrays.asList(EstadoConservacaoBem.getValoresPossiveisParaLevantamentoDeBemPatrimonial()));
    }

    public List<SelectItem> getListaSelectItemTipoRecursoAquisicaoBem() {
        return Util.getListSelectItem(Arrays.asList(TipoRecursoAquisicaoBem.values()));
    }

    public List<SelectItem> getListaSelectItemSituacaoConservacaoBem() {
        try {
            return Util.getListSelectItem(this.itemSolicitacaoIncorporacaoMovelSelecionado.getEstadoConservacaoBem().getSituacoes());
        } catch (NullPointerException ex) {
            return new ArrayList<>();
        }
    }

    public List<SelectItem> getListaSelectItemTipoDeAquisicaoBem() {
        List<SelectItem> lista = new ArrayList<>();
        for (SelectItem item : Util.getListSelectItem(Arrays.asList(TipoAquisicaoBem.values()))) {
            if (!TipoAquisicaoBem.CESSAO.equals(item.getValue()) && !TipoAquisicaoBem.COMPRA.equals(item.getValue())) {
                lista.add(item);
            }
        }
        return lista;
    }

    public HierarquiaOrganizacional getHierarquiaAdministrativa() {
        return hierarquiaAdministrativa;
    }

    public void setHierarquiaAdministrativa(HierarquiaOrganizacional hierarquiaAdministrativa) {
        if (hierarquiaAdministrativa != null) {
            selecionado.setUnidadeAdministrativa(hierarquiaAdministrativa.getSubordinada());
        }
        this.hierarquiaAdministrativa = hierarquiaAdministrativa;
    }

    public OrigemRecursoBem getOrigemRecursoBemSelecionada() {
        return origemRecursoBemSelecionada;
    }

    public void setOrigemRecursoBemSelecionada(OrigemRecursoBem origemRecursoBemSelecionada) {
        this.origemRecursoBemSelecionada = origemRecursoBemSelecionada;
    }

    public DocumetoComprobatorioIncorporacaoMovel getNovoDocumetoComprobatorio() {
        return novoDocumetoComprobatorio;
    }

    public void setNovoDocumetoComprobatorio(DocumetoComprobatorioIncorporacaoMovel novoDocumetoComprobatorio) {
        this.novoDocumetoComprobatorio = novoDocumetoComprobatorio;
    }

    public ItemSolicitacaoIncorporacaoMovel getItemSolicitacaoIncorporacaoMovelSelecionado() {
        return itemSolicitacaoIncorporacaoMovelSelecionado;
    }

    public void setItemSolicitacaoIncorporacaoMovelSelecionado(ItemSolicitacaoIncorporacaoMovel itemSolicitacaoIncorporacaoMovelSelecionado) {
        this.itemSolicitacaoIncorporacaoMovelSelecionado = itemSolicitacaoIncorporacaoMovelSelecionado;
    }

    public List<SelectItem> retornaHierarquiaOrcamentaria() {
        if (hierarquiaAdministrativa != null) {
            List<SelectItem> toReturn = new ArrayList<>();
            toReturn.add(new SelectItem(null, ""));
            for (HierarquiaOrganizacional obj : facade.getHierarquiaOrganizacionalFacade().retornaHierarquiaOrcamentariaPorUnidadeAdministrativa(hierarquiaAdministrativa.getSubordinada(), facade.getSistemaFacade().getDataOperacao())) {
                toReturn.add(new SelectItem(obj.getSubordinada(), obj.toString()));
            }
            return toReturn;
        }
        return null;
    }

    public HierarquiaOrganizacional getHierarquiaOrcamentaria() {
        return hierarquiaOrcamentaria;
    }

    public void setHierarquiaOrcamentaria(HierarquiaOrganizacional hierarquiaOrcamentaria) {
        this.hierarquiaOrcamentaria = hierarquiaOrcamentaria;
    }

    public List<SelectItem> getTipoGrupo() {
        try {
            return Util.getListSelectItem(Arrays.asList(TipoGrupo.values()));
        } catch (NullPointerException ex) {
            return new ArrayList<>();
        }
    }

    public void adicionarDocumentoComprobatorio() {
        try {
            validarDocumentoComprobatorio();
            Util.adicionarObjetoEmLista(selecionado.getDocumetosComprobatorio(), novoDocumetoComprobatorio);
            novoDocumetoComprobatorio = new DocumetoComprobatorioIncorporacaoMovel(selecionado);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private void validarDocumentoComprobatorio() {
        ValidacaoException ve = new ValidacaoException();
        if (novoDocumetoComprobatorio.getDataDocumento() == null
            && novoDocumetoComprobatorio.getNumero() == null
            && novoDocumetoComprobatorio.getTipoDocumento() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Deve ser preenchido ao menos um dos campos do documento comprobatório (Data, Número ou Tipo do Documento Fiscal)");

        }
        if (novoDocumetoComprobatorio.getNumero() != null && novoDocumetoComprobatorio.getNumero() < 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo número deve ser positivo.");
        }
        ve.lancarException();
    }

    public void documentoSelecionado(DocumetoComprobatorioIncorporacaoMovel doc) {
        documetoComprobatorioSelecionado = doc;
        if (mapAssintente.get(doc) == null) {
            mapAssintente.put(doc, new AssistenteDetentorArquivoComposicao(doc, facade.getSistemaFacade().getDataOperacao()));
        }
        assistenteDetentorArquivoComposicao = mapAssintente.get(doc);
    }

    public List<SelectItem> listaSelectItemEstadoConservacaoAlteracaoConservacao() {
        try {
            return Util.getListSelectItem(Arrays.asList(EstadoConservacaoBem.getValoresPossiveisParaAlteracaoConservacao(TipoBem.MOVEIS)));
        } catch (NullPointerException ex) {
            return new ArrayList<>();
        }
    }

    public void removerDocumentoComprobatorio(DocumetoComprobatorioIncorporacaoMovel doc) {
        selecionado.getDocumetosComprobatorio().remove(doc);
    }

    public void editarDocumentoComprobatorio(DocumetoComprobatorioIncorporacaoMovel doc) {
        novoDocumetoComprobatorio = (DocumetoComprobatorioIncorporacaoMovel) Util.clonarObjeto(doc);
    }

    public DocumetoComprobatorioIncorporacaoMovel getDocumetoComprobatorioSelecionado() {
        return documetoComprobatorioSelecionado;
    }

    public void setDocumetoComprobatorioSelecionado(DocumetoComprobatorioIncorporacaoMovel documetoComprobatorioSelecionado) {
        this.documetoComprobatorioSelecionado = documetoComprobatorioSelecionado;
    }

    public AssistenteDetentorArquivoComposicao getAssistenteDetentorArquivoComposicao() {
        return assistenteDetentorArquivoComposicao;
    }

    public void setAssistenteDetentorArquivoComposicao(AssistenteDetentorArquivoComposicao assistenteDetentorArquivoComposicao) {
        this.assistenteDetentorArquivoComposicao = assistenteDetentorArquivoComposicao;
    }

    public void removerBem(ItemSolicitacaoIncorporacaoMovel item) {
        selecionado.getItensSolicitacaoIncorporacaoMovel().remove(item);
    }

    public void editarBem(ItemSolicitacaoIncorporacaoMovel item) {
        itemSolicitacaoIncorporacaoMovelSelecionado = (ItemSolicitacaoIncorporacaoMovel) Util.clonarObjeto(item);
        if (itemSolicitacaoIncorporacaoMovelSelecionado.getGarantia() != null) {
            garantido = Boolean.TRUE;
        }
        if (itemSolicitacaoIncorporacaoMovelSelecionado.getSeguradora() == null) {
            itemSolicitacaoIncorporacaoMovelSelecionado.setSeguradora(new Seguradora());
        } else {
            segurado = Boolean.TRUE;
        }
    }

    public void novoBem() {
        itemSolicitacaoIncorporacaoMovelSelecionado = new ItemSolicitacaoIncorporacaoMovel(selecionado);
        itemSolicitacaoIncorporacaoMovelSelecionado.setValorBem(BigDecimal.ZERO);
        novaGarantia();
        novaSeguradora();
    }

    private void novaGarantia() {
        itemSolicitacaoIncorporacaoMovelSelecionado.setGarantia(new Garantia());
    }

    public void cancelarBem() {
        itemSolicitacaoIncorporacaoMovelSelecionado = null;
        segurado = Boolean.FALSE;
        garantido = Boolean.FALSE;
    }

    public void adicionarBem() {
        try {
            itemSolicitacaoIncorporacaoMovelSelecionado.validarNegocio();
            validarBemSeguradoAoAdicionar();
            validarBemComGarantiaAoAdicionar();
            Util.adicionarObjetoEmLista(selecionado.getItensSolicitacaoIncorporacaoMovel(), itemSolicitacaoIncorporacaoMovelSelecionado);
            cancelarBem();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarBemComGarantiaAoAdicionar() {
        if (!garantido) {
            itemSolicitacaoIncorporacaoMovelSelecionado.setGarantia(null);
        } else {
            Util.validarCampos(itemSolicitacaoIncorporacaoMovelSelecionado.getGarantia());
        }
    }

    private void validarBemSeguradoAoAdicionar() {
        if (!segurado) {
            itemSolicitacaoIncorporacaoMovelSelecionado.setSeguradora(null);
        } else {
            Util.validarCampos(itemSolicitacaoIncorporacaoMovelSelecionado.getSeguradora());
        }
    }

    public void concluirSolicitacao() {
        try {
            selecionado.realizarValidacoes();
            selecionado.validarNegocio(facade.getSistemaFacade().getDataOperacao());
            selecionado = facade.concluirSolicitacao(selecionado);
            redirecionarParaVer();
            FacesUtil.addOperacaoRealizada("Incorporação concluída com sucesso.");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    private void redirecionarParaVer() {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
    }

    public Boolean canExcluirSolicitacao() {
        return facade.isSolicitacaoIncorporacaoAprovada(selecionado, SituacaoAprovacao.APROVADO);
    }

    public List<SolicitacaoIncorporacaoMovel> completaSolicitacaoIncorporacaoMovelSemAprovacao(String parte) {
        return facade.completaSolicitacaoIncorporacaoMovelSemAprovacao(parte.trim());
    }

    public List<SolicitacaoIncorporacaoMovel> completaSolicitacaoIncorporacaoMovelComAprovacao(String parte) {
        return facade.completaSolicitacaoIncorporacaoMovelComAprovacao(parte.trim());
    }

    public void novaSeguradora() {
        if (itemSolicitacaoIncorporacaoMovelSelecionado.getSeguradora() == null) {
            itemSolicitacaoIncorporacaoMovelSelecionado.setSeguradora(new Seguradora());
        }
    }

    public Boolean getSegurado() {
        return segurado;
    }

    public void setSegurado(Boolean segurado) {
        this.segurado = segurado;
    }

    public Boolean getGarantido() {
        return garantido;
    }

    public void setGarantido(Boolean garantido) {
        this.garantido = garantido;
    }

    public List<SelectItem> getSituacoesConservacaoBem(EstadoConservacaoBem estadoConservacaoBem) {
        try {
            return Util.getListSelectItem(estadoConservacaoBem.getSituacoes());
        } catch (NullPointerException ex) {
            return new ArrayList<>();
        }
    }
}
