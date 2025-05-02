package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.ProtocoloImpressao;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.VoProcesso;
import br.com.webpublico.entidadesauxiliares.VoTramite;
import br.com.webpublico.entidadesauxiliares.administrativo.FiltroListaProtocolo;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.ProcessoFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.ConverterGenerico;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.*;

@ManagedBean(name = "processoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoProcesso", pattern = "/processo/novo/", viewId = "/faces/tributario/cadastromunicipal/processo/edita.xhtml"),
    @URLMapping(id = "editarProcesso", pattern = "/processo/editar/#{processoControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/processo/edita.xhtml"),
    @URLMapping(id = "listarProcesso", pattern = "/processo/listar/", viewId = "/faces/tributario/cadastromunicipal/processo/lista.xhtml"),
    @URLMapping(id = "consultaProcesso", pattern = "/processo/consultar/", viewId = "/faces/tributario/cadastromunicipal/processo/consulta.xhtml"),
    @URLMapping(id = "verProcesso", pattern = "/processo/ver/#{processoControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/processo/visualizar.xhtml"),
    @URLMapping(id = "verConsultaProcesso", pattern = "/processo/verconsulta/#{processoControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/processo/visualizar.xhtml"),
    @URLMapping(id = "gerirProcesso", pattern = "/processo/gerir/", viewId = "/faces/tributario/cadastromunicipal/processo/gerir.xhtml"),
    @URLMapping(id = "gerirProcessoNotificacao", pattern = "/processo/gerir/#{processoControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/processo/gerir.xhtml")
})
public class ProcessoControlador extends PrettyControlador<Processo> implements Serializable, CRUD {

    private static final Logger logger = LoggerFactory.getLogger(ProcessoControlador.class);

    @EJB
    private ProcessoFacade processoFacade;
    protected ConverterAutoComplete converterProcesso;
    private ConverterGenerico converterAssunto;
    private ConverterGenerico converterSubAssunto;
    private ConverterAutoComplete converterUsuario;
    private ConverterAutoComplete converterPessoa;
    private Assunto assunto;
    private ConverterGenerico converterDocumento;
    private ConverterGenerico converterUO;
    private ConverterAutoComplete converterDoc;
    private DocumentoProcesso documento;
    private Processo processoSelecionado;
    private Processo englobado;
    private Arquivo arquivos;
    private ConverterAutoComplete converterHierarquiaOrganizacional;
    private TreeNode rootOrc;
    private TreeNode selectedNode;
    private String numeroPesquisaGerado;
    private String anoPesquisaGerado;
    private String solicitantePesquisaGerado;
    private String numeroPesquisaEncaminhado;
    private String anoPesquisaEncaminhado;
    private String solicitantePesquisaEncaminhado;
    private String numeroPesquisaArquivado;
    private String anoPesquisaArquivado;
    private String solicitantePesquisaArquivado;
    private String numeroPesquisaFinalizado;
    private String anoPesquisaFinalizado;
    private String solicitantePesquisaFinalizado;
    private Boolean consulta;
    private List<VoProcesso> listaConsultaProcessoGerados;
    private List<VoProcesso> listaConsultaProcessoEncaminhado;
    private List<VoProcesso> listaConsultaProcessoArquivado;
    private List<VoProcesso> listaConsultaProcessoFinalizado;
    private List<VoTramite> listaTramitePendenteProcesso;
    private List<VoTramite> listaTramitesRecebidosProcesso;
    protected int inicioConsultaProcessoGerados = 0;
    protected int inicioConsultaProcessoEncaminhado = 0;
    protected int inicioConsultaProcessoArquivado = 0;
    protected int inicioConsultaProcessoFinalizado = 0;
    private int inicioConsultaTramitePendente = 0;
    private int inicioConsultaTramitesRecebidos = 0;
    protected int maximoRegistrosTabela = 10;

    private Boolean podeEditar;

    private Boolean podeFazerDownload;

    public Boolean getConsulta() {
        return consulta;
    }

    public void setConsulta(Boolean consulta) {
        this.consulta = consulta;
    }

    public String getNumeroPesquisaGerado() {
        return numeroPesquisaGerado;
    }

    public void setNumeroPesquisaGerado(String numeroPesquisaGerado) {
        this.numeroPesquisaGerado = numeroPesquisaGerado;
    }

    public String getAnoPesquisaGerado() {
        return anoPesquisaGerado;
    }

    public void setAnoPesquisaGerado(String anoPesquisaGerado) {
        this.anoPesquisaGerado = anoPesquisaGerado;
    }

    public String getSolicitantePesquisaGerado() {
        return solicitantePesquisaGerado;
    }

    public void setSolicitantePesquisaGerado(String solicitantePesquisaGerado) {
        this.solicitantePesquisaGerado = solicitantePesquisaGerado;
    }

    public String getNumeroPesquisaEncaminhado() {
        return numeroPesquisaEncaminhado;
    }

    public void setNumeroPesquisaEncaminhado(String numeroPesquisaEncaminhado) {
        this.numeroPesquisaEncaminhado = numeroPesquisaEncaminhado;
    }

    public String getAnoPesquisaEncaminhado() {
        return anoPesquisaEncaminhado;
    }

    public void setAnoPesquisaEncaminhado(String anoPesquisaEncaminhado) {
        this.anoPesquisaEncaminhado = anoPesquisaEncaminhado;
    }

    public String getSolicitantePesquisaEncaminhado() {
        return solicitantePesquisaEncaminhado;
    }

    public void setSolicitantePesquisaEncaminhado(String solicitantePesquisaEncaminhado) {
        this.solicitantePesquisaEncaminhado = solicitantePesquisaEncaminhado;
    }

    public String getNumeroPesquisaArquivado() {
        return numeroPesquisaArquivado;
    }

    public void setNumeroPesquisaArquivado(String numeroPesquisaArquivado) {
        this.numeroPesquisaArquivado = numeroPesquisaArquivado;
    }

    public String getAnoPesquisaArquivado() {
        return anoPesquisaArquivado;
    }

