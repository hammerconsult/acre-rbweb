/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.*;
import com.google.common.base.Strings;
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
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.html.HtmlInputText;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Andre Peres
 */
@ManagedBean(name = "pessoaFisicaPensionistaControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoPessoaPensionista", pattern = "/pessoa-pensionista/novo/", viewId = "/faces/rh/administracaodepagamento/pessoapensionista/edita.xhtml"),
    @URLMapping(id = "editarPessoaPensionista", pattern = "/pessoa-pensionista/editar/#{pessoaFisicaPensionistaControlador.id}/", viewId = "/faces/rh/administracaodepagamento/pessoapensionista/edita.xhtml"),
    @URLMapping(id = "listarPessoaPensionista", pattern = "/pessoa-pensionista/listar/", viewId = "/faces/rh/administracaodepagamento/pessoapensionista/lista.xhtml"),
    @URLMapping(id = "verPessoaPensionista", pattern = "/pessoa-pensionista/ver/#{pessoaFisicaPensionistaControlador.id}/", viewId = "/faces/rh/administracaodepagamento/pessoapensionista/visualizar.xhtml")
})
public class PessoaFisicaPensionistaControlador extends PrettyControlador<PessoaFisica> implements Serializable, CRUD {

    private static final Logger logger = LoggerFactory.getLogger(PessoaFisicaPensionistaControlador.class);
    protected ConverterGenerico converterNacionalidade;
    protected ConverterGenerico converterNacionalidadeConjuge;
    protected ConverterAutoComplete converterNaturalidade;
    protected ConverterGenerico converterNivelEscolaridade;
    protected ConverterGenerico converterUf;
    protected ConverterAutoComplete converterAgencia;
    private String pessoaDaContaCorrente;
    private Arquivo arquivo;
    private StreamedContent imagemFoto;
    private int index;
    private boolean formacaoEscolarHabilitada;
    private Banco banco;
    private boolean contaConjunta;
    @EJB
    private CIDFacade cidFacade;
    private ConverterAutoComplete converterCid;
    @EJB
    private ArquivoFacade arquivoFacade;
    @EJB
    private ContaCorrenteBancPessoaFacade contaCorrenteBancPessoaFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private NacionalidadeFacade nacionalidadeFacade;
    @EJB
    private CidadeFacade cidadeFacade;
    private ConverterGenerico converterCidade;
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
    private HierarquiaOrganizacionalFacadeOLD hierarquiaOrganizacionalFacade;
    @EJB
    private ConsultaCepFacade consultaCepFacade;
    private CarteiraTrabalho carteiraTrabalho;
    private List<CarteiraTrabalho> carteiraTrabalhos;
    private List<RG> rgs;
    private RG rg;
    private CarteiraVacinacao carteiraVacinacao;
    private TituloEleitor tituloEleitor;
    private List<TituloEleitor> tituloEleitores;
    private Habilitacao habilitacao;
    private List<Habilitacao> habilitacoes;
    private SituacaoMilitar situacaoMilitar;
    private CertidaoNascimento certidaoNascimento;
    private CertidaoCasamento certidaoCasamento;
    private Telefone telefone = new Telefone();
    private EnderecoCorreio endereco = new EnderecoCorreio();
    private ContaCorrenteBancaria contaCorrenteBancaria;
    private List<ContaCorrenteBancaria> listaContas;
    private Endereco enderecoAux;
    private Telefone telefoneAux;
    private CEPLogradouro logradouro;
    private String complemento;
    private String numero;
    private HtmlInputText textocep;
    private List<CEPLogradouro> logradouros;
    @EJB
    private ContaCorrenteBancariaFacade contaCorrenteBancariaFacade;
    private ContaCorrenteBancPessoa contaCorrenteBancPessoa;
    private List<ContaCorrenteBancPessoa> listaExcluidos;
    private ConverterAutoComplete converterContaCorrenteBancaria;
    @ManagedProperty(name = "cidadeControlador", value = "#{cidadeControlador}")
    private CidadeControlador cidadeControlador;
    @ManagedProperty(name = "uFControlador", value = "#{uFControlador}")
    private UFControlador uFControlador;
    @ManagedProperty(name = "contaCorrenteBancariaControlador", value = "#{contaCorrenteBancariaControlador}")
    private ContaCorrenteBancariaControlador contaCorrenteBancariaControlador;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private HierarquiaOrganizacional unidadeOrganizacionalSelecionada;
    @EJB
    private ClasseCredorFacade classeCredorFacade;
    @EJB
    private ClassificacaoCredorFacade classificacaoCredorFacade;
    private ConverterAutoComplete converterClassificacao;
    private ConverterAutoComplete converterClasseCredor;
    private ClasseCredorPessoa classeCredorSelecionada;
    private ConverterAutoComplete converterHierarquiaOrganizacional;
    private List<ClasseCredorPessoa> listaClasseCredorPessoa;
    @EJB
    private BancoFacade bancoFacade;
    private ConverterAutoComplete converterBanco;
    private ConverterAutoComplete converterCidadeCasamento;
    private PessoaFisicaPensionistaControlador pessoaFisicaPensionistaControlador = this;
    private boolean skip;
    @EJB
    private DocumentoPessoalFacade documentoPessoalFacade;
    private PessoaFisicaCid pessoaFisicaCid;

