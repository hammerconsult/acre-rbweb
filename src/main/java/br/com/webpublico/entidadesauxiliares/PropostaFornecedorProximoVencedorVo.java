package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.LancePregao;
import br.com.webpublico.entidades.PropostaFornecedor;
import br.com.webpublico.util.Util;
import com.google.common.collect.ComparisonChain;

import java.io.Serializable;
import java.util.Objects;

public class PropostaFornecedorProximoVencedorVo implements Serializable, Comparable<PropostaFornecedorProximoVencedorVo> {

    private Integer classificacao;
    private LancePregao lancePregao;
    private PropostaFornecedor propostaFornecedor;

    private Boolean selecionado;


    public PropostaFornecedorProximoVencedorVo(PropostaFornecedor propostaFornecedor) {
        this.propostaFornecedor = propostaFornecedor;
    }

    public Integer getClassificacao() {
        return classificacao;
    }

    public void setClassificacao(Integer classificacao) {
        this.classificacao = classificacao;
    }

    public LancePregao getLancePregao() {
        return lancePregao;
    }

    public void setLancePregao(LancePregao lancePregao) {
        this.lancePregao = lancePregao;
    }

    public PropostaFornecedor getPropostaFornecedor() {
        return propostaFornecedor;
    }

    public void setPropostaFornecedor(PropostaFornecedor propostaFornecedor) {
        this.propostaFornecedor = propostaFornecedor;
    }

    public Boolean getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(Boolean selecionado) {
        this.selecionado = selecionado;
    }

    @Override
    public String toString() {
        try {
            return classificacao + "º " + propostaFornecedor.getFornecedor().getNome() + ",  Lance: " + Util.formataValorSemCifrao(lancePregao.getValor());
        } catch (NullPointerException e) {
            return propostaFornecedor.getFornecedor().getNome();
        }
    }

    @Override
    public int compareTo(PropostaFornecedorProximoVencedorVo o) {
        try {
            if (getClassificacao() !=null && o.getClassificacao() !=null) {
                return ComparisonChain.start().compare(getClassificacao(), o.getClassificacao()).result();
            }
            return ComparisonChain.start().compare(getLancePregao().getValor(), o.getLancePregao().getValor()).result();
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PropostaFornecedorProximoVencedorVo that = (PropostaFornecedorProximoVencedorVo) o;
        return Objects.equals(propostaFornecedor, that.propostaFornecedor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(propostaFornecedor);
    }
}
