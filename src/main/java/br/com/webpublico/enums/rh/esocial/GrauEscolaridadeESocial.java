package br.com.webpublico.enums.rh.esocial;

public enum GrauEscolaridadeESocial {

    ANALFABETO("Analfabeto, inclusive o que, embora tenha recebido instrução, não se alfabetizou",  "01"),
    QUINTO_ANO_INCOMPLETO("Até o 5º ano incompleto do ensino fundamental (antiga 4ª série) ou que se tenha alfabetizado sem ter frequentado escola regular",  "02"),
    QUINTO_ANO_COMPLETO("5º ano completo do ensino fundamental",  "03"),
    SEXTO_AO_NONO_ANO(" Do 6º ao 9º ano do ensino fundamental incompleto (antiga 5ª a 8ª série)",  "04"),
    FUNDAMENTAL_COMPLETO("Ensino fundamental completo",  "05"),
    MEDIO_INCOMPLETO("Ensino médio incompleto.",  "06"),
    MEDIO_COMPLETO("Ensino médio completo",  "07"),
    SUPERIOR_INCOMPLETO("Educação superior incompleta",  "08"),
    SUPERIOR_COMPLETO("Educação superior completa",  "09"),
    POS_GRADUACAO_COMPLETA("Pós-graduação completa",  "10"),
    MESTRADO_COMPLETO("Mestrado completo",  "11"),
    DOUTORADO_COMPLETO("Doutorado completo",  "12");

    private String descricao;
    private String codigo;

    private GrauEscolaridadeESocial(String descricao, String codigo) {
        this.descricao = descricao;
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getCodigo() {
        return codigo;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
