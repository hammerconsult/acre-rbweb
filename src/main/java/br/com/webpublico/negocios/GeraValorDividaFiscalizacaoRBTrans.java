package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Calculo;
import br.com.webpublico.entidades.CalculoFiscalizacaoRBTrans;
import br.com.webpublico.entidades.ItemCalculoFiscalizacaoRBTrans;
import br.com.webpublico.entidades.ItemValorDivida;
import br.com.webpublico.entidades.OpcaoPagamento;
import br.com.webpublico.entidades.ParametrosFiscalizacaoRBTrans;
import br.com.webpublico.entidades.ParcelaValorDivida;
import br.com.webpublico.entidades.ProcessoCalculoFiscalizacaoRBTrans;
import br.com.webpublico.entidades.ValorDivida;
import java.util.Calendar;
import java.util.Date;
import javax.ejb.EJB;
import javax.ejb.Stateless;

@Stateless
public class GeraValorDividaFiscalizacaoRBTrans extends ValorDividaFacade {

    @EJB
    private ParametrosTransitoRBTransFacade parametrosTransitoRBTransFacade;

    public GeraValorDividaFiscalizacaoRBTrans() {
        super(false);
    }

    @Override
    protected void lancaItem(ValorDivida valorDivida) {
        for (ItemCalculoFiscalizacaoRBTrans item : ((CalculoFiscalizacaoRBTrans) valorDivida.getCalculo()).getItensCalculoFiscalizacaoRBTrans()) {
            ItemValorDivida itemValorDivida = new ItemValorDivida();
            itemValorDivida.setTributo(item.getTributo());
            itemValorDivida.setValor(item.getValor());
            itemValorDivida.setValorDivida(valorDivida);
            valorDivida.getItemValorDividas().add(itemValorDivida);
        }
    }

    @Override
    protected void defineVencimentosParcelas(Calculo calculo, OpcaoPagamento op, int i, ParcelaValorDivida parcelaValorDivida, ValorDivida valorDivida, Calendar vencimento, Integer numeroParcelas) {
        ParametrosFiscalizacaoRBTrans parametroVigente = parametrosTransitoRBTransFacade.getParametrosFiscalizacaoRBTransVigente();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_YEAR, parametroVigente.getVencimentoDoDAM());

        parcelaValorDivida.setVencimento(calendar.getTime());
    }
}
