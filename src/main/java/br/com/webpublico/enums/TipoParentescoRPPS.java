package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

/**
 * Created with IntelliJ IDEA.
 * User: Claudio
 * Date: 05/02/14
 * Time: 11:11
 * To change this template use File | Settings | File Templates.
 */
public enum TipoParentescoRPPS implements EnumComDescricao {
    FILHO("0", "Filho(a)", "2"),
    NETO("1", "Neto(a)", "6"),
    CONJUGE("2", "Cônjuge", "1"),
    COMPANHEIRO("3", "Companheiro(a)", "1"),
    ENTEADO("4", "Enteado(a)", "6"),
    PAI_MAE("5", "Pai(Mãe)", "4"),
    MENOR_TUTELADO("6", "Menor Tutelado", "6"),
    EX_CONJUGE("7", "Ex-Cônjuge", "6"),
    IRMAO("8", "Irmão(ã)", "5"),
    OUTROS("9", "Outros", "6"),
    FILHO_INVALIDO("10", "Filho Inválido", "3"),
    UNIAO_ESTAVEL("11", "União Estável", "6");

    private String codigo;
    private String descricao;
    private String codigoEstudoAtuarial;

    private TipoParentescoRPPS(String codigo, String descricao, String codigoEstudoAtuarial) {
        this.codigo = codigo;
        this.descricao = descricao;
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
}
