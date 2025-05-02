package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.DamMalaDireta;
import br.com.webpublico.entidadesauxiliares.FiltroCadastroMalaDiretaIPTU;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.enums.TipoCobrancaTributario;
import br.com.webpublico.enums.TipoEndereco;
import br.com.webpublico.enums.TipoImovel;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.negocios.tributario.dao.JdbcDamDAO;
import br.com.webpublico.negocios.tributario.dao.JdbcMalaDiretaIptuDAO;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.util.AssistenteBarraProgresso;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.hibernate.Hibernate;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by William on 07/06/2016.
 */
@Stateless
public class MalaDiretaIptuFacade extends AbstractFacade<MalaDiretaIPTU> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ConfiguracaoTributarioFacade configuracaoTributarioFacade;
    @EJB
    private ConfiguracaoDAMFacade configuracaoDAMFacade;
    private JdbcMalaDiretaIptuDAO daoMalaDiretaIptu;
    private JdbcDamDAO daoDAM;
    @EJB
    private FeriadoFacade feriadoFacade;
    @EJB
    private DAMFacade damFacade;
    @EJB
    private ParametroMalaDiretaIptuFacade parametroMalaDiretaIptuFacade;
    @EJB
    private CadastroImobiliarioFacade cadastroImobiliarioFacade;


    public MalaDiretaIptuFacade() {
        super(MalaDiretaIPTU.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public ParametroMalaDiretaIptuFacade getParametroMalaDiretaIptuFacade() {
        return parametroMalaDiretaIptuFacade;
    }

    @Override
    public MalaDiretaIPTU recuperar(Object id) {
        MalaDiretaIPTU mala = super.recuperar(id);
        Hibernate.initialize(mala.getCadastroMalaDiretaIPTU());
        return mala;
    }

    @TransactionTimeout(value = 1, unit = TimeUnit.HOURS)
    public List<Long> buscarIdsDeBcisParaMalaDiretaDeIPTU(MalaDiretaIPTU malaDiretaIPTU, ConfiguracaoTributario configuracaoTributario) {
        String sql = "select distinct ci.id " +
            "   from cadastroimobiliario ci " +
            "  inner join propriedade pp on pp.imovel_id = ci.id " +
            "  left join vwenderecobci vwend on vwend.cadastroimobiliario_id = ci.id " +
            " where sysdate between pp.iniciovigencia and coalesce(pp.finalvigencia, sysdate) " +
            "  and ci.inscricaocadastral between :inicial and :final " +
            "  and exists (select pvd.id from PARCELAVALORDIVIDA pvd " +
            "            inner join SITUACAOPARCELAVALORDIVIDA spvd on spvd.id = pvd.SITUACAOATUAL_ID " +
            "            inner join VALORDIVIDA vd on vd.id = pvd.VALORDIVIDA_ID " +
            "            inner join OPCAOPAGAMENTO op on op.ID = pvd.OPCAOPAGAMENTO_ID " +
            "            inner join Calculo cal on cal.id = vd.CALCULO_ID " +
            "            where spvd.SITUACAOPARCELA = :situacao " +
            "              and cal.CADASTRO_ID = ci.id " +
            "              and vd.DIVIDA_ID = :idDivida " +
            "              and vd.EXERCICIO_ID = :idExercicio ";
        if (TipoCobrancaTributario.COTA_UNICA.equals(malaDiretaIPTU.getTipoCobranca())) {
            sql += " and (coalesce(op.PROMOCIONAL,0) = :cotaunica and trunc(pvd.vencimento) >= trunc(sysdate))";
        } else if (TipoCobrancaTributario.PARCELADO.equals(malaDiretaIPTU.getTipoCobranca())) {
            sql += " and (coalesce(op.PROMOCIONAL,0) = :cotaunica)";
        }
        sql += " ) ";
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put("inicial", malaDiretaIPTU.getInscricaoInicial().trim());
        parameters.put("final", malaDiretaIPTU.getInscricaoFinal().trim());
        parameters.put("situacao", SituacaoParcela.EM_ABERTO.name());
        if (!TipoCobrancaTributario.NOTIFICACAO.equals(malaDiretaIPTU.getTipoCobranca())) {
            parameters.put("cotaunica", TipoCobrancaTributario.COTA_UNICA.equals(malaDiretaIPTU.getTipoCobranca()));
        }
        parameters.put("idExercicio", malaDiretaIPTU.getExercicio().getId());
        parameters.put("idDivida", configuracaoTributario.getDividaIPTU().getId());

        if (malaDiretaIPTU.getBairro() != null && malaDiretaIPTU.getBairro().getId() != null) {
            sql += " and vwend.id_bairro = :bairro ";
            parameters.put("bairro", malaDiretaIPTU.getBairro().getId());
        }
        if (malaDiretaIPTU.getPessoa() != null && malaDiretaIPTU.getPessoa().getId() != null) {
            sql += " and pp.pessoa_id = :pessoa ";
            parameters.put("pessoa", malaDiretaIPTU.getPessoa().getId());
        }
        if (malaDiretaIPTU.getTipoImovel() != null) {
            sql += "   and case " +
                "         when (select count(1) from construcao ct " +
                "               where ct.imovel_id = ci.id and coalesce(ct.cancelada,0) <> :ativo) >= 1 " +
                "         then '" + TipoImovel.PREDIAL.name() + "' " +
                "         else '" + TipoImovel.TERRITORIAL.name() + "' end = :tipoimovel ";
            parameters.put("ativo", true);
            parameters.put("tipoimovel", malaDiretaIPTU.getTipoImovel().name());
        }
        Query q = em.createNativeQuery(sql);
        for (String parametro : parameters.keySet()) {
            q.setParameter(parametro, parameters.get(parametro));
        }
        List<Long> toReturn = Lists.newArrayList();
        for (Object id : q.getResultList()) {
            toReturn.add(((BigDecimal) id).longValue());
        }
        return toReturn;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public List<DamMalaDireta> listaDamsMalaDireta(List<Long> listaIdsDosDams, String texto) {
        String sql = "SELECT dam.id as DAM_ID, " +
            "  dam.vencimento, " +
            "  dam.numerodam, " +
            "  dam.valororiginal, " +
            "  coalesce((SELECT sum(itempvd.VALOR) FROM itemdam " +
            "    INNER JOIN parcelavalordivida parcela ON parcela.id = itemdam.parcela_id " +
            "    inner join itemparcelavalordivida itempvd on itempvd.parcelavalordivida_id = parcela.id " +
            "    inner join itemvalordivida ivd on ivd.id = itempvd.ITEMVALORDIVIDA_ID " +
            "    inner join tributo on tributo.id = ivd.TRIBUTO_ID " +
            "    WHERE itemdam.dam_id = dam.id and tributo.TIPOTRIBUTO = 'IMPOSTO'),0) as imposto, " +
            "  coalesce((SELECT sum(itempvd.VALOR) FROM itemdam " +
            "    INNER JOIN parcelavalordivida parcela ON parcela.id = itemdam.parcela_id " +
            "    inner join itemparcelavalordivida itempvd on itempvd.parcelavalordivida_id = parcela.id " +
            "    inner join itemvalordivida ivd on ivd.id = itempvd.ITEMVALORDIVIDA_ID " +
            "    inner join tributo on tributo.id = ivd.TRIBUTO_ID " +
            "    WHERE itemdam.dam_id = dam.id and tributo.TIPOTRIBUTO = 'TAXA'),0) as taxa, " +
            "  dam.correcaomonetaria, " +
            "  dam.juros, " +
            "  dam.multa, " +
            "  dam.honorarios, " +
            "  dam.desconto, " +
            "  ((dam.valororiginal + dam.correcaomonetaria + dam.juros + dam.honorarios + dam.multa) - dam.desconto) AS valor, " +
            "  dam.codigobarras AS codigobarrasdigitos, " +
            "  (SUBSTR(dam.codigoBarras, 0, 11) || SUBSTR(dam.codigoBarras, 15, 11) || SUBSTR(dam.codigoBarras, 29, 11) || SUBSTR(dam.codigoBarras, 43, 11)) AS codigobarras, " +
            "  CI.INSCRICAOCADASTRAL, " +
            "  COALESCE(PJ.CNPJ,PF.CPF) AS CPF_CNPJ, " +
            "  COALESCE(PJ.RAZAOSOCIAL,PF.NOME) AS CONTRIBUINTE, " +
            "  (select vwenderecobci.logradouro || ', N°' || vwenderecobci.numero " +
            "    from vwenderecobci " +
            "    WHERE vwenderecobci.cadastroimobiliario_id = CALCULOIPTU.CADASTROIMOBILIARIO_ID and rownum = 1) AS logradouro, " +
            "  (select vwenderecobci.logradouro || ', N°' || vwenderecobci.numero ||  ', Complemento: ' || ' ' || vwenderecobci.complemento " +
            "    from vwenderecobci " +
            "    WHERE vwenderecobci.cadastroimobiliario_id = CALCULOIPTU.CADASTROIMOBILIARIO_ID and rownum = 1) AS logradouroComplemento, " +
            "  (select vwenderecobci.bairro as endereco " +
            "    from vwenderecobci " +
            "    WHERE vwenderecobci.cadastroimobiliario_id = CALCULOIPTU.CADASTROIMOBILIARIO_ID and rownum = 1) AS bairro, " +
            "  EX.ANO, " +
            "  (SELECT listagg(parcela.SEQUENCIAPARCELA, '/') " +
            "    WITHIN GROUP (ORDER BY parcela.SEQUENCIAPARCELA) " +
            "    FROM itemdam " +
            "    INNER JOIN parcelavalordivida parcela ON parcela.id = itemdam.parcela_id " +
            "    WHERE itemdam.dam_id = dam.id) as parcelas, " +
            "  (select vwenderecobci.cep as cep " +
            "    from vwenderecobci " +
            "    WHERE vwenderecobci.cadastroimobiliario_id = CALCULOIPTU.CADASTROIMOBILIARIO_ID and rownum = 1) AS cep, " +
            " dam.qrcodepix, " +
            " pessoa.id " +
            "FROM dam " +
            "INNER JOIN VALORDIVIDA ON VALORDIVIDA.ID = " +
            "  (SELECT MAX(parcela.valordivida_id) " +
            "  FROM itemdam " +
            "  INNER JOIN parcelavalordivida parcela ON parcela.id = itemdam.parcela_id " +
            "  WHERE itemdam.dam_id = dam.id) " +
            "INNER JOIN CALCULOIPTU CALCULOIPTU ON CALCULOIPTU.ID = VALORDIVIDA.CALCULO_ID " +
            "INNER JOIN CALCULO CALCULOSUPER ON CALCULOSUPER.ID = CALCULOIPTU.ID " +
            "INNER JOIN PROCESSOCALCULOIPTU  PROCESSO ON PROCESSO.ID = CALCULOIPTU.PROCESSOCALCULOIPTU_ID " +
            "INNER JOIN PROCESSOCALCULO ON PROCESSOCALCULO.ID = PROCESSO.ID " +
            "INNER JOIN DIVIDA ON DIVIDA.ID = VALORDIVIDA.DIVIDA_ID " +
            "INNER JOIN CONFIGURACAODAM ON CONFIGURACAODAM.ID = DIVIDA.CONFIGURACAODAM_ID " +
            "INNER JOIN EXERCICIO EX ON EX.ID = PROCESSOCALCULO.EXERCICIO_ID " +
            "INNER JOIN CADASTROIMOBILIARIO CI ON CI.ID = CALCULOIPTU.CADASTROIMOBILIARIO_ID " +
            "INNER JOIN pessoa ON pessoa.id = " +
            "  (SELECT MAX(calculopessoa.pessoa_id) " +
            "  FROM itemdam " +
            "  INNER JOIN parcelavalordivida parcela ON parcela.id = itemdam.parcela_id " +
            "  INNER JOIN valordivida ON valordivida.id = parcela.valordivida_id " +
            "  INNER JOIN calculo ON calculo.id = valordivida.calculo_id " +
            "  INNER JOIN calculopessoa ON calculopessoa.calculo_id = calculo.id " +
            "  WHERE itemdam.dam_id = dam.id) " +
            "LEFT JOIN pessoafisica pf ON pf.id = pessoa.id " +
            "LEFT JOIN pessoajuridica pj ON pj.id = pessoa.id " +
            "where dam.id in (:listaIds) " +
            "order by CI.inscricaoCadastral";

        Query q = em.createNativeQuery(sql);
        q.setParameter("listaIds", listaIdsDosDams);
        List<Object[]> lista = q.getResultList();
        List<DamMalaDireta> retorno = Lists.newArrayList();
        for (Object[] obj : lista) {
            DamMalaDireta dam = new DamMalaDireta();
            dam.setIdDam(((BigDecimal) obj[0]).longValue());
            dam.setVencimento((Date) obj[1]);
            dam.setNumeroDam((String) obj[2]);
            dam.setValorOriginal((BigDecimal) obj[3]);
            dam.setValorImposto((BigDecimal) obj[4]);
            dam.setValorTaxa((BigDecimal) obj[5]);
            dam.setValorCorrecao((BigDecimal) obj[6]);
            dam.setValorJuros((BigDecimal) obj[7]);
            dam.setValorMulta((BigDecimal) obj[8]);
            dam.setValorHonorarios((BigDecimal) obj[9]);
            dam.setValorDesconto((BigDecimal) obj[10]);
            dam.setValorDam((BigDecimal) obj[11]);
            dam.setCodigoBarrasDigitos((String) obj[12]);
            dam.setCodigoBarras((String) obj[13]);
            dam.setInscricaoCadastral((String) obj[14]);
            dam.setCpfCnpj((String) obj[15]);
            dam.setContributinte((String) obj[16]);

            EnderecoCorreio ec = buscarEnderecoProprietarioDomicilioFiscal(((BigDecimal) obj[24]).longValue());
            dam.setLogradouro(ec != null ? ec.getLogradouro() : (String) obj[17]);
            dam.setLogradouroComplemento(ec != null
                ? (ec.getLogradouro() + ", N°" + ec.getNumero() + ", Complemento: " + ec.getComplemento())
                : (String) obj[18]);
            dam.setBairro(ec != null ? ec.getBairro() : (String) obj[19]);

            dam.setExercicio(((BigDecimal) obj[20]).intValue());
            dam.setParcelas((String) obj[21]);
            dam.setCep(ec != null ? ec.getCep() : (obj[22] != null ? (String) obj[22] : null));
            dam.setQrCodePix(obj[23] != null ? (String) obj[23] : null);
            dam.setTexto(texto);
            retorno.add(dam);
        }
        return retorno;
    }

    private EnderecoCorreio buscarEnderecoProprietarioDomicilioFiscal(Long idPessoa) {
        String sql = " SELECT EC.ID, " +
            " EC.LOGRADOURO, " +
            " EC.NUMERO, " +
            " EC.BAIRRO, " +
            " EC.CEP, " +
            " EC.COMPLEMENTO " +
            " FROM ENDERECOCORREIO EC " +
            " INNER JOIN PESSOA_ENDERECOCORREIO PEC on EC.ID = PEC.ENDERECOSCORREIO_ID " +
            " WHERE EC.TIPOENDERECO = :tipoEndereco " +
            "  AND PEC.PESSOA_ID = :idPessoa";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idPessoa", idPessoa);
        q.setParameter("tipoEndereco", TipoEndereco.DOMICILIO_FISCAL.name());
        List<Object> resultado = q.getResultList();
        if (resultado != null && !resultado.isEmpty()) {
            Object[] obj = (Object[]) resultado.get(0);
            EnderecoCorreio ec = new EnderecoCorreio();
            ec.setId(((BigDecimal) obj[0]).longValue());
            ec.setLogradouro(obj[1] != null ? obj[1].toString() : "");
            ec.setNumero(obj[2] != null ? obj[2].toString() : "");
            ec.setBairro(obj[3] != null ? obj[3].toString() : "");
            ec.setCep(obj[4] != null ? obj[4].toString() : "");
            ec.setComplemento(obj[5] != null ? obj[5].toString() : "");
            return ec;
        }
        return null;
    }

    public ConfiguracaoTributarioFacade getConfiguracaoTributarioFacade() {
        return configuracaoTributarioFacade;
    }

    public JdbcMalaDiretaIptuDAO getDaoMalaDiretaIptu() {
        if (daoMalaDiretaIptu == null) {
            ApplicationContext ap = ContextLoader.getCurrentWebApplicationContext();
            daoMalaDiretaIptu = (JdbcMalaDiretaIptuDAO) ap.getBean("malaDiretaIptuDAO");
        }
        return daoMalaDiretaIptu;
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 10)
    public Future<Map<CadastroImobiliario, List<ResultadoParcela>>> buscarDebitosDaMalaDiretaDeIPTU
        (ConfiguracaoTributario configuracaoTributario, MalaDiretaIPTU
            malaDiretaIPTU, List<Long> idsCadastroImobiliarios) {
        Map<CadastroImobiliario, List<ResultadoParcela>> toReturn = Maps.newHashMap();

        for (Long idCadastro : idsCadastroImobiliarios) {
            CadastroImobiliario cadastroImobiliario = em.find(CadastroImobiliario.class, idCadastro);
            if (!TipoCobrancaTributario.NOTIFICACAO.equals(malaDiretaIPTU.getTipoCobranca())) {
                ConsultaParcela consultaParcela = new ConsultaParcela();
                consultaParcela.addParameter(ConsultaParcela.Campo.EXERCICIO_ANO, ConsultaParcela.Operador.IGUAL, malaDiretaIPTU.getExercicio().getAno());
                consultaParcela.addParameter(ConsultaParcela.Campo.CADASTRO_ID, ConsultaParcela.Operador.IGUAL, cadastroImobiliario.getId());
                consultaParcela.addParameter(ConsultaParcela.Campo.DIVIDA_ID, ConsultaParcela.Operador.IGUAL, configuracaoTributario.getDividaIPTU().getId());
                consultaParcela.addParameter(ConsultaParcela.Campo.PARCELA_SITUACAO, ConsultaParcela.Operador.IGUAL, SituacaoParcela.EM_ABERTO);

                if (TipoCobrancaTributario.COTA_UNICA.equals(malaDiretaIPTU.getTipoCobranca())) {
                    consultaParcela.addParameter(ConsultaParcela.Campo.PROMOCIAL, ConsultaParcela.Operador.IGUAL, true);
                    consultaParcela.addParameter(ConsultaParcela.Campo.PARCELA_VENCIMENTO, ConsultaParcela.Operador.MAIOR_IGUAL, new Date());
                } else if (TipoCobrancaTributario.PARCELADO.equals(malaDiretaIPTU.getTipoCobranca())) {
                    consultaParcela.addParameter(ConsultaParcela.Campo.PROMOCIAL, ConsultaParcela.Operador.IGUAL, false);
                }
                List<ResultadoParcela> parcelas = consultaParcela.executaConsulta().getResultados();
                if (!parcelas.isEmpty()) {
                    if (filtrarParcelasPorValor(malaDiretaIPTU.getValorInicial(), malaDiretaIPTU.getValorFinal(), parcelas)) {
                        toReturn.put(cadastroImobiliario, parcelas);
                    }
                }
            } else {
                toReturn.put(cadastroImobiliario, Lists.newArrayList());
            }
        }
        return new AsyncResult<>(toReturn);
    }

    private boolean filtrarParcelasPorValor(BigDecimal valorInicial, BigDecimal
        valorFinal, List<ResultadoParcela> parcelas) {
        boolean gerar = true;
        BigDecimal valorDebito = getValorTotalDebito(parcelas);
        if (valorInicial.compareTo(BigDecimal.ZERO) > 0) {
            gerar = valorDebito.compareTo(valorInicial) >= 0;
        }
        if (gerar && valorFinal.compareTo(BigDecimal.ZERO) > 0) {
            gerar = valorDebito.compareTo(valorFinal) <= 0;
        }
        return gerar;

    }

    private List<ParcelaMalaDiretaIPTU> criarParcelasMalaDiretaIPTU(CadastroMalaDiretaIPTU
                                                                        cadastroMalaDiretaIPTU, List<ResultadoParcela> debitos) {
        List<ParcelaMalaDiretaIPTU> toReturn = Lists.newArrayList();

        for (ResultadoParcela resultadoParcela : debitos) {
            ParcelaMalaDiretaIPTU parcelaMalaDiretaIPTU = new ParcelaMalaDiretaIPTU();
            parcelaMalaDiretaIPTU.setCadastroMalaDiretaIPTU(cadastroMalaDiretaIPTU);
            parcelaMalaDiretaIPTU.setParcela(em.find(ParcelaValorDivida.class, resultadoParcela.getIdParcela()));
            toReturn.add(parcelaMalaDiretaIPTU);
        }

        return toReturn;
    }

    public BigDecimal getValorTotal(List<ResultadoParcela> parcelas) {
        BigDecimal toReturn = BigDecimal.ZERO;
        for (ResultadoParcela parcela : parcelas) {
            toReturn = toReturn.add(parcela.getValorTotal());
        }

        return toReturn;
    }

    public BigDecimal getValorOriginal(List<ResultadoParcela> parcelas) {
        BigDecimal toReturn = BigDecimal.ZERO;
        for (ResultadoParcela parcela : parcelas) {
            toReturn = toReturn.add(parcela.getValorOriginal());
        }

        return toReturn;
    }

    public BigDecimal getValorJuros(List<ResultadoParcela> parcelas) {
        BigDecimal toReturn = BigDecimal.ZERO;
        for (ResultadoParcela parcela : parcelas) {
            toReturn = toReturn.add(parcela.getValorJuros());
        }

        return toReturn;
    }

    public BigDecimal getValorMulta(List<ResultadoParcela> parcelas) {
        BigDecimal toReturn = BigDecimal.ZERO;
        for (ResultadoParcela parcela : parcelas) {
            toReturn = toReturn.add(parcela.getValorMulta());
        }

        return toReturn;
    }

    public BigDecimal getValorCorrecao(List<ResultadoParcela> parcelas) {
        BigDecimal toReturn = BigDecimal.ZERO;
        for (ResultadoParcela parcela : parcelas) {
            toReturn = toReturn.add(parcela.getValorCorrecao());
        }

        return toReturn;
    }

    public BigDecimal getValorDesconto(List<ResultadoParcela> parcelas) {
        BigDecimal toReturn = BigDecimal.ZERO;
        for (ResultadoParcela parcela : parcelas) {
            toReturn = toReturn.add(parcela.getValorDesconto());
        }

        return toReturn;
    }

    public BigDecimal getValorHonorarios(List<ResultadoParcela> parcelas) {
        BigDecimal toReturn = BigDecimal.ZERO;
        for (ResultadoParcela parcela : parcelas) {
            toReturn = toReturn.add(parcela.getValorHonorarios());
        }

        return toReturn;
    }

    private void criarDamCadastroMalaDireta(List<ResultadoParcela> parcelas, UsuarioSistema usuario) {
        for (ResultadoParcela parcela : parcelas) {
            damFacade.gerarDAMUnicoViaApi(usuario, parcela);
        }
    }

    public CadastroMalaDiretaIPTU gerarCadastroMalaDiretaIPTU(MalaDiretaIPTU malaDiretaIPTU, CadastroImobiliario
        cadastroImobiliario, List<ResultadoParcela> debitosDoCadastro, UsuarioSistema usuario) throws
        HeuristicRollbackException, HeuristicMixedException, NotSupportedException, RollbackException, SystemException {
        CadastroMalaDiretaIPTU cadastroMalaDiretaIPTU = new CadastroMalaDiretaIPTU();
        cadastroMalaDiretaIPTU.setMalaDiretaIPTU(malaDiretaIPTU);
        cadastroMalaDiretaIPTU.setCadastroImobiliario(cadastroImobiliario);
        if (!TipoCobrancaTributario.NOTIFICACAO.equals(malaDiretaIPTU.getTipoCobranca())) {
            cadastroMalaDiretaIPTU.setParcelaMalaDiretaIPTU(criarParcelasMalaDiretaIPTU(cadastroMalaDiretaIPTU, debitosDoCadastro));
            criarDamCadastroMalaDireta(debitosDoCadastro, usuario);
        }
        getDaoMalaDiretaIptu().persisteCadastroMalaDiretaIptu(cadastroMalaDiretaIPTU);
        return cadastroMalaDiretaIPTU;
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 10)
    public Future<List<CadastroMalaDiretaIPTU>> criarCadastrosMalaDireraIPTU(MalaDiretaIPTU malaDiretaIPTU,
                                                                             Map<CadastroImobiliario, List<ResultadoParcela>> mapaParcelasPorCadastro,
                                                                             AssistenteBarraProgresso assistenteBarraProgresso, UsuarioSistema usuario) throws
        HeuristicRollbackException, HeuristicMixedException, NotSupportedException, RollbackException, SystemException {
        List<CadastroMalaDiretaIPTU> toReturn = Lists.newArrayList();

        for (CadastroImobiliario cadastroImobiliario : mapaParcelasPorCadastro.keySet()) {
            boolean gerar = true;
            if (!TipoCobrancaTributario.NOTIFICACAO.equals(malaDiretaIPTU.getTipoCobranca())) {
                BigDecimal valorDebito = getValorTotalDebito(mapaParcelasPorCadastro.get(cadastroImobiliario));
                if (malaDiretaIPTU.getValorInicial().compareTo(BigDecimal.ZERO) > 0) {
                    gerar = valorDebito.compareTo(malaDiretaIPTU.getValorInicial()) >= 0;
                }
                if (gerar && malaDiretaIPTU.getValorFinal().compareTo(BigDecimal.ZERO) > 0) {
                    gerar = valorDebito.compareTo(malaDiretaIPTU.getValorFinal()) <= 0;
                }
            }
            if (gerar) {
                toReturn.add(gerarCadastroMalaDiretaIPTU(malaDiretaIPTU, cadastroImobiliario,
                    mapaParcelasPorCadastro.get(cadastroImobiliario), usuario));
            }
            assistenteBarraProgresso.conta();
        }
        return new AsyncResult<>(toReturn);
    }

    private BigDecimal getValorTotalDebito(List<ResultadoParcela> parcelas) {
        return (getValorOriginal(parcelas).add(getValorJuros(parcelas)).add(getValorMulta(parcelas)).add(getValorCorrecao(parcelas)).add(getValorHonorarios(parcelas))).subtract(getValorDesconto(parcelas));
    }

    public MalaDiretaIPTU salvarRetornando(MalaDiretaIPTU malaDiretaIPTU) {
        return em.merge(malaDiretaIPTU);
    }

    public Integer buscarQuantidadeDeCadastrosNaMalaDireta(MalaDiretaIPTU malaDiretaIPTU) {
        String sql = " select count(1) from cadastromaladiretaiptu cad " +
            " where cad.maladiretaiptu_id = :maladiretaiptu_id ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("maladiretaiptu_id", malaDiretaIPTU.getId());
        if (!q.getResultList().isEmpty()) {
            return ((BigDecimal) q.getSingleResult()).intValue();
        }
        return 0;
    }

    public int buscarQuantidadeCadastrosDaMalaDiretaDeIPTU(MalaDiretaIPTU
                                                               malaDiretaIPTU, FiltroCadastroMalaDiretaIPTU filtroCadastroMalaDiretaIPTU) {
        String sql = " select count(1) " +
            "    from cadastromaladiretaiptu cmi " +
            "   inner join cadastroimobiliario ci on ci.id = cmi.cadastroimobiliario_id " +
            " where cmi.maladiretaiptu_id = :maladiretaiptu_id ";
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put("maladiretaiptu_id", malaDiretaIPTU.getId());
        if (filtroCadastroMalaDiretaIPTU.getInscricaoInicial() != null && !filtroCadastroMalaDiretaIPTU.getInscricaoInicial().trim().isEmpty()) {
            sql += " and ci.inscricaocadastral >= :inscricao_inicial ";
            parameters.put("inscricao_inicial", filtroCadastroMalaDiretaIPTU.getInscricaoInicial().trim());
        }
        if (filtroCadastroMalaDiretaIPTU.getInscricaoFinal() != null && !filtroCadastroMalaDiretaIPTU.getInscricaoFinal().trim().isEmpty()) {
            sql += " and ci.inscricaocadastral <= :inscricao_final ";
            parameters.put("inscricao_final", filtroCadastroMalaDiretaIPTU.getInscricaoFinal());
        }
        Query q = em.createNativeQuery(sql);
        for (String param : parameters.keySet()) {
            q.setParameter(param, parameters.get(param));
        }
        if (!q.getResultList().isEmpty()) {
            return ((BigDecimal) q.getSingleResult()).intValue();
        }
        return 0;
    }

    public List<CadastroMalaDiretaIPTU> buscarCadastrosDaMalaDiretaDeIPTU(MalaDiretaIPTU
                                                                              malaDiretaIPTU, FiltroCadastroMalaDiretaIPTU filtroCadastroMalaDiretaIPTU,
                                                                          int indexInicial, int quantidade) {
        String sql = " select cmi.* " +
            "    from cadastromaladiretaiptu cmi " +
            "   inner join cadastroimobiliario ci on ci.id = cmi.cadastroimobiliario_id " +
            " where cmi.maladiretaiptu_id = :maladiretaiptu_id ";
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put("maladiretaiptu_id", malaDiretaIPTU.getId());
        if (filtroCadastroMalaDiretaIPTU.getInscricaoInicial() != null && !filtroCadastroMalaDiretaIPTU.getInscricaoInicial().trim().isEmpty()) {
            sql += " and ci.inscricaocadastral >= :inscricao_inicial ";
            parameters.put("inscricao_inicial", filtroCadastroMalaDiretaIPTU.getInscricaoInicial().trim());
        }
        if (filtroCadastroMalaDiretaIPTU.getInscricaoFinal() != null && !filtroCadastroMalaDiretaIPTU.getInscricaoFinal().trim().isEmpty()) {
            sql += " and ci.inscricaocadastral <= :inscricao_final ";
            parameters.put("inscricao_final", filtroCadastroMalaDiretaIPTU.getInscricaoFinal());
        }
        sql += " order by ci.inscricaocadastral ";
        Query q = em.createNativeQuery(sql, CadastroMalaDiretaIPTU.class);
        for (String param : parameters.keySet()) {
            q.setParameter(param, parameters.get(param));
        }
        q.setFirstResult(indexInicial);
        q.setMaxResults(quantidade);
        return q.getResultList();
    }

    public CadastroMalaDiretaIPTU recuperarCadastroMalaDiretaIPTU(Long id) {
        return em.find(CadastroMalaDiretaIPTU.class, id);
    }
}
