package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * Created by AndreGustavo on 09/09/2014.
 */
@Entity
@Audited
@Etiqueta("Cálculo de Credencial RBTrans")
public class CalculoCredencialRBTrans extends SuperEntidade {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToOne
    private CalculoRBTrans calculoRBTrans;
    @ManyToOne
    private CredencialRBTrans credencialRBTrans;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CalculoRBTrans getCalculoRBTrans() {
        return calculoRBTrans;
    }

    public void setCalculoRBTrans(CalculoRBTrans calculoRBTrans) {
        this.calculoRBTrans = calculoRBTrans;
    }

    public CredencialRBTrans getCredencialRBTrans() {
        return credencialRBTrans;
    }

    public void setCredencialRBTrans(CredencialRBTrans credencialRBTrans) {
        this.credencialRBTrans = credencialRBTrans;
    }
}
