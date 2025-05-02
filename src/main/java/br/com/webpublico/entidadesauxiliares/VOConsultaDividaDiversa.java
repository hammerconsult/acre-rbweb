package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.Calculo;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Wellington on 05/05/2017.
 */
public class VOConsultaDividaDiversa extends AbstractVOConsultaLancamento {

    private Integer numeroProcesso;
    private Long idTipoDividaDiversa;
    private String tipoDividaDiversa;
    private String observacao;

    public VOConsultaDividaDiversa() {
    }

    public VOConsultaDividaDiversa(Object[] obj) {
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
        this.getResultadoParcela().setTipoCalculo(Calculo.TipoCalculo.INSCRICAO_DA.name());
        this.setCpfOrCnpj(resultado[23] != null ? (String) resultado[23] : "");
        this.setNomeOrRazaoSocial(resultado[24] != null ? (String) resultado[24] : "");
        this.numeroProcesso = ((BigDecimal) resultado[25]).intValue();
        this.idTipoDividaDiversa = ((BigDecimal) resultado[26]).longValue();
        this.tipoDividaDiversa = (String) resultado[27];
        this.observacao = (String) resultado[28];
        this.setBairro((String) resultado[29]);
        this.setLogradouro((String) resultado[30]);
        this.setNumeroEndereco((String) resultado[31]);
        this.setComplemento((String) resultado[32]);
        this.setCep((String) resultado[33]);
    }

    public Integer getNumeroProcesso() {
        return numeroProcesso;
    }

    public void setNumeroProcesso(Integer numeroProcesso) {
        this.numeroProcesso = numeroProcesso;
    }

    public Long getIdTipoDividaDiversa() {
        return idTipoDividaDiversa;
    }

    public void setIdTipoDividaDiversa(Long idTipoDividaDiversa) {
        this.idTipoDividaDiversa = idTipoDividaDiversa;
    }

    public String getTipoDividaDiversa() {
        return tipoDividaDiversa;
    }

    public void setTipoDividaDiversa(String tipoDividaDiversa) {
        this.tipoDividaDiversa = tipoDividaDiversa;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}
