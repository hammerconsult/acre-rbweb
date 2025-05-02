package br.com.webpublico.entidadesauxiliares.rh.calculo;

import br.com.webpublico.entidades.EventoFP;
import br.com.webpublico.entidades.rh.administracaodepagamento.MemoriaCalculoRH;
import br.com.webpublico.enums.OperacaoFormula;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.List;

public class MemoriaCalculoRHDTO {
    private String codigoBase;
    private String descricao;
    private EventoFP eventoFP;
    private TipoMemoriaCalculo tipo;
    private OperacaoFormula operacao;
    private BigDecimal valor;


    public MemoriaCalculoRHDTO() {
    }

    public MemoriaCalculoRHDTO(String codigoBase, String descricaoBase, EventoFP eventoFP, TipoMemoriaCalculo tipo, OperacaoFormula operacao, BigDecimal valor) {
        this.codigoBase = codigoBase;
        this.descricao = descricaoBase;
        this.eventoFP = eventoFP;
        this.tipo = tipo;
        this.operacao = operacao;
        this.valor = valor;
    }

    public MemoriaCalculoRHDTO(String codigoBase, EventoFP eventoFP, TipoMemoriaCalculo tipo, OperacaoFormula operacao, BigDecimal valor) {
        this.codigoBase = codigoBase;
        this.eventoFP = eventoFP;
        this.tipo = tipo;
        this.operacao = operacao;
        this.valor = valor;
    }

    public static List<MemoriaCalculoRHDTO> toDto(List<MemoriaCalculoRH> memorias) {
        List<MemoriaCalculoRHDTO> memoriasDto = Lists.newArrayList();
        for (MemoriaCalculoRH memoria : memorias) {
            memoriasDto.add(toDto(memoria));
        }
        return memoriasDto;
    }

    public static MemoriaCalculoRHDTO toDto(MemoriaCalculoRH memoria) {
        return new MemoriaCalculoRHDTO(memoria.getCodigoBase(), memoria.getDescricao(), memoria.getEventoFP(), memoria.getTipo(), memoria.getOperacao(), memoria.getValor());
    }

    public String getCodigoBase() {
        return codigoBase;
    }

    public void setCodigoBase(String codigoBase) {
        this.codigoBase = codigoBase;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public EventoFP getEventoFP() {
        return eventoFP;
    }

    public void setEventoFP(EventoFP eventoFP) {
        this.eventoFP = eventoFP;
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

    @Override
    public String toString() {
        return "MemoriaCalculoRHDTO{" +
            "codigoBase='" + codigoBase + '\'' +
            ", descricaoBase='" + descricao + '\'' +
            ", eventoFP=" + eventoFP +
            ", tipo=" + tipo +
            ", operacao=" + operacao +
            ", valor=" + valor +
            '}';
    }
}
