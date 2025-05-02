/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.ValidadorEntidade;
import br.com.webpublico.interfaces.ValidadorVigencia;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@GrupoDiagrama(nome = "RecursosHumanos")
@Entity

@Audited
public class AssociacaoVinculoFP extends SuperEntidade implements Serializable, ValidadorEntidade, ValidadorVigencia, Comparable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Início da Vígencia")
    @Obrigatorio
    @Temporal(TemporalType.DATE)
    private Date inicioVigencia;
    @Etiqueta("Final da Vígencia")
    @Temporal(TemporalType.DATE)
    private Date finalVigencia;
    @Tabelavel
    @ManyToOne
    @Etiqueta("Vínculo Folha de Pagamento")
    private VinculoFP vinculoFP;
    @Etiqueta("Associação")
    @Tabelavel
    @Obrigatorio
    @ManyToOne
    private Associacao associacao;

    @Etiqueta("Evento FP")
    @Obrigatorio
    @ManyToOne
    private EventoFP eventoFP;

    @Transient
    private Operacoes operacao;

    public AssociacaoVinculoFP() {
    }

    public AssociacaoVinculoFP(VinculoFP vinculoFP) {
        setVinculoFP(vinculoFP);
    }

    public AssociacaoVinculoFP(Date inicioVigencia, Date finalVigencia, VinculoFP vinculoFP, Associacao associacao) {
        this.inicioVigencia = inicioVigencia;
        this.finalVigencia = finalVigencia;
        this.vinculoFP = vinculoFP;
        this.associacao = associacao;
    }

    public Date getFinalVigencia() {
        return finalVigencia;
    }

    public void setFinalVigencia(Date finalVigencia) {
        this.finalVigencia = finalVigencia;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    @Override
    public Date getFimVigencia() {
        return this.finalVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    @Override
    public void setFimVigencia(Date data) {
        this.finalVigencia = data;
    }

    public Associacao getAssociacao() {
        return associacao;
    }

    public void setAssociacao(Associacao associacao) {
        this.associacao = associacao;
    }

    public VinculoFP getVinculoFP() {
        return vinculoFP;
    }

    public void setVinculoFP(VinculoFP vinculoFP) {
        this.vinculoFP = vinculoFP;
    }

    public EventoFP getEventoFP() {
        return eventoFP;
    }

    public void setEventoFP(EventoFP eventoFP) {
        this.eventoFP = eventoFP;
    }

    public Operacoes getOperacao() {
        return operacao;
    }

    public void setOperacao(Operacoes operacao) {
        this.operacao = operacao;
    }

    @Override
    public String toString() {
        return "Vinculo: " + vinculoFP + " Associacao: " + associacao + " Inicio: " + inicioVigencia + " Final: " + finalVigencia;
    }

    @Override
    public void validarConfirmacao() throws ValidacaoException {
        ValidacaoException ve = new ValidacaoException();
        if (getFinalVigencia() != null && getFinalVigencia().compareTo(getInicioVigencia()) <= 0) {
            ve.adicionarMensagemError(SummaryMessages.OPERACAO_NAO_PERMITIDA, "A data de final de vigência deve ser posterior a data de início de vigência.");
        }
        if (ve.temMensagens()) {
            throw ve;
        }
    }

    @Override
    public int compareTo(Object o) {
        return this.getInicioVigencia().compareTo(((AssociacaoVinculoFP) o).getInicioVigencia());
    }

    public Boolean estaEditando() {
        return Operacoes.EDITAR.equals(operacao);
    }
}
