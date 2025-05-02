package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Criado por Mateus
 * Data: 27/09/2016.
 */
@Entity
@Audited
@GrupoDiagrama(nome = "tributario")
@Etiqueta(value = "ItemCalculoContribuicaoMelhoria", genero = "M")
@Table(name = "ITEMCALCCONTMELHORIA")
public class ItemCalculoContribuicaoMelhoria extends ItemCalculo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Version
    @Column(columnDefinition = "integer DEFAULT 0", nullable = false)
    @Invisivel
    private Integer versao;
    @ManyToOne
    private CalculoContribuicaoMelhoria calculoContribuicao;
    @ManyToOne
    private Tributo tributo;
    private BigDecimal valor;

    public ItemCalculoContribuicaoMelhoria() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getVersao() {
        return versao;
    }

    public void setVersao(Integer versao) {
        this.versao = versao;
    }

    @Override
    public Tributo getTributo() {
        return tributo;
    }

    public void setTributo(Tributo tributo) {
        this.tributo = tributo;
    }

    @Override
    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public CalculoContribuicaoMelhoria getCalculoContribuicao() {
        return calculoContribuicao;
    }

    public void setCalculoContribuicao(CalculoContribuicaoMelhoria calculoContribuicao) {
        this.calculoContribuicao = calculoContribuicao;
    }
}
