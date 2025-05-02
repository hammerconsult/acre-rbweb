package br.com.webpublico.enums.rh.esocial;

public enum TipoPontoEletronicoESocial {
    NAO_UTILIZA("Não utiliza", 0),
    MANUAL("Manual", 1),
    MECANICO("Mecânico", 2),
    ELETRONICO_PORTARIA_1510("Eletrônico (portaria MTE 1.510/2009)", 3),
    NAO_ELETRONICO_ALTERNATIVO("Não eletrônico alternativo (art. 1° da Portaria MTE 373/2011)", 4),
    ELETRONICO_ALTERNATIVO("Eletrônico alternativo ( art. 2° da Portaria MTE 373/2011)", 5),
    ELETRONICO_OUTROS("Eletrônico - outros", 6);

    private String descricao;
    private Integer codigo;

    TipoPontoEletronicoESocial(String descricao, Integer codigo) {
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
