package br.com.webpublico.enums;

public enum TipoArquivoTransferenciaMovBCI {
    SIMULACAO ("RelatorioSimulacaoMovBCI", 1),
    OUTROS( 2);

    private String nomeArquivo;
    private final int ordenacao;

    TipoArquivoTransferenciaMovBCI(String nomeArquivo, int ordenacao) {
        this.nomeArquivo = nomeArquivo;
        this.ordenacao = ordenacao;
    }

    TipoArquivoTransferenciaMovBCI(int ordenacao) {
        this.ordenacao = ordenacao;
    }

    public String getNomeArquivo() {
        return nomeArquivo;
    }

    public int getOrdenacao() {
        return ordenacao;
    }
}
