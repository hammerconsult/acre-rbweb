package br.com.webpublico.enums;

import br.com.webpublico.ott.enums.SituacaoCondutorDTO;

public enum SituacaoCondutorOTT {
    CADASTRADO("Cadastrado", SituacaoCondutorDTO.CADASTRADO),
    AGUARDANDO_DOCUMENTACAO("Aguardando Documentação", SituacaoCondutorDTO.AGUARDANDO_DOCUMENTACAO),
    DESATIVADO("Desativado", SituacaoCondutorDTO.AGUARDANDO_DOCUMENTACAO),
    AGUARDANDO_VISTORIA("Aguardando vistoria", SituacaoCondutorDTO.AGUARDANDO_VISTORIA),
    RETORNO_DOCUMENTACAO("Retorno Documentação", SituacaoCondutorDTO.RETORNO_DOCUMENTACAO),
    APROVADO("Aprovado", SituacaoCondutorDTO.APROVADO);

    private String descricao;
    private SituacaoCondutorDTO situacaoCondutorDTO;

    SituacaoCondutorOTT(String descricao, SituacaoCondutorDTO situacaoCondutorDTO) {
        this.descricao = descricao;
        this.situacaoCondutorDTO = situacaoCondutorDTO;
    }

    public String getDescricao() {
        return descricao;
    }

    public SituacaoCondutorDTO getSituacaoCondutorDTO() {
        return situacaoCondutorDTO;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
