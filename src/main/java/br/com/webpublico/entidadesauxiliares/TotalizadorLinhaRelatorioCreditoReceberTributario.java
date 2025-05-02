package br.com.webpublico.entidadesauxiliares;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Created by rodolfo on 01/09/17.
 */
public class TotalizadorLinhaRelatorioCreditoReceberTributario {

    private static final Logger logger = LoggerFactory.getLogger(TotalizadorLinhaRelatorioCreditoReceberTributario.class);

    Map<String, BigDecimal> agrupadorGeral = Maps.newHashMap();
    Map<String, BigDecimal> agrupadorEntidade = Maps.newHashMap();
    Map<String, BigDecimal> agrupadorTipoCredito = Maps.newHashMap();
    Map<String, BigDecimal> agrupadorAcrescimo = Maps.newHashMap();
    Map<String, BigDecimal> agrupadorPrazo = Maps.newHashMap();

    public TotalizadorLinhaRelatorioCreditoReceberTributario() {

    }

    public void somar(LinhaRelatorioCreditoReceberTributario linha) {
        try {
            somar(agrupadorEntidade, linha.getEntidade(), linha.getValor());
            somar(agrupadorGeral, linha.getAgrupadorPrincial() + linha.getEntidade(), linha.getValor());
            somar(agrupadorTipoCredito, linha.getAgrupadorPrincial() + linha.getEntidade() + linha.getTiposCredito().name(), linha.getValor());
            somar(agrupadorAcrescimo, linha.getAgrupadorPrincial() + linha.getEntidade() + linha.getTiposCredito().name() + linha.getAcrescrimo(), linha.getValor());
            somar(agrupadorPrazo, linha.getAgrupadorPrincial() + linha.getEntidade() + linha.getTiposCredito().name() + linha.getAcrescrimo() + linha.getPrazo().toString(), linha.getValor());
        } catch (Exception ex) {
            logger.error("Erro ao somar o totalizador: {}", ex);
        }
    }

    public void somar(Map<String, BigDecimal> mapa, String key, BigDecimal valor) {
        if (!mapa.containsKey(key)) {
            mapa.put(key, BigDecimal.ZERO);
        }
        mapa.put(key, mapa.get(key).add(valor));
    }

    public BigDecimal getSomaGeral(String key) {
        return agrupadorGeral.get(key);
    }

    public BigDecimal getSomaEntidade(String key) {
        return agrupadorEntidade.get(key);
    }

    public BigDecimal getSomaTipoCredito(String key) {
        return agrupadorTipoCredito.get(key);
    }

    public BigDecimal getSomaAcrescimo(String key) {
        return agrupadorAcrescimo.get(key);
    }

    public BigDecimal getSomaPrazo(String key) {
        return agrupadorPrazo.get(key);
    }
}
