package br.com.webpublico.entidades.administrativo.licitacao;

import br.com.webpublico.entidades.ItemPregao;
import br.com.webpublico.entidades.LancePregao;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.enums.OrigemItemPregaoLanceVencedor;
import br.com.webpublico.enums.StatusLancePregao;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import com.google.common.collect.ComparisonChain;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity

@Audited
@GrupoDiagrama(nome = "Licitacao")
@Etiqueta("Item Pregão Lance Vencedor")
public class ItemPregaoLanceVencedor extends SuperEntidade implements Comparable<ItemPregaoLanceVencedor> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @Obrigatorio
    @Etiqueta("Item Pregão")
    private ItemPregao itemPregao;

    @ManyToOne
    @Obrigatorio
    @Etiqueta("Lance Pregão")
    private LancePregao lancePregao;

    @Enumerated(EnumType.STRING)
    @Etiqueta("Status")
    private StatusLancePregao status;

    @Enumerated(EnumType.STRING)
    @Etiqueta("Status")
    private OrigemItemPregaoLanceVencedor origem;

    @Etiqueta("Valor")
    private BigDecimal valor;

    @Etiqueta("Percentual Desconto")
    private BigDecimal percentualDesconto;

    public ItemPregaoLanceVencedor() {
        super();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ItemPregao getItemPregao() {
        return itemPregao;
    }

    public void setItemPregao(ItemPregao itemPregao) {
        this.itemPregao = itemPregao;
    }

    public LancePregao getLancePregao() {
        return lancePregao;
    }

    public void setLancePregao(LancePregao lancePregao) {
        this.lancePregao = lancePregao;
    }

    public StatusLancePregao getStatus() {
        return status;
    }

    public void setStatus(StatusLancePregao status) {
        this.status = status;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public BigDecimal getPercentualDesconto() {
        return percentualDesconto;
    }

    public void setPercentualDesconto(BigDecimal percentualDesconto) {
        this.percentualDesconto = percentualDesconto;
    }

    public OrigemItemPregaoLanceVencedor getOrigem() {
        return origem;
    }

    public void setOrigem(OrigemItemPregaoLanceVencedor origem) {
        this.origem = origem;
    }

    @Override
    public int compareTo(ItemPregaoLanceVencedor o) {
        return ComparisonChain.start().compare(id, o.getId()).result();
    }
}
