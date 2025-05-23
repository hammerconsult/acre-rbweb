package br.com.webpublico.negocios.rh.arquivos;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.arquivos.EstudoAtuarial;
import br.com.webpublico.entidades.rh.cadastrofuncional.TempoContratoFPPessoa;
import br.com.webpublico.entidadesauxiliares.AuxiliarAndamentoArquivoAtuarial;
import br.com.webpublico.enums.*;
import br.com.webpublico.enums.rh.estudoatuarial.TipoDependenciaEstudoAtuarial;
import br.com.webpublico.enums.rh.estudoatuarial.TipoEspecificacaoCargo;
import br.com.webpublico.enums.rh.estudoatuarial.TipoRegimePrevidenciarioEstudoAtuarial;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.springframework.util.CollectionUtils;

import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static br.com.webpublico.enums.rh.estudoatuarial.TipoDependenciaEstudoAtuarial.getByCodigo;

/**
 * Created by Buzatto on 18/05/2016.
 */
@Stateless
public class EstudoAtuarialFacade extends AbstractFacade<EstudoAtuarial> {

    public static final String CODIGO_ABONO_PERMANENCIA = "441";
    public static final String CODIGO_RPPS = "898";
    private static final String GRUPO_FINANCEIRO = "F";
    public static final String CONTRIBUICAO_SINDICAL = "676";
    public static final String BASE_RPPS = "1001";
    public static final String BASE_INSS = "1002";
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private FichaFinanceiraFPFacade fichaFinanceiraFPFacade;
    @EJB
    private AverbacaoTempoServicoFacade averbacaoTempoServicoFacade;
    @EJB
    private DocumentoPessoalFacade documentoPessoalFacade;
    @EJB
    private DependenteFacade dependenteFacade;
    @EJB
    private AposentadoriaFacade aposentadoriaFacade;
    @EJB
    private PensionistaFacade pensionistaFacade;
    @EJB
    private ArquivoFacade arquivoFacade;
    @EJB
    private ArquivoAtuarialAcompanhamentoFacade arquivoAtuarialAcompanhamentoFacade;
    @EJB
    private EntidadeFacade entidadeFacade;
    @EJB
    private EnquadramentoFuncionalFacade enquadramentoFuncionalFacade;
    @EJB
    private LancamentoFPFacade lancamentoFPFacade;
    @EJB
    private PessoaFisicaFacade pessoaFisicaFacade;
    @EJB
    private PrevidenciaVinculoFPFacade previdenciaVinculoFPFacade;
    @EJB
    private RegistroDeObitoFacade registroDeObitoFacade;
    @EJB
    private PensaoFacade pensaoFacade;
    @EJB
    private ExoneracaoRescisaoFacade exoneracaoRescisaoFacade;
    @EJB
    private CedenciaContratoFPFacade cedenciaContratoFPFacade;
    @EJB
    private AfastamentoFacade afastamentoFacade;
    @EJB
    private CargoConfiancaFacade cargoConfiancaFacade;
    @EJB
    private CidadeFacade cidadeFacade;
    @EJB
    private ReferenciaFPFacade referenciaFPFacade;
    @EJB
    private GrauDeParentescoFacade grauDeParentescoFacade;

