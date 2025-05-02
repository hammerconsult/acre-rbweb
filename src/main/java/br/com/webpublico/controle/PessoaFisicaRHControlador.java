/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.concursos.ClassificacaoInscricao;
import br.com.webpublico.entidadesauxiliares.FileUploadPrimeFaces;
import br.com.webpublico.entidadesauxiliares.rh.AverbacaoContratoPorServico;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.rh.concursos.ClassificacaoConcursoFacade;
import br.com.webpublico.util.*;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.FlowEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlInputText;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.persistence.NoResultException;
import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Andre Peres
 */
@ManagedBean(name = "pessoaFisicaRHControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoPessoaRH", pattern = "/pessoa/novo/", viewId = "/faces/rh/administracaodepagamento/pessoarh/edita.xhtml"),
    @URLMapping(id = "editarPessoaRH", pattern = "/pessoa/editar/#{pessoaFisicaRHControlador.id}/", viewId = "/faces/rh/administracaodepagamento/pessoarh/edita.xhtml"),
    @URLMapping(id = "listarPessoaRH", pattern = "/pessoa/listar/", viewId = "/faces/rh/administracaodepagamento/pessoarh/lista.xhtml"),
    @URLMapping(id = "verPessoaRH", pattern = "/pessoa/ver/#{pessoaFisicaRHControlador.id}/", viewId = "/faces/rh/administracaodepagamento/pessoarh/visualizar.xhtml"),
    @URLMapping(id = "novoPessoaCandidatoConcurso", pattern = "/pessoa/novo/candidato-concurso/#{pessoaFisicaRHControlador.id}/", viewId = "/faces/rh/administracaodepagamento/pessoarh/edita.xhtml"),

    @URLMapping(id = "novaPessoaPrestador", pattern = "/pessoa-prestador/novo/", viewId = "/faces/rh/administracaodepagamento/pessoaprestador/edita.xhtml"),
    @URLMapping(id = "editarPessoaPrestador", pattern = "/pessoa-prestador/editar/#{pessoaFisicaRHControlador.id}/", viewId = "/faces/rh/administracaodepagamento/pessoaprestador/edita.xhtml"),
    @URLMapping(id = "listarPessoaPrestador", pattern = "/pessoa-prestador/listar/", viewId = "/faces/rh/administracaodepagamento/pessoaprestador/lista.xhtml"),
    @URLMapping(id = "verPessoaPrestador", pattern = "/pessoa-prestador/ver/#{pessoaFisicaRHControlador.id}/", viewId = "/faces/rh/administracaodepagamento/pessoaprestador/visualizar.xhtml")
})
public class PessoaFisicaRHControlador extends PrettyControlador<PessoaFisica> implements Serializable, CRUD {

    public static final int BRASILEIRO = 10;
    private static final Logger logger = LoggerFactory.getLogger(PessoaFisicaRHControlador.class);
    private static int DIGIT_COUNT = 11; //170.33259.50-4 numero do pis/pasep
    private final long NIVEL_SUPERIOR_ACIMA = 8L;
    protected ConverterGenerico converterNacionalidade;
    protected ConverterGenerico converterNacionalidadeConjuge;
    protected ConverterGenerico converterEscritorioContabil;
    protected ConverterAutoComplete converterNaturalidade;
    protected ConverterGenerico converterNivelEscolaridade;
    protected ConverterGenerico converterUf;
    protected ConverterAutoComplete converterAgencia;
    private String pessoaDaContaCorrente;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private NacionalidadeFacade nacionalidadeFacade;
    @EJB
    private CidadeFacade cidadeFacade;
    @EJB
    private EscritorioContabilFacade escritorioContabilFacade;
    @EJB
    private CidadeFacade naturalidadeFacade;
    @EJB
    private NivelEscolaridadeFacade nivelEscolaridadeFacade;
    @EJB
    private UFFacade ufFacade;
    @EJB
    private AgenciaFacade agenciaFacade;
    @EJB
    private ContaCorrenteBancPessoaFacade contaCorrenteBancPessoaFacade;
    @EJB
    private HierarquiaOrganizacionalFacadeOLD hierarquiaOrganizacionalFacade;
    @EJB
    private ContaCorrenteBancariaFacade contaCorrenteBancariaFacade;
    @EJB
    private ClasseCredorFacade classeCredorFacade;
    @EJB
    private ClassificacaoCredorFacade classificacaoCredorFacade;
    @EJB
    private ArquivoFacade arquivoFacade;
    @EJB
    private BancoFacade bancoFacade;
    @EJB
    private CIDFacade cidFacade;
    @EJB
    private ConselhoClasseOrdemFacade conselhoClasseOrdemFacade;
    @EJB
    private AreaFormacaoFacade areaFormacaoFacade;
    @EJB
    private HabilidadeFacade habilidadeFacade;
    @EJB
    private VinculoFPFacade vinculoFPFacade;
    @EJB
    private AverbacaoTempoServicoFacade averbacaoTempoServicoFacade;
    @EJB
    private ClassificacaoConcursoFacade classificacaoConcursoFacade;
    @EJB
    private PessoaFisicaFacade pessoaFisicaFacade;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private ConverterGenerico converterCidade;
    private CarteiraTrabalho carteiraTrabalho;
    private List<CarteiraTrabalho> carteiraTrabalhos;
    private List<RG> rgs;
    private RG rg;
    private DocumentoNacionalIdentidade documentoNacionalIdentidade;
    private Emancipacao emancipacao;
    private CarteiraVacinacao carteiraVacinacao;
    private TituloEleitor tituloEleitor;
    private List<TituloEleitor> tituloEleitores;
    private Habilitacao habilitacao;
    private List<Habilitacao> habilitacoes;
    private SituacaoMilitar situacaoMilitar;
    private List<SituacaoMilitar> situacaoMilitares;
    private CertidaoNascimento certidaoNascimento;
    private CertidaoCasamento certidaoCasamento;
    private Telefone telefoneSelecionado;
    private EnderecoCorreio endereco = new EnderecoCorreio();
    private ContaCorrenteBancPessoa contaCorrenteBancPessoaSelecionada;
    private List<ContaCorrenteBancaria> listaContas;
    private Endereco enderecoAux;
    private CEPLogradouro logradouro;
    private String complemento;
    private String numero;
    private HtmlInputText textocep;
    private List<CEPLogradouro> logradouros;
    private List<ContaCorrenteBancPessoa> listaExcluidos;
    private ConverterAutoComplete converterContaCorrenteBancaria;
    private HierarquiaOrganizacional unidadeOrganizacionalSelecionada;
    private ClasseCredorPessoa classeCredorSelecionada;
    private ConverterAutoComplete converterHierarquiaOrganizacional;
    private Arquivo arquivo;
    private StreamedContent imagemFoto;
    private ConverterAutoComplete converterBanco;
    private ConverterAutoComplete converterCidadeCasamento;
    private ConverterAutoComplete converterCid;
    private PessoaFisicaCid pessoaFisicaCid;
    private ConselhoClasseContratoFP conselho;
    private ConverterGenerico converterConselho;
    private int index;
    private boolean formacaoEscolarHabilitada;
    private Banco banco;
    private boolean contaConjunta;
    private PessoaFisicaRHControlador pessoaFisicaRHControlador = this;
    private MatriculaFormacao matriculaFormacao;
    private PessoaHabilidade pessoaHabilidade;
    private ClassificacaoInscricao classificacaoInscricaoSelecionada;
    private boolean skip;
    @EJB
    private DocumentoPessoalFacade documentoPessoalFacade;
    private List<ContaCorrenteBancPessoa> contasParaRemoverDoVinculoFP;
    private Map<Long, Boolean> averbacoesMap;
    private String caminhoPadrao;
    private PerfilEnum perfilSelecionado;

    public PessoaFisicaRHControlador() {
//        metadata = new EntidadeMetaData(selecionado.class);
        super(PessoaFisica.class);
    }

