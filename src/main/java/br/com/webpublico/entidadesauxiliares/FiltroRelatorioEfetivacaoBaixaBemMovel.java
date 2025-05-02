package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.EfetivacaoBaixaPatrimonial;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.enums.TipoBaixa;

import java.util.Date;

public class FiltroRelatorioEfetivacaoBaixaBemMovel {

    private HierarquiaOrganizacional hierarquiaAdministrativa;
    private HierarquiaOrganizacional hierarquialOrcamentaria;
    private String numeroSolicitacao;
    private String numeroEfetivacao;
    private Date dataInicial;
    private Date dataFinal;
    private Date dataOperacao;
    private TipoBaixa tipoBaixa;
    private UsuarioSistema usuarioSistema;
    private EfetivacaoBaixaPatrimonial efetivacaoBaixa;
    private Boolean verificarGestor;

    public FiltroRelatorioEfetivacaoBaixaBemMovel() {
        verificarGestor = Boolean.FALSE;
        numeroSolicitacao = null;
        numeroEfetivacao = null;
    }

    public HierarquiaOrganizacional getHierarquiaAdministrativa() {
        return hierarquiaAdministrativa;
    }

    public void setHierarquiaAdministrativa(HierarquiaOrganizacional hierarquiaAdministrativa) {
        this.hierarquiaAdministrativa = hierarquiaAdministrativa;
    }

    public HierarquiaOrganizacional getHierarquialOrcamentaria() {
        return hierarquialOrcamentaria;
    }

    public void setHierarquialOrcamentaria(HierarquiaOrganizacional hierarquialOrcamentaria) {
        this.hierarquialOrcamentaria = hierarquialOrcamentaria;
    }

    public String getNumeroEfetivacao() {
        return numeroEfetivacao;
    }

    public void setNumeroEfetivacao(String numeroEfetivacao) {
        this.numeroEfetivacao = numeroEfetivacao;
    }

    public Date getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(Date dataInicial) {
        this.dataInicial = dataInicial;
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    public TipoBaixa getTipoBaixa() {
        return tipoBaixa;
    }

    public void setTipoBaixa(TipoBaixa tipoBaixa) {
        this.tipoBaixa = tipoBaixa;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public String getNumeroSolicitacao() {
        return numeroSolicitacao;
    }

    public void setNumeroSolicitacao(String numeroSolicitacao) {
        this.numeroSolicitacao = numeroSolicitacao;
    }

    public Date getDataOperacao() {
        return dataOperacao;
    }

    public void setDataOperacao(Date dataOperacao) {
        this.dataOperacao = dataOperacao;
    }

    public EfetivacaoBaixaPatrimonial getEfetivacaoBaixa() {
        return efetivacaoBaixa;
    }

    public void setEfetivacaoBaixa(EfetivacaoBaixaPatrimonial efetivacaoBaixa) {
        this.efetivacaoBaixa = efetivacaoBaixa;
    }

    public Boolean getVerificarGestor() {
        return verificarGestor;
    }

    public void setVerificarGestor(Boolean verificarGestor) {
        this.verificarGestor = verificarGestor;
    }

}
