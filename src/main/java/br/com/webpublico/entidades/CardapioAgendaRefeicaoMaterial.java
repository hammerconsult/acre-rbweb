package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.List;

@Entity

@Audited
@Table(name="CARDAPIOAGENDAREFEICAOMAT")
@GrupoDiagrama(nome = "Materiais")
@Etiqueta("Cardápio Agenda Refeição Material")
public class CardapioAgendaRefeicaoMaterial extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private CardapioAgendaRefeicao cardapioAgendaRefeicao;

    @Etiqueta("Materiais")
    @ManyToOne
    private Material material;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public CardapioAgendaRefeicao getCardapioAgendaRefeicao() {
        return cardapioAgendaRefeicao;
    }

    public void setCardapioAgendaRefeicao(CardapioAgendaRefeicao cardapioAgendaRefeicao) {
        this.cardapioAgendaRefeicao = cardapioAgendaRefeicao;
    }
}
