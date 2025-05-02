/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author leonardo
 */
@GrupoDiagrama(nome = "Alvara")
@Entity
@Audited
public class CNAEAlvara implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private CNAE cnae;
    @ManyToOne
    private Alvara alvara;
    @Transient
    private Long criadoEm;
    @OneToOne(cascade = CascadeType.MERGE)
    private HorarioFuncionamento horarioFuncionamento;
    private Boolean exercidaNoLocal;

    public CNAEAlvara() {
        this.criadoEm = System.nanoTime();
        this.exercidaNoLocal = Boolean.FALSE;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Alvara getAlvara() {
        return alvara;
    }

    public void setAlvara(Alvara alvara) {
        this.alvara = alvara;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public CNAE getCnae() {
        return cnae;
    }

    public void setCnae(CNAE cnae) {
        this.cnae = cnae;
    }

    public HorarioFuncionamento getHorarioFuncionamento() {
        return horarioFuncionamento;
    }

    public void setHorarioFuncionamento(HorarioFuncionamento horarioFuncionamento) {
        this.horarioFuncionamento = horarioFuncionamento;
    }

    public Boolean getExercidaNoLocal() {
        return exercidaNoLocal != null ? exercidaNoLocal : Boolean.FALSE;
    }

    public void setExercidaNoLocal(Boolean exercidaNoLocal) {
        this.exercidaNoLocal = exercidaNoLocal;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    @Override
    public String toString() {
        return getCnae().getDescricaoDetalhada();
    }

}
