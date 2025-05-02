/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.geradores.GrupoDiagrama;

/**
 * @author Munif
 */
@GrupoDiagrama(nome = "Administrativo")
public enum PropositoAtoLegal {

    ORCAMENTO("Orçamento"),
    ALTERACAO_ORCAMENTARIA("Alteração Orçamentária"),
    RESERVA_DOTACAO("Reserva de Dotação"),
    DIRETRIZES_ORCAMENTARIAS("Diretrizes Orçamentárias"),
    PPA("PPA"),
    OUTROS("Outros"),
    ATO_DE_PESSOAL("Ato de Pessoal"),
    ATO_DE_CARGO("Ato de Cargo"),
    PROGRMACAO_ORACMENTARIA("Programação Orçamentária"),
    COMISSAO("Comissão"),
    TRIBUTARIO("Tributário"),
    OFICIO_BCE("Ofício de Cadastro Econômico"),
    CONCESSAO_BENEFICIO("Concessão de Benefício"),
    BENEFICIO_PREVIDENCIARIO("Benefício Previdenciário"),
    CONVENIO("Convênio"),
    ATO_LEGAL_CONCESSAO_DIARIA("Transparência - Ato legal de concessão de diária"),
    ATO_LEGAL_PRECATORIO("Transparência - Ato legal de precatório"),
    ATO_LEGAL_REPASSE("Transparência - Ato legal de repasse"),
    ATO_LEGAL_PESSOAL("Transparência - Ato legal de pessoal"),
    ATO_LEGAL_PLANO_MUNICIPAL("Transparência - Ato legal de plano municipal"),
    EDUCACAO("Educação"),
    CULTURA("Cultura"),
    HABITACAO("Habitação"),
    SANEAMENTO("Saneamento"),
    SAUDE("Saúde"),
    TRANSPORTE_PUBLICO("Transporte Público"),
    SEGURANCA("Segurança"),
    DESENVOLVIMENTO_SUSTENTAVEL("Desenvolvimento Sustentável"),
    GESTAO_URBANA("Gestão Urbana"),
    CONTRATACOES_EMERGENCIAIS("Contratações Emergenciais"),
    DOACOES("Doações"),
    MEDIDAS_FISCAIS("Medidas Fiscais"),
    ISOLAMENTO_SOCIAL("Isolamento Social"),
    POLITICA_SOCIAL("Políticas Sociais"),
    SERVICO_ESSENCIAIS("Serviços Essenciais");

    private String descricao;

    private PropositoAtoLegal(String d) {
        descricao = d;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
