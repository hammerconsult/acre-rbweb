package br.com.webpublico.enums;

import br.com.webpublico.webreportdto.dto.comum.OperacaoRelatorioDTO;

/**
 * Created with IntelliJ IDEA.
 * User: usuario
 * Date: 30/05/14
 * Time: 08:18
 * To change this template use File | Settings | File Templates.
 */
public enum OperacaoRelatorio {

    BETWEEN("between", OperacaoRelatorioDTO.BETWEEN),
    IGUAL("=", OperacaoRelatorioDTO.IGUAL),
    MAIOR_IGUAL(">=", OperacaoRelatorioDTO.MAIOR_IGUAL),
    MAIOR(">", OperacaoRelatorioDTO.MAIOR),
    MENOR_IGUAL("<=", OperacaoRelatorioDTO.MENOR_IGUAL),
    MENOR("<", OperacaoRelatorioDTO.MENOR),
    IN("in", OperacaoRelatorioDTO.IN),
    NOT_IN("not in", OperacaoRelatorioDTO.NOT_IN),
    LIKE("like", OperacaoRelatorioDTO.LIKE),
    DIFERENTE("<>", OperacaoRelatorioDTO.DIFERENTE),
    IS_NULL(" is null ", OperacaoRelatorioDTO.IS_NULL),
    IS_NOT_NULL(" is not null ", OperacaoRelatorioDTO.IS_NOT_NULL);

    private String descricao;
    private OperacaoRelatorioDTO toDto;

    OperacaoRelatorio(String descricao, OperacaoRelatorioDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    public String getDescricao() {
        return descricao;
    }

    public OperacaoRelatorioDTO getToDto() {
        return toDto;
    }
}
