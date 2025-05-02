package br.com.webpublico.enums;

/**
 * Created by AndreGustavo on 25/09/2014.
 */
public enum TipoContagemEspecial {
    NAO("Não", 1, 0),
    PROFESSOR("Professor", 2, 2),
    MAGISTRADO("Magistrado", 3, 1),
    MEMBRO_MINISTERIO_PUBLICO("Membro do Ministério Público", 4, 1),
    MEMBRO_TRIBUNAL_CONTAS("Membro do Tribunal de Contas", 5, 1),
    APOSENTADORIA_ESPECIAL("Aposentadoria Especial (EC 47/2005)", 6, 6),
    OUTROS("Outros", 7, 7),
    PROFESSOR_ENS_SUPERIOR("Professor Ens. Superior", 8, 5);

    private Integer codigoAtuarial;
    private Integer codigo;
    private String descricao;

    TipoContagemEspecial(String descricao, Integer codigo, Integer codigoAtuarial) {
        this.codigoAtuarial = codigoAtuarial;
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public static Integer getCodigoAtuarial(TipoContagemEspecial tipoContagemEspecial) {
        if (tipoContagemEspecial != null) {
            return tipoContagemEspecial.getCodigoAtuarial();
        }
        return TipoContagemEspecial.NAO.getCodigoAtuarial();
    }

    public Integer getCodigoAtuarial() {
        return codigoAtuarial;
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
}
