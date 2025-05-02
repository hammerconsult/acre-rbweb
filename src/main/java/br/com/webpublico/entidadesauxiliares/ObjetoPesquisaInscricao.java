package br.com.webpublico.entidadesauxiliares;

import java.util.Date;

/**
 * Created by andregustavo on 30/06/2015.
 */
public class ObjetoPesquisaInscricao {
    private Integer inscricaoExercicio;
    private Long inscricaoNumero;
    private Date vencimentoParcelaDividaAtiva;
    private Long certidaoNumero;
    private Integer certidaoExercicio;
    private String numeroAjuizamento;
    private String codigoCadastro;
    private String nomeRazaoSocial;
    private String cpfCnpj;

    public ObjetoPesquisaInscricao() {
        inscricaoExercicio = null;
        inscricaoNumero = null;
        vencimentoParcelaDividaAtiva = null;
        certidaoNumero = null;
        certidaoExercicio = null;
        numeroAjuizamento = null;
        codigoCadastro = null;
        nomeRazaoSocial = null;
        cpfCnpj = null;
    }

    public Integer getInscricaoExercicio() {
        return inscricaoExercicio;
    }

    public void setInscricaoExercicio(Integer inscricaoExercicio) {
        this.inscricaoExercicio = inscricaoExercicio;
    }

    public Long getInscricaoNumero() {
        return inscricaoNumero;
    }

    public void setInscricaoNumero(Long inscricaoNumero) {
        this.inscricaoNumero = inscricaoNumero;
    }

    public Date getVencimentoParcelaDividaAtiva() {
        return vencimentoParcelaDividaAtiva;
    }

    public void setVencimentoParcelaDividaAtiva(Date vencimentoParcelaDividaAtiva) {
        this.vencimentoParcelaDividaAtiva = vencimentoParcelaDividaAtiva;
    }

    public Long getCertidaoNumero() {
        return certidaoNumero;
    }

    public void setCertidaoNumero(Long certidaoNumero) {
        this.certidaoNumero = certidaoNumero;
    }

    public Integer getCertidaoExercicio() {
        return certidaoExercicio;
    }

    public void setCertidaoExercicio(Integer certidaoExercicio) {
        this.certidaoExercicio = certidaoExercicio;
    }

    public String getNumeroAjuizamento() {
        return numeroAjuizamento;
    }

    public void setNumeroAjuizamento(String numeroAjuizamento) {
        this.numeroAjuizamento = numeroAjuizamento;
    }

    public String getCodigoCadastro() {
        return codigoCadastro;
    }

    public void setCodigoCadastro(String codigoCadastro) {
        this.codigoCadastro = codigoCadastro;
    }

    public String getNomeRazaoSocial() {
        return nomeRazaoSocial;
    }

    public void setNomeRazaoSocial(String nomeRazaoSocial) {
        this.nomeRazaoSocial = nomeRazaoSocial;
    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
    }
}
