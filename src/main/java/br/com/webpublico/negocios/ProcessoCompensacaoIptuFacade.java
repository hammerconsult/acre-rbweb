/*
 * Codigo gerado automaticamente em Thu May 10 14:10:07 BRT 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.negocios.tributario.auxiliares.AssistenteCalculadorIPTU;
import br.com.webpublico.negocios.tributario.auxiliares.CalculadorIPTU;
import br.com.webpublico.negocios.tributario.dao.JdbcCalculoIptuDAO;
import br.com.webpublico.singletons.SingletonGeradorCodigoPorExercicio;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.List;

@Stateless
public class ProcessoCompensacaoIptuFacade extends AbstractFacade<ProcessoCompensacaoIptu> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SingletonGeradorCodigoPorExercicio singletonGeradorCodigoPorExercicio;
    @EJB
    private MoedaFacade moedaFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private ConfiguracaoTributarioFacade configuracaoTributarioFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ConfiguracaoEventoCalculoIPTUFacade configuracaoEventoCalculoIPTUFacade;
    @EJB
    private OpcaoPagamentoFacade opcaoPagamentoFacade;

    public ProcessoCompensacaoIptuFacade() {
        super(ProcessoCompensacaoIptu.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public ProcessoCompensacaoIptu recuperar(Object id) {
        ProcessoCompensacaoIptu p = super.recuperar(id);
        Hibernate.initialize(p.getItens());
        return p;
    }

    @Override
    public void salvar(ProcessoCompensacaoIptu entity) {
        gerarCodigoPorExercicio(entity);
        super.salvar(entity);
    }

    @Override
    public ProcessoCompensacaoIptu salvarRetornando(ProcessoCompensacaoIptu entity) {
        gerarCodigoPorExercicio(entity);
        return super.salvarRetornando(entity);
    }

    private void gerarCodigoPorExercicio(ProcessoCompensacaoIptu entity) {
        if (entity.getCodigo() == null) {
            entity.setCodigo(singletonGeradorCodigoPorExercicio.getProximoCodigoDoExercicio(ProcessoCompensacaoIptu.class, "codigo", "exercicio_id", entity.getExercicio()));
        }
    }

    public void buscarIptuPorExercicioProcesso(ProcessoCompensacaoIptu processoCompensacaoIptu, List<Exercicio> exercicios) {
        String sql = "select ex.ano, \n" +
            "    sum((select sum(ipvd.valor) from ItemParcelaValorDivida ipvd \n" +
            "        inner join ItemValorDivida ivd on ivd.id = ipvd.itemValorDivida_id and coalesce(ivd.isento,0) = 0\n" +
            "        inner join Tributo tr on tr.id = ivd.tributo_id\n" +
            "        where ipvd.parcelaValorDivida_id = pvd.id\n" +
            "          and tr.tipoTributo = :tipoImposto)) as imposto,\n" +
            "      sum((select sum(ipvd.valor) from ItemParcelaValorDivida ipvd \n" +
            "        inner join ItemValorDivida ivd on ivd.id = ipvd.itemValorDivida_id and coalesce(ivd.isento,0) = 0\n" +
            "        inner join Tributo tr on tr.id = ivd.tributo_id\n" +
            "        where ipvd.parcelaValorDivida_id = pvd.id\n" +
            "          and tr.tipoTributo = :tipoTaxa)) as taxa,\n" +
            "       sum(coalesce((select sum(coalesce(dam.desconto,0)) from DAM dam \n" +
            "         inner join ItemDam idam on idam.dam_id = dam.id\n" +
            "         where dam.situacao = :situacaoDam\n" +
            "           and idam.parcela_id = pvd.id),0)) as desconto,\n" +
            "       sum(coalesce((select sum(coalesce(dam.juros,0)) from DAM dam \n" +
            "         inner join ItemDam idam on idam.dam_id = dam.id\n" +
            "         where dam.situacao = :situacaoDam\n" +
            "           and idam.parcela_id = pvd.id),0)) as juros,\n" +
            "       sum(coalesce((select sum(coalesce(dam.multa,0)) from DAM dam \n" +
            "         inner join ItemDam idam on idam.dam_id = dam.id\n" +
            "         where dam.situacao = :situacaoDam\n" +
            "           and idam.parcela_id = pvd.id),0)) as multa\n" +
            "from CalculoIptu iptu\n" +
            "inner join ValorDivida vd on vd.calculo_id = iptu.id\n" +
            "inner join Exercicio ex on ex.id = vd.exercicio_id\n" +
            "inner join ParcelaValorDivida pvd on pvd.valorDivida_id = vd.id \n" +
            "inner join SituacaoParcelaValorDivida spvd on spvd.id = pvd.situacaoAtual_Id\n" +
            "where iptu.cadastroImobiliario_id = :idCadastro\n" +
            "  and ex.ano in (:exercicios)\n" +
            "  and spvd.situacaoParcela in (:situacaoParcela)\n" +
            "group by ex.ano order by ex.ano";
        Query q = em.createNativeQuery(sql);
        q.setParameter("tipoImposto", Tributo.TipoTributo.IMPOSTO.name());
        q.setParameter("tipoTaxa", Tributo.TipoTributo.TAXA.name());
        q.setParameter("situacaoDam", DAM.Situacao.PAGO.name());
        q.setParameter("situacaoParcela", Lists.newArrayList(SituacaoParcela.PAGO.name(), SituacaoParcela.BAIXADO.name(), SituacaoParcela.COMPENSACAO.name()));
        List<Integer> exercicioInteger = Lists.newArrayList();
        for (Exercicio exercicio : exercicios) {
            exercicioInteger.add(exercicio.getAno());
        }
        q.setParameter("exercicios", exercicioInteger);
        q.setParameter("idCadastro", processoCompensacaoIptu.getCadastroImobiliario().getId());
        List<Object[]> resultList = q.getResultList();
        List<ProcessoCompensacaoIptuItem> itens = Lists.newArrayList();
        for (Object[] obj : resultList) {
            ProcessoCompensacaoIptuItem item = new ProcessoCompensacaoIptuItem();
            item.setProcessoCompensacaoIptu(processoCompensacaoIptu);
            item.setExercicio(exercicioFacade.recuperarExercicioPeloAno(((BigDecimal) obj[0]).intValue()));
            item.setValorImposto((BigDecimal) obj[1]);
            item.setValorTaxa((BigDecimal) obj[2]);
            item.setValorDesconto((BigDecimal) obj[3]);
            item.setValorJuros((BigDecimal) obj[4]);
            item.setValorMulta((BigDecimal) obj[5]);
            item.setValorUfm(moedaFacade.recuperaValorUFMPorExercicio(item.getExercicio().getAno()));
            item.setTipoItem(ProcessoCompensacaoIptuItem.TipoItem.INCORRETO);
            itens.add(item);
        }
        processoCompensacaoIptu.setItens(itens);
    }

    public BigDecimal buscarValorUfmExercicioProcesso(Exercicio exercicio) {
        return moedaFacade.recuperaValorUFMPorExercicio(exercicio.getAno());
    }

    public List<ProcessoCalculoIPTU> calcularIPTUCompensacao(ProcessoCompensacaoIptu processoCompensacaoIptu, List<Exercicio> exercicios) {
        ApplicationContext ap = ContextLoader.getCurrentWebApplicationContext();
        JdbcCalculoIptuDAO calculoDAO = (JdbcCalculoIptuDAO) ap.getBean("jdbcCalculoIptuDAO");
        List<ProcessoCalculoIPTU> processos = Lists.newArrayList();

        for (Exercicio exercicio : exercicios) {
            ProcessoCalculoIPTU processo = new ProcessoCalculoIPTU();
            processo.setDivida(configuracaoTributarioFacade.retornaUltimo().getDividaIPTU());
            processo.setConfiguracaoEventoIPTU(configuracaoEventoCalculoIPTUFacade.listarAtivos().get(0));
            processo.setUsuarioSistema(sistemaFacade.getUsuarioCorrente());
            processo.setExercicio(exercicio);
            processo.setDataLancamento(processoCompensacaoIptu.getLancamento());
            processo.setDescricao("Processo de Compensação de IPTU " + processoCompensacaoIptu.getCodigoExercicio() + " - " + processoCompensacaoIptu.getCadastroImobiliario().getInscricaoCadastral() + " (" + exercicio.getAno() + ")");
            processo.setCadastroInical(processoCompensacaoIptu.getCadastroImobiliario().getInscricaoCadastral());
            processo.setCadastroFinal(processoCompensacaoIptu.getCadastroImobiliario().getInscricaoCadastral());

            processo = calculoDAO.geraProcessoCalculo(processo);
            AssistenteCalculadorIPTU assistente = new AssistenteCalculadorIPTU(processo, 1);

            CalculadorIPTU calculador = new CalculadorIPTU();
            calculador.calcularIPTU(Lists.newArrayList(processoCompensacaoIptu.getCadastroImobiliario()), assistente, null);
            processos.add(processo);
        }
        return processos;
    }

    public BigDecimal buscarPercetualDescontoIptu(Divida divida, Exercicio exercicio) {
        List<OpcaoPagamentoDivida> opcaoPagamentoDividas = opcaoPagamentoFacade.retornaOpcoesDaDividaNoExercicio(divida, exercicio);
        OpcaoPagamento op = null;
        for (OpcaoPagamentoDivida opcaoPagamentoDivida : opcaoPagamentoDividas) {
            if (opcaoPagamentoDivida.getOpcaoPagamento().getPromocional()) {
                op = opcaoPagamentoDivida.getOpcaoPagamento();
                break;
            }
        }
        if (op != null) {
            if (!op.getDescontos().isEmpty()) {
                return op.getDescontos().get(0).getPercentualDescontoAdimplente();
            }
        }
        return BigDecimal.ZERO;
    }
}