    public PessoaFisicaPensionistaControlador() {
        super(PessoaFisica.class);
    }

    @URLAction(mappingId = "novoPessoaPensionista", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void nova() {
        novoFisico();
    }

    @URLAction(mappingId = "editarPessoaPensionista", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        selecionarPessoa();
    }

    @URLAction(mappingId = "verPessoaPensionista", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        selecionarPessoa();
    }

    public void selecionarPessoa() {
        contaCorrenteBancPessoa = new ContaCorrenteBancPessoa();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("imagem-foto", null);
        selecionar(null);
    }

    public void novoFisico() {
        imagemFoto = null;
        operacao = Operacoes.NOVO;
        selecionado = new PessoaFisica();
        selecionado.getPerfis().add(PerfilEnum.PERFIL_PENSIONISTA);
        selecionado.setTelefones(new ArrayList<Telefone>());
        selecionado.setEnderecos(new ArrayList<EnderecoCorreio>());
        selecionado.setSituacaoCadastralPessoa(SituacaoCadastralPessoa.ATIVO);
        selecionado.setViveUniaoEstavel(Boolean.FALSE);
        telefone = new Telefone();
        endereco = new EnderecoCorreio();
        rg = new RG();
        rg.setDataRegistro(SistemaFacade.getDataCorrente());
        rg.setPessoaFisica(selecionado);
        tituloEleitor = new TituloEleitor();
        tituloEleitor.setDataRegistro(new Date());
        tituloEleitor.setPessoaFisica(selecionado);
        carteiraTrabalho = new CarteiraTrabalho();
        carteiraTrabalhos = new ArrayList<>();
        habilitacao = new Habilitacao();
        habilitacoes = new ArrayList<>();
        situacaoMilitar = new SituacaoMilitar();
        situacaoMilitar.setDataRegistro(new Date());
        situacaoMilitar.setPessoaFisica(selecionado);
        certidaoNascimento = new CertidaoNascimento();
        certidaoNascimento.setDataRegistro(new Date());
        certidaoNascimento.setPessoaFisica(selecionado);
        certidaoCasamento = new CertidaoCasamento();
        certidaoCasamento.setDataRegistro(new Date());
        certidaoCasamento.setPessoaFisica(selecionado);

        telefoneAux = new Telefone();
        selecionado.setContaCorrenteBancPessoas(new ArrayList<ContaCorrenteBancPessoa>());
        contaCorrenteBancPessoa = new ContaCorrenteBancPessoa();
        listaExcluidos = new ArrayList<>();
        tituloEleitores = new ArrayList<>();

        unidadeOrganizacionalSelecionada = new HierarquiaOrganizacional();
        classeCredorSelecionada = new ClasseCredorPessoa();
        listaClasseCredorPessoa = new ArrayList<>();
        arquivo = new Arquivo();
        pessoaFisicaCid = new PessoaFisicaCid();
        carteiraVacinacao = new CarteiraVacinacao();
        carteiraVacinacao.setDataRegistro(new Date());
        carteiraVacinacao.setPessoaFisica(selecionado);
        addDocumentoPessoa(carteiraVacinacao);

        index = 0;
        formacaoEscolarHabilitada = false;
        selecionado.setNacionalidade(getNacionalidadeBrasileira());
        selecionado.setNacionalidadePai(getNacionalidadeBrasileira());
        selecionado.setNacionalidadeMae(getNacionalidadeBrasileira());
        banco = null;
        selecionado.setNivelEscolaridade(nivelEscolaridadeFacade.lista().get(0));
        cancelarContaCorrente();
        definirSessao();
        carregarDaSessao();
        try {
            if (selecionado.getFileUploadPrimeFaces().getFileUploadEvent() != null) {
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
        PessoaFisicaPensionistaControlador p = (PessoaFisicaPensionistaControlador) Web.pegaDaSessao(PessoaFisicaPensionistaControlador.class);
        if (!isSessao() || p == null) {
            return;
        }
        pessoaFisicaPensionistaControlador = p;
        definirAtributos(p);
    }

    public void definirAtributos(PessoaFisicaPensionistaControlador controler) {
        selecionado = controler.getSelecionado();
        rg = controler.rg;
        tituloEleitor = controler.tituloEleitor;
        habilitacao = controler.habilitacao;
        habilitacoes = controler.habilitacoes;
        tituloEleitores = controler.tituloEleitores;
        carteiraTrabalho = controler.carteiraTrabalho;
        carteiraTrabalhos = controler.carteiraTrabalhos;
        situacaoMilitar = controler.situacaoMilitar;
        certidaoNascimento = controler.certidaoNascimento;
        certidaoCasamento = controler.certidaoCasamento;
        telefone = controler.telefone;
        endereco = controler.endereco;
        contaConjunta = controler.contaConjunta;
        carteiraVacinacao = controler.carteiraVacinacao;
        operacao = controler.operacao;
        index = controler.index;
        banco = controler.banco;
        formacaoEscolarHabilitada = controler.formacaoEscolarHabilitada;
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

    public void removeClasse(ActionEvent evt) {
        ClasseCredorPessoa ccp = (ClasseCredorPessoa) evt.getComponent().getAttributes().get("objeto");
        selecionado.getClasseCredorPessoas().remove(ccp);
    }

    public List<ClasseCredor> completaClasseCredor(String parte) {
        return classeCredorFacade.listaFiltrando(parte.trim(), "descricao");
    }

    public ConverterAutoComplete getConverterClasseCredor() {
        if (converterClasseCredor == null) {
            converterClasseCredor = new ConverterAutoComplete(ClasseCredor.class, classeCredorFacade);
        }
        return converterClasseCredor;
    }

    public Boolean liberaContaPrincipal() {
        Boolean controle = true;
        if (selecionado != null) {
            if (selecionado.getContaCorrenteBancPessoas() != null) {
                for (ContaCorrenteBancPessoa ccbf : selecionado.getContaCorrenteBancPessoas()) {
                    if (ccbf.getPrincipal() != null && ccbf.getPrincipal()) {
                        controle = false;
                    }
                }
            }
        }
        return controle;
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

    public Boolean liberaTelefonePrincipal() {
        Boolean retorno = true;
        if (selecionado != null) {
            for (Telefone fone : selecionado.getTelefones()) {
                if (fone.getPrincipal()) {
                    retorno = false;
                }
            }
        }
        return retorno;
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

    public Telefone getTelefone() {
        return telefone;
    }

    public void setTelefone(Telefone telefone) {
        this.telefone = telefone;
    }

    public ContaCorrenteBancaria getContaCorrenteBancaria() {
        return contaCorrenteBancaria;
    }

    public void setContaCorrenteBancaria(ContaCorrenteBancaria contaCorrenteBancaria) {
        this.contaCorrenteBancaria = contaCorrenteBancaria;
    }

    public EnderecoCorreio getEndereco() {
        return endereco;
    }

    public void setEndereco(EnderecoCorreio endereco) {
        this.endereco = endereco;
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

    public Endereco getEnderecoAux() {
        return enderecoAux;
    }

    public void setEnderecoAux(Endereco enderecoAux) {
        this.enderecoAux = enderecoAux;
    }

    public Telefone getTelefoneAux() {
        return telefoneAux;
    }

    public void setTelefoneAux(Telefone telefoneAux) {
        this.telefoneAux = telefoneAux;
    }

    public List<Telefone> getTelefones() {
        return selecionado.getTelefones();
    }

    public List<EnderecoCorreio> getEnderecos() {
        return selecionado.getEnderecos();
    }

    public CidadeControlador getCidadeControlador() {
        return cidadeControlador;
    }

    public void setCidadeControlador(CidadeControlador cidadeControlador) {
        this.cidadeControlador = cidadeControlador;
    }

    public ContaCorrenteBancariaControlador getContaCorrenteBancariaControlador() {
        return contaCorrenteBancariaControlador;
    }

    public void setContaCorrenteBancariaControlador(ContaCorrenteBancariaControlador contaCorrenteBancariaControlador) {
        this.contaCorrenteBancariaControlador = contaCorrenteBancariaControlador;
    }

    public UFControlador getuFControlador() {
        return uFControlador;
    }

    public void setuFControlador(UFControlador uFControlador) {
        this.uFControlador = uFControlador;
    }

    public void novoFone() {
        if (validaTelefone()) {
            telefone.setPessoa(selecionado);
            selecionado.getTelefones().add(telefone);
            telefone = new Telefone();
        }
    }

    public void novoRg() {
        if (rg != null && !rg.getNumero().isEmpty()) {
            PessoaFisica pf = selecionado;
            rg.setDataRegistro(new Date());
            rg.setPessoaFisica(pf);
            if (pf.getDocumentosPessoais().contains(rg)) {
                pf.getDocumentosPessoais().set(pf.getDocumentosPessoais().indexOf(rg), rg);
            } else {
                pf.getDocumentosPessoais().add(rg);
            }
        }
    }

    public void novoCertidaoNascimento() {
        PessoaFisica pf = selecionado;
        if (isCerticaoNascimentoPreenchida()) {
            certidaoNascimento.setDataRegistro(new Date());
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
            && certidaoNascimento.getNomeCartorio() != null && !certidaoNascimento.getNomeCartorio().trim().isEmpty()
            && certidaoNascimento.getNumeroRegistro() != null
            && certidaoNascimento.getNumeroFolha() != null && !certidaoNascimento.getNumeroFolha().trim().isEmpty()
            && certidaoNascimento.getNumeroLivro() != null && !certidaoNascimento.getNumeroLivro().trim().isEmpty()) {
            return true;
        }
        return false;
    }

    private void novoPessoaFisicaCID() {
        if (pessoaFisicaCid != null) {
            pessoaFisicaCid.setPessoaFisica(esteSelecionado());
            esteSelecionado().setPessoaFisicaCid(pessoaFisicaCid);
        }
    }

    public void novoCertidaoCasamento() {
        if (certidaoCasamento != null) {
            PessoaFisica pf = selecionado;
            certidaoCasamento.setDataRegistro(new Date());
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
            tituloEleitor.setDataRegistro(new Date());
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

    public void novaHabilitacao() {
        if (validaHabilitacao()) {
            PessoaFisica pf = selecionado;
            habilitacao.setDataRegistro(new Date());
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
            situacaoMilitar.setDataRegistro(new Date());
            situacaoMilitar.setPessoaFisica((PessoaFisica) selecionado);
            if (pf.getDocumentosPessoais().contains(situacaoMilitar)) {
                pf.getDocumentosPessoais().set(pf.getDocumentosPessoais().indexOf(situacaoMilitar), situacaoMilitar);
            } else {
                pf.getDocumentosPessoais().add(situacaoMilitar);
            }
        }
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

    public void removeEndereco(ActionEvent evento) {
        selecionado.getEnderecos().remove((EnderecoCorreio) evento.getComponent().getAttributes().get("vendereco"));
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

    public void setaTelefone(ActionEvent evento) {
        setTelefoneAux((Telefone) evento.getComponent().getAttributes().get("alteraFone"));
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
        for (NivelEscolaridade object : nivelEscolaridadeFacade.buscarTodosNiveisEscolaridadeOrdenadoPeloCodigo()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
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
        List<SelectItem> toReturn = new ArrayList<>();
        for (RacaCor object : RacaCor.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
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

        if (!selecionado.getPerfis().contains(PerfilEnum.PERFIL_PENSIONISTA)) {
            selecionado.getPerfis().add(PerfilEnum.PERFIL_PENSIONISTA);
        }
        carteiraVacinacao = new CarteiraVacinacao();
        enderecoAux = new Endereco();
        telefoneAux = new Telefone();
        telefone = new Telefone();
        endereco = new EnderecoCorreio();
        habilitacao = new Habilitacao();
        habilitacoes = new ArrayList<>();
        carteiraTrabalho = new CarteiraTrabalho();
        carteiraTrabalhos = new ArrayList<>();
        enderecoAux = new Endereco();
        classeCredorSelecionada = new ClasseCredorPessoa();
        listaClasseCredorPessoa = new ArrayList<>();
//        conselho = new ConselhoClasseContratoFP();


        if (selecionado instanceof PessoaFisica) {
            if (verificaRG() == null) {
                rg = new RG();
                rg.setDataRegistro(new Date());
                rg.setPessoaFisica((PessoaFisica) selecionado);
                addDocumentoPessoa(rg);
            }

            if (verificaCarteiraVacinacao() == null) {
                carteiraVacinacao = new CarteiraVacinacao();
                carteiraVacinacao.setDataRegistro(new Date());
                carteiraVacinacao.setPessoaFisica((PessoaFisica) selecionado);
                addDocumentoPessoa(carteiraVacinacao);
            }
            if (verificaTitulo() == null) {
                tituloEleitor = new TituloEleitor();
                tituloEleitor.setDataRegistro(new Date());
                tituloEleitor.setPessoaFisica((PessoaFisica) selecionado);
                addDocumentoPessoa(tituloEleitor);
            }
            if (verificaMilitar() == null) {
                situacaoMilitar = new SituacaoMilitar();
                situacaoMilitar.setDataRegistro(new Date());
                situacaoMilitar.setPessoaFisica((PessoaFisica) selecionado);
                addDocumentoPessoa(situacaoMilitar);
            }
            if (verificaCertidaoNascimento() == null) {
                certidaoNascimento = new CertidaoNascimento();
                certidaoNascimento.setDataRegistro(new Date());
                certidaoNascimento.setPessoaFisica((PessoaFisica) selecionado);
                addDocumentoPessoa(certidaoNascimento);
            }
            if (verificaCertidaoCasamento() == null) {
                certidaoCasamento = new CertidaoCasamento();
                certidaoCasamento.setDataRegistro(new Date());
                certidaoCasamento.setPessoaFisica((PessoaFisica) selecionado);
                addDocumentoPessoa(certidaoCasamento);
            }

            PessoaFisica pf = selecionado;
            for (DocumentoPessoal doc : pf.getDocumentosPessoais()) {
                if (doc instanceof RG) {
                    rg = (RG) doc;
                } else if (doc instanceof TituloEleitor) {
                    tituloEleitor = (TituloEleitor) doc;
                } else if (doc instanceof CarteiraTrabalho) {
                    carteiraTrabalhos.add((CarteiraTrabalho) doc);
                } else if (doc instanceof Habilitacao) {
                    habilitacoes.add((Habilitacao) doc);
                } else if (doc instanceof SituacaoMilitar) {
                    situacaoMilitar = (SituacaoMilitar) doc;
                } else if (doc instanceof CertidaoNascimento) {
                    certidaoNascimento = (CertidaoNascimento) doc;
                } else if (doc instanceof CertidaoCasamento) {
                    certidaoCasamento = (CertidaoCasamento) doc;
                } else if (doc instanceof CarteiraVacinacao) {
                    carteiraVacinacao = (CarteiraVacinacao) doc;
                }
            }
        }

        listaExcluidos = new ArrayList<>();
        cancelarContaCorrente();
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

    public List<RG> getRgs() {
        return rgs;
    }

    public void setRgs(List<RG> rgs) {
        this.rgs = rgs;
    }

    public List<ContaCorrenteBancaria> getListaContas() {
        listaContas.add(pessoaFacade.recuperarConta(contaCorrenteBancaria));
        return listaContas;
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
        consultaCepFacade.atualizarLogradouros(endereco);
    }

    @Override
    public void salvar() {
        try {
            if (selecionado instanceof PessoaFisica) {
                novoRg();
                novoTitulo();
                novoMilitar();
                novoCertidaoNascimento();
                novoCertidaoCasamento();
                novoPessoaFisicaCID();

                validarCampos();
            }

            if (isOperacaoNovo()) {
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

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Salvo com Sucesso", ""));
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

    }

    public void cancelarContaCorrente() {
        contaCorrenteBancaria = null;
        contaCorrenteBancPessoa = null;
    }

    public void adicionarContaCorrenteBancPessoa() {
        try {
            validarCamposObrigatoriosInformacoesBancarias();
            if (selecionado.getContaCorrenteBancPessoas().contains(contaCorrenteBancPessoa)) {
                int i = selecionado.getContaCorrenteBancPessoas().indexOf(contaCorrenteBancPessoa);
                selecionado.getContaCorrenteBancPessoas().set(i, contaCorrenteBancPessoa);
            } else {
                contaCorrenteBancPessoa.setDataRegistro(new Date());
                contaCorrenteBancPessoa.setPessoa(selecionado);
                selecionado.setContaCorrenteBancPessoas(Util.adicionarObjetoEmLista(selecionado.getContaCorrenteBancPessoas(), contaCorrenteBancPessoa));
                banco = null;
            }
            verificarDuplicidadeDeContaPrincipal();
            cancelarContaCorrente();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
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

    private void validarCamposObrigatoriosInformacoesBancarias() {
        ValidacaoException ve = new ValidacaoException();
        if (banco == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Banco deve ser informado.");
        }
        ContaCorrenteBancaria contaPessoa = contaCorrenteBancPessoa.getContaCorrenteBancaria();
        if (contaPessoa.getAgencia() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Agência deve ser informado.");
        }
        if (Strings.isNullOrEmpty(contaPessoa.getNumeroConta())) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Número da Conta Corrente deve ser informado.");
        }
        if (Strings.isNullOrEmpty(contaPessoa.getDigitoVerificador())) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Dígito Verificador deve ser informado.");
        }
        if (contaPessoa.getModalidadeConta() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Modalidade da Conta deve ser informado.");
        }
        ve.lancarException();
        for (ContaCorrenteBancPessoa c : selecionado.getContaCorrenteBancPessoas()) {
            if (StringUtil.cortarOuCompletarEsquerda(c.getContaCorrenteBancaria().getNumeroConta(), 12, "0").equals(StringUtil.cortarOuCompletarEsquerda(contaCorrenteBancPessoa.getNumeroConta(), 12, "0"))
                && !c.equals(contaCorrenteBancPessoa)
                && c.getContaCorrenteBancaria().getAgencia().getId().equals(contaCorrenteBancPessoa.getContaCorrenteBancaria().getAgencia().getId())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Este número de conta e agência já foram adicionados.");
                break;
            }
        }
        ve.lancarException();
    }

    public void selecionarContaCorrenteBancPessoa(ContaCorrenteBancPessoa ccbp) {
        contaCorrenteBancPessoa = (ContaCorrenteBancPessoa) Util.clonarObjeto(ccbp);
        contaCorrenteBancPessoa.setEditando(true);
        banco = contaCorrenteBancPessoa.getBanco();
    }


    public void removerContaCorrenteBancPessoa(ContaCorrenteBancPessoa c) {
        selecionado.getContaCorrenteBancPessoas().remove(c);
        if (selecionado.getContaCorrentePrincipal() != null && selecionado.getContaCorrentePrincipal().equals(c)) {
            selecionado.setContaCorrentePrincipal(null);
        }
        if (c != null) {
            listaExcluidos.add(c);
        }
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

    public void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        ValidaPessoa.validar(selecionado, PerfilEnum.PERFIL_PENSIONISTA, ve);

        if (!selecionado.getCpf_Cnpj().equals("")) {
            if (Util.valida_CpfCnpj(String.valueOf(selecionado.getCpf_Cnpj()))) {
                if (pessoaFacade.hasOutraPessoaComMesmoCpf(selecionado, false)) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Este CPF já pertence a " + getDescricaoConcatenada(pessoaFacade.getPessoasComMesmoCPF(selecionado)));
                }
            } else {
                ve.adicionarMensagemDeOperacaoNaoPermitida("CPF inválido.");
            }
        }

        if (selecionado.getEnderecos().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("É necessário informar pelo menos um endereço.");
        }

        if (selecionado.getContaCorrenteBancPessoas() == null || selecionado.getContaCorrenteBancPessoas().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("É necessário informar pelo menos uma conta bancária.");
        }

        if (selecionado.getNacionalidade() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a Nacionalidade.");
        }

        if (selecionado.getNivelEscolaridade() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Nível de Escolaridade.");
        }

        ve.lancarException();

        validarCamposDeAcordoComEstadoCivil(ve);

        ve.lancarException();
    }

    private void validarCamposDeAcordoComEstadoCivil(ValidacaoException ve) {
        if (EstadoCivil.DIVORCIADO.equals(selecionado.getEstadoCivil()) || EstadoCivil.CASADO.equals(selecionado.getEstadoCivil())) {
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
            if (EstadoCivil.CASADO.equals(selecionado.getEstadoCivil()) && certidaoCasamento.getDataCasamento() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Data Casamento na aba Certidão de Casamento deve ser informado!");
            }
            if (Strings.isNullOrEmpty(certidaoCasamento.getNomeConjuge().trim())) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Nome do Cônjuge na aba Certidão de Casamento deve ser informado!");
            }
            if (certidaoCasamento.getNaturalidadeConjuge() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Naturalidade do Cônjuge na aba Certidão de Casamento deve ser informado!");
            }
            if (Strings.isNullOrEmpty(certidaoCasamento.getEstado().trim())) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo UF na aba Certidão de Casamento deve ser informado!");
            }
            if (certidaoCasamento.getDataNascimentoConjuge() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Data de Nascimento do Cônjuge na aba Certidão de Casamento deve ser informado!");
            }
            if (EstadoCivil.CASADO.equals(selecionado.getEstadoCivil()) && Strings.isNullOrEmpty(certidaoCasamento.getLocalTrabalhoConjuge().trim())) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Local Trabalho do Cônjuge na aba Certidão de Casamento deve ser informado!");
            }
        }

        if (EstadoCivil.VIUVO.equals(selecionado.getEstadoCivil())) {
            if (certidaoCasamento.getDataObito() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Data do Óbito na aba Certidão de Casamento/Informações do Óbito deve ser informado!");
            }
            if (Strings.isNullOrEmpty(certidaoCasamento.getCartorio().trim())) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Cartório na aba Certidão de Casamento/Informações do Óbito deve ser informado!");
            }
        }
        if (!canValidarCertidaoCasamento()) {
            if (Strings.isNullOrEmpty(certidaoNascimento.getNomeCartorio().trim())) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Nome do Cartório na aba Certidão de Nascimento deve ser informado!");
            }
            if (certidaoNascimento.getCidade() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Cidade na aba Certidão de Nascimento deve ser informado!.");
            }
        }
    }


    public ContaCorrenteBancPessoa getContaCorrenteBancPessoa() {
        return contaCorrenteBancPessoa;
    }

    public void setContaCorrenteBancPessoa(ContaCorrenteBancPessoa contaCorrenteBancPessoa) {
        this.contaCorrenteBancPessoa = contaCorrenteBancPessoa;
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
            selecionado.getFileUploadPrimeFaces().setFileUploadEvent(event);
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
            FacesUtil.addMessageWarn("Formulario:iCPF", "Atenção!", "O CPF digitado é inválido");
        } else if (pessoaFacade.hasOutraPessoaComMesmoCpf(selecionado, false)) {
            FacesUtil.addMessageWarn("Formulario:iCPF", "Atenção!", "O CPF digitado já pertence a " + getDescricaoConcatenada(pessoaFacade.getPessoasComMesmoCPF((PessoaFisica) selecionado)));
        }
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
            descricaoCompleta = " " + pf.getNome() + " " + pf.getCpf() + " " + pf.getPerfis();
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
        return agenciaFacade.listaFiltrandoPorBanco(parte.trim(), banco);
    }

    public List<SelectItem> getModalidades() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, " "));
        for (ModalidadeConta object : ModalidadeConta.values()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public void novoTrabalho() {
        if (validaCarteiraTrabalho()) {
            PessoaFisica pf = selecionado;
            carteiraTrabalho.setDataRegistro(new Date());
            carteiraTrabalho.setPessoaFisica(pf);

            carteiraTrabalhos.add(carteiraTrabalho);
            if (operacao == Operacoes.NOVO) {
            }
            if (pf.getDocumentosPessoais().contains(carteiraTrabalho)) {
                pf.getDocumentosPessoais().set(pf.getDocumentosPessoais().indexOf(carteiraTrabalho), carteiraTrabalho);
            } else {
                pf.getDocumentosPessoais().add(carteiraTrabalho);
            }
            carteiraTrabalho = new CarteiraTrabalho();
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
        if (!pessoaFacade.validaPisPasep((PessoaFisica) selecionado, carteiraTrabalho.getPisPasep())) {
            FacesUtil.addOperacaoNaoPermitida("Já existe um PIS/PASEP com esse número!");
            return;
        }
    }

    private boolean validaCarteiraTrabalho() {
        boolean retorno = true;

        if (!ArquivoPisPasepFacade.isValid(carteiraTrabalho.getPisPasep())) {
            FacesUtil.addMessageWarn("Formulario:pisPasep", "", "O campo PIS/PASEP é inválido!");
            retorno = false;
        }
        if (!pessoaFacade.validaPisPasep((PessoaFisica) selecionado, carteiraTrabalho.getPisPasep())) {
            FacesUtil.addMessageWarn("Formulario:pisPasep", "", "Já existe um PIS/PASEP com esse número!");
            retorno = false;
        }
        if (carteiraTrabalho.getNumero().equals("")) {
            FacesUtil.addMessageWarn("Formulario:pisPasep", "", "O campo Número da Carterira de trabalho é obrigatório");
            retorno = false;
        }
        if (carteiraTrabalho.getSerie().equals("")) {
            FacesUtil.addMessageWarn("Formulario:pisPasep", "", "O campo Série da Carterira de trabalho é obrigatório");
            retorno = false;
        }
        if (carteiraTrabalho.getOrgaoExpedidor().equals("")) {
            FacesUtil.addMessageWarn("Formulario:pisPasep", "", "O campo Órgão Expedidor da Carterira de trabalho é obrigatório");
            retorno = false;
        }
        if (carteiraTrabalho.getDataEmissao() == null) {
            FacesUtil.addMessageWarn("Formulario:pisPasep", "", "O campo Data Emissão da Carterira de trabalho é obrigatório");
            retorno = false;
        }
        if (carteiraTrabalho.getUf() == null) {
            FacesUtil.addMessageWarn("Formulario:pisPasep", "", "O campo Estado da Carterira de trabalho é obrigatório");
            retorno = false;
        }
        if (carteiraTrabalho.getBancoPisPasep() == null) {
            FacesUtil.addMessageWarn("Formulario:pisPasep", "", "O campo Banco da Carterira de trabalho é obrigatório");
            retorno = false;
        }
        for (CarteiraTrabalho ct : carteiraTrabalhos) {
            if (ct.getPisPasep().equals(carteiraTrabalho.getPisPasep())) {
                FacesUtil.addMessageWarn("Formulario:pisPasep", "", "Já existe um PIS/PASEP com esse número na lista!");
                retorno = false;
            }
        }

        return retorno;
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

        if (endereco.getCep() == null || endereco.getCep().trim().isEmpty()) {
            FacesUtil.addCampoObrigatorio("O campo CEP deve ser informado! ");
            retorno = false;
        } else if (StringUtil.removeCaracteresEspeciaisSemEspaco(endereco.getCep()).length() != 8) {
            FacesUtil.addOperacaoNaoPermitida("Informe o CEP corretamente.");
            retorno = false;
        }
        if (endereco.getUf() == null || endereco.getUf().trim().isEmpty()) {
            FacesUtil.addCampoObrigatorio("O campo Estado deve ser informado ");
            retorno = false;
        } else if (endereco.getUf().length() != 2) {
            FacesUtil.addOperacaoNaoPermitida("O campo Estado é uma Sigla é deve ter 2 caractéres");
            retorno = false;
        }
        if (endereco.getLocalidade() == null || endereco.getLocalidade().trim().isEmpty()) {
            FacesUtil.addCampoObrigatorio("O campo Cidade deve ser informado ");
            retorno = false;
        }
        if (endereco.getBairro() == null || endereco.getBairro().trim().isEmpty()) {
            FacesUtil.addCampoObrigatorio("O campo Bairro deve ser informado ");
            retorno = false;
        }
        if (endereco.getLogradouro() == null || endereco.getLogradouro().trim().isEmpty()) {
            FacesUtil.addCampoObrigatorio("O campo Logradouro deve ser informado");
            retorno = false;
        }

        return retorno;
    }

    public boolean validaTelefone() {
        if (telefone.getTelefone() == null || telefone.getTelefone().isEmpty()) {
            FacesUtil.addMessageError("Impossível continuar!", "Informe o número do telefone.");
            return false;
        }
        return true;
    }

    public void mudouCampoDeficienteFisico(ValueChangeEvent evento) {
        TipoDeficiencia t = (TipoDeficiencia) evento.getNewValue();
        esteSelecionado().setTipoDeficiencia(t);
        if (esteSelecionado().possuiDeficienciaFisica()) {
            esteSelecionado().setPessoaFisicaCid(new PessoaFisicaCid());
        }

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
        if (contaCorrenteBancPessoa.getContaCorrenteBancaria().getAgencia() != null
            && contaCorrenteBancPessoa.getContaCorrenteBancaria().getAgencia().getId() != null
            && contaCorrenteBancPessoa.getContaCorrenteBancaria().getNumeroConta() != null
            && !contaCorrenteBancPessoa.getContaCorrenteBancaria().getNumeroConta().trim().isEmpty()) {
            ContaCorrenteBancaria contaCorrenteExistente = contaCorrenteBancariaFacade.contaCorrenteExistente(contaCorrenteBancPessoa.getContaCorrenteBancaria());

            if (contaCorrenteExistente != null) {
                contaCorrenteExistente.setNumeroConta(contaCorrenteBancPessoa.getContaCorrenteBancaria().getNumeroConta());
                contaCorrenteBancPessoa.setContaCorrenteBancaria(contaCorrenteExistente);
            }

            if (contaCorrenteExistente == null) {
                contaConjunta = false;
                pessoaDaContaCorrente = "";
                return;
            }

            contaCorrenteBancaria = contaCorrenteExistente;
            List<Pessoa> pessoas = contaCorrenteBancPessoaFacade.listaPorContaCorrenteBancaria(contaCorrenteBancPessoa.getContaCorrenteBancaria());

            if (pessoas.contains(selecionado)) {
                contaConjunta = false;
                pessoaDaContaCorrente = "";
                return;
            }

            if (pessoas.size() == 1) {
                contaConjunta = false;
                pessoaDaContaCorrente = pessoas.get(0).getNome();
                mostraDialogContaExistente();
            } else if (pessoas.size() == 2) {
                contaConjunta = true;
                pessoaDaContaCorrente = pessoas.get(0).getNome() + " e " + pessoas.get(1).getNome();
                mostraDialogContaExistente();
            }

        }
    }

    public void mostraDialogContaExistente() {
        RequestContext.getCurrentInstance().update("formDialogContaExistente");
        RequestContext.getCurrentInstance().execute("dialogContaExistente.show()");
    }

    public void limpaAgencia() {
        contaCorrenteBancPessoa.getContaCorrenteBancaria().setAgencia(new Agencia());
    }

    public void limpaDadosContaCorrente() {
        contaCorrenteBancPessoa.getContaCorrenteBancaria().setNumeroConta("");
        contaCorrenteBancPessoa.getContaCorrenteBancaria().setDigitoVerificador("");
        contaCorrenteBancPessoa.getContaCorrenteBancaria().setSituacao(null);
        contaCorrenteBancPessoa.getContaCorrenteBancaria().setModalidadeConta(null);
    }

    public boolean mostraImagemSilhueta() {
        try {
            return ((StreamedContent) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("imagem-foto")).getName().trim().length() <= 0;
        } catch (Exception e) {
            return true;
        }
    }

    public PessoaFisicaPensionistaControlador getControlador() {
        return pessoaFisicaPensionistaControlador;
    }

    public void setControlador(PessoaFisicaPensionistaControlador pessoaFisicaPensionistaControlador) {
        this.pessoaFisicaPensionistaControlador = pessoaFisicaPensionistaControlador;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/pessoa-pensionista/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public void novaContaBancaria() {
        contaCorrenteBancPessoa = new ContaCorrenteBancPessoa();
        contaCorrenteBancPessoa.setPessoa((PessoaFisica) selecionado);
        contaCorrenteBancPessoa.setContaCorrenteBancaria(new ContaCorrenteBancaria());
        contaCorrenteBancPessoa.setEditando(false);
    }

    public DocumentoPessoalFacade getDocumentoPessoalFacade() {
        return documentoPessoalFacade;
    }

    public boolean canValidarCertidaoCasamento() {
        return selecionado.getEstadoCivil() != null && (EstadoCivil.CASADO.equals(selecionado.getEstadoCivil())
            || EstadoCivil.DIVORCIADO.equals(selecionado.getEstadoCivil()) || EstadoCivil.VIUVO.equals(selecionado.getEstadoCivil()));
    }
}
