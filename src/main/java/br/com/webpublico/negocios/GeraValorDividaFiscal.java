package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import java.util.Calendar;
import javax.ejb.Stateless;

@Stateless
public class GeraValorDividaFiscal extends ValorDividaFacade {

    public GeraValorDividaFiscal() {
        super(false);
    }

    @Override
    protected void lancaItem(ValorDivida valorDivida) {
        for (ItensCalculoFiscalizacao item : ((CalculoFiscalizacao) valorDivida.getCalculo()).getItensCalculoFiscalizacao()) {
            ItemValorDivida itemValorDivida = new ItemValorDivida();
            itemValorDivida.setTributo(item.getTributo());
            itemValorDivida.setValor(item.getValor());
            itemValorDivida.setValorDivida(valorDivida);
            valorDivida.getItemValorDividas().add(itemValorDivida);
        }
    }

    @Override
    protected void defineVencimentosParcelas(Calculo calculo, OpcaoPagamento op, int i, ParcelaValorDivida parcelaValorDivida, ValorDivida valorDivida, Calendar vencimento, Integer numeroParcelas) {
        parcelaValorDivida.setVencimento(((CalculoFiscalizacao) calculo).getVencimento());
    }
}
