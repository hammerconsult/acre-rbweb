package br.com.webpublico.negocios.rh.arquivos;


import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.arquivos.Sig;
import br.com.webpublico.entidadesauxiliares.ValorFinanceiroRH;
import br.com.webpublico.enums.*;
import br.com.webpublico.enums.rh.esocial.TipoAfastamentoESocial;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.StringUtil;
import com.google.common.collect.Maps;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.joda.time.DateTime;
import org.primefaces.model.DefaultStreamedContent;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static br.com.webpublico.util.Util.getValorSemPontoEVirgulas;

@Stateful
public class SigFacade extends AbstractFacade<Sig> {


    public static final Long MODULO = 6l;
    public static final Long GRUPO = 6l;

    private File file;

    private Map<String, File> files;

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private PessoaFisicaFacade pessoaFisicaFacade;
    @EJB
    private UnidadeOrganizacionalFacade unidadeOrganizacionalFacade;
    @EJB
    private ArquivoFacade arquivoFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private VinculoFPFacade vinculoFPFacade;
    @EJB
    private FichaFinanceiraFPFacade fichaFinanceiraFPFacade;
    @EJB
    private EnquadramentoFuncionalFacade enquadramentoFuncionalFacade;
    @EJB
    private DependenteFacade dependenteFacade;
    @EJB
    private PensaoAlimenticiaFacade pensaoAlimenticiaFacade;
    @EJB
    private PensionistaFacade pensionistaFacade;
    @EJB
    private PessoaJuridicaFacade pessoaJuridicaFacade;
    @EJB
    private RegistroDeObitoFacade registroDeObitoFacade;
    @EJB
    private ExoneracaoRescisaoFacade exoneracaoRescisaoFacade;
    @EJB
    private AposentadoriaFacade aposentadoriaFacade;
    @EJB
    ModuloExportacaoFacade moduloExportacaoFacade;
    @EJB
    GrupoExportacaoFacade grupoExportacaoFacade;
    @EJB
    private CidadeFacade cidadeFacade;
    private List<VinculoFP> vinculos;
    private File zipFile;
    private DefaultStreamedContent fileDownload;

    public SigFacade() {
        super(Sig.class);

        files = new HashMap<>();
    }

    public PessoaFacade getPessoaFacade() {
        return pessoaFacade;
    }

    public ContratoFPFacade getContratoFPFacade() {
        return contratoFPFacade;
    }

    public PessoaJuridicaFacade getPessoaJuridicaFacade() {
        return pessoaJuridicaFacade;
    }

    public ExoneracaoRescisaoFacade getExoneracaoRescisaoFacade() {
        return exoneracaoRescisaoFacade;
    }

    public AposentadoriaFacade getAposentadoriaFacade() {
        return aposentadoriaFacade;
    }

