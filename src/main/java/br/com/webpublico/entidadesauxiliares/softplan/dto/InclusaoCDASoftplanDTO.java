package br.com.webpublico.entidadesauxiliares.softplan.dto;

import br.com.webpublico.entidadesauxiliares.softplan.enums.TipoServicoSoftplan;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("inclusaoCDA")
public class InclusaoCDASoftplanDTO extends ServicoSoftplanDTO<DadosInclusaoCDASoftplanDTO> {

    public InclusaoCDASoftplanDTO(Long idCda, Long idParcelamento) {
        super(idCda, idParcelamento, TipoServicoSoftplan.INCLUSAO_CDA);
    }

}
