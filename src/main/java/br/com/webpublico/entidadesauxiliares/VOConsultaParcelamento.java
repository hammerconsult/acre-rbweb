package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.Calculo;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Wellington on 26/06/2015.
 */
public class VOConsultaParcelamento extends AbstractVOConsultaLancamento {

    private Long numeroParcelamento;
    private Long numeroReparcelamento;
    private String numeroAjuizamento;
    private Long idParametro;
    private BigDecimal desconto;
    private String faixaDesconto;
    private Long parametroCodigo;
    private String parametroDescricao;


    public VOConsultaParcelamento() {
    }

    public VOConsultaParcelamento(Object[] obj) {
        preencheObjeto(obj);
    }

    public void preencheObjeto(Object[] resultado) {
        this.setResultadoParcela(new ResultadoParcela());
        this.getResultadoParcela().setIdCadastro(resultado[0] != null ? ((BigDecimal) resultado[0]).longValue() : null);
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
        this.getResultadoParcela().setPagamento(resultado[20] != null ? (Date) resultado[20] : null);
        this.getResultadoParcela().setIdConfiguracaoAcrescimo(((BigDecimal) resultado[21]).longValue());
        this.getResultadoParcela().setTipoCadastro(resultado[22] != null ? (String) resultado[22] : "");
        this.getResultadoParcela().setTipoCalculo(Calculo.TipoCalculo.PARCELAMENTO.name());
        this.setCpfOrCnpj(resultado[23] != null ? (String) resultado[23] : "");
        this.setNomeOrRazaoSocial(resultado[24] != null ? (String) resultado[24] : "");
        this.numeroParcelamento = ((BigDecimal) resultado[25]).longValue();
        this.numeroAjuizamento = resultado[26] == null ? "N/A" : (String) resultado[26];
        this.numeroReparcelamento = resultado[27] != null ? ((BigDecimal) resultado[27]).longValue() : 0L;
        this.idParametro = resultado[28] != null ? ((BigDecimal) resultado[28]).longValue() : 0L;
        this.desconto = (BigDecimal) resultado[29];
        this.faixaDesconto = resultado[30] == null ? "" : (String) resultado[30];
        this.parametroCodigo = ((BigDecimal) resultado[31]).longValue();
        this.parametroDescricao = resultado[32] == null ? "" : (String) resultado[32];
    }

    public Long getNumeroParcelamento() {
        return numeroParcelamento;
    }

    public void setNumeroParcelamento(Long numeroParcelamento) {
        this.numeroParcelamento = numeroParcelamento;
    }

    public Long getNumeroReparcelamento() {
        return numeroReparcelamento;
    }

    public void setNumeroReparcelamento(Long numeroReparcelamento) {
        this.numeroReparcelamento = numeroReparcelamento;
    }

    public String getNumeroAjuizamento() {
        return numeroAjuizamento;
    }

    public void setNumeroAjuizamento(String numeroAjuizamento) {
        this.numeroAjuizamento = numeroAjuizamento;
    }

    public Long getIdParametro() {
        return idParametro;
    }

    public void setIdParametro(Long idParametro) {
        this.idParametro = idParametro;
    }

    public BigDecimal getDesconto() {
        return desconto;
    }

    public void setDesconto(BigDecimal desconto) {
        this.desconto = desconto;
    }

    public String getFaixaDesconto() {
        return faixaDesconto;
    }

    public void setFaixaDesconto(String faixaDesconto) {
        this.faixaDesconto = faixaDesconto;
    }

    public Long getParametroCodigo() {
        return parametroCodigo;
    }

    public void setParametroCodigo(Long parametroCodigo) {
        this.parametroCodigo = parametroCodigo;
    }

    public String getParametroDescricao() {
        return parametroDescricao;
    }

    public void setParametroDescricao(String parametroDescricao) {
        this.parametroDescricao = parametroDescricao;
    }
}