    public CidadeFacade getCidadeFacade() {
        return cidadeFacade;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 2)
    @Asynchronous
    public Future<Sig> gerarArquivoSig(Sig selecionado, AssistenteBarraProgresso assistenteBarraProgresso) throws ParserConfigurationException, IOException, TransformerException {

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        file = File.createTempFile("sig", "xml");
        Document doc = docBuilder.newDocument();
        doc.setXmlStandalone(true);
        vinculos = vinculoFPFacade.recuperarTodosVinculosVigentesComFicha(new Date(), selecionado.getMes(), selecionado.getExercicio().getAno());
        Element arquivo = doc.createElement("arquivo");
        doc.appendChild(arquivo);
        assistenteBarraProgresso.setTotal(12);

        DateTime dateTime = DataUtil.criarDataComMesEAno(selecionado.getMes().getNumeroMes(), selecionado.getExercicio().getAno());

        Element cabecalho = criarTag(doc, "cabecalho", arquivo);
        preencherTagCabecario(doc, cabecalho, selecionado);

        String tipoLogradouro = "";
        if (selecionado.getEnteFederativo().getEnderecoPrincipal() != null && selecionado.getEnteFederativo().getEnderecoPrincipal().getTipoLogradouro() != null) {
            tipoLogradouro = getTipoLogradouro(selecionado);
        }

        Cidade cidade = null;
        if (selecionado.getEnteFederativo().getEnderecoPrincipal() != null) {
            cidade = cidadeFacade.recuperaCidadePorNomeEEstado(selecionado.getEnteFederativo().getEnderecoPrincipal().getLocalidade(), selecionado.getEnteFederativo().getEnderecoPrincipal().getUf());
        }
        Element endereco = criarTag(doc, "endereco", arquivo);
        preencherEndereco(doc, endereco, selecionado, tipoLogradouro, cidade);
        assistenteBarraProgresso.conta();

        Pessoa enteFederativo = pessoaFacade.recuperarPJ(selecionado.getEnteFederativo().getId());
        if (!enteFederativo.getRepresentantesLegal().isEmpty()) {
            RepresentanteLegalPessoa rep = enteFederativo.getRepresentantesLegal().get(0);
            Element chefe = criarTag(doc, "chefe", arquivo);
            preencherTagChefe(doc, chefe, rep);
        }

        List<HierarquiaOrganizacional> hos = hierarquiaOrganizacionalFacade.recuperaHierarquiaOrgaoVigentes(assistenteBarraProgresso.getDataAtual());
        Integer resumoqtOrgao = 0;
        for (HierarquiaOrganizacional ho : hos) {
            try {
                if (ho.getSubordinada() == null || ho.getSubordinada().getEntidade() == null) {
                    logger.error("Não existe entidade para a unidade {}", ho.getDescricao());
                }
                PessoaJuridica pj = pessoaJuridicaFacade.recuperar(ho.getSubordinada().getEntidade().getPessoaJuridica().getId());
                String uniGestora = "";
                UnidadeOrganizacional uo = unidadeOrganizacionalFacade.recuperar(ho.getSubordinada().getId());
                if (uo.getUnidadeGestoraUnidadesOrganizacionais().isEmpty()) {
                    uniGestora = "N";
                } else {
                    uniGestora = "S";
                }
                resumoqtOrgao += 1;
                Element orgao = criarTag(doc, "orgao", arquivo);
                preencherTagOrgao(doc, orgao, ho, uniGestora, pj);

            } catch (Exception e) {
                logger.error("Não existe entidade para a unidade {}", ho.getDescricao());
            }
        }
        assistenteBarraProgresso.conta();

        Integer resumoqtServidor = 0;
        for (VinculoFP vinculo : vinculos) {
            PessoaFisica pf = pessoaFisicaFacade.recuperar(vinculo.getMatriculaFP().getPessoa().getId());
            RegistroDeObito registroDeObito = registroDeObitoFacade.buscarRegistroDeObitoPorPessoaFisica(pf);
            resumoqtServidor += 1;
            Element servidor = criarTag(doc, "servidor", arquivo);
            preencherTagServidor(doc, servidor, vinculo, pf, registroDeObito);
        }
        assistenteBarraProgresso.conta();

        Integer resumoqtVinculoFuncional = 0;
        Map<Long, BigDecimal> mapaCacheSitprev = recuperarMapCampoSitPrevid(selecionado.getMes().getNumeroMes(), selecionado.getExercicio().getAno());
        for (VinculoFP vinculo : vinculos) {
            Date data = new Date();
            if (vinculo instanceof ContratoFP) {
                ContratoFP contrato = (ContratoFP) vinculo;
                String tipoVinculo = "";
                if (contrato.getSituacaoContratoFPVigente().getInicioVigencia().before(data)) {

                    if (ModalidadeContratoFP.CONCURSADOS.equals(contrato.getModalidadeContratoFP().getCodigo()) && TipoRegime.ESTATUTARIO.equals(contrato.getTipoRegime().getCodigo())) {
                        tipoVinculo = "1";
                    } else if (ModalidadeContratoFP.CONTRATO_TEMPORARIO.equals(contrato.getModalidadeContratoFP().getCodigo())) {
                        tipoVinculo = "3";
                    } else if (ModalidadeContratoFP.CARGO_COMISSAO.equals(contrato.getModalidadeContratoFP().getCodigo())) {
                        tipoVinculo = "4";
                    } else if (ModalidadeContratoFP.CARGO_ELETIVO.equals(contrato.getModalidadeContratoFP().getCodigo())) {
                        tipoVinculo = "5";
                    } else if (TipoRegime.CELETISTA.equals(contrato.getTipoRegime().getCodigo())) {
                        tipoVinculo = "6";
                    } else {
                        tipoVinculo = "99";
                    }
                }
                BigDecimal sitPrevid = null;
                sitPrevid = mapaCacheSitprev.get(vinculo.getId());
                Element vinculoFuncional = criarTag(doc, "vinculoFuncional", arquivo);
                preencherTagVinculoFuncional(doc, vinculoFuncional, contrato, tipoVinculo, sitPrevid);

            }
        }
        assistenteBarraProgresso.conta();

        Integer resumoqtMovimentacaoFuncional = 0;
        for (VinculoFP vinculo : vinculos) {
            if (vinculo instanceof ContratoFP) {
                ExoneracaoRescisao exoneracao = exoneracaoRescisaoFacade.recuperaExoneracaoRecisao2(vinculo.getContratoFP());
                if (exoneracao != null) {
                    resumoqtMovimentacaoFuncional += 1;
                    Element movimentacaoFuncional = criarTag(doc, "movimentacaoFuncional", arquivo);
                    preencherTagMovimentacaoFuncional(doc, movimentacaoFuncional, exoneracao);
                }
            }
        }
        assistenteBarraProgresso.conta();

        Integer resumoqtHistoricoFinanceiro = 0;
        List<FichaFinanceiraFP> fichas = fichaFinanceiraFPFacade.recuperarFichaFinanceiraFPPorAnoMes(selecionado.getExercicio().getAno(), selecionado.getMes().getNumeroMes());

        ModuloExportacao moduloExportacao = moduloExportacaoFacade.recuperaModuloExportacaoPorCodigo(MODULO);
        GrupoExportacao grupo = grupoExportacaoFacade.recuperaGrupoExportacaoPorCodigoEModuloExportacao(GRUPO, moduloExportacao);
        if (!fichas.isEmpty()) {

            for (FichaFinanceiraFP ficha : fichas) {
                if (ficha.getVinculoFP() instanceof ContratoFP) {
                    ContratoFP contrato = (ContratoFP) ficha.getVinculoFP();
                    EnquadramentoFuncional enquadramentoFuncional = enquadramentoFuncionalFacade.recuperaEnquadramentoFuncionalVigente(contrato, dateTime.toDate());
                    String vlRemCargoEfetivo = "0";
                    if (enquadramentoFuncional.getVencimentoBase() != null && contrato.getModalidadeContratoFP().getDescricao().equals("CONCURSADO")) {
                        vlRemCargoEfetivo = getValorSemPontoEVirgulas(enquadramentoFuncional.getVencimentoBase());
                    }
                    resumoqtHistoricoFinanceiro += 1;
                    Element historicoFinanceiro = criarTag(doc, "historicoFinanceiro", arquivo);

                    ValorFinanceiroRH valorFinanceiroRH = fichaFinanceiraFPFacade.recuperarValorPorMesAndAnoPorVinculoFpEModuloExportacao(ficha.getVinculoFP().getId(), selecionado.getMes(), selecionado.getExercicio().getAno(), grupo, moduloExportacao, TipoFolhaDePagamento.values());
                    preencherTagHistoricoFinanceiro(doc, historicoFinanceiro, ficha, contrato, valorFinanceiroRH, vlRemCargoEfetivo);
                }
            }
        }
        assistenteBarraProgresso.conta();

        Integer resumoqtBeneficioServidor = 0;
        for (VinculoFP vinculo : vinculos) {
            if (vinculo instanceof Aposentadoria) {
                Aposentadoria aposentadoria = (Aposentadoria) vinculo;
                if (aposentadoria != null) {
                    String paridade = null;
                    if (TipoReajusteAposentadoria.PARIDADE.equals(aposentadoria.getTipoReajusteAposentadoria())) {
                        paridade = "S";
                    } else {
                        paridade = "N";
                    }
                    String tipoBeneficio = "";
                    if (TipoAposentadoria.TEMPO_DE_CONTRIBUICAO.equals(aposentadoria.getTipoAposentadoria().getCodigo())) {
                        tipoBeneficio = "1";
                    } else if (TipoAposentadoria.COMPULSORIA.equals(aposentadoria.getTipoAposentadoria().getCodigo())) {
                        tipoBeneficio = "4";
                    } else if (TipoAposentadoria.INVALIDEZ.equals(aposentadoria.getTipoAposentadoria().getCodigo())) {
                        tipoBeneficio = "3";
                    } else if (TipoAposentadoria.IDADE.equals(aposentadoria.getTipoAposentadoria().getCodigo())) {
                        tipoBeneficio = "2";
                    }
                    BigDecimal salarioInicial = aposentadoriaFacade.buscarValorAposentadoriaPorAposentado(vinculo, vinculo.getInicioVigencia());
                    BigDecimal salarioAtual = aposentadoriaFacade.buscarValorAposentadoriaPorAposentado(vinculo, dateTime.toDate());
                    resumoqtBeneficioServidor += 1;
                    Element beneficioServidor = criarTag(doc, "beneficioServidor", arquivo);
                    preencherTagBeneficioServidor(doc, beneficioServidor, vinculo, aposentadoria, paridade, salarioInicial, salarioAtual, tipoBeneficio);
                }
            }
        }
        assistenteBarraProgresso.conta();

        Integer resumoqtDependente = 0;
        for (VinculoFP vinculo : vinculos) {
            List<Dependente> dependentes = dependenteFacade.dependentesPorResponsavelNative(vinculo.getMatriculaFP().getPessoa());
            for (Dependente dependenciSig : dependentes) {
                resumoqtDependente += 1;
                Element dependente = criarTag(doc, "dependente", arquivo);
                preencherTagDependente(doc, dependente, dependenciSig);
            }
        }
        assistenteBarraProgresso.conta();

        Integer resumoqtPensionista = 0;

        List<Pensionista> listaPensionista = pensionistaFacade.recuperaPensionistasVigente(dateTime.toDate());
        for (Pensionista pensi : listaPensionista) {
            resumoqtPensionista += 1;
            Element pensionista = criarTag(doc, "pensionista", arquivo);
            preencherTagPensionista(doc, pensionista, pensi);
        }
        assistenteBarraProgresso.conta();

        for (VinculoFP vinculo : vinculos) {
            List<Dependente> dependentes = dependenteFacade.dependentesPorResponsavelNative(vinculo.getMatriculaFP().getPessoa());
            for (Dependente dependenciSig : dependentes) {
                DependenteVinculoFP dependenciaVigente = dependenciSig.getDependentesVinculosFPsVigente(dateTime.toDate());
                List<BeneficioPensaoAlimenticia> beneficioPensaoAlimenticias = pensaoAlimenticiaFacade.buscarBeneficioPensaoAlimenticiaVigentePorPessoa(dependenciSig.getDependente(), new Date());

                String tipoDependencia = "";
                if(dependenciSig.getGrauDeParentesco() != null && dependenciSig.getGrauDeParentesco().getTipoParentesco() != null) {
                    tipoDependencia = getTipoDependencia(dependenciSig);
                }
                Element dependencia = criarTag(doc, "dependencia", arquivo);
                preencherTagDependencia(doc, dependencia, dependenciSig, vinculo, dependenciaVigente, tipoDependencia, !beneficioPensaoAlimenticias.isEmpty());
            }
        }
        assistenteBarraProgresso.conta();


        Integer resumoqtBeneficioPensionista = 0;
        for (Pensionista pensi : listaPensionista) {
            resumoqtBeneficioPensionista += 1;
            BigDecimal salarioInicial = pensionistaFacade.buscarValorPensionistaPerPensionista(pensi, pensi.getInicioVigencia());
            BigDecimal salarioFinal = pensionistaFacade.buscarValorPensionistaPerPensionista(pensi, dateTime.toDate());

            String tipoPensaoMorte = "";
            if (TipoFundamentacao.ART_40_CF_I.equals(pensi.getTipoFundamentacao())) {
                tipoPensaoMorte = "1";
            } else if (TipoFundamentacao.ART_40_CF_II.equals(pensi.getTipoFundamentacao())) {
                tipoPensaoMorte = "2";
            }
            Element beneficioPensionista = criarTag(doc, "beneficioPensionista", arquivo);
            preencherTagBeneficioPensionista(doc, beneficioPensionista, pensi, tipoPensaoMorte, salarioInicial, salarioFinal);
        }
        assistenteBarraProgresso.conta();


        Element resumo = criarTag(doc, "resumo", arquivo);
        preencherTagResumo(doc, resumo, resumoqtOrgao, resumoqtServidor, resumoqtVinculoFuncional, resumoqtMovimentacaoFuncional, resumoqtHistoricoFinanceiro,
            resumoqtBeneficioServidor, resumoqtDependente, resumoqtPensionista, resumoqtBeneficioPensionista);
        assistenteBarraProgresso.conta();

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
        transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(file);
        transformer.transform(source, result);
        files.put("sig.xml", file);
        addToZip();
        montarArquivo(selecionado);
        return new AsyncResult<>(selecionado);
    }



