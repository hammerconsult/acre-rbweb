package br.com.webpublico.entidadesauxiliares.redis;

import java.util.List;

public class RedisSistema {

    private List<RedisInformacao> informacoes;
    private List<RedisIndice> indices;

    public RedisSistema() {
    }

    public List<RedisIndice> getIndices() {
        return indices;
    }

    public void setIndices(List<RedisIndice> indices) {
        this.indices = indices;
    }

    public List<RedisInformacao> getInformacoes() {
        return informacoes;
    }

    public void setInformacoes(List<RedisInformacao> informacoes) {
        this.informacoes = informacoes;
    }
}
