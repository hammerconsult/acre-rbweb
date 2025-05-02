package br.com.webpublico.entidades;


import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import java.io.Serializable;
import javax.persistence.*;

import org.hibernate.envers.Audited;

@GrupoDiagrama(nome = "Arrecadação")
@Entity

@Audited
@Etiqueta("Itens do Lançamento de Desconto")
public class ItemLancamentoDesconto implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne(cascade = CascadeType.ALL)
    private DescontoItemParcela descontoItemParcela;
    @ManyToOne
    private TributoDesconto tributoDesconto;
    @Transient
    private Long criadoEm;

    public ItemLancamentoDesconto() {
        criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DescontoItemParcela getDescontoItemParcela() {
        return descontoItemParcela;
    }

    public void setDescontoItemParcela(DescontoItemParcela descontoItemParcela) {
        this.descontoItemParcela = descontoItemParcela;
    }

    public TributoDesconto getTributoDesconto() {
        return tributoDesconto;
    }

    public void setTributoDesconto(TributoDesconto tributoDesconto) {
        this.tributoDesconto = tributoDesconto;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
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
        return "br.com.webpublico.entidades.ItemLancamentoDesconto[ id=" + id + " ]";
    }

}
