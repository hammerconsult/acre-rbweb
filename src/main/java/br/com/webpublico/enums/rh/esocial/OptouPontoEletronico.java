package br.com.webpublico.enums.rh.esocial;

public enum OptouPontoEletronico {

    NAO_OPTOU_PONTO_ELETRONICO("Não optou pelo registro eletrônico de empregados", 0),
    OPTOU_PONTO_ELETRONICO("Optou pelo registro eletrônico de empregados", 1);

    private String descricao;
    private Integer codigo;

    OptouPontoEletronico(String descricao, Integer codigo) {
        this.descricao = descricao;
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public Integer getCodigo() {
        return codigo;
    }

    @Override
    public String toString() {
        return codigo + " - " + descricao;
    }
}
