package br.com.webpublico.negocios.jdbc;

/**
 * Created by venom on 20/02/15.
 */
public class MapContaAuxiliar {
    private String codigo;
    private Long contaContabil;
    private Long planoDeContas;

    public MapContaAuxiliar() {
    }

    public MapContaAuxiliar(String codigo, Long contaContabil, Long planoDeContas) {
        this.codigo = codigo;
        this.contaContabil = contaContabil;
        this.planoDeContas = planoDeContas;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Long getContaContabil() {
        return contaContabil;
    }

    public void setContaContabil(Long contaContabil) {
        this.contaContabil = contaContabil;
    }

    public Long getPlanoDeContas() {
        return planoDeContas;
    }

    public void setPlanoDeContas(Long planoDeContas) {
        this.planoDeContas = planoDeContas;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MapContaAuxiliar that = (MapContaAuxiliar) o;

        if (!codigo.equals(that.codigo)) return false;
        if (!contaContabil.equals(that.contaContabil)) return false;
        if (!planoDeContas.equals(that.planoDeContas)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = codigo.hashCode();
        result = 31 * result + contaContabil.hashCode();
        result = 31 * result + planoDeContas.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "MapContaAuxiliar{" +
                "codigo='" + codigo + '\'' +
                ", contaContabil=" + contaContabil +
                ", planoDeContas=" + planoDeContas +
                '}';
    }
}
