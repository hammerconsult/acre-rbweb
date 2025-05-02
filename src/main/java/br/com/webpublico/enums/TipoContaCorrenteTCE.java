package br.com.webpublico.enums;

import br.com.webpublico.webreportdto.dto.contabil.TipoContaCorrenteTCEDTO;

/**
 * Created by Edi on 29/03/2016.
 */
public enum TipoContaCorrenteTCE {
    NAO_APLICAVEL("Não Aplicável", "00", TipoContaCorrenteTCEDTO.NAO_APLICAVEL),
    EDUCACAO_FUNDEB_MAGISTERIO("Educação Fundeb Magistério", "11", TipoContaCorrenteTCEDTO.EDUCACAO_FUNDEB_MAGISTERIO),
    EDUCACAO_FUNDEB_MAGISTERIO_CRECHE("Educação Fundeb Creche", "12", TipoContaCorrenteTCEDTO.EDUCACAO_FUNDEB_MAGISTERIO_CRECHE),
    EDUCACAO_FUNDEB_MAGISTERIO_PRE_ESCOLA("Educação Fundeb Pré-Escola", "13", TipoContaCorrenteTCEDTO.EDUCACAO_FUNDEB_MAGISTERIO_PRE_ESCOLA),
    EDUCACAO_FUNDEB_OUTROS("Educação Fundeb Outros", "21", TipoContaCorrenteTCEDTO.EDUCACAO_FUNDEB_OUTROS),
    EDUCACAO_FUNDEB_OUTROS_CRECHE("Educação Fundeb Outros Creche", "22", TipoContaCorrenteTCEDTO.EDUCACAO_FUNDEB_OUTROS_CRECHE),
    EDUCACAO_FUNDEB_OUTROS_PRE_ESCOLA("Educação Fundeb Outros Pré-Escola", "23", TipoContaCorrenteTCEDTO.EDUCACAO_FUNDEB_OUTROS_PRE_ESCOLA),
    MANUTENCAO_DESENVOLVIMENTO_ENSINO_MDE("Manutenção e Desenvolvimento do Ensino - MDE", "31", TipoContaCorrenteTCEDTO.MANUTENCAO_DESENVOLVIMENTO_ENSINO_MDE),
    MDE_CRECHE("MDE - Creche", "32", TipoContaCorrenteTCEDTO.MDE_CRECHE),
    MDE_PRE_ESCOLA("MDE - Pré-Escola", "33", TipoContaCorrenteTCEDTO.MDE_PRE_ESCOLA),
    APLICACAO_ACOES_SERVICOS_PUBLICOS_SAUDE_ASPS("Aplicação em Ações e Serviços Públicos de Saúde - ASPS", "41", TipoContaCorrenteTCEDTO.APLICACAO_ACOES_SERVICOS_PUBLICOS_SAUDE_ASPS),
    CUSTEIO_ACOES_SERVICOS_PUBLICOS_SAUDE("Custeio das Ações e Serviços Públicos de Saúde", "42", TipoContaCorrenteTCEDTO.CUSTEIO_ACOES_SERVICOS_PUBLICOS_SAUDE),
    INVESTIMENTO_REDE_SERVICOS_PUBLICOS_SAUDE("Investimento na Rede de Serviços Públicos de Saúde", "43", TipoContaCorrenteTCEDTO.INVESTIMENTO_REDE_SERVICOS_PUBLICOS_SAUDE),
    INATIVOS_E_PENSIONISTAS_COM_RECURSOS_VINCULADOS("Inativos e Pensionistas com Recursos Vinculados", "51", TipoContaCorrenteTCEDTO.INATIVOS_E_PENSIONISTAS_COM_RECURSOS_VINCULADOS);
    private String descricao;
    private String codigo;
    private TipoContaCorrenteTCEDTO toDto;

    TipoContaCorrenteTCE(String descricao, String codigo, TipoContaCorrenteTCEDTO toDto) {
        this.descricao = descricao;
        this.codigo = codigo;
        this.toDto = toDto;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getCodigo() {
        return codigo;
    }

    public TipoContaCorrenteTCEDTO getToDto() {
        return toDto;
    }

    @Override
    public String toString() {
        return codigo + " - " + descricao;
    }
}
