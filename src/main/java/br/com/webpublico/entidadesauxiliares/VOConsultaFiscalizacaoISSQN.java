package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.Calculo;
import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 02/05/17
 * Time: 11:30
 * To change this template use File | Settings | File Templates.
 */
public class VOConsultaFiscalizacaoISSQN extends AbstractVOConsultaLancamento {


    private Long numeroProgramacaoFiscal;
    private Long numeroOrdemServico;
    private Long numeroAutoInfracao;
    private Integer anoAutoInfracao;
    private Long idAutoInfracao;


    public VOConsultaFiscalizacaoISSQN(Object[] obj) {
        this.preencher(obj);
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
        this.getResultadoParcela().setPagamento(resultado[20] != null ? (Date) resultado[20] : null);
        this.getResultadoParcela().setTipoCalculo(Calculo.TipoCalculo.FISCALIZACAO.name());
        this.getResultadoParcela().setTipoCadastro(TipoCadastroTributario.ECONOMICO.name());
        this.getResultadoParcela().setIdConfiguracaoAcrescimo(((BigDecimal) resultado[21]).longValue());
        this.setCpfOrCnpj(resultado[22] != null ? (String) resultado[22] : "");
        this.setNomeOrRazaoSocial(resultado[23] != null ? (String) resultado[23] : "");
        super.setTipoLogradouro((String) resultado[24]);
        super.setLogradouro((String) resultado[25]);
        super.setNumeroEndereco((String) resultado[26]);
        super.setComplemento((String) resultado[27]);
        super.setBairro((String) resultado[28]);
        super.setCep((String) resultado[29]);
        this.numeroProgramacaoFiscal = ((BigDecimal) resultado[30]).longValue();
        this.numeroOrdemServico = ((BigDecimal) resultado[31]).longValue();
        this.numeroAutoInfracao = ((BigDecimal) resultado[32]).longValue();
        this.anoAutoInfracao = ((BigDecimal) resultado[33]).intValue();
        this.idAutoInfracao = ((BigDecimal) resultado[34]).longValue();
    }

    public Long getNumeroProgramacaoFiscal() {
        return numeroProgramacaoFiscal;
    }

    public void setNumeroProgramacaoFiscal(Long numeroProgramacaoFiscal) {
        this.numeroProgramacaoFiscal = numeroProgramacaoFiscal;
    }

    public Long getNumeroOrdemServico() {
        return numeroOrdemServico;
    }

    public void setNumeroOrdemServico(Long numeroOrdemServico) {
        this.numeroOrdemServico = numeroOrdemServico;
    }

    public Long getNumeroAutoInfracao() {
        return numeroAutoInfracao;
    }

    public void setNumeroAutoInfracao(Long numeroAutoInfracao) {
        this.numeroAutoInfracao = numeroAutoInfracao;
    }

    public Integer getAnoAutoInfracao() {
        return anoAutoInfracao;
    }

    public void setAnoAutoInfracao(Integer anoAutoInfracao) {
        this.anoAutoInfracao = anoAutoInfracao;
    }

    public Long getIdAutoInfracao() {
        return idAutoInfracao;
    }

    public void setIdAutoInfracao(Long idAutoInfracao) {
        this.idAutoInfracao = idAutoInfracao;
    }
}
