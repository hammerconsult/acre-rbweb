package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;

/**
 * Created by Fabio on 10/04/2018.
 */
public class VOItemDam implements Comparable<VOItemDam> {

    private Long id;
    private Long idDam;
    private Long idParcela;
    private BigDecimal valorOriginalDevido;
    private BigDecimal juros;
    private BigDecimal multa;
    private BigDecimal correcaoMonetaria;
    private BigDecimal honorarios;
    private BigDecimal desconto;
    private BigDecimal outrosAcrescimos;
    private Long idExercicio;
    private Integer exercicio;
    private Boolean dividaAtiva;
    private Long idEntidade;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdDam() {
        return idDam;
    }

    public void setIdDam(Long idDam) {
        this.idDam = idDam;
    }

    public Long getIdParcela() {
        return idParcela;
    }

    public void setIdParcela(Long idParcela) {
        this.idParcela = idParcela;
    }

    public BigDecimal getValorOriginalDevido() {
        return valorOriginalDevido != null ? valorOriginalDevido : BigDecimal.ZERO;
    }

    public void setValorOriginalDevido(BigDecimal valorOriginalDevido) {
        this.valorOriginalDevido = valorOriginalDevido;
    }

    public BigDecimal getJuros() {
        return juros != null ? juros : BigDecimal.ZERO;
    }

    public void setJuros(BigDecimal juros) {
        this.juros = juros;
    }

    public BigDecimal getMulta() {
        return multa != null ? multa : BigDecimal.ZERO;
    }

    public void setMulta(BigDecimal multa) {
        this.multa = multa;
    }

    public BigDecimal getCorrecaoMonetaria() {
        return correcaoMonetaria != null ? correcaoMonetaria : BigDecimal.ZERO;
    }

    public void setCorrecaoMonetaria(BigDecimal correcaoMonetaria) {
        this.correcaoMonetaria = correcaoMonetaria;
    }

    public BigDecimal getHonorarios() {
        return honorarios != null ? honorarios : BigDecimal.ZERO;
    }

    public void setHonorarios(BigDecimal honorarios) {
        this.honorarios = honorarios;
    }

    public BigDecimal getDesconto() {
        return desconto != null ? desconto : BigDecimal.ZERO;
    }

    public void setDesconto(BigDecimal desconto) {
        this.desconto = desconto;
    }

    public BigDecimal getOutrosAcrescimos() {
        return outrosAcrescimos != null ? outrosAcrescimos : BigDecimal.ZERO;
    }

    public void setOutrosAcrescimos(BigDecimal outrosAcrescimos) {
        this.outrosAcrescimos = outrosAcrescimos;
    }

    public Integer getExercicio() {
        return exercicio;
    }

    public void setExercicio(Integer exercicio) {
        this.exercicio = exercicio;
    }

    public Boolean getDividaAtiva() {
        return dividaAtiva != null ? dividaAtiva : Boolean.FALSE;
    }

    public void setDividaAtiva(Boolean dividaAtiva) {
        this.dividaAtiva = dividaAtiva;
    }

    public Long getIdEntidade() {
        return idEntidade;
    }

    public void setIdEntidade(Long idEntidade) {
        this.idEntidade = idEntidade;
    }

    public Long getIdExercicio() {
        return idExercicio;
    }

    public void setIdExercicio(Long idExercicio) {
        this.idExercicio = idExercicio;
    }

    public BigDecimal getTotalAcrescimosSemHonorarios() {
        return (getJuros().add(getMulta().add(getCorrecaoMonetaria())));
    }

    public BigDecimal getValorTotal() {
        return (getValorOriginalDevido().add(getJuros().add(getMulta().add(getOutrosAcrescimos()).add(getCorrecaoMonetaria()).add(getHonorarios())))).subtract(getDesconto());
    }

    @Override
    public int compareTo(VOItemDam o) {
        return this.getId().compareTo(o.getId());
    }
}
