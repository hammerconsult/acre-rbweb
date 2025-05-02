/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoMovimentoProcessoLicitatorio;
import br.com.webpublico.enums.administrativo.SituacaoCotacao;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.EntidadeDetendorDocumentoLicitacao;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.*;

/**
 * @author lucas
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Licitacao")
@Etiqueta("Cotação/Planilha Orçamentária")
public class Cotacao extends SuperEntidade implements EntidadeDetendorDocumentoLicitacao {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Pesquisavel
    @Tabelavel
    @Etiqueta("Número")
    private Integer numero;

    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Exercício")
    @ManyToOne
    private Exercicio exercicio;

    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Data da Cotação/Planilha Orçamentária")
    @Temporal(TemporalType.DATE)
    private Date dataCotacao;

    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Length(maximo = 255)
    @Etiqueta("Descrição")
    private String descricao;

    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Formulário Cotação/Planilha Orçamentária")
    @ManyToOne
    private FormularioCotacao formularioCotacao;

    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Validade da Cotação/Planilha Orçamentária")
    @Temporal(TemporalType.DATE)
    private Date validadeCotacao;

    @OneToMany(mappedBy = "cotacao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LoteCotacao> lotes;

    @Pesquisavel
    @Tabelavel
    @Etiqueta("Unidade Administrativa")
    @Obrigatorio
    @ManyToOne
    private UnidadeOrganizacional unidadeOrganizacional;

    @Enumerated(EnumType.STRING)
    @Etiqueta("Situação")
    private SituacaoCotacao situacao;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private DetentorDocumentoLicitacao detentorDocumentoLicitacao;

    public Cotacao() {
        lotes = Lists.newArrayList();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public Date getDataCotacao() {
        return dataCotacao;
    }

    public void setDataCotacao(Date dataCotacao) {
        this.dataCotacao = dataCotacao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public FormularioCotacao getFormularioCotacao() {
        return formularioCotacao;
    }

    public void setFormularioCotacao(FormularioCotacao formularioCotacao) {
        this.formularioCotacao = formularioCotacao;
    }

    public Date getValidadeCotacao() {
        return validadeCotacao;
    }

    public void setValidadeCotacao(Date validadeCotacao) {
        this.validadeCotacao = validadeCotacao;
    }

    public List<LoteCotacao> getLotes() {
        if (lotes != null) {
            Collections.sort(lotes, new Comparator<LoteCotacao>() {
                @Override
                public int compare(LoteCotacao o1, LoteCotacao o2) {
                    return o1.getNumero().compareTo(o2.getNumero());
                }
            });
        }
        return lotes;
    }

    public void setLotes(List<LoteCotacao> lotes) {
        this.lotes = lotes;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    @Override
    public String toString() {
        return "Nº " + numero + "/" + exercicio + " - "
            + (descricao.length() > 100 ? getDescricaoCurta() : descricao);
    }

    public String getDescricaoCurta() {
        return descricao.substring(0, 100);
    }

    public int getQuantidadeAtualDeLotes() {
        return lotes.size();
    }

    public List<Pessoa> getFornecedores() {
        List<Pessoa> fornecedores = new ArrayList<>();
        for (LoteCotacao lote : lotes) {
            for (ItemCotacao item : lote.getItens()) {
                for (ValorCotacao valor : item.getValoresCotacao()) {
                    if (!fornecedores.contains(valor.getFornecedor())) {
                        fornecedores.add(valor.getFornecedor());
                    }
                }
            }
        }
        return fornecedores;
    }

    public List<ValorCotacao> getValorCotacao() {
        List<ValorCotacao> valorCotacoes = new ArrayList<>();
        for (LoteCotacao lote : lotes) {
            for (ItemCotacao item : lote.getItens()) {
                for (ValorCotacao valor : item.getValoresCotacao()) {
                    if (!valorCotacoes.contains(valor.getFornecedor())) {
                        valorCotacoes.add(valor);
                    }
                }
            }
        }
        return valorCotacoes;
    }

    public SituacaoCotacao getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoCotacao situacao) {
        this.situacao = situacao;
    }

    @Override
    public DetentorDocumentoLicitacao getDetentorDocumentoLicitacao() {
        return detentorDocumentoLicitacao;
    }

    @Override
    public void setDetentorDocumentoLicitacao(DetentorDocumentoLicitacao detentorDocumentoLicitacao) {
        this.detentorDocumentoLicitacao = detentorDocumentoLicitacao;
    }

    @Override
    public TipoMovimentoProcessoLicitatorio getTipoAnexo() {
        return TipoMovimentoProcessoLicitatorio.COTACAO;
    }
}
