package br.com.webpublico.enums;

/**
 * Created by Mateus on 06/02/2015.
 */
public enum TipoMovimentoArquivoBancario {

    PAGAMENTO("Pagamento", "16"),
    PAGAMENTO_EXTRA("Despesa Extra", "26"),
    TRANSFERENCIA_FINANCEIRA("Transferência Financeira", "36"),
    TRANSFERENCIA_MESMA_UNIDADE("Transferência Mesma Unidade", "46"),
    LIBERACAO("Liberação", "56");

    private String descricao;
    private String codigo;

    private TipoMovimentoArquivoBancario(String descricao, String codigo) {
        this.descricao = descricao;
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
