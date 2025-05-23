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
import java.io.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static br.com.webpublico.util.Util.getValorSemPontoEVirgulas;
import static br.com.webpublico.util.Util.isStringNulaOuVazia;

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
        DateTime dateTime = DataUtil.criarDataComMesEAno(selecionado.getMes().getNumeroMes(), selecionado.getExercicio().getAno());
        vinculos = vinculoFPFacade.recuperarTodosVinculosVigentesComFicha(dateTime.toDate(), selecionado.getMes(), selecionado.getExercicio().getAno());
        Element arquivo = doc.createElement("arquivo");
        doc.appendChild(arquivo);
//        assistenteBarraProgresso.setTotal(12);
        assistenteBarraProgresso.setTotal(14);

        Element cabecalho = criarTag(doc, "cabecalho", arquivo);
        preencherTagCabecario(doc, cabecalho, selecionado);
        assistenteBarraProgresso.conta();

//        String tipoLogradouro = "";
//        if (selecionado.getEnteFederativo().getEnderecoPrincipal() != null && selecionado.getEnteFederativo().getEnderecoPrincipal().getTipoLogradouro() != null) {
//            tipoLogradouro = getTipoLogradouro(selecionado);
//        }
//
//        Cidade cidade = null;
//        if (selecionado.getEnteFederativo().getEnderecoPrincipal() != null) {
//            cidade = cidadeFacade.recuperaCidadePorNomeEEstado(selecionado.getEnteFederativo().getEnderecoPrincipal().getLocalidade(), selecionado.getEnteFederativo().getEnderecoPrincipal().getUf());
//        }
//        Element endereco = criarTag(doc, "endereco", arquivo);
//        preencherEndereco(doc, endereco, selecionado, tipoLogradouro, cidade);
//        assistenteBarraProgresso.conta();
//
//        Pessoa enteFederativo = pessoaFacade.recuperarPJ(selecionado.getEnteFederativo().getId());
//        if (!enteFederativo.getRepresentantesLegal().isEmpty()) {
//            RepresentanteLegalPessoa rep = enteFederativo.getRepresentantesLegal().get(0);
//            Element chefe = criarTag(doc, "chefe", arquivo);
//            preencherTagChefe(doc, chefe, rep);
//        }
//
//        List<HierarquiaOrganizacional> hos = hierarquiaOrganizacionalFacade.recuperaHierarquiaOrgaoVigentes(dateTime.toDate());
//        Integer resumoqtOrgao = 0;
//        for (HierarquiaOrganizacional ho : hos) {
//            try {
//                if (ho.getSubordinada() == null || ho.getSubordinada().getEntidade() == null) {
//                    logger.error("Não existe entidade para a unidade {}", ho.getDescricao());
//                }
//                PessoaJuridica pj = pessoaJuridicaFacade.recuperar(ho.getSubordinada().getEntidade().getPessoaJuridica().getId());
//                String uniGestora = "";
//                UnidadeOrganizacional uo = unidadeOrganizacionalFacade.recuperar(ho.getSubordinada().getId());
//                if (uo.getUnidadeGestoraUnidadesOrganizacionais().isEmpty()) {
//                    uniGestora = "N";
//                } else {
//                    uniGestora = "S";
//                }
//                resumoqtOrgao += 1;
//                Element orgao = criarTag(doc, "orgao", arquivo);
//                preencherTagOrgao(doc, orgao, ho, uniGestora, pj);
//
//            } catch (Exception e) {
//                logger.error("Não existe entidade para a unidade {}", ho.getDescricao());
//            }
//        }
//        assistenteBarraProgresso.conta();
//
//        Integer resumoqtServidor = 0;
//        for (VinculoFP vinculo : vinculos) {
//            PessoaFisica pf = pessoaFisicaFacade.recuperar(vinculo.getMatriculaFP().getPessoa().getId());
//            RegistroDeObito registroDeObito = registroDeObitoFacade.buscarRegistroDeObitoPorPessoaFisica(pf);
//            resumoqtServidor += 1;
//            Element servidor = criarTag(doc, "servidor", arquivo);
//            preencherTagServidor(doc, servidor, vinculo, pf, registroDeObito);
//        }
//        assistenteBarraProgresso.conta();
//
//        Integer resumoqtVinculoFuncional = 0;
//        Map<Long, BigDecimal> mapaCacheSitprev = recuperarMapCampoSitPrevid(selecionado.getMes().getNumeroMes(), selecionado.getExercicio().getAno());
//        for (VinculoFP vinculo : vinculos) {
//            if (vinculo instanceof ContratoFP) {
//                ContratoFP contrato = (ContratoFP) vinculo;
//                String tipoVinculo = "";
//                if (contrato.getSituacaoContratoFPVigente().getInicioVigencia().before(dateTime.toDate())) {
//
//                    if (ModalidadeContratoFP.CONCURSADOS.equals(contrato.getModalidadeContratoFP().getCodigo()) && TipoRegime.ESTATUTARIO.equals(contrato.getTipoRegime().getCodigo())) {
//                        tipoVinculo = "1";
//                    } else if (ModalidadeContratoFP.CONTRATO_TEMPORARIO.equals(contrato.getModalidadeContratoFP().getCodigo())) {
//                        tipoVinculo = "3";
//                    } else if (ModalidadeContratoFP.CARGO_COMISSAO.equals(contrato.getModalidadeContratoFP().getCodigo())) {
//                        tipoVinculo = "4";
//                    } else if (ModalidadeContratoFP.CARGO_ELETIVO.equals(contrato.getModalidadeContratoFP().getCodigo())) {
//                        tipoVinculo = "5";
//                    } else if (TipoRegime.CELETISTA.equals(contrato.getTipoRegime().getCodigo())) {
//                        tipoVinculo = "6";
//                    } else {
//                        tipoVinculo = "99";
//                    }
//                }
//                resumoqtVinculoFuncional += 1;
//                BigDecimal sitPrevid = null;
//                sitPrevid = mapaCacheSitprev.get(vinculo.getId());
//                Element vinculoFuncional = criarTag(doc, "vinculoFuncional", arquivo);
//                preencherTagVinculoFuncional(doc, vinculoFuncional, contrato, tipoVinculo, sitPrevid);
//
//            }
//        }
//        assistenteBarraProgresso.conta();
//
//        Integer resumoqtMovimentacaoFuncional = 0;
//        for (VinculoFP vinculo : vinculos) {
//            if (vinculo instanceof ContratoFP) {
//                ExoneracaoRescisao exoneracao = exoneracaoRescisaoFacade.recuperaExoneracaoRecisao2(vinculo.getContratoFP());
//                if (exoneracao != null) {
//                    resumoqtMovimentacaoFuncional += 1;
//                    Element movimentacaoFuncional = criarTag(doc, "movimentacaoFuncional", arquivo);
//                    preencherTagMovimentacaoFuncional(doc, movimentacaoFuncional, exoneracao);
//                }
//            }
//        }
//        assistenteBarraProgresso.conta();
//
//        Integer resumoqtHistoricoFinanceiro = 0;
//        List<FichaFinanceiraFP> fichas = fichaFinanceiraFPFacade.recuperarFichaFinanceiraFPPorAnoMes(selecionado.getExercicio().getAno(), selecionado.getMes().getNumeroMes());
//
//        ModuloExportacao moduloExportacao = moduloExportacaoFacade.recuperaModuloExportacaoPorCodigo(MODULO);
//        GrupoExportacao grupo = grupoExportacaoFacade.recuperaGrupoExportacaoPorCodigoEModuloExportacao(GRUPO, moduloExportacao);
//        if (!fichas.isEmpty()) {
//
//            for (FichaFinanceiraFP ficha : fichas) {
//                if (ficha.getVinculoFP() instanceof ContratoFP) {
//                    ContratoFP contrato = (ContratoFP) ficha.getVinculoFP();
//                    EnquadramentoFuncional enquadramentoFuncional = enquadramentoFuncionalFacade.recuperaEnquadramentoFuncionalVigente(contrato, dateTime.toDate());
//                    String vlRemCargoEfetivo = "0";
//                    if (enquadramentoFuncional.getVencimentoBase() != null && contrato.getModalidadeContratoFP().getDescricao().equals("CONCURSADO")) {
//                        vlRemCargoEfetivo = getValorSemPontoEVirgulas(enquadramentoFuncional.getVencimentoBase());
//                    }
//                    resumoqtHistoricoFinanceiro += 1;
//                    Element historicoFinanceiro = criarTag(doc, "historicoFinanceiro", arquivo);
//
//                    ValorFinanceiroRH valorFinanceiroRH = fichaFinanceiraFPFacade.recuperarValorPorMesAndAnoPorVinculoFpEModuloExportacao(ficha.getVinculoFP().getId(), selecionado.getMes(), selecionado.getExercicio().getAno(), grupo, moduloExportacao, TipoFolhaDePagamento.values());
//                    preencherTagHistoricoFinanceiro(doc, historicoFinanceiro, ficha, contrato, valorFinanceiroRH, vlRemCargoEfetivo);
//                }
//            }
//        }
//        assistenteBarraProgresso.conta();
//
//        Integer resumoqtBeneficioServidor = 0;
//        for (VinculoFP vinculo : vinculos) {
//            if (vinculo instanceof Aposentadoria) {
//                Aposentadoria aposentadoria = (Aposentadoria) vinculo;
//                if (aposentadoria != null) {
//                    String paridade = null;
//                    if (TipoReajusteAposentadoria.PARIDADE.equals(aposentadoria.getTipoReajusteAposentadoria())) {
//                        paridade = "S";
//                    } else {
//                        paridade = "N";
//                    }
//                    String tipoBeneficio = "";
//                    if (TipoAposentadoria.TEMPO_DE_CONTRIBUICAO.equals(aposentadoria.getTipoAposentadoria().getCodigo())) {
//                        tipoBeneficio = "1";
//                    } else if (TipoAposentadoria.COMPULSORIA.equals(aposentadoria.getTipoAposentadoria().getCodigo())) {
//                        tipoBeneficio = "4";
//                    } else if (TipoAposentadoria.INVALIDEZ.equals(aposentadoria.getTipoAposentadoria().getCodigo())) {
//                        tipoBeneficio = "3";
//                    } else if (TipoAposentadoria.IDADE.equals(aposentadoria.getTipoAposentadoria().getCodigo())) {
//                        tipoBeneficio = "2";
//                    }
//                    BigDecimal salarioInicial = aposentadoriaFacade.buscarValorAposentadoriaPorAposentado(vinculo, vinculo.getInicioVigencia());
//                    BigDecimal salarioAtual = aposentadoriaFacade.buscarValorAposentadoriaPorAposentado(vinculo, dateTime.toDate());
//                    resumoqtBeneficioServidor += 1;
//                    Element beneficioServidor = criarTag(doc, "beneficioServidor", arquivo);
//                    preencherTagBeneficioServidor(doc, beneficioServidor, vinculo, aposentadoria, paridade, salarioInicial, salarioAtual, tipoBeneficio);
//                }
//            }
//        }
//        assistenteBarraProgresso.conta();
//
//        Integer resumoqtDependente = 0;
//        for (VinculoFP vinculo : vinculos) {
//            List<Dependente> dependentes = dependenteFacade.dependentesPorResponsavelNative(vinculo.getMatriculaFP().getPessoa());
//            for (Dependente dependenciSig : dependentes) {
//                resumoqtDependente += 1;
//                Element dependente = criarTag(doc, "dependente", arquivo);
//                preencherTagDependente(doc, dependente, dependenciSig);
//            }
//        }
//        assistenteBarraProgresso.conta();
//
//        Integer resumoqtPensionista = 0;
//
//        List<Pensionista> listaPensionista = pensionistaFacade.recuperaPensionistasVigente(dateTime.toDate());
//        for (Pensionista pensi : listaPensionista) {
//            resumoqtPensionista += 1;
//            Element pensionista = criarTag(doc, "pensionista", arquivo);
//            preencherTagPensionista(doc, pensionista, pensi);
//        }
//        assistenteBarraProgresso.conta();
//
//        for (VinculoFP vinculo : vinculos) {
//            List<Dependente> dependentes = dependenteFacade.dependentesPorResponsavelNative(vinculo.getMatriculaFP().getPessoa());
//            for (Dependente dependenciSig : dependentes) {
//                DependenteVinculoFP dependenciaVigente = dependenciSig.getDependentesVinculosFPsVigente(dateTime.toDate());
//                List<BeneficioPensaoAlimenticia> beneficioPensaoAlimenticias = pensaoAlimenticiaFacade.buscarBeneficioPensaoAlimenticiaVigentePorPessoa(dependenciSig.getDependente(), dateTime.toDate());
//
//                String tipoDependencia = "";
//                if(dependenciSig.getGrauDeParentesco() != null && dependenciSig.getGrauDeParentesco().getTipoParentesco() != null) {
//                    tipoDependencia = getTipoDependencia(dependenciSig);
//                }
//                Element dependencia = criarTag(doc, "dependencia", arquivo);
//                preencherTagDependencia(doc, dependencia, dependenciSig, vinculo, dependenciaVigente, tipoDependencia, !beneficioPensaoAlimenticias.isEmpty());
//            }
//        }
//        assistenteBarraProgresso.conta();
//
//
//        Integer resumoqtBeneficioPensionista = 0;
//        for (Pensionista pensi : listaPensionista) {
//            resumoqtBeneficioPensionista += 1;
//            BigDecimal salarioInicial = pensionistaFacade.buscarValorPensionistaPerPensionista(pensi, pensi.getInicioVigencia());
//            BigDecimal salarioFinal = pensionistaFacade.buscarValorPensionistaPerPensionista(pensi, dateTime.toDate());
//
//            String tipoPensaoMorte = "";
//            if (TipoFundamentacao.ART_40_CF_I.equals(pensi.getTipoFundamentacao())) {
//                tipoPensaoMorte = "1";
//            } else if (TipoFundamentacao.ART_40_CF_II.equals(pensi.getTipoFundamentacao())) {
//                tipoPensaoMorte = "2";
//            }
//            Element beneficioPensionista = criarTag(doc, "beneficioPensionista", arquivo);
//            preencherTagBeneficioPensionista(doc, beneficioPensionista, pensi, tipoPensaoMorte, salarioInicial, salarioFinal);
//        }
//        assistenteBarraProgresso.conta();
//
//
//        Element resumo = criarTag(doc, "resumo", arquivo);
//        preencherTagResumo(doc, resumo, resumoqtOrgao, resumoqtServidor, resumoqtVinculoFuncional, resumoqtMovimentacaoFuncional, resumoqtHistoricoFinanceiro,
//            resumoqtBeneficioServidor, resumoqtDependente, resumoqtPensionista, resumoqtBeneficioPensionista);
//        assistenteBarraProgresso.conta();

