package br.com.webpublico.entidades;

import br.com.webpublico.enums.Mes;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
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
public class ComparadorFolha implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Enumerated(EnumType.STRING)
    private Mes mes;
    private Integer ano;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "comparadorFolha")
    private List<ItemComparadorFolha> itemComparadorFolhas;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataRegistro;
    @Transient
    public Long criadoEm;

    public ComparadorFolha() {
        criadoEm = System.nanoTime();
        itemComparadorFolhas = new LinkedList<>();
    }

    public List<ItemComparadorFolha> getItemComparadorFolhas() {
        return itemComparadorFolhas;
    }

    public void setItemComparadorFolhas(List<ItemComparadorFolha> itemComparadorFolhas) {
        this.itemComparadorFolhas = itemComparadorFolhas;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
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

    public Long getCriadoEm() {
        return criadoEm;
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
