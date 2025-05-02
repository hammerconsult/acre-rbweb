package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.math.BigDecimal;
import java.util.Calendar;

@Stateless
public class GeraValorDividaMultaFiscal extends ValorDividaFacade {
    @EJB
    private ParametroFiscalizacaoFacade parametroFiscalizacaoFacade;
    @EJB
    private SistemaFacade sistemaFacade;

    public GeraValorDividaMultaFiscal() {
        super(false);
    }

    @Override
    protected void lancaItem(ValorDivida valorDivida) {
        for (ItemCalculoMultaFiscal item : ((CalculoMultaFiscalizacao) valorDivida.getCalculo()).getItemCalculoMultaFiscal()) {
            ItemValorDivida itemValorDivida = new ItemValorDivida();
            itemValorDivida.setTributo(item.getTributo());
            itemValorDivida.setValor(item.getValor());
            itemValorDivida.setValorDivida(valorDivida);
            valorDivida.getItemValorDividas().add(itemValorDivida);
        }
    }

    @Override
    protected void defineVencimentosParcelas(Calculo calculo, OpcaoPagamento op, int i, ParcelaValorDivida parcelaValorDivida, ValorDivida valorDivida, Calendar vencimento, Integer numeroParcelas) {
        parcelaValorDivida.setVencimento(((CalculoMultaFiscalizacao) calculo).getVencimento());
    }

    @Override
    protected void lancaDescontos(ItemParcelaValorDivida item, DescontoItemParcela.Origem origem) {
        ParametroFiscalizacao param = parametroFiscalizacaoFacade.recuperarParametroFiscalizacao(sistemaFacade.getExercicioCorrente());
        BigDecimal totalPorcentagem = (param.getDescontoDeMulta() == null ? BigDecimal.ZERO : param.getDescontoDeMulta()).add((param.getDescontoMultaTrintaDias() == null ? BigDecimal.ZERO : param.getDescontoMultaTrintaDias()));
        BigDecimal proporcional = BigDecimal.ZERO;
        if (item.getParcelaValorDivida().getValorDivida().getValor().compareTo(BigDecimal.ZERO) > 0) {
            proporcional = (item.getValor().multiply(totalPorcentagem)).divide(item.getParcelaValorDivida().getValorDivida().getValor());
        }
        adicionaDescontoAoItem(item, proporcional, origem);
    }
}