    @URLAction(mappingId = "novoPessoaCandidatoConcurso", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void criarPessoaDecorrenteDeCandidatoConcurso() {
        classificacaoInscricaoSelecionada = classificacaoConcursoFacade.buscarClassificacaoInscricao(getId());
        nova();
        transferirDadosDoCandidatoParaPessoaFisica();
        caminhoPadrao = "/pessoa/";
        perfilSelecionado = PerfilEnum.PERFIL_RH;
    }

    @URLAction(mappingId = "novoPessoaRH", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void nova() {
        novoFisicoRH();
        averbacoesMap = new HashMap<>();
        caminhoPadrao = "/pessoa/";
        perfilSelecionado = PerfilEnum.PERFIL_RH;
        if (isSessao()) {
            try {
                if (Web.isTodosFieldsNaSessao(this)) {
                    Web.pegaTodosFieldsNaSessao(this);
                    recuperaAtributosSessaoFormacao();
                }
            } catch (IllegalAccessException e) {
                logger.error(e.getMessage());
            }
        }

    }

    @URLAction(mappingId = "novaPessoaPrestador", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novaPessoaPrestador() {
        novoFisicoPrestador();
        averbacoesMap = new HashMap<>();
        caminhoPadrao = "/pessoa-prestador/";
        perfilSelecionado = PerfilEnum.PERFIL_PRESTADOR;
        if (isSessao()) {
            try {
                if (Web.isTodosFieldsNaSessao(this)) {
                    Web.pegaTodosFieldsNaSessao(this);
                    recuperaAtributosSessaoFormacao();
                }
            } catch (IllegalAccessException e) {
                logger.error(e.getMessage());
            }
        }
    }

    @URLAction(mappingId = "editarPessoaPrestador", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editarPrestador() {
        caminhoPadrao = "/pessoa-prestador/";
        perfilSelecionado = PerfilEnum.PERFIL_PRESTADOR;
        editar();
    }

    @URLAction(mappingId = "editarPessoaRH", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editarPessoaRH() {
        caminhoPadrao = "/pessoa/";
        perfilSelecionado = PerfilEnum.PERFIL_RH;
        editar();
    }

    @Override
    public void editar() {
        Long idPessoa = getId();
        definirSessao();
        contasParaRemoverDoVinculoFP = Lists.newArrayList();
        averbacoesMap = new HashMap<>();
        if (isSessao()) {
            try {
                if (Web.isTodosFieldsNaSessao(this)) {
                    Web.pegaTodosFieldsNaSessao(this);
                    recuperaAtributosSessaoFormacao();
                } else {
                    setId(idPessoa);
                    selecionarPessoa();
                    matriculaFormacao = new MatriculaFormacao();
                    pessoaHabilidade = new PessoaHabilidade();
                }
            } catch (IllegalAccessException e) {
                logger.error(e.getMessage());
            }
        } else {
            selecionarPessoa();
            matriculaFormacao = new MatriculaFormacao();
            pessoaHabilidade = new PessoaHabilidade();
        }
    }

    public DocumentoNacionalIdentidade getDocumentoNacionalIdentidade() {
        return documentoNacionalIdentidade;
    }

    public void setDocumentoNacionalIdentidade(DocumentoNacionalIdentidade documentoNacionalIdentidade) {
        this.documentoNacionalIdentidade = documentoNacionalIdentidade;
    }

    @URLAction(mappingId = "verPessoaRH", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        caminhoPadrao = "/pessoa/";
        perfilSelecionado = PerfilEnum.PERFIL_RH;
        selecionarPessoa();
        averbacoesMap = new HashMap<>();
    }

    @URLAction(mappingId = "verPessoaPrestador", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verPrestador() {
        caminhoPadrao = "/pessoa-prestador/";
        perfilSelecionado = PerfilEnum.PERFIL_PRESTADOR;
        selecionarPessoa();
        averbacoesMap = new HashMap<>();
    }

    public void selecionarPessoa() {
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("imagem-foto", null);
        selecionar(null);
    }

    public void novoFisicoRH() {
        selecionado = new PessoaFisica();
        selecionado.getPerfis().add(PerfilEnum.PERFIL_RH);
        selecionado.getPerfis().add(PerfilEnum.PERFIL_DEPENDENTE);
        selecionado.getPerfis().add(PerfilEnum.PERFIL_PENSIONISTA);
        novoFisico();
    }

    public void novoFisicoPrestador() {
        selecionado = new PessoaFisica();
        selecionado.getPerfis().add(PerfilEnum.PERFIL_PRESTADOR);
        novoFisico();
    }

    public void novoFisico() {
        imagemFoto = null;
        operacao = Operacoes.NOVO;
        selecionado.setTelefones(new ArrayList<Telefone>());
        selecionado.setEnderecos(new ArrayList<EnderecoCorreio>());
        selecionado.setSituacaoCadastralPessoa(SituacaoCadastralPessoa.ATIVO);
        endereco = new EnderecoCorreio();
        rg = new RG();
        rg.setDataRegistro(SistemaFacade.getDataCorrente());
        rg.setPessoaFisica(selecionado);
        documentoNacionalIdentidade = new DocumentoNacionalIdentidade();
        documentoNacionalIdentidade.setDataExpedicao(SistemaFacade.getDataCorrente());
        documentoNacionalIdentidade.setPessoaFisica(selecionado);
        emancipacao = new Emancipacao();
        emancipacao.setDataRegistro(SistemaFacade.getDataCorrente());
        emancipacao.setPessoaFisica(selecionado);
        tituloEleitor = new TituloEleitor();
        tituloEleitor.setDataRegistro(sistemaControlador.getDataOperacao());
        tituloEleitor.setPessoaFisica(selecionado);
        carteiraTrabalho = new CarteiraTrabalho();
        carteiraTrabalhos = new ArrayList<>();
        habilitacao = new Habilitacao();
        habilitacoes = new ArrayList<>();
        situacaoMilitar = new SituacaoMilitar();
        situacaoMilitar.setDataRegistro(sistemaControlador.getDataOperacao());
        situacaoMilitar.setPessoaFisica(selecionado);
        certidaoNascimento = new CertidaoNascimento();
        certidaoNascimento.setDataRegistro(sistemaControlador.getDataOperacao());
        certidaoNascimento.setPessoaFisica(selecionado);
        certidaoCasamento = new CertidaoCasamento();
        certidaoCasamento.setDataRegistro(sistemaControlador.getDataOperacao());
        certidaoCasamento.setPessoaFisica(selecionado);
        certidaoCasamento.setNacionalidade(getNacionalidadeBrasileira());
        selecionado.setContaCorrenteBancPessoas(new ArrayList<ContaCorrenteBancPessoa>());
        listaExcluidos = new ArrayList<>();
        tituloEleitores = new ArrayList<>();
        unidadeOrganizacionalSelecionada = new HierarquiaOrganizacional();
        classeCredorSelecionada = new ClasseCredorPessoa();
        arquivo = new Arquivo();
        carteiraVacinacao = new CarteiraVacinacao();
        carteiraVacinacao.setDataRegistro(sistemaControlador.getDataOperacao());
        carteiraVacinacao.setPessoaFisica(selecionado);
        addDocumentoPessoa(carteiraVacinacao);
        pessoaFisicaCid = new PessoaFisicaCid();
        conselho = new ConselhoClasseContratoFP();
        index = 0;
        matriculaFormacao = new MatriculaFormacao();
        pessoaHabilidade = new PessoaHabilidade();
        formacaoEscolarHabilitada = false;
        selecionado.setNacionalidade(getNacionalidadeBrasileira());
        selecionado.setNacionalidadePai(getNacionalidadeBrasileira());
        selecionado.setNacionalidadeMae(getNacionalidadeBrasileira());
        selecionado.setNivelEscolaridade(nivelEscolaridadeFacade.lista().get(0));
        selecionado.setViveUniaoEstavel(Boolean.FALSE);
        contasParaRemoverDoVinculoFP = Lists.newArrayList();
        definirSessao();
        carregarDaSessao();
        try {
            if (selecionado.getFileUploadPrimeFaces() != null && selecionado.getFileUploadPrimeFaces().getFileUploadEvent() != null) {
                imagemFoto = new DefaultStreamedContent(selecionado.getFileUploadPrimeFaces().getFileUploadEvent().getFile().getInputstream(), "image/png", selecionado.getFileUploadPrimeFaces().getFileUploadEvent().getFile().getFileName());
            } else {
                carregaFoto();
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("imagem-foto", imagemFoto);
    }

    @Override
    public void carregarDaSessao() {
        PessoaFisicaRHControlador p = (PessoaFisicaRHControlador) Web.pegaDaSessao(PessoaFisicaRHControlador.class);
        if (!isSessao() || p == null) {
            return;
        }
        pessoaFisicaRHControlador = p;
        definirAtributos(p);
    }

    public void definirAtributos(PessoaFisicaRHControlador controler) {
        selecionado = controler.getSelecionado();
        rg = controler.rg;
        emancipacao = controler.emancipacao;
        tituloEleitor = controler.tituloEleitor;
        habilitacao = controler.habilitacao;
        habilitacoes = controler.habilitacoes;
        tituloEleitores = controler.tituloEleitores;
        carteiraTrabalho = controler.carteiraTrabalho;
        carteiraTrabalhos = controler.carteiraTrabalhos;
        situacaoMilitar = controler.situacaoMilitar;
        certidaoNascimento = controler.certidaoNascimento;
        certidaoCasamento = controler.certidaoCasamento;
        endereco = controler.endereco;
        contaConjunta = controler.contaConjunta;
        conselho = controler.conselho;
        carteiraVacinacao = controler.carteiraVacinacao;
        operacao = controler.operacao;
        index = controler.index;
        banco = controler.banco;
        conselho = controler.conselho;
        formacaoEscolarHabilitada = controler.formacaoEscolarHabilitada;
    }

    public boolean isFormacaoEscolarHabilitada() {
        return formacaoEscolarHabilitada;
    }

    public Boolean selecionadoPossuiDeficienciaFisica() {
        try {
            return esteSelecionado().possuiDeficienciaFisica();
        } catch (Exception e) {
            return Boolean.FALSE;
        }
    }

    public PessoaFisicaCid getPessoaFisicaCid() {
        return pessoaFisicaCid;
    }

    public void setPessoaFisicaCid(PessoaFisicaCid pfCid) {
        pessoaFisicaCid = pfCid;
    }

    public ConverterAutoComplete getConverterCid() {
        if (converterCid == null) {
            converterCid = new ConverterAutoComplete(CID.class, cidFacade);
        }
        return converterCid;
    }

    public List<CID> completarCid(String parte) {
        return cidFacade.listaFiltrando(parte.trim(), 9999, "descricao", "codigoDaCid");
    }

    public PessoaFisica esteSelecionado() {
        return selecionado;
    }

    public Converter getConverterHierarquiaOrganizacional() {
        if (converterHierarquiaOrganizacional == null) {
            converterHierarquiaOrganizacional = new ConverterAutoComplete(HierarquiaOrganizacional.class, hierarquiaOrganizacionalFacade);
        }
        return converterHierarquiaOrganizacional;
    }

    public List<HierarquiaOrganizacional> completaHierarquiaOrganizacional(String parte) {
        boolean temUnidade = false;
        List<HierarquiaOrganizacional> lista = hierarquiaOrganizacionalFacade.filtraPorNivel(parte, "3", TipoHierarquiaOrganizacional.ORCAMENTARIA, sistemaControlador.getExercicioCorrente());
        if (unidadeOrganizacionalSelecionada != null) {
            for (HierarquiaOrganizacional h : lista) {
                if (h.getSubordinada().equals(unidadeOrganizacionalSelecionada.getSubordinada())) {
                    temUnidade = true;
                }
            }
            if (temUnidade == true) {
                return lista;
            } else {
                String str = unidadeOrganizacionalSelecionada.getSubordinada().getDescricao() + unidadeOrganizacionalSelecionada.getCodigo();
                if (str.toLowerCase().contains(parte.toLowerCase())) {
                    lista.add(unidadeOrganizacionalSelecionada);
                }
                return lista;
            }
        } else {
            return lista;
        }
    }

    public Boolean liberaEnderecoPrincipal() {
        Boolean controle = false;
        if (selecionado != null) {
            for (EnderecoCorreio ec : selecionado.getEnderecos()) {
                if (ec.getPrincipal() != null && ec.getPrincipal()) {
                    controle = true;
                }
            }
        }
        return controle;
    }

    public CarteiraVacinacao getCarteiraVacinacao() {
        return carteiraVacinacao;
    }

    public void setCarteiraVacinacao(CarteiraVacinacao carteiraVacinacao) {
        this.carteiraVacinacao = carteiraVacinacao;
    }

    public void addDocumentoPessoa(DocumentoPessoal dp) {
        PessoaFisica pf = selecionado;
        pf.getDocumentosPessoais().add(dp);
    }

    public SituacaoMilitar getSituacaoMilitar() {
        return situacaoMilitar;
    }

    public void setSituacaoMilitar(SituacaoMilitar situacaoMilitar) {
        this.situacaoMilitar = situacaoMilitar;
    }

    public Habilitacao getHabilitacao() {
        return habilitacao;
    }

    public void setHabilitacao(Habilitacao habilitacao) {
        this.habilitacao = habilitacao;
    }

    public CarteiraTrabalho getCarteiraTrabalho() {
        return carteiraTrabalho;
    }

    public void setCarteiraTrabalho(CarteiraTrabalho carteiraTrabalho) {
        this.carteiraTrabalho = carteiraTrabalho;
    }

    public TituloEleitor getTituloEleitor() {
        return tituloEleitor;
    }

    public void setTituloEleitor(TituloEleitor tituloEleitor) {
        this.tituloEleitor = tituloEleitor;
    }

    public Telefone getTelefoneSelecionado() {
        return telefoneSelecionado;
    }

    public void setTelefoneSelecionado(Telefone telefoneSelecionado) {
        this.telefoneSelecionado = telefoneSelecionado;
    }

    public ContaCorrenteBancPessoa getContaCorrenteBancPessoaSelecionada() {
        return contaCorrenteBancPessoaSelecionada;
    }

    public void setContaCorrenteBancPessoaSelecionada(ContaCorrenteBancPessoa contaCorrenteBancPessoaSelecionada) {
        this.contaCorrenteBancPessoaSelecionada = contaCorrenteBancPessoaSelecionada;
    }

    public EnderecoCorreio getEndereco() {
        return endereco;
    }

    public void setEndereco(EnderecoCorreio endereco) {
        this.endereco = endereco;
    }

    public List<TituloEleitor> getTituloEleitores() {
        return tituloEleitores;
    }

    public void setTituloEleitores(List<TituloEleitor> tituloEleitores) {
        this.tituloEleitores = tituloEleitores;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public CEPLogradouro getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(CEPLogradouro logradouro) {
        this.logradouro = logradouro;
    }

    public List<CEPLogradouro> getLogradouros() {
        return logradouros;
    }

    public void setLogradouros(List<CEPLogradouro> logradouros) {
        this.logradouros = logradouros;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public HtmlInputText getTextocep() {
        return textocep;
    }

    public void setTextocep(HtmlInputText textocep) {
        this.textocep = textocep;
    }

    public String getPessoaDaContaCorrente() {
        return pessoaDaContaCorrente;
    }

    public void setPessoaDaContaCorrente(String pessoaDaContaCorrente) {
        this.pessoaDaContaCorrente = pessoaDaContaCorrente;
    }

    public Banco getBanco() {
        return banco;
    }

    public void setBanco(Banco banco) {
        this.banco = banco;
    }

    public boolean isContaConjunta() {
        return contaConjunta;
    }

    public List<SelectItem> getTiposEnderecos() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (TipoEndereco t : TipoEndereco.values()) {
            toReturn.add(new SelectItem(t, t.getDescricao()));
        }
        return toReturn;
    }

    public List<Habilitacao> getHabilitacoes() {
        return habilitacoes;
    }

    public void setHabilitacoes(List<Habilitacao> habilitacoes) {
        this.habilitacoes = habilitacoes;
    }

    public List<CarteiraTrabalho> getCarteiraTrabalhos() {
        return carteiraTrabalhos;
    }

    public void setCarteiraTrabalhos(List<CarteiraTrabalho> carteiraTrabalhos) {
        this.carteiraTrabalhos = carteiraTrabalhos;
    }

    public List<SituacaoMilitar> getSituacaoMilitares() {
        return situacaoMilitares;
    }

    public void setSituacaoMilitares(List<SituacaoMilitar> situacaoMilitares) {
        this.situacaoMilitares = situacaoMilitares;
    }

    public Endereco getEnderecoAux() {
        return enderecoAux;
    }

    public void setEnderecoAux(Endereco enderecoAux) {
        this.enderecoAux = enderecoAux;
    }

    public List<Telefone> getTelefones() {
        return selecionado.getTelefones();
    }

    public List<EnderecoCorreio> getEnderecos() {
        return selecionado.getEnderecos();
    }

    public void novoTelefoneFone() {
        telefoneSelecionado = new Telefone();
        telefoneSelecionado.setPessoa(selecionado);
    }

    public void novoRg() {
        if (rg != null && !rg.getNumero().isEmpty()) {
            PessoaFisica pf = selecionado;
            rg.setDataRegistro(sistemaControlador.getDataOperacao());
            rg.setPessoaFisica(pf);
            if (pf.getDocumentosPessoais().contains(rg)) {
                pf.getDocumentosPessoais().set(pf.getDocumentosPessoais().indexOf(rg), rg);
            } else {
                pf.getDocumentosPessoais().add(rg);
            }
        }
    }

    private void novoDocumentoNacionalIdentidade() {
        if (documentoNacionalIdentidade != null && !documentoNacionalIdentidade.getDni().isEmpty()) {
            documentoNacionalIdentidade.setDataRegistro(sistemaControlador.getDataOperacao());
            documentoNacionalIdentidade.setPessoaFisica(selecionado);
            if (selecionado.getDocumentosPessoais().contains(documentoNacionalIdentidade)) {
                selecionado.getDocumentosPessoais().set(selecionado.getDocumentosPessoais().indexOf(documentoNacionalIdentidade), documentoNacionalIdentidade);
            } else {
                selecionado.getDocumentosPessoais().add(documentoNacionalIdentidade);
            }
        }
    }

    public void novoEmancipacao() {
        if (emancipacao != null && emancipacao.getDetentorArquivoComposicao() != null && emancipacao.getDetentorArquivoComposicao().getArquivoComposicao() != null) {
            PessoaFisica pf = selecionado;
            emancipacao.setDataRegistro(sistemaControlador.getDataOperacao());
            emancipacao.setPessoaFisica(pf);
            if (pf.getDocumentosPessoais().contains(emancipacao)) {
                pf.getDocumentosPessoais().set(pf.getDocumentosPessoais().indexOf(emancipacao), emancipacao);
            } else {
                pf.getDocumentosPessoais().add(emancipacao);
            }
        }
    }

    public void removeRg(ActionEvent evento) {
        PessoaFisica pf = selecionado;
        pf.getDocumentosPessoais().remove((RG) evento.getComponent().getAttributes().get("removeRg"));
        rgs.remove(0);
        rg = new RG();
    }

    public void novoCertidaoNascimento() {
        PessoaFisica pf = selecionado;
        if (isCerticaoNascimentoPreenchida()) {
            certidaoNascimento.setDataRegistro(sistemaControlador.getDataOperacao());
            certidaoNascimento.setPessoaFisica(pf);
            if (pf.getDocumentosPessoais().contains(certidaoNascimento)) {
                pf.getDocumentosPessoais().set(pf.getDocumentosPessoais().indexOf(certidaoNascimento), certidaoNascimento);
            } else {
                pf.getDocumentosPessoais().add(certidaoNascimento);
            }
        }
    }

    private boolean isCerticaoNascimentoPreenchida() {
        if (certidaoNascimento != null
            && certidaoNascimento.getNomeCartorio() != null && !certidaoNascimento.getNomeCartorio().trim().isEmpty()) {
            return true;
        }
        return false;
    }

    private void novoPessoaFisicaCID() {
        if (pessoaFisicaCid != null && pessoaFisicaCid.getCid() != null) {
            pessoaFisicaCid.setPessoaFisica(esteSelecionado());
            esteSelecionado().setPessoaFisicaCid(pessoaFisicaCid);
        }
    }

    public void novoCertidaoCasamento() {
        if (certidaoCasamento != null) {
            PessoaFisica pf = selecionado;
            certidaoCasamento.setDataRegistro(sistemaControlador.getDataOperacao());
            certidaoCasamento.setPessoaFisica(pf);
            if (pf.getDocumentosPessoais().contains(certidaoCasamento)) {
                pf.getDocumentosPessoais().set(pf.getDocumentosPessoais().indexOf(certidaoCasamento), certidaoCasamento);
            } else {
                pf.getDocumentosPessoais().add(certidaoCasamento);
            }
        }
    }

    public void novoTitulo() {
        if (tituloEleitor != null && !tituloEleitor.getNumero().isEmpty()) {
            PessoaFisica pf = selecionado;
//            tituloEleitores.clear();
            tituloEleitor.setDataRegistro(sistemaControlador.getDataOperacao());
            tituloEleitor.setPessoaFisica(pf);
//            tituloEleitores.add(tituloEleitor);
//            tituloEleitores.get(0).setPessoaFisica((PessoaFisica) selecionado);
            if (pf.getDocumentosPessoais().contains(tituloEleitor)) {
                pf.getDocumentosPessoais().set(pf.getDocumentosPessoais().indexOf(tituloEleitor), tituloEleitor);
            } else {
                pf.getDocumentosPessoais().add(tituloEleitor);
            }
        }
    }

    public void removeTitulo(ActionEvent evento) {
        PessoaFisica pf = selecionado;
        pf.getDocumentosPessoais().remove((TituloEleitor) evento.getComponent().getAttributes().get("removeTitulo"));
        tituloEleitores.remove(0);
        tituloEleitor = new TituloEleitor();
    }

    public void novaHabilitacao() {
        if (validaHabilitacao()) {
            PessoaFisica pf = selecionado;
            habilitacao.setDataRegistro(sistemaControlador.getDataOperacao());
            habilitacao.setPessoaFisica(pf);
            habilitacoes.add(habilitacao);
            if (pf.getDocumentosPessoais().contains(habilitacao)) {
                pf.getDocumentosPessoais().set(pf.getDocumentosPessoais().indexOf(habilitacao), habilitacao);
            } else {
                pf.getDocumentosPessoais().add(habilitacao);
            }
            habilitacao = new Habilitacao();
        }
    }

    private boolean validaHabilitacao() {
        boolean retorno = true;

        if (habilitacao.getNumero() == null || habilitacao.getNumero().isEmpty()) {
            FacesUtil.addMessageWarn(null, "Atenção!", "O campo Número deve ser preenchido.");
            retorno = false;
        }
        if (habilitacao.getCategoria() == null) {
            FacesUtil.addMessageWarn(null, "Atenção!", "O campo Categoria deve ser preenchido.");
            retorno = false;
        }
        if (habilitacao.getValidade() == null) {
            FacesUtil.addMessageWarn(null, "Atenção!", "O campo Validade deve ser preenchido.");
            retorno = false;
        }
        if (habilitacao.getDataEmissao() == null) {
            FacesUtil.addMessageWarn(null, "Atenção!", "O campo Data de Emissão deve ser preenchido.");
            retorno = false;
        }

        return retorno;
    }

    public void removeHabilitacao(ActionEvent evento) {
        PessoaFisica pf = selecionado;
        pf.getDocumentosPessoais().remove((Habilitacao) evento.getComponent().getAttributes().get("removeHabilitacao"));
        habilitacoes.remove((Habilitacao) evento.getComponent().getAttributes().get("removeHabilitacao"));
        habilitacao = new Habilitacao();
    }

    public void removeTrabalho(ActionEvent evento) {
        PessoaFisica pf = selecionado;
        pf.getDocumentosPessoais().remove((CarteiraTrabalho) evento.getComponent().getAttributes().get("removeTrabalho"));
        carteiraTrabalhos.remove((CarteiraTrabalho) evento.getComponent().getAttributes().get("removeTrabalho"));
        carteiraTrabalho = new CarteiraTrabalho();
    }

    public void novoMilitar() {
        if (situacaoMilitar != null && situacaoMilitar.getCertificadoMilitar() != null && !situacaoMilitar.getCertificadoMilitar().isEmpty()) {
            PessoaFisica pf = selecionado;
            situacaoMilitar.setDataRegistro(sistemaControlador.getDataOperacao());
            situacaoMilitar.setPessoaFisica((PessoaFisica) selecionado);
            if (pf.getDocumentosPessoais().contains(situacaoMilitar)) {
                pf.getDocumentosPessoais().set(pf.getDocumentosPessoais().indexOf(situacaoMilitar), situacaoMilitar);
            } else {
                pf.getDocumentosPessoais().add(situacaoMilitar);
            }
        }
    }

    public void removeMilitar(ActionEvent evento) {
        PessoaFisica pf = selecionado;
        pf.getDocumentosPessoais().remove((SituacaoMilitar) evento.getComponent().getAttributes().get("removeMilitar"));
        situacaoMilitares.remove(0);
        situacaoMilitar = new SituacaoMilitar();
    }

    public void novoEndereco() {
        if (validaEndereco()) {
            selecionado.getEnderecos().add(endereco);
            endereco = new EnderecoCorreio();
        }
    }

    public void removeFone(ActionEvent evento) {
        selecionado.getTelefones().remove((Telefone) evento.getComponent().getAttributes().get("vfone"));
    }

    //    public void removeConta(ActionEvent evento) {
//        selecionado.getContasCorrentesBancarias().remove((ContaCorrenteBancaria) evento.getComponent().getAttributes().get("vconta"));
//    }
    public void removeEndereco(ActionEvent evento) {
        selecionado.getEnderecos().remove((EnderecoCorreio) evento.getComponent().getAttributes().get("vendereco"));
    }

    public void tituloNulo() {
        tituloEleitor = new TituloEleitor();
    }

    public PessoaFacade getFacade() {
        return pessoaFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return pessoaFacade;
    }

    public List<SelectItem> getTiposFone() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (TipoTelefone object : TipoTelefone.values()) {
            toReturn.add(new SelectItem(object, object.getTipoFone()));
        }
        return toReturn;
    }

    public List<SelectItem> getNacionalidade() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (Nacionalidade object : nacionalidadeFacade.lista()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getTipoSituacao() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoSituacaoMilitar object : TipoSituacaoMilitar.values()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getCategoria() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (CategoriaCertificadoMilitar object : CategoriaCertificadoMilitar.values()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public void setaEndereco(ActionEvent evento) {
        setEnderecoAux((Endereco) evento.getComponent().getAttributes().get("alteraEndereco"));
    }

    public ConverterGenerico getConverterNacionalidade() {
        if (converterNacionalidade == null) {
            converterNacionalidade = new ConverterGenerico(Nacionalidade.class, nacionalidadeFacade);
        }
        return converterNacionalidade;
    }

    public ConverterGenerico getConverterNacionalidadeConjuge() {
        if (converterNacionalidadeConjuge == null) {
            converterNacionalidadeConjuge = new ConverterGenerico(Nacionalidade.class, nacionalidadeFacade);
        }
        return converterNacionalidadeConjuge;
    }

    public ConverterGenerico getConverterCidade() {
        if (converterCidade == null) {
            converterCidade = new ConverterGenerico(Cidade.class, cidadeFacade);
        }
        return converterCidade;
    }

    public List<SelectItem> getEscritorioContabil() {
        List<SelectItem> toReturn = new ArrayList<>();

        for (EscritorioContabil object : escritorioContabilFacade.lista()) {
            toReturn.add(new SelectItem(object, object.getPessoa().getNome()));
        }
        return toReturn;
    }

    public ConverterGenerico getConverterEscritorioContabil() {
        if (converterEscritorioContabil == null) {
            converterEscritorioContabil = new ConverterGenerico(EscritorioContabil.class, escritorioContabilFacade);
        }
        return converterEscritorioContabil;
    }

    public List<Cidade> completaNaturalidade(String parte) {
        return cidadeFacade.listaFiltrando(parte.trim(), "nome");
    }

    public List<SelectItem> getCidades() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (Cidade object : cidadeFacade.lista()) {
            toReturn.add(new SelectItem(object, object.getNome()));
        }
        return toReturn;
    }

    public Converter getConverterNaturalidade() {
        if (converterNaturalidade == null) {
            converterNaturalidade = new ConverterAutoComplete(Cidade.class, naturalidadeFacade);
        }
        return converterNaturalidade;
    }

    public List<SelectItem> getNivelEscolaridade() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (NivelEscolaridade nivelEscolaridade : nivelEscolaridadeFacade.buscarTodosNiveisEscolaridadeOrdenadoPeloCodigo()) {
            if (!GrauInstrucaoCAGED.ANALFABETO.equals(nivelEscolaridade.getGrauInstrucaoCAGED())) {
                toReturn.add(new SelectItem(nivelEscolaridade, nivelEscolaridade.getDescricao()));
            }
        }
        return toReturn;
    }

    public List<SelectItem> getEstados() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (UF object : ufFacade.listaUFNaoNula()) {
            toReturn.add(new SelectItem(object, object.getNome()));
        }
        return toReturn;
    }

    public ConverterGenerico getConverterNivelEscolaridade() {
        if (converterNivelEscolaridade == null) {
            converterNivelEscolaridade = new ConverterGenerico(NivelEscolaridade.class, nivelEscolaridadeFacade);
        }
        return converterNivelEscolaridade;
    }

    public List<SelectItem> getSexo() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (Sexo object : Sexo.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getCategoriaCNH() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (CategoriaHabilitacao object : CategoriaHabilitacao.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getRacaCor() {
        Set<Integer> valoresPermitidos = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5));

        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));

        for (RacaCor object : RacaCor.values()) {
           if (valoresPermitidos.contains(object.getCodigoEsocial())) {
                toReturn.add(new SelectItem(object, object.getDescricao()));
            }
        }
        return toReturn;
    }

    public List<SelectItem> getEstadoCivil() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (EstadoCivil object : EstadoCivil.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public ConverterGenerico getConverterUf() {
        if (converterUf == null) {
            converterUf = new ConverterGenerico(UF.class, ufFacade);
        }
        return converterUf;
    }

    public List<SelectItem> getUf() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (UF object : ufFacade.listaUFNaoNula()) {
            toReturn.add(new SelectItem(object, object.getNome()));
        }
        return toReturn;
    }

    public List<SelectItem> getSituacaoConta() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (SituacaoConta object : SituacaoConta.values()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getTipoSanguineo() {
        List<SelectItem> toReturn = new ArrayList<>();
        //toReturn.add(new SelectItem(null, ""));
        for (TipoSanguineo object : TipoSanguineo.values()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public Converter getConverterAgencia() {
        if (converterAgencia == null) {
            converterAgencia = new ConverterAutoComplete(Agencia.class, agenciaFacade);
        }
        return converterAgencia;
    }

    public List<Agencia> completa(String parte) {
        return agenciaFacade.listaFiltrando(parte.trim(), "nomeAgencia", "numeroAgencia");
    }

    public boolean isSkip() {
        return skip;
    }

    public void setSkip(boolean skip) {
        this.skip = skip;
    }

    public String onFlowProcess(FlowEvent event) {
        if (skip) {
            skip = false;   //reset in case user goes back
            return "confirm";
        } else {
            return event.getNewStep();
        }
    }

    public void selecionar(ActionEvent evento) {
        operacao = Operacoes.EDITAR;
        if (getId() == null) {
            selecionado = (PessoaFisica) evento.getComponent().getAttributes().get("objeto");
            setId(selecionado.getId());
        }

        // TODO
        selecionado = new PessoaFisica();

        if (selecionado instanceof PessoaFisica) {
            selecionado = (PessoaFisica) pessoaFacade.recuperarPF(getId());

            arquivo = new Arquivo();
//            fileUploadEvent = null;
            if ((selecionado).getArquivo() != null) {
                arquivo = (selecionado).getArquivo();
            }

        }

        if (PerfilEnum.PERFIL_PRESTADOR.equals(perfilSelecionado)) {
            if (!selecionado.getPerfis().contains(PerfilEnum.PERFIL_PRESTADOR)) {
                selecionado.getPerfis().add(PerfilEnum.PERFIL_PRESTADOR);
            }
        } else {
            if (!selecionado.getPerfis().contains(PerfilEnum.PERFIL_RH)) {
                selecionado.getPerfis().add(PerfilEnum.PERFIL_RH);
            }
        }
        carteiraVacinacao = new CarteiraVacinacao();
        enderecoAux = new Endereco();
        endereco = new EnderecoCorreio();
        habilitacao = new Habilitacao();
        habilitacoes = new ArrayList<>();
        carteiraTrabalho = new CarteiraTrabalho();
        carteiraTrabalhos = new ArrayList<>();
        enderecoAux = new Endereco();
        classeCredorSelecionada = new ClasseCredorPessoa();
        conselho = new ConselhoClasseContratoFP();

        if (selecionado.getNacionalidadeMae() == null) {
            selecionado.setNacionalidadeMae(getNacionalidadeBrasileira());
        }

        if (selecionado.getNacionalidadePai() == null) {
            selecionado.setNacionalidadePai(getNacionalidadeBrasileira());
        }
        if (selecionado instanceof PessoaFisica) {
            rg = verificaRG();
            if (rg == null) {
                rg = new RG();
                rg.setDataRegistro(sistemaControlador.getDataOperacao());
                rg.setPessoaFisica((PessoaFisica) selecionado);
                addDocumentoPessoa(rg);
            }
            emancipacao = selecionado.getEmancipacao();
            if (emancipacao == null) {
                emancipacao = new Emancipacao();
                emancipacao.setDataRegistro(sistemaControlador.getDataOperacao());
                emancipacao.setPessoaFisica((PessoaFisica) selecionado);
                addDocumentoPessoa(emancipacao);
            }

            documentoNacionalIdentidade = selecionado.getDocumentoNacionalIdentidade();
            if (documentoNacionalIdentidade == null) {
                documentoNacionalIdentidade = new DocumentoNacionalIdentidade();
                documentoNacionalIdentidade.setDataRegistro(sistemaControlador.getDataOperacao());
                documentoNacionalIdentidade.setPessoaFisica(selecionado);
                addDocumentoPessoa(documentoNacionalIdentidade);
            }


            if (verificaCarteiraVacinacao() == null) {
                carteiraVacinacao = new CarteiraVacinacao();
                carteiraVacinacao.setDataRegistro(sistemaControlador.getDataOperacao());
                carteiraVacinacao.setPessoaFisica((PessoaFisica) selecionado);
                addDocumentoPessoa(carteiraVacinacao);
            }
            if (verificaTitulo() == null) {
                tituloEleitor = new TituloEleitor();
                tituloEleitor.setDataRegistro(sistemaControlador.getDataOperacao());
                tituloEleitor.setPessoaFisica((PessoaFisica) selecionado);
                addDocumentoPessoa(tituloEleitor);
            }
            if (verificaMilitar() == null) {
                situacaoMilitar = new SituacaoMilitar();
                situacaoMilitar.setDataRegistro(sistemaControlador.getDataOperacao());
                situacaoMilitar.setPessoaFisica((PessoaFisica) selecionado);
                addDocumentoPessoa(situacaoMilitar);
            }
            if (verificaCertidaoNascimento() == null) {
                certidaoNascimento = new CertidaoNascimento();
                certidaoNascimento.setDataRegistro(sistemaControlador.getDataOperacao());
                certidaoNascimento.setPessoaFisica((PessoaFisica) selecionado);
                addDocumentoPessoa(certidaoNascimento);
            }
            if (verificaCertidaoCasamento() == null) {
                certidaoCasamento = new CertidaoCasamento();
                certidaoCasamento.setDataRegistro(sistemaControlador.getDataOperacao());
                certidaoCasamento.setPessoaFisica((PessoaFisica) selecionado);
                certidaoCasamento.setNacionalidade(getNacionalidadeBrasileira());
                addDocumentoPessoa(certidaoCasamento);
            }

            PessoaFisica pf = selecionado;
            for (DocumentoPessoal doc : pf.getDocumentosPessoais()) {
                if (doc instanceof CarteiraTrabalho) {
                    carteiraTrabalhos.add((CarteiraTrabalho) doc);
                } else if (doc instanceof Habilitacao) {
                    habilitacoes.add((Habilitacao) doc);
                } else if (doc instanceof CarteiraVacinacao) {
                    carteiraVacinacao = (CarteiraVacinacao) doc;
                }
            }
            if (selecionado.getRg() != null) {
                rg = selecionado.getRg();
            }
            if (selecionado.getTituloEleitor() != null) {
                tituloEleitor = selecionado.getTituloEleitor();
            }
            if (selecionado.getSituacaoMilitar() != null) {
                situacaoMilitar = selecionado.getSituacaoMilitar();
            }
            if (selecionado.getCertidaoNascimento() != null) {
                certidaoNascimento = selecionado.getCertidaoNascimento();
            }
            if (selecionado.getCertidaoCasamento() != null) {
                certidaoCasamento = selecionado.getCertidaoCasamento();
            }
            if (selecionado.getEmancipacao() != null) {
                emancipacao = selecionado.getEmancipacao();
            }
        }

        listaExcluidos = new ArrayList<>();
        if (selecionado instanceof PessoaFisica) {
            carregaFoto();
            try {
                pessoaFisicaCid = esteSelecionado().getPessoaFisicaCid();
                if (pessoaFisicaCid == null) {
                    pessoaFisicaCid = new PessoaFisicaCid();
                }
            } catch (NullPointerException npe) {
            }
        }
    }

    public RG verificaRG() {
        PessoaFisica pf = selecionado;
        for (DocumentoPessoal documento : pf.getDocumentosPessoais()) {
            if (documento instanceof RG) {
                return (RG) documento;
            }
        }
        return null;
    }

    public CarteiraVacinacao verificaCarteiraVacinacao() {
        PessoaFisica pf = selecionado;
        for (DocumentoPessoal documento : pf.getDocumentosPessoais()) {
            if (documento instanceof CarteiraVacinacao) {
                return (CarteiraVacinacao) documento;
            }
        }
        return null;
    }

    public TituloEleitor verificaTitulo() {
        PessoaFisica pf = selecionado;
        for (DocumentoPessoal documento : pf.getDocumentosPessoais()) {
            if (documento instanceof TituloEleitor) {
                return (TituloEleitor) documento;
            }
        }
        return null;
    }

    public SituacaoMilitar verificaMilitar() {
        PessoaFisica pf = selecionado;
        for (DocumentoPessoal documento : pf.getDocumentosPessoais()) {
            if (documento instanceof SituacaoMilitar) {
                return (SituacaoMilitar) documento;
            }
        }
        return null;
    }

    public CertidaoNascimento verificaCertidaoNascimento() {
        PessoaFisica pf = selecionado;
        for (DocumentoPessoal documento : pf.getDocumentosPessoais()) {
            if (documento instanceof CertidaoNascimento) {
                return (CertidaoNascimento) documento;
            }
        }
        return null;
    }

    public CertidaoCasamento verificaCertidaoCasamento() {
        PessoaFisica pf = selecionado;
        for (DocumentoPessoal documento : pf.getDocumentosPessoais()) {
            if (documento instanceof CertidaoCasamento) {
                if (((CertidaoCasamento) documento).getNacionalidade() == null) {
                    ((CertidaoCasamento) documento).setNacionalidade(getNacionalidadeBrasileira());
                }
                return (CertidaoCasamento) documento;
            }
        }
        return null;
    }

    public RG getRg() {
        return rg;
    }

    public void setRg(RG rg) {
        this.rg = rg;
    }

    public Emancipacao getEmancipacao() {
        return emancipacao;
    }

    public void setEmancipacao(Emancipacao emancipacao) {
        this.emancipacao = emancipacao;
    }

    public List<RG> getRgs() {
        return rgs;
    }

    public void setRgs(List<RG> rgs) {
        this.rgs = rgs;
    }

    public void setListaContas(List<ContaCorrenteBancaria> listaContas) {
        this.listaContas = listaContas;
    }

    public CertidaoCasamento getCertidaoCasamento() {
        return certidaoCasamento;
    }

    public void setCertidaoCasamento(CertidaoCasamento certidaoCasamento) {
        this.certidaoCasamento = certidaoCasamento;
    }

    public CertidaoNascimento getCertidaoNascimento() {
        return certidaoNascimento;
    }

    public void setCertidaoNascimento(CertidaoNascimento certidaoNascimento) {
        this.certidaoNascimento = certidaoNascimento;
    }

    private boolean validaEmail(String email) {
        if (email.isEmpty()) {
            return true;
        } else {
            Pattern p = Pattern.compile(".+@.+\\.[a-z]+");
            Matcher m = p.matcher(email);
            return m.matches();
        }
    }


    public List<SelectItem> tiposEnderecos() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, " "));
        for (TipoEndereco t : TipoEndereco.values()) {
            toReturn.add(new SelectItem(t, t.getDescricao()));
        }
        return toReturn;
    }

    public List<CEPUF> completaUF(String parte) {
        return pessoaFacade.getConsultaCepFacade().consultaUf(parte.trim());
    }

    public void setaUf(SelectEvent e) {
        endereco.setUf((String) e.getObject());
    }

    public void setaCidade(SelectEvent e) {
        endereco.setLocalidade((String) e.getObject());
    }

    public List<CEPLocalidade> completaCidade(String parte) {
        String l = "";
        if (endereco.getUf() != null) {
            l = endereco.getUf();
        }
        return pessoaFacade.getConsultaCepFacade().consultaLocalidades(l, parte.trim());
    }

    public List<CEPLogradouro> completaLogradouro(String parte) {
        String l = "";
        if (endereco.getLocalidade() != null) {
            l = endereco.getLocalidade();
        }
        return pessoaFacade.getConsultaCepFacade().consultaLogradouros(l, parte.trim());
    }

    public List<CEPBairro> completaBairro(String parte) {
        String l = "";
        if (endereco.getLocalidade() != null) {
            l = endereco.getLocalidade();
        }
        return pessoaFacade.getConsultaCepFacade().consultaBairros(l, parte.trim());
    }

    public Converter getConverterStringObjt() {
        return new Converter() {
            @Override
            public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String s) {
                if (s != null) {
                    try {
                        Long chave = new Long(s);
                        CEPLogradouro cepLogradouro = pessoaFacade.getConsultaCepFacade().recuperaCEPLogradouro(chave);
                        if (cepLogradouro == null) {
                            return s;
                        }
                        return cepLogradouro.getCep();
                    } catch (NumberFormatException nfe) {
                        logger.warn("erro ao converter chave de cep " + s);
                    } catch (NoResultException nre) {
                        logger.warn("erro, nenhum resultado encontrado para " + s);
                        return s;
                    }
                }


                return s;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object o) {
                try {
                    CEPLogradouro cepLogradouro = (CEPLogradouro) o;
                    return cepLogradouro.getCep();

                } catch (ClassCastException cce) {
                    logger.warn("erro de conversao getAsString " + cce);
                    return o + "";
                }

                //return o.toString();  //To change body of implemented methods use File | Settings | File Templates.
            }
        };
    }

    public List<String> completaCEP(String parte) {
//        String l = "";
//        String c = "";
//        if (endereco.getLogradouro() != null) {
//            l = endereco.getLogradouro();
//        }
//        if (endereco.getLocalidade() != null) {
//            c = endereco.getLocalidade();
//        }
//        return pessoaFacade.getConsultaCepFacade().consultaCEPs(l, c, parte.trim());
        return pessoaFacade.getConsultaCepFacade().consultaLogradouroPorParteCEPByString(parte.trim());
    }

    public void atualizarLogradouros() {
        pessoaFacade.getConsultaCepFacade().atualizarLogradouros(endereco);
    }

    public void atualizaLogradourosAntigo() {
        if (textocep.getValue() == null) {
            return;
        }
        //cep = textocep.getValue().toString();
        //logradouros = pessoaFacade.getConsultaCepFacade().consultaLogradouroPorCEP(cep);
        logger.debug("entrou" + logradouros);

        if (!logradouros.isEmpty()) {
            logger.debug("existe registro");
            logradouro = logradouros.get(0);
            logger.debug(logradouro.getNome() + " logradourodocepe");
            endereco.setBairro(logradouro.getBairro().getNome());
            endereco.setCep(logradouro.getCep());
            endereco.setLogradouro(logradouro.getNome());
            endereco.setUf(logradouro.getLocalidade().getCepuf().getNome());
            endereco.setLocalidade(logradouro.getLocalidade().getNome());
        }
    }

    @Override
    public void salvar() {
        try {

            if (selecionado instanceof PessoaFisica) {
                novoRg();
                novoDocumentoNacionalIdentidade();
                novoEmancipacao();
                novoTitulo();
                novoMilitar();
                novoCertidaoNascimento();
                novoCertidaoCasamento();
                novoPessoaFisicaCID();
                if (PerfilEnum.PERFIL_PRESTADOR.equals(perfilSelecionado)) {
                    validarCamposPessoaFisicaPrestador();
                } else {
                    validarCamposPessoaFisica();
                }
                selecionado.setNome(StringUtil.removeEspacos(selecionado.getNome()).trim());
            }

            if (Operacoes.NOVO.equals(operacao)) {
                selecionado.setUnidadeOrganizacional(unidadeOrganizacionalSelecionada.getSubordinada());
                pessoaFacade.salvarNovo(((PessoaFisica) selecionado), arquivo, selecionado.getFileUploadPrimeFaces().getFileUploadEvent());
                carregaFoto();
            } else {
                if (selecionado.getPerfis().equals(PerfilEnum.PERFIL_CREDOR)) {
                    selecionado.setUnidadeOrganizacional(unidadeOrganizacionalSelecionada.getSubordinada());
                }
                pessoaFacade.salvar(((PessoaFisica) selecionado), arquivo, selecionado.getFileUploadPrimeFaces().getFileUploadEvent(), listaExcluidos);
                carregaFoto();
            }

            if (!contasParaRemoverDoVinculoFP.isEmpty()) {
                List<VinculoFP> vinculos = pessoaFacade.buscarVinculoFPPessoa(selecionado);
                if (!vinculos.isEmpty()) {
                    vinculoFPFacade.removerContaCorrenteBancariaVinculoFP(vinculos, contasParaRemoverDoVinculoFP);
                }
            }


            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Salvo com Sucesso", ""));
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addErrorPadrao(e);
            logger.error("Erro: ", e);
            return;
        }

    }

    public List<ContaCorrenteBancaria> completaContaCorrenteBancarias(String parte) {
        return contaCorrenteBancariaFacade.listaContasFiltrandoAtributosAgencia(parte.trim());
    }

    public Converter getConverterContaCorrenteBancaria() {
        if (converterContaCorrenteBancaria == null) {
            converterContaCorrenteBancaria = new ConverterAutoComplete(ContaCorrenteBancaria.class, contaCorrenteBancariaFacade);
        }
        return converterContaCorrenteBancaria;
    }

    public List<SelectItem> getSituacaoCadastral() {
        List<SelectItem> lista = new ArrayList<>();
        for (SituacaoCadastralPessoa p : SituacaoCadastralPessoa.values()) {
            lista.add(new SelectItem(p, p.getDescricao()));
        }
        return lista;
    }

    public List<SelectItem> getTipoDeficiencia() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (TipoDeficiencia object : TipoDeficiencia.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    private void validarCamposDeAcordoComEstadoCivil(ValidacaoException ve) {
        if (selecionado.getEstadoCivil() != null) {
            if (EstadoCivil.CASADO.equals(selecionado.getEstadoCivil()) || EstadoCivil.VIUVO.equals(selecionado.getEstadoCivil())) {

                if (Strings.isNullOrEmpty(certidaoCasamento.getNomeCartorio().trim())) {
                    ve.adicionarMensagemDeCampoObrigatorio("O campo Nome do Cartório na aba Certidão de Casamento deve ser informado!");
                }
                if (EstadoCivil.CASADO.equals(selecionado.getEstadoCivil()) && Strings.isNullOrEmpty(certidaoCasamento.getLocalTrabalhoConjuge().trim())) {
                    ve.adicionarMensagemDeCampoObrigatorio("O campo Local de Trabalho do Cônjuge na aba Certidão de Casamento deve ser informado!");
                }
                if (Strings.isNullOrEmpty(certidaoCasamento.getNomeConjuge().trim())) {
                    ve.adicionarMensagemDeCampoObrigatorio("O campo Nome do Cônjuge na aba Certidão de Casamento deve ser informado!");
                }
                if (certidaoCasamento.getNacionalidade() == null) {
                    ve.adicionarMensagemDeCampoObrigatorio("O campo Nacionalidade do Cônjuge na aba Certidão de Casamento deve ser informado!");
                }
                if (Strings.isNullOrEmpty(certidaoCasamento.getEstado().trim())) {
                    ve.adicionarMensagemDeCampoObrigatorio("O campo Unidade Federativa do Cônjuge na aba Certidão de Casamento deve ser informado!");
                }
                if (certidaoCasamento.getDataCasamento() == null) {
                    ve.adicionarMensagemDeCampoObrigatorio("O campo Data de Casamento do Cônjuge na aba Certidão de Casamento deve ser informado!");
                }
                if (certidaoCasamento.getDataNascimentoConjuge() == null) {
                    ve.adicionarMensagemDeCampoObrigatorio("O campo Data de Nascimento do Cônjuge na aba Certidão de Casamento deve ser informado!");
                }
                if (certidaoCasamento.getCidadeCartorio() == null) {
                    ve.adicionarMensagemDeCampoObrigatorio("O campo Cidade do Cartório na aba Certidão de Casamento deve ser informado!");
                }
            } else if (EstadoCivil.DIVORCIADO.equals(selecionado.getEstadoCivil())) {
                if (Strings.isNullOrEmpty(certidaoCasamento.getNomeCartorio().trim())) {
                    ve.adicionarMensagemDeCampoObrigatorio("O campo Nome do Cartório na aba Certidão de Casamento deve ser informado!");
                }
                if (certidaoCasamento.getCidadeCartorio() == null) {
                    ve.adicionarMensagemDeCampoObrigatorio("O campo Cidade do Cartório na aba Certidão de Casamento deve ser informado!");
                }
                if (Strings.isNullOrEmpty(certidaoCasamento.getNumeroLivro().trim())) {
                    ve.adicionarMensagemDeCampoObrigatorio("O campo Número do livro na aba Certidão de Casamento deve ser informado!");
                }
                if (certidaoCasamento.getNumeroFolha() == null) {
                    ve.adicionarMensagemDeCampoObrigatorio("O campo Número da folha na aba Certidão de Casamento deve ser informado!");
                }
                if (certidaoCasamento.getNumeroRegistro() == null) {
                    ve.adicionarMensagemDeCampoObrigatorio("O campo Número do Registro na aba Certidão de Casamento deve ser informado!");
                }
                if (EstadoCivil.DIVORCIADO.equals(selecionado.getEstadoCivil()) && certidaoCasamento.getDataAverbacao() == null) {
                    ve.adicionarMensagemDeCampoObrigatorio("O campo Data da Averbação na aba Certidão de Casamento deve ser informado!");
                }
            } else if (EstadoCivil.VIUVO.equals(selecionado.getEstadoCivil())) {
                if (certidaoCasamento.getDataObito() == null) {
                    ve.adicionarMensagemDeCampoObrigatorio("O campo Data do Óbito na aba Certidão de Casamento/Informações do Óbito deve ser informado!");
                }
                if (Strings.isNullOrEmpty(certidaoCasamento.getCartorio().trim())) {
                    ve.adicionarMensagemDeCampoObrigatorio("O campo Cartório na aba Certidão de Casamento/Informações do Óbito deve ser informado!");
                }
            } else {
                if (Strings.isNullOrEmpty(certidaoNascimento.getNomeCartorio().trim())) {
                    ve.adicionarMensagemDeCampoObrigatorio("O campo Nome do Cartório na aba Certidão de Nascimento deve ser informado!");
                }
                if (certidaoNascimento.getCidade() == null) {
                    ve.adicionarMensagemDeCampoObrigatorio("O campo Cidade na aba Certidão de Nascimento deve ser informado!");
                }
            }
        }
    }


    public void validarCamposPessoaFisica() {
        ValidacaoException ve = new ValidacaoException();
        ValidaPessoa.validar(selecionado, PerfilEnum.PERFIL_RH, ve);

        validarCamposRG(ve);
        validarCamposDeAcordoComEstadoCivil(ve);

        if (selecionado.getTelefones().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Adicione pelo menos um Telefone.");
        }
        if (selecionado.getEnderecos().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Adicione pelo menos um Endereço.");
        }
        if (selecionado.getContaCorrenteBancPessoas().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Adicione pelo menos uma Conta Corrente.");
        }

        if (Util.valida_CpfCnpj(selecionado.getCpf())) {
            if (pessoaFacade.hasOutraPessoaComMesmoCpf(selecionado, true)) {
            } else if (pessoaFacade.hasOutraPessoaComMesmoCpf(selecionado, false)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Este CPF já pertence a " + getDescricaoConcatenada(pessoaFacade.getPessoasComMesmoCPF(selecionado)));
            }
        } else {
            ve.adicionarMensagemDeOperacaoNaoPermitida("CPF inválido.");
        }

        if (selecionado.getNacionalidade() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a Nacionalidade.");
        } else if (selecionado.getNacionalidade().getCodigoRaiz() != BRASILEIRO && selecionado.getTipoCondicaoIngresso() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a Condição de ingresso na aba 'Estrangeiro'.");
        }
        if (selecionado.getNaturalidade() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a Naturalidade.");
        }
        if (selecionado.getNivelEscolaridade() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Nível de Escolaridade.");
        }
        if (Strings.isNullOrEmpty(selecionado.getEmail().trim())) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o e-mail.");
        }
        ve.lancarException();
    }

    public void validarCamposPessoaFisicaPrestador() {
        ValidacaoException ve = new ValidacaoException();
        ValidaPessoa.validar(selecionado, PerfilEnum.PERFIL_PRESTADOR, ve);

        if (Util.valida_CpfCnpj(selecionado.getCpf())) {
            if (pessoaFacade.hasOutraPessoaComMesmoCpf(selecionado, false)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Este CPF já pertence a " + getDescricaoConcatenada(pessoaFacade.getPessoasComMesmoCPF(selecionado)));
            }
        } else {
            ve.adicionarMensagemDeOperacaoNaoPermitida("CPF inválido.");
        }
        if (selecionado.getNacionalidade() != null && (selecionado.getNacionalidade().getCodigoRaiz() != BRASILEIRO && selecionado.getTipoCondicaoIngresso() == null)) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a Condição de ingresso na aba 'Estrangeiro'.");
        }
        ve.lancarException();
    }

    private void validarCamposRG(ValidacaoException ve) {
        if (rg != null) {
            if (rg.getNumero().equals("")) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Número na aba RG deve ser informado.");
            }
            if (rg.getDataemissao() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Data Emissão na aba RG deve ser informado.");
            }
            if (rg.getOrgaoEmissao().equals("")) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Órgão Emissor na aba RG deve ser informado.");
            }
            if (rg.getUf() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Estado na aba RG deve ser informado.");
            }
        }
    }

    public String caminho() {
        return getCaminhoPadrao();
    }

    public HierarquiaOrganizacional getUnidadeOrganizacionalSelecionada() {
        return unidadeOrganizacionalSelecionada;
    }

    public void setUnidadeOrganizacionalSelecionada(HierarquiaOrganizacional unidadeOrganizacionalSelecionada) {
        this.unidadeOrganizacionalSelecionada = unidadeOrganizacionalSelecionada;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public ClasseCredorPessoa getClasseCredorSelecionada() {
        return classeCredorSelecionada;
    }

    public void setClasseCredorSelecionada(ClasseCredorPessoa classeCredorSelecionada) {
        this.classeCredorSelecionada = classeCredorSelecionada;
    }

    public void uploadArquivo(FileUploadEvent event) {
        try {

            selecionado.setFileUploadPrimeFaces(new FileUploadPrimeFaces(event));
            imagemFoto = new DefaultStreamedContent(event.getFile().getInputstream(), "image/png", event.getFile().getFileName());
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("imagem-foto", imagemFoto);
        } catch (IOException ex) {
            logger.error("Erro: ", ex);
        }

    }

    public StreamedContent getImagemFoto() {
        carregaFoto();
        return imagemFoto;
    }

    public void setImagemFoto(StreamedContent imagemFoto) {
        this.imagemFoto = imagemFoto;
    }

    public void carregaFoto() {
        Arquivo arq = (selecionado).getArquivo();
        if (arq != null) {
            try {
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();

                for (ArquivoParte a : arquivoFacade.recuperaDependencias(arq.getId()).getPartes()) {
                    buffer.write(a.getDados());
                }

                InputStream teste = new ByteArrayInputStream(buffer.toByteArray());
                imagemFoto = new DefaultStreamedContent(teste, arq.getMimeType(), arq.getNome());
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("imagem-foto", imagemFoto);
            } catch (Exception ex) {
                logger.error("Erro: ", ex);
            }
        }
    }

    //    @Override
    public List getLista() {
        return null;
    }

    public String visualizaPessoa() {
        return "visualizar";
    }

    public String editaPessoa() {
        if (selecionado instanceof PessoaFisica) {
            carregaFoto();
            return "edita";
        }
        return "edita";
    }

    public void validaCpfRh() {

        if (!Util.validarCpf((selecionado).getCpf())) {
            FacesUtil.addOperacaoNaoPermitida("O CPF digitado é inválido");
        } else if (pessoaFacade.hasOutraPessoaComMesmoCpf(selecionado, false)) {
            FacesUtil.addAtencao("O CPF digitado já pertence a " + getDescricaoConcatenada(pessoaFacade.getPessoasComMesmoCPF((PessoaFisica) selecionado))
                + " já possui cadastro no sistema. Os dados existentes foram carregados. Por favor, atente para o devido preenchimento de campos obrigatórios antes do salvamento.");
        }
        if (pessoaFacade.hasOutraPessoaComMesmoCpf(selecionado, true)) {
            setId(pessoaFacade.recuperarIdPessoaFisicaPorCPF(selecionado.getCpf().trim()));
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "editar/" + getId() + "/");
        }
    }

    public void validaCpfRhPrestador() {
        if (!Util.validarCpf((selecionado).getCpf())) {
            selecionado.setCpf(null);
            FacesUtil.addOperacaoNaoPermitida("O CPF digitado é inválido");
        } else {
            Long idPessoa = pessoaFisicaFacade.buscarIdDePessoaPorCpf(selecionado.getCpf());
            if (idPessoa != null) {
                redireciona(getCaminhoPadrao() + "editar/" + idPessoa);
                FacesUtil.addAtencao("A pessoa com CPF " + selecionado.getCpf() + " já possui cadastro no sistema. Os dados existentes foram carregados. Por favor, atente para o devido preenchimento de campos obrigatórios antes do salvamento.");
            }
        }
    }

    public Boolean validarCpfNuloOuVazio(){
        return Strings.isNullOrEmpty(selecionado.getCpf());
    }

    public void validarCpfCompanheiro() {

        if (!Util.validarCpf((certidaoNascimento).getCpfCompanheiro())) {
            FacesUtil.addOperacaoNaoPermitida("O CPF digitado é inválido");
        } else if (!getDocumentoPessoalFacade().verificarCpf((CertidaoNascimento) certidaoNascimento)) {
            FacesUtil.addOperacaoNaoPermitida("O CPF digitado já pertence a " + certidaoNascimento.getNomeCompanheiro());
        }
    }

    public String getDescricaoConcatenada(List<PessoaFisica> pessoas) {
        String descricaoCompleta = "";
        for (PessoaFisica pf : pessoas) {
            descricaoCompleta = " " + pf.getNome() + " " + pf.getCpf() + (pf.getPerfis().isEmpty() ? "" : " Perfis: " + pf.getPerfis());
        }
        return descricaoCompleta;
    }

    public List<SelectItem> classesPessoa() {
        List<SelectItem> lista = new ArrayList<>();
        lista.add(new SelectItem(null, ""));
        for (ClassePessoa cp : ClassePessoa.values()) {
            lista.add(new SelectItem(cp, cp.getDescricao()));
        }
        return lista;
    }

    public List<Agencia> completaAgencia(String parte) {
        return agenciaFacade.listaFiltrandoAtributosAgenciaBanco(parte.trim());
    }

    public List<Agencia> completaAgenciaDoBanco(String parte) {
        try {
            return agenciaFacade.listaFiltrandoPorBanco(parte.trim(), banco);
        } catch (Exception ex) {
            return new ArrayList<>();
        }
    }

    public void novoTrabalho() {
        try {
            validarCarteiraTrabalho();
            PessoaFisica pf = selecionado;
            carteiraTrabalho.setDataRegistro(sistemaControlador.getDataOperacao());
            carteiraTrabalho.setPessoaFisica(pf);

            carteiraTrabalhos.add(carteiraTrabalho);
            if (pf.getDocumentosPessoais().contains(carteiraTrabalho)) {
                pf.getDocumentosPessoais().set(pf.getDocumentosPessoais().indexOf(carteiraTrabalho), carteiraTrabalho);
            } else {
                pf.getDocumentosPessoais().add(carteiraTrabalho);
            }
            carteiraTrabalho = new CarteiraTrabalho();
        }catch (ValidacaoException ve){
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void pisValido() {
        if (!ArquivoPisPasepFacade.isValid(carteiraTrabalho.getPisPasep())) {
            FacesUtil.addOperacaoNaoPermitida("O campo PIS/PASEP é inválido!");
            return;
        }
        if (ArquivoPisPasepFacade.identificaNIT(carteiraTrabalho.getPisPasep())) {
            FacesUtil.addOperacaoNaoPermitida("O PIS/PASEP informado é um número de NIT, por favor informe outro número de PIS/PASEP.");
            carteiraTrabalho.setPisPasep("");
            return;
        }
        PessoaFisica pf = pessoaFacade.validarPisExistentePorPessoa(selecionado, carteiraTrabalho.getPisPasep());
        if (pf != null) {
            FacesUtil.addOperacaoNaoPermitida("Já existe um PIS/PASEP com esse número para a pessoa " + pf.getNome() + ".");
        }
    }

    private void validarCarteiraTrabalho() {
        ValidacaoException ve = new ValidacaoException();
        if (!ArquivoPisPasepFacade.isValid(carteiraTrabalho.getPisPasep())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo PIS/PASEP é inválido!");
        }

        PessoaFisica pf = pessoaFacade.validarPisExistentePorPessoa(selecionado, carteiraTrabalho.getPisPasep());
        if (pf != null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe um PIS/PASEP com esse número para a pessoa " + pf.getNome() + ".");
        }
        if (carteiraTrabalho.getNumero().equals("")) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Número da Carterira de trabalho é obrigatório");
        }
        if (carteiraTrabalho.getSerie().equals("")) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Série da Carterira de trabalho é obrigatório");
        }
        if ("".equals(carteiraTrabalho.getOrgaoExpedidor())) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Órgão Expedidor da Carteira de trabalho é obrigatório");
        }
        if (carteiraTrabalho.getDataEmissao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data Emissão da Carterira de trabalho é obrigatório");
        }
        if (carteiraTrabalho.getUf() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Estado da Carterira de trabalho é obrigatório");
        }
        if (carteiraTrabalho.getBancoPisPasep() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Banco da Carterira de trabalho é obrigatório");
        }

        for (CarteiraTrabalho ct : carteiraTrabalhos) {
            if (ct.getPisPasep().equals(carteiraTrabalho.getPisPasep())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe um PIS/PASEP com esse número na lista!");
            }
        }
        ve.lancarException();
    }

