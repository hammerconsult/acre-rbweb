package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.AutorizacaoTributario;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.negocios.tributario.auxiliares.AssistenteEfetivacaoIPTU;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.negocios.tributario.dao.JdbcDividaAtivaDAO;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;

import javax.annotation.Resource;
import javax.ejb.*;
import javax.persistence.Query;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Stateless

@TransactionManagement(TransactionManagementType.BEAN)
public class GeraValorDividaIPTU extends ValorDividaFacade {

    private final Logger log = LoggerFactory.getLogger(GeraValorDividaIPTU.class);
    private JdbcDividaAtivaDAO dao;
    private Exercicio exercicio;
    private Map<Exercicio, List<OpcaoPagamentoDivida>> opcoesPorExercicio;
    private AssistenteEfetivacaoIPTU assistente;
    @EJB
    private MoedaFacade moedaFacade;
    @EJB
    private CadastroImobiliarioFacade cadastroImobiliarioFacade;
    @EJB
    private ParametroParcelamentoFacade parametroParcelamentoFacade;
    private Map<OpcaoPagamento, Boolean> adimplente;
    private Divida dividaIPTU = null;
    private List<Long> idsDividasIptu = null;
    private Map<OpcaoPagamento, List<ConsultaParcela>> mapaConsultas;
    @EJB
    private UsuarioSistemaFacade usuarioSistemaFacade;
    @Resource
    private SessionContext context;
    @Resource
    private UserTransaction userTransaction;
    private Date hoje;

    public GeraValorDividaIPTU() {
        super(true);
    }

    public Boolean processoLancado(ProcessoCalculo processoCalculo) {
        return false;
    }


    private JdbcDividaAtivaDAO getDAO() {
        if (dao == null) {
            ApplicationContext ap = ContextLoader.getCurrentWebApplicationContext();
            dao = (JdbcDividaAtivaDAO) ap.getBean("dividaAtivaDAO");
        }
        return dao;
    }

    public CalculoIPTU buscarCalculoIPTU(Long id) {
        CalculoIPTU calculo = em.find(CalculoIPTU.class, id);
        return calculo;

    }

    public MoedaFacade getMoedaFacade() {
        return moedaFacade;
    }

