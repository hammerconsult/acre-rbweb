package br.com.webpublico.entidades.rh.pccr;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

/**
 * Created with IntelliJ IDEA.
 * User: mga
 * Date: 20/06/14
 * Time: 17:49
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
public class CategoriaPCCR implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Pesquisavel
    private String descricao;
    @ManyToOne
    private PlanoCargosSalariosPCCR planoCargosSalariosPCCR;
    @ManyToOne
    private CategoriaPCCR superior;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "superior")
    private List<CategoriaPCCR> filhos;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "categoriaPCCR", orphanRemoval = true)
    private List<CargoCategoriaPCCR> cargosCategoriaPCCR = new ArrayList<>();
    @Transient
    private Long criadoEm = System.nanoTime();

    public CategoriaPCCR() {
        cargosCategoriaPCCR = new LinkedList<>();
        filhos = new LinkedList<>();
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public PlanoCargosSalariosPCCR getPlanoCargosSalariosPCCR() {
        return planoCargosSalariosPCCR;
    }

    public void setPlanoCargosSalariosPCCR(PlanoCargosSalariosPCCR planoCargosSalariosPCCR) {
        this.planoCargosSalariosPCCR = planoCargosSalariosPCCR;
    }

    public CategoriaPCCR getSuperior() {
        return superior;
    }

    public void setSuperior(CategoriaPCCR superior) {
        this.superior = superior;
    }

    public List<CategoriaPCCR> getFilhos() {
        return filhos;
    }

    public void setFilhos(List<CategoriaPCCR> filhos) {
        this.filhos = filhos;
    }

    public List<CargoCategoriaPCCR> getCargosCategoriaPCCR() {
        return cargosCategoriaPCCR;
    }

    public void setCargosCategoriaPCCR(List<CargoCategoriaPCCR> cargosCategoriaPCCR) {
        this.cargosCategoriaPCCR = cargosCategoriaPCCR;
    }

    @Override
    public boolean equals(Object o) {
       return IdentidadeDaEntidade.calcularEquals(this,o);
    }

    @Override
    public int hashCode() {
    return IdentidadeDaEntidade.calcularHashCode(this);
    }

    //    @Override
//    public String toString() {
//        return "CategoriaPCCR{" +
//                "id=" + id +
//                ", descricao='" + descricao + '\'' +
//                ", planoCargosSalarios=" + planoCargosSalariosPCCR+
//                ", superior=" + superior +
//                ", filhos=" + filhos +
//                ", cargosCategoriaPCCR=" + cargosCategoriaPCCR +
//                '}';
//    }
    @Override
    public String toString() {
        return descricao;
    }
}
