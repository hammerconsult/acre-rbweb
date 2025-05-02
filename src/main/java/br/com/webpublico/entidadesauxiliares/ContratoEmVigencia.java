package br.com.webpublico.entidadesauxiliares;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class ContratoEmVigencia implements Comparable<ContratoEmVigencia> {

    private Long idContrato;
    private Long idProcesso;
    private String numeroProcesso;
    private String numeroContrato;
    private String numeroTermo;
    private String contratado;
    private String unidadeAdministrativa;
    private String objeto;
    private Integer exercicio;
    private BigDecimal valorTotal;
    public Long criadoEm;
    private List<ItemContratoEmVigencia> itens;

    public ContratoEmVigencia() {
        criadoEm = System.nanoTime();
        itens = Lists.newArrayList();
    }

    public Long getIdProcesso() {
        return idProcesso;
    }

    public void setIdProcesso(Long idProcesso) {
        this.idProcesso = idProcesso;
    }

    public String getNumeroProcesso() {
        return numeroProcesso;
    }

    public void setNumeroProcesso(String numeroProcesso) {
        this.numeroProcesso = numeroProcesso;
    }

    public Long getIdContrato() {
        return idContrato;
    }

    public void setIdContrato(Long idContrato) {
        this.idContrato = idContrato;
    }

    public List<ItemContratoEmVigencia> getItens() {
        return itens;
    }

    public void setItens(List<ItemContratoEmVigencia> itens) {
        this.itens = itens;
    }

    public String getNumeroContrato() {
        return numeroContrato;
    }

    public void setNumeroContrato(String numeroContrato) {
        this.numeroContrato = numeroContrato;
    }

    public String getNumeroTermo() {
        return numeroTermo;
    }

    public void setNumeroTermo(String numeroTermo) {
        this.numeroTermo = numeroTermo;
    }

    public String getContratado() {
        return contratado;
    }

    public void setContratado(String contratado) {
        this.contratado = contratado;
    }

    public String getUnidadeAdministrativa() {
        return unidadeAdministrativa;
    }

    public void setUnidadeAdministrativa(String unidadeAdministrativa) {
        this.unidadeAdministrativa = unidadeAdministrativa;
    }

    public String getObjeto() {
        return objeto;
    }

    public void setObjeto(String objeto) {
        this.objeto = objeto;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Integer getExercicio() {
        return exercicio;
    }

    public void setExercicio(Integer exercicio) {
        this.exercicio = exercicio;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContratoEmVigencia that = (ContratoEmVigencia) o;
        return Objects.equals(criadoEm, that.criadoEm);
    }

    @Override
    public int hashCode() {
        return Objects.hash(criadoEm);
    }

    @Override
    public int compareTo(ContratoEmVigencia o) {
        return ComparisonChain.start()
            .compare(getExercicio(), o.getExercicio())
            .compare(Long.valueOf(getNumeroContrato()), Long.valueOf(o.getNumeroContrato())).result();
    }
}
