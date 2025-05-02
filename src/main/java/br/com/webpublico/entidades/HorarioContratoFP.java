/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.geradores.CorEntidade;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@GrupoDiagrama(nome = "RecursosHumanos")
@Entity

@Audited
@CorEntidade(value = "#FFFF00")
@Etiqueta("Horário Contrato FP")
public class HorarioContratoFP extends SuperEntidade implements Serializable, ValidadorEntidade, ValidadorVigencia, Comparable, PossuidorHistorico {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Etiqueta("Início da Vigência")
    @Temporal(TemporalType.DATE)
    @Obrigatorio
    private Date inicioVigencia;
    @Tabelavel
    @Etiqueta("Final da Vigência")
    @Temporal(TemporalType.DATE)
    private Date finalVigencia;
    @Tabelavel
    @Etiqueta("Horário de Trabalho")
    @ManyToOne
    @Obrigatorio
    private HorarioDeTrabalho horarioDeTrabalho;
    @OneToMany(mappedBy = "horarioContratoFP")
    private List<LotacaoFuncional> localTrabalho;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @NotAudited
    private HorarioContratoFPHist horarioContratoFPHist;
    @Transient
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataRegistro;

    public HorarioContratoFP() {
        localTrabalho = new ArrayList<LotacaoFuncional>();
        dataRegistro = new Date();
        horarioDeTrabalho = new HorarioDeTrabalho();
    }

    public HorarioContratoFP(Date inicioVigencia, Date finalVigencia, HorarioDeTrabalho horarioDeTrabalho, ContratoFP contratoFP) {
        this.inicioVigencia = inicioVigencia;
        this.finalVigencia = finalVigencia;
        this.horarioDeTrabalho = horarioDeTrabalho;
    }

    public Date getFinalVigencia() {
        return finalVigencia;
    }

    @Override
    public void criarOrAtualizarAndAssociarHistorico(PossuidorHistorico original) {
        HorarioContratoFPHist historico = temHistorico() ? this.horarioContratoFPHist : new HorarioContratoFPHist();
        historico.setDataCadastro(new Date());
        historico.setInicioVigencia(original.getInicioVigencia());
        if (atualTemFinalVigenciaAndFinalVigenciaAtualDiferenteFinalVigenciaOriginalOrHistoricoNaoTemId(original, historico)
            || atualNaoTemFinalVigenciaAndOriginalTemFinalVigencia(original)) {
            historico.setFinalVigencia(original.getFinalVigencia());
        }
        this.horarioContratoFPHist = historico;
    }

    @Override
    public void voltarHistorico() {
        if (temHistorico()) {
            setFinalVigencia(horarioContratoFPHist.getFinalVigencia());
        } else {
            setFinalVigencia(null);
        }
    }

    public boolean atualNaoTemFinalVigenciaAndOriginalTemFinalVigencia(PossuidorHistorico original) {
        return !this.temFinalVigencia() && original.temFinalVigencia();
    }

    public boolean atualTemFinalVigenciaAndFinalVigenciaAtualDiferenteFinalVigenciaOriginalOrHistoricoNaoTemId(PossuidorHistorico original, HorarioContratoFPHist historico) {
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

    public HorarioDeTrabalho getHorarioDeTrabalho() {
        return horarioDeTrabalho;
    }

    public void setHorarioDeTrabalho(HorarioDeTrabalho horarioDeTrabalho) {
        this.horarioDeTrabalho = horarioDeTrabalho;
    }

    public List<LotacaoFuncional> getLocalTrabalho() {
        return localTrabalho;
    }

    public void setLocalTrabalho(List<LotacaoFuncional> localTrabalho) {
        this.localTrabalho = localTrabalho;
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

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    @Override
    public Date getFimVigencia() {
        return this.finalVigencia;
    }

    @Override
    public void setFimVigencia(Date data) {
        this.finalVigencia = data;
    }

    public HorarioContratoFPHist getHorarioContratoFPHist() {
        return horarioContratoFPHist;
    }

    public void setHorarioContratoFPHist(HorarioContratoFPHist horarioContratoFPHist) {
        this.horarioContratoFPHist = horarioContratoFPHist;
    }

    @Override
    public String toString() {
        return "Horário de Trab.:" + horarioDeTrabalho;
    }

    public String getDataFormatada(Date data) {
        if (data != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            return sdf.format(data);
        } else {
            return "";
        }
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

    public String getUnidadeOrganizacionalDoHorarioContratoFP() {
        for (LotacaoFuncional lf : localTrabalho) {
            if (lf.getInicioVigencia().equals(this.getInicioVigencia())) {
                return lf.getUnidadeOrganizacional().getDescricao();
            }
        }

        return "";
    }

    @Override
    public int compareTo(Object o) {
        return this.getInicioVigencia().compareTo(((HorarioContratoFP) o).getInicioVigencia());
    }

    public boolean temHistorico() {
        return horarioContratoFPHist != null;
    }

    public boolean temId() {
        return id != null;
    }
}
