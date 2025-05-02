package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.StatusFornecedorLicitacao;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;

import java.util.List;

public class StatusFornecedorLicitacaoVo implements Comparable<StatusFornecedorLicitacaoVo> {

    private StatusFornecedorLicitacao statusFornecedorLicitacao;
    private List<LoteStatusFornecedorLicitacao> lotes;
    private List<ItemStatusFornecedorLicitacaoVo> itens;

    public StatusFornecedorLicitacaoVo(StatusFornecedorLicitacao statusFornecedorLicitacao) {
        this.statusFornecedorLicitacao = statusFornecedorLicitacao;
        this.lotes = Lists.newArrayList();
        this.itens = Lists.newArrayList();
    }

    public List<ItemStatusFornecedorLicitacaoVo> getItens() {
        return itens;
    }

    public void setItens(List<ItemStatusFornecedorLicitacaoVo> itens) {
        this.itens = itens;
    }

    public StatusFornecedorLicitacao getStatusFornecedorLicitacao() {
        return statusFornecedorLicitacao;
    }

    public void setStatusFornecedorLicitacao(StatusFornecedorLicitacao statusFornecedorLicitacao) {
        this.statusFornecedorLicitacao = statusFornecedorLicitacao;
    }

    public List<LoteStatusFornecedorLicitacao> getLotes() {
        return lotes;
    }

    public void setLotes(List<LoteStatusFornecedorLicitacao> lotes) {
        this.lotes = lotes;
    }

    @Override
    public int compareTo(StatusFornecedorLicitacaoVo o) {
        try {
            return ComparisonChain.start().compare(getStatusFornecedorLicitacao().getTipoSituacao().getDescricao(), o.getStatusFornecedorLicitacao().getTipoSituacao().getDescricao())
                .compare(getStatusFornecedorLicitacao().getNumero(), o.getStatusFornecedorLicitacao().getNumero()).result();
        } catch (NullPointerException e) {
            return 0;
        }
    }
}
