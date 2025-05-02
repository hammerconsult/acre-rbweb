package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.enums.TipoCadastroTributario;

/**
 * Created by mga on 03/07/2017.
 */
public class AssistenteGeracaoRelatorio {

    private String usuario;
    private String filtros;
    private String labeInscricao;
    private String caminhoReport;
    private String caminhoImagem;
    private String arquivoJasper;
    private String nomeRelatorio;
    private TipoCadastroTributario tipoCadastroTributario;

    public AssistenteGeracaoRelatorio() {
    }

    public String getLabeInscricao() {
        return labeInscricao;
    }

    public void setLabeInscricao(String labeInscricao) {
        this.labeInscricao = labeInscricao;
    }

    public TipoCadastroTributario getTipoCadastroTributario() {
        return tipoCadastroTributario;
    }

    public void setTipoCadastroTributario(TipoCadastroTributario tipoCadastroTributario) {
        this.tipoCadastroTributario = tipoCadastroTributario;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getFiltros() {
        return filtros;
    }

    public void setFiltros(String filtros) {
        this.filtros = filtros;
    }

    public String getCaminhoReport() {
        return caminhoReport;
    }

    public void setCaminhoReport(String caminhoReport) {
        this.caminhoReport = caminhoReport;
    }

    public String getCaminhoImagem() {
        return caminhoImagem;
    }

    public void setCaminhoImagem(String caminhoImagem) {
        this.caminhoImagem = caminhoImagem;
    }

    public String getArquivoJasper() {
        return arquivoJasper;
    }

    public void setArquivoJasper(String arquivoJasper) {
        this.arquivoJasper = arquivoJasper;
    }

    public String getNomeRelatorio() {
        return nomeRelatorio;
    }

    public void setNomeRelatorio(String nomeRelatorio) {
        this.nomeRelatorio = nomeRelatorio;
    }
}
