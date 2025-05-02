package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * Created by mga on 24/08/2017.
 */
@Entity
@Audited
@Etiqueta("Ajuste Depósito Receita Extra")
public class AjusteDepositoReceitaExtra extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @Etiqueta("Ajuste em Depósito")
    private AjusteDeposito ajusteDeposito;

    @ManyToOne
    @Etiqueta("Receita Extra")
    private ReceitaExtra receitaExtra;

    public AjusteDepositoReceitaExtra() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AjusteDeposito getAjusteDeposito() {
        return ajusteDeposito;
    }

    public void setAjusteDeposito(AjusteDeposito ajusteDeposito) {
        this.ajusteDeposito = ajusteDeposito;
    }

    public ReceitaExtra getReceitaExtra() {
        return receitaExtra;
    }

    public void setReceitaExtra(ReceitaExtra receitaExtra) {
        this.receitaExtra = receitaExtra;
    }

    @Override
    public String toString() {
        try {
            return this.receitaExtra.getContaExtraorcamentaria().toString();
        } catch (NullPointerException e) {
            return "";
        }
    }
}
