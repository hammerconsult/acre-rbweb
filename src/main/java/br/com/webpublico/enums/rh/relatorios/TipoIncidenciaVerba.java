package br.com.webpublico.enums.rh.relatorios;

/**
 * Created by zaca on 14/07/17.
 */
public enum TipoIncidenciaVerba {
    ATIVO("Ativo", 1L), INATIVO("Inativo", 0L);

    private String descricao;
    private Long valor;

    public String getDescricao() {
        return descricao;
    }

    public Long getValor() {
        return valor;
    }

    TipoIncidenciaVerba(String descricao, Long valor) {
        this.descricao = descricao;
        this.valor = valor;
    }

    @Override
    public String toString() {
        return getDescricao();
    }
}
