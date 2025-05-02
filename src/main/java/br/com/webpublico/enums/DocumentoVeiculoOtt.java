package br.com.webpublico.enums;

import br.com.webpublico.ott.enums.DocumentoVeiculoOttDTO;

public enum DocumentoVeiculoOtt {
    CERTIFICADO_REGISTRO_LICENCIAMENTO_VEICULO("Certificado de Registro de Licenciamento de Veículo", DocumentoVeiculoOttDTO.CERTIFICADO_REGISTRO_LICENCIAMENTO_VEICULO),
    OUTRO("Outros documentos", DocumentoVeiculoOttDTO.OUTRO);

    private String descricao;
    private DocumentoVeiculoOttDTO documentoVeiculoOttDTO;

    private DocumentoVeiculoOtt(String descricao, DocumentoVeiculoOttDTO documentoVeiculoOttDTO) {
        this.descricao = descricao;
        this.documentoVeiculoOttDTO = documentoVeiculoOttDTO;
    }

    public static DocumentoVeiculoOtt getEnumByDescricao(String descricao) {
        for (DocumentoVeiculoOtt value : values()) {
            if (value.getDescricao().equals(descricao)) {
                return value;
            }
        }
        return null;
    }

    public String getDescricao() {
        return descricao;
    }

    public DocumentoVeiculoOttDTO getDocumentoVeiculoOttDTO() {
        return documentoVeiculoOttDTO;
    }
}
