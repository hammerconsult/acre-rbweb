package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.*;
import br.com.webpublico.util.AssistenteBarraProgresso;
import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

public class AssistenteExportacaoDebitosIPTU extends AssistenteBarraProgresso {
    private ExportacaoDebitosIPTU selecionado;
    private ConfiguracaoDAM configuracaoDAM;
    private boolean isPerfilDev;
    private boolean encontrouParcelas;

    private List<DAM> dans;
    private List<ItemDAM> itensDAM;
    private List<HistoricoImpressaoDAM> impressoesDAM;
    private List<TributoDAM> tributosDAM;

    private List<VOParcelaExportacaoDebitosIPTU> parcelasIPTU;
    private List<VOLinhasExportacaoDebitosIPTU> linhas;
    private Map<Long, List<VOParcelaExportacaoDebitosIPTU>> parcelasPorValorDivida;

    public AssistenteExportacaoDebitosIPTU() {
        super();
        this.encontrouParcelas = true;
        this.dans = Lists.newArrayList();
        this.itensDAM = Lists.newArrayList();
        this.impressoesDAM = Lists.newArrayList();
        this.tributosDAM = Lists.newArrayList();
        this.parcelasIPTU = Lists.newArrayList();
        this.linhas = Lists.newArrayList();
        this.parcelasPorValorDivida = Maps.newHashMap();
    }

    @Override
    public ExportacaoDebitosIPTU getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(ExportacaoDebitosIPTU selecionado) {
        this.selecionado = selecionado;
    }

    public boolean isPerfilDev() {
        return isPerfilDev;
    }

    public void setPerfilDev(boolean perfilDev) {
        isPerfilDev = perfilDev;
    }

    public ConfiguracaoDAM getConfiguracaoDAM() {
        return configuracaoDAM;
    }

    public void setConfiguracaoDAM(ConfiguracaoDAM configuracaoDAM) {
        this.configuracaoDAM = configuracaoDAM;
    }

    public boolean isEncontrouParcelas() {
        return encontrouParcelas;
    }

    public void setEncontrouParcelas(boolean encontrouParcelas) {
        this.encontrouParcelas = encontrouParcelas;
    }

    public List<DAM> getDans() {
        return dans;
    }

    public void setDans(List<DAM> dans) {
        this.dans = dans;
    }

    public List<ItemDAM> getItensDAM() {
        return itensDAM;
    }

    public void setItensDAM(List<ItemDAM> itensDAM) {
        this.itensDAM = itensDAM;
    }

    public List<HistoricoImpressaoDAM> getImpressoesDAM() {
        return impressoesDAM;
    }

    public void setImpressoesDAM(List<HistoricoImpressaoDAM> impressoesDAM) {
        this.impressoesDAM = impressoesDAM;
    }

    public List<TributoDAM> getTributosDAM() {
        return tributosDAM;
    }

    public void setTributosDAM(List<TributoDAM> tributosDAM) {
        this.tributosDAM = tributosDAM;
    }

    public List<VOParcelaExportacaoDebitosIPTU> getParcelasIPTU() {
        return parcelasIPTU;
    }

    public void setParcelasIPTU(List<VOParcelaExportacaoDebitosIPTU> parcelasIPTU) {
        this.parcelasIPTU = parcelasIPTU;
    }

    public List<VOLinhasExportacaoDebitosIPTU> getLinhas() {
        return linhas;
    }

    public void setLinhas(List<VOLinhasExportacaoDebitosIPTU> linhas) {
        this.linhas = linhas;
    }

    public Map<Long, List<VOParcelaExportacaoDebitosIPTU>> getParcelasPorValorDivida() {
        return parcelasPorValorDivida;
    }

    public void setParcelasPorValorDivida(Map<Long, List<VOParcelaExportacaoDebitosIPTU>> parcelasPorValorDivida) {
        this.parcelasPorValorDivida = parcelasPorValorDivida;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AssistenteExportacaoDebitosIPTU that = (AssistenteExportacaoDebitosIPTU) o;
        return Objects.equal(selecionado.getId(), that.selecionado.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(selecionado.getId());
    }
}
