package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@GrupoDiagrama(nome = "Conta Corrente")
@Audited
@Entity
public class CalculoContaCorrente extends Calculo implements Serializable {

    @ManyToOne
    private Pessoa pessoa;
    @ManyToOne(cascade = CascadeType.ALL)
    private ProcessoCalculoContaCorrente processoCalculo;
    @ManyToOne
    private UsuarioSistema usuarioSistema;
    @ManyToOne
    private ContaCorrenteTributaria contaCorrenteTributaria;
    @ManyToOne
    private Compensacao compensacao;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date vencimento;
    private Boolean dividaAtiva;
    private Boolean dividaAtivaAjuizada;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "calculoContaCorrente")
    private List<ItemCalculoContaCorrente> itens;
    @ManyToOne
    private Restituicao restituicao;
    private String sequenciaParcela;
    private Long quantidadeParcela;
    @Transient
    private Long criadoEm;

    public CalculoContaCorrente() {
        this.criadoEm = System.nanoTime();
        super.setSubDivida(1L);
        super.setTipoCalculo(TipoCalculo.CONTA_CORRENTE);
        this.itens = Lists.newArrayList();
    }

    public ProcessoCalculoContaCorrente getProcessoCalculo() {
        return processoCalculo;
    }

    public void setProcessoCalculo(ProcessoCalculoContaCorrente processoCalculo) {
        super.setProcessoCalculo(processoCalculo);
        this.processoCalculo = processoCalculo;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public ContaCorrenteTributaria getContaCorrenteTributaria() {
        return contaCorrenteTributaria;
    }

    public void setContaCorrenteTributaria(ContaCorrenteTributaria contaCorrenteTributaria) {
        this.contaCorrenteTributaria = contaCorrenteTributaria;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public Date getVencimento() {
        return vencimento;
    }

    public void setVencimento(Date vencimento) {
        this.vencimento = vencimento;
    }

    public List<ItemCalculoContaCorrente> getItens() {
        return itens;
    }

    public void setItens(List<ItemCalculoContaCorrente> itens) {
        this.itens = itens;
    }

    public Compensacao getCompensacao() {
        return compensacao;
    }

    public void setCompensacao(Compensacao compensacao) {
        this.compensacao = compensacao;
    }

    public Boolean getDividaAtiva() {
        return dividaAtiva != null ? dividaAtiva : false;
    }

    public void setDividaAtiva(Boolean dividaAtiva) {
        this.dividaAtiva = dividaAtiva;
    }

    public Boolean getDividaAtivaAjuizada() {
        return dividaAtivaAjuizada != null ? dividaAtivaAjuizada : false;
    }

    public void setDividaAtivaAjuizada(Boolean dividaAtivaAjuizada) {
        this.dividaAtivaAjuizada = dividaAtivaAjuizada;
    }

    public Restituicao getRestituicao() {
        return restituicao;
    }

    public void setRestituicao(Restituicao restituicao) {
        this.restituicao = restituicao;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof CalculoContaCorrente)) {
            return false;
        }
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.CalculoContaCorrente[ id=" + super.getId() + " ]";
    }

    public String getSequenciaParcela() {
        return sequenciaParcela;
    }

    public void setSequenciaParcela(String sequenciaParcela) {
        this.sequenciaParcela = sequenciaParcela;
    }

    public Long getQuantidadeParcela() {
        return quantidadeParcela;
    }

    public void setQuantidadeParcela(Long quantidadeParcela) {
        this.quantidadeParcela = quantidadeParcela;
    }
}
