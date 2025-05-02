/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.negocios.OuvinteAuditoria;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Munif
 */
@GrupoDiagrama(nome = "Auditoria")
@Entity
@RevisionEntity(value = OuvinteAuditoria.class)
public class RevisaoAuditoria extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @RevisionNumber
    private Long id;
    @RevisionTimestamp
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataHora;
    private String usuario;
    private String ip;
    @Transient
    //Define qual a classe da revisao no auditoriaJDBC.buscarRevisaoAuditoria
    private Class classeEntidade;
    @Transient
    //Define qual o ID da classe da revisao no auditoriaJDBC.buscarRevisaoAuditoria
    private Long idEntidade;
    //Usado para recuperar o nome do usuário na tabela de pessoa física
    @Transient
    private String nomeUsuario;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataHora() {
        return dataHora;
    }

    public void setDataHora(Date dataHora) {
        this.dataHora = dataHora;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public Class getClasseEntidade() {
        return classeEntidade;
    }

    public void setClasseEntidade(Class classeEntidade) {
        this.classeEntidade = classeEntidade;
    }

    public Long getIdEntidade() {
        return idEntidade;
    }

    public void setIdEntidade(Long idEntidade) {
        this.idEntidade = idEntidade;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RevisaoAuditoria)) {
            return false;
        }
        RevisaoAuditoria other = (RevisaoAuditoria) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entidades.RevisaoAuditoria[id=" + id + "]";
    }
}
