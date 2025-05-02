package br.com.webpublico.enums;

/**
 * Created with IntelliJ IDEA.
 * User: André Gustavo
 * Date: 27/06/13
 * Time: 10:36
 * To change this template use File | Settings | File Templates.
 */
public enum TipoFormato {
    VALOR_UFM("Valor em UFM - ###,##0.0000"),
    VALOR_REAIS("Valor em REAL - ###,##0.00"),
    DATA_HORA_SEGUNDOS("Data e Hora - dd/MM/yyyy HH:mm:ss"),
    HORA_MINUTO("Hora - HH:mm"),
    DATA("Data - dd/MM/yyyy"),
    DECIMAL("Decimal - #,##0.00");

    String descricao;

    private TipoFormato(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }


    @Override
    public String toString() {
        return getDescricao();
    }
}
