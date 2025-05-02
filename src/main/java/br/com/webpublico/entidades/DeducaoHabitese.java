package br.com.webpublico.entidades;

import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Audited
public class DeducaoHabitese extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private Habitese habitese;
    private String descricao;
    private BigDecimal baseDeCalculo;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Habitese getHabitese() {
        return habitese;
    }

    public void setHabitese(Habitese habitese) {
        this.habitese = habitese;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getBaseDeCalculo() {
        return baseDeCalculo;
    }

    public void setBaseDeCalculo(BigDecimal baseDeCalculo) {
        this.baseDeCalculo = baseDeCalculo;
    }
}
