/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.List;

/**
 * @author andre
 */
@Entity

@Audited
@GrupoDiagrama(nome = "RecursosHumanos")
@Etiqueta("Pensão Alimentícia")
public class PensaoAlimenticia extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Etiqueta("Instituidor")
    @ManyToOne
    @Obrigatorio
    private VinculoFP vinculoFP;
    @Etiqueta("Beneficiários de Pensão Alimentícia")
    @Tabelavel
    @OneToMany(mappedBy = "pensaoAlimenticia", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BeneficioPensaoAlimenticia> beneficiosPensaoAlimenticia;

    public Long getId() {
        return id;
    }

    public List<BeneficioPensaoAlimenticia> getBeneficiosPensaoAlimenticia() {
        return beneficiosPensaoAlimenticia;
    }

    public void setBeneficiosPensaoAlimenticia(List<BeneficioPensaoAlimenticia> beneficiosPensaoAlimenticia) {
        this.beneficiosPensaoAlimenticia = beneficiosPensaoAlimenticia;
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

    @Override
    public String toString() {
        return "Instituidor : " + vinculoFP;
    }
}
