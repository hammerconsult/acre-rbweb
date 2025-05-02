package br.com.webpublico.entidades.rh.creditodesalario;

import br.com.webpublico.entidades.FichaFinanceiraFP;
import br.com.webpublico.entidades.PessoaFisica;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.entidades.VinculoFP;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Audited
public class VinculoFPCreditoSalario extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private VinculoFP vinculoFP;
    @ManyToOne
    private ItemCreditoSalario itemCreditoSalario;
    @ManyToOne
    private PessoaFisica pessoaFisica;
    @ManyToOne
    private FichaFinanceiraFP fichaFinanceiraFP;
    private BigDecimal valorLiquido;
    private String identificador;


    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public VinculoFP getVinculoFP() {
        return vinculoFP;
    }

    public void setVinculoFP(VinculoFP vinculoFP) {
        this.vinculoFP = vinculoFP;
    }

    public ItemCreditoSalario getItemCreditoSalario() {
        return itemCreditoSalario;
    }

    public void setItemCreditoSalario(ItemCreditoSalario itemCreditoSalario) {
        this.itemCreditoSalario = itemCreditoSalario;
    }

    public PessoaFisica getPessoaFisica() {
        return pessoaFisica;
    }

    public void setPessoaFisica(PessoaFisica pessoaFisica) {
        this.pessoaFisica = pessoaFisica;
    }

    public FichaFinanceiraFP getFichaFinanceiraFP() {
        return fichaFinanceiraFP;
    }

    public void setFichaFinanceiraFP(FichaFinanceiraFP fichaFinanceiraFP) {
        this.fichaFinanceiraFP = fichaFinanceiraFP;
    }

    public BigDecimal getValorLiquido() {
        return valorLiquido;
    }

    public void setValorLiquido(BigDecimal valorLiquido) {
        this.valorLiquido = valorLiquido;
    }

    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }

    @Override
    public String toString() {
        return vinculoFP + (pessoaFisica != null ? " Beneficiário Pensão Alimentícia: " + pessoaFisica : "");
    }
}