    private String getTipoLogradouro(Sig selecionado) {
        switch (selecionado.getEnteFederativo().getEnderecoPrincipal().getTipoLogradouro()) {
            case AER:
                return "501";
            case AL:
                return "4";
            case A:
                return "472";
            case AV:
                return "8";
            case CPO:
                return "23";
            case CH:
                return "481";
            case COL:
                return "21";
            case CON:
                return "485";
            case CJ:
                return "664";
            case DT:
                return "28";
            case ESP:
                return "474";
            case ETC:
                return "32";
            case EST:
                return "31";
            case FAV:
                return "36";
            case FAZ:
                return "37";
            case FRA:
                return "40";
            case JD:
                return "52";
            case LD:
                return "53";
            case LGO:
                return "499";
            case LGA:
                return "55";
            case LRG:
                return "54";
            case LOT:
                return "56";
            case MRO:
                return "59";
            case NUC:
                return "500";
            case PRQ:
                return "72";
            case PSA:
                return "73";
            case PAT:
                return "27";
            case PC:
                return "65";
            case Q:
                return "77";
            case REC:
                return "87";
            case PRR:
                return "487";
            case ROD:
                return "90";
            case R:
                return "81";
            case ST:
                return "95";
            case SIT:
                return "92";
            case TV:
                return "100";
            case TR:
                return "452";
            case TRV:
                return "99";
            case VLE:
                return "106";
            case VER:
                return "453";
            case V:
                return "101";
            case VD:
                return "103";
            case VLA:
                return "105";
            case VL:
                return "104";
            default:
                return "999";

        }
    }

    private String getTipoDependencia(Dependente dependenciSig) {
        switch (dependenciSig.getGrauDeParentesco().getTipoParentesco()) {
            case CONJUGE:
                return "1";
            case COMPANHEIRO:
                return "2";
            case FILHO:
                return "3";
            case FILHO_INVALIDO:
                return "4";
            case PAI_MAE:
                return "5";
            case ENTEADO:
                return "6";
            case IRMAO:
                return "8";
            case MENOR_TUTELADO:
                return "10";
            case NETO:
                return "11";
            case EX_CONJUGE:
                return "12";
            case OUTROS:
                return "99";
            default:
                return "";
        }
    }

    private Element criarTag(Document doc, String nomeTag, Element elementoPai) {
        Element element = doc.createElement(nomeTag);
        elementoPai.appendChild(element);
        return element;
    }

