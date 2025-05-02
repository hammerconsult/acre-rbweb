/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.webreportdto.dto.contabil.NaturezaDividaPublicaDTO;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * @author venon
 */
public enum NaturezaDividaPublica implements EnumComDescricao {

    NAO_APLICAVEL("Não Aplicável", NaturezaDividaPublicaDTO.NAO_APLICAVEL),
    OPERACAO_CREDITO("Operação de Crédito", NaturezaDividaPublicaDTO.OPERACAO_CREDITO),
    OBRIGACOES_PESSOAL("Obrigações de Pessoal", NaturezaDividaPublicaDTO.OBRIGACOES_PESSOAL),
    DEMAIS_OBRIGACOES("Demais Obrigações", NaturezaDividaPublicaDTO.DEMAIS_OBRIGACOES),
    OBRIGACOES_FORNECEDORES("Obrigações com Fornecedores", NaturezaDividaPublicaDTO.OBRIGACOES_FORNECEDORES),
    PRECATORIO("Precatório", NaturezaDividaPublicaDTO.PRECATORIO),
    PASSIVO_ATUARIAL("Passivo Atuarial", NaturezaDividaPublicaDTO.PASSIVO_ATUARIAL),
    OBRIGACOES_FISCAIS("Obrigacoes Fiscais", NaturezaDividaPublicaDTO.OBRIGACOES_FISCAIS);
    private String descricao;
    private NaturezaDividaPublicaDTO toDto;

    NaturezaDividaPublica(String descricao, NaturezaDividaPublicaDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

    public NaturezaDividaPublicaDTO getToDto() {
        return toDto;
    }

    @Override
    public String toString() {
        return descricao;
    }

    public static List<NaturezaDividaPublica> naturezasPorContrato() {
        List<NaturezaDividaPublica> toReturn = Lists.newArrayList();
        toReturn.add(OPERACAO_CREDITO);
        toReturn.add(OBRIGACOES_FISCAIS);
        toReturn.add(DEMAIS_OBRIGACOES);
        toReturn.add(OBRIGACOES_PESSOAL);
        return toReturn;
    }

    public static List<String> montarClausulaInPorContrato() {
        List<String> toReturn = Lists.newArrayList();
        for (NaturezaDividaPublica naturezaDividaPublica : naturezasPorContrato()) {
            toReturn.add(naturezaDividaPublica.name());
        }
        return toReturn;
    }
}