    public void setAnoPesquisaArquivado(String anoPesquisaArquivado) {
        this.anoPesquisaArquivado = anoPesquisaArquivado;
    }

    public String getSolicitantePesquisaArquivado() {
        return solicitantePesquisaArquivado;
    }

    public void setSolicitantePesquisaArquivado(String solicitantePesquisaArquivado) {
        this.solicitantePesquisaArquivado = solicitantePesquisaArquivado;
    }

    public String getNumeroPesquisaFinalizado() {
        return numeroPesquisaFinalizado;
    }

    public void setNumeroPesquisaFinalizado(String numeroPesquisaFinalizado) {
        this.numeroPesquisaFinalizado = numeroPesquisaFinalizado;
    }

    public String getAnoPesquisaFinalizado() {
        return anoPesquisaFinalizado;
    }

    public void setAnoPesquisaFinalizado(String anoPesquisaFinalizado) {
        this.anoPesquisaFinalizado = anoPesquisaFinalizado;
    }

    public String getSolicitantePesquisaFinalizado() {
        return solicitantePesquisaFinalizado;
    }

    public void setSolicitantePesquisaFinalizado(String solicitantePesquisaFinalizado) {
        this.solicitantePesquisaFinalizado = solicitantePesquisaFinalizado;
    }

    public TreeNode getSelectedNode() {
        return selectedNode;
    }

    public void setSelectedNode(TreeNode selectedNode) {
        this.selectedNode = selectedNode;
    }

    public TreeNode getRootOrc() {
        return rootOrc;
    }

    public void setRootOrc(TreeNode rootOrc) {
        this.rootOrc = rootOrc;
    }

    public Arquivo getArquivo() {
        return arquivos;
    }

    public void setArquivo(Arquivo arquivo) {
        this.arquivos = arquivo;
    }

    public List<VoProcesso> getListaConsultaProcessoGerados() {
        return listaConsultaProcessoGerados;
    }

    public void setListaConsultaProcessoGerados(List<VoProcesso> listaConsultaProcessoGerados) {
        this.listaConsultaProcessoGerados = listaConsultaProcessoGerados;
    }

    public List<VoProcesso> getListaConsultaProcessoEncaminhado() {
        return listaConsultaProcessoEncaminhado;
    }

    public void setListaConsultaProcessoEncaminhado(List<VoProcesso> listaConsultaProcessoEncaminhado) {
        this.listaConsultaProcessoEncaminhado = listaConsultaProcessoEncaminhado;
    }

    public List<VoProcesso> getListaConsultaProcessoArquivado() {
        return listaConsultaProcessoArquivado;
    }

    public void setListaConsultaProcessoArquivado(List<VoProcesso> listaConsultaProcessoArquivado) {
        this.listaConsultaProcessoArquivado = listaConsultaProcessoArquivado;
    }

    public List<VoProcesso> getListaConsultaProcessoFinalizado() {
        return listaConsultaProcessoFinalizado;
    }

    public void setListaConsultaProcessoFinalizado(List<VoProcesso> listaConsultaProcessoFinalizado) {
        this.listaConsultaProcessoFinalizado = listaConsultaProcessoFinalizado;
    }

    public List<VoTramite> getListaTramitePendenteProcesso() {
        return listaTramitePendenteProcesso;
    }

    public void setListaTramitePendenteProcesso(List<VoTramite> listaTramitePendenteProcesso) {
        this.listaTramitePendenteProcesso = listaTramitePendenteProcesso;
    }

    public List<VoTramite> getListaTramitesRecebidosProcesso() {
        return listaTramitesRecebidosProcesso;
    }

    public void setListaTramitesRecebidosProcesso(List<VoTramite> listaTramitesRecebidosProcesso) {
        this.listaTramitesRecebidosProcesso = listaTramitesRecebidosProcesso;
    }

    public Processo getEnglobado() {
        return englobado;
    }

    public void setEnglobado(Processo englobado) {
        this.englobado = englobado;
    }

    public List<SelectItem> getListaSituacao() {
        List<SelectItem> listaSituacao = new ArrayList<SelectItem>();
        listaSituacao.add(new SelectItem(null, ""));
        for (SituacaoProcesso sp : SituacaoProcesso.values()) {
            listaSituacao.add(new SelectItem(sp, sp.getDescricao()));
        }
        return listaSituacao;
    }

