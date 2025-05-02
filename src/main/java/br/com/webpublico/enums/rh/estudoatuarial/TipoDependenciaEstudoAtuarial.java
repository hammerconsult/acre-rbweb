package br.com.webpublico.enums.rh.estudoatuarial;

public enum TipoDependenciaEstudoAtuarial {
    CONJUGE(" Cônjuge", "1", "1"),
    COMPANHEIRO("Companheiro(a)", "2", "1"),
    FILHO_MENOR_NAO_EMANCIPADO("Filho(a) menor não emancipado(a)", "3","2"),
    FILHO_INVALIDO("Filho(a) inválido(a)", "4", "3"),
    PAI_MAE_COM_DEPENDENCIA_ECONOMICA("Pai(mãe) com dependência econômica", "5", "4"),
    ENTEADO_MENOR_NAO_EMANCIPADO_COM_DEPENDENCIA_ECONOMICA("Enteado(a) menor não emancipado(a) com dependência econômica", "6", "6"),
    ENTEADO_INVALIDO_COM_DEPENDENCIA_ECONOMICA("Enteado(a) inválido(a) com dependência econômica", "7", "6"),
    IRMAO_MENOR_NAO_EMANCIPADO_COM_DEPENDENCIA_ECONOMICA("Irmão(a) menor não emancipado(a) com dependência econômica" , "8", "5"),
    IRMAO_INVALIDO_COM_DEPENDENCIA_ECONOMICA("Irmão(a) inválido(a) com dependência econômica", "9", "5"),
    MENOR_TUTELADO("Menor tutelado", "10", "6"),
    NETO("Neto", "11", "6"),
    EX_CONJUGE_QUE_RECEBA_PENSAO_DE_ALIMENTOS("Ex-cônjuge que receba pensão de alimentos", "12", "6"),
    OUTROS("Outros", "99", "6");

    private String descricao;
    private String codigo;
    private String codigoEstudoAtuarial;

    TipoDependenciaEstudoAtuarial(String descricao, String codigo, String codigoEstudoAtuarial) {
        this.descricao = descricao;
        this.codigo = codigo;
        this.codigoEstudoAtuarial = codigoEstudoAtuarial;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getCodigoEstudoAtuarial() {
        return codigoEstudoAtuarial;
    }

    public static String getByCodigo(String codigo) {
        for (TipoDependenciaEstudoAtuarial tipo : values()) {
            if (tipo.name().equals(codigo)) {
                return tipo.getCodigo();
            }
        }
        return "";
    }

    @Override
    public String toString() {
        return codigo + " - " + descricao;
    }
}
