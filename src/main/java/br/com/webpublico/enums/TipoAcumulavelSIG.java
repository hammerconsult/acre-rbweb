package br.com.webpublico.enums;

/**
 * Created by AndreGustavo on 25/09/2014.
 */
public enum TipoAcumulavelSIG {
    NAO("Não acumulável", 1),
    PROFISSIONAL_SAUDE("Profissional de Saúde", 2),
    PROFESSOR("Professor", 3),
    OUTROS("Outros", 4);

    private Integer codigo;
    private String descricao;

    TipoAcumulavelSIG(String descricao, Integer codigo) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return getCodigo() + " - " + getDescricao();
    }
}
