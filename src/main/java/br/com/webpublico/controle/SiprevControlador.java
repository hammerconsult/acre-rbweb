package br.com.webpublico.controle;

/**
 * Created with IntelliJ IDEA.
 * User: Carnage
 * Date: 20/01/14
 * Time: 11:35
 * To change this template use File | Settings | File Templates.
 */

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.AssistenteDetentorArquivoComposicao;
import br.com.webpublico.entidadesauxiliares.BeneficioServidorSiprev;
import br.com.webpublico.entidadesauxiliares.CargoSiprev;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.*;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.joda.time.DateTime;
import org.primefaces.model.DefaultStreamedContent;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@ManagedBean(name = "siprevControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-siprev", pattern = "/arquivo/siprev/novo/", viewId = "/faces/rh/administracaodepagamento/arquivosiprev/edita.xhtml"),
    @URLMapping(id = "ver-siprev", pattern = "/arquivo/siprev/ver/#{siprevControlador.id}/", viewId = "/faces/rh/administracaodepagamento/arquivosiprev/visualizar.xhtml"),
    @URLMapping(id = "editar-siprev", pattern = "/arquivo/siprev/editar/#{siprevControlador.id}/", viewId = "/faces/rh/administracaodepagamento/arquivosiprev/edita.xhtml"),
    @URLMapping(id = "lista-siprev", pattern = "/arquivo/siprev/listar/", viewId = "/faces/rh/administracaodepagamento/arquivosiprev/lista.xhtml")
})
public class SiprevControlador extends PrettyControlador<Siprev> implements Serializable, CRUD {

    private Entidade entidade;
    @EJB
    private SiprevFacade siprevFacade;
    private List<VinculoFP> vinculoFPs;
    @EJB
    private VinculoFPFacade vinculoFPFacade;
    @EJB
    private RegistroDeObitoFacade registroObitoFacade;
    @EJB
    private PessoaFisicaFacade pessoaFisicaFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private EnderecoFacade enderecoFacade;
    @EJB
    private DependenteFacade dependenteFacade;
    @EJB
    private DependenteVinculoFPFacade dependenteVinculoFPFacade;
    @EJB
    private TipoDependenteFacade tipoDependenteFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private UnidadeOrganizacionalFacade unidadeOrganizacionalFacade;
    @EJB
    private ReferenciaFPFacade referenciaFPFacade;
    @EJB
    private EnquadramentoFGFacade enquadramentoFGFacade;
    @EJB
    private LotacaoFuncionalFacade lotacaoFuncionalFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private BeneficioServidorFacade beneficioServidorFacade;
    @EJB
    private PensionistaFacade pensionistaFacade;
    @EJB
    private CedenciaContratoFPFacade cedenciaContratoFPFacade;
    @EJB
    private EnquadramentoFuncionalFacade enquadramentoFuncionalFacade;
    @EJB
    private EnquadramentoPCSFacade enquadramentoPCSFacade;
    @EJB
    private PlanoCargosSalariosFacade planoCargosSalariosFacade;
    @EJB
    private CargoFacade cargoFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    private ConverterAutoComplete converterPessoa;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private EventoFPFacade eventoFPFacade;
    @EJB
    private FichaFinanceiraFPFacade fichaFinanceiraFPFacade;
    @EJB
    private ArquivoFacade arquivoFacade;
    private Entidade entidadeRPPS;
    private File zipFile;
    private ConverterExercicio converterExercicio;
    private DefaultStreamedContent fileDownload;
    private Map<String, File> files;
    private File file;
    private ConverterAutoComplete converterPessoaContratoFP;
    private ArquivoSiprev[] arquivoSiprev;
    private List<ArquivoSiprev> listaArquivoSiprev;
    private AssistenteDetentorArquivoComposicao assistenteDetentorArquivoComposicao;
    private Siprev siprevExistente;
    private PessoaJuridica pessoaJuridicaUnidadeGestora;
    private DecimalFormat format = new DecimalFormat("#0.00");


    public SiprevControlador() {
        super(Siprev.class);
    }

    public Converter getConverterPessoaContratoFP() {
        if (converterPessoaContratoFP == null) {
            converterPessoaContratoFP = new ConverterAutoComplete(Pessoa.class, pessoaFisicaFacade);
        }
        return converterPessoaContratoFP;
    }


    @Override
    public AbstractFacade getFacede() {
        return siprevFacade;
    }

    public ArquivoSiprev[] getArquivoSiprev() {
        return arquivoSiprev;
    }

    public void setArquivoSiprev(ArquivoSiprev[] arquivoSiprev) {
        this.arquivoSiprev = arquivoSiprev;
    }

    public ConverterExercicio getConverterExercicio() {
        if (converterExercicio == null) {
            converterExercicio = new ConverterExercicio(exercicioFacade);
        }
        return converterExercicio;
    }

    public List<ArquivoSiprev> getListaArquivoSiprev() {
        List<ArquivoSiprev> retorno = new ArrayList<>();
        for (ArquivoSiprev siprev : ArquivoSiprev.values()) {
            retorno.add(siprev);
        }
        return retorno;
    }

    public void setListaArquivoSiprev(List<ArquivoSiprev> listaArquivoSiprev) {
        this.listaArquivoSiprev = listaArquivoSiprev;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/arquivo/siprev/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public void salvar() {
        if (validaRegrasParaSalvar()) {
            try {
                selecionado.setDataGeracao(new Date());
                selecionado = siprevFacade.salvarSiprev(selecionado);
                FacesUtil.addOperacaoRealizada("Registro salvo com sucesso.");
            } catch (ValidacaoException ex) {
                FacesUtil.printAllFacesMessages(ex.getMensagens());
                return;
            } catch (Exception e) {
                descobrirETratarException(e);

            }
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId());
        }
    }

    protected boolean validaRegrasParaSalvar() {
        if (!Util.validaCampos(selecionado)) {
            return false;
        }
        return true;
    }

    @Override
    @URLAction(mappingId = "novo-siprev", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        vinculoFPs = new ArrayList<>();
        files = new HashMap<>();
        assistenteDetentorArquivoComposicao = new AssistenteDetentorArquivoComposicao(selecionado, new Date());
    }

    @URLAction(mappingId = "ver-siprev", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
        assistenteDetentorArquivoComposicao = new AssistenteDetentorArquivoComposicao(selecionado, new Date());
    }

    public Entidade getEntidade() {
        return entidade;
    }

    public void setEntidade(Entidade entidade) {
        this.entidade = entidade;
    }


