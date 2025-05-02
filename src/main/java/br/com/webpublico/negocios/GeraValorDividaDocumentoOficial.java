package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Calculo;
import br.com.webpublico.entidades.CalculoDoctoOficial;
import br.com.webpublico.entidades.ItemValorDivida;
import br.com.webpublico.entidades.OpcaoPagamento;
import br.com.webpublico.entidades.ParcelaValorDivida;
import br.com.webpublico.entidades.ValorDivida;
import java.util.Calendar;
import javax.ejb.Stateless;

@Stateless
public class GeraValorDividaDocumentoOficial extends ValorDividaFacade {

    public GeraValorDividaDocumentoOficial() {
        super(false);
    }

    @Override
    protected void lancaItem(ValorDivida valorDivida) {
        ItemValorDivida itemValorDivida = new ItemValorDivida();
        itemValorDivida.setTributo(((CalculoDoctoOficial) valorDivida.getCalculo()).getTributo());
        itemValorDivida.setValor(valorDivida.getValor());
        itemValorDivida.setValorDivida(valorDivida);
        valorDivida.getItemValorDividas().add(itemValorDivida);
    }

    @Override
    protected void defineVencimentosParcelas(Calculo calculo, OpcaoPagamento op, int i, ParcelaValorDivida parcelaValorDivida, ValorDivida valorDivida, Calendar vencimento, Integer numeroParcelas) {
        parcelaValorDivida.setVencimento(((CalculoDoctoOficial) calculo).getVencimento());
    }
}
