package br.com.webpublico.entidades;

import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author Alex
 */
@Entity
@Audited
@Table(name = "OBRAMEDSOLICITACAOEMPENHO")
public class ObraMedicaoSolicitacaoEmpenho extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ObraMedicao obraMedicao;
    @ManyToOne
    private DespesaORC despesaORC;
    @ManyToOne
    private FonteDespesaORC fonteDespesaORC;
    @OneToOne
    private SolicitacaoEmpenho solicitacaoEmpenho;
    @ManyToOne
    private Empenho empenho;
    private BigDecimal valor;

    public ObraMedicaoSolicitacaoEmpenho() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ObraMedicao getObraMedicao() {
        return obraMedicao;
    }

    public void setObraMedicao(ObraMedicao obraMedicao) {
        this.obraMedicao = obraMedicao;
    }

    public DespesaORC getDespesaORC() {
        return despesaORC;
    }

    public void setDespesaORC(DespesaORC despesaORC) {
        this.despesaORC = despesaORC;
    }

    public FonteDespesaORC getFonteDespesaORC() {
        return fonteDespesaORC;
    }

    public void setFonteDespesaORC(FonteDespesaORC fonteDespesaORC) {
        this.fonteDespesaORC = fonteDespesaORC;
    }

    public SolicitacaoEmpenho getSolicitacaoEmpenho() {
        return solicitacaoEmpenho;
    }

    public void setSolicitacaoEmpenho(SolicitacaoEmpenho solicitacaoEmpenho) {
        this.solicitacaoEmpenho = solicitacaoEmpenho;
    }

    public Empenho getEmpenho() {
        return empenho;
    }

    public void setEmpenho(Empenho empenho) {
        this.empenho = empenho;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    @Override
    public String toString() {
        return this.fonteDespesaORC.toString();
    }
}
