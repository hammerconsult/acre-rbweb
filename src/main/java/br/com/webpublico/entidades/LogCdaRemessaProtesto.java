package br.com.webpublico.entidades;

import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

@Entity
public class LogCdaRemessaProtesto extends SuperEntidade {

    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    private CdaRemessaProtesto cdaRemessaProtesto;
    private Date dataRegistro;
    private String usuario;
    private String log;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public CdaRemessaProtesto getCdaRemessaProtesto() {
        return cdaRemessaProtesto;
    }

    public void setCdaRemessaProtesto(CdaRemessaProtesto cdaRemessaProtesto) {
        this.cdaRemessaProtesto = cdaRemessaProtesto;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }
}
