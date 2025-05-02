package br.com.webpublico.enums;

/**
 * Criado por Mateus
 * Data: 24/05/2016.
 */
public enum SicapTipoArquivo {
    CARGOS("Cargos", "cargos", "cargos", "cargo", "/br/com/webpublico/validacaoxml/rh/sicap/cargo.xsd"),
    SERVIDORES("Servidores", "servidores", "servidores", "servidor", "/br/com/webpublico/validacaoxml/rh/sicap/servidor.xsd"),
    PENSIONISTAS("Pensionistas", "pensionistas", "pensionistas", "pensionista", "/br/com/webpublico/validacaoxml/rh/sicap/pensionista.xsd"),
    VERBAS("Verbas", "verbas", "verbas", "verba", "/br/com/webpublico/validacaoxml/rh/sicap/verbas.xsd"),
    TIPOS_DE_FOLHA("Tipos de Folha", "tiposFolha", "tiposFolha", "folha", "/br/com/webpublico/validacaoxml/rh/sicap/tiposFolha.xsd"),
    TABELAS_DE_VENCIMENTO("Tabelas de Vencimento", "tabelaVencimentos", "tabelasVencimentos", "tabela", "/br/com/webpublico/validacaoxml/rh/sicap/tabelasVencimento.xsd"),
    UNIDADES_DE_LOTACAO("Unidades de Lotação", "unidadesLotacao", "unidadesLotacao", "unidade", "/br/com/webpublico/validacaoxml/rh/sicap/unidadesLotacao.xsd"),
    HISTORICO_FUNCIONAL("Histórico Funcional", "historicoFuncional", "historicoFuncional", "movimentacao", "/br/com/webpublico/validacaoxml/rh/sicap/historicoFuncional.xsd"),
    CONTRACHEQUE("Contracheque", "contracheque", "contraCheques", "contraCheque", "/br/com/webpublico/validacaoxml/rh/sicap/contracheque.xsd"),
    RESCISAO("Rescisão", "rescisao", "rescisoes", "rescisao", "/br/com/webpublico/validacaoxml/rh/sicap/rescisao.xsd");

    SicapTipoArquivo(String descricao, String headerArquivo, String grupoArquivo, String linhaArquivo, String caminhoXsd) {
        this.descricao = descricao;
        this.headerArquivo = headerArquivo;
        this.grupoArquivo = grupoArquivo;
        this.linhaArquivo = linhaArquivo;
        this.caminhoXsd = caminhoXsd;
    }

    private String descricao;
    private String headerArquivo;
    private String grupoArquivo;
    private String linhaArquivo;
    private String caminhoXsd;

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getHeaderArquivo() {
        return headerArquivo;
    }

    public void setHeaderArquivo(String headerArquivo) {
        this.headerArquivo = headerArquivo;
    }

    public String getGrupoArquivo() {
        return grupoArquivo;
    }

    public void setGrupoArquivo(String grupoArquivo) {
        this.grupoArquivo = grupoArquivo;
    }

    public String getLinhaArquivo() {
        return linhaArquivo;
    }

    public void setLinhaArquivo(String linhaArquivo) {
        this.linhaArquivo = linhaArquivo;
    }

    public String getCaminhoXsd() {
        return caminhoXsd;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
