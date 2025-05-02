package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Maps;

import javax.ejb.Stateless;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

@Stateless
public class GeraValorDividaISSRbtrans extends ValorDividaFacade {

    public GeraValorDividaISSRbtrans() {
        super(false);
    }

    @Override
    protected Date primeiroVencimento(Calculo calculo, Parcela p) {
        Calendar retorno = Calendar.getInstance();
        retorno.setTime(new Date());
        return retorno.getTime();
    }

    @Override
    protected void defineVencimentosParcelas(Calculo calculo, OpcaoPagamento op, int i, ParcelaValorDivida parcelaValorDivida, ValorDivida valorDivida, Calendar vencimento, Integer numeroParcelas) {
        CalculoISS calculoISS = (CalculoISS) calculo;
        Calendar dataVencimento = Calendar.getInstance();
        dataVencimento.setTime(new Date());
        Integer diaVencimento = 1;
        Integer mesVencimento = calculoISS.getProcessoCalculoISS().getMesReferencia();
        Integer anoVencimento = calculoISS.getProcessoCalculoISS().getExercicio().getAno();

        Calendar dataLancamento = Calendar.getInstance();
        dataLancamento.setTime(calculoISS.getDataCalculo());
        Integer mesDataLancamento = dataLancamento.get(Calendar.MONTH);
        Integer anoDataLancamento = dataLancamento.get(Calendar.YEAR);
        Integer anoRefCalculoISS = calculoISS.getProcessoCalculoISS().getExercicio().getAno();
        Calendar abertura = Calendar.getInstance();
        abertura.setTime(calculoISS.getCadastroEconomico().getAbertura());
        Integer mesAbertura = abertura.get(Calendar.MONTH);
        Integer anoAbertura = abertura.get(Calendar.YEAR);
        Calendar dataAuxiliar = Calendar.getInstance();

        ConfiguracaoTributario configuracaoTributario = configuracaoTributarioFacade.retornaUltimo();
        Calendar mesAtual = Calendar.getInstance();
        mesAtual.setTime(new Date());
        if (valorDivida.getExercicio().getAno() == 2020) {
            diaVencimento = 31;
            mesVencimento = 12;
            anoVencimento = 2020;
        } else {
            if (anoDataLancamento.compareTo(anoAbertura) == 0) {
                if (configuracaoTributario.getMesVencimentoIssFixo().getNumeroMesIniciandoEmZero() < mesAtual.get(Calendar.MONTH)) {
                    dataAuxiliar.set(Calendar.MONTH, mesAtual.get(Calendar.MONTH));
                } else {
                    dataAuxiliar.set(Calendar.MONTH, configuracaoTributario.getMesVencimentoIssFixo().getNumeroMesIniciandoEmZero());
                }
                dataAuxiliar.set(Calendar.YEAR, anoDataLancamento);
                dataAuxiliar.set(Calendar.DAY_OF_MONTH, dataAuxiliar.getActualMaximum(Calendar.DAY_OF_MONTH));
                dataAuxiliar = DataUtil.ultimoDiaUtil(dataAuxiliar, feriadoFacade);
                if (dataLancamento.compareTo(dataAuxiliar) <= 0) {
                    diaVencimento = dataAuxiliar.get(Calendar.DAY_OF_MONTH);
                    mesVencimento = dataAuxiliar.get(Calendar.MONTH);
                    anoVencimento = dataAuxiliar.get(Calendar.YEAR);
                } else {
                    dataAuxiliar.set(Calendar.MONTH, mesDataLancamento);
                    dataAuxiliar.set(Calendar.YEAR, anoDataLancamento);
                    dataAuxiliar.set(Calendar.DAY_OF_MONTH, dataAuxiliar.getActualMaximum(Calendar.DAY_OF_MONTH));
                    dataAuxiliar = DataUtil.ultimoDiaUtil(dataAuxiliar, feriadoFacade);

                    diaVencimento = dataAuxiliar.get(Calendar.DAY_OF_MONTH);
                    mesVencimento = dataAuxiliar.get(Calendar.MONTH);
                    anoVencimento = dataAuxiliar.get(Calendar.YEAR);
                }
            } else {
                if (anoRefCalculoISS.compareTo(anoAbertura) == 0) {
                    dataAuxiliar.set(Calendar.MONTH, mesAbertura);
                    dataAuxiliar.set(Calendar.YEAR, anoAbertura);
                    dataAuxiliar.set(Calendar.DAY_OF_MONTH, dataAuxiliar.getActualMaximum(Calendar.DAY_OF_MONTH));
                    dataAuxiliar = DataUtil.ultimoDiaUtil(dataAuxiliar, feriadoFacade);

                    diaVencimento = dataAuxiliar.get(Calendar.DAY_OF_MONTH);
                    mesVencimento = dataAuxiliar.get(Calendar.MONTH);
                    anoVencimento = dataAuxiliar.get(Calendar.YEAR);
                } else {
                    if (configuracaoTributario.getMesVencimentoIssFixo().getNumeroMesIniciandoEmZero() < mesAtual.get(Calendar.MONTH)) {
                        dataAuxiliar.set(Calendar.MONTH, mesAtual.get(Calendar.MONTH));
                    } else {
                        dataAuxiliar.set(Calendar.MONTH, configuracaoTributario.getMesVencimentoIssFixo().getNumeroMesIniciandoEmZero());
                    }
                    dataAuxiliar.set(Calendar.YEAR, anoRefCalculoISS);
                    dataAuxiliar.set(Calendar.DAY_OF_MONTH, dataAuxiliar.getActualMaximum(Calendar.DAY_OF_MONTH));
                    dataAuxiliar = DataUtil.ultimoDiaUtil(dataAuxiliar, feriadoFacade);

                    diaVencimento = dataAuxiliar.get(Calendar.DAY_OF_MONTH);
                    mesVencimento = dataAuxiliar.get(Calendar.MONTH);
                    anoVencimento = dataAuxiliar.get(Calendar.YEAR);
                }
            }
        }


        dataVencimento.set(Calendar.DAY_OF_MONTH, diaVencimento);
        dataVencimento.set(Calendar.MONTH, mesVencimento);
        dataVencimento.set(Calendar.YEAR, anoVencimento);

        if (valorDivida.getExercicio().getAno() == 2020) {
            parcelaValorDivida.setVencimento(DataUtil.getDia(diaVencimento, mesVencimento, anoVencimento));
        } else {
            parcelaValorDivida.setVencimento(DataUtil.ajustarDataUtil(dataVencimento.getTime(), feriadoFacade));
        }
    }

