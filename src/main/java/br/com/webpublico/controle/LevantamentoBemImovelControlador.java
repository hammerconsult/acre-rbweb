package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.LevantamentoBemImovelFacade;
import br.com.webpublico.util.DataUtil;
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
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: MGA
 * Date: 03/09/14
 * Time: 10:22
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean(name = "levantamentoBemImovelControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoLevantamentoBemImovel", pattern = "/levantamento-de-bem-imovel/novo/", viewId = "/faces/administrativo/patrimonio/levantamentobemimovel/edita.xhtml"),
    @URLMapping(id = "editarLevantamentoBemImovel", pattern = "/levantamento-de-bem-imovel/editar/#{levantamentoBemImovelControlador.id}/", viewId = "/faces/administrativo/patrimonio/levantamentobemimovel/edita.xhtml"),
    @URLMapping(id = "listarLevantamentoBemImovel", pattern = "/levantamento-de-bem-imovel/listar/", viewId = "/faces/administrativo/patrimonio/levantamentobemimovel/lista.xhtml"),
    @URLMapping(id = "verLevantamentoBemImovel", pattern = "/levantamento-de-bem-imovel/ver/#{levantamentoBemImovelControlador.id}/", viewId = "/faces/administrativo/patrimonio/levantamentobemimovel/visualizar.xhtml")
})
public class LevantamentoBemImovelControlador extends PrettyControlador<LevantamentoBemImovel> implements Serializable, CRUD {

    public static final String CAMINHO_NOVO = "/administrativo/patrimonio/levantamentobemimovel/edita.xhtml";
    @EJB
    private LevantamentoBemImovelFacade facade;
    private OrigemRecursoBem origemRecursoBemSelecionada;
    private DocumentoComprobatorioLevantamentoBemImovel documentoComprobatorioLevantamentoBemImovelSelecionado;
    private LevantamentoBemImovel levantamentoBemImovelOriginal;
    private boolean bloqueado = false;
    private Boolean segurado = Boolean.FALSE;
    private Boolean garantido = Boolean.FALSE;
    private List<HierarquiaOrganizacional> unidadesOrcamentarias;
    private HierarquiaOrganizacional unidadeAdministrativa;
    private HierarquiaOrganizacional unidadeOrcamentaria;

