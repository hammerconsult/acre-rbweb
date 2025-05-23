package br.com.webpublico.negocios.rh.previdencia;

import br.com.webpublico.DateUtils;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.configuracao.ReajusteMediaAposentadoria;
import br.com.webpublico.entidades.rh.previdencia.ReajusteAplicado;
import br.com.webpublico.entidadesauxiliares.ProcessoCalculoReajuste;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.TipoReajusteAposentadoria;
import br.com.webpublico.interfaces.ItemValorPrevidencia;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.rh.configuracao.ReajusteAplicadoFacade;
import br.com.webpublico.negocios.rh.configuracao.ReajusteMediaAposentadoriaFacade;
import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * @Author peixe on 20/01/2016  10:30.
 */
@Stateless
public class CalculoReajusteMediaFacade extends AbstractFacade<ReajusteAplicado> {


    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ReajusteAplicadoFacade reajusteAplicadoFacade;
    @EJB
    private AposentadoriaFacade aposentadoriaFacade;
    @EJB
    private VinculoFPFacade vinculoFPFacade;
    @EJB
    private PensionistaFacade pensionistaFacade;
    @EJB
    private ReajusteMediaAposentadoriaFacade reajusteMediaAposentadoriaFacade;


    public CalculoReajusteMediaFacade() {
        super(ReajusteAplicado.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    public void iniciarProcessamento(Exercicio exercicioReferencia, List<ReajusteMediaAposentadoria> reajustesMediaAposentadoria, List<ProcessoCalculoReajuste> calculos, Date inicioVigenciaReajustes, Exercicio exercicioAplicacao) {
        try {
            reajustesMediaAposentadoria.addAll(buscarMesesNaoCadastradosReajustes(exercicioAplicacao, reajustesMediaAposentadoria));
            for (ReajusteMediaAposentadoria reajusteMediaAposentadoria : reajustesMediaAposentadoria) {
                processarReajuste(exercicioReferencia, reajusteMediaAposentadoria, inicioVigenciaReajustes);
                Collections.sort(reajusteMediaAposentadoria.getProcessosCalculo(), new Comparator<ProcessoCalculoReajuste>() {
                    @Override
                    public int compare(ProcessoCalculoReajuste o1, ProcessoCalculoReajuste o2) {
                        return o1.getVinculoFP().getMatriculaFP().getPessoa().getNome().compareTo(o2.getVinculoFP().getMatriculaFP().getPessoa().getNome());
                    }
                });
                calculos.addAll(reajusteMediaAposentadoria.getProcessosCalculo());
            }
        } catch (Exception e) {
            throw e;
        }
    }

    private List<ReajusteMediaAposentadoria> buscarMesesNaoCadastradosReajustes(Exercicio exercicio, List<ReajusteMediaAposentadoria> reajustes) {
        List<ReajusteMediaAposentadoria> reajustesNaoCadastrados = Lists.newArrayList();
        for (Mes m : Mes.values()) {
            boolean contemMes = false;
            for (ReajusteMediaAposentadoria reajuste : reajustes) {
                if (reajuste.getMes().equals(m)) {
                    contemMes = true;
                    break;
                }
            }
            if (!contemMes) {
                ReajusteMediaAposentadoria reajusteMediaAposentadoria = new ReajusteMediaAposentadoria();
                reajusteMediaAposentadoria.setReajusteTransiente(Boolean.TRUE);
                reajusteMediaAposentadoria.setExercicio(exercicio);
                reajusteMediaAposentadoria.setMes(m);
                reajusteMediaAposentadoria.setValorReajuste(BigDecimal.ZERO);
                reajustesNaoCadastrados.add(reajusteMediaAposentadoria);
                logger.info("Mês não cadastrado nos rajustes de média de aposentadoria: " + m.toString());
            }
        }
        return reajustesNaoCadastrados;
    }

    public void processarReajuste(Exercicio exercicio, ReajusteMediaAposentadoria reajuste, Date inicioVigenciaReajustes) {
        logger.debug("iniciando processo.");
        reajuste.setProcessosCalculo(new LinkedList<ProcessoCalculoReajuste>());

        Date refereciaIncial = DateUtils.dataSemHorario(DateUtils.getData(1, reajuste.getMes().getNumeroMes(), exercicio.getAno()));
        Date referenciaFinal = DateUtils.dataSemHorario(DateUtils.getUltimoDiaMes(refereciaIncial).getTime());

        List<VinculoFP> vinculos = new LinkedList<>();
        buscarVinculosProcesso(refereciaIncial, referenciaFinal, vinculos);

        for (VinculoFP vinculoFP : vinculos) {
            VinculoFP v = recuperarItemConformeTipo(vinculoFP);
            if (v.getItensValorPrevidencia() != null) {
                processarReajusteAposentado(v.getItensValorPrevidencia(), reajuste, inicioVigenciaReajustes);
            }
        }
    }

    private void buscarVinculosProcesso(Date refereciaIncial, Date referenciaFinal, List<VinculoFP> vinculos) {
        Date refeInicio = refereciaIncial;
        if (DateUtils.getMes(refereciaIncial) == Mes.JANEIRO.getNumeroMes().intValue()) {
            vinculos.addAll(aposentadoriaFacade.buscarAposentadodosNomeadosAtePorTipoReajusteAposentadoria(refeInicio,
                DateUtils.adicionarMeses(referenciaFinal, 12), TipoReajusteAposentadoria.MEDIA));
            vinculos.addAll(pensionistaFacade.buscarPenionistasNomeadosAte(refeInicio, DateUtils.adicionarMeses(referenciaFinal, 12)));
        } else {
            vinculos.addAll(aposentadoriaFacade.buscarAposentadodosNomeadosPorPeriodoAndTipoReajusteAposentadoria(refeInicio,
                referenciaFinal, TipoReajusteAposentadoria.MEDIA));
            vinculos.addAll(pensionistaFacade.buscarPenionistasNomeadosNoPeriodoPorTipoReajuste(refeInicio, referenciaFinal));
        }
    }

    private VinculoFP recuperarItemConformeTipo(VinculoFP vinculoFP) {
        if (vinculoFP instanceof Aposentadoria) {
            Aposentadoria apo = aposentadoriaFacade.recuperarItensAposentadoria(vinculoFP.getId());
            apo.getItemAposentadorias().size();
            vinculoFP.setItensValorPrevidencia(apo.getItemAposentadorias());
        }
        if (vinculoFP instanceof Pensionista) {
            Pensionista pen = pensionistaFacade.recuperarPensionistaComItens(vinculoFP.getId());
            pen.getItensValorPensionista().size();
            vinculoFP.setItensValorPrevidencia(pen.getItensValorPensionista());
        }
        return vinculoFP;
    }

    private void processarReajusteAposentado(List<ItemValorPrevidencia> itens, ReajusteMediaAposentadoria reajuste, Date inicioVigenciaReajustes) {
        for (ItemValorPrevidencia itemPrevidencia : itens) {
            if (podeProcessarItem(itemPrevidencia, inicioVigenciaReajustes) && !itemPrevidenciaJaLancado(itemPrevidencia, reajuste)) {
                logger.debug("processando..." + itemPrevidencia.getVinculoFP());
                ItemValorPrevidencia item = null;
                if (!reajuste.getReajusteTransiente()) {
                    item = criarNovoItemPrevidencia(itemPrevidencia, reajuste, inicioVigenciaReajustes);
                    itemPrevidencia.setFinalVigencia(DateUtils.adicionarDias(inicioVigenciaReajustes, -1));
                }
                finalizarProcesso(item, reajuste, itemPrevidencia);
            }
        }
    }

    private boolean itemPrevidenciaJaLancado(ItemValorPrevidencia itemPrevidencia, ReajusteMediaAposentadoria reajuste) {
        ReajusteMediaAposentadoria reajusteRecebido = itemPrevidencia.getReajusteRecebido();
        return reajusteRecebido != null && reajusteRecebido.getMes().equals(reajuste.getMes()) && reajusteRecebido.getExercicio().equals(reajuste.getExercicio()) && reajusteRecebido.getExercicioReferencia().equals(reajuste.getExercicioReferencia());
    }

    private void finalizarProcesso(ItemValorPrevidencia novo, ReajusteMediaAposentadoria reajuste, ItemValorPrevidencia antigo) {
        ProcessoCalculoReajuste processoCalculoReajuste = new ProcessoCalculoReajuste();
        processoCalculoReajuste.setItemValorPrevidenciaAntigo(antigo);
        processoCalculoReajuste.setItemValorPrevidenciaNovo(novo);
        processoCalculoReajuste.setVinculoFP(antigo.getVinculoFP());
        processoCalculoReajuste.setReajusteMediaAposentadoria(reajuste);
        reajuste.getProcessosCalculo().add(processoCalculoReajuste);
    }

    private ItemValorPrevidencia criarNovoItemPrevidencia(ItemValorPrevidencia itemValor, ReajusteMediaAposentadoria reajuste, Date referenciaFinal) {
        ItemValorPrevidencia item = getInstanciaCorreta(itemValor.getVinculoFP());
        item.setVinculoFP(itemValor.getVinculoFP());
        item.setEventoFP(itemValor.getEventoFP());
        item.setInicioVigencia(referenciaFinal);
        item.setFinalVigencia(itemValor.getFinalVigencia());
        item.setDataRegistro(new Date());
        item.setReajusteRecebido(reajuste);
        item.setValor(calcularNovoValor(itemValor.getValor(), reajuste.getValorReajuste()));
        return item;
    }

    private ItemValorPrevidencia getInstanciaCorreta(VinculoFP vinculoFP) {
        if (vinculoFP instanceof Pensionista) {
            return new ItemValorPensionista();
        } else {
            return new ItemAposentadoria();
        }
    }


    private BigDecimal calcularNovoValor(BigDecimal valor, BigDecimal valorReajuste) {
        if (valor == null) return BigDecimal.ZERO;
        BigDecimal novoValor = valor.add(valor.multiply(BigDecimal.valueOf(valorReajuste.doubleValue() / 100)).setScale(2, RoundingMode.HALF_UP));
        return novoValor;
    }

    private boolean podeProcessarItem(ItemValorPrevidencia itemValorPrevidencia, Date referencia) {
        return DataUtil.isBetween(itemValorPrevidencia.getInicioVigencia(), itemValorPrevidencia.getFinalVigencia(), referencia);
    }

    public void efetivarReajuste(List<ProcessoCalculoReajuste> calculosReajustes, ReajusteAplicado reajusteAplicado) {
        try {
            for (ProcessoCalculoReajuste processoCalculoReajuste : calculosReajustes) {
                if (podeSalvarReajuste(processoCalculoReajuste)) {
                    em.merge(processoCalculoReajuste.getItemValorPrevidenciaAntigo());
                    em.merge(processoCalculoReajuste.getItemValorPrevidenciaNovo());
                }
            }
            em.merge(reajusteAplicado);
        } catch (ExcecaoNegocioGenerica e) {
            new ExcecaoNegocioGenerica("Erro ao tentar salvar o reajuste." + e);
        }
    }

    private boolean podeSalvarReajuste(ProcessoCalculoReajuste processoCalculoReajuste) {
        return processoCalculoReajuste.getItemValorPrevidenciaAntigo() != null && processoCalculoReajuste.getItemValorPrevidenciaNovo() != null && processoCalculoReajuste.getItemValorPrevidenciaNovo().getValor() != null && processoCalculoReajuste.getItemValorPrevidenciaNovo().getValor().compareTo(BigDecimal.ZERO) != 0;
    }

    public List<ReajusteMediaAposentadoria> buscarReajustesPorExercicio(Exercicio exercicioAplicacao, Exercicio exercicioReferencia) {
        List<ReajusteMediaAposentadoria> reajustes = reajusteMediaAposentadoriaFacade.buscarPorExercicios(exercicioAplicacao, exercicioReferencia);
        for (ReajusteMediaAposentadoria reajuste : reajustes) {
            List<ItemValorPrevidencia> itens = buscarTodosItens(reajuste);
            for (ItemValorPrevidencia item : itens) {
                reajuste.addProcessoCalculo(criarItemDeProcesso(item, reajuste));
            }
        }
        return reajustes;
    }

    private List<ItemValorPrevidencia> buscarTodosItens(ReajusteMediaAposentadoria reajuste) {
        List<ItemValorPrevidencia> itens = new LinkedList<>();
        itens.addAll(aposentadoriaFacade.buscarItemAposentadoriaPorReajuste(reajuste));
        itens.addAll(pensionistaFacade.buscarItemValorPensionistaPorReajuste(reajuste));
        return itens;
    }

    private ProcessoCalculoReajuste criarItemDeProcesso(ItemValorPrevidencia item, ReajusteMediaAposentadoria reajuste) {
        return new ProcessoCalculoReajuste(item.getVinculoFP(), reajuste, null, item);
    }
}
