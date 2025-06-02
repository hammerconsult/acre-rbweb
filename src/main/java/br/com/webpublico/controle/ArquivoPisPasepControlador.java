package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.RegistroPisPasep;
import br.com.webpublico.enums.TipoFolhaDePagamento;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.ConverterGenerico;
import br.com.webpublico.util.EntidadeMetaData;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.StringUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Claudio
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoArquivoPisPasep", pattern = "/arquivo-pis-pasep/novo/", viewId = "/faces/rh/administracaodepagamento/arquivopispasep/edita.xhtml"),
    @URLMapping(id = "listarArquivoPisPasep", pattern = "/arquivo-pis-pasep/listar/", viewId = "/faces/rh/administracaodepagamento/arquivopispasep/lista.xhtml"),
    @URLMapping(id = "visualizarArquivoPisPasep", pattern = "/arquivo-pis-pasep/ver/#{arquivoPisPasepControlador.id}/", viewId = "/faces/rh/administracaodepagamento/arquivopispasep/visualizar.xhtml")
})
public class ArquivoPisPasepControlador extends PrettyControlador<ArquivoPisPasep> implements Serializable, CRUD {

    @EJB
    private ArquivoPisPasepFacade arquivoPisPasepFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private FuncoesFolhaFacade funcoesFolhaFacade;
    @EJB
    private UnidadeOrganizacionalFacade unidadeOrganizacionalFacade;
    @EJB
    private ContaBancariaEntidadeFacade contaBancariaEntidadeFacade;
    @EJB
    private EnderecoFacade enderecoFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    private List<ContratoFP> listaContratosVigente;
    private List<ContratoFP> contratosComTrintaDiasTrabalhados;
    private Integer ano;
    private UnidadeOrganizacional unidadeOrganizacional;
    private RegistroPisPasep registroPisPasep;
    private ConverterGenerico converterContaBancariaEntidade;
    private Date dataPagamento;
    private String numeroConvenio;
    private final String CODIGO_FORMA_REPASSE = "1";
    private String agenciaLancamento;
    private String digitoVerificadorAgenciaLancamento;
    private String email;
    private Agencia agencia;
    private EnderecoCorreio enderecoCorreio;
    private File arquivo;
    //private StreamedContent streamedContent;
    //private List<RegistroPisPasep> listaDetalhes;
    private List<String> listaErrosDetalhe = new ArrayList<String>();
    private boolean erroDetalhe = false;
    private boolean erroHeader = false;
    private final int INICIO_ARQUIVO = 0;
    private ContaBancariaEntidade contaBancariaEntidade;
    private StreamedContent fileDownload;
    private ArquivoPisPasep arquivoPisPasep;

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public Date getDataPagamento() {
        return dataPagamento;
    }

    public void setDataPagamento(Date dataPagamento) {
        this.dataPagamento = dataPagamento;
    }

    public String getNumeroConvenio() {
        return numeroConvenio;
    }

