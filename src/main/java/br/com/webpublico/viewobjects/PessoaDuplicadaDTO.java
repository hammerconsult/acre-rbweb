package br.com.webpublico.viewobjects;

import br.com.webpublico.enums.SituacaoCadastralPessoa;
import br.com.webpublico.enums.TipoPessoa;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class PessoaDuplicadaDTO implements Serializable {

    private Long id;
    private Boolean correta;
    private Date dataCadastro;
    private TipoPessoa tipoPessoa;
    private String cpfCnpj;
    private String nomeRazaoSocial;
    private SituacaoCadastralPessoa situacao;
    private String perfis;
    private Boolean possuiMovimentoTributario;
    private Boolean possuiMovimentoContabil;
    private Boolean possuiMovimentoRh;
    private Boolean possuiMovimentoAdministrativo;

    public PessoaDuplicadaDTO() {
        correta = Boolean.FALSE;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getCorreta() {
        return correta;
    }

    public void setCorreta(Boolean correta) {
        this.correta = correta;
    }

    public Date getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(Date dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public TipoPessoa getTipoPessoa() {
        return tipoPessoa;
    }

    public void setTipoPessoa(TipoPessoa tipoPessoa) {
        this.tipoPessoa = tipoPessoa;
    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
    }

    public String getNomeRazaoSocial() {
        return nomeRazaoSocial;
    }

    public void setNomeRazaoSocial(String nomeRazaoSocial) {
        this.nomeRazaoSocial = nomeRazaoSocial;
    }

    public SituacaoCadastralPessoa getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoCadastralPessoa situacao) {
        this.situacao = situacao;
    }

    public String getPerfis() {
        return perfis;
    }

    public void setPerfis(String perfis) {
        this.perfis = perfis;
    }

    public Boolean getPossuiMovimentoTributario() {
        return possuiMovimentoTributario;
    }

    public void setPossuiMovimentoTributario(Boolean possuiMovimentoTributario) {
        this.possuiMovimentoTributario = possuiMovimentoTributario;
    }

    public Boolean getPossuiMovimentoContabil() {
        return possuiMovimentoContabil;
    }

    public void setPossuiMovimentoContabil(Boolean possuiMovimentoContabil) {
        this.possuiMovimentoContabil = possuiMovimentoContabil;
    }

    public Boolean getPossuiMovimentoRh() {
        return possuiMovimentoRh;
    }

    public void setPossuiMovimentoRh(Boolean possuiMovimentoRh) {
        this.possuiMovimentoRh = possuiMovimentoRh;
    }

    public Boolean getPossuiMovimentoAdministrativo() {
        return possuiMovimentoAdministrativo;
    }

    public void setPossuiMovimentoAdministrativo(Boolean possuiMovimentoAdministrativo) {
        this.possuiMovimentoAdministrativo = possuiMovimentoAdministrativo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PessoaDuplicadaDTO that = (PessoaDuplicadaDTO) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
