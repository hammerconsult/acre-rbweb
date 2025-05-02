package br.com.webpublico.enums;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.EnumComDescricao;

/**
 * @author octavio
 */
@GrupoDiagrama(nome = "Fiscalização")
public enum SituacaoMonitoramentoFiscal implements EnumComDescricao {

    ADICIONADO("Adicionado"),
    DESIGNADO("Designado"),
    INICIADO("Iniciado"),
    COMUNICADO("Comunicado"),
    PENDENTE("Pendente"),
    CANCELADO("Cancelado"),
    FINALIZADO("Finalizado");

    private String descricao;

    private SituacaoMonitoramentoFiscal(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }
}
