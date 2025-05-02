package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * Created by mga on 24/08/2017.
 */
@Entity
@Audited
@Etiqueta("Ajuste Depósito Estorno de Receita Extra")
@Table(name = "AJUSTEDEPOSITORECEITAEST")
public class AjusteDepositoReceitaExtraEstorno extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @Etiqueta("Estorno de Ajuste em Depósito")
    private AjusteDepositoEstorno ajusteDepositoEstorno;

    @ManyToOne
    @Etiqueta("Estorno de Receita Extra")
    private ReceitaExtraEstorno receitaExtraEstorno;

    public AjusteDepositoReceitaExtraEstorno() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AjusteDepositoEstorno getAjusteDepositoEstorno() {
        return ajusteDepositoEstorno;
    }

    public void setAjusteDepositoEstorno(AjusteDepositoEstorno ajusteDepositoEstorno) {
        this.ajusteDepositoEstorno = ajusteDepositoEstorno;
    }

    public ReceitaExtraEstorno getReceitaExtraEstorno() {
        return receitaExtraEstorno;
    }

    public void setReceitaExtraEstorno(ReceitaExtraEstorno receitaExtraEstorno) {
        this.receitaExtraEstorno = receitaExtraEstorno;
    }

    @Override
    public String toString() {
        try {
            return this.receitaExtraEstorno.getReceitaExtra().toString();
        } catch (NullPointerException e) {
            return "";
        }
    }
}
