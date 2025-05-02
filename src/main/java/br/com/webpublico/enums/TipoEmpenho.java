/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.webreportdto.dto.contabil.TipoEmpenhoDTO;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * @author venon
 */
public enum TipoEmpenho implements EnumComDescricao {

    ORDINARIO("Ordinário", TipoEmpenhoDTO.ORDINARIO),
    GLOBAL("Global", TipoEmpenhoDTO.GLOBAL),
    ESTIMATIVO("Estimativo", TipoEmpenhoDTO.ESTIMATIVO);
    private String descricao;
    private TipoEmpenhoDTO toDto;

    TipoEmpenho(String descricao, TipoEmpenhoDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    public static List<TipoEmpenho> getTipoEmpenhoPorFormaEntregaExecucaoContrato(FormaEntregaExecucao formaEntrega) {
        List<TipoEmpenho> toReturn = Lists.newArrayList();
        if (formaEntrega.isUnica()) {
            toReturn.add(TipoEmpenho.ORDINARIO);
        }
        if (formaEntrega.isParcelada()) {
            toReturn.add(TipoEmpenho.GLOBAL);
            toReturn.add(TipoEmpenho.ESTIMATIVO);
        }
        return toReturn;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

    public TipoEmpenhoDTO getToDto() {
        return toDto;
    }

    @Override
    public String toString() {
        return descricao;
    }

    public boolean isOrdinario() {
        return TipoEmpenho.ORDINARIO.equals(this);
    }
}
