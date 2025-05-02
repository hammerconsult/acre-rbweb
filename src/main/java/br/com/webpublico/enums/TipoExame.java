package br.com.webpublico.enums;

/**
 * Created by carlos on 19/06/15.
 */
public enum TipoExame {

    ADMISSIONAL("Exame médico admissional", 0),
    PERIÓDICO("Exame médico periódico", 1),
    RETORNO_TRABALHO("Exame médico de retorno ao trabalho", 2),
    MUDANÇA_FUNCAO_OU_RISCO_OCUPACIONAL("Exame médico de mudança de função ou de mudança de risco ocupacional", 3),
    MONITORAMENTO_PESSOAL("Exame médico de monitoração pontual, não enquadrado nos demais casos", 4),
    DEMISSIONAL("Demissional", 9);

    private String descricao;

    private Integer codigo;

    TipoExame(String descricao, Integer codigo) {
        this.descricao = descricao;
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    @Override
    public String toString() {
        return codigo + " - " + descricao;
    }
}
