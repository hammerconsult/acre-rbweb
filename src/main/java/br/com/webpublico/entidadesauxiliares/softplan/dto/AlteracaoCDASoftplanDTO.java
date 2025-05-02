package br.com.webpublico.entidadesauxiliares.softplan.dto;

import br.com.webpublico.entidadesauxiliares.softplan.enums.TipoServicoSoftplan;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("alteracaoCDA")
public class AlteracaoCDASoftplanDTO extends ServicoSoftplanDTO<DadosAlteracaoCDASoftplanDTO> {

    public AlteracaoCDASoftplanDTO(Long idCda, Long idParcelamento) {
        super(idCda, idParcelamento, TipoServicoSoftplan.ALTERACAO_CDA);
    }

}
