package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SituacaoParcela;

import javax.ejb.Stateless;
import java.util.Calendar;
import java.util.List;

@Stateless
public class GeraValorDividaAlvara extends ValorDividaFacade {

    public GeraValorDividaAlvara() {
        super(false);
    }

    @Override
    protected void defineVencimentosParcelas(Calculo calculo, OpcaoPagamento op, int i, ParcelaValorDivida parcelaValorDivida, ValorDivida valorDivida, Calendar vencimento, Integer numeroParcelas) {
        parcelaValorDivida.setVencimento(((CalculoAlvara) calculo).getVencimento());
    }

    @Override
    public void lancaItem(ValorDivida valorDivida) {
        CalculoAlvara calculo = em.find(CalculoAlvara.class, valorDivida.getCalculo().getId());
        for (ItemCalculoAlvara item : calculo.getItensAlvara()) {
            ItemValorDivida itemValorDivida = new ItemValorDivida();
            itemValorDivida.setTributo(item.getTributo());
            itemValorDivida.setValor(item.getValorEfetivo());
            itemValorDivida.setValorDivida(valorDivida);
            itemValorDivida.setIsento(calculo.getIsento());
            valorDivida.getItemValorDividas().add(itemValorDivida);
        }
    }

    @Override
    public void gerarValorDivida(Calculo calculo, boolean emNovaTransacao, List<OpcaoPagamentoDivida> opcoesPagamento) throws Exception {
        CalculoAlvara calculoAlvara = (CalculoAlvara) calculo;
        if (calculoAlvara.getIsento() == null || !calculoAlvara.getIsento()) {
            ValorDivida valorDivida = inicializaValorDivida(calculoAlvara.getValorEfetivo(),
                calculoAlvara, calculoAlvara.getDivida(), calculoAlvara.getProcessoCalculo().getExercicio());
            lancaItem(valorDivida);
            if (!valorDivida.getItemValorDividas().isEmpty()) {
                lancaOpcaoPagamento(valorDivida, opcoesPagamento);
            }
            if (emNovaTransacao) {
                ctx.getBusinessObject(this.getClass()).salvarValorDividaEmNovaTransacao(valorDivida);
            } else {
                salvaValorDivida(valorDivida);
            }
        }
    }

    @Override
    protected void lancaOpcaoPagamento(ValorDivida valorDivida, List<OpcaoPagamentoDivida> opcoesPagamento) throws Exception {
        for (OpcaoPagamentoDivida opcaoPagamentoDivida : opcoesPagamento) {
            geraParcelas(opcaoPagamentoDivida, valorDivida, 1);
        }
    }

    @Override
    protected void lancaSituacao(ParcelaValorDivida parcelaValorDivida) {
        if (parcelaValorDivida.getValorDivida().getCalculo().getIsento()) {
            parcelaValorDivida.getSituacoes().add(new SituacaoParcelaValorDivida(SituacaoParcela.ISENTO, parcelaValorDivida, parcelaValorDivida.getValor()));
        } else {
            parcelaValorDivida.getSituacoes().add(new SituacaoParcelaValorDivida(SituacaoParcela.EM_ABERTO, parcelaValorDivida, parcelaValorDivida.getValor()));
        }
    }
}
