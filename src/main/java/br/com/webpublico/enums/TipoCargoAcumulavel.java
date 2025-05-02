package br.com.webpublico.enums;

/**
 * Created by AndreGustavo on 25/09/2014.
 */
public enum TipoCargoAcumulavel {
    NAO_ACUMULAVEL("Não acumulável", 1),
    PROFISSIONAL_SAUDE("Profissional de Saúde", 2),
    PROFESSOR("Professor", 3),
    TECNICO_CIENTIFICO("Técnico/Cientifico", 4);

    private Integer codigo;
    private String descricao;

    TipoCargoAcumulavel(String descricao, Integer codigo) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return getCodigo() + " - " + getDescricao();
    }
}
