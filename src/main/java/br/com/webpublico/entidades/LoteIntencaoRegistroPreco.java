package br.com.webpublico.entidades;

import br.com.webpublico.entidadesauxiliares.LoteFormularioCompraVO;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Positivo;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Audited
public class LoteIntencaoRegistroPreco extends SuperEntidade implements Comparable<LoteIntencaoRegistroPreco>{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @Etiqueta("Intenção de Registro de Preço")
    private IntencaoRegistroPreco intencaoRegistroPreco;

    @Positivo(permiteZero = false)
    @Obrigatorio
    @Etiqueta("Número")
    private Integer numero;

    @Obrigatorio
    @Etiqueta("Descrição")
    private String descricao;

    @Etiqueta("Valor Lote (R$)")
    private BigDecimal valorTotal;

    @Invisivel
    @OneToMany(mappedBy = "loteIntencaoRegistroPreco", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemIntencaoRegistroPreco> itens;

    public LoteIntencaoRegistroPreco() {
        super();
        itens = Lists.newArrayList();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public IntencaoRegistroPreco getIntencaoRegistroPreco() {
        return intencaoRegistroPreco;
    }

    public void setIntencaoRegistroPreco(IntencaoRegistroPreco intencaoRegistroPreco) {
        this.intencaoRegistroPreco = intencaoRegistroPreco;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public List<ItemIntencaoRegistroPreco> getItens() {
        return itens;
    }

    public void setItens(List<ItemIntencaoRegistroPreco> itens) {
        this.itens = itens;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    @Override
    public int compareTo(LoteIntencaoRegistroPreco o) {
        if (o.getNumero() != null && getNumero() != null) {
            return (getNumero().compareTo(o.getNumero()));
        }
        return 0;
    }

    @Override
    public String toString() {
        return numero + " - " + descricao;
    }

    public static LoteIntencaoRegistroPreco fromVO(LoteFormularioCompraVO loteVO, IntencaoRegistroPreco entity){
        LoteIntencaoRegistroPreco lote =  loteVO.getLoteIRP()  !=null ? loteVO.getLoteIRP() :new LoteIntencaoRegistroPreco();
        lote.setIntencaoRegistroPreco(entity);
        lote.setNumero(loteVO.getNumero());
        lote.setDescricao(loteVO.getDescricao());
        lote.setValorTotal(loteVO.getValorTotal());
        return lote;
    }
}
