package br.com.webpublico.entidadesauxiliares.contabil;

import br.com.webpublico.enums.CategoriaOrcamentaria;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class LiquidacaoVO {
    private Long idLiquidacao;
    private String numero;
    private Date data;
    private CategoriaOrcamentaria categoriaOrcamentaria;
    private String liquidacao;
    private BigDecimal valor;

    private Long idEstorno;
    private String estorno;

    private List<LiquidacaoEstornoVO> estornos;

    public LiquidacaoVO() {
    }

    public LiquidacaoVO(Long idLiquidacao, String liquidacao, Long idEstorno, String estorno, CategoriaOrcamentaria categoriaOrcamentaria) {
        this.idLiquidacao = idLiquidacao;
        this.liquidacao = liquidacao;
        this.idEstorno = idEstorno;
        this.estorno = estorno;
        this.categoriaOrcamentaria = categoriaOrcamentaria;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Long getIdLiquidacao() {
        return idLiquidacao;
    }

    public void setIdLiquidacao(Long idLiquidacao) {
        this.idLiquidacao = idLiquidacao;
    }

    public String getLiquidacao() {
        return liquidacao;
    }

    public void setLiquidacao(String liquidacao) {
        this.liquidacao = liquidacao;
    }

    public Long getIdEstorno() {
        return idEstorno;
    }

    public void setIdEstorno(Long idEstorno) {
        this.idEstorno = idEstorno;
    }

    public String getEstorno() {
        return estorno;
    }

    public void setEstorno(String estorno) {
        this.estorno = estorno;
    }

    public CategoriaOrcamentaria getCategoriaOrcamentaria() {
        return categoriaOrcamentaria;
    }

    public void setCategoriaOrcamentaria(CategoriaOrcamentaria categoriaOrcamentaria) {
        this.categoriaOrcamentaria = categoriaOrcamentaria;
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

    public List<LiquidacaoEstornoVO> getEstornos() {
        return estornos;
    }

    public void setEstornos(List<LiquidacaoEstornoVO> estornos) {
        this.estornos = estornos;
    }

    public String getLinkLiquidacao(){
        if (categoriaOrcamentaria.isNormal()){
            return "liquidacao/ver/" + idLiquidacao + "/";
        }
        return "liquidacao/resto-a-pagar/ver/" +idLiquidacao + "/";
    }
}
