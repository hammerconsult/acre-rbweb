package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: BRAINIAC
 * Date: 20/01/14
 * Time: 18:11
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Etiqueta("Item de Cálculos de NFS-e")
@Audited
public class ItemCalculoNfse implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private Tributo tributo;
    @ManyToOne
    private CalculoNfse calculoNfse;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Tributo getTributo() {
        return tributo;
    }

    public void setTributo(Tributo tributo) {
        this.tributo = tributo;
    }

    public CalculoNfse getCalculoNfse() {
        return calculoNfse;
    }

    public void setCalculoNfse(CalculoNfse calculoNfse) {
        this.calculoNfse = calculoNfse;
    }
}
