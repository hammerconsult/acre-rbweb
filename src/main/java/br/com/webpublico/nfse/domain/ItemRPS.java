package br.com.webpublico.nfse.domain;

import br.com.webpublico.entidades.*;
import br.com.webpublico.nfse.domain.dtos.ItemDeclaracaoServicoNfseDTO;
import br.com.webpublico.nfse.domain.dtos.NfseEntity;
import com.google.common.collect.Lists;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;


/**
 * A ItemDeclaracaoServico.
 */
@Entity
public class ItemRPS extends SuperEntidade implements NfseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private BigDecimal iss;
    private BigDecimal baseCalculo;
    private BigDecimal deducoes;
    private BigDecimal descontosIncondicionados;
    private BigDecimal descontosCondicionados;
    private BigDecimal quantidade;
    private BigDecimal valorServico;
    private String descricao;
    private String nomeServico;
    private BigDecimal aliquotaServico;
    private Boolean prestadoNoPais;
    private String observacoes;
    @ManyToOne
    private Servico servico;
    @ManyToOne
    private Cidade municipio;
    @ManyToOne
    private Rps rps;
    @ManyToOne
    private Pais pais;
    @ManyToOne
    private CNAE cnae;
    @OneToMany(mappedBy = "itemRPS")
    private List<DetalheItemRPS> detalhes;

    public ItemRPS() {
        detalhes = Lists.newArrayList();
    }

    public ItemRPS(Rps rps, ItemDeclaracaoServico itemDeclaracaoServico) {
        this.iss = itemDeclaracaoServico.getIss();
        this.baseCalculo = itemDeclaracaoServico.getBaseCalculo();
        this.deducoes = itemDeclaracaoServico.getDeducoes();
        this.descontosIncondicionados = itemDeclaracaoServico.getDescontosIncondicionados();
        this.descontosCondicionados = itemDeclaracaoServico.getDescontosCondicionados();
        this.quantidade = itemDeclaracaoServico.getQuantidade();
        this.valorServico = itemDeclaracaoServico.getValorServico();
        this.descricao = itemDeclaracaoServico.getDescricao();
        this.nomeServico = itemDeclaracaoServico.getNomeServico();
        this.aliquotaServico = itemDeclaracaoServico.getAliquotaServico();
        this.prestadoNoPais = itemDeclaracaoServico.getPrestadoNoPais();
        this.observacoes = itemDeclaracaoServico.getObservacoes();
        this.servico = itemDeclaracaoServico.getServico();
        this.municipio = itemDeclaracaoServico.getMunicipio();
        this.pais = itemDeclaracaoServico.getPais();
        this.rps = rps;
        this.cnae = itemDeclaracaoServico.getCnae();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getIss() {
        return iss != null ? iss : BigDecimal.ZERO;
    }

    public void setIss(BigDecimal iss) {
        this.iss = iss;
    }

    public BigDecimal getBaseCalculo() {
        return baseCalculo != null ? baseCalculo : BigDecimal.ZERO;
    }

    public void setBaseCalculo(BigDecimal baseCalculo) {
        this.baseCalculo = baseCalculo;
    }

    public BigDecimal getDeducoes() {
        return deducoes != null ? deducoes : BigDecimal.ZERO;
    }

    public void setDeducoes(BigDecimal deducoes) {
        this.deducoes = deducoes;
    }

    public BigDecimal getDescontosIncondicionados() {
        return descontosIncondicionados != null ? descontosIncondicionados : BigDecimal.ZERO;
    }

    public void setDescontosIncondicionados(BigDecimal descontosIncondicionados) {
        this.descontosIncondicionados = descontosIncondicionados;
    }

    public Pais getPais() {
        return pais;
    }

    public void setPais(Pais pais) {
        this.pais = pais;
    }

    public BigDecimal getDescontosCondicionados() {
        return descontosCondicionados != null ? descontosCondicionados : BigDecimal.ZERO;
    }

    public void setDescontosCondicionados(BigDecimal descontosCondicionados) {
        this.descontosCondicionados = descontosCondicionados;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getValorServico() {
        return valorServico != null ? valorServico : BigDecimal.ZERO;
    }

    public void setValorServico(BigDecimal valorServico) {
        this.valorServico = valorServico;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Boolean getPrestadoNoPais() {
        return prestadoNoPais;
    }

    public void setPrestadoNoPais(Boolean prestadoNoPais) {
        this.prestadoNoPais = prestadoNoPais;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public Servico getServico() {
        return servico;
    }

    public void setServico(Servico servicoPrestador) {
        this.servico = servicoPrestador;
    }

    public Cidade getMunicipio() {
        return municipio;
    }

    public void setMunicipio(Cidade municipio) {
        this.municipio = municipio;
    }

    public Rps getRps() {
        return rps;
    }

    public void setRps(Rps rps) {
        this.rps = rps;
    }

    public String getNomeServico() {
        return nomeServico;
    }

    public void setNomeServico(String nomeServico) {
        this.nomeServico = nomeServico;
    }

    public BigDecimal getAliquotaServico() {
        return aliquotaServico != null ? aliquotaServico : BigDecimal.ZERO;
    }

    public void setAliquotaServico(BigDecimal aliquotaServico) {
        this.aliquotaServico = aliquotaServico;
    }

    public CNAE getCnae() {
        return cnae;
    }

    public void setCnae(CNAE cnae) {
        this.cnae = cnae;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ItemRPS itemDeclaracaoServico = (ItemRPS) o;

        if (!Objects.equals(id, itemDeclaracaoServico.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "ItemDeclaracaoServico{" +
            "id=" + id +
            ", iss='" + iss + "'" +
            ", baseCalculo='" + baseCalculo + "'" +
            ", deducoes='" + deducoes + "'" +
            ", descontosIncondicionados='" + descontosIncondicionados + "'" +
            ", descontosCondicionados='" + descontosCondicionados + "'" +
            ", quantidade='" + quantidade + "'" +
            ", valorServico='" + valorServico + "'" +
            ", descricao='" + descricao + "'" +
            ", prestadoNoPais='" + prestadoNoPais + "'" +
            ", observacoes='" + observacoes + "'" +
            '}';
    }

    public BigDecimal getValorTotal() {
        if (quantidade != null) {
            valorServico.multiply(quantidade);
        }
        return valorServico;
    }

    @Override
    public ItemDeclaracaoServicoNfseDTO toNfseDto() {
        ItemDeclaracaoServicoNfseDTO dto = new ItemDeclaracaoServicoNfseDTO();
        dto.setAliquotaServico(this.getAliquotaServico());
        dto.setBaseCalculo(this.getBaseCalculo());
        dto.setDescricao(this.getDescricao());
        dto.setDeducoes(this.getDeducoes());
        dto.setDescontosCondicionados(this.getDescontosCondicionados());
        dto.setDescontosIncondicionados(this.getDescontosIncondicionados());
        dto.setIss(this.getIss());
        dto.setNomeServico(this.getNomeServico());
        dto.setObservacoes(this.getObservacoes());
        dto.setQuantidade(this.getQuantidade());
        if (this.getPais() != null) {
            dto.setPais(this.getPais().toNfseDto());
        }
        if (this.getMunicipio() != null) {
            dto.setMunicipio(this.getMunicipio().toNfseDto());
        }
        if (this.getCnae() != null) {
            dto.setCnae(this.getCnae().toNfseDto());
        }
        dto.setPrestadoNoPais(this.getPrestadoNoPais());
        if (this.getServico() != null)
            dto.setServico(this.getServico().toNfseDto());
        dto.setValorServico(this.getValorServico());
        return dto;
    }
}
