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
public class VOConsultaFiscalizacaoSecretaria extends AbstractVOConsultaLancamento {

    private String numeroProcesso;
    private String numeroAutoInfracao;
    private Long idUsuario;
    private String loginUsuario;
    private Long idSecretariaFiscalizacao;
    private String secretaria;

    public VOConsultaFiscalizacaoSecretaria(Object[] resultado) {
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
        this.getResultadoParcela().setTipoCalculo(Calculo.TipoCalculo.PROC_FISCALIZACAO.name());
        this.setCpfOrCnpj(resultado[22] != null ? (String) resultado[22] : "");
        this.setNomeOrRazaoSocial(resultado[23] != null ? (String) resultado[23] : "");
        this.numeroProcesso = resultado[24] != null ? (String) resultado[24] : "";
        this.numeroAutoInfracao = resultado[25] != null ? (String) resultado[25] : "";
        this.setBairro((String) resultado[26]);
        this.setLogradouro((String) resultado[27]);
        this.setNumeroEndereco((String) resultado[28]);
        this.setComplemento((String) resultado[29]);
        this.setCep((String) resultado[30]);
        this.getResultadoParcela().setTipoCadastro((String) resultado[31]);
        this.setIdUsuario(((BigDecimal) resultado[32]).longValue());
        this.setLoginUsuario((String) resultado[33]);
        this.setIdSecretariaFiscalizacao(resultado[34] != null ? ((BigDecimal) resultado[34]).longValue() : null);
        this.setSecretaria((String) resultado[35]);
    }

    public String getNumeroProcesso() {
        return numeroProcesso;
    }

    public void setNumeroProcesso(String numeroProcesso) {
        this.numeroProcesso = numeroProcesso;
    }

    public String getNumeroAutoInfracao() {
        return numeroAutoInfracao;
    }

    public void setNumeroAutoInfracao(String numeroAutoInfracao) {
        this.numeroAutoInfracao = numeroAutoInfracao;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getLoginUsuario() {
        return loginUsuario;
    }

    public void setLoginUsuario(String loginUsuario) {
        this.loginUsuario = loginUsuario;
    }

    public Long getIdSecretariaFiscalizacao() {
        return idSecretariaFiscalizacao;
    }

    public void setIdSecretariaFiscalizacao(Long idSecretariaFiscalizacao) {
        this.idSecretariaFiscalizacao = idSecretariaFiscalizacao;
    }

    public String getSecretaria() {
        return secretaria;
    }

    public void setSecretaria(String secretaria) {
        this.secretaria = secretaria;
    }
}
