package br.com.webpublico.enums;

import br.com.webpublico.ott.enums.DocumentoOperadoraTransporteDTO;

public enum DocumentosOperadoraTrasporte {
    CARTAO_CNPJ("Cartão CNPJ da empresa", DocumentoOperadoraTransporteDTO.CARTAO_CNPJ),
    DOCUMENTO_DE_IDENTIFICACAO("Documento de Identificação nacional do responsável técnico (com RG e CPF)", DocumentoOperadoraTransporteDTO.DOCUMENTO_DE_IDENTIFICACAO),
    DOCUMENTO_DE_COMPROBACAO("Documento de comprobação de relação institucional do responsável técnico com a empresa", DocumentoOperadoraTransporteDTO.DOCUMENTO_DE_COMPROBACAO),
    OUTRO("Outros documentos", DocumentoOperadoraTransporteDTO.OUTRO);

    private String descricao;
    private DocumentoOperadoraTransporteDTO documentoOperadoraTransporteDTO;

    private DocumentosOperadoraTrasporte(String descricao, DocumentoOperadoraTransporteDTO documentoOperadoraTransporteDTO) {
        this.descricao = descricao;
        this.documentoOperadoraTransporteDTO = documentoOperadoraTransporteDTO;
    }

    public static DocumentosOperadoraTrasporte getEnumByDescricao(String descricao) {
        for (DocumentosOperadoraTrasporte value : values()) {
            if (value.getDescricao().equals(descricao)) {
                return value;
            }
        }
        return null;
    }

    public String getDescricao() {
        return descricao;
    }

    public DocumentoOperadoraTransporteDTO getDocumentoOperadoraTransporteDTO() {
        return documentoOperadoraTransporteDTO;
    }
}
