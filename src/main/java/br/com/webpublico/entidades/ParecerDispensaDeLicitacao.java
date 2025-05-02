package br.com.webpublico.entidades;

import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.ValidadorEntidade;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: JoãoPaulo
 * Date: 10/07/14
 * Time: 11:39
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited

@Table(name = "PARECERDISPENSALICI")
@Etiqueta("Parecer Dispensa de Licitação")
public class ParecerDispensaDeLicitacao implements Serializable, ValidadorEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Etiqueta("Dispensa de Licitação")
    @ManyToOne
    private DispensaDeLicitacao dispensaDeLicitacao;

    @Etiqueta("Parecer")
    @ManyToOne(cascade = CascadeType.ALL)
    private Parecer parecer;

    @Invisivel
    @Transient
    private Long criadoEm;

    public ParecerDispensaDeLicitacao() {
        this.criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DispensaDeLicitacao getDispensaDeLicitacao() {
        return dispensaDeLicitacao;
    }

    public void setDispensaDeLicitacao(DispensaDeLicitacao dispensaDeLicitacao) {
        this.dispensaDeLicitacao = dispensaDeLicitacao;
    }

    public Parecer getParecer() {
        return parecer;
    }

    public void setParecer(Parecer parecer) {
        this.parecer = parecer;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    @Override
    public void validarConfirmacao() throws ValidacaoException {
        return;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        return IdentidadeDaEntidade.calcularEquals(this, obj);
    }
}
