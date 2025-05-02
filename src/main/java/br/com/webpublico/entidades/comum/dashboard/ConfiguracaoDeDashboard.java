package br.com.webpublico.entidades.comum.dashboard;

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
 * Created by renato on 14/08/19.
 */
@GrupoDiagrama(nome = "Comum")
@Audited
@Entity
@Etiqueta(value = "Configuração de Dashboard")
public class ConfiguracaoDeDashboard extends SuperEntidade {

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
    @Etiqueta("Chave")
    @Tabelavel
    @Pesquisavel
    private String chave;

    public ConfiguracaoDeDashboard() {
    }

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

    public String getChave() {
        return chave;
    }

    public void setChave(String chave) {
        this.chave = chave;
    }

    public String getParametroBancoUrl(){
        return "?bancodados=" + getChave().toLowerCase();
    }

    public String getUrlCompleta(String api){
        return  getUrl() + api + getParametroBancoUrl();
    }
}
