package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity

@Audited
@GrupoDiagrama(nome = "Materiais")
@Etiqueta("Cardápio Agenda Refeição")
public class CardapioAgendaRefeicao extends SuperEntidade{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Etiqueta("Observação")
    private String observacao;

    @ManyToOne
    private CardapioAgenda cardapioAgenda;

    @ManyToOne
    private Refeicao refeicao;

    @Etiqueta("Horário")
    @Temporal(TemporalType.TIME)
    private Date horario;

    @Etiqueta("Cardápio Agenda Refeição Materiais")
    @OneToMany(mappedBy = "cardapioAgendaRefeicao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CardapioAgendaRefeicaoMaterial> materiais;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public List<CardapioAgendaRefeicaoMaterial> getMateriais() {
        return materiais;
    }

    public void setMateriais(List<CardapioAgendaRefeicaoMaterial> materiais) {
        this.materiais = materiais;
    }

    public CardapioAgenda getCardapioAgenda() {
        return cardapioAgenda;
    }

    public void setCardapioAgenda(CardapioAgenda cardapioAgenda) {
        this.cardapioAgenda = cardapioAgenda;
    }

    public Refeicao getRefeicao() {
        return refeicao;
    }

    public void setRefeicao(Refeicao refeicao) {
        this.refeicao = refeicao;
    }

    public Date getHorario() {
        return horario;
    }

    public void setHorario(Date horario) {
        this.horario = horario;
    }
}