    public List<Banco> completaBanco(String parte) {
        return bancoFacade.listaBancoPorCodigoOuNome(parte.trim());
    }

    public Converter getConverterBanco() {
        if (converterBanco == null) {
            converterBanco = new ConverterAutoComplete(Banco.class, bancoFacade);
        }
        return converterBanco;
    }

    public List<Cidade> completaCidadeCasamento(String parte) {
        return cidadeFacade.listaFiltrando(parte.trim(), "nome");
    }

    public ConverterAutoComplete getConverterCidadeCasamento() {
        if (converterCidadeCasamento == null) {
            converterCidadeCasamento = new ConverterAutoComplete(Cidade.class, cidadeFacade);
        }
        return converterCidadeCasamento;
    }

    private boolean validaEndereco() {
        boolean retorno = true;

        if (endereco.getCep().length() != 8) {
            FacesUtil.addOperacaoNaoPermitida("Informe o CEP corretamente.");
            retorno = false;
        }
        if (endereco.getCep() == null || endereco.getCep().trim().isEmpty()) {
            FacesUtil.addCampoObrigatorio("O campo CPF deve ser informado! ");
            retorno = false;
        }
        if (endereco.getUf() == null || endereco.getUf().trim().isEmpty()) {
            FacesUtil.addOperacaoNaoPermitida(" O campo Estado deve ser informado ");
            retorno = false;
        } else if (endereco.getUf().length() != 2) {
            FacesUtil.addOperacaoNaoPermitida(" O campo Estado é uma Sigla é deve ter 2 caractéres");
            retorno = false;
        }
        if (endereco.getLocalidade() == null || endereco.getLocalidade().trim().isEmpty()) {
            FacesUtil.addOperacaoNaoPermitida(" O campo Cidade deve ser informado ");
            retorno = false;
        }
        if (endereco.getLogradouro() == null || endereco.getLogradouro().trim().isEmpty()) {
            FacesUtil.addOperacaoNaoPermitida(" O campo Logradouro deve ser informado");
            retorno = false;
        }
        if (endereco.getBairro() == null || endereco.getBairro().trim().isEmpty()) {
            FacesUtil.addOperacaoNaoPermitida(" O campo Bairro deve ser informado ");
            retorno = false;
        }
        if (endereco.getNumero() == null || endereco.getNumero().trim().isEmpty()) {
            FacesUtil.addOperacaoNaoPermitida(" O campo Número deve ser informado ");
            retorno = false;
        }

        return retorno;
    }

