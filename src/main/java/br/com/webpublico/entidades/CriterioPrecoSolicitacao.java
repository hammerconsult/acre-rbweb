package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: JoãoPaulo
 * Date: 22/08/14
 * Time: 17:33
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited

@Etiqueta("Critério Preço Solicitação")
public class CriterioPrecoSolicitacao extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Etiqueta("Solicitação Material")
    @OneToOne
    private SolicitacaoMaterial solicitacaoMaterial;

    @Etiqueta("Nota da Menor Proposta")
    @Pesquisavel
    @Tabelavel
    @Positivo(permiteZero = false)
    @Obrigatorio
    private BigDecimal nota;

    public CriterioPrecoSolicitacao() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SolicitacaoMaterial getSolicitacaoMaterial() {
        return solicitacaoMaterial;
    }

    public void setSolicitacaoMaterial(SolicitacaoMaterial solicitacaoMaterial) {
        this.solicitacaoMaterial = solicitacaoMaterial;
    }

    public BigDecimal getNota() {
        return nota;
    }

    public void setNota(BigDecimal nota) {
        this.nota = nota;
    }
}
