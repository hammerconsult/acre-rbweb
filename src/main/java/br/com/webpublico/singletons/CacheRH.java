package br.com.webpublico.singletons;

import br.com.webpublico.entidades.Cidade;
import br.com.webpublico.entidades.Feriado;
import br.com.webpublico.entidades.rh.configuracao.ConfiguracaoRH;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Singleton
public class CacheRH {

    private ConfiguracaoRH configuracaoRH;
    private List<Cidade> cidades;
    private Map<Long, List<Cidade>> cidadesPorEstado;
    private Map<Integer, List<Feriado>> feriadosPorAno;

    @PostConstruct
    public void init() {
        cidadesPorEstado = new HashMap<>();
        feriadosPorAno = new HashMap<>();
    }

    public ConfiguracaoRH getConfiguracaoRH() {
        return configuracaoRH;
    }

    public void setConfiguracaoRH(ConfiguracaoRH configuracaoRH) {
        this.configuracaoRH = configuracaoRH;
    }

    public List<Cidade> getCidades() {
        return cidades;
    }

    public void setCidades(List<Cidade> cidades) {
        this.cidades = cidades;
    }

    public Map<Long, List<Cidade>> getCidadesPorEstado() {
        return cidadesPorEstado;
    }

    public void setCidadesPorEstado(Map<Long, List<Cidade>> cidadesPorEstado) {
        this.cidadesPorEstado = cidadesPorEstado;
    }

    public Map<Integer, List<Feriado>> getFeriadosPorAno() {
        return feriadosPorAno;
    }

    public void setFeriadosPorAno(Map<Integer, List<Feriado>> feriadosPorAno) {
        this.feriadosPorAno = feriadosPorAno;
    }
}
