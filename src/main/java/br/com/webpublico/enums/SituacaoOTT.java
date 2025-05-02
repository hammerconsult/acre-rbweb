package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.ott.enums.SituacaoOttDTO;

public enum SituacaoOTT implements EnumComDescricao {

    CADASTRADO("Cadastrado", SituacaoOttDTO.CADASTRADO),
    AGUARDANDO_DOCUMENTACAO("Aguardando Documentação", SituacaoOttDTO.AGUARDANDO_DOCUMENTACAO),
    RETORNO_DOCUMENTACAO("Retorno Documentação", SituacaoOttDTO.RETORNO_DOCUMENTACAO),
    APROVADO("Aprovado", SituacaoOttDTO.APROVADO),
    REJEITADO("Rejeitado", SituacaoOttDTO.REJEITADO);

    private String descricao;
    private SituacaoOttDTO situacaoOttDTO;

    private SituacaoOTT(String descricao, SituacaoOttDTO situacaoOttDTO){
        this.descricao = descricao;
        this.situacaoOttDTO = situacaoOttDTO;
    }

    public String getDescricao(){
        return descricao;
    }

    public SituacaoOttDTO getSituacaoOttDTO() {
        return situacaoOttDTO;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
