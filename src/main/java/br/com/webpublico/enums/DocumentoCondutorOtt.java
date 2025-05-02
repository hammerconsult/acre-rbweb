package br.com.webpublico.enums;

import br.com.webpublico.ott.enums.DocumentoCondutorOttDTO;

public enum DocumentoCondutorOtt {
    COMPROVACAO_BONS_ANTECEDENTES_CRIMINAIS("Comprovação de Bons Antecedentes Criminais", DocumentoCondutorOttDTO.COMPROVACAO_BONS_ANTECEDENTES_CRIMINAIS),
    CARTEIRA_NACIONAL_HABILITACAO("Carteira Nacional de Habilitação", DocumentoCondutorOttDTO.CARTEIRA_NACIONAL_HABILITACAO),
    CURSO_FORMAÇÃO_TRANSPORTE_PASSAGEIROS("Curso de Formação de Transporte de Passageiros", DocumentoCondutorOttDTO.CURSO_FORMAÇÃO_TRANSPORTE_PASSAGEIROS),
    SEGURO_ACIDENTES_PESSOAIS_PASSAGEIROS("Seguro de Acidentes Pessoais a Passageiros", DocumentoCondutorOttDTO.SEGURO_ACIDENTES_PESSOAIS_PASSAGEIROS),
    OUTRO("Outros documentos", DocumentoCondutorOttDTO.OUTRO);

    private String descricao;
    private DocumentoCondutorOttDTO documentoCondutorOttDTO;

    private DocumentoCondutorOtt(String descricao, DocumentoCondutorOttDTO documentoCondutorOttDTO) {
        this.descricao = descricao;
        this.documentoCondutorOttDTO = documentoCondutorOttDTO;
    }

    public static DocumentoCondutorOtt getEnumByDescricao(String descricao) {
        for (DocumentoCondutorOtt value : values()) {
            if (value.getDescricao().equals(descricao)) {
                return value;
            }
        }
        return null;
    }

    public String getDescricao() {
        return descricao;
    }

    public DocumentoCondutorOttDTO getDocumentoCondutorOttDTO() {
        return documentoCondutorOttDTO;
    }
}
