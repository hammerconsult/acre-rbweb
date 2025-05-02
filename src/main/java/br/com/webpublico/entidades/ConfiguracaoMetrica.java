package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Invisivel;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class ConfiguracaoMetrica extends SuperEntidade {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Invisivel
    private Long id;
    private String cron;
    private String usuarioJboss;
    private String senhaJboss;
    private String ultimoUsuario;
    private Boolean ligado;
    private Boolean verificarTimeout;

    public ConfiguracaoMetrica() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public String getUsuarioJboss() {
        return usuarioJboss;
    }

    public void setUsuarioJboss(String usuarioJboss) {
        this.usuarioJboss = usuarioJboss;
    }

    public String getSenhaJboss() {
        return senhaJboss;
    }

    public void setSenhaJboss(String senhaJboss) {
        this.senhaJboss = senhaJboss;
    }

    public Boolean getLigado() {
        return ligado;
    }

    public void setLigado(Boolean ligado) {
        this.ligado = ligado;
    }

    public Boolean getVerificarTimeout() {
        return verificarTimeout;
    }

    public void setVerificarTimeout(Boolean verificarTimeout) {
        this.verificarTimeout = verificarTimeout;
    }

    public String getUltimoUsuario() {
        return ultimoUsuario;
    }

    public void setUltimoUsuario(String ultimoUsuario) {
        this.ultimoUsuario = ultimoUsuario;
    }

    @Override
    public String toString() {
        return "ConfiguracaoMetrica{" +
            "id=" + id +
            ", cron='" + cron + '\'' +
            ", usuarioJboss='" + usuarioJboss + '\'' +
            ", senhaJboss='" + senhaJboss + '\'' +
            ", ligado='" + ligado + '\'' +
            '}';
    }
}
