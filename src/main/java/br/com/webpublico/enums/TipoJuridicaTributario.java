/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.geradores.GrupoDiagrama;

/**
 * @author gustavo
 */
@GrupoDiagrama(nome = "Tributario")
public enum TipoJuridicaTributario {

    SOCIEDADE_POR_COTA_RESPONSABILIDAE("Sociedade por cota de Responsabilidade"),
    SOCIEDADE_POR_COTA_PARTICIPACAO("Sociedade por cota de Participação"),
    AUTARQUIA_FUNDACAO("Autarquia ou Fundação"),
    FIRMA_INDIVIDUAL("Firma Individual"),
    COOPERATIVA("Cooperativa"),
    SOCIEDADE_CIVIL("Sociedade Civil"),
    SOCIEDADE_EM_NOME_COLETIVO("Sociedade em Nome Coletivo"),
    SOCIEDADE_CAPITAL_INDUSTRIA("Sociedade de Capital e Indústria"),
    SOCIEDADE_COMANDITA_SIMPLES("Sociedade em Comandita Simples"),
    SOCIEDADE_COMANDITA_ACOES("Sociedade em Comandita por Ações"),
    SOCIEDADE_CIVIL_SEM_FINS_LUCRATIVOS("Sociedade Civil sem Fins Lucrativos"),
    SOCIEDADE_CIVIL_LIBERAIS_COM_FINS_LUCRATIVOS("Sociedade Civil Liberais com Fins Lucrativos"),
    DEMAIS_SOCIEDADES_CIVIS_COM_FINS_LUCRATIVOS("Demais Sociedades civis com Fins Lucrativos"),
    SOCIEDADE_CONTA_PARTICIPACAO("Sociedade em Conta de Participação"),
    SOCIEDADE_ANONIMA("Sociedade Anônima"),
    SOCIEDADE_ECONOMIA_MISTA("Sociedade de Economia Mista"),
    EMPRESA_PUBLICA("Empresa Pública"),
    FUNDACAO_COM_FINS_LUCRATIVOS("Fundação com Fins Lucrativos"),
    FUNDACAO_SEM_FINS_LUCRATIVOS("Fundação sem Fins Lucrativos"),
    ASSOCIACAO_COM_FINS_LUCRATIVOS("Associação com Fins Lucrativos"),
    ASSOCIACAO_SEM_FINS_LUCRATIVOS("Associação sem Fins Lucrativos"),
    AUTARQUIA("Autarquia"),
    ORGAO_PUBLICO("Órgão Público"),
    CLUBE_ESPORTIVO("Clube Esportivo"),
    OUTRAS_ASSOCIACOES("Outras Associações"),
    ORGANIZACAO_SOCIAL("Organização Social"),
    SINDICATO("Sindicato"),
    OSCIP("Organização da Sociedade Civil de Interesse Público (OSCIP)"),
    ENTIDADE_SINDICAL("Entidade Sindical"),
    SERVICO_SOCIAL_AUTONOMO("Serviço Social Autônomo"),
    SOCIEDADE_ANONIMA_ABERTA("Sociedade Anônima Aberta"),
    SOCIEDADE_ANONIMA_FECHADA("Sociedade Anônima Fechada"),
    CONDOMINIO_EDILICIO("Condomínio Edilício"),
    ORGAO_PUBLICO_EXECUTIVO_MUNICIPAL("Órgão Público do Poder Executivo Municipal"),
    OUTRAS_FUNDACOES_MANTIDAS_RECURSOS_PUBLICOS("Outras Fundações Mantidas com Recursos Públicos"),
    ASSOCIACAO_PRIVADA("Associação Privada"),
    AUTARQUIA_MUNICIPAL("Autarquia Municipal"),
    AUTARQUIA_ESTADUAL("Autarquia Estadual"),
    AUTARQUIA_FEDERAL("Autarquia Federal"),
    SERVICO_NOTARIAL_REGISTRAL_CARTORIO("Serviço Notarial e Registral (Cartório)"),
    OUTRAS("Outras");
    private String descricao;

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }

    private TipoJuridicaTributario(String descricao) {
        this.descricao = descricao;
    }
}
