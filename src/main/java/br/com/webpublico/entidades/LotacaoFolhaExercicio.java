/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * @author Usuario
 */
//Histórico de Lotações
@GrupoDiagrama(nome = "RecursosHumanos")
@Entity

@Audited
public class LotacaoFolhaExercicio implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ContratoFP contratoFP;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date inicioVigencia;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date fimVigencia;
    private String codigoLotacaoFolha;
    private String descricaoLotacaoFolha;
    private String codigoLotacaoExercicio;
    private String descricaoLotacaoExercicio;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigoLotacaoExercicio() {
        return codigoLotacaoExercicio;
    }

    public void setCodigoLotacaoExercicio(String codigoLotacaoExercicio) {
        this.codigoLotacaoExercicio = codigoLotacaoExercicio;
    }

    public String getCodigoLotacaoFolha() {
        return codigoLotacaoFolha;
    }

    public void setCodigoLotacaoFolha(String codigoLotacaoFolha) {
        this.codigoLotacaoFolha = codigoLotacaoFolha;
    }

    public String getDescricaoLotacaoExercicio() {
        return descricaoLotacaoExercicio;
    }

    public void setDescricaoLotacaoExercicio(String descricaoLotacaoExercicio) {
        this.descricaoLotacaoExercicio = descricaoLotacaoExercicio;
    }

    public String getDescricaoLotacaoFolha() {
        return descricaoLotacaoFolha;
    }

    public void setDescricaoLotacaoFolha(String descricaoLotacaoFolha) {
        this.descricaoLotacaoFolha = descricaoLotacaoFolha;
    }

    public Date getFimVigencia() {
        return fimVigencia;
    }

    public void setFimVigencia(Date fimVigencia) {
        this.fimVigencia = fimVigencia;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public ContratoFP getContratoFP() {
        return contratoFP;
    }

    public void setContratoFP(ContratoFP contratoFP) {
        this.contratoFP = contratoFP;
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final LotacaoFolhaExercicio other = (LotacaoFolhaExercicio) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.id);
        return hash;
    }

    public String toString() {
        return "LotacaoFolhaExercicio{" + "id=" + id + '}';
    }

    public String getCodigoDescricaoLotacaoFolha() {
        if (codigoLotacaoFolha != null && descricaoLotacaoFolha != null) {
            return codigoLotacaoFolha + " - " + descricaoLotacaoFolha;
        }
        return "";
    }

    public String getCodigoDescricaoLotacaoExercicio() {
        if (codigoLotacaoExercicio != null && descricaoLotacaoExercicio != null) {
            return codigoLotacaoExercicio + " - " + descricaoLotacaoExercicio;
        }
        return "";
    }
}
