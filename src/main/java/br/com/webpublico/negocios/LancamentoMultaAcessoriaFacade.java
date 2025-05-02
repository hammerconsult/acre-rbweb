package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Alex
 * @since 30/03/2017 15:05
 */
@Stateless
public class LancamentoMultaAcessoriaFacade extends AbstractFacade<LancamentoMultaAcessoria> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private MultaFiscalizacaoFacade multaFiscalizacaoFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ConfiguracaoTributarioFacade configuracaoTributarioFacade;
    @EJB
    private GeraValorDividaMultaAcessoria geraDebitoMultaAcessoria;
    @EJB
    private DAMFacade damFacade;
    @EJB
    private MoedaFacade moedaFacade;

    public LancamentoMultaAcessoriaFacade() {
        super(LancamentoMultaAcessoria.class);
    }

    @Override
    public LancamentoMultaAcessoria recuperar(Object id) {
        LancamentoMultaAcessoria multa = em.find(LancamentoMultaAcessoria.class, id);
        multa.getItemLancamentoMultaAcessoria().size();
        if (multa.getProcessoMultaAcessoria() != null) {
            multa.getProcessoMultaAcessoria().getCalculos().size();
            for (CalculoMultaAcessoria calculo : multa.getProcessoMultaAcessoria().getCalculos()) {
                calculo.getListaItemCalculoMultaAcessoria().size();
            }
        }
        return multa;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public LancamentoMultaAcessoria atualizarLancamentoWithProcessoGerado(LancamentoMultaAcessoria lancamento) {
        return em.merge(lancamento);
    }

    public ProcessoCalculoMultaAcessoria criarProcessoCalculoMultaAcessoria(LancamentoMultaAcessoria calculo, ConfiguracaoTributario configuracao, String descricao, BigDecimal valor, Calculo.TipoCalculo tipoCalculo, Boolean issComMOvimento) {
        try {
            ProcessoCalculoMultaAcessoria processo = criarProcessoMultaAcessoria(sistemaFacade.getExercicioCorrente(), configuracao.getDividaMultaFiscalizacao());
            processo.setDescricao(descricao);
            processo.setDataLancamento(sistemaFacade.getDataOperacao());
            processo.setLancamentoMultaAcessoria(calculo);
            processo.setConfiguracaoTributario(configuracao);
            processo.getCalculos().add(criarCalculoMultaAcessoria(calculo, sistemaFacade.getUsuarioCorrente(), processo, configuracao, valor, tipoCalculo, issComMOvimento));
            processo = em.merge(processo);
            return processo;
        } catch (Exception e) {
            logger.error("Não foi possível criar o processo de calculo da multa acessoria: "+ e);
            return null;
        }
    }

    private ProcessoCalculoMultaAcessoria criarProcessoMultaAcessoria(Exercicio exercicio, Divida divida) {
        ProcessoCalculoMultaAcessoria processo = new ProcessoCalculoMultaAcessoria();
        processo.setDataLancamento(getSistemaFacade().getDataOperacao());
        processo.setExercicio(exercicio);
        processo.setDivida(divida);
        processo.setCalculos(new ArrayList<CalculoMultaAcessoria>());
        return processo;
    }

    public CalculoMultaAcessoria criarCalculoMultaAcessoria(LancamentoMultaAcessoria lancamento, UsuarioSistema usuarioSistema, ProcessoCalculoMultaAcessoria processo, ConfiguracaoTributario configuracao, BigDecimal valor, Calculo.TipoCalculo tipoCalculo, Boolean issComMovimento) {
        CalculoMultaAcessoria calculoMultaAcessoria = new CalculoMultaAcessoria();
        calculoMultaAcessoria.setIssComMovimento(issComMovimento);
        if (lancamento.getCadastroEconomico() != null) {
            calculoMultaAcessoria.setCadastro(lancamento.getCadastroEconomico());
        } else {
            calculoMultaAcessoria.setCadastro(null);
        }
        calculoMultaAcessoria.setTipoCalculo(tipoCalculo);
        calculoMultaAcessoria.setUsuarioLancamento(usuarioSistema);
        calculoMultaAcessoria.setProcessoCalculoMultaAcessoria(processo);
        calculoMultaAcessoria.setValorEfetivo(valor);
        calculoMultaAcessoria.setValorReal(valor);
        calculoMultaAcessoria.setListaItemCalculoMultaAcessoria(new ArrayList<ItemCalculoMultaAcessoria>());

        List<ItemCalculoMultaAcessoria> itens = criarItemCalculoMultaAcessoria(lancamento, configuracao, calculoMultaAcessoria);

        calculoMultaAcessoria.getListaItemCalculoMultaAcessoria().addAll(itens);

        CalculoPessoa calculoPessoa = new CalculoPessoa();
        if (lancamento.getCadastroEconomico() != null) {
            calculoPessoa.setPessoa(lancamento.getCadastroEconomico().getPessoa());
        } else {
            calculoPessoa.setPessoa(lancamento.getPessoa());
        }
        calculoPessoa.setCalculo(calculoMultaAcessoria);
        if (calculoMultaAcessoria.getPessoas() == null) {
            calculoMultaAcessoria.setPessoas(new ArrayList<CalculoPessoa>());
        }
        calculoMultaAcessoria.getPessoas().add(calculoPessoa);
        return calculoMultaAcessoria;
    }

    private List<ItemCalculoMultaAcessoria> criarItemCalculoMultaAcessoria(LancamentoMultaAcessoria lancamento,ConfiguracaoTributario configuracao, CalculoMultaAcessoria calculoMultaAcessoria) {
        List<ItemCalculoMultaAcessoria> itens = new ArrayList<>();
        for (ItemLancamentoMultaAcessoria multa : lancamento.getItemLancamentoMultaAcessoria()) {
            ItemCalculoMultaAcessoria item = new ItemCalculoMultaAcessoria();
            item.setTributo(configuracao.getTributoMultaAcessoria());
            item.setValor(multa.getValorMulta());
            item.setCalculoMultaAcessoria(calculoMultaAcessoria);
            itens.add(item);
        }
        return itens;
    }

    public CalculoMultaAcessoria recuperarCalculoMultaAcessoria(LancamentoMultaAcessoria lancamento) {
        String hql = "select calc from CalculoMultaAcessoria calc where calc.procCalcMultaAcessoria.lancamentoMultaAcessoria = :lancamento";
        Query q = em.createQuery(hql);
        q.setParameter("lancamento", lancamento);
        CalculoMultaAcessoria clc = (CalculoMultaAcessoria) q.getSingleResult();
        clc.getListaItemCalculoMultaAcessoria().size();
        return (CalculoMultaAcessoria) q.getSingleResult();
    }

    public MultaFiscalizacaoFacade getMultaFiscalizacaoFacade() {
        return multaFiscalizacaoFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public ConfiguracaoTributarioFacade getConfiguracaoTributarioFacade() {
        return configuracaoTributarioFacade;
    }

    public GeraValorDividaMultaAcessoria getGeraDebitoMultaAcessoria() {
        return geraDebitoMultaAcessoria;
    }

    public DAMFacade getDamFacade() {
        return damFacade;
    }

    public List<ItemLancamentoMultaAcessoria> buscarItensMultaAcessorioPorIdCalculo(Long idCalculo) {
        Query q = em.createNativeQuery("select item.* " +
                "from ITEMLANCMULTAACESSORIA item " +
                "inner join LANCAMENTOMULTAACESSORIA lanc on lanc.id = item.LANCAMENTOMULTAACESSORIA_ID " +
                "inner join PROC_CALCULOMULTAACESSORIA proc on proc.id = lanc.PROCESSOMULTAACESSORIA_ID " +
                "inner join CalculoMultaAcessoria calc on calc.PROCCALCMULTAACESSORIA_ID = proc.id " +
                "where calc.id = :id",
            ItemLancamentoMultaAcessoria.class);
        q.setParameter("id", idCalculo);
        return q.getResultList();
    }

    public MoedaFacade getMoedaFacade() {
        return moedaFacade;
    }
}
