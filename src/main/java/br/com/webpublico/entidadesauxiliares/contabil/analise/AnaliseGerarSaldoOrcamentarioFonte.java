package br.com.webpublico.entidadesauxiliares.contabil.analise;

import br.com.webpublico.entidades.FonteDespesaORC;
import br.com.webpublico.entidadesauxiliares.contabil.apiservicecontabil.SaldoFonteDespesaORCVO;
import com.google.common.collect.Lists;

import java.util.List;

public class AnaliseGerarSaldoOrcamentarioFonte {

    private FonteDespesaORC fonte;
    private List<SaldoFonteDespesaORCVO> saldos;


    public AnaliseGerarSaldoOrcamentarioFonte(FonteDespesaORC fonteDespesaORC) {
        this.fonte = fonteDespesaORC;
        saldos = Lists.newArrayList();
    }

    public List<SaldoFonteDespesaORCVO> getSaldos() {
        return saldos;
    }

    public void setSaldos(List<SaldoFonteDespesaORCVO> saldos) {
        this.saldos = saldos;
    }

    public FonteDespesaORC getFonte() {
        return fonte;
    }

    public void setFonte(FonteDespesaORC fonte) {
        this.fonte = fonte;
    }
}
