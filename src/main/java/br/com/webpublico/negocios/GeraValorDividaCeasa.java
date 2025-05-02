package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SituacaoContratoCEASA;

import javax.ejb.Stateless;
import java.util.Calendar;
import java.util.List;

@Stateless
public class GeraValorDividaCeasa extends ValorDividaFacade {

    public GeraValorDividaCeasa() {
        super(false);
    }

    @Override
    protected void lancaOpcaoPagamento(ValorDivida valorDivida, List<OpcaoPagamentoDivida> opcoesPagamento) throws Exception {
        for (OpcaoPagamentoDivida opcaoPagamentoDivida : opcoesPagamento) {
            geraParcelas(opcaoPagamentoDivida, valorDivida, ((CalculoCEASA) valorDivida.getCalculo()).getContrato().getQuantidadeParcelas());
        }
    }

    @Override
    protected void lancaItem(ValorDivida valorDivida) {
        for (ItensCalculoCEASA item : ((CalculoCEASA) valorDivida.getCalculo()).getItensCalculoCEASA()) {
            ItemValorDivida itemValorDivida = new ItemValorDivida();
            itemValorDivida.setTributo(item.getTributo());
            itemValorDivida.setValor(item.getValor());
            itemValorDivida.setValorDivida(valorDivida);
            valorDivida.getItemValorDividas().add(itemValorDivida);
        }
    }

    @Override
    protected void defineVencimentosParcelas(Calculo calculo, OpcaoPagamento op, int i, ParcelaValorDivida parcelaValorDivida, ValorDivida valorDivida, Calendar vencimento, Integer numeroParcelas) {
        ContratoCEASA contrato = ((CalculoCEASA) valorDivida.getCalculo()).getContrato();
        int diaVencimento = contrato.getDiaVencimento();

        Calendar dataAuxiliar = Calendar.getInstance();
        if (contrato.getOriginario() != null &&
            SituacaoContratoCEASA.ALTERADO.equals(contrato.getOriginario().getSituacaoContrato()) &&
            contrato.getDataOperacao() != null) {
            dataAuxiliar.setTime(contrato.getDataOperacao());
        } else {
            dataAuxiliar.setTime(contrato.getDataInicio());
        }

        if (diaVencimento >= dataAuxiliar.get(Calendar.DAY_OF_MONTH)) {
            dataAuxiliar.set(Calendar.DAY_OF_MONTH, diaVencimento);
            dataAuxiliar.add(Calendar.MONTH, i);
            parcelaValorDivida.setVencimento(dataAuxiliar.getTime());
        } else {
            dataAuxiliar.set(Calendar.DAY_OF_MONTH, diaVencimento);
            dataAuxiliar.add(Calendar.MONTH, ++i);
            parcelaValorDivida.setVencimento(dataAuxiliar.getTime());
        }

        parcelaValorDivida.setVencimento(dataAuxiliar.getTime());
    }
}
