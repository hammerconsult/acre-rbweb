package br.com.webpublico.controle.portaltransparencia.enums;

/**
 * Created by renato on 04/09/17.
 */
public enum ModuloSistema {
    PAGINA_INICIAL("Página inicial"),
    INDICADORES("Indicadores"),
    PORTAL("Portal"),
    RECEITA("Receita"),
    DESPESA("Despesa"),
    CONVENIO("Convênio"),
    REPASSE("Repasse"),
    PESSOAL("Pessoal"),
    PLANEJAMENTO("Planejamento"),
    BOLSA_FAMILIA("Bolsa Familia"),
    LICITACAO("Licitação"),
    PRESTACAO_CONTAS("Prestação de contas"),
    RESPONSABILIDADE_FISCAL("Responsabilidade Fiscal"),
    LEGISLACAO("Legislação"),
    PRECATORIO("Precatório"),
    CONTROLE_INTERNO("Controle Interno"),
    CALAMIDADE_PUBLICA("Calamidade Pública"),
    COVID_19("COVID-19"),
    BENS_PATRIMONIAIS("Bens Patrimoniais"),
    CONTRATO("Contratações"),
    OBRA("Obras"),
    INSTITUCIONAL("Institucional"),
    EMENDA("Emendas Parlamentares"),
    ORIENTACOES("Orientações"),
    DADOS_ABERTOS("Dados Abertos"),
    ENCONTRE_EM_RB("Encontre em Rio Branco");

    private String descricao;

    ModuloSistema(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
