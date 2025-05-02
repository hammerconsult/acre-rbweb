package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.Bairro;
import br.com.webpublico.entidades.Logradouro;
import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.enums.SituacaoITBI;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.enums.TipoITBI;
import br.com.webpublico.enums.TipoImovel;

import java.math.BigDecimal;

/**
 * User: Fabio
 * Date: 13/06/18
 */
public class FiltroRelacaoLancamentoITBIImovel extends AbstractFiltroRelacaoLancamentoDebito {

    private String inscricaoInicial;
    private String inscricaoFinal;
    private BigDecimal valorVenalImovelInicial;
    private BigDecimal valorVenalImovelFinal;
    private BigDecimal valorVenalTerrenoInicial;
    private BigDecimal valorVenalTerrenoFinal;
    private TipoImovel tipoImovel;
    private String setorInicial;
    private String setorFinal;
    private String quadraInicial;
    private String quadraFinal;
    private Bairro bairro;
    private Logradouro logradouro;
    private Pessoa proprietario;
    private BigDecimal baseCalculoInicial;
    private BigDecimal baseCalculoFinal;
    private TipoITBI tipoITBI;
    private Integer numeroLaudoInicial;
    private Integer numeroLaudoFinal;
    private Pessoa adquirente;
    private Pessoa transmitente;
    private SituacaoITBI situacaoItbi;
    private BigDecimal valorItbiInicial;
    private BigDecimal valorItbiFinal;

    public FiltroRelacaoLancamentoITBIImovel() {
        setSituacoes(new SituacaoParcela[]{SituacaoParcela.EM_ABERTO});
        setTipoRelatorio(TipoRelatorio.RESUMIDO);
        setSomenteTotalizador(Boolean.FALSE);
        setValorVenalImovelInicial(null);
        setValorVenalImovelFinal(null);
        setValorVenalTerrenoInicial(null);
        setValorVenalTerrenoFinal(null);
        setValorItbiInicial(null);
        setValorItbiFinal(null);
        setBaseCalculoInicial(null);
        setBaseCalculoFinal(null);
        setTipoITBI(null);
        setSituacaoItbi(null);
    }

    public TipoImovel getTipoImovel() {
        return tipoImovel;
    }

    public void setTipoImovel(TipoImovel tipoImovel) {
        this.tipoImovel = tipoImovel;
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

    public String getSetorInicial() {
        return setorInicial;
    }

    public void setSetorInicial(String setorInicial) {
        this.setorInicial = setorInicial;
    }

    public String getSetorFinal() {
        return setorFinal;
    }

    public void setSetorFinal(String setorFinal) {
        this.setorFinal = setorFinal;
    }

    public String getQuadraInicial() {
        return quadraInicial;
    }

    public void setQuadraInicial(String quadraInicial) {
        this.quadraInicial = quadraInicial;
    }

    public String getQuadraFinal() {
        return quadraFinal;
    }

    public void setQuadraFinal(String quadraFinal) {
        this.quadraFinal = quadraFinal;
    }

    public Bairro getBairro() {
        return bairro;
    }

    public void setBairro(Bairro bairro) {
        this.bairro = bairro;
    }

    public Logradouro getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(Logradouro logradouro) {
        this.logradouro = logradouro;
    }

    public BigDecimal getValorVenalImovelInicial() {
        return valorVenalImovelInicial;
    }

    public void setValorVenalImovelInicial(BigDecimal valorVenalImovelInicial) {
        this.valorVenalImovelInicial = valorVenalImovelInicial;
    }

    public BigDecimal getValorVenalImovelFinal() {
        return valorVenalImovelFinal;
    }

    public void setValorVenalImovelFinal(BigDecimal valorVenalImovelFinal) {
        this.valorVenalImovelFinal = valorVenalImovelFinal;
    }

    public BigDecimal getValorVenalTerrenoInicial() {
        return valorVenalTerrenoInicial;
    }

    public void setValorVenalTerrenoInicial(BigDecimal valorVenalTerrenoInicial) {
        this.valorVenalTerrenoInicial = valorVenalTerrenoInicial;
    }

    public BigDecimal getValorVenalTerrenoFinal() {
        return valorVenalTerrenoFinal;
    }

    public void setValorVenalTerrenoFinal(BigDecimal valorVenalTerrenoFinal) {
        this.valorVenalTerrenoFinal = valorVenalTerrenoFinal;
    }

    public Pessoa getProprietario() {
        return proprietario;
    }

    public void setProprietario(Pessoa proprietario) {
        this.proprietario = proprietario;
    }

    public BigDecimal getBaseCalculoInicial() {
        return baseCalculoInicial;
    }

    public void setBaseCalculoInicial(BigDecimal baseCalculoInicial) {
        this.baseCalculoInicial = baseCalculoInicial;
    }

    public BigDecimal getBaseCalculoFinal() {
        return baseCalculoFinal;
    }

    public void setBaseCalculoFinal(BigDecimal baseCalculoFinal) {
        this.baseCalculoFinal = baseCalculoFinal;
    }

    public TipoITBI getTipoITBI() {
        return tipoITBI;
    }

    public void setTipoITBI(TipoITBI tipoITBI) {
        this.tipoITBI = tipoITBI;
    }

    public Pessoa getAdquirente() {
        return adquirente;
    }

    public void setAdquirente(Pessoa adquirente) {
        this.adquirente = adquirente;
    }

    public Pessoa getTransmitente() {
        return transmitente;
    }

    public void setTransmitente(Pessoa transmitente) {
        this.transmitente = transmitente;
    }

    public SituacaoITBI getSituacaoItbi() {
        return situacaoItbi;
    }

    public void setSituacaoItbi(SituacaoITBI situacaoItbi) {
        this.situacaoItbi = situacaoItbi;
    }

    public BigDecimal getValorItbiInicial() {
        return valorItbiInicial;
    }

    public void setValorItbiInicial(BigDecimal valorItbiInicial) {
        this.valorItbiInicial = valorItbiInicial;
    }

    public BigDecimal getValorItbiFinal() {
        return valorItbiFinal;
    }

    public void setValorItbiFinal(BigDecimal valorItbiFinal) {
        this.valorItbiFinal = valorItbiFinal;
    }

    public Integer getNumeroLaudoInicial() {
        return numeroLaudoInicial;
    }

    public void setNumeroLaudoInicial(Integer numeroLaudoInicial) {
        this.numeroLaudoInicial = numeroLaudoInicial;
    }

    public Integer getNumeroLaudoFinal() {
        return numeroLaudoFinal;
    }

    public void setNumeroLaudoFinal(Integer numeroLaudoFinal) {
        this.numeroLaudoFinal = numeroLaudoFinal;
    }
}