    @Asynchronous
    public void geraDebito(List<Long> ids, AssistenteEfetivacaoIPTU assistente) {
        try {
            userTransaction = context.getUserTransaction();
            this.assistente = assistente;
            this.hoje = new Date();
            this.exercicio = assistente.getExercicio();
            assistente.addLog("Iniciando o lançamento de novos débitos de IPTU");
            BigDecimal ufmVigente = moedaFacade.recuperaValorUFMPorExercicio(exercicio.getAno());
            BigDecimal meioUfm = ufmVigente.divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);
            Divida dividaIPTU = configuracaoTributarioFacade.retornaUltimo().getDividaIPTU();

            if (idsDividasIptu == null || idsDividasIptu.isEmpty()) {
                idsDividasIptu = Lists.newArrayList();
                idsDividasIptu.addAll(parametroParcelamentoFacade.buscarIdsDividasParcelamento(dividaIPTU));
                idsDividasIptu.add(dividaIPTU.getId());
                idsDividasIptu.add(dividaIPTU.getDivida().getId());
            }

            validaOpcoesPagamento(dividaIPTU, exercicio);
            for (Long id : ids) {
                try {
                    userTransaction.begin();
                    CalculoIPTU calculo = buscarCalculoIPTU(id);
                    if (calculo != null
                        && validarLancamentosAnteriores(calculo, assistente)) {
                        cancelarDebitosExistentes(calculo);
                        if (calculo.getValorEfetivo().compareTo(meioUfm) >= 0) {
                            gerarIPTU(calculo);
                        }
                    }
                    userTransaction.commit();

                } catch (Exception e) {
                    try {
                        userTransaction.rollback();
                    } catch (SystemException e1) {
                        e1.printStackTrace();
                    }
                    log.error("Não foi possivel finalizar a geração de debito de IPTU ", e);
                } finally {
                    assistente.contarLinha();
                }
            }
        } finally {
            assistente.finalizar();
            assistente.addLog("Finalizando o lançamento de novos débitos de IPTU");
        }
    }

    private void cancelarDebitosExistentes(CalculoIPTU calculo) {
        List<Long> dividasDoCadastro = getDividasDoCadastro(calculo.getCadastro().getId(),
            calculo.getProcessoCalculo().getExercicio().getAno());
        for (Long idParcela : dividasDoCadastro) {
            SituacaoParcela situacao = SituacaoParcela.CANCELADO_RECALCULO;
            if (calculo.getIsencaoCadastroImobiliario() != null) {
                situacao = SituacaoParcela.CANCELADO_ISENCAO;
            }
            getDAO().persistirSituacaoParcelaValorDividaComMesmaReferencia(idParcela, situacao, hoje);
        }

    }

    private boolean validarLancamentosAnteriores(CalculoIPTU calculo, AssistenteEfetivacaoIPTU assistente) {
        List<String> situacoes = Lists.newArrayList(
            SituacaoParcela.INSCRITA_EM_DIVIDA_ATIVA.name(),
            SituacaoParcela.COMPENSACAO.name(),
            SituacaoParcela.SUSPENSAO.name());

        if (!usuarioSistemaFacade.validaAutorizacaoUsuario(assistente.getUsuarioSistema(), AutorizacaoTributario.PERMITIR_LANCAMENTO_IPTU_PAGO)) {
            situacoes.add(SituacaoParcela.PAGO.name());
            situacoes.add(SituacaoParcela.BAIXADO.name());
        }

        String sql = "select pvd.id from parcelavalordivida pvd\n" +
            "inner join situacaoparcelavalordivida spvd on spvd.id = pvd.situacaoatual_id\n" +
            "inner join valordivida vd on vd.id = pvd.valordivida_id\n" +
            "inner join calculoiptu iptu on iptu.id = vd.calculo_id\n" +
            "where iptu.cadastroimobiliario_id = :cadastro\n" +
            "and spvd.situacaoparcela  in (:parcelas) and vd.exercicio_id = :exercicio ";
        Query q = em.createNativeQuery(sql)
            .setParameter("cadastro", calculo.getCadastro().getId())
            .setParameter("exercicio", calculo.getProcessoCalculo().getExercicio().getId())
            .setParameter("parcelas", situacoes)
            .setMaxResults(1);

        return q.getResultList().isEmpty();
    }


    public void validaOpcoesPagamento(Divida divida, Exercicio exercicio) throws IllegalArgumentException {
        List<OpcaoPagamentoDivida> opcoesPagamento = Lists.newArrayList();
        if (opcoesPorExercicio == null) {
            opcoesPorExercicio = Maps.newHashMap();
        }
        if (opcoesPorExercicio.get(exercicio) != null) {
            opcoesPagamento = opcoesPorExercicio.get(exercicio);
        } else {
            Date primeiroDiaAno = DataUtil.dataSemHorario(DataUtil.getPrimeiroDiaAno(exercicio.getAno()));
            Date ultimoDiaAno = DataUtil.dataSemHorario(DataUtil.getUltimoDiaAno(exercicio.getAno()));
            Query q = em.createQuery(" from OpcaoPagamentoDivida op "
                + " where op.divida = :divida "
                + " and coalesce(trunc(op.inicioVigencia), current_date) <= :inicioVigencia "
                + " and coalesce(trunc(op.finalVigencia), current_date) >= :finalVigencia ");
            q.setParameter("divida", divida);
            q.setParameter("inicioVigencia", primeiroDiaAno);
            q.setParameter("finalVigencia", ultimoDiaAno);
            opcoesPagamento = q.getResultList();

            if (opcoesPagamento.isEmpty()) {
                throw new IllegalArgumentException("Nenhuma opção de pagamento válida para a dívida selecionada: " + divida.getDescricao());
            }
            opcoesPorExercicio.put(exercicio, opcoesPagamento);
        }
    }

    public BigDecimal buscarValorPagoJaLancadoIPTU(CalculoIPTU calculoIPTU) {
        List<ResultadoParcela> resultados = new ConsultaParcela()
            .addParameter(ConsultaParcela.Campo.CADASTRO_ID, ConsultaParcela.Operador.IGUAL, calculoIPTU.getCadastroImobiliario().getId())
            .addParameter(ConsultaParcela.Campo.EXERCICIO_ANO, ConsultaParcela.Operador.IGUAL, calculoIPTU.getProcessoCalculoIPTU().getExercicio().getAno())
            .addParameter(ConsultaParcela.Campo.PARCELA_SITUACAO, ConsultaParcela.Operador.IGUAL, SituacaoParcela.PAGO)
            .executaConsulta().getResultados();
        if (resultados.isEmpty()) {
            return BigDecimal.ZERO;
        }
        BigDecimal total = BigDecimal.ZERO;
        for (ResultadoParcela resultado : resultados) {
            total = total.add(resultado.getValorTotal());
        }
        return total;
    }


    private void gerarIPTU(CalculoIPTU calculoIPTU) throws Exception {
        if (calculoIPTU.getIsento()) {
            this.assistente.addLog("Cadastro " + calculoIPTU.getCadastroImobiliario().getInscricaoCadastral() + " isento de IPTU ");
            return;
        }
        if (calculoIPTU.getConstrucao() != null && calculoIPTU.getConstrucao().getEnglobado() != null) return;
        gerarValorDividaIPTU(calculoIPTU);
    }

    public List<Long> getDividasDoCadastro(Long idCadastro, Integer ano) {

        List<String> situacoesDeveCancelar = Lists.newArrayList(SituacaoParcela.EM_ABERTO.name());

        List<String> situacoesNaoPodeCancelar = Lists.newArrayList(
            SituacaoParcela.INSCRITA_EM_DIVIDA_ATIVA.name(),
            SituacaoParcela.COMPENSACAO.name(),
            SituacaoParcela.SUSPENSAO.name());

        if (!usuarioSistemaFacade.validaAutorizacaoUsuario(assistente.getUsuarioSistema(), AutorizacaoTributario.PERMITIR_LANCAMENTO_IPTU_PAGO)) {
            situacoesNaoPodeCancelar.add(SituacaoParcela.PAGO.name());
            situacoesNaoPodeCancelar.add(SituacaoParcela.BAIXADO.name());
        }

        String sql = "select TRUNC(pvd.id) from parcelavalordivida pvd " +
            "inner join situacaoparcelavalordivida spvd on spvd.id = pvd.situacaoAtual_id " +
            "inner join valordivida vd on vd.id = pvd.valordivida_id " +
            "inner join exercicio ex on ex.id = vd.exercicio_id " +
            "inner join calculoiptu iptu on iptu.id = vd.calculo_id " +
            "inner join cadastroimobiliario ci on ci.id = iptu.cadastroimobiliario_id " +
            "where spvd.situacaoparcela in (:situacaoCancelar) " +
            "and ci.id = :cadastro " +
            "and ex.ano = :exercicio " +
            " and not exists  " +
            "( " +
            " select parcela.id from parcelavalordivida parcela " +
            "  inner join situacaoparcelavalordivida situacao on situacao.id = parcela.situacaoAtual_id " +
            "  where situacao.situacaoparcela in (:situacaoNaoCancelar) AND parcela.valordivida_id = vd.id " +
            ")";

        Query q = em.createNativeQuery(sql);
        q.setParameter("cadastro", idCadastro);
        q.setParameter("exercicio", ano);
        q.setParameter("situacaoCancelar", situacoesDeveCancelar);
        q.setParameter("situacaoNaoCancelar", situacoesNaoPodeCancelar);

        List<Long> ids = new java.util.ArrayList<>();
        java.util.Iterator res = q.getResultList().iterator();

        try {
            while (res.hasNext()) {
                ids.add(new Long(res.next().toString()));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return ids;
    }

    private void gerarValorDividaIPTU(CalculoIPTU calculoIPTU) throws Exception {
        this.assistente.addLog("Gerando débito para o cadastro " + calculoIPTU.getCadastroImobiliario().getInscricaoCadastral());
        BigDecimal valorEnglobado = calculaValorEnglobadoIPTU(calculoIPTU.getConstrucao());
        ValorDivida valorDivida = inicializaValorDivida(calculoIPTU.getValorEfetivo().add(valorEnglobado), calculoIPTU, calculoIPTU.getProcessoCalculoIPTU().getDivida(), calculoIPTU.getProcessoCalculoIPTU().getExercicio());
        lancaItem(valorDivida);
        if (!valorDivida.getItemValorDividas().isEmpty()) {
            lancaOpcaoPagamento(valorDivida, opcoesPorExercicio.get(calculoIPTU.getProcessoCalculoIPTU().getExercicio()));
        }
        em.persist(valorDivida);
    }

    private BigDecimal calculaValorEnglobadoIPTU(Construcao construcao) {
        Query qValorEnglobado = em.createQuery("select coalesce(sum(c.valorEfetivo), 0) FROM CalculoIPTU c where c.construcao.englobado = :construcao");
        qValorEnglobado.setParameter("construcao", construcao);
        qValorEnglobado.setMaxResults(1);
        return (BigDecimal) qValorEnglobado.getSingleResult();
    }

    @Override
    protected void lancaItem(ValorDivida valorDivida) {
        for (ItemCalculoIPTU itemCalculo : getItensCalculo((CalculoIPTU) valorDivida.getCalculo())) {
            if (itemCalculo.getEvento().getEventoCalculo().getTributo() != null || (!itemCalculo.getIsento() && itemCalculo.getEvento().getEventoCalculo().getDescontoSobre() == null)) {
                ItemValorDivida itemValorDivida = new ItemValorDivida();
                itemValorDivida.setTributo(itemCalculo.getEvento().getEventoCalculo().getTributo());
                itemValorDivida.setValor(itemCalculo.getValorEfetivo().setScale(2, RoundingMode.HALF_EVEN));
                itemValorDivida.setValorDivida(valorDivida);
                itemValorDivida.setIsento(itemCalculo.getIsento());
                itemValorDivida.setImune(itemCalculo.getImune());
                valorDivida.getItemValorDividas().add(itemValorDivida);
            }
        }
    }

    private List<ItemCalculoIPTU> getItensCalculo(CalculoIPTU calculo) {
        return em.createQuery("select i from " +
                " ItemCalculoIPTU  i where i.calculoIPTU = :calculo")
            .setParameter("calculo", calculo).getResultList();
    }

    private Divida getDividaIptu() {
        if (dividaIPTU == null) {
            dividaIPTU = configuracaoTributarioFacade.retornaUltimo().getDividaIPTU();
        }
        return dividaIPTU;
    }

    private List<Long> getIdsDividasIptuDeParcelamento() {
        return parametroParcelamentoFacade.buscarIdsDividasParcelamento(getDividaIptu());
    }

    @Override
    protected void lancaOpcaoPagamento(ValorDivida valorDivida, List<OpcaoPagamentoDivida> opcoesPagamento) throws Exception {
        mapaConsultas = Maps.newHashMap();
        this.adimplente = Maps.newHashMap();
        for (OpcaoPagamentoDivida opcaoPagamentoDivida : opcoesPagamento) {
            consideraDesconto = opcaoPagamentoDivida.getOpcaoPagamento().getDataVerificacaoDebito() != null;

            mapaConsultas.put(opcaoPagamentoDivida.getOpcaoPagamento(), Lists.newArrayList());
            mapaConsultas.get(opcaoPagamentoDivida.getOpcaoPagamento()).add(criarConsultaParcela(opcaoPagamentoDivida.getOpcaoPagamento().getDataVerificacaoDebito(),
                (CalculoIPTU) valorDivida.getCalculo(), getIdsDividasIptuDeParcelamento()));
            ConsultaParcela consultaParcela = criarConsultaParcela(opcaoPagamentoDivida.getOpcaoPagamento().getDataVerificacaoDebito(),
                (CalculoIPTU) valorDivida.getCalculo(), Lists.newArrayList(getDividaIptu().getId()));
            consultaParcela.addParameter(ConsultaParcela.Campo.EXERCICIO_ANO, ConsultaParcela.Operador.MENOR,
                valorDivida.getCalculo().getProcessoCalculo().getExercicio().getAno());
            mapaConsultas.get(opcaoPagamentoDivida.getOpcaoPagamento()).add(consultaParcela);

            geraParcelas(opcaoPagamentoDivida, valorDivida);
        }
    }

    private void geraParcelas(OpcaoPagamentoDivida opcaoPagamentoDivida, ValorDivida valorDivida) throws Exception {
        List<Parcela> parcelas = calculaPercentualParcelas(opcaoPagamentoDivida.getOpcaoPagamento(), valorDivida);
        if (!parcelas.isEmpty()) {
            if (parcelasOpcaoPagamento == null) parcelasOpcaoPagamento = Maps.newHashMap();
            parcelasOpcaoPagamento.put(opcaoPagamentoDivida.getOpcaoPagamento(), new ArrayList<ParcelaValorDivida>());
            BigDecimal valorTotalBruto = valorDivida.getValor().setScale(2, MODO_ARREDONDAMENTO);
            OpcaoPagamento opcaoPagamento = em.find(OpcaoPagamento.class, opcaoPagamentoDivida.getOpcaoPagamento().getId());
            BigDecimal valorParcelaBruto;
            BigDecimal valorLancadoBruto = BigDecimal.ZERO;
            CalculoIPTU calculoIPTU = ((CalculoIPTU) valorDivida.getCalculo());
            for (Parcela p : parcelas) {
                valorParcelaBruto = valorTotalBruto.multiply(p.getPercentualValorTotal()).divide(CEM, NUMERO_CASAS_DECIMAIS, MODO_ARREDONDAMENTO);
                ParcelaValorDivida parcelaValorDivida = new ParcelaValorDivida();
                parcelaValorDivida.setDividaAtiva(Boolean.FALSE);
                parcelaValorDivida.setItensParcelaValorDivida(Lists.newArrayList());
                parcelaValorDivida.setOpcaoPagamento(opcaoPagamento);
                parcelaValorDivida.setValorDivida(valorDivida);
                parcelaValorDivida.setValor(valorParcelaBruto);
                parcelaValorDivida.setVencimento(p.getVencimento());
                parcelaValorDivida.setPercentualValorTotal(p.getPercentualValorTotal());
                parcelaValorDivida.setSequenciaParcela(p.getSequenciaParcela());
                if (todosItemsImunes(calculoIPTU)) {
                    parcelaValorDivida.getSituacoes().add(new SituacaoParcelaValorDivida(opcaoPagamento.getPromocional() ? SituacaoParcela.CANCELADO_ISENCAO : SituacaoParcela.IMUNE, parcelaValorDivida, parcelaValorDivida.getValor()));
                } else {
                    parcelaValorDivida.getSituacoes().add(new SituacaoParcelaValorDivida(SituacaoParcela.EM_ABERTO, parcelaValorDivida, parcelaValorDivida.getValor()));
                }
                parcelasOpcaoPagamento.get(opcaoPagamento).add(parcelaValorDivida);
                valorDivida.getParcelaValorDividas().add(parcelaValorDivida);
                valorLancadoBruto = valorLancadoBruto.add(parcelaValorDivida.getValor());
            }
            processaDiferencaValorDivida(valorLancadoBruto, valorTotalBruto, opcaoPagamento);
            lancaItensParcela(valorDivida, opcaoPagamentoDivida);
        }
    }


    @Override
    protected void defineVencimentosParcelas(Calculo calculo, OpcaoPagamento op, int i, ParcelaValorDivida parcelaValorDivida, ValorDivida valorDivida, Calendar vencimento, Integer numeroParcelas) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isAdimplente(OpcaoPagamento opcaoPagamento) {
        if (adimplente != null && adimplente.containsKey(opcaoPagamento)) {
            return adimplente.get(opcaoPagamento);
        }
        if (adimplente == null) {
            adimplente = Maps.newHashMap();
        }
        List<ResultadoParcela> parcelas = Lists.newArrayList();
        for (ConsultaParcela consulta : mapaConsultas.get(opcaoPagamento)) {
            if (parcelas.isEmpty()) {
                parcelas.addAll(consulta.executaConsulta().getResultados());
            }
        }
        adimplente.put(opcaoPagamento, parcelas.isEmpty());
        return adimplente.get(opcaoPagamento);
    }


    private ConsultaParcela criarConsultaParcela(Date dataVerificacaoDebito, CalculoIPTU calculo, List<Long> idsDividasIptu) {
        ConsultaParcela consultaParcela = dataVerificacaoDebito != null ? new ConsultaParcela(dataVerificacaoDebito) : new ConsultaParcela();
        consultaParcela.addParameter(ConsultaParcela.Campo.CADASTRO_ID, ConsultaParcela.Operador.IGUAL, calculo.getCadastroImobiliario().getId());
        consultaParcela.addParameter(ConsultaParcela.Campo.PARCELA_SITUACAO, ConsultaParcela.Operador.IGUAL, SituacaoParcela.EM_ABERTO);
        consultaParcela.addParameter(ConsultaParcela.Campo.DIVIDA_ID, ConsultaParcela.Operador.IN, idsDividasIptu);
        return consultaParcela;
    }

    @Override
    public void lancaItensParcela(ValorDivida valorDivida, OpcaoPagamentoDivida opcaoPagamentoDivida) {
        for (ParcelaValorDivida parcelaValorDivida : valorDivida.getParcelaValorDividas()) {
            if (parcelaValorDivida.getOpcaoPagamento().equals(opcaoPagamentoDivida.getOpcaoPagamento())) {
                BigDecimal valorTotalBruto = BigDecimal.ZERO;
                CalculoIPTU calculoIPTU = (CalculoIPTU) valorDivida.getCalculo();
                for (ItemValorDivida item : valorDivida.getItemValorDividas()) {
                    if (todosItemsIsentos(calculoIPTU)
                        || todosItemsImunes(calculoIPTU)
                        || !item.getIsento()
                        || !item.getImune()) {
                        ItemParcelaValorDivida itemParcela = new ItemParcelaValorDivida();
                        itemParcela.setItemValorDivida(item);
                        itemParcela.setParcelaValorDivida(parcelaValorDivida);
                        BigDecimal percentualParcela = parcelaValorDivida.getPercentualValorTotal().divide(CEM, 8, MODO_ARREDONDAMENTO);
                        BigDecimal valorBruto = item.getValor().multiply(percentualParcela).setScale(NUMERO_CASAS_DECIMAIS, MODO_ARREDONDAMENTO);
                        if (valorBruto.compareTo(BigDecimal.ZERO) > 0) {
                            itemParcela.setValor(valorBruto);
                        } else {
                            itemParcela.setValor(BigDecimal.ZERO);
                        }
                        lancaDescontos(itemParcela, calculoIPTU.getTipoCalculo().getOrigem());
                        parcelaValorDivida.getItensParcelaValorDivida().add(itemParcela);
                        valorTotalBruto = valorTotalBruto.add(itemParcela.getValor());
                    }
                }
                processaDiferencaParcela(parcelaValorDivida.getValor(), valorTotalBruto, parcelaValorDivida);
            }
        }
    }

    private boolean todosItemsIsentos(CalculoIPTU calculo) {
        for (ItemCalculoIPTU item : calculo.getItensCalculo()) {
            if (!item.getIsento()) {
                return Boolean.FALSE;
            }
        }
        return Boolean.TRUE;
    }

    private boolean todosItemsImunes(CalculoIPTU calculo) {
        for (ItemCalculoIPTU item : calculo.getItensCalculo()) {
            if (!item.getImune()) {
                return Boolean.FALSE;
            }
        }
        return Boolean.TRUE;
    }

}
