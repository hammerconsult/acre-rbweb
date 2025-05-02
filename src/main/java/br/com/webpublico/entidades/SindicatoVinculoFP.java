/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.PossuidorHistorico;
import br.com.webpublico.interfaces.ValidadorEntidade;
import br.com.webpublico.interfaces.ValidadorVigencia;
import br.com.webpublico.util.UtilRH;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@GrupoDiagrama(nome = "RecursosHumanos")
@Entity

@Audited
//@Inheritance(strategy= InheritanceType.JOINED)
public class SindicatoVinculoFP extends SuperEntidade implements Serializable, ValidadorEntidade, ValidadorVigencia, Comparable, PossuidorHistorico {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Início da Vígencia")
    @Temporal(TemporalType.DATE)
    @Obrigatorio
    private Date inicioVigencia;
    @Etiqueta("Final da Vígencia")
    @Temporal(TemporalType.DATE)
    private Date finalVigencia;
    @Tabelavel
    @ManyToOne
    @Etiqueta("Vínculo Folha de Pagamento")
    private VinculoFP vinculoFP;
    @Tabelavel
    @Obrigatorio
    @ManyToOne
    private Sindicato sindicato;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @NotAudited
    private SindicatoVinculoFPHist sindicatoVinculoFPHist;

    public SindicatoVinculoFP() {
    }

    public SindicatoVinculoFP(Date inicioVigencia, Date finalVigencia, VinculoFP vinculoFP, Sindicato sindicato) {
        this.inicioVigencia = inicioVigencia;
        this.finalVigencia = finalVigencia;
        this.vinculoFP = vinculoFP;
        this.sindicato = sindicato;
    }

    public Date getFinalVigencia() {
        return finalVigencia;
    }

    @Override
    public void criarOrAtualizarAndAssociarHistorico(PossuidorHistorico original) {
        SindicatoVinculoFPHist historico = temHistorico() ? this.sindicatoVinculoFPHist : new SindicatoVinculoFPHist();
        historico.setDataCadastro(new Date());
        historico.setInicioVigencia(original.getInicioVigencia());
        if (atualTemFinalVigenciaAndFinalVigenciaAtualDiferenteFinalVigenciaOriginalOrHistoricoNaoTemId(original, historico)
            || atualNaoTemFinalVigenciaAndOriginalTemFinalVigencia(original)) {
            historico.setFinalVigencia(original.getFinalVigencia());
        }
        this.sindicatoVinculoFPHist = historico;
    }

    @Override
    public void voltarHistorico() {
        if (temHistorico()) {
            setFinalVigencia(sindicatoVinculoFPHist.getFinalVigencia());
        } else {
            setFinalVigencia(null);
        }
    }

    public boolean atualNaoTemFinalVigenciaAndOriginalTemFinalVigencia(PossuidorHistorico original) {
        return !this.temFinalVigencia() && original.temFinalVigencia();
    }

    public boolean atualTemFinalVigenciaAndFinalVigenciaAtualDiferenteFinalVigenciaOriginalOrHistoricoNaoTemId(PossuidorHistorico original, SindicatoVinculoFPHist historico) {
        return this.temFinalVigencia() && finalVigenciaAtualDiferenteFinalVigenciaOriginal(original) || !historico.temId();
    }

    public boolean finalVigenciaAtualDiferenteFinalVigenciaOriginal(PossuidorHistorico original) {
        return !this.getFinalVigencia().equals(original.getFinalVigencia());
    }

    @Override
    public boolean temFinalVigencia() {
        return finalVigencia != null;
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

    public Sindicato getSindicato() {
        return sindicato;
    }

    public void setSindicato(Sindicato sindicato) {
        this.sindicato = sindicato;
    }

    public VinculoFP getVinculoFP() {
        return vinculoFP;
    }

    public void setVinculoFP(VinculoFP vinculoFP) {
        this.vinculoFP = vinculoFP;
    }

    public SindicatoVinculoFPHist getSindicatoVinculoFPHist() {
        return sindicatoVinculoFPHist;
    }

    public void setSindicatoVinculoFPHist(SindicatoVinculoFPHist sindicatoVinculoFPHist) {
        this.sindicatoVinculoFPHist = sindicatoVinculoFPHist;
    }

    @Override
    public String toString() {
        return "Vinculo: " + vinculoFP + " Sindicato: " + sindicato + " Inicio: " + inicioVigencia + " Final: " + finalVigencia;
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
        return this.getInicioVigencia().compareTo(((SindicatoVinculoFP) o).getInicioVigencia());
    }

    @Override
    public boolean temHistorico() {
        return sindicatoVinculoFPHist != null;
    }
}
