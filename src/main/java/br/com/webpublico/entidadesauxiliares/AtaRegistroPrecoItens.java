package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.UnidadeMedida;
import br.com.webpublico.webreportdto.dto.administrativo.AtaRegistroPrecoItensDTO;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class AtaRegistroPrecoItens {

    private Long idContrato;
    private Long numeroSolicitacao;
    private Long numeroAta;
    private Integer exercicioSolicitacao;
    private Date dataSolicitacao;
    private String unidadeSolicitante;
    private String ataRegistroPreco;
    private String licitacao;

    private List<AtaRegistroPrecoItens> itensAdesao;
    private String objetoCompra;
    private String descricaoComplementar;
    private BigDecimal quantidade;
    private BigDecimal valorUnitario;
    private BigDecimal valorTotal;
    private BigDecimal quantidadeDisponivel;
    private BigDecimal quantidadeContratada;
    private BigDecimal valorDisponivel;
    private BigDecimal valorContratado;

    private List<AtaRegistroPrecoContrato> contratos;
    private List<AtaRegistroPrecoItensContrato> itensContrato;
    private String contrato;
    private BigDecimal valorTotalContrato;
    private Long numeroItemContrato;
    private String descricaoItemContrato;
    private String mascaraQuantidade;
    private String mascaraValorUnitario;
    private UnidadeMedida unidadeMedida;
    private String fornecedor;

    public AtaRegistroPrecoItens(){
        itensAdesao = Lists.newArrayList();
        contratos = Lists.newArrayList();
        itensContrato = Lists.newArrayList();
    }

    public static List<AtaRegistroPrecoItensDTO> itensToDto(List<AtaRegistroPrecoItens> itens) {
        List<AtaRegistroPrecoItensDTO> retorno = Lists.newArrayList();
        if (itens != null && !itens.isEmpty()) {
            for (AtaRegistroPrecoItens item : itens) {
                retorno.add(itemToDto(item));
            }
        }
        return retorno;
    }

    public static AtaRegistroPrecoItensDTO itemToDto(AtaRegistroPrecoItens item) {
        AtaRegistroPrecoItensDTO retorno = new AtaRegistroPrecoItensDTO();
        retorno.setObjetoCompra(item.getObjetoCompra());
        retorno.setDescricaoComplementar(item.getDescricaoComplementar());
        retorno.setQuantidade(item.getQuantidade());
        retorno.setValorUnitario(item.getValorUnitario());
        retorno.setValorTotal(item.getValorTotal());
        retorno.setQuantidadeDisponivel(item.getQuantidadeDisponivel());
        retorno.setQuantidadeContratada(item.getQuantidadeContratada());
        retorno.setValorDisponivel(item.getValorDisponivel());
        retorno.setValorContratado(item.getValorContratado());
        retorno.setMascaraQuantidade(item.getMascaraQuantidade());
        retorno.setMascaraValorUnitario(item.getMascaraValorUnitario());
        return retorno;
    }

    public String getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(String fornecedor) {
        this.fornecedor = fornecedor;
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

    public Long getIdContrato() {
        return idContrato;
    }

    public void setIdContrato(Long idContrato) {
        this.idContrato = idContrato;
    }

    public BigDecimal getValorTotalContrato() {
        return valorTotalContrato;
    }

    public void setValorTotalContrato(BigDecimal valorTotalContrato) {
        this.valorTotalContrato = valorTotalContrato;
    }

    public Long getNumeroItemContrato() {
        return numeroItemContrato;
    }

    public void setNumeroItemContrato(Long numeroItemContrato) {
        this.numeroItemContrato = numeroItemContrato;
    }

    public String getDescricaoItemContrato() {
        return descricaoItemContrato;
    }

    public void setDescricaoItemContrato(String descricaoItemContrato) {
        this.descricaoItemContrato = descricaoItemContrato;
    }

    public String getLicitacao() {
        return licitacao;
    }

    public void setLicitacao(String licitacao) {
        this.licitacao = licitacao;
    }

    public Long getNumeroAta() {
        return numeroAta;
    }

    public void setNumeroAta(Long numeroAta) {
        this.numeroAta = numeroAta;
    }

    public String getAtaRegistroPreco() {
        return ataRegistroPreco;
    }

    public void setAtaRegistroPreco(String ataRegistroPreco) {
        this.ataRegistroPreco = ataRegistroPreco;
    }

    public Long getNumeroSolicitacao() {
        return numeroSolicitacao;
    }

    public void setNumeroSolicitacao(Long numeroSolicitacao) {
        this.numeroSolicitacao = numeroSolicitacao;
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

    public String getContrato() {
        return contrato;
    }

    public void setContrato(String contrato) {
        this.contrato = contrato;
    }

    public List<AtaRegistroPrecoItens> getItensAdesao() {
        return itensAdesao;
    }

    public void setItensAdesao(List<AtaRegistroPrecoItens> itensAdesao) {
        this.itensAdesao = itensAdesao;
    }

    public List<AtaRegistroPrecoContrato> getContratos() {
        return contratos;
    }

    public void setContratos(List<AtaRegistroPrecoContrato> contratos) {
        this.contratos = contratos;
    }

    public List<AtaRegistroPrecoItensContrato> getItensContrato() {
        return itensContrato;
    }

    public void setItensContrato(List<AtaRegistroPrecoItensContrato> itensContrato) {
        this.itensContrato = itensContrato;
    }

    public BigDecimal getValorDisponivel() {
        return valorDisponivel;
    }

    public void setValorDisponivel(BigDecimal valorDisponivel) {
        this.valorDisponivel = valorDisponivel;
    }

    public BigDecimal getValorContratado() {
        return valorContratado;
    }

    public void setValorContratado(BigDecimal valorContratado) {
        this.valorContratado = valorContratado;
    }
}
