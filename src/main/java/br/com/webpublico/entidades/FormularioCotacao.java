package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoApuracaoLicitacao;
import br.com.webpublico.enums.TipoCotacao;
import br.com.webpublico.enums.TipoMovimentoProcessoLicitatorio;
import br.com.webpublico.enums.TipoSolicitacao;
import br.com.webpublico.interfaces.EntidadeDetendorDocumentoLicitacao;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 17/04/15
 * Time: 08:19
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
@Etiqueta("Formulário de Cotação/Planilha Orçamentária")
public class FormularioCotacao extends SuperEntidade implements EntidadeDetendorDocumentoLicitacao {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Tabelavel(ordemApresentacao = 1)
    @Pesquisavel
    @Etiqueta("Exercício")
    @Obrigatorio
    @ManyToOne
    private Exercicio exercicio;

    @Tabelavel(ordemApresentacao = 2)
    @Pesquisavel
    @Etiqueta("Número")
    private Integer numero;

    @Tabelavel(ordemApresentacao = 3)
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Criado em")
    @Temporal(value = TemporalType.DATE)
    private Date dataFormulario;

    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Tipo de Objeto")
    @Enumerated(value = EnumType.STRING)
    private TipoSolicitacao tipoSolicitacao;

    @Tabelavel(ordemApresentacao = 4, TIPOCAMPO = Tabelavel.TIPOCAMPO.TEXTO)
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Objeto")
    private String objeto;

    @Etiqueta("Unidade Administrativa")
    @Obrigatorio
    @ManyToOne
    private UnidadeOrganizacional unidadeOrganizacional;

    @OneToMany(mappedBy = "formularioCotacao", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<LoteFormularioCotacao> lotesFormularioCotacao;

    @Tabelavel(ordemApresentacao = 5)
    @Pesquisavel
    @Etiqueta("Criado Por")
    @ManyToOne
    private UsuarioSistema usuarioCriacao;

    @Pesquisavel
    @Etiqueta("Tipo de Apuração")
    @Obrigatorio
    @Enumerated(EnumType.STRING)
    private TipoApuracaoLicitacao tipoApuracaoLicitacao;

    @Pesquisavel
    @Etiqueta("Observações")
    @Obrigatorio
    private String observacao;

    @Etiqueta("Intenção de Registro Preço")
    @ManyToOne
    private IntencaoRegistroPreco intencaoRegistroPreco;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private DetentorDocumentoLicitacao detentorDocumentoLicitacao;

    public FormularioCotacao() {
        super();
        dataFormulario = new Date();
        lotesFormularioCotacao = Lists.newArrayList();
    }

    public FormularioCotacao(Long id, Exercicio exercicio, Integer numero, Date dataFormulario) {
        this.id = id;
        this.exercicio = exercicio;
        this.numero = numero;
        this.dataFormulario = dataFormulario;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public Date getDataFormulario() {
        return dataFormulario;
    }

    public void setDataFormulario(Date dataFormulario) {
        this.dataFormulario = dataFormulario;
    }

    public TipoSolicitacao getTipoSolicitacao() {
        return tipoSolicitacao;
    }

    public void setTipoSolicitacao(TipoSolicitacao tipoSolicitacao) {
        this.tipoSolicitacao = tipoSolicitacao;
    }

    public String getObjeto() {
        return objeto;
    }

    public void setObjeto(String objeto) {
        this.objeto = objeto;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public List<LoteFormularioCotacao> getLotesFormulario() {
        if (lotesFormularioCotacao != null) {
            Collections.sort(lotesFormularioCotacao, new Comparator<LoteFormularioCotacao>() {
                @Override
                public int compare(LoteFormularioCotacao o1, LoteFormularioCotacao o2) {
                    return o1.getNumero().compareTo(o2.getNumero());
                }
            });
        }
        return lotesFormularioCotacao;
    }

    public void setLotesFormularioCotacao(List<LoteFormularioCotacao> lotesFormularioCotacao) {
        this.lotesFormularioCotacao = lotesFormularioCotacao;
    }

    public UsuarioSistema getUsuarioCriacao() {
        return usuarioCriacao;
    }

    public void setUsuarioCriacao(UsuarioSistema usuarioCriacao) {
        this.usuarioCriacao = usuarioCriacao;
    }

    public TipoApuracaoLicitacao getTipoApuracaoLicitacao() {
        return tipoApuracaoLicitacao;
    }

    public void setTipoApuracaoLicitacao(TipoApuracaoLicitacao tipoApuracaoLicitacao) {
        this.tipoApuracaoLicitacao = tipoApuracaoLicitacao;
    }

    public IntencaoRegistroPreco getIntencaoRegistroPreco() {
        return intencaoRegistroPreco;
    }

    public void setIntencaoRegistroPreco(IntencaoRegistroPreco intencaoRegistroPreco) {
        this.intencaoRegistroPreco = intencaoRegistroPreco;
    }

    public Boolean getFormularioIrp() {
        return intencaoRegistroPreco != null;
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
        return TipoMovimentoProcessoLicitatorio.FORMULARIO_COTACAO;
    }

    @Override
    public String toString() {
        return Util.cortarString("Nº " + numero + "/" + exercicio + " - " + objeto, 70);
    }

    public void criarItemLoteFormularioCotacaoComBaseNosItensReposicao(SolicitacaoReposicaoEstoque solicitacaoReposicaoEstoque) {

        LoteFormularioCotacao lote = new LoteFormularioCotacao();
        lote.setDescricao("Lote 1 - " + solicitacaoReposicaoEstoque.toString());
        lote.setFormularioCotacao(this);
        lote.setNumero(1);

        this.getLotesFormulario().add(lote);

        int numItem = 0;
        for (ItemSolicitacaoReposicaoEstoque isre : solicitacaoReposicaoEstoque.getItensSolicitados()) {
            numItem++;
            ItemLoteFormularioCotacao itemLoteFormularioCotacao = new ItemLoteFormularioCotacao(lote, numItem, isre.getMaterial().getObjetoCompra(), "", isre.getQuantidadeParaComprar());
            isre.setItemLoteFormulario(itemLoteFormularioCotacao);
            lote.getItensLoteFormularioCotacao().add(itemLoteFormularioCotacao);
        }
    }

    public boolean isTipoApuracaoPorLote() {
        return TipoApuracaoLicitacao.LOTE.equals(tipoApuracaoLicitacao);
    }

    public boolean isTipoApuracaoPorItem() {
        return TipoApuracaoLicitacao.ITEM.equals(tipoApuracaoLicitacao);
    }

    public boolean isItemCotacaoPorValorReferencia() {
        return containsItensCotacaoPor(TipoCotacao.VALOR_REFERENCIA);
    }

    public boolean isItemCotacaoPorFornecedor() {
        return containsItensCotacaoPor(TipoCotacao.FORNECEDOR);
    }

    private boolean containsItensCotacaoPor(TipoCotacao tipoCotacao) {
        if (getLotesFormulario() != null) {
            for (LoteFormularioCotacao lote : getLotesFormulario()) {
                for (ItemLoteFormularioCotacao item : lote.getItensLoteFormularioCotacao()) {
                    if (item.getObjetoCompra().getGrupoObjetoCompra().getTipoCotacao().equals(tipoCotacao)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
