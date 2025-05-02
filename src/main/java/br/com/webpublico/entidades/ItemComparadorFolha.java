package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: mga
 * Date: 11/06/2015
 * Time: 14:55
 */
@Entity
@Audited
@GrupoDiagrama(nome = "RecursosHumanos")
public class ItemComparadorFolha implements Serializable{

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String codigoHierarquia;
    private String unidade;
    @ManyToOne
    private ComparadorFolha comparadorFolha;
    @ManyToOne
    private VinculoFP vinculoFP;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "itemComparadorFolha", orphanRemoval = true)
    private List<DetalheComparador> detalheComparador;
    @Transient
    private Long criadoEm = System.nanoTime();

    public ItemComparadorFolha() {
        detalheComparador = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigoHierarquia() {
        return codigoHierarquia;
    }

    public void setCodigoHierarquia(String codigoHierarquia) {
        this.codigoHierarquia = codigoHierarquia;
    }

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }

    public ComparadorFolha getComparadorFolha() {
        return comparadorFolha;
    }

    public void setComparadorFolha(ComparadorFolha comparadorFolha) {
        this.comparadorFolha = comparadorFolha;
    }

    public VinculoFP getVinculoFP() {
        return vinculoFP;
    }

    public void setVinculoFP(VinculoFP vinculoFP) {
        this.vinculoFP = vinculoFP;
    }

    public List<DetalheComparador> getDetalheComparador() {
        return detalheComparador;
    }

    public void setDetalheComparador(List<DetalheComparador> detalheComparador) {
        this.detalheComparador = detalheComparador;
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
}
