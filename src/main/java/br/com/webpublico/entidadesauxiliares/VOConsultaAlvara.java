package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.Calculo;
import br.com.webpublico.enums.ClassificacaoAtividade;
import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;

import java.math.BigDecimal;
import java.util.Date;

import static br.com.webpublico.util.Util.traduzirEnum;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 21/07/15
 * Time: 17:07
 * To change this template use File | Settings | File Templates.
 */
public class VOConsultaAlvara extends AbstractVOConsultaLancamento {

    private String tipoAlvaraNameEnum;
    private Boolean provisorio;
    private String classificacaoAtividade;
    private Long idNaturezaJuridica;
    private String naturezaJuridica;
    private Long idTipoAutonomo;
    private String tipoAutonomo;
    private Boolean mei;


    public VOConsultaAlvara(Object[] resultado) {
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
        this.getResultadoParcela().setTipoCadastro(TipoCadastroTributario.ECONOMICO.getDescricao());
        this.getResultadoParcela().setTipoCalculo(Calculo.TipoCalculo.INSCRICAO_DA.name());
        this.setCpfOrCnpj(resultado[22] != null ? (String) resultado[22] : "");
        this.setNomeOrRazaoSocial(resultado[23] != null ? (String) resultado[23] : "");
        this.setTipoAlvaraNameEnum(resultado[24] != null ? (String) resultado[24] : "");
        this.setProvisorio(resultado[25] != null && ((BigDecimal) resultado[25]).compareTo(BigDecimal.ZERO) > 0);
        ClassificacaoAtividade classificacaoAtividade = traduzirEnum(ClassificacaoAtividade.class, (String) resultado[26]);
        if (classificacaoAtividade != null) {
            this.setClassificacaoAtividade(classificacaoAtividade.getDescricao());
        }
        this.setIdNaturezaJuridica(resultado[27] != null ? ((BigDecimal) resultado[27]).longValue() : null);
        this.setNaturezaJuridica(resultado[28] != null ? (String) resultado[28] : "");
        this.setIdTipoAutonomo(resultado[29] != null ? ((BigDecimal) resultado[29]).longValue() : null);
        this.setTipoAutonomo(resultado[30] != null ? (String) resultado[30] : "");
        this.setMei(resultado[31] != null && ((BigDecimal) resultado[31]).compareTo(BigDecimal.ZERO) > 0);
        super.setTipoLogradouro(resultado[32] != null ? (String) resultado[32] : "");
        super.setLogradouro(resultado[33] != null ?  (String) resultado[33] : "");
        super.setNumeroEndereco(resultado[34] != null ? (String) resultado[34] : "");
        super.setComplemento(resultado[35] != null ? (String) resultado[35] : "");
        super.setBairro(resultado[36] != null ? (String) resultado[36] : "");
        super.setCep(resultado[37] != null ? (String) resultado[37] : "");
    }

    public String getTipoAlvaraNameEnum() {
        return tipoAlvaraNameEnum;
    }

    public void setTipoAlvaraNameEnum(String tipoAlvaraNameEnum) {
        this.tipoAlvaraNameEnum = tipoAlvaraNameEnum;
    }

    public Boolean getProvisorio() {
        return provisorio;
    }

    public void setProvisorio(Boolean provisorio) {
        this.provisorio = provisorio;
    }

    public String getClassificacaoAtividade() {
        return classificacaoAtividade;
    }

    public void setClassificacaoAtividade(String classificacaoAtividade) {
        this.classificacaoAtividade = classificacaoAtividade;
    }

    public String getNaturezaJuridica() {
        return naturezaJuridica;
    }

    public void setNaturezaJuridica(String naturezaJuridica) {
        this.naturezaJuridica = naturezaJuridica;
    }

    public String getTipoAutonomo() {
        return tipoAutonomo;
    }

    public void setTipoAutonomo(String tipoAutonomo) {
        this.tipoAutonomo = tipoAutonomo;
    }

    public Boolean getMei() {
        return mei;
    }

    public void setMei(Boolean mei) {
        this.mei = mei;
    }

    public Long getIdNaturezaJuridica() {
        return idNaturezaJuridica;
    }

    public void setIdNaturezaJuridica(Long idNaturezaJuridica) {
        this.idNaturezaJuridica = idNaturezaJuridica;
    }

    public Long getIdTipoAutonomo() {
        return idTipoAutonomo;
    }

    public void setIdTipoAutonomo(Long idTipoAutonomo) {
        this.idTipoAutonomo = idTipoAutonomo;
    }
}
