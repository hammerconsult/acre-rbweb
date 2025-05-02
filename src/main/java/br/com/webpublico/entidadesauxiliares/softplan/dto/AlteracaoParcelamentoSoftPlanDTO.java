package br.com.webpublico.entidadesauxiliares.softplan.dto;

import br.com.webpublico.entidadesauxiliares.softplan.enums.TipoServicoSoftplan;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("alteracaoParcelamento")
public class AlteracaoParcelamentoSoftPlanDTO extends ServicoSoftplanDTO<DadosAlteracaoParcelamentoSoftPlanDTO> {

    public AlteracaoParcelamentoSoftPlanDTO(Long idCda, Long idParcelamento) {
        super(idCda, idParcelamento, TipoServicoSoftplan.ALTERACAO_PARCELAMENTO);
    }

}
