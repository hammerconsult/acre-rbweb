package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.enums.TipoMascaraUnidadeMedida;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Desenvolvimento on 28/09/2015.
 */
public class FormularioLoteMaterial {

    private Long idLote;
    private Long idLocalEstoque;
    private String identificacao;
    private Date validade;
    private BigDecimal quantidadeLote;
    private BigDecimal quantidadeSaida;
    private TipoMascaraUnidadeMedida mascaraQuantidade;
    private TipoMascaraUnidadeMedida mascaraValorUnitario;

    public FormularioLoteMaterial() {
        quantidadeLote = BigDecimal.ZERO;
        quantidadeSaida = BigDecimal.ZERO;
    }

    public Long getIdLote() {
        return idLote;
    }

    public void setIdLote(Long idLote) {
        this.idLote = idLote;
    }

    public String getIdentificacao() {
        return identificacao;
    }

    public void setIdentificacao(String identificacao) {
        this.identificacao = identificacao;
    }

    public Date getValidade() {
        return validade;
    }

    public void setValidade(Date validade) {
        this.validade = validade;
    }

    public BigDecimal getQuantidadeLote() {
        return quantidadeLote;
    }

    public void setQuantidadeLote(BigDecimal quantidadeLote) {
        this.quantidadeLote = quantidadeLote;
    }

    public BigDecimal getQuantidadeSaida() {
        return quantidadeSaida;
    }

    public void setQuantidadeSaida(BigDecimal quantidadeSaida) {
        this.quantidadeSaida = quantidadeSaida;
    }

    public Long getIdLocalEstoque() {
        return idLocalEstoque;
    }

    public void setIdLocalEstoque(Long idLocalEstoque) {
        this.idLocalEstoque = idLocalEstoque;
    }

    public boolean hasQuantidadeSaida() {
        return quantidadeSaida != null && quantidadeSaida.compareTo(BigDecimal.ZERO) > 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FormularioLoteMaterial that = (FormularioLoteMaterial) o;

        if (idLote != null ? !idLote.equals(that.idLote) : that.idLote != null) return false;
        return !(identificacao != null ? !identificacao.equals(that.identificacao) : that.identificacao != null);
    }

    @Override
    public int hashCode() {
        int result = idLote != null ? idLote.hashCode() : 0;
        result = 31 * result + (identificacao != null ? identificacao.hashCode() : 0);
        return result;
    }

    public static List<FormularioLoteMaterial> preencherFormularioLoteMaterialApartirDeArrayObject(List<Object[]> lista) {
        List<FormularioLoteMaterial> retorno = new ArrayList<>();
        for (Object[] obj : lista) {
            FormularioLoteMaterial formLote = new FormularioLoteMaterial();
            formLote.setIdLocalEstoque(((BigDecimal) obj[0]).longValue());
            formLote.setIdLote(((BigDecimal) obj[1]).longValue());
            formLote.setIdentificacao((String) obj[2]);
            formLote.setValidade((Date) obj[3]);
            formLote.setQuantidadeLote((BigDecimal) obj[4]);
            formLote.setQuantidadeSaida((BigDecimal) obj[5]);
            formLote.setMascaraQuantidade(obj[6] != null ? TipoMascaraUnidadeMedida.valueOf((String) obj[6]) : TipoMascaraUnidadeMedida.DUAS_CASAS_DECIMAL);
            formLote.setMascaraValorUnitario(obj[7] != null ? TipoMascaraUnidadeMedida.valueOf((String) obj[7]) : TipoMascaraUnidadeMedida.DUAS_CASAS_DECIMAL);
            retorno.add(formLote);
        }
        return retorno;
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
}
