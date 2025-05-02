package br.com.webpublico.entidades.comum.relatorio;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by renato on 17/07/19.
 */
@GrupoDiagrama(nome = "Comum")
@Audited
@Entity
@Etiqueta(value = "Configuração de Relatório")
public class ConfiguracaoDeRelatorio extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Temporal(javax.persistence.TemporalType.DATE)
    @Etiqueta("Data")
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    private Date data;

    @Obrigatorio
    @Etiqueta("Url")
    @Tabelavel
    @Pesquisavel
    private String url;


    @Obrigatorio
    @Etiqueta("Url do webpublico")
    @Tabelavel
    @Pesquisavel
    private String urlWebpublico;

    @Obrigatorio
    @Etiqueta("Chave")
    @Tabelavel
    @Pesquisavel
    private String chave;

    @Obrigatorio
    @Etiqueta("Url")
    @Tabelavel
    @Pesquisavel
    private Integer quantidadeRelatorio;

    private Boolean verificarCache;


    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getQuantidadeRelatorio() {
        return quantidadeRelatorio;
    }

    public void setQuantidadeRelatorio(Integer quantidadeRelatorio) {
        this.quantidadeRelatorio = quantidadeRelatorio;
    }

    public String getChave() {
        return chave;
    }

    public void setChave(String chave) {
        this.chave = chave;
    }

    public Boolean getVerificarCache() {
        if (verificarCache == null) {
            verificarCache = Boolean.FALSE;
        }
        return verificarCache;
    }

    public void setVerificarCache(Boolean verificarCache) {
        this.verificarCache = verificarCache;
    }

    @Override
    public String toString() {
        return url;
    }


    public String getParametroBancoUrl() {
        return "?bancodados=" + getChave().toLowerCase();
    }

    public String getUrlCompleta(String api) {
        return getUrl() + api + getParametroBancoUrl();
    }

    public String getUrlWebpublico() {
        return urlWebpublico;
    }

    public void setUrlWebpublico(String urlWebpublico) {
        this.urlWebpublico = urlWebpublico;
    }
}
