package br.com.webpublico.enums.administrativo;

import br.com.webpublico.webreportdto.dto.administrativo.OpcaoRelatorioCotacaoDTO;

public enum OpcaoRelatorioCotacao {
    COM_FORNECEDOR("Com Fornecedor", OpcaoRelatorioCotacaoDTO.COM_FORNECEDOR),
    SEM_FORNECEDOR("Sem Fornecedor", OpcaoRelatorioCotacaoDTO.SEM_FORNECEDOR),
    PRECOS_MINIMO_MEDIO_MEDIANO_MAXIMO("Preços Mínimo/Médio/Mediano/Máximo", OpcaoRelatorioCotacaoDTO.PRECOS_MINIMO_MEDIO_MEDIANO_MAXIMO);
    private String descricao;
    private OpcaoRelatorioCotacaoDTO toDto;

    OpcaoRelatorioCotacao(String descricao, OpcaoRelatorioCotacaoDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    public String getDescricao() {
        return descricao;
    }

    public OpcaoRelatorioCotacaoDTO getToDto() {
        return toDto;
    }

    @Override
    public String toString() {
        return this.descricao;
    }
}
