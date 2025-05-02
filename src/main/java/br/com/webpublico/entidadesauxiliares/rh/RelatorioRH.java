package br.com.webpublico.entidadesauxiliares.rh;

import br.com.webpublico.entidades.EventoFP;
import br.com.webpublico.entidades.VinculoFP;
import br.com.webpublico.enums.TipoEventoFP;
import com.google.common.collect.Maps;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class RelatorioRH {
    Map<EventoFP, Map<TipoEventoFP, BigDecimal>> valores;
    Map<Class, List<VinculoFP>> vinculos;
    Map<Class, Map<VinculoFP, Map<EventoFP, Map<TipoEventoFP, BigDecimal>>>> vinculosValor;


    public RelatorioRH() {
        valores = Maps.newHashMap();
        vinculos = Maps.newHashMap();
        vinculosValor = Maps.newHashMap();
    }

    public Map<EventoFP, Map<TipoEventoFP, BigDecimal>> getValores() {
        return valores;
    }

    public void setValores(Map<EventoFP, Map<TipoEventoFP, BigDecimal>> valores) {
        this.valores = valores;
    }

    public Map<Class, List<VinculoFP>> getVinculos() {
        return vinculos;
    }

    public void setVinculos(Map<Class, List<VinculoFP>> vinculos) {
        this.vinculos = vinculos;
    }

    public Map<Class, Map<VinculoFP, Map<EventoFP, Map<TipoEventoFP, BigDecimal>>>> getVinculosValor() {
        return vinculosValor;
    }

    public void setVinculosValor(Map<Class, Map<VinculoFP, Map<EventoFP, Map<TipoEventoFP, BigDecimal>>>> vinculosValor) {
        this.vinculosValor = vinculosValor;
    }
}
