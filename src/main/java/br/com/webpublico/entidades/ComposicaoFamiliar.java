package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Positivo;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Criado por Mateus
 * Data: 11/05/2017.
 */
@Entity
@Audited
public class ComposicaoFamiliar extends SuperEntidade{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private AuxilioFuneral auxilioFuneral;
    @Etiqueta("Nome")
    @Obrigatorio
    private String nome;
    @Etiqueta("Idade")
    private Integer idade;
    @Etiqueta("Grau de Parentesco")
    private String grauDeParentesco;
    @Etiqueta("Ocupação")
    private String ocupacao;
    @Etiqueta("Renda")
    @Obrigatorio
    @Positivo
    private BigDecimal renda;

    public ComposicaoFamiliar() {
        renda = BigDecimal.ZERO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AuxilioFuneral getAuxilioFuneral() {
        return auxilioFuneral;
    }

    public void setAuxilioFuneral(AuxilioFuneral auxilioFuneral) {
        this.auxilioFuneral = auxilioFuneral;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Integer getIdade() {
        return idade;
    }

    public void setIdade(Integer idade) {
        this.idade = idade;
    }

    public String getGrauDeParentesco() {
        return grauDeParentesco;
    }

    public void setGrauDeParentesco(String grauDeParentesco) {
        this.grauDeParentesco = grauDeParentesco;
    }

    public String getOcupacao() {
        return ocupacao;
    }

    public void setOcupacao(String ocupacao) {
        this.ocupacao = ocupacao;
    }

    public BigDecimal getRenda() {
        return renda;
    }

    public void setRenda(BigDecimal renda) {
        this.renda = renda;
    }
}
