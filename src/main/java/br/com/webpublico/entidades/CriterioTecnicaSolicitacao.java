package br.com.webpublico.entidades;

import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.ValidadorEntidade;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: JoãoPaulo
 * Date: 22/08/14
 * Time: 15:51
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited

@Etiqueta("Critério Técnica Solicitação")
public class CriterioTecnicaSolicitacao extends SuperEntidade implements Serializable, ValidadorEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Etiqueta("Solicitação Material")
    @OneToOne
    private SolicitacaoMaterial solicitacaoMaterial;

    @Etiqueta("Valor Para Habilitação")
    @Pesquisavel
    @Tabelavel
    @Positivo(permiteZero = false)
    @Obrigatorio
    private BigDecimal valorHabilitacao;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "criterioTecnicaSolicitacao")
    private List<ItemCriterioTecnica> itens;

    public CriterioTecnicaSolicitacao() {
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

    public BigDecimal getValorHabilitacao() {
        return valorHabilitacao;
    }

    public void setValorHabilitacao(BigDecimal valorHabilitacao) {
        this.valorHabilitacao = valorHabilitacao;
    }

    public List<ItemCriterioTecnica> getItens() {
        return itens;
    }

    public void setItens(List<ItemCriterioTecnica> itens) {
        this.itens = itens;
    }

    @Override
    public void validarConfirmacao() throws ValidacaoException {
        return;
    }

}