// alterado ------------------------------------------------------------------
        // esdereço

        String tipoLogradouros = "";
        if (selecionado.getEnteFederativo().getEnderecoPrincipal() != null && selecionado.getEnteFederativo().getEnderecoPrincipal().getTipoLogradouro() != null) {
            tipoLogradouros = getTipoLogradouro(selecionado);
        }

        Cidade cidade = null;
        if (selecionado.getEnteFederativo().getEnderecoPrincipal() != null) {
            cidade = cidadeFacade.recuperaCidadePorNomeEEstado(selecionado.getEnteFederativo().getEnderecoPrincipal().getLocalidade(), selecionado.getEnteFederativo().getEnderecoPrincipal().getUf());

            Element endereco = criarTag(doc, "endereco", arquivo);

            if (selecionado.getEnteFederativo().getEmail() != null) {
                Element emailEnte = doc.createElement("emailEnte");
                emailEnte.appendChild(doc.createTextNode(selecionado.getEnteFederativo().getEmail()));
                endereco.appendChild(emailEnte);
            }

            if (!isStringNulaOuVazia(tipoLogradouros)) {
                Element tipoLogradouro = doc.createElement("tipoLogradouro");
                tipoLogradouro.appendChild(doc.createTextNode(tipoLogradouros));
                endereco.appendChild(tipoLogradouro);
            }

            if (selecionado.getEnteFederativo().getEnderecoPrincipal() != null) {
                Element logradouro = doc.createElement("logradouro");
                logradouro.appendChild(doc.createTextNode(selecionado.getEnteFederativo().getEnderecoPrincipal().getLogradouro()));
                endereco.appendChild(logradouro);

                Element numLogradouro = doc.createElement("numLogradouro");
                numLogradouro.appendChild(doc.createTextNode(selecionado.getEnteFederativo().getEnderecoPrincipal().getNumero()));
                endereco.appendChild(numLogradouro);

                Element complemento = doc.createElement("complemento");
                complemento.appendChild(doc.createTextNode(selecionado.getEnteFederativo().getEnderecoPrincipal().getComplemento()));
                endereco.appendChild(complemento);

                Element bairro = doc.createElement("bairro");
                bairro.appendChild((doc.createTextNode(selecionado.getEnteFederativo().getEnderecoPrincipal().getBairro())));
                endereco.appendChild(bairro);

                if (cidade != null) {
                    Element municipio = doc.createElement("municipio");
                    municipio.appendChild(doc.createTextNode(cidade.getCodigoIBGE().toString()));
                    endereco.appendChild(municipio);
                }

                Element uf = doc.createElement("uf");
                uf.appendChild(doc.createTextNode(selecionado.getEnteFederativo().getEnderecoPrincipal().getUf()));
                endereco.appendChild(uf);

                Element cep = doc.createElement("cep");
                cep.appendChild(doc.createTextNode(selecionado.getEnteFederativo().getEnderecoPrincipal().getCep()));
                endereco.appendChild(cep);
            }
        }
        assistenteBarraProgresso.conta();

        // Chefe
        Pessoa enteFederativo = pessoaFacade.recuperarPJ(selecionado.getEnteFederativo().getId());
        if (!enteFederativo.getRepresentantesLegal().isEmpty()) {
            RepresentanteLegalPessoa rep = enteFederativo.getRepresentantesLegal().get(0);

            Element chefe = criarTag(doc, "chefe", arquivo);

            Element idChefe = doc.createElement("idChefe");
            idChefe.appendChild(doc.createTextNode(rep.getId().toString()));
            chefe.appendChild(idChefe);

            Element nome = doc.createElement("nome");
            nome.appendChild(doc.createTextNode(rep.getRepresentante().getNome()));
            chefe.appendChild(nome);

            Element cpf = doc.createElement("cpf");
            cpf.appendChild(doc.createTextNode(StringUtil.removeCaracteresEspeciaisSemEspaco(rep.getRepresentante().getCpf_Cnpj())));
            chefe.appendChild(cpf);

            Element dtInicioMand = doc.createElement("dtInicioMand");
            dtInicioMand.appendChild(doc.createTextNode(converterDataString("yyyy-MM-dd", rep.getDataInicio())));
            chefe.appendChild(dtInicioMand);

            if(rep.getDataFim() != null) {
                Element dtFimMand = doc.createElement("dtFimMand");
                dtFimMand.appendChild(doc.createTextNode(converterDataString("yyyy-MM-dd", rep.getDataFim())));
                chefe.appendChild(dtFimMand);
            }

            if(rep.getRepresentante().getEmail() != null) {
                Element email = doc.createElement("email");
                email.appendChild(doc.createTextNode(rep.getRepresentante().getEmail()));
                chefe.appendChild(email);
            }
        }
        assistenteBarraProgresso.conta();

        //orgão
        List<HierarquiaOrganizacional> hos = hierarquiaOrganizacionalFacade.recuperaHierarquiaOrgaoVigentes(dateTime.toDate());
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

                Element idOrgao = doc.createElement("idOrgao");
                idOrgao.appendChild(doc.createTextNode(ho.getSubordinada().getId().toString()));
                orgao.appendChild(idOrgao);

                Element nomeOrgao = doc.createElement("nomeOrgao");
                nomeOrgao.appendChild(doc.createTextNode(ho.getDescricao()));
                orgao.appendChild(nomeOrgao);

                Element cnpjOrgao = doc.createElement("cnpjOrgao");
                cnpjOrgao.appendChild(doc.createTextNode(StringUtil.removeCaracteresEspeciaisSemEspaco(pj.getCnpj())));
                orgao.appendChild(cnpjOrgao);

                if(ho.getSubordinada().getEntidade() != null) {
                    Element poder = doc.createElement("poder");
                    poder.appendChild(doc.createTextNode(ho.getSubordinada().getEntidade().getEsferaDoPoder().getCodigoSiprev()));
                    orgao.appendChild(poder);
                }

                Element unidadeGestora = doc.createElement("unidadeGestora");
                unidadeGestora.appendChild(doc.createTextNode(uniGestora));
                orgao.appendChild(unidadeGestora);

            } catch (Exception e) {
                logger.error("Não existe entidade para a unidade {}", ho.getDescricao());
            }
        }
        assistenteBarraProgresso.conta();

        //servidor----------------------------------------------------------------------------------------------------------------------

        Integer resumoqtServidor = 0;
        for (VinculoFP vinculo : vinculos) {
            PessoaFisica pf = pessoaFisicaFacade.recuperar(vinculo.getMatriculaFP().getPessoa().getId());
            RegistroDeObito registroDeObito = registroDeObitoFacade.buscarRegistroDeObitoPorPessoaFisica(pf);
            resumoqtServidor += 1;

            Element servidor = criarTag(doc, "servidor", arquivo);

            Element idServidor = doc.createElement("idServidor");
            idServidor.appendChild(doc.createTextNode(vinculo.getId().toString()));
            servidor.appendChild(idServidor);

            if (pf.getCarteiraDeTrabalho() != null) {
                Element pasep = doc.createElement("pasep");
                pasep.appendChild(doc.createTextNode(pf.getCarteiraDeTrabalho().getPisPasep()));
                servidor.appendChild(pasep);
            }

            Element nome = doc.createElement("nome");
            nome.appendChild(doc.createTextNode(pf.getNome()));
            servidor.appendChild(nome);

            Element cpf = doc.createElement("cpf");
            cpf.appendChild(doc.createTextNode(StringUtil.removeCaracteresEspeciaisSemEspaco(pf.getCpf())));
            servidor.appendChild(cpf);

            Element dtNascto = doc.createElement("dtNascto");
            dtNascto.appendChild(doc.createTextNode(DataUtil.getDataFormatada(pf.getDataNascimento(), "yyyy-MM-dd")));
            servidor.appendChild(dtNascto);

            Element sexo = doc.createElement("sexo");
            sexo.appendChild(doc.createTextNode(pf.getSexo().getSigla()));
            servidor.appendChild(sexo);

            if(registroDeObito != null) {
                Element dtObito = doc.createElement("dtObito");
                dtObito.appendChild(doc.createTextNode(converterDataString("yyyy-MM-dd", registroDeObito.getDataFalecimento())));
                servidor.appendChild(dtObito);
            }

            Element nomeMae = doc.createElement("nomeMae");
            nomeMae.appendChild(doc.createTextNode(pf.getMae()));
            servidor.appendChild(nomeMae);

            if(pf.getEstadoCivil() != null) {
                Element estadoCivil = doc.createElement("estadoCivil");
                estadoCivil.appendChild(doc.createTextNode(pf.getEstadoCivil().getCodigoSiprev()));
                servidor.appendChild(estadoCivil);
            }

            Element dtIngressoServPublico = doc.createElement("dtIngressoServPublico");
            dtIngressoServPublico.appendChild(doc.createTextNode(converterDataString("yyyy-MM-dd", vinculo.getInicioVigencia())));
            servidor.appendChild(dtIngressoServPublico);
        }
        assistenteBarraProgresso.conta();

        // vinculoFuncional
        Integer resumoqtVinculoFuncional = 0;
        Map<Long, BigDecimal> mapaCacheSitprev = recuperarMapCampoSitPrevid(selecionado.getMes().getNumeroMes(), selecionado.getExercicio().getAno());
        for (VinculoFP vinculo : vinculos) {
            if (vinculo instanceof ContratoFP) {
                ContratoFP contrato = (ContratoFP) vinculo;
                String tipoVinculos = "";
                if (contrato.getSituacaoContratoFPVigente().getInicioVigencia().before(dateTime.toDate())) {

                    if (ModalidadeContratoFP.CONCURSADOS.equals(contrato.getModalidadeContratoFP().getCodigo()) && TipoRegime.ESTATUTARIO.equals(contrato.getTipoRegime().getCodigo())) {
                        tipoVinculos = "1";
                    } else if (ModalidadeContratoFP.CONTRATO_TEMPORARIO.equals(contrato.getModalidadeContratoFP().getCodigo())) {
                        tipoVinculos = "3";
                    } else if (ModalidadeContratoFP.CARGO_COMISSAO.equals(contrato.getModalidadeContratoFP().getCodigo())) {
                        tipoVinculos = "4";
                    } else if (ModalidadeContratoFP.CARGO_ELETIVO.equals(contrato.getModalidadeContratoFP().getCodigo())) {
                        tipoVinculos = "5";
                    } else if (TipoRegime.CELETISTA.equals(contrato.getTipoRegime().getCodigo())) {
                        tipoVinculos = "6";
                    } else {
                        tipoVinculos = "99";
                    }
                }
                BigDecimal sitPrevi = null;
                sitPrevi = mapaCacheSitprev.get(vinculo.getId());
                resumoqtVinculoFuncional += 1;
                Element vinculoFuncional = criarTag(doc, "vinculoFuncional", arquivo);

                Element idVinculoFuncional = doc.createElement("idVinculoFuncional");
                idVinculoFuncional.appendChild(doc.createTextNode(contrato.getModalidadeContratoFP().getId().toString()));
                vinculoFuncional.appendChild(idVinculoFuncional);

                Element idServidor = doc.createElement("idServidor");
                idServidor.appendChild(doc.createTextNode(contrato.getId().toString()));
                vinculoFuncional.appendChild(idServidor);

                if(!isStringNulaOuVazia(tipoVinculos)) {
                    Element tipoVinculo = doc.createElement("tipoVinculo");
                    tipoVinculo.appendChild(doc.createTextNode(tipoVinculos));
                    vinculoFuncional.appendChild(tipoVinculo);
                }

                Element idOrgao = doc.createElement("idOrgao");
                idOrgao.appendChild(doc.createTextNode(contrato.getUnidadeOrganizacional().getId().toString()));
                vinculoFuncional.appendChild(idOrgao);

                Element cargo = doc.createElement("cargo");
                cargo.appendChild(doc.createTextNode(StringUtil.removeEspacos(contrato.getCargo().getDescricao())));
                vinculoFuncional.appendChild(cargo);

                if(contrato.getCargo().getInicioVigencia() != null) {
                    Element dtIniCargo = doc.createElement("dtIniCargo");
                    dtIniCargo.appendChild(doc.createTextNode(converterDataString("yyyy-MM-dd", contrato.getCargo().getInicioVigencia())));
                    vinculoFuncional.appendChild(dtIniCargo);
                }

                Element matricula = doc.createElement("matricula");
                matricula.appendChild(doc.createTextNode(StringUtil.removeCaracteresEspeciaisSemEspaco(contrato.getMatriculaFP().getMatricula())));
                vinculoFuncional.appendChild(matricula);

                if(!isStringNulaOuVazia(contrato.getCargo().getDescricaoCarreira())) {
                    Element carreira = doc.createElement("carreira");
                    carreira.appendChild(doc.createTextNode(contrato.getCargo().getDescricaoCarreira()));
                    vinculoFuncional.appendChild(carreira);
                }

                Element regime = doc.createElement("regime");
                regime.appendChild(doc.createTextNode(contrato.getPrevidenciaVinculoFPVigente().getTipoPrevidenciaFP().getCodigo() == 1 ? "5" : "1"));
                vinculoFuncional.appendChild(regime);

                Element sitFuncional = doc.createElement("sitFuncional");
                sitFuncional.appendChild(doc.createTextNode("1"));
                vinculoFuncional.appendChild(sitFuncional);

                if(sitPrevi != null) {
                    Element sitPrevid = doc.createElement("sitPrevid");
                    sitPrevid.appendChild(doc.createTextNode(sitPrevi.toString()));
                    vinculoFuncional.appendChild(sitPrevid);
                }
                Element dedicacaoExclusiva = doc.createElement("dedicacaoExclusiva");
                dedicacaoExclusiva.appendChild(doc.createTextNode( contrato.getCargo().getDedicacaoExclusivaSIPREV() ? "S" : "N"));
                vinculoFuncional.appendChild(dedicacaoExclusiva);

                Element tecnicoCientifico = doc.createElement("tecnicoCientifico");
                tecnicoCientifico.appendChild(doc.createTextNode(contrato.getCargo().getTecnicoCientificoSIPREV() ? "S" : "N"));
                vinculoFuncional.appendChild(tecnicoCientifico);

                Element acumulavel = doc.createElement("acumulavel");
                acumulavel.appendChild(doc.createTextNode(contrato.getCargo().getTipoAcumulavelSIG().getCodigo().toString()));
                vinculoFuncional.appendChild(acumulavel);

                Element tipoServidor = doc.createElement("tipoServidor");
                tipoServidor.appendChild(doc.createTextNode("1"));
                vinculoFuncional.appendChild(tipoServidor);

            }
        }
        assistenteBarraProgresso.conta();

        // movimentação funcional -------------------------------------------------------------

        Integer resumoqtMovimentacaoFuncional = 0;
        for (VinculoFP vinculo : vinculos) {
            if (vinculo instanceof ContratoFP) {
                ExoneracaoRescisao exoneracao = exoneracaoRescisaoFacade.recuperaExoneracaoRecisao2(vinculo.getContratoFP());
                if (exoneracao != null) {
                    resumoqtMovimentacaoFuncional += 1;
                    Element movimentacaoFuncional = criarTag(doc, "movimentacaoFuncional", arquivo);

                    Element idMovimentacaoFuncional = doc.createElement("idMovimentacaoFuncional");
                    idMovimentacaoFuncional.appendChild(doc.createTextNode(exoneracao.getId().toString()));
                    movimentacaoFuncional.appendChild(idMovimentacaoFuncional);

                    Element idVinculoFuncional = doc.createElement("idVinculoFuncional");
                    idVinculoFuncional.appendChild(doc.createTextNode(exoneracao.getVinculoFP().getContratoFP().getModalidadeContratoFP().getId().toString()));
                    movimentacaoFuncional.appendChild(idVinculoFuncional);

                    Element sitFuncional = doc.createElement("sitFuncional");
                    sitFuncional.appendChild(doc.createTextNode("2"));
                    movimentacaoFuncional.appendChild(sitFuncional);

                    Element dtDesligamentoOrgao = doc.createElement("dtDesligamentoOrgao");
                    dtDesligamentoOrgao.appendChild(doc.createTextNode(converterDataString("yyyy-MM-dd", exoneracao.getDataRescisao())));
                    movimentacaoFuncional.appendChild(dtDesligamentoOrgao);
                }
            }
        }
        assistenteBarraProgresso.conta();

        //historicoFinanceiro ----------------------------------------------------------------------

        Integer resumoqtHistoricoFinanceiro = 0;
        List<FichaFinanceiraFP> fichas = fichaFinanceiraFPFacade.recuperarFichaFinanceiraFPPorAnoMes(selecionado.getExercicio().getAno(), selecionado.getMes().getNumeroMes());

        ModuloExportacao moduloExportacao = moduloExportacaoFacade.recuperaModuloExportacaoPorCodigo(MODULO);
        GrupoExportacao grupo = grupoExportacaoFacade.recuperaGrupoExportacaoPorCodigoEModuloExportacao(GRUPO, moduloExportacao);
        if (!fichas.isEmpty()) {

            for (FichaFinanceiraFP ficha : fichas) {
                if (ficha.getVinculoFP() instanceof ContratoFP) {
                    ContratoFP contrato = (ContratoFP) ficha.getVinculoFP();
                    EnquadramentoFuncional enquadramentoFuncional = enquadramentoFuncionalFacade.recuperaEnquadramentoFuncionalVigente(contrato, dateTime.toDate());
                    String vlRemCargoEfetivos = "0";
                    if (enquadramentoFuncional.getVencimentoBase() != null && contrato.getModalidadeContratoFP().getDescricao().equals("CONCURSADO")) {
                        vlRemCargoEfetivos = getValorSemPontoEVirgulas(enquadramentoFuncional.getVencimentoBase());
                    }
                    resumoqtHistoricoFinanceiro += 1;
                    ValorFinanceiroRH valorFinanceiroRH = fichaFinanceiraFPFacade.recuperarValorPorMesAndAnoPorVinculoFpEModuloExportacao(ficha.getVinculoFP().getId(), selecionado.getMes(), selecionado.getExercicio().getAno(), grupo, moduloExportacao, TipoFolhaDePagamento.values());

                    Element historicoFinanceiro = criarTag(doc, "historicoFinanceiro", arquivo);

                    BigDecimal remuneracaoBruta = BigDecimal.ZERO;
                    for (ItemFichaFinanceiraFP itemFichaFinanceiraFP : ficha.getItemFichaFinanceiraFP()) {
                        if (TipoEventoFP.VANTAGEM.equals(itemFichaFinanceiraFP.getTipoEventoFP())) {
                            remuneracaoBruta = remuneracaoBruta.add(itemFichaFinanceiraFP.getValor());
                        }
                    }

                    Element idHistoricoFinanceiro = doc.createElement("idHistoricoFinanceiro");
                    idHistoricoFinanceiro.appendChild(doc.createTextNode(ficha.getId().toString()));
                    historicoFinanceiro.appendChild(idHistoricoFinanceiro);

                    Element idServidor = doc.createElement("idServidor");
                    idServidor.appendChild(doc.createTextNode(ficha.getVinculoFP().getId().toString()));
                    historicoFinanceiro.appendChild(idServidor);

                    Element idVinculoFuncional = doc.createElement("idVinculoFuncional");
                    idVinculoFuncional.appendChild(doc.createTextNode(contrato.getModalidadeContratoFP().getId().toString()));
                    historicoFinanceiro.appendChild(idVinculoFuncional);

                    Element ano = doc.createElement("ano");
                    ano.appendChild(doc.createTextNode(ficha.getFolhaDePagamento().getAno().toString()));
                    historicoFinanceiro.appendChild(ano);

                    Element mes = doc.createElement("mes");
                    mes.appendChild(doc.createTextNode(ficha.getFolhaDePagamento().getMes().getNumeroMes().toString()));
                    historicoFinanceiro.appendChild(mes);

                    Element numeroFolha = doc.createElement("numeroFolha");
                    numeroFolha.appendChild(doc.createTextNode(ficha.getFolhaDePagamento().getVersao().toString()));
                    historicoFinanceiro.appendChild(numeroFolha);

                    Element vlRemBruta = doc.createElement("vlRemBruta");
                    vlRemBruta.appendChild(doc.createTextNode(getValorSemPontoEVirgulas(remuneracaoBruta)));
                    historicoFinanceiro.appendChild(vlRemBruta);

                    if(valorFinanceiroRH != null) {
                        Element vlRemContributiva = doc.createElement("vlRemContributiva");
                        vlRemContributiva.appendChild(doc.createTextNode(getValorSemPontoEVirgulas(valorFinanceiroRH.getTotalBase())));
                        historicoFinanceiro.appendChild(vlRemContributiva);
                    }

                    Element vlRemCargoEfetivo = doc.createElement("vlRemCargoEfetivo");
                    vlRemCargoEfetivo.appendChild(doc.createTextNode(vlRemCargoEfetivos));
                    historicoFinanceiro.appendChild(vlRemCargoEfetivo);

                }
            }
        }
        assistenteBarraProgresso.conta();

        // beneficio servidor

        Integer resumoqtBeneficioServidor = 0;
        for (VinculoFP vinculo : vinculos) {
            if (vinculo instanceof Aposentadoria) {
                Aposentadoria aposentadoria = (Aposentadoria) vinculo;
                if (aposentadoria != null) {
                    String paridades = null;
                    if (TipoReajusteAposentadoria.PARIDADE.equals(aposentadoria.getTipoReajusteAposentadoria())) {
                        paridades = "S";
                    } else {
                        paridades = "N";
                    }
                    String tipoBeneficios = "";
                    if (TipoAposentadoria.TEMPO_DE_CONTRIBUICAO.equals(aposentadoria.getTipoAposentadoria().getCodigo())) {
                        tipoBeneficios = "1";
                    } else if (TipoAposentadoria.COMPULSORIA.equals(aposentadoria.getTipoAposentadoria().getCodigo())) {
                        tipoBeneficios = "4";
                    } else if (TipoAposentadoria.INVALIDEZ.equals(aposentadoria.getTipoAposentadoria().getCodigo())) {
                        tipoBeneficios = "3";
                    } else if (TipoAposentadoria.IDADE.equals(aposentadoria.getTipoAposentadoria().getCodigo())) {
                        tipoBeneficios = "2";
                    }
                    BigDecimal salarioInicial = aposentadoriaFacade.buscarValorAposentadoriaPorAposentado(vinculo, vinculo.getInicioVigencia());
                    BigDecimal salarioAtual = aposentadoriaFacade.buscarValorAposentadoriaPorAposentado(vinculo, dateTime.toDate());
                    resumoqtBeneficioServidor += 1;

                    Element beneficioServidor = criarTag(doc, "beneficioServidor", arquivo);

                    Element idBeneficio = doc.createElement("idBeneficio");
                    idBeneficio.appendChild(doc.createTextNode(aposentadoria.getId().toString()));
                    beneficioServidor.appendChild(idBeneficio);

                    Element idServidor = doc.createElement("idServidor");
                    idServidor.appendChild(doc.createTextNode(vinculo.getId().toString()));
                    beneficioServidor.appendChild(idServidor);

                    Element idOrgao = doc.createElement("idOrgao");
                    idOrgao.appendChild(doc.createTextNode(vinculo.getUnidadeOrganizacional().getId().toString()));
                    beneficioServidor.appendChild(idOrgao);

                    if(aposentadoria.getDataRegistro() != null) {
                        Element dtInicioBeneficio = doc.createElement("dtInicioBeneficio");
                        dtInicioBeneficio.appendChild(doc.createTextNode(converterDataString("yyyy-MM-dd", aposentadoria.getDataRegistro())));
                        beneficioServidor.appendChild(dtInicioBeneficio);
                    }

                    Element tipoBeneficio = doc.createElement("tipoBeneficio");
                    tipoBeneficio.appendChild(doc.createTextNode(tipoBeneficios));
                    beneficioServidor.appendChild(tipoBeneficio);

                    if(aposentadoria.getFinalVigencia() != null) {
                        Element dtFimBeneficio = doc.createElement("dtFimBeneficio");
                        dtFimBeneficio.appendChild(doc.createTextNode(converterDataString("yyyy-MM-dd", aposentadoria.getFinalVigencia())));
                        beneficioServidor.appendChild(dtFimBeneficio);
                    }

                    Element vlAtualBeneficio = doc.createElement("vlAtualBeneficio");
                    vlAtualBeneficio.appendChild(doc.createTextNode(getValorSemPontoEVirgulas(salarioAtual)));
                    beneficioServidor.appendChild(vlAtualBeneficio);

                    if(salarioInicial != null) {
                        Element vlInicialBeneficio = doc.createElement("vlInicialBeneficio");
                        vlInicialBeneficio.appendChild(doc.createTextNode(getValorSemPontoEVirgulas(salarioInicial)));
                        beneficioServidor.appendChild(vlInicialBeneficio);
                    }

                    if(aposentadoria.getDataAlteracao() != null) {
                        Element dtUltimaAtualizacao = doc.createElement("dtUltimaAtualizacao");
                        dtUltimaAtualizacao.appendChild(doc.createTextNode(converterDataString("yyyy-MM-dd", aposentadoria.getDataAlteracao())));
                        beneficioServidor.appendChild(dtUltimaAtualizacao);
                    }

                    if(aposentadoria.getDataPublicacao() != null) {
                        Element dtPublicacao = doc.createElement("dtPublicacao");
                        dtPublicacao.appendChild(doc.createTextNode(converterDataString("yyyy-MM-dd", aposentadoria.getDataPublicacao())));
                        beneficioServidor.appendChild(dtPublicacao);
                    }

                    Element paridade = doc.createElement("paridade");
                    paridade.appendChild(doc.createTextNode(paridades));
                    beneficioServidor.appendChild(paridade);

                    Element regime = doc.createElement("regime");
                    regime.appendChild(doc.createTextNode(vinculo.getContratoFP().getPrevidenciaVinculoFPVigente().getTipoPrevidenciaFP().getCodigo() == 1 ? "5" : "1"));
                    beneficioServidor.appendChild(regime);

                    if(aposentadoria.getTipoAposentadoriaEspecialSIG() != null) {
                        Element tipoAposentadoriaEspecial = doc.createElement("tipoAposentadoriaEspecial");
                        tipoAposentadoriaEspecial.appendChild(doc.createTextNode(aposentadoria.getTipoAposentadoriaEspecialSIG().getCodigo().toString()));
                        beneficioServidor.appendChild(tipoAposentadoriaEspecial);
                    }
                }
            }
        }
        assistenteBarraProgresso.conta();

        //dependente ---------------------------------------------------------------------------

        Integer resumoqtDependente = 0;
        for (VinculoFP vinculo : vinculos) {
            List<Dependente> dependentes = dependenteFacade.dependentesPorResponsavelNative(vinculo.getMatriculaFP().getPessoa());
            for (Dependente dependenciSig : dependentes) {
                resumoqtDependente += 1;
                Element dependente = criarTag(doc, "dependente", arquivo);

                Element idDependente = doc.createElement("idDependente");
                idDependente.appendChild(doc.createTextNode(dependenciSig.getId().toString()));
                dependente.appendChild(idDependente);

                Element nome = doc.createElement("nome");
                nome.appendChild(doc.createTextNode(dependenciSig.getDependente().getNome()));
                dependente.appendChild(nome);

                if(dependenciSig.getDependente().getDataNascimento() != null) {
                    Element dtNascto = doc.createElement("dtNascto");
                    dtNascto.appendChild(doc.createTextNode(converterDataString("yyyy-MM-dd", dependenciSig.getDependente().getDataNascimento())));
                    dependente.appendChild(dtNascto);
                }

                if(dependenciSig.getDependente().getCpf() != null) {
                    Element cpf = doc.createElement("cpf");
                    cpf.appendChild(doc.createTextNode(StringUtil.removeCaracteresEspeciaisSemEspaco(dependenciSig.getDependente().getCpf())));
                    dependente.appendChild(cpf);
                }

                if(dependenciSig.getDependente().getMae() != null) {
                    Element nomeMae = doc.createElement("nomeMae");
                    nomeMae.appendChild(doc.createTextNode(dependenciSig.getDependente().getMae()));
                    dependente.appendChild(nomeMae);
                }

                if(dependenciSig.getDependente().getSexo() != null) {
                    Element sexo = doc.createElement("sexo");
                    sexo.appendChild(doc.createTextNode(dependenciSig.getDependente().getSexo().getSigla()));
                    dependente.appendChild(sexo);
                }

                if(dependenciSig.getDependente().getCarteiraDeTrabalho() != null) {
                    Element pasep = doc.createElement("pasep");
                    pasep.appendChild(doc.createTextNode(dependenciSig.getDependente().getCarteiraDeTrabalho().getPisPasep()));
                    dependente.appendChild(pasep);
                }

                if(dependenciSig.getDependente().getEstadoCivil() != null) {
                    Element estadoCivil = doc.createElement("estadoCivil");
                    estadoCivil.appendChild(doc.createTextNode(dependenciSig.getDependente().getEstadoCivil().getCodigoSiprev()));
                    dependente.appendChild(estadoCivil);
                }

            }
        }
        assistenteBarraProgresso.conta();


        //pensionista --------------------------------------------------------------------------

        Integer resumoqtPensionista = 0;

        List<Pensionista> listaPensionista = pensionistaFacade.recuperaPensionistasVigente(dateTime.toDate());
        for (Pensionista pensi : listaPensionista) {
            resumoqtPensionista += 1;
            Element pensionista = criarTag(doc, "pensionista", arquivo);

            Element idPensionista = doc.createElement("idPensionista");
            idPensionista.appendChild(doc.createTextNode(pensi.getId().toString()));
            pensionista.appendChild(idPensionista);

            Element nome = doc.createElement("nome");
            nome.appendChild(doc.createTextNode(pensi.getMatriculaFP().getPessoa().getNome()));
            pensionista.appendChild(nome);

            Element matricula = doc.createElement("matricula");
            matricula.appendChild(doc.createTextNode(pensi.getMatriculaFP().getMatricula()));
            pensionista.appendChild(matricula);

            if(pensi.getMatriculaFP().getPessoa().getDataNascimento() != null) {
                Element dtNascto = doc.createElement("dtNascto");
                dtNascto.appendChild(doc.createTextNode(converterDataString("yyyy-MM-dd", (pensi.getMatriculaFP().getPessoa().getDataNascimento()))));
                pensionista.appendChild(dtNascto);
            }

            Element cpf = doc.createElement("cpf");
            cpf.appendChild(doc.createTextNode(StringUtil.removeCaracteresEspeciaisSemEspaco(pensi.getMatriculaFP().getPessoa().getCpf())));
            pensionista.appendChild(cpf);

            Element nomeMae = doc.createElement("nomeMae");
            nomeMae.appendChild(doc.createTextNode(pensi.getMatriculaFP().getPessoa().getMae()));
            pensionista.appendChild(nomeMae);

            Element sexo = doc.createElement("sexo");
            sexo.appendChild(doc.createTextNode(pensi.getMatriculaFP().getPessoa().getSexo().getSigla()));
            pensionista.appendChild(sexo);

            if( pensi.getMatriculaFP().getPessoa().getCarteiraDeTrabalho() != null) {
                Element pasep = doc.createElement("pasep");
                pasep.appendChild(doc.createTextNode(pensi.getMatriculaFP().getPessoa().getCarteiraDeTrabalho().getPisPasep()));
                pensionista.appendChild(pasep);
            }

            if(pensi.getMatriculaFP().getPessoa().getEstadoCivil() != null) {
                Element estadoCivil = doc.createElement("estadoCivil");
                estadoCivil.appendChild(doc.createTextNode(pensi.getMatriculaFP().getPessoa().getEstadoCivil().getCodigoSiprev()));
                pensionista.appendChild(estadoCivil);
            }
        }
        assistenteBarraProgresso.conta();

        //dependencia ---------------------------------------------------------------------------

        for (VinculoFP vinculo : vinculos) {
            List<Dependente> dependentes = dependenteFacade.dependentesPorResponsavelNative(vinculo.getMatriculaFP().getPessoa());
            for (Dependente dependenciSig : dependentes) {
                DependenteVinculoFP dependenciaVigente = dependenciSig.getDependentesVinculosFPsVigente(dateTime.toDate());
                List<BeneficioPensaoAlimenticia> beneficioPensaoAlimenticias = pensaoAlimenticiaFacade.buscarBeneficioPensaoAlimenticiaVigentePorPessoa(dependenciSig.getDependente(), dateTime.toDate());

                String campoTipoDependencia = "";
                if(dependenciSig.getGrauDeParentesco() != null && dependenciSig.getGrauDeParentesco().getTipoParentesco() != null) {
                    campoTipoDependencia = getTipoDependencia(dependenciSig);
                }

                boolean dependentePA= !beneficioPensaoAlimenticias.isEmpty() ? true : false;

                Element dependencia = criarTag(doc, "dependencia", arquivo);

                if(dependenciaVigente != null) {
                    Element idDependencia = doc.createElement("idDependencia");
                    idDependencia.appendChild(doc.createTextNode(dependenciaVigente.getId().toString()));
                    dependencia.appendChild(idDependencia);
                }

                Element idDependente = doc.createElement("idDependente");
                idDependente.appendChild(doc.createTextNode(dependenciSig.getId().toString()));
                dependencia.appendChild(idDependente);

                Element idServidor = doc.createElement("idServidor");
                idServidor.appendChild(doc.createTextNode(vinculo.getId().toString()));
                dependencia.appendChild(idServidor);

                if(dependenciaVigente != null) {
                    Element dtIniPensao = doc.createElement("dtIniPensao");
                    dtIniPensao.appendChild(doc.createTextNode(converterDataString("yyyy-MM-dd", dependenciaVigente.getInicioVigencia())));
                    dependencia.appendChild(dtIniPensao);
                }

                if(dependenciaVigente != null && dependenciaVigente.getFinalVigencia() != null) {
                    Element dtFimPrevisto = doc.createElement("dtFimPrevisto");
                    dtFimPrevisto.appendChild(doc.createTextNode(converterDataString("yyyy-MM-dd", dependenciaVigente.getFinalVigencia())));
                    dependencia.appendChild(dtFimPrevisto);
                }

                if(!isStringNulaOuVazia(campoTipoDependencia)) {
                    Element tipoDependencia = doc.createElement("tipoDependencia");
                    tipoDependencia.appendChild(doc.createTextNode(campoTipoDependencia));
                    dependencia.appendChild(tipoDependencia);
                }

                if( campoTipoDependencia == "99") {
                    Element outroTipoDependencia = doc.createElement("outroTipoDependencia");
                    outroTipoDependencia.appendChild(doc.createTextNode(dependenciSig.getGrauDeParentesco().getDescricao()));
                    dependencia.appendChild(outroTipoDependencia);
                }

                if(!dependentePA) {
                    Element dtInicioDependencia = doc.createElement("dtInicioDependencia");
                    dtInicioDependencia.appendChild(doc.createTextNode(converterDataString("yyyy-MM-dd", vinculo.getInicioVigencia())));
                    dependencia.appendChild(dtInicioDependencia);
                }

                if(vinculo.getFinalVigencia() != null && !dependentePA) {
                    Element dtFimDependencia = doc.createElement("dtFimDependencia");
                    dtFimDependencia.appendChild(doc.createTextNode(converterDataString("yyyy-MM-dd", vinculo.getFinalVigencia())));
                    dependencia.appendChild(dtFimDependencia);
                }

                Element pensionista = doc.createElement("pensionista");
                pensionista.appendChild(doc.createTextNode(dependentePA ? "S" : "N"));
                dependencia.appendChild(pensionista);
            }
        }
        assistenteBarraProgresso.conta();

        //beneficioPensionista

        Integer resumoqtBeneficioPensionista = 0;
        for (Pensionista pensi : listaPensionista) {
            resumoqtBeneficioPensionista += 1;
            BigDecimal salarioInicial = pensionistaFacade.buscarValorPensionistaPerPensionista(pensi, pensi.getInicioVigencia());
            BigDecimal salarioFinal = pensionistaFacade.buscarValorPensionistaPerPensionista(pensi, dateTime.toDate());

            String campoTipoPensaoMorte = "";
            if (TipoFundamentacao.ART_40_CF_I.equals(pensi.getTipoFundamentacao())) {
                campoTipoPensaoMorte = "1";
            } else if (TipoFundamentacao.ART_40_CF_II.equals(pensi.getTipoFundamentacao())) {
                campoTipoPensaoMorte = "2";
            }
            Element beneficioPensionista = criarTag(doc, "beneficioPensionista", arquivo);

            Element idBeneficio = doc.createElement("idBeneficio");
            idBeneficio.appendChild(doc.createTextNode(pensi.getPensao().getId().toString()));
            beneficioPensionista.appendChild(idBeneficio);

            Element idOrgao = doc.createElement("idOrgao");
            idOrgao.appendChild(doc.createTextNode(pensi.getPensao().getContratoFP().getUnidadeOrganizacional().getId().toString()));
            beneficioPensionista.appendChild(idOrgao);

            Element tipoBeneficio = doc.createElement("tipoBeneficio");
            tipoBeneficio.appendChild(doc.createTextNode("12"));
            beneficioPensionista.appendChild(tipoBeneficio);

            Element dtInicioBeneficio = doc.createElement("dtInicioBeneficio");
            dtInicioBeneficio.appendChild(doc.createTextNode(converterDataString("yyyy-MM-dd", pensi.getInicioVigencia())));
            beneficioPensionista.appendChild(dtInicioBeneficio);

            if(pensi.getFinalVigencia() != null) {
                Element dtFimBeneficio = doc.createElement("dtFimBeneficio");
                dtFimBeneficio.appendChild(doc.createTextNode(converterDataString("yyyy-MM-dd", pensi.getFinalVigencia())));
                beneficioPensionista.appendChild(dtFimBeneficio);
            }

            Element vlAtualBeneficio = doc.createElement("vlAtualBeneficio");
            vlAtualBeneficio.appendChild(doc.createTextNode(getValorSemPontoEVirgulas(salarioFinal)));
            beneficioPensionista.appendChild(vlAtualBeneficio);

            Element vlInicialBeneficio = doc.createElement("vlInicialBeneficio");
            vlInicialBeneficio.appendChild(doc.createTextNode(getValorSemPontoEVirgulas(salarioInicial)));
            beneficioPensionista.appendChild(vlInicialBeneficio);

            if(pensi.getDataAlteracao() != null) {
                Element dtUltimaAtualizacao = doc.createElement("dtUltimaAtualizacao");
                dtUltimaAtualizacao.appendChild(doc.createTextNode(converterDataString("yyyy-MM-dd", pensi.getDataAlteracao())));
                beneficioPensionista.appendChild(dtUltimaAtualizacao);
            }

            if(pensi.getProvimentoFP() != null && pensi.getProvimentoFP().getVinculoFP() != null) {
                Element idServidorInstituidor = doc.createElement("idServidorInstituidor");
                idServidorInstituidor.appendChild(doc.createTextNode(pensi.getProvimentoFP().getVinculoFP().getId().toString()));
                beneficioPensionista.appendChild(idServidorInstituidor);
            }

            Element regimeServidorInstituidor = doc.createElement("regimeServidorInstituidor");
            regimeServidorInstituidor.appendChild(doc.createTextNode(pensi.getPensao().getContratoFP().getPrevidenciaVinculoFPVigente().getTipoPrevidenciaFP().getCodigo() == 1 ? "5" : "1"));
            beneficioPensionista.appendChild(regimeServidorInstituidor);

            if(!isStringNulaOuVazia(pensi.getPensao().getContratoFP().getCargo().getDescricaoCarreira())) {
                Element carreira = doc.createElement("carreira");
                carreira.appendChild(doc.createTextNode(pensi.getPensao().getContratoFP().getCargo().getDescricaoCarreira()));
                beneficioPensionista.appendChild(carreira);
            }

            Element cargo = doc.createElement("cargo");
            cargo.appendChild(doc.createTextNode(StringUtil.removeEspacos(pensi.getPensao().getContratoFP().getCargo().getDescricao())));
            beneficioPensionista.appendChild(cargo);

            if(!isStringNulaOuVazia(campoTipoPensaoMorte)) {
                Element tipoPensaoMorte = doc.createElement("tipoPensaoMorte");
                tipoPensaoMorte.appendChild(doc.createTextNode(StringUtil.removeEspacos(pensi.getPensao().getContratoFP().getCargo().getDescricao())));
                beneficioPensionista.appendChild(tipoPensaoMorte);
            }
        }
        assistenteBarraProgresso.conta();


        //resumo

        Element resumo = criarTag(doc, "resumo", arquivo);

        Element qtEndereco = doc.createElement("qtEndereco");
        qtEndereco.appendChild(doc.createTextNode("1"));
        resumo.appendChild(qtEndereco);

        Element qtMaioridade = doc.createElement("qtMaioridade");
        qtMaioridade.appendChild(doc.createTextNode("0"));
        resumo.appendChild(qtMaioridade);

        Element qtChefe = doc.createElement("qtChefe");
        qtChefe.appendChild(doc.createTextNode("1"));
        resumo.appendChild(qtChefe);

        Element qtAliquota = doc.createElement("qtAliquota");
        qtAliquota.appendChild(doc.createTextNode("0"));
        resumo.appendChild(qtAliquota);

        Element qtContribuicaoEnte = doc.createElement("qtContribuicaoEnte");
        qtContribuicaoEnte.appendChild(doc.createTextNode("0"));
        resumo.appendChild(qtContribuicaoEnte);

        Element qtLimiteRemuneratorio = doc.createElement("qtLimiteRemuneratorio");
        qtLimiteRemuneratorio.appendChild(doc.createTextNode("0"));
        resumo.appendChild(qtLimiteRemuneratorio);

        Element qtOrgao = doc.createElement("qtOrgao");
        qtOrgao.appendChild(doc.createTextNode(resumoqtOrgao.toString()));
        resumo.appendChild(qtOrgao);

        Element qtServidor = doc.createElement("qtServidor");
        qtServidor.appendChild(doc.createTextNode(resumoqtServidor.toString()));
        resumo.appendChild(qtServidor);

        Element qtVinculoFuncional = doc.createElement("qtVinculoFuncional");
        qtVinculoFuncional.appendChild(doc.createTextNode(resumoqtVinculoFuncional.toString()));
        resumo.appendChild(qtVinculoFuncional);

        Element qtMovimentacaoFuncional = doc.createElement("qtMovimentacaoFuncional");
        qtMovimentacaoFuncional.appendChild(doc.createTextNode(resumoqtMovimentacaoFuncional.toString()));
        resumo.appendChild(qtMovimentacaoFuncional);

        Element qtTempoContribuicaoRGPS = doc.createElement("qtTempoContribuicaoRGPS");
        qtTempoContribuicaoRGPS.appendChild(doc.createTextNode("0"));
        resumo.appendChild(qtTempoContribuicaoRGPS);

        Element qtTempoContribuicaoRPPS = doc.createElement("qtTempoContribuicaoRPPS");
        qtTempoContribuicaoRPPS.appendChild(doc.createTextNode("0"));
        resumo.appendChild(qtTempoContribuicaoRPPS);

        Element qtTempoSemContribuicao = doc.createElement("qtTempoSemContribuicao");
        qtTempoSemContribuicao.appendChild(doc.createTextNode("0"));
        resumo.appendChild(qtTempoSemContribuicao);

        Element qtTempoFicticio = doc.createElement("qtTempoFicticio");
        qtTempoFicticio.appendChild(doc.createTextNode("0"));
        resumo.appendChild(qtTempoFicticio);

        Element qtHistoricoFinanceiro = doc.createElement("qtHistoricoFinanceiro");
        qtHistoricoFinanceiro.appendChild(doc.createTextNode(resumoqtHistoricoFinanceiro.toString()));
        resumo.appendChild(qtHistoricoFinanceiro);

        Element qtBeneficioServidor = doc.createElement("qtBeneficioServidor");
        qtBeneficioServidor.appendChild(doc.createTextNode(resumoqtBeneficioServidor.toString()));
        resumo.appendChild(qtBeneficioServidor);

        Element qtDependente = doc.createElement("qtDependente");
        qtDependente.appendChild(doc.createTextNode(resumoqtDependente.toString()));
        resumo.appendChild(qtDependente);

        Element qtPensionista = doc.createElement("qtPensionista");
        qtPensionista.appendChild(doc.createTextNode(resumoqtPensionista.toString()));
        resumo.appendChild(qtPensionista);

        Element qtBeneficioPensionista = doc.createElement("qtBeneficioPensionista");
        qtBeneficioPensionista.appendChild(doc.createTextNode(resumoqtBeneficioPensionista.toString()));
        resumo.appendChild(qtBeneficioPensionista);

        Element qtQuotaPensionista = doc.createElement("qtQuotaPensionista");
        qtQuotaPensionista.appendChild(doc.createTextNode("0"));
        resumo.appendChild(qtQuotaPensionista);

        Element qtfuncaoGratificada = doc.createElement("qtfuncaoGratificada");
        qtfuncaoGratificada.appendChild(doc.createTextNode("0"));
        resumo.appendChild(qtfuncaoGratificada);

        assistenteBarraProgresso.conta();


        // fim alteração -----------------------------------------------------------------------
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "iso-8859-1");
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
        if(selecionado.getEnteFederativo().getEmail() != null) {
            endereco.setAttributeNode(criarAtributo(doc, "emailEnte", selecionado.getEnteFederativo().getEmail()));
        }
        if(!isStringNulaOuVazia(tipoLogradouro)) {
            endereco.setAttributeNode(criarAtributo(doc, "tipoLogradouro", tipoLogradouro));
        }
        if(selecionado.getEnteFederativo().getEnderecoPrincipal() != null) {
            endereco.setAttributeNode(criarAtributo(doc, "logradouro", selecionado.getEnteFederativo().getEnderecoPrincipal().getLogradouro()));
            endereco.setAttributeNode(criarAtributo(doc, "numLogradouro", selecionado.getEnteFederativo().getEnderecoPrincipal().getNumero()));
            endereco.setAttributeNode(criarAtributo(doc, "complemento",  selecionado.getEnteFederativo().getEnderecoPrincipal().getComplemento()));
            endereco.setAttributeNode(criarAtributo(doc, "bairro",  selecionado.getEnteFederativo().getEnderecoPrincipal().getBairro()));
            if (cidade!= null) {
                endereco.setAttributeNode(criarAtributo(doc, "municipio", cidade.getCodigoIBGE().toString()));
            }
            endereco.setAttributeNode(criarAtributo(doc, "uf", selecionado.getEnteFederativo().getEnderecoPrincipal().getUf()));
            endereco.setAttributeNode(criarAtributo(doc, "cep",  selecionado.getEnteFederativo().getEnderecoPrincipal().getCep()));
        }
    }

    private void preencherTagChefe(Document doc, Element chefe, RepresentanteLegalPessoa rep) {
        chefe.setAttributeNode(criarAtributo(doc, "idChefe", rep.getId().toString()));
        chefe.setAttributeNode(criarAtributo(doc, "nome", rep.getRepresentante().getNome()));
        chefe.setAttributeNode(criarAtributo(doc, "cpf", StringUtil.removeCaracteresEspeciaisSemEspaco(rep.getRepresentante().getCpf_Cnpj())));
        chefe.setAttributeNode(criarAtributo(doc, "dtInicioMand", converterDataString("yyyy-MM-dd", rep.getDataInicio())));
        if(rep.getDataFim() != null) {
            chefe.setAttributeNode(criarAtributo(doc, "dtFimMand", converterDataString("yyyy-MM-dd", rep.getDataFim())));
        }
        chefe.setAttributeNode(criarAtributo(doc, "email", rep.getRepresentante().getEmail()));
    }

    private void preencherTagOrgao(Document doc, Element orgao, HierarquiaOrganizacional ho, String uniGestora, PessoaJuridica pj) {
        orgao.setAttributeNode(criarAtributo(doc, "idOrgao", ho.getSubordinada().getId().toString()));
        orgao.setAttributeNode(criarAtributo(doc, "nomeOrgao", ho.getDescricao()));
        orgao.setAttributeNode(criarAtributo(doc, "cnpjOrgao", StringUtil.removeCaracteresEspeciaisSemEspaco(pj.getCnpj())));
        if(ho.getSubordinada().getEntidade() != null) {
            orgao.setAttributeNode(criarAtributo(doc, "poder",ho.getSubordinada().getEntidade().getEsferaDoPoder().getCodigoSiprev()));
        }
        orgao.setAttributeNode(criarAtributo(doc, "unidadeGestora", uniGestora));

    }

    private void preencherTagServidor(Document doc, Element servidor, VinculoFP vinculo, PessoaFisica pf, RegistroDeObito registroDeObito) {
        servidor.setAttributeNode(criarAtributo(doc, "idServidor", vinculo.getId().toString()));
        if (pf.getCarteiraDeTrabalho() != null) {
            servidor.setAttributeNode(criarAtributo(doc, "pasep", StringUtil.removeCaracteresEspeciaisSemEspaco(pf.getCarteiraDeTrabalho().getPisPasep())));
        }
        servidor.setAttributeNode(criarAtributo(doc, "nome", pf.getNome()));
        servidor.setAttributeNode(criarAtributo(doc, "cpf", StringUtil.removeCaracteresEspeciaisSemEspaco(pf.getCpf())));
        servidor.setAttributeNode(criarAtributo(doc, "dtNascto", DataUtil.getDataFormatada(pf.getDataNascimento(), "yyyy-MM-dd")));
        servidor.setAttributeNode(criarAtributo(doc, "sexo", pf.getSexo().getSigla()));
        if(registroDeObito != null) {
            servidor.setAttributeNode(criarAtributo(doc, "dtObito", converterDataString("yyyy-MM-dd", registroDeObito.getDataFalecimento())));
        }
        servidor.setAttributeNode(criarAtributo(doc, "nomeMae", pf.getMae()));
        if(pf.getEstadoCivil() != null) {
            servidor.setAttributeNode(criarAtributo(doc, "estadoCivil", pf.getEstadoCivil().getCodigoSiprev()));
        }
//        servidor.setAttributeNode(criarAtributo(doc, "dtRecenseamento", ""));
        servidor.setAttributeNode(criarAtributo(doc, "dtIngressoServPublico", converterDataString("yyyy-MM-dd", vinculo.getInicioVigencia())));
    }

    private void preencherTagVinculoFuncional(Document doc, Element vinculoFuncional, ContratoFP contrato, String tipoVinculo, BigDecimal sitPrevid) {
        vinculoFuncional.setAttributeNode(criarAtributo(doc, "idVinculoFuncional", contrato.getModalidadeContratoFP().getId().toString()));
        vinculoFuncional.setAttributeNode(criarAtributo(doc, "idServidor", contrato.getId().toString()));
        if(!isStringNulaOuVazia(tipoVinculo)) {
            vinculoFuncional.setAttributeNode(criarAtributo(doc, "tipoVinculo", tipoVinculo));
        }
        vinculoFuncional.setAttributeNode(criarAtributo(doc, "idOrgao", contrato.getUnidadeOrganizacional().getId().toString()));
        vinculoFuncional.setAttributeNode(criarAtributo(doc, "cargo", StringUtil.removeEspacos(contrato.getCargo().getDescricao())));
        if(contrato.getCargo().getInicioVigencia() != null) {
            vinculoFuncional.setAttributeNode(criarAtributo(doc, "dtIniCargo", converterDataString("yyyy-MM-dd", contrato.getCargo().getInicioVigencia())));
        }
        vinculoFuncional.setAttributeNode(criarAtributo(doc, "matricula", StringUtil.removeCaracteresEspeciaisSemEspaco(contrato.getMatriculaFP().getMatricula())));
        if(!isStringNulaOuVazia(contrato.getCargo().getDescricaoCarreira())) {
            vinculoFuncional.setAttributeNode(criarAtributo(doc, "carreira", contrato.getCargo().getDescricaoCarreira()));
        }
        vinculoFuncional.setAttributeNode(criarAtributo(doc, "regime", contrato.getPrevidenciaVinculoFPVigente().getTipoPrevidenciaFP().getCodigo() == 1 ? "5" : "1"));
        vinculoFuncional.setAttributeNode(criarAtributo(doc, "sitFuncional", "1"));
        if(sitPrevid != null) {
            vinculoFuncional.setAttributeNode(criarAtributo(doc, "sitPrevid", sitPrevid.toString()));
        }
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
        movimentacaoFuncional.setAttributeNode(criarAtributo(doc, "idVinculoFuncional",  exoneracao.getVinculoFP().getContratoFP().getModalidadeContratoFP().getId().toString()));
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
        if(valorFinanceiroRH != null) {
            historicoFinanceiro.setAttributeNode(criarAtributo(doc, "vlRemContributiva", getValorSemPontoEVirgulas(valorFinanceiroRH.getTotalBase())));
        }
        historicoFinanceiro.setAttributeNode(criarAtributo(doc, "vlRemCargoEfetivo", vlRemCargoEfetivo));
    }

    private void preencherTagBeneficioServidor(Document doc, Element beneficioServidor, VinculoFP vinculo, Aposentadoria aposentadoria,
                                              String paridade, BigDecimal salarioInicial, BigDecimal salarioAtual, String tipoBeneficio) {
        beneficioServidor.setAttributeNode(criarAtributo(doc, "idBeneficio", aposentadoria.getId().toString()));
        beneficioServidor.setAttributeNode(criarAtributo(doc, "idServidor", vinculo.getId().toString()));
        beneficioServidor.setAttributeNode(criarAtributo(doc, "idOrgao", vinculo.getUnidadeOrganizacional().getId().toString()));
        if(aposentadoria.getDataRegistro() != null) {
            beneficioServidor.setAttributeNode(criarAtributo(doc, "dtInicioBeneficio", converterDataString("yyyy-MM-dd", aposentadoria.getDataRegistro())));
        }
        beneficioServidor.setAttributeNode(criarAtributo(doc, "tipoBeneficio", tipoBeneficio));
        if(aposentadoria.getFinalVigencia() != null) {
            beneficioServidor.setAttributeNode(criarAtributo(doc, "dtFimBeneficio", converterDataString("yyyy-MM-dd", aposentadoria.getFinalVigencia())));
        }
        beneficioServidor.setAttributeNode(criarAtributo(doc, "vlAtualBeneficio", getValorSemPontoEVirgulas(salarioAtual)));
        if(salarioInicial != null) {
            beneficioServidor.setAttributeNode(criarAtributo(doc, "vlInicialBeneficio", getValorSemPontoEVirgulas(salarioInicial)));
        }
        if(aposentadoria.getDataAlteracao() != null) {
            beneficioServidor.setAttributeNode(criarAtributo(doc, "dtUltimaAtualizacao", converterDataString("yyyy-MM-dd", aposentadoria.getDataAlteracao())));
        }
        if(aposentadoria.getDataPublicacao() != null) {
            beneficioServidor.setAttributeNode(criarAtributo(doc, "dtPublicacao", converterDataString("yyyy-MM-dd", aposentadoria.getDataPublicacao())));
        }
        beneficioServidor.setAttributeNode(criarAtributo(doc, "paridade", paridade));
        beneficioServidor.setAttributeNode(criarAtributo(doc, "regime", vinculo.getContratoFP().getPrevidenciaVinculoFPVigente().getTipoPrevidenciaFP().getCodigo() == 1 ? "5" : "1"));
        if(aposentadoria.getTipoAposentadoriaEspecialSIG() != null) {
            beneficioServidor.setAttributeNode(criarAtributo(doc, "tipoAposentadoriaEspecial", aposentadoria.getTipoAposentadoriaEspecialSIG().getCodigo().toString()));
        }
    }

    private void preencherTagDependente(Document doc, Element dependente, Dependente dependenciSig) {
        dependente.setAttributeNode(criarAtributo(doc, "idDependente", dependenciSig.getId().toString()));
        dependente.setAttributeNode(criarAtributo(doc, "nome", dependenciSig.getDependente().getNome()));
        if(dependenciSig.getDependente().getDataNascimento() != null) {
            dependente.setAttributeNode(criarAtributo(doc, "dtNascto", converterDataString("yyyy-MM-dd", dependenciSig.getDependente().getDataNascimento())));
        }
        if(dependenciSig.getDependente().getCpf() != null) {
            dependente.setAttributeNode(criarAtributo(doc, "cpf", StringUtil.removeCaracteresEspeciaisSemEspaco(dependenciSig.getDependente().getCpf())));
        }
        if(dependenciSig.getDependente().getMae() != null) {
            dependente.setAttributeNode(criarAtributo(doc, "nomeMae", dependenciSig.getDependente().getMae()));
        }
        if(dependenciSig.getDependente().getSexo() != null) {
            dependente.setAttributeNode(criarAtributo(doc, "sexo", dependenciSig.getDependente().getSexo().getSigla()));
        }
        if(dependenciSig.getDependente().getCarteiraDeTrabalho() != null) {
            dependente.setAttributeNode(criarAtributo(doc, "pasep", dependenciSig.getDependente().getCarteiraDeTrabalho().getPisPasep()));
        }
        if(dependenciSig.getDependente().getEstadoCivil() != null) {
            dependente.setAttributeNode(criarAtributo(doc, "estadoCivil", dependenciSig.getDependente().getEstadoCivil().getCodigoSiprev()));
        }
    }

    private void preencherTagPensionista(Document doc, Element pensionista, Pensionista pensi) {
        pensionista.setAttributeNode(criarAtributo(doc, "idPensionista", pensi.getId().toString()));
        pensionista.setAttributeNode(criarAtributo(doc, "nome", pensi.getMatriculaFP().getPessoa().getNome()));
        pensionista.setAttributeNode(criarAtributo(doc, "matricula", pensi.getMatriculaFP().getMatricula()));
        if(pensi.getMatriculaFP().getPessoa().getDataNascimento() != null) {
            pensionista.setAttributeNode(criarAtributo(doc, "dtNascto", converterDataString("yyyy-MM-dd", (pensi.getMatriculaFP().getPessoa().getDataNascimento()))));
        }
        pensionista.setAttributeNode(criarAtributo(doc, "cpf", StringUtil.removeCaracteresEspeciaisSemEspaco(pensi.getMatriculaFP().getPessoa().getCpf())));
        pensionista.setAttributeNode(criarAtributo(doc, "nomeMae", pensi.getMatriculaFP().getPessoa().getMae()));
        pensionista.setAttributeNode(criarAtributo(doc, "sexo", pensi.getMatriculaFP().getPessoa().getSexo().getSigla()));
        if( pensi.getMatriculaFP().getPessoa().getCarteiraDeTrabalho() != null) {
            pensionista.setAttributeNode(criarAtributo(doc, "pasep", pensi.getMatriculaFP().getPessoa().getCarteiraDeTrabalho().getPisPasep()));
        }
        if(pensi.getMatriculaFP().getPessoa().getEstadoCivil() != null) {
            pensionista.setAttributeNode(criarAtributo(doc, "estadoCivil", pensi.getMatriculaFP().getPessoa().getEstadoCivil().getCodigoSiprev()));
        }
    }

    private void preencherTagDependencia(Document doc, Element dependencia, Dependente dependenciSig, VinculoFP vinculo, DependenteVinculoFP dependenciaVigente,
                                        String tipoDependencia, boolean dependentePA) {
        if(dependenciaVigente != null) {
            dependencia.setAttributeNode(criarAtributo(doc, "idDependencia", dependenciaVigente.getId().toString()));
        }
        dependencia.setAttributeNode(criarAtributo(doc, "idDependente", dependenciSig.getId().toString()));
        dependencia.setAttributeNode(criarAtributo(doc, "idServidor", vinculo.getId().toString()));
        if(dependenciaVigente != null) {
            dependencia.setAttributeNode(criarAtributo(doc, "dtIniPensao", converterDataString("yyyy-MM-dd", dependenciaVigente.getInicioVigencia())));
        }
        if(dependenciaVigente != null && dependenciaVigente.getFinalVigencia() != null) {
            dependencia.setAttributeNode(criarAtributo(doc, "dtFimPrevisto", converterDataString("yyyy-MM-dd", dependenciaVigente.getFinalVigencia())));
        }
        if(!isStringNulaOuVazia(tipoDependencia)) {
            dependencia.setAttributeNode(criarAtributo(doc, "tipoDependencia", tipoDependencia));
        }
        if( tipoDependencia == "99") {
            dependencia.setAttributeNode(criarAtributo(doc, "outroTipoDependencia", dependenciSig.getGrauDeParentesco().getDescricao()));
        }
//        dependencia.setAttributeNode(criarAtributo(doc, "motInicioDep", ""));
//        dependencia.setAttributeNode(criarAtributo(doc, "outroMotInicioDep", ""));
//        dependencia.setAttributeNode(criarAtributo(doc, "motFimDep", ""));
//        dependencia.setAttributeNode(criarAtributo(doc, "outroMotFimDep", ""));

        if(!dependentePA) {
            dependencia.setAttributeNode(criarAtributo(doc, "dtInicioDependencia", converterDataString("yyyy-MM-dd", vinculo.getInicioVigencia())));
        }
        if(vinculo.getFinalVigencia() != null && !dependentePA) {
            dependencia.setAttributeNode(criarAtributo(doc, "dtFimDependencia", converterDataString("yyyy-MM-dd", vinculo.getFinalVigencia())));
        }
//        dependencia.setAttributeNode(criarAtributo(doc, "motInicioPensao", ""));
//        dependencia.setAttributeNode(criarAtributo(doc, "motFimPensao", ""));
//        dependencia.setAttributeNode(criarAtributo(doc, "justificativaMotivoInicioPensao", ""));
//        dependencia.setAttributeNode(criarAtributo(doc, "justificativaMotivoFimPensao", ""));
        dependencia.setAttributeNode(criarAtributo(doc, "pensionista", dependentePA ? "S" : "N"));
    }

    private void preencherTagBeneficioPensionista(Document doc, Element beneficioPensionista, Pensionista pensi, String tipoPensaoMorte, BigDecimal salarioInicial, BigDecimal salarioFinal) {
        beneficioPensionista.setAttributeNode(criarAtributo(doc, "idBeneficio", pensi.getPensao().getId().toString()));
        if(pensi.getFolha() != null) {
            beneficioPensionista.setAttributeNode(criarAtributo(doc, "idOrgao", pensi.getFolha().getUnidadeOrganizacional().getId().toString()));
        }
        beneficioPensionista.setAttributeNode(criarAtributo(doc, "tipoBeneficio", "12"));
        beneficioPensionista.setAttributeNode(criarAtributo(doc, "dtInicioBeneficio", converterDataString("yyyy-MM-dd", pensi.getInicioVigencia())));
        if(pensi.getFinalVigencia() != null) {
            beneficioPensionista.setAttributeNode(criarAtributo(doc, "dtFimBeneficio", converterDataString("yyyy-MM-dd", pensi.getFinalVigencia())));
        }
        beneficioPensionista.setAttributeNode(criarAtributo(doc, "vlAtualBeneficio", getValorSemPontoEVirgulas(salarioFinal)));
        if(salarioFinal != null) {
            beneficioPensionista.setAttributeNode(criarAtributo(doc, "vlInicialBeneficio", getValorSemPontoEVirgulas(salarioInicial)));
        }
        if(pensi.getDataAlteracao() != null) {
            beneficioPensionista.setAttributeNode(criarAtributo(doc, "dtUltimaAtualizacao", converterDataString("yyyy-MM-dd", pensi.getDataAlteracao())));
        }
        if(pensi.getProvimentoFP() != null && pensi.getProvimentoFP().getVinculoFP() != null) {
            beneficioPensionista.setAttributeNode(criarAtributo(doc, "idServidorInstituidor", pensi.getProvimentoFP().getVinculoFP().getId().toString()));
        }
        beneficioPensionista.setAttributeNode(criarAtributo(doc, "regimeServidorInstituidor", pensi.getPensao().getContratoFP().getPrevidenciaVinculoFPVigente().getTipoPrevidenciaFP().getCodigo() == 1 ? "5" : "1"));
        if(!isStringNulaOuVazia(pensi.getPensao().getContratoFP().getCargo().getDescricaoCarreira())) {
            beneficioPensionista.setAttributeNode(criarAtributo(doc, "carreira", pensi.getPensao().getContratoFP().getCargo().getDescricaoCarreira()));
        }
        beneficioPensionista.setAttributeNode(criarAtributo(doc, "cargo", StringUtil.removeEspacos(pensi.getPensao().getContratoFP().getCargo().getDescricao())));
//        beneficioPensionista.setAttributeNode(criarAtributo(doc, "motivoInicioPensao", ""));
//        beneficioPensionista.setAttributeNode(criarAtributo(doc, "motivoFimPensao", ""));
        if(!isStringNulaOuVazia(tipoPensaoMorte)) {
            beneficioPensionista.setAttributeNode(criarAtributo(doc, "tipoPensaoMorte", tipoPensaoMorte));
        }
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
            "where apo.REGRAAPOSENTADORIA = :regraaposentadoria " +
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
        q.setParameter("regraaposentadoria", RegraAposentadoria.VOLUNTARIA_INTEGRAL_COMNUM_ART_2005.name());


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
            "                 where apo.REGRAAPOSENTADORIA = :regraaposentadoria " +
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
        q.setParameter("regraaposentadoria", RegraAposentadoria.VOLUNTARIA_INTEGRAL_COMNUM_ART_2005.name());
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

