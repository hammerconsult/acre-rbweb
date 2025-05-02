package br.com.webpublico.nfse.domain;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.entidades.comum.UsuarioWeb;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

@Entity
@Audited
@Table(name = "USUARIOWEBDISPOSITIVO")
public class UsuarioWebDispositivo extends SuperEntidade {

    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    private UsuarioWeb usuario;
    @Temporal(TemporalType.TIMESTAMP)
    private Date acessoEm;
    private String dispositivo;
    private String codigo;
    private Boolean confirmado;

    public UsuarioWebDispositivo() {
        this.acessoEm = new Date();
        this.confirmado = false;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UsuarioWeb getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioWeb usuarioNfse) {
        this.usuario = usuarioNfse;
    }

    public Date getAcessoEm() {
        return acessoEm;
    }

    public void setAcessoEm(Date acessoEm) {
        this.acessoEm = acessoEm;
    }

    public String getDispositivo() {
        return dispositivo;
    }

    public void setDispositivo(String dispositivo) {
        this.dispositivo = dispositivo;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Boolean getConfirmado() {
        return confirmado;
    }

    public void setConfirmado(Boolean confirmado) {
        this.confirmado = confirmado;
    }
}
