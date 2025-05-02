package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.enums.TipoMascaraUnidadeMedida;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Desenvolvimento on 28/09/2015.
 */
public class FormularioLocalEstoque {
    private String codigoLocalEstoque;
    private String descricaLocalEstoque;
    private String tipoEstoque;
    private BigDecimal fisico;
    private BigDecimal financeiro;
    private BigDecimal qtdeDesejada;
    private Boolean controleLote;
    private Long idLocalEstoque;
    private Long idUnidadeOrcamentaria;
    private TipoMascaraUnidadeMedida mascaraQuantidade;
    private TipoMascaraUnidadeMedida mascaraValorUnitario;
    private List<FormularioLoteMaterial> listaLotes;

    public FormularioLocalEstoque() {
        listaLotes = Lists.newArrayList();
    }

    public static List<FormularioLocalEstoque> preencherFormularioApartirDaConsulta(List<Object[]> lista) {
        List<FormularioLocalEstoque> retorno = new ArrayList<>();
        for (Object[] obj : lista) {
            FormularioLocalEstoque formulario = new FormularioLocalEstoque();
            formulario.setCodigoLocalEstoque((String) obj[0]);
            formulario.setDescricaLocalEstoque((String) obj[1]);
            formulario.setTipoEstoque((String) obj[2]);
            formulario.setFisico((BigDecimal) obj[3]);
            formulario.setQtdeDesejada((BigDecimal) obj[4]);
            formulario.setFinanceiro((BigDecimal) obj[5]);
            formulario.setControleLote(((BigDecimal) obj[6]).compareTo(BigDecimal.ONE) == 0);
            formulario.setIdLocalEstoque(((BigDecimal) obj[7]).longValue());
            formulario.setMascaraQuantidade(obj[8] != null ? TipoMascaraUnidadeMedida.valueOf((String) obj[8]) : TipoMascaraUnidadeMedida.DUAS_CASAS_DECIMAL);
            formulario.setMascaraValorUnitario(obj[9] != null ? TipoMascaraUnidadeMedida.valueOf((String) obj[9]) : TipoMascaraUnidadeMedida.DUAS_CASAS_DECIMAL);
            formulario.setIdUnidadeOrcamentaria(((BigDecimal) obj[10]).longValue());
            retorno.add(formulario);

        }
        return retorno;
    }

    public String getCodigoLocalEstoque() {
        return codigoLocalEstoque;
    }

    public void setCodigoLocalEstoque(String codigoLocalEstoque) {
        this.codigoLocalEstoque = codigoLocalEstoque;
    }

    public String getDescricaLocalEstoque() {
        return descricaLocalEstoque;
    }

    public void setDescricaLocalEstoque(String descricaLocalEstoque) {
        this.descricaLocalEstoque = descricaLocalEstoque;
    }

    public String getTipoEstoque() {
        return tipoEstoque;
    }

    public void setTipoEstoque(String tipoEstoque) {
        this.tipoEstoque = tipoEstoque;
    }

    public BigDecimal getFisico() {
        return fisico;
    }

    public void setFisico(BigDecimal fisico) {
        this.fisico = fisico;
    }

    public BigDecimal getQtdeDesejada() {
        return qtdeDesejada;
    }

    public void setQtdeDesejada(BigDecimal qtdeDesejada) {
        this.qtdeDesejada = qtdeDesejada;
    }

    public BigDecimal getFinanceiro() {
        return financeiro;
    }

    public void setFinanceiro(BigDecimal financeiro) {
        this.financeiro = financeiro;
    }

    public Boolean getControleLote() {
        return controleLote;
    }

    public Long getIdLocalEstoque() {
        return idLocalEstoque;
    }

    public void setIdLocalEstoque(Long idLocalEstoque) {
        this.idLocalEstoque = idLocalEstoque;
    }

    public Long getIdUnidadeOrcamentaria() {
        return idUnidadeOrcamentaria;
    }

    public void setIdUnidadeOrcamentaria(Long idUnidadeOrcamentaria) {
        this.idUnidadeOrcamentaria = idUnidadeOrcamentaria;
    }

    public List<FormularioLoteMaterial> getListaLotes() {
        return listaLotes;
    }

    public void setListaLotes(List<FormularioLoteMaterial> listaLotes) {
        this.listaLotes = listaLotes;
    }

    public void setControleLote(Boolean controleLote) {
        this.controleLote = controleLote;
    }

    public TipoMascaraUnidadeMedida getMascaraQuantidade() {
        return mascaraQuantidade;
    }

    public void setMascaraQuantidade(TipoMascaraUnidadeMedida mascaraQuantidade) {
        this.mascaraQuantidade = mascaraQuantidade;
    }

    public TipoMascaraUnidadeMedida getMascaraValorUnitario() {
        return mascaraValorUnitario;
    }

    public void setMascaraValorUnitario(TipoMascaraUnidadeMedida mascaraValorUnitario) {
        this.mascaraValorUnitario = mascaraValorUnitario;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FormularioLocalEstoque that = (FormularioLocalEstoque) o;
        return idLocalEstoque.equals(that.idLocalEstoque);
    }

    @Override
    public int hashCode() {
        return idLocalEstoque.hashCode();
    }

    public String getDescricaoControleLote() {
        return getControleLote() ? "Sim" : "Não";
    }

    public BigDecimal getValorUnitario() {
        try {
            return getFinanceiro().divide(getFisico(), 8, RoundingMode.HALF_EVEN);
        } catch (ArithmeticException a) {
            return BigDecimal.ZERO;
        }
    }

    public String getQtdeFisicoFormatada() {
        return Util.formataQuandoDecimal(fisico, mascaraQuantidade);
    }
}
