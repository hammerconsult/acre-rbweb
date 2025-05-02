/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoMovimentoProcessoLicitatorio;
import br.com.webpublico.enums.TipoPrazo;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.EntidadeDetendorDocumentoLicitacao;
import br.com.webpublico.interfaces.ValidadorEntidade;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * @author renato
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Licitacao")
@Etiqueta("Proposta do Fornecedor")
public class PropostaFornecedor extends SuperEntidade implements ValidadorEntidade, EntidadeDetendorDocumentoLicitacao {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Obrigatorio
    @Tabelavel
    @Etiqueta("Data da Proposta")
    @Pesquisavel
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataProposta;

    @ManyToOne
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Fornecedor")
    @Pesquisavel
    private Pessoa fornecedor;

    @OneToOne
    @Tabelavel
    @Etiqueta("Fornecedor")
    @Pesquisavel
    private LicitacaoFornecedor licitacaoFornecedor;

    @ManyToOne
    @Tabelavel
    @Etiqueta("Representante")
    private RepresentanteLicitacao representante;

    @ManyToOne
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Licitação")
    private Licitacao licitacao;

    @Etiqueta("Itens da proposta")
    @OneToMany(mappedBy = "propostaFornecedor", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Tabelavel(campoSelecionado = false)
    private List<ItemPropostaFornecedor> listaDeItensPropostaFornecedor;

    @Invisivel
    @OneToMany(mappedBy = "propostaFornecedor", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Tabelavel(campoSelecionado = false)
    private List<LotePropostaFornecedor> lotes;

    @Etiqueta("Prazo de Validade")
    @Obrigatorio
    @Pesquisavel
    private Integer validadeDaProposta;

    @Etiqueta("Tipo Prazo de Validade")
    @Obrigatorio
    @Enumerated(EnumType.STRING)
    @Pesquisavel
    private TipoPrazo tipoPrazoValidade;

    @Etiqueta("Prazo de Execução")
    @Obrigatorio
    @Pesquisavel
    private Integer prazoDeExecucao;

    @Etiqueta("Tipo Prazo de Execução")
    @Obrigatorio
    @Enumerated(EnumType.STRING)
    @Pesquisavel
    private TipoPrazo tipoPrazoExecucao;

    @Etiqueta("Instrumento Representação")
    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    private String instrumentoRepresentacao;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private DetentorDocumentoLicitacao detentorDocumentoLicitacao;


    public PropostaFornecedor() {

    }

    public PropostaFornecedor(Long id, Date data) {
        this.id = id;
        this.dataProposta = data;
    }

    public Integer getPrazoDeExecucao() {
        return prazoDeExecucao;
    }

    public void setPrazoDeExecucao(Integer prazoDeExecucao) {
        this.prazoDeExecucao = prazoDeExecucao;
    }

    public TipoPrazo getTipoPrazoExecucao() {
        return tipoPrazoExecucao;
    }

    public void setTipoPrazoExecucao(TipoPrazo tipoPrazoExecucao) {
        this.tipoPrazoExecucao = tipoPrazoExecucao;
    }

    public TipoPrazo getTipoPrazoValidade() {
        return tipoPrazoValidade;
    }

    public void setTipoPrazoValidade(TipoPrazo tipoPrazoValidade) {
        this.tipoPrazoValidade = tipoPrazoValidade;
    }

    public Integer getValidadeDaProposta() {
        return validadeDaProposta;
    }

    public void setValidadeDaProposta(Integer validadeDaProposta) {
        this.validadeDaProposta = validadeDaProposta;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataProposta() {
        return dataProposta;
    }

    public void setDataProposta(Date dataProposta) {
        this.dataProposta = dataProposta;
    }

    public Pessoa getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(Pessoa fornecedor) {
        this.fornecedor = fornecedor;
    }

    public LicitacaoFornecedor getLicitacaoFornecedor() {
        return licitacaoFornecedor;
    }

    public void setLicitacaoFornecedor(LicitacaoFornecedor licitacaoFornecedor) {
        this.licitacaoFornecedor = licitacaoFornecedor;
    }

    public Licitacao getLicitacao() {
        return licitacao;
    }

    public void setLicitacao(Licitacao licitacao) {
        this.licitacao = licitacao;
    }

    public List<ItemPropostaFornecedor> getListaDeItensPropostaFornecedor() {
        if (listaDeItensPropostaFornecedor != null) {
            Collections.sort(listaDeItensPropostaFornecedor, new Comparator<ItemPropostaFornecedor>() {
                @Override
                public int compare(ItemPropostaFornecedor o1, ItemPropostaFornecedor o2) {
                    if (o1.getItemProcessoDeCompra() != null) {
                        int i = o1.getItemProcessoDeCompra().getLoteProcessoDeCompra().getNumero().compareTo(
                            o2.getItemProcessoDeCompra().getLoteProcessoDeCompra().getNumero());
                        if (i == 0) {
                            i = o1.getItemProcessoDeCompra().getNumero().compareTo(
                                o2.getItemProcessoDeCompra().getNumero());
                        }
                        return i;
                    } else {
                        return o1.getLotePropostaFornecedor().getLoteProcessoDeCompra().getNumero().compareTo(
                            o2.getLotePropostaFornecedor().getLoteProcessoDeCompra().getNumero());
                    }
                }
            });
        }
        return listaDeItensPropostaFornecedor;
    }

    public void setListaDeItensPropostaFornecedor(List<ItemPropostaFornecedor> listaDeItensPropostaFornecedor) {
        this.listaDeItensPropostaFornecedor = listaDeItensPropostaFornecedor;
    }

    public List<LotePropostaFornecedor> getLotes() {
        return lotes;
    }

    public void setLotes(List<LotePropostaFornecedor> lotes) {
        this.lotes = lotes;
    }

    public RepresentanteLicitacao getRepresentante() {
        return representante;
    }

    public void setRepresentante(RepresentanteLicitacao representante) {
        this.representante = representante;
    }

    public String getInstrumentoRepresentacao() {
        return instrumentoRepresentacao;
    }

    public void setInstrumentoRepresentacao(String instrumentoRepresentacao) {
        this.instrumentoRepresentacao = instrumentoRepresentacao;
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
        return TipoMovimentoProcessoLicitatorio.PROPOSTA_FORNECEDOR;
    }

    @Override
    public String toString() {
        try {
            return fornecedor.toString();
        } catch (Exception e) {
            return "";
        }
    }

    @Override
    public void validarConfirmacao() throws ValidacaoException {
        return;
    }

    public BigDecimal getValorTotalProposta() {
        BigDecimal total = BigDecimal.ZERO;
        try {
            for (LotePropostaFornecedor lote : getLotes()) {
                total = total.add(lote.getValor());
            }
            return total;
        } catch (Exception ex) {
            return total;
        }
    }

    public LotePropostaFornecedor getLotePropostaDoLoteProcessoDeCompra(LoteProcessoDeCompra loteProcessoDeCompra) {
        for (LotePropostaFornecedor loteProposta : this.getLotes()) {
            if (loteProposta.getLoteProcessoDeCompra().equals(loteProcessoDeCompra)) {
                return loteProposta;
            }
        }
        return null;
    }

    public List<ItemPropostaFornecedor> getItensCotadosPorItemProcessoDeCompra(ItemProcessoDeCompra itemProcessoDeCompra) {
        List<ItemPropostaFornecedor> itensPropostaCotado = Lists.newArrayList();
        for (ItemPropostaFornecedor itemProposta : itemProcessoDeCompra.getItensPropostaFornecedor()) {
            if (itemProposta.hasPreco()) {
                itensPropostaCotado.add(itemProposta);
            }
        }
        return itensPropostaCotado;
    }

    public List<LotePropostaFornecedor> getLotesCotadosPorLoteProcessoDeCompra(LoteProcessoDeCompra loteProcessoDeCompra) {
        List<LotePropostaFornecedor> lotesPropostaCotado = Lists.newArrayList();
        for (LotePropostaFornecedor loteProposta : loteProcessoDeCompra.getLotesProposta()) {
            if (loteProposta.hasValor()) {
                lotesPropostaCotado.add(loteProposta);
            }
        }
        return lotesPropostaCotado;
    }

    public ItemPropostaFornecedor getItemDeMenorPropostaPorItemProcessoDeCompra(ItemProcessoDeCompra itemProcessoDeCompra) {
        List<ItemPropostaFornecedor> itensPropostaCotado = getItensCotadosPorItemProcessoDeCompra(itemProcessoDeCompra);
        if (!itensPropostaCotado.isEmpty()) {
            ComparatorPropostaFornecedor.ordenarItensPorPreco(itensPropostaCotado);
            return itensPropostaCotado.get(0);
        }
        return null;
    }

    public LotePropostaFornecedor getLoteDeMenorPropostaPorLoteProcessoDeCompra(LoteProcessoDeCompra loteProcessoDeCompra) {
        List<LotePropostaFornecedor> lotesPropostaCotado = getLotesCotadosPorLoteProcessoDeCompra(loteProcessoDeCompra);
        if (!lotesPropostaCotado.isEmpty()) {
            ComparatorPropostaFornecedor.ordenarLotesPorValor(lotesPropostaCotado);
            return lotesPropostaCotado.get(0);
        }
        return null;
    }

    public List<ItemProcessoDeCompra> getItensProcessoDeCompra() {
        List<ItemProcessoDeCompra> itens = Lists.newArrayList();
        for (ItemPropostaFornecedor item : listaDeItensPropostaFornecedor) {
            if (!itens.contains(item.getItemProcessoDeCompra())) {
                itens.add(item.getItemProcessoDeCompra());
            }
        }
        return itens;
    }

    public void anularNotas() {
        for (LotePropostaFornecedor lote : lotes) {
            if (licitacao.isTipoApuracaoPrecoItem()) {
                lote.anularNotas();
            }
            if (licitacao.isTipoApuracaoPrecoLote()) {
                lote.setNotaPreco(null);
                lote.setNotaClassificacaoFinal(null);
            }
        }
    }
}

class ComparatorPropostaFornecedor {

    private ComparatorPropostaFornecedor() {
    }

    public static Comparator<ItemPropostaFornecedor> ordenarItensPorPreco(List<ItemPropostaFornecedor> itens) {
        Comparator<ItemPropostaFornecedor> comparator = new Comparator<ItemPropostaFornecedor>() {
            @Override
            public int compare(ItemPropostaFornecedor o1, ItemPropostaFornecedor o2) {
                return o1.getPreco().compareTo(o2.getPreco());
            }
        };
        Collections.sort(itens, comparator);
        return comparator;
    }

    public static Comparator<LotePropostaFornecedor> ordenarLotesPorValor(List<LotePropostaFornecedor> lotes) {
        Comparator<LotePropostaFornecedor> comparator = new Comparator<LotePropostaFornecedor>() {
            @Override
            public int compare(LotePropostaFornecedor o1, LotePropostaFornecedor o2) {
                return o1.getValor().compareTo(o2.getValor());
            }
        };
        Collections.sort(lotes, comparator);
        return comparator;
    }
}
