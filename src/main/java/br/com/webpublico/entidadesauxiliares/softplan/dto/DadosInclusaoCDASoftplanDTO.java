package br.com.webpublico.entidadesauxiliares.softplan.dto;

import br.com.webpublico.entidadesauxiliares.softplan.enums.AjuizadoCDASoftPlan;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.List;

@JsonTypeName("dadosInclusaoCDA")
public class DadosInclusaoCDASoftplanDTO extends DadosServicoSoftPlanDTO {

    private Long idCda;
    private Boolean retificacao;
    private String nuCDA;
    private String dtInscricao;
    private String cdEvento;
    private String nuLivro;
    private String nuFolha;
    private String nuProcessoAdm;
    private String nuCMC;
    private String nuCadastral;
    private String cdTipoCDA;
    private String deOrigemDebito;
    private String deFundamentacaoLegal;
    private String deCapitulacaoMulta;
    private String deCapitulacaoJuros;
    private String deCapitulacaoCorrecaoMonet;
    private String deCapitulacaoTributo;
    private BigDecimal vlPrincipal;
    private String dtAtuValores;
    private BigDecimal vlAtualizadoPrincipal;
    private BigDecimal vlAtualizadoMulta;
    private BigDecimal vlAtualizadoJuros;
    private BigDecimal vlAtualizadoTotalCDA;
    private BigDecimal vlTotalUFMRB;
    private List<PessoaCDASoftPlanDTO> listaDePessoas;
    private List<DividaCDASoftPlanDTO> listaDeDividas;
    private AjuizadoCDASoftPlan flAjuizado;
    private String nuProcJudicial;
    private String dtAjuizamento;

    public DadosInclusaoCDASoftplanDTO() {
        retificacao = Boolean.FALSE;
        listaDePessoas = Lists.newArrayList();
        listaDeDividas = Lists.newArrayList();
    }

    public Long getIdCda() {
        return idCda;
    }

    public void setIdCda(Long idCda) {
        this.idCda = idCda;
    }

    public Boolean getRetificacao() {
        return retificacao;
    }

    public void setRetificacao(Boolean retificacao) {
        this.retificacao = retificacao;
    }

    public String getNuCDA() {
        return nuCDA;
    }

    public void setNuCDA(String nuCDA) {
        this.nuCDA = nuCDA;
    }

    public String getDtInscricao() {
        return dtInscricao;
    }

    public void setDtInscricao(String dtInscricao) {
        this.dtInscricao = dtInscricao;
    }

    public String getCdEvento() {
        return cdEvento;
    }

    public void setCdEvento(String cdEvento) {
        this.cdEvento = cdEvento;
    }

    public String getNuLivro() {
        return nuLivro;
    }

    public void setNuLivro(String nuLivro) {
        this.nuLivro = nuLivro;
    }

    public String getNuFolha() {
        return nuFolha;
    }

    public void setNuFolha(String nuFolha) {
        this.nuFolha = nuFolha;
    }

    public String getNuProcessoAdm() {
        return nuProcessoAdm;
    }

    public void setNuProcessoAdm(String nuProcessoAdm) {
        this.nuProcessoAdm = nuProcessoAdm;
    }

    public String getNuCMC() {
        return nuCMC;
    }

    public void setNuCMC(String nuCMC) {
        this.nuCMC = nuCMC;
    }

    public String getNuCadastral() {
        return nuCadastral;
    }

    public void setNuCadastral(String nuCadastral) {
        this.nuCadastral = nuCadastral;
    }

    public String getCdTipoCDA() {
        return cdTipoCDA;
    }

    public void setCdTipoCDA(String cdTipoCDA) {
        this.cdTipoCDA = cdTipoCDA;
    }

    public String getDeOrigemDebito() {
        return deOrigemDebito;
    }

    public void setDeOrigemDebito(String deOrigemDebito) {
        this.deOrigemDebito = deOrigemDebito;
    }

    public String getDeFundamentacaoLegal() {
        return deFundamentacaoLegal;
    }

    public void setDeFundamentacaoLegal(String deFundamentacaoLegal) {
        this.deFundamentacaoLegal = deFundamentacaoLegal;
    }

