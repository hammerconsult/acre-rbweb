package br.com.webpublico.enums;

/**
 * @author octavio
 */
public enum TipoArquivoExportacaoDebitosIPTU {
    RBC800("RBC800");

    private String descricao;

    TipoArquivoExportacaoDebitosIPTU(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
