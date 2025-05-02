package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;

import javax.ejb.Stateless;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Stateless
public class GeraValorDividaLicitacaoCeasa extends ValorDividaFacade {

    public GeraValorDividaLicitacaoCeasa() {
        super(false);
    }

    @Override
    protected void lancaOpcaoPagamento(ValorDivida valorDivida, List<OpcaoPagamentoDivida> opcoesPagamento) throws Exception {
        for (OpcaoPagamentoDivida opcaoPagamentoDivida : opcoesPagamento) {
            geraParcelas(opcaoPagamentoDivida, valorDivida, 1);
        }
    }

    @Override
    protected Date primeiroVencimento(Calculo calculo, Parcela p) {
        Calendar vencimento = Calendar.getInstance();
        vencimento.setTime(((CalculoLicitacaoCEASA) calculo).getContrato().getDataInicio());
        Integer ultimoDiaMes = vencimento.getActualMaximum(Calendar.DAY_OF_MONTH);
        vencimento.set(Calendar.DAY_OF_MONTH, ultimoDiaMes);
        return vencimento.getTime();
    }

    @Override
    protected void lancaItem(ValorDivida valorDivida) {
        ItemValorDivida itemValorDivida = new ItemValorDivida();
        itemValorDivida.setTributo(configuracaoTributarioFacade.retornaUltimo().getCeasaTributoLicitacao());
        itemValorDivida.setValor(valorDivida.getValor());
        itemValorDivida.setValorDivida(valorDivida);
        valorDivida.getItemValorDividas().add(itemValorDivida);
    }

    @Override
    protected void defineVencimentosParcelas(Calculo calculo, OpcaoPagamento op, int i, ParcelaValorDivida parcelaValorDivida, ValorDivida valorDivida, Calendar vencimento, Integer numeroParcelas) {
        parcelaValorDivida.setVencimento(vencimento.getTime());
    }
}
