package br.com.webpublico.entidades.rh.webservices;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.entidades.rh.configuracao.ConfiguracaoRH;
import br.com.webpublico.enums.tributario.TipoWebService;
import org.apache.commons.lang.time.DateUtils;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by William on 23/08/2018.
 */
@Entity
@Audited
public class ConfiguracaoWebServiceRH extends SuperEntidade {
    public static int MINUTOS_ANTES_DO_VENCIMENTO_TOKEN = 10;
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private Long id;
    @Enumerated(EnumType.STRING)
    private TipoWebService tipoWebService;
    private String chave;
    private String url;
    private Integer clientId;
    private String clientSecret;
    private String token;
    @Temporal(TemporalType.TIMESTAMP)
    private Date validadeToken;
    @ManyToOne
    private ConfiguracaoRH configuracaoRH;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoWebService getTipoWebService() {
        return tipoWebService;
    }

    public void setTipoWebService(TipoWebService tipoWebService) {
        this.tipoWebService = tipoWebService;
    }

    public String getChave() {
        return chave;
    }

    public void setChave(String chave) {
        this.chave = chave;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public ConfiguracaoRH getConfiguracaoRH() {
        return configuracaoRH;
    }

    public void setConfiguracaoRH(ConfiguracaoRH configuracaoRH) {
        this.configuracaoRH = configuracaoRH;
    }

    public Integer getClientId() {
        return clientId;
    }

    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getValidadeToken() {
        return validadeToken;
    }

    public void setValidadeToken(Date validadeToken) {
        this.validadeToken = validadeToken;
    }

    public boolean tokenValido() {
        return token != null && validadeToken.after(DateUtils.addMinutes(new Date(), MINUTOS_ANTES_DO_VENCIMENTO_TOKEN));
    }
}
