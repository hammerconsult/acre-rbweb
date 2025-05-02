package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;
import com.google.common.collect.Lists;

import java.util.List;

public enum TipoAjusteProcessoCompra implements EnumComDescricao {

    INCLUIR_FORNECEDOR("Incluir Fornecedor"),
    EDITAR_FORNECEDOR("Editar Fornecedor"),
    INCLUIR_PROPOSTA_FORNECEDOR("Incluir Proposta Fornecedor"),
    EDITAR_PROPOSTA_FORNECEDOR("Editar Proposta Fornecedor"),
    SUBSTITUIR_FORNECEDOR("Substituir Fornecedor"),
    SUBSTITUIR_OBJETO_COMPRA("Substituir Objeto de Compra"),
    SUBSTITUIR_CONTROLE_ITEM("Substituir Controle Item");
    private String descricao;

    TipoAjusteProcessoCompra(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public static List<TipoAjusteProcessoCompra> getTiposAlteracaoFornecedorLicitacao() {
        List<TipoAjusteProcessoCompra> tipos = Lists.newArrayList();
        tipos.add(TipoAjusteProcessoCompra.INCLUIR_FORNECEDOR);
        tipos.add(TipoAjusteProcessoCompra.EDITAR_FORNECEDOR);
        tipos.add(TipoAjusteProcessoCompra.INCLUIR_PROPOSTA_FORNECEDOR);
        tipos.add(TipoAjusteProcessoCompra.EDITAR_PROPOSTA_FORNECEDOR);
        tipos.add(TipoAjusteProcessoCompra.SUBSTITUIR_FORNECEDOR);
        return tipos;
    }

    public boolean isIncluirFornecedor() {
        return INCLUIR_FORNECEDOR.equals(this);
    }

    public boolean isIncluirProposta() {
        return INCLUIR_PROPOSTA_FORNECEDOR.equals(this);
    }

    public boolean isEditarProposta() {
        return EDITAR_PROPOSTA_FORNECEDOR.equals(this);
    }

    public boolean isEditarFornecedor() {
        return EDITAR_FORNECEDOR.equals(this);
    }

    public boolean isSubstituirFornecedor() {
        return SUBSTITUIR_FORNECEDOR.equals(this);
    }

    public boolean isSubstituirControleItem() {
        return SUBSTITUIR_CONTROLE_ITEM.equals(this);
    }

    public boolean isSubstituirObjetoCompra() {
        return SUBSTITUIR_OBJETO_COMPRA.equals(this);
    }

    public boolean isTipoAjusteAlteracaoFornecedor() {
        return getTiposAlteracaoFornecedorLicitacao().contains(this);
    }

    @Override
    public String toString() {
        return descricao;
    }
}


