package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.Conta;
import br.com.webpublico.entidades.Empenho;
import br.com.webpublico.entidades.SolicitacaoEmpenho;
import br.com.webpublico.enums.OrigemSubstituicaoObjetoCompra;
import com.google.common.collect.ComparisonChain;

import java.util.Date;
import java.util.Objects;

public class SubstituicaoObjetoCompraMovimento implements Comparable<SubstituicaoObjetoCompraMovimento> {

    private Long id;
    private Date data;
    private Integer ano;
    private String numero;
    private String descricao;
    private OrigemSubstituicaoObjetoCompra tipoMovimento;
    private Long idItem;
    private Conta contaDesdobrada;
    private Empenho empenho;
    private SolicitacaoEmpenho solicitacaoEmpenho;

    public SubstituicaoObjetoCompraMovimento() {
    }

    public Conta getContaDesdobrada() {
        return contaDesdobrada;
    }

    public void setContaDesdobrada(Conta contaDesdobrada) {
        this.contaDesdobrada = contaDesdobrada;
    }

    public Empenho getEmpenho() {
        return empenho;
    }

    public void setEmpenho(Empenho empenho) {
        this.empenho = empenho;
    }

    public SolicitacaoEmpenho getSolicitacaoEmpenho() {
        return solicitacaoEmpenho;
    }

    public void setSolicitacaoEmpenho(SolicitacaoEmpenho solicitacaoEmpenho) {
        this.solicitacaoEmpenho = solicitacaoEmpenho;
    }

    public Long getIdItem() {
        return idItem;
    }

    public void setIdItem(Long idItem) {
        this.idItem = idItem;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OrigemSubstituicaoObjetoCompra getTipoMovimento() {
        return tipoMovimento;
    }

    public void setTipoMovimento(OrigemSubstituicaoObjetoCompra tipoMovimento) {
        this.tipoMovimento = tipoMovimento;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubstituicaoObjetoCompraMovimento that = (SubstituicaoObjetoCompraMovimento) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(SubstituicaoObjetoCompraMovimento o) {
        if (o.getId() !=null && getId() !=null) {
            return ComparisonChain.start().compare(getId(), o.getId()).result();
        }
        return 0;
    }
}
