package br.com.webpublico.enums;

/**
 * Created with IntelliJ IDEA.
 * User: William
 * Date: 15/01/15
 * Time: 16:35
 * To change this template use File | Settings | File Templates.
 */
public enum TipoMagisterio {
    NAO("Não", "1"),
    INFANTIL("Infantil", "2"),
    FUNDAMENTAL("Fundamental", "3"),
    MEDIO("Médio", "4"),
    LEI("Lei 11.301/2006", "5"),
    SUPERIOR("Superior", "6");

    private String descricao;
    private String codigo;


    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    private TipoMagisterio(String descricao, String codigo) {
        this.descricao = descricao;
        this.codigo = codigo;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
