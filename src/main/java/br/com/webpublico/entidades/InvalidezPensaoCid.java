package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * Created by mateus on 19/09/17.
 */
@Audited
@Entity

@Etiqueta("Invalidez Pensão - CID")
@GrupoDiagrama(nome = "RecursosHumanos")
public class InvalidezPensaoCid extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Etiqueta("CID")
    @Obrigatorio
    private CID cid;
    @ManyToOne
    @Etiqueta("Invalidez Pensão")
    @Obrigatorio
    private InvalidezPensao invalidezPensao;

    public InvalidezPensaoCid() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CID getCid() {
        return cid;
    }

    public void setCid(CID CID) {
        this.cid = CID;
    }

    public InvalidezPensao getInvalidezPensao() {
        return invalidezPensao;
    }

    public void setInvalidezPensao(InvalidezPensao invalidezPensao) {
        this.invalidezPensao = invalidezPensao;
    }

    @Override
    public String toString() {
        return cid.toString();
    }
}
