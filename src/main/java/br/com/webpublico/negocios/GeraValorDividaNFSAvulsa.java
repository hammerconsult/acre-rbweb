package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;

import javax.ejb.Stateless;
import java.util.Calendar;

@Stateless
public class GeraValorDividaNFSAvulsa extends ValorDividaFacade {

    public GeraValorDividaNFSAvulsa() {
        super(false);
    }

    @Override
    protected void lancaItem(ValorDivida valorDivida) {
        CalculoNFSAvulsa calculo = (CalculoNFSAvulsa) valorDivida.getCalculo();
        ItemValorDivida itemValorDivida = new ItemValorDivida();
        itemValorDivida.setTributo(calculo.getTributo());
        itemValorDivida.setValor(calculo.getValorEfetivo());
        itemValorDivida.setValorDivida(valorDivida);
        valorDivida.getItemValorDividas().add(itemValorDivida);
    }

    @Override
    protected void defineVencimentosParcelas(Calculo calculo, OpcaoPagamento op, int i, ParcelaValorDivida parcelaValorDivida, ValorDivida valorDivida, Calendar vencimento, Integer numeroParcelas) {
        vencimento.set(Calendar.DAY_OF_MONTH, 10);
        parcelaValorDivida.setVencimento(ajustaVencimento(vencimento.getTime(), Calendar.MONTH, 1));
    }
}
