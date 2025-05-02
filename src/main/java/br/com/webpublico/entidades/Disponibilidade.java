/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

/**
 * @author Felipe Marinzeck
 */
@GrupoDiagrama(nome = "RecursosHumanos")
@Etiqueta("Disponibilidade")
@Audited
@Entity
public class Disponibilidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @Tabelavel
    @OneToOne
    @Etiqueta("Extinção do Cargo")
    private ExtincaoCargo extincaoCargo;
    @Tabelavel
    @Etiqueta("Ato Legal da Disponibilidade")
    @ManyToOne
    @Obrigatorio
    private AtoLegal atoLegal;
    @Etiqueta("Contratos")
    @OneToMany(mappedBy = "disponibilidade", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemDisponibilidade> itensDiponibilidade;
    @Transient
    @Invisivel
    private Long criadoEm;

    public Disponibilidade() {
        this.criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AtoLegal getAtoLegal() {
        return atoLegal;
    }

    public void setAtoLegal(AtoLegal atoLegal) {
        this.atoLegal = atoLegal;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public List<ItemDisponibilidade> getItensDiponibilidade() {
        if (itensDiponibilidade == null) {
            itensDiponibilidade = new ArrayList<>();
        }

        Collections.sort(itensDiponibilidade, new Comparator<ItemDisponibilidade>() {
            @Override
            public int compare(ItemDisponibilidade o1, ItemDisponibilidade o2) {
                String mat1 = o1.getContratoFP().getMatriculaFP().getMatricula();
                String mat2 = o2.getContratoFP().getMatriculaFP().getMatricula();
                Integer m1 = Integer.parseInt(mat1);
                Integer m2 = Integer.parseInt(mat2);
                return m1.compareTo(m2);
            }
        });
        return itensDiponibilidade;
    }

    public void setItensDiponibilidade(List<ItemDisponibilidade> itensDiponibilidade) {
        this.itensDiponibilidade = itensDiponibilidade;
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
        return atoLegal + " - " + extincaoCargo;
    }

    public boolean selecionarContrato(ContratoFP c) {
        if (itensDiponibilidade == null) {
            return false;
        }
        for (ItemDisponibilidade id : itensDiponibilidade) {
            if (id.getContratoFP().equals(c)) {
                id.setSelecionadoEmLista(true);
                return true;
            }
        }
        return false;
    }

    public void removerItensNaoMarcados() {
        for (Iterator<ItemDisponibilidade> it = this.getItensDiponibilidade().iterator(); it.hasNext(); ) {
            ItemDisponibilidade id = it.next();
            if (!id.isSelecionadoEmLista()) {
                it.remove();
            }
        }
    }
}
