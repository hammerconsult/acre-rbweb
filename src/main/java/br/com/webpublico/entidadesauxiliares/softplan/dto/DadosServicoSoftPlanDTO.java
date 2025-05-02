package br.com.webpublico.entidadesauxiliares.softplan.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = DadosInclusaoCDASoftplanDTO.class, name = "dadosInclusaoCDA"),
    @JsonSubTypes.Type(value = DadosAlteracaoCDASoftplanDTO.class, name = "dadosAlteracaoCDA"),
    @JsonSubTypes.Type(value = DadosInclusaoParcelamentoSoftPlanDTO.class, name = "dadosInclusaoParcelamento"),
    @JsonSubTypes.Type(value = DadosAlteracaoParcelamentoSoftPlanDTO.class, name = "dadosAlteracaoParcelamento")
})
public abstract class DadosServicoSoftPlanDTO {
}
