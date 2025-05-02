package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

@Stateless
public class GeraValorDividaProcFiscal extends ValorDividaFacade {

    @EJB
    private SistemaFacade sistemaFacade;

    public GeraValorDividaProcFiscal() {
        super(false);
    }


    @Override
    protected ValorDivida inicializaValorDivida(BigDecimal valor, Calculo calculo, Divida divida, Exercicio exercicio) {
        ValorDivida valorDivida = new ValorDivida();
        valorDivida.setOcorrenciaValorDivida(new ArrayList<OcorrenciaValorDivida>());
        valorDivida.setCalculo(calculo);
        valorDivida.setItemValorDividas(new ArrayList<ItemValorDivida>());
        valorDivida.setParcelaValorDividas(new ArrayList<ParcelaValorDivida>());
        valorDivida.setDivida(divida);
        valorDivida.setEmissao(sistemaFacade.getDataOperacao());
        valorDivida.setExercicio(exercicio);
        valorDivida.setValor(valor);
        return valorDivida;
    }

    @Override
    protected Date primeiroVencimento(Calculo calculo, Parcela p) {
        return sistemaFacade.getDataOperacao();
    }

    @Override
    protected void lancaItem(ValorDivida valorDivida) {
        CalculoProcFiscalizacao calculo = (CalculoProcFiscalizacao) valorDivida.getCalculo();
        for (ItemCalcProcFiscalizacao itemCalculoProcFiscalizacao : calculo.getItemCalcProcFiscalizacoes()) {
            ItemValorDivida itemValorDivida = new ItemValorDivida();
            itemValorDivida.setIsento(Boolean.FALSE);
            itemValorDivida.setTributo(itemCalculoProcFiscalizacao.getItemProcessoFiscalizacao().getPenalidadeFiscalizacaoSecretaria().getTributo());
            itemValorDivida.setValor(itemCalculoProcFiscalizacao.getValorEfetivo());
            itemValorDivida.setValorDivida(valorDivida);
            valorDivida.getItemValorDividas().add(itemValorDivida);
        }
    }

    @Override
    protected void defineVencimentosParcelas(Calculo calculo, OpcaoPagamento op, int i, ParcelaValorDivida parcelaValorDivida, ValorDivida valorDivida, Calendar vencimento, Integer numeroParcelas) {
        vencimento.add(Calendar.DAY_OF_MONTH, ((CalculoProcFiscalizacao) calculo).getProcessoFiscalizacao().getSecretariaFiscalizacao().getVencimentoDam());
        parcelaValorDivida.setVencimento(vencimento.getTime());
    }

}

