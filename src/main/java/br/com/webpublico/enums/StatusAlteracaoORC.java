package br.com.webpublico.enums;

import br.com.webpublico.webreportdto.dto.contabil.StatusAlteracaoORCDTO;

/**
 * Created with IntelliJ IDEA.
 * User: Buzatto
 * Date: 25/11/13
 * Time: 16:55
 * To change this template use File | Settings | File Templates.
 */
public enum StatusAlteracaoORC {
    ELABORACAO("Em Elaboração", StatusAlteracaoORCDTO.ELABORACAO),
    EM_ANALISE("Em análise", StatusAlteracaoORCDTO.EM_ANALISE),
    ESTORNADA("Estornada", StatusAlteracaoORCDTO.ESTORNADA),
    DEFERIDO("Deferida", StatusAlteracaoORCDTO.DEFERIDO),
    INDEFERIDO("Indeferida", StatusAlteracaoORCDTO.INDEFERIDO),
    EXCLUIDA("Excluída", StatusAlteracaoORCDTO.EXCLUIDA);
    private String descricao;
    private StatusAlteracaoORCDTO toDto;

    StatusAlteracaoORC(String descricao, StatusAlteracaoORCDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    public String getDescricao() {
        return descricao;
    }

    public StatusAlteracaoORCDTO getToDto() {
        return toDto;
    }

    @Override
    public String toString() {
        return descricao;
    }

    public boolean isElaboracao() {
        return StatusAlteracaoORC.ELABORACAO.equals(this);
    }

    public boolean isExcluido() {
        return StatusAlteracaoORC.EXCLUIDA.equals(this);
    }
}
