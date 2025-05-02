package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * Criado por Mateus
 * Data: 26/01/2017.
 */
public enum StatusMaterial implements EnumComDescricao {
    AGUARDANDO("Aguardando Efetivação"),
    DEFERIDO("Deferido"),
    INDEFERIDO("Indeferido para Correção"),
    INATIVO("Inativo");
    private String descricao;

    StatusMaterial(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public static List<StatusMaterial> getSituacoesDeferidoInativo() {
        return Lists.newArrayList(DEFERIDO, INATIVO);
    }

    public boolean isDeferido() {
        return DEFERIDO.equals(this);
    }

    public boolean isIndeferido() {
        return INDEFERIDO.equals(this);
    }

    public boolean isAguardando() {
        return AGUARDANDO.equals(this);
    }
}
