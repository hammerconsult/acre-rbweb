package br.com.webpublico.enums;

public enum TipoGestor {
    PROTOCOLO, MATERIAIS, LICITACAO, PATRIMONIO, ORCAMENTARIO, FINANCEIRO;

    public static String getNomeColunaGestor(TipoGestor  tipoGestor){
        switch (tipoGestor){
            case PROTOCOLO:
                return "gestorProtocolo";
            case MATERIAIS:
                return "gestorMateriais";
            case LICITACAO:
                return "gestorLicitacao";
            case PATRIMONIO:
                return "gestorPatrimonio";
            case ORCAMENTARIO:
                return "gestorOrcamento";
            case FINANCEIRO:
                return "gestorFinanceiro";
            default:
                return "";
        }
    }
}
