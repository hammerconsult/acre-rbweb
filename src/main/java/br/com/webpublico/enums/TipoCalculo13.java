package br.com.webpublico.enums;

/**
 * Created with IntelliJ IDEA.
 * User: mga
 * Date: 13/11/13
 * Time: 18:04
 * To change this template use File | Settings | File Templates.
 */
public enum TipoCalculo13 {
    /* Foi definido em 15/01/2015 por Alysson, Cleber, Felipe e Mailson que
       para as opções do turmalina da tabela arh025.sal13vba:
       Valor Turmalina     |  Valor WebPublico
            U              |        ULTIMO_ACUMULADO
            A              |        ULTIMO_ACUMULADO
            V              |        VALOR_ANO
            N              |        NÃO
            H              |        HORAS_ANO
           ***             |        LER_FORMULA (Ler fórmula é utilizado específicamente em 2 eventos de desconto do WP(500 e 640)
    */

    NAO("Não"),
    ULTIMO_ACUMULADO("Ultimo Acumulado"),
    HORAS_ANO("Horas Ano"),
    VALOR_ANO("Valor Ano"),
    LER_FORMULA("Ler Fórmula");

    private String descricao;

    private TipoCalculo13(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
