package br.com.webpublico.entidadesauxiliares.redis;

import br.com.webpublico.seguranca.NotificacaoService;

import java.util.List;

public class RedisIndice {
    private String nome;
    private Integer quantidade;
    private List<NotificacaoService.NotificacaoDTO> notificacoes;

    public RedisIndice() {

    }

    public RedisIndice(String key) {
        this.nome = key;
        this.quantidade = 0;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public List<NotificacaoService.NotificacaoDTO> getNotificacoes() {
        return notificacoes;
    }

    public void setNotificacoes(List<NotificacaoService.NotificacaoDTO> notificacoes) {
        this.notificacoes = notificacoes;
    }
}
