package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * Created by William on 15/02/2017.
 */
@Entity
@Audited
public class AprovacaoAlienacaoAtoLegal extends SuperEntidade {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @Etiqueta("Aprovação de Alienação")
    private AprovacaoAlienacao aprovacaoAlienacao;

    @ManyToOne
    @Obrigatorio
    @Etiqueta("Ato Legal")
    private AtoLegal atoLegal;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AprovacaoAlienacao getAprovacaoAlienacao() {
        return aprovacaoAlienacao;
    }

    public void setAprovacaoAlienacao(AprovacaoAlienacao aprovacaoAlienacao) {
        this.aprovacaoAlienacao = aprovacaoAlienacao;
    }

    public AtoLegal getAtoLegal() {
        return atoLegal;
    }

    public void setAtoLegal(AtoLegal atoLegal) {
        this.atoLegal = atoLegal;
    }
}
