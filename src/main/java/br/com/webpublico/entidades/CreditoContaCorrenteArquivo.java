package br.com.webpublico.entidades;

import br.com.webpublico.util.IdentidadeDaEntidade;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author Fabio
 */
@Entity
@Audited
@Table(name = "CREDITOCONTACORRENTEARQ")
public class CreditoContaCorrenteArquivo extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private CreditoContaCorrente creditoContaCorrente;
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private Arquivo arquivo;

    public CreditoContaCorrenteArquivo() {
        criadoEm = System.nanoTime();
    }

    public CreditoContaCorrente getCreditoContaCorrente() {
        return creditoContaCorrente;
    }

    public void setCreditoContaCorrente(CreditoContaCorrente creditoContaCorrente) {
        this.creditoContaCorrente = creditoContaCorrente;
    }

    public Arquivo getArquivo() {
        return arquivo;
    }

    public void setArquivo(Arquivo arquivo) {
        this.arquivo = arquivo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
