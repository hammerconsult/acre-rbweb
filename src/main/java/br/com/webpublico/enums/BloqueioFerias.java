package br.com.webpublico.enums;

/**
 * Created with IntelliJ IDEA.
 * User: Paschualleto
 * Date: 11/11/14
 * Time: 09:07
 * To change this template use File | Settings | File Templates.
 */
public enum BloqueioFerias {

    NAO_CALCULAVEL("Não Calculável"),
    NAO_LANCAVEL("Não Lançável"),
    NAO_CALCULAVEL_NAO_LANCAVEL("Não Calculável/Não Lançável"),
    SEM_EFEITO("Sem Efeito");

    private String descricao;

    private BloqueioFerias(String descricao){
        this.descricao = descricao;
    }

    @Override
    public String toString(){
        return descricao;
    }
}
