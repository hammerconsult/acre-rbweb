/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.OperacaoFormula;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.rh.integracao.TipoContabilizacao;
import br.com.webpublico.enums.rh.integracao.TipoContribuicao;
import org.hibernate.envers.Audited;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author peixe
 */
@Entity

@Audited
public class FonteEventoFP implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(FonteEventoFP.class);

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private FontesRecursoFP fontesRecursoFP;
    @ManyToOne
    private Pessoa credor;
    @ManyToOne
    private Pessoa fornecedor;
    @ManyToOne
    private ClasseCredor classeCredor;
    @ManyToOne
    private ContaExtraorcamentaria contaExtraorcamentaria;
    @ManyToOne
    private SubConta subConta;
    @ManyToOne
    private EventoFP eventoFP;
    @Enumerated(EnumType.STRING)
    private OperacaoFormula operacaoFormula;
    @ManyToOne
    private Conta desdobramento;
    @Enumerated(EnumType.STRING)
    private TipoContabilizacao tipoContabilizacao;
    @ManyToOne
    private DespesaORC despesaORC;
    @ManyToOne
    private FonteDespesaORC fonteDespesaORC;
    @ManyToOne
    private FonteDeRecursos fonteDeRecursos;
    @Enumerated(EnumType.STRING)
    private TipoContribuicao tipoContribuicao;
    @Transient
    private Operacoes operacao;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ContaExtraorcamentaria getContaExtraorcamentaria() {
        return contaExtraorcamentaria;
    }

    public void setContaExtraorcamentaria(ContaExtraorcamentaria contaExtraorcamentaria) {
        this.contaExtraorcamentaria = contaExtraorcamentaria;
    }

    public Pessoa getCredor() {
        return credor;
    }

    public SubConta getSubConta() {
        return subConta;
    }

    public void setSubConta(SubConta subConta) {
        this.subConta = subConta;
    }

    public void setCredor(Pessoa credor) {
        this.credor = credor;
    }

    public EventoFP getEventoFP() {
        return eventoFP;
    }

    public void setEventoFP(EventoFP eventoFP) {
        this.eventoFP = eventoFP;
    }

    public FontesRecursoFP getFontesRecursoFP() {
        return fontesRecursoFP;
    }

    public void setFontesRecursoFP(FontesRecursoFP fontesRecursoFP) {
        this.fontesRecursoFP = fontesRecursoFP;
    }

    public OperacaoFormula getOperacaoFormula() {
        return operacaoFormula;
    }

    public void setOperacaoFormula(OperacaoFormula operacaoFormula) {
        this.operacaoFormula = operacaoFormula;
    }

    public ClasseCredor getClasseCredor() {
        return classeCredor;
    }

    public void setClasseCredor(ClasseCredor classeCredor) {
        this.classeCredor = classeCredor;
    }

    public Conta getDesdobramento() {
        return desdobramento;
    }

    public void setDesdobramento(Conta desdobramento) {
        this.desdobramento = desdobramento;
    }

    public boolean isOperacaoEditar() {
        return Operacoes.EDITAR.equals(operacao);
    }

    public DespesaORC getDespesaORC() {
        return despesaORC;
    }

    public void setDespesaORC(DespesaORC despesaORC) {
        this.despesaORC = despesaORC;
    }

    public Operacoes getOperacao() {
        return operacao;
    }

    public void setOperacao(Operacoes operacao) {
        this.operacao = operacao;
    }

    public Pessoa getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(Pessoa fornecedor) {
        this.fornecedor = fornecedor;
    }

    public TipoContabilizacao getTipoContabilizacao() {
        return tipoContabilizacao;
    }

    public void setTipoContabilizacao(TipoContabilizacao tipoContabilizacao) {
        this.tipoContabilizacao = tipoContabilizacao;
    }

    public FonteDespesaORC getFonteDespesaORC() {
        return fonteDespesaORC;
    }

    public void setFonteDespesaORC(FonteDespesaORC fonteDespesaORC) {
        this.fonteDespesaORC = fonteDespesaORC;
    }

    public FonteDeRecursos getFonteDeRecursos() {
        return fonteDeRecursos;
    }

    public void setFonteDeRecursos(FonteDeRecursos fonteDeRecursos) {
        this.fonteDeRecursos = fonteDeRecursos;
    }

    public TipoContribuicao getTipoContribuicao() {
        return tipoContribuicao;
    }

    public void setTipoContribuicao(TipoContribuicao tipoContribuicao) {
        this.tipoContribuicao = tipoContribuicao;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof FonteEventoFP)) {
            return false;
        }
        FonteEventoFP other = (FonteEventoFP) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.FonteEventoFP[ id=" + id + " ]";
    }

}
