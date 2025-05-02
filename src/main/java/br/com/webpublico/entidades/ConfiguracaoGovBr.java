package br.com.webpublico.entidades;

import br.com.webpublico.enums.Ambiente;
import br.com.webpublico.enums.Sistema;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
public class ConfiguracaoGovBr extends SuperEntidade {

    @Id
    @GeneratedValue
    private Long id;
    @Obrigatorio
    @Etiqueta("Sistema")
    @Enumerated(EnumType.STRING)
    private Sistema sistema;
    @Obrigatorio
    @Etiqueta("Ambiente")
    @Enumerated(EnumType.STRING)
    private Ambiente ambiente;
    @Obrigatorio
    @Etiqueta("URL Provider")
    private String urlProvider;
    @Obrigatorio
    @Etiqueta("Client ID")
    private String clientId;
    @Obrigatorio
    @Etiqueta("Secret")
    private String secret;
    @Obrigatorio
    @Etiqueta("Redirect URI")
    private String redirectUri;
    private String codeVerifier;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Sistema getSistema() {
        return sistema;
    }

    public void setSistema(Sistema sistema) {
        this.sistema = sistema;
    }

    public Ambiente getAmbiente() {
        return ambiente;
    }

    public void setAmbiente(Ambiente ambiente) {
        this.ambiente = ambiente;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    public String getUrlProvider() {
        return urlProvider;
    }

    public void setUrlProvider(String urlProvider) {
        this.urlProvider = urlProvider;
    }

    public String getCodeVerifier() {
        return codeVerifier;
    }

    public void setCodeVerifier(String codeVerifier) {
        this.codeVerifier = codeVerifier;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public ConfiguracaoGovBr() {
    }


}
