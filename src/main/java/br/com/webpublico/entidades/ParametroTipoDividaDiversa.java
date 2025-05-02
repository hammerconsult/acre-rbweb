/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Claudio
 */
@GrupoDiagrama(nome = "DividaDiversa")

@Entity
@Audited
@Etiqueta("Parâmetros do Tipo de Dívidas Diversas")
public class ParametroTipoDividaDiversa implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Pesquisavel
    @Tabelavel
    @ManyToOne
    @Etiqueta(value="Tipo de Dívida Diversa")
    private TipoDividaDiversa tipoDividaDiversa;
    @Tabelavel
    @Pesquisavel
    @ManyToOne
    @Etiqueta(value="Exercício")
    private Exercicio exercicio;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "parametroTipoDividaDiversa", orphanRemoval = true)
    private List<ItemParametroTipoDividaDiv> itensParametro;
    @Transient
    private Long criadoEm;

    public ParametroTipoDividaDiversa() {
        criadoEm = System.nanoTime();
        itensParametro = new ArrayList<ItemParametroTipoDividaDiv>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public List<ItemParametroTipoDividaDiv> getItensParametro() {
        return itensParametro;
    }

    public void setItensParametro(List<ItemParametroTipoDividaDiv> itensParametro) {
        this.itensParametro = itensParametro;
    }

    public TipoDividaDiversa getTipoDividaDiversa() {
        return tipoDividaDiversa;
    }

    public void setTipoDividaDiversa(TipoDividaDiversa tipoDividaDiversa) {
        this.tipoDividaDiversa = tipoDividaDiversa;
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
        return "br.com.webpublico.entidades.ParametroTipoDividaDiversa[ id=" + id + " ]";
    }
}
