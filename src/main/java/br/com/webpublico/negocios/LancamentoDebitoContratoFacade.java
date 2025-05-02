package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.negocios.tributario.GeraValorDividaContrato;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Stateless
public class LancamentoDebitoContratoFacade extends AbstractFacade<ProcessoCalculoContrato> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ConfiguracaoTributarioFacade configuracaoTributarioFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;
    @EJB
    private ContratoFacade contratoFacade;
    @EJB
    private GeraValorDividaContrato geraValorDividaContrato;

    public LancamentoDebitoContratoFacade() {
        super(ProcessoCalculoContrato.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ProcessoCalculoContrato gerarDebito(ProcessoCalculoContrato processo, BigDecimal valorLancamento) {
        try {
            if (processo.getNumero() == null) {
                processo.setNumero(singletonGeradorCodigo.getProximoCodigo(ProcessoCalculoContrato.class, "numero"));
            }
            ConfiguracaoTributario configTrib = configuracaoTributarioFacade.retornaUltimo();
            processo.setDivida(configTrib.getDividaContratoConcessao());
            processo = em.merge(processo);
            CalculoContrato calculoContrato = gerarNovoCalculoContrato(processo, valorLancamento);

            List<OpcaoPagamentoDivida> opcoesPagamentoDividas = buscarOpcoesPagamentoDivida(configTrib);
            geraValorDividaContrato.geraValorDivida(calculoContrato, opcoesPagamentoDividas);
            contratoFacade.diminuirSaldoAtualContrato(valorLancamento, processo.getContrato());
            return processo;
        } catch (Exception e) {
            logger.error("Problema ao gerar opções de pagamento do débito no cálculo de contrato de concessão {}", e);
            throw new ExcecaoNegocioGenerica(e.getMessage());
        }
    }

    private CalculoContrato gerarNovoCalculoContrato(ProcessoCalculoContrato processo, BigDecimal valorLancamento) {
        CalculoContrato calculoContrato = new CalculoContrato();
        calculoContrato.setProcessoCalculoContrato(processo);
        calculoContrato.setProcessoCalculo(processo);
        calculoContrato.setDataCalculo(processo.getDataLancamento());
        calculoContrato.setSubDivida(Long.valueOf(processo.getCalculos().size()));
        calculoContrato.setReferencia(processo.getContrato().getNumeroContrato() + " - " + processo.getContrato().getNumeroAnoTermo());
        calculoContrato.setObservacao(processo.getContrato().toString() + " - " + processo.getContrato().getObjeto());
        calculoContrato.setValorEfetivo(valorLancamento);
        calculoContrato.setValorReal(valorLancamento);
        gerarCalculoPessoa(processo, calculoContrato);
        calculoContrato = em.merge(calculoContrato);
        return calculoContrato;
    }

    private List<OpcaoPagamentoDivida> buscarOpcoesPagamentoDivida(ConfiguracaoTributario configTrib) {
        if (configTrib.getDividaContratoConcessao() == null) {
            throw new ExcecaoNegocioGenerica("Dívida não configurada para o tipo contrato concessão.");
        }
        if (configTrib.getTributoContratoConcessao() == null) {
            throw new ExcecaoNegocioGenerica("Tributo não configurado para o tipo contrato concessão.");
        }
        return geraValorDividaContrato.validaOpcoesPagamento(configTrib.getDividaContratoConcessao(), new Date());
    }

    private void gerarCalculoPessoa(ProcessoCalculoContrato entity, CalculoContrato calculoContrato) {
        CalculoPessoa calculoPessoa = new CalculoPessoa();
        calculoPessoa.setCalculo(calculoContrato);
        calculoPessoa.setPessoa(entity.getContrato().getContratado());
        calculoContrato.getPessoas().add(calculoPessoa);
    }

    public List<ResultadoParcela> buscarParcelas(Contrato contrato) {
        List<Long> idsCalculo = getIdsCalculo(contrato);
        if (!idsCalculo.isEmpty()) {
            ConsultaParcela consultaParcela = new ConsultaParcela();
            consultaParcela.addParameter(ConsultaParcela.Campo.CALCULO_ID, ConsultaParcela.Operador.IN, idsCalculo);
            return consultaParcela.executaConsulta().getResultados();
        }
        return Lists.newArrayList();
    }

    private List<Long> getIdsCalculo(Contrato contrato) {
        List<Long> idsCalculos = Lists.newArrayList();
        List<CalculoContrato> calculosContrato = buscarCalculoContrato(contrato);
        for (CalculoContrato cc : calculosContrato) {
            idsCalculos.add(cc.getId());
        }
        return idsCalculos;
    }

    public void gerarDam(ResultadoParcela parcela) {
        try {
            DAM dam = geraValorDividaContrato.getDamFacade().buscarOuGerarDam(parcela);
            if (dam == null) {
                throw new ExcecaoNegocioGenerica("Não foi possível gerar o dam para a parcela referência: " + parcela.getReferencia() + ".");
            }
            ImprimeDAM imprimeDAM = new ImprimeDAM();
            imprimeDAM.setGeraNoDialog(true);
            imprimeDAM.imprimirDamUnicoViaApi(dam);
        } catch (Exception e) {
            logger.error("Não foi possível gerar o dam para a parcela id {" + parcela.getIdParcela() + "}");
        }
    }

    public ProcessoCalculoContrato buscarProcessoCalculoContratoPorExercicio(Contrato contrato, Exercicio exercicio) {
        String sql = " select proc.*, processo.* from processocalculocontrato proc " +
            " inner join processocalculo processo on processo.id = proc.id " +
            " where proc.contrato_id = :idContrato " +
            " and processo.exercicio_id = :ano ";
        Query q = em.createNativeQuery(sql, ProcessoCalculoContrato.class);
        q.setParameter("idContrato", contrato.getId());
        q.setParameter("ano", exercicio.getId());
        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            ProcessoCalculoContrato processo = (ProcessoCalculoContrato) q.getResultList().get(0);
            Hibernate.initialize(processo.getCalculos());
            return processo;
        }
        return null;
    }

    public List<CalculoContrato> buscarCalculoContrato(Contrato contrato) {
        String sql = " select cc.*, calc.* from calculocontrato cc " +
            " inner join calculo calc on calc.id = cc.id " +
            " inner join processocalculocontrato proc on cc.processocalculocontrato_id = proc.id " +
            " where proc.contrato_id = :idContrato ";
        Query q = em.createNativeQuery(sql, CalculoContrato.class);
        q.setParameter("idContrato", contrato.getId());
        return q.getResultList();
    }

    public ConfiguracaoTributarioFacade getConfiguracaoTributarioFacade() {
        return configuracaoTributarioFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public ContratoFacade getContratoFacade() {
        return contratoFacade;
    }

    public GeraValorDividaContrato getGeraValorDividaContrato() {
        return geraValorDividaContrato;
    }
}
