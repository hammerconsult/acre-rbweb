/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.PossuidorHistorico;
import br.com.webpublico.interfaces.ValidadorVigencia;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.UtilRH;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.joda.time.DateTime;
import org.joda.time.Interval;

import javax.persistence.*;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

@GrupoDiagrama(nome = "RecursosHumanos")
@Entity
@Audited
@Inheritance(strategy = InheritanceType.JOINED)
@Etiqueta("Lotação Funcional")
public class LotacaoFuncional extends SuperEntidade implements Serializable, ValidadorVigencia, Comparable, PossuidorHistorico {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Unidade Organizacional")
    @ManyToOne
    private UnidadeOrganizacional unidadeOrganizacional;
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Horário Contrato FP")
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private HorarioContratoFP horarioContratoFP;
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Início da Vigência")
    @Temporal(TemporalType.DATE)
    private Date inicioVigencia;
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Final da Vigência")
    @Temporal(TemporalType.DATE)
    private Date finalVigencia;
    @ManyToOne
    private VinculoFP vinculoFP;
    @ManyToOne
    private RetornoCedenciaContratoFP retornoCedenciaContratoFP;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataRegistro;
    @Etiqueta("Provimento")
    @ManyToOne
    private ProvimentoFP provimentoFP;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @NotAudited
    private LotacaoFuncionalHist lotacaoFuncionalHist;
    @Transient
    private Operacoes operacao;

    public LotacaoFuncional() {
        this.dataRegistro = new Date();
    }

    public LotacaoFuncional(UnidadeOrganizacional unidadeOrganizacional, HorarioContratoFP horarioContratoFP, Date inicioVigencia, Date finalVigencia) {
        this.unidadeOrganizacional = unidadeOrganizacional;
        this.horarioContratoFP = horarioContratoFP;
        this.inicioVigencia = inicioVigencia;
        this.finalVigencia = finalVigencia;
    }

    public Date getFinalVigencia() {
        return finalVigencia;
    }

    @Override
    public void criarOrAtualizarAndAssociarHistorico(PossuidorHistorico original) {
        LotacaoFuncionalHist historico = temHistorico() ? this.lotacaoFuncionalHist : new LotacaoFuncionalHist();
        historico.setDataCadastro(new Date());
        historico.setInicioVigencia(original.getInicioVigencia());
        if (atualTemFinalVigenciaAndFinalVigenciaAtualDiferenteFinalVigenciaOriginalOrHistoricoNaoTemId(original, historico)
            || atualNaoTemFinalVigenciaAndOriginalTemFinalVigencia(original)) {
            historico.setFinalVigencia(original.getFinalVigencia());
        }
        this.lotacaoFuncionalHist = historico;
    }

    @Override
    public void voltarHistorico() {
        if (temHistorico()) {
            setFinalVigencia(lotacaoFuncionalHist.getFinalVigencia());
        } else {
            setFinalVigencia(null);
        }
        if (horarioContratoFP != null) {
            horarioContratoFP.voltarHistorico();
        }
    }

    public boolean atualNaoTemFinalVigenciaAndOriginalTemFinalVigencia(PossuidorHistorico original) {
        return !this.temFinalVigencia() && original.temFinalVigencia();
    }

    public boolean atualTemFinalVigenciaAndFinalVigenciaAtualDiferenteFinalVigenciaOriginalOrHistoricoNaoTemId(PossuidorHistorico original, LotacaoFuncionalHist historico) {
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

    public HorarioContratoFP getHorarioContratoFP() {
        return horarioContratoFP;
    }

    public void setHorarioContratoFP(HorarioContratoFP horarioContratoFP) {
        this.horarioContratoFP = horarioContratoFP;
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
        return this.getFinalVigencia();
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    @Override
    public void setFimVigencia(Date data) {
        this.setFinalVigencia(data);
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public VinculoFP getVinculoFP() {
        return vinculoFP;
    }

    public void setVinculoFP(VinculoFP vinculoFP) {
        this.vinculoFP = vinculoFP;
    }

    public RetornoCedenciaContratoFP getRetornoCedenciaContratoFP() {
        return retornoCedenciaContratoFP;
    }

    public void setRetornoCedenciaContratoFP(RetornoCedenciaContratoFP retornoCedenciaContratoFP) {
        this.retornoCedenciaContratoFP = retornoCedenciaContratoFP;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public ProvimentoFP getProvimentoFP() {
        return provimentoFP;
    }

    public void setProvimentoFP(ProvimentoFP provimentoFP) {
        this.provimentoFP = provimentoFP;
    }

    public LotacaoFuncionalHist getLotacaoFuncionalHist() {
        return lotacaoFuncionalHist;
    }

    public void setLotacaoFuncionalHist(LotacaoFuncionalHist lotacaoFuncionalHist) {
        this.lotacaoFuncionalHist = lotacaoFuncionalHist;
    }

    public Operacoes getOperacao() {
        return operacao;
    }

    public void setOperacao(Operacoes operacao) {
        this.operacao = operacao;
    }

    @Override
    public String toString() {
        String retorno = DataUtil.getDataFormatada(inicioVigencia) + " - ";
        if (finalVigencia != null) {
            retorno += DataUtil.getDataFormatada(finalVigencia) + " - ";
        } else {
            retorno += " (Atual Vigente) - ";
        }
        retorno += unidadeOrganizacional;
        return retorno;
    }

    public String getFormataData(Date datas) {
        if (datas != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            return sdf.format(datas);
        } else {
            return "";
        }
    }

    public boolean isFinalVigenciaNull() {
        return this.getFinalVigencia() == null;
    }

    public boolean isVigente(Date dataVerificacao) {
        return getFinalVigencia() == null || new Interval(new DateTime(getInicioVigencia()), new DateTime(getFinalVigencia())).contains(new DateTime(dataVerificacao));
    }

    public boolean isOrigemAlteracaoCargo(AlteracaoCargo alteracaoCargo) {
        try {
            if (this.equals(alteracaoCargo.getLotacaoFuncional())) {
                return true;
            }
            return false;
        } catch (NullPointerException npe) {
            return false;
        }
    }

    public boolean isEditando() {
        return Operacoes.EDITAR.equals(operacao);
    }

    public boolean isNovo() {
        return Operacoes.NOVO.equals(operacao);
    }

    @Override
    public int compareTo(Object o) {
        return this.inicioVigencia.compareTo(((LotacaoFuncional) o).getInicioVigencia());
    }

    public boolean temHistorico() {
        return lotacaoFuncionalHist != null;
    }

    public boolean temHorarioContratoFP(HorarioContratoFP horarioContrato) {
        return horarioContratoFP.equals(horarioContrato);
    }

    public boolean temProvimentoFPTipoTransferencia() {
        if (provimentoFP != null) {
            if (Integer.valueOf(TipoProvimento.TRANSFERENCIA).equals(provimentoFP.getTipoProvimento().getCodigo())) {
                return true;
            }
        }
        return false;
    }
}
