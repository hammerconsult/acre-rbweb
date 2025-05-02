package br.com.webpublico.entidades.rh.administracaodepagamento;

import br.com.webpublico.entidades.EventoFP;
import br.com.webpublico.entidades.ItemFichaFinanceiraFP;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.entidadesauxiliares.rh.calculo.MemoriaCalculoRHDTO;
import br.com.webpublico.entidadesauxiliares.rh.calculo.TipoMemoriaCalculo;
import br.com.webpublico.enums.OperacaoFormula;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
public class MemoriaCalculoRH extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ItemFichaFinanceiraFP itemFichaFinanceiraFP;
    @ManyToOne
    private EventoFP eventoFP;
    private String codigoBase;
    private String descricao;
    @Enumerated(EnumType.STRING)
    private TipoMemoriaCalculo tipo;
    @Enumerated(EnumType.STRING)
    private OperacaoFormula operacao;
    private BigDecimal valor;

    public MemoriaCalculoRH() {
    }


    public MemoriaCalculoRH(ItemFichaFinanceiraFP item, MemoriaCalculoRHDTO dto) {
        this.itemFichaFinanceiraFP = item;
        this.descricao = dto.getDescricao();
        this.eventoFP = dto.getEventoFP();
        this.codigoBase = dto.getCodigoBase();
        this.operacao = dto.getOperacao();
        this.tipo = dto.getTipo();
        this.valor = dto.getValor();
    }

    public ItemFichaFinanceiraFP getItemFichaFinanceiraFP() {
        return itemFichaFinanceiraFP;
    }

    public void setItemFichaFinanceiraFP(ItemFichaFinanceiraFP itemFichaFinanceiraFP) {
        this.itemFichaFinanceiraFP = itemFichaFinanceiraFP;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EventoFP getEventoFP() {
        return eventoFP;
    }

    public void setEventoFP(EventoFP eventoFP) {
        this.eventoFP = eventoFP;
    }

    public String getCodigoBase() {
        return codigoBase;
    }

    public void setCodigoBase(String codigoBase) {
        this.codigoBase = codigoBase;
    }

    public TipoMemoriaCalculo getTipo() {
        return tipo;
    }

    public void setTipo(TipoMemoriaCalculo tipo) {
        this.tipo = tipo;
    }

    public OperacaoFormula getOperacao() {
        return operacao;
    }

    public void setOperacao(OperacaoFormula operacao) {
        this.operacao = operacao;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
