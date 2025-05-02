/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * @author major
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Contabil")
public class DespesaExercConvenio extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Tabelavel
    private DespesaORC despesaORC;
    @ManyToOne
    @Tabelavel
    private Exercicio exercicio;
    @ManyToOne
    private ConvenioDespesa convenioDespesa;
    @ManyToOne
    private FonteDespesaORC fonteDespesaORC;
    private BigDecimal valor;
    private Boolean gerarSolicitacaoEmpenho;

    @OneToMany(mappedBy = "despesaExercConvenio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ConvenioDespSolicEmpenho> solicitacoes;

    public DespesaExercConvenio() {
        super();
        this.valor = new BigDecimal(BigInteger.ZERO);
        solicitacoes = new ArrayList<>();
    }

    public DespesaExercConvenio(DespesaORC despesaORC, Exercicio exercicio, ConvenioDespesa convenioDespesa) {
        this.despesaORC = despesaORC;
        this.exercicio = exercicio;
        this.convenioDespesa = convenioDespesa;
    }

    public DespesaORC getDespesaORC() {
        return despesaORC;
    }

    public void setDespesaORC(DespesaORC despesaORC) {
        this.despesaORC = despesaORC;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public ConvenioDespesa getConvenioDespesa() {
        return convenioDespesa;
    }

    public void setConvenioDespesa(ConvenioDespesa convenioDespesa) {
        this.convenioDespesa = convenioDespesa;
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

    public Boolean getGerarSolicitacaoEmpenho() {
        return gerarSolicitacaoEmpenho;
    }

    public void setGerarSolicitacaoEmpenho(Boolean gerarSolicitacaoEmpenho) {
        this.gerarSolicitacaoEmpenho = gerarSolicitacaoEmpenho;
    }

    public List<ConvenioDespSolicEmpenho> getSolicitacoes() {
        return solicitacoes;
    }

    public void setSolicitacoes(List<ConvenioDespSolicEmpenho> solicitacoes) {
        this.solicitacoes = solicitacoes;
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.DespesaExercConvenio[ id=" + id + " ]";
    }
}
