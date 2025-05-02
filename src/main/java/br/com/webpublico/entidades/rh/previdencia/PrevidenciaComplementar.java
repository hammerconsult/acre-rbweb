package br.com.webpublico.entidades.rh.previdencia;

import br.com.webpublico.entidades.*;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@GrupoDiagrama(nome = "RecursosHumanos")
@Audited
@Entity
public class PrevidenciaComplementar extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Servidor")
    private ContratoFP contratoFP;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "previdenciaComplementar", orphanRemoval = true)
    private List<ItemPrevidenciaComplementar> itens;

    public PrevidenciaComplementar() {
        itens = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ContratoFP getContratoFP() {
        return contratoFP;
    }

    public void setContratoFP(ContratoFP contratoFP) {
        this.contratoFP = contratoFP;
    }

    public List<ItemPrevidenciaComplementar> getItens() {
        return itens;
    }

    public void setItens(List<ItemPrevidenciaComplementar> itens) {
        this.itens = itens;
    }
}
