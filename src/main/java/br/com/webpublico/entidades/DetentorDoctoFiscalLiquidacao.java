package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Desenvolvimento
 * Date: 16/10/14
 * Time: 15:54
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
@Etiqueta("Detentor Documento Fiscal Liquidação")
@Table(name = "DENTDOCFISCLIQUIDACAO")
public class DetentorDoctoFiscalLiquidacao extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToMany(mappedBy = "detDoctoFiscalLiquidacao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetentorDocto> doctoLista;

    public DetentorDoctoFiscalLiquidacao() {
        super();
        this.doctoLista = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<DetentorDocto> getDoctoLista() {
        for (DetentorDocto d : doctoLista) {
            d.setDetDoctoFiscalLiquidacao(this);
        }
        return doctoLista;
    }

    public void setDoctoLista(List<DetentorDocto> doctoLista) {
        this.doctoLista = doctoLista;
    }
}
