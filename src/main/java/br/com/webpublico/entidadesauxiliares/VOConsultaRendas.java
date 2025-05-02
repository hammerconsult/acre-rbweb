package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.Calculo;
import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 03/08/15
 * Time: 17:30
 * To change this template use File | Settings | File Templates.
 */
public class VOConsultaRendas extends AbstractVOConsultaLancamento {

    private Long idContrato;
    private String numeroContrato;
    private String localizacao;
    private String box;
    private String situacaoContrato;
    private Integer vigenciaContrato;
    private Date inicioContrato;
    private Date fimContrato;
    private String atividadeContrato;
    private BigDecimal valorContrato;
    private BigDecimal ufmMesContrato;
    private BigDecimal areaContrato;
    private Integer numeroParcelasContrato;

    public VOConsultaRendas(Object[] obj) {
        this.preencher(obj);
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
        this.getResultadoParcela().setTipoCadastro(TipoCadastroTributario.RURAL.getDescricao());
        this.getResultadoParcela().setTipoCalculo(Calculo.TipoCalculo.RENDAS.name());
        this.setCpfOrCnpj(resultado[22] != null ? (String) resultado[22] : "");
        this.setNomeOrRazaoSocial(resultado[23] != null ? (String) resultado[23] : "");
        this.setIdContrato(((BigDecimal) resultado[24]).longValue());
        this.setNumeroContrato(resultado[25] != null ? (String) resultado[25] : "");
        this.setLocalizacao(resultado[26] != null ? (String) resultado[26] : "");
        this.setBox(resultado[27] != null ? (String) resultado[27] : "");
        this.setSituacaoContrato(resultado[28] != null ? (String) resultado[28] : "");
        this.setVigenciaContrato(((BigDecimal) resultado[29]).intValue());
        this.setInicioContrato((Date) resultado[30]);
        this.setFimContrato((Date) resultado[31]);
        this.setAtividadeContrato(resultado[32] != null ? (String) resultado[32] : "");
        this.setValorContrato(resultado[33] != null && resultado[37] != null ? ((BigDecimal) resultado[33]).setScale(2, RoundingMode.HALF_UP).multiply((BigDecimal) resultado[37]) : BigDecimal.ZERO);
        this.setUfmMesContrato(resultado[34] != null ? (BigDecimal) resultado[34] : BigDecimal.ZERO);
        this.setAreaContrato(resultado[35] != null ? (BigDecimal) resultado[35] : BigDecimal.ZERO);
        this.setNumeroParcelasContrato(((BigDecimal) resultado[36]).intValue());
    }

    public Long getIdContrato() {
        return idContrato;
    }

    public void setIdContrato(Long idContrato) {
        this.idContrato = idContrato;
    }

    public String getNumeroContrato() {
        return numeroContrato;
    }

    public void setNumeroContrato(String numeroContrato) {
        this.numeroContrato = numeroContrato;
    }

    public String getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(String localizacao) {
        this.localizacao = localizacao;
    }

    public String getBox() {
        return box;
    }

    public void setBox(String box) {
        this.box = box;
    }

    public String getSituacaoContrato() {
        return situacaoContrato;
    }

    public void setSituacaoContrato(String situacaoContrato) {
        this.situacaoContrato = situacaoContrato;
    }

    public Integer getVigenciaContrato() {
        return vigenciaContrato;
    }

    public void setVigenciaContrato(Integer vigenciaContrato) {
        this.vigenciaContrato = vigenciaContrato;
    }

    public Date getInicioContrato() {
        return inicioContrato;
    }

    public void setInicioContrato(Date inicioContrato) {
        this.inicioContrato = inicioContrato;
    }

    public Date getFimContrato() {
        return fimContrato;
    }

    public void setFimContrato(Date fimContrato) {
        this.fimContrato = fimContrato;
    }

    public String getAtividadeContrato() {
        return atividadeContrato;
    }

    public void setAtividadeContrato(String atividadeContrato) {
        this.atividadeContrato = atividadeContrato;
    }

    public BigDecimal getValorContrato() {
        return valorContrato;
    }

    public void setValorContrato(BigDecimal valorContrato) {
        this.valorContrato = valorContrato;
    }

    public BigDecimal getUfmMesContrato() {
        return ufmMesContrato;
    }

    public void setUfmMesContrato(BigDecimal ufmMesContrato) {
        this.ufmMesContrato = ufmMesContrato;
    }

    public BigDecimal getAreaContrato() {
        return areaContrato;
    }

    public void setAreaContrato(BigDecimal areaContrato) {
        this.areaContrato = areaContrato;
    }

    public Integer getNumeroParcelasContrato() {
        return numeroParcelasContrato;
    }

    public void setNumeroParcelasContrato(Integer numeroParcelasContrato) {
        this.numeroParcelasContrato = numeroParcelasContrato;
    }
}
