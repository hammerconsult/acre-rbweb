package br.com.webpublico.enums.rh.administracaopagamento;

import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.webreportdto.dto.rh.TipoVinculoAnaliseConformidadeDTO;


public enum TipoVinculoAnaliseConformidade implements EnumComDescricao {

    CONCURSADO("Concursado", 1L, TipoVinculoAnaliseConformidadeDTO.CONCURSADO),
    CARGO_EM_COMISSAO("Cargo em Comissão", 2L, TipoVinculoAnaliseConformidadeDTO.CARGO_EM_COMISSAO),
    AGENTE_POLITICO("Agente Político", 3L, TipoVinculoAnaliseConformidadeDTO.AGENTE_POLITICO),
    CONTRATO_TEMPORARIO("Contrato Temporário", 4L, TipoVinculoAnaliseConformidadeDTO.CONTRATO_TEMPORARIO),
    CONSELHEIRO_TUTELAR("Conselheiro Tutelar", 6L, TipoVinculoAnaliseConformidadeDTO.CONSELHEIRO_TUTELAR),
    CARGO_ELETIVO("Cargo Eletivo", 7L, TipoVinculoAnaliseConformidadeDTO.CARGO_ELETIVO),
    PRESTADOR_DE_SERVICO("Prestador de Serviço", 8L, TipoVinculoAnaliseConformidadeDTO.PRESTADOR_DE_SERVICO),
    FUNCAO_DE_COORDENACAO("Função de Coordenação", 9L, TipoVinculoAnaliseConformidadeDTO.FUNCAO_DE_COORDENACAO),
    ACESSO_CARGO_EM_COMISSAO("Acesso a Cargo em Comissão", 10L, TipoVinculoAnaliseConformidadeDTO.ACESSO_CARGO_EM_COMISSAO),
    ACESSO_FUNCAO_GRATIFICADA("Acesso a Função Gratificada", 11L, TipoVinculoAnaliseConformidadeDTO.ACESSO_FUNCAO_GRATIFICADA),
    APOSENTADO("Aposentado", 12L, TipoVinculoAnaliseConformidadeDTO.APOSENTADO),
    PENSIONISTA("Pensionista", 13L, TipoVinculoAnaliseConformidadeDTO.PENSIONISTA),
    ESTAGIARIO("Estagiário", 14L, TipoVinculoAnaliseConformidadeDTO.ESTAGIARIO);

    private String descricao;
    private Long codigo;
    private TipoVinculoAnaliseConformidadeDTO toDto;

    TipoVinculoAnaliseConformidade(String descricao, Long codigo, TipoVinculoAnaliseConformidadeDTO toDto) {
        this.descricao = descricao;
        this.codigo = codigo;
        this.toDto = toDto;
    }

    public String getDescricao() {
        return descricao;
    }

    public Long getCodigo() {
        return codigo;
    }

    public TipoVinculoAnaliseConformidadeDTO getToDto() {
        return toDto;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
