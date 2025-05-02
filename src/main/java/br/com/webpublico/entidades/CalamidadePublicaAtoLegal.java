package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Audited
@Etiqueta("Calamidade Pública/Ato Legal")
public class CalamidadePublicaAtoLegal extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private CalamidadePublica calamidadePublica;
    @ManyToOne
    @Etiqueta("Ato Legal")
    @Obrigatorio
    private AtoLegal atoLegal;

    public CalamidadePublicaAtoLegal() {
        super();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CalamidadePublica getCalamidadePublica() {
        return calamidadePublica;
    }

    public void setCalamidadePublica(CalamidadePublica calamidadePublica) {
        this.calamidadePublica = calamidadePublica;
    }

    public AtoLegal getAtoLegal() {
        return atoLegal;
    }

    public void setAtoLegal(AtoLegal atoLegal) {
        this.atoLegal = atoLegal;
    }
}
