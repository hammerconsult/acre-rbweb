package br.com.webpublico.entidades;

import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.ValidadorEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Positivo;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: JoãoPaulo
 * Date: 28/08/14
 * Time: 11:38
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited

@Etiqueta("Pontuação Item Critério Técnica")
public class PontuacaoTecnica extends SuperEntidade implements Serializable, ValidadorEntidade, Comparable<PontuacaoTecnica> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Etiqueta("Item Critério Técnica")
    @ManyToOne
    private ItemCriterioTecnica itemCriterioTecnica;

    @Etiqueta("Quantidade Inicial")
    @Pesquisavel
    @Tabelavel
    @Positivo
    private BigDecimal quantidadeInicial;

    @Etiqueta("Quantidade Final")
    @Pesquisavel
    @Tabelavel
    @Positivo
    private BigDecimal quantidadeFinal;

    @Etiqueta("Ponto")
    @Pesquisavel
    @Tabelavel
    @Positivo
    private BigDecimal ponto;

    public PontuacaoTecnica() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ItemCriterioTecnica getItemCriterioTecnica() {
        return itemCriterioTecnica;
    }

    public void setItemCriterioTecnica(ItemCriterioTecnica itemCriterioTecnica) {
        this.itemCriterioTecnica = itemCriterioTecnica;
    }

    public BigDecimal getQuantidadeInicial() {
        return quantidadeInicial;
    }

    public void setQuantidadeInicial(BigDecimal quantidadeInicial) {
        this.quantidadeInicial = quantidadeInicial;
    }

    public BigDecimal getQuantidadeFinal() {
        return quantidadeFinal;
    }

    public void setQuantidadeFinal(BigDecimal quantidadeFinal) {
        this.quantidadeFinal = quantidadeFinal;
    }

    public BigDecimal getPonto() {
        return ponto;
    }

    public void setPonto(BigDecimal ponto) {
        this.ponto = ponto;
    }

    @Override
    public void validarConfirmacao() throws ValidacaoException {
        ValidacaoException ve = new ValidacaoException();
        if (getItemCriterioTecnica().getTipoItemCriterioTecnica().equals(ItemCriterioTecnica.TipoItemCriterioTecnica.QUANTITATIVO)) {
            if (getQuantidadeInicial() == null || getQuantidadeInicial().toString().trim().length() <= 0) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Quantidade Inicial deve ser informado.");
            }

            if (getQuantidadeFinal() == null || getQuantidadeFinal().toString().trim().length() <= 0) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Quantidade Final deve ser informado.");
            }

            if (getPonto() == null || getPonto().toString().trim().isEmpty()) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Ponto deve ser informado.");
            }

        }

        if (getItemCriterioTecnica().getTipoItemCriterioTecnica().equals(ItemCriterioTecnica.TipoItemCriterioTecnica.QUALITATIVO)) {
            if (getQuantidadeInicial() == null || getQuantidadeInicial().toString().trim().length() <= 0) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Pontuação Mínima deve ser informado.");
            }

            if (getQuantidadeFinal() == null || getQuantidadeFinal().toString().trim().length() <= 0) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Pontuação Máxima deve ser informado.");
            }
        }

        if (ve.temMensagens()) {
            throw ve;
        }


        if (getQuantidadeInicial().compareTo(getQuantidadeFinal()) > 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A quantidade inicial deve ser igual ou inferior à quantidade final.");
        }

//        if (getItemCriterioTecnica().getTipoItemCriterioTecnica().equals(ItemCriterioTecnica.TipoItemCriterioTecnica.QUANTITATIVO) && (getPonto() == null || getPonto().toString().isEmpty())) {
//            ve.adicionarMensagemDeCampoObrigatorio("O campo Ponto deve ser informado.");
//        }

        if (ve.temMensagens()) {
            throw ve;
        }
    }

    @Override
    public int compareTo(PontuacaoTecnica o) {
        return this.getQuantidadeInicial().compareTo(o.getQuantidadeInicial());
    }
}
