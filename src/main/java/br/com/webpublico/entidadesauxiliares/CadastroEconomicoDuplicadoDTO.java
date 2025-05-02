package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.enums.SituacaoCadastralCadastroEconomico;
import br.com.webpublico.enums.TipoIssqn;
import br.com.webpublico.enums.TipoPessoa;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class CadastroEconomicoDuplicadoDTO implements Serializable {

    private Long id;
    private Boolean correto;
    private Date dataCadastro;
    private String inscricaoCadastral;
    private TipoPessoa tipoPessoa;
    private String cpfCnpj;
    private String nomeRazaoSocial;
    private SituacaoCadastralCadastroEconomico situacao;
    private TipoIssqn tipoIssqn;
    private Boolean possuiMovimento;

    public CadastroEconomicoDuplicadoDTO() {
        correto = Boolean.FALSE;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getCorreto() {
        return correto;
    }

    public void setCorreto(Boolean correto) {
        this.correto = correto;
    }

    public Date getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(Date dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public String getInscricaoCadastral() {
        return inscricaoCadastral;
    }

    public void setInscricaoCadastral(String inscricaoCadastral) {
        this.inscricaoCadastral = inscricaoCadastral;
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

    public SituacaoCadastralCadastroEconomico getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoCadastralCadastroEconomico situacao) {
        this.situacao = situacao;
    }

    public TipoIssqn getTipoIssqn() {
        return tipoIssqn;
    }

    public void setTipoIssqn(TipoIssqn tipoIssqn) {
        this.tipoIssqn = tipoIssqn;
    }

    public Boolean getPossuiMovimento() {
        return possuiMovimento;
    }

    public void setPossuiMovimento(Boolean possuiMovimento) {
        this.possuiMovimento = possuiMovimento;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CadastroEconomicoDuplicadoDTO that = (CadastroEconomicoDuplicadoDTO) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
