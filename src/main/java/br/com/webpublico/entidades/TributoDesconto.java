package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import com.google.common.collect.Lists;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import org.hibernate.envers.Audited;

@GrupoDiagrama(nome = "Arrecadação")
@Entity

@Audited
@Etiqueta("Tributos do Lançamento de Desconto")
public class TributoDesconto implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private Tributo tributo;
    private BigDecimal porcentagemDesconto;
    @ManyToOne
    private LancamentoDesconto lancamentoDesconto;
    @OneToMany(mappedBy = "tributoDesconto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemLancamentoDesconto> itens;
    @Transient
    private Long criadoEm;
    @Transient
    private BigDecimal valorOriginal;
    @Transient
    private BigDecimal valorComDesconto;

    public TributoDesconto() {
        criadoEm = System.nanoTime();
        itens = Lists.newLinkedList();
        valorOriginal = new BigDecimal(BigInteger.ZERO);
        valorComDesconto = new BigDecimal(BigInteger.ZERO);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Tributo getTributo() {
        return tributo;
    }

    public void setTributo(Tributo tributo) {
        this.tributo = tributo;
    }

    public BigDecimal getPorcentagemDesconto() {
        return porcentagemDesconto;
    }

    public void setPorcentagemDesconto(BigDecimal porcentagemDesconto) {
        this.porcentagemDesconto = porcentagemDesconto;
    }

    public LancamentoDesconto getLancamentoDesconto() {
        return lancamentoDesconto;
    }

    public void setLancamentoDesconto(LancamentoDesconto lancamentoDesconto) {
        this.lancamentoDesconto = lancamentoDesconto;
    }

    public List<ItemLancamentoDesconto> getItens() {
        return itens;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public BigDecimal getValorOriginal() {
        return valorOriginal;
    }

    public void setValorOriginal(BigDecimal valorOriginal) {
        this.valorOriginal = valorOriginal;
    }

    public BigDecimal getValorComDesconto() {
        return valorComDesconto;
    }

    public void setValorComDesconto(BigDecimal valorComDesconto) {
        this.valorComDesconto = valorComDesconto;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.TributoDesconto[ id=" + id + " ]";
    }
}
