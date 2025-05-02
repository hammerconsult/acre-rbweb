/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.*;
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
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.UIOutput;
import javax.faces.component.html.HtmlInputText;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author jaimaum
 */
@ManagedBean
@SessionScoped
public class PessoaControlador extends SuperControladorCRUD implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(PessoaControlador.class);

    private Pessoa pessoa;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private NacionalidadeFacade nacionalidadeFacade;
    protected ConverterGenerico converterNacionalidade;
    protected ConverterGenerico converterNacionalidadeConjuge;
    @EJB
    private CidadeFacade cidadeFacade;
    private ConverterGenerico converterCidade;
    @EJB
    private EscritorioContabilFacade escritorioContabilFacade;
    protected ConverterGenerico converterEscritorioContabil;
    @EJB
    private CidadeFacade naturalidadeFacade;
    protected ConverterAutoComplete converterNaturalidade;
    @EJB
    private NivelEscolaridadeFacade nivelEscolaridadeFacade;
    protected ConverterGenerico converterNivelEscolaridade;
    @EJB
    private UFFacade ufFacade;
    protected ConverterGenerico converterUf;
    @EJB
    private AgenciaFacade agenciaFacade;
    @EJB
    private HierarquiaOrganizacionalFacadeOLD hierarquiaOrganizacionalFacade;
    protected ConverterAutoComplete converterAgencia;
    private CarteiraTrabalho carteiraTrabalho;
    private List<CarteiraTrabalho> carteiraTrabalhos;
    private List<RG> rgs;
    private RG rg;
    private CarteiraVacinacao carteiraVacinacao;
    private List<CarteiraVacinacao> carteiraVacinacaos;
    private TituloEleitor tituloEleitor;
    private List<TituloEleitor> tituloEleitores;
    private Habilitacao habilitacao;
    private List<Habilitacao> habilitacoes;
    private SituacaoMilitar situacaoMilitar;
    private List<SituacaoMilitar> situacaoMilitares;
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
    private String cep;
    private HtmlInputText textocep;
    @EJB
    private ConsultaCepFacade consultaCepFacade;
    private List<CEPLogradouro> logradouros;
    @EJB
    private ContaCorrenteBancariaFacade contaCorrenteBancariaFacade;
    private ContaCorrenteBancPessoa contaCorrenteBancPessoa;
    private List<ContaCorrenteBancPessoa> listaExcluidos;
    private ConverterAutoComplete converterContaCorrenteBancaria;
    private PerfilEnum perfilEnum;
    private UIComponent componente;
    private br.com.webpublico.util.PerfilEnumConverter perfilEnumConveter = new PerfilEnumConverter();
    @ManagedProperty(name = "cidadeControlador", value = "#{cidadeControlador}")
    private CidadeControlador cidadeControlador;
    @ManagedProperty(name = "nacionalidadeControlador", value = "#{nacionalidadeControlador}")
    private NacionalidadeControlador nacionalidadeControlador;
    @ManagedProperty(name = "uFControlador", value = "#{uFControlador}")
    private UFControlador uFControlador;
    @ManagedProperty(name = "contaCorrenteBancariaControlador", value = "#{contaCorrenteBancariaControlador}")
    private ContaCorrenteBancariaControlador contaCorrenteBancariaControlador;
    @ManagedProperty(name = "classeCredorControlador", value = "#{classeCredorControlador}")
    private ClasseCredorControlador classeCredorControlador;
    @ManagedProperty(name = "classificacaoCredorControlador", value = "#{classificacaoCredorControlador}")
    private ClassificacaoCredorControlador classificacaoCredorControlador;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private Boolean validouCamposObrigatorios;
    private PerfilEnum perfilDependente;
    private PerfilEnum perfilAdministrativo;
    private PerfilEnum perfilRH;
    private PerfilEnum perfilTributario;
    private PerfilEnum perfilPensionista;
    private PerfilEnum perfilCredor;
    private ConverterAutoComplete converterEspecialidades;
    private Boolean mostraPainelEspecialidades;
    private HierarquiaOrganizacional unidadeOrganizacionalSelecionada;
    @EJB
    private ClasseCredorFacade classeCredorFacade;
    @EJB
    private ClassificacaoCredorFacade classificacaoCredorFacade;
    private ConverterAutoComplete converterClassificacao;
    private ConverterAutoComplete converterClasseCredor;
    //private PessoaClassificacaoCredor pessoaClassificacaoCredorSelecionada;
    private ClasseCredorPessoa classeCredorSelecionada;
    private ConverterAutoComplete converterHierarquiaOrganizacional;
    private List<ClasseCredorPessoa> listaClasseCredorPessoa;
    private FileUploadEvent fileUploadEvent;
    private Arquivo arquivo;
    private StreamedContent imagemFoto;
    @EJB
    private ArquivoFacade arquivoFacade;
    private boolean operacaoRBTrans;
    private ContaCorrenteBancaria contaCorrenteBancariaCadastravel;

    public void novoFisico() {
        reiniciaComponente();
        operacao = Operacoes.NOVO;
        selecionado = new PessoaFisica();
        pessoa = (PessoaFisica) selecionado;
        pessoa.getPerfis().add(perfilEnum);
        pessoa.setTelefones(new ArrayList<Telefone>());
//        pessoa.setContasCorrentesBancarias(new ArrayList<ContaCorrenteBancaria>());
        pessoa.setEnderecos(new ArrayList<EnderecoCorreio>());
        pessoa.setSituacaoCadastralPessoa(SituacaoCadastralPessoa.ATIVO);
        telefone = new Telefone();
        endereco = new EnderecoCorreio();
        rg = new RG();
        rg.setDataRegistro(SistemaFacade.getDataCorrente());
        rg.setPessoaFisica((PessoaFisica) pessoa);
        tituloEleitor = new TituloEleitor();
        tituloEleitor.setDataRegistro(new Date());
        tituloEleitor.setPessoaFisica((PessoaFisica) pessoa);
        carteiraTrabalho = new CarteiraTrabalho();
        carteiraTrabalhos = new ArrayList<CarteiraTrabalho>();
        habilitacao = new Habilitacao();
        habilitacoes = new ArrayList<Habilitacao>();
        situacaoMilitar = new SituacaoMilitar();
        situacaoMilitar.setDataRegistro(new Date());
        situacaoMilitar.setPessoaFisica((PessoaFisica) pessoa);
        certidaoNascimento = new CertidaoNascimento();
        certidaoNascimento.setDataRegistro(new Date());
        certidaoNascimento.setPessoaFisica((PessoaFisica) pessoa);
        certidaoCasamento = new CertidaoCasamento();
        certidaoCasamento.setDataRegistro(new Date());
        certidaoCasamento.setPessoaFisica((PessoaFisica) pessoa);
        contaCorrenteBancaria = new ContaCorrenteBancaria();
        contaCorrenteBancariaCadastravel = new ContaCorrenteBancaria();
        telefoneAux = new Telefone();
        pessoa.setContaCorrenteBancPessoas(new ArrayList<ContaCorrenteBancPessoa>());
        contaCorrenteBancPessoa = new ContaCorrenteBancPessoa();
        listaExcluidos = new ArrayList<ContaCorrenteBancPessoa>();
        tituloEleitores = new ArrayList<TituloEleitor>();
        unidadeOrganizacionalSelecionada = new HierarquiaOrganizacional();
        //pessoaClassificacaoCredorSelecionada = new PessoaClassificacaoCredor();
        classeCredorSelecionada = new ClasseCredorPessoa();
        listaClasseCredorPessoa = new ArrayList<ClasseCredorPessoa>();
        arquivo = new Arquivo();
        fileUploadEvent = null;
        carteiraVacinacaos = new ArrayList<CarteiraVacinacao>();
        carteiraVacinacao = new CarteiraVacinacao();
        carteiraVacinacao.setDataRegistro(new Date());
        carteiraVacinacao.setPessoaFisica((PessoaFisica) pessoa);
        addDocumentoPessoa(carteiraVacinacao);

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

    public void adicionaClasseCredor() {
        if (listaClasseCredorPessoa.contains(classeCredorSelecionada)) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, " A Classe Credora ja existe na lista!", " "));
        } else if (classeCredorSelecionada.getClasseCredor() == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, " O campo Classe do Credor é obrigatório!", " "));
        } else {
            classeCredorSelecionada.setPessoa(pessoa);
            pessoa.getClasseCredorPessoas().add(classeCredorSelecionada);
            classeCredorSelecionada = new ClasseCredorPessoa();
        }
    }

    public void removeClasse(ActionEvent evt) {
        ClasseCredorPessoa ccp = (ClasseCredorPessoa) evt.getComponent().getAttributes().get("objeto");
        pessoa.getClasseCredorPessoas().remove(ccp);
    }

    public List<ClasseCredor> completaClasseCredor(String parte) {
        return classeCredorFacade.listaFiltrandoDescricao(parte.trim());
    }

    public ConverterAutoComplete getConverterClasseCredor() {
        if (converterClasseCredor == null) {
            converterClasseCredor = new ConverterAutoComplete(ClasseCredor.class, classeCredorFacade);
        }
        return converterClasseCredor;
    }

    public List<ClassificacaoCredor> completaClassificacao(String parte) {
        return classificacaoCredorFacade.listaFiltrando(parte.trim(), "descricao");
    }

    public ConverterAutoComplete getConverterClassificacao() {
        if (converterClassificacao == null) {
            converterClassificacao = new ConverterAutoComplete(ClassificacaoCredor.class, classificacaoCredorFacade);
        }
        return converterClassificacao;
    }

    //    public void adicionaClassificacaoCredor() {
