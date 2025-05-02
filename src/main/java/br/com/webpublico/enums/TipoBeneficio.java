package br.com.webpublico.enums;


import br.com.webpublico.interfaces.EnumComDescricao;

public enum TipoBeneficio implements EnumComDescricao {
    PARTICIPACAO_EXCLUSIVA(1, "Participação Exclusiva para ME/EPP"),
    SUBCONTRATACAO(2, "Subcontratação para ME/EPP"),
    COTA_RESERVADA(3, "Cota Reservada para ME/EPP"),
    SEM_BENEFICIO(4, "Sem Benefício"),
    NAO_SE_APLICA(5, "Não se Aplica");

    private Integer codigo;
    private String descricao;

    private TipoBeneficio(Integer codigo, String descricao) {
        this.descricao = descricao;
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public boolean isParticipacaoExclusiva() {
        return TipoBeneficio.PARTICIPACAO_EXCLUSIVA.equals(this);
    }

    public boolean isCotaReservadaME() {
        return TipoBeneficio.COTA_RESERVADA.equals(this);
    }

    public boolean isSemBeneficio() {
        return TipoBeneficio.SEM_BENEFICIO.equals(this);
    }

    @Override
    public String toString() {
        return descricao;
    }
}
