package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.enums.TipoCalculoISS;
import br.com.webpublico.enums.TipoPeriodoValorEstimado;
import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Maps;
import org.hibernate.proxy.HibernateProxy;

import javax.ejb.Stateless;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

@Stateless
public class GeraValorDividaISS extends ValorDividaFacade {

    public GeraValorDividaISS() {
        super(false);
    }

    @Override
    protected Date primeiroVencimento(Calculo calculo, Parcela p) {
        Calendar retorno = Calendar.getInstance();
        retorno.setTime(new Date());
        return retorno.getTime();
    }

    public void addMes(Integer mes, Integer ano) {
        if (mes == 12) {
            mes = 1;
            ano = ano + 1;
        } else {
            mes = mes + 1;
        }
    }

    public void subtractMes(Integer mes, Integer ano) {
        if (mes == 1) {
            mes = 12;
            ano = ano - 1;
        } else {
            mes = mes - 1;
        }
    }

    @Override
    protected void defineVencimentosParcelas(Calculo calculo, OpcaoPagamento op, int i, ParcelaValorDivida parcelaValorDivida, ValorDivida valorDivida, Calendar vencimento, Integer numeroParcelas) {
        ParcelaFixaPeriodica p = (ParcelaFixaPeriodica) op.getParcelas().get(0);
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
        Integer mesRefCalculoISS = calculoISS.getProcessoCalculoISS().getMesReferencia();
        Integer anoRefCalculoISS = calculoISS.getProcessoCalculoISS().getExercicio().getAno();
        Calendar abertura = Calendar.getInstance();
        abertura.setTime(calculoISS.getCadastroEconomico().getAbertura());
        Integer mesAbertura = abertura.get(Calendar.MONTH);
        Integer anoAbertura = abertura.get(Calendar.YEAR);
        Calendar dataAuxiliar = Calendar.getInstance();
        Integer mesLancamentoAux = mesRefCalculoISS;
        Integer anoLancamentoAux = anoRefCalculoISS;

        CadastroEconomico cadastroEconomico = calculoDividaDiversaFacade.getCadastroEconomicoFacade().recuperar(calculoISS.getCadastroEconomico().getId());
        TipoPeriodoValorEstimado periodoValorEstimado = cadastroEconomico.getEnquadramentoVigente().getTipoPeriodoValorEstimado();

        if (TipoCalculoISS.MENSAL.equals(calculoISS.getTipoCalculoISS())) {
            if (mesDataLancamento.compareTo(mesRefCalculoISS) == 0 && anoDataLancamento.compareTo(anoRefCalculoISS) == 0) {
                diaVencimento = p.getDiaVencimento();
                mesVencimento = mesRefCalculoISS;
                anoVencimento = anoRefCalculoISS;
                addMes(mesVencimento, anoVencimento);
            } else {
                mesLancamentoAux = mesDataLancamento;
                anoLancamentoAux = anoDataLancamento;
                subtractMes(mesLancamentoAux, anoLancamentoAux);
                dataAuxiliar.set(Calendar.DAY_OF_MONTH, p.getDiaVencimento());
                dataAuxiliar.set(Calendar.MONTH, mesDataLancamento);
                dataAuxiliar.set(Calendar.YEAR, anoDataLancamento);
                if (mesLancamentoAux.compareTo(mesRefCalculoISS) == 0 && anoLancamentoAux.compareTo(anoRefCalculoISS) == 0
                        && dataLancamento.getTime().compareTo(dataAuxiliar.getTime()) <= 0) {
                    diaVencimento = p.getDiaVencimento();
                    mesVencimento = mesDataLancamento;
                    anoVencimento = anoDataLancamento;
                } else {
                    diaVencimento = p.getDiaVencimento();
                    mesVencimento = mesRefCalculoISS;
                    anoVencimento = anoRefCalculoISS;
                    addMes(mesVencimento, anoVencimento);
                }
            }
        }

        if (TipoCalculoISS.ESTIMADO.equals(calculoISS.getTipoCalculoISS())) {
            if (periodoValorEstimado.equals(TipoPeriodoValorEstimado.ANUAL)) {
                if (anoDataLancamento.compareTo(anoAbertura) == 0) {
                    dataAuxiliar.set(Calendar.MONTH, Calendar.JANUARY);
                    dataAuxiliar.set(Calendar.YEAR, anoDataLancamento);
                    dataAuxiliar.set(Calendar.DAY_OF_MONTH, dataAuxiliar.getActualMaximum(Calendar.DAY_OF_MONTH));
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
                        dataAuxiliar.set(Calendar.MONTH, Calendar.JANUARY);
                        dataAuxiliar.set(Calendar.YEAR, anoRefCalculoISS);
                        dataAuxiliar.set(Calendar.DAY_OF_MONTH, dataAuxiliar.getActualMaximum(Calendar.DAY_OF_MONTH));
                        dataAuxiliar = DataUtil.ultimoDiaUtil(dataAuxiliar, feriadoFacade);

                        diaVencimento = dataAuxiliar.get(Calendar.DAY_OF_MONTH);
                        mesVencimento = dataAuxiliar.get(Calendar.MONTH);
                        anoVencimento = dataAuxiliar.get(Calendar.YEAR);
                    }
                }
            } else {
                if (mesDataLancamento.compareTo(mesRefCalculoISS) == 0 && anoDataLancamento.compareTo(anoRefCalculoISS) == 0) {
                    diaVencimento = p.getDiaVencimento();
                    mesVencimento = mesRefCalculoISS;
                    anoVencimento = anoRefCalculoISS;
                    addMes(mesVencimento, anoVencimento);
                } else {
                    mesLancamentoAux = mesDataLancamento;
                    anoLancamentoAux = anoDataLancamento;
                    subtractMes(mesLancamentoAux, anoLancamentoAux);
                    dataAuxiliar.set(Calendar.DAY_OF_MONTH, p.getDiaVencimento());
                    dataAuxiliar.set(Calendar.MONTH, mesDataLancamento);
                    dataAuxiliar.set(Calendar.YEAR, anoDataLancamento);
                    if (mesLancamentoAux.compareTo(mesRefCalculoISS) == 0 && anoLancamentoAux.compareTo(anoRefCalculoISS) == 0
                            && dataLancamento.getTime().compareTo(dataAuxiliar.getTime()) <= 0) {
                        diaVencimento = p.getDiaVencimento();
                        mesVencimento = mesDataLancamento;
                        anoVencimento = anoDataLancamento;
                    } else {
                        diaVencimento = p.getDiaVencimento();
                        mesVencimento = mesRefCalculoISS;
                        anoVencimento = anoRefCalculoISS;
                        addMes(mesVencimento, anoVencimento);
                    }
                }
            }
        }

