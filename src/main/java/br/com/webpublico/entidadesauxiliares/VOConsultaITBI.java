package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.Calculo;
import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 25/06/15
 * Time: 16:48
 * To change this template use File | Settings | File Templates.
 */

public class VOConsultaITBI extends AbstractVOConsultaLancamento {

    private String tipoImovel;
    private String setor;
    private String quadra;
    private String lote;
    private String bairro;
    private String logradouro;
    private String numero;
    private String complemento;
    private String cep;
    private String proprietario;
    private String transmitente;
    private String adquirente;
    private BigDecimal valorVenal;
    private BigDecimal baseCalculo;
    private String numeroLaudo;

    public VOConsultaITBI(Object[] resultado) {
        this.preencher(resultado);
    }

    public void preencher(Object[] resultado) {
        this.setResultadoParcela(new ResultadoParcela());
        this.getResultadoParcela().setIdCadastro(((BigDecimal) resultado[0]).longValue());
        this.getResultadoParcela().setCadastro(resultado[1] != null ? (String) resultado[1] : "");
        this.getResultadoParcela().setExercicio(((BigDecimal) resultado[2]).intValue());
        this.getResultadoParcela().setIdPessoa(((BigDecimal) resultado[3]).longValue());
        this.getResultadoParcela().setIdCalculo(((BigDecimal) resultado[4]).longValue());
        this.getResultadoParcela().setSd(((BigDecimal) resultado[5]).intValue());
        this.getResultadoParcela().setIdValorDivida(((BigDecimal) resultado[6]).longValue());
        this.getResultadoParcela().setEmissao((Date) resultado[7]);
        this.getResultadoParcela().setIdParcela(((BigDecimal) resultado[8]).longValue());
        this.getResultadoParcela().setIdOpcaoPagamento(((BigDecimal) resultado[9]).longValue());
        this.getResultadoParcela().setCotaUnica(((BigDecimal) resultado[10]).compareTo(BigDecimal.ZERO) > 0);
        this.getResultadoParcela().setVencimento((Date) resultado[11]);
        this.getResultadoParcela().setSituacao(resultado[12] != null ? (String) resultado[12] : "");
        this.getResultadoParcela().setReferencia(resultado[13] != null ? (String) resultado[13] : "");
        this.getResultadoParcela().setParcela(resultado[14] != null ? (String) resultado[14] : "");
        this.getResultadoParcela().setValorOriginal((BigDecimal) resultado[15]);
        this.getResultadoParcela().setDivida((String) resultado[16]);
        this.getResultadoParcela().setIdDivida(((BigDecimal) resultado[17]).longValue());
        this.getResultadoParcela().setDividaAtiva(((BigDecimal) resultado[18]).compareTo(BigDecimal.ZERO) > 0);
        this.getResultadoParcela().setDividaAtivaAjuizada(((BigDecimal) resultado[19]).compareTo(BigDecimal.ZERO) > 0);
        this.getResultadoParcela().setTipoCalculo(Calculo.TipoCalculo.IPTU.name());
        this.getResultadoParcela().setTipoCadastro(TipoCadastroTributario.IMOBILIARIO.name());
        this.setCpfOrCnpj(resultado[20] != null ? (String) resultado[20] : "");
        this.setNomeOrRazaoSocial(resultado[21] != null ? (String) resultado[21] : "");
        this.tipoImovel = resultado[22] != null ? (String) resultado[22] : "";
        this.setor = resultado[23] != null ? (String) resultado[23] : "";
        this.quadra = resultado[24] != null ? (String) resultado[24] : "";
        this.lote = resultado[25] != null ? (String) resultado[25] : "";
        this.bairro = resultado[26] != null ? (String) resultado[26] : "";
        this.logradouro = resultado[27] != null ? (String) resultado[27] : "";
        this.numero = resultado[28] != null ? (String) resultado[28] : "";
        this.complemento = resultado[29] != null ? (String) resultado[29] : "";
        this.cep = resultado[30] != null ? (String) resultado[30] : "";
        this.getResultadoParcela().setIdConfiguracaoAcrescimo(((BigDecimal) resultado[31]).longValue());

        String nomeProprietario = resultado[43] != null ? (String) resultado[43] : "";
        String cpfCnpjProprietario = resultado[44] != null ? (String) resultado[44] : "";
        this.proprietario = cpfCnpjProprietario + " " + nomeProprietario;

        String nomeAdquirente = resultado[46] != null ? (String) resultado[46] : "";
        String cpfCnpjAdquirente = resultado[47] != null ? (String) resultado[47] : "";
        this.adquirente = cpfCnpjAdquirente + " " + nomeAdquirente;

        String nomeTransmitente = resultado[49] != null ? (String) resultado[49] : "";
        String cpfCnpjTransmitente = resultado[50] != null ? (String) resultado[50] : "";
        this.transmitente = cpfCnpjTransmitente + " " + nomeTransmitente;

        this.valorVenal = resultado[51] != null ? (BigDecimal) resultado[51] : BigDecimal.ZERO;
        this.baseCalculo = resultado[40] != null ? (BigDecimal) resultado[40] : BigDecimal.ZERO;;
        this.numeroLaudo = resultado[38] != null ? ((BigDecimal) resultado[38]) + "" : "";
    }

    public String getTipoImovel() {
        return tipoImovel;
    }

    public void setTipoImovel(String tipoImovel) {
        this.tipoImovel = tipoImovel;
    }

    public String getSetor() {
        return setor;
    }

    public void setSetor(String setor) {
        this.setor = setor;
    }

    public String getQuadra() {
        return quadra;
    }

    public void setQuadra(String quadra) {
        this.quadra = quadra;
    }

    public String getLote() {
        return lote;
    }

    public void setLote(String lote) {
        this.lote = lote;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getProprietario() {
        return proprietario;
    }

    public void setProprietario(String proprietario) {
        this.proprietario = proprietario;
    }

    public String getTransmitente() {
        return transmitente;
    }

    public void setTransmitente(String transmitente) {
        this.transmitente = transmitente;
    }

    public String getAdquirente() {
        return adquirente;
    }

    public void setAdquirente(String adquirente) {
        this.adquirente = adquirente;
    }

    public BigDecimal getValorVenal() {
        return valorVenal;
    }

    public void setValorVenal(BigDecimal valorVenal) {
        this.valorVenal = valorVenal;
    }

    public BigDecimal getBaseCalculo() {
        return baseCalculo;
    }

    public void setBaseCalculo(BigDecimal baseCalculo) {
        this.baseCalculo = baseCalculo;
    }

    public String getNumeroLaudo() {
        return numeroLaudo;
    }

    public void setNumeroLaudo(String numeroLaudo) {
        this.numeroLaudo = numeroLaudo;
    }
}
