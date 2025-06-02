package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.ReservaEstoque;
import br.com.webpublico.enums.TipoMascaraUnidadeMedida;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

public class ConsultaLocalEstoqueMaterial implements Comparable<ConsultaLocalEstoqueMaterial> {

    private Long idMaterial;
    private Long codigo;
    private String descricao;
    private String unidadeMedida;
    private String grupoMaterial;
    private Boolean medicoHospitalar;
    private Boolean controleLote;
    private BigDecimal quantidadeEstoque;
    private BigDecimal valorEstoque;
    private BigDecimal valorUnitario;
    private TipoMascaraUnidadeMedida mascaraQuantidade;
    private TipoMascaraUnidadeMedida mascaraValorUnitario;
    private List<ConsultaLocalEstoqueLoteMaterial> lotes;
    private List<ReservaEstoque> reservasEstoque;
    private MovimentacaoGrupoMaterial movimentacaoGrupoMaterial;


    public ConsultaLocalEstoqueMaterial() {
        controleLote = false;
        medicoHospitalar = false;
        reservasEstoque = Lists.newArrayList();
    }

    public BigDecimal getValorEstoque() {
        return valorEstoque;
    }

    public void setValorEstoque(BigDecimal valorEstoque) {
        this.valorEstoque = valorEstoque;
    }

    public BigDecimal getQuantidadeEstoque() {
        if (getControleLote()) {
            return getQuantidadeTotalLotes();
        }
        return quantidadeEstoque;
    }

    public void setQuantidadeEstoque(BigDecimal quantidadeEstoque) {
        this.quantidadeEstoque = quantidadeEstoque;
    }

    public List<ConsultaLocalEstoqueLoteMaterial> getLotes() {
        return lotes;
    }

    public void setLotes(List<ConsultaLocalEstoqueLoteMaterial> lotes) {
        this.lotes = lotes;
    }

    public BigDecimal getQuantidadeDisponivel() {
        return quantidadeEstoque.subtract(getQuantidadeReservada());
    }

    public BigDecimal getQuantidadeTotalLotes() {
        BigDecimal valor = BigDecimal.ZERO;
        for (ConsultaLocalEstoqueLoteMaterial lmp : this.lotes) {
            valor = valor.add(lmp.getQuantidade());
        }
        return valor;
    }

    public BigDecimal getQuantidadeReservada() {
        BigDecimal valor = BigDecimal.ZERO;
        for (ReservaEstoque res : this.reservasEstoque) {
            valor = valor.add(res.getQuantidadeReservada());
        }
        return valor;
    }


    public BigDecimal calcularValorUnitario() {
        if (valorEstoque.compareTo(BigDecimal.ZERO) > 0 && quantidadeEstoque.compareTo(BigDecimal.ZERO) > 0) {
            return valorEstoque.divide(quantidadeEstoque, 8, RoundingMode.HALF_EVEN).setScale(4, RoundingMode.HALF_EVEN);
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(BigDecimal valorUnitario) {
        this.valorUnitario = valorUnitario;
    }

    public Long getIdMaterial() {
        return idMaterial;
    }

    public void setIdMaterial(Long idMaterial) {
        this.idMaterial = idMaterial;
    }

    public String getUnidadeMedida() {
        return unidadeMedida;
    }

    public void setUnidadeMedida(String unidadeMedida) {
        this.unidadeMedida = unidadeMedida;
    }

    public String getGrupoMaterial() {
        return grupoMaterial;
    }

    public void setGrupoMaterial(String grupoMaterial) {
        this.grupoMaterial = grupoMaterial;
    }

    public Boolean getMedicoHospitalar() {
        return medicoHospitalar;
    }

    public void setMedicoHospitalar(Boolean medicoHospitalar) {
        this.medicoHospitalar = medicoHospitalar;
    }

    public Boolean getControleLote() {
        return controleLote;
    }

    public void setControleLote(Boolean controleLote) {
        this.controleLote = controleLote;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String toString() {
        return codigo + " - " + descricao;
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

    public List<ReservaEstoque> getReservasEstoque() {
        return reservasEstoque;
    }

    public void setReservasEstoque(List<ReservaEstoque> reservasEstoque) {
        this.reservasEstoque = reservasEstoque;
    }

    public MovimentacaoGrupoMaterial getMovimentacaoGrupoMaterial() {
        return movimentacaoGrupoMaterial;
    }

    public void setMovimentacaoGrupoMaterial(MovimentacaoGrupoMaterial movimentacaoGrupoMaterial) {
        this.movimentacaoGrupoMaterial = movimentacaoGrupoMaterial;
    }

    @Override
    public int compareTo(ConsultaLocalEstoqueMaterial o) {
        return this.getCodigo().compareTo(o.getCodigo());
    }

    public String getQuantidadeEstoqueFormatada() {
        return Util.formataQuandoDecimal(getQuantidadeEstoque(), mascaraQuantidade);
    }

    public String getQuantidadeReservadaFormatada() {
        return Util.formataQuandoDecimal(getQuantidadeReservada(), mascaraQuantidade);
    }

    public String getQuantidadeDisponivelFormatada() {
        return Util.formataQuandoDecimal(getQuantidadeDisponivel(), mascaraQuantidade);
    }

    public String getValorUnitarioFormatado() {
        return Util.formataQuandoDecimal(getValorUnitario(),mascaraValorUnitario);
    }

    public String getCodigoDescricaoMaterial() {
        return codigo + " - " + descricao;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConsultaLocalEstoqueMaterial that = (ConsultaLocalEstoqueMaterial) o;
        return Objects.equals(idMaterial, that.idMaterial);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idMaterial);
    }
}
