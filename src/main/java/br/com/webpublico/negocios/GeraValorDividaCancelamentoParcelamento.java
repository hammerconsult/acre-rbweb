package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SituacaoParcela;

import javax.ejb.Stateless;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;

@Stateless
public class GeraValorDividaCancelamentoParcelamento extends ValorDividaFacade {

    private OpcaoPagamento opcaoPagamentoParcela;

    public GeraValorDividaCancelamentoParcelamento() {
        super(false);
    }

    public void setOpcaoPagamentoParcela(OpcaoPagamento opcaoPagamentoParcela) {
        this.opcaoPagamentoParcela = opcaoPagamentoParcela;
    }

    @Override
    protected void defineVencimentosParcelas(Calculo calculo, OpcaoPagamento op, int i, ParcelaValorDivida
        parcelaValorDivida, ValorDivida valorDivida, Calendar vencimento, Integer numeroParcelas) {
        parcelaValorDivida.setVencimento(((CancelamentoParcelamento) calculo).getVencimento());

    }

    @Override
    protected void lancaItem(ValorDivida valorDivida) {
        for (ItemCancelamentoParcelamento item : ((CancelamentoParcelamento) valorDivida.getCalculo()).getItens()) {
            ItemValorDivida itemValorDivida = new ItemValorDivida();
            itemValorDivida.setTributo(item.getTributo());
            itemValorDivida.setValor(item.getValor());
            itemValorDivida.setValorDivida(valorDivida);
            valorDivida.getItemValorDividas().add(itemValorDivida);
        }
    }

    @Override
    protected void lancaSituacao(ParcelaValorDivida parcelaValorDivida) {
        SituacaoParcelaValorDivida spvd = new SituacaoParcelaValorDivida(SituacaoParcela.EM_ABERTO, parcelaValorDivida, parcelaValorDivida.getValor());
        spvd.setGeraReferencia(false);
        spvd.setReferencia(parcelaValorDivida.getValorDivida().getCalculo().getReferencia());
        parcelaValorDivida.getSituacoes().add(spvd);
        parcelaValorDivida.setDividaAtiva(((CancelamentoParcelamento) parcelaValorDivida.getValorDivida().getCalculo()).getProcessoCalculo().getDividaAtiva());
        parcelaValorDivida.setDividaAtivaAjuizada(((CancelamentoParcelamento) parcelaValorDivida.getValorDivida().getCalculo()).getProcessoCalculo().getDividaAtivaAjuizada());
    }

    @Override
    protected void geraParcelas(OpcaoPagamentoDivida opcaoPagamentoDivida, ValorDivida valorDivida, Integer numeroParcelas) throws Exception {
        if (opcaoPagamentoParcela != null && !opcaoPagamentoDivida.getOpcaoPagamento().getPromocional()) {
            parcelasOpcaoPagamento.put(opcaoPagamentoParcela, new ArrayList<ParcelaValorDivida>());
            BigDecimal valorTotal = valorDivida.getValor().setScale(NUMERO_CASAS_DECIMAIS, MODO_ARREDONDAMENTO);
            BigDecimal valorLancadoBruto = BigDecimal.ZERO;
            Calendar vencimento = Calendar.getInstance();
            Calculo calculo = valorDivida.getCalculo();
            BigDecimal valorParcelaBruto = valorTotal.divide(BigDecimal.valueOf(numeroParcelas), NUMERO_CASAS_DECIMAIS, RoundingMode.DOWN);

            ParcelaValorDivida parcelaValorDivida = new ParcelaValorDivida();
            parcelaValorDivida.setDividaAtiva(((CancelamentoParcelamento) calculo).getDividaAtiva());
            parcelaValorDivida.setDividaAtivaAjuizada(((CancelamentoParcelamento) calculo).getDividaAtivaAjuizada());
            parcelaValorDivida.setSequenciaParcela(((CancelamentoParcelamento) calculo).getSequenciaParcela());
            parcelaValorDivida.setQuantidadeParcela(((CancelamentoParcelamento) calculo).getQuantidadeParcela());
            parcelaValorDivida.setItensParcelaValorDivida(new ArrayList<ItemParcelaValorDivida>());
            parcelaValorDivida.setOpcaoPagamento(opcaoPagamentoParcela);
            parcelaValorDivida.setValorDivida(valorDivida);
            parcelaValorDivida.setValor(valorParcelaBruto);
            defineVencimentosParcelas(calculo, opcaoPagamentoParcela, 0, parcelaValorDivida, valorDivida, vencimento, numeroParcelas);
            parcelaValorDivida.setPercentualValorTotal(CEM);
            parcelasOpcaoPagamento.get(opcaoPagamentoParcela).add(parcelaValorDivida);
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

    @Override
    public void lancaItensParcela(ValorDivida valorDivida, OpcaoPagamentoDivida opcaoPagamentoDivida) {
        for (ParcelaValorDivida parcelaValorDivida : valorDivida.getParcelaValorDividas()) {
            BigDecimal valorTotalBruto = BigDecimal.ZERO;
            for (ItemValorDivida item : valorDivida.getItemValorDividas()) {
                ItemParcelaValorDivida itemParcela = new ItemParcelaValorDivida();
                itemParcela.setItemValorDivida(item);
                itemParcela.setParcelaValorDivida(parcelaValorDivida);
                itemParcela.setValor(item.getValor());
                lancaDescontos(itemParcela, valorDivida.getCalculo().getTipoCalculo().getOrigem());
                parcelaValorDivida.getItensParcelaValorDivida().add(itemParcela);
                valorTotalBruto = valorTotalBruto.add(itemParcela.getValor());
            }
            processaDiferencaParcela(parcelaValorDivida.getValor(), valorTotalBruto, parcelaValorDivida);
        }
    }
}
