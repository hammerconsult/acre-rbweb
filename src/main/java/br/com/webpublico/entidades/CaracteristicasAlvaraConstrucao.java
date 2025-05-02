package br.com.webpublico.entidades;

import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 07/05/15
 * Time: 15:18
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
@Table(name = "ALVARA_VALORATRIBUTO")
public class CaracteristicasAlvaraConstrucao extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ConstrucaoAlvara construcaoAlvara;
    @ManyToOne
    @JoinColumn(name = "ATRIBUTOS_KEY")
    private Atributo atributo;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ATRIBUTOS_ID")
    private ValorAtributo valorAtributo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ConstrucaoAlvara getConstrucaoAlvara() {
        return construcaoAlvara;
    }

    public Atributo getAtributo() {
        return atributo;
    }

    public void setAtributo(Atributo atributo) {
        this.atributo = atributo;
    }

    public ValorAtributo getValorAtributo() {
        return valorAtributo;
    }

    public void setValorAtributo(ValorAtributo valorAtributo) {
        this.valorAtributo = valorAtributo;
    }

    public void setConstrucaoAlvara(ConstrucaoAlvara construcaoAlvara) {
        this.construcaoAlvara = construcaoAlvara;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        CaracteristicasAlvaraConstrucao that = (CaracteristicasAlvaraConstrucao) o;

        if (atributo != null ? !atributo.equals(that.atributo) : that.atributo != null) return false;
        return !(atributo != null ? !atributo.equals(that.atributo) : that.atributo != null);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (atributo != null ? atributo.hashCode() : 0);
        result = 31 * result + (atributo != null ? atributo.hashCode() : 0);
        return result;
    }
}
