package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TipoParcela;

import javax.ejb.Stateless;
import java.util.Calendar;
import java.util.Date;

@Stateless
public class GeraValorDividaISSForaMunicipio extends ValorDividaFacade {

    public GeraValorDividaISSForaMunicipio() {
        super(false);
    }

    @Override
    protected void defineVencimentosParcelas(Calculo calculo, OpcaoPagamento op, int i, ParcelaValorDivida parcelaValorDivida, ValorDivida valorDivida, Calendar vencimento, Integer numeroParcelas) {
        if (TipoParcela.PERIODICA.equals(op.getTipoParcela()) && !op.getParcelas().isEmpty()) {
            Integer diaVencimento = ((ParcelaFixaPeriodica) op.getParcelas().get(0)).getDiaVencimento();
            vencimento.set(Calendar.DAY_OF_MONTH, diaVencimento);
            vencimento.set(Calendar.MONTH, calculo.getMesReferencia().getNumeroMes());
            vencimento.set(Calendar.YEAR, calculo.getProcessoCalculo().getExercicio().getAno());
            parcelaValorDivida.setVencimento(vencimento.getTime());
        } else if (TipoParcela.FIXA.equals(op.getTipoParcela())) {
            parcelaValorDivida.setVencimento(op.getParcelas().get(0).getVencimento());
        } else {
            parcelaValorDivida.setVencimento(new Date());
        }
    }

    @Override
    protected void lancaItem(ValorDivida valorDivida) {
        for (ItemCalculoIss item : ((CalculoISS) valorDivida.getCalculo()).getItemCalculoIsss()) {
            ItemValorDivida itemValorDivida = new ItemValorDivida();
            itemValorDivida.setTributo(item.getTributo());
            itemValorDivida.setValor(item.getValorCalculado());
            itemValorDivida.setValorDivida(valorDivida);
            valorDivida.getItemValorDividas().add(itemValorDivida);
        }
    }
}
