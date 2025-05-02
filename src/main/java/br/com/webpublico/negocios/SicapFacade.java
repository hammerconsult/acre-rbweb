package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.arquivos.TipoSicapArquivo;
import br.com.webpublico.entidadesauxiliares.DependenciasDirf;
import br.com.webpublico.enums.SicapModalidadeArquivo;
import br.com.webpublico.enums.SicapTipoArquivo;
import br.com.webpublico.enums.TipoFolhaDePagamento;
import br.com.webpublico.enums.TipoFolhaDePagamentoSicap;
import br.com.webpublico.enums.rh.GrauEscolaridadeSICAP;
import br.com.webpublico.enums.rh.TipoVinculoSicap;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import org.apache.commons.io.FileUtils;
import org.primefaces.model.DefaultStreamedContent;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Criado por Mateus
 * Data: 24/05/2016.
 */
@Stateless
public class SicapFacade extends AbstractFacade<Sicap> {

    public static final String DECIMO_13 = "13";
    public static final int TAMANHO_CAMPO_PIS = 11;
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private EntidadeFacade entidadeFacade;
    @EJB
    private SefipAcompanhamentoFacade sefipAcompanhamentoFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private PessoaFisicaFacade pessoaFisicaFacade;
    @EJB
    private ArquivoFacade arquivoFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ItemEntidadeDPContasFacade itemEntidadeDPContasFacade;
    @EJB
    private FuncaoGratificadaFacade funcaoGratificadaFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    private Sicap sicap;
    private DependenciasDirf dependenciasDirf;
    private File zipFile;
    private DefaultStreamedContent fileDownload;
    private Map<String, File> files;
    private Map<String, File> arquivos;
    private boolean existeAposentadosInstituidorPensao ;

    public SicapFacade() {
        super(Sicap.class);
    }

    @Override
    public Sicap recuperar(Object id) {
        Sicap sicap = em.find(Sicap.class, id);
        if (sicap.getArquivo() != null) {
            sicap.getArquivo().getPartes().size();
        }
        sicap.getSicapTipoArquivo().size();
        return sicap;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public Sicap buscarSicapEventualPorTipoArquivo(SicapTipoArquivo sicapTipoArquivo) {
        String sql = " select sic.* from sicap sic " +
            " where sic.sicapModalidadeArquivo = :modalidade " +
            " and sic.sicapTipoArquivo = :tipoArquivo ";
        Query q = em.createNativeQuery(sql, Sicap.class);
        q.setParameter("modalidade", SicapModalidadeArquivo.EVENTUAL.name());
        q.setParameter("tipoArquivo", sicapTipoArquivo.name());
        if (!q.getResultList().isEmpty()) {
            return (Sicap) q.getResultList().get(0);
        }
        return null;
    }

    public Sicap buscarSicapPeriodicoPorTipoArquivoAndMesAndExercicio(Sicap selecionado) {
        String sql = " select sic.* from sicap sic " +
            " where sic.mes = :mes " +
            " and sic.exercicio_id = :exercicio and sic.entidade_id = :entidade ";
        Query q = em.createNativeQuery(sql, Sicap.class);
        q.setParameter("mes", selecionado.getMes().name());
        q.setParameter("exercicio", selecionado.getExercicio().getId());
        q.setParameter("entidade", selecionado.getEntidade().getId());
        if (!q.getResultList().isEmpty()) {
            List<Sicap> sicaps = q.getResultList();
            Sicap b = validarSicapComTiposDeArquivo(sicaps, selecionado.getSicapTipoArquivo());
            if (b != null) {
                b = validarSubFolhaSicap(sicaps, selecionado.getGruposRecursos());
            }
            boolean hasFolha = false;
            if (!selecionado.getSicapFolhasPagamentos().isEmpty()) {
                for (SicapFolhaPagamento sfp : selecionado.getSicapFolhasPagamentos()) {
                    if (hasFolhaDePagamento(sicaps, sfp)) {
                        hasFolha = true;
                        break;
                    }
                }
            }
            if (hasFolha) {
                return b;
            }
        }
        return null;
    }

    private boolean hasFolhaDePagamento(List<Sicap> sicaps, SicapFolhaPagamento sicapFolhaPagamento) {
        for (Sicap s : sicaps) {
            for (SicapFolhaPagamento sfp : s.getSicapFolhasPagamentos()) {
                if (sfp.getFolhaDePagamento().equals(sicapFolhaPagamento.getFolhaDePagamento())) {
                    return true;
                }
            }
        }
        return false;
    }


    private Sicap validarSubFolhaSicap(List<Sicap> sicaps, List<GrupoRecursoFP> gruposRecursoFPs) {
        Sicap retorno = null;
        for (Sicap sicap1 : sicaps) {
            sicap1.getGrupos().size();
            List<GrupoRecursoFP> lista = new ArrayList<>(gruposRecursoFPs);
            if (sicap1.getGruposRecursos().containsAll(gruposRecursoFPs)) {
                return sicap1;
            }
        }
        return retorno;
    }

    private Sicap validarSicapComTiposDeArquivo(List<Sicap> sicaps, List<TipoSicapArquivo> tipos) {
        for (Sicap sicap1 : sicaps) {
            sicap1.getSicapTipoArquivo().size();
            List<TipoSicapArquivo> lista = new ArrayList<>(tipos);
            for (TipoSicapArquivo tipoSicapArquivo : sicap1.getSicapTipoArquivo()) {
                for (Iterator<TipoSicapArquivo> iter = lista.iterator(); iter.hasNext(); ) {
                    TipoSicapArquivo tipo = iter.next();
                    if (tipo.getSicapTipoArquivo().equals(tipoSicapArquivo.getSicapTipoArquivo())) {
                        iter.remove();
                    }
                }
            }
            if (lista.isEmpty()) {
                return sicap1;
            }

        }
        return null;
    }

    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void gerarArquivo(Sicap selecionado, DependenciasDirf dependenciasDirf) throws ParserConfigurationException, IOException, TransformerException {
        files = new HashMap<>();
        arquivos = new HashMap<>();
        sicap = selecionado;
        this.dependenciasDirf = dependenciasDirf;
        List<File> arquivos = new ArrayList<>();
        inicializarGeracaoArquivo();
        for (TipoSicapArquivo tipo : selecionado.getSicapTipoArquivo()) {
            File file = criarArquivoXML(tipo.getSicapTipoArquivo().getHeaderArquivo());
            Document doc = criarDocument();
            Element registro = criarElemento(doc, tipo.getSicapTipoArquivo());
            adicionarInformacoes(doc, registro, tipo.getSicapTipoArquivo(), selecionado.getTipoFolhaDePagamentoSicap());
            File arquivo = finalizarGeracaoArquivo(doc, file, tipo.getSicapTipoArquivo());
            arquivos.add(arquivo);
            files = new HashMap<>();
        }
        finalizarGeracaoSicap(arquivos);

    }

    private void validarXmlComXsd(File arquivoXml, InputStream arquivoXsd) {
        try {
            File schemaFile = File.createTempFile("temFileXsd", ".xsd");
            FileUtils.copyInputStreamToFile(arquivoXsd, schemaFile);

            Source xmlFile = new StreamSource(arquivoXml);
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            try {
                Schema schema = schemaFactory.newSchema(schemaFile);
                Validator validator = schema.newValidator();


                validator.validate(xmlFile);
                logger.debug(xmlFile.getSystemId() + " is valid");
            } catch (SAXException e) {
                logger.error(xmlFile.getSystemId() + " is NOT valid reason:" + e);
                adicionarLogTipoErro(dependenciasDirf, "Documento XML não é válido, erro:" + e);
            } catch (IOException e) {
                logger.error("Erro ao validar xml", e);
            }
        } catch (Exception e) {
            logger.error("Erro ", e);
        }
    }

    private Element criarElemento(Document doc, SicapTipoArquivo sicapTipoArquivo) {
        Element header = criarTagHeader(doc, "sicap", sicapTipoArquivo);
        return criarTag(doc, sicapTipoArquivo.getGrupoArquivo(), header);
    }

    private void adicionarInformacoes(Document doc, Element registro, SicapTipoArquivo tipo, TipoFolhaDePagamentoSicap tipoFolhaDePagamentoSicap) {
        adicionarLogTipoTodos(dependenciasDirf, "RECUPERANDO " + tipo.getDescricao().toUpperCase() + " PARA A GERAÇÃO DO ARQUIVO.");
        switch (tipo) {
            case CONTRACHEQUE:
                adicionarContraCheque(doc, registro, tipo, tipoFolhaDePagamentoSicap);
                break;
            case RESCISAO:
                adicionarRescisao(doc, registro, tipo);
                break;
            case CARGOS:
                adicionarCargos(doc, registro, tipo);
                break;
            case HISTORICO_FUNCIONAL:
                adicionarHistoricoFuncional(doc, registro, tipo);
                break;
            case PENSIONISTAS:
                //Aqui há uma inversão: No XML o Pensionista equivale à nossa Pensão no sistema e a Pensão no XML equivale à nosso Pensionista no sistema
                adicionarPensionistas(doc, registro, tipo);
                break;
            case SERVIDORES:
                adicionarServidores(doc, registro, tipo);
                break;
            case TABELAS_DE_VENCIMENTO:
                adicionarTabelasDeVencimentos(doc, registro, tipo);
                break;
            case TIPOS_DE_FOLHA:
                adicionarTiposDeFolha(doc, registro, tipo, tipoFolhaDePagamentoSicap);
                break;
            case UNIDADES_DE_LOTACAO:
                adicionarUnidadesLotacao(doc, registro, tipo);
                break;
            case VERBAS:
                adicionarVerbas(doc, registro, tipo);
                break;
        }
    }

    private void inicializarGeracaoArquivo() {
        sefipAcompanhamentoFacade.salvarNovo(sicap);
        adicionarLogTipoTodos(dependenciasDirf, "PREPARANDO ARQUIVO PARA GERAÇÃO DOS DADOS.");
        dependenciasDirf.setTotalCadastros(1);
    }

    private void finalizarGeracaoSicap(List<File> arquivos) {
        try {
            adicionarLogTipoTodos(dependenciasDirf, "CRIANDO ARQUIVO PARA DOWNLOAD.");

            createMasterFile(arquivos);

            dependenciasDirf.incrementarContador();
            dependenciasDirf.setContadorProcessos(dependenciasDirf.getTotalCadastros());

            criarArquivo(sicap);
            sicap = (Sicap) sefipAcompanhamentoFacade.salvar(sicap);
            adicionarLogTipoSucesso(dependenciasDirf, "ARQUIVO GERADO E SALVO COM SUCESSO!");
        } catch (Exception e) {
            e.printStackTrace();
            adicionarLogTipoErro(dependenciasDirf, "NÃO FOI POSSÍVEL GERAR O ARQUIVO. ENTRE EM CONTATO COM O SUPORTE PARA MAIORES INFORMAÇÕES. DETALHES DO ERRO: " + e);
        }
        dependenciasDirf.pararProcessamento();
    }

    private File finalizarGeracaoArquivo(Document doc, File file, SicapTipoArquivo tipo) {
        try {
            adicionarLogTipoTodos(dependenciasDirf, "CRIANDO ARQUIVO PARA DOWNLOAD.");
            transformarFile(doc, file, tipo);
            File arquivo = addToZip(tipo);
            arquivos.put("sicap-" + tipo.getHeaderArquivo() + ".zip", arquivo);
            dependenciasDirf.incrementarContador();
            dependenciasDirf.setContadorProcessos(Integer.parseInt("" + dependenciasDirf.getTotalCadastros()));
            return arquivo;
        } catch (Exception e) {
            e.printStackTrace();
            adicionarLogTipoErro(dependenciasDirf, "NÃO FOI POSSÍVEL GERAR O ARQUIVO. ENTRE EM CONTATO COM O SUPORTE PARA MAIORES INFORMAÇÕES. DETALHES DO ERRO: " + e);
        }
        return null;
    }

    private void transformarFile(Document doc, File file, SicapTipoArquivo tipo) throws TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
        transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(file);
        transformer.transform(source, result);

        InputStream resourceAsStream = getClass().getResourceAsStream(tipo.getCaminhoXsd());
        validarXmlComXsd(file, resourceAsStream);
        files.put(tipo.getHeaderArquivo() + ".xml", file);
    }

    private File criarArquivoXML(String nomeArquivo) throws IOException {
        File file = File.createTempFile(nomeArquivo, ".xml");
        return file;
    }

