/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoOperacaoORC;
import br.com.webpublico.enums.TipoReservaSolicitacaoCompra;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import com.google.common.collect.ComparisonChain;
import org.hibernate.envers.Audited;
import org.joda.time.LocalDateTime;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Wellington
 */
@Entity
@Audited
@GrupoDiagrama(nome = "Licitacao")
@Etiqueta("Reserva de Dotação de Solicitação de Compra - Fontes")
@Table(name = "DOTACAOSOLMATITEMFONTE")
public class DotacaoSolicitacaoMaterialItemFonte extends SuperEntidade implements Comparable<DotacaoSolicitacaoMaterialItemFonte>{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @Etiqueta("Dotação Solicitaão Material Item")
    private DotacaoSolicitacaoMaterialItem dotacaoSolMatItem;

    @Temporal(TemporalType.TIMESTAMP)
    @Etiqueta("Data de Lançamento")
    private Date dataLancamento;

    @Obrigatorio
    @ManyToOne
    @Etiqueta("Fonte Despesa Orc")
    private FonteDespesaORC fonteDespesaORC;

    @Obrigatorio
    @Etiqueta("Valor Reservado")
    private BigDecimal valor;

    @ManyToOne
    @Etiqueta("Exercício")
    private Exercicio exercicio;

    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo de Operação")
    private TipoOperacaoORC tipoOperacao;

    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo de Reserva")
    private TipoReservaSolicitacaoCompra tipoReserva;

    private Long idOrigem;

    private Boolean geraReservaOrc;

    public DotacaoSolicitacaoMaterialItemFonte() {
        this.valor = BigDecimal.ZERO;
        this.geraReservaOrc = false;
        this.tipoOperacao = TipoOperacaoORC.NORMAL;
        this.tipoReserva = TipoReservaSolicitacaoCompra.SOLICITACAO_COMPRA;
        this.dataLancamento = LocalDateTime.now().toDate();
    }

    public Long getIdOrigem() {
        return idOrigem;
    }

    public void setIdOrigem(Long idOrigemReserva) {
        this.idOrigem = idOrigemReserva;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DotacaoSolicitacaoMaterialItem getDotacaoSolMatItem() {
        return dotacaoSolMatItem;
    }

    public void setDotacaoSolMatItem(DotacaoSolicitacaoMaterialItem dotacaoSolicitacaoMaterialItem) {
        this.dotacaoSolMatItem = dotacaoSolicitacaoMaterialItem;
    }

    public FonteDespesaORC getFonteDespesaORC() {
        return fonteDespesaORC;
    }

    public void setFonteDespesaORC(FonteDespesaORC fonteDespesaORC) {
        this.fonteDespesaORC = fonteDespesaORC;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public TipoOperacaoORC getTipoOperacao() {
        return tipoOperacao;
    }

    public void setTipoOperacao(TipoOperacaoORC tipoOperacao) {
        this.tipoOperacao = tipoOperacao;
    }

    public TipoReservaSolicitacaoCompra getTipoReserva() {
        return tipoReserva;
    }

    public void setTipoReserva(TipoReservaSolicitacaoCompra tipoReserva) {
        this.tipoReserva = tipoReserva;
    }

    public Date getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(Date dataLancamento) {
        this.dataLancamento = dataLancamento;
    }

    public Boolean getGeraReservaOrc() {
        return geraReservaOrc != null ? geraReservaOrc : false;
    }

    public void setGeraReservaOrc(Boolean geraReservaOrc) {
        this.geraReservaOrc = geraReservaOrc;
    }

    @Override
    public int compareTo(DotacaoSolicitacaoMaterialItemFonte dotFonte) {
        if (dotFonte.getFonteDespesaORC() != null && getFonteDespesaORC() != null) {
            return ComparisonChain.start()
                .compare(getFonteDespesaORC().getDespesaORC().getCodigo(), dotFonte.getFonteDespesaORC().getDespesaORC().getCodigo())
                .compare(getDataLancamento(), dotFonte.getDataLancamento())
                .result();
        }
        return 0;
    }

}
