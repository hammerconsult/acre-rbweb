package br.com.webpublico.entidades;

import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.ValidadorEntidade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
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
 * Time: 16:35
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited

@Etiqueta("Item Critério Técnica")
public class ItemCriterioTecnica extends SuperEntidade implements Serializable, ValidadorEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private CriterioTecnicaSolicitacao criterioTecnicaSolicitacao;

    @Etiqueta("Descrição")
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    private String descricao;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "itemCriterioTecnica")
    private List<PontuacaoTecnica> pontos;


    @Enumerated(EnumType.STRING)
    @Invisivel
    private TipoItemCriterioTecnica tipoItemCriterioTecnica;


    public ItemCriterioTecnica() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CriterioTecnicaSolicitacao getCriterioTecnicaSolicitacao() {
        return criterioTecnicaSolicitacao;
    }

    public void setCriterioTecnicaSolicitacao(CriterioTecnicaSolicitacao criterioTecnicaSolicitacao) {
        this.criterioTecnicaSolicitacao = criterioTecnicaSolicitacao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public List<PontuacaoTecnica> getPontos() {
        return pontos;
    }

    public void setPontos(List<PontuacaoTecnica> intervalos) {
        this.pontos = intervalos;
    }

    @Override
    public void validarConfirmacao() throws ValidacaoException {
        return;
    }

    public PontuacaoTecnica getPontuacaoTecnicaReferenteQuantidadeInformada(BigDecimal quantidade) {
        for (PontuacaoTecnica pontuacaoTecnica : pontos) {
            if (quantidade.compareTo(pontuacaoTecnica.getQuantidadeInicial()) >= 0 && quantidade.compareTo(pontuacaoTecnica.getQuantidadeFinal()) <= 0) {
                return pontuacaoTecnica;
            }
        }
        throw new ExcecaoNegocioGenerica("Nenhuma pontuação encontrada para a quantidade: " + quantidade);
    }

    public PontuacaoTecnica getPontuacaoTecnicaQualitativaReferenteQuantidadeInformada(BigDecimal quantidade) {
        for (PontuacaoTecnica pontuacaoTecnica : pontos) {
            if (quantidade.compareTo(pontuacaoTecnica.getQuantidadeInicial()) >= 0 && quantidade.compareTo(pontuacaoTecnica.getQuantidadeFinal()) <= 0) {
                return pontuacaoTecnica;
            }
        }
        throw new ExcecaoNegocioGenerica("Nenhuma pontuação encontrada para a quantidade: " + quantidade);
    }

    public TipoItemCriterioTecnica getTipoItemCriterioTecnica() {
        return tipoItemCriterioTecnica;
    }

    public void setTipoItemCriterioTecnica(TipoItemCriterioTecnica tipoItemCriterioTecnica) {
        this.tipoItemCriterioTecnica = tipoItemCriterioTecnica;
    }

    public enum TipoItemCriterioTecnica {
        QUALITATIVO("Qualitativo"),
        QUANTITATIVO("Quantitativo");

        private String descricao;

        private TipoItemCriterioTecnica(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }
}
