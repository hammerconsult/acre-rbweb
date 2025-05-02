package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;

@Stateless
public class GeraValorDividaSolicitacaoCadastroCredor extends ValorDividaFacade {
    protected static final Logger logger = LoggerFactory.getLogger(GeraValorDividaSolicitacaoCadastroCredor.class);
    @EJB
    private ConfiguracaoPortalContribuinteFacade configuracaoPortalContribuinteFacade;
    @EJB
    private MoedaFacade moedaFacade;

    public GeraValorDividaSolicitacaoCadastroCredor() {
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
        ConfiguracaoPortalContribuinte config = configuracaoPortalContribuinteFacade.buscarUtilmo();
        BigDecimal valorUFMVigente = moedaFacade.recuperaValorVigenteUFM();
        if (valorUFMVigente.equals(BigDecimal.ZERO)) {
            logger.error("GeraValorDividaSolicitacaoCadastroCredor, Valor ufm vigente está zerado: " + valorUFMVigente);
        }
        ItemValorDivida itemValorDivida = new ItemValorDivida();
        itemValorDivida.setTributo(config.getTributoCredor());
        itemValorDivida.setValor(valorUFMVigente.multiply(config.getPorcentagemUfmCredor()).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP));
        itemValorDivida.setValorDivida(valorDivida);
        valorDivida.getItemValorDividas().add(itemValorDivida);
    }
}
