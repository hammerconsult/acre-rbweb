package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.util.DataUtil;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Stateless
public class GeraValorDividaLancamentoOutorga extends ValorDividaFacade {

    @EJB
    private ParametroOutorgaFacade parametroOutorgaFacade;

    public GeraValorDividaLancamentoOutorga() {
        super(false);
    }

    @Override
    protected void lancaItem(ValorDivida valorDivida) {
        ItemValorDivida itemValorDivida = new ItemValorDivida();
        itemValorDivida.setTributo(((CalculoLancamentoOutorga) valorDivida.getCalculo()).getTributo());
        itemValorDivida.setValor(valorDivida.getValor());
        itemValorDivida.setValorDivida(valorDivida);
        valorDivida.getItemValorDividas().add(itemValorDivida);
    }


    @Override
    protected void lancaOpcaoPagamento(ValorDivida valorDivida, List<OpcaoPagamentoDivida> opcoesPagamento) throws Exception {
        int numeroParcela;
        ParametrosOutorgaRBTrans parametro = parametroOutorgaFacade.getParametroOutorgaRBTransVigente();
        if (parametro.getDiaVencimentoSegundaParcela() == null) {
            numeroParcela = 1;
        } else {
            numeroParcela = 2;
        }
        for (OpcaoPagamentoDivida opcaoPagamentoDivida : opcoesPagamento) {
            geraParcelas(opcaoPagamentoDivida, valorDivida, numeroParcela);
        }
    }

    @Override
    protected Date primeiroVencimento(Calculo calculo, Parcela p) {
        Calendar vencimento = Calendar.getInstance();
        if (calculo instanceof CalculoLancamentoOutorga) {
            vencimento.set(Calendar.DAY_OF_MONTH, ((ParcelaFixaPeriodica) p).getDiaVencimento());
            vencimento.set(Calendar.MONTH, vencimento.get(Calendar.MONTH) + 1);
        }
        return vencimento.getTime();
    }

    @Override
    protected void defineVencimentosParcelas(Calculo calculo, OpcaoPagamento op, int i, ParcelaValorDivida parcelaValorDivida, ValorDivida valorDivida, Calendar vencimento, Integer numeroParcelas) {
        Calendar vencimentoParcela = Calendar.getInstance();

        Integer ano = ((CalculoLancamentoOutorga) calculo).getProcCalcLancamentoOutorga().getLancamentoOutorga().getAnoVencimento();
        Mes mes = ((CalculoLancamentoOutorga) calculo).getProcCalcLancamentoOutorga().getLancamentoOutorga().getMesVencimento();
        Date vencimentoDefinido = ((CalculoLancamentoOutorga) calculo).getProcCalcLancamentoOutorga().getLancamentoOutorga().getDiaVencimentoParaAparecerNoCalender();

        if (vencimentoDefinido != null && i == 0) {
            parcelaValorDivida.setVencimento(vencimentoDefinido);
        } else {
            Integer dia;
            if (i == 0) {
                dia = parametroOutorgaFacade.getParametroOutorgaRBTransVigente().getDiaVencimentoPrimeiraParcela();
            } else {
                dia = parametroOutorgaFacade.getParametroOutorgaRBTransVigente().getDiaVencimentoSegundaParcela();
            }
            vencimentoParcela.set(ano, mes.getNumeroMesIniciandoEmZero(), dia);
            parcelaValorDivida.setVencimento(vencimentoParcela.getTime());
        }
    }

    private boolean diaDiferenteDoParametro(ParcelaValorDivida parcelaValorDivida, Calendar vencimentoParcela, Integer ano, Mes mes, Integer dia, int i) {
        if (dia != null) {
            if (i != 0) {
                dia = parametroOutorgaFacade.getParametroOutorgaRBTransVigente().getDiaVencimentoSegundaParcela();
            }
            vencimentoParcela.set(ano, mes.getNumeroMes() - 1, dia);
            parcelaValorDivida.setVencimento(vencimentoParcela.getTime());
            return true;
        }
        return false;
    }
}
