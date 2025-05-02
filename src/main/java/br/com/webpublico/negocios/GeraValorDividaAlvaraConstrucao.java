package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SituacaoContratoCEASA;
import org.joda.time.LocalDate;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import java.util.Calendar;

@Stateless
public class GeraValorDividaAlvaraConstrucao extends ValorDividaFacade {

    public GeraValorDividaAlvaraConstrucao() {
        super(false);
    }

    @Override
    protected void lancaItem(ValorDivida valorDivida) {
        for (ItemCalculoAlvaraConstrucaoHabitese item : ((CalculoAlvaraConstrucaoHabitese) valorDivida.getCalculo()).getItensCalculo()) {
            ItemValorDivida itemValorDivida = new ItemValorDivida();
            itemValorDivida.setTributo(item.getTributo());
            itemValorDivida.setValor(item.getValorReal());
            itemValorDivida.setValorDivida(valorDivida);
            valorDivida.getItemValorDividas().add(itemValorDivida);
        }
    }

    @Override
    protected void defineVencimentosParcelas(Calculo calculo, OpcaoPagamento op, int i, ParcelaValorDivida parcelaValorDivida, ValorDivida valorDivida, Calendar vencimento, Integer numeroParcelas) {
        CalculoAlvaraConstrucaoHabitese calculoAlvaraConstrucaoHabitese = (CalculoAlvaraConstrucaoHabitese) valorDivida.getCalculo();
        Calendar dataAuxiliar = Calendar.getInstance();
        if (calculoAlvaraConstrucaoHabitese.getVencimento() != null) {
            dataAuxiliar.setTime(calculoAlvaraConstrucaoHabitese.getVencimento());
        } else {
            LocalDate localDate = LocalDate.now().withDayOfMonth(((ParcelaFixaPeriodica) op.getParcelas().get(0)).getDiaVencimento());
            if (localDate.isBefore(LocalDate.now()) || localDate.equals(LocalDate.now())) {
                localDate = localDate.plusMonths(1);
            }
            dataAuxiliar.setTime(localDate.toDate());
            calculoAlvaraConstrucaoHabitese.setVencimento(localDate.toDate());
        }
        dataAuxiliar.add(Calendar.MONTH, i++);
        parcelaValorDivida.setVencimento(dataAuxiliar.getTime());
    }
}
