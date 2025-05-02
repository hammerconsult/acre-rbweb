/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoExtincaoCargo;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author Felipe Marinzeck
 */
@GrupoDiagrama(nome = "RecursosHumanos")
@Etiqueta("Extinção de Cargos")
@Audited
@Entity
public class ItemExtincaoCargo implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Etiqueta("Tipo de extinção do cargo")
    @Obrigatorio
    private TipoExtincaoCargo tipoExtincaoCargo;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Cargo")
    @OneToOne
    @Obrigatorio
    private Cargo cargo;
    @ManyToOne
    private ExtincaoCargo extincaoCargo;
    @Transient
    @Invisivel
    private Long criadoEm;

    public ItemExtincaoCargo() {
        this.criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoExtincaoCargo getTipoExtincaoCargo() {
        return tipoExtincaoCargo;
    }

    public void setTipoExtincaoCargo(TipoExtincaoCargo tipoExtincaoCargo) {
        this.tipoExtincaoCargo = tipoExtincaoCargo;
    }

    public Cargo getCargo() {
        return cargo;
    }

    public void setCargo(Cargo cargo) {
        this.cargo = cargo;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public ExtincaoCargo getExtincaoCargo() {
        return extincaoCargo;
    }

    public void setExtincaoCargo(ExtincaoCargo extincaoCargo) {
        this.extincaoCargo = extincaoCargo;
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
        return ""+cargo;
    }
}
