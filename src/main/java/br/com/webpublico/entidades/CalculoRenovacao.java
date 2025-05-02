package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * Created by AndreGustavo on 08/09/2014.
 */
@Entity
@Audited
@Etiqueta("Cálculo Renovação")
public class CalculoRenovacao extends SuperEntidade {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToOne
    CalculoRBTrans calculoRBTrans;
    @ManyToOne
    private RenovacaoPermissao renovacaoPermissao;

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

    public RenovacaoPermissao getRenovacaoPermissao() {
        return renovacaoPermissao;
    }

    public void setRenovacaoPermissao(RenovacaoPermissao renovacaoPermissao) {
        this.renovacaoPermissao = renovacaoPermissao;
    }
}
