package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.enums.NaturezaDividaSubvencao;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;

/**
 * Created by william on 18/08/17.
 */

public class ParcelaSubvencao extends SuperEntidade {
    private ResultadoParcela resultadoParcela;
    private NaturezaDividaSubvencao naturezaDividaSubvencao;
    private Boolean parcelamento;

    public ResultadoParcela getResultadoParcela() {
        return resultadoParcela;
    }

    public void setResultadoParcela(ResultadoParcela resultadoParcela) {
        this.resultadoParcela = resultadoParcela;
    }

    public NaturezaDividaSubvencao getNaturezaDividaSubvencao() {
        return naturezaDividaSubvencao;
    }

    public void setNaturezaDividaSubvencao(NaturezaDividaSubvencao naturezaDividaSubvencao) {
        this.naturezaDividaSubvencao = naturezaDividaSubvencao;
    }

    public Boolean getParcelamento() {
        return parcelamento;
    }

    public void setParcelamento(Boolean parcelamento) {
        this.parcelamento = parcelamento;
    }

    @Override
    public Long getId() {
        return null;
    }
}