    public List<SelectItem> getTipoRepresentatividade() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, " "));
        for (TipoRepresentatividadeSiprev tipo : TipoRepresentatividadeSiprev.values()) {
            retorno.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return retorno;
    }


    public void gerarArquivoSiprev() {
        try {
            files = new HashMap<>();
            if (arquivoSiprev.length > 0) {
                validarGeracao();
                siprevExistente = siprevFacade.recuperarSiprevGeradoExercicioAndAno(selecionado.getExercicio(), selecionado.getMes());
                if (siprevExistente == null) {
                    criarArquivosSiprev();
                } else {
                    FacesUtil.atualizarComponente("idConfirmarGeracao");
                    FacesUtil.executaJavaScript("confirmarGeracao.show()");
                }
            } else {
                FacesUtil.addOperacaoNaoPermitida("Não existe arquivo do SIPREV selecionado para ser gerado.");
            }
        } catch (ValidacaoException ve) {
            logger.error("Erro de validação na geração do siprev: ", ve);
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            logger.error("Erro ao remover arquivo: ", ex);
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }

    public void imprimirLogErros() {
        Util.geraPDF("Problemas na Exportação", gerarLogErros(), FacesContext.getCurrentInstance());
    }

    private String gerarLogErros() {

        String caminhoDaImagem = FacesUtil.geraUrlImagemDir() + "img/Brasao_de_Rio_Branco.gif";

        StringBuilder conteudoMensagem = new StringBuilder();
        conteudoMensagem.append("<?xml version='1.0' encoding='iso-8859-1'?>");
        conteudoMensagem.append(" <!DOCTYPE HTML PUBLIC \"HTML 4.01 Transitional//PT\" \"http://www.w3.org/TR/html4/loose.dtd\">");
        conteudoMensagem.append(" <html>");
        conteudoMensagem.append("<html> ");
        conteudoMensagem.append("<div style='text-align:center'> ");
        conteudoMensagem.append("<img src=\"").append(caminhoDaImagem).append("\" alt=\"PREFEITURA DO MUNIC&Iacute;PIO DE RIO BRANCO\" /> </br> ");
        conteudoMensagem.append("<b> PREFEITURA DE RIO BRANCO </br> ");
        conteudoMensagem.append("</br>PROBLEMAS NA EXPORTAÇÃO DO SIPREV</b> ");
        conteudoMensagem.append("</div> ");
        conteudoMensagem.append("</br>");

        conteudoMensagem.append("<div style='text-align:left'> ");
        for (SiprevErro erro : selecionado.getErros()) {
            conteudoMensagem.append(erro.getMensagem()).append("</br>");
        }
        conteudoMensagem.append("</div>");
        conteudoMensagem.append("</html>");
        return conteudoMensagem.toString();
    }

    public void removerArquivoExistenteAndCriarNovo() {
        try {
            siprevFacade.remover(siprevExistente);
            criarArquivosSiprev();
        } catch (ValidacaoException ve) {
            logger.error("Erro de validação na geração do siprev: ", ve);
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            logger.error("Erro ao remover arquivo: ", ex);
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }


    public void criarArquivosSiprev() throws ParserConfigurationException, IOException, TransformerException {
        Date data = getDataOperacao().toDate();
        for (ArquivoSiprev arquivo : arquivoSiprev) {
            if (arquivo.equals(ArquivoSiprev.GERAR_ARQUIVO_SERVIDORES)) {
                gerarArquivoServidores(data);
            }
            if (arquivo.equals(ArquivoSiprev.GERAR_ARQUIVO_ALIQUOTA)) {
                gerarArquivoAliquota();
            }
            if (arquivo.equals(ArquivoSiprev.GERAR_ARQUIVO_BENEFICIO_SERVIDOR)) {
                gerarArquivoBeneficioServidor(data);
            }
            if (arquivo.equals(ArquivoSiprev.GERAR_ARQUIVO_CARGOS)) {
                gerarArquivoCargos(data);
            }
            if (arquivo.equals(ArquivoSiprev.GERAR_ARQUIVO_CARREIRAS)) {
                gerarArquivoCarreiras();
            }
            if (arquivo.equals(ArquivoSiprev.GERAR_ARQUIVO_DEPENDENTES)) {
                gerarArquivoDependentes(data);
            }
            if (arquivo.equals(ArquivoSiprev.GERAR_ARQUIVO_ORGAO)) {
                gerarArquivoOrgaos();
            }
            if (arquivo.equals(ArquivoSiprev.GERAR_ARQUIVO_FUNCAO_GRATIFICADA)) {
                gerarArquivoFuncaoGratificada(data);
            }
            if (arquivo.equals(ArquivoSiprev.GERAR_ARQUIVO_TEMPO_CONTRIBUICAO_RGPS)) {
                gerarArquivoTempoContribuicaoRGPS(data);
            }
            if (arquivo.equals(ArquivoSiprev.GERAR_ARQUIVO_PENSIONISTA)) {
                gerarArquivoPensionista(data);
            }
            if (arquivo.equals(ArquivoSiprev.GERAR_ARQUIVO_BENEFICIO_PENSIONISTA)) {
                gerarArquivoBeneficioPensionista(data);
            }
            if (arquivo.equals(ArquivoSiprev.GERAR_ARQUIVO_HISTORICO_FINANCEIRO)) {
                gerarArquivoHistoricoFinanceiro(data);
            }
            if (arquivo.equals(ArquivoSiprev.GERAR_ARQUIVO_HISTORICO_FUNCIONAL)) {
                if (selecionado.getMes().getNumeroMes() <= 3 && selecionado.getExercicio().getAno() <= 2010) {
                    gerarArquivoHistoricoFuncionalRGPS(data);
                } else {
                    gerarArquivoHistoricoFuncionalRPPS(data);
                }
            }
        }
        criarArquivo();
        salvar();
    }

    private void criarArquivo() throws IOException {
        if (fileDownload != null) {
            Arquivo arquivo = new Arquivo();
            arquivo.setMimeType(arquivoFacade.getMimeType(fileDownload.getName()));
            arquivo.setNome(fileDownload.getName());
            arquivo.setTamanho(Long.valueOf(fileDownload.getStream().available()));
            arquivo.setDescricao(fileDownload.getName());
            arquivo.setInputStream(fileDownload.getStream());
            arquivo = criarPartesDoArquivo(arquivo);
            adicionarArquivo(arquivo);
        }
    }

    private Arquivo criarPartesDoArquivo(Arquivo arquivo) throws IOException {
        int bytesLidos = 0;

        while (true) {
            int restante = arquivo.getInputStream().available();
            byte[] buffer = new byte[restante > ArquivoParte.TAMANHO_MAXIMO ? ArquivoParte.TAMANHO_MAXIMO : restante];
            bytesLidos = arquivo.getInputStream().read(buffer);
            if (bytesLidos <= 0) {
                break;
            }
            ArquivoParte arquivoParte = new ArquivoParte();
            arquivoParte.setDados(buffer);
            arquivoParte.setArquivo(arquivo);
            arquivo.getPartes().add(arquivoParte);
        }
        return arquivo;
    }

    private void adicionarArquivo(Arquivo arquivo) throws IOException {
        ArquivoComposicao arquivoComposicao = criarArquivoComposicao(arquivo);
        selecionado.getDetentorArquivoComposicao().setArquivosComposicao(new ArrayList());
        selecionado.getDetentorArquivoComposicao().getArquivosComposicao().add(arquivoComposicao);
    }

    private ArquivoComposicao criarArquivoComposicao(Arquivo arquivo) {
        ArquivoComposicao arquivoComposicao = new ArquivoComposicao();
        arquivoComposicao.setArquivo(arquivo);
        arquivoComposicao.setFile(file);
        arquivoComposicao.setDataUpload(new Date());
        arquivoComposicao.setDetentorArquivoComposicao(selecionado.getDetentorArquivoComposicao());

        return arquivoComposicao;
    }


    public void gerarArquivoFuncaoGratificada(Date dataOperacao) throws ParserConfigurationException, IOException, TransformerException {
        Document doc = getDocument("siprevFuncaoGratificada");
        Element namaspace = criarCabecalhoXML(doc);

        List<ContratoFP> contratos = contratoFPFacade.recuperarFGComContratoFPVigente(selecionado.getExercicio().getAno(), selecionado.getMes().getNumeroMes());
        DateTime dt = getDataOperacao();
        for (ContratoFP c : contratos) {
            LotacaoFuncional lotacaoFuncional = lotacaoFuncionalFacade.recuperaLotacaoFuncionalVigente(c, dt.toDate());
            if (lotacaoFuncional == null || lotacaoFuncional.getUnidadeOrganizacional() == null) {
                continue;
            }
            HierarquiaOrganizacional hierarquiaOrganizacional = hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalPorUnidade(lotacaoFuncional.getUnidadeOrganizacional(), TipoHierarquiaOrganizacional.ADMINISTRATIVA);
            if (hierarquiaOrganizacional == null) {
                continue;
            }
            HierarquiaOrganizacional ho = hierarquiaOrganizacionalFacade.getHierarquiaAdministrativaPorOrgaoAndTipoAdministrativa(hierarquiaOrganizacional.getCodigoDo2NivelDeHierarquia());

            EnquadramentoFG enquadramentoFG = enquadramentoFGFacade.recuperaEnquadramentoFGVigente(c, getDataOperacao().toDate());
            if (enquadramentoFG.getId() == null) {
                continue;
            }
            BigDecimal valorEnquadramentoPCS = enquadramentoPCSFacade.recuperaValorDecimal(enquadramentoFG.getCategoriaPCS(), enquadramentoFG.getProgressaoPCS(), dt.toDate());

            Element funcaoGratificada = criarTag(doc, "funcaoGratificada", namaspace);
            preencheTagFuncaoGratificada(doc, funcaoGratificada, c, enquadramentoFG, ho.getSubordinada(), lotacaoFuncional, valorEnquadramentoPCS);

            PessoaFisica pessoa = pessoaFisicaFacade.recuperar(c.getMatriculaFP().getPessoa().getId());

            Element servidor = criarTag(doc, "servidor", funcaoGratificada);
            preencheTagServidor(doc, servidor, pessoa);
//
//            Element cargo = criarTag(doc, "cargo", servidor);
//            preencherTagCargoBeneficioServidor(doc, cargo, new BigDecimal(c.getCargo().getId()));
//
//            Element carreira = criarTag(doc, "carreira", cargo);
//            preencherTagCarreiraBeneficioServidor(doc, carreira, new BigDecimal(c.getId()), dataOperacao);
//
//            Element orgao = criarTag(doc, "orgao", carreira);
//            preencheTagOrgao(doc, orgao, lotacaoFuncional);

        }

        Transformer transformer = encodingStandaloneXML();
        criarXML(doc, transformer, "siprevFuncaoGratificada.xml");
        addToZip();


    }

    public DateTime getDataOperacao() {
        DateTime dt = new DateTime();
        dt = dt.withYear(selecionado.getExercicio().getAno());
        dt = dt.withMonthOfYear(selecionado.getMes().getNumeroMes());
        dt = dt.dayOfMonth().withMinimumValue();
        return dt;
    }

    public void gerarArquivoAliquota() throws ParserConfigurationException, IOException, TransformerException {
        validarGeracao();
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        file = File.createTempFile("siprevAliquota", "xml");
        Document doc = docBuilder.newDocument();
        doc.setXmlStandalone(true);

        Element namaspace = doc.createElementNS("http://www.dataprev.gov.br/siprev", "ns2:siprev");
        doc.appendChild(namaspace);

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");

        Element ente = doc.createElement("ente");
        namaspace.appendChild(ente);
        ente.setAttributeNode(criarAtributo(doc, "siafi", selecionado.getCodigo()));
        ente.setAttributeNode(criarAtributo(doc, "cnpj", pessoaJuridicaUnidadeGestora.getCnpj().replace("/", "").replace(".", "").replace("-", "")));

        Element aliquotaEnte = criarTag(doc, "aliquotaEnte", namaspace);
        preencheTagGeralAliquota(doc, aliquotaEnte);

        Element atoLegal = criarTag(doc, "atoLegal", aliquotaEnte);
        preencherTagGeralAtoLegal(doc, atoLegal);

        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(file);
        transformer.transform(source, result);

        files.put("siprevAliquota.xml", file);
        addToZip();
    }

    public void gerarArquivoTempoContribuicaoRGPS(Date data) throws ParserConfigurationException, IOException, TransformerException {

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        file = File.createTempFile("siprevTempoContribuicaoRGPS", "xml");
        Document doc = docBuilder.newDocument();
        doc.setXmlStandalone(true);
        Element namaspace = doc.createElementNS("http://www.dataprev.gov.br/siprev", "ns2:siprev");
        doc.appendChild(namaspace);

        Element ente = doc.createElement("ente");
        namaspace.appendChild(ente);
        ente.setAttributeNode(criarAtributo(doc, "siafi", selecionado.getCodigo()));
        ente.setAttributeNode(criarAtributo(doc, "cnpj", pessoaJuridicaUnidadeGestora.getCnpj().replace("/", "").replace(".", "").replace("-", "")));

        for (ContratoFP contratoFP : contratoFPFacade.buscarContratoFPVigente()) {
            AverbacaoTempoServico averbacaoTempoServico = siprevFacade.getAverbacaoTempoServicoFacade().buscarAverbacaoTempoServicoValidosSIPREV(contratoFP);
            if (averbacaoTempoServico == null) {
                continue;
            }
            Element tempoContribuicaoRGPS = criarTag(doc, "tempoContribuicaoRGPS", namaspace);
            preencherTagTempoContribuicaoRGPS(doc, tempoContribuicaoRGPS, averbacaoTempoServico, contratoFP.getMatriculaFP().getPessoa());

        }

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
        transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");


        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(file);
        transformer.transform(source, result);

        files.put("siprevTempoContribuicaoRGPS.xml", file);
        addToZip();
    }

    private void preencheTagGeral(Document doc, Element servidores) {
        servidores.setAttributeNode(criarAtributo(doc, "operacao", OperacaoRegistro.INCLUSAO.getCodigo()));
//        servidores.setAttributeNode(criarAtributo(doc, "erroImportacao", ""));
    }

    private void preencheTagEnte(Document doc, Element ente) {
        ente.setAttributeNode(criarAtributo(doc, "siafi", selecionado.getCodigo()));
        ente.setAttributeNode(criarAtributo(doc, "cnpj", pessoaJuridicaUnidadeGestora.getCnpj().replace("/", "").replace(".", "").replace("-", "").replace("-", "")));
//        servidores.setAttributeNode(criarAtributo(doc, "erroImportacao", ""));
    }

    private void preencheTagSomenteOperacao(Document doc, Element servidores) {
        servidores.setAttributeNode(criarAtributo(doc, "operacao", OperacaoRegistro.INCLUSAO.getCodigo()));
    }

    private void preencheTagVinculosFuncionaisRpps(Document doc, Element vinculo, ContratoFP contratoFP, Date dt) {
        SituacaoContratoFP situacaoContratoFP = contratoFPFacade.recuperaSituacaoVigenteContratoFP(contratoFP, dt);
        vinculo.setAttributeNode(criarAtributo(doc, "operacao", OperacaoRegistro.INCLUSAO.getCodigo()));
        vinculo.setAttributeNode(criarAtributo(doc, "regime", "1"));
        vinculo.setAttributeNode(criarAtributo(doc, "tipoServidor", "1"));
        vinculo.setAttributeNode(criarAtributo(doc, "tipoVinculo", "1"));
        vinculo.setAttributeNode(criarAtributo(doc, "dataExercicioCargo", DataUtil.getDataFormatada(contratoFP.getInicioVigencia(), "yyyy-MM-dd")));
        vinculo.setAttributeNode(criarAtributo(doc, "dataIngressoCarreira", DataUtil.getDataFormatada(contratoFP.getInicioVigencia(), "yyyy-MM-dd")));
        vinculo.setAttributeNode(criarAtributo(doc, "dataIngressoOrgao", DataUtil.getDataFormatada(contratoFP.getInicioVigencia(), "yyyy-MM-dd")));
        vinculo.setAttributeNode(criarAtributo(doc, "situacaoFuncional", recuperaSituacaoFuncional(contratoFP, dt, situacaoContratoFP)));
        vinculo.setAttributeNode(criarAtributo(doc, "matricula", contratoFP.getMatriculaFP().getMatricula() + "/" + contratoFP.getNumero()));

    }

    private void preencheTagVinculosFuncionaisRgps(Document doc, Element vinculo, ContratoFP contratoFP, Date dt) {
        vinculo.setAttributeNode(criarAtributo(doc, "dataInicioFuncao", DataUtil.getDataFormatada(contratoFP.getInicioVigencia(), "yyyy-MM-dd")));
        vinculo.setAttributeNode(criarAtributo(doc, "operacao", "I"));
        vinculo.setAttributeNode(criarAtributo(doc, "matricula", contratoFP.getMatriculaFP().getMatricula() + "/" + contratoFP.getNumero()));
        vinculo.setAttributeNode(criarAtributo(doc, "regime", "2"));
        vinculo.setAttributeNode(criarAtributo(doc, "tipoVinculo", "1"));
        if (contratoFP.getCargo() != null) {
            vinculo.setAttributeNode(criarAtributo(doc, "descricaoFuncao", beneficioServidorFacade.recuperaDescricaoDoCargo(new BigDecimal(contratoFP.getCargo().getId())).trim()));
        }

    }

    private void preencheTagGeralBeneficioPensionista(Document doc, Element servidores, Pensionista pensionista, ItemFichaFinanceiraFP item) {
        DecimalFormat format = new DecimalFormat("#0.00");
        servidores.setAttributeNode(criarAtributo(doc, "operacao", OperacaoRegistro.INCLUSAO.getCodigo()));
//        servidores.setAttributeNode(criarAtributo(doc, "dataInicioBeneficio", DataUtil.getDataFormatada(pensionista.getInicioVigencia(), "yyyy-MM-dd")));
        servidores.setAttributeNode(criarAtributo(doc, "vlAtualBeneficio", format.format(item.getValor()).replace(",", ".")));
        servidores.setAttributeNode(criarAtributo(doc, "tipoBeneficio", "12")); // layout só tem a opção de benefico por morte
        servidores.setAttributeNode(criarAtributo(doc, "dataUltimaAtualizacao", DataUtil.getDataFormatada(pensionista.getInicioVigencia(), "yyyy-MM-dd")));
    }

    private void preencheTagBeneficioServidor(Document doc, Element servidores, BeneficioServidorSiprev beneficio) {
        DecimalFormat format = new DecimalFormat("#0.00");
        Date dtInicioBeneficio = beneficioServidorFacade.recuperaDataDeInicioDoBeneficio(beneficio.getDependenteVinculoFPID());
        Date dtAtualizacaoBeneficio = beneficioServidorFacade.recuperaDataAtualizacaoDoBeneficio(beneficio.getDependenteVinculoFPID());
        servidores.setAttributeNode(criarAtributo(doc, "operacao", OperacaoRegistro.INCLUSAO.getCodigo()));
        servidores.setAttributeNode(criarAtributo(doc, "dtInicioBeneficio", DataUtil.getDataFormatada(dtInicioBeneficio, "yyyy-MM-dd")));
        if (beneficio.getDtFimBeneficio() != null) {
            servidores.setAttributeNode(criarAtributo(doc, "dtFimBeneficio", DataUtil.getDataFormatada(beneficio.getDtFimBeneficio(), "yyyy-MM-dd")));
        }
        servidores.setAttributeNode(criarAtributo(doc, "dtUltimaAtualizacao", DataUtil.getDataFormatada(dtAtualizacaoBeneficio, "yyyy-MM-dd")));
        servidores.setAttributeNode(criarAtributo(doc, "tipoBeneficio", "14")); // Segundo o Victor Hugo, no municipio paga somente salário fámilia
        servidores.setAttributeNode(criarAtributo(doc, "vlAtualBeneficio", format.format(beneficio.getVlAtualBeneficio()).replace(",", ".")));
    }


    private void preencherTagCargoBeneficioServidor(Document doc, Element cargo, BigDecimal idCargo) {
        cargo.setAttributeNode(criarAtributo(doc, "nome", beneficioServidorFacade.recuperaDescricaoDoCargo(idCargo).trim()));
    }

    private void preencheTagQuota(Document doc, Element quota, Pensionista pensionista) {
        quota.setAttributeNode(criarAtributo(doc, "dataInicioPensao", DataUtil.getDataFormatada(pensionista.getInicioVigencia(), "yyyy-MM-dd")));
        quota.setAttributeNode(criarAtributo(doc, "matricula", pensionista.getMatriculaFP().getMatricula() + "/" + pensionista.getNumero()));
    }

    private void preencherTagVinculoFuncional(Document doc, Element vinculoFuncional, ContratoFP contratoFP, Date dt) {
        SituacaoContratoFP situacaoContratoFP = contratoFPFacade.recuperaSituacaoVigenteContratoFP(contratoFP, dt);
        vinculoFuncional.setAttributeNode(criarAtributo(doc, "dataExercicioCargo", DataUtil.getDataFormatada(contratoFP.getInicioVigencia(), "yyyy-MM-dd")));
        vinculoFuncional.setAttributeNode(criarAtributo(doc, "dataIngressoCarreira", DataUtil.getDataFormatada(contratoFP.getInicioVigencia(), "yyyy-MM-dd")));
        vinculoFuncional.setAttributeNode(criarAtributo(doc, "dataIngressoOrgao", DataUtil.getDataFormatada(contratoFP.getInicioVigencia(), "yyyy-MM-dd")));
        vinculoFuncional.setAttributeNode(criarAtributo(doc, "matricula", contratoFP.getMatriculaFP().getMatricula() + "/" + contratoFP.getNumero()));
        vinculoFuncional.setAttributeNode(criarAtributo(doc, "tipoServidor", "1")); // de acordo com o Alysson são todos "civil"
        vinculoFuncional.setAttributeNode(criarAtributo(doc, "tipoVinculo", "1")); // de acordo com o Alysson são todos "SERVIDOR(A) DE CARGO EFETIVO"
        String situacao = recuperaSituacaoFuncional(contratoFP, dt, situacaoContratoFP);
        if (!"".equals(situacao)) {
            vinculoFuncional.setAttributeNode(criarAtributo(doc, "situacaoFuncional", situacao));
        } else {
            selecionado.getErros().add(new SiprevErro(selecionado, "A situação funcional do servidor " + contratoFP + " não foi encontrada na data " + DataUtil.getDataFormatada(dt)));
        }
        if (selecionado.getMes().getNumeroMes() <= 3 && selecionado.getExercicio().getAno() <= 2010) {
            vinculoFuncional.setAttributeNode(criarAtributo(doc, "regime", "2"));
        } else {
            vinculoFuncional.setAttributeNode(criarAtributo(doc, "regime", "1"));
        }
    }

    private void preencheTagOrgao(Document doc, Element orgao, LotacaoFuncional lotacao) {
        orgao.setAttributeNode(criarAtributo(doc, "nome", lotacao != null && lotacao.getUnidadeOrganizacional() != null ? StringUtil.substituirTravessaoPorHifen(lotacao.getUnidadeOrganizacional().getDescricao().trim()).replace("'", "&apos;") : ""));
        orgao.setAttributeNode(criarAtributo(doc, "poder", "1")); //segundo o alysson sempre será poder 1 - Executivo
    }


    private void preencheTagDadosHistoricoFinanceiro(Document doc, Element dadosHistoricoFinanceiro, int mes, int ano, ContratoFPeItemFichaFinanceira contratoEItemFicha, Date data) {
        DecimalFormat format = new DecimalFormat("#0.00");
        BigDecimal valor = BigDecimal.ZERO;
        EnquadramentoFuncional enquadramentoFuncional = enquadramentoFuncionalFacade.recuperaEnquadramentoFuncionalVigenteSiprev(contratoEItemFicha.getContratoFP(), data);
        EnquadramentoPCS enquadramentoPCS = null;
        if (enquadramentoFuncional != null) {
            enquadramentoPCS = enquadramentoPCSFacade.recuperaValor(enquadramentoFuncional.getCategoriaPCS(), enquadramentoFuncional.getProgressaoPCS(), data);
        } else {
            enquadramentoPCS = new EnquadramentoPCS(null, null, null, null, BigDecimal.ZERO);
        }

        List<ItemFichaFinanceiraFP> itensFichaFinanceira = fichaFinanceiraFPFacade.recuperarItensDaFichaFinanceira(contratoEItemFicha.getItemFichaFinanceiraFP().getFichaFinanceiraFP());

        for (ItemFichaFinanceiraFP item : itensFichaFinanceira) {
            if (item.getTipoEventoFP() == TipoEventoFP.VANTAGEM) {
                valor = valor.add(item.getValor());
            }
        }
        BigDecimal valorBaseDeCalculo = contratoEItemFicha.getItemFichaFinanceiraFP().getValorBaseDeCalculo();
        dadosHistoricoFinanceiro.setAttributeNode(criarAtributo(doc, "remuneracaoBruta", format.format(valor).replace(",", ".")));
        dadosHistoricoFinanceiro.setAttributeNode(criarAtributo(doc, "anoContribuicao", ano + ""));
        dadosHistoricoFinanceiro.setAttributeNode(criarAtributo(doc, "mesContribuicao", mes + ""));
        dadosHistoricoFinanceiro.setAttributeNode(criarAtributo(doc, "baseCalculoSegurado", format.format(valorBaseDeCalculo).replace(",", ".")));
        dadosHistoricoFinanceiro.setAttributeNode(criarAtributo(doc, "baseCalculoPatronal", format.format(valorBaseDeCalculo).replace(",", ".")));
        dadosHistoricoFinanceiro.setAttributeNode(criarAtributo(doc, "remuneracaoContrib", format.format(valorBaseDeCalculo).replace(",", ".")));
        dadosHistoricoFinanceiro.setAttributeNode(criarAtributo(doc, "contribSegurado", format.format(contratoEItemFicha.getItemFichaFinanceiraFP().getValor()).replace(",", ".")));
        dadosHistoricoFinanceiro.setAttributeNode(criarAtributo(doc, "remuneracaoCargo", format.format(enquadramentoPCS.getVencimentoBase()).replace(",", ".")));
        dadosHistoricoFinanceiro.setAttributeNode(criarAtributo(doc, "contribPatronal", format.format(valorBaseDeCalculo.multiply(referenciaFPFacade.valorRefenciaFPVigente(23, data)).divide(new BigDecimal(100)).setScale(2, RoundingMode.UP)).replace(",", ".")));
        dadosHistoricoFinanceiro.setAttributeNode(criarAtributo(doc, "diferencaContrib", "0.00"));   // de acordo com o ticket, fixo 0.00
        dadosHistoricoFinanceiro.setAttributeNode(criarAtributo(doc, "compoeMediaBeneficio", "1"));   // de acordo com o ticket, fixo 1
        dadosHistoricoFinanceiro.setAttributeNode(criarAtributo(doc, "folhaPagamento", contratoEItemFicha.getItemFichaFinanceiraFP().getFichaFinanceiraFP().getFolhaDePagamento().getVersao() + ""));
        dadosHistoricoFinanceiro.setAttributeNode(criarAtributo(doc, "decimoTerceiroSalario", contratoEItemFicha.getItemFichaFinanceiraFP().getFichaFinanceiraFP().getFolhaDePagamento().getTipoFolhaDePagamento() == TipoFolhaDePagamento.SALARIO_13 ? "1" : "0"));

    }

    private void preencherTagMovimentacoesFuncionaisRpps(Document doc, Element movimentacoes, ContratoFP contratoFP, Date dataOperacao, SituacaoContratoFP situacaoContratoFP) {
        movimentacoes.setAttributeNode(criarAtributo(doc, "situacaoFuncional", recuperaSituacaoFuncional(contratoFP, dataOperacao, situacaoContratoFP)));
        movimentacoes.setAttributeNode(criarAtributo(doc, "dataMovimentacao", DataUtil.getDataFormatada(situacaoContratoFP.getInicioVigencia(), "yyyy-MM-dd")));
        movimentacoes.setAttributeNode(criarAtributo(doc, "tipoMagisterio", contratoFP.getCargo().getTipoMagisterio() != null ? contratoFP.getCargo().getTipoMagisterio().getCodigo() : "1"));
        movimentacoes.setAttributeNode(criarAtributo(doc, "operacao", "I"));
    }

    private void preencherTagMovimentacoesFuncionaisRGPS(Document doc, Element movimentacoes, ContratoFP contratoFP, Date dataOperacao, SituacaoContratoFP situacaoContratoFP) {
        movimentacoes.setAttributeNode(criarAtributo(doc, "dataMovimentacao", DataUtil.getDataFormatada(situacaoContratoFP.getInicioVigencia(), "yyyy-MM-dd")));
        movimentacoes.setAttributeNode(criarAtributo(doc, "operacao", "I"));
        if (contratoFP.getCargo() != null) {
            movimentacoes.setAttributeNode(criarAtributo(doc, "descricaoFuncao", beneficioServidorFacade.recuperaDescricaoDoCargo(new BigDecimal(contratoFP.getCargo().getId())).trim()));
        }

    }

    private String recuperaSituacaoFuncional(ContratoFP contrato, Date dataOperacao, SituacaoContratoFP situacaoContratoFP) {
        if (situacaoContratoFP != null) {
            if (situacaoContratoFP.getSituacaoFuncional().getCodigo() == 1 || situacaoContratoFP.getSituacaoFuncional().getCodigo() == 2) {
                return "1";
            }
            if (situacaoContratoFP.getSituacaoFuncional().getCodigo() == 6) {
                return "2";
            }
            if (situacaoContratoFP.getSituacaoFuncional().getCodigo() == 3) {
                return "3";
            }

            if (situacaoContratoFP.getSituacaoFuncional().getCodigo() == 10) {
                return "11";
            }
            if (situacaoContratoFP.getSituacaoFuncional().getCodigo() == 7) {
                return "12";
            }
            if (situacaoContratoFP.getSituacaoFuncional().getCodigo() == 4) {
                CedenciaContratoFP cedencia = cedenciaContratoFPFacade.recuperaCedenciaVigentePorContratoFP(contrato, dataOperacao);
                if (cedencia != null && cedencia.getTipoContratoCedenciaFP() == TipoCedenciaContratoFP.COM_ONUS) {
                    return "5";
                }
                return "6";
            }
            if (situacaoContratoFP.getSituacaoFuncional().getCodigo() == 5) {
                CedenciaContratoFP cedencia = cedenciaContratoFPFacade.recuperaCedenciaVigentePorContratoFP(contrato, dataOperacao);
                if (cedencia != null && cedencia.getTipoContratoCedenciaFP() == TipoCedenciaContratoFP.COM_ONUS) {
                    return "7";
                }
                return "8";
            }
        }
        return "";
    }

    private void preencherTagCarreiraBeneficioServidor(Document doc, Element cargo, BigDecimal idCcntrato, Date dataOperacao) {
        cargo.setAttributeNode(criarAtributo(doc, "nome", beneficioServidorFacade.recuperaCarreiraBeneficioServidor(idCcntrato, dataOperacao).trim()));
    }

    private void preencherTagTempoContribuicaoRGPS(Document doc, Element tempoContribuicaoRGPS, AverbacaoTempoServico averbacao, PessoaFisica pessoa) {

        tempoContribuicaoRGPS.setAttributeNode(criarAtributo(doc, "cargo", (averbacao.getCargo() != null ? Util.cortarString(averbacao.getCargo(), 40) : "")));
        tempoContribuicaoRGPS.setAttributeNode(criarAtributo(doc, "operacao", OperacaoRegistro.INCLUSAO.getCodigo()));
        tempoContribuicaoRGPS.setAttributeNode(criarAtributo(doc, "numCertidao", averbacao.getNumeroDocumento() != null ? averbacao.getNumeroDocumento().toString() : "0"));
        tempoContribuicaoRGPS.setAttributeNode(criarAtributo(doc, "dtEmissao", DataUtil.getDataFormatada(averbacao.getDataEmissaoDocumento() != null ?
            averbacao.getDataEmissaoDocumento() : averbacao.getInicioVigencia(), "yyyy-MM-dd")));
        tempoContribuicaoRGPS.setAttributeNode(criarAtributo(doc, "dtInicial", DataUtil.getDataFormatada(averbacao.getInicioVigencia(), "yyyy-MM-dd")));
        tempoContribuicaoRGPS.setAttributeNode(criarAtributo(doc, "dtFinal", DataUtil.getDataFormatada(averbacao.getFinalVigencia(), "yyyy-MM-dd")));
        tempoContribuicaoRGPS.setAttributeNode(criarAtributo(doc, "tempoLiqAnoMesDia", DataUtil.getDataFormatada(averbacao.getFinalVigencia(), "yy/MM/dd")));
        if (DataUtil.getAno(averbacao.getInicioVigencia()) < 1900) {
            PessoaFisicaSiprev pessoaFisicaSiprev = pessoaFisicaFacade.buscarPessoaFisicaSiprev(pessoa.getId());
            selecionado.getErros().add(new SiprevErro(selecionado, "Data inválida no inicio de vigência da averbação para a pessoa: " + pessoaFisicaSiprev.getNome()));
        } else {
            tempoContribuicaoRGPS.setAttributeNode(criarAtributo(doc, "numeroDias", DataUtil.diferencaDiasInteira(averbacao.getInicioVigencia(), averbacao.getFinalVigencia()) + ""));
        }


        Element servidor = criarTag(doc, "servidor", tempoContribuicaoRGPS);
        preencherTagServidorCompleto(doc, servidor, pessoa);
    }

    public void gerarArquivoBeneficioServidor(Date dataOperacao) throws ParserConfigurationException, IOException, TransformerException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        file = File.createTempFile("siprevBeneficioServidor", "xml");
        Document doc = docBuilder.newDocument();
        doc.setXmlStandalone(true);
        Element namaspace = doc.createElementNS("http://www.dataprev.gov.br/siprev", "ns2:siprev");
        doc.appendChild(namaspace);

        Element ente = doc.createElement("ente");
        namaspace.appendChild(ente);
        ente.setAttributeNode(criarAtributo(doc, "siafi", selecionado.getCodigo()));
        ente.setAttributeNode(criarAtributo(doc, "cnpj", pessoaJuridicaUnidadeGestora.getCnpj().replace("/", "").replace(".", "").replace("-", "").replace("-", "")));


        List<BeneficioServidorSiprev> beneficioServidorSiprevs = beneficioServidorFacade.recuperarBeneficios(selecionado.getMes().getNumeroMes(), selecionado.getExercicio().getAno(), dataOperacao);

        for (BeneficioServidorSiprev beneficio : beneficioServidorSiprevs) {
            Element beneficioServidor = criarTag(doc, "beneficioServidor", namaspace);
            preencheTagBeneficioServidor(doc, beneficioServidor, beneficio);

            Element cargoBeneficioServidor = criarTag(doc, "cargo", beneficioServidor);
            preencherTagCargoBeneficioServidor(doc, cargoBeneficioServidor, beneficio.getIdCargo());

            Element carreiraBeneficioServidor = criarTag(doc, "carreira", cargoBeneficioServidor);
            preencherTagCarreiraBeneficioServidor(doc, carreiraBeneficioServidor, beneficio.getIdContratoFP(), dataOperacao);


            ContratoFP c = contratoFPFacade.recuperar(beneficio.getIdContratoFP().longValue());
            LotacaoFuncional lotacaoFuncional = lotacaoFuncionalFacade.recuperaLotacaoFuncionalVigente(c, dataOperacao);

            Element orgaoBS = criarTag(doc, "orgao", carreiraBeneficioServidor);
            preencheTagOrgao(doc, orgaoBS, lotacaoFuncional);

            PessoaFisica pessoaFisica = pessoaFisicaFacade.recuperar(beneficio.getIdServidor().longValue());

            Element servidor = criarTag(doc, "servidor", beneficioServidor);
            preencheTagServidor(doc, servidor, pessoaFisica);
        }

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
        transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");


        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(file);
        transformer.transform(source, result);

        files.put("siprevBeneficioServidor.xml", file);
        addToZip();
    }


    public void gerarArquivoCargos(Date dataOperacao) throws ParserConfigurationException, IOException, TransformerException {

        List<CargoSiprev> listCargos = cargoFacade.retornarCargosSiprev(TipoPCS.QUADRO_EFETIVO, dataOperacao);
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        file = File.createTempFile("siprevCargos", "xml");
        Document doc = docBuilder.newDocument();
        doc.setXmlStandalone(true);
        Element namaspace = doc.createElementNS("http://www.dataprev.gov.br/siprev", "ns2:siprev");
        doc.appendChild(namaspace);

        Element ente = doc.createElement("ente");
        namaspace.appendChild(ente);
        ente.setAttributeNode(criarAtributo(doc, "siafi", selecionado.getCodigo()));
        ente.setAttributeNode(criarAtributo(doc, "cnpj", pessoaJuridicaUnidadeGestora.getCnpj().replace("/", "").replace(".", "").replace("-", "").replace("-", "")));


        for (CargoSiprev cargo : listCargos) {
            Element cargos = criarTag(doc, "cargos", namaspace);
            preencheTagGeral(doc, cargos);

            Element dadosCargos = criarTag(doc, "dadosCargo", cargos);
            preencheTagDadosCargos(doc, dadosCargos, cargo);
            cargos.appendChild(dadosCargos);

            /* comentado, descomentar se precisar gerar ato legal para o cargo

            if (cargo.getAtoLegalId() != null) {
                Element dadosAtoLegal = criarTag(doc, "atoLegal", ente);
                preencheTagAtoLegal(doc, dadosAtoLegal, cargo);
                dadosCargos.appendChild(dadosAtoLegal);
            }
            */
            Element carreira = criarTag(doc, "carreira", dadosCargos);
            preencheTagNomeCarreira(doc, carreira, cargo, dataOperacao);
            dadosCargos.appendChild(carreira);

            Element orgao = criarTag(doc, "orgao", carreira);
            preencheTagOrgao(doc, orgao, cargo);
            carreira.appendChild(orgao);

        }

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
        transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");


        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(file);
        transformer.transform(source, result);

        files.put("siprevCargos.xml", file);
        addToZip();

    }

    private void preencheTagCarreiras(Document doc, Element carreira, CargoSiprev cargo) {
        carreira.setAttributeNode(criarAtributo(doc, "nome", cargo.getCodigoCarreira()));
    }

    private void preencheTagOrgao(Document doc, Element orgao, CargoSiprev cargo) {
        if (cargo.getNomeEntidade() == null || cargo.getNomeEntidade().isEmpty()) {
            PlanoCargosSalarios planoCargosSalarios = planoCargosSalariosFacade.recuperar(cargo.getPlanoId().longValue());
            selecionado.getErros().add(new SiprevErro(selecionado, "Não há Entidade vinculada com o plano de cargos e salários: " + planoCargosSalarios.getDescricao() + " do Cargo: " + cargo.getDescricao().trim()));
        }
        orgao.setAttributeNode(criarAtributo(doc, "nome", cargo.getNomeEntidade()));
        orgao.setAttributeNode(criarAtributo(doc, "poder", "1"));
    }

    private void preencheTagDadosCargos(Document doc, Element element, CargoSiprev cargo) {
        if (cargo.getDescricao() != null) {
            element.setAttributeNode(criarAtributo(doc, "nome", cargo.getDescricao().trim()));
        }
        if (cargo.getInicioVigencia() != null) {
            element.setAttributeNode(criarAtributo(doc, "dataCriacao", DataUtil.getDataFormatada(cargo.getInicioVigencia(), "yyyy-MM-dd")));
        }
        if (cargo.getFinalVigencia() != null) {
            element.setAttributeNode(criarAtributo(doc, "dataExtincao", DataUtil.getDataFormatada(cargo.getFinalVigencia(), "yyyy-MM-dd")));
        }
        element.setAttributeNode(criarAtributo(doc, "valorRemuneracaoInicial", cargo.getValorMinimo() != null ? format.format(cargo.getValorMinimo()).replace(",", ".") : "0.0"));
        element.setAttributeNode(criarAtributo(doc, "valorRemuneracaoFinal", cargo.getValorMaximo() != null ? format.format(cargo.getValorMaximo()).replace(",", ".") : "0.0"));
        element.setAttributeNode(criarAtributo(doc, "codigoOrigem", cargo.getCodigoCargo()));
        element.setAttributeNode(criarAtributo(doc, "aposentadoriaEspecial", cargo.getAposentadoriaEspecial().equals(BigDecimal.ONE) ? "1" : "0"));
        element.setAttributeNode(criarAtributo(doc, "cargoAcumulacao", defineTipoCargoAcumulavel(cargo.getTipoCargoAcumulavelSIPREV())));
        element.setAttributeNode(criarAtributo(doc, "contagemEspecial", defineTipoContagemSiprev(cargo.getTipoContagemSIPREV())));
        element.setAttributeNode(criarAtributo(doc, "dedicacaoExclusiva", cargo.getDedicacaoExclusiva().equals(BigDecimal.ONE) ? "1" : "0"));
        element.setAttributeNode(criarAtributo(doc, "tecnicoCientifico", cargo.getTecnicoCientifico().equals(BigDecimal.ONE) ? "1" : "0"));
    }

    private String defineTipoCargoAcumulavel(String tipo) {
        if (tipo.equals("PROFISSIONAL_SAUDE")) {
            return "2";
        }
        if (tipo.equals("PROFESSOR")) {
            return "3";
        }
        if (tipo.equals("OUTROS")) {
            return "9";
        }
        return "1";
    }

    private String defineTipoContagemSiprev(String tipo) {
        if (tipo.equals("PROFESSOR")) {
            return "2";
        }
        if (tipo.equals("MAGISTRADO")) {
            return "3";
        }
        if (tipo.equals("MEMBRO_MINISTERIO_PUBLICO")) {
            return "4";
        }
        if (tipo.equals("MEMBRO_TRIBUNAL_CONTAS")) {
            return "5";
        }
        if (tipo.equals("APOSENTADORIA_ESPECIAL")) {
            return "6";
        }
        if (tipo.equals("OUTROS")) {
            return "7";
        }
        return "1";
    }

    private void preencheTagAtoLegal(Document doc, Element element, CargoSiprev cargo) {
        if (cargo.getAtoAno() != null) {
            element.setAttributeNode(criarAtributo(doc, "ano", cargo.getAtoAno().toString()));
        }
        if (cargo.getAtoDataEmissao() != null) {
            element.setAttributeNode(criarAtributo(doc, "dataPublicacao", DataUtil.getDataFormatada(cargo.getAtoDataEmissao(), "yyyy-MM-dd")));
        }
        if (cargo.getAtoDataPublicacao() != null) {
            element.setAttributeNode(criarAtributo(doc, "dataPublicacao", DataUtil.getDataFormatada(cargo.getAtoDataPublicacao(), "yyyy-MM-dd")));
        }
        if (!cargo.getAtoNumero().equals("")) {
            element.setAttributeNode(criarAtributo(doc, "numero", cargo.getAtoNumero()));
        }
        if (cargo.getAtoEmenta().equals("")) {
            element.setAttributeNode(criarAtributo(doc, "numero", cargo.getAtoEmenta()));
        }
    }

    public void gerarArquivoCarreiras() throws ParserConfigurationException, IOException, TransformerException {

        List<EnquadramentoPCS> enquadramentoPCS = enquadramentoPCSFacade.listaEnquadramentosVigentesTipoQuadroEfetivo(getDataOperacao().toDate());

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        file = File.createTempFile("siprevCarreiras", "xml");
        Document doc = docBuilder.newDocument();
        doc.setXmlStandalone(true);
        Element namaspace = doc.createElementNS("http://www.dataprev.gov.br/siprev", "ns2:siprev");
        doc.appendChild(namaspace);

        Element ente = doc.createElement("ente");
        namaspace.appendChild(ente);
        ente.setAttributeNode(criarAtributo(doc, "siafi", selecionado.getCodigo()));
        ente.setAttributeNode(criarAtributo(doc, "cnpj", pessoaJuridicaUnidadeGestora.getCnpj().replace("/", "").replace(".", "").replace("-", "").replace("-", "")));

        for (EnquadramentoPCS epcs : enquadramentoPCS) {
            Element carreiras = criarTag(doc, "carreiras", namaspace);
            preencheTagGeral(doc, carreiras);

            Element dadosCarreiras = criarTag(doc, "dadosCarreira", carreiras);
            preencheTagDadosCarreiras(doc, dadosCarreiras, epcs);

            Element orgao = criarTag(doc, "orgao", dadosCarreiras);
            preencherTagOrgao(doc, orgao, epcs);
        }


        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
        transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");


        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(file);
        transformer.transform(source, result);

        files.put("siprevCarreiras.xml", file);
        addToZip();

    }

    private void preencherTagOrgao(Document doc, Element orgao, EnquadramentoPCS epcs) {
        if (epcs.getCategoriaPCS() != null && epcs.getCategoriaPCS().getPlanoCargosSalarios() != null) {
            PlanoCargosSalarios pcs = planoCargosSalariosFacade.recuperar(epcs.getCategoriaPCS().getPlanoCargosSalarios().getId());
            if (!pcs.getEntidadesPCS().isEmpty()) {
                orgao.setAttributeNode(criarAtributo(doc, "nome", pcs.getEntidadesPCS().get(0).getEntidade().getNome()));
                orgao.setAttributeNode(criarAtributo(doc, "poder", "1"));
            } else {
                selecionado.getErros().add(new SiprevErro(selecionado, "Não há Entidade vinculada com o plano de cargos e salários: " + pcs.getDescricao() + " da Carreira: " + epcs.getCategoriaPCS().getDescricao() + " " + epcs.getProgressaoPCS().getDescricao()));
            }
        }
    }

    private void preencheTagNomeCarreira(Document doc, Element carreiras, CargoSiprev cargo, Date data) {
        String planoCargosSalarios = cargoFacade.retornarPlanoDecargosSalario(cargo, data);
        carreiras.setAttributeNode(criarAtributo(doc, "nome", planoCargosSalarios != null ? planoCargosSalarios : ""));
    }

    private void preencheTagDadosCarreiras(Document doc, Element carreiras, EnquadramentoPCS epcs) {
        if (epcs.getCategoriaPCS() != null && epcs.getProgressaoPCS() != null) {
            carreiras.setAttributeNode(criarAtributo(doc, "nome", epcs.getCategoriaPCS().getDescricao() + " " + epcs.getProgressaoPCS().getDescricao()));
        }

        if (epcs.getInicioVigencia() != null) {
            carreiras.setAttributeNode(criarAtributo(doc, "dataCriacao", DataUtil.getDataFormatada(epcs.getInicioVigencia(), "yyyy-MM-dd")));
        }
    }

    private void preencheTagDescricaoCarreira(Document doc, Element carreiras, VinculoFP vinculoFP) {
        carreiras.setAttributeNode(criarAtributo(doc, "nome", contratoFPFacade.recuperarDescricaoCarreiraPensionista(vinculoFP)));
    }

    private void preencheTagDescricaoCarreiraContratoFP(Document doc, Element carreiras, ContratoFP contrato) {
        carreiras.setAttributeNode(criarAtributo(doc, "nome", contratoFPFacade.recuperarDescricaoCarreiraContratoFP(contrato)));
    }

    public void gerarArquivoOrgaos() throws ParserConfigurationException, IOException, TransformerException {

        List<HierarquiaOrganizacional> hos = hierarquiaOrganizacionalFacade.buscarHierarquiaOrgaoVigentesSiprev();

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        file = File.createTempFile("siprevOrgaos", "xml");
        Document doc = docBuilder.newDocument();
        doc.setXmlStandalone(true);

        Element namaspace = doc.createElementNS("http://www.dataprev.gov.br/siprev", "ns2:siprev");
        doc.appendChild(namaspace);

        Element ente = doc.createElement("ente");
        namaspace.appendChild(ente);
        ente.setAttributeNode(criarAtributo(doc, "siafi", selecionado.getCodigo()));
        ente.setAttributeNode(criarAtributo(doc, "cnpj", pessoaJuridicaUnidadeGestora.getCnpj().replace("/", "").replace(".", "").replace("-", "").replace("-", "")));

        for (HierarquiaOrganizacional ho : hos) {
            if (ho.getSubordinada() == null || ho.getSubordinada().getEntidade() == null) {
                logger.error("Não existe entidade para a unidade {}", ho.getDescricao());
                continue;
            }
            Element orgaos = criarTag(doc, "orgaos", namaspace);
            preencheTagGeral(doc, orgaos);

            Element dadosOrgao = criarTag(doc, "dadosOrgao", orgaos);
            preencheTagDadosOrgaos(doc, dadosOrgao, ho);

            PessoaJuridica pj = (PessoaJuridica) pessoaFacade.recuperar(ho.getSubordinada().getEntidade().getPessoaJuridica().getId());
            Element endereco = criarTag(doc, "endereco", dadosOrgao);
            preencherTagEndereco(doc, pj, endereco);

            ResponsavelUnidade responsavel = hierarquiaOrganizacionalFacade.recuperarResponsavelPorUnidadeOrganizacional(ho.getSubordinada());
            if (responsavel != null && responsavel.getAtoLegal() != null) {
                Element unidadeGestora = criarTag(doc, "unidadeGestora", dadosOrgao);
                Element atoLegal = criarTag(doc, "atoLegal", unidadeGestora);
                preencheTagAtoLegalDependente(doc, atoLegal, responsavel.getAtoLegal());
            }
        }


        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
        transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");


        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(file);
        transformer.transform(source, result);

        files.put("siprevOrgaos.xml", file);
        addToZip();

    }

    private void preencheTagDadosOrgaos(Document doc, Element dadosOrgao, HierarquiaOrganizacional ho) {
        dadosOrgao.setAttributeNode(criarAtributo(doc, "nome", ho.getSubordinada().getEntidade().getNome()));
        if (ho.getSubordinada().getEntidade() != null && ho.getSubordinada().getEntidade().getPessoaJuridica() != null) {
            PessoaJuridica pj = (PessoaJuridica) pessoaFacade.recuperar(ho.getSubordinada().getEntidade().getPessoaJuridica().getId());

            Entidade entidade = ho.getSubordinada().getEntidade();
            if (pj.getRazaoSocial() != null) {
                dadosOrgao.setAttributeNode(criarAtributo(doc, "razaoSocial", pj.getRazaoSocial()));
            }
            //sigla
            if (pj.getCnpj() != null) {
                dadosOrgao.setAttributeNode(criarAtributo(doc, "cnpj", StringUtil.removeCaracteresEspeciaisSemEspaco(pj.getCnpj())));
            }
            // 3 - Municipal
            dadosOrgao.setAttributeNode(criarAtributo(doc, "esfera", "3"));
            if (entidade.getTipoUnidade() != null) {
                dadosOrgao.setAttributeNode(criarAtributo(doc, "naturezaJuridica", entidade.getTipoUnidade().getCodigoSiprev()));
            }
            if (entidade.getEsferaDoPoder() != null) {
                dadosOrgao.setAttributeNode(criarAtributo(doc, "poder", entidade.getEsferaDoPoder().getCodigoSiprev()));
            }

            Telefone telefone = pj.getTelefoneValido();
            if (telefone != null && telefone.getDDD() != null) {
                dadosOrgao.setAttributeNode(criarAtributo(doc, "dddTelefone", telefone.getDDD()));
            }
            if (telefone != null && telefone.getSomenteTelefone() != null) {
                dadosOrgao.setAttributeNode(criarAtributo(doc, "telefone", telefone.getSomenteTelefone().replace("(", "").replace(")", "")));
            }
            if (pj.getFax() != null) {
                dadosOrgao.setAttributeNode(criarAtributo(doc, "fax", pj.getFax().getTelefone()));
            }
            if (pj.getEmail() != null) {
                dadosOrgao.setAttributeNode(criarAtributo(doc, "email", pj.getEmail()));
            }
            if (pj.getHomePage() != null) {
                dadosOrgao.setAttributeNode(criarAtributo(doc, "site", pj.getHomePage()));
            }

            UnidadeOrganizacional uo = unidadeOrganizacionalFacade.recuperar(ho.getSubordinada().getId());
            if (uo.getUnidadeGestoraUnidadesOrganizacionais().isEmpty()) {
                dadosOrgao.setAttributeNode(criarAtributo(doc, "gestora", "0"));
            } else {
                dadosOrgao.setAttributeNode(criarAtributo(doc, "gestora", "1"));
            }

        }
    }

    private void validarGeracao() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getRepresentante() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O representante legal é obrigatório.");
        }
        if (selecionado.getInicioRepresentatividade() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A data inicial da representatividade é obrigatório.");
        }
        if (selecionado.getFimRepresentatividade() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A data de término prevista da representatividade é obrigatório.");
        }
        if (selecionado.getInicioRepresentatividade() != null && selecionado.getFimRepresentatividade() != null) {
            if (selecionado.getFimRepresentatividade().compareTo(selecionado.getInicioRepresentatividade()) < 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A data de término da representatividade não pode ser menor que a data de início da representatividade");
            }
        }
        if (selecionado.getTipoRepresentatividade() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O tipo da representatividade é obrigatório.");
        }
        if (selecionado.getMes() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O mês é obrigatório.");
        }
        if (selecionado.getExercicio() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O exercício é obrigatório.");
        }
        UnidadeGestora ug = siprevFacade.getUnidadeGestoraFacade().getUnidadeGestoraDo(CategoriaDeclaracaoPrestacaoContas.SIPREV);
        DeclaracaoPrestacaoContas dpc = siprevFacade.getDeclaracaoPrestacaoContasFacade().recuperarDeclaracaoParaFinalidade(CategoriaDeclaracaoPrestacaoContas.SIPREV);
        if (ug == null) {
            if (dpc != null) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("As Declarações/Prestações de Contas do SIPREV não está relacionada a nenhuma Unidade Gestora. Por favor, associe uma Unidade Gestora clicando '<b>" + Util.linkBlack("/declaracao-prestacao-contas/editar/" + dpc.getId() + "/", "AQUI") + "</b>'.");
            } else {
                ve.adicionarMensagemDeOperacaoNaoPermitida("As Declarações/Prestações de Contas do SIPREV não foram cadastradas. Por favor, crie o cadastro clicando '<b>" + Util.linkBlack("/declaracao-prestacao-contas/novo/", "AQUI") + "</b>'.");
            }
        } else {
            try {
                siprevFacade.getEntidadeFacade().recuperarEntidadePorUnidadeOrcamentaria(dpc.getUnidadeGestora().getUnidadeGestoraUnidadesOrganizacionais().get(0).getUnidadeOrganizacional());
            } catch (NullPointerException ne) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A Unidade Gestora " + ug.getCodigo() + " - " + ug.getDescricao() + " do Exercício de " + ug.getExercicio().getAno() + " informada na Declarações/Prestações de Contas do SIPREV não está relacionada a nenhuma Entidade. Por favor, selecione outra Unidade Gestora '<b>" + Util.linkBlack("/declaracao-prestacao-contas/editar/" + dpc.getId() + "/", "AQUI") + "</b>'.");
            }
            pessoaJuridicaUnidadeGestora = (PessoaJuridica) pessoaFacade.recuperar(ug.getPessoaJuridica().getId());
        }
        ve.lancarException();
    }

    public void gerarArquivoServidores(Date data) throws ParserConfigurationException, IOException, TransformerException {
        validarGeracao();
        if (vinculoFPs.isEmpty()) {
            vinculoFPs.addAll(vinculoFPFacade.recuperarTodosVinculosVigentes(data));
        }

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        file = File.createTempFile("siprevServidores", "xml");
        Document doc = docBuilder.newDocument();
        doc.setXmlStandalone(true);

        Element namaspace = doc.createElementNS("http://www.dataprev.gov.br/siprev", "ns2:siprev");
        doc.appendChild(namaspace);

        Element ente = doc.createElement("ente");
        namaspace.appendChild(ente);
        ente.setAttributeNode(criarAtributo(doc, "siafi", selecionado.getCodigo()));
        ente.setAttributeNode(criarAtributo(doc, "cnpj", pessoaJuridicaUnidadeGestora.getCnpj().replace("/", "").replace(".", "").replace("-", "")));

        for (VinculoFP vinculoFP : vinculoFPs) {
            Element servidores = criarTag(doc, "servidores", namaspace);
            preencheTagGeral(doc, servidores);

            PessoaFisica pessoa = pessoaFisicaFacade.recuperar(vinculoFP.getMatriculaFP().getPessoa().getId());
            RegistroDeObito registroDeObito = registroObitoFacade.buscarRegistroDeObitoPorPessoaFisica(pessoa);

            Element dadosPessoais = criarTag(doc, "dadosPessoais", servidores);
            preencheTagDadosPessoais(doc, vinculoFP, dadosPessoais, pessoa, registroDeObito);

            Element documentos = criarTag(doc, "documentos", servidores);
            preencheTagDocumentos(doc, pessoa, documentos);
            Element endereco = criarTag(doc, "endereco", servidores);
            preencherTagEndereco(doc, pessoa, endereco);
        }

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
        transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");


        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(file);
        transformer.transform(source, result);

        files.put("siprevServidores.xml", file);
        addToZip();
    }

    public void gerarArquivoDependentes(Date data) throws ParserConfigurationException, IOException, TransformerException {
        validarGeracao();
        if (vinculoFPs.isEmpty()) {
            vinculoFPs.addAll(vinculoFPFacade.recuperarTodosVinculosVigentes(data));
        }
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        file = File.createTempFile("dependentes", "xml");
        Document doc = docBuilder.newDocument();
        doc.setXmlStandalone(true);

        Element namaspace = doc.createElementNS("http://www.dataprev.gov.br/siprev", "ns2:siprev");
        doc.appendChild(namaspace);

        Element ente = criarTag(doc, "ente", namaspace);
        preencheTagEnte(doc, ente);

        selecionado.setRepresentante(pessoaFisicaFacade.recuperar(selecionado.getRepresentante().getId()));
        for (VinculoFP vinculoFP : vinculoFPs) {
            List<Dependente> listaDependente = dependenteFacade.dependentesPorResponsavel(vinculoFP.getMatriculaFP().getPessoa());
            for (Dependente dependente : listaDependente) {

                Element dependentes = criarTag(doc, "dependentes", namaspace);
                preencheTagGeral(doc, dependentes);

                Element dependencias = criarTag(doc, "dependencias", dependentes);
                preencheTagDependencias(doc, dependencias, dependente);

                Element servidor = criarTag(doc, "servidor", dependencias);
                preencherTagServidorCompleto(doc, servidor, dependente.getResponsavel());

                if (dependente.getAtoLegal() != null) {
                    Element atoLegal = criarTag(doc, "atoLegal", dependencias);
                    preencheTagAtoLegalDependente(doc, atoLegal, dependente.getAtoLegal());
                }

                Element dadosPessoais = criarTag(doc, "dadosPessoais", dependentes);
                preencheTagDadosPessoaisDependentes(doc, dadosPessoais, dependente);

                Element documentos = criarTag(doc, "documentos", dependentes);
                preencheTagDocumentosDependentes(doc, documentos, dependente);

                PessoaFisica pessoaFisica = pessoaFisicaFacade.recuperar(dependente.getResponsavel().getId());

                Element contato = criarTag(doc, "contato", dependentes);
                preencheTagContato(doc, pessoaFisica, contato);

                Element endereco = criarTag(doc, "endereco", dependentes);
                preencherTagEndereco(doc, pessoaFisica, endereco);

                Element certidaoNascimento = criarTag(doc, "certidaoNascimento", dependentes);
                preencheTagCertidaoNascimentoDependentes(doc, pessoaFisica, certidaoNascimento);

                Element representatividade = criarTag(doc, "representatividade", dependentes);
                preencheTagRepresentatividade(doc, selecionado.getRepresentante().getAsPessoaFisica(), representatividade);

                Element representanteLegal = criarTag(doc, "representanteLegal", representatividade);
                preencheTagRepresentanteLegal(doc, selecionado.getRepresentante().getAsPessoaFisica(), representanteLegal);

                Element representanteLegalEndereco = criarTag(doc, "representanteLegalEndereco", representanteLegal);
                preencheTagRepresentanteLegalEndereco(doc, selecionado.getRepresentante().getAsPessoaFisica(), representanteLegalEndereco);

                Element representanteLegalContato = criarTag(doc, "representanteLegalContato", representanteLegal);
                preencheTagRepresentanteLegalContato(doc, selecionado.getRepresentante().getAsPessoaFisica(), representanteLegalContato);

            }
        }

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
        transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");


        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(file);
        transformer.transform(source, result);

        files.put("siprevDependentes.xml", file);
        addToZip();
    }


    public void gerarArquivoPensionista(Date dt) throws ParserConfigurationException, IOException, TransformerException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        file = File.createTempFile("pensionista", "xml");
        Document doc = docBuilder.newDocument();
        doc.setXmlStandalone(true);

        Element namaspace = doc.createElementNS("http://www.dataprev.gov.br/siprev", "ns2:siprev");
        doc.appendChild(namaspace);

        Element ente = doc.createElement("ente");
        namaspace.appendChild(ente);
        ente.setAttributeNode(criarAtributo(doc, "siafi", selecionado.getCodigo()));
        ente.setAttributeNode(criarAtributo(doc, "cnpj", pessoaJuridicaUnidadeGestora.getCnpj().replace("/", "").replace(".", "").replace("-", "")));

        List<Pensionista> listaPensionista = pensionistaFacade.recuperaPensionistasVigente(dt);
        for (Pensionista pensionista : listaPensionista) {
            Pensao pensao = pensionistaFacade.recuperarPensaoDoPensionista(pensionista);

            PessoaFisica instituidor = pessoaFisicaFacade.recuperar(pensao.getContratoFP().getMatriculaFP().getPessoa().getId());
            PessoaFisica pessoaPensionista = pessoaFisicaFacade.recuperar(pensionista.getMatriculaFP().getPessoa().getId());

            Element pensionistas = criarTag(doc, "pensionistas", namaspace);
            preencheTagGeral(doc, pensionistas);

            Element dependencias = criarTag(doc, "dependencias", pensionistas);
            preencheTagPensionista(doc, dependencias, pensionista);

            Element tagInstituidor = criarTag(doc, "servidor", dependencias);
            preencheTagServidor(doc, tagInstituidor, instituidor);

            Element dadosPessoais = criarTag(doc, "dadosPessoais", pensionistas);
            preencheTagDadosPessoaisPensionista(doc, dadosPessoais, pessoaPensionista);

        }

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
        transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");


        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(file);
        transformer.transform(source, result);

        files.put("siprevPensionitas.xml", file);
        addToZip();
    }

    public void gerarArquivoHistoricoFinanceiro(Date data) throws ParserConfigurationException, IOException, TransformerException {
        Document doc = getDocument("historicoFinanceiro");
        Element ente = criarCabecalhoXML(doc);
        List<String> codigoEvento = Lists.newArrayList();
        codigoEvento.add("898");
        codigoEvento.add("896");


        for (ContratoFPeItemFichaFinanceira contratoFPeItemFichaFinanceira : contratoFPFacade.recuperarContratoFPComFichaFinanceira(selecionado.getMes().getNumeroMes(), selecionado.getExercicio().getAno(), codigoEvento)) {
            LotacaoFuncional lotacaoFuncional = lotacaoFuncionalFacade.recuperaLotacaoFuncionalInstituidor(contratoFPeItemFichaFinanceira.getContratoFP());

            Element historicosFinanceiros = criarTag(doc, "historicosFinanceiros", ente);
            preencheTagSomenteOperacao(doc, historicosFinanceiros);

            Element vinculoFuncional = criarTag(doc, "vinculoFuncional", historicosFinanceiros);
            preencherTagVinculoFuncional(doc, vinculoFuncional, contratoFPeItemFichaFinanceira.getContratoFP(), data);

            Element orgao = criarTag(doc, "orgao", vinculoFuncional);
            preencheTagOrgao(doc, orgao, lotacaoFuncional);

            Element servidor = criarTag(doc, "servidor", vinculoFuncional);
            preencherTagServidorCompleto(doc, servidor, contratoFPeItemFichaFinanceira.getContratoFP().getMatriculaFP().getPessoa());

            Element cargo = criarTag(doc, "cargo", vinculoFuncional);
            preencherTagCargoBeneficioServidor(doc, cargo, new BigDecimal(contratoFPeItemFichaFinanceira.getContratoFP().getCargo().getId()));
            Element carreira = criarTag(doc, "carreira", cargo);
            preencheTagDescricaoCarreiraContratoFP(doc, carreira, contratoFPeItemFichaFinanceira.getContratoFP());

            Element orgao2 = criarTag(doc, "orgao", carreira);
            preencheTagOrgao(doc, orgao2, lotacaoFuncional);

            Element dadosHistoricoFinanceiro = criarTag(doc, "dadosHistoricoFinanceiro", historicosFinanceiros);
            preencheTagDadosHistoricoFinanceiro(doc, dadosHistoricoFinanceiro, selecionado.getMes().getNumeroMes(), selecionado.getExercicio().getAno(), contratoFPeItemFichaFinanceira, data);

        }

        Transformer transformer = encodingStandaloneXML();
        criarXML(doc, transformer, "historicoFinanceiro.xml");
        addToZip();
    }

    public void gerarArquivoHistoricoFuncionalRPPS(Date data) throws ParserConfigurationException, IOException, TransformerException {
        Document doc = getDocument("historicoFuncional");
        Element ente = criarCabecalhoXML(doc);

        List<Long> codigo = Lists.newArrayList();
        codigo.add(1l);
        codigo.add(2l);
        codigo.add(3l);
        codigo.add(4l);
        codigo.add(5l);
        codigo.add(6l);
        codigo.add(7l);
        codigo.add(10l);

        for (ContratoFP contratoFP : contratoFPFacade.recuperarContratoHistoricoFuncionalRPPs(codigo, data)) {
            LotacaoFuncional lotacaoFuncional = lotacaoFuncionalFacade.recuperaLotacaoFuncionalInstituidor(contratoFP);

            Element vinculosFuncionaisRpps = criarTag(doc, "vinculosFuncionaisRpps", ente);
            preencheTagVinculosFuncionaisRpps(doc, vinculosFuncionaisRpps, contratoFP, data);

            Element orgao = criarTag(doc, "orgao", vinculosFuncionaisRpps);
            preencheTagOrgao(doc, orgao, lotacaoFuncional);

            Element servidor = criarTag(doc, "servidor", vinculosFuncionaisRpps);
            preencherTagServidorCompleto(doc, servidor, contratoFP.getMatriculaFP().getPessoa());

            if (contratoFP.getCargo() != null) {
                Element cargo = criarTag(doc, "cargo", vinculosFuncionaisRpps);
                preencherTagCargoBeneficioServidor(doc, cargo, new BigDecimal(contratoFP.getCargo().getId()));

                Element carreira = criarTag(doc, "carreira", cargo);
                preencheTagDescricaoCarreiraContratoFP(doc, carreira, contratoFP);

                Element orgao2 = criarTag(doc, "orgao", carreira);
                preencheTagOrgao(doc, orgao2, lotacaoFuncional);

                //um contratoFP pode ter 1 ou mais SituacaoContratoFP
                for (SituacaoContratoFP situacao : contratoFPFacade.recuperarSituacaoFuncionalDoContratoFP(contratoFP, data)) {
                    Element movimentacoesFuncionaisRpps = criarTag(doc, "movimentacoesFuncionaisRpps", vinculosFuncionaisRpps);
                    preencherTagMovimentacoesFuncionaisRpps(doc, movimentacoesFuncionaisRpps, contratoFP, data, situacao);
                }
            }

        }

        Transformer transformer = encodingStandaloneXML();
        criarXML(doc, transformer, "historicoFuncional.xml");
        addToZip();
    }

    public void gerarArquivoHistoricoFuncionalRGPS(Date data) throws ParserConfigurationException, IOException, TransformerException {
        Document doc = getDocument("historicoFuncional");
        Element ente = criarCabecalhoXML(doc);

        List<Long> codigo = Lists.newArrayList();
        codigo.add(1l);
        codigo.add(2l);
        codigo.add(3l);
        codigo.add(4l);
        codigo.add(5l);
        codigo.add(6l);
        codigo.add(7l);
        codigo.add(10l);

        for (ContratoFP contratoFP : contratoFPFacade.recuperarContratoHistoricoFuncionalRPPs(codigo, data)) {
            LotacaoFuncional lotacaoFuncional = lotacaoFuncionalFacade.recuperaLotacaoFuncionalInstituidor(contratoFP);

            Element vinculosFuncionaisRgps = criarTag(doc, "vinculosFuncionaisRgps", ente);
            preencheTagVinculosFuncionaisRgps(doc, vinculosFuncionaisRgps, contratoFP, data);

            if (lotacaoFuncional.getId() != null) {
                Element orgao = criarTag(doc, "orgao", vinculosFuncionaisRgps);
                preencheTagOrgao(doc, orgao, lotacaoFuncional);
            }


            Element servidor = criarTag(doc, "servidor", vinculosFuncionaisRgps);
            preencherTagServidorCompleto(doc, servidor, contratoFP.getMatriculaFP().getPessoa());
//
//                //um contratoFP pode ter 1 ou mais SituacaoContratoFP
            for (SituacaoContratoFP situacao : contratoFPFacade.recuperarSituacaoFuncionalDoContratoFP(contratoFP, data)) {
                Element movimentacoesFuncionaisRpps = criarTag(doc, "movimentacoesFuncionaisRpps", vinculosFuncionaisRgps);
                preencherTagMovimentacoesFuncionaisRGPS(doc, movimentacoesFuncionaisRpps, contratoFP, data, situacao);
            }


        }

        Transformer transformer = encodingStandaloneXML();
        criarXML(doc, transformer, "historicoFuncional.xml");
        addToZip();
    }


    public void gerarArquivoBeneficioPensionista(Date dt) throws ParserConfigurationException, IOException, TransformerException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        file = File.createTempFile("beneficioPensionista", "xml");
        Document doc = docBuilder.newDocument();
        doc.setXmlStandalone(true);

        Element namaspace = doc.createElementNS("http://www.dataprev.gov.br/siprev", "ns2:siprev");
        doc.appendChild(namaspace);
        Element ente = criarTag(doc, "ente", namaspace);
        preencheTagEnte(doc, ente);

        List<Pensionista> listaPensionista = pensionistaFacade.recuperaPensionistasVigente(dt);
        for (Pensionista pensionista : listaPensionista) {

            Pensao pensao = pensionistaFacade.recuperarPensaoDoPensionista(pensionista);
            PessoaFisica instituidor = pessoaFisicaFacade.recuperar(pensao.getContratoFP().getMatriculaFP().getPessoa().getId());
            PessoaFisica pessoaPensionista = pessoaFisicaFacade.recuperar(pensionista.getMatriculaFP().getPessoa().getId());

            LotacaoFuncional lotacaoFuncional = lotacaoFuncionalFacade.recuperaLotacaoFuncionalInstituidor(pensao.getContratoFP());
            if (lotacaoFuncional == null || lotacaoFuncional.getUnidadeOrganizacional() == null) {
                continue;
            }

            Element beneficioPensionista = criarTag(doc, "beneficioPensionista", namaspace);
            List<EventoFP> eventos = new LinkedList<>();
            eventos.addAll(eventosPensionsita());
            ItemFichaFinanceiraFP itemFicha = fichaFinanceiraFPFacade.recuperaValorItemFichaFinanceiraFPPorEventos(pensionista.getId(), selecionado.getMes().getNumeroMes(), selecionado.getExercicio().getAno(), TipoFolhaDePagamento.NORMAL, eventos);
            preencheTagGeralBeneficioPensionista(doc, beneficioPensionista, pensionista, itemFicha);


            Element cargo = criarTag(doc, "cargo", beneficioPensionista);
            if (pensao.getContratoFP().getCargo() != null) {
                preencherTagCargoBeneficioServidor(doc, cargo, new BigDecimal(pensao.getContratoFP().getCargo().getId()));
            }

            Element carreira = criarTag(doc, "carreira", cargo);
            preencheTagDescricaoCarreira(doc, carreira, pensao.getContratoFP());

            Element orgao = criarTag(doc, "orgao", carreira);
            preencheTagOrgao(doc, orgao, lotacaoFuncional);

            Element tagInstituidor = criarTag(doc, "instituidor", beneficioPensionista);
            preencheTagServidor(doc, tagInstituidor, instituidor);
//
            Element quota = criarTag(doc, "quota", beneficioPensionista);
            preencheTagQuota(doc, quota, pensionista);

            Element tagPensionista = criarTag(doc, "pensionista", quota);
            preencheTagServidor(doc, tagPensionista, pessoaPensionista);
//
        }

        Transformer transformer = encodingStandaloneXML();
        criarXML(doc, transformer, "beneficioPensionista.xml");
        addToZip();
    }

    private List<EventoFP> eventosPensionsita() {
        List<EventoFP> lista = new LinkedList<>();
        lista.add(eventoFPFacade.recuperaEvento("101", TipoExecucaoEP.FOLHA));
        lista.add(eventoFPFacade.recuperaEvento("600", TipoExecucaoEP.FOLHA));
        lista.add(eventoFPFacade.recuperaEvento("354", TipoExecucaoEP.FOLHA));
        lista.add(eventoFPFacade.recuperaEvento("355", TipoExecucaoEP.FOLHA));
        return lista;
    }

    private void preencheTagAtoLegalDependente(Document doc, Element element, AtoLegal atoLegal) {
        if (atoLegal.getDataEmissao() != null) {
            Integer ano = DataUtil.getAno(atoLegal.getDataEmissao());
            element.setAttributeNode(criarAtributo(doc, "ano", ano.toString()));
        }
        if (atoLegal.getDataEmissao() != null) {
            element.setAttributeNode(criarAtributo(doc, "dataPublicacao", DataUtil.getDataFormatada(atoLegal.getDataEmissao(), "yyyy-MM-dd")));
        }
        if (atoLegal.getDataPublicacao() != null) {
            element.setAttributeNode(criarAtributo(doc, "dataPublicacao", DataUtil.getDataFormatada(atoLegal.getDataPublicacao(), "yyyy-MM-dd")));
        }
        if (!atoLegal.getNumero().equals("")) {
            element.setAttributeNode(criarAtributo(doc, "numero", atoLegal.getNumero()));
        }
        if (atoLegal.getFundamentacaoLegal().equals("")) {
            element.setAttributeNode(criarAtributo(doc, "ementa", atoLegal.getFundamentacaoLegal()));
        }
    }

    private void preencheTagCertidaoNascimentoDependentes(Document doc, PessoaFisica pessoaFisica, Element certidaoNascimento) {
        if (pessoaFisica.getCertidaoNascimento() != null) {
            if (pessoaFisica.getCertidaoNascimento().getNumeroRegistro() != null) {
                certidaoNascimento.setAttributeNode(criarAtributo(doc, "numeroTermoCertidao", pessoaFisica.getCertidaoNascimento().getNumeroRegistro().toString()));
            }
            if (pessoaFisica.getCertidaoNascimento().getNumeroFolha() != null) {
                certidaoNascimento.setAttributeNode(criarAtributo(doc, "numeroFolhaCertidao", Util.cortarString(pessoaFisica.getCertidaoNascimento().getNumeroFolha().replace(" ", ""), 6)));
            }
            if (pessoaFisica.getCertidaoNascimento().getNumeroLivro() != null) {
                certidaoNascimento.setAttributeNode(criarAtributo(doc, "numeroLivroCertidao", Util.cortarString(pessoaFisica.getCertidaoNascimento().getNumeroLivro().replace(" ", ""), 6)));
            }
        }
    }


    private void preencheTagDocumentosDependentes(Document doc, Element documentos, Dependente dependente) {
        PessoaFisica depPessoaFisica = pessoaFisicaFacade.recuperar(dependente.getDependente().getId());
        if (depPessoaFisica.getCpf() != null) {
            documentos.setAttributeNode(criarAtributo(doc, "numeroCPF", StringUtil.removeCaracteresEspeciaisSemEspaco(depPessoaFisica.getCpf()).replace(" ", "")));
        }
        if (depPessoaFisica.getCarteiraDeTrabalho() != null && depPessoaFisica.getCarteiraDeTrabalho().getPisPasep() != null) {
            documentos.setAttributeNode(criarAtributo(doc, "numeroNIT", depPessoaFisica.getCarteiraDeTrabalho().getPisPasep()));
        }
        if (depPessoaFisica.getRg() != null) {
            if (depPessoaFisica.getRg().getNumero() != null) {
                documentos.setAttributeNode(criarAtributo(doc, "numeroRG", depPessoaFisica.getRg().getNumero()));
            }
            if (depPessoaFisica.getRg().getDataemissao() != null) {
                documentos.setAttributeNode(criarAtributo(doc, "dataEmissaoRG", DataUtil.getDataFormatada(depPessoaFisica.getRg().getDataemissao(), "yyyy-MM-dd")));
            }
            //órgão expedidor
            if (depPessoaFisica.getRg().getUf() != null) {
                documentos.setAttributeNode(criarAtributo(doc, "ufRG", depPessoaFisica.getRg().getUf().getUf()));
            }
        }
    }

    private void preencheTagDadosPessoaisDependentes(Document doc, Element dadosPessoais, Dependente dependente) {
        if (dependente.getDependente() != null) {
            if (!dependente.getDependente().getNome().equals("") && dependente.getDependente().getNome() != null) {
                dadosPessoais.setAttributeNode(criarAtributo(doc, "nome", Util.removerCaracteresEspeciais(dependente.getDependente().getNome()).replace("'", "&apos;")));
            }
            if (dependente.getDependente().getEstadoCivil() != null) {
                dadosPessoais.setAttributeNode(criarAtributo(doc, "estadoCivil", dependente.getDependente().getEstadoCivil().getCodigoSiprev()));
            }
            if (dependente.getDependente().getDataNascimento() != null) {
                dadosPessoais.setAttributeNode(criarAtributo(doc, "dataNascimento", dependente.getDependente().getDataNascimentoFormatada("yyyy-MM-dd")));
            } else {
                selecionado.getErros().add(new SiprevErro(selecionado, "O dependente " + dependente.getDependente().getNome() + " não possui data de nascimento informado."));
            }
            if (dependente.getDependente().getNacionalidade() != null) {
                if (dependente.getDependente().getNacionalidade().getDescricao().equals("BRASILEIRO")) {
                    dadosPessoais.setAttributeNode(criarAtributo(doc, "nacionalidade", "76"));
                }
            }

            if (dependente.getDependente().getNaturalidade() != null) {
                dadosPessoais.setAttributeNode(criarAtributo(doc, "naturalidade", Util.cortarString(dependente.getDependente().getNaturalidade().getCodigo().toString(), 6)));
            }
            if (dependente.getDependente().getSexo() != null) {
                dadosPessoais.setAttributeNode(criarAtributo(doc, "sexo", dependente.getDependente().getSexo().equals(Sexo.FEMININO) ? "F" : "M"));
            }
            if (dependente.getDependente().getPai() != null) {
                dadosPessoais.setAttributeNode(criarAtributo(doc, "nomePai", Util.removerCaracteresEspeciais(dependente.getDependente().getPai()).replace("'", "&apos;")));
            }

            dadosPessoais.setAttributeNode(criarAtributo(doc, "nomeMae", (dependente.getDependente().getMae() != null ? Util.removerCaracteresEspeciais(dependente.getDependente().getMae()).replace("'", "&apos;") : "")));
        }
    }

    private void preencherTagServidorCompleto(Document doc, Element servidor, PessoaFisica pessoa) {
        PessoaFisicaSiprev pessoaFisicaSiprev = pessoaFisicaFacade.buscarPessoaFisicaSiprev(pessoa.getId());
        servidor.setAttributeNode(criarAtributo(doc, "nome", pessoaFisicaSiprev.getNome() != null ? Util.removerCaracteresEspeciais(pessoaFisicaSiprev.getNome()).replace("'", "&apos;") : pessoaFisicaSiprev.getNome()));
        servidor.setAttributeNode(criarAtributo(doc, "numeroCPF", StringUtil.removeCaracteresEspeciaisSemEspaco(pessoaFisicaSiprev.getCpf() != null ? pessoaFisicaSiprev.getCpf().trim() : "")));
        if (pessoaFisicaSiprev.pISPASEP != null) {
            servidor.setAttributeNode(criarAtributo(doc, "numeroNIT", StringUtil.removeCaracteresEspeciaisSemEspaco(pessoaFisicaSiprev.pISPASEP)));
        }
        if (pessoaFisicaSiprev.getRg() != null) {
            servidor.setAttributeNode(criarAtributo(doc, "numeroRG", StringUtil.removeCaracteresEspeciaisSemEspaco(pessoaFisicaSiprev.getRg())));
        }
        if (pessoaFisicaSiprev.getDataNascimento() != null) {
            servidor.setAttributeNode(criarAtributo(doc, "dataNascimento", DataUtil.getDataFormatada(pessoaFisicaSiprev.getDataNascimento(), "yyyy-MM-dd")));
        }
        servidor.setAttributeNode(criarAtributo(doc, "nomeMae", pessoaFisicaSiprev.getNomeMae() != null ? Util.removerCaracteresEspeciais(pessoaFisicaSiprev.getNomeMae()).replace("'", "&apos;") : ""));
    }

    private void preencheTagDependencias(Document doc, Element dependencias, Dependente dependente) {

        dependencias.setAttributeNode(criarAtributo(doc, "tipoDependencia", defineTipoDependencia(dependente)));

//        TipoDependente finsPrevidenciario = tipoDependenteFacade.recuperarTipoDependentePorCodigoEGrau("5", dependente.getGrauDeParentesco());
//        dependencias.setAttributeNode(criarAtributo(doc, "finsPrevidenciario", finsPrevidenciario != null ? "0" : "1"));

        DependenteVinculoFP depVin = dependenteVinculoFPFacade.recuperaDependenteVigentePorDependente(dependente.getDependente());
        if (depVin != null) {
            dependencias.setAttributeNode(criarAtributo(doc, "dataInicioDependencia", DataUtil.getDataFormatada(depVin.getInicioVigencia(), "yyyy-MM-dd")));
            if (depVin.getFinalVigencia() != null) {
                dependencias.setAttributeNode(criarAtributo(doc, "dataPrevistaFimDependencia", DataUtil.getDataFormatada(depVin.getFinalVigencia(), "yyyy-MM-dd")));
                dependencias.setAttributeNode(criarAtributo(doc, "dataFimDependencia", DataUtil.getDataFormatada(depVin.getFinalVigencia(), "yyyy-MM-dd")));
            }
        }
    }

    private void preencheTagPensionista(Document doc, Element p, Pensionista pensionista) {
        p.setAttributeNode(criarAtributo(doc, "tipoDependencia", "99")); // em conversa com o Alysson foi defino que sempre seria 99
        p.setAttributeNode(criarAtributo(doc, "motivoInicioDependencia", "98")); // em conversa com o Alysson foi defino que sempre seria 98
        p.setAttributeNode(criarAtributo(doc, "dataInicioPensao", DataUtil.getDataFormatada(pensionista.getInicioVigencia(), "yyyy-MM-dd")));
    }


    //método que define o tipo de dependencia seguindo a tabela 07 do layout de dependencia
    private String defineTipoDependencia(Dependente dependente) {
        if (dependente.getGrauDeParentesco() == null || dependente.getGrauDeParentesco().getCodigoRais() == null) {
            return "99";
        }
        if (dependente.getGrauDeParentesco().getCodigoRais() == 1) {
            return "1";
        }
        if (dependente.getGrauDeParentesco().getCodigoRais() == 2) {
            return "3";
        }
        if (dependente.getGrauDeParentesco().getCodigoRais() == 4) {
            return "5";
        }
        if (dependente.getGrauDeParentesco().getCodigoRais() == 10) {
            return "6";
        }
        if (dependente.getGrauDeParentesco().getCodigoRais() == 6) {
            return "8";
        }
        if (dependente.getGrauDeParentesco().getCodigoRais() == 7) {
            return "10";
        }
        if (dependente.getGrauDeParentesco().getCodigoRais() == 9) {
            return "12";
        }
        return "99";
    }

    private void preencheTagDependentes(Document doc, Element dependentes) {
        dependentes.setAttributeNode(criarAtributo(doc, "operacao", OperacaoRegistro.INCLUSAO.getCodigo()));
        dependentes.setAttributeNode(criarAtributo(doc, "erroImportacao", ""));
    }

    private void preencheTagCartorioRegistroObito(Document doc, PessoaFisica pessoa, Element cartorioRegistroObito, RegistroDeObito registroDeObito) {
        if (registroDeObito != null) {
            if (!Strings.isNullOrEmpty(registroDeObito.getCartorio())) {
                cartorioRegistroObito.setAttributeNode(criarAtributo(doc, "nome", registroDeObito.getCartorio()));
            }
        }
    }

    private void preencheTagCertidaoObito(Document doc, PessoaFisica pessoa, Element certidaoObito, RegistroDeObito registroDeObito) {

        if (registroDeObito != null) {
            if (registroDeObito.getTermo() != null) {
                certidaoObito.setAttributeNode(criarAtributo(doc, "numeroTermoCertidao", registroDeObito.getTermo()));
            }
            if (registroDeObito.getFolha() != null) {
                certidaoObito.setAttributeNode(criarAtributo(doc, "numeroFolhaCertidao", registroDeObito.getFolha()));
            }
            if (registroDeObito.getLivro() != null) {
                certidaoObito.setAttributeNode(criarAtributo(doc, "numeroLivroCertidao", registroDeObito.getLivro()));
            }
        }
    }

    private void preencheTagRepresentatividade(Document doc, PessoaFisica pessoa, Element certidaoRepresentatividade) {
        certidaoRepresentatividade.setAttributeNode(criarAtributo(doc, "dataInicio", DataUtil.getDataFormatada(selecionado.getInicioRepresentatividade(), "yyyy-MM-dd")));
        certidaoRepresentatividade.setAttributeNode(criarAtributo(doc, "dataTerminoPrevisto", DataUtil.getDataFormatada(selecionado.getFimRepresentatividade(), "yyyy-MM-dd")));
        certidaoRepresentatividade.setAttributeNode(criarAtributo(doc, "tipoRepresentatividade", selecionado.getTipoRepresentatividade().getCodigo()));
    }

    private void preencheTagRepresentanteLegal(Document doc, PessoaFisica pessoa, Element certidaoRepresentanteLegal) {
        if (pessoa.getRg() != null && pessoa.getRg().getDataemissao() != null) {
            certidaoRepresentanteLegal.setAttributeNode(criarAtributo(doc, "dataEmissaoRG", DataUtil.getDataFormatada(pessoa.getRg().getDataemissao(), "yyyy-MM-dd")));
        }
        if (pessoa.getDataNascimento() != null) {
            certidaoRepresentanteLegal.setAttributeNode(criarAtributo(doc, "dataNascimento", DataUtil.getDataFormatada(pessoa.getDataNascimento(), "yyyy-MM-dd")));
        }
        certidaoRepresentanteLegal.setAttributeNode(criarAtributo(doc, "nome", pessoa.getNome()));
        if (pessoa.getCpf() != null) {
            certidaoRepresentanteLegal.setAttributeNode(criarAtributo(doc, "numeroCPF", StringUtil.removeCaracteresEspeciaisSemEspaco(pessoa.getCpf())));
        }
        if (pessoa.getRg() != null) {
            if (!pessoa.getRg().getNumero().equals("")) {
                certidaoRepresentanteLegal.setAttributeNode(criarAtributo(doc, "numeroRG", StringUtil.removeCaracteresEspeciaisSemEspaco(pessoa.getRg().getNumero())));
            }
            if (!pessoa.getRg().getOrgaoEmissao().equals("")) {
                certidaoRepresentanteLegal.setAttributeNode(criarAtributo(doc, "orgaoExpedidorRG", "1")); // Pedido do Alysson, se o órgão emissor não estiver nulo setar 1 (SSP)
            }

            if (pessoa.getRg().getUf() != null) {
                certidaoRepresentanteLegal.setAttributeNode(criarAtributo(doc, "ufRG", pessoa.getRg().getUf().getUf()));
            }
        }
        if (pessoa.getSexo() != null) {
            certidaoRepresentanteLegal.setAttributeNode(criarAtributo(doc, "sexo", pessoa.getSexo().getSigla()));
        }
    }

    private void preencheTagRepresentanteLegalEndereco(Document doc, PessoaFisica pessoa, Element certidaoRepresentatividade) {
        if (!pessoa.getEnderecoscorreio().isEmpty()) {
            preencherTagEndereco(doc, pessoa, certidaoRepresentatividade);
        }
    }

    private void preencheTagRepresentanteLegalContato(Document doc, PessoaFisica pessoa, Element certidaoRepresentatividade) {
        preencheTagContato(doc, pessoa, certidaoRepresentatividade);
    }

    private void preencheTagFuncaoGratificada(Document doc, Element funcaoGratificada, ContratoFP contratoFP, EnquadramentoFG enquadramentoFG, UnidadeOrganizacional uo, LotacaoFuncional lote, BigDecimal valorEnquadramentoPCS) {
        DecimalFormat format = new DecimalFormat("#0.00");
        funcaoGratificada.setAttributeNode(criarAtributo(doc, "operacao", OperacaoRegistro.INCLUSAO.getCodigo()));
        funcaoGratificada.setAttributeNode(criarAtributo(doc, "descricao", enquadramentoFG.getCategoriaPCS().getDescricao()));
        funcaoGratificada.setAttributeNode(criarAtributo(doc, "codigoFuncao", enquadramentoFG.getCategoriaPCS().getCodigo() != null ? enquadramentoFG.getCategoriaPCS().getCodigo().toString() : "0"));
        funcaoGratificada.setAttributeNode(criarAtributo(doc, "dataNomeacao", DataUtil.getDataFormatada(enquadramentoFG.getInicioVigencia(), "yyyy-MM-dd")));
        funcaoGratificada.setAttributeNode(criarAtributo(doc, "opcaoFuncaoGratificada", "1"));
        funcaoGratificada.setAttributeNode(criarAtributo(doc, "nomeOrgaoOrigem", lote.getUnidadeOrganizacional().getDescricao().trim()));
        funcaoGratificada.setAttributeNode(criarAtributo(doc, "dedicacaoExclusiva", contratoFP.getCargo().getDedicacaoExclusivaSIPREV() != null ? (contratoFP.getCargo().getDedicacaoExclusivaSIPREV() ? "1" : "0") : "0"));
        funcaoGratificada.setAttributeNode(criarAtributo(doc, "valorContribuicao", format.format(valorEnquadramentoPCS).replace(",", ".")));
        funcaoGratificada.setAttributeNode(criarAtributo(doc, "incideContribuicao", "1"));   //Pela pesquisa no arquivo, vamos utilizar o valor 1 fixo
        if (uo.getEntidade() != null) {
            funcaoGratificada.setAttributeNode(criarAtributo(doc, "CNPJOrgaoOrigem", StringUtil.removeCaracteresEspeciaisSemEspaco(uo.getEntidade().getPessoaJuridica().getCpf_Cnpj())));
        }
    }

    private void preencheTagGeralAliquota(Document doc, Element aliquota) {
        aliquota.setAttributeNode(criarAtributo(doc, "operacao", OperacaoRegistro.INCLUSAO.getCodigo()));
        aliquota.setAttributeNode(criarAtributo(doc, "publicoAlvo", "1"));
        ValorReferenciaFP valorReferenciaFP = referenciaFPFacade.buscarValorRefenciaFPPorCodigo(1);
        if (valorReferenciaFP != null) {
            aliquota.setAttributeNode(criarAtributo(doc, "aliquotaBeneficiario", String.format("%.2f", valorReferenciaFP.getValor()).replace(",", ".")));
            aliquota.setAttributeNode(criarAtributo(doc, "aliquotaEnte", "0.00"));
            aliquota.setAttributeNode(criarAtributo(doc, "dataInicioAliquota", DataUtil.getDataFormatada(valorReferenciaFP.getInicioVigencia(), "yyyy-MM-dd")));
            if (valorReferenciaFP.getFinalVigencia() != null) {
                aliquota.setAttributeNode(criarAtributo(doc, "dataFimAliquota", DataUtil.getDataFormatada(valorReferenciaFP.getFinalVigencia(), "yyyy-MM-dd")));
            }
        } else {
            aliquota.setAttributeNode(criarAtributo(doc, "aliquotaBeneficiario", "0.00"));
            aliquota.setAttributeNode(criarAtributo(doc, "aliquotaEnte", "0.00"));
        }

    }

    private void preencherTagGeralAtoLegal(Document doc, Element atoLegal) {
        DeclaracaoPrestacaoContas dpc = siprevFacade.getDeclaracaoPrestacaoContasFacade().recuperarDeclaracaoParaFinalidade(CategoriaDeclaracaoPrestacaoContas.SIPREV);
        Entidade entidade = siprevFacade.getEntidadeFacade().recuperarEntidadePorUnidadeOrcamentaria(dpc.getUnidadeGestora().getUnidadeGestoraUnidadesOrganizacionais().get(0).getUnidadeOrganizacional());
        atoLegal.setAttributeNode(criarAtributo(doc, "ano", entidade.getAtoLegal().getExercicio().getAno().toString()));
        atoLegal.setAttributeNode(criarAtributo(doc, "tipoAto", buscarCodigoTipoAto(entidade.getAtoLegal().getTipoAtoLegal())));
        atoLegal.setAttributeNode(criarAtributo(doc, "numero", entidade.getAtoLegal().getNumero()));
        atoLegal.setAttributeNode(criarAtributo(doc, "dataPublicacao", DataUtil.getDataFormatada(entidade.getAtoLegal().getDataPublicacao(), "yyyy-MM-dd")));
        atoLegal.setAttributeNode(criarAtributo(doc, "dataInicioVigencia", DataUtil.getDataFormatada(entidade.getAtoLegal().getDataPromulgacao(), "yyyy-MM-dd")));
        if (entidade.getAtoLegal().getDataValidade() != null) {
            atoLegal.setAttributeNode(criarAtributo(doc, "dataRevogacao", DataUtil.getDataFormatada(entidade.getAtoLegal().getDataValidade(), "yyyy-MM-dd")));
        }
    }

    private String buscarCodigoTipoAto(TipoAtoLegal tipoAtoLegal) {
        switch (tipoAtoLegal) {
            case DECRETO:
                return "2";
            case EMENDA:
                return "4";
            case LEI_COMPLEMENTAR:
                return "5";
            case LEI_ORDINARIA:
                return "6";
            case LEI_ORGANICA:
                return "8";
            case MEDIDA_PROVISORIA:
                return "9";
            case PORTARIA:
                return "10";
            case RESOLUCAO:
                return "11";
            case PARECER:
                return "12";
            default:
                return "99";
        }
    }

    private void preencherTagEndereco(Document doc, Pessoa pessoa, Element endereco) {

        if (!pessoa.getEnderecoscorreio().isEmpty()) {
            EnderecoCorreio enderecoCorreio = enderecoFacade.retornaPrimeiroEnderecoCorreioValido(pessoa);
            if (enderecoCorreio != null) {
                if (enderecoCorreio.getLogradouro() != null) {
                    endereco.setAttributeNode(criarAtributo(doc, "logradouro", Util.cortarString(enderecoCorreio.getLogradouroReduzido(), 30)));
                }
                if (enderecoCorreio.getNumero() != null) {
                    endereco.setAttributeNode(criarAtributo(doc, "numero", Util.cortarString(enderecoCorreio.getNumero(), 5).replace("'", "")));
                }
                if (enderecoCorreio.getComplemento() != null) {
                    endereco.setAttributeNode(criarAtributo(doc, "complemento", Util.cortarString(enderecoCorreio.getComplemento(), 30).replace("'", "&apos;")));
                }
                if (enderecoCorreio.getBairro() != null) {
                    endereco.setAttributeNode(criarAtributo(doc, "bairro", Util.cortarString(enderecoCorreio.getBairro(), 30)));
                }
                if (enderecoCorreio.getCep() != null) {
                    endereco.setAttributeNode(criarAtributo(doc, "cep", (enderecoCorreio.getCep().replace("-", "").length() > 8 ? enderecoCorreio.getCep().replace("-", "").substring(0, 8) : enderecoCorreio.getCep().replace("-", ""))));
                }
            }
        }
    }

    private Element criarTag(Document doc, String nomeTag, Element elementoPai) {
        Element element = doc.createElement(nomeTag);
        elementoPai.appendChild(element);
        return element;
    }

    private void preencheTagDadosPessoaisPensionista(Document doc, Element dadosPessoais, PessoaFisica pessoa) {
        dadosPessoais.setAttributeNode(criarAtributo(doc, "nome", Util.removerCaracteresEspeciais(pessoa.getNome()).replace("'", "&apos;")));
        dadosPessoais.setAttributeNode(criarAtributo(doc, "estadoCivil", pessoa.getEstadoCivil().getCodigoSiprev()));
        dadosPessoais.setAttributeNode(criarAtributo(doc, "dataNascimento", pessoa.getDataNascimentoFormatada("yyyy-MM-dd")));
        dadosPessoais.setAttributeNode(criarAtributo(doc, "sexo", pessoa.getSexo() != null ? pessoa.getSexo().getSigla() : "F"));
        dadosPessoais.setAttributeNode(criarAtributo(doc, "nomeMae", (pessoa.getMae() != null ? Util.removerCaracteresEspeciais(pessoa.getMae()).replace("'", "&apos;") : pessoa.getMae())));
        dadosPessoais.setAttributeNode(criarAtributo(doc, "nomePai", (pessoa.getPai() != null ? Util.removerCaracteresEspeciais(pessoa.getPai()).replace("'", "&apos;") : pessoa.getPai())));
    }

    private void preencheTagDadosPessoais(Document doc, VinculoFP vinculoFP, Element dadosPessoais, PessoaFisica pessoa, RegistroDeObito registroDeObito) {
        dadosPessoais.setAttributeNode(criarAtributo(doc, "nome", Util.removerCaracteresEspeciais(pessoa.getNome()).replace("'", "&apos;")));
        dadosPessoais.setAttributeNode(criarAtributo(doc, "estadoCivil", pessoa.getEstadoCivil().getCodigoSiprev()));
        dadosPessoais.setAttributeNode(criarAtributo(doc, "dataNascimento", pessoa.getDataNascimentoFormatada("yyyy-MM-dd")));

        if (pessoa.getNacionalidade() != null) {
            dadosPessoais.setAttributeNode(criarAtributo(doc, "nacionalidade", pessoa.getNacionalidade().getCodigoRaiz().toString()));
        }
        dadosPessoais.setAttributeNode(criarAtributo(doc, "sexo", pessoa.getSexo() != null ? pessoa.getSexo().getSigla() : "F"));
        dadosPessoais.setAttributeNode(criarAtributo(doc, "nomeMae", (pessoa.getMae() != null ? Util.removerCaracteresEspeciais(pessoa.getMae()).replace("'", "&apos;") : pessoa.getMae())));
        dadosPessoais.setAttributeNode(criarAtributo(doc, "nomePai", (pessoa.getPai() != null ? Util.removerCaracteresEspeciais(pessoa.getPai()).replace("'", "&apos;") : pessoa.getPai())));
        dadosPessoais.setAttributeNode(criarAtributo(doc, "dataIngressoServicoPublico", DataUtil.getDataFormatada(vinculoFP.getInicioVigencia(), "dddd-MM-dd")));
        dadosPessoais.setAttributeNode(criarAtributo(doc, "deficiente", pessoa.getDeficienteFisico() == null ? "0" : pessoa.getDeficienteFisico() ? "1" : "0"));
    }

    private void preencheTagDocumentos(Document doc, PessoaFisica pessoa, Element documentos) {
        documentos.setAttributeNode(criarAtributo(doc, "numeroCPF", pessoa.getCpf() != null ? StringUtil.removeCaracteresEspeciaisSemEspaco(pessoa.getCpf()).replace(" ", "") : ""));

        if (pessoa.getCarteiraDeTrabalho() != null && pessoa.getCarteiraDeTrabalho().getPisPasep() != null) {
            documentos.setAttributeNode(criarAtributo(doc, "numeroNIT", pessoa.getCarteiraDeTrabalho().getPisPasep().replace("-", "").replace(".", "")));
        }
        if (pessoa.getRg() != null) {
            documentos.setAttributeNode(criarAtributo(doc, "numeroRG", pessoa.getRg().getNumero()));
            if (pessoa.getRg().getUf() != null) {
                documentos.setAttributeNode(criarAtributo(doc, "ufRG", pessoa.getRg().getUf().getUf()));
            }

            if (pessoa.getRg().getDataemissao() != null) {
                documentos.setAttributeNode(criarAtributo(doc, "dataEmissaoRG", DataUtil.getDataFormatada(pessoa.getRg().getDataemissao(), "yyyy-MM-dd")));
            }

            //verificar o que deve ser feito
//            if (pessoa.getRg().getOrgaoEmissao() != null) {
//                documentos.setAttributeNode(criarAtributo(doc, "orgaoExpedidorRG", pessoa.getRg().getOrgaoEmissao()));
//            }
        }
        if (pessoa.getCarteiraDeTrabalho() != null) {
            if (pessoa.getCarteiraDeTrabalho().getNumero() != null) {
                documentos.setAttributeNode(criarAtributo(doc, "numeroCTPS", (pessoa.getCarteiraDeTrabalho().getNumero().length() > 12 ? pessoa.getCarteiraDeTrabalho().getNumero().substring(0, 12) : pessoa.getCarteiraDeTrabalho().getNumero())));
            }
            if (pessoa.getCarteiraDeTrabalho().getSerie() != null) {
                documentos.setAttributeNode(criarAtributo(doc, "serieCTPS", pessoa.getCarteiraDeTrabalho().getSerie()));
            }
            if (pessoa.getCarteiraDeTrabalho().getDataEmissao() != null) {
                documentos.setAttributeNode(criarAtributo(doc, "dataEmissaoCTPS", DataUtil.getDataFormatada(pessoa.getCarteiraDeTrabalho().getDataEmissao(), "yyyy-MM-dd")));
            }
        }

        if (pessoa.getTituloEleitor() != null) {
            if (pessoa.getTituloEleitor().getNumero() != null) {
                documentos.setAttributeNode(criarAtributo(doc, "numeroTituloEleitor", pessoa.getTituloEleitor().getNumero().trim().replace(" ", "")));
            }
            if (pessoa.getTituloEleitor().getZona() != null) {
                documentos.setAttributeNode(criarAtributo(doc, "zonaTituloEleitor", pessoa.getTituloEleitor().getZona()));
            }
            if (pessoa.getTituloEleitor().getSessao() != null) {
                documentos.setAttributeNode(criarAtributo(doc, "secaoTituloEleitor", pessoa.getTituloEleitor().getSessao().trim()));
            }
            if (pessoa.getTituloEleitor().getCidade() != null && pessoa.getTituloEleitor().getCidade().getUf() != null) {
                documentos.setAttributeNode(criarAtributo(doc, "ufTituloEleitor", pessoa.getTituloEleitor().getCidade().getUf().getUf()));
            }
        }
    }


    private void preencheTagServidor(Document doc, Element servidor, PessoaFisica pessoaFisica) {
        servidor.setAttributeNode(criarAtributo(doc, "nome", pessoaFisica.getNome()));
//        servidor.setAttributeNode(criarAtributo(doc, "numeroCPF", StringUtil.removeCaracteresEspeciaisSemEspaco(pessoaFisica.getCpf())));
//        if (pessoaFisica.getCarteiraDeTrabalho() != null && pessoaFisica.getCarteiraDeTrabalho().getPisPasep() != null) {
//            servidor.setAttributeNode(criarAtributo(doc, "numeroNIT", StringUtil.removeCaracteresEspeciaisSemEspaco(pessoaFisica.getCarteiraDeTrabalho().getPisPasep())));
//        }
//        if (pessoaFisica.getDataNascimento() != null) {
//            servidor.setAttributeNode(criarAtributo(doc, "dataNascimento", DataUtil.getDataFormatada(pessoaFisica.getDataNascimento(), "yyyy-MM-dd")));
//        }
//        if (pessoaFisica.getMae() != null) {
//            servidor.setAttributeNode(criarAtributo(doc, "nomeMae", pessoaFisica.getMae()));
//        }
    }


    private void preencheTagContato(Document doc, PessoaFisica pessoa, Element contato) {
        if (!pessoa.getTelefones().isEmpty()) {
            if (pessoa.getTelefoneValido() != null) {
                if (!pessoa.getTelefoneValido().getDDD().equals("")) {
                    contato.setAttributeNode(criarAtributo(doc, "dddTelefone", pessoa.getTelefoneValido().getDDD().replace("(", "").replace(")", "")));
                }
                if (pessoa.getTelefoneValido().getTelefone() != null) {
                    contato.setAttributeNode(criarAtributo(doc, "telefone", StringUtil.removeCaracteresEspeciaisSemEspaco(pessoa.getTelefoneValido().getSomenteTelefone())));
                }
            }
        }
        if (pessoa.getEmail() != null) {
            contato.setAttributeNode(criarAtributo(doc, "email", pessoa.getEmail()));
        }
    }

    private Attr criarAtributo(Document doc, String descricao, String valor) {
        Attr attr = doc.createAttribute(descricao);
        attr.setValue(valor);
        return attr;
    }


    public void addToZip() {
        try {
            zipFile = File.createTempFile("SIPREV", "zip");

            byte[] buffer = new byte[1024];

            FileOutputStream fout = new FileOutputStream(zipFile);
            ZipOutputStream zout = new ZipOutputStream(fout);
            for (Map.Entry<String, File> m : files.entrySet()) {

                //cria o objeto FileInputStream do File em questão
                FileInputStream fin = new FileInputStream(m.getValue());

                //coloca o nome do arquivo dentro do zip com o nome passado no parametro
                zout.putNextEntry(new ZipEntry(m.getKey()));

                //escreve o arquivo dentro do zip
                int length;
                while ((length = fin.read(buffer)) > 0) {
                    zout.write(buffer, 0, length);
                }

                //fecha o arquivo dentro do zip
                zout.closeEntry();

                //fecha o inputStream
                fin.close();
            }
            FileInputStream fis = new FileInputStream(zipFile);
            fileDownload = new DefaultStreamedContent(fis, "application/zip", "SIPREV.zip");

            //fecha o arquivo zip
            zout.close();

            //joga o arquivo zip concluido para um FileInputStream para fazer download

        } catch (IOException ioe) {
            FacesUtil.addError("Erro grave!", "Ocorreu um erro para gerar o arquivo ZIP do Siprev, comunique o administrador.");
        }
    }

    public DefaultStreamedContent getFileDownload() {
        if (fileDownload != null) {
            return fileDownload;
        }
        FacesUtil.addOperacaoNaoPermitida("Não existe arquivo do SIPRED gerado para download.");
        return null;
    }

    public void setFileDownload(DefaultStreamedContent fileDownload) {
        this.fileDownload = fileDownload;
    }


    public Converter getConverterPessoa() {
        if (converterPessoa == null) {
            converterPessoa = new ConverterAutoComplete(Pessoa.class, pessoaFisicaFacade);
        }
        return converterPessoa;
    }

    public List<PessoaFisica> completaPessoa(String parte) {
        return contratoFPFacade.listaPessoasComContratosVigentes(parte);
    }

    public List<SelectItem> getRetornaMes() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, " "));
        for (Mes mes : Mes.values()) {
            retorno.add(new SelectItem(mes, mes.getDescricao()));
        }
        return retorno;
    }


    private void criarXML(Document doc, Transformer transformer, String nomeArquivo) throws TransformerException {
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(file);
        transformer.transform(source, result);
        files.put(nomeArquivo, file);
    }

    private Transformer encodingStandaloneXML() throws TransformerConfigurationException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
        transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        return transformer;
    }

    private Element criarCabecalhoXML(Document doc) {
        Element namaspace = doc.createElementNS("http://www.dataprev.gov.br/siprev", "ns2:siprev");
        doc.appendChild(namaspace);
        Element ente = criarTag(doc, "ente", namaspace);
        preencheTagEnte(doc, ente);
        ente.setAttributeNode(criarAtributo(doc, "siafi", selecionado.getCodigo()));
        ente.setAttributeNode(criarAtributo(doc, "cnpj", pessoaJuridicaUnidadeGestora.getCnpj().replace("/", "").replace(".", "").replace("-", "")));
        return namaspace;
    }

    private Document getDocument(String arquivo) throws ParserConfigurationException, IOException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        file = File.createTempFile(arquivo, "xml");
        Document doc = docBuilder.newDocument();
        doc.setXmlStandalone(true);
