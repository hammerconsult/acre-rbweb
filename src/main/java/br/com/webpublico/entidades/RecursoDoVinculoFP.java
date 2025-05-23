/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.CorEntidade;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.PossuidorHistorico;
import br.com.webpublico.interfaces.ValidadorVigencia;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.UtilRH;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.joda.time.DateTime;
import org.joda.time.Interval;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@GrupoDiagrama(nome = "RecursosHumanos")
@Entity
@Audited
@CorEntidade(value = "#00FFFF")

@Etiqueta("Recurso do Vínculo Folha de Pagamento")
public class RecursoDoVinculoFP extends SuperEntidade implements Serializable, PossuidorHistorico, ValidadorVigencia {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Etiqueta("Contrato")
    @ManyToOne
    @Obrigatorio
    private VinculoFP vinculoFP;
    @Tabelavel
    @Etiqueta("Recurso da Folha de Pagamento")
    @ManyToOne
    @Obrigatorio
    @Pesquisavel
    private RecursoFP recursoFP;
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Início da Vigência")
    @Temporal(TemporalType.DATE)
    private Date inicioVigencia;
    @Tabelavel
    @Pesquisavel
    @Temporal(TemporalType.DATE)
    @Etiqueta("Final da Vigência")
    private Date finalVigencia;
    @Etiqueta("Data de Registro")
    @Pesquisavel
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataRegistro;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @NotAudited
    private RecursoDoVinculoFPHist recursoDoVinculoFPHist;

    public RecursoDoVinculoFP() {
        dataRegistro = new Date();
    }

    public RecursoDoVinculoFP(Date inicioVigencia, Date finalVigencia, VinculoFP vinculoFP, RecursoFP recursoFP, Date dataRegistro) {
        this.inicioVigencia = inicioVigencia;
        this.finalVigencia = finalVigencia;
        this.vinculoFP = vinculoFP;
        this.recursoFP = recursoFP;
        this.dataRegistro = dataRegistro;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public Date getFinalVigencia() {
        return finalVigencia;
    }

    @Override
    public void criarOrAtualizarAndAssociarHistorico(PossuidorHistorico original) {
        RecursoDoVinculoFPHist historico = temHistorico() ? this.recursoDoVinculoFPHist : new RecursoDoVinculoFPHist();
        historico.setDataCadastro(new Date());
        historico.setInicioVigencia(original.getInicioVigencia());
        if (atualTemFinalVigenciaAndFinalVigenciaAtualDiferenteFinalVigenciaOriginalOrHistoricoNaoTemId(original, historico)
            || atualNaoTemFinalVigenciaAndOriginalTemFinalVigencia(original)) {
            historico.setFinalVigencia(original.getFinalVigencia());
        }
        this.recursoDoVinculoFPHist = historico;
    }

    @Override
    public void voltarHistorico() {
        if (temHistorico()) {
            setFinalVigencia(recursoDoVinculoFPHist.getFinalVigencia());
        } else {
            setFinalVigencia(null);
        }
    }

    public boolean atualNaoTemFinalVigenciaAndOriginalTemFinalVigencia(PossuidorHistorico original) {
        return !this.temFinalVigencia() && original.temFinalVigencia();
    }

    public boolean atualTemFinalVigenciaAndFinalVigenciaAtualDiferenteFinalVigenciaOriginalOrHistoricoNaoTemId(PossuidorHistorico original, RecursoDoVinculoFPHist historico) {
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
        return finalVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    @Override
    public void setFimVigencia(Date data) {
        this.finalVigencia = data;
    }

    public RecursoFP getRecursoFP() {
        return recursoFP;
    }

    public void setRecursoFP(RecursoFP recursoFP) {
        this.recursoFP = recursoFP;
    }

    public VinculoFP getVinculoFP() {
        return vinculoFP;
    }

    public void setVinculoFP(VinculoFP vinculoFP) {
        this.vinculoFP = vinculoFP;
    }

    public RecursoDoVinculoFPHist getRecursoDoVinculoFPHist() {
        return recursoDoVinculoFPHist;
    }

    public void setRecursoDoVinculoFPHist(RecursoDoVinculoFPHist recursoDoVinculoFPHist) {
        this.recursoDoVinculoFPHist = recursoDoVinculoFPHist;
    }

    @Override
    public String toString() {
        return inicioVigencia + " - " + finalVigencia + " - " + vinculoFP + " - " + recursoFP;
    }

    public String getDescricao() {
        String dataIni = inicioVigencia != null ? Util.dateToString(inicioVigencia) : " ";
        String dataEnd = finalVigencia != null ? Util.dateToString(finalVigencia) : " ";
        String recurso = recursoFP != null ? recursoFP + "" : "Não Disponível";
        return dataIni + " - " + dataEnd + " - " + recurso;
    }

    public boolean temHistorico() {
        return recursoDoVinculoFPHist != null;
    }

    public boolean isVigente() {
        if (finalVigencia == null) return true;
        if (finalVigencia != null) {
            if (new Interval(new DateTime(inicioVigencia), new DateTime(finalVigencia)).contains(new DateTime(UtilRH.getDataOperacao()))) {
                return true;
            }
        }
        return false;
    }

    public boolean temId() {
        return id != null;
    }
}
