package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.OperadoraTecnologiaTransporte;
import br.com.webpublico.enums.TipoRelatorioCertificado;

import java.io.Serializable;
import java.util.Date;

public class FiltroCertificadoAutorizacaoOTT implements Serializable {

    private Date dataInicial;
    private Date dataFinal;
    private OperadoraTecnologiaTransporte operadora;
    private String criteriosUtilizados;
    private Boolean condutoresRegulares;
    private Boolean condutoresIrregulares;
    private Boolean aguardandoAprovacao;
    private TipoRelatorioCertificado tipoRelatorio;
    private Date dataOperacao;

    public FiltroCertificadoAutorizacaoOTT() {
        condutoresRegulares = Boolean.FALSE;
        condutoresIrregulares = Boolean.FALSE;
        aguardandoAprovacao = Boolean.FALSE;
    }

    public Date getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(Date dataInicial) {
        this.dataInicial = dataInicial;
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    public OperadoraTecnologiaTransporte getOperadora() {
        return operadora;
    }

    public void setOperadora(OperadoraTecnologiaTransporte operadora) {
        this.operadora = operadora;
    }

    public String getCriteriosUtilizados() {
        return criteriosUtilizados;
    }

    public void setCriteriosUtilizados(String criteriosUtilizados) {
        this.criteriosUtilizados = criteriosUtilizados;
    }

    public boolean isCondutoresRegulares() {
        return condutoresRegulares;
    }

    public void setCondutoresRegulares(boolean condutoresRegulares) {
        this.condutoresRegulares = condutoresRegulares;
    }

    public boolean isCondutoresIrregulares() {
        return condutoresIrregulares;
    }

    public void setCondutoresIrregulares(boolean condutoresIrregulares) {
        this.condutoresIrregulares = condutoresIrregulares;
    }

    public boolean isAguardandoAprovacao() {
        return aguardandoAprovacao;
    }

    public void setAguardandoAprovacao(boolean aguardandoAprovacao) {
        this.aguardandoAprovacao = aguardandoAprovacao;
    }

    public TipoRelatorioCertificado getTipoRelatorio() {
        return tipoRelatorio;
    }

    public void setTipoRelatorio(TipoRelatorioCertificado tipoRelatorio) {
        this.tipoRelatorio = tipoRelatorio;
    }

    public Date getDataOperacao() {
        return dataOperacao;
    }

    public void setDataOperacao(Date dataOperacao) {
        this.dataOperacao = dataOperacao;
    }
}
