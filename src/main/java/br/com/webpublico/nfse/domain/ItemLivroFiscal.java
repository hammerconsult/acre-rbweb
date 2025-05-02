package br.com.webpublico.nfse.domain;

import br.com.webpublico.entidades.SuperEntidade;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Audited
public class ItemLivroFiscal extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JsonIgnore
    private LivroFiscal livroFiscal;

    @ManyToOne
    private DeclaracaoMensalServico declaracaoMensalServico;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LivroFiscal getLivroFiscal() {
        return livroFiscal;
    }

    public void setLivroFiscal(LivroFiscal livroFiscal) {
        this.livroFiscal = livroFiscal;
    }

    public DeclaracaoMensalServico getDeclaracaoMensalServico() {
        return declaracaoMensalServico;
    }

    public void setDeclaracaoMensalServico(DeclaracaoMensalServico declaracaoMensalServico) {
        this.declaracaoMensalServico = declaracaoMensalServico;
    }
}
