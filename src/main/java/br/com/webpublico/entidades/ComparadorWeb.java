package br.com.webpublico.entidades;

import br.com.webpublico.enums.Mes;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: mga
 * Date: 03/02/2015
 * Time: 18:02
 */
@Entity

@Audited
@GrupoDiagrama(nome = "RecursosHumanos")
public class ComparadorWeb implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Enumerated(EnumType.STRING)
    private Mes mes;
    private Integer ano;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "comparadorWeb")
    private List<ItemComparadorWeb> rejeitados;
    @Transient
    public Long criadoEm;

    public ComparadorWeb() {
        criadoEm = System.nanoTime();
        rejeitados = new LinkedList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Mes getMes() {
        return mes;
    }

    public void setMes(Mes mes) {
        this.mes = mes;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public List<ItemComparadorWeb> getRejeitados() {
        return rejeitados;
    }

    public void setRejeitados(List<ItemComparadorWeb> rejeitados) {
        this.rejeitados = rejeitados;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
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
        return (mes == null ? mes : mes.getNumeroMesString()) + "" + ano;
    }
}
