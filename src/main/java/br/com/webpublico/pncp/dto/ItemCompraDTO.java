package br.com.webpublico.pncp.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.validation.constraints.*;
import java.math.BigDecimal;

public class ItemCompraDTO {

    @JsonIgnore
    private String codigoObjetoCompra;

    @JsonIgnore
    private String descricaoObjetoCompra;

    @JsonIgnore
    private String especificacao;

    @JsonIgnore
    private String valorUnitarioEstimadoComMascara;

    @JsonIgnore
    private String valorTotalComMascara;

    @NotNull(message = "O campo 'numeroItem' é obrigatório")
    @Min(value = 1, message = "O 'numeroItem' deve ser maior ou igual a 1")
    private Integer numeroItem;

    @NotBlank(message = "O campo 'materialOuServico' é obrigatório")
    @NotEmpty(message = "O campo 'materialOuServico' não pode estar vazio")
    private String materialOuServico;

    @NotNull(message = "O campo 'tipoBeneficioId' é obrigatório")
    private Integer tipoBeneficioId;

    @NotNull(message = "O campo 'incentivoProdutivoBasico' é obrigatório")
    private Boolean incentivoProdutivoBasico;

    @NotBlank(message = "O campo 'descricao' é obrigatório")
    @NotEmpty(message = "O campo 'descricao' não pode estar vazio")
    private String descricao;

    @NotNull(message = "O campo 'quantidade' é obrigatório")
    @DecimalMin(value = "0.0000", message = "A 'quantidade' deve ser maior ou igual a 0.0000")
    private BigDecimal quantidade;

    @NotBlank(message = "O campo 'unidadeMedida' é obrigatório")
    @NotEmpty(message = "O campo 'unidadeMedida' não pode estar vazio")
    private String unidadeMedida;

    @NotNull(message = "O campo 'orcamentoSigiloso' é obrigatório")
    private Boolean orcamentoSigiloso;

    @NotNull(message = "O campo 'valorUnitarioEstimado' é obrigatório")
    @DecimalMin(value = "0.0000", message = "O 'valorUnitarioEstimado' deve ser maior ou igual a 0.0000")
    private BigDecimal valorUnitarioEstimado;

    @NotNull(message = "O campo 'valorTotal' é obrigatório")
    @DecimalMin(value = "0.0000", message = "O 'valorTotal' deve ser maior ou igual a 0.0000")
    private BigDecimal valorTotal;

    @NotNull(message = "O campo 'criterioJulgamentoId' é obrigatório")
    private Integer criterioJulgamentoId;

    private Integer itemCategoriaId;

    private String patrimonio;

    @NotBlank(message = "O campo 'codigoRegistroImobiliario' é obrigatório")
    @NotEmpty(message = "O campo 'codigoRegistroImobiliario' não pode estar vazio")
    private String codigoRegistroImobiliario;

    public String getCodigoObjetoCompra() {
        return codigoObjetoCompra;
    }

    public void setCodigoObjetoCompra(String codigoObjetoCompra) {
        this.codigoObjetoCompra = codigoObjetoCompra;
    }

    public String getDescricaoObjetoCompra() {
        return descricaoObjetoCompra;
    }

    public void setDescricaoObjetoCompra(String descricaoObjetoCompra) {
        this.descricaoObjetoCompra = descricaoObjetoCompra;
    }

    public String getEspecificacao() {
        return especificacao;
    }

    public void setEspecificacao(String especificacao) {
        this.especificacao = especificacao;
    }

    public String getValorUnitarioEstimadoComMascara() {
        return valorUnitarioEstimadoComMascara;
    }

    public void setValorUnitarioEstimadoComMascara(String valorUnitarioEstimadoComMascara) {
        this.valorUnitarioEstimadoComMascara = valorUnitarioEstimadoComMascara;
    }

    public String getValorTotalComMascara() {
        return valorTotalComMascara;
    }

    public void setValorTotalComMascara(String valorTotalComMascara) {
        this.valorTotalComMascara = valorTotalComMascara;
    }

    public Integer getNumeroItem() {
        return numeroItem;
    }

    public void setNumeroItem(Integer numeroItem) {
        this.numeroItem = numeroItem;
    }

    public String getMaterialOuServico() {
        return materialOuServico;
    }

    public void setMaterialOuServico(String materialOuServico) {
        this.materialOuServico = materialOuServico;
    }

    public Integer getTipoBeneficioId() {
        return tipoBeneficioId;
    }

    public void setTipoBeneficioId(Integer tipoBeneficioId) {
        this.tipoBeneficioId = tipoBeneficioId;
    }

    public Boolean getIncentivoProdutivoBasico() {
        return incentivoProdutivoBasico;
    }

    public void setIncentivoProdutivoBasico(Boolean incentivoProdutivoBasico) {
        this.incentivoProdutivoBasico = incentivoProdutivoBasico;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }

    public String getUnidadeMedida() {
        return unidadeMedida;
    }

    public void setUnidadeMedida(String unidadeMedida) {
        this.unidadeMedida = unidadeMedida;
    }

    public BigDecimal getValorUnitarioEstimado() {
        return valorUnitarioEstimado;
    }

    public void setValorUnitarioEstimado(BigDecimal valorUnitarioEstimado) {
        this.valorUnitarioEstimado = valorUnitarioEstimado;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public Integer getCriterioJulgamentoId() {
        return criterioJulgamentoId;
    }

    public void setCriterioJulgamentoId(Integer criterioJulgamentoId) {
        this.criterioJulgamentoId = criterioJulgamentoId;
    }

    public Boolean getOrcamentoSigiloso() {
        return orcamentoSigiloso;
    }

    public void setOrcamentoSigiloso(Boolean orcamentoSigiloso) {
        this.orcamentoSigiloso = orcamentoSigiloso;
    }

    public Integer getItemCategoriaId() {
        return itemCategoriaId;
    }

    public void setItemCategoriaId(Integer itemCategoriaId) {
        this.itemCategoriaId = itemCategoriaId;
    }

    public String getPatrimonio() {
        return patrimonio;
    }

    public void setPatrimonio(String patrimonio) {
        this.patrimonio = patrimonio;
    }

    public String getCodigoRegistroImobiliario() {
        return codigoRegistroImobiliario;
    }

    public void setCodigoRegistroImobiliario(String codigoRegistroImobiliario) {
        this.codigoRegistroImobiliario = codigoRegistroImobiliario;
    }
}
