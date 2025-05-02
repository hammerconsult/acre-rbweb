package br.com.webpublico.enums;

/**
 * Created with IntelliJ IDEA.
 * User: William
 * Date: 30/10/14
 * Time: 10:43
 * To change this template use File | Settings | File Templates.
 */
public enum ArquivoSiprev {
    GERAR_ARQUIVO_SERVIDORES("Gerar Arquivo Servidores"),
    GERAR_ARQUIVO_DEPENDENTES("Gerar Arquivo Dependentes"),
    GERAR_ARQUIVO_ORGAO("Gerar Arquivo Orgão"),
    GERAR_ARQUIVO_CARREIRAS("Gerar Arquivo Carreiras"),
    GERAR_ARQUIVO_CARGOS("Gerar Arquivo Cargos"),
    GERAR_ARQUIVO_BENEFICIO_SERVIDOR("Gerar Arquivo Benefício Servidor"),
    GERAR_ARQUIVO_FUNCAO_GRATIFICADA("Gerar Arquivo Função Gratificada"),
    GERAR_ARQUIVO_TEMPO_CONTRIBUICAO_RGPS("Gerar Arquivo Tempo de Contribuiçao RGPS"),
    GERAR_ARQUIVO_ALIQUOTA("Gerar Arquivo Alíquota"),
    GERAR_ARQUIVO_PENSIONISTA("Gerar Arquivo Pensionista"),
    GERAR_ARQUIVO_BENEFICIO_PENSIONISTA("Gerar Arquivo Benefício Pensionista"),
    GERAR_ARQUIVO_HISTORICO_FINANCEIRO("Gerar Arquivo Histórico Financeiro"),
    GERAR_ARQUIVO_HISTORICO_FUNCIONAL("Gerar Arquivo Histórico Funcional");

    private String descricao;

    private ArquivoSiprev(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;

    }
}
