package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
@Etiqueta("Item Verificação")
public class ItemVerificacaoBemMovel extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @Etiqueta("Verificação de Bem Móvel")
    private VerificacaoBemMovel verificacaoBemMovel;

    @ManyToOne
    @Etiqueta("Bem")
    private Bem bem;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public VerificacaoBemMovel getVerificacaoBemMovel() {
        return verificacaoBemMovel;
    }

    public void setVerificacaoBemMovel(VerificacaoBemMovel verificacaoBemMovel) {
        this.verificacaoBemMovel = verificacaoBemMovel;
    }

    public Bem getBem() {
        return bem;
    }

    public void setBem(Bem bem) {
        this.bem = bem;
    }

    @Override
    public String toString() {
        try {
            return bem.toString();
        } catch (NullPointerException e) {
            return "";
        }
    }
}
