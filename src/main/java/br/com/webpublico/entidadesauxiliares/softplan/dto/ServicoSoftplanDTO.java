package br.com.webpublico.entidadesauxiliares.softplan.dto;

import br.com.webpublico.entidadesauxiliares.softplan.enums.TipoServicoSoftplan;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = InclusaoCDASoftplanDTO.class, name = "inclusaoCDA"),
    @JsonSubTypes.Type(value = AlteracaoCDASoftplanDTO.class, name = "alteracaoCDA"),
    @JsonSubTypes.Type(value = InclusaoParcelamentoSoftPlanDTO.class, name = "inclusaoParcelamento"),
    @JsonSubTypes.Type(value = AlteracaoParcelamentoSoftPlanDTO.class, name = "alteracaoParcelamento")
})
public abstract class ServicoSoftplanDTO<T extends DadosServicoSoftPlanDTO> {

    private TipoServicoSoftplan tipoServicoSoftplan;
    private Long idCda;
    private Long idParcelamento;
    private T dadosServico;

    public ServicoSoftplanDTO(Long idCda, Long idParcelamento, TipoServicoSoftplan tipoServicoSoftplan) {
        this.idCda = idCda;
        this.idParcelamento = idParcelamento;
        this.tipoServicoSoftplan = tipoServicoSoftplan;
    }

    public Long getIdCda() {
        return idCda;
    }

    public void setIdCda(Long idCda) {
        this.idCda = idCda;
    }

    public Long getIdParcelamento() {
        return idParcelamento;
    }

    public void setIdParcelamento(Long idParcelamento) {
        this.idParcelamento = idParcelamento;
    }

    public TipoServicoSoftplan getTipoServicoSoftplan() {
        return tipoServicoSoftplan;
    }

    public void setTipoServicoSoftplan(TipoServicoSoftplan tipoServicoSoftplan) {
        this.tipoServicoSoftplan = tipoServicoSoftplan;
    }

    public T getDadosServico() {
        return dadosServico;
    }

    public void setDadosServico(T dadosServico) {
        this.dadosServico = dadosServico;
    }
}
