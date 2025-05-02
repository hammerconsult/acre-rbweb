package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;

import javax.ejb.Stateless;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Stateless
public class GeraValorDividaContaCorrente extends ValorDividaFacade {

    public GeraValorDividaContaCorrente() {
        super(false);
    }

    @Override
    protected void defineVencimentosParcelas(Calculo calculo, OpcaoPagamento op, int i, ParcelaValorDivida parcelaValorDivida, ValorDivida valorDivida, Calendar vencimento, Integer numeroParcelas) {
        parcelaValorDivida.setVencimento(((CalculoContaCorrente) calculo).getVencimento());
    }

    @Override
    public boolean definirComoDividaAtiva(Calculo calculo) {
        if (calculo instanceof CalculoContaCorrente) {
            return ((CalculoContaCorrente) calculo).getDividaAtiva();
        }
        return super.definirComoDividaAtiva(calculo);
    }

    @Override
    public boolean definirComoDividaAtivaAjuizada(Calculo calculo) {
        if (calculo instanceof CalculoContaCorrente) {
            return ((CalculoContaCorrente) calculo).getDividaAtivaAjuizada();
        }
        return super.definirComoDividaAtivaAjuizada(calculo);
    }

    @Override
    protected void lancaItem(ValorDivida valorDivida) {
        for (ItemCalculoContaCorrente item : ((CalculoContaCorrente) valorDivida.getCalculo()).getItens()) {
            ItemValorDivida itemValorDivida = new ItemValorDivida();
            itemValorDivida.setTributo(item.getTributo());
            itemValorDivida.setValor(item.getValor());
            itemValorDivida.setValorDivida(valorDivida);
            valorDivida.getItemValorDividas().add(itemValorDivida);
        }
    }

    @Override
    protected void lancaOpcaoPagamento(ValorDivida valorDivida, List<OpcaoPagamentoDivida> opcoesPagamento) throws Exception {
        for (OpcaoPagamentoDivida opcaoPagamentoDivida : opcoesPagamento) {
            if (!opcaoPagamentoDivida.getOpcaoPagamento().getPromocional()) {
                geraParcelas(opcaoPagamentoDivida, valorDivida, 1);
            }
        }
    }

    @Override
    protected void geraParcelas(OpcaoPagamentoDivida opcaoPagamentoDivida, ValorDivida valorDivida, Integer numeroParcelas) throws Exception {
        if (opcaoPagamentoDivida != null) {
            parcelasOpcaoPagamento.put(opcaoPagamentoDivida.getOpcaoPagamento(), new ArrayList<ParcelaValorDivida>());
            BigDecimal valorTotal = valorDivida.getValor().setScale(NUMERO_CASAS_DECIMAIS, MODO_ARREDONDAMENTO);
            BigDecimal valorLancadoBruto = BigDecimal.ZERO;
            Calendar vencimento = Calendar.getInstance();
            Calculo calculo = valorDivida.getCalculo();
            BigDecimal valorParcelaBruto = valorTotal.divide(BigDecimal.valueOf(numeroParcelas), NUMERO_CASAS_DECIMAIS, RoundingMode.DOWN);

            ParcelaValorDivida parcelaValorDivida = new ParcelaValorDivida();
            parcelaValorDivida.setDividaAtiva(((CalculoContaCorrente) calculo).getDividaAtiva());
            parcelaValorDivida.setDividaAtivaAjuizada(((CalculoContaCorrente) calculo).getDividaAtivaAjuizada());
            parcelaValorDivida.setSequenciaParcela(((CalculoContaCorrente) calculo).getSequenciaParcela());
            parcelaValorDivida.setQuantidadeParcela(((CalculoContaCorrente) calculo).getQuantidadeParcela());
            parcelaValorDivida.setItensParcelaValorDivida(new ArrayList<ItemParcelaValorDivida>());
            parcelaValorDivida.setOpcaoPagamento(opcaoPagamentoDivida.getOpcaoPagamento());
            parcelaValorDivida.setValorDivida(valorDivida);
            parcelaValorDivida.setValor(valorParcelaBruto);
            defineVencimentosParcelas(calculo, opcaoPagamentoDivida.getOpcaoPagamento(), 0, parcelaValorDivida, valorDivida, vencimento, numeroParcelas);
            parcelaValorDivida.setPercentualValorTotal(CEM);
            parcelasOpcaoPagamento.get(opcaoPagamentoDivida.getOpcaoPagamento()).add(parcelaValorDivida);
            valorDivida.getParcelaValorDividas().add(parcelaValorDivida);
            valorLancadoBruto = valorLancadoBruto.add(parcelaValorDivida.getValor());

            if (valorLancadoBruto.compareTo(valorTotal) != 0) {
                BigDecimal valor = valorDivida.getParcelaValorDividas().get(0).getValor();
                valor = valor.add(valorTotal.subtract(valorLancadoBruto));
                valorDivida.getParcelaValorDividas().get(0).setValor(valor);
            }
            lancaSituacoes(valorDivida);
            lancaItensParcela(valorDivida, opcaoPagamentoDivida);
        }
    }

    private void lancaSituacoes(ValorDivida valorDivida) {
        for (ParcelaValorDivida pvd : valorDivida.getParcelaValorDividas()) {
            lancaSituacao(pvd);
        }
    }
}
