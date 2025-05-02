package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

@GrupoDiagrama(nome = "Tributario")
@Entity
@Audited
public class ValorDividaIntegracao implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ValorDivida valorDivida;
    @ManyToOne(cascade = CascadeType.ALL)
    private ItemIntegracaoTributarioContabil itemItengracao;
    @Transient
    private Long criadoEm;

    public ValorDividaIntegracao(){
        criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ValorDivida getValorDivida() {
        return valorDivida;
    }

    public void setValorDivida(ValorDivida parcela) {
        this.valorDivida = parcela;
    }

    public ItemIntegracaoTributarioContabil getItemItengracao() {
        return itemItengracao;
    }

    public void setItemItengracao(ItemIntegracaoTributarioContabil itemItengracao) {
        this.itemItengracao = itemItengracao;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    @Override
    public boolean equals(Object o) {
      return IdentidadeDaEntidade.calcularEquals(o, this);
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }
}
