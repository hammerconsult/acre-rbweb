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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 17/04/15
 * Time: 08:31
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
public class LoteFormularioCotacao extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @Etiqueta("Formulário de Cotação")
    private FormularioCotacao formularioCotacao;

    @Positivo(permiteZero = false)
    @Obrigatorio
    @Etiqueta("Nº do Lote")
    private Integer numero;

    @Obrigatorio
    @Etiqueta("Descrição do Lote")
    private String descricao;

    @Invisivel
    @OneToMany(mappedBy = "loteFormularioCotacao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemLoteFormularioCotacao> itensLoteFormularioCotacao;

    @Etiqueta("Valor Lote (R$)")
    private BigDecimal valorTotal;

    public LoteFormularioCotacao() {
        super();
        itensLoteFormularioCotacao = Lists.newArrayList();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public FormularioCotacao getFormularioCotacao() {
        return formularioCotacao;
    }

    public void setFormularioCotacao(FormularioCotacao formularioCotacao) {
        this.formularioCotacao = formularioCotacao;
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

    public List<ItemLoteFormularioCotacao> getItensLoteFormularioCotacao() {
        if (itensLoteFormularioCotacao != null) {
            Collections.sort(itensLoteFormularioCotacao, new Comparator<ItemLoteFormularioCotacao>() {
                @Override
                public int compare(ItemLoteFormularioCotacao o1, ItemLoteFormularioCotacao o2) {
                    return o1.getNumero().compareTo(o2.getNumero());
                }
            });
        }
        return itensLoteFormularioCotacao;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public static LoteFormularioCotacao fromVO(LoteFormularioCompraVO loteVO, FormularioCotacao entity){
        LoteFormularioCotacao lote = new LoteFormularioCotacao();
        lote.setFormularioCotacao(entity);
        lote.setNumero(loteVO.getNumero());
        lote.setDescricao(loteVO.getDescricao());
        lote.setValorTotal(loteVO.getValorTotal());
        return lote;
    }
}
