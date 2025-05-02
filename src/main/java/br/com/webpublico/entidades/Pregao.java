/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoStatusItemPregao;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * @author lucas
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Licitacao")
@Etiqueta("Pregão")
public class Pregao extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Temporal(TemporalType.DATE)
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Realizado em")
    @Pesquisavel
    private Date realizadoEm;

    @ManyToOne
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Licitação")
    @Pesquisavel
    private Licitacao licitacao;

    @Invisivel
    @Etiqueta("Itens do Pregão")
    @OneToMany(mappedBy = "pregao", cascade = CascadeType.REMOVE)
    private List<ItemPregao> listaDeItemPregao;

    @Invisivel
    @Etiqueta("Itenções de Recurso")
    @OneToMany(mappedBy = "pregao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<IntencaoDeRecurso> listaDeIntencaoDeRecursos;

    public Pregao() {
        super();
        listaDeIntencaoDeRecursos = Lists.newArrayList();
        listaDeItemPregao = Lists.newArrayList();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getRealizadoEm() {
        return realizadoEm;
    }

    public void setRealizadoEm(Date realizadoEm) {
        this.realizadoEm = realizadoEm;
    }

    public Licitacao getLicitacao() {
        return licitacao;
    }

    public void setLicitacao(Licitacao licitacao) {
        this.licitacao = licitacao;
    }

    public List<ItemPregao> getListaDeItemPregao() {
        if (listaDeItemPregao != null) {
            Collections.sort(listaDeItemPregao, new Comparator<ItemPregao>() {
                @Override
                public int compare(ItemPregao o1, ItemPregao o2) {
                    if (o1.getItemPregaoItemProcesso() != null &&
                        o1.getItemPregaoItemProcesso().getItemProcessoDeCompra() != null &&
                        o1.getItemPregaoItemProcesso().getItemProcessoDeCompra().getLoteProcessoDeCompra() != null &&
                        o2.getItemPregaoItemProcesso() != null &&
                        o2.getItemPregaoItemProcesso().getItemProcessoDeCompra() != null &&
                        o2.getItemPregaoItemProcesso().getItemProcessoDeCompra().getLoteProcessoDeCompra() != null) {
                        int i = o1.getItemPregaoItemProcesso().getItemProcessoDeCompra().getLoteProcessoDeCompra().getNumero().compareTo(
                            o2.getItemPregaoItemProcesso().getItemProcessoDeCompra().getLoteProcessoDeCompra().getNumero());
                        if (i == 0) {
                            i = o1.getItemPregaoItemProcesso().getItemProcessoDeCompra().getNumero().compareTo(
                                o2.getItemPregaoItemProcesso().getItemProcessoDeCompra().getNumero());
                        }
                        return i;
                    } else if (o1.getItemPregaoLoteProcesso() != null && o2.getItemPregaoItemProcesso() != null) {
                        return o1.getItemPregaoLoteProcesso().getLoteProcessoDeCompra().getNumero().compareTo(
                            o2.getItemPregaoLoteProcesso().getLoteProcessoDeCompra().getNumero());
                    }
                    return 0;
                }
            });
        }
        return listaDeItemPregao;
    }

    public void setListaDeItemPregao(List<ItemPregao> listaDeObjetoPregao) {
        this.listaDeItemPregao = listaDeObjetoPregao;
    }

    public List<IntencaoDeRecurso> getListaDeIntencaoDeRecursos() {
        return listaDeIntencaoDeRecursos;
    }

    public void setListaDeIntencaoDeRecursos(List<IntencaoDeRecurso> listaDeIntencaoDeRecursos) {
        this.listaDeIntencaoDeRecursos = listaDeIntencaoDeRecursos;
    }

    @Override
    public String toString() {
        String descricao = " Realizado em : " + new SimpleDateFormat("dd/MM/yyy").format(realizadoEm) + " - " + licitacao.toStringAutoComplete();
        if (descricao.length() > 110) {
            return descricao.substring(0, 110) + "...";
        }
        return descricao;
    }

    public boolean isApurado() {
        for (ItemPregao itemPregao : listaDeItemPregao) {
            if (TipoStatusItemPregao.EM_ANDAMENTO.equals(itemPregao.getTipoStatusItemPregao())) {
                return false;
            }
        }
        return true;
    }

    public void atribuirValorLanceRodadaAnterior(ItemPregao itemPregao, RodadaPregao rodadaPregao) {
        RodadaPregao rodadaAnteriorAEsta = itemPregao.getRodadaAnteriorAEsta(rodadaPregao);
        if (rodadaAnteriorAEsta != null) {
            for (LancePregao lanceRodadaAnterior : rodadaAnteriorAEsta.getListaDeLancePregao()) {
                for (LancePregao lanceAtual : rodadaPregao.getListaDeLancePregao()) {
                    if (lanceAtual.getPropostaFornecedor().equals(lanceRodadaAnterior.getPropostaFornecedor())) {
                        boolean isMaiorDesconto = itemPregao.getPregao().getLicitacao().getProcessoDeCompra().getSolicitacaoMaterial().getTipoAvaliacao().isMaiorDesconto();
                        BigDecimal valorLanceAnterior = isMaiorDesconto ? lanceRodadaAnterior.getPercentualDesconto() : lanceRodadaAnterior.getValor();
                        lanceAtual.setValorNaRodadaAnterior(valorLanceAnterior);
                    }
                }
            }
        }
    }
}
