package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;

/**
 * Created by Desenvolvimento on 06/09/2016.
 */
public class DetalhesRelatorioRecolhimentoSefip implements Comparable<DetalhesRelatorioRecolhimentoSefip> {

    private String orgao;
    private String matricula;
    private String contrato;
    private String nome;

    private BigDecimal base;

    private BigDecimal porcentagemSegurado;
    private BigDecimal valorSegurado;

    private BigDecimal porcentagemPatronal;
    private BigDecimal valorPatronal;

    private BigDecimal salarioMaternidade;
    private BigDecimal salarioFamilia;

    private Boolean agrupado;

    public DetalhesRelatorioRecolhimentoSefip() {
        salarioMaternidade = BigDecimal.ZERO;
        salarioFamilia = BigDecimal.ZERO;
        agrupado = Boolean.FALSE;
    }

    public String getOrgao() {
        return orgao;
    }

    public void setOrgao(String orgao) {
        this.orgao = orgao;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getContrato() {
        return contrato;
    }

    public void setContrato(String contrato) {
        this.contrato = contrato;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public BigDecimal getBase() {
        return base;
    }

    public void setBase(BigDecimal base) {
        this.base = base;
    }

    public BigDecimal getPorcentagemSegurado() {
        return porcentagemSegurado;
    }

    public void setPorcentagemSegurado(BigDecimal porcentagemSegurado) {
        this.porcentagemSegurado = porcentagemSegurado;
    }

    public BigDecimal getValorSegurado() {
        return valorSegurado;
    }

    public void setValorSegurado(BigDecimal valorSegurado) {
        this.valorSegurado = valorSegurado;
    }

    public BigDecimal getPorcentagemPatronal() {
        return porcentagemPatronal;
    }

    public void setPorcentagemPatronal(BigDecimal porcentagemPatronal) {
        this.porcentagemPatronal = porcentagemPatronal;
    }

    public BigDecimal getValorPatronal() {
        return valorPatronal;
    }

    public void setValorPatronal(BigDecimal valorPatronal) {
        this.valorPatronal = valorPatronal;
    }

    public BigDecimal getSalarioMaternidade() {
        return salarioMaternidade;
    }

    public void setSalarioMaternidade(BigDecimal salarioMaternidade) {
        this.salarioMaternidade = salarioMaternidade;
    }

    public BigDecimal getSalarioFamilia() {
        return salarioFamilia;
    }

    public void setSalarioFamilia(BigDecimal salarioFamilia) {
        this.salarioFamilia = salarioFamilia;
    }

    public Boolean getAgrupado() {
        return agrupado;
    }

    public void setAgrupado(Boolean agrupado) {
        this.agrupado = agrupado;
    }

    @Override
    public int compareTo(DetalhesRelatorioRecolhimentoSefip o) {
        int comparacao = 0;
        if (agrupado) {
            comparacao = this.orgao.compareTo(o.getOrgao());
            if (comparacao == 0) {
                comparacao = this.nome.compareTo(o.getNome());
            }
            return comparacao;
        }
        comparacao = this.nome.compareTo(o.getNome());
        return comparacao;
    }
}