//        if (pessoa.getClasseCredorPessoas().contains(classeCredorSelecionada)) {
//            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "A Classificação ja esta adicionada!", " "));
//        } else if (pessoaClassificacaoCredorSelecionada.getClassificacaoCredor() == null) {
//            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, " O campo Classificação do Credor é obrigatório!", " "));
//        } else {
//            pessoaClassificacaoCredorSelecionada.setPessoa(pessoa);
//            pessoa.getClassificacaoCredores().add(pessoaClassificacaoCredorSelecionada);
//            pessoaClassificacaoCredorSelecionada = new PessoaClassificacaoCredor();
//        }
//    }
    public void removeClassificacao(ActionEvent evt) {
        PessoaClassificacaoCredor pcc = (PessoaClassificacaoCredor) evt.getComponent().getAttributes().get("objeto");
        pessoa.getClassificacaoCredores().remove(pcc);
    }

    public Boolean liberaContaPrincipal() {
        Boolean controle = true;
        if (pessoa != null) {
            for (ContaCorrenteBancPessoa ccbf : pessoa.getContaCorrenteBancPessoas()) {
                if (ccbf.getPrincipal()) {
                    controle = false;
                }
            }
        }
        return controle;
    }

    public Boolean liberaEnderecoPrincipal() {
        Boolean controle = true;
        if (pessoa != null) {
            for (EnderecoCorreio ec : pessoa.getEnderecos()) {
                if (ec.getPrincipal()) {
                    controle = false;
                }
            }
        }
        return controle;
    }

    public Boolean liberaTelefonePrincipal() {
        Boolean retorno = true;
        if (pessoa != null) {
            for (Telefone fone : pessoa.getTelefones()) {
                if (fone.getPrincipal()) {
                    retorno = false;
                }
            }
        }
        return retorno;
    }

    public void novoJuridico() {
        reiniciaComponente();
        operacao = Operacoes.NOVO;
        selecionado = new PessoaJuridica();
        pessoa = (PessoaJuridica) selecionado;
        pessoa.getPerfis().add(perfilEnum);
        pessoa.setSituacaoCadastralPessoa(SituacaoCadastralPessoa.ATIVO);
        pessoa.setTelefones(new ArrayList<Telefone>());
        //pessoa.setContasCorrentesBancarias(new ArrayList<ContaCorrenteBancaria>());
        pessoa.setEnderecos(new ArrayList<EnderecoCorreio>());
        telefone = new Telefone();
        endereco = new EnderecoCorreio();
        contaCorrenteBancaria = new ContaCorrenteBancaria();
        contaCorrenteBancariaCadastravel = new ContaCorrenteBancaria();
        telefoneAux = new Telefone();
        pessoa.setContaCorrenteBancPessoas(new ArrayList<ContaCorrenteBancPessoa>());
        contaCorrenteBancPessoa = new ContaCorrenteBancPessoa();
        listaExcluidos = new ArrayList<ContaCorrenteBancPessoa>();
        unidadeOrganizacionalSelecionada = new HierarquiaOrganizacional();
        classeCredorSelecionada = new ClasseCredorPessoa();
        //pessoaClassificacaoCredorSelecionada = new PessoaClassificacaoCredor();
    }

    public CarteiraVacinacao getCarteiraVacinacao() {
        return carteiraVacinacao;
    }

    public void setCarteiraVacinacao(CarteiraVacinacao carteiraVacinacao) {
        this.carteiraVacinacao = carteiraVacinacao;
    }

    public List<CarteiraVacinacao> getCarteiraVacinacaos() {
        return carteiraVacinacaos;
    }

    public void setCarteiraVacinacaos(List<CarteiraVacinacao> carteiraVacinacaos) {
        this.carteiraVacinacaos = carteiraVacinacaos;
    }

    public void addDocumentoPessoa(DocumentoPessoal dp) {
        PessoaFisica pf = (PessoaFisica) pessoa;
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

    public List<TituloEleitor> getTituloEleitores() {
        return tituloEleitores;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
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

    public List<SelectItem> getTiposEnderecos() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (TipoEndereco t : TipoEndereco.values()) {
            toReturn.add(new SelectItem(t, t.getDescricao()));
        }
        return toReturn;
    }

    public void setTituloEleitores(List<TituloEleitor> tituloEleitores) {
        this.tituloEleitores = tituloEleitores;
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

    public Telefone getTelefoneAux() {
        return telefoneAux;
    }

    public void setTelefoneAux(Telefone telefoneAux) {
        this.telefoneAux = telefoneAux;
    }

    public List<Telefone> getTelefones() {
        return this.pessoa.getTelefones();
    }

    public List<EnderecoCorreio> getEnderecos() {
        return pessoa.getEnderecos();
    }

    //    public List<ContaCorrenteBancaria> getContasCorrentes() {
//        return pessoa.getContasCorrentesBancarias();
//    }
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

    public ClasseCredorControlador getClasseCredorControlador() {
        return classeCredorControlador;
    }

    public void setClasseCredorControlador(ClasseCredorControlador classeCredorControlador) {
        this.classeCredorControlador = classeCredorControlador;
    }

    public ClassificacaoCredorControlador getClassificacaoCredorControlador() {
        return classificacaoCredorControlador;
    }

    public void setClassificacaoCredorControlador(ClassificacaoCredorControlador classificacaoCredorControlador) {
        this.classificacaoCredorControlador = classificacaoCredorControlador;
    }

    public NacionalidadeControlador getNacionalidadeControlador() {
        return nacionalidadeControlador;
    }

    public void setNacionalidadeControlador(NacionalidadeControlador nacionalidadeControlador) {
        this.nacionalidadeControlador = nacionalidadeControlador;
    }

    public void novoFone() {
        telefone.setPessoa((PessoaFisica) selecionado);
        pessoa.getTelefones().add(telefone);
        telefone = new Telefone();
    }

    public void novoFonePJ() {
        telefone.setPessoa((PessoaJuridica) selecionado);
        pessoa.getTelefones().add(telefone);
        telefone = new Telefone();
    }

    public void novoRg() {
        if (rg != null && !rg.getNumero().isEmpty()) {
            PessoaFisica pf = (PessoaFisica) pessoa;
            rg.setDataRegistro(new Date());
            rg.setPessoaFisica(pf);
            if (pf.getDocumentosPessoais().contains(rg)) {
                pf.getDocumentosPessoais().set(pf.getDocumentosPessoais().indexOf(rg), rg);
            } else {
                pf.getDocumentosPessoais().add(rg);
            }
        }
    }

    public void removeRg(ActionEvent evento) {
        PessoaFisica pf = (PessoaFisica) pessoa;
        pf.getDocumentosPessoais().remove((RG) evento.getComponent().getAttributes().get("removeRg"));
        rgs.remove(0);
        rg = new RG();
    }

    public void novoCarteiraVacinacao() {
        PessoaFisica pf = (PessoaFisica) pessoa;
        carteiraVacinacao.setDataRegistro(new Date());
        carteiraVacinacao.setPessoaFisica(pf);
        if (pf.getDocumentosPessoais().contains(carteiraVacinacao)) {
            pf.getDocumentosPessoais().set(pf.getDocumentosPessoais().indexOf(carteiraVacinacao), carteiraVacinacao);
        } else {
            pf.getDocumentosPessoais().add(carteiraVacinacao);
        }
    }

    public void removeCarteiraVacinacao(ActionEvent evento) {
        PessoaFisica pf = (PessoaFisica) pessoa;
        pf.getDocumentosPessoais().remove((CarteiraVacinacao) evento.getComponent().getAttributes().get("removeCarteiraVacinacao"));
        carteiraVacinacaos.remove(0);
        carteiraVacinacao = new CarteiraVacinacao();
    }

    public void novoCertidaoNascimento() {
        PessoaFisica pf = (PessoaFisica) pessoa;
        certidaoNascimento.setDataRegistro(new Date());
        certidaoNascimento.setPessoaFisica(pf);
        if (pf.getDocumentosPessoais().contains(certidaoNascimento)) {
            pf.getDocumentosPessoais().set(pf.getDocumentosPessoais().indexOf(certidaoNascimento), certidaoNascimento);
        } else {
            pf.getDocumentosPessoais().add(certidaoNascimento);
        }
    }

    public void novoCertidaoCasamento() {
        if (certidaoCasamento == null) {
            PessoaFisica pf = (PessoaFisica) pessoa;
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
        if (tituloEleitor != null && tituloEleitor.getId() != null && !tituloEleitor.getNumero().isEmpty() && tituloEleitor.getNumero() != null) {
            PessoaFisica pf = (PessoaFisica) pessoa;
//            tituloEleitores.clear();
            tituloEleitor.setDataRegistro(new Date());
            tituloEleitor.setPessoaFisica(pf);
//            tituloEleitores.add(tituloEleitor);
//            tituloEleitores.get(0).setPessoaFisica((PessoaFisica) selecionado);
            if (pf.getDocumentosPessoais().contains(tituloEleitor)) {
                pf.getDocumentosPessoais().set(pf.getDocumentosPessoais().lastIndexOf(tituloEleitor), tituloEleitor);
            } else {
                pf.getDocumentosPessoais().add(tituloEleitor);
            }
        }
    }

    public void removeTitulo(ActionEvent evento) {
        PessoaFisica pf = (PessoaFisica) pessoa;
        pf.getDocumentosPessoais().remove((TituloEleitor) evento.getComponent().getAttributes().get("removeTitulo"));
        tituloEleitores.remove(0);
        tituloEleitor = new TituloEleitor();
    }

    public void novaHabilitacao() {
        PessoaFisica pf = (PessoaFisica) pessoa;
        habilitacao.setDataRegistro(new Date());
        habilitacao.setPessoaFisica(pf);
        habilitacoes.add(habilitacao);
        if (pf.getDocumentosPessoais().contains(habilitacao)) {
            pf.getDocumentosPessoais().set(pf.getDocumentosPessoais().indexOf(habilitacao), habilitacao);
        } else {
            pf.getDocumentosPessoais().add(habilitacao);
        }
    }

    public void removeHabilitacao(ActionEvent evento) {
        PessoaFisica pf = (PessoaFisica) pessoa;
        pf.getDocumentosPessoais().remove((Habilitacao) evento.getComponent().getAttributes().get("removeHabilitacao"));
        habilitacoes.remove((Habilitacao) evento.getComponent().getAttributes().get("removeHabilitacao"));
        habilitacao = new Habilitacao();
    }

    public void novoTrabalho() {
        PessoaFisica pf = (PessoaFisica) pessoa;
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
    }

    public void removeTrabalho(ActionEvent evento) {
        PessoaFisica pf = (PessoaFisica) pessoa;
        pf.getDocumentosPessoais().remove((CarteiraTrabalho) evento.getComponent().getAttributes().get("removeTrabalho"));
        carteiraTrabalhos.remove((CarteiraTrabalho) evento.getComponent().getAttributes().get("removeTrabalho"));
        carteiraTrabalho = new CarteiraTrabalho();
    }

    public void novoMilitar() {
        if (situacaoMilitar != null && situacaoMilitar.getId() != null && situacaoMilitar.getCertificadoMilitar() != null && !situacaoMilitar.getCertificadoMilitar().isEmpty()) {
            PessoaFisica pf = (PessoaFisica) pessoa;
            situacaoMilitar.setDataRegistro(new Date());
            situacaoMilitar.setPessoaFisica((PessoaFisica) selecionado);
            if (pf.getDocumentosPessoais().contains(situacaoMilitar)) {
                pf.getDocumentosPessoais().set(pf.getDocumentosPessoais().indexOf(situacaoMilitar), situacaoMilitar);
            } else {
                pf.getDocumentosPessoais().add(situacaoMilitar);
            }
        }
    }

    public void removeMilitar(ActionEvent evento) {
        PessoaFisica pf = (PessoaFisica) pessoa;
        pf.getDocumentosPessoais().remove((SituacaoMilitar) evento.getComponent().getAttributes().get("removeMilitar"));
        situacaoMilitares.remove(0);
        situacaoMilitar = new SituacaoMilitar();
    }

    public void novoEndereco() {
        pessoa.getEnderecos().add(endereco);
        endereco = new EnderecoCorreio();
    }

    public void removeFone(ActionEvent evento) {
        pessoa.getTelefones().remove((Telefone) evento.getComponent().getAttributes().get("vfone"));
    }

    //    public void removeConta(ActionEvent evento) {
//        pessoa.getContasCorrentesBancarias().remove((ContaCorrenteBancaria) evento.getComponent().getAttributes().get("vconta"));
//    }
    public void removeEndereco(ActionEvent evento) {
        pessoa.getEnderecos().remove((EnderecoCorreio) evento.getComponent().getAttributes().get("vendereco"));
    }

    public void tituloNulo() {
        tituloEleitor = new TituloEleitor();
    }

    public PessoaControlador() {
        metadata = new EntidadeMetaData(Pessoa.class);
        perfilDependente = PerfilEnum.PERFIL_DEPENDENTE;
        perfilAdministrativo = PerfilEnum.PERFIL_ADMINISTRATIVO;
        perfilRH = PerfilEnum.PERFIL_RH;
        perfilTributario = PerfilEnum.PERFIL_TRIBUTARIO;
        perfilCredor = PerfilEnum.PERFIL_CREDOR;
        mostraPainelEspecialidades = false;
    }

    public PessoaFacade getFacade() {
        return pessoaFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return pessoaFacade;
    }

    public List<SelectItem> getTiposFone() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (TipoTelefone object : TipoTelefone.values()) {
            toReturn.add(new SelectItem(object, object.getTipoFone()));
        }
        return toReturn;
    }

    public List<SelectItem> getNacionalidade() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (Nacionalidade object : nacionalidadeFacade.lista()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getTipoSituacao() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoSituacaoMilitar object : TipoSituacaoMilitar.values()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getCategoria() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (CategoriaCertificadoMilitar object : CategoriaCertificadoMilitar.values()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public void setaEndereco(ActionEvent evento) {
        setEnderecoAux((Endereco) evento.getComponent().getAttributes().get("alteraEndereco"));
    }

    public void painelNaTela() {
        if (((PessoaFisica) pessoa).getNivelEscolaridade() == null) {
            mostraPainelEspecialidades = false;
        } else {
            mostraPainelEspecialidades = true;
        }
    }

    public Boolean getMostraPainelEspecialidades() {
        return mostraPainelEspecialidades;
    }

    public void setMostraPainelEspecialidades(Boolean mostraPainelEspecialidades) {
        this.mostraPainelEspecialidades = mostraPainelEspecialidades;
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

    public List<SelectItem> getEscritorioContabil() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();

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
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
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
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (NivelEscolaridade object : nivelEscolaridadeFacade.lista()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getEstados() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (UF object : ufFacade.lista()) {
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
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (Sexo object : Sexo.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getRacaCor() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (RacaCor object : RacaCor.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getEstadoCivil() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
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
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (UF object : ufFacade.lista()) {
            toReturn.add(new SelectItem(object, object.getNome()));
        }
        return toReturn;
    }

    public List<SelectItem> getSituacaoConta() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (SituacaoConta object : SituacaoConta.values()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getTipoSanguineo() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
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

    private boolean skip;

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

    @Override
    public void selecionar(ActionEvent evento) {
        reiniciaComponente();
        operacao = Operacoes.EDITAR;
        pessoa = (Pessoa) evento.getComponent().getAttributes().get("objeto");

        if (pessoa instanceof PessoaFisica) {
            pessoa = pessoaFacade.recuperarPF(pessoa.getId());

            arquivo = new Arquivo();
            fileUploadEvent = null;
            if (((PessoaFisica) pessoa).getArquivo() != null) {
                arquivo = ((PessoaFisica) pessoa).getArquivo();
            }

        } else if (pessoa instanceof PessoaJuridica) {
            pessoa = pessoaFacade.recuperarPJ(pessoa.getId());
        }
        selecionado = pessoa;
        if (!pessoa.getPerfis().contains(perfilEnum)) {
            pessoa.getPerfis().add(perfilEnum);
        }
        carteiraVacinacaos = new ArrayList<CarteiraVacinacao>();
        carteiraVacinacao = new CarteiraVacinacao();
        enderecoAux = new Endereco();
        telefoneAux = new Telefone();
        telefone = new Telefone();
        endereco = new EnderecoCorreio();
        habilitacao = new Habilitacao();
        habilitacoes = new ArrayList<Habilitacao>();
        carteiraTrabalho = new CarteiraTrabalho();
        carteiraTrabalhos = new ArrayList<CarteiraTrabalho>();
        enderecoAux = new Endereco();
        if (pessoa.getPerfis().equals(PerfilEnum.PERFIL_CREDOR)) {
            unidadeOrganizacionalSelecionada = new HierarquiaOrganizacional();
            unidadeOrganizacionalSelecionada.setTipoHierarquiaOrganizacional(TipoHierarquiaOrganizacional.ORCAMENTARIA);
            unidadeOrganizacionalSelecionada.setSubordinada(pessoa.getUnidadeOrganizacional());
            unidadeOrganizacionalSelecionada = hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalPorUnidade(pessoa.getUnidadeOrganizacional(), unidadeOrganizacionalSelecionada, sistemaControlador.getExercicioCorrente());
        }
        //pessoaClassificacaoCredorSelecionada = new PessoaClassificacaoCredor();
        classeCredorSelecionada = new ClasseCredorPessoa();
        listaClasseCredorPessoa = new ArrayList<ClasseCredorPessoa>();

        if (pessoa instanceof PessoaFisica) {
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

            PessoaFisica pf = (PessoaFisica) pessoa;
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
                }
            }
        }

        listaExcluidos = new ArrayList<ContaCorrenteBancPessoa>();
        contaCorrenteBancPessoa = new ContaCorrenteBancPessoa();
        contaCorrenteBancaria = new ContaCorrenteBancaria();
        contaCorrenteBancariaCadastravel = new ContaCorrenteBancaria();

        if (pessoa instanceof PessoaFisica) {
            carregaFoto();
        }
    }

    //    public void setaUnidade(SelectEvent evt) {
//        HierarquiaOrganizacional hie = (HierarquiaOrganizacional) evt.getObject();
//        pessoa.setUnidadeOrganizacional(hie.getSubordinada());
//    }
    public RG verificaRG() {
        PessoaFisica pf = (PessoaFisica) pessoa;
        for (DocumentoPessoal documento : pf.getDocumentosPessoais()) {
            if (documento instanceof RG) {
                return (RG) documento;
            }
        }
        return null;
    }

    public CarteiraVacinacao verificaCarteiraVacinacao() {
        PessoaFisica pf = (PessoaFisica) pessoa;
        for (DocumentoPessoal documento : pf.getDocumentosPessoais()) {
            if (documento instanceof CarteiraVacinacao) {
                return (CarteiraVacinacao) documento;
            }
        }
        return null;
    }

    public TituloEleitor verificaTitulo() {
        PessoaFisica pf = (PessoaFisica) pessoa;
        for (DocumentoPessoal documento : pf.getDocumentosPessoais()) {
            if (documento instanceof TituloEleitor) {
                return (TituloEleitor) documento;
            }
        }
        return null;
    }

    public SituacaoMilitar verificaMilitar() {
        PessoaFisica pf = (PessoaFisica) pessoa;
        for (DocumentoPessoal documento : pf.getDocumentosPessoais()) {
            if (documento instanceof SituacaoMilitar) {
                return (SituacaoMilitar) documento;
            }
        }
        return null;
    }

    public CertidaoNascimento verificaCertidaoNascimento() {
        PessoaFisica pf = (PessoaFisica) pessoa;
        for (DocumentoPessoal documento : pf.getDocumentosPessoais()) {
            if (documento instanceof CertidaoNascimento) {
                return (CertidaoNascimento) documento;
            }
        }
        return null;
    }

    public CertidaoCasamento verificaCertidaoCasamento() {
        PessoaFisica pf = (PessoaFisica) pessoa;
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

    private Boolean isPessoaValida() throws Exception {
        Boolean validou = Boolean.TRUE;
        if (!realizouValidacoesEspecificasPessoa()) {
            validou = Boolean.FALSE;
        }

        if (!realizouValidacoesCamposObrigatorios()) {
            validou = Boolean.FALSE;
        }

        return validou;
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

    public boolean validaCpf(String s_aux) {
        if (s_aux.isEmpty()) {
            return true;
        } else {
            s_aux = s_aux.replace(".", "");
            s_aux = s_aux.replace("-", "");
//------- Rotina para CPF
            if (s_aux.length() == 11) {
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
            else {
                return false;
            }
        }
    }

    //    @Override
//    public Boolean validaCampos() {
//        Boolean retorno = super.validaCampos();
//        if (validaCpf(pessoa.getCpf()) == false) {
//            FacesContext.getCurrentInstance().addMessage("Formulario:cpf", new FacesMessage(FacesMessage.SEVERITY_ERROR, "CPF inválida!", "Por favor, verificar CPF."));
//            retorno = false;
//        }
//        if (pessoa.getEnderecos().isEmpty()) {
//            FacesContext.getCurrentInstance().addMessage("Formulario:logradouro", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Nenhum Endereco detectado!", "Deve existir pelo menos um!"));
//            retorno = false;
//        }
//        if (!pessoa.getEmail().isEmpty()) {
//            if (validaEmail(pessoa.getEmail()) == false) {
//                FacesContext.getCurrentInstance().addMessage("Formulario:email", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Email inválido!", "Por Favor verifique!"));
//                retorno = false;
//            }
//        }
//        if (!pessoaFacade.verificaCpf(pessoa)) {
//            FacesContext.getCurrentInstance().addMessage("Formulario:cpf", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Impossível Salvar", "Já existe um registro com este número de CPF"));
//            retorno = false;
//        }
//
//        //se não for o cadastro a partir do RH as validações param aqui
//        if (!"RH".equals(cadastroPessoaFisica)) {
//            return true;
//        }
//
//        if (pessoa.getDataNascimento() == null) {
//            FacesContext.getCurrentInstance().addMessage("Formulario:dataNascimento", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Data de Nascimento não informada", "A data de Nascimento deve ser informada!"));
//            retorno = false;
//        }
//
//        if (pessoa.getSexo() == null) {
//            FacesContext.getCurrentInstance().addMessage("Formulario:sexo", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Sexo não informado", "O Sexo não foi informado !"));
//            retorno = false;
//        }
//
//
//        if (pessoa.getSexo() == Sexo.MASCULINO) {
//            if (situacaoMilitar.getDataEmissao() == null || situacaoMilitar.getOrgaoEmissao() == null || situacaoMilitar.getCertificadoMilitar() == null) {
//                FacesContext.getCurrentInstance().addMessage("Formulario:painelMilitar:situacaoMilitar", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Nenhuma Situação Militar", "Deve ser cadastrada ao menos uma situação militar!"));
//                retorno = false;
//            }
//        } else {
//            pessoa.getDocumentosPessoais().remove(situacaoMilitar);
//        }
//
//        if (rg.getNumero() == null || rg.getDataemissao() == null || rg.getOrgaoEmissao() == null) {
//            FacesContext.getCurrentInstance().addMessage("Formulario:painelRg:numerorg", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Nenhum RG Cadastrado", "Deve ser cadastrado todos os dados do RG!"));
//            retorno = false;
//        }
//
//        if (tituloEleitor.getNumero() == null || tituloEleitor.getZona() == null || tituloEleitor.getCidade() == null || tituloEleitor.getSessao() == null || tituloEleitor.getDataEmissao() == null) {
//            FacesContext.getCurrentInstance().addMessage("Formulario:painelTitulo:inputTituloNumero", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Nenhum Título Cadastrado", "Deve ser cadastrado todos os dados do Título de Eleitor!"));
//            retorno = false;
//        }
//
//        if (carteiraTrabalhos.isEmpty()) {
//            FacesContext.getCurrentInstance().addMessage("Formulario:painelTraba:numeroCarteiraTrabalho", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Nenhuma Carteira de Trabalho Cadastrada", "Deve ser cadastrada ao menos uma Carteira de Trabalho!"));
//            retorno = false;
//        }
//
//        if (pessoa.getEnderecos().isEmpty()) {
//            FacesContext.getCurrentInstance().addMessage("Formulario:logra:estado", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Nenhum Endereço Cadastrado", "Deve ser cadastrado ao menos um Endereço!"));
//            retorno = false;
//        }
//
//        return retorno;
//    }
    public List<SelectItem> tiposEnderecos() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, " "));
        for (TipoEndereco t : TipoEndereco.values()) {
            toReturn.add(new SelectItem(t, t.getDescricao()));
        }
        return toReturn;
    }

    public List<CEPUF> completaUF(String parte) {
        return consultaCepFacade.consultaUf(parte.trim());
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
        return consultaCepFacade.consultaLocalidades(l, parte.trim());
    }

    public List<CEPLogradouro> completaLogradouro(String parte) {
        String l = "";
        if (endereco.getLocalidade() != null) {
            l = endereco.getLocalidade();
        }
        return consultaCepFacade.consultaLogradouros(l, parte.trim());
    }

    public List<CEPBairro> completaBairro(String parte) {
        String l = "";
        if (endereco.getLocalidade() != null) {
            l = endereco.getLocalidade();
        }
        return consultaCepFacade.consultaBairros(l, parte.trim());
    }

    public List<CEPLogradouro> completaCEP(String parte) {
        String l = "";
        String c = "";
        if (endereco.getLogradouro() != null) {
            l = endereco.getLogradouro();
        }
        if (endereco.getLocalidade() != null) {
            c = endereco.getLocalidade();
        }
        return consultaCepFacade.consultaCEPs(l, c, parte.trim());
    }

    public void atualizaLogradouros() {
        reiniciaComponente();
        cep = textocep.getValue().toString();
        logradouros = consultaCepFacade.consultaLogradouroPorCEP(cep);
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
        } else {
            endereco = new EnderecoCorreio();
        }
    }
//linha

    @Override
    public String salvar() {
        try {
            if (pessoa instanceof PessoaFisica) {
                novoRg();
                novoTitulo();
                novoMilitar();
                novoCertidaoNascimento();
                novoCertidaoCasamento();
            }
            if (isPessoaValida() && validaCampos()) {
                if (operacao == Operacoes.NOVO) {
                    pessoa.setUnidadeOrganizacional(unidadeOrganizacionalSelecionada.getSubordinada());

                    ((Pessoa) selecionado).adicionarHistoricoSituacaoCadastral();
                    if (pessoa instanceof PessoaJuridica) {
                        pessoaFacade.salvarNovo((Pessoa) selecionado);
                    } else {
                        pessoaFacade.salvarNovo(((PessoaFisica) selecionado), arquivo, fileUploadEvent);
                    }
                } else {
                    if (pessoa.getPerfis().equals(PerfilEnum.PERFIL_CREDOR)) {
                        pessoa.setUnidadeOrganizacional(unidadeOrganizacionalSelecionada.getSubordinada());
                    }

                    if (pessoa instanceof PessoaJuridica) {
                        pessoaFacade.salvar((Pessoa) selecionado, listaExcluidos);
                    } else {
                        pessoaFacade.salvar(((PessoaFisica) selecionado), arquivo, fileUploadEvent, listaExcluidos);
                    }
                }
                reiniciaComponente();
                lista = null;
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Salvo com Sucesso", ""));
                return caminho();
            } else {
                reiniciaComponente();
                return editaPessoa();
            }

        } catch (Exception e) {
            reiniciaComponente();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Atenção ! " + e.getMessage(), ""));
            logger.error(e.getMessage());
            return editaPessoa();
        }

    }

    public void adicionaContaCorrenteBancPessoa() {
        if (contaCorrenteBancaria != null) {
            contaCorrenteBancPessoa.setContaCorrenteBancaria(contaCorrenteBancaria);
            contaCorrenteBancPessoa.setPessoa(((PessoaFisica) selecionado));
            contaCorrenteBancPessoa.setDataRegistro(new Date());
            ((PessoaFisica) selecionado).getContaCorrenteBancPessoas().add(contaCorrenteBancPessoa);
            contaCorrenteBancPessoa = new ContaCorrenteBancPessoa();
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Atenção", "Selecione uma conta corrente bancária"));
        }
    }

    public void adicionaContaCorrenteBancPessoaJuridica() {
        if (contaCorrenteBancaria != null) {
            contaCorrenteBancPessoa.setContaCorrenteBancaria(contaCorrenteBancaria);
            contaCorrenteBancPessoa.setPessoa(((PessoaJuridica) selecionado));
            contaCorrenteBancPessoa.setDataRegistro(new Date());
            ((PessoaJuridica) selecionado).getContaCorrenteBancPessoas().add(contaCorrenteBancPessoa);
            contaCorrenteBancPessoa = new ContaCorrenteBancPessoa();
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Atenção", "Selecione uma conta corrente bancária"));
        }
    }

    public void removeContaCorrenteBancPessoa(ActionEvent evento) {
        ContaCorrenteBancPessoa c = (ContaCorrenteBancPessoa) evento.getComponent().getAttributes().get("objeto");
        ((PessoaFisica) selecionado).getContaCorrenteBancPessoas().remove(c);
        listaExcluidos.add(c);
    }

    public void removeContaCorrenteBancPessoaJuridica(ActionEvent evento) {
        ContaCorrenteBancPessoa c = (ContaCorrenteBancPessoa) evento.getComponent().getAttributes().get("objeto");
        ((PessoaJuridica) selecionado).getContaCorrenteBancPessoas().remove(c);
        listaExcluidos.add(c);
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

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public void setPerfilEspecial() {
        perfilEnum = PerfilEnum.PERFIL_ESPECIAL;
    }

    public void setPerfilCredor() {
        perfilEnum = PerfilEnum.PERFIL_CREDOR;
    }

    public void setPerfilRH() {
        perfilEnum = PerfilEnum.PERFIL_RH;
    }

    public void setPerfilPensionista() {
        perfilEnum = PerfilEnum.PERFIL_PENSIONISTA;
    }

    public void setPerfilAdmnistrativo() {
        perfilEnum = PerfilEnum.PERFIL_ADMINISTRATIVO;
    }

    public void setPerfilTributario() {
        perfilEnum = PerfilEnum.PERFIL_TRIBUTARIO;
    }

    public void setPerfilDependente() {
        perfilEnum = PerfilEnum.PERFIL_DEPENDENTE;
    }

    public PerfilEnum getPerfilPensionista() {
        return perfilPensionista = PerfilEnum.PERFIL_PENSIONISTA;
    }

    public UIComponent getComponente() {
        return this.componente;
    }

    public void setComponente(UIComponent componente) {
        this.componente = componente;
    }

    public PerfilEnumConverter getPerfilEnumConveter() {
        return perfilEnumConveter;
    }

    public void setPerfilEnumConveter(PerfilEnumConverter perfilEnumConveter) {
        this.perfilEnumConveter = perfilEnumConveter;
    }

    private void percorreFilhos(UIComponent comp) throws Exception {
        if (comp.getChildren() == null) {
            return;
        }
        for (UIComponent child : comp.getChildren()) {
            percorreFilhos(child);
            try {
                Boolean renderizado = Boolean.FALSE;
                Boolean requerido = Boolean.FALSE;
                for (PerfilEnum pe : this.pessoa.getPerfis()) {
                    Perfil p = pe.getPerfil();
                    if (!renderizado) {
                        renderizado = p.ehRenderizado(child.getId());
                    }
                    if (!requerido && renderizado) {
                        String s = child.getId();
                        boolean vai = s.startsWith("o") || s.startsWith("i") || s.startsWith("m");
                        if (vai) {
                            requerido = p.ehRequerido(child.getId());
                        }
                    }
                }
                child.setRendered(renderizado);
                if (child instanceof UIInput) {
                    if (child.getId().startsWith("i")) {
                        ((UIInput) child).setRequired(requerido);
                    }
                }
            } catch (RuntimeException e) {
            }
        }

    }

    public void perfilMudou() {
        try {
            if (this.componente != null) {
                if (this.pessoa.getPerfis().size() > 0) {
                    percorreFilhos(this.componente);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public String getDisparaMetodo() {
        perfilMudou();
        return " ";
    }

    public void atalhoCidade() {
        reiniciaComponente();
        cidadeControlador.novo();
        cidadeControlador.setSessao("/tributario/cadastromunicipal/pessoafisica/edita");
    }

    public void atalhoEspecialidades() {
        reiniciaComponente();
        throw new UnsupportedOperationException("O vínculo entre pessoa e especialidade precisa ser refeito - Claudio");
        //especialidadesControlador.setCaminho("/tributario/cadastromunicipal/pessoafisica/edita");
    }

    public void atalhoClasseCredorPJ() {
        reiniciaComponente();
        classeCredorControlador.novo();
//        classeCredorControlador.setCaminho("/tributario/cadastromunicipal/pessoafisica/editaJuridica");
    }

    public void atalhoClasseCredorPF() {
        reiniciaComponente();
        classeCredorControlador.novo();
//        classeCredorControlador.setCaminho("/tributario/cadastromunicipal/pessoafisica/edita");
    }

    public void atalhoClassificacaoCredorPF() {
        reiniciaComponente();
        classificacaoCredorControlador.novo();
        classificacaoCredorControlador.setCaminho("/tributario/cadastromunicipal/pessoafisica/edita");
    }

    public void atalhoClassificacaoCredorPJ() {
        reiniciaComponente();
        classificacaoCredorControlador.novo();
        classificacaoCredorControlador.setCaminho("/tributario/cadastromunicipal/pessoafisica/editaJuridica");
    }

    public void componenteNull() {
        reiniciaComponente();
    }

    public List<SelectItem> getTipoEmpresas() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (TipoEmpresa t : TipoEmpresa.values()) {
            toReturn.add(new SelectItem(t, t.getDescricao()));
        }
        return toReturn;
    }

    public String preparaCamposVisiveisPerfilPessoa() throws Exception {
        if (this.componente != null && this.pessoa != null) {
            if (this.pessoa.getPerfis().size() > 0) {
                manipulaCamposPerfilPessoa(this.componente);
            }
        }
        return " ";
    }

    private void manipulaCamposPerfilPessoa(UIComponent componente) throws Exception {
        if (componente.getChildren() == null) {
            return;
        }
        for (UIComponent campo : componente.getChildren()) {
            manipulaCamposPerfilPessoa(campo);
            Boolean visivel = Boolean.FALSE;
            if (perfilEnum != null) {
                visivel = defineVisibilidadeCampo(perfilEnum, visivel, campo);
            } else {
                for (PerfilEnum pe : this.pessoa.getPerfis()) {
                    visivel = defineVisibilidadeCampo(pe, visivel, campo);
                }
            }
            campo.setRendered(visivel);
        }
    }

    private boolean realizouValidacoesEspecificasPessoa() {
        Boolean validou = Boolean.TRUE;
//        for (PerfilEnum pe : pessoa.getPerfis()) {
//            Perfil p = pe.getPerfil();
//            validou = p.validarPessoa(pessoa);
//        }
        return validou;
    }

    private boolean realizouValidacoesCamposObrigatorios() throws Exception {
        validouCamposObrigatorios = Boolean.TRUE;
        validarCamposObrigatorios(componente);
        return validouCamposObrigatorios;
    }

    public Boolean validarCamposObrigatorios(UIComponent componente) throws Exception {
        if (componente == null || componente.getChildren() == null) {
            return validouCamposObrigatorios;
        }
        for (UIComponent campo : componente.getChildren()) {
            validarCamposObrigatorios(campo);
            if (campo.getId().startsWith("i")) {
                Boolean requerido = Boolean.FALSE;
                for (PerfilEnum pe1 : this.pessoa.getPerfis()) {
                    if (pe1 != null && pe1.getPerfil() != null) {
                        requerido = pe1.getPerfil().ehRequerido(campo.getId());
                        if (requerido) {
                            if ((((UIInput) campo).getValue() == null) || ((UIInput) campo).getValue().toString().trim().length() <= 0) {
                                validouCamposObrigatorios = Boolean.FALSE;
                                prepararMensagemValidacaoPessoa(campo);
                            }
                        }
                    }
                }
            }
        }
        return validouCamposObrigatorios;
    }

    private String substituiPrimeiraLetraPalavra(String conteudo, String primeiraLetra, String substituirPor) {
        return conteudo.replaceFirst(primeiraLetra, substituirPor);
    }

    private void prepararMensagemValidacaoPessoa(UIComponent campo) {
        String mensagem = "O campo " + prepararLabelCampo(campo.getId()) + " é obrigatório.";
        FacesContext.getCurrentInstance().addMessage("Formulario:" + campo.getId(), new FacesMessage(FacesMessage.SEVERITY_ERROR, mensagem, mensagem));
    }

    private String prepararLabelCampo(String idCampo) {
        String labelRetorno = "";
        idCampo = substituiPrimeiraLetraPalavra(idCampo, "i", "o");
        labelRetorno = recuperarLabelCampo(componente, idCampo);
        return labelRetorno;
    }

    private String recuperarLabelCampo(UIComponent componente, String idCampo) {
        String labelRetorno = "";
        if (componente.getChildren() == null) {
            return labelRetorno;
        }

        for (UIComponent campo : componente.getChildren()) {
            if (recuperarLabelCampo(campo, idCampo).trim().length() > 0) {
                labelRetorno = recuperarLabelCampo(campo, idCampo);
            }

            if (campo.getId().startsWith("o")) {
                if (campo.getId().equals(idCampo)) {
                    if (campo instanceof UIOutput) {
                        if (((((UIOutput) campo).getValue()) != null) && ((UIOutput) campo).getValue().toString().trim().length() >= 0) {
                            labelRetorno = ((UIOutput) campo).getValue().toString();
                            labelRetorno = substituiPrimeiraLetraPalavra(labelRetorno.toString(), ":", "");
                            return labelRetorno;
                        }
                    }
                }
            }
        }

        return labelRetorno;
    }

    public void novoFisicoEspecial() {
        setPerfilEspecial();
        novoFisico();
    }

    public void novoFisicoDependente() {
        setPerfilDependente();
        novoFisico();
    }

    public void novoFisicoCredor() {
        setPerfilCredor();
        novoFisico();
    }

    public void novoFisicoRH() {
        setPerfilRH();
        novoFisico();
    }

    public void novoFisicoAdm() {
        setPerfilAdmnistrativo();
        novoFisico();
    }

    public void novoJuridicoAdm() {
        setPerfilAdmnistrativo();
        novoJuridico();
    }

    public void novoFisicoTributario() {
        setPerfilTributario();
        novoFisico();
    }

    public void novoJuridicoCredor() {
        setPerfilCredor();
        novoJuridico();
    }

    public void novoJuridicoRH() {
        setPerfilRH();
        novoJuridico();
    }

    public void novoJuridicoTributario() {
        setPerfilTributario();
        novoJuridico();
    }

    public List<SelectItem> getSituacaoCadastral() {
        List<SelectItem> lista = new ArrayList<SelectItem>();
        for (SituacaoCadastralPessoa p : SituacaoCadastralPessoa.values()) {
            lista.add(new SelectItem(p, p.getDescricao()));
        }
        return lista;
    }

    public List<SelectItem> getTipoDeficiencia() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (TipoDeficiencia object : TipoDeficiencia.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    @Override
    public Boolean validaCampos() {
        Pessoa p = (Pessoa) selecionado;
        if (perfilEnum == PerfilEnum.PERFIL_PENSIONISTA && ((Pessoa) selecionado).getContaCorrenteBancPessoas().isEmpty()) {
            FacesContext.getCurrentInstance().addMessage("Formulario:contaCorrenteBancaria", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Impossível "
                + ""
                + "", "É obrigatório adicionar pelo menos uma Conta Corrente."));
            return false;
        }

        if (perfilEnum == PerfilEnum.PERFIL_RH && ((Pessoa) selecionado).getEnderecos().isEmpty()) {
            FacesUtil.addMessageWarn("msgs", "Atenção !", "É Obrigatório adicionar pelo menos um endereço");
            return false;
        }

        if (!p.getCpf_Cnpj().equals("")) {
            if (Util.valida_CpfCnpj(String.valueOf(p.getCpf_Cnpj()))) {
                if (p instanceof PessoaFisica) {
                    if (pessoaFacade.hasOutraPessoaComMesmoCpf((PessoaFisica) p, false)) {
                        FacesContext.getCurrentInstance().addMessage("Formulario:iCPF", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Impossível Salvar", "Já existe um registro com este número de CPF"));
                        return false;
                    }
                } else if (p instanceof PessoaJuridica) {
                    if (pessoaFacade.hasOutraPessoaComMesmoCnpj((PessoaJuridica) p, false)) {
                        FacesContext.getCurrentInstance().addMessage("Formulario:iCNPJ", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Impossível Salvar", "Já existe um registro com este número de CNPJ"));
                        return false;
                    }
                }
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Impossível Salvar", "CPF/CNPJ inválido"));
                return false;
            }
        }

        if (operacaoRBTrans && imagemFoto == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Impossível Salvar", "Selecione uma imagem!"));
            return false;
        }

        return true;
    }

    @Override
    public void cancelar() {
        reiniciaComponente();

//        if (operacaoRBTrans) {
//            permissaoTransporteControlador.cancelarInsercaoDeFotoParaChamarDoPessoaControlador();
//        }
        super.cancelar();
    }

    public String caminho() {
        if (getCaminho() != null) {
            return getCaminho();
        }

        if (perfilEnum.equals(PerfilEnum.PERFIL_ESPECIAL)) {
            return "listaespecial";
        } else if (perfilEnum.equals(PerfilEnum.PERFIL_CREDOR)) {
            return "listacontabil";
        } else if (perfilEnum.equals(PerfilEnum.PERFIL_RH)) {
            return "listarh";
        } else if (perfilEnum.equals(PerfilEnum.PERFIL_TRIBUTARIO)) {
            return "listatributario";
        }
        return "lista";
    }

    public void preencheNomeReduzido() {
        if (!(this.pessoa instanceof PessoaJuridica)) {
            return;
        }
        PessoaJuridica pj = (PessoaJuridica) pessoa;
        if (pj.getNomeReduzido() == null || (pj.getNomeReduzido().trim().isEmpty()) || (pj.getNomeReduzido().trim().equalsIgnoreCase(pj.getRazaoSocial().trim()))) {
            pj.setNomeReduzido(pj.getRazaoSocial());
        }
    }

    public void novoFisicoMatricula() {
        if (perfilEnum.equals(PerfilEnum.PERFIL_RH)) {
            setPerfilRH();
        } else if (perfilEnum.equals(PerfilEnum.PERFIL_DEPENDENTE)) {
            setPerfilDependente();
        } else if (perfilEnum.equals(PerfilEnum.PERFIL_TRIBUTARIO)) {
            setPerfilTributario();
        } else if (perfilEnum.equals(PerfilEnum.PERFIL_ADMINISTRATIVO)) {
            setPerfilAdmnistrativo();
        } else if (perfilEnum.equals(PerfilEnum.PERFIL_PENSIONISTA)) {
            setPerfilPensionista();
        } else if (perfilEnum.equals(PerfilEnum.PERFIL_CREDOR)) {
            setPerfilCredor();
        } else if (perfilEnum.equals(PerfilEnum.PERFIL_ESPECIAL)) {
            setPerfilEspecial();
        }
        novoFisico();
    }

    public PerfilEnum getPerfilDependente() {
        return perfilDependente;
    }

    public void setPerfilDependente(PerfilEnum perfilDependente) {
        this.perfilDependente = perfilDependente;
    }

    public PerfilEnum getPerfilAdministrativo() {
        return perfilAdministrativo;
    }

    public void setPerfilAdministrativo(PerfilEnum perfilAdministrativo) {
        this.perfilAdministrativo = perfilAdministrativo;
    }

    public PerfilEnum getPerfilCredor() {
        return perfilCredor;
    }

    public void setPerfilCredor(PerfilEnum perfilCredor) {
        this.perfilCredor = perfilCredor;
    }

    public PerfilEnum getPerfilRH() {
        return perfilRH;
    }

    public void setPerfilRH(PerfilEnum perfilRH) {
        this.perfilRH = perfilRH;
    }

    public PerfilEnum getPerfilTributario() {
        return perfilTributario;
    }

    public void setPerfilTributario(PerfilEnum perfilTributario) {
        this.perfilTributario = perfilTributario;
    }

    public PerfilEnum getPerfilEnum() {
        return perfilEnum;
    }

    public void setPerfilEnum(PerfilEnum perfilEnum) {
        this.perfilEnum = perfilEnum;
    }

    public void setPerfilPensionista(PerfilEnum perfilPensionista) {
        this.perfilPensionista = perfilPensionista;
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

    public List<ClasseCredorPessoa> getListaClasseCredorPessoa() {
        return listaClasseCredorPessoa;
    }

    public void setListaClasseCredorPessoa(List<ClasseCredorPessoa> listaClasseCredorPessoa) {
        this.listaClasseCredorPessoa = listaClasseCredorPessoa;
    }

    public ClasseCredorPessoa getClasseCredorSelecionada() {
        return classeCredorSelecionada;
    }

    public void setClasseCredorSelecionada(ClasseCredorPessoa classeCredorSelecionada) {
        this.classeCredorSelecionada = classeCredorSelecionada;
    }

    public boolean isOperacaoRBTrans() {
        return operacaoRBTrans;
    }

    public void setOperacaoRBTrans(boolean operacaoRBTrans) {
        this.operacaoRBTrans = operacaoRBTrans;
    }

    public void uploadArquivo(FileUploadEvent event) {
        try {
            fileUploadEvent = event;
            imagemFoto = new DefaultStreamedContent(event.getFile().getInputstream(), "image/png");
        } catch (IOException ex) {
            logger.error("Erro: ", ex);
        }

    }

    public FileUploadEvent getFileUploadEvent() {
        return fileUploadEvent;
    }

    public void setFileUploadEvent(FileUploadEvent fileUploadEvent) {
        this.fileUploadEvent = fileUploadEvent;
    }

    public StreamedContent getImagemFoto() {
        return imagemFoto;
    }

    public void setImagemFoto(StreamedContent imagemFoto) {
        this.imagemFoto = imagemFoto;
    }

    private int inicio = 0;

    @Override
    public List listaFiltrandoX() {
        lista = pessoaFacade.listaFiltrandoX(super.getFiltro(), inicio, super.getMaximoRegistrosTabela());
        return lista;
    }

    public List listaFiltrandoXPerfilRH() {
        listaPerfilRH = pessoaFacade.listaFiltrandoXPerfil(super.getFiltro(), inicio, super.getMaximoRegistrosTabela(), "rh");
        return listaPerfilRH;
    }

    public void carregaFoto() {
        Arquivo arq = ((PessoaFisica) pessoa).getArquivo();
        if (arq != null) {
            try {
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();

                for (ArquivoParte a : arquivoFacade.recuperaDependencias(arq.getId()).getPartes()) {
                    buffer.write(a.getDados());
                }

                InputStream teste = new ByteArrayInputStream(buffer.toByteArray());
                imagemFoto = new DefaultStreamedContent(teste, arq.getMimeType());
            } catch (Exception ex) {
                logger.error("Erro: ", ex);
            }
        }
    }

    @Override
    public List getLista() {
        return lista;
    }

    private List listaPerfilRH;

    public List getListaPerfilRH() {
        listaFiltrandoXPerfilRH();
        return listaPerfilRH;
    }

    public void setListaPerfilRH(List listaPerfilRH) {
        this.listaPerfilRH = listaPerfilRH;
    }

    public List<SelectItem> getTiposInscricao() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(TipoInscricao.CNPJ, TipoInscricao.CNPJ.getDescricao()));
        toReturn.add(new SelectItem(TipoInscricao.CEI, TipoInscricao.CEI.getDescricao()));
        return toReturn;
    }

    public String visualizaPessoa() {
        if (pessoa instanceof PessoaJuridica) {
            return "visualizarJuridica";
        } else {
            return "visualizar";
        }
    }

    public String editaPessoa() {
        reiniciaComponente();
        if (pessoa instanceof PessoaFisica) {
            return "edita";
        } else if (pessoa instanceof PessoaJuridica) {
            return "editaJuridica";
        }
        return "";
    }

    public void validaCpfRh() {

        if (!validaCpf(((PessoaFisica) pessoa).getCpf())) {
            FacesUtil.addMessageWarn("Formulario:iCPF", "Atenção!", "O CPF digitado é inválido");
        } else if (pessoaFacade.hasOutraPessoaComMesmoCpf((PessoaFisica) pessoa, false)) {
            FacesUtil.addMessageWarn("Formulario:iCPF", "Atenção!", "O CPF digitado já pertence a outra pessoa física !");
        }
    }

//    public String iniciarOperacaoRBTransPermissionario() {
//        setCaminho("/tributario/rbtrans/permissaotransporte/edita.xhtml");
//        return terminaDeIniciarOperacaoRBTrans();
//    }

//    public String iniciarOperacaoRBTransMotorista(String pagina) {
//        setCaminho("/tributario/rbtrans/permissaotransporte/pagina.xhtml");
//        return terminaDeIniciarOperacaoRBTrans();
//    }

//    private String terminaDeIniciarOperacaoRBTrans() {
//        operacaoRBTrans = true;
//        setPerfilTributario();
//       permissaoTransporteControlador.getForm().setExibirPanelCadFoto(false);
//        return "/tributario/cadastromunicipal/pessoafisica/edita.xhtml";
//    }

    public List<SelectItem> classesPessoa() {
        List<SelectItem> lista = new ArrayList<SelectItem>();
        lista.add(new SelectItem(null, ""));
        for (ClassePessoa cp : ClassePessoa.values()) {
            lista.add(new SelectItem(cp, cp.getDescricao()));
        }
        return lista;
    }

    private Boolean defineVisibilidadeCampo(PerfilEnum pe, Boolean visivel, UIComponent campo) throws Exception {
        if (pe != null && pe.getPerfil() != null) {
            Perfil p = pe.getPerfil();
            if (!visivel) {
                visivel = p.ehRenderizado(campo.getId());
            }
        }
        return visivel;
    }

    private void reiniciaComponente() {
        componente = null;
    }

    public List<PessoaFisica> getListaEspecial() {
        return pessoaFacade.getPessoaFisicaFacade().listaFiltrandoPorTipoPerfil("", PerfilEnum.PERFIL_ESPECIAL);
    }

    public ContaCorrenteBancaria getContaCorrenteBancariaCadastravel() {
        return contaCorrenteBancariaCadastravel;
    }

    public void setContaCorrenteBancariaCadastravel(ContaCorrenteBancaria contaCorrenteBancariaCadastravel) {
        this.contaCorrenteBancariaCadastravel = contaCorrenteBancariaCadastravel;
    }

    public List<Agencia> completaAgencia(String parte) {
        return agenciaFacade.listaFiltrandoAtributosAgenciaBanco(parte.trim());
    }

    public List<SelectItem> getModalidades() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, " "));
        for (ModalidadeConta object : ModalidadeConta.values()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }
}
