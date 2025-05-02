package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.Calculo;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 21/07/15
 * Time: 17:07
 * To change this template use File | Settings | File Templates.
 */
public class VOConsultaNotaFiscal extends AbstractVOConsultaLancamento {

    private Long idNota;
    private Long numero;
    private Date emissao;
    private Integer exercicio;
    private BigDecimal percentualIss;
    private BigDecimal totalServico;
    private BigDecimal totalIss;
    private String situacao;
    private Long idUsuario;
    private String usuarioEmissao;
    private String cmcTomador;
    private Long idTomador;
    private String cpfOrCnpjTomador;
    private String nomeOrRazaoSocialTomador;
    private String cmcPrestador;
    private Long idPrestador;
    private String cpfOrCnpjPrestador;
    private String nomeOrRazaoSocialPrestador;
    private Long idHistorico;
    private String usuarioEmissaoDam;


    public VOConsultaNotaFiscal(Object[] resultado) {
        this.preencher(resultado);
    }

    public void preencher(Object[] resultado) {
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
        this.getResultadoParcela().setTipoCadastro((String) resultado[22]);
        this.getResultadoParcela().setTipoCalculo(Calculo.TipoCalculo.NFS_AVULSA.name());
        this.setCpfOrCnpj(resultado[23] != null ? (String) resultado[23] : "");
        this.setNomeOrRazaoSocial(resultado[24] != null ? (String) resultado[24] : "");
        this.setIdNota(((BigDecimal) resultado[25]).longValue());
        this.setNumero(resultado[26] != null ? ((BigDecimal) resultado[26]).longValue() : null);
        this.setEmissao((Date) resultado[27]);
        this.setExercicio(((BigDecimal) resultado[28]).intValue());
        this.setPercentualIss((BigDecimal) resultado[29]);
        this.setTotalServico((BigDecimal) resultado[30]);
        this.setTotalIss((BigDecimal) resultado[31]);
        this.setSituacao((String) resultado[32]);
        this.setIdUsuario(resultado[33] != null ? ((BigDecimal) resultado[33]).longValue() : null);
        this.setUsuarioEmissao((String) resultado[34]);
        this.setCmcTomador((String) resultado[35]);
        this.setIdTomador(resultado[36] != null ? ((BigDecimal) resultado[36]).longValue() : null);
        this.setCpfOrCnpjTomador(resultado[37] != null ? (String) resultado[37] : "");
        this.setNomeOrRazaoSocialTomador(resultado[38] != null ? (String) resultado[38] : "");
        this.setCmcPrestador((String) resultado[39]);
        this.setIdPrestador(resultado[40] != null ? ((BigDecimal) resultado[40]).longValue() : null);
        this.setCpfOrCnpjPrestador(resultado[41] != null ? (String) resultado[41] : "");
        this.setNomeOrRazaoSocialPrestador(resultado[42] != null ? (String) resultado[42] : "");
        this.setIdHistorico(resultado[43] != null ? ((BigDecimal) resultado[43]).longValue() : null);
        this.setUsuarioEmissaoDam((String) resultado[44]);
    }

    public Long getIdNota() {
        return idNota;
    }

    public void setIdNota(Long idNota) {
        this.idNota = idNota;
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    public Date getEmissao() {
        return emissao;
    }

    public void setEmissao(Date emissao) {
        this.emissao = emissao;
    }

    public Integer getExercicio() {
        return exercicio;
    }

    public void setExercicio(Integer exercicio) {
        this.exercicio = exercicio;
    }

    public BigDecimal getPercentualIss() {
        return percentualIss;
    }

    public void setPercentualIss(BigDecimal percentualIss) {
        this.percentualIss = percentualIss;
    }

    public BigDecimal getTotalServico() {
        return totalServico;
    }

    public void setTotalServico(BigDecimal totalServico) {
        this.totalServico = totalServico;
    }

    public BigDecimal getTotalIss() {
        return totalIss;
    }

    public void setTotalIss(BigDecimal totalIss) {
        this.totalIss = totalIss;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getUsuarioEmissao() {
        return usuarioEmissao;
    }

    public void setUsuarioEmissao(String usuarioEmissao) {
        this.usuarioEmissao = usuarioEmissao;
    }

    public String getCmcTomador() {
        return cmcTomador;
    }

    public void setCmcTomador(String cmcTomador) {
        this.cmcTomador = cmcTomador;
    }

    public Long getIdTomador() {
        return idTomador;
    }

    public void setIdTomador(Long idTomador) {
        this.idTomador = idTomador;
    }

    public String getCpfOrCnpjTomador() {
        return cpfOrCnpjTomador;
    }

    public void setCpfOrCnpjTomador(String cpfOrCnpjTomador) {
        this.cpfOrCnpjTomador = cpfOrCnpjTomador;
    }

    public String getNomeOrRazaoSocialTomador() {
        return nomeOrRazaoSocialTomador;
    }

    public void setNomeOrRazaoSocialTomador(String nomeOrRazaoSocialTomador) {
        this.nomeOrRazaoSocialTomador = nomeOrRazaoSocialTomador;
    }

    public String getCmcPrestador() {
        return cmcPrestador;
    }

    public void setCmcPrestador(String cmcPrestador) {
        this.cmcPrestador = cmcPrestador;
    }

    public Long getIdPrestador() {
        return idPrestador;
    }

    public void setIdPrestador(Long idPrestador) {
        this.idPrestador = idPrestador;
    }

    public String getCpfOrCnpjPrestador() {
        return cpfOrCnpjPrestador;
    }

    public void setCpfOrCnpjPrestador(String cpfOrCnpjPrestador) {
        this.cpfOrCnpjPrestador = cpfOrCnpjPrestador;
    }

    public String getNomeOrRazaoSocialPrestador() {
        return nomeOrRazaoSocialPrestador;
    }

    public void setNomeOrRazaoSocialPrestador(String nomeOrRazaoSocialPrestador) {
        this.nomeOrRazaoSocialPrestador = nomeOrRazaoSocialPrestador;
    }

    public Long getIdHistorico() {
        return idHistorico;
    }

    public void setIdHistorico(Long idHistorico) {
        this.idHistorico = idHistorico;
    }

    public String getUsuarioEmissaoDam() {
        return usuarioEmissaoDam;
    }

    public void setUsuarioEmissaoDam(String usuarioEmissaoDam) {
        this.usuarioEmissaoDam = usuarioEmissaoDam;
    }
}
