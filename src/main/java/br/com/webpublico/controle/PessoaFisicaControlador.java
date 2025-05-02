
/*
 * Codigo gerado automaticamente em Mon Feb 28 10:12:01 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.ConverterGenerico;
import br.com.webpublico.util.EntidadeMetaData;
import org.primefaces.event.FlowEvent;
import org.primefaces.event.SelectEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.html.HtmlInputText;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ManagedBean
@SessionScoped
public class PessoaFisicaControlador extends SuperControladorCRUD implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(PessoaFisicaControlador.class);

    @EJB
    private PessoaFisicaFacade pessoaFisicaFacade;
    @EJB
    private NacionalidadeFacade nacionalidadeFacade;
    protected ConverterGenerico converterNacionalidade;
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
    protected ConverterAutoComplete converterAgencia;
    private CarteiraTrabalho carteiraTrabalho;
    private List<CarteiraTrabalho> carteiraTrabalhos;
    private List<RG> rgs;
    private RG rg;
    private TituloEleitor tituloEleitor;
    private List<TituloEleitor> tituloEleitores;
    private Habilitacao habilitacao;
    private List<Habilitacao> habilitacoes;
    private SituacaoMilitar situacaoMilitar;
    private List<SituacaoMilitar> situacaoMilitares;
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
    //variavel para controlar as validacoes do cadastro
    private String cadastroPessoaFisica;
    @EJB
    private ContaCorrenteBancariaFacade contaCorrenteBancariaFacade;
    private ContaCorrenteBancPessoa contaCorrenteBancPessoa;
    private List<ContaCorrenteBancPessoa> listaExcluidos;
    private ConverterAutoComplete converterContaCorrenteBancaria;
    private ConverterAutoComplete converterPessoaFisica;

    @Override
    public void novo() {
        super.novo();
        getPessoaFisicaSelecionada().setTelefones(new ArrayList<Telefone>());
        getPessoaFisicaSelecionada().setDocumentosPessoais(new ArrayList<DocumentoPessoal>());
//        getPessoaFisicaSelecionada().setContasCorrentesBancarias(new ArrayList<ContaCorrenteBancaria>());
        getPessoaFisicaSelecionada().setEnderecos(new ArrayList<EnderecoCorreio>());
        getPessoaFisicaSelecionada().setClassePessoa(ClassePessoa.EXTRA);

        telefone = new Telefone();
        endereco = new EnderecoCorreio();

        rg = new RG();
        rg.setDataRegistro(new Date());
        rg.setPessoaFisica((PessoaFisica) selecionado);
        addDocumentoPessoa(rg);
        tituloEleitor = new TituloEleitor();
        tituloEleitor.setDataRegistro(new Date());
        tituloEleitor.setPessoaFisica(getPessoaFisicaSelecionada());
        addDocumentoPessoa(tituloEleitor);
        carteiraTrabalho = new CarteiraTrabalho();
        carteiraTrabalhos = new ArrayList<CarteiraTrabalho>();
        habilitacao = new Habilitacao();
        habilitacoes = new ArrayList<Habilitacao>();
        situacaoMilitar = new SituacaoMilitar();
        situacaoMilitar.setDataRegistro(new Date());
        situacaoMilitar.setPessoaFisica((PessoaFisica) selecionado);
        addDocumentoPessoa(situacaoMilitar);
        contaCorrenteBancaria = new ContaCorrenteBancaria();
        telefoneAux = new Telefone();

        ((PessoaFisica) selecionado).setContaCorrenteBancPessoas(new ArrayList<ContaCorrenteBancPessoa>());
        contaCorrenteBancPessoa = new ContaCorrenteBancPessoa();
        listaExcluidos = new ArrayList<ContaCorrenteBancPessoa>();
    }

    public void addDocumentoPessoa(DocumentoPessoal dp) {
        getPessoaFisicaSelecionada().getDocumentosPessoais().add(dp);
    }

    private PessoaFisica getPessoaFisicaSelecionada() {
        return (PessoaFisica) selecionado;
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
        return getPessoaFisicaSelecionada().getTelefones();
    }

    public List<EnderecoCorreio> getEnderecos() {
        return getPessoaFisicaSelecionada().getEnderecos();
    }

//    public List<ContaCorrenteBancaria> getContasCorrentes() {
//        return getPessoaFisicaSelecionada().getContasCorrentesBancarias();
//    }

    public String getCadastroPessoaFisica() {
        return cadastroPessoaFisica;
    }

    public void setCadastroPessoaFisica(String cadastroPessoaFisica) {
        this.cadastroPessoaFisica = cadastroPessoaFisica;
    }

    public void novoFone() {

        telefone.setPessoa((PessoaFisica) selecionado);
        getPessoaFisicaSelecionada().getTelefones().add(telefone);
        telefone = new Telefone();
    }

    public void novoRg() {
        rg.setDataRegistro(new Date());
        rg.setPessoaFisica((PessoaFisica) selecionado);
        if (getPessoaFisicaSelecionada().getDocumentosPessoais().contains(rg)) {
            getPessoaFisicaSelecionada().getDocumentosPessoais().set(getPessoaFisicaSelecionada().getDocumentosPessoais().indexOf(rg), rg);
        } else {
            getPessoaFisicaSelecionada().getDocumentosPessoais().add(rg);
        }
    }

    public void removeRg(ActionEvent evento) {
        getPessoaFisicaSelecionada().getDocumentosPessoais().remove((RG) evento.getComponent().getAttributes().get("removeRg"));
        rgs.remove(0);
        rg = new RG();
    }

    public void novoTitulo() {
        tituloEleitores.clear();
        tituloEleitor.setDataRegistro(new Date());
        tituloEleitores.add(tituloEleitor);
        tituloEleitores.get(0).setPessoaFisica((PessoaFisica) selecionado);

        if (getPessoaFisicaSelecionada().getDocumentosPessoais().contains(tituloEleitor)) {
            getPessoaFisicaSelecionada().getDocumentosPessoais().set(getPessoaFisicaSelecionada().getDocumentosPessoais().lastIndexOf(tituloEleitor), (DocumentoPessoal) tituloEleitores.get(0));
        } else {
            getPessoaFisicaSelecionada().getDocumentosPessoais().add(tituloEleitores.get(0));
        }
    }

    public void removeTitulo(ActionEvent evento) {
        getPessoaFisicaSelecionada().getDocumentosPessoais().remove((TituloEleitor) evento.getComponent().getAttributes().get("removeTitulo"));
        tituloEleitores.remove(0);
        tituloEleitor = new TituloEleitor();
    }

    public void novaHabilitacao() {
        habilitacao.setDataRegistro(new Date());
        habilitacao.setPessoaFisica(getPessoaFisicaSelecionada());
        habilitacoes.add(habilitacao);
        if (getPessoaFisicaSelecionada().getDocumentosPessoais().contains(habilitacao)) {
            getPessoaFisicaSelecionada().getDocumentosPessoais().set(getPessoaFisicaSelecionada().getDocumentosPessoais().indexOf(habilitacao), habilitacao);
        } else {
            getPessoaFisicaSelecionada().getDocumentosPessoais().add(habilitacao);
        }
    }

    public void removeHabilitacao(ActionEvent evento) {
        getPessoaFisicaSelecionada().getDocumentosPessoais().remove((Habilitacao) evento.getComponent().getAttributes().get("removeHabilitacao"));
        habilitacoes.remove((Habilitacao) evento.getComponent().getAttributes().get("removeHabilitacao"));
        habilitacao = new Habilitacao();
    }

    public void novoTrabalho() {
        carteiraTrabalho.setDataRegistro(new Date());
        carteiraTrabalho.setPessoaFisica(getPessoaFisicaSelecionada());
        carteiraTrabalhos.add(carteiraTrabalho);
        if (operacao == Operacoes.NOVO) {
        }
        if (getPessoaFisicaSelecionada().getDocumentosPessoais().contains(carteiraTrabalho)) {
            getPessoaFisicaSelecionada().getDocumentosPessoais().set(getPessoaFisicaSelecionada().getDocumentosPessoais().indexOf(carteiraTrabalho), carteiraTrabalho);
        } else {
            getPessoaFisicaSelecionada().getDocumentosPessoais().add(carteiraTrabalho);
        }
    }

    public void removeTrabalho(ActionEvent evento) {
        getPessoaFisicaSelecionada().getDocumentosPessoais().remove((CarteiraTrabalho) evento.getComponent().getAttributes().get("removeTrabalho"));
        carteiraTrabalhos.remove((CarteiraTrabalho) evento.getComponent().getAttributes().get("removeTrabalho"));
        carteiraTrabalho = new CarteiraTrabalho();
    }

    public void novoMilitar() {
        situacaoMilitar.setDataRegistro(new Date());
        situacaoMilitar.setPessoaFisica((PessoaFisica) selecionado);
        if (getPessoaFisicaSelecionada().getDocumentosPessoais().contains(situacaoMilitar)) {
            getPessoaFisicaSelecionada().getDocumentosPessoais().set(getPessoaFisicaSelecionada().getDocumentosPessoais().indexOf(situacaoMilitar), situacaoMilitar);
        } else {
            getPessoaFisicaSelecionada().getDocumentosPessoais().add(situacaoMilitar);
        }
    }

    public void removeMilitar(ActionEvent evento) {
        getPessoaFisicaSelecionada().getDocumentosPessoais().remove((SituacaoMilitar) evento.getComponent().getAttributes().get("removeMilitar"));
        situacaoMilitares.remove(0);
        situacaoMilitar = new SituacaoMilitar();
    }

    public void novoEndereco() {
        getPessoaFisicaSelecionada().getEnderecos().add(endereco);
        endereco = new EnderecoCorreio();
    }

    public void removeFone(ActionEvent evento) {
        getPessoaFisicaSelecionada().getTelefones().remove((Telefone) evento.getComponent().getAttributes().get("vfone"));
    }

//    public void removeConta(ActionEvent evento) {
//        getPessoaFisicaSelecionada().getContasCorrentesBancarias().remove((ContaCorrenteBancaria) evento.getComponent().getAttributes().get("vconta"));
//    }

    public void removeEndereco(ActionEvent evento) {
        getPessoaFisicaSelecionada().getEnderecos().remove((EnderecoCorreio) evento.getComponent().getAttributes().get("vendereco"));
    }

    public void tituloNulo() {
        tituloEleitor = new TituloEleitor();
    }

    public PessoaFisicaControlador() {
        metadata = new EntidadeMetaData(PessoaFisica.class);
        cadastroPessoaFisica = new String("");
    }

    public PessoaFisicaFacade getFacade() {
        return pessoaFisicaFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return pessoaFisicaFacade;
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
        for (TipoSituacaoMilitar object : TipoSituacaoMilitar.values()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getCategoria() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();

        for (CategoriaCertificadoMilitar object : CategoriaCertificadoMilitar.values()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public void setaEndereco(ActionEvent evento) {
        setEnderecoAux((Endereco) evento.getComponent().getAttributes().get("alteraEndereco"));
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
        operacao = Operacoes.EDITAR;
        Pessoa p = (Pessoa) evento.getComponent().getAttributes().get("objeto");
        selecionado = pessoaFisicaFacade.recuperar(p.getId());
        enderecoAux = new Endereco();
        telefoneAux = new Telefone();
        telefone = new Telefone();
        endereco = new EnderecoCorreio();
        habilitacao = new Habilitacao();
        habilitacoes = new ArrayList<Habilitacao>();
        carteiraTrabalho = new CarteiraTrabalho();
        carteiraTrabalhos = new ArrayList<CarteiraTrabalho>();
        enderecoAux = new Endereco();

        if (verificaRG() == null) {
            rg = new RG();
            rg.setDataRegistro(new Date());
            rg.setPessoaFisica((PessoaFisica) selecionado);
            addDocumentoPessoa(rg);
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
        for (DocumentoPessoal doc : getPessoaFisicaSelecionada().getDocumentosPessoais()) {
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
            }
        }

        listaExcluidos = new ArrayList<ContaCorrenteBancPessoa>();
        contaCorrenteBancPessoa = new ContaCorrenteBancPessoa();
        contaCorrenteBancaria = new ContaCorrenteBancaria();
    }

    public RG verificaRG() {
        for (DocumentoPessoal documento : getPessoaFisicaSelecionada().getDocumentosPessoais()) {
            if (documento instanceof RG) {
                return (RG) documento;
            }
        }
        return null;
    }

    public TituloEleitor verificaTitulo() {
        for (DocumentoPessoal documento : getPessoaFisicaSelecionada().getDocumentosPessoais()) {
            if (documento instanceof TituloEleitor) {
                return (TituloEleitor) documento;
            }
        }
        return null;
    }

    public SituacaoMilitar verificaMilitar() {
        for (DocumentoPessoal documento : getPessoaFisicaSelecionada().getDocumentosPessoais()) {
            if (documento instanceof SituacaoMilitar) {
                return (SituacaoMilitar) documento;
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
        listaContas.add(pessoaFisicaFacade.recuperarConta(contaCorrenteBancaria));
        return listaContas;
    }

    public void setListaContas(List<ContaCorrenteBancaria> listaContas) {
        this.listaContas = listaContas;
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

    @Override
    public Boolean validaCampos() {
        Boolean retorno = super.validaCampos();
        if (validaCpf(getPessoaFisicaSelecionada().getCpf()) == false) {
            FacesContext.getCurrentInstance().addMessage("Formulario:cpf", new FacesMessage(FacesMessage.SEVERITY_ERROR, "CPF inválida!", "Por favor, verificar CPF."));
            retorno = false;
        }
        if (getPessoaFisicaSelecionada().getEnderecos().isEmpty()) {
            FacesContext.getCurrentInstance().addMessage("Formulario:logradouro", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Nenhum Endereco detectado!", "Deve existir pelo menos um!"));
            retorno = false;
        }
        if (!getPessoaFisicaSelecionada().getEmail().isEmpty()) {
            if (validaEmail(getPessoaFisicaSelecionada().getEmail()) == false) {
                FacesContext.getCurrentInstance().addMessage("Formulario:email", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Email inválido!", "Por Favor verifique!"));
                retorno = false;
            }
        }
        if (pessoaFisicaFacade.getPessoaFacade().hasOutraPessoaComMesmoCpf(getPessoaFisicaSelecionada(), false)) {
            FacesContext.getCurrentInstance().addMessage("Formulario:cpf", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Impossível Salvar", "Já existe um registro com este número de CPF"));
            retorno = false;
        }

        //se não for o cadastro a partir do RH as validações param aqui
        if (!"RH".equals(cadastroPessoaFisica)) {
            return true;
        }

        if (getPessoaFisicaSelecionada().getDataNascimento() == null) {
            FacesContext.getCurrentInstance().addMessage("Formulario:dataNascimento", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Data de Nascimento não informada", "A data de Nascimento deve ser informada!"));
            retorno = false;
        }

        if (getPessoaFisicaSelecionada().getSexo() == null) {
            FacesContext.getCurrentInstance().addMessage("Formulario:sexo", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Sexo não informado", "O Sexo não foi informado !"));
            retorno = false;
        }


        if (getPessoaFisicaSelecionada().getSexo() == Sexo.MASCULINO) {
            if (situacaoMilitar.getDataEmissao() == null || situacaoMilitar.getOrgaoEmissao() == null || situacaoMilitar.getCertificadoMilitar() == null) {
                FacesContext.getCurrentInstance().addMessage("Formulario:painelMilitar:situacaoMilitar", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Nenhuma Situação Militar", "Deve ser cadastrada ao menos uma situação militar!"));
                retorno = false;
            }
        } else {
            getPessoaFisicaSelecionada().getDocumentosPessoais().remove(situacaoMilitar);
        }

        if (rg.getNumero() == null || rg.getDataemissao() == null || rg.getOrgaoEmissao() == null) {
            FacesContext.getCurrentInstance().addMessage("Formulario:painelRg:numerorg", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Nenhum RG Cadastrado", "Deve ser cadastrado todos os dados do RG!"));
            retorno = false;
        }

        if (tituloEleitor.getNumero() == null || tituloEleitor.getZona() == null || tituloEleitor.getCidade() == null || tituloEleitor.getSessao() == null || tituloEleitor.getDataEmissao() == null) {
            FacesContext.getCurrentInstance().addMessage("Formulario:painelTitulo:inputTituloNumero", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Nenhum Título Cadastrado", "Deve ser cadastrado todos os dados do Título de Eleitor!"));
            retorno = false;
        }

        if (carteiraTrabalhos.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage("Formulario:painelTraba:numeroCarteiraTrabalho", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Nenhuma Carteira de Trabalho Cadastrada", "Deve ser cadastrada ao menos uma Carteira de Trabalho!"));
            retorno = false;
        }

        if (getPessoaFisicaSelecionada().getEnderecos().isEmpty()) {
            FacesContext.getCurrentInstance().addMessage("Formulario:logra:estado", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Nenhum Endereço Cadastrado", "Deve ser cadastrado ao menos um Endereço!"));
            retorno = false;
        }

        return retorno;
    }

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
        return consultaCepFacade.consultaLocalidades(endereco.getUf(), parte.trim());
    }

    public List<CEPLogradouro> completaLogradouro(String parte) {
        return consultaCepFacade.consultaLogradouros(endereco.getLocalidade(), parte.trim());
    }

    public List<CEPBairro> completaBairro(String parte) {
        return consultaCepFacade.consultaBairros(endereco.getLocalidade(), parte.trim());
    }

    public Boolean liberaMaster() {
        PessoaFisica pf = ((PessoaFisica) selecionado);
        for (ContaCorrenteBancPessoa ccbf : pf.getContaCorrenteBancPessoas()) {
            if (ccbf.getPrincipal()) {
                return false;
            }
        }
        return true;
    }

    public void atualizaLogradouros() {
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
        }
    }

    @Override
    public String salvar() {
        if (validaCampos() == true) {
            try {

                if (operacao == Operacoes.NOVO) {
                    this.getFacede().salvarNovo(selecionado);
                } else {
                    pessoaFisicaFacade.salvar((PessoaFisica) selecionado, listaExcluidos);
                }
                lista = null;
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Salvo com Sucesso", ""));
                return getCaminho();

            } catch (Exception e) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Exceção do sistema!", e.getMessage().toString()));
                return "edita.xhtml";
            }
        } else {
            return "edita.xhtml";
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

    public void removeContaCorrenteBancPessoa(ActionEvent evento) {
        ContaCorrenteBancPessoa c = (ContaCorrenteBancPessoa) evento.getComponent().getAttributes().get("objeto");
        ((PessoaFisica) selecionado).getContaCorrenteBancPessoas().remove(c);
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

    public ContaCorrenteBancPessoa getContaCorrenteBancPessoa() {
        return contaCorrenteBancPessoa;
    }

    public void setContaCorrenteBancPessoa(ContaCorrenteBancPessoa contaCorrenteBancPessoa) {
        this.contaCorrenteBancPessoa = contaCorrenteBancPessoa;
    }

    public List<PessoaFisica> completaPessoaFisica(String parte) {
        return pessoaFisicaFacade.listaFiltrando(parte.trim(), "nome");
    }

    public ConverterAutoComplete getConverterPessoaFisica() {
        if (converterPessoaFisica == null) {
            converterPessoaFisica = new ConverterAutoComplete(PessoaFisica.class, pessoaFisicaFacade);
        }
        return converterPessoaFisica;
    }

}
