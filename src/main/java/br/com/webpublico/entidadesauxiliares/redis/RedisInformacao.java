package br.com.webpublico.entidadesauxiliares.redis;

public class RedisInformacao {
    private String nome;

    public RedisInformacao() {

    }

    public RedisInformacao(String key) {
        this.nome = key;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
