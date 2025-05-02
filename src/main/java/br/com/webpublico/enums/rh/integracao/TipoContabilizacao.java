package br.com.webpublico.enums.rh.integracao;

import java.util.List;

public enum TipoContabilizacao {
    EMPENHO("Empenho", 2),
    RETENCAO_EXTRAORCAMENTARIA_CONSIGNACOES("Retenção ExtraOrçamentária (Consignações)", 4),
    OBRIGACAO_A_APAGAR("Obrigação a Pagar", 5);

    private String descricao;
    private Integer codigo;

    TipoContabilizacao(String descricao, Integer codigo) {
        this.descricao = descricao;
        this.codigo = codigo;
    }

    public static TipoContabilizacao getTipoContabilizacaoPorCodigo(Integer codigo) {
        for (TipoContabilizacao tipoContabilizacao : TipoContabilizacao.values()) {
            if (tipoContabilizacao.getCodigo().equals(codigo)) {
                return tipoContabilizacao;
            }
        }
        return null;
    }

    public static String montarClausulaIn(List<TipoContabilizacao> registros) {
        StringBuilder in = new StringBuilder();
        String juncao = "(";
        for (Enum item : registros) {
            in.append(juncao);
            in.append("'");
            in.append(item.name());
            in.append("'");
            juncao = ", ";
        }
        in.append(") ");
        return in.toString();
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
