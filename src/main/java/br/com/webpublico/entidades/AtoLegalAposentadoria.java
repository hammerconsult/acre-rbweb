package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * @author octavio
 */
@Entity
@Audited
@GrupoDiagrama(nome = "Recursos Humanos")
public class AtoLegalAposentadoria extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Etiqueta("Ato Legal")
    private AtoLegal atoLegal;
    @ManyToOne
    @Etiqueta("Aposentadoria")
    private Aposentadoria aposentadoria;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AtoLegal getAtoLegal() {
        return atoLegal;
    }

    public void setAtoLegal(AtoLegal atoLegal) {
        this.atoLegal = atoLegal;
    }

    public Aposentadoria getAposentadoria() {
        return aposentadoria;
    }

    public void setAposentadoria(Aposentadoria aposentadoria) {
        this.aposentadoria = aposentadoria;
    }
}
