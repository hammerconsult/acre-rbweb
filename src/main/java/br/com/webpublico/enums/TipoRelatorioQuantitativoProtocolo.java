package br.com.webpublico.enums;

import br.com.webpublico.webreportdto.dto.administrativo.TipoRelatorioQuantitativoProtocoloDTO;

public enum TipoRelatorioQuantitativoProtocolo {
    EXPEDIDOS("Expedidos", "QuantitativoProtocolosExpedidos.jrxml", TipoRelatorioQuantitativoProtocoloDTO.EXPEDIDOS),
    RECEBIDOS("Recebidos", "QuantitativoProtocolosRecebidos.jrxml", TipoRelatorioQuantitativoProtocoloDTO.RECEBIDOS),
    ENCAMINHADOS("Encaminhados", "QuantitativoProtocolosEncaminhado.jrxml", TipoRelatorioQuantitativoProtocoloDTO.ENCAMINHADOS),
    ARQUIVADOS("Arquivados", "QuantitativoProtocolosArquivado.jrxml", TipoRelatorioQuantitativoProtocoloDTO.ARQUIVADOS),
    FINALIZADOS("Finalizados", "QuantitativoProtocolosFinalizado.jrxml", TipoRelatorioQuantitativoProtocoloDTO.FINALIZADOS),
    ACEITOS("Aceitos", "QuantitativoProtocolosAceitos.jrxml", TipoRelatorioQuantitativoProtocoloDTO.ACEITOS);

    private String descricao;
    private String arquivoJrxml;
    private TipoRelatorioQuantitativoProtocoloDTO toDto;

    TipoRelatorioQuantitativoProtocolo(String descricao, String arquivoJrxml, TipoRelatorioQuantitativoProtocoloDTO toDto) {
        this.descricao = descricao;
        this.arquivoJrxml = arquivoJrxml;
        this.toDto = toDto;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getArquivoJrxml() {
        return arquivoJrxml;
    }

    public TipoRelatorioQuantitativoProtocoloDTO getToDto() {
        return toDto;
    }

    @Override
    public String toString() {
        return descricao;
    }

    public boolean isExpedidos() {
        return EXPEDIDOS.equals(this);
    }

    public boolean isRecebidos() {
        return RECEBIDOS.equals(this);
    }

    public boolean isEncaminhados() {
        return ENCAMINHADOS.equals(this);
    }

    public boolean isArquivados() {
        return ARQUIVADOS.equals(this);
    }

    public boolean isFinalizados() {
        return FINALIZADOS.equals(this);
    }

    public boolean isAceitos() {
        return ACEITOS.equals(this);
    }

}
