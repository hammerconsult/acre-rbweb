package br.com.webpublico.entidades;

import br.com.webpublico.entidadesauxiliares.LoteFormularioCompraVO;
import br.com.webpublico.enums.TipoBeneficio;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Novo
 * Date: 23/06/15
 * Time: 11:59
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
@Etiqueta("Lote Cotação/Planilha Orçamentária")
public class LoteCotacao extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Número")
    private Integer numero;

    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Length(maximo = 255)
    @Etiqueta("Descrição")
    private String descricao;

    @Pesquisavel
    @Tabelavel
    @Etiqueta("Valor")
    private BigDecimal valor;

    @Pesquisavel
    @Tabelavel
    @Etiqueta("Cotação")
    @ManyToOne
    private Cotacao cotacao;

    @Enumerated(EnumType.STRING)
    private TipoBeneficio tipoBeneficio;

    @Invisivel
    @OneToMany(mappedBy = "loteCotacao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemCotacao> itens;

    public LoteCotacao() {
        valor = BigDecimal.ZERO;
        this.itens = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Cotacao getCotacao() {
        return cotacao;
    }

    public void setCotacao(Cotacao cotacao) {
        this.cotacao = cotacao;
    }

    public List<ItemCotacao> getItens() {
        if (itens != null) {
            Collections.sort(itens, new Comparator<ItemCotacao>() {
                @Override
                public int compare(ItemCotacao o1, ItemCotacao o2) {
                    return o1.getNumero().compareTo(o2.getNumero());
                }
            });
        }
        return itens;
    }

    public void setItens(List<ItemCotacao> itens) {
        this.itens = itens;
    }

    public boolean hasItens(){
        return !Util.isListNullOrEmpty(itens);
    }

    @Override
    public String toString() {
        return numero + " - " + descricao;
    }

    public TipoBeneficio getTipoBeneficio() {
        return tipoBeneficio;
    }

    public void setTipoBeneficio(TipoBeneficio tipoBeneficio) {
        this.tipoBeneficio = tipoBeneficio;
    }

    public static LoteCotacao fromVO(LoteFormularioCompraVO loteVO, Cotacao entity){
        LoteCotacao lote = new LoteCotacao();
        lote.setCotacao(entity);
        lote.setNumero(loteVO.getNumero());
        lote.setDescricao(loteVO.getDescricao());
        lote.setValor(loteVO.getValorTotal());
        return lote;
    }
}
