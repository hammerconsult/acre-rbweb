package br.com.webpublico.enums;

import br.com.webpublico.geradores.GrupoDiagrama;

/**
 * @author octavio
 */
@GrupoDiagrama(nome = "Fiscalização")
public enum TipoLevantamentoContabilMonitoramentoFiscal {

    SIMPLES_NACIONAL("Simples Nacional"),
    NFSE("NFS-e"),
    OUTRO("Outro");

    private String descricao;

    private TipoLevantamentoContabilMonitoramentoFiscal(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