    public LevantamentoBemImovelControlador() {
        super(LevantamentoBemImovel.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/levantamento-de-bem-imovel/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    @URLAction(mappingId = "novoLevantamentoBemImovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        selecionado.setDataLevantamento(facade.getSistemaFacade().getDataOperacao());
        selecionado.setUsuarioLevantamento(facade.getSistemaFacade().getUsuarioCorrente());
        inicializarAtributos();
        levantamentoBemImovelOriginal = selecionado;
        selecionado.setGarantia(new Garantia());
        novaSeguradora();
    }

    @Override
    @URLAction(mappingId = "editarLevantamentoBemImovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
        inicializarAtributos();
        recuperarHierarquia();
        levantamentoBemImovelOriginal = (LevantamentoBemImovel) Util.clonarObjeto(selecionado);
        bloqueado = bloqueioPorFase(selecionado);
        segurado = selecionado.getSeguradora() != null;
        garantido = selecionado.getGarantia() != null;
        if (selecionado.getGarantia() == null) {
            selecionado.setGarantia(new Garantia());
        }
        novaSeguradora();
    }

    @URLAction(mappingId = "verLevantamentoBemImovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        operacao = Operacoes.VER;
        recuperarObjeto();
        recuperarHierarquia();
        levantamentoBemImovelOriginal = (LevantamentoBemImovel) Util.clonarObjeto(selecionado);
        segurado = selecionado.getSeguradora() != null;
        garantido = selecionado.getGarantia() != null;
    }

    @Override
    public void salvar() {
        try {
            Util.validarCampos(selecionado);
            Fase fase = facade.getFaseFacade().buscarFase(CAMINHO_NOVO, selecionado.getUnidadeOrcamentaria(), selecionado.getDataAquisicao());
            String nomeFase = fase != null ? fase.toString() : "Nenhuma configuração (Fase ou Período Fase) encontrada!";
            validarLevantamento(nomeFase);
            if (isOperacaoEditar() && bloqueado) {
                validarCamposAlterados(nomeFase);
            }
            selecionado = facade.salvarRetornando(selecionado);
            FacesUtil.executaJavaScript("iniciarSalvar()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    public boolean existeEfetivacaoLevantamento() {
        return facade.isExisteEfetivacaoToLevantamentoSelecionado(selecionado.getId());
    }

    private void inicializarAtributos() {
        this.origemRecursoBemSelecionada = new OrigemRecursoBem();
        this.documentoComprobatorioLevantamentoBemImovelSelecionado = new DocumentoComprobatorioLevantamentoBemImovel();
    }

    private void recuperarHierarquia() {
        try {
            setUnidadeAdministrativa(facade.getHierarquiaOrganizacionalFacade().getHierarquiaDaUnidade(TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), this.selecionado.getUnidadeAdministrativa(), facade.getSistemaFacade().getDataOperacao()));
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    private void validarLevantamento(String nomeFase) {
        validarBloqueadoPorFase(nomeFase);
        validarSeguradora();
        validarGarantia();
        selecionado.validarRegrasDeNegocio();
        Util.validarDataMinima(selecionado.getDataAquisicao(), "data de aquisição");
    }

    public void validarBloqueadoPorFase(String nomeFase) {
        ValidacaoException ve = new ValidacaoException();
        if (bloqueioPorFase(selecionado)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Bloqueado pelo controle de fase: " + nomeFase + ".");
        }
        ve.lancarException();
    }

    public BigDecimal getValorTotal() {
        BigDecimal total = BigDecimal.ZERO;
        try {
            for (DocumentoComprobatorioLevantamentoBemImovel documentoComprobatorioLevantamentoBemImovel : selecionado.getDocumentosComprobatorios()) {
                total = total.add(documentoComprobatorioLevantamentoBemImovel.getValor());
            }
            return total;
        } catch (NullPointerException npe) {
            return BigDecimal.ZERO;
        }
    }

    public void validarGarantia() {
        if (!garantido) {
            selecionado.setGarantia(null);
        } else {
            Util.validarCampos(selecionado.getGarantia());
        }
    }

    public void validarSeguradora() {
        if (!segurado) {
            selecionado.setSeguradora(null);
        } else {
            Util.validarCampos(selecionado.getSeguradora());
        }
    }

    @Override
    public void excluir() {
        if (!bloqueioPorFase(selecionado)) {
            FacesUtil.executaJavaScript("confirmarExclusao.show()");
        } else {
            try {
                FacesUtil.addOperacaoNaoPermitida("Bloqueado pelo controle de fase: " + facade.getFaseFacade().buscarFase(CAMINHO_NOVO, selecionado.getUnidadeOrcamentaria(), selecionado.getDataAquisicao()));
            } catch (NullPointerException nu) {
                FacesUtil.addOperacaoNaoRealizada("Nenhuma configuração (Fase ou Período Fase) encontrada!");
            }
        }
    }

    public void novaSeguradora() {
        if (selecionado.getSeguradora() == null) {
            selecionado.setSeguradora(new Seguradora());
        }
    }

    public void confirmarExclusao() {
        super.excluir();
    }

    public OrigemRecursoBem getOrigemRecursoBemSelecionada() {
        return origemRecursoBemSelecionada;
    }

    public void setOrigemRecursoBemSelecionada(OrigemRecursoBem origemRecursoBemSelecionada) {
        this.origemRecursoBemSelecionada = origemRecursoBemSelecionada;
    }

    public DocumentoComprobatorioLevantamentoBemImovel getDocumentoComprobatorioLevantamentoBemImovelSelecionado() {
        return documentoComprobatorioLevantamentoBemImovelSelecionado;
    }

    public void setDocumentoComprobatorioLevantamentoBemImovelSelecionado(DocumentoComprobatorioLevantamentoBemImovel documentoComprobatorioLevantamentoBemImovelSelecionado) {
        this.documentoComprobatorioLevantamentoBemImovelSelecionado = documentoComprobatorioLevantamentoBemImovelSelecionado;
    }

    public Boolean getSegurado() {
        return segurado;
    }

    public void setSegurado(Boolean segurado) {
        this.segurado = segurado;
    }

    public List<HierarquiaOrganizacional> completaHierarquiaSegundoNivel(String parte) {
        return facade.getHierarquiaOrganizacionalFacade().filtraPorNivel(parte.trim(), "2", TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), facade.getSistemaFacade().getDataOperacao());
    }

    public List<LevantamentoBemImovel> completarLevantamentoBemImovel(String parte) {
        return facade.listaFiltrando(parte.trim(), "codigoPatrimonio", "descricaoImovel");
    }

    public List<CadastroImobiliario> completarCadastroImobiliario(String parte) {
        return facade.getCadastroImobiliarioFacade().buscarCadastroImobiliarioPorSituacao(parte.trim(), true, "inscricaoCadastral");
    }

    public List<SelectItem> retornaHierarquiaOrcamentaria() {
        List<SelectItem> toReturn = new ArrayList<>();
        if (selecionado.getUnidadeAdministrativa() != null) {
            toReturn.add(new SelectItem(null, ""));
            for (HierarquiaOrganizacional obj : unidadesOrcamentarias) {
                toReturn.add(new SelectItem(obj.getSubordinada(), obj.toString()));
            }
        }
        return toReturn;
    }

    public void listenerUnidadeAdministrativa() {
        unidadesOrcamentarias = facade.getHierarquiaOrganizacionalFacade().retornaHierarquiaOrcamentariaPorUnidadeAdministrativa(
            unidadeAdministrativa.getSubordinada(), facade.getSistemaFacade().getDataOperacao());
    }

    public List<SelectItem> getListaSelectItemTipoDeAquisicaoBem() {
        return Util.getListSelectItem(Arrays.asList(LevantamentoBemImovel.tiposDeAquisicaoPermitidosNoCadastro));
    }

    public void adicionarOrigemRecurso() {
        try {
            selecionado.adicionarOrigemRecurso(origemRecursoBemSelecionada);
            origemRecursoBemSelecionada = new OrigemRecursoBem();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void removerOrigemRecurso(OrigemRecursoBem origem) {
        selecionado.getListaDeOriemRecursoBem().remove(origem);
    }

    public void selecionarDocumento(DocumentoComprobatorioLevantamentoBemImovel documento) {
        documentoComprobatorioLevantamentoBemImovelSelecionado = documento;
    }

    public boolean bloqueioPorFase(LevantamentoBemImovel levantamentoBemImovel) {
        return !facade.getFaseFacade().existePeriodoFaseVigenteNaDataDeFechamento("/administrativo/patrimonio/levantamentobemimovel/edita.xhtml", levantamentoBemImovel.getDataAquisicao(), levantamentoBemImovel.getUnidadeOrcamentaria());
    }

    public void validarCamposAlterados(String nomeFase) {
        ValidacaoException ve = new ValidacaoException();
        if (!selecionado.getUnidadeOrcamentaria().getId().equals(levantamentoBemImovelOriginal.getUnidadeOrcamentaria().getId())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Unidade Orçamentária não pode ser alterado por que está bloqueado pelo controle de fase: " + nomeFase);
        }
        if (!DataUtil.dataSemHorario(selecionado.getDataAquisicao()).equals(DataUtil.dataSemHorario(levantamentoBemImovelOriginal.getDataAquisicao()))) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Data de Aquisição não pode ser alterado por que está bloqueado pelo controle de fase: " + nomeFase);
        }
        if (!selecionado.getGrupoBem().equals(levantamentoBemImovelOriginal.getGrupoBem())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Grupo Bem não pode ser alterado por que está bloqueado pelo controle de fase: " + nomeFase);
        }
        if (selecionado.getValorBem().compareTo(levantamentoBemImovelOriginal.getValorBem()) != 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Valor do Bem não pode ser alterado por que está bloqueado pelo controle de fase: " + nomeFase);
        }
        ve.lancarException();
    }

    public List<SelectItem> getListaSelectItemSituacaoConservacaoBem() {
        try {
            return Util.getListSelectItem(this.selecionado.getEstadoConservacaoBem().getSituacoes());
        } catch (NullPointerException ex) {
            return new ArrayList<>();
        }
    }

    public Boolean getGarantido() {
        return garantido;
    }

    public void setGarantido(Boolean garantido) {
        this.garantido = garantido;
    }

    public HierarquiaOrganizacional getUnidadeOrcamentaria() {
        return unidadeOrcamentaria;
    }

    public void setUnidadeOrcamentaria(HierarquiaOrganizacional unidadeOrcamentaria) {
        this.unidadeOrcamentaria = unidadeOrcamentaria;
    }

    public HierarquiaOrganizacional getUnidadeAdministrativa() {
        return unidadeAdministrativa;
    }

    public void setUnidadeAdministrativa(HierarquiaOrganizacional unidadeAdministrativa) {
        this.unidadeAdministrativa = unidadeAdministrativa;
        if (this.unidadeAdministrativa != null) {
            selecionado.setUnidadeAdministrativa(this.unidadeAdministrativa.getSubordinada());
        }
    }
}
