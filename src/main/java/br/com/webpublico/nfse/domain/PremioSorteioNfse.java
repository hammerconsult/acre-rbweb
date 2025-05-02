package br.com.webpublico.nfse.domain;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Monetario;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Created by William on 19/01/2019.
 */
@Audited
@Entity
public class PremioSorteioNfse extends SuperEntidade {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private SorteioNfse sorteio;
    @Obrigatorio
    @Etiqueta("Sequência")
    private Integer sequencia;
    @Obrigatorio
    @Etiqueta("Descrição")
    private String descricao;
    @Obrigatorio
    @Etiqueta("Quantidade")
    private Integer quantidade;
    @Obrigatorio
    @Monetario
    @Etiqueta("Valor R$")
    private BigDecimal valor;

    public PremioSorteioNfse() {
        quantidade = 1;
        valor = BigDecimal.ZERO;
        sequencia = 1;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getSequencia() {
        return sequencia;
    }

    public void setSequencia(Integer sequencia) {
        this.sequencia = sequencia;
    }

    public SorteioNfse getSorteio() {
        return sorteio;
    }

    public void setSorteio(SorteioNfse sorteio) {
        this.sorteio = sorteio;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }
}

