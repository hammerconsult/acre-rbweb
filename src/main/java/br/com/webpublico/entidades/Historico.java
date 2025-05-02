/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.entidadesauxiliares.ListenerHistoricoCadastro;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Heinz
 */
@Entity
@Audited
//@EntityListeners(value = {ListenerHistoricoCadastro.class})
public class Historico implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataRegistro;
    private String motivo;
    @ManyToOne
    private UsuarioSistema digitador;
    private Long sequencia;
    private String solicitante;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataSolicitacao;
    @ManyToOne
    private Cadastro cadastro;

    public Historico() {
        dataRegistro = new Date();
        dataSolicitacao = new Date();
        solicitante = "";
    }

    public Historico(Long sequencia) {
        this.sequencia = sequencia;
        dataRegistro = new Date();
        dataSolicitacao = new Date();
        solicitante = "";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Cadastro getCadastro() {
        return cadastro;
    }

    public void setCadastro(Cadastro cadastro) {
        this.cadastro = cadastro;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public Date getDataSolicitacao() {
        return dataSolicitacao;
    }

    public void setDataSolicitacao(Date dataSolicitacao) {
        this.dataSolicitacao = dataSolicitacao;
    }

    public UsuarioSistema getDigitador() {
        return digitador;
    }

    public void setDigitador(UsuarioSistema digitador) {
        this.digitador = digitador;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public Long getSequencia() {
        return sequencia;
    }

    public void setSequencia(Long sequencia) {
        this.sequencia = sequencia;
    }

    public String getSolicitante() {
        return solicitante;
    }

    public void setSolicitante(String solicitante) {
        this.solicitante = solicitante;
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
        if (!(object instanceof Historico)) {
            return false;
        }
        Historico other = (Historico) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.Historico[ id=" + id + " ]";
    }
}