//        doc.setxm
        return doc;
    }

    public enum OperacaoRegistro {

        INCLUSAO("Inclusão", "I"),
        ALTERACAO("Alteração", "A"),
        EXCLUSAO("Exclusão", "E");

        private String descricao;
        private String codigo;

        private OperacaoRegistro(String descricao, String codigo) {
            this.descricao = descricao;
            this.codigo = codigo;
        }

        public String getDescricao() {
            return descricao;
        }

        public void setDescricao(String descricao) {
            this.descricao = descricao;
        }

        public String getCodigo() {
            return codigo;
        }

        public void setCodigo(String codigo) {
            this.codigo = codigo;
        }
    }

    public static class ContratoFPeItemFichaFinanceira {
        private ItemFichaFinanceiraFP itemFichaFinanceiraFP;
        private ContratoFP contratoFP;

        public ItemFichaFinanceiraFP getItemFichaFinanceiraFP() {
            return itemFichaFinanceiraFP;
        }

        public void setItemFichaFinanceiraFP(ItemFichaFinanceiraFP itemFichaFinanceiraFP) {
            this.itemFichaFinanceiraFP = itemFichaFinanceiraFP;
        }

        public ContratoFP getContratoFP() {
            return contratoFP;
        }

        public void setContratoFP(ContratoFP contratoFP) {
            this.contratoFP = contratoFP;
        }
    }

    public static class PessoaFisicaSiprev {
        private String nome;
        private String cpf;
        private String rg;
        private Date dataNascimento;
        private String nomeMae;
        private String pISPASEP;

        public String getNome() {
            return nome;
        }

        public void setNome(String nome) {
            this.nome = nome;
        }

        public String getCpf() {
            return cpf;
        }

        public void setCpf(String cpf) {
            this.cpf = cpf;
        }

        public String getRg() {
            return rg;
        }

        public void setRg(String rg) {
            this.rg = rg;
        }

        public Date getDataNascimento() {
            return dataNascimento;
        }

        public void setDataNascimento(Date dataNascimento) {
            this.dataNascimento = dataNascimento;
        }

        public String getNomeMae() {
            return nomeMae;
        }

        public void setNomeMae(String nomeMae) {
            this.nomeMae = nomeMae;
        }

        public String getpISPASEP() {
            return pISPASEP;
        }

        public void setpISPASEP(String pISPASEP) {
            this.pISPASEP = pISPASEP;
        }
    }
}
