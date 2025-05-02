package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by andregustavo on 26/06/2015.
 */
public class VOConsultaDividaAtiva extends AbstractVOConsultaLancamento {

    private Long numeroInscricao;
    private String anoInscricao;
    private Integer exercicioCda;
    private Long numeroCertidao;
    private String identificacaoCda;
    private String numeroAjuizamento;
    private String identificacaoAjuizamento;
    private Integer exercicioInscricao;
    private String situacaoProcessoJudicial;


    public VOConsultaDividaAtiva() {
    }

    public VOConsultaDividaAtiva(Object[] obj) {
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
        this.setCpfOrCnpj(resultado[23] != null ? (String) resultado[23] : "");
        this.setNomeOrRazaoSocial(resultado[24] != null ? (String) resultado[24] : "");
        this.setNumeroInscricao(resultado[25] != null ? ((BigDecimal) resultado[25]).longValue() : null);
        this.setExercicioInscricao(resultado[26] != null ? ((BigDecimal) resultado[26]).intValue() : null);
        this.setNumeroCertidao(resultado[27] != null ? ((BigDecimal) resultado[27]).longValue() : null);
        this.setNumeroAjuizamento(resultado[28] != null ? (String) resultado[28] : "");
        this.setExercicioCda(resultado[29] != null ? ((BigDecimal) resultado[29]).intValue() : null);
        this.setSituacaoProcessoJudicial(resultado[30] != null ? (String) resultado[30].toString() : null);
        this.getResultadoParcela().setTipoCalculo((String) resultado[31]);
    }

    public String getIdentificacaoCda() {
        if (numeroCertidao != null && exercicioCda != null) {
            identificacaoCda = numeroCertidao + "/" + exercicioCda;
        } else {
            identificacaoCda = "N/A";
        }
        return identificacaoCda;
    }

    public String getIdentificacaoAjuizamento() {
        if (numeroAjuizamento != null && !numeroAjuizamento.isEmpty()) {
            identificacaoAjuizamento = numeroAjuizamento;
        } else {
            identificacaoAjuizamento = "N/A";
        }
        return identificacaoAjuizamento;
    }

    public Long getNumeroInscricao() {
        return numeroInscricao;
    }

    public void setNumeroInscricao(Long numeroInscricao) {
        this.numeroInscricao = numeroInscricao;
    }

    public String getAnoInscricao() {
        return anoInscricao;
    }

    public void setAnoInscricao(String anoInscricao) {
        this.anoInscricao = anoInscricao;
    }

    public Long getNumeroCertidao() {
        return numeroCertidao;
    }

    public void setNumeroCertidao(Long numeroCertidao) {
        this.numeroCertidao = numeroCertidao;
    }

    public String getNumeroAjuizamento() {
        return numeroAjuizamento;
    }

    public void setNumeroAjuizamento(String numeroAjuizamento) {
        this.numeroAjuizamento = numeroAjuizamento;
    }

    public Integer getExercicioCda() {
        return exercicioCda;
    }

    public void setExercicioCda(Integer exercicioCda) {
        this.exercicioCda = exercicioCda;
    }

    public Integer getExercicioInscricao() {
        return exercicioInscricao;
    }

    public void setExercicioInscricao(Integer exercicioInscricao) {
        this.exercicioInscricao = exercicioInscricao;
    }

    public String getSituacaoProcessoJudicial() {
        return situacaoProcessoJudicial;
    }

    public void setSituacaoProcessoJudicial(String situacaoProcessoJudicial) {
        this.situacaoProcessoJudicial = situacaoProcessoJudicial;
    }
}
