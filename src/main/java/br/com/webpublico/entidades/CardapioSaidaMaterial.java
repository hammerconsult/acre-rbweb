package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
public class CardapioSaidaMaterial extends SuperEntidade {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @Obrigatorio
    @Etiqueta("Cardápio")
    private Cardapio cardapio;

    @ManyToOne(cascade = CascadeType.ALL)
    @Etiqueta("Saída Material")
    private SaidaMaterial saidaMaterial;

    public CardapioSaidaMaterial() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Cardapio getCardapio() {
        return cardapio;
    }

    public void setCardapio(Cardapio cardapio) {
        this.cardapio = cardapio;
    }

    public SaidaMaterial getSaidaMaterial() {
        return saidaMaterial;
    }

    public void setSaidaMaterial(SaidaMaterial saidaMaterial) {
        this.saidaMaterial = saidaMaterial;
    }
}
