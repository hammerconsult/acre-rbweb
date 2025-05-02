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
@Table(name = "CI_VALORATRIBUTOS")
public class CaracteristicasBci extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private CadastroImobiliario cadastroImobiliario;
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

    public CadastroImobiliario getCadastroImobiliario() {
        return cadastroImobiliario;
    }

    public void setCadastroImobiliario(CadastroImobiliario cadastroImobiliario) {
        this.cadastroImobiliario = cadastroImobiliario;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        CaracteristicasBci that = (CaracteristicasBci) o;

        if (cadastroImobiliario != null ? !cadastroImobiliario.equals(that.cadastroImobiliario) : that.cadastroImobiliario != null)
            return false;
        return !(atributo != null ? !atributo.equals(that.atributo) : that.atributo != null);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (cadastroImobiliario != null ? cadastroImobiliario.hashCode() : 0);
        result = 31 * result + (atributo != null ? atributo.hashCode() : 0);
        return result;
    }
}
