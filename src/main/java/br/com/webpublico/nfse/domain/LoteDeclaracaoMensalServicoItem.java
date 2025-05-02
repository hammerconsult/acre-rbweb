package br.com.webpublico.nfse.domain;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;


/**
 * A DeclaracaoMensalServico.
 */
@Entity
@Table(name = "LOTEDECMENSALSERVICOITEM")
@Audited
@Etiqueta(value = "Item do Lote de Declaração Mensal de Serviço")
public class LoteDeclaracaoMensalServicoItem extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private LoteDeclaracaoMensalServico lote;
    @ManyToOne
    private DeclaracaoMensalServico declaracaoMensalServico;


    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LoteDeclaracaoMensalServico getLote() {
        return lote;
    }

    public void setLote(LoteDeclaracaoMensalServico lote) {
        this.lote = lote;
    }

    public DeclaracaoMensalServico getDeclaracaoMensalServico() {
        return declaracaoMensalServico;
    }

    public void setDeclaracaoMensalServico(DeclaracaoMensalServico declaracaoMensalServico) {
        this.declaracaoMensalServico = declaracaoMensalServico;
    }
}