    public void mudouCampoDeficienteFisico(ValueChangeEvent evento) {
        TipoDeficiencia t = (TipoDeficiencia) evento.getNewValue();
        esteSelecionado().setTipoDeficiencia(t);
        if (esteSelecionado().possuiDeficienciaFisica()) {
            esteSelecionado().setPessoaFisicaCid(new PessoaFisicaCid());
        }

    }

    public ConselhoClasseContratoFP getConselho() {
        return conselho;
    }

    public void setConselho(ConselhoClasseContratoFP conselho) {
        this.conselho = conselho;
    }

    public List<SelectItem> getConselhos() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (ConselhoClasseOrdem object : conselhoClasseOrdemFacade.lista()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public ConverterGenerico getConverterConselho() {
        if (converterConselho == null) {
            converterConselho = new ConverterGenerico(ConselhoClasseOrdem.class, conselhoClasseOrdemFacade);
        }
        return converterConselho;
    }

    public void adicionarConselho() {
        boolean valida = true;

        try {
            if (conselho == null) {
                FacesUtil.addOperacaoNaoPermitida("Conselho não instanciado.");
                valida = false;
            }
            if (conselho.getConselhoClasseOrdem() == null) {
                FacesUtil.addOperacaoNaoPermitida("Selecione um Conselho Classe/Ordem.");
                valida = false;
            }
            if (conselho.getNumeroDocumento().equals("")) {
                FacesUtil.addOperacaoNaoPermitida("Número de documento não preenchido");
                valida = false;
            }
            if (conselho.getUf() == null) {
                FacesUtil.addOperacaoNaoPermitida("Estado não selecionado.");
                valida = false;
            }
            if (conselho.getDataEmissao() == null) {
                FacesUtil.addOperacaoNaoPermitida("Data de Emissão não preenchido.");
                valida = false;
            }
        } finally {
            if (valida) {
                conselho.setPessoaFisica(((PessoaFisica) selecionado));
                ((PessoaFisica) selecionado).getConselhoClasseContratos().add(conselho);
                conselho = new ConselhoClasseContratoFP();
            }
        }
    }

    public void removerConselho(ActionEvent evento) {
        ConselhoClasseContratoFP c = (ConselhoClasseContratoFP) evento.getComponent().getAttributes().get("objeto");
        ((PessoaFisica) selecionado).getConselhoClasseContratos().remove(c);
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void limparCamposEstadoCivil() {
        certidaoCasamento = verificaCertidaoCasamento();
        if (certidaoCasamento == null) {
            certidaoCasamento = new CertidaoCasamento();
        }

        certidaoNascimento = verificaCertidaoNascimento();
        if (certidaoNascimento == null) {
            certidaoNascimento = new CertidaoNascimento();
        }
    }

    @Override
    public void cancelar() {
        if (operacao != null && operacao.equals(Operacoes.EDITAR)) {
            carregaFoto();
        }
        super.cancelar();
    }

    private Nacionalidade getNacionalidadeBrasileira() {
        List<Nacionalidade> lista = nacionalidadeFacade.executaConsultaGenerica("from Nacionalidade n where n.codigoRaiz = 10");
        return (Nacionalidade) lista.get(0);
    }

    public void limpaAnoChegada() {
        if (selecionado.getNacionalidade() != null && selecionado.getNacionalidade().getCodigoRaiz() == 10) {
            selecionado.setAnoChegada(null);
        }
    }

    public void contaCorrenteExistente() {
        if (contaCorrenteBancPessoaSelecionada.getContaCorrenteBancaria().getAgencia() != null
            && contaCorrenteBancPessoaSelecionada.getContaCorrenteBancaria().getAgencia().getId() != null
            && contaCorrenteBancPessoaSelecionada.getContaCorrenteBancaria().getNumeroConta() != null
            && !contaCorrenteBancPessoaSelecionada.getContaCorrenteBancaria().getNumeroConta().trim().isEmpty()) {
            ContaCorrenteBancaria contaCorrenteExistente = contaCorrenteBancariaFacade.contaCorrenteExistente(contaCorrenteBancPessoaSelecionada.getContaCorrenteBancaria());
            if (contaCorrenteExistente != null) {
                contaCorrenteExistente.setNumeroConta(contaCorrenteBancPessoaSelecionada.getContaCorrenteBancaria().getNumeroConta());
                contaCorrenteBancPessoaSelecionada.setContaCorrenteBancaria(contaCorrenteExistente);
            }
            if (contaCorrenteExistente != null && contaCorrenteExistente.getId() != null) {
                List<Pessoa> pessoas = contaCorrenteBancPessoaFacade.listaPorContaCorrenteBancaria(contaCorrenteBancPessoaSelecionada.getContaCorrenteBancaria());
                if (pessoas.size() == 1) {
                    contaConjunta = false;
                    pessoaDaContaCorrente = pessoas.get(0).getNome();
                    mostraDialogContaExistente();
                } else if (pessoas.size() > 1) {
                    contaConjunta = true;
                    pessoaDaContaCorrente = pessoas.get(0).getNome() + " e " + pessoas.get(1).getNome();
                    mostraDialogContaExistente();
                }
            }
        }
    }

    public void mostraDialogContaExistente() {
        RequestContext.getCurrentInstance().update("formDialogContaExistente");
        RequestContext.getCurrentInstance().execute("dialogContaExistente.show()");
    }

    public void limpaDadosContaCorrente() {
        contaCorrenteBancPessoaSelecionada.getContaCorrenteBancaria().setNumeroConta("");
        contaCorrenteBancPessoaSelecionada.getContaCorrenteBancaria().setDigitoVerificador("");
        contaCorrenteBancPessoaSelecionada.getContaCorrenteBancaria().setSituacao(null);
        contaCorrenteBancPessoaSelecionada.getContaCorrenteBancaria().setModalidadeConta(null);
    }

    public boolean mostraImagemSilhueta() {
        try {
            return ((StreamedContent) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("imagem-foto")).getName().trim().length() <= 0;
        } catch (Exception e) {
            return true;
        }
    }

    @Override
    public String getCaminhoPadrao() {
        return caminhoPadrao;
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public void novaEspecialidade(ActionEvent ev) {
        String url = "/especialidade/novo/";
        Web.navegacao(getUrlAtual(), url, selecionado);
    }

    public PessoaFisicaRHControlador getControlador() {
        return pessoaFisicaRHControlador;
    }

    public void setControlador(PessoaFisicaRHControlador pessoaFisicaRHControlador) {
        this.pessoaFisicaRHControlador = pessoaFisicaRHControlador;
    }

    public PessoaHabilidade getPessoaHabilidade() {
        return pessoaHabilidade;
    }

    public void setPessoaHabilidade(PessoaHabilidade pessoaHabilidade) {
        this.pessoaHabilidade = pessoaHabilidade;
    }

    public MatriculaFormacao getMatriculaFormacao() {
        return matriculaFormacao;
    }

    public void setMatriculaFormacao(MatriculaFormacao matriculaFormacao) {
        this.matriculaFormacao = matriculaFormacao;
    }

    public List<AreaFormacao> completaAreaFormacao(String parte) {
        return areaFormacaoFacade.completaAreaFormacao(parte);
    }

    public List<Habilidade> completaHabilidade(String parte) {
        return habilidadeFacade.completaHabilidade(parte);
    }

    public void adicionarFormacao() {
        try {
            validaFormacao();
            matriculaFormacao.setPessoaFisica(selecionado);
            selecionado.getFormacoes().add(matriculaFormacao);
            matriculaFormacao = new MatriculaFormacao();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void adicionaHabilidade() {
        if (validaHabilidade()) {
            pessoaHabilidade.setPessoaFisica(selecionado);
            selecionado.getHabilidades().add(pessoaHabilidade);
            pessoaHabilidade = new PessoaHabilidade();
        }
    }

    public void removeFormacao(Object obj) {
        selecionado.getFormacoes().remove(obj);
    }

    public void removeHabilidade(Object obj) {
        selecionado.getHabilidades().remove(obj);
    }

    public boolean validaHabilidade() {
        boolean valida = true;

        if (pessoaHabilidade.getHabilidade() == null) {
            FacesUtil.addCampoObrigatorio("A Habilidade é um campo obrigatório.");
            valida = false;
        }

        for (PessoaHabilidade obj : selecionado.getHabilidades()) {
            if (obj.getHabilidade().equals(pessoaHabilidade.getHabilidade())) {
                valida = false;
                FacesUtil.addOperacaoNaoPermitida("A Habilidade já foi inserida.");
            }
        }
        return valida;
    }

    public void validaFormacao() {
        ValidacaoException ve = new ValidacaoException();
        if (matriculaFormacao.getFormacao().getAreaFormacao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("A Área de Formação é um campo obrigatório.");

        }

        if (matriculaFormacao.getFormacao().getNome() == null || matriculaFormacao.getFormacao().getNome().trim().equals("")) {
            ve.adicionarMensagemDeCampoObrigatorio("O Nome do Curso é um campo obrigatório.");

        }

        if (matriculaFormacao.getFormacao().getPromotorEvento() == null && (matriculaFormacao.getInstituicao() == null || matriculaFormacao.getInstituicao().trim().equals(""))) {
            ve.adicionarMensagemDeCampoObrigatorio("A Instituição é um campo obrigatório.");

        }

        if (matriculaFormacao.getDataInicio() != null && matriculaFormacao.getDataFim() != null) {
            if (matriculaFormacao.getDataInicio().after(matriculaFormacao.getDataFim())) {
                ve.adicionarMensagemDeCampoObrigatorio("A Data de Início deve ser superior a Data de Término");

            }
        }
        if (ve.temMensagens()) {
            throw ve;
        }
    }

    public void navegaAreaFormacao() {
        Web.poeTodosFieldsNaSessao(this);
        Web.navegacao(getUrlAtual(), "/area-de-formacao/novo/");
    }

    public void navegaHabilidade() {
        Web.poeTodosFieldsNaSessao(this);
        Web.navegacao(getUrlAtual(), "/habilidade/novo/");
    }

    public void navegaInstituicao() {
        Web.poeTodosFieldsNaSessao(this);
    }

    public List<SituacaoCadastralPessoa> getSituacoesPesquisaPessoa() {
        return Lists.newArrayList(SituacaoCadastralPessoa.ATIVO);
    }

    public void recuperaAtributosSessaoFormacao() {
        if (matriculaFormacao != null) {
            AreaFormacao areaFormacao = (AreaFormacao) Web.pegaDaSessao(AreaFormacao.class);
            if (areaFormacao != null) {
                matriculaFormacao.getFormacao().setAreaFormacao(areaFormacao);
            }

            Habilidade habilidadeCadastrada = (Habilidade) Web.pegaDaSessao(AreaFormacao.class);
            if (habilidadeCadastrada != null) {
                pessoaHabilidade.setHabilidade(habilidadeCadastrada);
            }
        } else {
//            novoFisico();
            matriculaFormacao = new MatriculaFormacao();
            pessoaHabilidade = new PessoaHabilidade();
        }
    }

    public boolean isVisualizar() {
        return Operacoes.VER.equals(operacao);
    }

    public void confirmarTelefone() {
        if (podeConfirmarTelefone()) {
            selecionado.setTelefones(Util.adicionarObjetoEmLista(selecionado.getTelefones(), telefoneSelecionado));
            cancelarTelefone();
        }
    }

    private boolean podeConfirmarTelefone() {
        if (telefoneSelecionado.getTelefone() == null || telefoneSelecionado.getTelefone().isEmpty()) {
            FacesUtil.addOperacaoNaoPermitida("O campo telefone é obrigatório.");
            return false;
        }
        return true;
    }

    public void cancelarTelefone() {
        telefoneSelecionado = null;
    }

    public void selecionarTelefone(Telefone telefoneSelecionadoNaTela) {
        telefoneSelecionado = (Telefone) Util.clonarObjeto(telefoneSelecionadoNaTela);
    }

    public void removerTelefone(Telefone telefoneSelecionadoNaTela) {
        selecionado.getTelefones().remove(telefoneSelecionadoNaTela);
    }

    public boolean desabilitaCheckBoxTelefonePrincipal() {
        Telefone telefonePrincipalAdicionado = selecionado.getTelefonePrincipalAdicionado();
        if (telefonePrincipalAdicionado != null && telefonePrincipalAdicionado.getPrincipal()) {
            if (telefonePrincipalAdicionado.equals(telefoneSelecionado)) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    public void novaContaCorrenteBancPessoa() {
        contaCorrenteBancPessoaSelecionada = new ContaCorrenteBancPessoa();
        contaCorrenteBancPessoaSelecionada.setPessoa((PessoaFisica) selecionado);
        contaCorrenteBancPessoaSelecionada.setContaCorrenteBancaria(new ContaCorrenteBancaria());
        contaCorrenteBancPessoaSelecionada.setEditando(false);
    }

    public void confirmarContaCorrenteBancPessoa() {
        if (podeConfirmarContaCorrenteBancPessoa()) {
            selecionado.setContaCorrenteBancPessoas(Util.adicionarObjetoEmLista(selecionado.getContaCorrenteBancPessoas(), contaCorrenteBancPessoaSelecionada));
            verificarDuplicidadeDeContaPrincipal();
            cancelarContaCorrenteBancPessoa();
        }
    }

    private void verificarDuplicidadeDeContaPrincipal() {
        int contador = 0;
        for (ContaCorrenteBancPessoa contaCorrenteBancPessoa : selecionado.getContaCorrenteBancPessoas()) {
            if (contaCorrenteBancPessoa.getPrincipal() != null && contaCorrenteBancPessoa.getPrincipal()) {
                contador++;
            }
        }
        if (contador >= 2) {
            FacesUtil.addAtencao("Existem duas contas marcadas como principal. Isso pode gerar problemas em relação a pagamentos de servidores/pensionistas, entre outras transações.");
        }
    }

    private boolean podeConfirmarContaCorrenteBancPessoa() {
        boolean retorno = true;

        try {
            if (banco == null) {
                FacesUtil.addCampoObrigatorio("O campo Banco é obrigatório.");
                retorno = false;
            }

            if (contaCorrenteBancPessoaSelecionada.getAgencia() == null) {
                FacesUtil.addCampoObrigatorio("O campo Agência é obrigatório.");
                retorno = false;
            }

            if (contaCorrenteBancPessoaSelecionada.getContaCorrenteBancaria().getNumeroConta() == null || contaCorrenteBancPessoaSelecionada.getContaCorrenteBancaria().getNumeroConta().isEmpty()) {
                FacesUtil.addCampoObrigatorio("O campo Número da Conta é obrigatório.");
                retorno = false;
            }

            if (contaCorrenteBancPessoaSelecionada.getContaCorrenteBancaria().getDigitoVerificador() == null || contaCorrenteBancPessoaSelecionada.getContaCorrenteBancaria().getDigitoVerificador().isEmpty()) {
                FacesUtil.addCampoObrigatorio("O campo Dígito Verificador é obrigatório.");
                retorno = false;
            }

            if (contaCorrenteBancPessoaSelecionada.getContaCorrenteBancaria().getSituacao() == null) {
                FacesUtil.addCampoObrigatorio("O campo Situação é obrigatório.");
                retorno = false;
            }

            if (contaCorrenteBancPessoaSelecionada.getContaCorrenteBancaria().getModalidadeConta() == null) {
                FacesUtil.addCampoObrigatorio("O campo Modalidade da Conta é obrigatório.");
                retorno = false;
            }
        } finally {
            for (ContaCorrenteBancPessoa c : ((PessoaFisica) selecionado).getContaCorrenteBancPessoas()) {
                if (StringUtil.cortaOuCompletaEsquerda(c.getNumeroConta(), 12, "0").equals(StringUtil.cortaOuCompletaEsquerda(contaCorrenteBancPessoaSelecionada.getContaCorrenteBancaria().getNumeroConta(), 12, "0"))
                    && c.getAgencia().getId().equals(contaCorrenteBancPessoaSelecionada.getContaCorrenteBancaria().getAgencia().getId())
                    && !c.equals(contaCorrenteBancPessoaSelecionada)) {
                    FacesUtil.addOperacaoNaoPermitida("Este número de conta e agência já foram adicionados.");
                    retorno = false;
                }
            }
        }

        return retorno;
    }

    public void cancelarContaCorrenteBancPessoa() {
        if (contaCorrenteBancPessoaSelecionada.isEditando()) {
            contaCorrenteBancPessoaSelecionada.setEditando(false);
            selecionado.setContaCorrenteBancPessoas(Util.adicionarObjetoEmLista(selecionado.getContaCorrenteBancPessoas(), contaCorrenteBancPessoaSelecionada));
        }

        contaCorrenteBancPessoaSelecionada = null;
        cancelarBanco();
    }

    private void cancelarBanco() {
        banco = null;
    }

    public void selecionarContaCorrenteBancPessoa(ContaCorrenteBancPessoa ccbp) {
        contaCorrenteBancPessoaSelecionada = (ContaCorrenteBancPessoa) Util.clonarObjeto(ccbp);
        contaCorrenteBancPessoaSelecionada.setEditando(true);
        banco = contaCorrenteBancPessoaSelecionada.getBanco();

        selecionado.getContaCorrenteBancPessoas().remove(ccbp);
    }

    public void removerContaCorrenteBancPessoa(ContaCorrenteBancPessoa ccbp) {
        contasParaRemoverDoVinculoFP.add(ccbp);
        selecionado.getContaCorrenteBancPessoas().remove(ccbp);
    }

    public void limparDadosContaCorrenteBancPessoa() {
        novaContaCorrenteBancPessoa();
    }

    private void transferirDadosDoCandidatoParaPessoaFisica() {
        selecionado.setNome(classificacaoInscricaoSelecionada.getInscricaoConcurso().getNome());
        selecionado.setDataNascimento(classificacaoInscricaoSelecionada.getInscricaoConcurso().getDataNascimento());
        selecionado.setSexo(classificacaoInscricaoSelecionada.getInscricaoConcurso().getSexo());
        rg = classificacaoInscricaoSelecionada.getInscricaoConcurso().getRg();
        selecionado.setCpf(classificacaoInscricaoSelecionada.getInscricaoConcurso().getCpf());
        selecionado.setDeficienteFisico(classificacaoInscricaoSelecionada.getInscricaoConcurso().getDeficienteFisico());
        classificacaoInscricaoSelecionada.getInscricaoConcurso().getEnderecoCorreio().setPrincipal(Boolean.TRUE);
        selecionado.getEnderecos().add(classificacaoInscricaoSelecionada.getInscricaoConcurso().getEnderecoCorreio());
        selecionado.setEmail(classificacaoInscricaoSelecionada.getInscricaoConcurso().getEmail());

        Telefone tfPrincipal = classificacaoInscricaoSelecionada.getInscricaoConcurso().getTelefonePrincipalAsEntidadeTelefone();
        Telefone tfAlternativo = classificacaoInscricaoSelecionada.getInscricaoConcurso().getTelefoneAlternativoAsEntidadeTelefone();
        if (tfPrincipal != null) {
            selecionado.setTelefones(Util.adicionarObjetoEmLista(selecionado.getTelefones(), tfPrincipal));
        }
        if (tfAlternativo != null) {
            selecionado.setTelefones(Util.adicionarObjetoEmLista(selecionado.getTelefones(), tfAlternativo));
        }
    }

    public List<SelectItem> getModalidadesDaContaBancaria() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, " "));
        for (ModalidadeConta modal : ModalidadeConta.getModalidadesPessoaFisica()) {
            toReturn.add(new SelectItem(modal, modal.getDescricao()));
        }
        return toReturn;
    }

    public void limparInstituicao() {
        matriculaFormacao.getFormacao().setPromotorEvento(null);
        matriculaFormacao.setInstituicao(null);
    }

    public List<AverbacaoContratoPorServico> getBuscarAverbacoesETempoContratoPorIDPessoa() {
        if (selecionado.getId() != null) {
            List<AverbacaoContratoPorServico> averbacaoTempoServicos = pessoaFacade.buscarAverbacoesETempoContratoPorIDPessoa(selecionado.getId());
            for (AverbacaoContratoPorServico averbacao : averbacaoTempoServicos) {
                if (!averbacoesMap.containsKey(averbacao.getId())) {
                    averbacoesMap.put(averbacao.getId(), verificarIdAverbacao(averbacao.getId()));
                }
            }

            return averbacaoTempoServicos;
        } else {
            return Lists.newArrayList();
        }
    }

    public DocumentoPessoalFacade getDocumentoPessoalFacade() {
        return documentoPessoalFacade;
    }

    public Boolean getMenor18Anos() {
        if (selecionado != null) return ValidaPessoa.isMenorDe18Anos(selecionado);
        return false;
    }

    public Map<Long, Boolean> getAverbacoesMap() {
        return averbacoesMap;
    }

    public void setAverbacoesMap(Map<Long, Boolean> averbacoesMap) {
        this.averbacoesMap = averbacoesMap;
    }

    public List<SelectItem> getTipoCondicaoIngresso() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, ""));
        for (TipoCondicaoIngresso tipoCondicaoIngresso : TipoCondicaoIngresso.values()) {
            toReturn.add(new SelectItem(tipoCondicaoIngresso, tipoCondicaoIngresso.getCodigo() + " - " + tipoCondicaoIngresso.getDescricao()));
        }
        return toReturn;
    }

    public PessoaFacade getPessoaFacade() {
        return pessoaFacade;
    }


    public void preencherCTPS() {
        try {
            validarCampoCTPS();
            if (carteiraTrabalho.getCtpsDigital()) {
                String cpf = selecionado.getCpf().replaceAll("[^a-zA-Z0-9]", "");
                carteiraTrabalho.setNumero(cpf.substring(0, 7));
                carteiraTrabalho.setSerie(cpf.substring(7, 11));
            } else {
                carteiraTrabalho.setNumero("");
                carteiraTrabalho.setSerie("");
            }
        } catch (ValidacaoException ve) {
            carteiraTrabalho.setCtpsDigital(Boolean.FALSE);
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public void validarCampoCTPS() {
        ValidacaoException ve = new ValidacaoException();
        if (Strings.isNullOrEmpty(selecionado.getCpf())) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo CPF deve ser informado.");
        }
        ve.lancarException();
    }

    public boolean canValidarCertidaoCasamento() {
        return selecionado.getEstadoCivil() != null && (EstadoCivil.CASADO.equals(selecionado.getEstadoCivil())
            || EstadoCivil.DIVORCIADO.equals(selecionado.getEstadoCivil()) || EstadoCivil.VIUVO.equals(selecionado.getEstadoCivil()));
    }

    public boolean isCasadoOuViuvo() {
        return selecionado.getEstadoCivil() != null && (EstadoCivil.CASADO.equals(selecionado.getEstadoCivil()) || EstadoCivil.VIUVO.equals(selecionado.getEstadoCivil()));
    }

    public boolean verificarIdAverbacao(Long id) {
        if (id != null) {
            AverbacaoTempoServico averbacaoTempoServico = averbacaoTempoServicoFacade.recuperar(id);
            return averbacaoTempoServico != null && averbacaoTempoServico.getNumero() != null;
        }
        return false;
    }
    public void removerFoto() {
        selecionado.setArquivo(null);
        selecionado.getFileUploadPrimeFaces().setFileUploadEvent(null);
        setImagemFoto(null);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("imagem-foto", imagemFoto);
    }
}
