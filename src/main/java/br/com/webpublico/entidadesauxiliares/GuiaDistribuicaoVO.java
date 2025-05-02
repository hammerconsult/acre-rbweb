package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.LocalEstoque;
import br.com.webpublico.entidades.RequisicaoMaterial;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;

import java.util.List;

public class GuiaDistribuicaoVO implements Comparable<GuiaDistribuicaoVO>{

    private LocalEstoque localEstoqueDestino;
    private RequisicaoMaterial requisicaoMaterial;
    private String descricao;
    private String enderecoCompleto;
    private EnderecoLocalEstoque enderecoLocalEstoque;
    private List<GuiaDistribuicaoItemVO> itens;

    public GuiaDistribuicaoVO(LocalEstoque localEstoque) {
        itens = Lists.newArrayList();
        localEstoqueDestino =localEstoque;
    }

    public LocalEstoque getLocalEstoqueDestino() {
        return localEstoqueDestino;
    }

    public void setLocalEstoqueDestino(LocalEstoque localEstoqueDestino) {
        this.localEstoqueDestino = localEstoqueDestino;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public List<GuiaDistribuicaoItemVO> getItens() {
        return itens;
    }

    public void setItens(List<GuiaDistribuicaoItemVO> itens) {
        this.itens = itens;
    }

    public String getEnderecoCompleto() {
        return enderecoCompleto;
    }

    public void setEnderecoCompleto(String enderecoCompleto) {
        this.enderecoCompleto = enderecoCompleto;
    }

    public RequisicaoMaterial getRequisicaoMaterial() {
        return requisicaoMaterial;
    }

    public void setRequisicaoMaterial(RequisicaoMaterial requisicaoMaterial) {
        this.requisicaoMaterial = requisicaoMaterial;
    }

    public EnderecoLocalEstoque getEnderecoLocalEstoque() {
        return enderecoLocalEstoque;
    }

    public void setEnderecoLocalEstoque(EnderecoLocalEstoque enderecoLocalEstoque) {
        this.enderecoLocalEstoque = enderecoLocalEstoque;
    }

    @Override
    public int compareTo(GuiaDistribuicaoVO o) {
        try {
            return ComparisonChain.start().compare(getLocalEstoqueDestino().getCodigo(), o.getLocalEstoqueDestino().getCodigo()).result();
        } catch (Exception e) {
            return 0;
        }
    }
}