    @Override
    protected void lancaItem(ValorDivida valorDivida) {
        Map<Tributo, BigDecimal> mapa = Maps.newHashMap();
        for (ItemCalculoIss item : ((CalculoISS) valorDivida.getCalculo()).getItemCalculoIsss()) {
            if (!mapa.containsKey(item.getTributo())) {
                mapa.put(item.getTributo(), BigDecimal.ZERO);
            }
            mapa.put(item.getTributo(), mapa.get(item.getTributo()).add(item.getValorCalculado()));
        }
        for (Tributo tributo : mapa.keySet()) {
            ItemValorDivida itemValorDivida = new ItemValorDivida();
            itemValorDivida.setTributo(tributo);
            itemValorDivida.setValor(mapa.get(tributo));
            itemValorDivida.setValorDivida(valorDivida);
            valorDivida.getItemValorDividas().add(itemValorDivida);
        }
    }

    @Override
    protected void lancaSituacao(ParcelaValorDivida parcelaValorDivida) {
        if (parcelaValorDivida.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            parcelaValorDivida.getSituacoes().add(new SituacaoParcelaValorDivida(SituacaoParcela.SEM_MOVIMENTO, parcelaValorDivida, parcelaValorDivida.getValor()));
        } else {
            parcelaValorDivida.getSituacoes().add(new SituacaoParcelaValorDivida(SituacaoParcela.EM_ABERTO, parcelaValorDivida, parcelaValorDivida.getValor()));
        }
    }
}
