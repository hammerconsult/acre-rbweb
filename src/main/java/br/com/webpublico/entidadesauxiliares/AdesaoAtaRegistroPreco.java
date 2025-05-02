package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.UnidadeMedida;
import br.com.webpublico.webreportdto.dto.administrativo.AdesaoAtaRegistroPrecoDTO;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class AdesaoAtaRegistroPreco {

    private Long numeroSolicitacao;
    private Long numeroAta;
    private Integer exercicioSolicitacao;
    private Date dataSolicitacao;
    private String unidadeSolicitante;
    private String ataRegistroPreco;
    private String licitacao;
    private String objetoCompra;
    private String descricaoComplementar;
    private BigDecimal quantidade;
    private BigDecimal valorUnitario;
    private BigDecimal valorTotal;
    private BigDecimal quantidadeDisponivel;
    private BigDecimal quantidadeContratada;
    private String mascaraQuantidade;
    private String mascaraValorUnitario;
    private UnidadeMedida unidadeMedida;
    private List<AtaRegistroPrecoContrato> contratos;

    public AdesaoAtaRegistroPreco(){
        contratos = Lists.newArrayList();
    }

    public static List<AdesaoAtaRegistroPrecoDTO> adesoesAtasToDto(List<AdesaoAtaRegistroPreco> adesoes) {
        List<AdesaoAtaRegistroPrecoDTO> retorno = Lists.newArrayList();
        if (adesoes != null && !adesoes.isEmpty()) {
            for (AdesaoAtaRegistroPreco adesao : adesoes) {
                retorno.add(adesaoAtaToDto(adesao));
            }
        }
        return retorno;
    }

    public static AdesaoAtaRegistroPrecoDTO adesaoAtaToDto(AdesaoAtaRegistroPreco adesao) {
        AdesaoAtaRegistroPrecoDTO retorno = new AdesaoAtaRegistroPrecoDTO();
        retorno.setNumeroSolicitacao(adesao.getNumeroSolicitacao());
        retorno.setNumeroAta(adesao.getNumeroAta());
        retorno.setExercicioSolicitacao(adesao.getExercicioSolicitacao());
        retorno.setDataSolicitacao(adesao.getDataSolicitacao());
        retorno.setUnidadeSolicitante(adesao.getUnidadeSolicitante());
        retorno.setAtaRegistroPreco(adesao.getAtaRegistroPreco());
        retorno.setLicitacao(adesao.getLicitacao());
        retorno.setObjetoCompra(adesao.getObjetoCompra());
        retorno.setDescricaoComplementar(adesao.getDescricaoComplementar());
        retorno.setQuantidade(adesao.getQuantidade());
        retorno.setValorUnitario(adesao.getValorUnitario());
        retorno.setValorTotal(adesao.getValorTotal());
        retorno.setQuantidadeDisponivel(adesao.getQuantidadeDisponivel());
        retorno.setQuantidadeContratada(adesao.getQuantidadeContratada());
        retorno.setMascaraQuantidade(adesao.getMascaraQuantidade());
        retorno.setMascaraValorUnitario(adesao.getMascaraValorUnitario());
        return retorno;
    }

    public Long getNumeroSolicitacao() {
        return numeroSolicitacao;
    }

    public void setNumeroSolicitacao(Long numeroSolicitacao) {
        this.numeroSolicitacao = numeroSolicitacao;
    }

    public Long getNumeroAta() {
        return numeroAta;
    }

    public void setNumeroAta(Long numeroAta) {
        this.numeroAta = numeroAta;
    }

    public Integer getExercicioSolicitacao() {
        return exercicioSolicitacao;
    }

    public void setExercicioSolicitacao(Integer exercicioSolicitacao) {
        this.exercicioSolicitacao = exercicioSolicitacao;
    }

    public Date getDataSolicitacao() {
        return dataSolicitacao;
    }

    public void setDataSolicitacao(Date dataSolicitacao) {
        this.dataSolicitacao = dataSolicitacao;
    }

    public String getUnidadeSolicitante() {
        return unidadeSolicitante;
    }

    public void setUnidadeSolicitante(String unidadeSolicitante) {
        this.unidadeSolicitante = unidadeSolicitante;
    }

    public String getAtaRegistroPreco() {
        return ataRegistroPreco;
    }

    public void setAtaRegistroPreco(String ataRegistroPreco) {
        this.ataRegistroPreco = ataRegistroPreco;
    }

    public String getLicitacao() {
        return licitacao;
    }

    public void setLicitacao(String licitacao) {
        this.licitacao = licitacao;
    }

    public String getObjetoCompra() {
        return objetoCompra;
    }

    public void setObjetoCompra(String objetoCompra) {
        this.objetoCompra = objetoCompra;
    }

    public String getDescricaoComplementar() {
        return descricaoComplementar;
    }

    public void setDescricaoComplementar(String descricaoComplementar) {
        this.descricaoComplementar = descricaoComplementar;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(BigDecimal valorUnitario) {
        this.valorUnitario = valorUnitario;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public BigDecimal getQuantidadeDisponivel() {
        return quantidadeDisponivel;
    }

    public void setQuantidadeDisponivel(BigDecimal quantidadeDisponivel) {
        this.quantidadeDisponivel = quantidadeDisponivel;
    }

    public BigDecimal getQuantidadeContratada() {
        return quantidadeContratada;
    }

    public void setQuantidadeContratada(BigDecimal quantidadeContratada) {
        this.quantidadeContratada = quantidadeContratada;
    }

    public String getMascaraQuantidade() {
        return mascaraQuantidade;
    }

    public void setMascaraQuantidade(String mascaraQuantidade) {
        this.mascaraQuantidade = mascaraQuantidade;
    }

    public String getMascaraValorUnitario() {
        return mascaraValorUnitario;
    }

    public void setMascaraValorUnitario(String mascaraValorUnitario) {
        this.mascaraValorUnitario = mascaraValorUnitario;
    }

    public UnidadeMedida getUnidadeMedida() {
        return unidadeMedida;
    }

    public void setUnidadeMedida(UnidadeMedida unidadeMedida) {
        this.unidadeMedida = unidadeMedida;
    }

    public List<AtaRegistroPrecoContrato> getContratos() {
        return contratos;
    }

    public void setContratos(List<AtaRegistroPrecoContrato> contratos) {
        this.contratos = contratos;
    }
}
