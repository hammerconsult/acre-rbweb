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
public class CalculoCompensacao extends Calculo implements Serializable {

    @ManyToOne
    private Pessoa pessoa;
    @ManyToOne(cascade = CascadeType.ALL)
    private ProcessoCalculoCompensacao processoCalculo;
    @ManyToOne
    private UsuarioSistema usuarioSistema;
    @ManyToOne
    private Compensacao compensacao;
    @Temporal(TemporalType.DATE)
    private Date vencimento;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "calculoCompensacao")
    private List<ItemCalculoCompensacao> itens;
    @Transient
    private Long criadoEm;

    public CalculoCompensacao() {
        this.criadoEm = System.nanoTime();
        super.setSubDivida(1L);
        super.setTipoCalculo(TipoCalculo.COMPENSACAO);
        this.itens = Lists.newArrayList();
    }

    @Override
    public ProcessoCalculoCompensacao getProcessoCalculo() {
        return processoCalculo;
    }

    public void setProcessoCalculo(ProcessoCalculoCompensacao processoCalculo) {
        super.setProcessoCalculo(processoCalculo);
        this.processoCalculo = processoCalculo;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public Compensacao getCompensacao() {
        return compensacao;
    }

    public void setCompensacao(Compensacao compensacao) {
        this.compensacao = compensacao;
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

    public List<ItemCalculoCompensacao> getItens() {
        return itens;
    }

    public void setItens(List<ItemCalculoCompensacao> itens) {
        this.itens = itens;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof CalculoCompensacao)) {
            return false;
        }
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.CalculoCompensacao[ id=" + super.getId() + " ]";
    }
}
