package br.com.webpublico.consultaentidade;

import br.com.webpublico.enums.Operador;
import br.com.webpublico.util.Util;

import javax.faces.model.SelectItem;
import java.util.List;

public enum TipoCampo {

    ENUM("Enum"),
    DATE("Data"),
    DATE_TIME("Data/Hora"),
    STRING("Texto"),
    INTEGER("Inteiro"),
    BOOLEAN("Boleano"),
    LINK("Link"),
    DECIMAL("Decimal"),
    MONETARIO("Monetário"),
    SELECT("Select"),
    MULTI_SELECT("Multi Select"),
    PERIODO("Período"),
    MES_ANO("Mês/Ano"),
    ARQUIVO("Arquivo"),
    IMAGEM("Imagem");

    private String descricao;

    TipoCampo(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }

    public boolean is(String name) {
        return this.name().equals(name);
    }

    public List<SelectItem> getSelectItens() {
        return Util.getListSelectItemSemCampoVazio(getOperadorPorTipo(), false);
    }

    public List<SelectItem> getSelectItensWithNull() {
        return Util.getListSelectItem(getOperadorPorTipo(), false);
    }

    public boolean isNumeric(){
        return DECIMAL.equals(this) || INTEGER.equals(this) || MONETARIO.equals(this);
    }

    public Operador[] getOperadorPorTipo() {
        switch (this) {
            case DATE:
            case INTEGER:
            case MONETARIO:
            case DECIMAL:
            case MES_ANO:
                return new Operador[]{Operador.IGUAL, Operador.DIFERENTE, Operador.MAIOR, Operador.MAIOR_IGUAL, Operador.MENOR, Operador.MENOR_IGUAL};
            case ENUM:
            case BOOLEAN:
                return new Operador[]{Operador.IGUAL, Operador.IS_NULL, Operador.IS_NOT_NULL};
            case STRING:
            case SELECT:
                return new Operador[]{Operador.IGUAL, Operador.DIFERENTE, Operador.LIKE, Operador.NOT_LIKE};
            default:
                return Operador.values();
        }
    }
}
