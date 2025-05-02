/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.CorEntidade;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@GrupoDiagrama(nome = "RecursosHumanos")
@Entity

@Audited
@CorEntidade(value = "#00FF00")
@Etiqueta("Benefício Folha de Pagamento")
@Deprecated
public class BeneficioFP extends VinculoFP implements Serializable {

    @Tabelavel
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Modalidade do Benefício")
    private ModalidadeBeneficioFP modalidadeBeneficioFP;
    @Tabelavel
    @OneToOne
    @Etiqueta("Contrato Folha de Pagamento")
    private ContratoFP contratoFP;
    @OneToMany(mappedBy = "beneficioFP", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ValorBeneficioFP> valoresBeneficiosFPs;

    public ContratoFP getContratoFP() {
        return contratoFP;
    }

    public void setContratoFP(ContratoFP contratoFP) {
        this.contratoFP = contratoFP;
    }

    public ModalidadeBeneficioFP getModalidadeBeneficioFP() {
        return modalidadeBeneficioFP;
    }

    public void setModalidadeBeneficioFP(ModalidadeBeneficioFP modalidadeBeneficioFP) {
        this.modalidadeBeneficioFP = modalidadeBeneficioFP;
    }

    public List<ValorBeneficioFP> getValoresBeneficiosFPs() {
        return valoresBeneficiosFPs;
    }

    public void setValoresBeneficiosFPs(List<ValorBeneficioFP> valoresBeneficiosFPs) {
        this.valoresBeneficiosFPs = valoresBeneficiosFPs;
    }

}
