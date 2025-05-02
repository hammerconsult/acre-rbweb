package br.com.webpublico.nfse.domain;

import br.com.webpublico.entidades.SuperEntidade;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.envers.Audited;

import javax.persistence.*;


/**
 * A NotaDeclarada.
 */
@Entity
@Table(name = "NOTADECLARADA")
@Audited
public class NotaDeclarada extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private DeclaracaoPrestacaoServico declaracaoPrestacaoServico;

    @JsonIgnore
    @ManyToOne
    private DeclaracaoMensalServico declaracaoMensalServico;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DeclaracaoPrestacaoServico getDeclaracaoPrestacaoServico() {
        return declaracaoPrestacaoServico;
    }

    public void setDeclaracaoPrestacaoServico(DeclaracaoPrestacaoServico declaracaoPrestacaoServico) {
        this.declaracaoPrestacaoServico = declaracaoPrestacaoServico;
    }

    public DeclaracaoMensalServico getDeclaracaoMensalServico() {
        return declaracaoMensalServico;
    }

    public void setDeclaracaoMensalServico(DeclaracaoMensalServico declaracaoMensalServico) {
        this.declaracaoMensalServico = declaracaoMensalServico;
    }
}
