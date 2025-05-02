package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;

import javax.ejb.Stateless;
import java.util.Calendar;
import java.util.List;

/**
 * Created by tharlyson on 05/12/19.
 */
@Stateless
public class GeraValorDividaPagamentoSubvencao extends ValorDividaFacade {

    public GeraValorDividaPagamentoSubvencao() {
        super(false);
    }

    @Override
    protected void defineVencimentosParcelas(Calculo calculo, OpcaoPagamento op, int i, ParcelaValorDivida parcelaValorDivida, ValorDivida valorDivida, Calendar vencimento, Integer numeroParcelas) {
        parcelaValorDivida.setVencimento(((CalculoPagamentoSubvencao) calculo).getVencimentoOriginalParcela());
    }

    @Override
    protected void lancaItem(ValorDivida valorDivida) {
        for (ItensCalculoSubvencao item : ((CalculoPagamentoSubvencao) valorDivida.getCalculo()).getItensCalculoSubvencao()) {
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
}
