package br.com.webpublico.seguranca;

import br.com.webpublico.entidades.ConfiguracaoMetrica;
import br.com.webpublico.entidades.MetricaSistema;

import javax.ejb.Singleton;
import java.util.List;

/**
 * @Author peixe on 26/04/2017  11:00.
 */
@Singleton
public class SingletonMetricasFacade {

    private ConfiguracaoMetrica configuracaoMetrica;
    private List<MetricaSistema> metricasIniciadas;
    private List<MetricaSistema> metricaSistemas;

    public ConfiguracaoMetrica getConfiguracaoMetrica() {
        return configuracaoMetrica;
    }

    public void setConfiguracaoMetrica(ConfiguracaoMetrica configuracaoMetrica) {
        this.configuracaoMetrica = configuracaoMetrica;
    }

    public List<MetricaSistema> getMetricasIniciadas() {
        return metricasIniciadas;
    }

    public void setMetricasIniciadas(List<MetricaSistema> metricasIniciadas) {
        this.metricasIniciadas = metricasIniciadas;
    }

    public List<MetricaSistema> getMetricaSistemas() {
        return metricaSistemas;
    }

    public void setMetricaSistemas(List<MetricaSistema> metricaSistemas) {
        this.metricaSistemas = metricaSistemas;
    }
}
