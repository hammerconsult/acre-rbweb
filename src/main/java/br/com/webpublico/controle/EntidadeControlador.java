/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.*;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;
import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author mga
 */
@ManagedBean(name = "entidadeControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoEntidade", pattern = "/entidade/novo/", viewId = "/faces/tributario/cadastromunicipal/entidade/edita.xhtml"),
    @URLMapping(id = "editarEntidade", pattern = "/entidade/editar/#{entidadeControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/entidade/edita.xhtml"),
    @URLMapping(id = "listarEntidade", pattern = "/entidade/listar/", viewId = "/faces/tributario/cadastromunicipal/entidade/lista.xhtml"),
    @URLMapping(id = "verEntidade", pattern = "/entidade/ver/#{entidadeControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/entidade/visualizar.xhtml")
})
public class EntidadeControlador extends PrettyControlador<Entidade> implements Serializable, CRUD {

    private static final Logger logger = LoggerFactory.getLogger(EntidadeControlador.class);

    @EJB
    EntidadeFacade entidadeFacade;
    private ConverterGenerico converterEsferaGoverno;
    private ConverterAutoComplete converterCNAE;
    private ConverterAutoComplete converterPessoaJuridica;
    private StreamedContent imagemFoto;
    private FileUploadEvent fileUploadEvent;
    private ConverterAutoComplete converterPessoa;
    private ConverterAutoComplete converterPagamentoDaGPS;
    private UnidadeOrganizacional unidadeSelecionado;
    private ResponsavelUnidade responsavelUnidade;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private HierarquiaOrganizacional hierarquiaOrganizacionalOrc;
    private ConverterAutoComplete converterHierarquiaOrganizacional;
    private ConverterAutoComplete converterHierarquiaOrganizacionalOrc;
    private ConverterAutoComplete converterAtoLegal;
    private ConverterAutoComplete converterContratoFP;
    private ConverterAutoComplete converterNaturezaJuridica;
    private PrevidenciaEntidade previdenciaEntidade;

    public EntidadeControlador() {
        super(Entidade.class);
        previdenciaEntidade = new PrevidenciaEntidade();
    }

