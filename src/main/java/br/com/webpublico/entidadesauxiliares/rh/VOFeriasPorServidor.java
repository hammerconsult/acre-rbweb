package br.com.webpublico.entidadesauxiliares.rh;

import java.math.BigDecimal;

/**
 * Created by israeleriston on 16/12/15.
 */
public class VOFeriasPorServidor {
    private String nomeServidor;
    private String matriculaServidor;
    private String contratoServidor;
    private String inicioPeriodoAquisitivo;
    private String finalPeriodoAquisitivo;
    private String admissaoFerias;
    private String programacaoInicio;
    private String programacaoFim;
    private String gozoInicial;
    private String gozoFinal;
    private BigDecimal dias;
    private BigDecimal diasAbono;
    private BigDecimal valorAdiantamento13Salario;
    private String orgao;

    public VOFeriasPorServidor() {
    }

    public String getNomeServidor() {
        return nomeServidor;
    }

    public void setNomeServidor(String nomeServidor) {
        this.nomeServidor = nomeServidor;
    }

    public String getMatriculaServidor() {
        return matriculaServidor;
    }

    public void setMatriculaServidor(String matriculaServidor) {
        this.matriculaServidor = matriculaServidor;
    }

    public String getContratoServidor() {
        return contratoServidor;
    }

    public void setContratoServidor(String contratoServidor) {
        this.contratoServidor = contratoServidor;
    }

    public String getInicioPeriodoAquisitivo() {
        return inicioPeriodoAquisitivo;
    }

    public void setInicioPeriodoAquisitivo(String inicioPeriodoAquisitivo) {
        this.inicioPeriodoAquisitivo = inicioPeriodoAquisitivo;
    }

    public String getFinalPeriodoAquisitivo() {
        return finalPeriodoAquisitivo;
    }

    public void setFinalPeriodoAquisitivo(String finalPeriodoAquisitivo) {
        this.finalPeriodoAquisitivo = finalPeriodoAquisitivo;
    }

    public String getAdmissaoFerias() {
        return admissaoFerias;
    }

    public void setAdmissaoFerias(String admissaoFerias) {
        this.admissaoFerias = admissaoFerias;
    }

    public String getProgramacaoInicio() {
        return programacaoInicio;
    }

    public void setProgramacaoInicio(String programacaoInicio) {
        this.programacaoInicio = programacaoInicio;
    }

    public String getProgramacaoFim() {
        return programacaoFim;
    }

    public void setProgramacaoFim(String programacaoFim) {
        this.programacaoFim = programacaoFim;
    }

    public String getGozoInicial() {
        return gozoInicial;
    }

    public void setGozoInicial(String gozoInicial) {
        this.gozoInicial = gozoInicial;
    }

    public String getGozoFinal() {
        return gozoFinal;
    }

    public void setGozoFinal(String gozoFinal) {
        this.gozoFinal = gozoFinal;
    }

    public BigDecimal getDias() {
        return dias;
    }

    public void setDias(BigDecimal dias) {
        this.dias = dias;
    }

    public BigDecimal getDiasAbono() {
        return diasAbono;
    }

    public void setDiasAbono(BigDecimal diasAbono) {
        this.diasAbono = diasAbono;
    }

    public BigDecimal getValorAdiantamento13Salario() {
        return valorAdiantamento13Salario;
    }

    public void setValorAdiantamento13Salario(BigDecimal valorAdiantamento13Salario) {
        this.valorAdiantamento13Salario = valorAdiantamento13Salario;
    }

    public String getOrgao() {
        return orgao;
    }

    public void setOrgao(String orgao) {
        this.orgao = orgao;
    }
}
