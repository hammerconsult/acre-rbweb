package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import com.google.common.collect.ComparisonChain;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Audited
@Etiqueta("Item Participante Intenção de Registro de Preço")
public class ItemParticipanteIRP extends SuperEntidade implements Comparable<ItemParticipanteIRP> {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @Etiqueta("Participante IRP")
    private ParticipanteIRP participanteIRP;

    @ManyToOne
    @Etiqueta(value = "Item Intenção de Registro Preço")
    private ItemIntencaoRegistroPreco itemIntencaoRegistroPreco;

    @Obrigatorio
    @Etiqueta("Quantidade")
    private BigDecimal quantidade;

    @Etiqueta("Valor (R$)")
    private BigDecimal valor;

    @Transient
    private Boolean selecionado;

    public ItemParticipanteIRP() {
        selecionado = Boolean.FALSE;
        valor = BigDecimal.ZERO;
    }

    public Boolean getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(Boolean selecionado) {
        this.selecionado = selecionado;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ParticipanteIRP getParticipanteIRP() {
        return participanteIRP;
    }

    public void setParticipanteIRP(ParticipanteIRP participanteIRP) {
        this.participanteIRP = participanteIRP;
    }

    public ItemIntencaoRegistroPreco getItemIntencaoRegistroPreco() {
        return itemIntencaoRegistroPreco;
    }

    public void setItemIntencaoRegistroPreco(ItemIntencaoRegistroPreco itemIntencaoRegistroPreco) {
        this.itemIntencaoRegistroPreco = itemIntencaoRegistroPreco;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public boolean isTipoControlePorQuantidade() {
        return itemIntencaoRegistroPreco != null && itemIntencaoRegistroPreco.isTipoControlePorQuantidade();
    }

    public boolean isTipoControlePorValor() {
        return itemIntencaoRegistroPreco != null && itemIntencaoRegistroPreco.isTipoControlePorValor();
    }

    public BigDecimal getValorColuna() {
        return isTipoControlePorQuantidade() ? quantidade : valor;
    }

    @Override
    public int compareTo(ItemParticipanteIRP o) {
        try {
            return ComparisonChain.start().compare(getItemIntencaoRegistroPreco().getLoteIntencaoRegistroPreco().getNumero(), o.getItemIntencaoRegistroPreco().getLoteIntencaoRegistroPreco().getNumero())
                .compare(getItemIntencaoRegistroPreco().getNumero(), o.getItemIntencaoRegistroPreco().getNumero()).result();
        } catch (NullPointerException e) {
            return 0;
        }
    }
}
