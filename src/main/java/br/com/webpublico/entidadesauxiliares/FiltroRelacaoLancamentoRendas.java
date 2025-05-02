package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.enums.SituacaoParcela;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 03/08/15
 * Time: 16:46
 * To change this template use File | Settings | File Templates.
 */
public class FiltroRelacaoLancamentoRendas extends AbstractFiltroRelacaoLancamentoDebito {

    private String numeroContratoInicial;
    private String numeroContratoFinal;
    private Long localizacaoInicial;
    private Long localizacaoFinal;
    private String numeroBoxInicial;
    private String numeroBoxFinal;

    public FiltroRelacaoLancamentoRendas() {
        setSituacoes(new SituacaoParcela[]{SituacaoParcela.EM_ABERTO});
        setTipoRelatorio(TipoRelatorio.RESUMIDO);
        setSomenteTotalizador(Boolean.FALSE);
    }

    public String getNumeroContratoInicial() {
        return numeroContratoInicial;
    }

    public void setNumeroContratoInicial(String numeroContratoInicial) {
        this.numeroContratoInicial = numeroContratoInicial;
    }

    public String getNumeroContratoFinal() {
        return numeroContratoFinal;
    }

    public void setNumeroContratoFinal(String numeroContratoFinal) {
        this.numeroContratoFinal = numeroContratoFinal;
    }

    public Long getLocalizacaoInicial() {
        return localizacaoInicial;
    }

    public void setLocalizacaoInicial(Long localizacaoInicial) {
        this.localizacaoInicial = localizacaoInicial;
    }

    public Long getLocalizacaoFinal() {
        return localizacaoFinal;
    }

    public void setLocalizacaoFinal(Long localizacaoFinal) {
        this.localizacaoFinal = localizacaoFinal;
    }

    public String getNumeroBoxInicial() {
        return numeroBoxInicial;
    }

    public void setNumeroBoxInicial(String numeroBoxInicial) {
        this.numeroBoxInicial = numeroBoxInicial;
    }

    public String getNumeroBoxFinal() {
        return numeroBoxFinal;
    }

    public void setNumeroBoxFinal(String numeroBoxFinal) {
        this.numeroBoxFinal = numeroBoxFinal;
    }
}
