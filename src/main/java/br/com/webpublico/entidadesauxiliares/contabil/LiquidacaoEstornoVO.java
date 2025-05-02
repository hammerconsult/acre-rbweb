package br.com.webpublico.entidadesauxiliares.contabil;

import br.com.webpublico.enums.CategoriaOrcamentaria;

import java.math.BigDecimal;
import java.util.Date;

public class LiquidacaoEstornoVO {
    private Long id;
    private String numero;
    private Date data;
    private BigDecimal valor;
    private CategoriaOrcamentaria categoriaOrcamentaria;

    public CategoriaOrcamentaria getCategoriaOrcamentaria() {
        return categoriaOrcamentaria;
    }

    public void setCategoriaOrcamentaria(CategoriaOrcamentaria categoriaOrcamentaria) {
        this.categoriaOrcamentaria = categoriaOrcamentaria;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getLinkLiquidacaoEstorno(){
        if (categoriaOrcamentaria.isNormal()){
            return "liquidacao-estorno/ver/" + id + "/";
        }
        return "liquidacao-estorno/resto-a-pagar/ver/" +id + "/";
    }
}
