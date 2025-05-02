package br.com.webpublico.enums.rh;

/**
 * Created by William on 25/06/2018.
 */
public enum TipoAutorizacaoRH {
    PERMITIR_ALTERAR_EXCLUIR_SITUACAO_FUNCIONAL("Permitir alterar e excluir Situação Funcional"),
    PERMITIR_EXCLUIR_CONCESSAO_FOLHA_PROCESSADA("Permitir excluir Concessão de Férias com folha processada."),
    PERMITIR_EXCLUIR_ENQUADRAMENTO_FUNCIONAL_FOLHA_PROCESSADA("Permitir excluir enquadramento funcional com folha processada."),
    PERMITIR_ALTERAR_INFORME_RENDIMENTO("Permitir realizar alterações do Informe de Rendimentos."),
    PERMITIR_LANCAMENTO_AVERBACAO_CONTRATOS_ENCERRADOS("Permitir lançamento de averbação para contratos encerrados."),
    PERMITIR_CONFIGURACAO_RETROACAO_DECIMO_TERCEIRO("Permitir parametrizar retroação em folhas de 13º e adiantamento de 13º"),
    PERMITIR_ALTERAR_EXCLUIR_RECURSOFP_HORARIO_LOTACAO("Permitir alterar e excluir RecursoFP, Horário de Trabalho e Lotação Funcional."),
    PERMITIR_REALIZAR_CONCESSAO_FERIAS_MES_ANO_PAGAMENTO_INFERIOR_FINAL_PA("Permitir realizar concessão de férias com o Mês/Ano do Pagamento inferior à data de aniversário do Período Aquisitivo."),
    PERMITIR_EXCLUIR_CEDENCIA_FICHA_FINANCEIRA_VINCULADA("Permitir excluir Cedência com ficha financeira vinculada.");

    private String descricao;

    TipoAutorizacaoRH(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