    private void validarCamposProcesso() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getPessoa() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Solicitante!");
        }
        if (assunto == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Assunto do processo!");
        }
        if (selecionado.getSubAssunto() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Subassunto do processo!");
        }
        if (selecionado.getUoCadastro() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a Unidade Organizacional que o protocolo será cadastrado!");
        }
        ve.lancarException();
    }

    public Boolean validarCampos() {
        Boolean retorno = Util.validaCampos(selecionado);
        if (selecionado.getPessoa() != null) {
            if (selecionado.getPessoa() instanceof PessoaFisica) {
                if (!valida_CpfCnpj(((PessoaFisica) selecionado.getPessoa()).getCpf())) {
                    FacesUtil.addWarn("Atenção!", "A Pessoa selecionada não possui um CPF válido!");
                    retorno = false;
                } else if (processoFacade.getPessoaFacade().hasOutraPessoaComMesmoCpf((PessoaFisica) selecionado.getPessoa(), false)) {
                    FacesUtil.addWarn("Atenção!", "O CPF da pessoa selecionada pertence a mais de uma pessoa física!");
                    retorno = false;
                }
            } else {
                if (!valida_CpfCnpj(((PessoaJuridica) selecionado.getPessoa()).getCnpj())) {
                    FacesUtil.addWarn("Atenção!", "A Pessoa selecionada não possui um CNPJ válido!");
                    retorno = false;
                } else if (processoFacade.getPessoaFacade().hasOutraPessoaComMesmoCnpj((PessoaJuridica) selecionado.getPessoa(), false)) {
                    FacesUtil.addWarn("Atenção!", "O CNPJ da pessoa selecionada pertence a mais de uma pessoa jurídica!");
                    retorno = false;
                }
            }
        }
        return retorno;
    }

    public List<Arquivo> getListaDocumentos(ActionEvent evt) {
        Processo pr = (Processo) evt.getComponent().getAttributes().get("arquivo");
        return processoFacade.listaArquivosUpload(pr.getId());
    }

    public void removeFileUpload(ActionEvent event) {
        ProcessoArquivo arq = (ProcessoArquivo) event.getComponent().getAttributes().get("remove");
        selecionado.getArquivos().remove(arq);
    }

    public SistemaControlador getSistemaControlador() {
        return (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }

    public Assunto getAssunto() {
        return assunto;
    }

    public void setAssunto(Assunto assunto) {
        this.assunto = assunto;
    }

    public ProcessoControlador() {
        super(Processo.class);
    }

    public ProcessoFacade getFacade() {
        return processoFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return processoFacade;
    }

    @URLAction(mappingId = "novoProcesso", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        operacao = Operacoes.NOVO;
        processoFacade.novoProcesso(selecionado, getSistemaControlador());
        englobado = null;
        documento = new DocumentoProcesso();
        assunto = new Assunto();
        if (selecionado.getSubAssunto() != null) {
            assunto = selecionado.getSubAssunto().getAssunto();
        }
    }

    @URLAction(mappingId = "gerirProcesso", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void gerirProcesso() {
        processoSelecionado = new Processo();
    }

    @URLAction(mappingId = "gerirProcessoNotificacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void gerirProcessoNotificacao() {
        Processo p = processoFacade.recuperar(getId());
        processoSelecionado = p;
        FacesUtil.atualizarComponente("Formulario");
    }

    public List<Processo> listaProcessosAbertos() {
        return processoFacade.listaProcessosAbertos(getSistemaControlador().getUsuarioCorrente());
    }

    public void salvaAlteracaoProcesso() throws ExcecaoNegocioGenerica {
        if (processoSelecionado.getId() != null) {
            if (processoSelecionado.getSituacao() != null && (processoSelecionado.getMotivo() != null && !"".equals(processoSelecionado.getMotivo().trim()))) {
                try {
                    processoFacade.atualizaSituacaoProcesso(processoSelecionado);
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Salvo com sucesso!", "Salvo com sucesso!"));
                    processoFacade.removerNotificacaoDoProcesso(processoSelecionado);
                } catch (ExcecaoNegocioGenerica ex) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, ex.getMessage(), ex.getMessage()));
                }
                processoSelecionado = new Processo();
            } else {
                FacesUtil.addError("Não é possível salvar!", "Informe a situação do processo e o motivo da alteração!");
            }
        }
    }

    public Boolean liberaEdicao() {
        if (processoSelecionado.getId() != null) {
            return true;
        }
        return false;
    }

    public void setaProcessoSelecionado(ActionEvent evt) {
        processoSelecionado = (Processo) evt.getComponent().getAttributes().get("viewProc");
        Processo p = processoFacade.recuperar(processoSelecionado.getId());
        processoSelecionado = p;
    }

    public List<Tramite> getTramites() {
        List<Tramite> trm = selecionado.getTramites();
        Collections.sort(trm);
        return trm;
    }

    public List<SelectItem> getListaUOUsuarioLogado() {
        List<SelectItem> listaUOS = new ArrayList<SelectItem>();
        listaUOS.add(new SelectItem(null, ""));
        for (HierarquiaOrganizacional uo : processoFacade.getTramiteFacade().getUnidadeOrganizacionalFacade().buscarUnidadesUsuarioCorrenteProtocolo(getSistemaControlador().getUsuarioCorrente())) {
            listaUOS.add(new SelectItem(uo.getSubordinada(), uo.toString()));
        }
        return listaUOS;
    }

    public void removeDocumento(ActionEvent evt) {
        DocumentoProcesso dec = (DocumentoProcesso) evt.getComponent().getAttributes().get("removeDocumento");
        selecionado.getDocumentoProcesso().remove(dec);
    }

    public List<Documento> completaDocumentos(String parte) {
        return processoFacade.getDocumentoFacade().listaFiltrando(parte.trim(), "descricao");
    }

    public ConverterAutoComplete getConverterDoc() {
        if (converterDoc == null) {
            converterDoc = new ConverterAutoComplete(Documento.class, processoFacade.getDocumentoFacade());
        }
        return converterDoc;
    }

    public List<SelectItem> getSituacaoProcessoGerir() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, " "));
        for (SituacaoProcesso object : SituacaoProcesso.values()) {
            if ((!object.equals(SituacaoProcesso.GERADO)) && (!object.equals(SituacaoProcesso.EMTRAMITE)) && (!object.equals(SituacaoProcesso.ARQUIVADO))) {
                toReturn.add(new SelectItem(object, object.getDescricao()));
            }
        }
        return toReturn;
    }

    public List<SelectItem> getProcessos() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, " "));
        for (Processo object : processoFacade.lista()) {
            toReturn.add(new SelectItem(object, object.getNumero().toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getDocumentos() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (Documento object : processoFacade.getDocumentoFacade().lista()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public ConverterGenerico getConverterDocumento() {
        if (converterDocumento == null) {
            converterDocumento = new ConverterGenerico(Documento.class, processoFacade.getDocumentoFacade());
        }
        return converterDocumento;
    }

    public ConverterGenerico getConverterUO() {
        if (converterUO == null) {
            converterUO = new ConverterGenerico(UnidadeOrganizacional.class, processoFacade.getUnidadeOrganizacionalFacade());
        }
        return converterUO;
    }

    public List<SelectItem> getAssuntos() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, " "));
        for (Assunto object : processoFacade.getAssuntoFacade().lista()) {
            toReturn.add(new SelectItem(object, object.getNome()));
        }
        return toReturn;
    }

    public ConverterGenerico getConverterAssunto() {
        if (converterAssunto == null) {
            converterAssunto = new ConverterGenerico(Assunto.class, processoFacade.getAssuntoFacade());
        }
        return converterAssunto;
    }

    public List<SelectItem> getSubAssuntos() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, " "));
        long id;
        if (assunto == null || assunto.getId() == null) {
            id = -1;
        } else {
            id = assunto.getId();
        }

        for (SubAssunto object : processoFacade.getSubAssuntoFacade().listaId(id, "assunto")) {
            toReturn.add(new SelectItem(object, object.getNome()));
        }
        return toReturn;
    }

    public ConverterGenerico getConverterSubAssunto() {
        if (converterSubAssunto == null) {
            converterSubAssunto = new ConverterGenerico(SubAssunto.class, processoFacade.getSubAssuntoFacade());
        }
        return converterSubAssunto;
    }

    public List<Processo> completaProcesso(String parte) {
        boolean teste = parte.matches("[0-9]*");
        if (teste) {
            return processoFacade.listaFiltrandoInteiro(parte.trim(), "numero");
        } else {
            parte = "-1";
            return processoFacade.listaFiltrandoInteiro(parte.trim(), "numero");
        }
    }

    public Converter getConverterProcesso() {
        if (converterProcesso == null) {
            converterProcesso = new ConverterAutoComplete(Processo.class, processoFacade);
        }
        return converterProcesso;
    }

    public List<Pessoa> completaPessoa(String parte) {
        return processoFacade.getPessoaFacade().listaTodasPessoas(parte.trim());
    }

    public Converter getConverterPessoa() {
        if (converterPessoa == null) {
            converterPessoa = new ConverterAutoComplete(Pessoa.class, processoFacade.getPessoaFacade());
        }
        return converterPessoa;
    }

    public void arvoreTramite() {
        List<ItemRota> its;
        if (selecionado.getSubAssunto() != null) {
            selecionado.getRotas().clear();
            selecionado.setDocumentoProcesso(new ArrayList<DocumentoProcesso>());
            selecionado.setTramites(new ArrayList<Tramite>());
            selecionado.setSubAssunto(processoFacade.getSubAssuntoFacade().recuperar(selecionado.getSubAssunto().getId()));
            atribuirProcessoRotaTramite();
        } else {
            selecionado.setTramites(new ArrayList<Tramite>());
            selecionado.setRotas(new ArrayList<ProcessoRota>());
        }
    }

    private void atribuirProcessoRotaTramite() {
        for (SubAssuntoDocumento s : selecionado.getSubAssunto().getSubAssuntoDocumentos()) {
            DocumentoProcesso dp = new DocumentoProcesso();
            dp.setDocumento(s.getDocumento());
            dp.setProcesso(selecionado);
            selecionado.getDocumentoProcesso().add(dp);
        }
        novoProcessoTramite();
    }

    private void novoProcessoTramite() {
        if (selecionado.getSubAssunto().getItensRota() != null) {
            ItemRota itemRota = selecionado.getSubAssunto().getItensRota().get(0);
            selecionado.getTramites().add(new Tramite("", selecionado, itemRota.getUnidadeOrganizacional(),
                null, new Date(), 0, true, itemRota.getPrazo(), false, itemRota.getTipoPrazo(), selecionado.getUoCadastro(), false));
            for (ItemRota item : selecionado.getSubAssunto().getItensRota()) {
                if (item != null) {
                    ProcessoRota rota = new ProcessoRota();
                    rota.setIndice(selecionado.getRotas().size());
                    rota.setDataRegistro(item.getDataRegistro());
                    rota.setPrazo(item.getPrazo());
                    rota.setTipoPrazo(item.getTipoPrazo());
                    rota.setUnidadeOrganizacional(item.getUnidadeOrganizacional());
                    rota.setProcesso(selecionado);
                    rota.setTramitado(rota.getIndice() == 0);
                    Util.adicionarObjetoEmLista(selecionado.getRotas(), rota);
                }
            }
        }
    }

    public void chamaDoc() {
        if (documento.getDocumento() != null) {
            addDocumento(documento);
            documento = new DocumentoProcesso();
        } else {
            FacesUtil.addError("Atenção!", "Selecione um Documento");
        }
    }

    public void addDocumento(DocumentoProcesso documentoProcesso) {
        documentoProcesso.setProcesso(selecionado);
        selecionado.getDocumentoProcesso().add(documentoProcesso);
    }

    @Override
    public void salvar() {
        try {
            validarCamposProcesso();
            validarCampos();
            gerarSenhaConsulta();
            atribuirDadosProcesso();
            selecionado = processoFacade.salvarArquivos(selecionado);
            selecionado = processoFacade.salvarProcessoProtocolo(selecionado);
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.redirecionamentoInterno(getUrlAtual());
            descobrirETratarExceptionProcesso(e);
        }
    }

    private void atribuirDadosProcesso() {
        if (selecionado.getSituacao() == null) {
            selecionado.setSituacao(SituacaoProcesso.GERADO);
        }
        selecionado.setProtocolo(Boolean.FALSE);
        selecionado.setDataRegistro(new Date());
        selecionado.setAssunto(assunto.getNome());
    }

    private void gerarSenhaConsulta() {
        if (selecionado.getSenha() == null) {
            int valor;
            String valorStr = "";
            for (int i = 0; i < 5; i++) {
                valor = 1 + (int) (Math.random() * 9);
                valorStr = valorStr + valor;
            }
            selecionado.setSenha(Integer.parseInt(valorStr));
        }
    }


    @URLAction(mappingId = "editarProcesso", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        try {
            operacao = Operacoes.EDITAR;
            documento = new DocumentoProcesso();
            englobado = null;
            assunto = new Assunto();
            if (selecionado.getSubAssunto() != null) {
                assunto = selecionado.getSubAssunto().getAssunto();
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    @URLAction(mappingId = "verProcesso", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        consulta = false;
        Collections.sort(selecionado.getRotas());
    }

    @URLAction(mappingId = "verConsultaProcesso", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verConsulta() {
        super.ver();
        consulta = true;
        Collections.sort(selecionado.getRotas());
    }

    public List<UnidadeOrganizacional> completaUnidade(String parte) {
        return processoFacade.getUnidadeOrganizacionalFacade().listaFiltrando(parte.trim(), "descricao");
    }

    public Converter getConverterUsuarioSistema() {
        if (converterUsuario == null) {
            converterUsuario = new ConverterAutoComplete(UsuarioSistema.class, processoFacade.getUsuarioSistemaFacade());
        }
        return converterUsuario;
    }

    public DocumentoProcesso getDocumento() {
        return documento;
    }

    public void setDocumento(DocumentoProcesso documento) {
        this.documento = documento;
    }

    public Processo getProcessoSelecionado() {
        return processoSelecionado;
    }

    public void setProcessoSelecionado(Processo processoSelecionado) {
        this.processoSelecionado = processoSelecionado;
    }

    public void imprimeCapaProcesso() {
        try {
            processoFacade.geraDocumento(TipoDocumentoProtocolo.CAPA_PROCESSO, selecionado, null, selecionado.getPessoa());
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
    }

    public void imprimeParecerProcesso(Tramite t) {
        try {
            processoFacade.geraDocumento(TipoDocumentoProtocolo.TRAMITE_PROCESSO, selecionado, t, selecionado.getPessoa());
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
    }

    public List<SelectItem> getTiposPrazo() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (TipoPrazo object : TipoPrazo.values()) {
            if (object.equals(TipoPrazo.DIAS) || object.equals(TipoPrazo.HORAS)) {
                toReturn.add(new SelectItem(object, object.getDescricao()));
            }
        }
        return toReturn;
    }

    public List<HierarquiaOrganizacional> completaHierarquiaOrganizacional(String parte) {
        return processoFacade.getHierarquiaOrganizacionalFacade().filtrandoHierarquiaOrganizacionalAdministrativa(parte.trim());
    }

    public Converter getConverterHierarquiaOrganizacional() {
        if (converterHierarquiaOrganizacional == null) {
            converterHierarquiaOrganizacional = new ConverterAutoComplete(HierarquiaOrganizacional.class, processoFacade.getHierarquiaOrganizacionalFacade());
        }
        return converterHierarquiaOrganizacional;
    }

    public void removeArquivo(ActionEvent evento) {
        ProcessoArquivo arq = (ProcessoArquivo) evento.getComponent().getAttributes().get("objeto");
        selecionado.getArquivos().remove(arq);
    }

    public void uploadArquivo(FileUploadEvent file) {
        try {
            Arquivo arq = new Arquivo();
            arq.setNome(file.getFile().getFileName());
            arq.setMimeType(processoFacade.getArquivoFacade().getMimeType(file.getFile().getFileName()));
            arq.setDescricao(new Date().toString());
            arq.setTamanho(file.getFile().getSize());

            ProcessoArquivo proArq = new ProcessoArquivo();
            proArq.setArquivo(processoFacade.getArquivoFacade().novoArquivoMemoria(arq, file.getFile().getInputstream()));
            proArq.setProcesso(selecionado);

            selecionado.getArquivos().add(proArq);
        } catch (Exception ex) {
            logger.error("Erro: ", ex);
        }
    }

    public boolean podeEditar() {
        if (podeEditar == null) {
            if (processoFacade.getUsuarioSistemaFacade().isGestorProtocolo(processoFacade.getSistemaFacade().getUsuarioCorrente(), selecionado.getUoCadastro(), selecionado.getDataRegistro())) {
                podeEditar = true;
            }
            if (!SituacaoProcesso.GERADO.equals(selecionado.getSituacao())) {
                podeEditar = false;
            }
        }
        return podeEditar;
    }

    public boolean podeFazerDownload() {
        if (podeFazerDownload == null) {
            podeFazerDownload = processoFacade.getUsuarioSistemaFacade().isGestorProtocolo(processoFacade.getSistemaFacade().getUsuarioCorrente(), selecionado.getUoCadastro(), selecionado.getDataRegistro());
            for (Tramite tra : selecionado.getTramites()) {
                UnidadeOrganizacional unidade = tra.getProcesso().isProcessoInterno() ? tra.getUnidadeOrganizacional() : tra.getProcesso().getUoCadastro();
                if (processoFacade.getUsuarioSistemaFacade().isGestorProtocolo(processoFacade.getSistemaFacade().getUsuarioCorrente(), unidade, selecionado.getDataRegistro())) {
                    podeFazerDownload = true;
                }
            }
        }
        return podeFazerDownload;
    }

    public void listaProcessosGerados() {
        FiltroListaProtocolo filtro = new FiltroListaProtocolo(numeroPesquisaGerado, anoPesquisaGerado, solicitantePesquisaGerado, getSistemaControlador().getUsuarioCorrente());
        filtro.setUnidadeOrganizacional(getSistemaControlador().getUnidadeOrganizacionalAdministrativaCorrente());
        filtro.setGestor(true);
        filtro.setConfidencial(true);
        filtro.setProtocolo(false);
        listaConsultaProcessoGerados = processoFacade.buscarProcessosGerados(filtro, inicioConsultaProcessoGerados, maximoRegistrosTabela);
    }

    public void buscarProcessosFinalizados() {
        FiltroListaProtocolo filtro = new FiltroListaProtocolo(numeroPesquisaFinalizado, anoPesquisaFinalizado, solicitantePesquisaFinalizado, getSistemaControlador().getUsuarioCorrente());
        filtro.setGestor(true);
        filtro.setProtocolo(false);
        listaConsultaProcessoFinalizado = processoFacade.buscarProcessosFinalizados(filtro, inicioConsultaProcessoFinalizado, maximoRegistrosTabela);
    }

    public void buscarProcessosArquivados() {
        FiltroListaProtocolo filtro = new FiltroListaProtocolo(numeroPesquisaArquivado, anoPesquisaArquivado, solicitantePesquisaArquivado, getSistemaControlador().getUsuarioCorrente());
        filtro.setProtocolo(false);
        listaConsultaProcessoArquivado = processoFacade.buscarProcessosArquivados(filtro, inicioConsultaProcessoArquivado, maximoRegistrosTabela);
    }

    public void listaProcessosEncaminhados() {
        FiltroListaProtocolo filtro = new FiltroListaProtocolo(numeroPesquisaEncaminhado, anoPesquisaEncaminhado, solicitantePesquisaEncaminhado, getSistemaControlador().getUsuarioCorrente());
        filtro.setProtocolo(false);
        filtro.setGestor(true);
        filtro.setDestinoExterno(null);
        filtro.setUnidadeOrganizacional(null);

        listaConsultaProcessoEncaminhado = processoFacade.buscarProcessosProtocoloEncaminhados(
            filtro,
            inicioConsultaProcessoEncaminhado,
            maximoRegistrosTabela);
    }

    public void listarTramitePendenteProcesso() {
        FiltroListaProtocolo filtro = new FiltroListaProtocolo("", "", "", processoFacade.getSistemaFacade().getUsuarioCorrente());
        filtro.setGestor(true);
        filtro.setProtocolo(false);
        listaTramitePendenteProcesso = processoFacade.getTramiteFacade().buscarTramitesPendentesUnidadeProcessoProtocolo(
            filtro,
            inicioConsultaTramitePendente,
            maximoRegistrosTabela);
    }

    public void listarTramitesRecebidosProcesso() {
        FiltroListaProtocolo filtro = new FiltroListaProtocolo("", "", "", getSistemaControlador().getUsuarioCorrente());
        filtro.setStatus(true);
        filtro.setGestor(true);
        filtro.setProtocolo(false);
        filtro.setAceito(true);
        listaTramitesRecebidosProcesso = processoFacade.getTramiteFacade().buscarProtocoloAceitoRecebidoPorUsuario(
            filtro,
            inicioConsultaTramitesRecebidos,
            maximoRegistrosTabela);
    }

    public void alterarTabs(TabChangeEvent evt) {
        int tab = evt.getTab().getFacetCount();
        switch (tab) {
            case 0: {
                listaProcessosGerados();
                break;
            }
            case 1: {
                listarTramitePendenteProcesso();
                listarTramitesRecebidosProcesso();
                break;
            }
            case 2: {
                listaProcessosEncaminhados();
                break;
            }
            case 3: {
                buscarProcessosArquivados();
                break;
            }
            case 4: {
                buscarProcessosFinalizados();
                break;
            }
        }
    }

    @URLAction(mappingId = "listarProcesso", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limpaPesquisa() {
        numeroPesquisaGerado = "";
        Calendar cal = Calendar.getInstance();
        anoPesquisaGerado = cal.get(Calendar.YEAR) + "";
        solicitantePesquisaGerado = "";

        numeroPesquisaEncaminhado = "";
        anoPesquisaEncaminhado = cal.get(Calendar.YEAR) + "";
        solicitantePesquisaEncaminhado = "";

        numeroPesquisaArquivado = "";
        anoPesquisaArquivado = cal.get(Calendar.YEAR) + "";
        solicitantePesquisaArquivado = "";

        numeroPesquisaFinalizado = "";
        anoPesquisaFinalizado = cal.get(Calendar.YEAR) + "";
        solicitantePesquisaFinalizado = "";
    }

    public void validaCPFCNPJPessoa() {
        Pessoa pes = selecionado.getPessoa();
        if (pes instanceof PessoaFisica) {
            if (!valida_CpfCnpj(((PessoaFisica) pes).getCpf())) {
                RequestContext.getCurrentInstance().execute("confirmEditarPessoaFisica.show()");
            } else if (processoFacade.getPessoaFacade().hasOutraPessoaComMesmoCpf((PessoaFisica) pes, true)) {
                FacesUtil.addWarn("Atenção!", "O CPF da pessoa selecionada pertence a mais de uma pessoa física!");
                selecionado.setPessoa(null);
            }
        } else {
            if (!valida_CpfCnpj(((PessoaJuridica) pes).getCnpj())) {
                RequestContext.getCurrentInstance().execute("confirmEditarPessoaJuridica.show()");
            } else if (processoFacade.getPessoaFacade().hasOutraPessoaComMesmoCnpj((PessoaJuridica) pes, true)) {
                FacesUtil.addWarn("Atenção!", "O CNPJ da pessoa selecionada pertence a mais de uma pessoa jurídica!");
                selecionado.setPessoa(null);
            }
        }
    }

    public boolean valida_CpfCnpj(String s_aux) {
        if (s_aux == null) {
            return false;
        }
        s_aux = s_aux.replace(".", "");
        s_aux = s_aux.replace("-", "");
        s_aux = s_aux.replace("/", "");
//------- Rotina para CPF
        if (s_aux.length() == 11) {
            if (Sets.newHashSet("00000000000", "11111111111", "999999999").contains(s_aux)) {
                return false;
            }
            int d1, d2;
            int digito1, digito2, resto;
            int digitoCPF;
            String nDigResult;
            d1 = d2 = 0;
            digito1 = digito2 = resto = 0;
            for (int n_Count = 1; n_Count < s_aux.length() - 1; n_Count++) {
                digitoCPF = Integer.valueOf(s_aux.substring(n_Count - 1, n_Count)).intValue();
//--------- Multiplique a ultima casa por 2 a seguinte por 3 a seguinte por 4 e assim por diante.
                d1 = d1 + (11 - n_Count) * digitoCPF;
//--------- Para o segundo digito repita o procedimento incluindo o primeiro digito calculado no passo anterior.
                d2 = d2 + (12 - n_Count) * digitoCPF;
            }
            ;
//--------- Primeiro resto da divisão por 11.
            resto = (d1 % 11);
//--------- Se o resultado for 0 ou 1 o digito é 0 caso contrário o digito é 11 menos o resultado anterior.
            if (resto < 2) {
                digito1 = 0;
            } else {
                digito1 = 11 - resto;
            }
            d2 += 2 * digito1;
//--------- Segundo resto da divisão por 11.
            resto = (d2 % 11);
//--------- Se o resultado for 0 ou 1 o digito é 0 caso contrário o digito é 11 menos o resultado anterior.
            if (resto < 2) {
                digito2 = 0;
            } else {
                digito2 = 11 - resto;
            }
//--------- Digito verificador do CPF que está sendo validado.
            String nDigVerific = s_aux.substring(s_aux.length() - 2, s_aux.length());
//--------- Concatenando o primeiro resto com o segundo.
            nDigResult = String.valueOf(digito1) + String.valueOf(digito2);
//--------- Comparar o digito verificador do cpf com o primeiro resto + o segundo resto.
            return nDigVerific.equals(nDigResult);
        } //-------- Rotina para CNPJ
        else if (s_aux.length() == 14) {
            if (Sets.newHashSet("00000000000000", "11111111111111", "999999999999").contains(s_aux)) {
                return false;
            }
            int soma = 0, dig;
            String cnpj_calc = s_aux.substring(0, 12);
            char[] chr_cnpj = s_aux.toCharArray();
//--------- Primeira parte
            for (int i = 0; i < 4; i++) {
                if (chr_cnpj[i] - 48 >= 0 && chr_cnpj[i] - 48 <= 9) {
                    soma += (chr_cnpj[i] - 48) * (6 - (i + 1));
                }
            }
            for (int i = 0; i < 8; i++) {
                if (chr_cnpj[i + 4] - 48 >= 0 && chr_cnpj[i + 4] - 48 <= 9) {
                    soma += (chr_cnpj[i + 4] - 48) * (10 - (i + 1));
                }
            }
            dig = 11 - (soma % 11);
            cnpj_calc += (dig == 10 || dig == 11) ? "0" : Integer.toString(dig);
//--------- Segunda parte
            soma = 0;
            for (int i = 0; i < 5; i++) {
                if (chr_cnpj[i] - 48 >= 0 && chr_cnpj[i] - 48 <= 9) {
                    soma += (chr_cnpj[i] - 48) * (7 - (i + 1));
                }
            }
            for (int i = 0; i < 8; i++) {
                if (chr_cnpj[i + 5] - 48 >= 0 && chr_cnpj[i + 5] - 48 <= 9) {
                    soma += (chr_cnpj[i + 5] - 48) * (10 - (i + 1));
                }
            }
            dig = 11 - (soma % 11);
            cnpj_calc += (dig == 10 || dig == 11) ? "0" : Integer.toString(dig);
            return s_aux.equals(cnpj_calc);
        } else {
            return false;
        }
    }

    public void gerarGuiaRecebimentoProcesso() {
        ProtocoloImpressao guia = new ProtocoloImpressao();
        try {
            guia.gerarGuiaRecebimentoProcesso(selecionado);
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            logger.error("Erro: ", ex);
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public void gerarGuiaRecebimento(Tramite tra) {
        ProtocoloImpressao guia = new ProtocoloImpressao();
        try {
            Boolean encMult = false;
            if (tra.getEncaminhamentoMultiplo() != null) {
                encMult = tra.getEncaminhamentoMultiplo();
            }
            guia.gerarGuiaRecebimentoTramite(tra, encMult);
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            logger.error("Erro: ", ex);
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public void imprimirListaPareceres() {
        ProtocoloImpressao guia = new ProtocoloImpressao();
        try {
            guia.geraListaParecerProtocoloProcesso(selecionado);
        } catch (Exception ex) {
            logger.error("Erro: ", ex);
        }
    }

    public String descricaoPrimeiroTramite(Processo processo) {
        Processo p = processoFacade.recuperar(processo.getId());
        if (!p.getTramites().isEmpty()) {
            Tramite t = p.getTramites().get(0);
            return t.getDestinoTramite();
        }
        return "";
    }

    public Tramite ultimoTramiteProcesso(Processo p) {
        p = processoFacade.recuperar(p.getId());
        if (p.getTramites() != null && p.getTramites().size() > 0) {
            return p.getTramites().get(p.getTramites().size() - 1);
        }
        return null;
    }

    public List<SelectItem> getSituacaoProcesso() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, " "));
        for (SituacaoProcesso object : SituacaoProcesso.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/processo/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public void novoSubAssunto() {
        Web.navegacao(getCaminhoPadrao() + "novo/",
            "/subassunto/novo/",
            selecionado);
    }

    public void novoAssunto() {
        Web.navegacao(getCaminhoPadrao() + "novo/",
            "/assunto/novo/",
            selecionado);
    }

    public void cancelarConsulta() {
        Web.navegacao(getCaminhoPadrao(),
            getCaminhoPadrao() + "consultar/");
    }

    public void novaPessoaFisica() {
        Web.navegacao(getCaminhoPadrao() + "novo/",
            "/tributario/configuracoes/pessoa/novapessoafisica/",
            selecionado);
    }

    public void novaPessoaJuridica() {
        Web.navegacao(getCaminhoPadrao() + "novo/",
            "/tributario/configuracoes/pessoa/novapessoajuridica/",
            selecionado);
    }

    public void alteraPessoaFisica() {
        Web.navegacao(getCaminhoPadrao() + "novo/",
            "/tributario/configuracoes/pessoa/editarpessoafisica/" + selecionado.getPessoa().getId() + "/",
            selecionado);
    }

    public void alteraPessoaJuridica() {
        Web.navegacao(getCaminhoPadrao() + "novo/",
            "/tributario/configuracoes/pessoa/editarpessoajuridica/" + selecionado.getPessoa().getId() + "/",
            selecionado);
    }

    public void proximosGerados() {
        inicioConsultaProcessoGerados += maximoRegistrosTabela;
        listaProcessosGerados();
    }

    public void proximosEncaminhado() {
        inicioConsultaProcessoEncaminhado += maximoRegistrosTabela;
        listaProcessosEncaminhados();
    }

    public void proximosArquivado() {
        inicioConsultaProcessoArquivado += maximoRegistrosTabela;
        buscarProcessosArquivados();
    }

    public void proximosFinalizado() {
        inicioConsultaProcessoFinalizado += maximoRegistrosTabela;
        buscarProcessosFinalizados();
    }

    public void proximosTramitePendente() {
        inicioConsultaTramitePendente += maximoRegistrosTabela;
        listarTramitePendenteProcesso();
    }

    public void proximosTramiteRecebidos() {
        inicioConsultaTramitesRecebidos += maximoRegistrosTabela;
        listarTramitesRecebidosProcesso();
    }

    public boolean isTemMaisResultadosGerados() {
        if (listaConsultaProcessoGerados == null) {
            listaProcessosGerados();
        }
        return listaConsultaProcessoGerados.size() >= maximoRegistrosTabela;
    }

    public boolean isTemMaisResultadosEncaminhado() {
        if (listaConsultaProcessoEncaminhado == null) {
            listaProcessosEncaminhados();
        }
        return listaConsultaProcessoEncaminhado.size() >= maximoRegistrosTabela;
    }

    public boolean isTemMaisResultadosArquivado() {
        if (listaConsultaProcessoArquivado == null) {
            buscarProcessosArquivados();
        }
        return listaConsultaProcessoArquivado.size() >= maximoRegistrosTabela;
    }

    public boolean isTemMaisResultadosFinalizado() {
        if (listaConsultaProcessoFinalizado == null) {
            buscarProcessosFinalizados();
        }
        return listaConsultaProcessoFinalizado.size() >= maximoRegistrosTabela;
    }

    public boolean isTemMaisResultadosTramitePendente() {
        if (listaTramitePendenteProcesso == null) {
            listarTramitePendenteProcesso();
        }
        return listaTramitePendenteProcesso.size() >= maximoRegistrosTabela;
    }

    public boolean isTemMaisResultadosTramiteRecebido() {
        if (listaTramitesRecebidosProcesso == null) {
            listarTramitesRecebidosProcesso();
        }
        return listaTramitesRecebidosProcesso.size() >= maximoRegistrosTabela;
    }

    public boolean isTemAnteriorGerados() {
        return inicioConsultaProcessoGerados > 0;
    }

    public boolean isTemAnteriorEncaminhado() {
        return inicioConsultaProcessoEncaminhado > 0;
    }

    public boolean isTemAnteriorArquivado() {
        return inicioConsultaProcessoArquivado > 0;
    }

    public boolean isTemAnteriorFinalizado() {
        return inicioConsultaProcessoFinalizado > 0;
    }

    public boolean isTemAnteriorTramitePendente() {
        return inicioConsultaTramitePendente > 0;
    }

    public boolean isTemAnteriorTramiteRecebido() {
        return inicioConsultaTramitesRecebidos > 0;
    }

    public void anterioresGerados() {
        inicioConsultaProcessoGerados -= maximoRegistrosTabela;
        if (inicioConsultaProcessoGerados < 0) {
            inicioConsultaProcessoGerados = 0;
        }
        listaProcessosGerados();
    }

    public void anterioresEncaminhado() {
        inicioConsultaProcessoEncaminhado -= maximoRegistrosTabela;
        if (inicioConsultaProcessoEncaminhado < 0) {
            inicioConsultaProcessoEncaminhado = 0;
        }
        listaProcessosEncaminhados();
    }

    public void anterioresArquivado() {
        inicioConsultaProcessoArquivado -= maximoRegistrosTabela;
        if (inicioConsultaProcessoArquivado < 0) {
            inicioConsultaProcessoArquivado = 0;
        }
        buscarProcessosArquivados();
    }

    public void anterioresFinalizado() {
        inicioConsultaProcessoFinalizado -= maximoRegistrosTabela;
        if (inicioConsultaProcessoFinalizado < 0) {
            inicioConsultaProcessoFinalizado = 0;
        }
        buscarProcessosFinalizados();
    }

    public void anterioresTramitePendente() {
        inicioConsultaTramitePendente -= maximoRegistrosTabela;
        if (inicioConsultaTramitePendente < 0) {
            inicioConsultaTramitePendente = 0;
        }
        listarTramitePendenteProcesso();
    }

    public void anterioresTramiteRecebido() {
        inicioConsultaTramitesRecebidos -= maximoRegistrosTabela;
        if (inicioConsultaTramitesRecebidos < 0) {
            inicioConsultaTramitesRecebidos = 0;
        }
        listarTramitesRecebidosProcesso();
    }

    public void descobrirETratarExceptionProcesso(Exception e) {
        try {
            Util.getRootCauseDataBaseException(e);
        } catch (SQLIntegrityConstraintViolationException sql) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), "Ocorreu um problema ao salvar o processo, tente cadastrar-lo novamente!");
        } catch (Exception throwable) {
            logger.error(throwable.getMessage());
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), "Não foi possível completar a operação solicitada. Verifique os passos executados e tente novamente. Se o problema persistir entre em contato com o suporte técnico.");
        }
    }

    public void adicionaEnglobado() {
        if (englobado != null && englobado.getId() != null) {
            ProcessoEnglobado pe = new ProcessoEnglobado();
            pe.setEnglobado(englobado);
            pe.setProcesso(selecionado);
            selecionado.getProcessosEnglobados().add(pe);
            englobado = null;
        } else {
            FacesUtil.addCampoObrigatorio("Informe o processo/protocolo englobado!");
        }
    }

    public void excluirEnglobado(ProcessoEnglobado processoEnglobado) {
        if (processoEnglobado != null) {
            selecionado.getProcessosEnglobados().remove(processoEnglobado);
        }
    }

    public List<SituacaoCadastralPessoa> getSituacoesDisponiveis() {
        return Lists.newArrayList(SituacaoCadastralPessoa.ATIVO);
    }

    public void poeNaSessao() throws IllegalAccessException {
        Web.poeNaSessao(selecionado);
        Web.poeTodosFieldsNaSessao(this);
    }

    public Boolean getPodeEditar() {
        return podeEditar;
    }

    public void setPodeEditar(Boolean podeEditar) {
        this.podeEditar = podeEditar;
    }

    public Boolean getPodeFazerDownload() {
        return podeFazerDownload;
    }

    public void setPodeFazerDownload(Boolean podeFazerDownload) {
        this.podeFazerDownload = podeFazerDownload;
    }
}
