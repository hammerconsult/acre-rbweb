package br.com.webpublico.pncp.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum DocumentoPncp {

    AVISO_CONTRATACAO_DIRETA(1, "Aviso de Contratação Direta"),
    EDITAL(2, "Edital"),
    MINUTA_CONTRATO(3, "Minuta do Contrato"),
    TERMO_REFERENCIA(4, "Termo de Referência"),
    ANTEPROJETO(5, "Anteprojeto"),
    PROJETO_BASICO(6, "Projeto Básico"),
    ESTUDO_TECNICO_PRELIMINAR(7, "Estudo Técnico Preliminar"),
    PROJETO_EXECUTIVO(8, "Projeto Executivo"),
    MAPA_RISCOS(9, "Mapa de Riscos"),
    DFD(10, "DFD"),
    ATA_REGISTRO_PRECO(11, "Ata de Registro de Preço"),
    CONTRATO(12, "Contrato"),
    TERMO_RESCISAO(13, "Termo de Rescisão"),
    TERMO_ADITIVO(14, "Termo Aditivo"),
    TERMO_APOSTILAMENTO(15, "Termo de Apostilamento"),
    OUTROS_DOCUMENTOS(16, "Outros Documentos"),
    NOTA_EMPENHO(17, "Nota de Empenho"),
    RELATORIO_FINAL_CONTRATO(18, "Relatório Final de Contrato");

    private final Integer codigo;
    private final String descricao;

    DocumentoPncp(Integer codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return codigo.toString();
    }

    @JsonValue
    public Integer toValue() {
        return codigo;
    }
}
