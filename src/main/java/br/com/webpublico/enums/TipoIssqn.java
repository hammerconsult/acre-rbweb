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
    ESTIMADO("Estimado", 1, TipoIssqnNfseDTO.ESTIMADO),
    FIXO("Fixo Anual", 2, TipoIssqnNfseDTO.FIXO),
    MENSAL("Mensal", 3, TipoIssqnNfseDTO.MENSAL),
    MEI("MEI", 4, TipoIssqnNfseDTO.MEI),
    NAO_INCIDENCIA("Não Incidência", 5, TipoIssqnNfseDTO.NAO_INCIDENCIA),
    SIMPLES_NACIONAL("Simples Nacional", 6, TipoIssqnNfseDTO.SIMPLES_NACIONAL),
    SUBLIMITE_ULTRAPASSADO("Sublimite Ultrapassado", 7, TipoIssqnNfseDTO.SUBLIMITE_ULTRAPASSADO),
    IMUNE("Imune", 8, TipoIssqnNfseDTO.IMUNE),
    ISENTO("Isento", 9, TipoIssqnNfseDTO.ISENTO);

    private String descricao;
    private Integer codigo;
    private TipoIssqnNfseDTO dto;

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

    TipoIssqn(String descricao, Integer codigo, TipoIssqnNfseDTO dto) {
        this.descricao = descricao;
        this.codigo = codigo;
        this.dto = dto;
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
