package br.com.webpublico.entidades;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by André on 27/01/2015.
 */
@Entity
public class UsuarioNodeServer extends SuperEntidade implements Comparable<UsuarioNodeServer> {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private UsuarioSistema usuario;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataHoraLogin;
    private String nodeServer;
    @Transient
    private Boolean ativo;
    @Transient
    private String ultimaPagina;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UsuarioSistema getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioSistema usuario) {
        this.usuario = usuario;
    }

    public Date getDataHoraLogin() {
        return dataHoraLogin;
    }

    public void setDataHoraLogin(Date dataHoraLogin) {
        this.dataHoraLogin = dataHoraLogin;
    }

    public String getNodeServer() {
        return nodeServer;
    }

    public void setNodeServer(String nodeServer) {
        this.nodeServer = nodeServer;
    }

    public Boolean getAtivo() {
        return ativo != null ? ativo : false;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public String getUltimaPagina() {
        return ultimaPagina;
    }

    public void setUltimaPagina(String ultimaPagina) {
        this.ultimaPagina = ultimaPagina;
    }

    @Override
    public int compareTo(UsuarioNodeServer o) {
        return o.dataHoraLogin.compareTo(this.dataHoraLogin);
    }
}