        if (TipoCalculoISS.FIXO.equals(calculoISS.getTipoCalculoISS())) {
            ConfiguracaoTributario configuracaoTributario = configuracaoTributarioFacade.retornaUltimo();
            Calendar mesAtual = Calendar.getInstance();
            mesAtual.setTime(new Date());
            if (anoDataLancamento.compareTo(anoAbertura) == 0) {
                if(configuracaoTributario.getMesVencimentoIssFixo().getNumeroMesIniciandoEmZero() < mesAtual.get(Calendar.MONTH)){
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
                    if(configuracaoTributario.getMesVencimentoIssFixo().getNumeroMesIniciandoEmZero() < mesAtual.get(Calendar.MONTH)){
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

        parcelaValorDivida.setVencimento(DataUtil.ajustarDataUtil(dataVencimento.getTime(), feriadoFacade));

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

    protected void geraDAM(ValorDivida valordivida) throws Exception {
        Calculo calculo = valordivida.getCalculo();
        // Isso foi comentado pq não se faz necessário, foi vossa majestade que colocou, não sei pq
        //Hibernate.initialize(calculo);
        if (calculo instanceof HibernateProxy) {
            calculo = (Calculo) ((HibernateProxy) calculo).getHibernateLazyInitializer().getImplementation();
        }
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

        Integer mesRefCalculoISS = calculoISS.getProcessoCalculoISS().getMesReferencia();
        Integer anoRefCalculoISS = calculoISS.getProcessoCalculoISS().getExercicio().getAno();

        Calendar abertura = Calendar.getInstance();
        abertura.setTime(calculoISS.getCadastroEconomico().getAbertura());

        Calendar dataAuxiliar = Calendar.getInstance();

        TipoPeriodoValorEstimado periodoValorEstimado = calculoISS.getCadastroEconomico().getEnquadramentoVigente().getTipoPeriodoValorEstimado();

        if (TipoCalculoISS.MENSAL.equals(calculoISS.getTipoCalculoISS())) {
            if (mesDataLancamento == mesRefCalculoISS && anoDataLancamento == anoRefCalculoISS) {
                dataAuxiliar.set(Calendar.MONTH, mesRefCalculoISS);
                dataAuxiliar.set(Calendar.YEAR, anoRefCalculoISS);
                dataAuxiliar.set(Calendar.DAY_OF_MONTH, dataAuxiliar.getActualMaximum(Calendar.DAY_OF_MONTH));
                dataAuxiliar = DataUtil.ultimoDiaUtil(dataAuxiliar, feriadoFacade);

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
        }

        if (TipoCalculoISS.ESTIMADO.equals(calculoISS.getTipoCalculoISS())) {
            if (TipoPeriodoValorEstimado.ANUAL.equals(periodoValorEstimado)) {
                if (anoDataLancamento == abertura.get(Calendar.YEAR)) {
                    return;
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
                if (mesDataLancamento == mesRefCalculoISS && anoDataLancamento == anoRefCalculoISS) {
                    addMes(mesRefCalculoISS, anoRefCalculoISS);
                    dataAuxiliar.set(Calendar.MONTH, mesRefCalculoISS);
                    dataAuxiliar.set(Calendar.YEAR, anoRefCalculoISS);
                    dataAuxiliar.set(Calendar.DAY_OF_MONTH, dataAuxiliar.getActualMaximum(Calendar.DAY_OF_MONTH));
                    dataAuxiliar = DataUtil.ultimoDiaUtil(dataAuxiliar, feriadoFacade);

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
            }
        }

        if (TipoCalculoISS.FIXO.equals(calculoISS.getTipoCalculoISS())) {
            if (anoDataLancamento == abertura.get(Calendar.YEAR)) {
                return;
            } else {
                dataAuxiliar.set(Calendar.MONTH, mesDataLancamento);
                dataAuxiliar.set(Calendar.YEAR, anoDataLancamento);
                dataAuxiliar.set(Calendar.DAY_OF_MONTH, dataAuxiliar.getActualMaximum(Calendar.DAY_OF_MONTH));
                dataAuxiliar = DataUtil.ultimoDiaUtil(dataAuxiliar, feriadoFacade);

                diaVencimento = dataAuxiliar.get(Calendar.DAY_OF_MONTH);
                mesVencimento = dataAuxiliar.get(Calendar.MONTH);
                anoVencimento = dataAuxiliar.get(Calendar.YEAR);
            }
        }
        dataVencimento.set(Calendar.DAY_OF_MONTH, diaVencimento);
        dataVencimento.set(Calendar.MONTH, mesVencimento);
        dataVencimento.set(Calendar.YEAR, anoVencimento);
        damFacade.geraDAM(valordivida, valordivida.getExercicio(), sistemaFacade.getUsuarioCorrente());
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
