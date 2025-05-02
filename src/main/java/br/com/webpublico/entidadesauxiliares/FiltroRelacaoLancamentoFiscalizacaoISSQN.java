package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.enums.SituacaoParcela;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 02/05/17
 * Time: 11:26
 * To change this template use File | Settings | File Templates.
 */
public class FiltroRelacaoLancamentoFiscalizacaoISSQN extends AbstractFiltroRelacaoLancamentoDebito {


    private String inscricaoInicial;
    private String inscricaoFinal;
    private Integer numeroProgramacaoInicial;
    private Integer numeroProgramacaoFinal;
    private Integer numeroOrdemServicoInicial;
    private Integer numeroOrdemServicoFinal;
    private Integer numeroAutoInfracaoInicial;
    private Integer numeroAutoInfracaoFinal;


    public FiltroRelacaoLancamentoFiscalizacaoISSQN() {
        setSituacoes(new SituacaoParcela[]{SituacaoParcela.EM_ABERTO});
        setTipoRelatorio(TipoRelatorio.RESUMIDO);
        setSomenteTotalizador(Boolean.FALSE);
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

    public Integer getNumeroProgramacaoInicial() {
        return numeroProgramacaoInicial;
    }

    public void setNumeroProgramacaoInicial(Integer numeroProgramacaoInicial) {
        this.numeroProgramacaoInicial = numeroProgramacaoInicial;
    }

    public Integer getNumeroProgramacaoFinal() {
        return numeroProgramacaoFinal;
    }

    public void setNumeroProgramacaoFinal(Integer numeroProgramacaoFinal) {
        this.numeroProgramacaoFinal = numeroProgramacaoFinal;
    }

    public Integer getNumeroOrdemServicoInicial() {
        return numeroOrdemServicoInicial;
    }

    public void setNumeroOrdemServicoInicial(Integer numeroOrdemServicoInicial) {
        this.numeroOrdemServicoInicial = numeroOrdemServicoInicial;
    }

    public Integer getNumeroOrdemServicoFinal() {
        return numeroOrdemServicoFinal;
    }

    public void setNumeroOrdemServicoFinal(Integer numeroOrdemServicoFinal) {
        this.numeroOrdemServicoFinal = numeroOrdemServicoFinal;
    }

    public Integer getNumeroAutoInfracaoInicial() {
        return numeroAutoInfracaoInicial;
    }

    public void setNumeroAutoInfracaoInicial(Integer numeroAutoInfracaoInicial) {
        this.numeroAutoInfracaoInicial = numeroAutoInfracaoInicial;
    }

    public Integer getNumeroAutoInfracaoFinal() {
        return numeroAutoInfracaoFinal;
    }

    public void setNumeroAutoInfracaoFinal(Integer numeroAutoInfracaoFinal) {
        this.numeroAutoInfracaoFinal = numeroAutoInfracaoFinal;
    }
}
