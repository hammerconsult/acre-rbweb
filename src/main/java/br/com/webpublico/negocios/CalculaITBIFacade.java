/*
 * Codigo gerado automaticamente em Mon Oct 31 21:28:23 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.enums.TipoITBI;
import br.com.webpublico.enums.TipoProprietario;
import br.com.webpublico.negocios.comum.ConfiguracaoEmailFacade;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.negocios.tributario.dao.JdbcCalculoDAO;
import br.com.webpublico.negocios.tributario.dao.JdbcParcelaValorDividaDAO;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.util.Seguranca;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import org.apache.commons.codec.binary.Base64;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Stateless
public class CalculaITBIFacade extends AbstractFacade<CalculoITBI> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ParametroITBIFacade parametroITBIFacade;
    @EJB
    private FeriadoFacade feriadoFacade;
    @EJB
    private DAMFacade DAMFacade;
    @EJB
    private CadastroImobiliarioFacade cadastroImobiliarioFacade;
    @EJB
    private CadastroRuralFacade cadastroRuralFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private LoteFacade loteFacade;
    @EJB
    private GeraValorDividaITBI geraValorDividaITBI;
    @EJB
    private TipoNegociacaoFacade tipoNegociacaoFacade;
    @EJB
    private EnderecoCorreioFacade enderecoCorreioFacade;
    @EJB
    private ArquivoFacade arquivoFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private PessoaFisicaFacade pessoaFisicaFacade;
    @EJB
    private UsuarioSistemaFacade usuarioSistemaFacade;
    @EJB
    private ProcessoFacade processoFacade;
    @EJB
    private TipoIsencaoITBIFacade tipoIsencaoITBIFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ConsultaDebitoFacade consultaDebitoFacade;
    @EJB
    private TributoTaxasDividasDiversasFacade tributoTaxasDividasDiversasFacade;
    @EJB
    private InscricaoDividaAtivaFacade inscricaoDividaAtivaFacade;
    @EJB
    private ParametroParcelamentoFacade parametroParcelamentoFacade;
    @EJB
    private DividaFacade dividaFacade;
    @EJB
    private CertidaoDividaAtivaFacade certidaoDividaAtivaFacade;
    @EJB
    private ComunicaSofPlanFacade comunicaSofPlanFacade;
    @EJB
    private ConfiguracaoTributarioFacade configuracaoTributarioFacade;
    @EJB
    private ConfiguracaoEmailFacade configuracaoEmailFacade;
    @EJB
    private ArrecadacaoFacade arrecadacaoFacade;
    @EJB
    private ProcessoParcelamentoFacade processoParcelamentoFacade;
    @EJB
    private CompensacaoFacade compensacaoFacade;
    public final int TAMANHO_ASSINATURA = 32;
    private static final BigDecimal CEM = new BigDecimal(100);
    private JdbcParcelaValorDividaDAO parcelaValorDividaDAO;
    private JdbcCalculoDAO calculoDAO;

    public CalculaITBIFacade() {
        super(CalculoITBI.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @PostConstruct
    public void init() {
        ApplicationContext ap = ContextLoader.getCurrentWebApplicationContext();
        parcelaValorDividaDAO = (JdbcParcelaValorDividaDAO) ap.getBean("jdbcParcelaValorDividaDAO");
        calculoDAO = (JdbcCalculoDAO) ap.getBean("calculoDAO");
    }

    public TipoIsencaoITBIFacade getTipoIsencaoITBIFacade() {
        return tipoIsencaoITBIFacade;
    }

    public ParametroITBIFacade getParametroITBIFacade() {
        return parametroITBIFacade;
    }

    public FeriadoFacade getFeriadoFacade() {
        return feriadoFacade;
    }

    public DAMFacade getDAMFacade() {
        return DAMFacade;
    }

    public CadastroImobiliarioFacade getCadastroImobiliarioFacade() {
        return cadastroImobiliarioFacade;
    }

    public CadastroRuralFacade getCadastroRuralFacade() {
        return cadastroRuralFacade;
    }

    public PessoaFacade getPessoaFacade() {
        return pessoaFacade;
    }

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }

    public LoteFacade getLoteFacade() {
        return loteFacade;
    }

    public GeraValorDividaITBI getGeraValorDividaITBI() {
        return geraValorDividaITBI;
    }

    public TipoNegociacaoFacade getTipoNegociacaoFacade() {
        return tipoNegociacaoFacade;
    }

    public EnderecoCorreioFacade getEnderecoCorreioFacade() {
        return enderecoCorreioFacade;
    }

    public ArquivoFacade getArquivoFacade() {
        return arquivoFacade;
    }

    public ContratoFPFacade getContratoFPFacade() {
        return contratoFPFacade;
    }

    public PessoaFisicaFacade getPessoaFisicaFacade() {
        return pessoaFisicaFacade;
    }

    public UsuarioSistemaFacade getUsuarioSistemaFacade() {
        return usuarioSistemaFacade;
    }

    public ProcessoFacade getProcessoFacade() {
        return processoFacade;
    }

    public CalculoITBI salvaCalculo(CalculoITBI calculo) {
        return em.merge(calculo);
    }

    public InscricaoDividaAtivaFacade getInscricaoDividaAtivaFacade() {
        return inscricaoDividaAtivaFacade;
    }

    public ParametroParcelamentoFacade getParametroParcelamentoFacade() {
        return parametroParcelamentoFacade;
    }

    public DividaFacade getDividaFacade() {
        return dividaFacade;
    }

    public ProcessoParcelamentoFacade getProcessoParcelamentoFacade() {
        return processoParcelamentoFacade;
    }

    public CompensacaoFacade getCompensacaoFacade() {
        return compensacaoFacade;
    }

    public ProcessoCalculoITBI gerarSequenciaDoCalculoItbi(ProcessoCalculoITBI processo) {
        String sql = " select max(ct.sequencia) + 1 "
            + "        from calculoitbi ct "
            + "  inner join processocalculoitbi pct on pct.id = ct.processocalculoitbi_id"
            + "  inner join processocalculo pc on pc.id = pct.id"
            + "       where pc.exercicio_id = :exercicio_id";

        Query q = em.createNativeQuery(sql);
        q.setParameter("exercicio_id", processo.getExercicio().getId());

        BigDecimal count = (BigDecimal) q.getSingleResult();
        if (count == null) {
            count = BigDecimal.ONE;
        }
        processo.getCalculos().get(0).setSequencia(count.intValue());
        processo.getCalculos().get(0).defineReferencia();

        Integer sequencia = processo.getCalculos().get(0).getSequencia();
        processo.getCalculos().get(0).setCodigoVerificacao(geraAssinaturaDigital(processo.getCalculos().get(0)));
        processo = em.merge(processo);
        return processo;
    }

    public String geraAssinaturaDigital(CalculoITBI calculoITBI) {
        String assinaturaDigital = calculoITBI.getDataLancamento().toString() + calculoITBI.getCadastro().getNumeroCadastro();
        assinaturaDigital = StringUtil.cortaOuCompletaEsquerda(Seguranca.md5(assinaturaDigital).toUpperCase(), TAMANHO_ASSINATURA, "0");
        return assinaturaDigital;
    }

    @Deprecated
    private String gerarCriptografia(Integer sequencia, String tipoITBI1, BigDecimal baseCalculo) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(geraCodigo(sequencia, new Date(), tipoITBI1, baseCalculo).getBytes());
            return Util.substituiCaracterEspecial(converteBytesParaString(md.digest()).toUpperCase(), "A");
        } catch (NoSuchAlgorithmException e) {
            return null;
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }


    public String geraCodigo(Integer sequencia, Date dataEmissao, String tipoItbi, BigDecimal baseCalculo) {
        StringBuilder sb = new StringBuilder();
        sb.append(sequencia).append(dataEmissao).append(tipoItbi).append(baseCalculo);
        return sb.toString();
    }

    public String converteBytesParaString(byte[] value) throws UnsupportedEncodingException {
        byte[] base64 = Base64.encodeBase64(value);
        String retorno = new String(base64, "UTF-8");
        return retorno;
    }


    public CalculoITBI recuperaCalculoITBI(CalculoITBI calculoITBI) {
        Query q = em.createQuery(" select calc from CalculoITBI calc where calc = :calculoITBI");
        q.setParameter("calculoITBI", calculoITBI);
        if (!q.getResultList().isEmpty()) {
            return (CalculoITBI) q.getResultList().get(0);
        }

        return null;

    }

    public ValorDivida retornaValorDividaDoCalculo(CalculoITBI calculo) {
        Query q = em.createQuery("from ValorDivida vd where vd.calculo.id = :calculo");
        q.setParameter("calculo", calculo.getId());
        List<ValorDivida> resultado = q.getResultList();
        if (resultado.isEmpty()) {
            return null;
        } else {
            resultado.get(0).getParcelaValorDividas().size();
            return resultado.get(0);
        }
    }

    @Override
    public List<CalculoITBI> lista() {
        Query q = em.createQuery("from CalculoITBI calcITBI "
            + "where calcITBI in (select calculo from ValorDivida vd where vd.calculo = calcITBI) order by sequencia desc");
        return q.getResultList();
    }

    public List<CalculoITBI> executaConsulta(String sql, int inicio, int max) {
        Query consulta = em.createQuery(sql, CalculoITBI.class);

        if (max != 0) {
            consulta.setMaxResults(max + 1);
            consulta.setFirstResult(inicio);
        }

        return consulta.getResultList();
    }

    @Override
    public CalculoITBI recuperar(Object id) {
        CalculoITBI calculo = super.recuperar(id);
        calculo.getProcessoCalculo().getCalculos().size();
        calculo.getAdquirentesCalculo().size();
        calculo.getTransmitentesCalculo().size();
        calculo.getArquivos().size();
        calculo.getItensCalculo().size();
        calculo.getHistoricosLaudo().size();
        return calculo;
    }

    public List<TransmitentesCalculoITBI> recuperarTransmitentes(CalculoITBI c) {
        return em.createQuery("from TransmitentesCalculoITBI where calculoITBI = :calculo").setParameter("calculo", c).getResultList();
    }

    public List<AdquirentesCalculoITBI> recuperarAdquirentes(CalculoITBI c) {
        return em.createQuery("from AdquirentesCalculoITBI where calculoITBI = :calculo").setParameter("calculo", c).getResultList();
    }

    public boolean verificaSeEstaPago(CalculoITBI iTBI) {
        String sql = "select itbi.id from parcelavalordivida p "
            + " inner join situacaoparcelavalordivida s on s.id = p.situacaoatual_id and s.situacaoparcela = 'PAGO' "
            + " inner join valordivida v on v.id = p.valordivida_id "
            + " inner join calculoitbi itbi on itbi.id = v.calculo_id where itbi.id = :aux";
        Query q = em.createNativeQuery(sql);
        q.setParameter("aux", iTBI.getId());
        if (q.getResultList().isEmpty()) {
            return false;
        }
        return true;
    }

    public void transfereProprietarios(CalculoITBI c) {
        if (c.getCadastroImobiliario() != null) {
            logger.error("Imobiliario");
            transfereProprietariosCadastroImobiliario(c);
        } else {
            logger.error("Rural");
            transfereProprietariosCadastroRural(c);
        }
    }

    public void transfereProprietariosCadastroImobiliario(CalculoITBI calculo) {
        logger.error("transfereProprietariosCadastroImobiliario");
        if (!podeTransferir(calculo, calculo.getCadastroImobiliario())) {
            return;
        }
        CalculoITBI calculoITBI = em.find(CalculoITBI.class, calculo.getId());
        CadastroImobiliario ci = em.find(CadastroImobiliario.class, calculoITBI.getCadastroImobiliario().getId());
        List<Pessoa> proprietario = recuperarPessoasDaPropriedadeImovel(ci);
        List<Pessoa> adquirentes = recuperarPessoasAdquirentes(calculoITBI);

        if (!proprietario.containsAll(adquirentes)) {
            if (calculo.getTotalPercentualAdquirentes().compareTo(CEM) == 0) {
                for (Propriedade propriedade : ci.getPropriedadeVigente()) {
                    propriedade.setFinalVigencia(new Date());
                }
            } else {
                List<Propriedade> propriedadesImovel = cadastroImobiliarioFacade.recuperarProprietariosVigentes(calculo.getCadastroImobiliario());

                BigDecimal proporcaoDosProprietarioJaVigentes = getProporcaoDosProprietarioJaVigentesImovel(propriedadesImovel);
                BigDecimal proporcaoAjustada = proporcaoDosProprietarioJaVigentes.subtract(calculo.getTotalPercentualAdquirentes()).setScale(2, RoundingMode.DOWN);

                for (Propriedade propriedadeAjustada : propriedadesImovel) {
                    BigDecimal proporcaoPropriedade = BigDecimal.valueOf(propriedadeAjustada.getProporcao());

                    propriedadeAjustada.setProporcao((proporcaoPropriedade.multiply(proporcaoAjustada).divide(CEM))
                        .setScale(2, RoundingMode.DOWN).doubleValue());
                    em.merge(propriedadeAjustada);
                }
            }
            for (AdquirentesCalculoITBI ad : calculoITBI.getAdquirentesCalculo()) {
                Propriedade propriedade = new Propriedade();
                propriedade.setDataRegistro(new Date());
                propriedade.setImovel(ci);
                propriedade.setInicioVigencia(new Date());
                propriedade.setPessoa(ad.getAdquirente());
                propriedade.setProporcao(ad.getPercentual().doubleValue());
                propriedade.setTipoProprietario(TipoProprietario.COMPRAVENDA);
                propriedade.setVeioPorITBI(true);
                ci.getPropriedade().add(propriedade);
            }
            ci = em.merge(ci);
            arredondarProporcao(ci);
            transfereDebitosCadastroImobiliario(ci);
            transferirCDAs(ci);
        }
    }

    private BigDecimal getProporcaoDosProprietarioJaVigentesImovel(List<Propriedade> propriedadesImovel) {
        BigDecimal proporcaoDosProprietarioJaVigentes = BigDecimal.ZERO;
        for (Propriedade propriedade : propriedadesImovel) {
            proporcaoDosProprietarioJaVigentes = proporcaoDosProprietarioJaVigentes.add(BigDecimal.valueOf(propriedade.getProporcao()));
        }
        return proporcaoDosProprietarioJaVigentes;
    }

    private BigDecimal getProporcaoDosProprietarioJaVigentesRural(List<PropriedadeRural> propriedadesRurais) {
        BigDecimal proporcaoDosProprietarioJaVigentes = BigDecimal.ZERO;
        for (PropriedadeRural propriedade : propriedadesRurais) {
            proporcaoDosProprietarioJaVigentes = proporcaoDosProprietarioJaVigentes.add(BigDecimal.valueOf(propriedade.getProporcao()));
        }
        return proporcaoDosProprietarioJaVigentes;
    }

    private void arredondarProporcao(Cadastro cadastro) {
        BigDecimal proporcaoAtual = BigDecimal.ZERO;
        if (cadastro instanceof CadastroImobiliario) {
            CadastroImobiliario imovel = cadastroImobiliarioFacade.recuperarSemCalcular(cadastro.getId());
            List<Propriedade> proprietariosVigentes = cadastroImobiliarioFacade.recuperarProprietariosVigentes(imovel);
            proporcaoAtual = getProporcaoDosProprietarioJaVigentesImovel(proprietariosVigentes);
            ajustarProporcaoQuandoMenorQueCemPorcentoImovel(proporcaoAtual, imovel);
            ajustarProporcaoQuandoMaiorQueCemPorcentoImovel(proporcaoAtual, imovel);
        } else if (cadastro instanceof CadastroRural) {
            CadastroRural rural = cadastroRuralFacade.recuperar(cadastro.getId());
            List<PropriedadeRural> proprietariosVigentes = cadastroRuralFacade.recuperarPropriedadesVigentes(rural);
            proporcaoAtual = getProporcaoDosProprietarioJaVigentesRural(proprietariosVigentes);
            ajustarProporcaoMenorQueCemPorcentoQuandoRural(proporcaoAtual, rural);
            ajudarProporcaoQuandoMaiorQueCemPorcentoRural(proporcaoAtual, rural);
        }
    }

    private void ajustarProporcaoMenorQueCemPorcentoQuandoRural(BigDecimal proporcaoAtual, CadastroRural rural) {
        if (proporcaoAtual.compareTo(CEM) < 0) {
            BigDecimal diferencaProporcao = CEM.subtract(proporcaoAtual).setScale(2, RoundingMode.HALF_UP);
            PropriedadeRural primeiroProprietarioRural = buscarPrimeiroProprietarioVigenteRural(rural);
            if (primeiroProprietarioRural != null) {
                BigDecimal proporcaoPrimeiroProprietarioRural = new BigDecimal(primeiroProprietarioRural.getProporcao());
                primeiroProprietarioRural.setProporcao((proporcaoPrimeiroProprietarioRural.add(diferencaProporcao)).doubleValue());
                em.merge(primeiroProprietarioRural);
            }
        }
    }

    private void ajudarProporcaoQuandoMaiorQueCemPorcentoRural(BigDecimal proporcaoAtual, CadastroRural rural) {
        if (proporcaoAtual.compareTo(CEM) > 0) {
            BigDecimal diferencaProporcao = proporcaoAtual.subtract(CEM).setScale(2, RoundingMode.HALF_UP);
            PropriedadeRural primeiroProprietarioRural = buscarPrimeiroProprietarioVigenteRural(rural);
            if (primeiroProprietarioRural != null) {
                BigDecimal proporcaoPrimeiroProprietarioRural = new BigDecimal(primeiroProprietarioRural.getProporcao());
                primeiroProprietarioRural.setProporcao((proporcaoPrimeiroProprietarioRural.subtract(diferencaProporcao)).doubleValue());
                em.merge(primeiroProprietarioRural);
            }
        }
    }

    private void ajustarProporcaoQuandoMenorQueCemPorcentoImovel(BigDecimal proporcaoAtual, CadastroImobiliario imovel) {
        if (proporcaoAtual.compareTo(CEM) < 0) {
            BigDecimal diferencaProporcao = CEM.subtract(proporcaoAtual).setScale(2, RoundingMode.HALF_UP);
            Propriedade primeiroProprietarioImovel = buscarPrimeiroProprietarioVigenteImovel(imovel);
            if (primeiroProprietarioImovel != null) {
                BigDecimal proporcaoPrimeiroProprietarioImovel = new BigDecimal(primeiroProprietarioImovel.getProporcao());
                primeiroProprietarioImovel.setProporcao((proporcaoPrimeiroProprietarioImovel.add(diferencaProporcao)).doubleValue());
                em.merge(primeiroProprietarioImovel);
            }
        }
    }

    private void ajustarProporcaoQuandoMaiorQueCemPorcentoImovel(BigDecimal proporcaoAtual, CadastroImobiliario imovel) {
        if (proporcaoAtual.compareTo(CEM) > 0) {
            BigDecimal diferencaProporcao = proporcaoAtual.subtract(CEM).setScale(2, RoundingMode.HALF_UP);
            Propriedade primeiroProprietarioImovel = buscarPrimeiroProprietarioVigenteImovel(imovel);
            if (primeiroProprietarioImovel != null) {
                BigDecimal proporcaoPrimeiroProprietarioImovel = new BigDecimal(primeiroProprietarioImovel.getProporcao());
                primeiroProprietarioImovel.setProporcao((proporcaoPrimeiroProprietarioImovel.subtract(diferencaProporcao)).doubleValue());
                em.merge(primeiroProprietarioImovel);
            }
        }
    }

    public Propriedade buscarPrimeiroProprietarioVigenteImovel(CadastroImobiliario imovel) {
        String sql = " SELECT * FROM PROPRIEDADE " +
            " WHERE IMOVEL_ID = :imovel_id " +
            " AND CURRENT_DATE BETWEEN trunc(INICIOVIGENCIA) AND trunc(COALESCE(FINALVIGENCIA,CURRENT_DATE)) " +
            " ORDER BY INICIOVIGENCIA ASC ";
        Query q = em.createNativeQuery(sql, Propriedade.class);
        q.setParameter("imovel_id", imovel.getId());
        List resultList = q.getResultList();
        return !resultList.isEmpty() ? (Propriedade) resultList.get(0) : null;
    }

    public PropriedadeRural buscarPrimeiroProprietarioVigenteRural(CadastroRural rural) {
        String sql = " SELECT * FROM PROPRIEDADERURAL " +
            " WHERE IMOVEL_ID = :imovel_id " +
            " AND CURRENT_DATE BETWEEN trunc(INICIOVIGENCIA) AND trunc(COALESCE(FINALVIGENCIA,CURRENT_DATE)) " +
            " ORDER BY INICIOVIGENCIA ASC ";
        Query q = em.createNativeQuery(sql, PropriedadeRural.class);
        q.setParameter("imovel_id", rural.getId());
        List resultList = q.getResultList();
        return !resultList.isEmpty() ? (PropriedadeRural) resultList.get(0) : null;
    }

    @TransactionTimeout(value = 1, unit = TimeUnit.HOURS)
    public void transfereDebitosCadastroImobiliario(CadastroImobiliario ci) {
        try {
            List<ResultadoParcela> resultados = new ConsultaParcela()
                .addParameter(ConsultaParcela.Campo.CADASTRO_ID, ConsultaParcela.Operador.IGUAL, ci.getId())
                .addParameter(ConsultaParcela.Campo.PARCELA_SITUACAO, ConsultaParcela.Operador.IGUAL, SituacaoParcela.EM_ABERTO)
                .executaConsulta().getResultados();


            List<Long> idsCalculos = Lists.newArrayList();
            for (ResultadoParcela resultado : resultados) {
                if (!idsCalculos.contains(resultado.getIdCalculo())) {
                    idsCalculos.add(resultado.getIdCalculo());
                }
            }
            for (Long idCalculo : idsCalculos) {
                Calculo calc = em.find(Calculo.class, idCalculo);
                calc.getPessoas().clear();
                for (Propriedade propriedade : ci.getPropriedadeVigente()) {
                    CalculoPessoa cp = new CalculoPessoa();
                    cp.setPessoa(propriedade.getPessoa());
                    cp.setCalculo(calc);
                    calc.getPessoas().add(cp);
                }
                em.merge(calc);
            }
        } catch (Exception ex) {
            logger.error("Erro ao transferir os débitos do imovel " + ci.getInscricaoCadastral() + ": ", ex);
        }
    }

    public void transferirCDAs(CadastroImobiliario ci) {
        try {
            List<CertidaoDividaAtiva> certidoes = certidaoDividaAtivaFacade.buscarCertidoesAbertasPorCadastro(ci);
            for (CertidaoDividaAtiva certidao : certidoes) {
                certidao.setPessoa(ci.getPropriedadeVigente().get(0).getPessoa());
                certidao = em.merge(certidao);
                logger.error("Alterou a CDA " + certidao.getNumero() + "/" + certidao.getExercicio().getAno() + " -> " + certidao.getPessoa());
            }
            enviarCdasSoftplan(certidoes);
        } catch (Exception e) {
            logger.error("Não foi possível comunicar a procuradoria sobre a troca de proprietários");
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void enviarCdasSoftplan(List<CertidaoDividaAtiva> certidoes) {
        try {
            comunicaSofPlanFacade.enviarCDA(certidoes);
        } catch (Exception ex) {
            logger.error("Erro ao enviar cdas do itbi para softplan. {}", ex.getMessage());
            logger.debug("Detalhes do erro ao enviar cdas do itbi para softplan.", ex);
        }
    }

    private List<Pessoa> recuperarPessoasDaPropriedadeImovel(CadastroImobiliario ci) {
        List<Pessoa> proprietarios = Lists.newArrayList();
        for (Propriedade propriedades : ci.getPropriedadeVigente()) {
            proprietarios.add(propriedades.getPessoa());
        }
        return proprietarios;
    }

    private List<Pessoa> recuperarPessoasDaPropriedadeRural(CadastroRural cr) {
        List<Pessoa> proprietarios = Lists.newArrayList();
        for (PropriedadeRural propriedades : cr.getPropriedade()) {
            proprietarios.add(propriedades.getPessoa());
        }
        return proprietarios;
    }

    private List<Pessoa> recuperarPessoasAdquirentes(CalculoITBI calculoITBI) {
        List<Pessoa> adquiresntes = Lists.newArrayList();
        for (AdquirentesCalculoITBI ad : calculoITBI.getAdquirentesCalculo()) {
            adquiresntes.add(ad.getAdquirente());
        }
        return adquiresntes;
    }

    public void transfereProprietariosCadastroRural(CalculoITBI calculo) {
        if (!podeTransferir(calculo, calculo.getCadastroRural())) {
            return;
        }

        CalculoITBI calculoITBI = em.find(CalculoITBI.class, calculo.getId());
        CadastroRural cr = em.find(CadastroRural.class, calculoITBI.getCadastroRural().getId());
        List<Pessoa> proprietario = recuperarPessoasDaPropriedadeRural(cr);
        List<Pessoa> adquirentes = recuperarPessoasAdquirentes(calculoITBI);

        if (!proprietario.containsAll(adquirentes)) {
            if (calculo.getTotalPercentualAdquirentes().compareTo(CEM) == 0) {
                for (PropriedadeRural propriedade : cr.getPropriedade()) {
                    propriedade.setFinalVigencia(new Date());
                }
            } else {
                List<PropriedadeRural> propriedadeRural = cadastroRuralFacade.recuperarPropriedadesVigentes(calculo.getCadastroRural());
                BigDecimal proporcaoDosProprietarioJaVigentes = getProporcaoDosProprietarioJaVigentesRural(propriedadeRural);
                BigDecimal proporcaoAjustada = proporcaoDosProprietarioJaVigentes.subtract(calculo.getTotalPercentualAdquirentes()).setScale(2, RoundingMode.DOWN);

                for (PropriedadeRural propriedadeAjusdata : propriedadeRural) {
                    BigDecimal proporcaoPropriedade = BigDecimal.valueOf(propriedadeAjusdata.getProporcao());

                    propriedadeAjusdata.setProporcao(
                        (proporcaoPropriedade.multiply(proporcaoAjustada).divide(CEM))
                            .setScale(2, RoundingMode.DOWN)
                            .doubleValue()
                    );
                    em.merge(propriedadeAjusdata);
                }
            }

            for (AdquirentesCalculoITBI ad : calculoITBI.getAdquirentesCalculo()) {
                PropriedadeRural propriedade = new PropriedadeRural();
                propriedade.setDataRegistro(new Date());
                propriedade.setImovel(cr);
                propriedade.setInicioVigencia(new Date());
                propriedade.setPessoa(ad.getAdquirente());
                propriedade.setProporcao(ad.getPercentual().doubleValue());
                propriedade.setTipoProprietario(TipoProprietario.COMPRAVENDA);
                cr.getPropriedade().add(propriedade);
            }
            em.merge(cr);
            arredondarProporcao(cr);
        }
    }

    public boolean podeTransferir(Calculo calculoItbi, Cadastro cadastro) {
        String sql = " select pvd.id from parcelavalordivida pvd"
            + "  inner join valordivida vd on vd.id = pvd.valordivida_id"
            + "  inner join calculoitbi itbi on itbi.id = vd.calculo_id"
            + "  inner join situacaoparcelavalordivida spvd on spvd.id = pvd.situacaoAtual_id"
            + "  where spvd.situacaoparcela = :situacaoAberto "
            + "    and coalesce(itbi.cadastrorural_id, itbi.cadastroimobiliario_id) = :cadastro_id"
            + "    and itbi.id = :calculo_id";
        Query q = em.createNativeQuery(sql);
        q.setParameter("cadastro_id", cadastro.getId());
        q.setParameter("calculo_id", calculoItbi.getId());
        q.setParameter("situacaoAberto", SituacaoParcela.EM_ABERTO.name());
        return q.getResultList().isEmpty();
    }

    public boolean cadastroPossuiDebitosVencidosOuAVencer(Cadastro cadastro) {
        List<ResultadoParcela> listaDebitos = getResultadoParcelasDoCadastro(cadastro);
        List<ResultadoParcela> debitos = Lists.newArrayList();
        for (ResultadoParcela p : listaDebitos) {
            if ((p.getVencido()
                && !Calculo.TipoCalculo.TAXAS_DIVERSAS.equals(p.getTipoCalculoEnumValue())
                && !Calculo.TipoCalculo.DOCTO_OFICIAL.equals(p.getTipoCalculoEnumValue())
                && !Calculo.TipoCalculo.NFS_AVULSA.equals(p.getTipoCalculoEnumValue()))) {
                debitos.add(p);
            } else if (p.getVencido() && Calculo.TipoCalculo.TAXAS_DIVERSAS.equals(p.getTipoCalculoEnumValue())) {
                if (tributoTaxasDividasDiversasFacade.parcelaComAlvaraBloqueada(p.getIdParcela())) {
                    debitos.add(p);
                }
            } else {
                debitos.add(p);
            }
        }
        return !debitos.isEmpty();
    }

    public boolean cadastroPossuiDebitosAjuizados(Cadastro cadastro) {
        List<ResultadoParcela> listaDebitos = getResultadoParcelasDoCadastro(cadastro);
        List<ResultadoParcela> debitos = Lists.newArrayList();
        for (ResultadoParcela p : listaDebitos) {
            if (p.getDividaAtivaAjuizada()) {
                debitos.add(p);
            }
        }
        return !debitos.isEmpty();
    }

    public boolean cadastroPossuiDebitosItbi(Cadastro cadastro) {
        return !getResultadoParcelasItbiDoCadastro(cadastro).isEmpty();
    }

    public boolean cadastroPossuiDebitosVencidos(Cadastro cadastro) {
        List<ResultadoParcela> listaDebitos = getResultadoParcelasDoCadastro(cadastro);
        List<ResultadoParcela> vencidos = Lists.newArrayList();
        for (ResultadoParcela p : listaDebitos) {
            if ((p.getVencido()
                && !Calculo.TipoCalculo.TAXAS_DIVERSAS.equals(p.getTipoCalculoEnumValue())
                && !Calculo.TipoCalculo.DOCTO_OFICIAL.equals(p.getTipoCalculoEnumValue())
                && !Calculo.TipoCalculo.NFS_AVULSA.equals(p.getTipoCalculoEnumValue()))) {
                vencidos.add(p);
            } else if (p.getVencido() && Calculo.TipoCalculo.TAXAS_DIVERSAS.equals(p.getTipoCalculoEnumValue())) {
                if (tributoTaxasDividasDiversasFacade.parcelaComAlvaraBloqueada(p.getIdParcela())) {
                    vencidos.add(p);
                }
            }
        }
        return !vencidos.isEmpty();
    }

    private List<ResultadoParcela> getResultadoParcelasDoCadastro(Cadastro cadastro) {
        ConsultaParcela consultaParcela = new ConsultaParcela();
        consultaParcela.addParameter(ConsultaParcela.Campo.CADASTRO_ID, ConsultaParcela.Operador.IGUAL, cadastro.getId());
        consultaParcela.addParameter(ConsultaParcela.Campo.PARCELA_SITUACAO, ConsultaParcela.Operador.IGUAL, SituacaoParcela.EM_ABERTO);
        return consultaParcela.executaConsulta().getResultados();
    }

    private List<ResultadoParcela> getResultadoParcelasItbiDoCadastro(Cadastro cadastro) {
        List<Long> idsCalculos = getIdsCalculosDosCadastros(cadastro);
        if (!idsCalculos.isEmpty()) {
            ConsultaParcela consultaParcela = new ConsultaParcela();
            consultaParcela.addParameter(ConsultaParcela.Campo.CALCULO_ID, ConsultaParcela.Operador.IN, idsCalculos);
            consultaParcela.addParameter(ConsultaParcela.Campo.PARCELA_SITUACAO, ConsultaParcela.Operador.IGUAL, SituacaoParcela.EM_ABERTO);
            consultaParcela.addParameter(ConsultaParcela.Campo.TIPO_CALCULO, ConsultaParcela.Operador.IGUAL, Calculo.TipoCalculo.ITBI);
            return consultaParcela.executaConsulta().getResultados();
        }
        return Lists.newArrayList();
    }

    private List<Long> getIdsCalculosDosCadastros(Cadastro cadastro) {
        String sql = "select c.id from CalculoITBI c where (c.cadastroImobiliario_id = :idCadastro or c.cadastroRural_id = :idCadastro)";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idCadastro", cadastro.getId());
        return q.getResultList();
    }

    public List<DAM> recuperaDAM(Long id) {
        return em.createQuery("select dam " +
            "                from ItemDAM item" +
            "                join item.DAM dam " +
            "                where item.parcela.id = :id ").setParameter("id", id).getResultList();
    }

    public List<String> dataPagamentoDAM(String id) {
        Query q = em.createNativeQuery(" select lote.datapagamento from lotebaixa " +
            " inner join itemlotebaixa item on item.lotebaixa_id = lotebaixa.id " +
            " inner join lotebaixa lote on item.lotebaixa_id = lote.id " +
            " inner join dam on item.dam_id = dam.id " +
            " where dam.situacao = 'PAGO' and dam.id in (:id)");
        q.setParameter("id", id);
        if (q.getResultList().isEmpty()) {
            return new ArrayList<>();
        }
        return q.getResultList();
    }

    public List<PessoaFisica> completaAvaliadoresParametro(String parte, Exercicio exercicio, TipoITBI tipoITBI) {
        String hql = "select av from ParametrosFuncionarios pf " +
            "join pf.parametrosbce p " +
            "join pf.pessoa av " +
            "where p.exercicio = :exercicio " +
            "and p.tipoITBI = :tipoItbi " +
            "and (:data >= pf.vigenciaInicial and :data < coalesce(pf.vigenciaFinal, current_date)) " +
            "and ((lower(av.nome) like :filtro) OR (lower(av.cpf) like :filtro))";
        Query q = em.createQuery(hql);
        q.setParameter("exercicio", exercicio);
        q.setParameter("tipoItbi", tipoITBI);
        q.setParameter("filtro", "%" + parte.toLowerCase() + "%");
        q.setParameter("data", new Date());

        List<PessoaFisica> retorno = q.getResultList();
        if (retorno.isEmpty()) {
            PessoaFisica p = new PessoaFisica();
            p.setNome("Nenhum avaliador cadastrado ou vigênte no parâmetro de ITBI.");
            retorno.add(p);
        }
        return retorno;
    }

    public List<CalculoITBI> recuperaCalculosDaPessoa(Pessoa pessoa) {
        Query q = em.createQuery("select distinct c from CalculoITBI c " +
                " join c.adquirentesCalculo ad " +
                " where ad.adquirente = :pessoa ")
            .setParameter("pessoa", pessoa);
        return q.getResultList();
    }

    public ByteArrayOutputStream imprimeLaudo(Long idCalculo, String damsParcela) {
        String separator = System.getProperty("file.separator");
        String report = Util.getApplicationPath("/WEB-INF/") + separator + "report" + separator;
        String img = Util.getApplicationPath("/img/") + separator;
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        InputStream inputStreamAssinaturaAvaliacaoComissao = null;
        InputStream inputStreamAssinaturaDiretorChefeTributo = null;
        CalculoITBI calculoITBI = recuperar(idCalculo);
        LaudoAvaliacaoITBI laudoAvaliacaoITBI = recuperarLaudo(calculoITBI);
        if (laudoAvaliacaoITBI.getRespComissaoAvaliadora() != null &&
            laudoAvaliacaoITBI.getRespComissaoAvaliadora().getDetentorArquivoComposicao() != null &&
            laudoAvaliacaoITBI.getRespComissaoAvaliadora().getDetentorArquivoComposicao().getArquivosComposicao() != null) {
            laudoAvaliacaoITBI.getRespComissaoAvaliadora().getDetentorArquivoComposicao().getArquivosComposicao().get(0).getArquivo().montarImputStream();
            inputStreamAssinaturaAvaliacaoComissao = laudoAvaliacaoITBI.getRespComissaoAvaliadora().getDetentorArquivoComposicao().getArquivosComposicao().get(0).getArquivo().getInputStream();
        }
        if (laudoAvaliacaoITBI.getDiretorChefeDeparTributo() != null &&
            laudoAvaliacaoITBI.getDiretorChefeDeparTributo().getDetentorArquivoComposicao() != null &&
            laudoAvaliacaoITBI.getDiretorChefeDeparTributo().getDetentorArquivoComposicao().getArquivosComposicao() != null) {
            laudoAvaliacaoITBI.getDiretorChefeDeparTributo().getDetentorArquivoComposicao().getArquivosComposicao().get(0).getArquivo().montarImputStream();
            inputStreamAssinaturaDiretorChefeTributo = laudoAvaliacaoITBI.getDiretorChefeDeparTributo().getDetentorArquivoComposicao().getArquivosComposicao().get(0).getArquivo().getInputStream();
        }
        try {
            HashMap parameters = new HashMap();
            parameters.put("CALCULO_ID", idCalculo);
            parameters.put("CAMINHOBRASAO", img);
            parameters.put("ASSINATURA_COMISSAO_AVALIACAO", inputStreamAssinaturaAvaliacaoComissao);
            parameters.put("ASSINATURA_DIRETOR_CHEFE_TRIBUTOS", inputStreamAssinaturaDiretorChefeTributo);
            parameters.put("SUB", report);
            if (damsParcela != null && !damsParcela.isEmpty()) {
                parameters.put("IDDAMS", damsParcela.substring(0, damsParcela.length() - 2));
            }
            Connection con = AbstractReport.getAbstractReport().recuperaConexaoJDBC();
            try {
                parameters.put(JRParameter.REPORT_LOCALE, new Locale("pt", "BR"));
                JasperPrint jasperPrint = JasperFillManager.fillReport(report + "LaudoAvaliacaoITBI.jasper", parameters, con);
                JRPdfExporter exporter = new JRPdfExporter();
                exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
                exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, bytes);
                exporter.exportReport();
            } catch (Exception ex) {
                logger.error("erro ", ex);
            } finally {
                AbstractReport.fecharConnection(con);
            }
            return bytes;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public LaudoAvaliacaoITBI recuperarLaudo(CalculoITBI calculoITBI) {
        String sql = " select laudo.* from laudoavaliacaoitbi laudo " +
            " where laudo.calculoitbi_id = :idCalculoItbi ";
        Query q = em.createNativeQuery(sql, LaudoAvaliacaoITBI.class);
        q.setParameter("idCalculoItbi", calculoITBI.getId());
        return q.getResultList() != null && !q.getResultList().isEmpty() ? (LaudoAvaliacaoITBI) q.getResultList().get(0) : null;
    }

    public LaudoAvaliacaoITBI salvarLaudoAvaliacao(LaudoAvaliacaoITBI laudoAvaliacaoITBI) {
        LaudoAvaliacaoITBI laudo = em.merge(laudoAvaliacaoITBI);
        if (laudo.getDiretorChefeDeparTributo() != null &&
            laudo.getDiretorChefeDeparTributo().getDetentorArquivoComposicao() != null) {
            laudo.getDiretorChefeDeparTributo().getDetentorArquivoComposicao().getArquivosComposicao().size();
            for (ArquivoComposicao arquivoComposicao : laudo.getDiretorChefeDeparTributo().getDetentorArquivoComposicao().getArquivosComposicao()) {
                arquivoComposicao.getArquivo().getPartes().size();
            }
        }
        if (laudo.getRespComissaoAvaliadora() != null &&
            laudo.getRespComissaoAvaliadora().getDetentorArquivoComposicao() != null) {
            laudo.getRespComissaoAvaliadora().getDetentorArquivoComposicao().getArquivosComposicao().size();
            for (ArquivoComposicao arquivoComposicao : laudo.getRespComissaoAvaliadora().getDetentorArquivoComposicao().getArquivosComposicao()) {
                arquivoComposicao.getArquivo().getPartes().size();
            }
        }
        return laudo;
    }

    public CalculoITBI salvarRetornando(CalculoITBI calculoITBI) {
        return em.merge(calculoITBI);
    }

    public ConfiguracaoEmailFacade getConfiguracaoEmailFacade() {
        return configuracaoEmailFacade;
    }

    public ConfiguracaoTributarioFacade getConfiguracaoTributarioFacade() {
        return configuracaoTributarioFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public void cancelarParcelasItbi(List<ResultadoParcela> parcelas) {
        for (ResultadoParcela parcela : parcelas) {
            if (parcela.isEmAberto()) {
                parcelaValorDividaDAO.inserirSituacaoParcelaValorDivida(
                    parcela.getIdParcela(),
                    parcela.getReferencia(),
                    arrecadacaoFacade.recuperarSaldoUltimaSituacao(parcela.getIdParcela(), SituacaoParcela.EM_ABERTO),
                    SituacaoParcela.CANCELAMENTO
                );
            }
        }
    }
}
