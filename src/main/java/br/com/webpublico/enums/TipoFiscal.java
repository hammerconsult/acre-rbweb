/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author Claudio
 */
public enum TipoFiscal {
    FISCAL_OBRAS("1 - Fiscal de Obras"),
    FISCAL_TRANSITO("2 - Fiscal de Trânsito"),
    FISCAL_TRANSITO_TRANSPORTE("3 - Fiscal de Trânsito / Transporte"),
    FISCAL_TRANSPORTE("4 - Fiscal de Transporte"),
    FISCAL_TRIBUTARIO("5 - Fiscal Tributário"),
    JULGADOR_CONTENCIOSO_ALVARA("6 - Julgador Contencioso Alvará"),
    JULGADOR_CONTENCIOSO_CMC("7 - Julgador Contencioso C.M.C"),
    JULGADOR_CONTENCIOSO_IPTU("8 - Julgador Contencioso IPTU"),
    JULGADOR_CONTECIOSO_ISSQN("9 - Julgador Contencioso ISSQN"),
    JULGADOR_CONTENCIOSO("10 - Julgador Contencioso"),
    PROCURADOR_MUNICIPAL("11 - Procurador Municipal"),
    RESPONSAVEL_JURIDICO("12 - Responsável Jurídico"),
    SERVIDOR_CADASTRO("13 - Servidor de Cadastro"),
    SERVIDOR_TRIBUTARIO("14 - Servidor Tributário"),
    TECNICO_ANALISE_PROJETO("15 - Técnico Análise Projeto Fiscal Sanitário"),
    FISCAL_MEIO_AMBIENTE("16 - Fiscal Meio Ambiente"),
    AVALIADOR_ITBI("17 - Avaliador de ITBI"),
    FISCAL_SANITARIO("18 - Fiscal Santário");

    private String descricao;

    public String getDescricao() {
        return descricao;
    }

    private TipoFiscal(String descricao) {
        this.descricao = descricao;
    }
}
