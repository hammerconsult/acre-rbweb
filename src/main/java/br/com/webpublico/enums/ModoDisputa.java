package br.com.webpublico.enums;


import br.com.webpublico.interfaces.EnumComDescricao;
import com.google.common.collect.Lists;

import java.util.List;

public enum ModoDisputa implements EnumComDescricao {
    ABERTO("Aberto", 1),
    FECHADO("Fechado", 2),
    ABERTO_FECHADO("Aberto-Fechado", 3),
    DISPENSA_COM_DISPUTA("Dispensa com Disputa", 4),
    NAO_APLICAVEL("Não se Aplica", 5),
    FECHADO_ABERTO("Fechado-Aberto", 6);

    private String descricao;
    private Integer codigo;

    private ModoDisputa(String descricao, Integer codigo) {
        this.descricao = descricao;
        this.codigo = codigo;
    }

    public static List<ModoDisputa> modosDisputa(TipoAvaliacaoLicitacao tipoAvaliacao) {
        List<ModoDisputa> list = Lists.newArrayList();
        switch (tipoAvaliacao) {
            case MENOR_PRECO:
            case MAIOR_DESCONTO:
                list.add(ABERTO);
                list.add(ABERTO_FECHADO);
                break;
            case CONTEUDO_ARTISTICO:
            case MAIOR_LANCE:
            case MAIOR_RETORNO_ECONOMICO:
                list.add(ABERTO);
                list.add(ABERTO_FECHADO);
                list.add(FECHADO);
                break;
            case TECNICA_PRECO:
                list.add(ABERTO_FECHADO);
                list.add(FECHADO);
                break;
            default:
                list.add(NAO_APLICAVEL);
        }
        return list;
    }

    public static List<ModoDisputa> modosDisputaLei14133ModalidadeDispensa(TipoNaturezaDoProcedimentoLicitacao tipoNatureza) {
        List<ModoDisputa> list = Lists.newArrayList();
        list.add(NAO_APLICAVEL);
        if (tipoNatureza != null
            && (tipoNatureza.isEletronico() || tipoNatureza.isPresencial())) {
            list.add(DISPENSA_COM_DISPUTA);
        }
        return list;
    }

    public boolean isDispensaComDisputa() {
        return DISPENSA_COM_DISPUTA.equals(this);
    }

    public String getDescricao() {
        return descricao;
    }

    public Integer getCodigo() {
        return codigo;
    }
}