    @Override
    public AbstractFacade getFacede() {
        return entidadeFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/entidade/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public List<NaturezaJuridicaEntidade> completaNaturezaJuridica(String parte) {
        return entidadeFacade.getNaturezaJuridicaEntidadeFacade().listaFiltrando(parte.trim(), "descricao", "codigo");
    }

    @Override
    @URLAction(mappingId = "novoEntidade", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        fileUploadEvent = null;
        unidadeSelecionado = new UnidadeOrganizacional();
        responsavelUnidade = null;
        hierarquiaOrganizacional = null;
        imagemFoto = null;
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("imagem-foto", imagemFoto);
        super.novo();
    }

    @Override
    @URLAction(mappingId = "editarEntidade", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
        fileUploadEvent = null;
        selecionado = entidadeFacade.recuperar(selecionado.getId());
        selecionar();
        imagemFoto = recuperarArquivoParaDownload();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("imagem-foto", imagemFoto);
    }

    private void selecionar() {
        if (selecionado.getUnidadeOrganizacional() != null) {
            this.hierarquiaOrganizacional = entidadeFacade.getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalPorUnidade(selecionado.getUnidadeOrganizacional(), TipoHierarquiaOrganizacional.ADMINISTRATIVA);
        }
        if (selecionado.getUnidadeOrganizacionalOrc() != null) {
            this.hierarquiaOrganizacionalOrc = entidadeFacade.getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalPorUnidade(selecionado.getUnidadeOrganizacionalOrc(), TipoHierarquiaOrganizacional.ORCAMENTARIA);
        }
    }

    @Override
    @URLAction(mappingId = "verEntidade", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
        selecionar();
    }

    public ResponsavelUnidade getResponsavelUnidade() {
        return responsavelUnidade;
    }

    public void setResponsavelUnidade(ResponsavelUnidade responsavelUnidade) {
        this.responsavelUnidade = responsavelUnidade;
    }

    public List<PessoaFisica> listaPessoaFisica(String parte) {
        return entidadeFacade.getPessoaFacade().listaPessoaComVinculoVigente(parte.trim());
    }

    public Boolean validaCampos() {
        boolean retorno = Util.validaCampos(selecionado);

        if (hierarquiaOrganizacional == null || hierarquiaOrganizacional.getSubordinada() == null) {
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), " O campo Unidade Organizacional deve ser informado.");
            retorno = false;
        }
        return retorno;
    }

    public void uploadImagen(FileUploadEvent event) {
        fileUploadEvent = event;
    }

    @Override
    public void salvar() {
        if (validaCampos()) {
            try {
                UnidadeOrganizacional u = entidadeFacade.getUnidadeOrganizacionalFacade().recuperar(hierarquiaOrganizacional.getSubordinada().getId());
                u.setEntidade(selecionado);
                entidadeFacade.getUnidadeOrganizacionalFacade().salvar(u);
                entidadeFacade.getHierarquiaOrganizacionalFacade().atualizaViewHierarquiaOrcamentaria();
                FacesUtil.addInfo(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), " Registro salvo com sucesso.");
                redireciona();
            } catch (Exception e) {
                FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), e.getMessage());
            }
        }
    }

    @Override
    public void excluir() {
        try {
            UnidadeOrganizacional uo = selecionado.getUnidadeOrganizacional();
            uo.setEntidade(null);
            entidadeFacade.getUnidadeOrganizacionalFacade().merge(uo);
            entidadeFacade.remover(selecionado);
            entidadeFacade.getHierarquiaOrganizacionalFacade().atualizaViewHierarquiaOrcamentaria();
            redireciona();
            FacesUtil.addInfo(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), " Registro excluído com sucesso.");
        } catch (Exception e) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), " Não é permitido a exclusão deste registro.");
        }
    }

    public void apagaArquivo() {
        Entidade e = (Entidade) selecionado;
        e.setArquivo(null);
    }

    public List<SelectItem> getClassificacaoUO() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (ClassificacaoUO cuo : ClassificacaoUO.values()) {
            toReturn.add(new SelectItem(cuo, cuo.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getEsferasDoPoder() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (EsferaDoPoder object : EsferaDoPoder.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public Converter getConverterUnidadeOrganizacional() {
        if (converterPessoaJuridica == null) {
            converterPessoaJuridica = new ConverterAutoComplete(PessoaJuridica.class, entidadeFacade.getPessoaJuridicaFacade());
        }
        return converterPessoaJuridica;
    }

    public Converter getConverterNaturezaJuridica() {
        if (converterNaturezaJuridica == null) {
            converterNaturezaJuridica = new ConverterAutoComplete(NaturezaJuridicaEntidade.class, entidadeFacade.getPessoaJuridicaFacade());
        }
        return converterNaturezaJuridica;
    }

    public List<PessoaJuridica> completaUnidadeOrganizacional(String parte) {
        return entidadeFacade.getPessoaJuridicaFacade().listaFiltrandoRazaoSocialCNPJ(parte.trim());
    }

    public List<SelectItem> getEsferaGoverno() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (EsferaGoverno object : entidadeFacade.getEsferaGovernoFacade().lista()) {
            toReturn.add(new SelectItem(object, object.getNome()));
        }
        return toReturn;
    }

    public ConverterGenerico getConverterEsferaGoverno() {
        if (converterEsferaGoverno == null) {
            converterEsferaGoverno = new ConverterGenerico(EsferaGoverno.class, entidadeFacade.getEsferaGovernoFacade());
        }
        return converterEsferaGoverno;
    }

    public ConverterAutoComplete getConverterPessoa() {
        if (converterPessoa == null) {
            converterPessoa = new ConverterAutoComplete(Pessoa.class, entidadeFacade.getPessoaFacade());
        }
        return converterPessoa;
    }

    public List<CNAE> completaCNAE(String parte) {
        return entidadeFacade.getCNAEFacade().listaFiltrando(parte.trim(), "descricaoDetalhada", "codigoCnae");
    }

    public Converter getConverterCNAE() {
        if (converterCNAE == null) {
            converterCNAE = new ConverterAutoComplete(CNAE.class, entidadeFacade.getCBOFacade());
        }
        return converterCNAE;
    }

    public List<SelectItem> getTipoEntidade() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (TipoEntidade object : TipoEntidade.values()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public void validacaoCnpj(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        if (entidadeFacade.valida_CpfCnpj((String) value) == false) {
            ((UIInput) component).setValid(false);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, " CNPJ inválida!", "Por favor, verificar CNPJ."));
        }
    }

    public List<PagamentoDaGPS> completaPagamentoDaGPS(String parte) {
        return entidadeFacade.getPagamentoDaGPSFacade().listaFiltrandoCodigoDescricao(parte);
    }

    public Converter getConverterPagamentoDaGPS() {
        if (converterPagamentoDaGPS == null) {
            converterPagamentoDaGPS = new ConverterAutoComplete(PagamentoDaGPS.class, entidadeFacade.getPagamentoDaGPSFacade());
        }
        return converterPagamentoDaGPS;
    }

    public List<SelectItem> getSimples() {
        List<SelectItem> retorno = new ArrayList<SelectItem>();
        for (Simples object : Simples.values()) {
            retorno.add(new SelectItem(object, object.toString()));
        }
        return retorno;
    }

    public List<SelectItem> getAliquotasRAT() {
        List<SelectItem> retorno = new ArrayList<SelectItem>();
        retorno.add(new SelectItem(null, ""));
        for (Simples object : Simples.values()) {
            retorno.add(new SelectItem(new BigDecimal("0"), "0.0"));
            retorno.add(new SelectItem(new BigDecimal("1"), "1.0"));
            retorno.add(new SelectItem(new BigDecimal("2"), "2.0"));
            retorno.add(new SelectItem(new BigDecimal("3"), "3.0"));
        }
        return retorno;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacionalOrc() {
        return hierarquiaOrganizacionalOrc;
    }

    public void setHierarquiaOrganizacionalOrc(HierarquiaOrganizacional hierarquiaOrganizacionalOrc) {
        this.hierarquiaOrganizacionalOrc = hierarquiaOrganizacionalOrc;
    }

    public UnidadeOrganizacional getUnidadeSelecionado() {
        return unidadeSelecionado;
    }

    public void setUnidadeSelecionado(UnidadeOrganizacional unidadeSelecionado) {
        this.unidadeSelecionado = unidadeSelecionado;
    }

    public void novoGestorOrdenadorEntidade() {
        this.responsavelUnidade = new ResponsavelUnidade();
    }

    public Entidade getSelecionadoAsEntidade() {
        return (Entidade) selecionado;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public List<HierarquiaOrganizacional> completaHierarquiaOrganizacional(String parte) {
        return entidadeFacade.getHierarquiaOrganizacionalFacade().filtrandoHierarquiaOrganizacionalTipo(parte.trim(), TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), UtilRH.getDataOperacao());
    }

    public List<HierarquiaOrganizacional> completaHierarquiaOrganizacionalOrc(String parte) {
        return entidadeFacade.getHierarquiaOrganizacionalFacade().filtrandoHierarquiaOrganizacionalTipo(parte.trim(), TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), UtilRH.getDataOperacao());
    }

    public Converter getConverterHierarquiaOrganizacional() {
        if (converterHierarquiaOrganizacional == null) {
            converterHierarquiaOrganizacional = new ConverterAutoComplete(HierarquiaOrganizacional.class, entidadeFacade.getHierarquiaOrganizacionalFacade());
        }
        return converterHierarquiaOrganizacional;
    }

    public Converter getConverterHierarquiaOrganizacionalOrc() {
        if (converterHierarquiaOrganizacionalOrc == null) {
            converterHierarquiaOrganizacionalOrc = new ConverterAutoComplete(HierarquiaOrganizacional.class, entidadeFacade.getHierarquiaOrganizacionalFacade());
        }
        return converterHierarquiaOrganizacionalOrc;
    }

    public Converter getConverterAtoLegal() {
        if (converterAtoLegal == null) {
            converterAtoLegal = new ConverterAutoComplete(AtoLegal.class, entidadeFacade.getAtoLegalFacade());
        }
        return converterAtoLegal;
    }

    public List<AtoLegal> completaAtoLegal(String parte) {
        return entidadeFacade.getAtoLegalFacade().listaFiltrandoNomeNumero(parte);
    }

//    public List<SelectItem> getFuncoesGestorOrdenador() {
//        List<SelectItem> toReturn = new ArrayList<SelectItem>();
//        for (FuncaoResponsavelUnidade object : FuncaoResponsavelUnidade.values()) {
//            toReturn.add(new SelectItem(object, object.getDescricao()));
//        }
//        return toReturn;
//    }

    public Converter getConverterContratoFP() {
        if (converterContratoFP == null) {
            converterContratoFP = new ConverterAutoComplete(ContratoFP.class, entidadeFacade.getContratoFPFacade());
        }
        return converterContratoFP;
    }

//    public List<ContratoFP> completaContratoFPs(String parte) {
//        try {
//            return contratoFPFacade.recuperaContratoVigenteMatriculaPorLocalDeTrabalhoRecursiva(parte, hierarquiaOrganizacional, responsavelUnidade.getInicioVigencia());
//        } catch (Exception e) {
//            return new ArrayList<>();
//        }
//    }
//
//    public void cancelarResponsavel() {
//        this.responsavelUnidade = null;
//    }

//    public void removerGestorOrdenador(ActionEvent ev) {
//        ResponsavelUnidade goe = (ResponsavelUnidade) ev.getComponent().getAttributes().get("item");
//        getSelecionadoAsEntidade().getResponsavelUnidade().remove(getSelecionadoAsEntidade().getResponsavelUnidade().indexOf(goe));
//    }
//
//    public void selecionarGestorOrdenador(ActionEvent ev) {
//        responsavelUnidade = (ResponsavelUnidade) ev.getComponent().getAttributes().get("item");
//    }
//
//    public void confirmarResponsavel() {
//        try {
//            responsavelUnidade.validarAntesDeConfirmar();
//            getSelecionadoAsEntidade().addGestorOrdenadorEntidade(this.responsavelUnidade);
//            responsavelUnidade = null;
//        } catch (Exception e) {
//            FacesUtil.addError("Operação não realizada!", e.getMessage());
//        }
//    }

//    public void verificarUnidadeIsNull() {
//        if (hierarquiaOrganizacional == null) {
//            FacesUtil.addWarn("Campos necessários não foram preenchidos.", "Para continuar, por favor informe a unidade organizacional.");
//            return;
//        }
//
//        if (responsavelUnidade.getInicioVigencia() == null) {
//            FacesUtil.addWarn("Campos necessários não foram preenchidos.", "Para continuar, por favor informe o início da vigência deste Gestor/Operador.");
//            return;
//        }
//    }

    public void recuperarUnidadeVinculadaComEntidade(SelectEvent ev) {
        HierarquiaOrganizacional ho = (HierarquiaOrganizacional) ev.getObject();
        if (ho.getSubordinada().getEntidade() != null) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), " A unidade organizacional <b>" + ho + "</b> já está vinculada a uma entidade e não pode ser vinculada novamente.");
            RequestContext.getCurrentInstance().update("Formulario:iUnidadeOrganizacional");
            ho = null;
        }

        hierarquiaOrganizacional = ho;
        RequestContext.getCurrentInstance().update("Formulario:esferaDoPoder");
        RequestContext.getCurrentInstance().update("Formulario:classificacao");
    }

    public void recuperarUnidadeOrcamentaria(SelectEvent ev) {
        HierarquiaOrganizacional ho = (HierarquiaOrganizacional) ev.getObject();
        selecionado.setUnidadeOrganizacionalOrc(ho.getSubordinada());
    }

    public List<SelectItem> getTiposDaUnidade() {
        List<SelectItem> retorno = new ArrayList<SelectItem>();

        for (TipoEntidade object : TipoEntidade.values()) {
            retorno.add(new SelectItem(object, object.getTipo()));
        }

        return retorno;
    }

    public List<SelectItem> getTiposAdministracao() {
        List<SelectItem> retorno = new ArrayList<SelectItem>();
        for (TipoAdministracao object : TipoAdministracao.values()) {
            retorno.add(new SelectItem(object, object.getDescricao()));
        }
        return retorno;
    }

    public StreamedContent recuperarArquivoParaDownload() {
        StreamedContent s = null;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        try {
            for (ArquivoParte a : selecionado.getArquivo().getPartes()) {
                try {
                    buffer.write(a.getDados());
                } catch (IOException ex) {
                    logger.error("Erro: ", ex);
                }
            }
            InputStream is = new ByteArrayInputStream(buffer.toByteArray());
            s = new DefaultStreamedContent(is, selecionado.getArquivo().getMimeType(), selecionado.getArquivo().getNome());

            return s;
        } catch (NullPointerException npe) {
            return null;
        }
    }

    public void handleFileUpload(FileUploadEvent event) {
        try {
            selecionado.setArquivo(new Arquivo());
            selecionado.getArquivo().setMimeType(event.getFile().getContentType());
            selecionado.getArquivo().setNome(event.getFile().getFileName());
            selecionado.getArquivo().setDescricao(event.getFile().getFileName());
            selecionado.getArquivo().setTamanho(event.getFile().getSize());
            selecionado.getArquivo().setInputStream(event.getFile().getInputstream());
            selecionado.setArquivo(entidadeFacade.getArquivoFacade().novoArquivoMemoria(selecionado.getArquivo()));

            imagemFoto = new DefaultStreamedContent(event.getFile().getInputstream(), "image/png", event.getFile().getFileName());
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("imagem-foto", imagemFoto);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
    }

    public void removerArquivo() {
        selecionado.setArquivo(null);
    }

    public List<Entidade> completarEntidades(String filtro) {
        return entidadeFacade.listaEntidades(filtro.trim());
    }

    public PrevidenciaEntidade getPrevidenciaEntidade() {
        return previdenciaEntidade;
    }

    public void setPrevidenciaEntidade(PrevidenciaEntidade previdenciaEntidade) {
        this.previdenciaEntidade = previdenciaEntidade;
    }


    public void adicionarPrevidencia() {
        try {
            validarPrevidencia();
            previdenciaEntidade.setEntidade(selecionado);
            Util.adicionarObjetoEmLista(selecionado.getPrevidenciaEntidades(), previdenciaEntidade);
            previdenciaEntidade = new PrevidenciaEntidade();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void validarPrevidencia() {
        ValidacaoException ve = new ValidacaoException();
        if (previdenciaEntidade.getInicioVigencia() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Informe a data inicial da vigência!");
        } else if (previdenciaEntidade.getFinalVigencia() != null) {
            if (previdenciaEntidade.getFinalVigencia().before(previdenciaEntidade.getInicioVigencia())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A data final da vigência deve ser posterior à data inicial da vigência!");
            }
        }
        for (PrevidenciaEntidade p : selecionado.getPrevidenciaEntidades()) {
            if (p.equals(previdenciaEntidade)) {
                continue;
            }
            if (p.getFinalVigencia() != null) {
                if (p.getFinalVigencia().after(previdenciaEntidade.getInicioVigencia())) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("A data Final da vigência anterior deve ser inferior à data inicial da nova vigência!");
                }
            } else {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A lista contém registro com vigência aberta!");
            }
        }
        ve.lancarException();
    }

    public void removerPrevidencia(PrevidenciaEntidade previdencia) {
        selecionado.getPrevidenciaEntidades().remove(previdencia);
    }

    public void editarPrevidencia(PrevidenciaEntidade previdencia) {
        previdenciaEntidade = (PrevidenciaEntidade) Util.clonarObjeto(previdencia);
    }
}
