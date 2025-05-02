package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.ott.enums.SituacaoRenovacaoDTO;

public enum SituacaoRenovacaoOTT implements EnumComDescricao {

    CADASTRADO("Cadastrado", SituacaoRenovacaoDTO.CADASTRADO),
    AGUARDANDO_DOCUMENTACAO("Aguardando Documentação", SituacaoRenovacaoDTO.AGUARDANDO_DOCUMENTACAO),
    RETORNO_DOCUMENTACAO("Retorno Documentação", SituacaoRenovacaoDTO.RETORNO_DOCUMENTACAO),
    APROVADO("Aprovado", SituacaoRenovacaoDTO.APROVADO),
    REJEITADO("Rejeitado", SituacaoRenovacaoDTO.REJEITADO);

    private String descricao;
    private SituacaoRenovacaoDTO situacaoRenovacaoDTO;

    SituacaoRenovacaoOTT(String descricao, SituacaoRenovacaoDTO situacaoRenovacaoDTO) {
        this.descricao = descricao;
        this.situacaoRenovacaoDTO = situacaoRenovacaoDTO;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

    public SituacaoRenovacaoDTO getSituacaoRenovacaoDTO() {
        return situacaoRenovacaoDTO;
    }

}