    private Document criarDocument() throws ParserConfigurationException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.newDocument();
        doc.setXmlStandalone(true);
        return doc;
    }

    public File addToZip(SicapTipoArquivo tipo) {
        try {
            zipFile = File.createTempFile("sicap-" + tipo.getHeaderArquivo(), ".zip");
            byte[] buffer = new byte[1024];
            FileOutputStream fout = new FileOutputStream(zipFile);
            ZipOutputStream zout = new ZipOutputStream(fout);
            for (Map.Entry<String, File> m : files.entrySet()) {
                FileInputStream fin = new FileInputStream(m.getValue());
                zout.putNextEntry(new ZipEntry(m.getKey()));
                int length;
                while ((length = fin.read(buffer)) > 0) {
                    zout.write(buffer, 0, length);
                }
                zout.closeEntry();
                fin.close();
            }
            FileInputStream fis = new FileInputStream(zipFile);
            //fileDownload = new DefaultStreamedContent(fis, "application/zip", "sicap-" + tipo.getHeaderArquivo() + ".zip");
            zout.close();
            return zipFile;
        } catch (IOException ioe) {
            FacesUtil.addError("Erro grave!", "Ocorreu um erro para gerar o arquivo ZIP do Sicap, comunique o administrador. Detalhe: " + ioe.getMessage());
        }
        return null;
    }

    public File createMasterFile(List<File> zipFiles) {
        try {
            zipFile = File.createTempFile("sicap-" + Util.dateToString(sicap.getUltimoDiaDoMes()), "zip");
            byte[] buffer = new byte[1024];
            FileOutputStream fout = new FileOutputStream(zipFile);
            ZipOutputStream zout = new ZipOutputStream(fout);
            /*            for (File file : zipFiles) {*/


            for (Map.Entry<String, File> m : arquivos.entrySet()) {
                FileInputStream fin = new FileInputStream(m.getValue());
                zout.putNextEntry(new ZipEntry(m.getKey()));
                int length;
                while ((length = fin.read(buffer)) > 0) {
                    zout.write(buffer, 0, length);
                }
                zout.closeEntry();
                fin.close();
            }
            FileInputStream fis = new FileInputStream(zipFile);
            fileDownload = new DefaultStreamedContent(fis, "application/zip", "sicap-" + Util.dateToString(sicap.getUltimoDiaDoMes()) + ".zip");
            zout.close();
            return zipFile;
        } catch (IOException ioe) {
            FacesUtil.addError("Erro grave!", "Ocorreu um erro para gerar o arquivo ZIP do Sicap, comunique o administrador. Detalhe: " + ioe.getMessage());
        }
        return null;
    }

    private void criarArquivo(Sicap s) throws IOException {
        if (fileDownload != null) {
            Arquivo arquivo = new Arquivo();
            arquivo.setMimeType(arquivoFacade.getMimeType(fileDownload.getName()));
            arquivo.setNome(fileDownload.getName());
            arquivo.setTamanho(Long.valueOf(fileDownload.getStream().available()));
            arquivo.setDescricao(fileDownload.getName());
            arquivo.setInputStream(fileDownload.getStream());
            arquivo = criarPartesDoArquivo(arquivo);
            s.setArquivo(arquivo);
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
        arquivo = (Arquivo) sefipAcompanhamentoFacade.salvar(arquivo);
        return arquivo;
    }

    private Attr criarAtributo(Document doc, String descricao, String valor) {
        Attr attr = doc.createAttribute(descricao);
        attr.setValue(valor);
        return attr;
    }

    private Element criarTagHeader(Document doc, String nomeTag, SicapTipoArquivo sicapTipoArquivo) {
        Element element = doc.createElement(nomeTag);
        doc.appendChild(element);
        element.setAttributeNode(criarAtributo(doc, "arquivo", sicapTipoArquivo.getHeaderArquivo()));
        return element;
    }

    private Element criarTag(Document doc, String nomeTag, Element elementoPai) {
        Element element = doc.createElement(nomeTag);
        elementoPai.appendChild(element);
        return element;
    }


    private void adicionarLogTipoTodos(DependenciasDirf dependenciasDirf, String mensagem) {
        dependenciasDirf.adicionarLog(DependenciasDirf.TipoLog.TODOS, mensagem);
    }

    private void adicionarLogTipoSucesso(DependenciasDirf dependenciasDirf, String mensagem) {
        dependenciasDirf.adicionarLog(DependenciasDirf.TipoLog.SUCESSO, mensagem);
    }

    private void adicionarLogTipoErro(DependenciasDirf dependenciasDirf, String mensagem) {
        dependenciasDirf.adicionarLog(DependenciasDirf.TipoLog.ERRO, mensagem);
    }

    private void adicionarCargos(Document doc, Element element, SicapTipoArquivo tipo) {
        List<Object[]> cargos = buscarCargos();
        dependenciasDirf.setTotalCadastros(cargos.size());
        for (Object[] obj : cargos) {
            atualizarContador(tipo);
            Element dados = criarElementoParaDados(doc, element, tipo);
            preencheTagDadosCargo(doc, dados, obj);
            dependenciasDirf.incrementarContador();
        }
    }

    private void adicionarHistoricoFuncional(Document doc, Element registro, SicapTipoArquivo tipo) {
        List<Object[]> historicosFuncionais = getHistoricosFuncionais();
        dependenciasDirf.setTotalCadastros(historicosFuncionais.size());
        for (Object[] historicoFuncional : historicosFuncionais) {
            atualizarContador(tipo);
            Element dados = criarElementoParaDados(doc, registro, tipo);
            preencherTagDadosHistoricoFuncional(doc, dados, historicoFuncional);
            dependenciasDirf.incrementarContador();
        }
    }

    private void adicionarPensionistas(Document doc, Element element, SicapTipoArquivo tipo) {
        List<Object[]> pensionistas = buscarPensionistas();
        dependenciasDirf.setTotalCadastros(pensionistas.size());
        for (Object[] obj : pensionistas) {
            atualizarContador(tipo);
            Element dados = criarElementoParaDados(doc, element, tipo);
            preencherTagDadosPensionistas(doc, obj, dados);
            adicionarPensao(doc, ((BigDecimal) obj[0]).longValue(), dados);
            dependenciasDirf.incrementarContador();
        }

    }


    private void adicionarVerbas(Document doc, Element element, SicapTipoArquivo tipo) {
        List<Object[]> verbas = buscarVerbas();
        dependenciasDirf.setTotalCadastros(verbas.size());
        for (Object[] obj : verbas) {
            atualizarContador(tipo);
            Element dados = criarElementoParaDados(doc, element, tipo);
            preencheTagDadosVerbas(doc, dados, obj);
            dependenciasDirf.incrementarContador();
        }
    }

    private void adicionarContraCheque(Document doc, Element element, SicapTipoArquivo tipo, TipoFolhaDePagamentoSicap tipoFolhaDePagamentoSicap) {
        List<Object[]> holerites = buscarContraCheques(tipoFolhaDePagamentoSicap);
        int totalHoleritesComVerbas = 0;
        preencheTagGrupoContraCheque(doc, element);
        for (Object[] obj : holerites) {
            List<Object[]> verbasContraCheque = buscarVerbasDoContraCheques(((BigDecimal) obj[0]).longValue());
            if (verbasContraCheque != null && !verbasContraCheque.isEmpty()) {
                atualizarContador(tipo);
                Element dados = criarElementoParaDados(doc, element, tipo);
                preencheTagDadosContraCheque(doc, dados, obj);

                Element beneficiario = criarElementoParaDados(doc, "beneficiario", dados);
                preencheTagDadosBeneficiarioContraCheque(doc, beneficiario, obj);

                adicionarVerbasContraChequeOuRescisao(doc, dados, ((BigDecimal) obj[0]).longValue());

                dependenciasDirf.incrementarContador();
                dependenciasDirf.setTotalCadastros(holerites.size());
                totalHoleritesComVerbas++;
            }
        }
        dependenciasDirf.setTotalCadastros(totalHoleritesComVerbas);
    }

    private void adicionarRescisao(Document doc, Element element, SicapTipoArquivo tipo) {
        List<Object[]> rescisoes = buscarRescisoes();
        dependenciasDirf.setTotalCadastros(rescisoes.size());
        for (Object[] obj : rescisoes) {
            atualizarContador(tipo);

            Element dados = criarElementoParaDados(doc, element, tipo);
            preencherTagDadosRescisao(doc, dados, obj);

            Element beneficiario = criarElementoParaDados(doc, "beneficiario", dados);
            preencherTagDadosBeneficiarioRescisao(doc, beneficiario, obj);

            adicionarVerbasContraChequeOuRescisao(doc, dados, ((BigDecimal) obj[0]).longValue());

            dependenciasDirf.incrementarContador();
        }
    }

    private void adicionarServidores(Document doc, Element element, SicapTipoArquivo tipo) {

        List<Object[]> servidores = buscarServidores();
        dependenciasDirf.setTotalCadastros(servidores.size());
        for (Object[] obj : servidores) {
            atualizarContador(tipo);
            Element dados = criarElementoParaDados(doc, element, tipo);
            preencheTagDadosServidor(doc, dados, obj);
            adicionarVinculos(doc, dados, ((BigDecimal) obj[0]).longValue(), ((String) obj[7]));
            dependenciasDirf.incrementarContador();
        }
        existeAposentadosInstituidorPensao = false;
    }

    private void adicionarUnidadesLotacao(Document doc, Element element, SicapTipoArquivo tipo) {
        List<Object[]> unidades = buscarUnidadesLotacao();
        dependenciasDirf.setTotalCadastros(unidades.size());
        for (Object[] obj : unidades) {
            atualizarContador(tipo);
            Element dados = criarElementoParaDados(doc, element, tipo);
            preencheTagDadosUnidadesLotacao(doc, dados, obj);
            dependenciasDirf.incrementarContador();
        }
    }

    private void adicionarVinculos(Document doc, Element element, Long idPessoa, String matricula) {
        List<Object[]> vinculos;
            vinculos = buscarVinculos(idPessoa, matricula);
            if (vinculos.isEmpty() || existeAposentadosInstituidorPensao) { //servidor aposentado ou nao encontrou vinculo que se adequa a modalidade de contrato prestador de serviço
                vinculos.addAll(buscarVinculosAposentadosAndPensionistas(idPessoa, matricula));
            }

        for (Object[] obj : vinculos) {
            Element dados = criarElementoParaDados(doc, "vinculo", element);
            preencheTagDadosVinculo(doc, dados, obj);
        }
    }

    private void adicionarPensao(Document doc, Long idPensao, Element element) {
        List<Object[]> pensoes = buscarPensoes(idPensao);
        for (Object[] obj : pensoes) {
            Element dados = criarElementoParaDados(doc, "pensao", element);
            preencherTagDadosPensao(doc, obj, dados);
        }
    }


    private void adicionarVerbasContraChequeOuRescisao(Document doc, Element element, Long idFicha) {
        List<Object[]> verbasContraCheque = buscarVerbasDoContraCheques(idFicha);
        if (verbasContraCheque != null && !verbasContraCheque.isEmpty()) {
            Element verbas = criarElementoParaDados(doc, "verbas", element);
            for (Object[] obj : verbasContraCheque) {
                Element dados = criarElementoParaDados(doc, "verba", verbas);
                preencherTagDadosVerbasContraChequeOuRescisao(doc, dados, obj);
            }
        }
    }

    private void adicionarTiposDeFolha(Document doc, Element element, SicapTipoArquivo tipo, TipoFolhaDePagamentoSicap tipoFolhaDePagamentoSicap) {
        List<TipoFolhaDePagamento> tiposDeFolha = Lists.newArrayList();

        if (TipoFolhaDePagamentoSicap.MENSAL.equals(tipoFolhaDePagamentoSicap)) {
            tiposDeFolha = TipoFolhaDePagamento.getFolhasPorPrioridadeDeUso();
            tiposDeFolha.remove(TipoFolhaDePagamento.SALARIO_13);
        } else {
            tiposDeFolha.add(TipoFolhaDePagamento.SALARIO_13);
        }
        dependenciasDirf.setTotalCadastros(tiposDeFolha.size());
        for (TipoFolhaDePagamento obj : tiposDeFolha) {
            atualizarContador(tipo);
            Element dados = criarElementoParaDados(doc, element, tipo);
            dados.setAttributeNode(criarAtributo(doc, "codigo", transformarCampoEmString(obj.getCodigo())));
            dados.setAttributeNode(criarAtributo(doc, "descricao", obj.getDescricao()));
            dependenciasDirf.incrementarContador();
        }
    }

    private void adicionarTabelasDeVencimentos(Document doc, Element element, SicapTipoArquivo tipo) {
        List<Object[]> tabelas = buscarTabelas();
        dependenciasDirf.setTotalCadastros(tabelas.size());
        for (Object[] obj : tabelas) {
            atualizarContador(tipo);
            Element dados = criarElementoParaDados(doc, element, tipo);
            preencheTagDadosTabelaDeVencimento(doc, dados, obj);
            adicionarNiveis(doc, dados, ((BigDecimal) obj[0]).longValue());
            adicionarCargosDaTabelaDeVencimento(doc, dados, ((BigDecimal) obj[0]).longValue());
            dependenciasDirf.incrementarContador();
        }
    }

    private void adicionarNiveis(Document doc, Element element, Long idTabela) {
        List<Object[]> vinculos = buscarNiveis(idTabela);
        Element niveis = criarElementoParaDados(doc, "niveis", element);
        for (Object[] obj : vinculos) {
            Element dados = criarElementoParaDados(doc, "nivel", niveis);
            preencheTagDadosNivel(doc, dados, obj);
        }
    }

    private void adicionarCargosDaTabelaDeVencimento(Document doc, Element element, Long idTabela) {
        List<BigDecimal> vinculos = buscarCargosDaTabelaDeVencimento(idTabela);
        Element cargos = criarElementoParaDados(doc, "cargos", element);
        for (BigDecimal obj : vinculos) {
            Element dados = criarElementoParaDados(doc, "cargo", cargos);
            preencheTagDadosCargosDaTabelaDeVencimento(doc, dados, obj.toString());
        }
    }

    private Element criarElementoParaDados(Document doc, Element element, SicapTipoArquivo tipo) {
        return criarTag(doc, tipo.getLinhaArquivo(), element);
    }

    private Element criarElementoParaGrupo(Document doc, Element element, SicapTipoArquivo tipo) {
        return criarTag(doc, tipo.getGrupoArquivo(), element);
    }

    private Element criarElementoParaDados(Document doc, String descricaoTag, Element element) {
        return criarTag(doc, descricaoTag, element);
    }

    private void atualizarContador(SicapTipoArquivo tipo) {
        adicionarLogTipoTodos(dependenciasDirf, "<b>" + (dependenciasDirf.getContadorProcessos() + 1) + " - GERANDO REGISTROS DE: " + tipo.getDescricao().toUpperCase() + "</b>");
    }

    private void preencheTagDadosCargo(Document doc, Element element, Object[] cargo) {
        if (!isCampoNullOuVazio(cargo[0])) {
            element.setAttributeNode(criarAtributo(doc, "codigo", transformarCampoEmString(cargo[0])));
        }
        if (!isCampoNullOuVazio(cargo[1])) {
            element.setAttributeNode(criarAtributo(doc, "nome", transformarCampoEmString(cargo[1])));
        }
        element.setAttributeNode(criarAtributo(doc, "cargaHorariaMensal", (cargo[2] == null ? "0" : transformarCampoEmString(cargo[2]))));
        element.setAttributeNode(criarAtributo(doc, "intervaloPromocao", (cargo[3] == null ? "0" : transformarCampoEmString(cargo[3]))));
        element.setAttributeNode(criarAtributo(doc, "escolaridade", (cargo[4] == null ? "9" : GrauEscolaridadeSICAP.valueOf((String) cargo[4]).getCodigo())));

        element.setAttributeNode(criarAtributo(doc, "tipo", (cargo[6] == null ? "" : transformarCampoEmString(cargo[6]))));
        if (!isCampoNullOuVazio(cargo[7])) {
            element.setAttributeNode(criarAtributo(doc, "dataCriacao", transformarCampoEmString(cargo[7])));
        }
        element.setAttributeNode(criarAtributo(doc, "situacao", (cargo[8] == null ? "" : transformarCampoEmString(cargo[8]))));
        if (!isCampoNullOuVazio(cargo[9])) {
            element.setAttributeNode(criarAtributo(doc, "dataExtincao", transformarCampoEmString(cargo[9])));
        }
        if (!isCampoNullOuVazio(cargo[10])) {
            element.setAttributeNode(criarAtributo(doc, "subGrupoClassificacaoFuncional", transformarCampoEmString(cargo[10])));
        }
    }

    private void preencherTagDadosHistoricoFuncional(Document doc, Element element, Object[] historicoFuncional) {
        Object cpf = historicoFuncional[0];
        Object matricula = historicoFuncional[1];
        Object situacao = historicoFuncional[2];
        Object dataOcorrencia = historicoFuncional[3];

        if (isCampoNullOuVazio(cpf)) {
            adicionarLogTipoErro(dependenciasDirf, "A Pessoa da Matrícula: " + transformarCampoEmString(matricula) + " não tem 'CPF' informado!");
        } else {
            element.setAttributeNode(criarAtributo(doc, "cpf", transformarCampoEmString(cpf)));
        }
        element.setAttributeNode(criarAtributo(doc, "matricula", transformarCampoEmString(matricula)));
        if (isCampoNullOuVazio(situacao)) {
            adicionarLogTipoErro(dependenciasDirf, "A Matrícula: " + transformarCampoEmString(matricula) + " não tem 'situação'!");
        } else {
            element.setAttributeNode(criarAtributo(doc, "situacao", transformarCampoEmString(situacao)));
        }
        if (!isCampoNullOuVazio(dataOcorrencia)) {
            element.setAttributeNode(criarAtributo(doc, "dataOcorrencia", transformarCampoEmString(dataOcorrencia)));
        }
    }

    private void preencheTagDadosVerbas(Document doc, Element element, Object[] verba) {
        element.setAttributeNode(criarAtributo(doc, "codigo", transformarCampoEmString(verba[0])));
        element.setAttributeNode(criarAtributo(doc, "descricao", transformarCampoEmString(verba[1])));
        if (!isCampoNullOuVazio(verba[2])) {
            element.setAttributeNode(criarAtributo(doc, "baseLegal", transformarCampoEmString(verba[2])));
        }
        element.setAttributeNode(criarAtributo(doc, "basePrevidencia", transformarCampoEmString(verba[3])));
        element.setAttributeNode(criarAtributo(doc, "baseIRPF", transformarCampoEmString(verba[4])));
        element.setAttributeNode(criarAtributo(doc, "baseFGTS", transformarCampoEmString(verba[5])));
        element.setAttributeNode(criarAtributo(doc, "natureza", transformarCampoEmString(verba[6])));
        if (isCampoNullOuVazio(verba[7])) {
            adicionarLogTipoErro(dependenciasDirf, "A Verba: " + transformarCampoEmString(verba[0]) + " está com o campo 'Tipo de Lançamento' não informado!");
        } else {
            element.setAttributeNode(criarAtributo(doc, "tipoReferencia", transformarCampoEmString(verba[7])));
        }
        element.setAttributeNode(criarAtributo(doc, "compoeVencimentoPadrao", transformarCampoEmString(verba[8])));
        if (!isCampoNullOuVazio(verba[9])) {
            element.setAttributeNode(criarAtributo(doc, "categoriaEconomica", transformarCampoEmString(verba[9])));
        }
        if (!isCampoNullOuVazio(verba[10])) {
            element.setAttributeNode(criarAtributo(doc, "grupoNaturezaDespesa", transformarCampoEmString(verba[10])));
        }
        if (!isCampoNullOuVazio(verba[11])) {
            element.setAttributeNode(criarAtributo(doc, "modalidadeAplicacao", transformarCampoEmString(verba[11])));
        }
        if (!isCampoNullOuVazio(verba[12])) {
            element.setAttributeNode(criarAtributo(doc, "elementoDespesa", transformarCampoEmString(verba[12])));
        }
        if (!isCampoNullOuVazio(verba[13])) {
            element.setAttributeNode(criarAtributo(doc, "subGrupoClassificacaoVerba", transformarCampoEmString(verba[13])));
        }
    }

    private void preencherTagDadosPensionistas(Document doc, Object[] pensionista, Element element) {
        if (isCampoNullOuVazio(pensionista[1])) {
            adicionarLogTipoErro(dependenciasDirf, "O Pensionista está com o campo 'Nome' não informado!");
        } else {
            element.setAttributeNode(criarAtributo(doc, "nome", transformarCampoEmString(pensionista[1] == null ? "" : transformarCampoEmString(pensionista[1]))));
        }
        if (isCampoNullOuVazio(pensionista[2])) {
            adicionarLogTipoErro(dependenciasDirf, "O Pensionista: " + pensionista[1] + " está com o campo 'CPF' não informado!");
        } else {
            element.setAttributeNode(criarAtributo(doc, "cpf", transformarCampoEmString(pensionista[2])));
        }
        if (isCampoNullOuVazio(pensionista[3])) {
            adicionarLogTipoErro(dependenciasDirf, "O Pensionista: " + pensionista[1] + " está com o campo 'Data Nascimento' não informado!");
        } else {
            element.setAttributeNode(criarAtributo(doc, "dataNascimento", transformarCampoEmString(pensionista[3])));
        }
        if (isCampoNullOuVazio(pensionista[4])) {
            adicionarLogTipoErro(dependenciasDirf, "O Pensionista: " + pensionista[1] + " está com o campo 'Nome Mãe' não informado!");
        } else {
            element.setAttributeNode(criarAtributo(doc, "nomeMae", (pensionista[4] == null ? "" : transformarCampoEmString(pensionista[4]))));
        }
        if (isCampoNullOuVazio(pensionista[5])) {
            adicionarLogTipoErro(dependenciasDirf, "O Pensionista: " + pensionista[1] + " está com o campo 'Sexo' não informado!");
        } else {
            element.setAttributeNode(criarAtributo(doc, "sexo", (String) pensionista[5]));
        }
        if (!isCampoNullOuVazio(pensionista[7])) {
            element.setAttributeNode(criarAtributo(doc, "nitPisPasep", StringUtil.cortarOuCompletarEsquerda(transformarCampoEmString(pensionista[7]), TAMANHO_CAMPO_PIS, "0")));
        }
    }

    private void preencheTagGrupoContraCheque(Document doc, Element element) {
        if (TipoFolhaDePagamentoSicap.SALARIO_13.equals(sicap.getTipoFolhaDePagamentoSicap())) {
            element.setAttributeNode(criarAtributo(doc, "mesCompetencia", DECIMO_13));
        } else {
            element.setAttributeNode(criarAtributo(doc, "mesCompetencia", sicap.getMes().getNumeroMesString()));
        }
        element.setAttributeNode(criarAtributo(doc, "anoCompetencia", transformarCampoEmString(sicap.getExercicio().getAno())));
    }

    private void preencheTagDadosContraCheque(Document doc, Element element, Object[] ficha) {
        TipoFolhaDePagamento tipoFolhaDePagamento = TipoFolhaDePagamento.valueOf(transformarCampoEmString(ficha[1]));
        element.setAttributeNode(criarAtributo(doc, "tipoFolha", tipoFolhaDePagamento != null ? tipoFolhaDePagamento.getCodigo().toString() : "1"));
        element.setAttributeNode(criarAtributo(doc, "baseFgts", transformarCampoEmString(ficha[7])));
        element.setAttributeNode(criarAtributo(doc, "basePrevidenciariaSegurado", transformarCampoEmString(ficha[8])));
        element.setAttributeNode(criarAtributo(doc, "basePrevidenciariaPatronal", transformarCampoEmString(ficha[9])));
        element.setAttributeNode(criarAtributo(doc, "baseIrpf", transformarCampoEmString(ficha[10])));
        element.setAttributeNode(criarAtributo(doc, "totalVencimentos", transformarCampoEmString(ficha[11])));
        element.setAttributeNode(criarAtributo(doc, "totalDescontos", transformarCampoEmString(ficha[12])));
        if (!isCampoNullOuVazio(ficha[17])) {
            BigDecimal idVinculo = (BigDecimal) ficha[17];
            String tipo = transformarCampoEmString(ficha[4]);//Tipo 2 é aposentado
            Object[] objeto = buscarTabelaVinculo(idVinculo.longValue());
            if (objeto == null) {
                Date dataGeracaoSicap = DataUtil.montaData(1, sicap.getMes().getNumeroMes(), sicap.getExercicio().getAno()).getTime();
                List<FuncaoGratificada> funcao = funcaoGratificadaFacade.recuperaFuncaoGratificadaContrato(contratoFPFacade.recuperarSomenteContrato(idVinculo.longValue()), dataGeracaoSicap);
                if (funcao != null && !funcao.isEmpty()) {
                    objeto = buscarTabelaVinculo(funcao.get(0).getContratoFPRef().getId());
                }
            }
            if (objeto == null && "2".equals(tipo)) {
                objeto = buscarTabelasParaAposentado(idVinculo);
            }
            if (objeto != null) {
                element.setAttributeNode(criarAtributo(doc, "codigoTabelaVencimento", transformarCampoEmString(objeto[0])));
                element.setAttributeNode(criarAtributo(doc, "referenciaNivel", transformarCampoEmString(objeto[1])));
            } else if (buscarIdContratoBeneficiario(idVinculo.longValue()) != null) {
                element.setAttributeNode(criarAtributo(doc, "codigoTabelaVencimento", transformarCampoEmString("APO001")));
                element.setAttributeNode(criarAtributo(doc, "referenciaNivel", transformarCampoEmString("1")));
            } else {
                logger.debug("Não foi possivel determinar tabela de vencimennto para o vinculo de ID {} e Matricula {} ", idVinculo, transformarCampoEmString(ficha[4]) + " - " + transformarCampoEmString(ficha[3]));
            }
        }
    }

    private void preencherTagDadosRescisao(Document doc, Element element, Object[] ficha) {
        element.setAttributeNode(criarAtributo(doc, "baseFgts", transformarCampoEmString(ficha[6])));
        element.setAttributeNode(criarAtributo(doc, "basePrevidenciariaSegurado", transformarCampoEmString(ficha[7])));
        element.setAttributeNode(criarAtributo(doc, "basePrevidenciariaPatronal", transformarCampoEmString(ficha[8])));
        element.setAttributeNode(criarAtributo(doc, "baseIrpf", transformarCampoEmString(ficha[9])));
        element.setAttributeNode(criarAtributo(doc, "totalVencimentos", transformarCampoEmString(ficha[10])));
        element.setAttributeNode(criarAtributo(doc, "totalDescontos", transformarCampoEmString(ficha[11])));
        if (!isCampoNullOuVazio(ficha[16])) {
            BigDecimal idVinculo = (BigDecimal) ficha[16];
            String tipo = transformarCampoEmString(ficha[3]);
            Object[] objeto = buscarTabelaVinculo(idVinculo.longValue());
            if (objeto == null && "2".equals(tipo)) {
                objeto = buscarTabelasParaAposentado(idVinculo);
            }
            if (objeto != null) {
                element.setAttributeNode(criarAtributo(doc, "codigoTabelaVencimento", transformarCampoEmString(objeto[0])));
                element.setAttributeNode(criarAtributo(doc, "referenciaNivel", transformarCampoEmString(objeto[1])));
            } else if (buscarIdContratoBeneficiario(idVinculo.longValue()) != null) {
                element.setAttributeNode(criarAtributo(doc, "codigoTabelaVencimento", transformarCampoEmString("APO001")));
                element.setAttributeNode(criarAtributo(doc, "referenciaNivel", transformarCampoEmString("1")));
            } else {
                logger.debug("Não foi possivel determinar tabela de vencimento para o vinculo de ID {} e Matricula {} ", idVinculo, transformarCampoEmString(ficha[4]) + " - " + transformarCampoEmString(ficha[3]));
            }
        }
    }

    private BigDecimal buscarIdContratoBeneficiario(Long idApose) {
        StringBuilder sb = new StringBuilder();
        sb.append("select ben.id ");
        sb.append(" from Beneficiario ben ");
        sb.append("where ben.id = :id");
        Query q = em.createNativeQuery(sb.toString());
        q.setParameter("id", idApose);
        if (!q.getResultList().isEmpty()) {
            return (BigDecimal) q.getResultList().get(0);
        }
        return null;
    }

    private Object[] buscarTabelasParaAposentado(BigDecimal idVinculo) {
        BigDecimal idContratoAposentado = buscarIdContratoAposentado(idVinculo.longValue());

        if (idContratoAposentado != null) {
            return buscarTabelaVinculo(idContratoAposentado.longValue());
        }
        return null;
    }

    private void preencherTagDadosBeneficiarioRescisao(Document doc, Element element, Object[] ficha) {
        element.setAttributeNode(criarAtributo(doc, "cpf", Util.removeMascaras(transformarCampoEmString(ficha[1]))));
        element.setAttributeNode(criarAtributo(doc, "matricula", transformarCampoEmString(ficha[2])));
        element.setAttributeNode(criarAtributo(doc, "dataAdmissao", transformarCampoEmString(ficha[17])));
        element.setAttributeNode(criarAtributo(doc, "dataExoneracao", transformarCampoEmString(ficha[18])));
        if (ficha[19] != null) {
            element.setAttributeNode(criarAtributo(doc, "dataAviso", transformarCampoEmString(ficha[19])));
        }
        element.setAttributeNode(criarAtributo(doc, "avisoIndenizado", transformarCampoEmString(ficha[20])));
        element.setAttributeNode(criarAtributo(doc, "numeroEmpenho", transformarCampoEmString(ficha[21])));
        element.setAttributeNode(criarAtributo(doc, "anoEmpenho", transformarCampoEmString(ficha[22])));
        element.setAttributeNode(criarAtributo(doc, "dataEmpenho", transformarCampoEmString(ficha[23])));
    }

    private void preencheTagDadosBeneficiarioContraCheque(Document doc, Element element, Object[] ficha) {
        element.setAttributeNode(criarAtributo(doc, "cpf", Util.removeMascaras(transformarCampoEmString(ficha[2]))));
        element.setAttributeNode(criarAtributo(doc, "matricula", transformarCampoEmString(ficha[3])));
        element.setAttributeNode(criarAtributo(doc, "situacaoBeneficiario", transformarCampoEmString(ficha[4])));
        if (!isCampoNullOuVazio(ficha[15])) {
            element.setAttributeNode(criarAtributo(doc, "unidadeLotacao", transformarCodigoDoOrgao(transformarCampoEmString(ficha[15]))));
        }

        if (!isCampoNullOuVazio(ficha[18])) {
            element.setAttributeNode(criarAtributo(doc, "codigoCargoAtual", transformarCampoEmString(ficha[18])));
        } else {
            buscarIdCargoParaAposentado(doc, ficha, element);
        }
        if (!ServidorSicap.SituacaoBeneficiario.PENSIONISTA.equals(ServidorSicap.SituacaoBeneficiario.getSituacaoPeloCodigo(transformarCampoEmString(ficha[4])))) {
            element.setAttributeNode(criarAtributo(doc, "situacaoAtualServidor", transformarCampoEmString(ficha[20])));
            if (Integer.valueOf(transformarCampoEmString(ficha[20])) >= 4 && Integer.valueOf(transformarCampoEmString(ficha[20])) <= 7) {
                element.setAttributeNode(criarAtributo(doc, "codigoCargoEfetivo", ficha[19] != null ? transformarCampoEmString(ficha[19]) : "0"));
            }
        }
    }

    private void buscarIdCargoParaAposentado(Document doc, Object[] ficha, Element element) {
        BigDecimal idVinculo = (BigDecimal) ficha[17];
        BigDecimal beneficiario = buscarIdBeneficiario(idVinculo.longValue());
        if (beneficiario != null) {
            element.setAttributeNode(criarAtributo(doc, "codigoCargoAtual", transformarCampoEmString(ficha[5])));
        } else {
            BigDecimal idApose = buscarIdContratoAposentado(idVinculo.longValue());
            if (idApose != null) {
                BigDecimal idCargo = buscarIdCargoPorIdContrato(idApose);
                if (idCargo != null) {
                    element.setAttributeNode(criarAtributo(doc, "codigoCargoAtual", transformarCampoEmString(idCargo)));
                }
            }
        }

    }

    private BigDecimal buscarIdCargoPorIdContrato(BigDecimal idVinculo) {
        StringBuilder sb = new StringBuilder();
        sb.append("select c.cargo_id ");
        sb.append(" from ContratoFP c ");
        sb.append("where c.id = :id");
        Query q = em.createNativeQuery(sb.toString());
        q.setParameter("id", idVinculo);
        if (!q.getResultList().isEmpty()) {
            return (BigDecimal) q.getResultList().get(0);
        }
        return null;
    }

    private String transformarCodigoDoOrgao(String codigo) {
        //ex: 01.24.00.00000.000.00
        return codigo.replaceAll("\\.", "");
    }

    private void preencherTagDadosVerbasContraChequeOuRescisao(Document doc, Element element, Object[] verba) {
        element.setAttributeNode(criarAtributo(doc, "codigo", transformarCampoEmString(verba[1])));
        element.setAttributeNode(criarAtributo(doc, "referencia", transformarCampoEmString(verba[2])));
        element.setAttributeNode(criarAtributo(doc, "valor", transformarCampoEmString(verba[3])));
    }

    private void preencheTagDadosServidor(Document doc, Element element, Object[] servidor) {
        element.setAttributeNode(criarAtributo(doc, "nome", transformarCampoEmString(servidor[1])));
        element.setAttributeNode(criarAtributo(doc, "cpf", transformarCampoEmString(servidor[2])));
        element.setAttributeNode(criarAtributo(doc, "dataNascimento", transformarCampoEmString(servidor[3])));
        if (isCampoNullOuVazio(servidor[4])) {
            adicionarLogTipoErro(dependenciasDirf, "O Servidor: " + transformarCampoEmString(servidor[1]) + " está com o campo 'Nome da Mãe' não informado!");
        } else {
            element.setAttributeNode(criarAtributo(doc, "nomeMae", (servidor[4] == null ? "" : transformarCampoEmString(servidor[4]))));
        }
        element.setAttributeNode(criarAtributo(doc, "sexo", transformarCampoEmString(servidor[5])));
        if (!isCampoNullOuVazio(servidor[8])) {
            element.setAttributeNode(criarAtributo(doc, "nitPisPasep", StringUtil.cortarOuCompletarEsquerda(transformarCampoEmString(servidor[8]), TAMANHO_CAMPO_PIS, "0")));
        }
    }

    private void preencheTagDadosUnidadesLotacao(Document doc, Element element, Object[] unidade) {
        element.setAttributeNode(criarAtributo(doc, "codigo", transformarCampoEmString(unidade[0]).replaceAll("\\.", "")));
        element.setAttributeNode(criarAtributo(doc, "nome", transformarCampoEmString(unidade[1])));
        element.setAttributeNode(criarAtributo(doc, "municipio", transformarCampoEmString(unidade[2])));
    }

    private void preencheTagDadosVinculo(Document doc, Element element, Object[] vinculo) {
        element.setAttributeNode(criarAtributo(doc, "matricula", transformarCampoEmString(vinculo[0])));
        if (isCampoNullOuVazio(vinculo[1])) {
            adicionarLogTipoErro(dependenciasDirf, "A matricula: " + transformarCampoEmString(vinculo[0]) + " está sem data de admissão!");
        } else {
            element.setAttributeNode(criarAtributo(doc, "dataAdmissao", transformarCampoEmString(vinculo[1])));
        }
        element.setAttributeNode(criarAtributo(doc, "codigoCargo", transformarCampoEmString(vinculo[2])));
        element.setAttributeNode(criarAtributo(doc, "tipoServidor", transformarCampoEmString(vinculo[3])));
        element.setAttributeNode(criarAtributo(doc, "tipoVinculo", vinculo[4] != null ? TipoVinculoSicap.valueOf(transformarCampoEmString(vinculo[4])).getCodigo() : ""));
        element.setAttributeNode(criarAtributo(doc, "regimePrevidenciario", transformarCampoEmString(vinculo[5])));
    }

    private void preencheTagDadosTabelaDeVencimento(Document doc, Element element, Object[] tabela) {
        element.setAttributeNode(criarAtributo(doc, "codigo", transformarCampoEmString(tabela[1])));
        element.setAttributeNode(criarAtributo(doc, "nome", transformarCampoEmString(tabela[2])));
    }

    private void preencheTagDadosNivel(Document doc, Element element, Object[] nivel) {
        element.setAttributeNode(criarAtributo(doc, "codigo", transformarCampoEmString(nivel[0])));
        element.setAttributeNode(criarAtributo(doc, "referencia", transformarCampoEmString(nivel[1])));
        element.setAttributeNode(criarAtributo(doc, "valor", transformarCampoEmString(nivel[2])));
    }

    private void preencheTagDadosCargosDaTabelaDeVencimento(Document doc, Element element, String cargo) {
        element.setAttributeNode(criarAtributo(doc, "codigo", cargo));
    }

    private boolean isCampoNullOuVazio(Object campo) {
        if (campo == null) {
            return true;
        } else if (campo instanceof String) {
            if (((String) campo).trim().equals("")) {
                return true;
            }
        }
        return false;
    }

    private String transformarCampoEmString(Object campo) {
        if (campo instanceof Date) {
            return formatarData((Date) campo);
        }
        if (campo instanceof BigDecimal) {
            return ((BigDecimal) campo).toString();
        }
        if (campo instanceof Integer) {
            return ((Integer) campo).toString();
        }
        if (campo instanceof Character) {
            return ((Character) campo).toString();
        }
        return (String) campo;
    }

    private void preencherTagDadosPensao(Document doc, Object[] pensao, Element element) {
        if (isCampoNullOuVazio(pensao[1])) {
            adicionarLogTipoErro(dependenciasDirf, "A pensao: " + transformarCampoEmString(pensao[0]) + " está sem o CPF do Instituidor!");
        } else {
            element.setAttributeNode(criarAtributo(doc, "cpfInstituidor", transformarCampoEmString(pensao[1])));
        }
        if (isCampoNullOuVazio(pensao[2])) {
            adicionarLogTipoErro(dependenciasDirf, "A pensao: " + transformarCampoEmString(pensao[0]) + " está sem a Matricula Instituidor!");
        } else {
            element.setAttributeNode(criarAtributo(doc, "matriculaInstituidor", transformarCampoEmString(pensao[2])));
        }
        if (isCampoNullOuVazio(pensao[3])) {
            adicionarLogTipoErro(dependenciasDirf, "A pensao: " + transformarCampoEmString(pensao[0]) + " está sem a Data Inicio!");
        } else {
            element.setAttributeNode(criarAtributo(doc, "dataInicio", transformarCampoEmString(pensao[3])));
        }
        if (isCampoNullOuVazio(pensao[4])) {
            adicionarLogTipoErro(dependenciasDirf, "A pensao: " + transformarCampoEmString(pensao[0]) + " está sem a Tipo Dependencia!");
        } else {
            element.setAttributeNode(criarAtributo(doc, "tipoDependencia", transformarCampoEmString(pensao[4])));
        }
        if (isCampoNullOuVazio(pensao[5])) {
            adicionarLogTipoErro(dependenciasDirf, "A pensao: " + transformarCampoEmString(pensao[0]) + " está sem a Matricula do Pensionista!");
        } else {
            element.setAttributeNode(criarAtributo(doc, "matriculaPensionista", transformarCampoEmString(pensao[5])));
        }
        if (isCampoNullOuVazio(pensao[6])) {
            adicionarLogTipoErro(dependenciasDirf, "A pensao: " + transformarCampoEmString(pensao[0]) + " está sem o Tipo de Pensão!");
        } else {
            element.setAttributeNode(criarAtributo(doc, "tipoPensao", transformarCampoEmString(pensao[6])));
        }
        if (!isCampoNullOuVazio(pensao[7])) {
            element.setAttributeNode(criarAtributo(doc, "dataFim", transformarCampoEmString(pensao[7])));
        }
    }

    private String formatarData(Date data) {
        return new SimpleDateFormat("yyyy-MM-dd").format(data);
    }

    private List<Object[]> buscarCargos() {
        StringBuilder sb = new StringBuilder();
        sb.append("select distinct c.id, ")
            .append(" ")
            .append("       trim(c.descricao) as nome, ")
            .append("       c.cargahoraria as cargahoraria, ")
            .append("       mp.meses, ")
            .append("       ne.GRAUESCOLARIDADESICAP as escolaridade, ")
            .append("       9 as tipoAcumulavel, ")
            .append("       case c.tipopcs ")
            .append("          when 'QUADRO_EFETIVO' then '1' ")
            .append("          when 'CARGO_EM_COMISSAO' then '2' ")
            .append("          when 'FUNCAO_GRATIFICADA' then '3' ")
            .append("          when 'QUADRO_TEMPORARIO' then '3' ")
            .append("          when 'FUNCAO_GRATIFICADA_COORDENACAO' then '3' ")
            .append("       end as tipo,")
            .append("       c.iniciovigencia as inicioVigencia, ")
            .append("       case ")
            .append("          when coalesce(c.finalvigencia,sysdate) >= sysdate  then '1' else '2' ")
            .append("        end as situacao, ")
            .append("       c.finalvigencia as finalvigencia, ")
            .append("       class.codigo as classificacao ")
            .append(" from cargo c ")
            .append(" left join nivelescolaridade ne on c.nivelescolaridade_id = ne.id ")
            .append(" left join cargocategoriapcs cc on c.id = cc.cargo_id ")
            .append(" left join categoriapcs categoria on cc.categoriapcs_id = categoria.id ")
            .append(" left join planocargossalarios pcs on categoria.planocargossalarios_id= pcs.id ")
            .append(" left join MesesPromocao mp on pcs.id = mp.planocargossalarios_id ")
            .append(" left join classificacaocargo class on class.id = c.classificacaocargo_id ");
        Query q = em.createNativeQuery(sb.toString());
        if (!q.getResultList().isEmpty()) {
            return (List<Object[]>) q.getResultList();
        }
        return new ArrayList<>();
    }

    private BigDecimal buscarIdBeneficiario(Long idBeneficiario) {
        StringBuilder sb = new StringBuilder();
        sb.append("select b.id from Beneficiario b ");
        sb.append("where b.id = :id");
        Query q = em.createNativeQuery(sb.toString());
        q.setParameter("id", idBeneficiario);
        List resultListBem = q.getResultList();
        if (!resultListBem.isEmpty()) {
            return (BigDecimal) resultListBem.get(0);
        }
        return null;
    }

    private BigDecimal buscarIdContratoAposentado(Long idApose) {
        StringBuilder sb = new StringBuilder();
        sb.append("select apo.contratoFP_id ");
        sb.append(" from Aposentadoria apo ");
        sb.append("where apo.id = :id");
        Query q = em.createNativeQuery(sb.toString());
        q.setParameter("id", idApose);
        List resultListApo = q.getResultList();
        if (!resultListApo.isEmpty()) {
            return (BigDecimal) resultListApo.get(0);
        } else {
            StringBuilder sbIntituidor = new StringBuilder();
            sbIntituidor.append("select pen.contratoFP_id ");
            sbIntituidor.append(" from Pensao pen inner join pensionista p on p.pensao_id = pen.id  ");
            sbIntituidor.append("where p.id = :id");
            Query query = em.createNativeQuery(sbIntituidor.toString());
            query.setParameter("id", idApose);
            List resultList = query.getResultList();
            if (!resultList.isEmpty()) {
                return (BigDecimal) resultList.get(0);
            }
        }
        return null;
    }


    private List<Object[]> getHistoricosFuncionais() {
        StringBuilder sb = new StringBuilder();
        sb.append("select distinct * from ( ");
        sb.append("select replace(replace(pf.cpf,'.',''),'-','') as cpf ");
        sb.append(", m.matricula || '0' ||  v.numero as matricula, ");
        sb.append(" case(sf.descricao) ");
        sb.append("     when 'EM FÉRIAS' then '1' ");
        sb.append("     when 'PENSIONISTA' then '1' ");
        sb.append("     when 'SUBSTITUIDO' then '1' ");
        sb.append("     when 'EM EXERCÍCIO' then '1' ");
        sb.append("     when 'INATIVO PARA FOLHA' then '2' ");
        sb.append("     when 'EXONERADO/RESCISO' then '2' ");
        sb.append("     when 'AFASTADO/LICENCIADO' then ");
        sb.append("           coalesce((select case tipo.descontarDiasTrabalhados ");
        sb.append("           when 0 then '3' else '4' end as situacao ");
        sb.append("           from Afastamento af ");
        sb.append("           inner join TipoAfastamento tipo on tipo.id = af.tipoAfastamento_ID ");
        sb.append("           where af.contratoFP_ID = v.id ");
        sb.append("           and scon.inicioVigencia = af.inicio and rownum = 1), '3') ");
        sb.append("     when 'A DISPOSIÇÃO' then '5' ");
        sb.append("     when 'A NOSSA DISPOSIÇÃO' then '6' ");
        sb.append("     when 'EM DISPONIBILIDADE - 10' then '10' ");
        sb.append("     when 'INSTITUIDOR (FALECIDO - GERA PENSÃO)' then '11' ");
        sb.append("     when 'APOSENTADO' then '12' ");
        sb.append("     else null ");
        sb.append("  end as situacao ");
        sb.append(", scon.inicioVigencia as dataOcorrencia ");
        sb.append(", grupo.nome  ");
        sb.append("from VinculoFP v ");
        sb.append("inner join MatriculaFP m on m.id = v.matriculaFP_ID ");
        sb.append("inner join PessoaFisica pf on pf.id = m.pessoa_ID ");
        sb.append("inner join SituacaoContratoFP scon on scon.contratoFP_ID = v.id ");
        sb.append("inner join SituacaoFuncional sf on sf.id = scon.situacaoFuncional_ID ");

        sb.append(" inner join RecursoDoVinculoFP recv on v.id = recv.vinculoFP_id ");
        sb.append(" inner join RecursoFP rec on rec.id = recv.recursoFP_id ");
        sb.append(" inner join AgrupamentoRecursoFP agrupamento on rec.id = agrupamento.recursoFP_id ");
        sb.append(" inner join GrupoRecursoFP grupo on grupo.id = agrupamento.grupoRecursoFP_id ");

        sb.append("where to_date(to_char(:dataReferencia,'mm/yyyy'),'mm/yyyy') between to_date(to_char(scon.inicioVigencia,'mm/yyyy'),'mm/yyyy') and to_date(to_char(coalesce(scon.finalVigencia,:dataReferencia),'mm/yyyy'),'mm/yyyy') ");
        sb.append(" and to_date(to_char(:dataReferencia,'dd/MM/yyyy'),'dd/mm/yyyy') between to_date(to_char(v.iniciovigencia,'dd/mm/yyyy'),'dd/mm/yyyy') and to_date(to_char(coalesce(v.finalvigencia,:dataReferencia),'dd/mm/yyyy'),'dd/mm/yyyy') ");
        sb.append(" and to_date(to_char(:dataReferencia,'mm/yyyy'),'mm/yyyy') between to_date(to_char(recv.iniciovigencia,'mm/yyyy'),'mm/yyyy') and to_date(to_char(coalesce(recv.finalvigencia,:dataReferencia),'mm/yyyy'),'mm/yyyy') ");
        sb.append(" and to_date(to_char(:dataReferencia,'mm/yyyy'),'mm/yyyy') between to_date(to_char(rec.iniciovigencia,'mm/yyyy'),'mm/yyyy') and to_date(to_char(coalesce(rec.finalVigencia,:dataReferencia),'mm/yyyy'),'mm/yyyy') ");
        sb.append("order by pf.cpf, scon.inicioVigencia");
        sb.append(")");
        sb.append(" union all ");
        sb.append("select distinct * from ( ");
        sb.append("select replace(replace(pf.cpf,'.',''),'-','') as cpf ,");
        sb.append(" m.matricula || '0' ||  v.numero as matricula, ");
        sb.append("  case(sf.descricao) ");
        sb.append("     when 'EM FÉRIAS' then '1' ");
        sb.append("     when 'PENSIONISTA' then '1' ");
        sb.append("     when 'SUBSTITUIDO' then '1' ");
        sb.append("     when 'EM EXERCÍCIO' then '1' ");
        sb.append("     when 'INATIVO PARA FOLHA' then '2' ");
        sb.append("     when 'EXONERADO/RESCISO' then '2' ");
        sb.append("     when 'AFASTADO/LICENCIADO' then ");
        sb.append("           coalesce((select case tipo.descontarDiasTrabalhados ");
        sb.append("           when 0 then '3' else '4' end as situacao ");
        sb.append("           from Afastamento af ");
        sb.append("           inner join TipoAfastamento tipo on tipo.id = af.tipoAfastamento_ID ");
        sb.append("           where af.contratoFP_ID = v.id ");
        sb.append("           and scon.inicioVigencia = af.inicio and rownum = 1), '3') ");
        sb.append("     when 'A DISPOSIÇÃO' then '5' ");
        sb.append("     when 'A NOSSA DISPOSIÇÃO' then '6' ");
        sb.append("     when 'EM DISPONIBILIDADE - 10' then '10' ");
        sb.append("     when 'INSTITUIDOR (FALECIDO - GERA PENSÃO)' then '11' ");
        sb.append("     when 'APOSENTADO' then '12' ");
        sb.append("     else null ");
        sb.append("  end as situacao ");
        sb.append(", scon.inicioVigencia as dataOcorrencia ");
        sb.append(", grupo.nome  ");
        sb.append("from VinculoFP v ");
        sb.append("inner join aposentadoria apo on apo.id = v.id ");
        sb.append("inner join contratofp contrato on contrato.id= apo.contratofp_id ");
        sb.append("inner join MatriculaFP m on m.id = v.matriculaFP_ID ");
        sb.append("inner join PessoaFisica pf on pf.id = m.pessoa_ID ");
        sb.append("inner join SituacaoContratoFP scon on scon.contratoFP_ID = contrato.id ");
        sb.append("inner join SituacaoFuncional sf on sf.id = scon.situacaoFuncional_ID ");

        sb.append(" inner join RecursoDoVinculoFP recv on v.id = recv.vinculoFP_id ");
        sb.append(" inner join RecursoFP rec on rec.id = recv.recursoFP_id ");
        sb.append(" inner join AgrupamentoRecursoFP agrupamento on rec.id = agrupamento.recursoFP_id ");
        sb.append(" inner join GrupoRecursoFP grupo on grupo.id = agrupamento.grupoRecursoFP_id ");

        sb.append("where to_date(to_char(:dataReferencia,'mm/yyyy'),'mm/yyyy') between to_date(to_char(scon.inicioVigencia,'mm/yyyy'),'mm/yyyy') and to_date(to_char(coalesce(scon.finalVigencia,:dataReferencia),'mm/yyyy'),'mm/yyyy') ");
        sb.append(" and to_date(to_char(:dataReferencia,'dd/MM/yyyy'),'dd/mm/yyyy') between to_date(to_char(v.iniciovigencia,'dd/mm/yyyy'),'dd/mm/yyyy') and to_date(to_char(coalesce(v.finalvigencia,:dataReferencia),'dd/mm/yyyy'),'dd/mm/yyyy') ");
        sb.append(" and to_date(to_char(:dataReferencia,'mm/yyyy'),'mm/yyyy') between to_date(to_char(recv.iniciovigencia,'mm/yyyy'),'mm/yyyy') and to_date(to_char(coalesce(recv.finalvigencia,:dataReferencia),'mm/yyyy'),'mm/yyyy') ");
        sb.append(" and to_date(to_char(:dataReferencia,'mm/yyyy'),'mm/yyyy') between to_date(to_char(rec.iniciovigencia,'mm/yyyy'),'mm/yyyy') and to_date(to_char(coalesce(rec.finalVigencia,:dataReferencia),'mm/yyyy'),'mm/yyyy') ");
        sb.append("order by pf.cpf, scon.inicioVigencia");
        sb.append(")");
        Query q = em.createNativeQuery(sb.toString());
        q.setParameter("dataReferencia", Util.getDataHoraMinutoSegundoZerado(sicap.getUltimoDiaDoMes()));
        if (!q.getResultList().isEmpty()) {
            List<Object[]> retorno = new ArrayList<>();
            List<Object[]> resultList = (List<Object[]>) q.getResultList();
            verificarAndAdicionarHierarquiaEntidade(retorno, resultList, 4);
            return retorno;
        }
        return new ArrayList<>();
    }

    private List<Object[]> buscarVerbas() {
        StringBuilder sb = new StringBuilder();
        sb.append(" select eve.codigo,  ");
        sb.append("        eve.descricao, ");
        sb.append("        '' as baseLegal, ");
        sb.append("        coalesce((select 'S' from itembasefp item  ");
        sb.append("        inner join basefp base on item.basefp_id = base.id ");
        sb.append("        where item.eventofp_id = eve.id and base.codigo in ('1001', '1002') group by 'S'), 'N') as basePrevidencia, ");
        sb.append("        coalesce((select 'S' from itembasefp item  ");
        sb.append("        inner join basefp base on item.basefp_id = base.id ");
        sb.append("        where item.eventofp_id = eve.id and base.codigo = '1003' group by 'S'), 'N') as baseIRPF, ");
        sb.append("        coalesce((select 'S' from itembasefp item  ");
        sb.append("        inner join basefp base on item.basefp_id = base.id ");
        sb.append("        where item.eventofp_id = eve.id and base.codigo = '1005' group by 'S'), 'N') as baseFGTS, ");
        sb.append("        case eve.tipoeventofp ");
        sb.append("           when 'VANTAGEM' then 'C' ");
        sb.append("           when 'DESCONTO' then 'D' ");
        sb.append("       end as natureza, ");
        sb.append("       case eve.tipolancamentofp ");
        sb.append("          when 'VALOR' then 4 ");
        sb.append("          when 'DIAS' then 1 ");
        sb.append("          when 'REFERENCIA' then 3 ");
        sb.append("          when 'HORAS' then 2 ");
        sb.append("          when 'QUANTIDADE' then 1 ");
        sb.append("       end as tipoReferencia, ");
        sb.append("       case eve.verbafixa ");
        sb.append("          when 1 then 'S' else 'N' ");
        sb.append("       end as compoeVencimento, ");
        sb.append("       '' as categoriaEconomica, ");
        sb.append("       '' as grupoNaturezaDespesa, ");
        sb.append("       '' as modalidadeAplicacao, ");
        sb.append("       '' as elementoDespesa, ");
        sb.append("       clas.codigo as codigoClassificacao ");
        sb.append(" from eventofp eve ");
        sb.append(" left join classificacaoverba clas on eve.classificacaoverba_id = clas.id ");
        sb.append(" where eve.tipoeventofp  <> 'INFORMATIVO' ");
        sb.append(" and eve.ativo = 1 ");
        sb.append(" and (eve.naoenviarverbasicap = 0 or eve.naoenviarverbasicap is null) ");
        Query q = em.createNativeQuery(sb.toString());
        List<Object[]> resultado = q.getResultList();
        if (!resultado.isEmpty()) {
            return resultado;
        }
        return Lists.newArrayList();
    }

    private List<Object[]> buscarPensionistas() {
        StringBuilder sb = new StringBuilder();
        sb.append(" select distinct pensionista.id as idPensao,  ");
        sb.append("  pfPensionista.nome as nome, ");
        sb.append("  replace(replace(pfPensionista.cpf, '.',''), '-','') as cpf, ");
        sb.append("  pfPensionista.DATANASCIMENTO as dataNascimento, ");
        sb.append("  pfPensionista.MAE as mae, ");
        sb.append("  substr(pfPensionista.sexo,0, 1) as sexo,  ");
        sb.append("  grupo.nome as grupo, ");
        sb.append(" (select replace(replace(max(PISPASEP),'.',''),'-','') as pisPasep  from CARTEIRATRABALHO ct inner join DOCUMENTOPESSOAL doc on ct.ID = doc.ID where doc.pessoafisica_id = pfPensionista.id) as pisPasep ");
        sb.append(" from Pensao pensao inner join pensionista pensionista on pensionista.PENSAO_ID = pensao.id ");
        sb.append(" inner join vinculofp vinculo on vinculo.id = pensionista.id ");
        sb.append(" inner join contratofp c on c.id = pensao.contratofp_id ");
        sb.append(" inner join vinculofp vinculoPensao on vinculoPensao.id = c.id ");
        sb.append(" inner join matriculafp mat on mat.id = vinculoPensao.matriculafp_id ");
        sb.append(" inner join pessoafisica pf on pf.id = mat.PESSOA_ID ");

        sb.append("        inner join matriculafp matPensionista on matPensionista.id = vinculo.matriculafp_id ");
        sb.append("inner join pessoafisica pfPensionista on pfPensionista.id = matPensionista.PESSOA_ID ");

        sb.append(" inner join RecursoDoVinculoFP recv on vinculo.id = recv.vinculoFP_id ");
        sb.append(" inner join RecursoFP rec on rec.id = recv.recursoFP_id ");
        sb.append(" inner join AgrupamentoRecursoFP agrupamento on rec.id = agrupamento.recursoFP_id ");
        sb.append(" inner join GrupoRecursoFP grupo on grupo.id = agrupamento.grupoRecursoFP_id ");

        sb.append(" where to_date(to_char(:dataReferencia,'mm/yyyy'),'mm/yyyy') between to_date(to_char(vinculo.inicioVigencia,'mm/yyyy'),'mm/yyyy') and to_date(to_char(coalesce(vinculo.finalVigencia,:dataReferencia),'mm/yyyy'),'mm/yyyy') ");
        sb.append(" and to_date(to_char(:dataReferencia,'mm/yyyy'),'mm/yyyy') between to_date(to_char(recv.iniciovigencia,'mm/yyyy'),'mm/yyyy') and to_date(to_char(coalesce(recv.finalvigencia,:dataReferencia),'mm/yyyy'),'mm/yyyy') ");
        sb.append(" and to_date(to_char(:dataReferencia,'mm/yyyy'),'mm/yyyy') between to_date(to_char(rec.iniciovigencia,'mm/yyyy'),'mm/yyyy') and to_date(to_char(coalesce(rec.finalVigencia,:dataReferencia),'mm/yyyy'),'mm/yyyy') ");
        Query q = em.createNativeQuery(sb.toString());
        q.setParameter("dataReferencia", getDataParametro());
        if (!q.getResultList().isEmpty()) {
            List<Object[]> retorno = new ArrayList<>();
            List<Object[]> resultList = (List<Object[]>) q.getResultList();
            verificarAndAdicionarHierarquiaEntidade(retorno, resultList, 6);
            return retorno;
        }
        return new ArrayList<>();
    }

    public Date getDataParametro() {
        return Util.getDataHoraMinutoSegundoZerado(sicap.getUltimoDiaDoMes());
    }

    private List<Object[]> buscarContraCheques(TipoFolhaDePagamentoSicap tipoFolhaDePagamentoSicap) {
        StringBuilder sb = new StringBuilder();
        sb.append(" select fichaid, ");
        sb.append("       tipoFolhaDePagamento, ");
        sb.append("       cpf, ");
        sb.append("       matricula, ");
        sb.append("       situacaobeneficiario, ");
        sb.append("       codigodocargo, ");
        sb.append("       descricao, ");
        sb.append("       sum(valorfgts) as valorfgts, ");
        sb.append("       sum(baseprevidenciasegurado) as baseprevidenciasegurado, ");
        sb.append("       sum(basePrevidenciaPatronal) as basePrevidenciaPatronal, ");
        sb.append("       sum(valorIRPF) as valorIRPF, ");
        sb.append("       sum(totalVencimentos) as totalVencimentos, ");
        sb.append("       sum(totalDescontos) as totalDescontos, ");
        sb.append("       codigoTabelaVencimento, ");
        sb.append("       referenciaNivel, ");
        sb.append("       codigo, ");
        sb.append("       grupo, ");
        sb.append("       idVinculo, ");
        sb.append("       cargoAtual, ");
        sb.append("       case when cargoEfetivo is not null then cargoEfetivo ");
        sb.append("           else cargoAtual end, ");
        sb.append(" case");
        sb.append(" when modalidade = 1 and cargoEfetivo is null and funcaogratificada is null then '1' ");
        sb.append(" when modalidade = 2 then '2' ");
        sb.append(" when modalidade = 4 and cargoEfetivo is null and funcaogratificada is null then '3' ");
        sb.append(" when modalidade = 1 and cargoEfetivo is not null then '4' ");
        sb.append(" when modalidade = 1 and funcaogratificada is not null then '5' ");
        sb.append(" when modalidade = 4 and cargoEfetivo is not null then '6' ");
        sb.append(" when modalidade = 4 and funcaogratificada is not null then '7' ");
        sb.append(" when tipoVinculoSicap = :empregadoCLT then '8' ");
        sb.append(" when estagiario is not null then '9' ");
        sb.append(" when tipoVinculoSicap = :contribuinteIndividual then '10' ");
        sb.append(" else '3' ");
        sb.append(" end as situacaoAtualServidor ");
        sb.append("from ( ");
        sb.append(" select ficha.id as fichaId, folha.tipoFolhaDePagamento, pf.cpf,  mat.matricula || '0' ||  v.numero as matricula,  ");
        sb.append(" case ");
        sb.append("    when (select pen.id from pensionista pen where pen.id = v.id) is not null then '3'");
        sb.append("    when (select ben.id from Beneficiario ben where ben.id = v.id) is not null then '1'");
        sb.append("    when (select apo.id from aposentadoria apo where apo.id = v.id) is not null then '2' ");
        sb.append("  else '1' end  as situacaoBeneficiario, coalesce(cargo.id, (select 638882892 from beneficiario ben where ben.id = v.id) ) as codigoDoCargo, un.descricao , ");
        sb.append("    coalesce(((select itemFicha.valorBaseDeCalculo from ItemFichaFinanceiraFP itemFicha inner join EventoFP e on e.id = itemFicha.eventoFP_id where itemFicha.id = item.id and e.codigo = 904)),0) as valorFGTS, ");
        sb.append("    coalesce(((select itemFicha.valorBaseDeCalculo from ItemFichaFinanceiraFP itemFicha inner join EventoFP e on e.id = itemFicha.eventoFP_id where itemFicha.id = item.id and e.codigo in(898, 900))),0) as basePrevidenciaSegurado, ");
        sb.append("    coalesce(((select itemFicha.valorBaseDeCalculo from ItemFichaFinanceiraFP itemFicha inner join EventoFP e on e.id = itemFicha.eventoFP_id where itemFicha.id = item.id and e.codigo in(898, 900))),0) as basePrevidenciaPatronal, ");
        sb.append("    coalesce(((select itemFicha.valorBaseDeCalculo from ItemFichaFinanceiraFP itemFicha inner join EventoFP e on e.id = itemFicha.eventoFP_id where itemFicha.id = item.id and e.codigo = 901)),0) as valorIRPF, ");
        sb.append("    coalesce(((select itemFicha.valor from ItemFichaFinanceiraFP itemFicha inner join EventoFP e on e.id = itemFicha.eventoFP_id where itemFicha.id = item.id and itemFicha.tipoEventoFP = 'VANTAGEM')),0) as totalVencimentos, ");
        sb.append("    coalesce(((select itemFicha.valor from ItemFichaFinanceiraFP itemFicha inner join EventoFP e on e.id = itemFicha.eventoFP_id where itemFicha.id = item.id and itemFicha.tipoEventoFP = 'DESCONTO')),0) as totalDescontos, ");
        sb.append("    '' as codigoTabelaVencimento, ");
        sb.append("    '' as referenciaNivel, ");
        sb.append("    vw.codigo as codigo, ");
        sb.append("    grupo.nome as grupo, ");
        sb.append("    v.id as idVinculo, ");
        sb.append("    cargo.id as cargoAtual, ");
        sb.append("    (select cc.cargo_id  from CargoConfianca cc   ");
        sb.append("       where to_date(to_char(:dataReferencia,'mm/yyyy'),'mm/yyyy') between to_date(to_char(cc.iniciovigencia,'mm/yyyy'),'mm/yyyy') and to_date(to_char(coalesce(cc.finalVigencia,:dataReferencia),'mm/yyyy'),'mm/yyyy') ");
        sb.append("       and cc.contratofp_id = c.id and cc.iniciovigencia = (select max(iniciovigencia) from  CargoConfianca cargoconf  ");
        sb.append("       where to_date(to_char(:dataReferencia,'mm/yyyy'),'mm/yyyy') between to_date(to_char(cargoconf.iniciovigencia,'mm/yyyy'),'mm/yyyy') and to_date(to_char(coalesce(cargoconf.finalVigencia,:dataReferencia),'mm/yyyy'),'mm/yyyy') ");
        sb.append("       and cargoconf.contratofp_id = cc.contratofp_id) ");
        sb.append("    and rownum = 1 ) as cargoEfetivo, ");
        sb.append("    (select cc.id  from funcaogratificada cc   ");
        sb.append("       where to_date(to_char(:dataReferencia,'mm/yyyy'),'mm/yyyy') between to_date(to_char(cc.iniciovigencia,'mm/yyyy'),'mm/yyyy') and to_date(to_char(coalesce(cc.finalVigencia,:dataReferencia),'mm/yyyy'),'mm/yyyy') ");
        sb.append("       and cc.contratofp_id = c.id and cc.iniciovigencia = (select max(iniciovigencia) from  funcaogratificada cargoconf  ");
        sb.append("       where to_date(to_char(:dataReferencia,'mm/yyyy'),'mm/yyyy') between to_date(to_char(cargoconf.iniciovigencia,'mm/yyyy'),'mm/yyyy') and to_date(to_char(coalesce(cargoconf.finalVigencia,:dataReferencia),'mm/yyyy'),'mm/yyyy') ");
        sb.append("       and cargoconf.contratofp_id = cc.contratofp_id) ");
        sb.append("    and rownum = 1 ) as funcaogratificada, ");
        sb.append("     MODA.CODIGO as modalidade, ");
        sb.append("     (select est.id from ESTAGIARIO est where est.id = v.id) as estagiario, ");
        sb.append("     c.TIPOVINCULOSICAP as tipoVinculoSicap ");
        sb.append("  from FolhaDePagamento folha ");
        sb.append(" inner join FichaFinanceiraFP ficha on ficha.folhaDePagamento_id = folha.id ");
        sb.append(" inner join ItemFichaFinanceiraFP item on item.fichaFinanceiraFP_id = ficha.id ");
        sb.append(" inner join EventoFP evento on evento.id = item.eventoFP_id ");
        sb.append(" inner join VinculoFP v on v.id = ficha.vinculoFP_id ");
        sb.append(" inner join MatriculaFP mat on mat.id = v.matriculaFP_id ");
        sb.append(" inner join PessoaFisica pf on pf.id = mat.pessoa_id ");
        sb.append(" inner join RecursoDoVinculoFP recv on v.id = recv.vinculoFP_id ");
        sb.append(" inner join RecursoFP rec on rec.id = recv.recursoFP_id ");
        sb.append(" inner join AgrupamentoRecursoFP agrupamento on rec.id = agrupamento.recursoFP_id ");
        sb.append(" inner join GrupoRecursoFP grupo on grupo.id = agrupamento.grupoRecursoFP_id ");
        sb.append(" inner join UnidadeOrganizacional un on un.id = ficha.unidadeOrganizacional_id ");
        sb.append(" inner join VwHierarquiaAdministrativa vw on vw.subordinada_id = un.id ");
        sb.append("  left join ContratoFP c on c.id = v.id ");
        sb.append("  LEFT join MODALIDADECONTRATOFP moda on c.MODALIDADECONTRATOFP_ID = moda.ID ");
        sb.append("  left join Cargo cargo on cargo.id = c.cargo_id ");
        sb.append("  left join CargoConfianca cc on cc.contratofp_id = c.id  ");
        sb.append("   and to_date(to_char(:dataReferencia,'mm/yyyy'),'mm/yyyy') between to_date(to_char(cc.iniciovigencia,'mm/yyyy'),'mm/yyyy') and to_date(to_char(coalesce(cc.finalVigencia,:dataReferencia),'mm/yyyy'),'mm/yyyy') ");
        sb.append(" where folha.ano = :ano and folha.mes = :mes and ");
        if (TipoFolhaDePagamentoSicap.MENSAL.equals(tipoFolhaDePagamentoSicap)) {
            sb.append(" folha.tipoFolhaDePagamento not in (:tipoFolha)");
        } else {
            sb.append(" folha.tipoFolhaDePagamento = :tipoFolha");
        }
        sb.append("   and to_date(to_char(:dataReferencia,'mm/yyyy'),'mm/yyyy') between to_date(to_char(vw.iniciovigencia,'mm/yyyy'),'mm/yyyy') and to_date(to_char(coalesce(vw.fimVigencia,:dataReferencia),'mm/yyyy'),'mm/yyyy') ");
        sb.append("   and to_date(to_char(:dataReferencia,'mm/yyyy'),'mm/yyyy') between to_date(to_char(recv.iniciovigencia,'mm/yyyy'),'mm/yyyy') and to_date(to_char(coalesce(recv.finalVigencia,:dataReferencia),'mm/yyyy'),'mm/yyyy') ");
        sb.append(" ) group by fichaid, ");
        sb.append("       tipoFolhaDePagamento, ");
        sb.append("       cpf, ");
        sb.append("       matricula, ");
        sb.append("       situacaobeneficiario, ");
        sb.append("       codigodocargo, ");
        sb.append("       descricao, ");
        sb.append("       codigoTabelaVencimento, ");
        sb.append("       referenciaNivel, ");
        sb.append("       codigo, ");
        sb.append("       grupo, ");
        sb.append("       idVinculo, ");
        sb.append("       cargoAtual, ");
        sb.append("       cargoEfetivo,  ");
        sb.append("       modalidade, ");
        sb.append("       funcaogratificada, ");
        sb.append("       tipoVinculoSicap, ");
        sb.append("       estagiario ");
        Query q = em.createNativeQuery(sb.toString());
        q.setParameter("mes", sicap.getMes().getNumeroMesIniciandoEmZero());
        q.setParameter("ano", sicap.getExercicio().getAno());
        q.setParameter("dataReferencia", getDataParametro());
        q.setParameter("tipoFolha", TipoFolhaDePagamento.SALARIO_13.name());
        q.setParameter("empregadoCLT", TipoVinculoSicap.EMPREGADO_CLT_CONTRATO_DE_GESTAO.name());
        q.setParameter("contribuinteIndividual", TipoVinculoSicap.CONTRIBUINTE_INDIVIDUAL_E_AUTONOMO.name());
        List<Object[]> resultado = (List<Object[]>) q.getResultList();
        if (!resultado.isEmpty()) {
            List<Object[]> retorno = Lists.newArrayList();
            verificarAndAdicionarHierarquiaEntidade(retorno, resultado, 16);
            return retorno;
        }
        return Lists.newArrayList();
    }

    private List<Object[]> buscarRescisoes() {
        StringBuilder sb = new StringBuilder();
        sb.append(" select fichaid, ")
            .append("       cpf, ")
            .append("       matricula, ")
            .append("       situacaobeneficiario, ")
            .append("       codigodocargo, ")
            .append("       descricao, ")
            .append("       sum(valorfgts)               as valorfgts, ")
            .append("       sum(baseprevidenciasegurado) as baseprevidenciasegurado, ")
            .append("       sum(basePrevidenciaPatronal) as basePrevidenciaPatronal, ")
            .append("       sum(valorIRPF)               as valorIRPF, ")
            .append("       sum(totalVencimentos)        as totalVencimentos, ")
            .append("       sum(totalDescontos)          as totalDescontos, ")
            .append("       codigoTabelaVencimento, ")
            .append("       referenciaNivel, ")
            .append("       codigo, ")
            .append("       grupo, ")
            .append("       idVinculo, ")
            .append("       admissao, ")
            .append("       dataExoneracao, ")
            .append("       dataAvisoPrevio, ")
            .append("       avisoPrevioIndenizado, ")
            .append("       numeroEmpenho, ")
            .append("       anoEmpenho, ")
            .append("       dataEmpenho ")
            .append("from ( ")
            .append("         select ficha.id as fichaId, ")
            .append("                pf.cpf, ")
            .append("                mat.matricula || '0' || v.numero as matricula, ")
            .append("                case ")
            .append("                    when (select pen.id from pensionista pen where pen.id = v.id) is not null then '3' ")
            .append("                    when (select ben.id from Beneficiario ben where ben.id = v.id) is not null then '1' ")
            .append("                    when (select apo.id from aposentadoria apo where apo.id = v.id) is not null then '2' ")
            .append("                    else '1' end as situacaoBeneficiario, ")
            .append("                coalesce(cargo.id, (select 638882892 from beneficiario ben where ben.id = v.id)) as codigoDoCargo, ")
            .append("                un.descricao, ")
            .append("                coalesce(((select itemFicha.valorBaseDeCalculo ")
            .append("                           from ItemFichaFinanceiraFP itemFicha ")
            .append("                                    inner join EventoFP e on e.id = itemFicha.eventoFP_id ")
            .append("                           where itemFicha.id = item.id ")
            .append("                             and e.codigo = 904)), 0) as valorFGTS, ")
            .append("                coalesce(((select itemFicha.valorBaseDeCalculo ")
            .append("                           from ItemFichaFinanceiraFP itemFicha ")
            .append("                                    inner join EventoFP e on e.id = itemFicha.eventoFP_id ")
            .append("                           where itemFicha.id = item.id ")
            .append("                             and e.codigo in (898, 900))), ")
            .append("                         0) as basePrevidenciaSegurado, ")
            .append("                coalesce(((select itemFicha.valorBaseDeCalculo ")
            .append("                           from ItemFichaFinanceiraFP itemFicha ")
            .append("                                    inner join EventoFP e on e.id = itemFicha.eventoFP_id ")
            .append("                           where itemFicha.id = item.id ")
            .append("                             and e.codigo in (898, 900))), ")
            .append("                         0) as basePrevidenciaPatronal, ")
            .append("                coalesce(((select itemFicha.valorBaseDeCalculo ")
            .append("                           from ItemFichaFinanceiraFP itemFicha ")
            .append("                                    inner join EventoFP e on e.id = itemFicha.eventoFP_id ")
            .append("                           where itemFicha.id = item.id ")
            .append("                             and e.codigo = 901)), 0) as valorIRPF, ")
            .append("                coalesce(((select itemFicha.valor ")
            .append("                           from ItemFichaFinanceiraFP itemFicha ")
            .append("                                    inner join EventoFP e on e.id = itemFicha.eventoFP_id ")
            .append("                           where itemFicha.id = item.id ")
            .append("                             and itemFicha.tipoEventoFP = 'VANTAGEM')), 0) as totalVencimentos, ")
            .append("                coalesce(((select itemFicha.valor ")
            .append("                           from ItemFichaFinanceiraFP itemFicha ")
            .append("                                    inner join EventoFP e on e.id = itemFicha.eventoFP_id ")
            .append("                           where itemFicha.id = item.id ")
            .append("                             and itemFicha.tipoEventoFP = 'DESCONTO')), 0) as totalDescontos, ")
            .append("                '' as codigoTabelaVencimento, ")
            .append("                '' as referenciaNivel, ")
            .append("                vw.codigo as codigo, ")
            .append("                grupo.nome as grupo, ")
            .append("                v.id as idVinculo, ")
            .append("                v.INICIOVIGENCIA as admissao, ")
            .append("                exoneracao.DATARESCISAO as dataExoneracao, ")
            .append("                exoneracao.DATAAVISOPREVIO as dataAvisoPrevio, ")
            .append("                (case when exoneracao.tipoAvisoPrevio = 'INDENIZADO' THEN 'S' ELSE 'N' END) as avisoPrevioIndenizado, ")
            .append("    coalesce((select emp.numero ")
            .append("    from EmpenhoFichaFinanceiraFP empfic ")
            .append("    left join EmpenhoVinculoFP empvinc on empvinc.ID = empfic.EmpenhoVinculoFP_id ")
            .append("   left join empenho emp on emp.ID = empvinc.empenho_id ")
            .append("   left join exercicio ex on emp.exercicio_ID = ex.id ")
            .append("   where ficha.ID = empfic.fichaFinanceiraFP_id), '')                     as numeroEmpenho, ")
            .append("   (select ex.ano ")
            .append("   from EmpenhoFichaFinanceiraFP empfic ")
            .append("   left join EmpenhoVinculoFP empvinc on empvinc.ID = empfic.EmpenhoVinculoFP_id ")
            .append("   left join empenho emp on emp.ID = empvinc.empenho_id ")
            .append("   left join exercicio ex on emp.exercicio_ID = ex.id ")
            .append("   where ficha.ID = empfic.fichaFinanceiraFP_id)                                   as anoEmpenho, ")
            .append("   (select emp.DATAEMPENHO ")
            .append("   from EmpenhoFichaFinanceiraFP empfic ")
            .append("   left join EmpenhoVinculoFP empvinc on empvinc.ID = empfic.EmpenhoVinculoFP_id ")
            .append("   left join empenho emp on emp.ID = empvinc.empenho_id ")
            .append("   left join exercicio ex on emp.exercicio_ID = ex.id ")
            .append("   where ficha.ID = empfic.fichaFinanceiraFP_id)                                   as dataEmpenho ")
            .append("         from FolhaDePagamento folha ")
            .append("                  inner join FichaFinanceiraFP ficha on ficha.folhaDePagamento_id = folha.id ")
            .append("                  inner join ItemFichaFinanceiraFP item on item.fichaFinanceiraFP_id = ficha.id ")
            .append("                  inner join EventoFP evento on evento.id = item.eventoFP_id ")
            .append("                  inner join VinculoFP v on v.id = ficha.vinculoFP_id ")
            .append("                  inner join MatriculaFP mat on mat.id = v.matriculaFP_id ")
            .append("                  inner join PessoaFisica pf on pf.id = mat.pessoa_id ")
            .append("                  inner join RecursoDoVinculoFP recv on v.id = recv.vinculoFP_id ")
            .append("                  inner join RecursoFP rec on rec.id = recv.recursoFP_id ")
            .append("                  inner join AgrupamentoRecursoFP agrupamento on rec.id = agrupamento.recursoFP_id ")
            .append("                  inner join GrupoRecursoFP grupo on grupo.id = agrupamento.grupoRecursoFP_id ")
            .append("                  inner join UnidadeOrganizacional un on un.id = ficha.unidadeOrganizacional_id ")
            .append("                  inner join VwHierarquiaAdministrativa vw on vw.subordinada_id = un.id ")
            .append("                  inner join EXONERACAORESCISAO exoneracao on exoneracao.VINCULOFP_ID = v.id ")
            .append("                  left join EmpenhoFichaFinanceiraFP empfic on ficha.ID = empfic.fichaFinanceiraFP_id ")
            .append("                  left join EmpenhoVinculoFP empvinc on empvinc.ID = empfic.EmpenhoVinculoFP_id ")
            .append("                  left join empenho emp on emp.ID = empvinc.empenho_id ")
            .append("                  left join exercicio ex on emp.exercicio_ID = ex.id ")
            .append("                  left join ContratoFP c on c.id = v.id ")
            .append("                  LEFT join MODALIDADECONTRATOFP moda on c.MODALIDADECONTRATOFP_ID = moda.ID ")
            .append("                  left join Cargo cargo on cargo.id = c.cargo_id ")
            .append("         where folha.ano = :ano and folha.mes = :mes ")
            .append("            and folha.TIPOFOLHADEPAGAMENTO in(:tipo, :tipoComp) ")
            .append("            and folha.id in (:idsFolha) ")
            .append("           and to_date(to_char(:dataReferencia,'mm/yyyy'),'mm/yyyy') between to_date(to_char(vw.iniciovigencia,'mm/yyyy'),'mm/yyyy') and to_date(to_char(coalesce(vw.fimVigencia,:dataReferencia),'mm/yyyy'),'mm/yyyy') ")
            .append("           and to_date(to_char(:dataReferencia,'mm/yyyy'),'mm/yyyy') between to_date(to_char(recv.iniciovigencia,'mm/yyyy'),'mm/yyyy') and to_date(to_char(coalesce(recv.finalVigencia,:dataReferencia),'mm/yyyy'),'mm/yyyy') ")
            .append("     ) ")
            .append("group by fichaid, ")
            .append("         cpf, ")
            .append("         matricula, ")
            .append("         situacaobeneficiario, ")
            .append("         codigodocargo, ")
            .append("         descricao, ")
            .append("         codigoTabelaVencimento, ")
            .append("         referenciaNivel, ")
            .append("         codigo, ")
            .append("         grupo, ")
            .append("         idVinculo, ")
            .append("         admissao, ")
            .append("         dataExoneracao, ")
            .append("         dataAvisoPrevio, ")
            .append("         avisoPrevioIndenizado, ")
            .append("         numeroEmpenho, ")
            .append("         anoEmpenho, ")
            .append("         dataEmpenho ");
        Query q = em.createNativeQuery(sb.toString());
        q.setParameter("mes", sicap.getMes().getNumeroMesIniciandoEmZero());
        q.setParameter("ano", sicap.getExercicio().getAno());
        q.setParameter("dataReferencia", getDataParametro());
        q.setParameter("tipo", TipoFolhaDePagamento.RESCISAO.name());
        q.setParameter("tipoComp", TipoFolhaDePagamento.RESCISAO_COMPLEMENTAR.name());
        q.setParameter("idsFolha", sicap.getFolhasPagamentosRescisao());
        List<Object[]> resultado = (List<Object[]>) q.getResultList();
        if (!resultado.isEmpty()) {
            List<Object[]> retorno = Lists.newArrayList();
            verificarAndAdicionarHierarquiaEntidade(retorno, resultado, 15);
            return retorno;
        }
        return Lists.newArrayList();
    }

    private List<Object[]> buscarVerbasDoContraCheques(Long idFichaFinanceira) {
        StringBuilder sb = new StringBuilder();
        sb.append("select ficha.id, ");
        sb.append("  evento.codigo as codigoVerba, ");
        sb.append("  item.ValorReferencia as referencia, ");
        sb.append("  item.valor as valor ");
        sb.append("from FolhaDePagamento folha ");
        sb.append("  inner join FichaFinanceiraFP ficha on ficha.folhaDePagamento_id = folha.id ");
        sb.append("  inner join itemFichaFinanceiraFP item on item.fichaFinanceiraFP_id = ficha.id ");
        sb.append("  inner join EventoFP evento on evento.id = item.eventofp_id ");
        sb.append("  inner join VinculoFP v on v.id = ficha.vinculoFP_id ");
        sb.append("  inner join MatriculaFP mat on mat.id = v.matriculaFP_id ");
        sb.append("  inner join PessoaFisica pf on pf.id = mat.pessoa_id ");
        sb.append("  inner join UnidadeOrganizacional un on un.id = v.unidadeOrganizacional_id ");
        sb.append("  left join ContratoFP c on c.id = v.id ");
        sb.append("  left join Cargo cargo on cargo.id = c.cargo_id ");
        sb.append("where ficha.id = :id and evento.tipoeventofp  <> 'INFORMATIVO'");
        Query q = em.createNativeQuery(sb.toString());
        q.setParameter("id", idFichaFinanceira);
        if (!q.getResultList().isEmpty()) {
            return (List<Object[]>) q.getResultList();
        }
        return new ArrayList<>();
    }

    private List<Object[]> buscarServidores() {
        List<Object[]> servidores = Lists.newLinkedList();

        List<Object[]> aposentados = buscarServidoresAposentados();
        List<Object[]> instituidoresPensao = buscarServidoresQueSaoInstituidoresDePensao();

        if (!aposentados.isEmpty() || !instituidoresPensao.isEmpty()) {
            servidores.addAll(aposentados);
            servidores.addAll(instituidoresPensao);
            existeAposentadosInstituidorPensao = true;
        }
        servidores.addAll(buscarServidoresAtivos());
        return servidores;
    }

    private List<Object[]> buscarServidoresAtivos() {
        StringBuilder sb = new StringBuilder();
        sb.append(" select pf.id,  ");
        sb.append("        pf.nome,  ");
        sb.append("        replace(replace(pf.cpf, '.',''), '-','') as cpf,  ");
        sb.append("        pf.datanascimento,  ");
        sb.append("        pf.mae,  ");
        sb.append("        substr(pf.sexo,0, 1) as sexo,  ");
        sb.append("        grupo.nome as codigo,  ");
        sb.append("        mat.matricula as matricula, ");
        sb.append("  (select replace(replace(max(PISPASEP),'.',''),'-','') as pisPasep  from CARTEIRATRABALHO ct inner join DOCUMENTOPESSOAL doc on ct.ID = doc.ID where doc.pessoafisica_id = pf.id) as pisPasep ");
        sb.append("  from vinculoFp vinc ");
        sb.append(" inner join matriculafp mat on vinc.matriculafp_id = mat.id ");
        sb.append(" inner join pessoafisica pf on mat.pessoa_id = pf.id ");
        sb.append(" left join contratofp cont on vinc.id = cont.id ");
        sb.append(" left join modalidadecontratofp moda on moda.id = cont.modalidadecontratofp_id and moda.codigo <> 8 ");

        sb.append(" inner join RecursoDoVinculoFP recv on vinc.id = recv.vinculoFP_id ");
        sb.append(" inner join RecursoFP rec on rec.id = recv.recursoFP_id ");
        sb.append(" inner join AgrupamentoRecursoFP agrupamento on rec.id = agrupamento.recursoFP_id ");
        sb.append(" inner join GrupoRecursoFP grupo on grupo.id = agrupamento.grupoRecursoFP_id ");
        sb.append(" where ");
        sb.append(" to_date(to_char(:dataReferencia,'mm/yyyy'),'mm/yyyy') between to_date(to_char(vinc.iniciovigencia,'mm/yyyy'),'mm/yyyy') and to_date(to_char(coalesce(vinc.finalvigencia,:dataReferencia),'mm/yyyy'),'mm/yyyy') ");
        sb.append(" and to_date(to_char(:dataReferencia,'mm/yyyy'),'mm/yyyy') between to_date(to_char(recv.iniciovigencia,'mm/yyyy'),'mm/yyyy') and to_date(to_char(coalesce(recv.finalvigencia,:dataReferencia),'mm/yyyy'),'mm/yyyy') ");
        sb.append(" and to_date(to_char(:dataReferencia,'mm/yyyy'),'mm/yyyy') between to_date(to_char(rec.iniciovigencia,'mm/yyyy'),'mm/yyyy') and to_date(to_char(coalesce(rec.finalVigencia,:dataReferencia),'mm/yyyy'),'mm/yyyy') ");
        sb.append(" and vinc.id not in(select id from pensionista ) ");
        sb.append(" group by pf.id, ");
        sb.append("          pf.nome, ");
        sb.append("          pf.cpf, ");
        sb.append("          pf.datanascimento, ");
        sb.append("          pf.mae, ");
        sb.append("          pf.sexo, ");
        sb.append("          mat.matricula, ");
        sb.append("          grupo.nome, ");
        sb.append("          8 ");
        Query q = em.createNativeQuery(sb.toString());
        q.setParameter("dataReferencia", Util.getDataHoraMinutoSegundoZerado(sicap.getUltimoDiaDoMes()));
        if (!q.getResultList().isEmpty()) {
            List<Object[]> retorno = new ArrayList<>();
            Set<ServidorSicap> retornoDistint = new HashSet<>();
            List<Object[]> resultList = (List<Object[]>) q.getResultList();
            verificarAndAdicionarHierarquiaEntidade(retorno, resultList, 6);
            //Remover os duplicados de retorno da query.
            for (Object[] objects : retorno) {
                ServidorSicap servidor = new ServidorSicap(((BigDecimal) objects[0]), (String) objects[1], (String) objects[2], (Date) objects[3], (String) objects[4], (String) objects[5], null, (String) objects[7], (String) objects[8]);
                retornoDistint.add(servidor);
            }
            retorno.clear();
            for (ServidorSicap se : retornoDistint) {
                retorno.add(new Object[]{se.getId(), se.getNome(), se.getCpf(), se.getDataNascimento(), se.getMae(), se.getSexo(), null, se.getMatricula(), se.getNitPisPasep()});
            }
            return retorno;
        }
        return new ArrayList<>();
    }

    private List<Object[]> buscarServidoresAposentados() {
        StringBuilder sb = new StringBuilder();
        sb.append(" select pf.id,  ");
        sb.append("        pf.nome,  ");
        sb.append("        replace(replace(pf.cpf, '.',''), '-','') as cpf,  ");
        sb.append("        pf.datanascimento,  ");
        sb.append("        pf.mae,  ");
        sb.append("        substr(pf.sexo,0, 1) as sexo,  ");
        sb.append("        grupo.nome as codigo,  ");
        sb.append("        mat.matricula as matricula, ");
        sb.append("        (select replace(replace(max(PISPASEP),'.',''),'-','') as pisPasep  from CARTEIRATRABALHO ct inner join DOCUMENTOPESSOAL doc on ct.ID = doc.ID where doc.pessoafisica_id = pf.id) as pisPasep ");
        sb.append("  from vinculoFp vinc ");
        sb.append(" inner join matriculafp mat on vinc.matriculafp_id = mat.id ");
        sb.append(" inner join pessoafisica pf on mat.pessoa_id = pf.id ");
        sb.append(" inner join aposentadoria apos on apos.id = vinc.id ");
        sb.append(" inner join contratofp cont on cont.id = apos.contratofp_id ");
        sb.append(" left join modalidadecontratofp moda on moda.id = cont.modalidadecontratofp_id and moda.codigo <> 8 ");
        sb.append(" inner join RecursoDoVinculoFP recv on vinc.id = recv.vinculoFP_id ");
        sb.append(" inner join RecursoFP rec on rec.id = recv.recursoFP_id ");
        sb.append(" inner join AgrupamentoRecursoFP agrupamento on rec.id = agrupamento.recursoFP_id ");
        sb.append(" inner join GrupoRecursoFP grupo on grupo.id = agrupamento.grupoRecursoFP_id ");
        sb.append(" where ");
        sb.append(" to_date(to_char(:dataReferencia,'mm/yyyy'),'mm/yyyy') between to_date(to_char(vinc.iniciovigencia,'mm/yyyy'),'mm/yyyy') and to_date(to_char(coalesce(vinc.finalvigencia,:dataReferencia),'mm/yyyy'),'mm/yyyy') ");
        sb.append(" and to_date(to_char(:dataReferencia,'mm/yyyy'),'mm/yyyy') between to_date(to_char(recv.iniciovigencia,'mm/yyyy'),'mm/yyyy') and to_date(to_char(coalesce(recv.finalvigencia,:dataReferencia),'mm/yyyy'),'mm/yyyy') ");
        sb.append(" and to_date(to_char(:dataReferencia,'mm/yyyy'),'mm/yyyy') between to_date(to_char(rec.iniciovigencia,'mm/yyyy'),'mm/yyyy') and to_date(to_char(coalesce(rec.finalVigencia,:dataReferencia),'mm/yyyy'),'mm/yyyy') ");
        sb.append(" group by pf.id, ");
        sb.append("          pf.nome, ");
        sb.append("          pf.cpf, ");
        sb.append("          pf.datanascimento, ");
        sb.append("          pf.mae, ");
        sb.append("          pf.sexo, ");
        sb.append("          mat.matricula, ");
        sb.append("          grupo.nome, 8 ");
        Query q = em.createNativeQuery(sb.toString());
        q.setParameter("dataReferencia", Util.getDataHoraMinutoSegundoZerado(sicap.getUltimoDiaDoMes()));
        if (!q.getResultList().isEmpty()) {
            List<Object[]> retorno = new ArrayList<>();
            Set<ServidorSicap> retornoDistint = new HashSet<>();
            List<Object[]> resultList = (List<Object[]>) q.getResultList();
            verificarAndAdicionarHierarquiaEntidade(retorno, resultList, 6);
            //Remover os duplicados de retorno da query.
            for (Object[] objects : retorno) {
                ServidorSicap servidor = new ServidorSicap(((BigDecimal) objects[0]), (String) objects[1], (String) objects[2], (Date) objects[3], (String) objects[4], (String) objects[5], null, (String) objects[7], (String) objects[8]);
                retornoDistint.add(servidor);
            }
            retorno.clear();
            for (ServidorSicap se : retornoDistint) {
                retorno.add(new Object[]{se.getId(), se.getNome(), se.getCpf(), se.getDataNascimento(), se.getMae(), se.getSexo(), null, se.getMatricula(), se.getNitPisPasep()});
            }
            return retorno;
        }
        return new ArrayList<>();
    }

    private List<Object[]> buscarServidoresQueSaoInstituidoresDePensao() {
        StringBuilder sb = new StringBuilder();
        sb.append(" select pf.id,  ");
        sb.append("        pf.nome,  ");
        sb.append("        replace(replace(pf.cpf, '.',''), '-','') as cpf,  ");
        sb.append("        pf.datanascimento,  ");
        sb.append("        pf.mae,  ");
        sb.append("        substr(pf.sexo,0, 1) as sexo,  ");
        sb.append("        grupo.nome as codigo, ");
        sb.append("        mat.matricula as matricula, ");
        sb.append("        (select replace(replace(max(PISPASEP),'.',''),'-','') as pisPasep  from CARTEIRATRABALHO ct inner join DOCUMENTOPESSOAL doc on ct.ID = doc.ID where doc.pessoafisica_id = pf.id) as pisPasep ");
        sb.append("  from vinculoFp vinc ");
        sb.append(" inner join matriculafp mat on vinc.matriculafp_id = mat.id ");
        sb.append(" inner join pessoafisica pf on mat.pessoa_id = pf.id ");
        sb.append(" inner join contratofp cont on vinc.id = cont.id ");
        sb.append(" inner join pensao pen on pen.contratofp_id = cont.id ");
        sb.append(" inner join pensionista pensionista on pensionista.pensao_id = pen.id ");
        sb.append(" inner join vinculoFP vinculoPen on vinculoPen.id = pensionista.id ");

        sb.append(" inner join RecursoDoVinculoFP recv on vinculoPen.id = recv.vinculoFP_id ");
        sb.append(" inner join RecursoFP rec on rec.id = recv.recursoFP_id ");
        sb.append(" inner join AgrupamentoRecursoFP agrupamento on rec.id = agrupamento.recursoFP_id ");
        sb.append(" inner join GrupoRecursoFP grupo on grupo.id = agrupamento.grupoRecursoFP_id ");

        sb.append(" where ");
        sb.append(" to_date(to_char(:dataReferencia,'mm/yyyy'),'mm/yyyy') between to_date(to_char(vinculoPen.iniciovigencia,'mm/yyyy'),'mm/yyyy') and to_date(to_char(coalesce(vinculoPen.finalvigencia,:dataReferencia),'mm/yyyy'),'mm/yyyy') ");
        sb.append(" and to_date(to_char(:dataReferencia,'mm/yyyy'),'mm/yyyy') between to_date(to_char(recv.iniciovigencia,'mm/yyyy'),'mm/yyyy') and to_date(to_char(coalesce(recv.finalvigencia,:dataReferencia),'mm/yyyy'),'mm/yyyy') ");
        sb.append(" and to_date(to_char(:dataReferencia,'mm/yyyy'),'mm/yyyy') between to_date(to_char(rec.iniciovigencia,'mm/yyyy'),'mm/yyyy') and to_date(to_char(coalesce(rec.finalVigencia,:dataReferencia),'mm/yyyy'),'mm/yyyy') ");
        sb.append(" group by pf.id, ");
        sb.append("          pf.nome, ");
        sb.append("          pf.cpf, ");
        sb.append("          pf.datanascimento, ");
        sb.append("          pf.mae, ");
        sb.append("          pf.sexo, ");
        sb.append("          mat.matricula, ");
        sb.append("          grupo.nome, 8 ");
        Query q = em.createNativeQuery(sb.toString());
        q.setParameter("dataReferencia", Util.getDataHoraMinutoSegundoZerado(sicap.getUltimoDiaDoMes()));
        if (!q.getResultList().isEmpty()) {
            List<Object[]> retorno = new ArrayList<>();
            Set<ServidorSicap> retornoDistint = new HashSet<>();
            List<Object[]> resultList = (List<Object[]>) q.getResultList();
            verificarAndAdicionarHierarquiaEntidade(retorno, resultList, 6);
            //Remover os duplicados de retorno da query.
            for (Object[] objects : retorno) {
                ServidorSicap servidor = new ServidorSicap(((BigDecimal) objects[0]), (String) objects[1], (String) objects[2], (Date) objects[3], (String) objects[4], (String) objects[5], null, (String) objects[7], (String) objects[8]);
                retornoDistint.add(servidor);
            }
            retorno.clear();
            for (ServidorSicap se : retornoDistint) {
                retorno.add(new Object[]{se.getId(), se.getNome(), se.getCpf(), se.getDataNascimento(), se.getMae(), se.getSexo(), null, se.getMatricula(), se.getNitPisPasep()});
            }
            return retorno;
        }
        return new ArrayList<>();
    }


    public void verificarAndAdicionarHierarquiaEntidade(List<Object[]> retorno, List<Object[]> lista, int indice) {
        for (Object[] objects : lista) {
            String codigo = (String) objects[indice];
            if (podeAdicionarNoRetorno(codigo)) {
                retorno.add(objects);
            }
        }
    }

    private boolean podeAdicionarNoRetorno(String codigo) {
        for (GrupoRecursoFP grupoRecursoFP : sicap.getGruposRecursos()) {
            if (codigo.contains(grupoRecursoFP.getNome())) {
                return true;
            }
        }
        return false;
    }

   /* private boolean verificarHierarquiaBloqueada(List<HierarquiaOrganizacional> hierarquiasBloqueadas, HierarquiaOrganizacional hierarquiaOrganizacional, String codigo) {
        if(hierarquiasBloqueadas.isEmpty()){
            return true;
        }
        for (HierarquiaOrganizacional hierarquiasBloqueada : hierarquiasBloqueadas) {
            if (codigo.contains(hierarquiasBloqueada.getCodigoSemZerosFinais())) {
                if(hierarquiaOrganizacional.getIndiceDoNo().compareTo(hierarquiasBloqueada.getIndiceDoNo()) > 0){
                    return true;
                }
                return false;
            }
        }
        return true;
    }*/

    private List<Object[]> buscarPensoes(Long idPensao) {
        StringBuilder sb = new StringBuilder();
        sb.append("select distinct pfPens.nome, replace(replace(pfPens.cpf, '.',''), '-','') as cpfInstituidor, matPens.matricula || '0' ||  vinPens.numero as matriculaInstituidor,vin.INICIOVIGENCIA as dataInicio, ");
        sb.append("grauPens.CODIGO as tipoDependente, mat.matricula || '0' || vin.numero as matriculaPensionista, ");
        sb.append("case pens.TIPOPENSAO ");
        sb.append("when 'VITALICIA' then 2 ");
        sb.append("when 'VITALICIA_INVALIDEZ' then 2 ");
        sb.append("when 'TEMPORARIA_INVALIDEZ' then 1 ");
        sb.append("when 'TEMPORARIA' then 1 ");
        sb.append("else null end as tipoPensao, ");
        sb.append("    vin.FINALVIGENCIA ");
        sb.append("        from pensao ");

        sb.append("inner join pensionista pens on pensao.id = pens.pensao_id ");
        sb.append("inner join vinculofp vin on vin.id = pens.id ");
        sb.append("inner join matriculafp mat on mat.id = vin.matriculafp_id ");
        sb.append("inner join pessoafisica pf on pf.id = mat.PESSOA_ID ");

        sb.append("inner join vinculofp vinPens on vinPens.id = pensao.contratofp_id ");
        sb.append("inner join matriculafp matPens on matPens.id = vinpens.MATRICULAFP_ID ");
        sb.append("inner join pessoafisica pfPens on pfPens.id = matPens.PESSOA_ID ");
        sb.append("inner join grauparentescopensionista grauPens on pens.GRAUPARENTESCOPENSIONISTA_ID = grauPens.id ");
        sb.append(" where pens.id = :idPensao ");
        sb.append(" and to_date(to_char(:dataReferencia,'mm/yyyy'),'mm/yyyy') between to_date(to_char(vin.inicioVigencia,'mm/yyyy'),'mm/yyyy') and to_date(to_char(coalesce(vin.finalVigencia,:dataReferencia),'mm/yyyy'),'mm/yyyy') ");
        Query q = em.createNativeQuery(sb.toString());
        q.setParameter("idPensao", idPensao);
        q.setParameter("dataReferencia", getDataParametro());
        if (!q.getResultList().isEmpty()) {
            return (List<Object[]>) q.getResultList();
        }
        return new ArrayList<>();
    }

    private List<Object[]> buscarVinculos(Long idPessoa, String matricula) {
        StringBuilder sb = new StringBuilder();
        sb.append(" select distinct mat.matricula || '0' ||  vinc.numero ,  ");
        sb.append("        vinc.iniciovigencia as dataAdmissao,  ");
        sb.append("        cargo.id,  ");
        sb.append("        1 as tipoServidor, ");
        sb.append(" cont.tipoVinculoSicap as tipoVinculo, ");
        sb.append(" coalesce((select case tipoprev.codigo when 3 then 2 else 1 end ");//2 RPPS // 1 RGPS
        sb.append(" from previdenciavinculofp prev inner join tipoprevidenciafp tipoprev on prev.tipoprevidenciafp_id = tipoprev.id ");
        sb.append(" where prev.contratofp_id = cont.id and rownum = 1 and prev.iniciovigencia = (select max(prevfinal.iniciovigencia) from previdenciavinculofp prevfinal where prevfinal.contratofp_id = cont.id)) ,1) ");
        sb.append("  as regimePrevidenciario ");
        sb.append("  from vinculoFp vinc ");
        sb.append(" inner join matriculafp mat on vinc.matriculafp_id = mat.id ");
        sb.append(" inner join pessoafisica pf on mat.pessoa_id = pf.id ");
        sb.append(" inner join contratofp cont on vinc.id = cont.id ");
        sb.append(" inner join tiporegime tr on cont.tiporegime_id = tr.id ");
        sb.append(" inner join cargo cargo on cont.cargo_id = cargo.id ");
        sb.append(" inner join modalidadecontratofp moda on moda.id = cont.modalidadecontratofp_id ");
        sb.append(" where  ");
        sb.append(" to_date(to_char(:dataParam,'mm/yyyy'),'mm/yyyy') between to_date(to_char(vinc.inicioVigencia,'mm/yyyy'),'mm/yyyy') and to_date(to_char(coalesce(vinc.finalVigencia,:dataParam),'mm/yyyy'),'mm/yyyy')");
        sb.append(" and pf.id = :idPessoa ");
        sb.append(" and moda.codigo <> 8 ");
        Query q = em.createNativeQuery(sb.toString());
        q.setParameter("idPessoa", idPessoa);
        q.setParameter("dataParam", getDataParametro());
        if (!q.getResultList().isEmpty()) {
            return (List<Object[]>) q.getResultList();
        } else {
            return buscarVinculosPensaoTransitadaAndJulgada(idPessoa, matricula);
        }

    }

    private List<Object[]> buscarVinculosAposentadosAndPensionistas(Long idPessoa, String matricula) {
        StringBuilder sb = new StringBuilder();
        sb.append(" select distinct mat.matricula || '0' ||  vinc.numero ,  ");
        sb.append("        vinc.iniciovigencia as dataAdmissao,  ");
        sb.append("        cargo.id,  ");
        sb.append("        1 as tipoServidor, ");
        sb.append(" cont.tipoVinculoSicap as tipoVinculo, ");
        sb.append(" coalesce((select case tipoprev.codigo when 3 then 2 else 1 end ");//2 RPPS // 1 RGPS
        sb.append(" from previdenciavinculofp prev inner join tipoprevidenciafp tipoprev on prev.tipoprevidenciafp_id = tipoprev.id ");
        sb.append(" where prev.contratofp_id = cont.id and rownum = 1 and prev.iniciovigencia = (select max(prevfinal.iniciovigencia) from previdenciavinculofp prevfinal where prevfinal.contratofp_id = cont.id)) ,1) ");
        sb.append("  as regimePrevidenciario ");
        sb.append("  from vinculoFp vinc ");
        sb.append(" inner join matriculafp mat on vinc.matriculafp_id = mat.id ");
        sb.append(" inner join pessoafisica pf on mat.pessoa_id = pf.id ");
        sb.append(" inner join aposentadoria apos ON apos.id = vinc.id ");
        sb.append(" inner join contratofp cont ON cont.id = apos.contratofp_id ");
        sb.append(" inner join tiporegime tr on cont.tiporegime_id = tr.id ");
        sb.append(" inner join cargo cargo on cont.cargo_id = cargo.id ");
        sb.append(" inner join modalidadecontratofp moda on moda.id = cont.modalidadecontratofp_id ");
        sb.append(" where  ");
        sb.append(" to_date(to_char(:dataParam,'mm/yyyy'),'mm/yyyy') between to_date(to_char(vinc.inicioVigencia,'mm/yyyy'),'mm/yyyy') and to_date(to_char(coalesce(vinc.finalVigencia,:dataParam),'mm/yyyy'),'mm/yyyy')");
        sb.append(" and pf.id = :idPessoa ");
        sb.append(" and moda.codigo <> 8 ");
        sb.append(" union ");
        sb.append(" select distinct mat.matricula || '0' ||  vinc.numero ,  ");
        sb.append("        vinc.iniciovigencia as dataAdmissao,  ");
        sb.append("        cargo.id,  ");
        sb.append("        1 as tipoServidor, ");
        sb.append(" cont.tipoVinculoSicap as tipoVinculo, ");
        sb.append(" coalesce((select case tipoprev.codigo when 3 then 2 else 1 end ");//2 RPPS // 1 RGPS
        sb.append(" from previdenciavinculofp prev inner join tipoprevidenciafp tipoprev on prev.tipoprevidenciafp_id = tipoprev.id ");
        sb.append(" where prev.contratofp_id = cont.id and rownum = 1 and prev.iniciovigencia = (select max(prevfinal.iniciovigencia) from previdenciavinculofp prevfinal where prevfinal.contratofp_id = cont.id)) ,1) ");
        sb.append("  as regimePrevidenciario ");
        sb.append("  from vinculoFp vinc ");
        sb.append(" inner join matriculafp mat on vinc.matriculafp_id = mat.id ");
        sb.append(" inner join pessoafisica pf on mat.pessoa_id = pf.id ");
        sb.append(" inner join pensionista pens ON vinc.id = pens.id ");
        sb.append(" inner join pensao on pensao.id = pens.pensao_id ");
        sb.append(" inner join contratofp cont ON cont.id = pensao.contratofp_id ");
        sb.append(" inner join tiporegime tr on cont.tiporegime_id = tr.id ");
        sb.append(" inner join cargo cargo on cont.cargo_id = cargo.id ");
        sb.append(" inner join modalidadecontratofp moda on moda.id = cont.modalidadecontratofp_id ");
        sb.append(" where  ");
        sb.append(" to_date(to_char(:dataParam,'mm/yyyy'),'mm/yyyy') between to_date(to_char(vinc.inicioVigencia,'mm/yyyy'),'mm/yyyy') and to_date(to_char(coalesce(vinc.finalVigencia,:dataParam),'mm/yyyy'),'mm/yyyy')");
        sb.append(" and pf.id = :idPessoa ");
        sb.append(" and moda.codigo <> 8 ");
        Query q = em.createNativeQuery(sb.toString());
        q.setParameter("idPessoa", idPessoa);
        q.setParameter("dataParam", getDataParametro());
        if (!q.getResultList().isEmpty()) {
            return (List<Object[]>) q.getResultList();
        } else {
            return buscarVinculosPensaoTransitadaAndJulgada(idPessoa, matricula);
        }

    }

    private List<Object[]> buscarVinculosVigenteNaPensao(Long idPessoa, String matricula) {
        StringBuilder sb = new StringBuilder();
        sb.append("select      distinct mat.matricula || '0' || vinc.NUMERO ,    ");
        sb.append("                vinc.iniciovigencia as dataAdmissao,    ");
        sb.append("                cargo.id,    ");
        sb.append("                1 as tipoServidor,   ");
        sb.append("                 cont.tipoVinculoSicap as tipoVinculo, ");
        sb.append("                 case tipoprev.codigo   ");
        sb.append("                    when 3 then 2     ");
        sb.append("                    else 1   ");
        sb.append("                 end as regimePrevidenciario   ");
        sb.append("          from vinculoFp vinc   ");
        sb.append("         inner join matriculafp mat on vinc.matriculafp_id = mat.id   ");
        sb.append("         inner join pessoafisica pf on mat.pessoa_id = pf.id   ");
        sb.append("         inner join contratofp cont on vinc.id = cont.id   ");
        sb.append("         inner join tiporegime tr on cont.tiporegime_id = tr.id   ");
        sb.append("         inner join previdenciavinculofp prev on prev.contratofp_id = cont.id   ");
        sb.append("         inner join tipoprevidenciafp tipoprev on prev.tipoprevidenciafp_id = tipoprev.id   ");
        sb.append("         inner join cargo cargo on cont.cargo_id = cargo.id   ");
        sb.append("         inner join modalidadecontratofp moda on moda.id = cont.modalidadecontratofp_id   ");
        sb.append("         inner join pensao pensao on pensao.CONTRATOFP_ID = cont.id");
        sb.append("         inner join Pensionista pensionista on pensionista.PENSAO_ID = pensao.id");
        sb.append("         inner join vinculofp vinculopensionista on pensionista.id = vinculopensionista.id      ");
        sb.append("         where prev.iniciovigencia = (select max(prevfinal.iniciovigencia) from previdenciavinculofp prevfinal where prevfinal.contratofp_id = cont.id)   ");
        sb.append("         and :dataParam between vinculopensionista.iniciovigencia and coalesce(vinculopensionista.FINALVIGENCIA, :dataParam)");
        sb.append("         and mat.matricula = :matricula ");
        sb.append("         and pf.id = :idPessoa");
        sb.append("         and moda.codigo <> 8");
        Query q = em.createNativeQuery(sb.toString());
        q.setParameter("idPessoa", idPessoa);
        q.setParameter("matricula", matricula);
        q.setParameter("dataParam", getDataParametro());
        if (!q.getResultList().isEmpty()) {
            return (List<Object[]>) q.getResultList();
        } else {
            return buscarPrimeiroVinculosSemVigencia(idPessoa, matricula);
        }
    }

    private List<Object[]> buscarPrimeiroVinculosSemVigencia(Long idPessoa, String matricula) {
        StringBuilder sb = new StringBuilder();
        sb.append("  select distinct mat.matricula || '0' ||  '1' ,  ");
        sb.append("        vinc.iniciovigencia as dataAdmissao,  ");
        sb.append("        cargo.id,  ");
        sb.append("        1 as tipoServidor, ");
        sb.append("         cont.tipoVinculoSicap as tipoVinculo, ");
        sb.append("         case tipoprev.codigo ");
        sb.append("            when 3 then 2  "); //RPPS
        sb.append("            else 1 "); // RGPS
        sb.append("         end as regimePrevidenciario ");
        sb.append("  from vinculoFp vinc ");
        sb.append(" inner join matriculafp mat on vinc.matriculafp_id = mat.id ");
        sb.append(" inner join pessoafisica pf on mat.pessoa_id = pf.id ");
        sb.append(" inner join contratofp cont on vinc.id = cont.id ");
        sb.append(" inner join tiporegime tr on cont.tiporegime_id = tr.id ");
        sb.append(" inner join previdenciavinculofp prev on prev.contratofp_id = cont.id ");
        sb.append(" inner join tipoprevidenciafp tipoprev on prev.tipoprevidenciafp_id = tipoprev.id ");
        sb.append(" inner join cargo cargo on cont.cargo_id = cargo.id ");
        sb.append(" inner join modalidadecontratofp moda on moda.id = cont.modalidadecontratofp_id ");
        sb.append(" where prev.iniciovigencia = (select max(prevfinal.iniciovigencia) from previdenciavinculofp prevfinal where prevfinal.contratofp_id = cont.id) ");
        sb.append(" and vinc.inicioVigencia = (select max(v.inicioVigencia) from vinculofp v inner join contratofp c on c.id = v.id inner join matriculafp m on m.id = v.matriculafp_id where m.id = mat.id) ");
        sb.append(" and pf.id = :idPessoa ");
        sb.append(" and mat.matricula = :matricula ");
        sb.append(" and moda.codigo <> 8 ");
        Query q = em.createNativeQuery(sb.toString());
        q.setParameter("idPessoa", idPessoa);
        q.setParameter("matricula", matricula);
        if (!q.getResultList().isEmpty()) {
            return (List<Object[]>) q.getResultList();
        } else {
            return new ArrayList<>();
        }
    }

    private List<Object[]> buscarVinculosPensaoTransitadaAndJulgada(Long idPessoa, String matricula) {
        StringBuilder sb = new StringBuilder();
        sb.append(" select distinct mat.matricula || '0' ||  vinc.numero , ");
        sb.append(" vinc.iniciovigencia as dataAdmissao, ");
        sb.append(" 638882892 as codigodocargo, ");
        sb.append(" 1 as tipoServidor, ");
        sb.append(" 'SERVIDOR_ESTAVEL_NAO_EFETIVO_NA_FORMA_DO_ART_19_DO_ADCT' as tipoVinculo, ");
        sb.append(" 2 as regimePrevidenciario  ");
        sb.append(" from vinculoFp vinc  ");
        sb.append(" inner join matriculafp mat on vinc.matriculafp_id = mat.id ");
        sb.append(" inner join pessoafisica pf on mat.pessoa_id = pf.id ");
        sb.append(" inner join Beneficiario cont on vinc.id = cont.id ");
        sb.append(" where  to_date(to_char(:dataParam,'mm/yyyy'),'mm/yyyy') between to_date(to_char(vinc.inicioVigencia,'mm/yyyy'),'mm/yyyy') and to_date(to_char(coalesce(vinc.finalVigencia,:dataParam),'mm/yyyy'),'mm/yyyy') ");
        sb.append(" and pf.id = :idPessoa ");
        Query q = em.createNativeQuery(sb.toString());
        q.setParameter("idPessoa", idPessoa);
        q.setParameter("dataParam", getDataParametro());
        if (!q.getResultList().isEmpty()) {
            return (List<Object[]>) q.getResultList();
        }
        return buscarVinculosVigenteNaPensao(idPessoa, matricula);
    }

    private List<Object[]> buscarUnidadesLotacao() {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT DISTINCT vw.codigo, ");
        sb.append("       UN.DESCRICAO AS NOME,");
        sb.append("       1200401 as municipio");
        sb.append("  FROM UNIDADEORGANIZACIONAL UN ");
        sb.append(" inner join vwhierarquiaadministrativa vw on un.id = vw.subordinada_id ");
        sb.append(" where  to_date(to_char(:dataReferencia,'mm/yyyy'),'mm/yyyy') between to_date(to_char(vw.inicioVigencia,'mm/yyyy'),'mm/yyyy') and to_date(to_char(coalesce(vw.fimVigencia,:dataReferencia),'mm/yyyy'),'mm/yyyy') ");
        sb.append(" ORDER BY nome ");

        Query q = em.createNativeQuery(sb.toString());
        q.setParameter("dataReferencia", getDataParametro());
        if (!q.getResultList().isEmpty()) {
            return (List<Object[]>) q.getResultList();
        }
        return new ArrayList<>();
    }

    private List<Object[]> buscarTabelas() {
        StringBuilder sb = new StringBuilder();
        sb.append(" select distinct progPai.id, progpai.codigo, progPai.descricao from ENQUADRAMENTOPCS pcs ");
        sb.append(" inner join progressaopcs prog on prog.id = pcs.progressaopcs_id ");
        sb.append(" inner join progressaopcs progPai on progPai.id = prog.SUPERIOR_ID ");
        sb.append(" inner join categoriapcs cat on pcs.categoriapcs_id = cat.id ");
        sb.append(" inner join categoriapcs catPai on catPai.id = cat.SUPERIOR_ID ");
        sb.append(" inner join CARGOCATEGORIAPCS cargocat on cargocat.CATEGORIAPCS_ID = cat.id ");
        sb.append(" inner join cargo cargo on cargo.id = cargocat.cargo_id ");
        sb.append(" where to_date(to_char(:dataReferencia,'mm/yyyy'),'mm/yyyy') between to_date(to_char(pcs.inicioVigencia,'mm/yyyy'),'mm/yyyy') and to_date(to_char(coalesce(pcs.finalVigencia,:dataReferencia),'mm/yyyy'),'mm/yyyy') ");
        //sb.append(" and to_date(to_char(:dataReferencia,'dd/MM/yyyy'),'dd/mm/yyyy') between to_date(to_char(v.iniciovigencia,'dd/mm/yyyy'),'dd/mm/yyyy') and to_date(to_char(coalesce(v.finalvigencia,:dataReferencia),'dd/mm/yyyy'),'dd/mm/yyyy') ");
        sb.append("  order by progpai.codigo ");
        Query q = em.createNativeQuery(sb.toString());
        q.setParameter("dataReferencia", getDataParametro());
        if (!q.getResultList().isEmpty()) {
            return (List<Object[]>) q.getResultList();
        }
        return new ArrayList<>();
    }


    private Object[] buscarTabelaVinculo(Long idVinculo) {
        StringBuilder sb = new StringBuilder();
        sb.append("select distinct progpai.codigo, prog.descricao from ENQUADRAMENTOPCS pcs ");
        sb.append("inner join progressaopcs prog on prog.id = pcs.progressaopcs_id ");
        sb.append("inner join progressaopcs progPai on progPai.id = prog.SUPERIOR_ID ");
        sb.append("inner join categoriapcs cat on pcs.categoriapcs_id = cat.id ");
        sb.append("inner join categoriapcs catPai on catPai.id = cat.SUPERIOR_ID ");
        sb.append("inner join enquadramentoFuncional enq on enq.categoriaPcs_id = cat.id and prog.id = enq.progressaopcs_id ");
        sb.append("  where enq.contratoServidor_id = :idVinculo and to_date(to_char(:dataReferencia,'mm/yyyy'),'mm/yyyy') between to_date(to_char(pcs.inicioVigencia,'mm/yyyy'),'mm/yyyy') and to_date(to_char(coalesce(pcs.finalVigencia,:dataReferencia),'mm/yyyy'),'mm/yyyy') ");
        sb.append(" and to_date(to_char(enq.iniciovigencia,'dd/mm/yyyy'),'dd/mm/yyyy') =  (select max(efv.inicioVigencia) from enquadramentoFuncional efv where efv.contratoServidor_id = enq.contratoServidor_id ");
        sb.append(" and extract (year from :dataReferencia) BETWEEN extract (year from efv.inicioVigencia) and coalesce (extract (year from efv.FINALVIGENCIA),extract (year from sysdate)) and efv.id in (enq.id))");
        sb.append(" order by progpai.codigo, prog.descricao ");
        Query q = em.createNativeQuery(sb.toString());
        q.setParameter("dataReferencia", getDataParametro());
        q.setParameter("idVinculo", idVinculo);
        if (!q.getResultList().isEmpty()) {
            return (Object[]) q.getResultList().get(0);
        }
        return null;
    }

    private Object[] buscarTabelaVinculoAposentado(Long idVinculo) {
        StringBuilder sb = new StringBuilder();
        sb.append(" select distinct ");
        sb.append("        catPai.codigo as tabelaCodigo,  ");
        sb.append("        trim(catPai.descricao) ||' - '|| trim(cat.descricao) as tabelaNome ");
        sb.append("   from enquadramentopcs pcs  ");
        sb.append("  inner join categoriapcs cat on cat.id = pcs.categoriapcs_id ");
        sb.append("  inner join progressaopcs prog on prog.id = pcs.progressaopcs_id ");
        sb.append("  inner join categoriapcs catPai on catPai.id = cat.superior_id ");
        sb.append("  inner join progressaopcs progPai on progPai.id = prog.superior_id ");
        sb.append("  inner join planocargossalarios plano on plano.id = cat.planocargossalarios_id and prog.planocargossalarios_id = plano.id ");
        sb.append("  inner join cargocategoriapcs cargoCategoria on cargocategoria.categoriapcs_id = cat.id ");
        sb.append("  inner join enquadramentofuncional ef on ef.categoriapcs_id = cat.id and ef.progressaopcs_id = prog.id ");
        sb.append("  inner join vinculofp v on v.id = ef.contratoServidor_id ");
        sb.append("  inner join contratofp cont on v.id = cont.id ");
        sb.append("  inner join cargo c on c.id = cargocategoria.cargo_id ");
        sb.append("  where v.id = :idVinculo and to_date(to_char(:dataReferencia,'mm/yyyy'),'mm/yyyy') between to_date(to_char(pcs.inicioVigencia,'mm/yyyy'),'mm/yyyy') and to_date(to_char(coalesce(pcs.finalVigencia,:dataReferencia),'mm/yyyy'),'mm/yyyy') ");
        sb.append(" and to_date(to_char(ef.iniciovigencia,'dd/mm/yyyy'),'dd/mm/yyyy') =  (select max(efv.inicioVigencia) from enquadramentoFuncional efv where efv.contratoServidor_id = v.id)   ");
        sb.append("  order by catPai.codigo, trim(catPai.descricao) ||' - '|| trim(cat.descricao)");
        Query q = em.createNativeQuery(sb.toString());
        q.setParameter("dataReferencia", getDataParametro());
        q.setParameter("idVinculo", idVinculo);
        if (!q.getResultList().isEmpty()) {
            return (Object[]) q.getResultList().get(0);
        }
        return null;
    }


    private List<Object[]> buscarNiveis(Long idTabela) {
        StringBuilder sb = new StringBuilder();
        sb.append("select distinct prog.ordem, prog.descricao, pcs.vencimentoBase from ENQUADRAMENTOPCS pcs ");
        sb.append(" inner join progressaopcs prog on prog.id = pcs.progressaopcs_id ");
        sb.append(" inner join progressaopcs progPai on progPai.id = prog.SUPERIOR_ID ");
        sb.append(" inner join categoriapcs cat on pcs.categoriapcs_id = cat.id ");
        sb.append(" inner join categoriapcs catPai on catPai.id = cat.SUPERIOR_ID ");
        sb.append("  where to_date(to_char(:dataReferencia,'mm/yyyy'),'mm/yyyy') between to_date(to_char(pcs.inicioVigencia,'mm/yyyy'),'mm/yyyy') and to_date(to_char(coalesce(pcs.finalVigencia,:dataReferencia),'mm/yyyy'),'mm/yyyy') ");
        sb.append(" and PCS.VENCIMENTOBASE <> 0 ");
        sb.append(" and progPai.id = :idProgressao ORDER BY PROG.ORDEM, pcs.vencimentoBase ");
        Query q = em.createNativeQuery(sb.toString());
        q.setParameter("idProgressao", idTabela);
        q.setParameter("dataReferencia", getDataParametro());
        if (!q.getResultList().isEmpty()) {
            return (List<Object[]>) q.getResultList();
        }
        return new ArrayList<>();
    }

    private List<BigDecimal> buscarCargosDaTabelaDeVencimento(Long idTabela) {
        StringBuilder sb = new StringBuilder();
        sb.append(" select distinct cargo.id as codigo from ENQUADRAMENTOPCS pcs ");
        sb.append(" inner join progressaopcs prog on prog.id = pcs.progressaopcs_id ");
        sb.append(" inner join progressaopcs progPai on progPai.id = prog.SUPERIOR_ID ");
        sb.append(" inner join categoriapcs cat on pcs.categoriapcs_id = cat.id ");
        sb.append(" inner join categoriapcs catPai on catPai.id = cat.SUPERIOR_ID ");
        sb.append(" inner join CARGOCATEGORIAPCS cargocat on cargocat.CATEGORIAPCS_ID = cat.id ");
        sb.append(" inner join cargo cargo on cargo.id = cargocat.cargo_id  ");
        sb.append("  where to_date(to_char(:dataReferencia,'mm/yyyy'),'mm/yyyy') between to_date(to_char(pcs.inicioVigencia,'mm/yyyy'),'mm/yyyy') and to_date(to_char(coalesce(pcs.finalVigencia,:dataReferencia),'mm/yyyy'),'mm/yyyy') ");
        sb.append("    and progPai.id = :idProgressao ");
        sb.append("  order by cargo.id ");
        Query q = em.createNativeQuery(sb.toString());
        q.setParameter("idProgressao", idTabela);
        q.setParameter("dataReferencia", getDataParametro());
        if (!q.getResultList().isEmpty()) {
            return (List<BigDecimal>) q.getResultList();
        }
        return new ArrayList<>();
    }

    public EntidadeFacade getEntidadeFacade() {
        return entidadeFacade;
    }

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public PessoaFisicaFacade getPessoaFisicaFacade() {
        return pessoaFisicaFacade;
    }

    public ArquivoFacade getArquivoFacade() {
        return arquivoFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public DependenciasDirf getDependenciasDirf() {
        return dependenciasDirf;
    }

    public void setDependenciasDirf(DependenciasDirf dependenciasDirf) {
        this.dependenciasDirf = dependenciasDirf;
    }

    public Sicap getSicap() {
        return sicap;
    }

    public void setSicap(Sicap sicap) {
        this.sicap = sicap;
    }

    public Map<String, File> getFiles() {
        return files;
    }

    public void setFiles(Map<String, File> files) {
        this.files = files;
    }

    public DefaultStreamedContent getFileDownload() {
        return fileDownload;
    }

    public void setFileDownload(DefaultStreamedContent fileDownload) {
        this.fileDownload = fileDownload;
    }

    public File getZipFile() {
        return zipFile;
    }

    public void setZipFile(File zipFile) {
        this.zipFile = zipFile;
    }
}

class ServidorSicap {
    private BigDecimal id;
    private String nome;
    private String cpf;
    private Date dataNascimento;
    private String mae;
    private String sexo;
    private String codigo;
    private String matricula;
    private String nitPisPasep;


    public ServidorSicap() {
    }

    public ServidorSicap(BigDecimal id, String nome, String cpf, Date dataNascimento, String mae, String sexo, String codigo, String matricula, String nitPisPasep) {
        this.id = id;
        this.nome = nome;
        this.cpf = cpf;
        this.dataNascimento = dataNascimento;
        this.mae = mae;
        this.sexo = sexo;
        this.codigo = codigo;
        this.matricula = matricula;
        this.nitPisPasep = nitPisPasep;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public BigDecimal getId() {
        return id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }

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

    public Date getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(Date dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getMae() {
        return mae;
    }

    public void setMae(String mae) {
        this.mae = mae;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNitPisPasep() {
        return nitPisPasep;
    }

    public void setNitPisPasep(String nitPisPasep) {
        this.nitPisPasep = nitPisPasep;
    }

    public enum SituacaoBeneficiario {
        ATIVO("1"),
        INATIVO("2"),
        PENSIONISTA("3");
        private String codigo;

        SituacaoBeneficiario(String codigo) {
            this.codigo = codigo;
        }

        public String getCodigo() {
            return codigo;
        }

        public static SituacaoBeneficiario getSituacaoPeloCodigo(String codigo) {
            switch (codigo) {
                case "1":
                    return ATIVO;
                case "2":
                    return INATIVO;
                case "3":
                    return PENSIONISTA;
            }
            return null;
        }
    }
}
