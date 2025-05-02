package br.com.webpublico.entidades.administrativo.frotas;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.entidades.TaxaVeiculo;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * Created by William on 15/06/2018.
 */
@Entity
@Audited
public class ParametroFrotasTaxa extends SuperEntidade {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ParametroFrotasHierarquia parametroFrotasHierarquia;
    @ManyToOne
    private TaxaVeiculo taxaVeiculo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ParametroFrotasHierarquia getParametroFrotasHierarquia() {
        return parametroFrotasHierarquia;
    }

    public void setParametroFrotasHierarquia(ParametroFrotasHierarquia parametroFrotasHierarquia) {
        this.parametroFrotasHierarquia = parametroFrotasHierarquia;
    }

    public TaxaVeiculo getTaxaVeiculo() {
        return taxaVeiculo;
    }

    public void setTaxaVeiculo(TaxaVeiculo taxaVeiculo) {
        this.taxaVeiculo = taxaVeiculo;
    }
}
