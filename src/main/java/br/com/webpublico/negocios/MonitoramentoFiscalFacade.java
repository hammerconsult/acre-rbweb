package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.enums.*;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.seguranca.NotificacaoService;
import br.com.webpublico.tributario.consultadebitos.calculadores.CalculadorJuros;
import br.com.webpublico.tributario.consultadebitos.calculadores.CalculadorMulta;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * @author octavio
 */
@Stateless
public class MonitoramentoFiscalFacade extends AbstractFacade<MonitoramentoFiscal> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private MoedaFacade moedaFacade;
    @EJB
    private DividaFacade dividaFacade;
    @EJB
    private ConfiguracaoTributarioFacade configuracaoTributarioFacade;
    @EJB
    private ParametroMonitoramentoFiscalFacade parametroMonitoramentoFiscalFacade;
    @EJB
    private ParametroFiscalizacaoFacade parametroFiscalizacaoFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private OrdemGeralMonitoramentoFacade ordemGeralMonitoramentoFacade;
    @EJB
    private EmpresaIrregularFacade empresaIrregularFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private CadastroEconomicoFacade cadastroEconomicoFacade;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;

    public MonitoramentoFiscalFacade() {
        super(MonitoramentoFiscal.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public void criarHistoricoSituacoesMonitoramentoFiscal(MonitoramentoFiscal monitoramentoFiscal) {
        HistoricoSituacaoMonitoramentoFiscal historico = new HistoricoSituacaoMonitoramentoFiscal(monitoramentoFiscal, monitoramentoFiscal.getSituacaoMF(), new Date());
        monitoramentoFiscal.getHistoricoSituacoesMonitoramentoFiscal().add(historico);
    }

    @Override
    public MonitoramentoFiscal recuperar(Object id) {
        MonitoramentoFiscal monitoramentoFiscal = em.find(MonitoramentoFiscal.class, id);
        inicializar(monitoramentoFiscal);

        return monitoramentoFiscal;
    }

    private void inicializar(MonitoramentoFiscal monitoramentoFiscal) {
        Hibernate.initialize(monitoramentoFiscal.getHistoricoSituacoesMonitoramentoFiscal());
        Hibernate.initialize(monitoramentoFiscal.getFiscaisMonitoramento());
        Hibernate.initialize(monitoramentoFiscal.getLevantamentosUFM());
        Hibernate.initialize(monitoramentoFiscal.getRegistrosLancamentoContabilMonitoramentoFiscal());
        if (monitoramentoFiscal.getDetentorArquivoComposicao() != null) {
            Hibernate.initialize(monitoramentoFiscal.getDetentorArquivoComposicao().getArquivosComposicao());
        }
        Hibernate.initialize(monitoramentoFiscal.getMalasDiretasMonitoramentoFiscal());
        if (monitoramentoFiscal.getRegistrosLancamentoContabilMonitoramentoFiscal() != null
            && !monitoramentoFiscal.getRegistrosLancamentoContabilMonitoramentoFiscal().isEmpty()) {
            for (RegistroLancamentoContabilMonitoramentoFiscal registroLancamentoContabilMonitoramentoFiscal : monitoramentoFiscal.getRegistrosLancamentoContabilMonitoramentoFiscal()) {
                Hibernate.initialize(registroLancamentoContabilMonitoramentoFiscal.getLancamentosFiscais());
            }
        }
    }

    public MonitoramentoFiscal salvarRetornando(MonitoramentoFiscal selecionado) {
        return em.merge(selecionado);
    }

    public MalaDiretaGeral salvarMalaGeral(MalaDiretaGeral malaDiretaGeral) {
        return em.merge(malaDiretaGeral);
    }

    public ItemMalaDiretaGeral salvarItemMalaGeral(ItemMalaDiretaGeral itemMalaDiretaGeral) {
        return em.merge(itemMalaDiretaGeral);
    }

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }

    public void setExercicioFacade(ExercicioFacade exercicioFacade) {
        this.exercicioFacade = exercicioFacade;
    }

    public MoedaFacade getMoedaFacade() {
        return moedaFacade;
    }

    public void setMoedaFacade(MoedaFacade moedaFacade) {
        this.moedaFacade = moedaFacade;
    }

    public DividaFacade getDividaFacade() {
        return dividaFacade;
    }

    public void setDividaFacade(DividaFacade dividaFacade) {
        this.dividaFacade = dividaFacade;
    }

    public ConfiguracaoTributarioFacade getConfiguracaoTributarioFacade() {
        return configuracaoTributarioFacade;
    }

    public void setConfiguracaoTributarioFacade(ConfiguracaoTributarioFacade configuracaoTributarioFacade) {
        this.configuracaoTributarioFacade = configuracaoTributarioFacade;
    }

    public ParametroMonitoramentoFiscalFacade getParametroMonitoramentoFiscalFacade() {
        return parametroMonitoramentoFiscalFacade;
    }

    public void setParametroMonitoramentoFiscalFacade(ParametroMonitoramentoFiscalFacade parametroMonitoramentoFiscalFacade) {
        this.parametroMonitoramentoFiscalFacade = parametroMonitoramentoFiscalFacade;
    }

    public ParametroFiscalizacaoFacade getParametroFiscalizacaoFacade() {
        return parametroFiscalizacaoFacade;
    }

    public void setParametroFiscalizacaoFacade(ParametroFiscalizacaoFacade parametroFiscalizacaoFacade) {
        this.parametroFiscalizacaoFacade = parametroFiscalizacaoFacade;
    }

    public EmpresaIrregularFacade getEmpresaIrregularFacade() {
        return empresaIrregularFacade;
    }

    public void setEmpresaIrregularFacade(EmpresaIrregularFacade empresaIrregularFacade) {
        this.empresaIrregularFacade = empresaIrregularFacade;
    }

    public String recuperarAlvarasPorTipo(TipoAlvara tipo, CadastroEconomico cmc) {
        String sql = " select 'Exercício: ' || ex.ano || ' - ' || 'Validade: ' || to_char(al.vencimento,'dd/MM/yyyy')\n" +
            " from Alvara al " +
            "  inner join Exercicio ex on ex.ID = al.exercicio_id " +
            " where al.iniciovigencia = (select max(aux.iniciovigencia) from Alvara aux \n" +
            "                             inner join " + tipo.getTabelaSql() + " calc on calc.alvara_id = aux.id " +
            "                             inner join ValorDivida vd on vd.calculo_id = calc.id " +
            "                             inner join ParcelaValorDivida pvd on pvd.valorDivida_id = vd.id " +
            "                             inner join SituacaoParcelaValorDivida spvd on spvd.id = pvd.situacaoAtual_id " +
            "                             where aux.cadastroEconomico_id = :idCMC " +
            "                               and aux.tipoAlvara = :tipoAlvara " +
            "                               and spvd.situacaoParcela in ('PAGO','BAIXADO')) " +
            " and al.cadastroEconomico_id = :idCMC " +
            " and al.tipoAlvara = :tipoAlvara ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("tipoAlvara", tipo.name());
        q.setParameter("idCMC", cmc.getId());
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            return q.getResultList().get(0).toString();
        }
        return null;
    }

    public Long retornaUltimoNumeroLancamentoFiscal() {
        Long num;
        String sql = " select max(coalesce(mf.numeroLevantamento,0)) from MonitoramentoFiscal mf ";
        Query query = getEntityManager().createNativeQuery(sql);
        query.setMaxResults(1);
        if (!query.getResultList().isEmpty()) {
            BigDecimal b = (BigDecimal) query.getSingleResult();

            if (b != null) {
                b = b.add(BigDecimal.ONE);
            } else {
                b = BigDecimal.ONE;
            }
            num = b.setScale(0, BigDecimal.ROUND_UP).longValueExact();
        } else {
            return 1l;
        }
        return num;
    }

    public BigDecimal buscaValorPagoISS(LancamentoFiscalMonitoramentoFiscal lancamentoFiscalMonitoramentoFiscal, CadastroEconomico cadastroEconomico) {
        List<ItemCalculoIss> itens = buscaLancamentoDeISS(lancamentoFiscalMonitoramentoFiscal.getMes(), lancamentoFiscalMonitoramentoFiscal.getAno(), cadastroEconomico);
        BigDecimal total = BigDecimal.ZERO;
        for (ItemCalculoIss item : itens) {
            if (item.getAliquota().compareTo(lancamentoFiscalMonitoramentoFiscal.getAliquotaISS()) == 0) {
                total = total.add(item.getValorCalculado());
            }
        }
        return total;
    }

    public List<ItemCalculoIss> buscaLancamentoDeISS(Mes mes, Integer ano, CadastroEconomico cadastroEconomico) {

        String sql = "SELECT distinct item.* FROM itemcalculoiss item "
            + "inner join calculoiss cal on cal.id = item.calculo_id "
            + "inner join processocalculo pro on pro.id = cal.processocalculoiss_id "
            + "inner join processocalculoiss proiss on proiss.id = cal.processocalculoiss_id "
            + "inner join exercicio ex on ex.id = pro.exercicio_id "
            + " where cal.tipocalculoiss = :tipocalculoiss "
            + " and cal.tipoSituacaoCalculoISS = :tiposituacaocalculoiss "
            + " and cal.cadastroeconomico_id = :cadastroEconomico"
            + " and proiss.mesreferencia = :mes "
            + " and ex.ano = :ano ";
        Query q = em.createNativeQuery(sql, ItemCalculoIss.class);

        q.setParameter("mes", mes.getNumeroMes());
        q.setParameter("ano", ano);
        q.setParameter("cadastroEconomico", cadastroEconomico.getId());
        q.setParameter("tipocalculoiss", TipoCalculoISS.MENSAL.name());
        q.setParameter("tiposituacaocalculoiss", TipoSituacaoCalculoISS.LANCADO.name());
        return q.getResultList();
    }

    public BigDecimal calcularCorrecaoLancamento(LancamentoFiscalMonitoramentoFiscal lcf, MonitoramentoFiscal monitoramentoFiscal) {
        if (podeCalcularAcrescimoLancamento(lcf)) {
            try {
                lcf.setCorrecao(calcularCorrecaoPorArbitramento(lcf.getAno(), lcf.getMes().getNumeroMes(), lcf.getIssDevido(), monitoramentoFiscal));
            } catch (Exception e) {
                lcf.setCorrecao(BigDecimal.ZERO);
                logger.debug("Erro calculando Correção do Lançamento", e);
            }
        }
        return lcf.getCorrecao();
    }

    private Boolean podeCalcularAcrescimoLancamento(LancamentoFiscalMonitoramentoFiscal lcf) {
        return lcf.getMes() != null && lcf.getAno() != null;
    }

    public BigDecimal calcularCorrecaoPorArbitramento(int ano, int mes, BigDecimal valor, MonitoramentoFiscal monitoramentoFiscal) {
        BigDecimal indice = recuperaIndice(ano, mes, monitoramentoFiscal);
        if (indice.compareTo(BigDecimal.ZERO) > 0 && valor.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal divisao = (valor.divide(indice, 6, RoundingMode.HALF_UP));
            BigDecimal multiplica = BigDecimal.ZERO;
            if (monitoramentoFiscal.getUfmArbitramento() != null) {
                multiplica = divisao.multiply(monitoramentoFiscal.getUfmArbitramento());
            }
            BigDecimal correcao = multiplica.subtract(valor);
            if (correcao.compareTo(BigDecimal.ZERO) < 0) {
                correcao = BigDecimal.ZERO;
            }
            return correcao;
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal recuperaIndice(int ano, int mes, MonitoramentoFiscal monitoramentoFiscal) {
        for (LevantamentoUFMMonitoramentoFiscal levantamentoUFM : monitoramentoFiscal.getLevantamentosUFM()) {
            if (levantamentoUFM.getMes().getNumeroMes() == mes && levantamentoUFM.getAno() == ano) {
                return levantamentoUFM.getValorIndice();
            }
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal calcularJurosLancamento(LancamentoFiscalMonitoramentoFiscal lancamentoFiscalMonitoramentoFiscal, MonitoramentoFiscal monitoramentoFiscal,
                                              Map<Integer, Integer> diaVencimentoPorAno) {
        if (podeCalcularAcrescimoLancamento(lancamentoFiscalMonitoramentoFiscal)) {
            Divida divida = getDividaFacade().recuperar(getConfiguracaoTributarioFacade().retornaUltimo().getDividaISSHomologado().getId());
            ConfiguracaoAcrescimos acrescimo = getDividaFacade().configuracaoVigente(divida);
            Calendar c = getDataVencimentoAcrescimo(lancamentoFiscalMonitoramentoFiscal, diaVencimentoPorAno);
            lancamentoFiscalMonitoramentoFiscal.setJuros(CalculadorJuros.calculaJuros(acrescimo.toDto(), c.getTime(), monitoramentoFiscal.getDataArbitramento(),
                lancamentoFiscalMonitoramentoFiscal.getIssDevido().add(lancamentoFiscalMonitoramentoFiscal.getCorrecao()), false));
        }
        if (lancamentoFiscalMonitoramentoFiscal.getIssDevido().compareTo(BigDecimal.ZERO) <= 0 || lancamentoFiscalMonitoramentoFiscal.getJuros().compareTo(BigDecimal.ZERO) < 0) {
            lancamentoFiscalMonitoramentoFiscal.setJuros(BigDecimal.ZERO);
        }
        return lancamentoFiscalMonitoramentoFiscal.getJuros();
    }

    public Calendar getDataVencimentoAcrescimo(LancamentoFiscalMonitoramentoFiscal lancamentoFiscalMonitoramentoFiscal, Map<Integer, Integer> diaVencimentoPorAno) {
        if (!diaVencimentoPorAno.containsKey(lancamentoFiscalMonitoramentoFiscal.getAno())) {
            carregaDiasPorAno(lancamentoFiscalMonitoramentoFiscal, diaVencimentoPorAno);
        }
        Calendar c = Calendar.getInstance();
        int ano = lancamentoFiscalMonitoramentoFiscal.getAno();
        int mes = lancamentoFiscalMonitoramentoFiscal.getMes().getNumeroMes();
        if (lancamentoFiscalMonitoramentoFiscal.getMes().equals(Mes.DEZEMBRO)) {
            mes = 0;
            ano++;
        }
        c.set(Calendar.DAY_OF_MONTH, diaVencimentoPorAno.get(lancamentoFiscalMonitoramentoFiscal.getAno()));
        c.set(Calendar.MONTH, mes);
        c.set(Calendar.YEAR, ano);
        return c;
    }

    private void carregaDiasPorAno(LancamentoFiscalMonitoramentoFiscal lancamentoFiscalMonitoramentoFiscal, Map<Integer, Integer> diaVencimentoPorAno) {
        ParametroFiscalizacao param = getParametroFiscalizacaoFacade().recuperarParametroFiscalizacao(lancamentoFiscalMonitoramentoFiscal.getAno());
        int dia = 1;
        if (param != null && param.getDiaVencimentoISS() != null) {
            dia = param.getDiaVencimentoISS();
        }
        diaVencimentoPorAno.put(lancamentoFiscalMonitoramentoFiscal.getAno(), dia);
    }

    public BigDecimal calcularMultaLancamento(LancamentoFiscalMonitoramentoFiscal lancamentoFiscalMonitoramentoFiscal,
                                              MonitoramentoFiscal monitoramentoFiscal, Map<Integer, Integer> diaVencimentoPorAno) {
        if (podeCalcularAcrescimoLancamento(lancamentoFiscalMonitoramentoFiscal)) {
            Divida divida = getDividaFacade().recuperar(getConfiguracaoTributarioFacade().retornaUltimo().getDividaISSHomologado().getId());
            ConfiguracaoAcrescimos acrescimo = getDividaFacade().configuracaoVigente(divida);
            Calendar c = getDataVencimentoAcrescimo(lancamentoFiscalMonitoramentoFiscal, diaVencimentoPorAno);
            lancamentoFiscalMonitoramentoFiscal.setMulta(CalculadorMulta.calculaMulta(acrescimo.toDto(), c.getTime(),
                monitoramentoFiscal.getDataArbitramento(), lancamentoFiscalMonitoramentoFiscal.getIssDevido().add(lancamentoFiscalMonitoramentoFiscal.getCorrecao()), true));
        }
        if (lancamentoFiscalMonitoramentoFiscal.getIssDevido().compareTo(BigDecimal.ZERO) <= 0 || lancamentoFiscalMonitoramentoFiscal.getMulta().compareTo(BigDecimal.ZERO) < 0) {
            lancamentoFiscalMonitoramentoFiscal.setMulta(BigDecimal.ZERO);
        }
        return lancamentoFiscalMonitoramentoFiscal.getMulta();
    }

    public BigDecimal retornaValorTotalDeclaradoLancamento(Integer ano, RegistroLancamentoContabilMonitoramentoFiscal reg, MonitoramentoFiscal monitoramentoFiscal) {
        BigDecimal soma = BigDecimal.ZERO;
        if (!monitoramentoFiscal.getLevantamentosUFM().isEmpty()
            || monitoramentoFiscal.getLevantamentosUFM() != null) {
            for (LancamentoFiscalMonitoramentoFiscal lmf : reg.getLancamentosFiscais()) {
                if (ano != null) {
                    if (ano.equals(lmf.getAno())) {
                        soma = soma.add(lmf.getValorDeclarado());
                    }
                } else {
                    soma = soma.add(lmf.getValorDeclarado());
                }
            }
        }
        return soma.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal retornaValorTotalApuradoLancamento(Integer ano, RegistroLancamentoContabilMonitoramentoFiscal reg, MonitoramentoFiscal monitoramentoFiscal) {
        BigDecimal soma = BigDecimal.ZERO;
        if (!monitoramentoFiscal.getLevantamentosUFM().isEmpty() || monitoramentoFiscal.getLevantamentosUFM() != null) {
            for (LancamentoFiscalMonitoramentoFiscal lmf : reg.getLancamentosFiscais()) {
                if (ano != null) {
                    if (ano.equals(lmf.getAno())) {
                        soma = soma.add(lmf.getValorApurado());
                    }
                } else {
                    soma = soma.add(lmf.getValorApurado());
                }
            }
        }
        return soma.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal retornaValorTotalBaseCalculoLancamento(Integer ano, RegistroLancamentoContabilMonitoramentoFiscal reg, MonitoramentoFiscal monitoramentoFiscal) {
        BigDecimal soma = BigDecimal.ZERO;
        if (!monitoramentoFiscal.getLevantamentosUFM().isEmpty() || monitoramentoFiscal.getLevantamentosUFM() != null) {
            for (LancamentoFiscalMonitoramentoFiscal lmf : reg.getLancamentosFiscais()) {
                if (ano != null) {
                    if (ano.equals(lmf.getAno())) {
                        soma = soma.add(lmf.getBaseCalculo());
                    }
                } else {
                    soma = soma.add(lmf.getBaseCalculo());
                }
            }
        }
        return soma.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal retornaValorTotalIssPagoLancamento(Integer ano, RegistroLancamentoContabilMonitoramentoFiscal reg, MonitoramentoFiscal monitoramentoFiscal) {
        BigDecimal soma = BigDecimal.ZERO;
        if (!monitoramentoFiscal.getLevantamentosUFM().isEmpty() || monitoramentoFiscal.getLevantamentosUFM() != null) {
            for (LancamentoFiscalMonitoramentoFiscal lmf : reg.getLancamentosFiscais()) {
                if (ano != null) {
                    if (ano.equals(lmf.getAno())) {
                        soma = soma.add(lmf.getIssPago());
                    }
                } else {
                    soma = soma.add(lmf.getIssPago());
                }
            }
        }
        return soma.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal retornaValorTotalIssApuradoLancamento(Integer ano, RegistroLancamentoContabilMonitoramentoFiscal reg, MonitoramentoFiscal monitoramentoFiscal) {
        BigDecimal soma = BigDecimal.ZERO;
        if (!monitoramentoFiscal.getLevantamentosUFM().isEmpty() || monitoramentoFiscal.getLevantamentosUFM() != null) {
            for (LancamentoFiscalMonitoramentoFiscal lmf : reg.getLancamentosFiscais()) {
                if (ano != null) {
                    if (ano.equals(lmf.getAno())) {
                        soma = soma.add(lmf.getIssApurado());
                    }
                } else {
                    soma = soma.add(lmf.getIssApurado());
                }
            }
        }
        return soma.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal retornaValorTotalIssDevidoLancamento(Integer ano, RegistroLancamentoContabilMonitoramentoFiscal reg, MonitoramentoFiscal monitoramentoFiscal) {
        BigDecimal soma = BigDecimal.ZERO;
        if (!monitoramentoFiscal.getLevantamentosUFM().isEmpty() || monitoramentoFiscal.getLevantamentosUFM() != null) {
            for (LancamentoFiscalMonitoramentoFiscal lmf : reg.getLancamentosFiscais()) {
                if (ano != null) {
                    if (ano.equals(lmf.getAno())) {
                        soma = soma.add(lmf.getIssDevido());
                    }
                } else {
                    soma = soma.add(lmf.getIssDevido());
                }
            }
        }
        return soma.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal retornarValorTotalCorrecaoMonetariaLancamento(Integer ano, RegistroLancamentoContabilMonitoramentoFiscal reg, MonitoramentoFiscal monitoramentoFiscal) {
        BigDecimal soma = BigDecimal.ZERO;
        for (LancamentoFiscalMonitoramentoFiscal lmf : reg.getLancamentosFiscais()) {
            if (ano != null) {
                if (ano.equals(lmf.getAno())) {
                    if (lmf.getCorrecao().compareTo(BigDecimal.ZERO) <= 0) {
                        lmf.setCorrecao(calcularCorrecaoLancamento(lmf, monitoramentoFiscal));
                    }
                    soma = soma.add(lmf.getCorrecao());
                }
            } else {
                soma = soma.add(lmf.getCorrecao());
            }
        }
        return soma.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal retornarValorTotalCorrigidoLancamento(Integer ano, RegistroLancamentoContabilMonitoramentoFiscal reg, MonitoramentoFiscal monitoramentoFiscal) {
        BigDecimal soma = BigDecimal.ZERO;

        if (!monitoramentoFiscal.getLevantamentosUFM().isEmpty() || monitoramentoFiscal.getLevantamentosUFM() != null) {
            for (LancamentoFiscalMonitoramentoFiscal lmf : reg.getLancamentosFiscais()) {
                if (ano != null) {
                    if (ano.equals(lmf.getAno())) {
                        soma = soma.add(lmf.getValorCorrigido());
                    }
                } else {
                    soma = soma.add(lmf.getValorCorrigido());
                }
            }
        }
        return soma.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal retornarValorTotalJurosMoraLancamento(Integer ano, RegistroLancamentoContabilMonitoramentoFiscal reg, MonitoramentoFiscal monitoramentoFiscal) {
        BigDecimal soma = BigDecimal.ZERO;
        if (!monitoramentoFiscal.getLevantamentosUFM().isEmpty() || monitoramentoFiscal.getLevantamentosUFM() != null) {
            for (LancamentoFiscalMonitoramentoFiscal lmf : reg.getLancamentosFiscais()) {
                if (ano != null) {
                    if (ano.equals(lmf.getAno())) {
                        soma = soma.add(lmf.getJuros());
                    }
                } else {
                    soma = soma.add(lmf.getJuros());
                }
            }
        }
        return soma.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal retornarValorTotalMultaMoraLancamento(Integer ano, RegistroLancamentoContabilMonitoramentoFiscal reg, MonitoramentoFiscal monitoramentoFiscal) {
        BigDecimal soma = BigDecimal.ZERO;
        if (!monitoramentoFiscal.getLevantamentosUFM().isEmpty() || monitoramentoFiscal.getLevantamentosUFM() != null) {
            for (LancamentoFiscalMonitoramentoFiscal lmf : reg.getLancamentosFiscais()) {
                if (ano != null) {
                    if (ano.equals(lmf.getAno())) {
                        soma = soma.add(lmf.getMulta());
                    }
                } else {
                    soma = soma.add(lmf.getMulta());
                }
            }
        }
        return soma.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal retornaValorTotalTotalLancamento(Integer ano, RegistroLancamentoContabilMonitoramentoFiscal reg, MonitoramentoFiscal monitoramentoFiscal) {
        BigDecimal soma = BigDecimal.ZERO;
        if (!monitoramentoFiscal.getLevantamentosUFM().isEmpty() || monitoramentoFiscal.getLevantamentosUFM() != null) {
            for (LancamentoFiscalMonitoramentoFiscal lmf : reg.getLancamentosFiscais()) {
                if (ano != null) {
                    if (ano.equals(lmf.getAno())) {
                        soma = soma.add(lmf.getCorrecao()).add(lmf.getIssDevido()).add(lmf.getJuros()).add(lmf.getMulta());
                    }
                } else {
                    soma = soma.add(lmf.getCorrecao()).add(lmf.getIssDevido()).add(lmf.getJuros()).add(lmf.getMulta());
                }
            }
        }
        return soma.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calculaTotalLancamento(LancamentoFiscalMonitoramentoFiscal lmf) {
        return lmf.getCorrecao().add(lmf.getIssDevido()).add(lmf.getJuros()).add(lmf.getMulta());
    }

    public BigDecimal calcularValorCorrigido(LancamentoFiscalMonitoramentoFiscal lmf) {
        return lmf.getCorrecao().add(lmf.getIssDevido());
    }

    public List<TotalizadorLancamentoContabil> buscarLancamentosNfse(MonitoramentoFiscal monitoramentoFiscal) {
        String sql = "select nfse.anoDeReferencia, nfse.mesDeReferencia, sum(nfse.valorTotalDoDebito) " +
            "from CalculoNFSe nfse " +
            "inner join Calculo cal on cal.id = nfse.id " +
            "where cal.cadastro_id = :cadastro_id " +
            "and to_date('15/'||nfse.MESDEREFERENCIA||'/'||nfse.ANODEREFERENCIA,'dd/MM/yyyy') BETWEEN :dataInicial and :dataFinal " +
            "group by nfse.anoDeReferencia, nfse.mesDeReferencia";

        Query q = em.createNativeQuery(sql);
        q.setParameter("cadastro_id", monitoramentoFiscal.getCadastroEconomico().getId());
        q.setParameter("dataInicial", monitoramentoFiscal.getDataLevantamentoInicial());
        q.setParameter("dataFinal", monitoramentoFiscal.getDataLevantamentoFinal());
        List<Object[]> lista = q.getResultList();
        List<TotalizadorLancamentoContabil> retorno = Lists.newArrayList();
        for (Object[] obj : lista) {
            TotalizadorLancamentoContabil total = new TotalizadorLancamentoContabil();
            total.setAno(((BigDecimal) obj[0]).intValue());
            total.setMes(Mes.getMesToInt(((BigDecimal) obj[1]).intValue()));
            total.setValor((BigDecimal) obj[2]);
            total.setAliquota(BigDecimal.ZERO);
            total.setTributado(true);
            retorno.add(total);
        }
        return retorno;
    }

    public List<BigDecimal> buscarAliquotasServicos(CadastroEconomico cadastroEconomico) {
        String sql = "select distinct s.aliquotaisshomologado from servico s join cadastroeconomico_servico sce on s.id = sce.servico_id where  sce.cadastroeconomico_id = :bce order by s.aliquotaisshomologado";
        Query q = this.em.createNativeQuery(sql);
        q.setParameter("bce", cadastroEconomico.getId());
        return q.getResultList();
    }

    public void criarNotificacaoVencimentoRespostaMalaDireta(MalaDiretaMonitoramentoFiscal mala, UsuarioSistema ususario) {
        ParametroMonitoramentoFiscal parametroMonitoramentoFiscal = parametroMonitoramentoFiscalFacade.retornarParametroExercicioCorrente();
        String msg = "Foi gerada uma Mala direta para o C.M.C " + mala.getMonitoramentoFiscal().getCadastroEconomico() +
            "com prazo de reposta até: " + parametroMonitoramentoFiscal.getPrazoRespostaComunicado();
        Notificacao notificacao = new Notificacao();
        notificacao.setGravidade(Notificacao.Gravidade.ATENCAO);
        notificacao.setTitulo(TipoNotificacao.AVISO_PRAZO_RESPOSTA_MALA_DIRETA_MONITORAMENTO_FISCAL.getDescricao());
        notificacao.setTipoNotificacao(TipoNotificacao.AVISO_PRAZO_RESPOSTA_MALA_DIRETA_MONITORAMENTO_FISCAL);
        notificacao.setUsuarioSistema(ususario);
        notificacao.setLink("/tributario/fiscalizacao/monitoramento-fiscal/ver/" + mala.getMonitoramentoFiscal().getId());
        NotificacaoService.getService().notificar(notificacao);
    }

    public void criarNotificacaoVencimentoRegularizacaoMalaDireta(MalaDiretaMonitoramentoFiscal mala, UsuarioSistema ususario) {
        ParametroMonitoramentoFiscal parametroMonitoramentoFiscal = parametroMonitoramentoFiscalFacade.retornarParametroExercicioCorrente();
        String msg = "Foi gerada uma Mala direta para o C.M.C " + mala.getMonitoramentoFiscal().getCadastroEconomico() +
            "com prazo de regularização até: " + parametroMonitoramentoFiscal.getPrazoRegularizaPendencia();
        Notificacao notificacao = new Notificacao();
        notificacao.setGravidade(Notificacao.Gravidade.ATENCAO);
        notificacao.setTitulo(TipoNotificacao.AVISO_PRAZO_RESPOSTA_MALA_DIRETA_MONITORAMENTO_FISCAL.getDescricao());
        notificacao.setTipoNotificacao(TipoNotificacao.AVISO_PRAZO_RESPOSTA_MALA_DIRETA_MONITORAMENTO_FISCAL);
        notificacao.setUsuarioSistema(ususario);
        notificacao.setLink("/tributario/fiscalizacao/monitoramento-fiscal/ver/" + mala.getMonitoramentoFiscal().getId());
        NotificacaoService.getService().notificar(notificacao);

    }

    public String buscarIptuCadastroImobiliarioDoCadastroEconomico(MonitoramentoFiscal monitoramentoFiscal) {
        CadastroEconomico cmc = monitoramentoFiscal.getCadastroEconomico();
        if (cmc.getCadastroImobiliario() != null) {
            ConsultaParcela consultaParcela = new ConsultaParcela();
            consultaParcela.addParameter(ConsultaParcela.Campo.CADASTRO_ID, ConsultaParcela.Operador.IGUAL, cmc.getCadastroImobiliario().getId());
            consultaParcela.addParameter(ConsultaParcela.Campo.PARCELA_SITUACAO, ConsultaParcela.Operador.NOT_IN, Lists.newArrayList(SituacaoParcela.CANCELAMENTO, SituacaoParcela.CANCELADO_RECALCULO));
            consultaParcela.addParameter(ConsultaParcela.Campo.EXERCICIO_ANO, ConsultaParcela.Operador.IGUAL, sistemaFacade.getExercicioCorrente().getAno());

            ConfiguracaoTributario configuracaoTributario = configuracaoTributarioFacade.retornaUltimo();
            if (configuracaoTributario != null) {
                consultaParcela.addParameter(ConsultaParcela.Campo.DIVIDA_ID, ConsultaParcela.Operador.IGUAL, configuracaoTributario.getDividaIPTU().getId());
            }
            consultaParcela.addParameter(ConsultaParcela.Campo.PROMOCIAL, ConsultaParcela.Operador.IGUAL, 1);
            consultaParcela.executaConsulta();
            if (!consultaParcela.getResultados().isEmpty()) {
                ResultadoParcela resultadoParcela = consultaParcela.getResultados().get(0);
                return "Exercício: " + resultadoParcela.getExercicio() + " - Vencimento: " + resultadoParcela.getVencimentoToString() + " - Valor: " + Util.formataValor(resultadoParcela.getValorTotal());
            }
        }
        return null;
    }

    public void atualizarSituacaoDaOrdemParaExecutando(OrdemGeralMonitoramento ordemGeralMonitoramento) {
        if (SituacaoOrdemGeralMonitoramento.DESIGNADO.equals(ordemGeralMonitoramento.getSituacaoOGM())) {
            boolean todosIniciados = true;
            ordemGeralMonitoramento = ordemGeralMonitoramentoFacade.recuperar(ordemGeralMonitoramento.getId());
            for (MonitoramentoFiscal monitoramento : ordemGeralMonitoramento.getMonitoramentosFiscais()) {
                if (SituacaoMonitoramentoFiscal.ADICIONADO.equals(monitoramento.getSituacaoMF()) ||
                    SituacaoMonitoramentoFiscal.DESIGNADO.equals(monitoramento.getSituacaoMF())) {
                    todosIniciados = false;
                }
            }
            if (todosIniciados) {
                ordemGeralMonitoramento.setSituacaoOGM(SituacaoOrdemGeralMonitoramento.EXECUTANDO);
                em.merge(ordemGeralMonitoramento);
            }
        }
    }

    public void atualizarSituacaoDaOrdemParaFinalizado(OrdemGeralMonitoramento ordemGeralMonitoramento) {
        if (SituacaoOrdemGeralMonitoramento.EXECUTANDO.equals(ordemGeralMonitoramento.getSituacaoOGM()) ||
            SituacaoOrdemGeralMonitoramento.DESIGNADO.equals(ordemGeralMonitoramento.getSituacaoOGM())) {
            boolean todosFinalizados = true;
            ordemGeralMonitoramento = ordemGeralMonitoramentoFacade.recuperar(ordemGeralMonitoramento.getId());
            for (MonitoramentoFiscal monitoramento : ordemGeralMonitoramento.getMonitoramentosFiscais()) {
                if (!SituacaoMonitoramentoFiscal.FINALIZADO.equals(monitoramento.getSituacaoMF())) {
                    todosFinalizados = false;
                }
            }
            if (todosFinalizados) {
                ordemGeralMonitoramento.setSituacaoOGM(SituacaoOrdemGeralMonitoramento.FINALIZADO);
                em.merge(ordemGeralMonitoramento);
            }
        }
    }

    public List<MonitoramentoFiscal> buscarMonitoramentosComunicadosOuFinalizados() {
        Query q = em.createQuery("from MonitoramentoFiscal where situacaoMF in (:situacoes)");
        q.setParameter("situacoes", Arrays.asList(SituacaoMonitoramentoFiscal.COMUNICADO, SituacaoMonitoramentoFiscal.FINALIZADO));
        return q.getResultList();
    }

    public void adicionarSituacaoNaEmpresaIrregular(MonitoramentoFiscal monitoramentoFiscal) {
        EmpresaIrregular empresaIrregular = getEmpresaIrregularFacade().buscarEmpresaIrregularParaCadastroEconomico(monitoramentoFiscal.getCadastroEconomico());
        if (empresaIrregular == null) {
            empresaIrregular = new EmpresaIrregular();
            empresaIrregular.setExercicio(sistemaFacade.getExercicioCorrente());
            CadastroEconomico cadastroEconomico = cadastroEconomicoFacade.recuperarEconomicoCnaeCadastro(monitoramentoFiscal.getCadastroEconomico().getId());
            empresaIrregular.setCadastroEconomico(cadastroEconomico);
            empresaIrregular.setJsonCadastroEconomicoDTO(empresaIrregular.getJsonCadastroEconomicoDTO());
            empresaIrregular.setCodigo(singletonGeradorCodigo.getProximoCodigo(EmpresaIrregular.class, "codigo"));
        }

        SituacaoEmpresaIrregular ultimaSituacao = new SituacaoEmpresaIrregular();
        ultimaSituacao.setEmpresaIrregular(empresaIrregular);
        ultimaSituacao.setMonitoramentoFiscal(monitoramentoFiscal);
        ultimaSituacao.setData(new Date());
        ultimaSituacao.setIrregularidade(monitoramentoFiscal.getIrregularidade());
        ultimaSituacao.setUsuarioSistema(sistemaFacade.getUsuarioCorrente());
        ultimaSituacao.setSituacao(monitoramentoFiscal.getEmpresaIrregular() ? SituacaoEmpresaIrregular.Situacao.INSERIDA : SituacaoEmpresaIrregular.Situacao.RETIRADA);
        ultimaSituacao.setObservacao(monitoramentoFiscal.getObservacaoIrregularidade());
        empresaIrregular.getSituacoes().add(ultimaSituacao);

        getEmpresaIrregularFacade().salvar(empresaIrregular, false);
    }

    public List<MonitoramentoFiscal> buscarMonitoramentosDoCadastroEconomico(CadastroEconomico cadastroEconomico, String parte) {
        String sql = "select mon.* from MonitoramentoFiscal mon " +
            " inner join OrdemGeralMonitoramento ordem on ordem.id = mon.ordemGeralMonitoramento_id " +
            " where mon.cadastroEconomico_id = :idCadastroEconomico " +
            "  and cast(ordem.numero as varchar(20)) like :parte";
        Query q = em.createNativeQuery(sql, MonitoramentoFiscal.class);
        q.setParameter("idCadastroEconomico", cadastroEconomico.getId());
        q.setParameter("parte", "%" + parte.trim() + "%");
        return q.getResultList();
    }
}