    public void setNumeroConvenio(String numeroConvenio) {
        this.numeroConvenio = numeroConvenio;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAgenciaLancamento() {
        return agenciaLancamento;
    }

    public void setAgenciaLancamento(String agenciaLancamento) {
        this.agenciaLancamento = agenciaLancamento;
    }

    public String getDigitoVerificadorAgenciaLancamento() {
        return digitoVerificadorAgenciaLancamento;
    }

    public void setDigitoVerificadorAgenciaLancamento(String digitoVerificadorAgenciaLancamento) {
        this.digitoVerificadorAgenciaLancamento = digitoVerificadorAgenciaLancamento;
    }

    public ContaBancariaEntidade getContaBancariaEntidade() {
        return contaBancariaEntidade;
    }

    public void setContaBancariaEntidade(ContaBancariaEntidade contaBancariaEntidade) {
        this.contaBancariaEntidade = contaBancariaEntidade;
    }

    public List<String> getListaErrosDetalhe() {
        return listaErrosDetalhe;
    }

    public boolean isErroDetalhe() {
        return erroDetalhe;
    }

    @Override
    public AbstractFacade getFacede() {
        return arquivoPisPasepFacade;
    }

    public ArquivoPisPasepControlador() {
        metadata = new EntidadeMetaData(ArquivoPisPasep.class);
    }

    public List<SelectItem> getUnidadesEntidade() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (UnidadeOrganizacional object : unidadeOrganizacionalFacade.retornaEntidades()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/arquivo-pis-pasep/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    @URLAction(mappingId = "novoArquivoPisPasep", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        registroPisPasep = new RegistroPisPasep();
        limparCampos();
    }

    @URLAction(mappingId = "visualizarArquivoPisPasep", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    public ConverterGenerico getConverterContaBancariaEntidade() {
        if (converterContaBancariaEntidade == null) {
            converterContaBancariaEntidade = new ConverterGenerico(ContaBancariaEntidade.class, contaBancariaEntidadeFacade);
        }
        return converterContaBancariaEntidade;
    }

    public List<ContratoFP> getContratosComTrintaDiasTrabalhados() {
        return contratosComTrintaDiasTrabalhados;
    }

    private List<ContratoFP> getContratosVigente() {

        Date data = montaData(31, 12, ano);

        HierarquiaOrganizacional ho = hierarquiaOrganizacionalFacade.getHierarquiaDaUnidade("ADMINISTRATIVA", unidadeOrganizacional, data);

        if (ho != null && ho.getId() != null) {
            listaContratosVigente = contratoFPFacade.recuperaContratoVigenteMatriculaPorLocalDeTrabalhoRecursivaPelaView(ho, data);
        } else {
            FacesUtil.addError("Problema!", "O sistema não encontrou a hierarquia organizacional da unidade: " + unidadeOrganizacional.getCodigoDescricao() + ". Contate o administrador.");
        }
        return listaContratosVigente;
    }

    private Date montaData(int dia, int mes, int ano) {
        Calendar c = Calendar.getInstance();
        c.set(ano, mes - 1, ano);
        return c.getTime();
    }

    public void getContratosVigenteComTrintaDiasTrabalhados() {
        getContratosVigente();
        contratosComTrintaDiasTrabalhados = new ArrayList<ContratoFP>();
        for (ContratoFP contrato : listaContratosVigente) {
            if (temTrintaDiasTrabalhados(contrato)) {
                contratosComTrintaDiasTrabalhados.add(contrato);
            }
        }
    }

    private boolean temTrintaDiasTrabalhados(ContratoFP contrato) {
        Double diasTrabalhados = 0d;

        contrato.setAno(ano);
        FolhaDePagamento folha = new FolhaDePagamento();
        folha.setTipoFolhaDePagamento(TipoFolhaDePagamento.NORMAL);
        contrato.setFolha(folha);

        /*percorre os meses do ano de 1 a 12
         somando a quantidade de dias trabalhados no ano*/
        for (int i = 1; i <= 12; i++) {
            contrato.setMes(i);
            diasTrabalhados += funcoesFolhaFacade.diasTrabalhados(contrato, i, ano, TipoDiasTrabalhados.NORMAL, null, null, null);
        }
        if (diasTrabalhados >= 30) {
            return true;
        }
        return false;
    }

    public String gerarArquivo() {
        getContratosVigenteComTrintaDiasTrabalhados();
        listaErrosDetalhe = new ArrayList<String>();
        registroPisPasep = new RegistroPisPasep();
        erroHeader = false;
        erroDetalhe = false;
        StringBuilder textoDoArquivo = new StringBuilder();
        if (!contratosComTrintaDiasTrabalhados.isEmpty()) {
            for (ContratoFP contrato : contratosComTrintaDiasTrabalhados) {
                textoDoArquivo.append(StringUtil.removeCaracteresEspeciais(montaDetalhe(contrato)));
            }

            if (erroDetalhe) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Impossível gerar o arquivo.", "Dados inconsistentes para " + registroPisPasep.getDetalheNome().trim() + ". Clique em Visualizar dados inconsistentes."));
            } else {
                textoDoArquivo.append(montaTrailer());
                textoDoArquivo.insert(INICIO_ARQUIVO, montaHeader());

                if (!erroHeader) {
                    return textoDoArquivo.toString();
                }
            }
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Impossível gerar o arquivo.", "Nenhum contrato encontrado."));
        }
        return "";
    }

    public void criaHeader() {
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
        registroPisPasep.setHeaderTipoRegistro("1");
        registroPisPasep.setHeaderNomeArquivo("FPSF900");
        registroPisPasep.setHeaderDataGeracao(sdf.format(new Date()));
        registroPisPasep.setHeaderCnpj(contaBancariaEntidade.getEntidade().getPessoaJuridica().getCnpj().replaceAll("\\.", "").replaceAll("/", "").replaceAll("-", ""));
        registroPisPasep.setHeaderNumeroRemessa(String.valueOf(arquivoPisPasepFacade.getUltimaSequenciaMaisUm()));
        registroPisPasep.setHeaderNumeroRemessa(StringUtil.cortaOuCompletaEsquerda(registroPisPasep.getHeaderNumeroRemessa(), 6, "0")); //sequencial incrementando a cada arquivo FPS900, recuperar do banco
        agencia = contaBancariaEntidade.getAgencia();
        if (agencia != null && agencia.getId() != null) {
            registroPisPasep.setHeaderAgenciaControle(agencia.getNumeroAgencia() != null ? agencia.getNumeroAgencia() : "");
            registroPisPasep.setHeaderDigitoVerificadorAgenciaControle(agencia.getDigitoVerificador() != null ? agencia.getDigitoVerificador() : " ");
        }
        registroPisPasep.setHeaderDataPagamento(sdf.format(dataPagamento));
        registroPisPasep.setHeaderNumeroConvenio(StringUtil.cortaOuCompletaEsquerda(numeroConvenio, 6, "0"));
        registroPisPasep.setHeaderCodigoFormaRepasse(CODIGO_FORMA_REPASSE);
        registroPisPasep.setHeaderAgenciaLancamento(StringUtil.cortaOuCompletaEsquerda(agenciaLancamento, 4, "0"));
        registroPisPasep.setHeaderDigitoVerificadorAgenciaLancamento(StringUtil.cortaOuCompletaEsquerda(digitoVerificadorAgenciaLancamento, 1, "0"));

        String numeroConta = contaBancariaEntidade.getNumeroConta() != null ? contaBancariaEntidade.getNumeroConta() : "";
        String digitoVerificador = contaBancariaEntidade.getDigitoVerificador() != null ? contaBancariaEntidade.getDigitoVerificador() : " ";
        registroPisPasep.setHeaderContaCorrenteLancamento(numeroConta.isEmpty() ? numeroConta : StringUtil.cortaOuCompletaEsquerda(numeroConta, 11, "0"));
        registroPisPasep.setHeaderDigitoVerificadorContaCorrenteLancamento(digitoVerificador);

        registroPisPasep.setHeaderCodigoLancamentoGRU(StringUtil.cortaOuCompletaEsquerda(" ", 14, " "));
        registroPisPasep.setHeaderCodigoBanco(StringUtil.cortaOuCompletaEsquerda(" ", 3, " "));
        registroPisPasep.setHeaderDigitoVerificadorBanco(StringUtil.cortaOuCompletaEsquerda(" ", 1, " "));
        registroPisPasep.setHeader14EspacoBranco(StringUtil.cortaOuCompletaEsquerda(" ", 14, " "));
        registroPisPasep.setHeaderEmail(StringUtil.cortaOuCompletaDireita(email.trim(), 80, " "));
        registroPisPasep.setHeader43EspacoBranco(StringUtil.cortaOuCompletaEsquerda(" ", 43, " "));
    }

    public String montaHeader() {
        criaHeader();
        StringBuilder sb = new StringBuilder();
        sb.append(registroPisPasep.getHeaderTipoRegistro());
        sb.append(registroPisPasep.getHeaderNomeArquivo());
        sb.append(registroPisPasep.getHeaderDataGeracao());
        sb.append(registroPisPasep.getHeaderCnpj().replaceAll("\\.", "").replaceAll("/", "").replaceAll("-", ""));
        sb.append(registroPisPasep.getHeaderNumeroRemessa());
        sb.append(registroPisPasep.getHeaderAgenciaControle()); // 4 caracteres, mas tem com de 3 e 5 no banco
        sb.append(registroPisPasep.getHeaderDigitoVerificadorAgenciaControle());
        sb.append(registroPisPasep.getHeaderDataPagamento());
        sb.append(registroPisPasep.getHeaderNumeroConvenio());
        sb.append(registroPisPasep.getHeaderCodigoFormaRepasse());
        sb.append(registroPisPasep.getHeaderAgenciaLancamento());
        sb.append(registroPisPasep.getHeaderDigitoVerificadorAgenciaLancamento());
        sb.append(registroPisPasep.getHeaderContaCorrenteLancamento());
        sb.append(registroPisPasep.getHeaderDigitoVerificadorContaCorrenteLancamento());
        sb.append(registroPisPasep.getHeaderCodigoLancamentoGRU());
        sb.append(registroPisPasep.getHeaderCodigoBanco());
        sb.append(registroPisPasep.getHeaderDigitoVerificadorBanco());
        sb.append(registroPisPasep.getHeader14EspacoBranco());
        sb.append(registroPisPasep.getHeaderEmail());
        sb.append(registroPisPasep.getHeader43EspacoBranco());

        if (sb.length() != 228) {
            erroHeader = true;
            localizaErroHeader();
        }
        sb.append("\r\n");
        return sb.toString();
    }

    public void localizaErroHeader() {
        if (registroPisPasep.getHeaderCnpj().length() != 14) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Impossível gerar o arquivo.", "Problema com o CNPJ da Entidade. " + registroPisPasep.getHeaderCnpj()));
        }
        if (registroPisPasep.getHeaderAgenciaControle() == null || registroPisPasep.getHeaderAgenciaControle().length() != 4) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Impossível gerar o arquivo.", "Problema com o Número da Agência da Entidade. " + registroPisPasep.getHeaderAgenciaControle()));
        }
        if (registroPisPasep.getHeaderDigitoVerificadorAgenciaControle() == null || registroPisPasep.getHeaderDigitoVerificadorAgenciaControle().length() != 1) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Impossível gerar o arquivo.", "Problema com o Dígito da Agência da Entidade. " + registroPisPasep.getHeaderDigitoVerificadorAgenciaControle()));
        }
        if (registroPisPasep.getHeaderContaCorrenteLancamento() == null || registroPisPasep.getHeaderContaCorrenteLancamento().length() != 11) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Impossível gerar o arquivo.", "Problema com a Conta Corrente da Entidade. " + registroPisPasep.getHeaderContaCorrenteLancamento()));
        }
        if (registroPisPasep.getHeaderDigitoVerificadorContaCorrenteLancamento() == null || registroPisPasep.getHeaderDigitoVerificadorContaCorrenteLancamento().length() != 1) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Impossível gerar o arquivo.", "Problema com o Dígito Verificador da Conta Corrente da Entidade. " + registroPisPasep.getHeaderDigitoVerificadorContaCorrenteLancamento()));
        }
    }

    public void criaDetalhe(ContratoFP contrato) {
        registroPisPasep.setDetalheTipoRegistro("2");
        String numeroPisPasep = getInscricaoPisPasep(contrato.getMatriculaFP().getPessoa());
        if (!numeroPisPasep.isEmpty()) {
            registroPisPasep.setDetalheInscricao(StringUtil.cortaOuCompletaEsquerda(numeroPisPasep, 11, "0"));
        } else {
            registroPisPasep.setDetalheInscricao(numeroPisPasep);
        }
        registroPisPasep.setDetalheNome(StringUtil.cortaOuCompletaDireita(contrato.getMatriculaFP().getPessoa().getNome(), 50, " "));
        registroPisPasep.setDetalheMatricula(StringUtil.preencheString(contrato.getMatriculaFP().getMatricula(), 15, '0'));

        enderecoCorreio = getEnderecoCorreio(contrato.getMatriculaFP().getPessoa());
        if (enderecoCorreio != null) {
            registroPisPasep.setDetalheEndereco(StringUtil.cortaOuCompletaDireita(enderecoCorreio.getLogradouro() != null ? enderecoCorreio.getLogradouro() : " ", 50, " "));
            registroPisPasep.setDetalheNumero(StringUtil.cortaOuCompletaDireita(enderecoCorreio.getNumero() != null ? enderecoCorreio.getNumero() : " ", 5, " "));
            registroPisPasep.setDetalheComplemento(StringUtil.cortaOuCompletaDireita(enderecoCorreio.getComplemento() != null ? enderecoCorreio.getComplemento() : " ", 15, " "));
            registroPisPasep.setDetalheBairro(StringUtil.cortaOuCompletaDireita(enderecoCorreio.getBairro() != null ? enderecoCorreio.getBairro() : " ", 30, " "));
            registroPisPasep.setDetalheMunicipio(StringUtil.cortaOuCompletaDireita(enderecoCorreio.getLocalidade() != null ? enderecoCorreio.getLocalidade() : " ", 30, " "));
            registroPisPasep.setDetalheUF(StringUtil.cortaOuCompletaDireita(enderecoCorreio.getUf() != null ? enderecoCorreio.getUf() : "", 2, ""));
            registroPisPasep.setDetalheCEP(StringUtil.cortaOuCompletaDireita(enderecoCorreio.getCep() != null ? enderecoCorreio.getCep().replaceAll("-", "") : " ", 8, "0"));
        }
        registroPisPasep.setDetalhe11EspacoBranco(StringUtil.preencheString(" ", 11, ' '));
    }

    public String getInscricaoPisPasep(PessoaFisica pessoa) {
        CarteiraTrabalho carteira = arquivoPisPasepFacade.getPessoaFacade().recuperarCarteiraTrabalhoPelaPessoa(pessoa);
        if (carteira == null || carteira.getId() == null) {
            return "";
        }
        return carteira.getPisPasep();
    }

    public String montaDetalhe(ContratoFP contrato) {
        criaDetalhe(contrato);
        StringBuilder sb = new StringBuilder();
        sb.append(registroPisPasep.getDetalheTipoRegistro());
        sb.append(registroPisPasep.getDetalheInscricao());
        sb.append(registroPisPasep.getDetalheNome());
        sb.append(registroPisPasep.getDetalheMatricula());
        sb.append(registroPisPasep.getDetalheEndereco());
        sb.append(registroPisPasep.getDetalheNumero());
        sb.append(registroPisPasep.getDetalheComplemento());
        sb.append(registroPisPasep.getDetalheBairro());
        sb.append(registroPisPasep.getDetalheMunicipio());
        sb.append(registroPisPasep.getDetalheUF());
        sb.append(registroPisPasep.getDetalheCEP());
        sb.append(registroPisPasep.getDetalhe11EspacoBranco());

        if (sb.length() != 228) {
            localizaErroDetalhe();
        }

        sb.append("\r\n");

        return sb.toString();

    }

    public void localizaErroDetalhe() {
        erroDetalhe = false;
        if (registroPisPasep.getDetalheInscricao() == null || registroPisPasep.getDetalheInscricao().length() != 11) {
            erroDetalhe = true;
            listaErrosDetalhe.add("A Inscrição do(a) " + registroPisPasep.getDetalheNome().trim() + " está incorreta.");
        }
        if (registroPisPasep.getDetalheMatricula() == null || registroPisPasep.getDetalheMatricula().length() != 15) {
            erroDetalhe = true;
            listaErrosDetalhe.add("A Matrícula do(a) " + registroPisPasep.getDetalheNome().trim() + " está incorreta.");
        }
        if (registroPisPasep.getDetalheCEP() == null || registroPisPasep.getDetalheCEP().length() != 8) {
            erroDetalhe = true;
            listaErrosDetalhe.add("O CEP do(a) " + registroPisPasep.getDetalheNome().trim() + " está incorreto.");
        }
    }

    public void criaTrailer() {
        registroPisPasep.setTrailerTipoRegistro("9");
        registroPisPasep.setTrailer221EspacoBranco(StringUtil.preencheString(" ", 221, ' '));
        registroPisPasep.setTrailerQuantidadeRegistroDetalhe(StringUtil.cortaOuCompletaEsquerda(String.valueOf(contratosComTrintaDiasTrabalhados.size()), 6, "0"));
    }

    public String montaTrailer() {
        criaTrailer();
        StringBuilder sb = new StringBuilder();
        sb.append(registroPisPasep.getTrailerTipoRegistro());
        sb.append(registroPisPasep.getTrailer221EspacoBranco());
        sb.append(registroPisPasep.getTrailerQuantidadeRegistroDetalhe());

        return sb.toString();
    }

    public Agencia getAgencia() {
        agencia = contaBancariaEntidadeFacade.getAgenciaFacade().getAgenciaPorEntidade(unidadeOrganizacional.getEntidade());
        return agencia;
    }

    public List<SelectItem> getContasBancarias() {
        List<SelectItem> lista = new ArrayList<SelectItem>();
        lista.add(new SelectItem(null, ""));
        for (ContaBancariaEntidade conta : this.contaBancariaEntidadeFacade.listaOrdenada()) {
            lista.add(new SelectItem(conta, conta.getNomeConta()));
        }
        return lista;
    }

    public EnderecoCorreio getEnderecoCorreio(Pessoa pessoa) {
        List<EnderecoCorreio> enderecos = enderecoFacade.retornaEnderecoCorreioDaPessoa(pessoa);
        if (enderecos != null && !enderecos.isEmpty()) {
            return enderecos.get(0);
        }
        return null;
    }

    public String gerarArquivoPisPasep() {
        if (validaCampos()) {
            String textoDoArquivo = gerarArquivo();
            if (!textoDoArquivo.isEmpty()) {
                arquivoPisPasep = new ArquivoPisPasep();
                arquivoPisPasep.setDataGerado(new Date());
                arquivoPisPasep.setSequencia(Long.parseLong(registroPisPasep.getHeaderNumeroRemessa()));
                arquivoPisPasep.setContaBancariaEntidade(contaBancariaEntidade);
                arquivoPisPasep.setUsuarioSistema(sistemaFacade.getUsuarioCorrente());
                arquivoPisPasep.setConteudo(textoDoArquivo);

                arquivoPisPasepFacade.salvar(arquivoPisPasep);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Arquivo gerado com sucesso.", ""));
                return "lista";
            }
        }
        return "edita";
    }

    public Boolean validaCampos() {
        boolean valido = true;
        if (contaBancariaEntidade == null || contaBancariaEntidade.getId() == null) {
            valido = false;
            FacesUtil.addError("Impossível continuar.", "Selecione uma Conta Bancária.");
        } else if (contaBancariaEntidade.getEntidade() == null) {
            valido = false;
            FacesUtil.addError("Impossível continuar.", "A Conta Bancária não possui Entidade.");
        }

        if (unidadeOrganizacional == null || unidadeOrganizacional.getId() == null) {
            valido = false;
            FacesUtil.addError("Impossível continuar.", "Não foi encontrada a Unidade Organizacional para a Entidade da Conta Bancária selecionada.");
        }

        if (ano == null) {
            valido = false;
            FacesUtil.addError("Impossível continuar.", "Informe o ano.");
        }
        if (dataPagamento == null) {
            valido = false;
            FacesUtil.addError("Impossível continuar.", "Informe a data de pagamento.");
        }
        if (numeroConvenio.trim().isEmpty()) {
            valido = false;
            FacesUtil.addError("Impossível continuar.", "Informe o número do convênio.");
        }
        if (CODIGO_FORMA_REPASSE.trim().isEmpty()) {
            valido = false;
            FacesUtil.addError("Impossível continuar.", "Informe o código da forma de repasse.");
        }
        if (agenciaLancamento.trim().isEmpty()) {
            valido = false;
            FacesUtil.addError("Impossível continuar.", "Informe a agência para lançamento.");
        }
//        if (digitoVerificadorAgenciaLancamento.trim().isEmpty()) {
//            valido = false;
//            FacesUtil.addError("Impossível continuar.", "Informe o dígito verificador da agência para lançamento.");
//        }
        if (email.trim().isEmpty()) {
            valido = false;
            FacesUtil.addError("Impossível continuar.", "Informe o E-mail.");
        } else if (!validaEmail(email.trim())) {
            valido = false;
            FacesUtil.addError("Impossível continuar.", "E-mail inválido. " + email.trim());
        }
        return valido;
    }

    public void limparCampos() {
        contaBancariaEntidade = null;
        ano = null;
        dataPagamento = null;
        numeroConvenio = "";
        agenciaLancamento = "";
        digitoVerificadorAgenciaLancamento = "";
        email = "";
        listaErrosDetalhe = new ArrayList<String>();
    }

    public StreamedContent fileDownload(ArquivoPisPasep arquivoPisPasep) throws FileNotFoundException, IOException {
        arquivo = new File("FPSF900.txt");
        FileOutputStream fos = new FileOutputStream(arquivo);
        fos.write(arquivoPisPasep.getConteudo().getBytes("UTF8"));
        fos.close();

        InputStream stream = new FileInputStream(arquivo);
        fileDownload = new DefaultStreamedContent(stream, "text/plain", "FPSF900.txt");
        arquivo = null;
        return fileDownload;
    }

    public void excluirArquivo(ArquivoPisPasep arquivoPisPasep) {
        try {
            this.arquivoPisPasep = arquivoPisPasepFacade.recuperar(arquivoPisPasep.getId());
            arquivoPisPasepFacade.remover(this.arquivoPisPasep);
//            this.lista.remove(this.arquivoPisPasep);
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro.", "Não foi possível excluir este registro. Contate o administrador."));
        }
    }

    private boolean validaEmail(String email) {
        Pattern p = Pattern.compile(".+@.+\\.[a-z]+");
        Matcher m = p.matcher(email);
        return m.matches();
    }

    public void setaUnidadeOrganizacional() {
        if (contaBancariaEntidade != null && contaBancariaEntidade.getId() != null) {
            if (contaBancariaEntidade.getEntidade() != null) {
                unidadeOrganizacional = unidadeOrganizacionalFacade.unidadeOrganizacionalPorEntidade(contaBancariaEntidade.getEntidade());
                if (unidadeOrganizacional.getId() == null) {
                    FacesUtil.addWarn("Atenção!", "Não foi encontrada a Unidade Organizacional para a Entidade da Conta Bancária selecionada.");
                }
            } else {
                FacesUtil.addWarn("Atenção!", "A Conta Bancária não possui Entidade.");
                contaBancariaEntidade = null;
            }
        }
    }
}
