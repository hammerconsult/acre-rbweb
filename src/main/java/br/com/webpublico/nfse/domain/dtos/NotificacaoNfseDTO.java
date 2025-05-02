package br.com.webpublico.nfse.domain.dtos;

import br.com.webpublico.nfse.domain.dtos.enums.GravidadeNfseDTO;
import br.com.webpublico.nfse.domain.dtos.enums.TipoNotificacaoNfseDTO;

import java.util.Date;

public class NotificacaoNfseDTO implements NfseDTO {
    private Long id;
    private String titulo;
    private String descricao;
    private String link;
    private GravidadeNfseDTO gravidade;
    private TipoNotificacaoNfseDTO tipoNotificacao;
    private Date criadoEm;

    public NotificacaoNfseDTO() {
        this.criadoEm = new Date();
    }

    public NotificacaoNfseDTO(String titulo, String descricao, String link,
                              GravidadeNfseDTO gravidade, TipoNotificacaoNfseDTO tipoNotificacao) {
        this();
        this.titulo = titulo;
        this.descricao = descricao;
        this.link = link;
        this.gravidade = gravidade;
        this.tipoNotificacao = tipoNotificacao;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public GravidadeNfseDTO getGravidade() {
        return gravidade;
    }

    public void setGravidade(GravidadeNfseDTO gravidade) {
        this.gravidade = gravidade;
    }

    public TipoNotificacaoNfseDTO getTipoNotificacao() {
        return tipoNotificacao;
    }

    public void setTipoNotificacao(TipoNotificacaoNfseDTO tipoNotificacao) {
        this.tipoNotificacao = tipoNotificacao;
    }

    public Date getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Date criadoEm) {
        this.criadoEm = criadoEm;
    }
}
