/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.nfse.enums.NfseEnum;
import br.com.webpublico.webreportdto.dto.tributario.TipoIssqnNfseDTO;

/**
 * @author gustavo
 */
public enum TipoIssqn implements NfseEnum, EnumComDescricao {
    ESTIMADO("Estimado", TipoIssqnNfseDTO.ESTIMADO, 1),
    FIXO("Fixo Anual", TipoIssqnNfseDTO.FIXO, 2),
    IMUNE("Imune", TipoIssqnNfseDTO.IMUNE, 8),
    ISENTO("Isento", TipoIssqnNfseDTO.ISENTO, 9),
    MEI("MEI", TipoIssqnNfseDTO.MEI, 4),
    MENSAL("Mensal", TipoIssqnNfseDTO.MENSAL, 3),
    NAO_INCIDENCIA("Não Incidência", TipoIssqnNfseDTO.NAO_INCIDENCIA,5),
    SIMPLES_NACIONAL("Simples Nacional", TipoIssqnNfseDTO.SIMPLES_NACIONAL, 6),
    SUBLIMITE_ULTRAPASSADO("Sublimite Ultrapassado", TipoIssqnNfseDTO.SUBLIMITE_ULTRAPASSADO, 7);

    private String descricao;
    private TipoIssqnNfseDTO dto;
    private Integer codigo;

    public String getDescricao() {
        return descricao;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public static TipoIssqn getPorDescricao(String descricao){
        for (TipoIssqn value : TipoIssqn.values()) {
            if(value.descricao.equalsIgnoreCase(descricao)){
                return value;
            }
        }
        return null;
    }


    TipoIssqn(String descricao, TipoIssqnNfseDTO dto, Integer codigo) {
        this.descricao = descricao;
        this.dto = dto;
        this.codigo = codigo;
    }

    @Override
    public String toString() {
        return descricao;
    }

    @Override
    public TipoIssqnNfseDTO toDto() {
        return dto;
    }
}
