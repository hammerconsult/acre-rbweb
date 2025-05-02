package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.CalculoAlvaraFuncionamento;

import java.util.Calendar;
import javax.ejb.Stateless;

@Stateless
public class GeraValorDividaAlvaraFuncionamento extends ValorDividaFacade {

    public GeraValorDividaAlvaraFuncionamento() {
        super(false);
    }

    @Override
    protected void lancaItem(ValorDivida valorDivida) {
        CalculoAlvaraFuncionamento calculo = em.find(CalculoAlvaraFuncionamento.class, valorDivida.getCalculo().getId());
        for (ItemCalculoAlvaraFunc item : calculo.getItens()) {
            ItemValorDivida itemValorDivida = new ItemValorDivida();
            itemValorDivida.setTributo(item.getTributo());
            itemValorDivida.setValor(item.getValorEfetivo());
            itemValorDivida.setValorDivida(valorDivida);
            valorDivida.getItemValorDividas().add(itemValorDivida);
        }
    }

    @Override
    protected void defineVencimentosParcelas(Calculo calculo, OpcaoPagamento op, int i, ParcelaValorDivida parcelaValorDivida, ValorDivida valorDivida, Calendar vencimento, Integer numeroParcelas) {
        parcelaValorDivida.setVencimento(((CalculoAlvaraFuncionamento) calculo).getVencimento());
    }
}
