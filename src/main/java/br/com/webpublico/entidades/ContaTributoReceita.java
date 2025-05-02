/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.OperacaoReceita;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.ValidadorVigencia;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author munif
 */
@GrupoDiagrama(nome = "Tributario")
@Entity

@Audited
public class ContaTributoReceita implements Serializable, Comparable<ContaTributoReceita>, ValidadorVigencia, Cloneable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private Tributo tributo;
    @ManyToOne
    private EnquadramentoTributoExerc enquadramento;
    @Temporal(TemporalType.DATE)
    private Date inicioVigencia;
    @Temporal(TemporalType.DATE)
    private Date fimVigencia;
    @ManyToOne
    private ContaReceita contaExercicio;
    @ManyToOne
    private FonteDeRecursos fonteDeRecursosExercicio;
    @ManyToOne
    private ContaReceita contaDividaAtiva;
    @ManyToOne
    private FonteDeRecursos fonteDeRecursosDividaAtiva;
    @ManyToOne
    private ContaReceita contaRenunciaExercicio;
    @ManyToOne
    private FonteDeRecursos fonteDeRecursosRenunciaExercicio;
    @ManyToOne
    private ContaReceita contaRenunciaDividaAtiva;
    @ManyToOne
    private FonteDeRecursos fonteDeRecursosRenunciaDividaAtiva;
    @ManyToOne
    private ContaReceita contaRestituicaoDividaAtiva;
    @ManyToOne
    private FonteDeRecursos fonteDeRecursosRestituicaoDividaAtiva;
    @ManyToOne
    private ContaReceita contaRestituicaoExercicio;
    @ManyToOne
    private FonteDeRecursos fonteDeRecursosRestituicaoExercicio;
    @ManyToOne
    private ContaReceita contaDescontoDividaAtiva;
    @ManyToOne
    private FonteDeRecursos fonteDeRecursosDescontoDividaAtiva;
    @ManyToOne
    private ContaReceita contaDescontoExercicio;
    @ManyToOne
    private FonteDeRecursos fonteDeRecursosDescontoExercicio;
    @ManyToOne
    private ContaReceita contaDeducoesDividaAtiva;
    @ManyToOne
    private FonteDeRecursos fonteDeRecursosDeducoesDividaAtiva;
    @ManyToOne
    private ContaReceita contaDeducoesExercicio;
    @ManyToOne
    private FonteDeRecursos fonteDeRecursosDeducoesExercicio;
    @Transient
    private Long criadoEm;

    @Enumerated(EnumType.STRING)
    @Etiqueta("Operação Receita da Arrecadação do Exercício")
    private OperacaoReceita operacaoArrecadacaoExercicio;

    @Enumerated(EnumType.STRING)
    @Etiqueta("Operação Receita da Arrecadação da Dívida Ativa")
    private OperacaoReceita operacaoArrecadacaoDivAtiva;

    @Enumerated(EnumType.STRING)
    @Etiqueta("Operação Receita da Renúncia do Exercício")
    private OperacaoReceita operacaoRenunciaExercicio;

    @Enumerated(EnumType.STRING)
    @Etiqueta("Operação Receita da Renúncia da Dívida Ativa")
    private OperacaoReceita operacaoRenunciaDivAtiva;

    @Enumerated(EnumType.STRING)
    @Etiqueta("Operação Receita da Restituição do Exercício")
    private OperacaoReceita operacaoRestituicaoExercicio;

    @Enumerated(EnumType.STRING)
    @Etiqueta("Operação Receita da Restituição da Dívida Ativa")
    private OperacaoReceita operacaoRestituicaoDivAtiva;

    @Enumerated(EnumType.STRING)
    @Etiqueta("Operação Receita de Desconto Concedidos do Exercício")
    private OperacaoReceita operacaoDescConcedidoExercicio;

    @Enumerated(EnumType.STRING)
    @Etiqueta("Operação Receita de Desconto Concedidos da Dívida Ativa")
    private OperacaoReceita operacaoDescConcedidoDivAtiva;

    @Enumerated(EnumType.STRING)
    @Etiqueta("Operação Receita de Outras Deduções do Exercício")
    private OperacaoReceita operacaoOutraDeducaoExercicio;

    @Enumerated(EnumType.STRING)
    @Etiqueta("Operação Receita de Outras Deduções da Dívida Ativa")
    private OperacaoReceita operacaoOutraDeducaoDivAtiva;

    public ContaTributoReceita() {
        criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Tributo getTributo() {
        return tributo;
    }

    public void setTributo(Tributo tributo) {
        this.tributo = tributo;
    }

    public EnquadramentoTributoExerc getEnquadramento() {
        return enquadramento;
    }

    public void setEnquadramento(EnquadramentoTributoExerc enquadramento) {
        this.enquadramento = enquadramento;
    }

    @Override
    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    @Override
    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    @Override
    public Date getFimVigencia() {
        return fimVigencia;
    }

    @Override
    public void setFimVigencia(Date fimVigencia) {
        this.fimVigencia = fimVigencia;
    }

    public ContaReceita getContaExercicio() {
        return contaExercicio;
    }

    public void setContaExercicio(ContaReceita contaExercicio) {
        this.contaExercicio = contaExercicio;
    }

    public FonteDeRecursos getFonteDeRecursosExercicio() {
        return fonteDeRecursosExercicio;
    }

    public void setFonteDeRecursosExercicio(FonteDeRecursos fonteDeRecursosExercicio) {
        this.fonteDeRecursosExercicio = fonteDeRecursosExercicio;
    }

    public ContaReceita getContaDividaAtiva() {
        return contaDividaAtiva;
    }

    public void setContaDividaAtiva(ContaReceita contaDividaAtiva) {
        this.contaDividaAtiva = contaDividaAtiva;
    }

    public FonteDeRecursos getFonteDeRecursosDividaAtiva() {
        return fonteDeRecursosDividaAtiva;
    }

    public void setFonteDeRecursosDividaAtiva(FonteDeRecursos fonteDeRecursosDividaAtiva) {
        this.fonteDeRecursosDividaAtiva = fonteDeRecursosDividaAtiva;
    }

    public ContaReceita getContaRenunciaExercicio() {
        return contaRenunciaExercicio;
    }

    public void setContaRenunciaExercicio(ContaReceita contaRenunciaExercicio) {
        this.contaRenunciaExercicio = contaRenunciaExercicio;
    }

    public FonteDeRecursos getFonteDeRecursosRenunciaExercicio() {
        return fonteDeRecursosRenunciaExercicio;
    }

    public void setFonteDeRecursosRenunciaExercicio(FonteDeRecursos fonteDeRecursosRenunciaExercicio) {
        this.fonteDeRecursosRenunciaExercicio = fonteDeRecursosRenunciaExercicio;
    }

    public ContaReceita getContaRenunciaDividaAtiva() {
        return contaRenunciaDividaAtiva;
    }

    public void setContaRenunciaDividaAtiva(ContaReceita contaRenunciaDividaAtiva) {
        this.contaRenunciaDividaAtiva = contaRenunciaDividaAtiva;
    }

    public FonteDeRecursos getFonteDeRecursosRenunciaDividaAtiva() {
        return fonteDeRecursosRenunciaDividaAtiva;
    }

    public void setFonteDeRecursosRenunciaDividaAtiva(FonteDeRecursos fonteDeRecursosRenunciaDividaAtiva) {
        this.fonteDeRecursosRenunciaDividaAtiva = fonteDeRecursosRenunciaDividaAtiva;
    }

    public ContaReceita getContaRestituicaoDividaAtiva() {
        return contaRestituicaoDividaAtiva;
    }

    public void setContaRestituicaoDividaAtiva(ContaReceita contaRestituicaoDividaAtiva) {
        this.contaRestituicaoDividaAtiva = contaRestituicaoDividaAtiva;
    }

    public FonteDeRecursos getFonteDeRecursosRestituicaoDividaAtiva() {
        return fonteDeRecursosRestituicaoDividaAtiva;
    }

    public void setFonteDeRecursosRestituicaoDividaAtiva(FonteDeRecursos fonteDeRecursosRestituicaoDividaAtiva) {
        this.fonteDeRecursosRestituicaoDividaAtiva = fonteDeRecursosRestituicaoDividaAtiva;
    }

    public ContaReceita getContaRestituicaoExercicio() {
        return contaRestituicaoExercicio;
    }

    public void setContaRestituicaoExercicio(ContaReceita contaRestituicaoExercicio) {
        this.contaRestituicaoExercicio = contaRestituicaoExercicio;
    }

    public FonteDeRecursos getFonteDeRecursosRestituicaoExercicio() {
        return fonteDeRecursosRestituicaoExercicio;
    }

    public void setFonteDeRecursosRestituicaoExercicio(FonteDeRecursos fonteDeRecursosRestituicaoExercicio) {
        this.fonteDeRecursosRestituicaoExercicio = fonteDeRecursosRestituicaoExercicio;
    }

    public ContaReceita getContaDescontoDividaAtiva() {
        return contaDescontoDividaAtiva;
    }

    public void setContaDescontoDividaAtiva(ContaReceita contaDescontoDividaAtiva) {
        this.contaDescontoDividaAtiva = contaDescontoDividaAtiva;
    }

    public FonteDeRecursos getFonteDeRecursosDescontoDividaAtiva() {
        return fonteDeRecursosDescontoDividaAtiva;
    }

    public void setFonteDeRecursosDescontoDividaAtiva(FonteDeRecursos fonteDeRecursosDescontoDividaAtiva) {
        this.fonteDeRecursosDescontoDividaAtiva = fonteDeRecursosDescontoDividaAtiva;
    }

    public ContaReceita getContaDescontoExercicio() {
        return contaDescontoExercicio;
    }

    public void setContaDescontoExercicio(ContaReceita contaDescontoExercicio) {
        this.contaDescontoExercicio = contaDescontoExercicio;
    }

    public FonteDeRecursos getFonteDeRecursosDescontoExercicio() {
        return fonteDeRecursosDescontoExercicio;
    }

    public void setFonteDeRecursosDescontoExercicio(FonteDeRecursos fonteDeRecursosDescontoExercicio) {
        this.fonteDeRecursosDescontoExercicio = fonteDeRecursosDescontoExercicio;
    }

    public ContaReceita getContaDeducoesDividaAtiva() {
        return contaDeducoesDividaAtiva;
    }

    public void setContaDeducoesDividaAtiva(ContaReceita contaDeducoesDividaAtiva) {
        this.contaDeducoesDividaAtiva = contaDeducoesDividaAtiva;
    }

    public FonteDeRecursos getFonteDeRecursosDeducoesDividaAtiva() {
        return fonteDeRecursosDeducoesDividaAtiva;
    }

    public void setFonteDeRecursosDeducoesDividaAtiva(FonteDeRecursos fonteDeRecursosDeducoesDividaAtiva) {
        this.fonteDeRecursosDeducoesDividaAtiva = fonteDeRecursosDeducoesDividaAtiva;
    }

    public ContaReceita getContaDeducoesExercicio() {
        return contaDeducoesExercicio;
    }

    public void setContaDeducoesExercicio(ContaReceita contaDeducoesExercicio) {
        this.contaDeducoesExercicio = contaDeducoesExercicio;
    }

    public FonteDeRecursos getFonteDeRecursosDeducoesExercicio() {
        return fonteDeRecursosDeducoesExercicio;
    }

    public void setFonteDeRecursosDeducoesExercicio(FonteDeRecursos fonteDeRecursosDeducoesExercicio) {
        this.fonteDeRecursosDeducoesExercicio = fonteDeRecursosDeducoesExercicio;
    }

    public OperacaoReceita getOperacaoArrecadacaoExercicio() {
        return operacaoArrecadacaoExercicio;
    }

    public void setOperacaoArrecadacaoExercicio(OperacaoReceita operacaoArrecadacaoExercicio) {
        this.operacaoArrecadacaoExercicio = operacaoArrecadacaoExercicio;
    }

    public OperacaoReceita getOperacaoArrecadacaoDivAtiva() {
        return operacaoArrecadacaoDivAtiva;
    }

    public void setOperacaoArrecadacaoDivAtiva(OperacaoReceita operacaoArrecadacaoDivAtiva) {
        this.operacaoArrecadacaoDivAtiva = operacaoArrecadacaoDivAtiva;
    }

    public OperacaoReceita getOperacaoRenunciaExercicio() {
        return operacaoRenunciaExercicio;
    }

    public void setOperacaoRenunciaExercicio(OperacaoReceita operacaoRenunciaExercicio) {
        this.operacaoRenunciaExercicio = operacaoRenunciaExercicio;
    }

    public OperacaoReceita getOperacaoRenunciaDivAtiva() {
        return operacaoRenunciaDivAtiva;
    }

    public void setOperacaoRenunciaDivAtiva(OperacaoReceita operacaoRenunciaDivAtiva) {
        this.operacaoRenunciaDivAtiva = operacaoRenunciaDivAtiva;
    }

    public OperacaoReceita getOperacaoRestituicaoExercicio() {
        return operacaoRestituicaoExercicio;
    }

    public void setOperacaoRestituicaoExercicio(OperacaoReceita operacaoRestituicaoExercicio) {
        this.operacaoRestituicaoExercicio = operacaoRestituicaoExercicio;
    }

    public OperacaoReceita getOperacaoRestituicaoDivAtiva() {
        return operacaoRestituicaoDivAtiva;
    }

    public void setOperacaoRestituicaoDivAtiva(OperacaoReceita operacaoRestituicaoDivAtiva) {
        this.operacaoRestituicaoDivAtiva = operacaoRestituicaoDivAtiva;
    }

    public OperacaoReceita getOperacaoDescConcedidoExercicio() {
        return operacaoDescConcedidoExercicio;
    }

    public void setOperacaoDescConcedidoExercicio(OperacaoReceita operacaoDescConcedidoExercicio) {
        this.operacaoDescConcedidoExercicio = operacaoDescConcedidoExercicio;
    }

    public OperacaoReceita getOperacaoDescConcedidoDivAtiva() {
        return operacaoDescConcedidoDivAtiva;
    }

    public void setOperacaoDescConcedidoDivAtiva(OperacaoReceita operacaoDescConcedidoDivAtiva) {
        this.operacaoDescConcedidoDivAtiva = operacaoDescConcedidoDivAtiva;
    }

    public OperacaoReceita getOperacaoOutraDeducaoExercicio() {
        return operacaoOutraDeducaoExercicio;
    }

    public void setOperacaoOutraDeducaoExercicio(OperacaoReceita operacaoOutraDeducaoExercicio) {
        this.operacaoOutraDeducaoExercicio = operacaoOutraDeducaoExercicio;
    }

    public OperacaoReceita getOperacaoOutraDeducaoDivAtiva() {
        return operacaoOutraDeducaoDivAtiva;
    }

    public void setOperacaoOutraDeducaoDivAtiva(OperacaoReceita operacaoOutraDeducaoDivAtiva) {
        this.operacaoOutraDeducaoDivAtiva = operacaoOutraDeducaoDivAtiva;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.novas.ContaReceitaTributo[ id=" + id + " ]";
    }

    @Override
    public int compareTo(ContaTributoReceita o) {
        int i = o.getEnquadramento().getExercicioVigente().getAno().compareTo(this.getEnquadramento().getExercicioVigente().getAno());
        if (i == 0) {
            i = o.getInicioVigencia().compareTo(this.getInicioVigencia());
        }
        return i;
    }

    public static enum TipoContaReceita {
        EXERCICIO,
        RENUNCIA,
        RESTITUICAO,
        DESCONTO,
        DEDUCAO;
    }

    @Override
    public ContaTributoReceita clone() throws CloneNotSupportedException {
        ContaTributoReceita contaTributoReceita = (ContaTributoReceita) super.clone();
        contaTributoReceita.setId(null);
        return contaTributoReceita;
    }
}
