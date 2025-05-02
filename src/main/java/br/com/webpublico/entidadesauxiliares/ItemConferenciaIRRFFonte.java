package br.com.webpublico.entidadesauxiliares;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by Buzatto on 26/11/2015.
 */
public class ItemConferenciaIRRFFonte implements Serializable {

    private String mes;
    private BigDecimal rendimentoInclusoFerias = BigDecimal.ZERO;
    private BigDecimal contribuicaoPrevidenciariaOficial = BigDecimal.ZERO;
    private BigDecimal contribuicaoPrevidenciaPrivada = BigDecimal.ZERO;
    private BigDecimal pensaoAlimenticia = BigDecimal.ZERO;
    private BigDecimal impostoRetidoFonte = BigDecimal.ZERO;
    private BigDecimal proventosAposentadoria = BigDecimal.ZERO;
    private BigDecimal decimoTerceiroSalario = BigDecimal.ZERO;
    private BigDecimal irrfDecimoTerceiroSalario = BigDecimal.ZERO;
    private BigDecimal salarioFamilia = BigDecimal.ZERO;
    private BigDecimal despesasMedicas = BigDecimal.ZERO;
    private BigDecimal diariaAjudaCusto = BigDecimal.ZERO;
    private BigDecimal isencaoIRRFLei = BigDecimal.ZERO;
    private BigDecimal pensaoJudicial13Salario = BigDecimal.ZERO;

    public ItemConferenciaIRRFFonte() {
    }

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }

    public BigDecimal getRendimentoInclusoFerias() {
        return rendimentoInclusoFerias;
    }

    public void setRendimentoInclusoFerias(BigDecimal rendimentoInclusoFerias) {
        this.rendimentoInclusoFerias = rendimentoInclusoFerias;
    }

    public BigDecimal getContribuicaoPrevidenciariaOficial() {
        return contribuicaoPrevidenciariaOficial;
    }

    public void setContribuicaoPrevidenciariaOficial(BigDecimal contribuicaoPrevidenciariaOficial) {
        this.contribuicaoPrevidenciariaOficial = contribuicaoPrevidenciariaOficial;
    }

    public BigDecimal getContribuicaoPrevidenciaPrivada() {
        return contribuicaoPrevidenciaPrivada;
    }

    public void setContribuicaoPrevidenciaPrivada(BigDecimal contribuicaoPrevidenciaPrivada) {
        this.contribuicaoPrevidenciaPrivada = contribuicaoPrevidenciaPrivada;
    }

    public BigDecimal getPensaoJudicial13Salario() {
        return pensaoJudicial13Salario;
    }

    public void setPensaoJudicial13Salario(BigDecimal pensaoJudicial13Salario) {
        this.pensaoJudicial13Salario = pensaoJudicial13Salario;
    }

    public BigDecimal getPensaoAlimenticia() {
        return pensaoAlimenticia;
    }

    public void setPensaoAlimenticia(BigDecimal pensaoAlimenticia) {
        this.pensaoAlimenticia = pensaoAlimenticia;
    }

    public BigDecimal getImpostoRetidoFonte() {
        return impostoRetidoFonte;
    }

    public void setImpostoRetidoFonte(BigDecimal impostoRetidoFonte) {
        this.impostoRetidoFonte = impostoRetidoFonte;
    }

    public BigDecimal getProventosAposentadoria() {
        return proventosAposentadoria;
    }

    public void setProventosAposentadoria(BigDecimal proventosAposentadoria) {
        this.proventosAposentadoria = proventosAposentadoria;
    }

    public BigDecimal getDecimoTerceiroSalario() {
        return decimoTerceiroSalario;
    }

    public void setDecimoTerceiroSalario(BigDecimal decimoTerceiroSalario) {
        this.decimoTerceiroSalario = decimoTerceiroSalario;
    }

    public BigDecimal getIrrfDecimoTerceiroSalario() {
        return irrfDecimoTerceiroSalario;
    }

    public void setIrrfDecimoTerceiroSalario(BigDecimal irrfDecimoTerceiroSalario) {
        this.irrfDecimoTerceiroSalario = irrfDecimoTerceiroSalario;
    }

    public BigDecimal getSalarioFamilia() {
        return salarioFamilia;
    }

    public void setSalarioFamilia(BigDecimal salarioFamilia) {
        this.salarioFamilia = salarioFamilia;
    }

    public BigDecimal getDespesasMedicas() {
        return despesasMedicas;
    }

    public void setDespesasMedicas(BigDecimal despesasMedicas) {
        this.despesasMedicas = despesasMedicas;
    }

    public BigDecimal getDiariaAjudaCusto() {
        return diariaAjudaCusto;
    }

    public void setDiariaAjudaCusto(BigDecimal diariaAjudaCusto) {
        this.diariaAjudaCusto = diariaAjudaCusto;
    }

    public BigDecimal getIsencaoIRRFLei() {
        return isencaoIRRFLei;
    }

    public void setIsencaoIRRFLei(BigDecimal isencaoIRRFLei) {
        this.isencaoIRRFLei = isencaoIRRFLei;
    }
}
