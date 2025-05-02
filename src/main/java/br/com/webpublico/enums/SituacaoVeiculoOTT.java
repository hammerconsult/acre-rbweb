package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.ott.enums.SituacaoVeiculoDTO;

public enum SituacaoVeiculoOTT implements EnumComDescricao {
    CADASTRADO("Cadastrado", SituacaoVeiculoDTO.CADASTRADO),
    AGUARDANDO_DOCUMENTACAO("Aguardando Documentação", SituacaoVeiculoDTO.AGUARDANDO_DOCUMENTACAO),
    DESATIVADO("Desativado", SituacaoVeiculoDTO.AGUARDANDO_DOCUMENTACAO),
    AGUARDANDO_VISTORIA("Aguardando vistoria", SituacaoVeiculoDTO.AGUARDANDO_VISTORIA),
    RETORNO_DOCUMENTACAO("Retorno Documentação", SituacaoVeiculoDTO.RETORNO_DOCUMENTACAO),
    APROVADO("Aprovado", SituacaoVeiculoDTO.APROVADO);

    private String descricao;
    private SituacaoVeiculoDTO situacaoVeiculoDTO;

    SituacaoVeiculoOTT(String descricao, SituacaoVeiculoDTO situacaoVeiculoDTO){
        this.descricao = descricao;
        this.situacaoVeiculoDTO = situacaoVeiculoDTO;
    }

    public String getDescricao() {
        return descricao;
    }

    public SituacaoVeiculoDTO getSituacaoVeiculoDTO() {
        return situacaoVeiculoDTO;
    }
}
