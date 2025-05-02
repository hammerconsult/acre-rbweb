/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.webreportdto.dto.comum.ModuloSistemaDTO;

/**
 * @author fabio
 */
public enum ModuloSistema {

    BI("B.I. - Business Intelligence", ModuloSistemaDTO.BI),
    CADASTROS("Cadastros Gerais", ModuloSistemaDTO.CADASTROS),
    CONFIGURACAO("Configurações", ModuloSistemaDTO.CONFIGURACAO),
    CONTRATOS("Contratos e Aditivos", ModuloSistemaDTO.CONTRATOS),
    CONTABIL("Contábil", ModuloSistemaDTO.CONTABIL),
    CONVENIOS("Convênios", ModuloSistemaDTO.CONVENIOS),
    DIVIDA_PUBLICA("Dívida Pública", ModuloSistemaDTO.DIVIDA_PUBLICA),
    EXECUCAO_ORCAMENTARIA("Execução Orçamentária", ModuloSistemaDTO.EXECUCAO_ORCAMENTARIA),
    FROTA("Frotas de Veículos", ModuloSistemaDTO.FROTA),
    FINANCEIRO("Financeiro", ModuloSistemaDTO.FINANCEIRO),
    GERENCIAMENTO("Gerenciamento de Recursos", ModuloSistemaDTO.GERENCIAMENTO),
    LICITACAO("Compras (Licitações)", ModuloSistemaDTO.LICITACAO),
    MATERIAIS("Materiais (Almoxarifado)", ModuloSistemaDTO.MATERIAIS),
    OBRAS("Obras", ModuloSistemaDTO.OBRAS),
    PATRIMONIO("Patrimônio Mobiliário e Imobiliário", ModuloSistemaDTO.PATRIMONIO),
    PLANEJAMENTO("Planejamento Público", ModuloSistemaDTO.PLANEJAMENTO),
    PROTOCOLO("Protocolo", ModuloSistemaDTO.PROTOCOLO),
    RBTRANS("Trânsito e Transporte (Rbtrans)", ModuloSistemaDTO.RBTRANS),
    RH("Recursos Humanos", ModuloSistemaDTO.RH),
    TRIBUTARIO("Tributário", ModuloSistemaDTO.TRIBUTARIO),
    NFSE("Nota Fiscal de Serviço Eletrônica", ModuloSistemaDTO.NFSE),
    FEIRAS("Feiras", ModuloSistemaDTO.FEIRAS);

    private String descricao;
    private ModuloSistemaDTO toDto;

    ModuloSistema(String descricao, ModuloSistemaDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    public String getDescricao() {
        return descricao;
    }

    public ModuloSistemaDTO getToDto() {
        return toDto;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
