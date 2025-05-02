package br.com.webpublico.entidades;

import javax.persistence.*;

@Entity
public class ConfiguracaoPixDam extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private ConfiguracaoPix configuracaoPix;
    @ManyToOne
    private ConfiguracaoDAM configuracaoDAM;

    private String numeroConvenio;
    private String chavePix;
    private String chaveAcesso;
    private String authorization;

    public ConfiguracaoPixDam() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ConfiguracaoPix getConfiguracaoPix() {
        return configuracaoPix;
    }

    public void setConfiguracaoPix(ConfiguracaoPix configuracaoPix) {
        this.configuracaoPix = configuracaoPix;
    }

    public ConfiguracaoDAM getConfiguracaoDAM() {
        return configuracaoDAM;
    }

    public void setConfiguracaoDAM(ConfiguracaoDAM configuracaoDAM) {
        this.configuracaoDAM = configuracaoDAM;
    }

    public String getNumeroConvenio() {
        return numeroConvenio;
    }

    public void setNumeroConvenio(String numeroConvenio) {
        this.numeroConvenio = numeroConvenio;
    }

    public String getChavePix() {
        return chavePix;
    }

    public void setChavePix(String chavePix) {
        this.chavePix = chavePix;
    }

    public String getChaveAcesso() {
        return chaveAcesso;
    }

    public void setChaveAcesso(String chaveAcesso) {
        this.chaveAcesso = chaveAcesso;
    }

    public String getAuthorization() {
        return authorization;
    }

    public void setAuthorization(String authorization) {
        this.authorization = authorization;
    }
}
