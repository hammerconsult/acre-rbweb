package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.util.DataUtil;

import javax.ejb.Stateless;
import java.util.Calendar;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Julio Cesar
 * Date: 07/11/13
 * Time: 15:11
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class GeraValorDividaMultaAcessoria extends ValorDividaFacade {

    public GeraValorDividaMultaAcessoria() {
        super(false);
    }

    @Override
    protected void defineVencimentosParcelas(Calculo calculo, OpcaoPagamento op, int i, ParcelaValorDivida parcelaValorDivida, ValorDivida valorDivida, Calendar vencimento, Integer numeroParcelas) {
        CalculoMultaAcessoria multaAcessoria = (CalculoMultaAcessoria) calculo;
        Calendar dataAuxiliar = Calendar.getInstance();
        dataAuxiliar.setTime(multaAcessoria.getDataCalculo());
        dataAuxiliar.set(Calendar.DAY_OF_MONTH, DataUtil.ultimoDiaDoMes(dataAuxiliar.get(Calendar.MONTH)+1));
        Calendar dataVencimento = DataUtil.ultimoDiaUtil(dataAuxiliar, feriadoFacade);
        parcelaValorDivida.setVencimento(dataVencimento.getTime());
    }

    @Override
    protected void lancaItem(ValorDivida valorDivida) {
        for (ItemCalculoMultaAcessoria item : ((CalculoMultaAcessoria) valorDivida.getCalculo()).getListaItemCalculoMultaAcessoria()) {
            ItemValorDivida itemValorDivida = new ItemValorDivida();
            itemValorDivida.setTributo(item.getTributo());
            itemValorDivida.setValor(item.getValor());
            itemValorDivida.setValorDivida(valorDivida);
            valorDivida.getItemValorDividas().add(itemValorDivida);
        }
    }
}
