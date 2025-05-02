package br.com.webpublico.entidadesauxiliares;

import com.beust.jcommander.internal.Lists;
import com.google.common.base.Objects;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class VORevisaoCalculoLocalizacao implements Serializable {
    private Long revisao_id;

    private Date dataRevisao;

    private boolean isCnae;
    private boolean isAlteracao;

    private VOEndereco endereco;
    private List<VOCnaeAlteracoesCMC> alteracoesCnae;

    public VORevisaoCalculoLocalizacao() {
        this.alteracoesCnae = Lists.newArrayList();
    }

    public Long getRevisao_id() {
        return revisao_id;
    }

    public void setRevisao_id(Long revisao_id) {
        this.revisao_id = revisao_id;
    }

    public Date getDataRevisao() {
        return dataRevisao;
    }

    public void setDataRevisao(Date dataRevisao) {
        this.dataRevisao = dataRevisao;
    }

    public boolean isCnae() {
        return isCnae;
    }

    public void setCnae(boolean cnae) {
        isCnae = cnae;
    }

    public boolean isAlteracao() {
        return isAlteracao;
    }

    public void setAlteracao(boolean alteracao) {
        isAlteracao = alteracao;
    }

    public VOEndereco getEndereco() {
        return endereco;
    }

    public void setEndereco(VOEndereco endereco) {
        this.endereco = endereco;
    }

    public List<VOCnaeAlteracoesCMC> getAlteracoesCnae() {
        return alteracoesCnae;
    }

    public void setAlteracoesCnae(List<VOCnaeAlteracoesCMC> alteracoesCnae) {
        this.alteracoesCnae = alteracoesCnae;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VORevisaoCalculoLocalizacao that = (VORevisaoCalculoLocalizacao) o;
        return Objects.equal(revisao_id, that.revisao_id) && Objects.equal(dataRevisao, that.dataRevisao);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(revisao_id, dataRevisao);
    }
}
