package br.com.webpublico.entidades;

import br.com.webpublico.util.Util;

import java.io.Serializable;
import java.math.BigDecimal;

public class CadastrosNaoEnquadrados implements Serializable {

    private String inscricao;
    private String descricao;

    private CadastrosNaoEnquadrados(String inscricao) {
        this.inscricao = inscricao;
    }

    public String getInscricao() {
        return inscricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    private CadastrosNaoEnquadrados() {
    }

    public static CadastrosNaoEnquadrados newNaoEnquadradoPorQuantidadeImoveis(String inscricao, Integer qtdPermitida, Integer qtdAtual) {
        CadastrosNaoEnquadrados instancia = new CadastrosNaoEnquadrados(inscricao);
        instancia.setDescricao("O proprietário desse imóvel tem " + Util.formataNumero(qtdAtual) + " propriedades e a quantida maxima permitida é " + Util.formataNumero(qtdPermitida));
        return instancia;
    }

    public static CadastrosNaoEnquadrados newNaoEnquadradoPorAreaLote(String inscricao, BigDecimal areaPermitida, BigDecimal areaAtual) {
        CadastrosNaoEnquadrados instancia = new CadastrosNaoEnquadrados(inscricao);
        instancia.setDescricao("A área do lote é " + Util.formataNumero(areaAtual) + " e a máxima permitida é " + Util.formataNumero(areaPermitida));
        return instancia;
    }

    public static CadastrosNaoEnquadrados newNaoEnquadradoPorAreaConstruída(String inscricao, BigDecimal areaPermitida, BigDecimal areaAtual) {
        CadastrosNaoEnquadrados instancia = new CadastrosNaoEnquadrados(inscricao);
        instancia.setDescricao("A área total construída é " + Util.formataNumero(areaAtual) + " e a máxima permitida é " + Util.formataNumero(areaPermitida));
        return instancia;
    }
}
