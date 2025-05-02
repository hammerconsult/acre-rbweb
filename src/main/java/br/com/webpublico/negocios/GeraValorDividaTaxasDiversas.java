package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SituacaoParcela;

import java.math.BigDecimal;
import java.util.Calendar;
import javax.ejb.Stateless;

@Stateless
public class GeraValorDividaTaxasDiversas extends ValorDividaFacade {

    public GeraValorDividaTaxasDiversas() {
        super(false);
    }

    @Override
    protected void lancaItem(ValorDivida valorDivida) {
        for (ItemCalculoTaxasDiversas it : ((CalculoTaxasDiversas) valorDivida.getCalculo()).getItens()) {
            ItemValorDivida itemValorDivida = new ItemValorDivida();
            itemValorDivida.setTributo(it.getTributoTaxaDividasDiversas().getTributo());
            BigDecimal quantidade = new BigDecimal(it.getQuantidadeTributoTaxas());
            itemValorDivida.setValor(quantidade.multiply(it.getValorReal()));
            itemValorDivida.setValorDivida(valorDivida);
            valorDivida.getItemValorDividas().add(itemValorDivida);
        }
    }

    @Override
    protected void defineVencimentosParcelas(Calculo calculo, OpcaoPagamento op, int i, ParcelaValorDivida parcelaValorDivida, ValorDivida valorDivida, Calendar vencimento, Integer numeroParcelas) {
        parcelaValorDivida.setVencimento(((CalculoTaxasDiversas) calculo).getVencimento());
    }

    @Override
    protected void lancaSituacao(ParcelaValorDivida parcelaValorDivida) {
        parcelaValorDivida.getSituacoes().add(new SituacaoParcelaValorDivida(SituacaoParcela.EM_ABERTO, parcelaValorDivida, parcelaValorDivida.getValor()));
    }

    @Override
    public void salvaValorDivida(ValorDivida valordivida) throws Exception {
        em.persist(valordivida);
        em.flush();
    }
}
