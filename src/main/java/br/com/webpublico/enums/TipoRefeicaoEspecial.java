package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum TipoRefeicaoEspecial implements EnumComDescricao {

    ZERO_LACTOSE("Zero Lactose (Intolerância à lactose)"),
    ZERO_GLUTEN("Zero Glúten (Celíaca - Sensibilidade ou intolerância ao glúten)"),
    ZERO_CARNE("Zero Carne (Vegetarianismo, veganismo e vertentes)"),
    BAIXO_ACUCAR("Baixo Teor de Açúcares (Diabetes)"),
    RESTRICAO_RELIGIOSA("Restrições Alimentares Religiosas "),
    SELETIVIDADE_AUTISMO("Seletividade pelo Autismo"),
    OUTRAS_ALERGIAS("Outras Alergias"),
    OUTRAS_RESTRICOES("Outras Restrições");

    private String descricao;

    TipoRefeicaoEspecial(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }
}
