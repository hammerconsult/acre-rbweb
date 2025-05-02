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
 * Time: 17:43
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited

@Table(name = "CLASSIFICACAOFINALSOL")
@Etiqueta("Classificação Final Solicitação")
public class ClassificacaoFinalSolicitacao extends SuperEntidade implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Etiqueta("Solicitação Material")
    @OneToOne
    private SolicitacaoMaterial solicitacaoMaterial;

    @Etiqueta("Peso Técnica")
    @Pesquisavel
    @Tabelavel
    @Positivo(permiteZero = false)
    @Obrigatorio
    private BigDecimal pesoTecnica;

    @Etiqueta("Peso Preço")
    @Pesquisavel
    @Tabelavel
    @Positivo(permiteZero = false)
    @Obrigatorio
    private BigDecimal pesoPreco;

    public ClassificacaoFinalSolicitacao() {
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

    public BigDecimal getPesoTecnica() {
        return pesoTecnica;
    }

    public void setPesoTecnica(BigDecimal pesoTecnica) {
        this.pesoTecnica = pesoTecnica;
    }

    public BigDecimal getPesoPreco() {
        return pesoPreco;
    }

    public void setPesoPreco(BigDecimal pesoPreco) {
        this.pesoPreco = pesoPreco;
    }

}
