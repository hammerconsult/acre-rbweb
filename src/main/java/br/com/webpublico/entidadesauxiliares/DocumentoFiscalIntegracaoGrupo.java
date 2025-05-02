package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.Empenho;
import br.com.webpublico.entidades.GrupoBem;
import br.com.webpublico.entidades.GrupoMaterial;
import br.com.webpublico.enums.TipoContaDespesa;
import br.com.webpublico.enums.TipoGrupo;
import com.google.common.collect.Lists;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class DocumentoFiscalIntegracaoGrupo implements Serializable {

    private GrupoMaterial grupoMaterial;
    private GrupoMaterial grupoMaterialDesdobramento;
    private GrupoBem grupoBem;
    private GrupoBem grupoBemDesdobramento;
    private Empenho empenho;
    private TipoGrupo tipoGrupo;
    private BigDecimal valorGrupo;
    private Boolean integrador;
    private TipoContaDespesa tipoContaDespesa;
    private List<DocumentoFiscalIntegracaoGrupoItem> itens;

    public DocumentoFiscalIntegracaoGrupo() {
        itens = Lists.newArrayList();
        integrador = Boolean.FALSE;
        valorGrupo = BigDecimal.ZERO;
    }

    public GrupoBem getGrupoBemDesdobramento() {
        return grupoBemDesdobramento;
    }

    public void setGrupoBemDesdobramento(GrupoBem grupoBemDesdobramento) {
        this.grupoBemDesdobramento = grupoBemDesdobramento;
    }

    public List<DocumentoFiscalIntegracaoGrupoItem> getItens() {
        return itens;
    }

    public void setItens(List<DocumentoFiscalIntegracaoGrupoItem> itens) {
        this.itens = itens;
    }

    public GrupoBem getGrupoBem() {
        return grupoBem;
    }

    public void setGrupoBem(GrupoBem grupoBem) {
        this.grupoBem = grupoBem;
    }

    public TipoGrupo getTipoGrupo() {
        return tipoGrupo;
    }

    public void setTipoGrupo(TipoGrupo tipoGrupo) {
        this.tipoGrupo = tipoGrupo;
    }

    public BigDecimal getValorGrupo() {
        return valorGrupo;
    }

    public void setValorGrupo(BigDecimal valorGrupo) {
        this.valorGrupo = valorGrupo;
    }

    public boolean hasItens() {
        return itens != null && !itens.isEmpty();
    }

    public Boolean getIntegrador() {
        return integrador == null ? Boolean.FALSE : integrador;
    }

    public void setIntegrador(Boolean integrador) {
        this.integrador = integrador;
    }

    public GrupoMaterial getGrupoMaterial() {
        return grupoMaterial;
    }

    public void setGrupoMaterial(GrupoMaterial grupoMaterial) {
        this.grupoMaterial = grupoMaterial;
    }

    public GrupoMaterial getGrupoMaterialDesdobramento() {
        return grupoMaterialDesdobramento;
    }

    public void setGrupoMaterialDesdobramento(GrupoMaterial grupoMaterialDesdobramento) {
        this.grupoMaterialDesdobramento = grupoMaterialDesdobramento;
    }

    public TipoContaDespesa getTipoContaDespesa() {
        return tipoContaDespesa;
    }

    public void setTipoContaDespesa(TipoContaDespesa tipoContaDespesa) {
        this.tipoContaDespesa = tipoContaDespesa;
    }

    public Empenho getEmpenho() {
        return empenho;
    }

    public void setEmpenho(Empenho empenho) {
        this.empenho = empenho;
    }

    public boolean isBemMovel() {
        return getTipoContaDespesa() != null && getTipoContaDespesa().isBemMovel();
    }

    public boolean isEstoque() {
        return getTipoContaDespesa() != null && getTipoContaDespesa().isEstoque();
    }

    public BigDecimal getValorTotalLiquidadoGrupo() {
        BigDecimal valor = BigDecimal.ZERO;
        if (hasItens()) {
            for (DocumentoFiscalIntegracaoGrupoItem item : getItens()) {
                valor = valor.add(item.getValorLiquidado() != null ? item.getValorLiquidado() : BigDecimal.ZERO);
            }
        }
        return valor;
    }

    public BigDecimal getValorTotalALiquidarGrupo() {
        BigDecimal valor = BigDecimal.ZERO;
        if (hasItens()) {
            for (DocumentoFiscalIntegracaoGrupoItem item : getItens()) {
                valor = valor.add(item.getValorALiquidar());
            }
        }
        return valor;
    }

    public BigDecimal getValorTotalLancamento() {
        BigDecimal valor = BigDecimal.ZERO;
        if (hasItens()) {
            for (DocumentoFiscalIntegracaoGrupoItem item : getItens()) {
                valor = valor.add(item.getValorLancamento());
            }
        }
        return valor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DocumentoFiscalIntegracaoGrupo that = (DocumentoFiscalIntegracaoGrupo) o;
        if (isBemMovel()) return Objects.equals(grupoBem, that.grupoBem) && tipoGrupo == that.tipoGrupo;
        return Objects.equals(grupoMaterial, that.grupoMaterial);
    }

    @Override
    public int hashCode() {
        if (isBemMovel()) return Objects.hash(grupoBem, tipoGrupo);
        return Objects.hash(grupoMaterial);
    }
}
