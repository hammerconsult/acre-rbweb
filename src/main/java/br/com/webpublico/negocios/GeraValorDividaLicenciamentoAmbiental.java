package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.util.DataUtil;

import javax.ejb.Stateless;
import java.util.Calendar;

@Stateless
public class GeraValorDividaLicenciamentoAmbiental extends ValorDividaFacade {

    public GeraValorDividaLicenciamentoAmbiental() {
        super(false);
    }

    @Override
    protected void defineVencimentosParcelas(Calculo calculo, OpcaoPagamento op, int i, ParcelaValorDivida parcelaValorDivida, ValorDivida valorDivida, Calendar vencimento, Integer numeroParcelas) {
        parcelaValorDivida.setVencimento(vencimento.getTime());
        vencimento.setTime(DataUtil.ajustarData(parcelaValorDivida.getVencimento(), Calendar.MONTH, i + 1, feriadoFacade, -1));
    }

    @Override
    protected void lancaItem(ValorDivida valorDivida) {
        for (ItemCalculoLicenciamentoAmbiental item : ((CalculoLicenciamentoAmbiental) valorDivida.getCalculo()).getItensCalculo()) {
            ItemValorDivida itemValorDivida = new ItemValorDivida();
            itemValorDivida.setTributo(item.getTributo());
            itemValorDivida.setValor(item.getValor());
            itemValorDivida.setValorDivida(valorDivida);
            valorDivida.getItemValorDividas().add(itemValorDivida);
        }
    }
}
