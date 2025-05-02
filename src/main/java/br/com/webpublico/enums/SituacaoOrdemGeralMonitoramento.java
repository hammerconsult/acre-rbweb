package br.com.webpublico.enums;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.EnumComDescricao;

/**
 * @author octavio
 */
@GrupoDiagrama(nome = "Fiscalização")
public enum SituacaoOrdemGeralMonitoramento implements EnumComDescricao {

    INICIADO("Iniciado"),
    DESIGNADO("Designado"),
    EXECUTANDO("Executando"),
    FINALIZADO("Finalizado"),
    CONCLUIDO("Concluído");

    private String descricao;

    private SituacaoOrdemGeralMonitoramento(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
