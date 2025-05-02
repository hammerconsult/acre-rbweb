/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.webreportdto.dto.administrativo.TipoClassificacaoFornecedorDTO;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author lucas
 */
public enum TipoClassificacaoFornecedor implements EnumComDescricao {
    HABILITADO("Habilitado", TipoClassificacaoFornecedorDTO.HABILITADO),
    HABILITADOCOMRESSALVA("Habilitado com Ressalva", TipoClassificacaoFornecedorDTO.HABILITADOCOMRESSALVA),
    INABILITADO("Inabilitado", TipoClassificacaoFornecedorDTO.INABILITADO),
    DESCLASSIFICADO("Desclassificado", TipoClassificacaoFornecedorDTO.DESCLASSIFICADO),
    AUSENTE("Ausente", TipoClassificacaoFornecedorDTO.AUSENTE);
    private String descricao;
    private TipoClassificacaoFornecedorDTO toDto;

    TipoClassificacaoFornecedor(String descricao, TipoClassificacaoFornecedorDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

    public TipoClassificacaoFornecedorDTO getToDto() {
        return toDto;
    }

    public static List<TipoClassificacaoFornecedor> getNaoHabilitados() {
        List<TipoClassificacaoFornecedor> retorno = new ArrayList<>();
        retorno.add(TipoClassificacaoFornecedor.INABILITADO);
        retorno.add(TipoClassificacaoFornecedor.DESCLASSIFICADO);
        retorno.add(TipoClassificacaoFornecedor.AUSENTE);
        return retorno;
    }

    public static List<TipoClassificacaoFornecedor> getHabilitados() {
        List<TipoClassificacaoFornecedor> retorno = new ArrayList<>();
        retorno.add(TipoClassificacaoFornecedor.HABILITADO);
        retorno.add(TipoClassificacaoFornecedor.HABILITADOCOMRESSALVA);
        return retorno;
    }

    public static List<TipoClassificacaoFornecedor> getTodosTipos() {
        return Arrays.asList(values());
    }

    public static List<String> listStringStatus(List<TipoClassificacaoFornecedor> status) {
        List<String> toReturn = Lists.newArrayList();
        if (status != null) {
            for (TipoClassificacaoFornecedor tipo : status) {
                toReturn.add(tipo.name());
            }
        }
        return toReturn;
    }
}
