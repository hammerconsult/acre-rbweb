package br.com.webpublico.negocios.tributario;

import br.com.webpublico.entidades.*;
import br.com.webpublico.negocios.ValorDividaFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import java.util.Calendar;
import java.util.Date;

@Stateless
public class GeraValorDividaContrato extends ValorDividaFacade {

    protected static final Logger logger = LoggerFactory.getLogger(GeraValorDividaContrato.class);

    public GeraValorDividaContrato() {
        super(false);
    }

    @Override
    protected void defineVencimentosParcelas(Calculo calculo, OpcaoPagamento op, int i, ParcelaValorDivida parcelaValorDivida, ValorDivida valorDivida, Calendar vencimento, Integer numeroParcelas) {
        vencimento.add(Calendar.DAY_OF_MONTH, 30);
        Date vencimentoDiaUtil = ajustaVencimento(vencimento.getTime(), Calendar.DAY_OF_MONTH, 1);
        parcelaValorDivida.setVencimento(vencimentoDiaUtil);
    }

    @Override
    protected void lancaItem(ValorDivida valorDivida) {
        ConfiguracaoTributario config = configuracaoTributarioFacade.retornaUltimo();
        ItemValorDivida itemValorDivida = new ItemValorDivida();
        itemValorDivida.setTributo(config.getTributoContratoConcessao());
        itemValorDivida.setValor(valorDivida.getCalculo().getValorEfetivo());
        itemValorDivida.setValorDivida(valorDivida);
        valorDivida.getItemValorDividas().add(itemValorDivida);
    }
}
