package br.com.webpublico.nfse.domain;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.nfse.domain.dtos.DetalheItemDeclaracaoServicoNfseDTO;
import br.com.webpublico.nfse.domain.dtos.NfseEntity;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;


/**
 * A ItemDeclaracaoServico.
 */
@Entity
@Audited
@Table(name = "DETALHEITEMDECSERVICO")
public class DetalheItemDeclaracaoServico extends SuperEntidade implements Serializable, NfseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @Etiqueta("Quantidade")
    private BigDecimal quantidade;
    @Obrigatorio
    @Etiqueta("Valor Serviço")
    private BigDecimal valorServico;
    @Obrigatorio
    @Etiqueta("Descrição")
    private String descricao;
    @ManyToOne
    private ItemDeclaracaoServico itemDeclaracaoServico;


    public DetalheItemDeclaracaoServico() {
    }

    public static DetalheItemDeclaracaoServico copy(DetalheItemDeclaracaoServico itemDeclaracaoServico, DeclaracaoPrestacaoServico declaracaoPrestacaoServico) {
        DetalheItemDeclaracaoServico item = new DetalheItemDeclaracaoServico();
        item.descricao = itemDeclaracaoServico.getDescricao();
        item.quantidade = itemDeclaracaoServico.getQuantidade();
        item.setValorServico(itemDeclaracaoServico.getValorServico());
        return item;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getValorServico() {
        return valorServico;
    }

    public void setValorServico(BigDecimal valorServico) {
        this.valorServico = valorServico;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public ItemDeclaracaoServico getItemDeclaracaoServico() {
        return itemDeclaracaoServico;
    }

    public void setItemDeclaracaoServico(ItemDeclaracaoServico itemDeclaracaoServico) {
        this.itemDeclaracaoServico = itemDeclaracaoServico;
    }

    @Override
    public String toString() {
        return "ItemDeclaracaoServico{" +
            "id=" + id +
            ", quantidade='" + quantidade + "'" +
            ", valorServico='" + valorServico + "'" +
            ", descricao='" + descricao + "'" +
            '}';
    }

    public BigDecimal getValorTotal() {
        if (quantidade != null) {
            valorServico.multiply(quantidade);
        }
        return valorServico;
    }


    @Override
    public DetalheItemDeclaracaoServicoNfseDTO toNfseDto() {
        DetalheItemDeclaracaoServicoNfseDTO dto = new DetalheItemDeclaracaoServicoNfseDTO();
        dto.setDescricao(this.getDescricao());
        dto.setQuantidade(this.getQuantidade());
        dto.setValorServico(this.getValorServico());
        return dto;
    }

}