    private String converterDataString(String pattern, Date data) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(data);
    }

    private void preencherTagCabecario(Document doc, Element cabecario, Sig selecionado) {
        cabecario.setAttributeNode(criarAtributo(doc, "cnpj", StringUtil.removeCaracteresEspeciaisSemEspaco(selecionado.getEnteFederativo().getCpf_Cnpj())));
        cabecario.setAttributeNode(criarAtributo(doc, "nome", selecionado.getEnteFederativo().getNome()));
        cabecario.setAttributeNode(criarAtributo(doc, "nomeRepresentanteLegalEnte", selecionado.getRepresentante().getNome()));
        cabecario.setAttributeNode(criarAtributo(doc, "cpfRepresentanteLegalEnte", StringUtil.removeCaracteresEspeciaisSemEspaco(selecionado.getRepresentante().getCpf_Cnpj())));
        cabecario.setAttributeNode(criarAtributo(doc, "cnpjTransmissor", StringUtil.removeCaracteresEspeciaisSemEspaco(selecionado.getEnteFederativo().getCpf_Cnpj())));
        cabecario.setAttributeNode(criarAtributo(doc, "esfera", selecionado.getEsferaPoderSig().getCodigo()));
        cabecario.setAttributeNode(criarAtributo(doc, "numSIAFI", selecionado.getCodigo()));
        cabecario.setAttributeNode(criarAtributo(doc, "cnpjUniGestora", StringUtil.removeCaracteresEspeciaisSemEspaco(selecionado.getRepresentanteUnidadeGestora().getCpf_Cnpj())));
        cabecario.setAttributeNode(criarAtributo(doc, "nomeUniGestora", selecionado.getRepresentanteUnidadeGestora().getNome()));
    }

    private void preencherEndereco(Document doc, Element endereco, Sig selecionado, String tipoLogradouro, Cidade cidade) {
        endereco.setAttributeNode(criarAtributo(doc, "emailEnte", selecionado.getEnteFederativo().getEmail() == null ? "" : selecionado.getEnteFederativo().getEmail()));
        endereco.setAttributeNode(criarAtributo(doc, "tipoLogradouro", tipoLogradouro));
        endereco.setAttributeNode(criarAtributo(doc, "logradouro", selecionado.getEnteFederativo().getEnderecoPrincipal() == null ? "" : selecionado.getEnteFederativo().getEnderecoPrincipal().getLogradouro()));
        endereco.setAttributeNode(criarAtributo(doc, "numLogradouro", selecionado.getEnteFederativo().getEnderecoPrincipal() == null ? "" : selecionado.getEnteFederativo().getEnderecoPrincipal().getNumero()));
        endereco.setAttributeNode(criarAtributo(doc, "complemento", selecionado.getEnteFederativo().getEnderecoPrincipal() == null ? "" : selecionado.getEnteFederativo().getEnderecoPrincipal().getComplemento()));
        endereco.setAttributeNode(criarAtributo(doc, "bairro", selecionado.getEnteFederativo().getEnderecoPrincipal() == null ? "" : selecionado.getEnteFederativo().getEnderecoPrincipal().getBairro()));
        endereco.setAttributeNode(criarAtributo(doc, "municipio", cidade == null ? "" : cidade.getCodigoIBGE().toString()));
        endereco.setAttributeNode(criarAtributo(doc, "uf", selecionado.getEnteFederativo().getEnderecoPrincipal() == null ? "" : selecionado.getEnteFederativo().getEnderecoPrincipal().getUf()));
        endereco.setAttributeNode(criarAtributo(doc, "cep", selecionado.getEnteFederativo().getEnderecoPrincipal() == null ? "" : selecionado.getEnteFederativo().getEnderecoPrincipal().getCep()));
    }

    private void preencherTagChefe(Document doc, Element chefe, RepresentanteLegalPessoa rep) {
        chefe.setAttributeNode(criarAtributo(doc, "idChefe", rep.getId().toString()));
        chefe.setAttributeNode(criarAtributo(doc, "nome", rep.getPessoa().getNome()));
        chefe.setAttributeNode(criarAtributo(doc, "cpf", StringUtil.removeCaracteresEspeciaisSemEspaco(rep.getPessoa().getCpf_Cnpj())));
        chefe.setAttributeNode(criarAtributo(doc, "dtInicioMand", converterDataString("yyyy-MM-dd", rep.getDataInicio())));
        chefe.setAttributeNode(criarAtributo(doc, "dtFimMand", rep.getDataFim() == null ? "" : converterDataString("yyyy-MM-dd", rep.getDataFim())));
        chefe.setAttributeNode(criarAtributo(doc, "email", rep.getPessoa().getEmail()));
    }

    private void preencherTagOrgao(Document doc, Element orgao, HierarquiaOrganizacional ho, String uniGestora, PessoaJuridica pj) {
        orgao.setAttributeNode(criarAtributo(doc, "idOrgao", ho.getId().toString()));
        orgao.setAttributeNode(criarAtributo(doc, "nomeOrgao", ho.getDescricao()));
        orgao.setAttributeNode(criarAtributo(doc, "cnpjOrgao", StringUtil.removeCaracteresEspeciaisSemEspaco(pj.getCnpj())));
        orgao.setAttributeNode(criarAtributo(doc, "poder", ho.getSubordinada().getEntidade() == null ? "" : ho.getSubordinada().getEntidade().getEsferaDoPoder().getCodigoSiprev()));
        orgao.setAttributeNode(criarAtributo(doc, "unidadeGestora", uniGestora));

    }

    private void preencherTagServidor(Document doc, Element servidor, VinculoFP vinculo, PessoaFisica pf, RegistroDeObito registroDeObito) {
        servidor.setAttributeNode(criarAtributo(doc, "idServidor", vinculo.getId().toString()));
        servidor.setAttributeNode(criarAtributo(doc, "pasep", pf.getCarteiraDeTrabalho() == null ? "" : StringUtil.removeCaracteresEspeciaisSemEspaco(pf.getCarteiraDeTrabalho().getPisPasep())));
        servidor.setAttributeNode(criarAtributo(doc, "nome", pf.getNome()));
        servidor.setAttributeNode(criarAtributo(doc, "cpf", StringUtil.removeCaracteresEspeciaisSemEspaco(pf.getCpf())));
        servidor.setAttributeNode(criarAtributo(doc, "dtNascto", DataUtil.getDataFormatada(pf.getDataNascimento(), "yyyy-MM-dd")));
        servidor.setAttributeNode(criarAtributo(doc, "sexo", pf.getSexo().getSigla()));
        servidor.setAttributeNode(criarAtributo(doc, "dtObito", registroDeObito == null ? "" : converterDataString("yyyy-MM-dd", registroDeObito.getDataFalecimento())));
        servidor.setAttributeNode(criarAtributo(doc, "nomeMae", pf.getMae()));
        servidor.setAttributeNode(criarAtributo(doc, "estadoCivil", pf.getEstadoCivil() == null ? "" : pf.getEstadoCivil().getCodigoSiprev()));
        servidor.setAttributeNode(criarAtributo(doc, "dtRecenseamento", ""));
        servidor.setAttributeNode(criarAtributo(doc, "dtIngressoServPublico", converterDataString("yyyy-MM-dd", vinculo.getInicioVigencia())));
    }

    private void preencherTagVinculoFuncional(Document doc, Element vinculoFuncional, ContratoFP contrato, String tipoVinculo, BigDecimal sitPrevid) {
        vinculoFuncional.setAttributeNode(criarAtributo(doc, "idVinculoFuncional", contrato.getModalidadeContratoFP().getId().toString()));
        vinculoFuncional.setAttributeNode(criarAtributo(doc, "idServidor", contrato.getId().toString()));
        vinculoFuncional.setAttributeNode(criarAtributo(doc, "tipoVinculo", tipoVinculo));
        vinculoFuncional.setAttributeNode(criarAtributo(doc, "idOrgao", contrato.getUnidadeOrganizacional().getId().toString()));
        vinculoFuncional.setAttributeNode(criarAtributo(doc, "cargo", StringUtil.removeEspacos(contrato.getCargo().getDescricao())));
        vinculoFuncional.setAttributeNode(criarAtributo(doc, "dtIniCargo", contrato.getCargo().getInicioVigencia() == null ? "" : converterDataString("yyyy-MM-dd", contrato.getCargo().getInicioVigencia())));
        vinculoFuncional.setAttributeNode(criarAtributo(doc, "matricula", StringUtil.removeCaracteresEspeciaisSemEspaco(contrato.getMatriculaFP().getMatricula())));
        vinculoFuncional.setAttributeNode(criarAtributo(doc, "carreira", contrato.getCargo().getDescricaoCarreira()));
        vinculoFuncional.setAttributeNode(criarAtributo(doc, "regime", contrato.getPrevidenciaVinculoFPVigente().getTipoPrevidenciaFP().getCodigo() == 1 ? "5" : "1"));
        vinculoFuncional.setAttributeNode(criarAtributo(doc, "sitFuncional", "1"));
        vinculoFuncional.setAttributeNode(criarAtributo(doc, "sitPrevid", sitPrevid == null ? "" : sitPrevid.toString()));

        if (contrato.getCargo().getDedicacaoExclusivaSIPREV()) {
            vinculoFuncional.setAttributeNode(criarAtributo(doc, "dedicacaoExclusiva", "S"));
        } else {
            vinculoFuncional.setAttributeNode(criarAtributo(doc, "dedicacaoExclusiva", "N"));
        }
        if (contrato.getCargo().getTecnicoCientificoSIPREV()) {
            vinculoFuncional.setAttributeNode(criarAtributo(doc, "tecnicoCientifico", "S"));
        } else {
            vinculoFuncional.setAttributeNode(criarAtributo(doc, "tecnicoCientifico", "N"));
        }
        vinculoFuncional.setAttributeNode(criarAtributo(doc, "acumulavel", contrato.getCargo().getTipoAcumulavelSIG().getCodigo().toString()));
        vinculoFuncional.setAttributeNode(criarAtributo(doc, "tipoServidor", "1"));
    }

    private void preencherTagMovimentacaoFuncional(Document doc, Element movimentacaoFuncional, ExoneracaoRescisao exoneracao) {
        movimentacaoFuncional.setAttributeNode(criarAtributo(doc, "idMovimentacaoFuncional", exoneracao.getId().toString()));
        movimentacaoFuncional.setAttributeNode(criarAtributo(doc, "idVinculoFuncional", exoneracao.getVinculoFP().getId().toString()));
        movimentacaoFuncional.setAttributeNode(criarAtributo(doc, "sitFuncional", "2"));
        movimentacaoFuncional.setAttributeNode(criarAtributo(doc, "dtDesligamentoOrgao", converterDataString("yyyy-MM-dd", exoneracao.getDataRescisao())));
    }

    private void preencherTagHistoricoFinanceiro(Document doc, Element historicoFinanceiro, FichaFinanceiraFP ficha, ContratoFP contrato,
                                                ValorFinanceiroRH valorFinanceiroRH, String vlRemCargoEfetivo) {
        BigDecimal remuneracaoBruta = BigDecimal.ZERO;
        for (ItemFichaFinanceiraFP itemFichaFinanceiraFP : ficha.getItemFichaFinanceiraFP()) {
            if (TipoEventoFP.VANTAGEM.equals(itemFichaFinanceiraFP.getTipoEventoFP())) {
                remuneracaoBruta = remuneracaoBruta.add(itemFichaFinanceiraFP.getValor());
            }
        }
        historicoFinanceiro.setAttributeNode(criarAtributo(doc, "idHistoricoFinanceiro", ficha.getId().toString()));
        historicoFinanceiro.setAttributeNode(criarAtributo(doc, "idServidor", ficha.getVinculoFP().getId().toString()));
        historicoFinanceiro.setAttributeNode(criarAtributo(doc, "idVinculoFuncional", contrato.getModalidadeContratoFP().getId().toString()));
        historicoFinanceiro.setAttributeNode(criarAtributo(doc, "ano", ficha.getFolhaDePagamento().getAno().toString()));
        historicoFinanceiro.setAttributeNode(criarAtributo(doc, "mes", ficha.getFolhaDePagamento().getMes().getNumeroMes().toString()));
        historicoFinanceiro.setAttributeNode(criarAtributo(doc, "numeroFolha", ficha.getFolhaDePagamento().getVersao().toString()));
        historicoFinanceiro.setAttributeNode(criarAtributo(doc, "vlRemBruta", getValorSemPontoEVirgulas(remuneracaoBruta)));
        historicoFinanceiro.setAttributeNode(criarAtributo(doc, "vlRemContributiva", valorFinanceiroRH != null ? getValorSemPontoEVirgulas(valorFinanceiroRH.getTotalBase()) : ""));
        historicoFinanceiro.setAttributeNode(criarAtributo(doc, "vlRemCargoEfetivo", vlRemCargoEfetivo));
    }

    private void preencherTagBeneficioServidor(Document doc, Element beneficioServidor, VinculoFP vinculo, Aposentadoria aposentadoria,
                                              String paridade, BigDecimal salarioInicial, BigDecimal salarioAtual, String tipoBeneficio) {
        beneficioServidor.setAttributeNode(criarAtributo(doc, "idBeneficio", aposentadoria.getId().toString()));
        beneficioServidor.setAttributeNode(criarAtributo(doc, "idServidor", vinculo.getId().toString()));
        beneficioServidor.setAttributeNode(criarAtributo(doc, "idOrgao", vinculo.getUnidadeOrganizacional().getId().toString()));
        beneficioServidor.setAttributeNode(criarAtributo(doc, "dtInicioBeneficio", aposentadoria.getDataRegistro() == null ? "" : converterDataString("yyyy-MM-dd", aposentadoria.getDataRegistro())));
        beneficioServidor.setAttributeNode(criarAtributo(doc, "tipoBeneficio", tipoBeneficio));
        beneficioServidor.setAttributeNode(criarAtributo(doc, "dtFimBeneficio", aposentadoria.getFinalVigencia() == null ? "" : converterDataString("yyyy-MM-dd", aposentadoria.getFinalVigencia())));
        beneficioServidor.setAttributeNode(criarAtributo(doc, "vlAtualBeneficio", salarioAtual == null ? "" : getValorSemPontoEVirgulas(salarioAtual)));
        beneficioServidor.setAttributeNode(criarAtributo(doc, "vlInicialBeneficio", salarioInicial == null ? "" : getValorSemPontoEVirgulas(salarioInicial)));
        beneficioServidor.setAttributeNode(criarAtributo(doc, "dtUltimaAtualizacao", aposentadoria.getDataAlteracao() == null ? "" : converterDataString("yyyy-MM-dd", aposentadoria.getDataAlteracao())));
        beneficioServidor.setAttributeNode(criarAtributo(doc, "dtPublicacao", aposentadoria.getDataPublicacao() == null ? "" : converterDataString("yyyy-MM-dd", aposentadoria.getDataPublicacao())));
        beneficioServidor.setAttributeNode(criarAtributo(doc, "paridade", paridade));
        beneficioServidor.setAttributeNode(criarAtributo(doc, "regime", vinculo.getContratoFP().getPrevidenciaVinculoFPVigente().getTipoPrevidenciaFP().getCodigo() == 1 ? "5" : "1"));
        beneficioServidor.setAttributeNode(criarAtributo(doc, "tipoAposentadoriaEspecial", aposentadoria.getTipoAposentadoriaEspecialSIG() == null ? "" : aposentadoria.getTipoAposentadoriaEspecialSIG().getCodigo().toString()));
    }

    private void preencherTagDependente(Document doc, Element dependente, Dependente dependenciSig) {
        dependente.setAttributeNode(criarAtributo(doc, "idDependente", dependenciSig.getId().toString()));
        dependente.setAttributeNode(criarAtributo(doc, "nome", dependenciSig.getDependente().getNome()));
        dependente.setAttributeNode(criarAtributo(doc, "dtNascto", dependenciSig.getDependente().getDataNascimento() == null ? "" : converterDataString("yyyy-MM-dd", dependenciSig.getDependente().getDataNascimento())));
        dependente.setAttributeNode(criarAtributo(doc, "cpf", dependenciSig.getDependente().getCpf() == null ? "" : StringUtil.removeCaracteresEspeciaisSemEspaco(dependenciSig.getDependente().getCpf())));
        dependente.setAttributeNode(criarAtributo(doc, "nomeMae", dependenciSig.getDependente().getMae() == null ? "" : dependenciSig.getDependente().getMae()));
        dependente.setAttributeNode(criarAtributo(doc, "sexo", dependenciSig.getDependente().getSexo() == null ? "" : dependenciSig.getDependente().getSexo().getSigla()));
        dependente.setAttributeNode(criarAtributo(doc, "pasep", dependenciSig.getDependente().getCarteiraDeTrabalho() == null ? "" : dependenciSig.getDependente().getCarteiraDeTrabalho().getPisPasep()));
        dependente.setAttributeNode(criarAtributo(doc, "estadoCivil", dependenciSig.getDependente().getEstadoCivil() == null ? "" : dependenciSig.getDependente().getEstadoCivil().getCodigoSiprev()));
    }

    private void preencherTagPensionista(Document doc, Element pensionista, Pensionista pensi) {
        pensionista.setAttributeNode(criarAtributo(doc, "idPensionista", pensi.getId().toString()));
        pensionista.setAttributeNode(criarAtributo(doc, "nome", pensi.getMatriculaFP().getPessoa().getNome()));
        pensionista.setAttributeNode(criarAtributo(doc, "matricula", pensi.getMatriculaFP().getMatricula()));
        pensionista.setAttributeNode(criarAtributo(doc, "dtNascto", pensi.getMatriculaFP().getPessoa().getDataNascimento() == null ? "" : converterDataString("yyyy-MM-dd", (pensi.getMatriculaFP().getPessoa().getDataNascimento()))));
        pensionista.setAttributeNode(criarAtributo(doc, "cpf", StringUtil.removeCaracteresEspeciaisSemEspaco(pensi.getMatriculaFP().getPessoa().getCpf())));
        pensionista.setAttributeNode(criarAtributo(doc, "nomeMae", pensi.getMatriculaFP().getPessoa().getMae()));
        pensionista.setAttributeNode(criarAtributo(doc, "sexo", pensi.getMatriculaFP().getPessoa().getSexo().getSigla()));
        pensionista.setAttributeNode(criarAtributo(doc, "pasep", pensi.getMatriculaFP().getPessoa().getCarteiraDeTrabalho() == null ? "" : pensi.getMatriculaFP().getPessoa().getCarteiraDeTrabalho().getPisPasep()));
        pensionista.setAttributeNode(criarAtributo(doc, "estadoCivil", pensi.getMatriculaFP().getPessoa().getEstadoCivil() == null ? "" : pensi.getMatriculaFP().getPessoa().getEstadoCivil().getCodigoSiprev()));
        pensionista.setAttributeNode(criarAtributo(doc, "dtRecenseamento", ""));
    }

    private void preencherTagDependencia(Document doc, Element dependencia, Dependente dependenciSig, VinculoFP vinculo, DependenteVinculoFP dependenciaVigente,
                                        String tipoDependencia, boolean dependentePA) {

        dependencia.setAttributeNode(criarAtributo(doc, "idDependencia", dependenciaVigente == null ? "" : dependenciaVigente.getId().toString()));
        dependencia.setAttributeNode(criarAtributo(doc, "idDependente", dependenciSig.getId().toString()));
        dependencia.setAttributeNode(criarAtributo(doc, "idServidor", dependenciSig.getResponsavel() == null ? "" : dependenciSig.getResponsavel().getId().toString()));
        dependencia.setAttributeNode(criarAtributo(doc, "dtIniPensao", dependenciaVigente == null || dependenciaVigente.getInicioVigencia() == null ? "" : converterDataString("yyyy-MM-dd", dependenciaVigente.getInicioVigencia())));
        dependencia.setAttributeNode(criarAtributo(doc, "dtFimPrevisto", dependenciaVigente == null || dependenciaVigente.getFinalVigencia() == null ? "" : converterDataString("yyyy-MM-dd", dependenciaVigente.getFinalVigencia())));
        dependencia.setAttributeNode(criarAtributo(doc, "tipoDependencia", tipoDependencia));
        dependencia.setAttributeNode(criarAtributo(doc, "outroTipoDependencia", tipoDependencia == "99" ? dependenciSig.getGrauDeParentesco().getDescricao() : ""));
        dependencia.setAttributeNode(criarAtributo(doc, "motInicioDep", ""));
        dependencia.setAttributeNode(criarAtributo(doc, "outroMotInicioDep", ""));
        dependencia.setAttributeNode(criarAtributo(doc, "motFimDep", ""));
        dependencia.setAttributeNode(criarAtributo(doc, "outroMotFimDep", ""));

        dependencia.setAttributeNode(criarAtributo(doc, "dtInicioDependencia", dependentePA ? "" : converterDataString("yyyy-MM-dd", vinculo.getInicioVigencia())));
        dependencia.setAttributeNode(criarAtributo(doc, "dtFimDependencia", vinculo.getFinalVigencia() == null || dependentePA ? "" : converterDataString("yyyy-MM-dd", vinculo.getFinalVigencia())));

        dependencia.setAttributeNode(criarAtributo(doc, "motInicioPensao", ""));
        dependencia.setAttributeNode(criarAtributo(doc, "motFimPensao", ""));
        dependencia.setAttributeNode(criarAtributo(doc, "justificativaMotivoInicioPensao", ""));
        dependencia.setAttributeNode(criarAtributo(doc, "justificativaMotivoFimPensao", ""));
        dependencia.setAttributeNode(criarAtributo(doc, "pensionista", dependentePA ? "S" : "N"));
    }

    private void preencherTagBeneficioPensionista(Document doc, Element beneficioPensionista, Pensionista pensi, String tipoPensaoMorte, BigDecimal salarioInicial, BigDecimal salarioFinal) {
        beneficioPensionista.setAttributeNode(criarAtributo(doc, "idBeneficio", pensi.getPensao().getId().toString()));
        beneficioPensionista.setAttributeNode(criarAtributo(doc, "idOrgao", pensi.getFolha() == null || pensi.getFolha().getUnidadeOrganizacional() == null ? "" : pensi.getFolha().getUnidadeOrganizacional().getId().toString()));
        beneficioPensionista.setAttributeNode(criarAtributo(doc, "tipoBeneficio", "12"));
        beneficioPensionista.setAttributeNode(criarAtributo(doc, "dtInicioBeneficio", converterDataString("yyyy-MM-dd", pensi.getInicioVigencia())));
        beneficioPensionista.setAttributeNode(criarAtributo(doc, "dtFimBeneficio", pensi.getFinalVigencia() == null ? "" : converterDataString("yyyy-MM-dd", pensi.getFinalVigencia())));
        beneficioPensionista.setAttributeNode(criarAtributo(doc, "vlAtualBeneficio", salarioInicial == null ? "" : getValorSemPontoEVirgulas(salarioFinal)));
        beneficioPensionista.setAttributeNode(criarAtributo(doc, "vlInicialBeneficio", salarioFinal == null ? "" : getValorSemPontoEVirgulas(salarioInicial)));
        beneficioPensionista.setAttributeNode(criarAtributo(doc, "dtUltimaAtualizacao", pensi.getDataAlteracao() == null ? "" : converterDataString("yyyy-MM-dd", pensi.getDataAlteracao())));
        beneficioPensionista.setAttributeNode(criarAtributo(doc, "idServidorInstituidor", pensi.getPensao().getContratoFP().getId().toString()));
        beneficioPensionista.setAttributeNode(criarAtributo(doc, "regimeServidorInstituidor", pensi.getPensao().getContratoFP().getPrevidenciaVinculoFPVigente().getTipoPrevidenciaFP().getCodigo() == 1 ? "5" : "1"));
        beneficioPensionista.setAttributeNode(criarAtributo(doc, "carreira", pensi.getPensao().getContratoFP().getCargo().getDescricaoCarreira()));
        beneficioPensionista.setAttributeNode(criarAtributo(doc, "cargo", StringUtil.removeEspacos(pensi.getPensao().getContratoFP().getCargo().getDescricao())));
        beneficioPensionista.setAttributeNode(criarAtributo(doc, "motivoInicioPensao", ""));
        beneficioPensionista.setAttributeNode(criarAtributo(doc, "motivoFimPensao", ""));
        beneficioPensionista.setAttributeNode(criarAtributo(doc, "tipoPensaoMorte", tipoPensaoMorte));
    }

    private void preencherTagResumo(Document doc, Element resumo, Integer resumoqtOrgao, Integer resumoqtServidor, Integer resumoqtVinculoFuncional,
                                   Integer resumoqtMovimentacaoFuncional, Integer resumoqtHistoricoFinanceiro, Integer resumoqtBeneficioServidor,
                                   Integer resumoqtDependente, Integer resumoqtPensionista, Integer resumoqtBeneficioPensionista) {
        resumo.setAttributeNode(criarAtributo(doc, "qtEndereco", "1"));
        resumo.setAttributeNode(criarAtributo(doc, "qtMaioridade", "0"));
        resumo.setAttributeNode(criarAtributo(doc, "qtChefe", "1"));
        resumo.setAttributeNode(criarAtributo(doc, "qtAliquota", "0"));
        resumo.setAttributeNode(criarAtributo(doc, "qtContribuicaoEnte", "0"));
        resumo.setAttributeNode(criarAtributo(doc, "qtLimiteRemuneratorio", "0"));
        resumo.setAttributeNode(criarAtributo(doc, "qtOrgao", resumoqtOrgao.toString()));
        resumo.setAttributeNode(criarAtributo(doc, "qtServidor", resumoqtServidor.toString()));
        resumo.setAttributeNode(criarAtributo(doc, "qtVinculoFuncional", resumoqtVinculoFuncional.toString()));
        resumo.setAttributeNode(criarAtributo(doc, "qtMovimentacaoFuncional", resumoqtMovimentacaoFuncional.toString()));
        resumo.setAttributeNode(criarAtributo(doc, "qtTempoContribuicaoRGPS", "0"));
        resumo.setAttributeNode(criarAtributo(doc, "qtTempoContribuicaoRPPS", "0"));
        resumo.setAttributeNode(criarAtributo(doc, "qtTempoSemContribuicao", "0"));
        resumo.setAttributeNode(criarAtributo(doc, "qtTempoFicticio", "0"));
        resumo.setAttributeNode(criarAtributo(doc, "qtHistoricoFinanceiro", resumoqtHistoricoFinanceiro.toString()));
        resumo.setAttributeNode(criarAtributo(doc, "qtBeneficioServidor", resumoqtBeneficioServidor.toString()));
        resumo.setAttributeNode(criarAtributo(doc, "qtDependente", resumoqtDependente.toString()));
        resumo.setAttributeNode(criarAtributo(doc, "qtPensionista", resumoqtPensionista.toString()));
        resumo.setAttributeNode(criarAtributo(doc, "qtBeneficioPensionista", resumoqtBeneficioPensionista.toString()));
        resumo.setAttributeNode(criarAtributo(doc, "qtQuotaPensionista", "0"));
        resumo.setAttributeNode(criarAtributo(doc, "qtfuncaoGratificada", "0"));
    }

    public void addToZip() {
        try {
            zipFile = File.createTempFile("SIG", "zip");

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
            fileDownload = new DefaultStreamedContent(fis, "application/zip", "SIG.zip");
            zout.close();

        } catch (IOException ioe) {
            FacesUtil.addError("Erro grave!", "Ocorreu um erro para gerar o arquivo ZIP do Siprev, comunique o administrador.");
        }
    }


    private void montarArquivo(Sig selecionado) throws IOException {
        Arquivo arquivo = new Arquivo();
        arquivo.setMimeType(arquivoFacade.getMimeType(fileDownload.getName()));
        arquivo.setNome(fileDownload.getName());
        arquivo.setTamanho(Long.valueOf(fileDownload.getStream().available()));
        arquivo.setDescricao(fileDownload.getName());
        arquivo.setInputStream(fileDownload.getStream());
        arquivo = criarPartesDoArquivo(arquivo);
        adicionarArquivo(selecionado, arquivo);
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

    private void adicionarArquivo(Sig selecionado, Arquivo arquivo) {
        ArquivoComposicao arquivoComposicao = criarArquivoComposicao(arquivo, selecionado);
        selecionado.getDetentorArquivoComposicao().setArquivosComposicao(new ArrayList());
        selecionado.getDetentorArquivoComposicao().getArquivosComposicao().add(arquivoComposicao);
    }

    private ArquivoComposicao criarArquivoComposicao(Arquivo arquivo, Sig selecionado) {
        ArquivoComposicao arquivoComposicao = new ArquivoComposicao();
        arquivoComposicao.setArquivo(arquivo);
        arquivoComposicao.setFile(file);
        arquivoComposicao.setDataUpload(new Date());
        arquivoComposicao.setDetentorArquivoComposicao(selecionado.getDetentorArquivoComposicao());
        return arquivoComposicao;
    }

    private Attr criarAtributo(Document doc, String descricao, String valor) {
        Attr attr = doc.createAttribute(descricao);
        attr.setValue(valor);
        return attr;
    }

    @Override
    public Sig recuperar(Object id) {
        Sig sig = em.find(Sig.class, id);
        if (sig.getDetentorArquivoComposicao().getArquivosComposicao() != null) {
            sig.getDetentorArquivoComposicao().getArquivosComposicao().size();
        }
        return sig;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public BigDecimal recuperarCampoSitPrevid(Long id, Integer mes, Integer ano) {
        Query q = this.em.createNativeQuery("" +
            "select case " +
            "when servidorAtivoComAbonoPermanencia is not null then servidorAtivoComAbonoPermanencia " +
            "when servidorAtivoRecluso is not null then servidorAtivoRecluso " +
            "when aposentadoriaPorDecisaoJudicial is not null then aposentadoriaPorDecisaoJudicial " +
            "when aposentadoCompulsoria is not null then aposentadoCompulsoria " +
            "when aposentadoPorInvalidez is not null then aposentadoPorInvalidez " +
            "when aposentadoPorIdade is not null then aposentadoPorIdade " +
            "when aposentadoTempoContribuicao is not null then aposentadoTempoContribuicao " +
            "when aposentado is not null then aposentado " +
            "when instituirPensao is not null then instituirPensao " +
            "when pensionista is not null then pensionista " +
            "when outros is not null then outros " +
            "else servidorAtivo " +
            "end as sitPrevid " +
            "from ( " +
            "select 1  as servidorAtivo, " +
            "(select 2 " +
            "from ITEMFICHAFINANCEIRAFP item " +
            "inner join eventofp e on item.EVENTOFP_ID = e.ID " +
            "where item.FICHAFINANCEIRAFP_ID = ficha.id " +
            "and e.CODIGO = '441'  and rownum <= 1) as servidorAtivoComAbonoPermanencia, " +
            "(select 3 " +
            "from AFASTAMENTO a " +
            "inner join TIPOAFASTAMENTO tipo on a.TIPOAFASTAMENTO_ID = tipo.ID " +
            "where tipo.TIPOAFASTAMENTOESOCIAL = :carcere " +
            "and a.CONTRATOFP_ID = v.id) as servidorAtivoRecluso, " +
            "(select 4 from APOSENTADORIA apo where apo.id = v.id) as aposentado, " +
            "(select 5 " +
            "from APOSENTADORIA apo " +
            "inner join tipoAposentadoria ta on apo.TIPOAPOSENTADORIA_ID = ta.ID " +
            "where ta.CODIGO = 1 " +
            " and apo.id = v.id) as aposentadoTempoContribuicao, " +
            "(select 6 " +
            "from APOSENTADORIA apo " +
            "inner join tipoAposentadoria ta on apo.TIPOAPOSENTADORIA_ID = ta.ID " +
            "where ta.CODIGO = 4 " +
            "and apo.id = v.id) as aposentadoPorIdade, " +
            "(select 7 " +
            "from APOSENTADORIA apo " +
            "inner join tipoAposentadoria ta on apo.TIPOAPOSENTADORIA_ID = ta.ID " +
            "where ta.CODIGO = 3 " +
            "and apo.id = v.id) as aposentadoPorInvalidez, " +
            "(select 8 " +
            "from APOSENTADORIA apo " +
            "inner join tipoAposentadoria ta on apo.TIPOAPOSENTADORIA_ID = ta.ID " +
            "where ta.CODIGO = 2 " +
            "and apo.id = v.id) as aposentadoCompulsoria, " +
            "(select 10 " +
            "from APOSENTADORIA apo " +
            "inner join regraaposentadoria r on r.id = apo.regraaposentadoria_id " +
            "where r.decisaojudicial = :decisaojudicial  " +
            "and apo.id = v.id) as aposentadoriaPorDecisaoJudicial, " +
            "(select 11 " +
            "from Pensao pensao " +
            "where pensao.CONTRATOFP_ID = v.id) as instituirPensao, " +
            "(select 12 " +
            "from PENSIONISTA pen " +
            "where pen.id = v.id) as pensionista, " +
            "coalesce((select 11 " +
            " from AFASTAMENTO a " +
            " inner join TIPOAFASTAMENTO tipo on a.TIPOAFASTAMENTO_ID = tipo.ID " +
            " where tipo.TIPOAFASTAMENTOESOCIAL <> :carcere " +
            "and folha.CALCULADAEM between a.INICIO and coalesce(a.TERMINO, folha.CALCULADAEM)" +
            "and a.CONTRATOFP_ID = v.id), (select 11 " +
            "from vinculofp vv " +
            "where vv.id = v.id  " +
            "and extract(month from vv.FINALVIGENCIA) = folha.mes + 1" +
            " and extract(year from vv.FINALVIGENCIA) = folha.ano)) as outros " +
            "from vinculofp v " +
            "inner join FICHAFINANCEIRAFP ficha on v.ID = ficha.VINCULOFP_ID " +
            "inner join folhadepagamento folha on ficha.FOLHADEPAGAMENTO_ID = folha.ID " +
            "where v.id = :id " +
            "and folha.mes = :mes " +
            "and folha.ano = :ano) " +
            "");
        q.setParameter("id", id);
        q.setParameter("mes", mes);
        q.setParameter("ano", ano);
        q.setParameter("carcere", TipoAfastamentoESocial.CARCERE.name());
        q.setParameter("decisaojudicial", true);


        List resultList = q.getResultList();
        if (resultList.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return (BigDecimal) resultList.get(0);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Map<Long, BigDecimal> recuperarMapCampoSitPrevid(Integer mes, Integer ano) {
        Query q = this.em.createNativeQuery(" " +
            "select id, case " +
            "                 when servidorAtivoComAbonoPermanencia is not null then servidorAtivoComAbonoPermanencia " +
            "                 when servidorAtivoRecluso is not null then servidorAtivoRecluso " +
            "                 when aposentadoriaPorDecisaoJudicial is not null then aposentadoriaPorDecisaoJudicial " +
            "                 when aposentadoCompulsoria is not null then aposentadoCompulsoria " +
            "                 when aposentadoPorInvalidez is not null then aposentadoPorInvalidez " +
            "                 when aposentadoPorIdade is not null then aposentadoPorIdade " +
            "                 when aposentadoTempoContribuicao is not null then aposentadoTempoContribuicao " +
            "                 when aposentado is not null then aposentado " +
            "                 when instituirPensao is not null then instituirPensao " +
            "                 when pensionista is not null then pensionista " +
            "                 when outros is not null then outros " +
            "                 else servidorAtivo " +
            "    end as sitPrevid " +
            "from ( " +
            "         select v.id, 1  as servidorAtivo, " +
            "                (select 2 " +
            "                 from ITEMFICHAFINANCEIRAFP item " +
            "                          inner join eventofp e on item.EVENTOFP_ID = e.ID " +
            "                 where item.FICHAFINANCEIRAFP_ID = ficha.id " +
            "                   and e.CODIGO = '441'  and rownum <= 1) as servidorAtivoComAbonoPermanencia, " +
            "                (select 3 " +
            "                 from AFASTAMENTO a " +
            "                          inner join TIPOAFASTAMENTO tipo on a.TIPOAFASTAMENTO_ID = tipo.ID " +
            "                 where tipo.TIPOAFASTAMENTOESOCIAL = :carcere " +
            "                   and a.CONTRATOFP_ID = v.id) as servidorAtivoRecluso, " +
            "                (select 4 from APOSENTADORIA apo where apo.id = v.id) as aposentado, " +
            "                (select 5 " +
            "                 from APOSENTADORIA apo " +
            "                          inner join tipoAposentadoria ta on apo.TIPOAPOSENTADORIA_ID = ta.ID " +
            "                 where ta.CODIGO = 1 " +
            "                   and apo.id = v.id) as aposentadoTempoContribuicao, " +
            "                (select 6 " +
            "                 from APOSENTADORIA apo " +
            "                          inner join tipoAposentadoria ta on apo.TIPOAPOSENTADORIA_ID = ta.ID " +
            "                 where ta.CODIGO = 4 " +
            "                   and apo.id = v.id) as aposentadoPorIdade, " +
            "                (select 7 " +
            "                 from APOSENTADORIA apo " +
            "                          inner join tipoAposentadoria ta on apo.TIPOAPOSENTADORIA_ID = ta.ID " +
            "                 where ta.CODIGO = 3 " +
            "                   and apo.id = v.id) as aposentadoPorInvalidez, " +
            "                (select 8 " +
            "                 from APOSENTADORIA apo " +
            "                          inner join tipoAposentadoria ta on apo.TIPOAPOSENTADORIA_ID = ta.ID " +
            "                 where ta.CODIGO = 2 " +
            "                   and apo.id = v.id) as aposentadoCompulsoria, " +
            "                (select 10 " +
            "                 from APOSENTADORIA apo " +
            "                       inner join regraaposentadoria r on r.id = apo.regraaposentadoria_id " +
            "                 where r.decisaojudicial = :decisaojudicial " +
            "                   and apo.id = v.id) as aposentadoriaPorDecisaoJudicial, " +
            "                (select 11 " +
            "                 from Pensao pensao " +
            "                 where pensao.CONTRATOFP_ID = v.id) as instituirPensao, " +
            "                (select 12 " +
            "                 from PENSIONISTA pen " +
            "                 where pen.id = v.id) as pensionista, " +
            "                coalesce((select 11 " +
            "                          from AFASTAMENTO a " +
            "                                   inner join TIPOAFASTAMENTO tipo on a.TIPOAFASTAMENTO_ID = tipo.ID " +
            "                          where tipo.TIPOAFASTAMENTOESOCIAL <> :carcere " +
            "                            and folha.CALCULADAEM between a.INICIO and coalesce(a.TERMINO, folha.CALCULADAEM) " +
            "                            and a.CONTRATOFP_ID = v.id), (select 11 " +
            "                                                          from vinculofp vv " +
            "                                                          where vv.id = v.id " +
            "                                                            and extract(month from vv.FINALVIGENCIA) = folha.mes + 1 " +
            "                                                            and extract(year from vv.FINALVIGENCIA) = folha.ano)) as outros " +
            "         from vinculofp v " +
            "                  inner join FICHAFINANCEIRAFP ficha on v.ID = ficha.VINCULOFP_ID " +
            "                  inner join folhadepagamento folha on ficha.FOLHADEPAGAMENTO_ID = folha.ID " +
            "         where folha.mes = :mes " +
            "           and folha.ano = :ano) ");
        q.setParameter("mes", mes);
        q.setParameter("ano", ano);
        q.setParameter("carcere", TipoAfastamentoESocial.CARCERE.name());
        q.setParameter("decisaojudicial", true);
        Map<Long, BigDecimal> mapa = Maps.newHashMap();
        List resultList = q.getResultList();

        if (!resultList.isEmpty()) {
            for (Object[] obj : (List<Object[]>) resultList) {
                Long id = obj[0] != null ? ((BigDecimal) obj[0]).longValue() : null;
                BigDecimal valor = (BigDecimal) obj[1];
                mapa.put(id, valor);
            }
        }
        return mapa;
    }
}

