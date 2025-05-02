package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity

@Audited
@GrupoDiagrama(nome = "Materiais")
@Etiqueta("Cardápio Agenda")
public class CardapioAgenda extends SuperEntidade{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Etiqueta("Dia")
    @Obrigatorio
    private Date diaSemana;
    @Etiqueta("Observação")
    private String observacao;

    @Etiqueta("Cardapio")
    @ManyToOne
    private Cardapio cardapio;

    @Etiqueta("Cardápio Agenda Refeições")
    @OneToMany(mappedBy = "cardapioAgenda", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CardapioAgendaRefeicao> refeicoes;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDiaSemana() {
        return diaSemana;
    }

    public void setDiaSemana(Date diaSemana) {
        this.diaSemana = diaSemana;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public List<CardapioAgendaRefeicao> getRefeicoes() {
        return refeicoes;
    }

    public void setRefeicoes(List<CardapioAgendaRefeicao> refeicoes) {
        this.refeicoes = refeicoes;
    }

    public Cardapio getCardapio() {
        return cardapio;
    }

    public void setCardapio(Cardapio cardapio) {
        this.cardapio = cardapio;
    }
}
