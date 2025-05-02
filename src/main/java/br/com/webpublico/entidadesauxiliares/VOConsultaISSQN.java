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
 * Date: 03/08/15
 * Time: 14:22
 * To change this template use File | Settings | File Templates.
 */
public class VOConsultaISSQN extends AbstractVOConsultaLancamento {


    private String tipoCalculoISSQN;
    private Integer mesReferencia;
    private String classificacaoAtividade;
    private Long idNaturezaJuridica;
    private String naturezaJuridica;
    private Boolean mei;


    public VOConsultaISSQN(Object[] obj) {
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
        this.getResultadoParcela().setTipoCalculo(Calculo.TipoCalculo.ISS.name());
        this.getResultadoParcela().setTipoCadastro(TipoCadastroTributario.ECONOMICO.name());
        this.getResultadoParcela().setIdConfiguracaoAcrescimo(((BigDecimal) resultado[21]).longValue());
        this.setCpfOrCnpj(resultado[22] != null ? (String) resultado[22] : "");
        this.setNomeOrRazaoSocial(resultado[23] != null ? (String) resultado[23] : "");
        this.tipoCalculoISSQN = resultado[24] != null ? (String) resultado[24] : "";
        this.mesReferencia = resultado[25] != null ? ((BigDecimal) resultado[25]).intValue() : 0;
        this.idNaturezaJuridica = resultado[26] != null ? ((BigDecimal) resultado[26]).longValue() : null;
        this.naturezaJuridica = (String) resultado[27];
        if (resultado[28] != null) {
            this.classificacaoAtividade = traduzirEnum(ClassificacaoAtividade.class, (String) resultado[28]).getDescricao();
        }
        this.mei = resultado[29] != null ? ((BigDecimal) resultado[29]).compareTo(BigDecimal.ZERO) > 0 : false;
        super.setTipoLogradouro((String) resultado[30]);
        super.setLogradouro((String) resultado[31]);
        super.setNumeroEndereco((String) resultado[32]);
        super.setComplemento((String) resultado[33]);
        super.setBairro((String) resultado[34]);
        super.setCep((String) resultado[35]);
    }

    public String getTipoCalculoISSQN() {
        return tipoCalculoISSQN;
    }

    public void setTipoCalculoISSQN(String tipoCalculoISSQN) {
        this.tipoCalculoISSQN = tipoCalculoISSQN;
    }

    public Integer getMesReferencia() {
        return mesReferencia;
    }

    public void setMesReferencia(Integer mesReferencia) {
        this.mesReferencia = mesReferencia;
    }

    public String getClassificacaoAtividade() {
        return classificacaoAtividade;
    }

    public void setClassificacaoAtividade(String classificacaoAtividade) {
        this.classificacaoAtividade = classificacaoAtividade;
    }

    public Long getIdNaturezaJuridica() {
        return idNaturezaJuridica;
    }

    public void setIdNaturezaJuridica(Long idNaturezaJuridica) {
        this.idNaturezaJuridica = idNaturezaJuridica;
    }

    public String getNaturezaJuridica() {
        return naturezaJuridica;
    }

    public void setNaturezaJuridica(String naturezaJuridica) {
        this.naturezaJuridica = naturezaJuridica;
    }

    public Boolean getMei() {
        return mei;
    }

    public void setMei(Boolean mei) {
        this.mei = mei;
    }
}
