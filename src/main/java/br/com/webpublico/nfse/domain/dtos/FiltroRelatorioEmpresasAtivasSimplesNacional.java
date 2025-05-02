package br.com.webpublico.nfse.domain.dtos;

import org.apache.logging.log4j.util.Strings;

public class FiltroRelatorioEmpresasAtivasSimplesNacional {

    private String inscricaoInicial;
    private String inscricaoFinal;
    private String cnpjInicial;
    private String cnpjFinal;

    public FiltroRelatorioEmpresasAtivasSimplesNacional() {
    }

    public String montarDescricaoFiltros() {
        StringBuilder retorno = new StringBuilder();
        if (inscricaoInicial != null) {
            retorno.append(" Inscrição Municipal Inicial: ").append(inscricaoInicial).append("; ");
        }
        if (inscricaoFinal != null) {
            retorno.append(" Inscrição Municipal Final: ").append(inscricaoFinal).append("; ");
        }
        if (Strings.isNotEmpty(cnpjInicial)) {
            retorno.append(" CNPJ Inicial: ").append(cnpjInicial).append("; ");
        }
        if (Strings.isNotEmpty(cnpjFinal)) {
            retorno.append(" CNPJ Final: ").append(cnpjFinal).append("; ");
        }
        return retorno.toString();
    }

    public String getInscricaoInicial() {
        return inscricaoInicial;
    }

    public void setInscricaoInicial(String inscricaoInicial) {
        this.inscricaoInicial = inscricaoInicial;
    }

    public String getInscricaoFinal() {
        return inscricaoFinal;
    }

    public void setInscricaoFinal(String inscricaoFinal) {
        this.inscricaoFinal = inscricaoFinal;
    }

    public String getCnpjInicial() {
        return cnpjInicial;
    }

    public void setCnpjInicial(String cnpjInicial) {
        this.cnpjInicial = cnpjInicial;
    }

    public String getCnpjFinal() {
        return cnpjFinal;
    }

    public void setCnpjFinal(String cnpjFinal) {
        this.cnpjFinal = cnpjFinal;
    }
}
