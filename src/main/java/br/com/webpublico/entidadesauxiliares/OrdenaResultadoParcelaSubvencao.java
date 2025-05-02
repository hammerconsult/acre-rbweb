package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.OrdenacaoParcelaSubvencao;
import br.com.webpublico.enums.TipoOrdenacaoSubvencao;

import java.util.Comparator;
import java.util.List;

/**
 * Created by william on 18/08/17.
 */
public class OrdenaResultadoParcelaSubvencao implements Comparator<ParcelaSubvencao> {

    private List<OrdenacaoParcelaSubvencao> ordenacaoParcelaSubvencao;

    public OrdenaResultadoParcelaSubvencao(List<OrdenacaoParcelaSubvencao> ordenacaoParcelaSubvencao) {
        this.ordenacaoParcelaSubvencao = ordenacaoParcelaSubvencao;
    }

    @Override
    public int compare(ParcelaSubvencao o1, ParcelaSubvencao o2) {
        int i = 0;
        for (OrdenacaoParcelaSubvencao ordenacao : ordenacaoParcelaSubvencao) {
            if (i == 0 && TipoOrdenacaoSubvencao.DATA_LANCAMENTO_CRESCENTE.equals(ordenacao.getTipoOrdenacaoSubvencao())) {
                i = o1.getResultadoParcela().getEmissao().compareTo(o2.getResultadoParcela().getEmissao());
            }
            if (i == 0 && TipoOrdenacaoSubvencao.DATA_LANCAMENTO_DECRESCENTE.equals(ordenacao.getTipoOrdenacaoSubvencao())) {
                i = o2.getResultadoParcela().getEmissao().compareTo(o1.getResultadoParcela().getEmissao());
            }
            if (i == 0 && TipoOrdenacaoSubvencao.DATA_VENCIMENTO_CRESCENTE.equals(ordenacao.getTipoOrdenacaoSubvencao())) {
                i = o1.getResultadoParcela().getVencimento().compareTo(o2.getResultadoParcela().getVencimento());
            }
            if (i == 0 && TipoOrdenacaoSubvencao.DATA_VENCIMENTO_DECRESCENTE.equals(ordenacao.getTipoOrdenacaoSubvencao())) {
                i = o2.getResultadoParcela().getVencimento().compareTo(o1.getResultadoParcela().getVencimento());
            }
            if (i == 0 && TipoOrdenacaoSubvencao.VALOR_TOTAL_CRESCENTE.equals(ordenacao.getTipoOrdenacaoSubvencao())) {
                i = o1.getResultadoParcela().getValorTotal().compareTo(o2.getResultadoParcela().getValorTotal());
            }
            if (i == 0 && TipoOrdenacaoSubvencao.VALOR_TOTAL_DECRESCENTE.equals(ordenacao.getTipoOrdenacaoSubvencao())) {
                i = o2.getResultadoParcela().getValorTotal().compareTo(o1.getResultadoParcela().getValorTotal());
            }
            if (o2.getNaturezaDividaSubvencao() != null && o1.getNaturezaDividaSubvencao() != null) {
                if (i == 0 && TipoOrdenacaoSubvencao.NATUREZA_TRIBUTARIA.equals(ordenacao.getTipoOrdenacaoSubvencao())) {
                    i = o1.getNaturezaDividaSubvencao().compareTo(o2.getNaturezaDividaSubvencao());
                }
                if (i == 0 && TipoOrdenacaoSubvencao.NATUREZA_NAO_TRIBUTARIA.equals(ordenacao.getTipoOrdenacaoSubvencao())) {
                    i = o2.getNaturezaDividaSubvencao().compareTo(o1.getNaturezaDividaSubvencao());
                }
            }
        }
        return i;
    }
}

