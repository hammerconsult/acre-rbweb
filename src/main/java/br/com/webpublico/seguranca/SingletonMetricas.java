package br.com.webpublico.seguranca;

import br.com.webpublico.entidades.ConfiguracaoMetrica;
import br.com.webpublico.entidades.ContadorAcessosPortal;
import br.com.webpublico.entidades.MetricaMemoria;
import br.com.webpublico.entidades.MetricaSistema;
import br.com.webpublico.negocios.ContadorAcessosPortalFacade;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by Fabio on 10/02/2017.
 */
@Service
@Scope(value = "singleton", proxyMode = ScopedProxyMode.TARGET_CLASS)
@Transactional
public class SingletonMetricas implements Serializable {

    protected static final Logger logger = LoggerFactory.getLogger(SingletonMetricas.class);

    private SingletonMetricasFacade singletonMetricasFacade;
    private ContadorAcessosPortalFacade contadorAcessosPortalFacade;
    private Long quantidadeAcessosPortal;

    @PersistenceContext
    protected transient EntityManager em;

    @PostConstruct
    public void init() {
        if (singletonMetricasFacade == null) {
            try {
                singletonMetricasFacade = (SingletonMetricasFacade) new InitialContext().lookup("java:module/SingletonMetricasFacade");
                contadorAcessosPortalFacade = (ContadorAcessosPortalFacade) new InitialContext().lookup("java:module/ContadorAcessosPortalFacade");
                quantidadeAcessosPortal = contadorAcessosPortalFacade.countTotalAcessos(ContadorAcessosPortal.TipoAcesso.LOGIN);
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
    }

    public Long getQuantidadeAcessosPortal() {
        return quantidadeAcessosPortal;
    }

    public void gravarMetricas() {
        if (getConfiguracaoMetrica().getLigado()) {
            List<MetricaSistema> salvar = Lists.newArrayList(getMetricaSistemas());
            em.persist(getMetricaMemoria());
            for (MetricaSistema metrica : salvar) {
                try {
                    em.persist(new MetricaSistema(metrica.getLogin(), metrica.getUrl(), metrica.getInicio(), metrica.getFim(), metrica.getData(), metrica.getTipo()));
                    getMetricaSistemas().remove(metrica);
                } catch (Exception e) {
                    logger.trace("Não foi possível salvar a metrica {} ", metrica);
                }
            }
        }
    }

    public ConfiguracaoMetrica getConfiguracaoMetrica() {
        if (singletonMetricasFacade.getConfiguracaoMetrica() == null) {
            singletonMetricasFacade.setConfiguracaoMetrica(buscarUltimaConfiguracao());
        }
        return singletonMetricasFacade.getConfiguracaoMetrica();
    }

    public void limparComfiguracao() {
        singletonMetricasFacade.setConfiguracaoMetrica(null);
    }

    public List<MetricaSistema> getMetricaSistemas() {
        if (singletonMetricasFacade.getMetricaSistemas() == null) {
            singletonMetricasFacade.setMetricaSistemas(Lists.<MetricaSistema>newArrayList());
        }
        return singletonMetricasFacade.getMetricaSistemas();
    }

    public void iniciarMetrica(String login, String descricao, Long start, MetricaSistema.Tipo tipo) {
        try {
            if (getConfiguracaoMetrica().getLigado()) {
                MetricaSistema metricaSistema = MetricaSistema.iniciar(login, descricao, start, new Date(), tipo);
                getMetricasIniciadas().add(metricaSistema);
            }
        } catch (Exception ex) {
            logger.error("Não foi possível iniciar a métrica {}", ex);
        }
    }

    public void finalizarMetrica(String login, String descricao, Long end, MetricaSistema.Tipo tipo) {
        try {
            if (getConfiguracaoMetrica().getLigado()) {
                MetricaSistema metricaSistema = MetricaSistema.finalizar(login, descricao, end, new Date(), tipo);
                for (MetricaSistema iniciada : getMetricasIniciadas()) {
                    if (iniciada.getLogin().equals(login) &&
                        iniciada.getUrl().equals(descricao) &&
                        iniciada.getTipo().equals(tipo) &&
                        iniciada.getFim() == null) {
                        metricaSistema.setInicio(iniciada.getInicio());
                        metricaSistema.setFim(end);
                        getMetricaSistemas().add(metricaSistema);
                        metricaSistema = iniciada;
                        break;
                    }
                }
                getMetricasIniciadas().remove(metricaSistema);
            }
        } catch (Exception ex) {
            logger.error("Não foi possível finalizar a métrica {}", ex);
        }
    }

    public MetricaMemoria getMetricaMemoria() {
        int mb = 1024 * 1024;
        Runtime runtime = Runtime.getRuntime();
        MetricaMemoria metricaMemoria = new MetricaMemoria();
        metricaMemoria.setData(new Date());
        metricaMemoria.setUsedMemory((runtime.totalMemory() - runtime.freeMemory()) / mb);
        metricaMemoria.setFreeMemory(runtime.freeMemory() / mb);
        metricaMemoria.setTotalMemory(runtime.totalMemory() / mb);
        metricaMemoria.setMaxMemory(runtime.maxMemory() / mb);
        return metricaMemoria;
    }


    private ConfiguracaoMetrica buscarUltimaConfiguracao() {
        Query q = em.createQuery("from ConfiguracaoMetrica");
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            return (ConfiguracaoMetrica) q.getResultList().get(0);
        }
        return null;
    }

    private List<MetricaSistema> getMetricasIniciadas() {
        if (singletonMetricasFacade.getMetricasIniciadas() == null) {
            singletonMetricasFacade.setMetricasIniciadas(Lists.<MetricaSistema>newArrayList());
        }
        return singletonMetricasFacade.getMetricasIniciadas();
    }


    public void contarAcessoPortal(ContadorAcessosPortal.TipoAcesso tipoAcesso, String login) {
        ContadorAcessosPortal contadorAcessosPortal = new ContadorAcessosPortal(new Date(), login, tipoAcesso);
        contadorAcessosPortalFacade.salvar(contadorAcessosPortal);
        if (ContadorAcessosPortal.TipoAcesso.LOGIN.equals(tipoAcesso)) {
            quantidadeAcessosPortal++;
        }
    }


}
