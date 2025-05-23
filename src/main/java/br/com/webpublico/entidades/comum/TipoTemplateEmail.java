package br.com.webpublico.entidades.comum;

public enum TipoTemplateEmail {
    SOLICITACAO_FORMULARIO_CADASTRO("Portal Contribuinte - Solicitação de Formulário de Cadastro"),
    REJEICAO_FORMULARIO_CADASTRO("Portal Contribuinte - Rejeição do Formulário de Cadastro"),
    REQUERIMENTO_LICENCIAMENTO_ETR("Portal Contribuinte - Requerimento de Licenciamento de ETR"),
    REGISTRO_EXIGENCIA_ETR("Portal Contribuinte - Registro de Exigência de ETR"),
    SOLICITACAO_ALVARA_IMEDIATO("Portal Contribuinte - Registro de Solicitação de Alvará Imediato"),
    REGISTRO_EXIGENCIA_ALVARA_IMEDIATO("Portal Contribuinte - Registro de Exigência de Alvará Imediato"),
    REJEICAO_DOCUMENTACAO_USUARIO_SAUD("Portal Contribuinte - Rejeição de Documentação para Usuário do SAUD"),
    SOLICITACAO_CADASTRO_CREDOR("Portal Contribuinte - Solicitação de Cadastro de Credor"),
    REJEICAO_CADASTRO_CREDOR("Portal Contribuinte - Rejeição do Cadastro de Credor"),
    REJEICAO_SOLICITACAO_ITBI_ONLINE("Portal Contribuinte - Rejeição da Solicitação de ITBI Online"),
    AVALIACAO_FISCAL_SOLICITACAO_ITBI_ONLINE("Portal Contribuinte - Avaliação do Fiscal sobre a Solicitação de ITBI Online");
    private String descricao;

    TipoTemplateEmail(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }

    public boolean isCadastroCredor() {
        return TipoTemplateEmail.SOLICITACAO_CADASTRO_CREDOR.equals(this) || TipoTemplateEmail.REJEICAO_CADASTRO_CREDOR.equals(this);
    }
}
