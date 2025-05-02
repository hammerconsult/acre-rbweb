package br.com.webpublico.entidadesauxiliares.softplan.dto;

import br.com.webpublico.entidadesauxiliares.softplan.enums.TipoServicoSoftplan;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("inclusaoParcelamento")
public class InclusaoParcelamentoSoftPlanDTO extends ServicoSoftplanDTO<DadosInclusaoParcelamentoSoftPlanDTO> {

    public InclusaoParcelamentoSoftPlanDTO(Long idCda, Long idParcelamento) {
        super(idCda, idParcelamento, TipoServicoSoftplan.INCLUSAO_PARCELAMENTO);
    }

}
