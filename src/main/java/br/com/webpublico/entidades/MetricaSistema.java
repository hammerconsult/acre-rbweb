package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Invisivel;

import javax.persistence.*;
import java.util.Date;

@Entity
public class MetricaSistema implements Comparable<MetricaSistema> {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Invisivel
    private Long id;
    private String login;
    private String url;
    private Long inicio;
    private Long fim;
    @Temporal(TemporalType.TIMESTAMP)
    private Date data;
    @Enumerated(EnumType.STRING)
    private Tipo tipo;

    public MetricaSistema() {
    }

    public MetricaSistema(String login, String url, Long inicio, Long fim, Date data, Tipo tipo) {
        this.login = login;
        this.url = url;
        this.inicio = inicio;
        this.fim = fim;
        this.data = data;
        this.tipo = tipo;
    }

    public static MetricaSistema iniciar(String login, String url, Long inicio, Date data, Tipo tipo) {
        return new MetricaSistema(login, url, inicio, null, data, tipo);
    }

    public static MetricaSistema finalizar(String login, String url, Long fim, Date data, Tipo tipo) {
        return new MetricaSistema(login, url, null, fim, data, tipo);
    }

    public Long getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getUrl() {
        return url;
    }


    public Date getData() {
        return data;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getInicio() {
        return inicio;
    }

    public Date getInicioDate() {
        return new Date(inicio);
    }

    public void setInicio(Long inicio) {
        this.inicio = inicio;
    }

    public Long getFim() {
        return fim;
    }

    public Date getFimDate() {
        return new Date(fim);
    }

    public void setFim(Long fim) {
        this.fim = fim;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public void setTipo(Tipo tipo) {
        this.tipo = tipo;
    }

    public Long getTimeInSeconds() {
        if (fim != null && inicio != null)
            return (fim - inicio) / 1000L;
        return 0L;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MetricaSistema metricaSistema = (MetricaSistema) o;

        if (login != null ? !login.equals(metricaSistema.login) : metricaSistema.login != null) return false;
        if (url != null ? !url.equals(metricaSistema.url) : metricaSistema.url != null) return false;
        if (tipo != null ? !tipo.equals(metricaSistema.tipo) : metricaSistema.tipo != null) return false;
        return !(data != null ? !data.equals(metricaSistema.data) : metricaSistema.data != null);

    }

    @Override
    public int hashCode() {
        int result = login != null ? login.hashCode() : 0;
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + (tipo != null ? tipo.hashCode() : 0);
        result = 31 * result + (data != null ? data.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "MetricaSistema{" +
            "data=" + data +
            ", inicio=" + inicio +
            ", fim=" + fim +
            ", url='" + url + '\'' +
            ", login='" + login + '\'' +
            '}';
    }

    @Override
    public int compareTo(MetricaSistema metrica) {
        return metrica.getTimeInSeconds().compareTo(this.getTimeInSeconds());
    }

    public enum Tipo {
        ROTINA_AGENDADA("Rotinas que já rodaram por agendamento"),
        ROTINA_USUARIO("Rotinas mapeadas e executas pelos usuários"),
        ACESSO_PAGINA("Páginas acessadas pelos usuários"),
        ACESSO_PORTAL("Rotinas acessadas via portal de serviços");
        private String descricao;

        Tipo(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

}
