package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.negocios.tributario.dao.JdbcDividaAtivaDAO;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by William on 26/01/2017.
 */
@Stateless
public class GeraValorDividaContribuicaoMelhoria extends ValorDividaFacade {

    @EJB
    private PersisteValorDivida persisteValorDivida;
    private JdbcDividaAtivaDAO dao;

    public GeraValorDividaContribuicaoMelhoria() {
        super(true);
    }

    @Override
    protected void lancaOpcaoPagamento(ValorDivida valorDivida, List<OpcaoPagamentoDivida> opcoesPagamento) throws Exception {
        for (OpcaoPagamentoDivida opcaoPagamentoDivida : opcoesPagamento) {
            geraParcelas(opcaoPagamentoDivida, valorDivida);
        }
    }

    @Override
    protected Date primeiroVencimento(Calculo calculo, Parcela p) {
        Calendar vencimento = Calendar.getInstance();
        Integer ultimoDiaMes = vencimento.getActualMaximum(Calendar.DAY_OF_MONTH);
        vencimento.set(Calendar.DAY_OF_MONTH, ultimoDiaMes);
        return vencimento.getTime();
    }

    @Override
    protected void lancaItem(ValorDivida valorDivida) {
        ItemValorDivida itemValorDivida = new ItemValorDivida();
        itemValorDivida.setTributo(configuracaoTributarioFacade.retornaUltimo().getTributoContribuicaoMelhoria());
        itemValorDivida.setValor(valorDivida.getValor());
        itemValorDivida.setValorDivida(valorDivida);
        valorDivida.getItemValorDividas().add(itemValorDivida);
    }

    @Override
    protected void defineVencimentosParcelas(Calculo calculo, OpcaoPagamento op, int i, ParcelaValorDivida parcelaValorDivida, ValorDivida valorDivida, Calendar vencimento, Integer numeroParcelas) {
        parcelaValorDivida.setVencimento(vencimento.getTime());
    }

    private void geraParcelas(OpcaoPagamentoDivida opcaoPagamentoDivida, ValorDivida valorDivida) throws Exception {
        List<Parcela> parcelas = calculaPercentualParcelas(opcaoPagamentoDivida.getOpcaoPagamento(), valorDivida);
        if (!parcelas.isEmpty()) {
            parcelasOpcaoPagamento.put(opcaoPagamentoDivida.getOpcaoPagamento(), new ArrayList<ParcelaValorDivida>());
            BigDecimal valorTotalBruto = valorDivida.getValor().setScale(2, MODO_ARREDONDAMENTO);
            OpcaoPagamento opcaoPagamento = em.find(OpcaoPagamento.class, opcaoPagamentoDivida.getOpcaoPagamento().getId());
            BigDecimal valorParcelaBruto;
            BigDecimal valorLancadoBruto = BigDecimal.ZERO;
            for (Parcela p : parcelas) {
                valorParcelaBruto = valorTotalBruto.multiply(p.getPercentualValorTotal()).divide(CEM, NUMERO_CASAS_DECIMAIS, RoundingMode.DOWN);
                ParcelaValorDivida parcelaValorDivida = new ParcelaValorDivida();
                parcelaValorDivida.setDividaAtiva(Boolean.FALSE);
                parcelaValorDivida.setItensParcelaValorDivida(new ArrayList<ItemParcelaValorDivida>());
                parcelaValorDivida.setOpcaoPagamento(opcaoPagamento);
                parcelaValorDivida.setValorDivida(valorDivida);
                parcelaValorDivida.setValor(valorParcelaBruto);
                parcelaValorDivida.setVencimento(p.getVencimento());
                parcelaValorDivida.setPercentualValorTotal(p.getPercentualValorTotal());
                parcelaValorDivida.setSequenciaParcela(p.getSequenciaParcela());
                parcelaValorDivida.getSituacoes().add(new SituacaoParcelaValorDivida(SituacaoParcela.EM_ABERTO, parcelaValorDivida, parcelaValorDivida.getValor()));
                parcelasOpcaoPagamento.get(opcaoPagamento).add(parcelaValorDivida);
                valorDivida.getParcelaValorDividas().add(parcelaValorDivida);
                valorLancadoBruto = valorLancadoBruto.add(parcelaValorDivida.getValor());
            }
            processaDiferencaValorDivida(valorLancadoBruto, valorTotalBruto, opcaoPagamento);
            lancaItensParcela(valorDivida, opcaoPagamentoDivida);
        }
    }

    private JdbcDividaAtivaDAO getDAO() {
        if (dao == null) {
            ApplicationContext ap = ContextLoader.getCurrentWebApplicationContext();
            dao = (JdbcDividaAtivaDAO) ap.getBean("dividaAtivaDAO");
        }
        return dao;
    }


    @Override
    public void salvaValorDivida(ValorDivida valordivida) throws Exception {
        getDAO().persisteValorDivida(valordivida);
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void geraDebito(CalculoContribuicaoMelhoria calculo) {
        try {
            List<OpcaoPagamentoDivida> opcoesPagamento = validaOpcoesPagamento(calculo.getProcessoCalcContMelhoria().getDivida());
            geraValorDivida(calculo, opcoesPagamento);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
