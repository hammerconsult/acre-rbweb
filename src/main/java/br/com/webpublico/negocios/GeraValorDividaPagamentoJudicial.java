package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SituacaoParcela;

import javax.ejb.Stateless;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: William
 * Date: 28/05/15
 * Time: 14:25
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class GeraValorDividaPagamentoJudicial extends ValorDividaFacade {

    public GeraValorDividaPagamentoJudicial() {
        super(false);
    }

    @Override
    protected void defineVencimentosParcelas(Calculo calculo, OpcaoPagamento op, int i, ParcelaValorDivida
            parcelaValorDivida, ValorDivida valorDivida, Calendar vencimento, Integer numeroParcelas) {
        if (((CalculoPagamentoJudicial) calculo).getSituacaoParcelaGerada().equals(SituacaoParcela.EM_ABERTO)) {
            if (((CalculoPagamentoJudicial) calculo).getPagamentoJudicial().getDataBloqueio() != null) {
                parcelaValorDivida.setVencimento(((CalculoPagamentoJudicial) calculo).getPagamentoJudicial().getDataBloqueio());
            } else {
                parcelaValorDivida.setVencimento(((CalculoPagamentoJudicial) calculo).getPagamentoJudicial().getDataCompensacao());
            }
        } else {
            parcelaValorDivida.setVencimento(((CalculoPagamentoJudicial) calculo).getVencimentoOriginalParcela());
        }
    }

    @Override
    protected void lancaItem(ValorDivida valorDivida) {
        for (ItensCalculoJudicial item : ((CalculoPagamentoJudicial) valorDivida.getCalculo()).getItensCalculoJudicial()) {
            ItemValorDivida itemValorDivida = new ItemValorDivida();
            itemValorDivida.setTributo(item.getTributo());
            itemValorDivida.setValor(item.getValor());
            itemValorDivida.setValorDivida(valorDivida);
            valorDivida.getItemValorDividas().add(itemValorDivida);
        }
    }

    @Override
    protected void lancaSituacao(ParcelaValorDivida parcelaValorDivida) {
        if (((CalculoPagamentoJudicial) parcelaValorDivida.getValorDivida().getCalculo()).getSituacaoParcelaGerada().equals(SituacaoParcela.EM_ABERTO)) {
            parcelaValorDivida.getSituacoes().add(new SituacaoParcelaValorDivida(SituacaoParcela.EM_ABERTO, parcelaValorDivida, parcelaValorDivida.getValor()));
        } else {
            parcelaValorDivida.getSituacoes().add(new SituacaoParcelaValorDivida(SituacaoParcela.COMPENSACAO, parcelaValorDivida, parcelaValorDivida.getValor()));
        }

    }

    @Override
    public List<OpcaoPagamentoDivida> validaOpcoesPagamento(Divida divida, Date dataBase) throws IllegalArgumentException {
        List<OpcaoPagamentoDivida> opcoesPagamento = super.validaOpcoesPagamento(divida, dataBase);
        if (opcoesPagamento.size() > 1) {
            OpcaoPagamentoDivida op = null;
            for (OpcaoPagamentoDivida opcaoPagamentoDivida : opcoesPagamento) {
                if (!opcaoPagamentoDivida.getOpcaoPagamento().getPromocional()) {
                    op = opcaoPagamentoDivida;
                }
            }
            if (op != null) {
                opcoesPagamento.clear();
                opcoesPagamento.add(op);
            }
        }
        return opcoesPagamento;
    }

    @Override
    protected void geraParcelas(OpcaoPagamentoDivida opcaoPagamentoDivida, ValorDivida valorDivida, Integer numeroParcelas) throws Exception {
        OpcaoPagamento op = recarregarOpcaoPagamento(opcaoPagamentoDivida.getOpcaoPagamento());
        parcelasOpcaoPagamento.put(op, new ArrayList<ParcelaValorDivida>());
        BigDecimal percentualParcela = CEM.divide(new BigDecimal(numeroParcelas), NUMERO_CASAS_DECIMAIS, MODO_ARREDONDAMENTO);
        BigDecimal valorTotal = valorDivida.getValor().setScale(NUMERO_CASAS_DECIMAIS, MODO_ARREDONDAMENTO);
        BigDecimal valorLancadoBruto = BigDecimal.ZERO;
        Calendar vencimento = Calendar.getInstance();
        Calculo calculo = valorDivida.getCalculo();
        BigDecimal valorParcelaBruto = valorTotal.divide(BigDecimal.valueOf(numeroParcelas), NUMERO_CASAS_DECIMAIS, RoundingMode.DOWN);
        for (int i = 0; i < 1; i++) {
            ParcelaValorDivida parcelaValorDivida = new ParcelaValorDivida();
            parcelaValorDivida.setDividaAtiva(((CalculoPagamentoJudicial) calculo).getDividaAtiva());
            parcelaValorDivida.setDividaAtivaAjuizada(((CalculoPagamentoJudicial) calculo).getDividaAtivaAjuizada());
            parcelaValorDivida.setSequenciaParcela(((CalculoPagamentoJudicial) calculo).getSequenciaParcela());
            parcelaValorDivida.setQuantidadeParcela(((CalculoPagamentoJudicial) calculo).getQuantidadeParcela());
            parcelaValorDivida.setItensParcelaValorDivida(new ArrayList<ItemParcelaValorDivida>());
            parcelaValorDivida.setOpcaoPagamento(op);
            parcelaValorDivida.setValorDivida(valorDivida);
            parcelaValorDivida.setValor(valorParcelaBruto);
            defineVencimentosParcelas(calculo, op, i, parcelaValorDivida, valorDivida, vencimento, numeroParcelas);
            parcelaValorDivida.setPercentualValorTotal(percentualParcela);
            parcelasOpcaoPagamento.get(op).add(parcelaValorDivida);
            valorDivida.getParcelaValorDividas().add(parcelaValorDivida);
            valorLancadoBruto = valorLancadoBruto.add(parcelaValorDivida.getValor());
        }
        if (valorLancadoBruto.compareTo(valorTotal) != 0) {
            BigDecimal valor = valorDivida.getParcelaValorDividas().get(0).getValor();
            valor = valor.add(valorTotal.subtract(valorLancadoBruto));
            valorDivida.getParcelaValorDividas().get(0).setValor(valor);
        }
        lancaSituacoes(valorDivida);
        lancaItensParcela(valorDivida, opcaoPagamentoDivida);
    }

    private void lancaSituacoes(ValorDivida valorDivida) {
        for (ParcelaValorDivida pvd : valorDivida.getParcelaValorDividas()) {
            lancaSituacao(pvd);
        }
    }

    @Override
    public void lancaItensParcela(ValorDivida valorDivida, OpcaoPagamentoDivida opcaoPagamentoDivida) {
        for (ParcelaValorDivida parcelaValorDivida : valorDivida.getParcelaValorDividas()) {
            if (parcelaValorDivida.getOpcaoPagamento().equals(opcaoPagamentoDivida.getOpcaoPagamento())) {
                for (ItemValorDivida item : valorDivida.getItemValorDividas()) {
                    ItemParcelaValorDivida itemParcela = new ItemParcelaValorDivida();
                    itemParcela.setItemValorDivida(item);
                    itemParcela.setParcelaValorDivida(parcelaValorDivida);
                    itemParcela.setValor(item.getValor());
                    lancaDescontos(itemParcela, valorDivida.getCalculo().getTipoCalculo().getOrigem());
                    parcelaValorDivida.getItensParcelaValorDivida().add(itemParcela);
                }
            }
        }
    }

}
