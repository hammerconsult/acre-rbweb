package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.ItemAquisicao;
import br.com.webpublico.entidades.ItemSolicitacaoAquisicao;
import com.google.common.collect.Lists;

import java.util.List;

public class VOItensAquisicao {

    private List<ItemSolicitacaoAquisicao> itensSolicitacaoAquisicao;
    private List<ItemAquisicao> itensEfetivacaoAquisicao;

    public VOItensAquisicao() {
        itensSolicitacaoAquisicao = Lists.newArrayList();
        itensEfetivacaoAquisicao = Lists.newArrayList();
    }

    public List<ItemSolicitacaoAquisicao> getItensSolicitacaoAquisicao() {
        return itensSolicitacaoAquisicao;
    }

    public void setItensSolicitacaoAquisicao(List<ItemSolicitacaoAquisicao> itensSolicitacaoAquisicao) {
        this.itensSolicitacaoAquisicao = itensSolicitacaoAquisicao;
    }

    public List<ItemAquisicao> getItensEfetivacaoAquisicao() {
        return itensEfetivacaoAquisicao;
    }

    public void setItensEfetivacaoAquisicao(List<ItemAquisicao> itensEfetivacaoAquisicao) {
        this.itensEfetivacaoAquisicao = itensEfetivacaoAquisicao;
    }
}