    public String getDeCapitulacaoMulta() {
        return deCapitulacaoMulta;
    }

    public void setDeCapitulacaoMulta(String deCapitulacaoMulta) {
        this.deCapitulacaoMulta = deCapitulacaoMulta;
    }

    public String getDeCapitulacaoJuros() {
        return deCapitulacaoJuros;
    }

    public void setDeCapitulacaoJuros(String deCapitulacaoJuros) {
        this.deCapitulacaoJuros = deCapitulacaoJuros;
    }

    public String getDeCapitulacaoCorrecaoMonet() {
        return deCapitulacaoCorrecaoMonet;
    }

    public void setDeCapitulacaoCorrecaoMonet(String deCapitulacaoCorrecaoMonet) {
        this.deCapitulacaoCorrecaoMonet = deCapitulacaoCorrecaoMonet;
    }

    public String getDeCapitulacaoTributo() {
        return deCapitulacaoTributo;
    }

    public void setDeCapitulacaoTributo(String deCapitulacaoTributo) {
        this.deCapitulacaoTributo = deCapitulacaoTributo;
    }

    public BigDecimal getVlPrincipal() {
        return vlPrincipal;
    }

    public void setVlPrincipal(BigDecimal vlPrincipal) {
        this.vlPrincipal = vlPrincipal;
    }

    public String getDtAtuValores() {
        return dtAtuValores;
    }

    public void setDtAtuValores(String dtAtuValores) {
        this.dtAtuValores = dtAtuValores;
    }

    public BigDecimal getVlAtualizadoPrincipal() {
        return vlAtualizadoPrincipal;
    }

    public void setVlAtualizadoPrincipal(BigDecimal vlAtualizadoPrincipal) {
        this.vlAtualizadoPrincipal = vlAtualizadoPrincipal;
    }

    public BigDecimal getVlAtualizadoMulta() {
        return vlAtualizadoMulta;
    }

    public void setVlAtualizadoMulta(BigDecimal vlAtualizadoMulta) {
        this.vlAtualizadoMulta = vlAtualizadoMulta;
    }

    public BigDecimal getVlAtualizadoJuros() {
        return vlAtualizadoJuros;
    }

    public void setVlAtualizadoJuros(BigDecimal vlAtualizadoJuros) {
        this.vlAtualizadoJuros = vlAtualizadoJuros;
    }

    public BigDecimal getVlAtualizadoTotalCDA() {
        return vlAtualizadoTotalCDA;
    }

    public void setVlAtualizadoTotalCDA(BigDecimal vlAtualizadoTotalCDA) {
        this.vlAtualizadoTotalCDA = vlAtualizadoTotalCDA;
    }

    public BigDecimal getVlTotalUFMRB() {
        return vlTotalUFMRB;
    }

    public void setVlTotalUFMRB(BigDecimal vlTotalUFMRB) {
        this.vlTotalUFMRB = vlTotalUFMRB;
    }

    public List<PessoaCDASoftPlanDTO> getListaDePessoas() {
        return listaDePessoas;
    }

    public void setListaDePessoas(List<PessoaCDASoftPlanDTO> listaDePessoas) {
        this.listaDePessoas = listaDePessoas;
    }

    public List<DividaCDASoftPlanDTO> getListaDeDividas() {
        return listaDeDividas;
    }

    public void setListaDeDividas(List<DividaCDASoftPlanDTO> listaDeDividas) {
        this.listaDeDividas = listaDeDividas;
    }

    public AjuizadoCDASoftPlan getFlAjuizado() {
        return flAjuizado;
    }

    public void setFlAjuizado(AjuizadoCDASoftPlan flAjuizado) {
        this.flAjuizado = flAjuizado;
    }

    public String getNuProcJudicial() {
        return nuProcJudicial;
    }

    public void setNuProcJudicial(String nuProcJudicial) {
        this.nuProcJudicial = nuProcJudicial;
    }

    public String getDtAjuizamento() {
        return dtAjuizamento;
    }

    public void setDtAjuizamento(String dtAjuizamento) {
        this.dtAjuizamento = dtAjuizamento;
    }
}
