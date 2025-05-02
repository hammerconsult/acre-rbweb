package br.com.webpublico.entidades;

import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.ValidadorEntidade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: JoãoPaulo
 * Date: 01/09/14
 * Time: 15:22
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited

@Etiqueta("Item Proposta Técnica")
public class ItemPropostaTecnica extends SuperEntidade implements Serializable, ValidadorEntidade, Comparable<ItemPropostaTecnica> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Etiqueta("Proposta Técnica")
    @ManyToOne
    private PropostaTecnica propostaTecnica;

    @Etiqueta("Item Critério Técnica")
    @ManyToOne
    private ItemCriterioTecnica itemCriterioTecnica;

    @Etiqueta("Quantidade")
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    private BigDecimal quantidade;

    @Etiqueta("Ponto")
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    private BigDecimal ponto;

    public ItemPropostaTecnica() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PropostaTecnica getPropostaTecnica() {
        return propostaTecnica;
    }

    public void setPropostaTecnica(PropostaTecnica propostaTecnica) {
        this.propostaTecnica = propostaTecnica;
    }

    public ItemCriterioTecnica getItemCriterioTecnica() {
        return itemCriterioTecnica;
    }

    public void setItemCriterioTecnica(ItemCriterioTecnica itemCriterioTecnica) {
        this.itemCriterioTecnica = itemCriterioTecnica;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getPonto() {
        return ponto;
    }

    public void setPonto(BigDecimal ponto) {
        this.ponto = ponto;
    }

    @Override
    public void validarConfirmacao() throws ValidacaoException {
        return;
    }

    public boolean isQuantidadeValida() {
        try {
            return itemCriterioTecnica.getPontuacaoTecnicaReferenteQuantidadeInformada(quantidade) != null;
        } catch (ExcecaoNegocioGenerica eng) {
            return false;
        }
    }

    @Override
    public int compareTo(ItemPropostaTecnica o) {
        return this.getItemCriterioTecnica().getDescricao().compareTo(o.getItemCriterioTecnica().getDescricao());
    }
}