    public EstudoAtuarialFacade() {
        super(EstudoAtuarial.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public EstudoAtuarial recuperar(Object id) {
        EstudoAtuarial ca = super.recuperar(id);
        if (ca.getDetentorArquivoComposicao() != null) {
            ca.getDetentorArquivoComposicao().getArquivosComposicao().size();
        }
        return ca;
    }

    public Long buscarProximoNumero() {
        String hql = "select coalesce(max(sequencia),0)+1 from EstudoAtuarial ";
        Query q = em.createQuery(hql);
        return (Long) q.getSingleResult();
    }

    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void gerarArquivos(EstudoAtuarial selecionado, AuxiliarAndamentoArquivoAtuarial auxiliar) throws Exception {
        auxiliar.setTotalRegistros(selecionado.getTodosServidores().size());

        auxiliar.criarPastaTrabalho();
        if (selecionado.getTiposArquivo().contains(TipoArquivoAtuarial.SERVIDORES_ATIVOS.name())) {
            HSSFSheet planilhaAtivos = auxiliar.criarPlanilha(AuxiliarAndamentoArquivoAtuarial.ATIVOS);
            popularConteudoPlanilhaServidoresAtivos(planilhaAtivos, auxiliar, selecionado);
        }
        if (selecionado.getTiposArquivo().contains(TipoArquivoAtuarial.APOSENTADOS.name())) {
            HSSFSheet planilhaAposentados = auxiliar.criarPlanilha(AuxiliarAndamentoArquivoAtuarial.APOSENTADOS);
            popularConteudoPlanilhaAposentados(planilhaAposentados, auxiliar, selecionado);
        }
        if (selecionado.getTiposArquivo().contains(TipoArquivoAtuarial.PENSIONISTAS.name())) {
            HSSFSheet planilhaPensionistas = auxiliar.criarPlanilha(AuxiliarAndamentoArquivoAtuarial.PENSIONISTAS);
            popularConteudoPlanilhaPensionistas(planilhaPensionistas, auxiliar, selecionado);
        }
        if (selecionado.getTiposArquivo().contains(TipoArquivoAtuarial.ATIVOS_FALECIDOS_OU_EXONERADOS.name())) {
            HSSFSheet ativosFalecidosOuExonerados = auxiliar.criarPlanilha(AuxiliarAndamentoArquivoAtuarial.ATIVOS_FALECIDOS_OU_EXONERADOS);
            popularConteudoPlanilhaAtivosFalecidosOuExonerados(ativosFalecidosOuExonerados, auxiliar, selecionado);
        }
        if (selecionado.getTiposArquivo().contains(TipoArquivoAtuarial.APOSENTADOS_FALECIDOS.name())) {
            HSSFSheet ativosFalecidosOuExonerados = auxiliar.criarPlanilha(AuxiliarAndamentoArquivoAtuarial.APOSENTADOS_FALECIDOS);
            popularConteudoPlanilhaAposentadosFalecidos(ativosFalecidosOuExonerados, auxiliar, selecionado);
        }
        if (selecionado.getTiposArquivo().contains(TipoArquivoAtuarial.PENSIONISTAS_FALECIDOS.name())) {
            HSSFSheet ativosFalecidosOuExonerados = auxiliar.criarPlanilha(AuxiliarAndamentoArquivoAtuarial.PENSIONISTAS_FALECIDOS);
            popularConteudoPlanilhaPensionistasFalecidos(ativosFalecidosOuExonerados, auxiliar, selecionado);
        }
        if (selecionado.getTiposArquivo().contains(TipoArquivoAtuarial.DEPENDENTES.name())) {
            HSSFSheet planilhaDependentes = auxiliar.criarPlanilha(AuxiliarAndamentoArquivoAtuarial.DEPENDENTES);
            popularConteudoPlanilhaDependentes(planilhaDependentes, auxiliar, selecionado);
        }

        auxiliar.criarArquivoXLSZipado();
        auxiliar.criarArquivoPDFLog();

        List<ArquivoComposicao> arquivos = Lists.newArrayList();
        arquivos.add(novoArquivoComposicao(auxiliar.getZipFile(), selecionado));
        arquivos.add(novoArquivoComposicaoLog(auxiliar.getPdfFile(), selecionado));
        selecionado.getDetentorArquivoComposicao().setArquivosComposicao(arquivos);

            selecionado.setDataReferencia(null);

            arquivoAtuarialAcompanhamentoFacade.salvarNovo(selecionado);

            auxiliar.pararProcessamento();

    }

    private ArquivoComposicao novoArquivoComposicao(File zipFile, EstudoAtuarial selecionado) throws Exception {
        Arquivo arquivo = new Arquivo();

        FileInputStream fis = new FileInputStream(zipFile);
        arquivo.setInputStream(fis);
        arquivo = arquivoFacade.novoArquivoMemoria(arquivo);

        arquivo.setMimeType("application/zip");
        arquivo.setNome(DataUtil.getDataFormatada(new Date(), "ddMMyyyy") + "_EstudoAtuarial.zip");
        arquivo.setDescricao(DataUtil.getDataFormatada(new Date(), "ddMMyyyy") + "_EstudoAtuarial");
        arquivo.setTamanho(zipFile.getTotalSpace());

        return getArquivoComposicao(zipFile, arquivo, selecionado);
    }

    private ArquivoComposicao novoArquivoComposicaoLog(File pdfFile, EstudoAtuarial selecionado) throws Exception {
        Arquivo arquivo = new Arquivo();

        FileInputStream fis = new FileInputStream(pdfFile);
        arquivo.setInputStream(fis);
        arquivo = arquivoFacade.novoArquivoMemoria(arquivo);

        arquivo.setMimeType("application/pdf");
        arquivo.setNome(DataUtil.getDataFormatada(new Date(), "ddMMyyyy") + "_EstudoAtuarialLog.pdf");
        arquivo.setDescricao(DataUtil.getDataFormatada(new Date(), "ddMMyyyy") + "_EstudoAtuarialLog");
        arquivo.setTamanho(pdfFile.getTotalSpace());

        return getArquivoComposicao(pdfFile, arquivo, selecionado);
    }

    private ArquivoComposicao getArquivoComposicao(File file, Arquivo arquivo, EstudoAtuarial selecionado) {
        ArquivoComposicao ac = new ArquivoComposicao();
        ac.setArquivo(arquivo);
        ac.setDataUpload(new Date());
        ac.setFile(file);
        ac.setDetentorArquivoComposicao(selecionado.getDetentorArquivoComposicao());
        return ac;
    }

    private void popularConteudoPlanilhaPensionistas(HSSFSheet planilhaPensionistas, AuxiliarAndamentoArquivoAtuarial auxiliar, EstudoAtuarial estudoAtuarial) {
        auxiliar.montarCabecalhoPensionistas(planilhaPensionistas);

        HashMap<Date, List<Pensionista>> IdsPensionistasPorMes = estudoAtuarial.getPensionistasPorMes();
        Map<Date, List<Pensionista>> IdsPensionistaPorMesOrdenado = new TreeMap<>(IdsPensionistasPorMes);

        Map<Long, String> carteiraDeTrabalhoCache = new HashMap<>();
        Map<String, Cidade> cidadeCache = new HashMap<>();

        for (Map.Entry<Date, List<Pensionista>> entry : IdsPensionistaPorMesOrdenado.entrySet()) {
            List<Pensionista> idsPensionistas = entry.getValue();
            Date competencia = entry.getKey();
            estudoAtuarial.setDataReferencia(Util.getDataHoraMinutoSegundoZerado(Util.recuperaDataUltimoDiaDoMes(competencia)));
            for (Pensionista pensionista : idsPensionistas) {

                Entidade entidade = getEntidade(pensionista, estudoAtuarial, auxiliar);
                String unidadeOrganizacionalLotacao = getUnidadeOrganizacionalLotacaoVigente(pensionista, estudoAtuarial, auxiliar);
                String remuneracaoContribuicao = getRemuneracaoContribuicao(pensionista, estudoAtuarial, auxiliar);
                String remuneracaoContribuicaoPrevidencia = getRemuneracaoContribuicaoPrevidencia(pensionista, estudoAtuarial, auxiliar);


                if (entidade == null) {
                    auxiliar.adicionarLog(AuxiliarAndamentoArquivoAtuarial.TipoLog.ALERTA, "Não foi possivel recuperar Entidade para o Pensionista " + getComplementoMensagemLog(pensionista));
                    auxiliar.incrementarContador();
                    continue;
                }

                String chaveCidade = entidade.getPessoaJuridica().getEnderecoPrincipal().getLocalidade() + "_" + entidade.getPessoaJuridica().getEnderecoPrincipal().getUf();
                Cidade cidade = cidadeCache.get(chaveCidade);
                if (cidade == null) {
                    cidade = cidadeFacade.recuperaCidadePorNomeEEstado(entidade.getPessoaJuridica().getEnderecoPrincipal().getLocalidade(), entidade.getPessoaJuridica().getEnderecoPrincipal().getUf());
                    if (cidade == null) {
                        auxiliar.adicionarLog(AuxiliarAndamentoArquivoAtuarial.TipoLog.ALERTA, "Não foi possivel recuperar Cidade para o Pensionista " + getComplementoMensagemLog(pensionista));
                        auxiliar.incrementarContador();
                        continue;
                    }
                    cidadeCache.put(chaveCidade, cidade);
                }

                String carteiraDeTrabalho = carteiraDeTrabalhoCache.get(pensionista.getId());
                if (carteiraDeTrabalho == null) {
                    carteiraDeTrabalho = getCarteiraDeTrabalho(pensionista.getPensao().getContratoFP());
                    carteiraDeTrabalhoCache.put(pensionista.getId(), carteiraDeTrabalho);
                }

                HSSFRow linha = auxiliar.getNovaLinha(planilhaPensionistas);
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaPensionistas.ANO_EXERCICIO.getIndex()).setCellValue(DataUtil.getAno(estudoAtuarial.getDataReferencia()));
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaPensionistas.MES.getIndex()).setCellValue(DataUtil.getMes(estudoAtuarial.getDataReferencia()));
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaPensionistas.CODIGO_DO_ENTE_NO_IBGE.getIndex()).setCellValue(cidade.getCodigoIBGE());
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaPensionistas.NOME_DO_ENTE.getIndex()).setCellValue(entidade.getPessoaJuridica().getEnderecoPrincipal().getLocalidade());
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaPensionistas.SIGLA_DA_UF_DO_ENTE.getIndex()).setCellValue(entidade.getPessoaJuridica().getEnderecoPrincipal().getUf());
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaPensionistas.COMPOSICAO_DA_MASSA_A.getIndex()).setCellValue(1);
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaPensionistas.COMPOSICAO_DA_MASSA_B.getIndex()).setCellValue(1);
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaPensionistas.CNPJ.getIndex()).setCellValue(entidade.getPessoaJuridica().getCnpj());
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaPensionistas.NOME_DO_ORGAO_ENTIDADE.getIndex()).setCellValue(unidadeOrganizacionalLotacao);
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaPensionistas.PODER.getIndex()).setCellValue(entidade.getEsferaDoPoder().getCodigoSiprev());
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaPensionistas.TIPO.getIndex()).setCellValue(TipoAdministracao.DIRETA.equals(entidade.getTipoAdministracao()) ? 1 : 2);
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaPensionistas.CO_TIPO_INSTITUIDOR.getIndex()).setCellValue(instituidorAposentado(pensionista.getPensao().getContratoFP()));
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaPensionistas.A2_IDENTIFICACAO_SEGURADO_INSTITUIDOR_PENSAO_MATRICULA.getIndex()).setCellValue(getMatriculaFP(pensionista.getPensao().getContratoFP()).getMatricula());
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaPensionistas.A3_IDENTIFICACAO_SEGURADO_INSTITUIDOR_PENSAO_CPF.getIndex()).setCellValue(pensionista.getPensao().getContratoFP().getMatriculaFP().getPessoa().getCpf());
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaPensionistas.A4_IDENTIFICACAO_SEGURADO_INSTITUIDOR_PENSAO_PIS_PASEP.getIndex()).setCellValue(carteiraDeTrabalho);
                //novo
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaPensionistas.CODIGO_SEXO_INSTITUIDOR_DA_PENSAO.getIndex()).setCellValue(pensionista.getPensao().getContratoFP().getMatriculaFP().getPessoa().getSexo().getCodigoEstudoAtuarial());
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaPensionistas.DATA_NASCIMENTO_INSTITUIDOR_PENSAO.getIndex()).setCellValue(pensionista.getPensao().getContratoFP().getMatriculaFP().getPessoa().getDataNascimentoFormatada());
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaPensionistas.DATA_FALESCIMENTO_INSTITUIDADE_PENSAO.getIndex()).setCellValue(dataFalecimentoInstituidorPensao(pensionista.getPensao().getContratoFP().getMatriculaFP().getPessoa(), pensionista.getPensao().getContratoFP()));
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaPensionistas.MATRICULA_PENSIONISTA.getIndex()).setCellValue(pensionista.getMatriculaFP().getMatricula());
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaPensionistas.IDENTIFICACAO_PENSIONISTA_CPF.getIndex()).setCellValue(pensionista.getMatriculaFP().getPessoa().getCpf());
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaPensionistas.CODIGO_SEXO_DO_PENSIONISTA.getIndex()).setCellValue(pensionista.getMatriculaFP().getPessoa().getSexo().getCodigoEstudoAtuarial());
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaPensionistas.DATA_DE_NASCIMENTO_PENSIONISTA.getIndex()).setCellValue(DataUtil.getDataFormatada(pensionista.getMatriculaFP().getPessoa().getDataNascimento()));
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaPensionistas.TIPO_RELACAO_PENSIONISTA_COM_SEGURADO_INSTITUIDOR.getIndex()).setCellValue(pensionista.getTipoDependEstudoAtuarial() != null ? pensionista.getTipoDependEstudoAtuarial().getCodigoEstudoAtuarial() : "");
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaPensionistas.DATA_DE_INICIO_DO_BENEFICIO_DE_PENSAO.getIndex()).setCellValue(pensionista.getInicioVigenciaFormatado());
                auxiliar.criarCelulaMonetario(linha, AuxiliarAndamentoArquivoAtuarial.ColunaPensionistas.VALOR_MENSAL_DO_BENEFICIO_DE_PENSAO.getIndex()).setCellValue(remuneracaoContribuicao);
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaPensionistas.VALOR_TOTAL_PENSAO.getIndex()).setCellValue(getValorPensaoInstituidor(pensionista, estudoAtuarial, auxiliar));
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaPensionistas.VALOR_PERCENTUAL_QUOTA_RECEBIDA_PELO_PENSIONISTA.getIndex()).setCellValue(pensionista.getPercentual().multiply(BigDecimal.valueOf(100)).toString());
                auxiliar.criarCelulaMonetario(linha, AuxiliarAndamentoArquivoAtuarial.ColunaPensionistas.VALOR_MENSAL_DA_CONTRIBUCAO_PREVIDENCIARIA.getIndex()).setCellValue(remuneracaoContribuicaoPrevidencia);
                auxiliar.criarCelulaMonetario(linha, AuxiliarAndamentoArquivoAtuarial.ColunaPensionistas.VALOR_MENSAL_DA_COMPENSACAO_PREVIDENCIARIA.getIndex()).setCellValue("0");
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaPensionistas.IDENTIFICADOR_DE_PARIDADE_COM_SERVIDORES_ATIVOS.getIndex()).setCellValue(getParidadeInstituidor(pensionista.getPensao().getContratoFP()));
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaPensionistas.CONDICAO_DO_PENSIONISTA.getIndex()).setCellValue(!pensionista.getListaInvalidez().isEmpty() ? 2 : 1);
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaPensionistas.DURACAO_DO_BENEFICIO.getIndex()).setCellValue(getTipoBeneficioPensao(pensionista));
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaPensionistas.TEMPO_DE_DURACAO_DO_BENEFICIO.getIndex()).setCellValue(tempoBeneficio(pensionista));
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaPensionistas.PREVIDENCIA_COMPLEMENTAR.getIndex()).setCellValue("2");
                String tetoRemuneratorioReferenciaFP = "0";
                if (pensionista.getContratoFP().getCargo().getReferenciaFP() != null) {
                    ReferenciaFP referenciaFP = referenciaFPFacade.recuperar(pensionista.getContratoFP().getCargo().getReferenciaFP().getId());
                    if (referenciaFP.getValoresReferenciasFPs() != null && !referenciaFP.getValoresReferenciasFPs().isEmpty()) {
                        tetoRemuneratorioReferenciaFP = referenciaFP.getValoresReferenciasFPs().get(0).getValor().toString();
                    }
                }
                auxiliar.criarCelulaMonetario(linha, AuxiliarAndamentoArquivoAtuarial.ColunaPensionistas.TETO_CONSTITUCIONAL_REMUNERATORIO_ESPECIFICO.getIndex()).setCellValue(tetoRemuneratorioReferenciaFP);

                auxiliar.incrementarContador();
            }
        }
    }

    private String getTipoBeneficioPensao(Pensionista pensionista) {
        if (TipoPensao.VITALICIA_INVALIDEZ.equals(pensionista.getTipoPensao())
            || TipoPensao.VITALICIA.equals(pensionista.getTipoPensao())) {
            return "1";
        } else {
            return "2";
        }
    }

    private String getOrigemPensao(Pensionista pensionista) {
        ContratoFP instituidor = pensionista.getContratoFP();
        Aposentadoria aposentadoria = aposentadoriaFacade.recuperaAposentadoriaPorContratoFP(instituidor);
        if (aposentadoria != null) {
            if (aposentadoria.getTipoAposentadoria().isInvalidez()) {
                return String.valueOf(AuxiliarAndamentoArquivoAtuarial.OrigemPensao.POR_MORTE_DE_SERVIDOR_POSENTADO_POR_INVALIDEZ.getCodigo());
            }
            return String.valueOf(AuxiliarAndamentoArquivoAtuarial.OrigemPensao.POR_MORTE_DE_SERVIDOR_APOSENTADO_POR_VOLUNTARIO_OU_COMPULSORIO.getCodigo());
        }
        return String.valueOf(AuxiliarAndamentoArquivoAtuarial.OrigemPensao.POR_MORTE_DE_SERVIDOR_EM_ATIVIDADE.getCodigo());
    }

    private String getNome(VinculoFP vinculoFP, AuxiliarAndamentoArquivoAtuarial auxiliar) {
        try {
            return getPessoaFisica(vinculoFP).getNome();
        } catch (ExcecaoNegocioGenerica eng) {
            auxiliar.adicionarLog(AuxiliarAndamentoArquivoAtuarial.TipoLog.ALERTA, eng.getMessage());
            return "";
        }
    }

    private void popularConteudoPlanilhaAposentados(HSSFSheet planilhaAposentados, AuxiliarAndamentoArquivoAtuarial auxiliar, EstudoAtuarial estudoAtuarial) {
        auxiliar.montarCabecalhoAposentados(planilhaAposentados);

        HashMap<Date, List<Aposentadoria>> IdsAposentadoriaPorMes = estudoAtuarial.getAposentadoriaPorMes();
        Map<Date, List<Aposentadoria>> IdsAposentadoriaPorMesOrdenado = new TreeMap<>(IdsAposentadoriaPorMes);

        Map<Long, String> carteiraDeTrabalhoCache = new HashMap<>();
        Map<String, Cidade> cidadeCache = new HashMap<>();
        Map<Long, PessoaFisica> pessoaCache = new HashMap<>();
        Map<Long, String> tempoContribuicaoRPPSMunicipalCache = new HashMap<>();
        Map<Long, String> tempoContribuicaoRPPSEstadualCache = new HashMap<>();
        Map<Long, String> tempoContribuicaoRPPSFederalCache = new HashMap<>();
        Map<Long, ContratoFP> contratoAposentadoCache = new HashMap<>();

        for (Map.Entry<Date, List<Aposentadoria>> entry : IdsAposentadoriaPorMesOrdenado.entrySet()) {
            List<Aposentadoria> idsAposentados = entry.getValue();
            Date competencia = entry.getKey();
            estudoAtuarial.setDataReferencia(Util.getDataHoraMinutoSegundoZerado(Util.recuperaDataUltimoDiaDoMes(competencia)));

            for (Aposentadoria aposentadoria : idsAposentados) {

                Entidade entidade = getEntidade(aposentadoria, estudoAtuarial, auxiliar);
                String unidadeOrganizacionalLotacao = getUnidadeOrganizacionalLotacaoVigente(aposentadoria, estudoAtuarial, auxiliar);
                String remuneracaoContribuicao = getRemuneracaoContribuicao(aposentadoria, estudoAtuarial, auxiliar);
                String remuneracaoContribuicaoPrevidencia = getRemuneracaoContribuicaoPrevidencia(aposentadoria, estudoAtuarial, auxiliar);

                if (entidade == null) {
                    auxiliar.adicionarLog(AuxiliarAndamentoArquivoAtuarial.TipoLog.ALERTA, "Não foi possivel recuperar Entidade para a Aposentadoria " + getComplementoMensagemLog(aposentadoria));
                    auxiliar.incrementarContador();
                    continue;
                }

                String chaveCidade = entidade.getPessoaJuridica().getEnderecoPrincipal().getLocalidade() + "_" + entidade.getPessoaJuridica().getEnderecoPrincipal().getUf();
                Cidade cidade = cidadeCache.get(chaveCidade);
                if (cidade == null) {
                    cidade = cidadeFacade.recuperaCidadePorNomeEEstado(entidade.getPessoaJuridica().getEnderecoPrincipal().getLocalidade(), entidade.getPessoaJuridica().getEnderecoPrincipal().getUf());
                    if (cidade == null) {
                        auxiliar.adicionarLog(AuxiliarAndamentoArquivoAtuarial.TipoLog.ALERTA, "Não foi possivel recuperar Cidade para a Aposentadoria " + getComplementoMensagemLog(aposentadoria));
                        auxiliar.incrementarContador();
                        continue;
                    }
                    cidadeCache.put(chaveCidade, cidade);
                }

                String carteiraDeTrabalho = carteiraDeTrabalhoCache.get(aposentadoria.getId());
                if (carteiraDeTrabalho == null) {
                    carteiraDeTrabalho = getCarteiraDeTrabalho(aposentadoria);
                    carteiraDeTrabalhoCache.put(aposentadoria.getId(), carteiraDeTrabalho);
                }

                PessoaFisica pessoaFisica = pessoaCache.get(aposentadoria.getId());
                if (pessoaFisica == null) {
                    pessoaFisica = getPessoaFisica(aposentadoria);
                    pessoaCache.put(aposentadoria.getId(), pessoaFisica);
                }

                ContratoFP contratoAposentado = contratoAposentadoCache.get(aposentadoria.getId());
                if (contratoAposentado == null) {
                    contratoAposentado = contratoFPFacade.buscarContratoPorMatriculaAndContrato(aposentadoria.getMatriculaFP().getMatricula(), aposentadoria.getNumero());
                    contratoAposentadoCache.put(aposentadoria.getId(), contratoAposentado);
                }

                String tempoContribuicaoRPPSMunicipal = tempoContribuicaoRPPSMunicipalCache.get(aposentadoria.getId());
                if (tempoContribuicaoRPPSMunicipal == null) {
                    tempoContribuicaoRPPSMunicipal = getTempoContribuicaoAnteriorAdmissao(contratoAposentado, auxiliar, TipoRegimePrevidenciarioEstudoAtuarial.RPPS_ESFERA_MUNICIPAL);
                    tempoContribuicaoRPPSMunicipalCache.put(aposentadoria.getId(), tempoContribuicaoRPPSMunicipal);
                }
                String tempoContribuicaoRPPSEstadual = tempoContribuicaoRPPSEstadualCache.get(aposentadoria.getId());
                if (tempoContribuicaoRPPSEstadual == null) {
                    tempoContribuicaoRPPSEstadual = getTempoContribuicaoAnteriorAdmissao(contratoAposentado, auxiliar, TipoRegimePrevidenciarioEstudoAtuarial.RPPS_ESFERA_ESTADUAL);
                    tempoContribuicaoRPPSEstadualCache.put(aposentadoria.getId(), tempoContribuicaoRPPSEstadual);
                }
                String tempoContribuicaoRPPSFederal = tempoContribuicaoRPPSFederalCache.get(aposentadoria.getId());
                if (tempoContribuicaoRPPSFederal == null) {
                    tempoContribuicaoRPPSFederal = getTempoContribuicaoAnteriorAdmissao(contratoAposentado, auxiliar, TipoRegimePrevidenciarioEstudoAtuarial.RPPS_ESFERA_FEDERAL);
                    tempoContribuicaoRPPSFederalCache.put(aposentadoria.getId(), tempoContribuicaoRPPSFederal);
                }

                HSSFRow linha = auxiliar.getNovaLinha(planilhaAposentados);
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAposentados.ANO_EXERCICIO.getIndex()).setCellValue(DataUtil.getAno(estudoAtuarial.getDataReferencia()));
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAposentados.MES.getIndex()).setCellValue(DataUtil.getMes(estudoAtuarial.getDataReferencia()));
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAposentados.CODIGO_DO_ENTE_NO_IBGE.getIndex()).setCellValue(cidade.getCodigoIBGE());
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAposentados.NOME_DO_ENTE.getIndex()).setCellValue(entidade.getPessoaJuridica().getEnderecoPrincipal().getLocalidade());
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAposentados.SIGLA_DA_UF_DO_ENTE.getIndex()).setCellValue(entidade.getPessoaJuridica().getEnderecoPrincipal().getUf());
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAposentados.COMPOSICAO_DA_MASSA_A.getIndex()).setCellValue(1);
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAposentados.COMPOSICAO_DA_MASSA_B.getIndex()).setCellValue(1);
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAposentados.CNPJ.getIndex()).setCellValue(entidade.getPessoaJuridica().getCnpj());
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAposentados.NOME_ORGAO.getIndex()).setCellValue(unidadeOrganizacionalLotacao);
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAposentados.PODER.getIndex()).setCellValue(entidade.getEsferaDoPoder().getCodigoSiprev());
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAposentados.TIPO.getIndex()).setCellValue(TipoAdministracao.DIRETA.equals(entidade.getTipoAdministracao()) ? 1 : 2);
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAposentados.A_POPULACAO_COBERTA.getIndex()).setCellValue(4);
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAposentados.B_ESPECIFICACAO_DO_TIPO_DE_CARGO.getIndex()).setCellValue(
                    String.valueOf(aposentadoria.getContratoFP().getCargo().getTipoEspecificacaoCargo() != null ? TipoEspecificacaoCargo.valueOf(aposentadoria.getContratoFP().getCargo().getTipoEspecificacaoCargo().name()).getCodigo() : TipoEspecificacaoCargo.DEMAIS_SERVIDORES.getCodigo()));
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAposentados.TIPO_DO_BENEFICIO.getIndex()).setCellValue(aposentadoria.getTipoBeneficioEstudoAtuarial() != null ? aposentadoria.getTipoBeneficioEstudoAtuarial().getCodigo() : "");
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAposentados.IDENTIFICACAO_DO_APOSENTADO_MATRICULA.getIndex()).setCellValue(getMatricula(aposentadoria, auxiliar));
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAposentados.IDENTIFICACAO_DO_APOSENTADO_CPF.getIndex()).setCellValue(pessoaFisica.getCpf());
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAposentados.IDENTIFICACAO_DO_APOSENTADO_PIS_PASEP.getIndex()).setCellValue(carteiraDeTrabalho);
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAposentados.SEXO_DO_APOSENTADO.getIndex()).setCellValue(getSexo(aposentadoria.getMatriculaFP().getPessoa(), auxiliar));
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAposentados.ESTADO_CIVIL_DO_APOSENTADO.getIndex()).setCellValue(pessoaFisica.getEstadoCivil().getCodigoSiprev());
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAposentados.DATA_DE_NASCIMENTO_APOSENTADO.getIndex()).setCellValue(getDataNascimento(aposentadoria, auxiliar));
                ContratoFP primeiroContrato = contratoFPFacade.buscarContratoPorMatriculaAndContrato(aposentadoria.getMatriculaFP().getMatricula(), "1");
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAposentados.DATA_DE_INGRESSO_NO_SERVICO_PUBLICO.getIndex()).setCellValue(primeiroContrato != null && primeiroContrato.getInicioVigencia() != null ? DataUtil.getDataFormatada(primeiroContrato.getInicioVigencia()) : "");
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAposentados.DATA_DE_INGRESSO_NO_ENTE.getIndex()).setCellValue(DataUtil.getDataFormatada(aposentadoria.getContratoFP().getInicioVigencia()));
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAposentados.DATA_DE_INICIO_DO_BENEFICIO_DE_APOSENTADORIA.getIndex()).setCellValue(aposentadoria.getInicioVigenciaFormatado());
                auxiliar.criarCelulaMonetario(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAposentados.VALOR_MENSAL_DO_BENEFICIO_DE_APOSENTADORIA.getIndex()).setCellValue(remuneracaoContribuicao);
                auxiliar.criarCelulaMonetario(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAposentados.CONTRIBUICAO_MENSAL_DO_APOSENTADO.getIndex()).setCellValue(remuneracaoContribuicaoPrevidencia);
                auxiliar.criarCelulaMonetario(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAposentados.VALOR_MENSAL_DA_COMPENSACAO_PREVIDENCIARIA.getIndex()).setCellValue("0");
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAposentados.IDENTIFICADOR_DE_PARIDADE_COM_SERVIDORES_APOSENTADO.getIndex()).setCellValue(aposentadoria.getTipoReajusteAposentadoria().equals(TipoReajusteAposentadoria.PARIDADE) ? "1" : "2");
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAposentados.CONDICAO_DO_APOSENTADO.getIndex()).setCellValue(hasCondicaoAposentado(aposentadoria) ? 2 : 1);
                List<Dependente> dependentes = dependenteFacade.buscarDependentesVinculoFP(pessoaFisica, estudoAtuarial.getDataReferencia());
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAposentados.NUMERO_DE_DEPENDENTES_DO_APOSENTADO_MILITAR_INATIVO.getIndex()).setCellValue(dependentes.size());
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAposentados.TEMPO_DE_CONTRIBUICAO_DO_SERVIDOR_PARA_OUTROS_RPPS_ANTERIOR_A_ADMISSAO_NO_ENTE_MUNICIPAL.getIndex()).setCellValue(tempoContribuicaoRPPSMunicipal);
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAposentados.TEMPO_DE_CONTRIBUICAO_DO_SERVIDOR_PARA_OUTROS_RPPS_ANTERIOR_A_ADMISSAO_NO_ENTE_ESTADUAL.getIndex()).setCellValue(tempoContribuicaoRPPSEstadual);
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAposentados.TEMPO_DE_CONTRIBUICAO_DO_SERVIDOR_PARA_OUTROS_RPPS_ANTERIOR_A_ADMISSAO_NO_ENTE_FEDERAL.getIndex()).setCellValue(tempoContribuicaoRPPSFederal);
                auxiliar.criarCelulaMonetario(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAposentados.PREVIDENCIA_COMPLEMENTAR.getIndex()).setCellValue("2");
                String tetoRemuneratorioReferenciaFP = "0";
                if (aposentadoria.getContratoFP().getCargo().getReferenciaFP() != null) {
                    ReferenciaFP referenciaFP = referenciaFPFacade.recuperar(aposentadoria.getContratoFP().getCargo().getReferenciaFP().getId());
                    if (referenciaFP.getValoresReferenciasFPs() != null && !referenciaFP.getValoresReferenciasFPs().isEmpty()) {
                        tetoRemuneratorioReferenciaFP = referenciaFP.getValoresReferenciasFPs().get(0).getValor().toString();
                    }
                }
                auxiliar.criarCelulaMonetario(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAposentados.TETO_CONSTITUCIONAL_REMUNERATORIO_ESPECIFICO.getIndex()).setCellValue(tetoRemuneratorioReferenciaFP);
                auxiliar.incrementarContador();
            }
        }
    }

    private void popularConteudoPlanilhaAposentadosFalecidos(HSSFSheet planilhaAposentadosFalecidos, AuxiliarAndamentoArquivoAtuarial auxiliar, EstudoAtuarial estudoAtuarial) {
        auxiliar.montarCabecalhoAposentadosFalecidos(planilhaAposentadosFalecidos);
        HashMap<Date, List<Aposentadoria>> IdsAposentadosFalecidosPorMes = estudoAtuarial.getAposentadosFalecidosPorMes();
        Map<Date, List<Aposentadoria>> IdsAposentadosFalecidosPorMesOrdenado = new TreeMap<>(IdsAposentadosFalecidosPorMes);

        Map<Long, String> carteiraDeTrabalhoCache = new HashMap<>();
        Map<String, Cidade> cidadeCache = new HashMap<>();
        Map<Long, PessoaFisica> pessoaCache = new HashMap<>();

        for (Map.Entry<Date, List<Aposentadoria>> entry : IdsAposentadosFalecidosPorMesOrdenado.entrySet()) {
            List<Aposentadoria> idsAposentados = entry.getValue();
            Date competencia = entry.getKey();
            estudoAtuarial.setDataReferencia(Util.getDataHoraMinutoSegundoZerado(Util.recuperaDataUltimoDiaDoMes(competencia)));

            for (Aposentadoria aposentadoria : idsAposentados) {

                Entidade entidade = getEntidade(aposentadoria, estudoAtuarial, auxiliar);
                String unidadeOrganizacionalLotacao = getUnidadeOrganizacionalLotacaoVigente(aposentadoria, estudoAtuarial, auxiliar);
                String remuneracaoContribuicao = getRemuneracaoContribuicao(aposentadoria, estudoAtuarial, auxiliar);
                String remuneracaoContribuicaoPrevidencia = getRemuneracaoContribuicaoPrevidencia(aposentadoria, estudoAtuarial, auxiliar);

                if (entidade == null) {
                    auxiliar.adicionarLog(AuxiliarAndamentoArquivoAtuarial.TipoLog.ALERTA, "Não foi possivel recuperar Entidade para a Aposentadoria " + getComplementoMensagemLog(aposentadoria));
                    auxiliar.incrementarContador();
                    continue;
                }

                String chaveCidade = entidade.getPessoaJuridica().getEnderecoPrincipal().getLocalidade() + "_" + entidade.getPessoaJuridica().getEnderecoPrincipal().getUf();
                Cidade cidade = cidadeCache.get(chaveCidade);
                if (cidade == null) {
                    cidade = cidadeFacade.recuperaCidadePorNomeEEstado(entidade.getPessoaJuridica().getEnderecoPrincipal().getLocalidade(), entidade.getPessoaJuridica().getEnderecoPrincipal().getUf());
                    if (cidade == null) {
                        auxiliar.adicionarLog(AuxiliarAndamentoArquivoAtuarial.TipoLog.ALERTA, "Não foi possivel recuperar Cidade para a Aposentadoria " + getComplementoMensagemLog(aposentadoria));
                        auxiliar.incrementarContador();
                        continue;
                    }
                    cidadeCache.put(chaveCidade, cidade);
                }

                String carteiraDeTrabalho = carteiraDeTrabalhoCache.get(aposentadoria.getId());
                if (carteiraDeTrabalho == null) {
                    carteiraDeTrabalho = getCarteiraDeTrabalho(aposentadoria);
                    carteiraDeTrabalhoCache.put(aposentadoria.getId(), carteiraDeTrabalho);
                }

                PessoaFisica pessoaFisica = pessoaCache.get(aposentadoria.getId());
                if (pessoaFisica == null) {
                    pessoaFisica = getPessoaFisica(aposentadoria);
                    pessoaCache.put(aposentadoria.getId(), pessoaFisica);
                }

                HSSFRow linha = auxiliar.getNovaLinha(planilhaAposentadosFalecidos);
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAposentadosFalecidos.ANO_REFERENCIA.getIndex()).setCellValue(DataUtil.getAno(estudoAtuarial.getDataReferencia()));
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAposentadosFalecidos.MES.getIndex()).setCellValue(DataUtil.getMes(estudoAtuarial.getDataReferencia()));
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAposentadosFalecidos.CODIGO_DO_ENTE_NO_IBGE.getIndex()).setCellValue(cidade.getCodigoIBGE());
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAposentadosFalecidos.NOME_DO_ENTE.getIndex()).setCellValue(entidade.getPessoaJuridica().getEnderecoPrincipal().getLocalidade());
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAposentadosFalecidos.SIGLA_DA_UF_DO_ENTE.getIndex()).setCellValue(entidade.getPessoaJuridica().getEnderecoPrincipal().getUf());
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAposentadosFalecidos.COMPOSICAO_DA_MASSA_A.getIndex()).setCellValue(1);
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAposentadosFalecidos.COMPOSICAO_DA_MASSA_B.getIndex()).setCellValue(1);
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAposentadosFalecidos.CNPJ.getIndex()).setCellValue(entidade.getPessoaJuridica().getCnpj());
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAposentadosFalecidos.DENOMINACAO_ORGAO_ENTIDADE.getIndex()).setCellValue(unidadeOrganizacionalLotacao);
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAposentadosFalecidos.PODER.getIndex()).setCellValue(entidade.getEsferaDoPoder().getCodigoSiprev());
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAposentadosFalecidos.TIPO.getIndex()).setCellValue(TipoAdministracao.DIRETA.equals(entidade.getTipoAdministracao()) ? 1 : 2);
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAposentadosFalecidos.A_POPULACAO_COBERTA.getIndex()).setCellValue(4);
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAposentadosFalecidos.B_ESPECIFICACAO_DO_TIPO_DE_CARGO.getIndex()).setCellValue(aposentadoria.getContratoFP().getCargo().getTipoEspecificacaoCargo() != null ?
                    aposentadoria.getContratoFP().getCargo().getTipoEspecificacaoCargo().getCodigo() : "7");
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAposentadosFalecidos.TIPO_DO_BENEFICIO.getIndex()).setCellValue(aposentadoria.getTipoBeneficioEstudoAtuarial() != null ? aposentadoria.getTipoBeneficioEstudoAtuarial().getCodigo() : "");
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAposentadosFalecidos.IDENTIFICACAO_DO_APOSENTADO_MILITAR_MATRICULA.getIndex()).setCellValue(getMatricula(aposentadoria, auxiliar));
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAposentadosFalecidos.IDENTIFICACAO_DO_APOSENTADO_MILITAR_CPF.getIndex()).setCellValue(pessoaFisica.getCpf());
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAposentadosFalecidos.IDENTIFICACAO_DO_APOSENTADO_MILITAR_PIS_PASEP.getIndex()).setCellValue(carteiraDeTrabalho);
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAposentadosFalecidos.SEXO_DO_APOSENTADO_MILITAR.getIndex()).setCellValue(getSexo(aposentadoria.getMatriculaFP().getPessoa(), auxiliar));
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAposentadosFalecidos.ESTADO_CIVIL_DO_APOSENTADO_MILITAR.getIndex()).setCellValue(pessoaFisica.getEstadoCivil().getCodigoSiprev());
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAposentadosFalecidos.DATA_DE_NASCIMENTO_APOSENTADO_MILITAR.getIndex()).setCellValue(getDataNascimento(aposentadoria, auxiliar));
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAposentadosFalecidos.SITUACAO.getIndex()).setCellValue("12");
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAposentadosFalecidos.DATA_SITUACAO.getIndex()).setCellValue(getDataFalecimento(aposentadoria.getMatriculaFP().getPessoa()));

                ContratoFP primeiroContrato = contratoFPFacade.buscarContratoPorMatriculaAndContrato(aposentadoria.getMatriculaFP().getMatricula(), "1");
                AverbacaoTempoServico averbacaoTempoServico = averbacaoTempoServicoFacade.buscarPrimeiraAverbacaoTempoServicoPorContratoETipo(aposentadoria.getContratoFP(), OrgaoEmpresa.ORGAO_PUBLICO);
                Date ingressoServicoPublico = (averbacaoTempoServico != null && averbacaoTempoServico.getInicioVigencia().before(primeiroContrato.getDataNomeacao())) ? averbacaoTempoServico.getInicioVigencia() : primeiroContrato.getDataNomeacao();

                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAposentadosFalecidos.DATA_DE_INGRESSO_SERVICO_PUBLICO.getIndex()).setCellValue(DataUtil.getDataFormatada(ingressoServicoPublico));
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAposentadosFalecidos.DATA_DE_INGRESSO_NO_ENTE.getIndex()).setCellValue(DataUtil.getDataFormatada(aposentadoria.getContratoFP().getInicioVigencia()));
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAposentadosFalecidos.DATA_DE_INICIO_DO_BENEFICIO_DE_APOSENTADORIA_INATIVIDADE_MILITAR.getIndex()).setCellValue(aposentadoria.getInicioVigenciaFormatado());
                auxiliar.criarCelulaMonetario(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAposentadosFalecidos.VALOR_MENSAL_DO_BENEFICIO_DE_APOSENTADORIA_INATIVIDADE_MILITAR.getIndex()).setCellValue(remuneracaoContribuicao);
                auxiliar.criarCelulaMonetario(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAposentadosFalecidos.CONTRIBUICAO_MENSAL.getIndex()).setCellValue(remuneracaoContribuicaoPrevidencia);
                auxiliar.criarCelulaMonetario(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAposentadosFalecidos.VALOR_MENSAL_DA_COMPENSACAO_PREVIDENCIARIA.getIndex()).setCellValue("0");
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAposentadosFalecidos.IDENTIFICADOR_DE_PARIDADE_COM_SERVIDORES_ATIVOS.getIndex()).setCellValue(aposentadoria.getTipoReajusteAposentadoria().equals(TipoReajusteAposentadoria.PARIDADE) ? "1" : "2");
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAposentadosFalecidos.CONDICAO_DO_APOSENTADO_MILITAR.getIndex()).setCellValue(hasCondicaoAposentado(aposentadoria) ? 2 : 1);
                auxiliar.criarCelulaMonetario(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAposentadosFalecidos.PREVIDENCIA_COMPLEMENTAR.getIndex()).setCellValue("2");

                String tetoRemuneratorioReferenciaFP = "0";
                if (aposentadoria.getContratoFP().getCargo().getReferenciaFP() != null) {
                    ReferenciaFP referenciaFP = referenciaFPFacade.recuperar(aposentadoria.getContratoFP().getCargo().getReferenciaFP().getId());
                    if (referenciaFP.getValoresReferenciasFPs() != null && !referenciaFP.getValoresReferenciasFPs().isEmpty()) {
                        tetoRemuneratorioReferenciaFP = referenciaFP.getValoresReferenciasFPs().get(0).getValor().toString();
                    }
                }
                auxiliar.criarCelulaMonetario(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAposentadosFalecidos.TETO_CONSTITUCIONAL_REMUNERATORIO_ESPECIFICO.getIndex()).setCellValue(tetoRemuneratorioReferenciaFP);

                List<Dependente> dependentes = dependenteFacade.buscarDependentesVinculoFP(pessoaFisica, estudoAtuarial.getDataReferencia());
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAposentadosFalecidos.NUMERO_DE_DEPENDENTES_DO_APOSENTADO_MILITAR_INATIVO.getIndex()).setCellValue(dependentes.size());
                auxiliar.incrementarContador();
            }
        }
    }

    private void popularConteudoPlanilhaPensionistasFalecidos(HSSFSheet planilhaPensionistasFalecidos, AuxiliarAndamentoArquivoAtuarial auxiliar, EstudoAtuarial estudoAtuarial) {
        auxiliar.montarCabecalhoPensionistasFalecidos(planilhaPensionistasFalecidos);
        HashMap<Date, List<Pensionista>> IdsPensionistasFalecidosPorMes = estudoAtuarial.getPensionistasFalecidosPorMes();
        Map<Date, List<Pensionista>> IdsPensionistaFalecidosPorMesOrdenado = new TreeMap<>(IdsPensionistasFalecidosPorMes);

        Map<Long, String> carteiraDeTrabalhoCache = new HashMap<>();
        Map<String, Cidade> cidadeCache = new HashMap<>();

        for (Map.Entry<Date, List<Pensionista>> entry : IdsPensionistaFalecidosPorMesOrdenado.entrySet()) {
            List<Pensionista> idsPensionistas = entry.getValue();
            Date competencia = entry.getKey();
            estudoAtuarial.setDataReferencia(Util.getDataHoraMinutoSegundoZerado(Util.recuperaDataUltimoDiaDoMes(competencia)));

            for (Pensionista pensionista : idsPensionistas) {

                Entidade entidade = getEntidade(pensionista, estudoAtuarial, auxiliar);
                String unidadeOrganizacionalLotacao = getUnidadeOrganizacionalLotacaoVigente(pensionista, estudoAtuarial, auxiliar);
                String remuneracaoContribuicao = getRemuneracaoContribuicao(pensionista, estudoAtuarial, auxiliar);
                String remuneracaoContribuicaoPrevidencia = getRemuneracaoContribuicaoPrevidencia(pensionista, estudoAtuarial, auxiliar);

                if (entidade == null) {
                    auxiliar.adicionarLog(AuxiliarAndamentoArquivoAtuarial.TipoLog.ALERTA, "Não foi possivel recuperar Entidade para o Pensionista " + getComplementoMensagemLog(pensionista));
                    auxiliar.incrementarContador();
                    continue;
                }

                String chaveCidade = entidade.getPessoaJuridica().getEnderecoPrincipal().getLocalidade() + "_" + entidade.getPessoaJuridica().getEnderecoPrincipal().getUf();
                Cidade cidade = cidadeCache.get(chaveCidade);
                if (cidade == null) {
                    cidade = cidadeFacade.recuperaCidadePorNomeEEstado(entidade.getPessoaJuridica().getEnderecoPrincipal().getLocalidade(), entidade.getPessoaJuridica().getEnderecoPrincipal().getUf());
                    if (cidade == null) {
                        auxiliar.adicionarLog(AuxiliarAndamentoArquivoAtuarial.TipoLog.ALERTA, "Não foi possivel recuperar Cidade para o Pensionista " + getComplementoMensagemLog(pensionista));
                        auxiliar.incrementarContador();
                        continue;
                    }
                    cidadeCache.put(chaveCidade, cidade);
                }

                String carteiraDeTrabalho = carteiraDeTrabalhoCache.get(pensionista.getId());
                if (carteiraDeTrabalho == null) {
                    carteiraDeTrabalho = getCarteiraDeTrabalho(pensionista.getPensao().getContratoFP());
                    carteiraDeTrabalhoCache.put(pensionista.getId(), carteiraDeTrabalho);
                }

                HSSFRow linha = auxiliar.getNovaLinha(planilhaPensionistasFalecidos);
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaPensionistasFalecidos.ANO_REFERENCIA.getIndex()).setCellValue(DataUtil.getAno(estudoAtuarial.getDataReferencia()));
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaPensionistasFalecidos.MES.getIndex()).setCellValue(DataUtil.getMes(estudoAtuarial.getDataReferencia()));
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaPensionistasFalecidos.CODIGO_DO_ENTE_NO_IBGE.getIndex()).setCellValue(cidade.getCodigoIBGE());
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaPensionistasFalecidos.NOME_DO_ENTE.getIndex()).setCellValue(entidade.getPessoaJuridica().getEnderecoPrincipal().getLocalidade());
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaPensionistasFalecidos.SIGLA_DA_UF_DO_ENTE.getIndex()).setCellValue(entidade.getPessoaJuridica().getEnderecoPrincipal().getUf());
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaPensionistasFalecidos.COMPOSICAO_DA_MASSA_A.getIndex()).setCellValue(1);
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaPensionistasFalecidos.COMPOSICAO_DA_MASSA_B.getIndex()).setCellValue(1);
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaPensionistasFalecidos.CNPJ.getIndex()).setCellValue(entidade.getPessoaJuridica().getCnpj());
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaPensionistasFalecidos.DENOMINACAO_DO_ORGAO_ENTIDADE.getIndex()).setCellValue(unidadeOrganizacionalLotacao);
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaPensionistasFalecidos.PODER.getIndex()).setCellValue(entidade.getEsferaDoPoder().getCodigoSiprev());
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaPensionistasFalecidos.TIPO.getIndex()).setCellValue(TipoAdministracao.DIRETA.equals(entidade.getTipoAdministracao()) ? 1 : 2);
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaPensionistasFalecidos.A1_IDENTIFICACAO_INSTITUIDOR_DA_PENSAO.getIndex()).setCellValue(instituidorAposentado(pensionista.getPensao().getContratoFP()));
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaPensionistasFalecidos.A2_IDENTIFICACAO_SEGURADO_INSTITUIDOR_PENSAO_MATRICULA.getIndex()).setCellValue(getMatriculaFP(pensionista.getPensao().getContratoFP()).getMatricula());
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaPensionistasFalecidos.A3_IDENTIFICACAO_SEGURADO_INSTITUIDOR_PENSAO_CPF.getIndex()).setCellValue(pensionista.getPensao().getContratoFP().getMatriculaFP().getPessoa().getCpf());
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaPensionistasFalecidos.A4_IDENTIFICACAO_SEGURADO_INSTITUIDOR_PENSAO_PIS_PASEP.getIndex()).setCellValue(carteiraDeTrabalho);
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaPensionistasFalecidos.A5_DATA_NASCIMENTO_INSTITUIDOR_PENSAO.getIndex()).setCellValue(pensionista.getPensao().getContratoFP().getMatriculaFP().getPessoa().getDataNascimentoFormatada());
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaPensionistasFalecidos.A6_DATA_FALESCIMENTO_INSTITUIDADE_PENSAO.getIndex()).setCellValue(dataFalecimentoInstituidorPensao(pensionista.getPensao().getContratoFP().getMatriculaFP().getPessoa(), pensionista.getPensao().getContratoFP()));
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaPensionistasFalecidos.B1_IDENTIFICACAO_PENSIONISTA_CPF.getIndex()).setCellValue(pensionista.getMatriculaFP().getPessoa().getCpf());
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaPensionistasFalecidos.B2_MATRICULA_PENSIONISTA.getIndex()).setCellValue(pensionista.getMatriculaFP().getMatricula());
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaPensionistasFalecidos.B3_SEXO_DO_PENSIONISTA.getIndex()).setCellValue(pensionista.getMatriculaFP().getPessoa().getSexo().getCodigoEstudoAtuarial());
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaPensionistasFalecidos.B4_DATA_DE_NASCIMENTO_PENSIONISTA.getIndex()).setCellValue(DataUtil.getDataFormatada(pensionista.getMatriculaFP().getPessoa().getDataNascimento()));
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaPensionistasFalecidos.B5_SITUACAO.getIndex()).setCellValue("12");
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaPensionistasFalecidos.B6_DATA_SITUACAO.getIndex()).setCellValue(getDataFalecimento(pensionista.getMatriculaFP().getPessoa()));
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaPensionistasFalecidos.B7_TIPO_RELACAO_PENSIONISTA_COM_SEGURADO_DO_INSTITUIDOR.getIndex()).setCellValue(pensionista.getTipoDependEstudoAtuarial() != null ? pensionista.getTipoDependEstudoAtuarial().getCodigoEstudoAtuarial() : "");
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaPensionistasFalecidos.DATA_DE_INICIO_DO_BENEFICIO_DE_PENSAO.getIndex()).setCellValue(pensionista.getInicioVigenciaFormatado());
                auxiliar.criarCelulaMonetario(linha, AuxiliarAndamentoArquivoAtuarial.ColunaPensionistasFalecidos.VALOR_MENSAL_DO_BENEFICIO_RECEBIDO_PELO_PENSIONISTA.getIndex()).setCellValue(remuneracaoContribuicao);
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaPensionistasFalecidos.VALOR_TOTAL_PENSAO.getIndex()).setCellValue(getValorPensaoInstituidor(pensionista, estudoAtuarial, auxiliar));
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaPensionistasFalecidos.VALOR_PERCENTUAL_QUOTA_RECEBIDA_PELO_PENSIONISTA.getIndex()).setCellValue(pensionista.getPercentual().multiply(BigDecimal.valueOf(100)).toString());
                auxiliar.criarCelulaMonetario(linha, AuxiliarAndamentoArquivoAtuarial.ColunaPensionistasFalecidos.CONTRIBUICAO_MENSAL.getIndex()).setCellValue(remuneracaoContribuicaoPrevidencia);
                auxiliar.criarCelulaMonetario(linha, AuxiliarAndamentoArquivoAtuarial.ColunaPensionistasFalecidos.VALOR_MENSAL_COMPENSACAO_PREVIDENCIARIA.getIndex()).setCellValue("0");
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaPensionistasFalecidos.IDENTIFICADOR_DE_PARIDADE_COM_SERVIDORES_ATIVOS.getIndex()).setCellValue(getParidadeInstituidor(pensionista.getPensao().getContratoFP()));
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaPensionistasFalecidos.CONDICAO_DO_PENSIONISTA.getIndex()).setCellValue(!pensionista.getListaInvalidez().isEmpty() ? 2 : 1);
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaPensionistasFalecidos.DURACAO_DO_BENEFICIO.getIndex()).setCellValue(getTipoBeneficioPensao(pensionista));
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaPensionistasFalecidos.TEMPO_DE_DURACAO_DO_BENEFICIO.getIndex()).setCellValue(tempoBeneficio(pensionista));
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaPensionistasFalecidos.PREVIDENCIA_COMPLEMENTAR.getIndex()).setCellValue("2");

                String tetoRemuneratorioReferenciaFP = "0";
                if (pensionista.getContratoFP().getCargo().getReferenciaFP() != null) {
                    ReferenciaFP referenciaFP = referenciaFPFacade.recuperar(pensionista.getContratoFP().getCargo().getReferenciaFP().getId());
                    if (referenciaFP.getValoresReferenciasFPs() != null && !referenciaFP.getValoresReferenciasFPs().isEmpty()) {
                        tetoRemuneratorioReferenciaFP = referenciaFP.getValoresReferenciasFPs().get(0).getValor().toString();
                    }
                }
                auxiliar.criarCelulaMonetario(linha, AuxiliarAndamentoArquivoAtuarial.ColunaPensionistasFalecidos.TETO_CONSTITUCIONAL_REMUNERATORIO_ESPECIFICO.getIndex()).setCellValue(tetoRemuneratorioReferenciaFP);
                auxiliar.incrementarContador();
            }
        }
    }

    private Integer getNumeroDeDependentesDoAposentado(Aposentadoria aposentadoria, EstudoAtuarial estudoAtuarial) {
        return dependenteFacade.getDependentesDe(getPessoaFisica(aposentadoria), estudoAtuarial.getDataReferencia()).size();
    }

    private boolean hasCondicaoAposentado(Aposentadoria aposentadoria) {
        return aposentadoria.getTipoAposentadoria().getCodigo().equals(TipoAposentadoria.INVALIDEZ);
    }

    private String getDataConcessao(VinculoFP vinculoFP) {
        return DataUtil.getDataFormatada(vinculoFP.getInicioVigencia());
    }

    private String getTipoAposentadoria(Aposentadoria aposentadoria) {
        TipoAposentadoria tipoAposentadoria = aposentadoria.getTipoAposentadoria();
        if (tipoAposentadoria.isInvalidez()) {
            return String.valueOf(AuxiliarAndamentoArquivoAtuarial.TipoAposentadoria.APOSENTADORIA_POR_INVALIDEZ.getCodigo());
        }
        if (tipoAposentadoria.isTempoContribuicao()) {
            return String.valueOf(AuxiliarAndamentoArquivoAtuarial.TipoAposentadoria.APOSENTADORIA_VOLUNTARIA_POR_TEMPO_CONTRIBUICAO_IDADE.getCodigo());
        }
        if (tipoAposentadoria.isPorIdade()) {
            return String.valueOf(AuxiliarAndamentoArquivoAtuarial.TipoAposentadoria.APOSENTADORIA_VOLUNTARIA_POR_IDADE.getCodigo());
        }
        if (tipoAposentadoria.isCompulsoria()) {
            return String.valueOf(AuxiliarAndamentoArquivoAtuarial.TipoAposentadoria.APOSENTADORIA_COMPULSORIA.getCodigo());
        }
        return "";
    }


    public void popularConteudoPlanilhaServidoresAtivos(HSSFSheet planilhaAtivos, AuxiliarAndamentoArquivoAtuarial auxiliar, EstudoAtuarial estudoAtuarial) {


        auxiliar.montarCabecalhoServidoresAtivos(planilhaAtivos);
        HashMap<Date, List<ContratoFP>> IdsServidoresPorMes = estudoAtuarial.getServidoresAtivosPorMes();
        Map<Date, List<ContratoFP>> IdsServidoresPorMesOrdenado = new TreeMap<>(IdsServidoresPorMes);

        Map<Long, String> tempoContribuicaoRGPSCache = new HashMap<>();
        Map<Long, String> tempoContribuicaoRPPSMunicipalCache = new HashMap<>();
        Map<Long, String> tempoContribuicaoRPPSEstadualCache = new HashMap<>();
        Map<Long, String> tempoContribuicaoRPPSFederalCache = new HashMap<>();
        Map<Long, Integer> hasProfessorCache = new HashMap<>();
        Map<Long, String> carteiraDeTrabalhoCache = new HashMap<>();
        Map<Long, Date> dataInicioPrimeiroContratoCache = new HashMap<>();
        Map<Long, String> carreiraAtualCache = new HashMap<>();
        Map<Long, Integer> lancamentoFPCache = new HashMap<>();
        Map<Long, String> dataInicialAbonoPermanenciaCache = new HashMap<>();
        Map<String, Cidade> cidadeCache = new HashMap<>();
        Map<Long, PessoaFisica> pessoaCache = new HashMap<>();
        Map<Long, ContratoFPCargo> contratoFPCargoCache = new HashMap<>();

        for (Map.Entry<Date, List<ContratoFP>> entry : IdsServidoresPorMesOrdenado.entrySet()) {
            List<ContratoFP> idsServidores = entry.getValue();
            Date competencia = entry.getKey();
            estudoAtuarial.setDataReferencia(Util.getDataHoraMinutoSegundoZerado(Util.recuperaDataUltimoDiaDoMes(competencia)));

            for(ContratoFP contrato: idsServidores){

                Entidade entidade = getEntidade(contrato, estudoAtuarial, auxiliar);
                String unidadeOrganizacionalLotacao = getUnidadeOrganizacionalLotacaoVigente(contrato, estudoAtuarial, auxiliar);
                String baseDeCalculoMensa = getBaseDeCalculoMensal(contrato, estudoAtuarial, auxiliar);
                String remuneracaoContribuicao = getRemuneracaoContribuicao(contrato, estudoAtuarial, auxiliar);
                String remuneracaoContribuicaoPrevidencia = getRemuneracaoContribuicaoPrevidencia(contrato, estudoAtuarial, auxiliar);
                String situacaoFuncional = getSituacaoFuncional(contrato, estudoAtuarial);
                String tipoVinculoServidorAtivo = getTipoVinculoServidorAtivo(contrato, estudoAtuarial);


                if (entidade == null) {
                    auxiliar.adicionarLog(AuxiliarAndamentoArquivoAtuarial.TipoLog.ALERTA, "Não foi possivel recuperar Entidade para o Servidor " + getComplementoMensagemLog(contrato));
                    auxiliar.incrementarContador();
                    continue;
                }

                String chaveCidade = entidade.getPessoaJuridica().getEnderecoPrincipal().getLocalidade() + "_" + entidade.getPessoaJuridica().getEnderecoPrincipal().getUf();
                Cidade cidade = cidadeCache.get(chaveCidade);
                if (cidade == null) {
                    cidade = cidadeFacade.recuperaCidadePorNomeEEstado(entidade.getPessoaJuridica().getEnderecoPrincipal().getLocalidade(), entidade.getPessoaJuridica().getEnderecoPrincipal().getUf());
                    if (cidade == null) {
                        auxiliar.adicionarLog(AuxiliarAndamentoArquivoAtuarial.TipoLog.ALERTA, "Não foi possivel recuperar Cidade para o Contrato " + getComplementoMensagemLog(contrato));
                        auxiliar.incrementarContador();
                        continue;
                    }
                    cidadeCache.put(chaveCidade, cidade);
                }

                String tempoContribuicaoRGPS = tempoContribuicaoRGPSCache.get(contrato.getId());
                if (tempoContribuicaoRGPS == null) {
                    tempoContribuicaoRGPS = getTempoContribuicaoAnteriorAdmissao(contrato, auxiliar, TipoRegimePrevidenciarioEstudoAtuarial.RGPS);
                    tempoContribuicaoRGPSCache.put(contrato.getId(), tempoContribuicaoRGPS);
                }
                String tempoContribuicaoRPPSMunicipal = tempoContribuicaoRPPSMunicipalCache.get(contrato.getId());
                if (tempoContribuicaoRPPSMunicipal == null) {
                    tempoContribuicaoRPPSMunicipal = getTempoContribuicaoAnteriorAdmissao(contrato, auxiliar, TipoRegimePrevidenciarioEstudoAtuarial.RPPS_ESFERA_MUNICIPAL);
                    tempoContribuicaoRPPSMunicipalCache.put(contrato.getId(), tempoContribuicaoRPPSMunicipal);
                }
                String tempoContribuicaoRPPSEstadual = tempoContribuicaoRPPSEstadualCache.get(contrato.getId());
                if (tempoContribuicaoRPPSEstadual == null) {
                    tempoContribuicaoRPPSEstadual = getTempoContribuicaoAnteriorAdmissao(contrato, auxiliar, TipoRegimePrevidenciarioEstudoAtuarial.RPPS_ESFERA_ESTADUAL);
                    tempoContribuicaoRPPSEstadualCache.put(contrato.getId(), tempoContribuicaoRPPSEstadual);
                }
                String tempoContribuicaoRPPSFederal = tempoContribuicaoRPPSFederalCache.get(contrato.getId());
                if (tempoContribuicaoRPPSFederal == null) {
                    tempoContribuicaoRPPSFederal = getTempoContribuicaoAnteriorAdmissao(contrato, auxiliar, TipoRegimePrevidenciarioEstudoAtuarial.RPPS_ESFERA_FEDERAL);
                    tempoContribuicaoRPPSFederalCache.put(contrato.getId(), tempoContribuicaoRPPSFederal);
                }

                Integer hasProfessor = hasProfessorCache.get(contrato.getId());
                if (hasProfessor == null) {
                    hasProfessor = (hasProfessor(contrato) ? 3 : 1);
                    hasProfessorCache.put(contrato.getId(), hasProfessor);
                }

                String carteiraDeTrabalho = carteiraDeTrabalhoCache.get(contrato.getId());
                if (carteiraDeTrabalho == null) {
                    carteiraDeTrabalho = getCarteiraDeTrabalho(contrato);
                    carteiraDeTrabalhoCache.put(contrato.getId(), carteiraDeTrabalho);
                }

                Date dataInicioPrimeiroContrato = dataInicioPrimeiroContratoCache.get(contrato.getId());
                if (dataInicioPrimeiroContrato == null) {
                    dataInicioPrimeiroContrato = contratoFPFacade.dataIncicioPrimeiroContratoPorMatriculaAndContrato(contrato.getMatriculaFP().getMatricula(), "1");
                    dataInicioPrimeiroContratoCache.put(contrato.getId(), dataInicioPrimeiroContrato);
                }

                String carreiraAtual = carreiraAtualCache.get(contrato.getId());
                if (carreiraAtual == null) {
                    carreiraAtual = getCarreiraAtual(contrato);
                    carreiraAtualCache.put(contrato.getId(), carreiraAtual);
                }

                String dataInicialAbonoPermanencia = dataInicialAbonoPermanenciaCache.get(contrato.getId());
                if (dataInicialAbonoPermanencia == null) {
                    dataInicialAbonoPermanencia = getDataInicialAbonoPermanencia(contrato) != null ? DataUtil.getDataFormatada(getDataInicialAbonoPermanencia(contrato)) : "";
                    dataInicialAbonoPermanenciaCache.put(contrato.getId(), dataInicialAbonoPermanencia);
                }

                Integer lancamentoFP = lancamentoFPCache.get(contrato.getId());
                if (lancamentoFP == null) {
                    lancamentoFP = getDataInicialAbonoPermanencia(contrato) != null ? 1 : 2;
                    lancamentoFPCache.put(contrato.getId(), lancamentoFP);
                }

                PessoaFisica pessoaFisica = pessoaCache.get(contrato.getId());
                if (pessoaFisica == null) {
                    pessoaFisica = getPessoaFisica(contrato);
                    pessoaCache.put(contrato.getId(), pessoaFisica);
                }

                ContratoFPCargo contratoFPCargo = contratoFPCargoCache.get(contrato.getId());
                if (contratoFPCargo == null) {
                    contratoFPCargo = getCargoVigente(contrato, estudoAtuarial);
                    contratoFPCargoCache.put(contrato.getId(), contratoFPCargo);
                }

                HSSFRow linha = auxiliar.getNovaLinha(planilhaAtivos);
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaServidoresAtivos.ANO_EXERCICIO.getIndex()).setCellValue(DataUtil.getAno(competencia));
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaServidoresAtivos.MES.getIndex()).setCellValue(DataUtil.getMes(competencia));
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaServidoresAtivos.CODIGO_DO_ENTE_NO_IBGE.getIndex()).setCellValue(cidade.getCodigoIBGE());
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaServidoresAtivos.NOME_DO_ENTE.getIndex()).setCellValue(entidade.getPessoaJuridica().getEnderecoPrincipal().getLocalidade());
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaServidoresAtivos.SIGLA_DA_UF_DO_ENTE.getIndex()).setCellValue(entidade.getPessoaJuridica().getEnderecoPrincipal().getUf());
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaServidoresAtivos.COMPOSICAO_DA_MASSA.getIndex()).setCellValue(1);
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaServidoresAtivos.TIPO_DE_FUNDO.getIndex()).setCellValue(1);
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaServidoresAtivos.CNPJ.getIndex()).setCellValue(entidade.getPessoaJuridica().getCnpj());
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaServidoresAtivos.NOME_ORGAO.getIndex()).setCellValue(unidadeOrganizacionalLotacao);
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaServidoresAtivos.CODIGO_PODER.getIndex()).setCellValue(entidade.getEsferaDoPoder().getCodigoSiprev());
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaServidoresAtivos.CODIGO_TIPO_PODER.getIndex()).setCellValue(getTipoAdministracao(entidade));
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaServidoresAtivos.CODIGO_TIPO_POPULACAO_COBERTA.getIndex()).setCellValue(1);
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaServidoresAtivos.CODIGO_TIPO_DE_CARGO.getIndex()).setCellValue(
                    String.valueOf(contrato.getCargo().getTipoEspecificacaoCargo() != null ? TipoEspecificacaoCargo.valueOf(contrato.getCargo().getTipoEspecificacaoCargo().name()).getCodigo() : TipoEspecificacaoCargo.DEMAIS_SERVIDORES.getCodigo()));
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaServidoresAtivos.CODIGO_CRITERIO_DE_ELEGIBILIDADE.getIndex()).setCellValue(hasProfessor);
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaServidoresAtivos.IDENTIFICACAO_DO_SERVIDOR.getIndex()).setCellValue(getMatriculaFP(contrato).getMatricula());

                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaServidoresAtivos.IDENTIFICACAO_DO_SERVIDOR_CPF.getIndex()).setCellValue(pessoaFisica.getCpf());
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaServidoresAtivos.IDENTIFICACAO_DO_SERVIDOR_PIS_PASEP.getIndex()).setCellValue(carteiraDeTrabalho);
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaServidoresAtivos.CODIGO_SEXO.getIndex()).setCellValue(pessoaFisica.getSexo().getCodigo());
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaServidoresAtivos.CODIGO_ESTADO_CIVIL.getIndex()).setCellValue(pessoaFisica.getEstadoCivil().getCodigoSiprev());
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaServidoresAtivos.DATA_DE_NASCIMENTO.getIndex()).setCellValue(DataUtil.getDataFormatada(getDataNascimento(contrato)));
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaServidoresAtivos.CODIGO_SITUACAO_FUNCIONAL.getIndex()).setCellValue(situacaoFuncional);
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaServidoresAtivos.CODIGO_TIPO_DE_VINCULO.getIndex()).setCellValue(tipoVinculoServidorAtivo);

                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaServidoresAtivos.DATA_DE_INGRESSO_NO_SERVICO_PUBLICO.getIndex()).setCellValue(dataInicioPrimeiroContrato != null ? DataUtil.getDataFormatada(dataInicioPrimeiroContrato) : "");
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaServidoresAtivos.DATA_DE_INGRESSO_NO_ENTE.getIndex()).setCellValue(DataUtil.getDataFormatada(contrato.getDataAdmissao()));
                EnquadramentoFuncional enquadramentoFuncional = getEnquadramentoFuncional(contrato, estudoAtuarial);
                if (enquadramentoFuncional != null && enquadramentoFuncional.getInicioVigencia() != null) {
                    auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaServidoresAtivos.DATA_DE_INGRESSO_NA_CARREIRA_ATUAL.getIndex()).setCellValue(DataUtil.getDataFormatada(enquadramentoFuncional.getInicioVigencia()));
                } else {
                    auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaServidoresAtivos.DATA_DE_INGRESSO_NA_CARREIRA_ATUAL.getIndex()).setCellValue("");
                }
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaServidoresAtivos.NOME_DA_CARREIRA_ATUAL.getIndex()).setCellValue(carreiraAtual);
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaServidoresAtivos.DATA_DE_INGRESSO_NO_CARGO_ATUAL.getIndex()).setCellValue(contratoFPCargo != null ? DataUtil.getDataFormatada(contratoFPCargo.getInicioVigencia()) : "");
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaServidoresAtivos.NOME_DO_CARGO_ATUAL.getIndex()).setCellValue(contratoFPCargo != null ? contratoFPCargo.getCargo().getDescricao() : "");
                auxiliar.criarCelulaMonetario(linha, AuxiliarAndamentoArquivoAtuarial.ColunaServidoresAtivos.BASE_DE_CALCULO_MENSAL_DO_SERVIDOR_ATIVO.getIndex()).setCellValue(baseDeCalculoMensa);
                auxiliar.criarCelulaMonetario(linha, AuxiliarAndamentoArquivoAtuarial.ColunaServidoresAtivos.REMUNERACAO_MENSAL_TOTAL_DO_SERVIDOR_ATIVO.getIndex()).setCellValue(remuneracaoContribuicao);
                auxiliar.criarCelulaMonetario(linha, AuxiliarAndamentoArquivoAtuarial.ColunaServidoresAtivos.CONTRIBUICAO_MENSAL.getIndex()).setCellValue(remuneracaoContribuicaoPrevidencia);

                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaServidoresAtivos.TEMPO_DE_CONTRIBUICAO_DO_SERVIDOR_PARA_RGPS_ANTERIOR_A_ADMISSAO_NO_ENTE.getIndex()).setCellValue(tempoContribuicaoRGPS);
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaServidoresAtivos.TEMPO_DE_CONTRIBUICAO_DO_SERVIDOR_PARA_OUTROS_RPPS_ANTERIOR_A_ADMISSAO_NO_ENTE_MUNICIPAL.getIndex()).setCellValue(tempoContribuicaoRPPSMunicipal);
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaServidoresAtivos.TEMPO_DE_CONTRIBUICAO_DO_SERVIDOR_PARA_OUTROS_RPPS_ANTERIOR_A_ADMISSAO_NO_ENTE_ESTADUAL.getIndex()).setCellValue(tempoContribuicaoRPPSEstadual);
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaServidoresAtivos.TEMPO_DE_CONTRIBUICAO_DO_SERVIDOR_PARA_OUTROS_RPPS_ANTERIOR_A_ADMISSAO_NO_ENTE_FEDERAL.getIndex()).setCellValue(tempoContribuicaoRPPSFederal);

                List<Dependente> dependentes = dependenteFacade.buscarDependentesVinculoFP(pessoaFisica, competencia);
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaServidoresAtivos.NUMERO_DE_DEPENDENTES_DO_SERVIDOR.getIndex()).setCellValue(dependentes.size());
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaServidoresAtivos.INDICADOR_RECEBIMENTO_ABONO_DE_PERMANENCIA.getIndex()).setCellValue(lancamentoFP);
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaServidoresAtivos.DATA_DE_INICIO_RECEBIMENTO_ABONO_DE_PERMANENCIA.getIndex()).setCellValue(dataInicialAbonoPermanencia);
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaServidoresAtivos.PREVIDENCIA_COMPLEMENTAR.getIndex()).setCellValue("2");


                String tetoRemuneratorioReferenciaFP = "0";
                if (contrato.getCargo().getReferenciaFP() != null) {
                    ReferenciaFP referenciaFP = referenciaFPFacade.recuperar(contrato.getCargo().getReferenciaFP().getId());
                    if (referenciaFP.getValoresReferenciasFPs() != null && !referenciaFP.getValoresReferenciasFPs().isEmpty()) {
                        tetoRemuneratorioReferenciaFP = referenciaFP.getValoresReferenciasFPs().get(0).getValor().toString();
                    }
                }
                auxiliar.criarCelulaMonetario(linha, AuxiliarAndamentoArquivoAtuarial.ColunaServidoresAtivos.TETO_CONSTITUCIONAL_REMUNERATORIO_ESPECIFICO.getIndex()).setCellValue(tetoRemuneratorioReferenciaFP);
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaServidoresAtivos.DATA_PROVAVEL_DE_APOSENTADORIA.getIndex()).setCellValue("");
                auxiliar.incrementarContador();
            }
        }

    }


    private String calcularTempoAnterior(PessoaFisica pf) {
        int quantidadeDias = 0;
        for (TempoContratoFPPessoa tempoContratoFPPessoa : pf.getItemTempoContratoFPPessoa()) {
            quantidadeDias += DataUtil.diferencaDiasInteira(tempoContratoFPPessoa.getInicio(), tempoContratoFPPessoa.getFim());
        }
        return Integer.toString(quantidadeDias);
    }

    public void popularConteudoPlanilhaAtivosFalecidosOuExonerados(HSSFSheet ativosFalecidosOuExonerados, AuxiliarAndamentoArquivoAtuarial auxiliar, EstudoAtuarial estudoAtuarial) {
        auxiliar.montarCabecalhoAtivosFalecidosOuExonerados(ativosFalecidosOuExonerados);
        HashMap<Date, List<ContratoFP>> IdsAtivosFalecidosExoneradosPorMes = estudoAtuarial.getServidoresAtivosFalecidosOuExonerados();
        Map<Date, List<ContratoFP>> IdsFalecidosExoneradosPorMesOrdenado = new TreeMap<>(IdsAtivosFalecidosExoneradosPorMes);

        Map<Long, String> tempoContribuicaoRGPSCache = new HashMap<>();
        Map<Long, String> tempoContribuicaoRPPSMunicipalCache = new HashMap<>();
        Map<Long, String> tempoContribuicaoRPPSEstadualCache = new HashMap<>();
        Map<Long, String> tempoContribuicaoRPPSFederalCache = new HashMap<>();
        Map<Long, Integer> hasProfessorCache = new HashMap<>();
        Map<Long, String> carteiraDeTrabalhoCache = new HashMap<>();
        Map<Long, String> carreiraAtualCache = new HashMap<>();
        Map<Long, Integer> lancamentoFPCache = new HashMap<>();
        Map<Long, String> dataInicialAbonoPermanenciaCache = new HashMap<>();
        Map<String, Cidade> cidadeCache = new HashMap<>();
        Map<Long, PessoaFisica> pessoaCache = new HashMap<>();
        Map<Long, ContratoFPCargo> contratoFPCargoCache = new HashMap<>();

        for (Map.Entry<Date, List<ContratoFP>> entry : IdsFalecidosExoneradosPorMesOrdenado.entrySet()) {
            List<ContratoFP> idsServidores = entry.getValue();
            Date competencia = entry.getKey();
            estudoAtuarial.setDataReferencia(Util.getDataHoraMinutoSegundoZerado(Util.recuperaDataUltimoDiaDoMes(competencia)));

            for (ContratoFP contrato : idsServidores) {

                Entidade entidade = getEntidade(contrato, estudoAtuarial, auxiliar);
                String unidadeOrganizacionalLotacao = getUnidadeOrganizacionalLotacaoVigente(contrato, estudoAtuarial, auxiliar);
                String baseDeCalculoMensa = getBaseDeCalculoMensal(contrato, estudoAtuarial, auxiliar);
                String remuneracaoContribuicao = getRemuneracaoContribuicao(contrato, estudoAtuarial, auxiliar);
                String remuneracaoContribuicaoPrevidencia = getRemuneracaoContribuicaoPrevidencia(contrato, estudoAtuarial, auxiliar);

                if (entidade == null) {
                    auxiliar.adicionarLog(AuxiliarAndamentoArquivoAtuarial.TipoLog.ALERTA, "Não foi possivel recuperar Entidade para o Servidor " + getComplementoMensagemLog(contrato));
                    auxiliar.incrementarContador();
                    continue;
                }

                String chaveCidade = entidade.getPessoaJuridica().getEnderecoPrincipal().getLocalidade() + "_" + entidade.getPessoaJuridica().getEnderecoPrincipal().getUf();
                Cidade cidade = cidadeCache.get(chaveCidade);
                if (cidade == null) {
                    cidade = cidadeFacade.recuperaCidadePorNomeEEstado(entidade.getPessoaJuridica().getEnderecoPrincipal().getLocalidade(), entidade.getPessoaJuridica().getEnderecoPrincipal().getUf());
                    if (cidade == null) {
                        auxiliar.adicionarLog(AuxiliarAndamentoArquivoAtuarial.TipoLog.ALERTA, "Não foi possivel recuperar Cidade para o Contrato " + getComplementoMensagemLog(contrato));
                        auxiliar.incrementarContador();
                        continue;
                    }
                    cidadeCache.put(chaveCidade, cidade);
                }

                String tempoContribuicaoRGPS = tempoContribuicaoRGPSCache.get(contrato.getId());
                if (tempoContribuicaoRGPS == null) {
                    tempoContribuicaoRGPS = getTempoContribuicaoAnteriorAdmissao(contrato, auxiliar, TipoRegimePrevidenciarioEstudoAtuarial.RGPS);
                    tempoContribuicaoRGPSCache.put(contrato.getId(), tempoContribuicaoRGPS);
                }
                String tempoContribuicaoRPPSMunicipal = tempoContribuicaoRPPSMunicipalCache.get(contrato.getId());
                if (tempoContribuicaoRPPSMunicipal == null) {
                    tempoContribuicaoRPPSMunicipal = getTempoContribuicaoAnteriorAdmissao(contrato, auxiliar, TipoRegimePrevidenciarioEstudoAtuarial.RPPS_ESFERA_MUNICIPAL);
                    tempoContribuicaoRPPSMunicipalCache.put(contrato.getId(), tempoContribuicaoRPPSMunicipal);
                }
                String tempoContribuicaoRPPSEstadual = tempoContribuicaoRPPSEstadualCache.get(contrato.getId());
                if (tempoContribuicaoRPPSEstadual == null) {
                    tempoContribuicaoRPPSEstadual = getTempoContribuicaoAnteriorAdmissao(contrato, auxiliar, TipoRegimePrevidenciarioEstudoAtuarial.RPPS_ESFERA_ESTADUAL);
                    tempoContribuicaoRPPSEstadualCache.put(contrato.getId(), tempoContribuicaoRPPSEstadual);
                }
                String tempoContribuicaoRPPSFederal = tempoContribuicaoRPPSFederalCache.get(contrato.getId());
                if (tempoContribuicaoRPPSFederal == null) {
                    tempoContribuicaoRPPSFederal = getTempoContribuicaoAnteriorAdmissao(contrato, auxiliar, TipoRegimePrevidenciarioEstudoAtuarial.RPPS_ESFERA_FEDERAL);
                    tempoContribuicaoRPPSFederalCache.put(contrato.getId(), tempoContribuicaoRPPSFederal);
                }

                Integer hasProfessor = hasProfessorCache.get(contrato.getId());
                if (hasProfessor == null) {
                    hasProfessor = (hasProfessor(contrato) ? 3 : 1);
                    hasProfessorCache.put(contrato.getId(), hasProfessor);
                }

                String carteiraDeTrabalho = carteiraDeTrabalhoCache.get(contrato.getId());
                if (carteiraDeTrabalho == null) {
                    carteiraDeTrabalho = getCarteiraDeTrabalho(contrato);
                    carteiraDeTrabalhoCache.put(contrato.getId(), carteiraDeTrabalho);
                }

                String carreiraAtual = carreiraAtualCache.get(contrato.getId());
                if (carreiraAtual == null) {
                    carreiraAtual = getCarreiraAtual(contrato);
                    carreiraAtualCache.put(contrato.getId(), carreiraAtual);
                }

                String dataInicialAbonoPermanencia = dataInicialAbonoPermanenciaCache.get(contrato.getId());
                if (dataInicialAbonoPermanencia == null) {
                    dataInicialAbonoPermanencia = getDataInicialAbonoPermanencia(contrato) != null ? DataUtil.getDataFormatada(getDataInicialAbonoPermanencia(contrato)) : "";
                    dataInicialAbonoPermanenciaCache.put(contrato.getId(), dataInicialAbonoPermanencia);
                }

                Integer lancamentoFP = lancamentoFPCache.get(contrato.getId());
                if (lancamentoFP == null) {
                    lancamentoFP = getDataInicialAbonoPermanencia(contrato) != null ? 1 : 2;
                    lancamentoFPCache.put(contrato.getId(), lancamentoFP);
                }

                PessoaFisica pessoaFisica = pessoaCache.get(contrato.getId());
                if (pessoaFisica == null) {
                    pessoaFisica = getPessoaFisica(contrato);
                    pessoaCache.put(contrato.getId(), pessoaFisica);
                }

                ContratoFPCargo contratoFPCargo = contratoFPCargoCache.get(contrato.getId());
                if (contratoFPCargo == null) {
                    contratoFPCargo = getCargoVigente(contrato, estudoAtuarial);
                    contratoFPCargoCache.put(contrato.getId(), contratoFPCargo);
                }

                HSSFRow linha = auxiliar.getNovaLinha(ativosFalecidosOuExonerados);
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAtivosFalecidosOuExonerados.ANO_REFERENCIA.getIndex()).setCellValue(DataUtil.getAno(estudoAtuarial.getDataReferencia()));
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAtivosFalecidosOuExonerados.MES.getIndex()).setCellValue(DataUtil.getMes(estudoAtuarial.getDataReferencia()));
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAtivosFalecidosOuExonerados.CODIGO_DO_ENTE_NO_IBGE.getIndex()).setCellValue(cidade.getCodigoIBGE());
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAtivosFalecidosOuExonerados.NOME_DO_ENTE.getIndex()).setCellValue(entidade.getPessoaJuridica().getEnderecoPrincipal().getLocalidade());
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAtivosFalecidosOuExonerados.SIGLA_DA_UF_DO_ENTE.getIndex()).setCellValue(entidade.getPessoaJuridica().getEnderecoPrincipal().getUf());
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAtivosFalecidosOuExonerados.COMPOSICAO_DA_MASSA_A.getIndex()).setCellValue(1);
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAtivosFalecidosOuExonerados.COMPOSICAO_DA_MASSA_B.getIndex()).setCellValue(1);
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAtivosFalecidosOuExonerados.CNPJ.getIndex()).setCellValue(entidade.getPessoaJuridica().getCnpj());
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAtivosFalecidosOuExonerados.DENOMINACAO_ORGAO_ENTIDADE.getIndex()).setCellValue(unidadeOrganizacionalLotacao);
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAtivosFalecidosOuExonerados.PODER.getIndex()).setCellValue(entidade.getEsferaDoPoder().getCodigoSiprev());
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAtivosFalecidosOuExonerados.TIPO.getIndex()).setCellValue(getTipoAdministracao(entidade));
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAtivosFalecidosOuExonerados.A_POPULACAO_COBERTA.getIndex()).setCellValue(1);
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAtivosFalecidosOuExonerados.B_ESPECIFICACAO_DO_TIPO_DE_CARGO.getIndex()).setCellValue(contrato.getCarreira().equals(TipoContagemEspecial.PROFESSOR.getCodigoAtuarial()) ? 2 : 3);
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAtivosFalecidosOuExonerados.C_CRITERIO_DE_ELEGIBILIDADE.getIndex()).setCellValue(hasProfessor);
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAtivosFalecidosOuExonerados.IDENTIFICACAO_DO_SEGURADO.getIndex()).setCellValue(getMatriculaFP(contrato).getMatricula());
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAtivosFalecidosOuExonerados.IDENTIFICACAO_DO_SEGURADO_CPF.getIndex()).setCellValue(pessoaFisica.getCpf());
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAtivosFalecidosOuExonerados.IDENTIFICACAO_DO_SEGURADO_PIS_PASEP.getIndex()).setCellValue(carteiraDeTrabalho);
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAtivosFalecidosOuExonerados.SEXO.getIndex()).setCellValue(pessoaFisica.getSexo().getCodigo());
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAtivosFalecidosOuExonerados.ESTADO_CIVIL.getIndex()).setCellValue(pessoaFisica.getEstadoCivil().getCodigoSiprev());
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAtivosFalecidosOuExonerados.DATA_DE_NASCIMENTO.getIndex()).setCellValue(DataUtil.getDataFormatada(getDataNascimento(contrato)));
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAtivosFalecidosOuExonerados.SITUACAO_FUNCIONAL.getIndex()).setCellValue(isObito(contrato.getMatriculaFP().getPessoa()) ? "13" : "12");
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAtivosFalecidosOuExonerados.DATA_SITUACAO_ATUAL.getIndex()).setCellValue(isObito(contrato.getMatriculaFP().getPessoa()) ? getDataFalecimento(contrato.getMatriculaFP().getPessoa()) : getDataExoneracao(contrato));
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAtivosFalecidosOuExonerados.TIPO_DE_VINCULO.getIndex()).setCellValue(getTipoVinculoServidorFalecidoOuExonerado(contrato, estudoAtuarial));

                AverbacaoTempoServico averbacaoTempoServico = averbacaoTempoServicoFacade.buscarPrimeiraAverbacaoTempoServicoPorContratoETipo(contrato, OrgaoEmpresa.ORGAO_PUBLICO);
                Date ingressoServicoPublico = (averbacaoTempoServico != null && averbacaoTempoServico.getInicioVigencia().before(contrato.getDataNomeacao())) ? averbacaoTempoServico.getInicioVigencia() : contrato.getDataNomeacao();

                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAtivosFalecidosOuExonerados.DATA_DE_INGRESSO_SERVICO_PUBLICO.getIndex()).setCellValue(DataUtil.getDataFormatada(ingressoServicoPublico));
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAtivosFalecidosOuExonerados.DATA_DE_INGRESSO_NO_ENTE.getIndex()).setCellValue(DataUtil.getDataFormatada(contrato.getDataAdmissao()));
                if (getEnquadramentoFuncional(contrato, estudoAtuarial) != null && getEnquadramentoFuncional(contrato, estudoAtuarial).getInicioVigencia() != null) {
                    auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAtivosFalecidosOuExonerados.DATA_DE_INGRESSO_NA_CARREIRA_ATUAL.getIndex()).setCellValue(DataUtil.getDataFormatada(getEnquadramentoFuncional(contrato, estudoAtuarial).getInicioVigencia()));
                } else {
                    auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAtivosFalecidosOuExonerados.DATA_DE_INGRESSO_NA_CARREIRA_ATUAL.getIndex()).setCellValue("");
                }
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAtivosFalecidosOuExonerados.IDENTIFICACAO_DA_CARREIRA_ATUAL.getIndex()).setCellValue(carreiraAtual);
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAtivosFalecidosOuExonerados.DATA_DE_INICIO_DE_EXERCICIO_NO_CARGO_ATUAL.getIndex()).setCellValue(contratoFPCargo != null ? DataUtil.getDataFormatada(contratoFPCargo.getInicioVigencia()) : "");
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAtivosFalecidosOuExonerados.IDENTIFICACAO_DO_CARGO_ATUAL.getIndex()).setCellValue(contratoFPCargo != null ? contratoFPCargo.getCargo().getDescricao() : "");
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAtivosFalecidosOuExonerados.BASE_DE_CALCULO_MENSAL_DO_SERVIDOR_ATIVO.getIndex()).setCellValue(baseDeCalculoMensa);
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAtivosFalecidosOuExonerados.REMUNERACAO_MENSAL_TOTAL_DO_SERVIDOR_ATIVO.getIndex()).setCellValue(remuneracaoContribuicao);
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAtivosFalecidosOuExonerados.CONTRIBUICAO_MENSAL.getIndex()).setCellValue(remuneracaoContribuicaoPrevidencia);
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAtivosFalecidosOuExonerados.TEMPO_DE_CONTRIBUICAO_DO_SERVIDOR_ATIVO_ANTERIOR_A_ADMISSAO_NO_ENTE_PARA_O_RGPS.getIndex()).setCellValue(tempoContribuicaoRGPS);
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAtivosFalecidosOuExonerados.TEMPO_DE_CONTRIBUICAO_DO_SERVIDOR_PARA_OUTROS_RPPS_ANTERIOR_A_ADMISSAO_NO_ENTE_MUNICIPAL.getIndex()).setCellValue(tempoContribuicaoRPPSMunicipal);
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAtivosFalecidosOuExonerados.TEMPO_DE_CONTRIBUICAO_DO_SERVIDOR_PARA_OUTROS_RPPS_ANTERIOR_A_ADMISSAO_NO_ENTE_ESTADUAL.getIndex()).setCellValue(tempoContribuicaoRPPSEstadual);
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAtivosFalecidosOuExonerados.TEMPO_DE_CONTRIBUICAO_DO_SERVIDOR_PARA_OUTROS_RPPS_ANTERIOR_A_ADMISSAO_NO_ENTE_FEDERAL.getIndex()).setCellValue(tempoContribuicaoRPPSFederal);
                List<Dependente> dependentes = dependenteFacade.buscarDependentesVinculoFP(pessoaFisica, estudoAtuarial.getDataReferencia());
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAtivosFalecidosOuExonerados.NUMERO_DE_DEPENDENTES_DO_SERVIDOR.getIndex()).setCellValue(dependentes.size());
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAtivosFalecidosOuExonerados.SEGURADO_EM_ABONO_DE_PERMANENCIA.getIndex()).setCellValue(lancamentoFP);
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAtivosFalecidosOuExonerados.DATA_DE_INICIO_DE_ABONO_DE_PERMANENCIA_DO_SERVIDOR_ATIVO.getIndex()).setCellValue(dataInicialAbonoPermanencia);
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAtivosFalecidosOuExonerados.PREVIDENCIA_COMPLEMENTAR.getIndex()).setCellValue("2");

                String tetoRemuneratorioReferenciaFP = "0";
                if (contrato.getCargo().getReferenciaFP() != null) {
                    ReferenciaFP referenciaFP = referenciaFPFacade.recuperar(contrato.getCargo().getReferenciaFP().getId());
                    if (referenciaFP.getValoresReferenciasFPs() != null && !referenciaFP.getValoresReferenciasFPs().isEmpty()) {
                        tetoRemuneratorioReferenciaFP = referenciaFP.getValoresReferenciasFPs().get(0).getValor().toString();
                    }
                }
                auxiliar.criarCelulaMonetario(linha, AuxiliarAndamentoArquivoAtuarial.ColunaAtivosFalecidosOuExonerados.TETO_CONSTITUCIONAL_REMUNERATORIO_ESPECIFICO.getIndex()).setCellValue(tetoRemuneratorioReferenciaFP);
                auxiliar.incrementarContador();
            }
        }
    }

    public void popularConteudoPlanilhaDependentes(HSSFSheet planilhaDependentes, AuxiliarAndamentoArquivoAtuarial auxiliar, EstudoAtuarial estudoAtuarial) {
        auxiliar.montarCabecalhoDependentes(planilhaDependentes);

        HashMap<Date, List<Dependente>> IdsDependentesPorMes = estudoAtuarial.getDependentePorMes();
        Map<Date, List<Dependente>> IdsDependentesPorMesOrdenado = new TreeMap<>(IdsDependentesPorMes);

        Map<Long, String> carteiraDeTrabalhoCache = new HashMap<>();
        Map<String, Cidade> cidadeCache = new HashMap<>();
        Map<Long, ContratoFP> contratoFPCache = new HashMap<>();

        for (Map.Entry<Date, List<Dependente>> entry : IdsDependentesPorMesOrdenado.entrySet()) {
            List<Dependente> idsDependentes = entry.getValue();
            Date competencia = entry.getKey();
            estudoAtuarial.setDataReferencia(Util.getDataHoraMinutoSegundoZerado(Util.recuperaDataUltimoDiaDoMes(competencia)));

            for (Dependente dependente : idsDependentes) {

                Long idContrato = Long.parseLong(contratoFPFacade.buscarIdVinculoFPPorPessoaFisica(dependente.getResponsavel(), estudoAtuarial.getDataReferencia()));

                ContratoFP contrato = contratoFPCache.get(idContrato);
                if (contrato == null) {
                    contrato = contratoFPFacade.recuperarParaArquivoAtuarial(idContrato);
                    contratoFPCache.put(contrato.getId(), contrato);
                }

                Entidade entidade = getEntidade(contrato, estudoAtuarial, auxiliar);
                String unidadeOrganizacionalLotacao = getUnidadeOrganizacionalLotacaoVigente(contrato, estudoAtuarial, auxiliar);

                if (entidade == null) {
                    auxiliar.adicionarLog(AuxiliarAndamentoArquivoAtuarial.TipoLog.ALERTA, "Não foi possivel recuperar Entidade para o Servidor " + getComplementoMensagemLog(contrato));
                    auxiliar.incrementarContador();
                    continue;
                }

                String chaveCidade = entidade.getPessoaJuridica().getEnderecoPrincipal().getLocalidade() + "_" + entidade.getPessoaJuridica().getEnderecoPrincipal().getUf();
                Cidade cidade = cidadeCache.get(chaveCidade);
                if (cidade == null) {
                    cidade = cidadeFacade.recuperaCidadePorNomeEEstado(entidade.getPessoaJuridica().getEnderecoPrincipal().getLocalidade(), entidade.getPessoaJuridica().getEnderecoPrincipal().getUf());
                    if (cidade == null) {
                        auxiliar.adicionarLog(AuxiliarAndamentoArquivoAtuarial.TipoLog.ALERTA, "Não foi possivel recuperar Cidade para o Contrato " + getComplementoMensagemLog(contrato));
                        auxiliar.incrementarContador();
                        continue;
                    }
                    cidadeCache.put(chaveCidade, cidade);
                }


                String carteiraDeTrabalho = carteiraDeTrabalhoCache.get(contrato.getId());
                if (carteiraDeTrabalho == null) {
                    carteiraDeTrabalho = getCarteiraDeTrabalho(contrato);
                    carteiraDeTrabalhoCache.put(contrato.getId(), carteiraDeTrabalho);
                }

                HSSFRow linha = auxiliar.getNovaLinha(planilhaDependentes);
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaDependentes.ANO_REFERENCIA.getIndex()).setCellValue(DataUtil.getAno(estudoAtuarial.getDataReferencia()));
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaDependentes.MES.getIndex()).setCellValue(DataUtil.getMes(estudoAtuarial.getDataReferencia()));
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaDependentes.CODIGO_DO_ENTE_NO_IBGE.getIndex()).setCellValue(cidade.getCodigoIBGE());
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaDependentes.NOME_DO_ENTE.getIndex()).setCellValue(entidade.getPessoaJuridica().getEnderecoPrincipal().getLocalidade());
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaDependentes.SIGLA_DA_UF_DO_ENTE.getIndex()).setCellValue(entidade.getPessoaJuridica().getEnderecoPrincipal().getUf());
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaDependentes.COMPOSICAO_DA_MASSA.getIndex()).setCellValue("1");
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaDependentes.TIPO_DE_FUNDO.getIndex()).setCellValue("1");
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaDependentes.CNPJ.getIndex()).setCellValue(entidade.getPessoaJuridica().getCnpj());
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaDependentes.NOME_DO_ORGAO_ENTIDADE.getIndex()).setCellValue(unidadeOrganizacionalLotacao);
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaDependentes.CODIGO_DO_PODER.getIndex()).setCellValue(entidade.getEsferaDoPoder().getCodigoSiprev());
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaDependentes.CODIGO_DO_TIPO_DE_PODER.getIndex()).setCellValue(getTipoAdministracao(entidade));
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaDependentes.MATRICULA_DO_SEGURADO_SERVIDOR.getIndex()).setCellValue(getMatriculaFP(contrato).getMatricula());
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaDependentes.CPF_DO_SEGURADO_SERVIDOR.getIndex()).setCellValue(dependente.getResponsavel().getCpf());
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaDependentes.PASEP_DO_SEGURADO_SERVIDOR.getIndex()).setCellValue(carteiraDeTrabalho);
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaDependentes.SEXO_DO_SEGURADO_SERVIDOR.getIndex()).setCellValue(getSexo(dependente.getResponsavel(), auxiliar));
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaDependentes.IDENTIFICADOR_UNICO_DO_DEPENDENTE.getIndex()).setCellValue(dependente.getId());
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaDependentes.CPF_DO_DEPENDENTE.getIndex()).setCellValue(dependente.getDependente().getCpf() != null ? dependente.getDependente().getCpf() : "");
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaDependentes.DATA_DE_NASCIMENTO_DO_DEPENDENTE.getIndex()).setCellValue(DataUtil.getDataFormatada(dependente.getDependente().getDataNascimento()));
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaDependentes.SEXO_DO_DEPENDENTE.getIndex()).setCellValue(getSexo(dependente.getDependente(), auxiliar));
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaDependentes.CONDICAO_DO_DEPENDENTE.getIndex()).setCellValue(getCondicaoDependente(dependente, estudoAtuarial));
                auxiliar.criarCelula(linha, AuxiliarAndamentoArquivoAtuarial.ColunaDependentes.TIPO_DEPENDENCIA_DEPENDENTE_COM_SEGURADO_SERVIDOR.getIndex()).setCellValue(recuperarTipoDependenteEstudoAtuarial(dependente.getId()));
                auxiliar.incrementarContador();
            }
        }
    }

    private String getCarreiraAtual(ContratoFP contratoFP) {
        return contratoFPFacade.recuperarDescricaoCarreiraContratoFP(contratoFP);
    }

    private EnquadramentoFuncional getEnquadramentoFuncional(ContratoFP contratoFP, EstudoAtuarial estudoAtuarial) {
        return enquadramentoFuncionalFacade.buscarEnquadramentoFuncionalPorData(contratoFP, estudoAtuarial.getDataReferencia());
    }

    private Boolean hasProfessor(ContratoFP contratoFP) {
        try {
            if (contratoFP.getCargo().getTipoContagemSIPREV().getCodigoAtuarial().equals(TipoContagemEspecial.PROFESSOR.getCodigoAtuarial())
                || contratoFP.getCargo().getTipoContagemSIPREV().getCodigoAtuarial().equals(TipoContagemEspecial.PROFESSOR_ENS_SUPERIOR.getCodigoAtuarial())) {
                return Boolean.TRUE;
            } else {
                return Boolean.FALSE;
            }
        } catch (NullPointerException np) {
            return Boolean.FALSE;
        }
    }

    private String getTipoDeDependenciaDependente(EstudoAtuarial estudoAtuarial, VinculoFP vinculoFP) {
        Dependente dependente = getDependenteMaisNovo(vinculoFP, estudoAtuarial.getDataReferencia());
        if (dependente != null && dependente.getGrauDeParentesco() != null && dependente.getGrauDeParentesco().getTipoParentesco() != null) {
            return dependente.getGrauDeParentesco().getTipoParentesco().getCodigoEstudoAtuarial();
        }
        return "";
    }

    private String getCondicaoDependenteMaisNovo(EstudoAtuarial estudoAtuarial, VinculoFP vinculoFP) {
        if (getDependenteMaisNovo(vinculoFP, estudoAtuarial.getDataReferencia()) != null
            && getDependenteMaisNovo(vinculoFP, estudoAtuarial.getDataReferencia()).getDependente().getDeficienteFisico() != null) {
            if (getDependenteMaisNovo(vinculoFP, estudoAtuarial.getDataReferencia()).getDependente().getDeficienteFisico()) {
                return "2";
            } else {
                return "1";
            }
        }
        return "";
    }

    private String getCondicaoDependenteInvalidoMaisNovo(EstudoAtuarial estudoAtuarial, VinculoFP vinculoFP) {
        if (getDependenteInvalidoMaisNovo(vinculoFP, estudoAtuarial.getDataReferencia()) != null
            && getDependenteInvalidoMaisNovo(vinculoFP, estudoAtuarial.getDataReferencia()).getDeficienteFisico() != null) {
            if (getDependenteInvalidoMaisNovo(vinculoFP, estudoAtuarial.getDataReferencia()).getDeficienteFisico()) {
                return "2";
            } else {
                return "1";
            }
        }
        return "";
    }

    private String getDataNascimentoDependenteInvalido(VinculoFP vinculoFP, EstudoAtuarial estudoAtuarial) {
        try {
            Date dataReferencia = DataUtil.getUltimoDiaAno(DataUtil.getAno(estudoAtuarial.getDataReferencia()) - 1);
            PessoaFisica dependenteInvalidoMaisNovo = getDependenteInvalidoMaisNovo(vinculoFP, dataReferencia);
            return dependenteInvalidoMaisNovo != null ? DataUtil.getDataFormatada(dependenteInvalidoMaisNovo.getDataNascimento()) : "";
        } catch (ExcecaoNegocioGenerica eng) {
            return "";
        }
    }

    private String getDataNascimentoDependenteValido(VinculoFP vinculoFP, EstudoAtuarial estudoAtuarial) {
        try {
            Date dataReferencia = DataUtil.getUltimoDiaAno(DataUtil.getAno(estudoAtuarial.getDataReferencia()) - 1);
            PessoaFisica dependenteValidoMaisNovo = null;
            if (getDependenteMaisNovo(vinculoFP, dataReferencia) != null) {
                dependenteValidoMaisNovo = getDependenteMaisNovo(vinculoFP, dataReferencia).getDependente();
            }
            return dependenteValidoMaisNovo != null ? DataUtil.getDataFormatada(dependenteValidoMaisNovo.getDataNascimento()) : "";
        } catch (ExcecaoNegocioGenerica eng) {
            return "";
        }
    }

    private PessoaFisica getDependenteInvalidoMaisNovo(VinculoFP vinculoFP, Date dataReferencia) {
        List<Dependente> dependentes = dependenteFacade.getDependentesDe(getPessoaFisica(vinculoFP), dataReferencia);
        List<PessoaFisica> invalidos = getDependentesInvalidos(dependentes);
        if (getDataNascimentoMaisNovo(dependentes) != null) {
            return getDataNascimentoMaisNovo(dependentes).getDependente();
        }
        return null;
    }

    private Dependente getDataNascimentoMaisNovo(List<Dependente> invalidos) {
        if (!CollectionUtils.isEmpty(invalidos)) {
            Collections.sort(invalidos, new Comparator<Dependente>() {
                @Override
                public int compare(Dependente pf1, Dependente pf2) {
                    if (pf2.getDependente().getDataNascimento() == null || pf1.getDependente().getDataNascimento() == null) {
                        return 0;
                    }
                    return pf2.getDependente().getDataNascimento().compareTo(pf1.getDependente().getDataNascimento());
                }
            });
            return invalidos.get(0);
        } else {
            return null;
        }
    }

    private Dependente getDependenteMaisNovo(VinculoFP vinculoFP, Date dataReferencia) {
        List<Dependente> dependentes = dependenteFacade.getDependentesDe(getPessoaFisica(vinculoFP), dataReferencia);
        return getDataNascimentoMaisNovo(dependentes);
    }

    private List<PessoaFisica> getDependentesInvalidos(List<Dependente> dependentes) {
        List<PessoaFisica> invalidos = Lists.newArrayList();
        for (Dependente dependente : dependentes) {
            if (dependente.getDependente().getDeficienteFisico() != null && dependente.getDependente().getDeficienteFisico() == Boolean.TRUE && dependente.getDependente().getDataNascimento() != null) {
                invalidos.add(dependente.getDependente());
            }
        }
        return invalidos;
    }

    private List<Dependente> getDependentes(VinculoFP vinculoFP, Date dataReferencia) {
        return dependenteFacade.getDependentesDe(getPessoaFisica(vinculoFP), dataReferencia);
    }

    private String getDataNascimentoConjugeOuCompanheiro(VinculoFP vinculoFP, AuxiliarAndamentoArquivoAtuarial auxiliar) {
        try {
            PessoaFisica pessoaFisica = getPessoaFisica(vinculoFP);
            if (pessoaFisica.temConjugeOrCompanheiro()) {
                CertidaoCasamento certidaoCasamento = getCertidaoCasamento(vinculoFP);
                Date dataNascimentoConjuge = getDataNascimentoConjuge(certidaoCasamento, vinculoFP);
                return DataUtil.getDataFormatada(dataNascimentoConjuge);
            }
        } catch (ExcecaoNegocioGenerica eng) {
            auxiliar.adicionarLog(AuxiliarAndamentoArquivoAtuarial.TipoLog.ALERTA, eng.getMessage());
        }
        return "";
    }

    private Date getDataNascimentoConjuge(CertidaoCasamento certidaoCasamento, VinculoFP vinculoFP) {
        try {
            return certidaoCasamento.getDataNascimentoConjuge();
        } catch (NullPointerException npe) {
            throw new ExcecaoNegocioGenerica("Não foi encontrada a data de nascimento do cônjuge do contrato " + getComplementoMensagemLog(vinculoFP));
        }
    }

    private CertidaoCasamento getCertidaoCasamento(VinculoFP vinculoFP) {
        CertidaoCasamento certidaoCasamento = documentoPessoalFacade.recuperaCertidaoCasamento(getPessoaFisica(vinculoFP));
        if (certidaoCasamento == null) {
            throw new ExcecaoNegocioGenerica("Não foi encontrado certidão de casamento do contrato " + getComplementoMensagemLog(vinculoFP));
        }
        return certidaoCasamento;
    }

    private String getCarteiraDeTrabalho(VinculoFP vinculoFP) {
        String pisPasep = documentoPessoalFacade.recuperaCarteiraTrabalhoAtuarial(getPessoaFisica(vinculoFP));
        if (pisPasep == null) {
            throw new ExcecaoNegocioGenerica("Não foi encontrado certidão de casamento do contrato " + getComplementoMensagemLog(vinculoFP));
        }
        return pisPasep;
    }

    private PessoaFisica getPessoaFisica(VinculoFP vinculoFP) {
        try {
            return getMatriculaFP(vinculoFP).getPessoa();
        } catch (NullPointerException npe) {
            throw new ExcecaoNegocioGenerica("Não foi encontrado a pessoa física da matrícula " + getMatriculaFP(vinculoFP));
        }
    }

    private MatriculaFP getMatriculaFP(VinculoFP vinculoFP) {
        try {
            return vinculoFP.getMatriculaFP();
        } catch (NullPointerException npe) {
            throw new ExcecaoNegocioGenerica("Não foi encontrado a matrícula do servidor " + vinculoFP.getId());
        }
    }


    private String getConjugeOuCompanheiro(VinculoFP vinculoFP, AuxiliarAndamentoArquivoAtuarial auxiliar) {
        try {
            PessoaFisica pessoaFisica = getPessoaFisica(vinculoFP);
            if (pessoaFisica.temConjugeOrCompanheiro()) {
                return AuxiliarAndamentoArquivoAtuarial.SimNao.SIM.getSigla();
            }
        } catch (ExcecaoNegocioGenerica eng) {
            auxiliar.adicionarLog(AuxiliarAndamentoArquivoAtuarial.TipoLog.ALERTA, eng.getMessage());
        }
        return AuxiliarAndamentoArquivoAtuarial.SimNao.NAO.getSigla();
    }

    private String getTempoContribuicaoAnteriorAdmissao(ContratoFP contrato, AuxiliarAndamentoArquivoAtuarial auxiliar, TipoRegimePrevidenciarioEstudoAtuarial tipoRegime) {
        int dias = 0;
        try {
            List<AverbacaoTempoServico> averbacoes = getAverbacaoTempoServicos(contrato, tipoRegime);
            if (!CollectionUtils.isEmpty(averbacoes)) {
                for (AverbacaoTempoServico averbacao : averbacoes) {
                    if (TipoRegimePrevidenciarioEstudoAtuarial.RGPS.equals(tipoRegime)) {
                        if (averbacao.getTipoRegPrevidenciarioEstAtua() == null) {
                            if (OrgaoEmpresa.EMPRESA_PRIVADA.equals(averbacao.getOrgaoEmpresa()) || OrgaoEmpresa.CONTRIBUICAO_INDIVIDUAL.equals(averbacao.getOrgaoEmpresa())) {
                                dias = dias + DataUtil.diasEntreDate(averbacao.getInicioVigencia(), averbacao.getFinalVigencia());
                            }
                        } else if (TipoRegimePrevidenciarioEstudoAtuarial.RGPS.equals(averbacao.getTipoRegPrevidenciarioEstAtua())) {
                            dias = dias + DataUtil.diasEntreDate(averbacao.getInicioVigencia(), averbacao.getFinalVigencia());
                        }
                    } else {
                        dias = dias + DataUtil.diasEntreDate(averbacao.getInicioVigencia(), averbacao.getFinalVigencia());
                    }
                }
            }
        } catch (ExcecaoNegocioGenerica eng) {
            auxiliar.adicionarLog(AuxiliarAndamentoArquivoAtuarial.TipoLog.ALERTA, eng.getMessage());
        }
        return String.valueOf(dias);
    }

    private List<AverbacaoTempoServico> getAverbacaoTempoServicos(ContratoFP contrato, TipoRegimePrevidenciarioEstudoAtuarial tipoRegime) {
        Date dataAdmissao = getDataAdmissao(contrato);
        if (TipoRegimePrevidenciarioEstudoAtuarial.RGPS.equals(tipoRegime)) {
            return averbacaoTempoServicoFacade.buscarAverbacaoPorContratoFPAndData(contrato, dataAdmissao, null);
        } else {
            return averbacaoTempoServicoFacade.buscarAverbacaoPorContratoFPAndTipoRegimePrevidenciario(contrato, dataAdmissao, tipoRegime);
        }
    }

    private Date getDataAdmissao(ContratoFP contrato) {
        try {
            return contrato.getDataAdmissao();
        } catch (NullPointerException npe) {
            throw new ExcecaoNegocioGenerica("Não foi encontrado data de admissão para o contrato " + getComplementoMensagemLog(contrato));
        }
    }

    private String getRemuneracaoContribuicao(VinculoFP vinculoFP, EstudoAtuarial estudoAtuarial, AuxiliarAndamentoArquivoAtuarial auxiliar) {
        BigDecimal remuneracao = new BigDecimal(0);
        try {
            Long idFicha = getFichaFinanceiraFP(vinculoFP, estudoAtuarial);
            FichaFinanceiraFP fichaFinanceiraFP = fichaFinanceiraFPFacade.recuperar(idFicha);
            for (ItemFichaFinanceiraFP itemFicha : fichaFinanceiraFP.getItemFichaFinanceiraFP()) {
                if (itemFicha.isTipoEventoVantagem()) {
                    remuneracao = remuneracao.add(itemFicha.getValor());
                }
            }
        } catch (ExcecaoNegocioGenerica eng) {
            auxiliar.adicionarLog(AuxiliarAndamentoArquivoAtuarial.TipoLog.ALERTA, eng.getMessage());
        }
        return remuneracao.toString();
    }
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 5)
    private String getBaseDeCalculoMensal(VinculoFP vinculoFP, EstudoAtuarial estudoAtuarial, AuxiliarAndamentoArquivoAtuarial auxiliar) {
        BigDecimal base = new BigDecimal(0);
        try {
            int mes = DataUtil.getMes(estudoAtuarial.getDataReferencia());
            int ano = DataUtil.getAno(estudoAtuarial.getDataReferencia());
            boolean existePrevidencia = previdenciaVinculoFPFacade.existePrevidenciaVinculoFPIsentoPorContratoFPData(vinculoFP.getContratoFP(), estudoAtuarial.getDataReferencia());
            if (!existePrevidencia) {
                if (isRPPS(vinculoFP)) {
                    base = fichaFinanceiraFPFacade.buscarValorBaseDeCalculoMensalDoServidor(BASE_RPPS, mes, ano, TipoFolhaDePagamento.NORMAL, vinculoFP);
                } else {
                    base = fichaFinanceiraFPFacade.buscarValorBaseDeCalculoMensalDoServidor(BASE_INSS, mes, ano, TipoFolhaDePagamento.NORMAL, vinculoFP);
                }
            }
        } catch (ExcecaoNegocioGenerica eng) {
            auxiliar.adicionarLog(AuxiliarAndamentoArquivoAtuarial.TipoLog.ALERTA, eng.getMessage());
        }
        return base.toString();
    }

    private boolean isRPPS(VinculoFP vinculoFP) {
        return vinculoFP.getContratoFP().getPrevidenciaVinculoFPVigente().getTipoPrevidenciaFP().getCodigo() == 3;
    }

    private String getRemuneracaoContribuicaoPrevidencia(VinculoFP vinculoFP, EstudoAtuarial estudoAtuarial, AuxiliarAndamentoArquivoAtuarial auxiliar) {
        BigDecimal remuneracao = new BigDecimal(0);
        try {
            Long idFicha  = getFichaFinanceiraFP(vinculoFP, estudoAtuarial);
            FichaFinanceiraFP fichaFinanceiraFP = fichaFinanceiraFPFacade.recuperar(idFicha);
            for (ItemFichaFinanceiraFP itemFicha : fichaFinanceiraFP.getItemFichaFinanceiraFP()) {
                if (CODIGO_RPPS.equals(itemFicha.getEventoFP().getCodigo())) {
                    remuneracao = remuneracao.add(itemFicha.getValor());
                }
            }
        } catch (ExcecaoNegocioGenerica eng) {
            auxiliar.adicionarLog(AuxiliarAndamentoArquivoAtuarial.TipoLog.ALERTA, eng.getMessage());
        }
        return remuneracao.toString();
    }

    private Boolean hasSeguradoComplentouRequisitosAposentadoria(VinculoFP vinculoFP, EstudoAtuarial estudoAtuarial) {
        Boolean retorno = Boolean.FALSE;
        try {
            Long idFicha = getFichaFinanceiraFP(vinculoFP, estudoAtuarial);
            FichaFinanceiraFP fichaFinanceiraFP = fichaFinanceiraFPFacade.recuperar(idFicha);
            for (ItemFichaFinanceiraFP itemFicha : fichaFinanceiraFP.getItemFichaFinanceiraFP()) {
                if (CODIGO_ABONO_PERMANENCIA.equals(itemFicha.getEventoFP().getCodigo())) {
                    retorno = Boolean.TRUE;
                }
                break;
            }
        } catch (ExcecaoNegocioGenerica eng) {
            retorno = Boolean.FALSE;
        }
        return retorno;
    }

    private Date getDataInicialAbonoPermanencia(ContratoFP contratoFP) {
        return lancamentoFPFacade.getLancamentoFPPorCodigoEventoFP(contratoFP, CODIGO_ABONO_PERMANENCIA);
    }

    private Long getFichaFinanceiraFP(VinculoFP vinculoFP, EstudoAtuarial estudoAtuarial) {
        Long fichaFinanceiraFP = fichaFinanceiraFPFacade.recuperaFichaFinanceiraFPPorVinculoFPMesAnoTipoFolhaAtuarial(vinculoFP, DataUtil.getMes(estudoAtuarial.getDataReferencia()), DataUtil.getAno(estudoAtuarial.getDataReferencia()), TipoFolhaDePagamento.NORMAL);
        if (fichaFinanceiraFP == null) {
            throw new ExcecaoNegocioGenerica("Não foi encontrado ficha financeira mês: " + DataUtil.getMes(estudoAtuarial.getDataReferencia()) + ", ano: " + DataUtil.getAno(estudoAtuarial.getDataReferencia()) + " para o contrato " + getComplementoMensagemLog(vinculoFP));
        }
        return fichaFinanceiraFP;
    }

    private String getDataPosseCargoEfetivoAtual(ContratoFP contrato, AuxiliarAndamentoArquivoAtuarial auxiliar) {
        try {
            Date dataPosse = getDataPosse(contrato);
            return DataUtil.getDataFormatada(dataPosse);
        } catch (ExcecaoNegocioGenerica eng) {
            auxiliar.adicionarLog(AuxiliarAndamentoArquivoAtuarial.TipoLog.ALERTA, eng.getMessage());
        }
        return "";
    }

    private Date getDataPosse(ContratoFP contrato) {
        try {
            return contrato.getDataPosse();
        } catch (NullPointerException npe) {
            throw new ExcecaoNegocioGenerica("Não foi encontrado data de posse do contrato " + getComplementoMensagemLog(contrato));
        }
    }

    private String getDataAdmissaoEntrePublico(VinculoFP vinculoFP) {
        return DataUtil.getDataFormatada(vinculoFP.getInicioVigencia());
    }

    private String getDataNascimento(VinculoFP vinculoFP, AuxiliarAndamentoArquivoAtuarial auxiliar) {
        try {
            return DataUtil.getDataFormatada(getDataNascimento(vinculoFP));
        } catch (ExcecaoNegocioGenerica eng) {
            auxiliar.adicionarLog(AuxiliarAndamentoArquivoAtuarial.TipoLog.ALERTA, eng.getMessage());
        }
        return "";
    }

    private Date getDataNascimento(VinculoFP vinculoFP) {
        PessoaFisica pessoaFisica = getPessoaFisica(vinculoFP);
        Date dataNascimento = pessoaFisica.getDataNascimento();
        if (dataNascimento == null) {
            throw new ExcecaoNegocioGenerica("Não foi encontrado data de nascimento do contrato " + getComplementoMensagemLog(vinculoFP));
        }
        return dataNascimento;
    }

    private String getSexo(PessoaFisica pessoaFisica, AuxiliarAndamentoArquivoAtuarial auxiliar) {
        try {
            if (pessoaFisica.getSexo() != null) {
                return pessoaFisica.getSexo().getCodigoEstudoAtuarial();
            }
        } catch (ExcecaoNegocioGenerica eng) {
            auxiliar.adicionarLog(AuxiliarAndamentoArquivoAtuarial.TipoLog.ALERTA, eng.getMessage());
        }
        return "";
    }

    private String getConcursado(ContratoFP contrato) {
        if (contrato.isModalidadeContratoFPConcursados()) {
            return AuxiliarAndamentoArquivoAtuarial.SimNao.SIM.getSigla();
        }
        return AuxiliarAndamentoArquivoAtuarial.SimNao.NAO.getSigla();
    }

    public String getUnidadeOrganizacionalLotacaoVigente(VinculoFP vinculoFP, EstudoAtuarial estudoAtuarial, AuxiliarAndamentoArquivoAtuarial auxiliar) {
        try {
            Date dataReferencia = DataUtil.getUltimoDiaAno(DataUtil.getAno(estudoAtuarial.getDataReferencia()) - 1);
            LotacaoFuncional lotacaoFuncionalVigente = getLotacaoFuncionalVigente(vinculoFP, dataReferencia);

            HierarquiaOrganizacional ho = getHierarquiaDoMapa(auxiliar, lotacaoFuncionalVigente.getUnidadeOrganizacional());
            if (ho == null) {
                ho = getHierarquiaOrganizacionalPorUnidade(lotacaoFuncionalVigente);

                ArrayList<UnidadeOrganizacional> unidadeOrganizacionals = Lists.<UnidadeOrganizacional>newArrayList();
                unidadeOrganizacionals.add(ho.getSubordinada());

                if (auxiliar.getMapHierarquia().containsKey(ho)) {
                    auxiliar.getMapHierarquia().get(ho).addAll(unidadeOrganizacionals);
                } else {
                    auxiliar.getMapHierarquia().put(ho, unidadeOrganizacionals);
                }
            }
            return ho.getCodigo() + " - " + lotacaoFuncionalVigente.getUnidadeOrganizacional().getDescricao();
        } catch (ExcecaoNegocioGenerica eng) {
            auxiliar.adicionarLog(AuxiliarAndamentoArquivoAtuarial.TipoLog.ALERTA, eng.getMessage());
        }
        return "";
    }

    public Entidade getEntidade(VinculoFP vinculoFP, EstudoAtuarial estudoAtuarial, AuxiliarAndamentoArquivoAtuarial auxiliar) {
        Entidade entidade = null;
        try {
            Date dataReferencia = DataUtil.getUltimoDiaAno(DataUtil.getAno(estudoAtuarial.getDataReferencia()) - 1);
            LotacaoFuncional lotacaoFuncionalVigente = getLotacaoFuncionalVigente(vinculoFP, dataReferencia);

            entidade = getEntidadeDoMapa(auxiliar, lotacaoFuncionalVigente.getUnidadeOrganizacional());

            if (entidade == null) {
                HierarquiaOrganizacional ho = getHierarquiaOrganizacionalPorUnidade(lotacaoFuncionalVigente);
                entidade = entidadeFacade.buscarEntidadePorUnidadeAdministrativa(ho.getSubordinada().getId(), estudoAtuarial.getDataReferencia());

                ArrayList<UnidadeOrganizacional> unidadeOrganizacionals = Lists.<UnidadeOrganizacional>newArrayList();
                unidadeOrganizacionals.add(ho.getSubordinada());

                if (auxiliar.getMapEntidade().containsKey(entidade)) {
                    auxiliar.getMapEntidade().get(entidade).addAll(unidadeOrganizacionals);
                } else {
                    auxiliar.getMapEntidade().put(entidade, unidadeOrganizacionals);
                }
            }
            return entidade;
        } catch (ExcecaoNegocioGenerica eng) {
            auxiliar.adicionarLog(AuxiliarAndamentoArquivoAtuarial.TipoLog.ALERTA, eng.getMessage());
        }
        return entidade;
    }

    private Entidade getEntidadeDoMapa(AuxiliarAndamentoArquivoAtuarial auxiliar, UnidadeOrganizacional uo) {
        for (Map.Entry<Entidade, List<UnidadeOrganizacional>> entidadeListEntry : auxiliar.getMapEntidade().entrySet()) {
            for (UnidadeOrganizacional unidadeOrganizacional : entidadeListEntry.getValue()) {
                if (unidadeOrganizacional.getId().equals(uo.getId())) {
                    return entidadeListEntry.getKey();
                }
            }
        }
        return null;
    }

    private HierarquiaOrganizacional getHierarquiaDoMapa(AuxiliarAndamentoArquivoAtuarial auxiliar, UnidadeOrganizacional uo) {
        for (Map.Entry<HierarquiaOrganizacional, List<UnidadeOrganizacional>> hierarquiaOrganizacionalListEntry : auxiliar.getMapHierarquia().entrySet()) {
            for (UnidadeOrganizacional unidadeOrganizacional : hierarquiaOrganizacionalListEntry.getValue()) {
                if (unidadeOrganizacional.getId().equals(uo.getId())) {
                    return hierarquiaOrganizacionalListEntry.getKey();
                }
            }
        }
        return null;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacionalPorUnidade(LotacaoFuncional lotacaoFuncionalVigente) {
        HierarquiaOrganizacional hierarquiaOrganizacional = hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalPorUnidade(lotacaoFuncionalVigente.getUnidadeOrganizacional(), TipoHierarquiaOrganizacional.ADMINISTRATIVA);
        if (hierarquiaOrganizacional == null) {
            throw new ExcecaoNegocioGenerica("Não foi encontrado hierarquia organizacional administrativa da unidade " + lotacaoFuncionalVigente.getUnidadeOrganizacional());
        }
        return hierarquiaOrganizacional;
    }

    public LotacaoFuncional getLotacaoFuncionalVigente(VinculoFP vinculoFP, Date dataReferencia) {
        LotacaoFuncional lotacaoFuncionalVigente = vinculoFP.getLotacaoFuncionalVigente();
        if (lotacaoFuncionalVigente == null) {
            throw new ExcecaoNegocioGenerica("Não foi encontado lotação funcional vigente na data " + DataUtil.getDataFormatada(dataReferencia) + " para o contrato " + getComplementoMensagemLog(vinculoFP));
        }
        return lotacaoFuncionalVigente;
    }

    private String getMatricula(VinculoFP vinculoFP, AuxiliarAndamentoArquivoAtuarial auxiliar) {
        try {
            return getMatriculaFP(vinculoFP).getMatricula();
        } catch (ExcecaoNegocioGenerica eng) {
            auxiliar.adicionarLog(AuxiliarAndamentoArquivoAtuarial.TipoLog.ALERTA, eng.getMessage());
        }
        return "";
    }

    private String getTipoVinculo(ContratoFP contrato) {
        if (contrato.isModalidadeContratoFPCargoEletivo()) {
            return String.valueOf(AuxiliarAndamentoArquivoAtuarial.TipoVinculo.EFETIVO.getCodigo());
        }
        if (contrato.isModalidadeContratoFPCargoComissao()) {
            return String.valueOf(AuxiliarAndamentoArquivoAtuarial.TipoVinculo.EXCLUSIVAMENTE_COMISSIONADO.getCodigo());
        }
        if (contrato.isModalidadeContratoFPConcursados()) {
            if (contrato.isTipoRegimeCeletista()) {
                return String.valueOf(AuxiliarAndamentoArquivoAtuarial.TipoVinculo.CELETISTA.getCodigo());
            } else {
                return String.valueOf(AuxiliarAndamentoArquivoAtuarial.TipoVinculo.ESTATUTARIO.getCodigo());
            }
        }
        return "";
    }

    public String instituidorAposentado(ContratoFP contratoFP) {
        return aposentadoriaFacade.isAposentado(contratoFP) ? "2" : "1";
    }

    public String dataFalecimentoInstituidorPensao(PessoaFisica pessoaFisica, ContratoFP contratoFP) {
        String resultado = "";
        RegistroDeObito registroDeObito = registroDeObitoFacade.buscarRegistroDeObitoPorPessoaFisica(pessoaFisica);
        Aposentadoria aposentadoria = aposentadoriaFacade.recuperaAposentadoriaPorContratoFP(contratoFP);
        if (registroDeObito != null) {
            resultado = DataUtil.getDataFormatada(registroDeObito.getDataFalecimento(), "dd-MM-yyyy");
        } else if (aposentadoria != null && aposentadoria.getFinalVigencia() != null) {
            resultado = DataUtil.getDataFormatada(aposentadoria.getFinalVigencia(), "dd-MM-yyyy");
        }
        return resultado;
    }

    public String getDataFalecimento(PessoaFisica pessoaFisica) {
        String resultado = "";
        RegistroDeObito registroDeObito = registroDeObitoFacade.buscarRegistroDeObitoPorPessoaFisica(pessoaFisica);
        if (registroDeObito != null) {
            resultado = DataUtil.getDataFormatada(registroDeObito.getDataFalecimento(), "dd-MM-yyyy");
        }
        return resultado;
    }

    public String getValorPensaoInstituidor(Pensionista pensionista, EstudoAtuarial estudoAtuarial, AuxiliarAndamentoArquivoAtuarial auxiliar) {
        BigDecimal remuneracao = new BigDecimal(0);
        try {
            Pensao pensao = pensaoFacade.recuperar(pensionista.getPensao().getId());
            for (Pensionista pensi : pensao.getListaDePensionistas()) {
                remuneracao = remuneracao.add(new BigDecimal(getRemuneracaoContribuicao(pensi, estudoAtuarial, auxiliar)));
            }
        } catch (ExcecaoNegocioGenerica eng) {
            auxiliar.adicionarLog(AuxiliarAndamentoArquivoAtuarial.TipoLog.ALERTA, eng.getMessage());
        }
        return remuneracao.toString();
    }

    public String tempoBeneficio(Pensionista pensionista) {
        String resultado = "0";
        if (TipoPensao.TEMPORARIA.equals(pensionista.getTipoPensao()) || TipoPensao.TEMPORARIA_INVALIDEZ.equals(pensionista.getTipoPensao())) {
            if (pensionista.getFinalVigencia() != null) {
                resultado = DataUtil.getDiferencaAnosMesesDias(pensionista.getInicioVigencia(), pensionista.getFinalVigencia()).getYears() + "";
            }
        }
        return resultado;
    }

    public String getCondicaoDependente(Dependente dependente, EstudoAtuarial estudoAtuarial) {
        String resultado = "1";
        if (dependente.getDependente().getPessoaFisicaCid() != null) {
            Date finalVigencia = dependente.getDependente().getPessoaFisicaCid().getFinalVigencia();
            Date inicioVigenca = dependente.getDependente().getPessoaFisicaCid().getInicioVigencia();
            if (DataUtil.isVigente(inicioVigenca, finalVigencia, estudoAtuarial.getDataReferencia())) {
                resultado = "2";
            }
        }
        return resultado;
    }

    public ContratoFPCargo getCargoVigente(ContratoFP contratoFP, EstudoAtuarial estudoAtuarial) {
        ContratoFPCargo cargo = null;
        List<ContratoFPCargo> contratoFPCargos = contratoFP.getCargos();
        if (contratoFPCargos != null) {
            for (ContratoFPCargo contratoFPCargo : contratoFPCargos) {
                if (DataUtil.isVigente(contratoFPCargo.getInicioVigencia(), contratoFPCargo.getFimVigencia(), estudoAtuarial.getDataReferencia())) {
                    if (cargo == null) {
                        cargo = contratoFPCargo;
                    } else if (contratoFPCargo.getId() > cargo.getId()) {
                        cargo = contratoFPCargo;
                    }
                }
            }
        }
        return cargo;
    }

    public boolean isObito(PessoaFisica pessoaFisica) {
        return registroDeObitoFacade.existeRegistroDeObitoPorPessoaFisica(pessoaFisica);
    }

    public String getDataExoneracao(ContratoFP contratoFP) {
        String resultado = "";
        ExoneracaoRescisao exoneracaoRescisao = exoneracaoRescisaoFacade.recuperaExoneracaoRecisao(contratoFP);
        if (exoneracaoRescisao != null) {
            resultado = DataUtil.getDataFormatada(exoneracaoRescisao.getDataRescisao(), "dd-MM-yyyy");
        }
        return resultado;
    }

    public String getSituacaoFuncional(ContratoFP contratoFP, EstudoAtuarial estudoAtuarial) {
        String resultado = "";
        Object[] cedenciaResult = cedenciaContratoFPFacade.recuperaCedenciaVigentePorContratoFPAtuarial(contratoFP, estudoAtuarial.getDataReferencia());
        Afastamento afastamento = afastamentoFacade.buscarAfastamentoVigenteDataReferencia(contratoFP, estudoAtuarial.getDataReferencia());
        TipoCedenciaContratoFP tipoContratoCedenciaFP = null;
        FinalidadeCedenciaContratoFP finalidadeCedenciaContratoFP = null;

        if (cedenciaResult != null) {
            tipoContratoCedenciaFP = (TipoCedenciaContratoFP) cedenciaResult[0];
            finalidadeCedenciaContratoFP = (FinalidadeCedenciaContratoFP) cedenciaResult[1];
        }

        if (tipoContratoCedenciaFP == null && afastamento == null) {
            resultado = "1";
        } else if (afastamento != null) {
            switch (afastamento.getTipoAfastamento().getTipoAfastamentoESocial()) {
                case CARCERE:
                    resultado = "10";
                    break;
                case MANDATO_ELEITORAL_COM_REMUNERACAO:
                case MANDATO_ELEITORAL_SEM_REMUNERACAO:
                    resultado = "9";
                    break;
                case SERVICO_PUBLICO:
                    resultado = "8";
                    break;
                case AFASTAMENTO_LICENCA_SEM_REMUNERACAO:
                case LICENCA_NAO_REMUNERADA:
                    resultado = "3";
                    break;
                case AFASTAMENTO_LICENCA_REGIME_PROPRIO_COM_REMUNERACAO:
                case CARGO_ELETIVO_COLESTISTAS:
                case CARGO_ELETIVO:
                case LICENCA_REMUNERADA:
                case MULHER_VITIMA_VIOLENCIA:
                    resultado = "2";
                    break;
                case ACIDENTE_DOENCA_TRABALHO:
                case ACIDENTE_DOENCA_NAO_RELACIONADA_TRABALHO:
                case LICENCA_MATERNIDADE_120:
                case LICENCA_MATERNIDADE_121_180:
                case LICENCA_MATERNIDADE_ABORTO_NAO_CRIMINOSO:
                case LICENCA_MATERNIDADE:
                case LICENCA_MATERNINADADE_180:
                    resultado = "1";
                    break;
                default:
                    resultado = "11";
                    break;
            }
        } else if (FinalidadeCedenciaContratoFP.INTERNA.equals(finalidadeCedenciaContratoFP)) {
            switch (tipoContratoCedenciaFP) {
                case SEM_ONUS:
                    resultado = "7";
                    break;
                case COM_ONUS:
                    resultado = "6";
                    break;
            }
        } else if (FinalidadeCedenciaContratoFP.EXTERNA.equals(finalidadeCedenciaContratoFP)) {
            switch (tipoContratoCedenciaFP) {
                case SEM_ONUS:
                    resultado = "5";
                    break;
                case COM_ONUS:
                    resultado = "4";
                    break;
            }
        }
        return resultado;
    }

    public String getTipoVinculoServidorFalecidoOuExonerado(ContratoFP contratoFP, EstudoAtuarial estudoAtuarial) {
        String resultado;
        CargoConfianca cargoConfianca = cargoConfiancaFacade.recuperaCargoConfiancaVigente(contratoFP, Util.getDataHoraMinutoSegundoZerado(Util.recuperaDataUltimoDiaDoMes(estudoAtuarial.getDataReferencia())));
        if (contratoFP.getModalidadeContratoFP().getCodigo() == 1 && cargoConfianca != null && cargoConfianca.getId() != null) {
            resultado = "2";
        } else if (contratoFP.getModalidadeContratoFP().getCodigo() == 1 && (cargoConfianca == null || cargoConfianca.getId() == null)) {
            resultado = "1";
        }
        // 3-Estável não recuperando ainda
        else
            resultado = "4";
        return resultado;
    }

    // 3-Estável não recuperando ainda
    public String getTipoVinculoServidorAtivo(ContratoFP contratoFP, EstudoAtuarial estudoAtuarial) {
        boolean existeCargoConfianca = cargoConfiancaFacade.existeCargoConfiancaVigente(contratoFP, estudoAtuarial.getDataReferencia());

        if (ModalidadeContratoFP.CONCURSADOS.equals(contratoFP.getModalidadeContratoFP().getCodigo())) {
            if (existeCargoConfianca) {
                return "2"; // Cargo de confiança vigente
            } else {
                if (TipoRegime.CELETISTA.equals(contratoFP.getTipoRegime().getCodigo())) {
                    return "5";
                }
                return "1"; // Estável sem cargo de confiança
            }
        } else if (ModalidadeContratoFP.CARGO_ELETIVO.equals(contratoFP.getModalidadeContratoFP().getCodigo())) {
            return "6";
        } else if (ModalidadeContratoFP.CARGO_COMISSAO.equals(contratoFP.getModalidadeContratoFP().getCodigo())) {
            return "7";
        } else {
            return "4";
        }
    }

    public String getTipoAdministracao(Entidade entidade) {
        String resultado = "";
        if (entidade != null) {
            resultado = TipoAdministracao.DIRETA.equals(entidade.getTipoAdministracao()) ? "1" : "2";
        }
        return resultado;
    }

    public String getParidadeInstituidor(ContratoFP contratoFP) {
        String resultado = "2";
        Aposentadoria aposentadoria = aposentadoriaFacade.recuperaAposentadoriaPorContratoFP(contratoFP);
        if (aposentadoria != null) {
            resultado = TipoReajusteAposentadoria.PARIDADE.equals(aposentadoria.getTipoReajusteAposentadoria()) ? "1" : "2";
        }
        return resultado;
    }

    private String getComplementoMensagemLog(VinculoFP vinculoFP) {
        return getMatriculaFP(vinculoFP).getMatricula() + "-" + vinculoFP.getNumero() + " " + getPessoaFisica(vinculoFP).getNome();
    }

    public String recuperarTipoDependenteEstudoAtuarial(Long id) {
        String sql = " select g.tipodependestudoatuarial from graudeparentesco g " +
            "     inner join dependente d on d.graudeparentesco_id = g.id " +
            "     where d.id = :idDependente ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idDependente", id);
        try {
            String codigo = (String) q.getSingleResult();
            return getByCodigo(codigo);
        } catch (NoResultException e) {
            return "";
        }
    }

}
