package br.com.webpublico.entidades;

import com.google.common.collect.Lists;

import javax.persistence.*;
import java.util.List;

@Entity
public class ConfiguracaoPix extends SuperEntidade {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String urlIntegrador;
    private String base;
    private String appKey;
    private String urlToken;
    private String urlQrCode;

    @OneToMany(mappedBy = "configuracaoPix", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ConfiguracaoPixDam> configuracoesPorDam;

    public ConfiguracaoPix() {
        this.configuracoesPorDam = Lists.newArrayList();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrlIntegrador() {
        return urlIntegrador;
    }

    public void setUrlIntegrador(String urlIntegrador) {
        this.urlIntegrador = urlIntegrador;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getUrlToken() {
        return urlToken;
    }

    public void setUrlToken(String urlToken) {
        this.urlToken = urlToken;
    }

    public String getUrlQrCode() {
        return urlQrCode;
    }

    public void setUrlQrCode(String urlQrCode) {
        this.urlQrCode = urlQrCode;
    }

    public List<ConfiguracaoPixDam> getConfiguracoesPorDam() {
        return configuracoesPorDam;
    }

    public void setConfiguracoesPorDam(List<ConfiguracaoPixDam> configuracoesPorDam) {
        this.configuracoesPorDam = configuracoesPorDam;
    }
}
