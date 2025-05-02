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
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.Date;

/**
 * @author peixe
 */
@Entity
@Audited
@GrupoDiagrama(nome = "RecursosHumanos")
public class SituacaoContratoFP extends SuperEntidade implements Serializable, ValidadorEntidade, ValidadorVigencia, Comparable, PossuidorHistorico {

    private static final Logger logger = LoggerFactory.getLogger(SituacaoContratoFP.class);
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ContratoFP contratoFP;
    @ManyToOne
    private SituacaoFuncional situacaoFuncional;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date inicioVigencia;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date finalVigencia;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @NotAudited
    private SituacaoContratoFPHist situacaoContratoFPHist;
    private String rotinaResponsavelAlteracao;

    public SituacaoContratoFP() {
        atribuirRotinaResponsavelAlteracao();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ContratoFP getContratoFP() {
        return contratoFP;
    }

    public void setContratoFP(ContratoFP contratoFP) {
        this.contratoFP = contratoFP;
    }

    @Override
    public Date getFinalVigencia() {
        return finalVigencia;
    }

    public void setFinalVigencia(Date finalVigencia) {
        this.finalVigencia = finalVigencia;
    }

    @Override
    public boolean temHistorico() {
        return situacaoContratoFPHist != null;
    }

    @Override
    public void criarOrAtualizarAndAssociarHistorico(PossuidorHistorico original) {
        SituacaoContratoFPHist historico = temHistorico() ? this.situacaoContratoFPHist : new SituacaoContratoFPHist();
        historico.setDataCadastro(new Date());
        historico.setInicioVigencia(original.getInicioVigencia());
        if (atualTemFinalVigenciaAndFinalVigenciaAtualDiferenteFinalVigenciaOriginalOrHistoricoNaoTemId(original, historico)
            || atualNaoTemFinalVigenciaAndOriginalTemFinalVigencia(original)) {
            historico.setFinalVigencia(original.getFinalVigencia());
        }
        this.situacaoContratoFPHist = historico;
    }

    @Override
    public void voltarHistorico() {
        if (temHistorico()) {
            setFinalVigencia(situacaoContratoFPHist.getFinalVigencia());
        } else {
            setFinalVigencia(null);
        }
    }

    public boolean atualNaoTemFinalVigenciaAndOriginalTemFinalVigencia(PossuidorHistorico original) {
        return !this.temFinalVigencia() && original.temFinalVigencia();
    }

    public boolean atualTemFinalVigenciaAndFinalVigenciaAtualDiferenteFinalVigenciaOriginalOrHistoricoNaoTemId(PossuidorHistorico original, SituacaoContratoFPHist historico) {
        return this.temFinalVigencia() && finalVigenciaAtualDiferenteFinalVigenciaOriginal(original) || !historico.temId();
    }

    public boolean finalVigenciaAtualDiferenteFinalVigenciaOriginal(PossuidorHistorico original) {
        return !this.getFinalVigencia().equals(original.getFinalVigencia());
    }

    @Override
    public boolean temFinalVigencia() {
        return finalVigencia != null;
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
        return this.finalVigencia;
    }

    @Override
    public void setFimVigencia(Date data) {
        this.finalVigencia = data;
    }

    public SituacaoFuncional getSituacaoFuncional() {
        return situacaoFuncional;
    }

    public void setSituacaoFuncional(SituacaoFuncional situacaoFuncional) {
        this.situacaoFuncional = situacaoFuncional;
    }

    public SituacaoContratoFPHist getSituacaoContratoFPHist() {
        return situacaoContratoFPHist;
    }

    public void setSituacaoContratoFPHist(SituacaoContratoFPHist situacaoContratoFPHist) {
        this.situacaoContratoFPHist = situacaoContratoFPHist;
    }

    public String getRotinaResponsavelAlteracao() {
        return rotinaResponsavelAlteracao;
    }

    public void setRotinaResponsavelAlteracao(String rotinaResponsavelAlteracao) {
        this.rotinaResponsavelAlteracao = rotinaResponsavelAlteracao;
    }

    @Override
    public String toString() {
        return situacaoFuncional + "";
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
        return this.inicioVigencia.compareTo(((SituacaoContratoFP) o).getInicioVigencia());
    }

    public boolean isEmExercicio() {
        return 1L == situacaoFuncional.getCodigo();
    }

    public boolean isEmFerias() {
        return 2L == situacaoFuncional.getCodigo();
    }

    private void atribuirRotinaResponsavelAlteracao() {
        StackTraceElement[] elementos = Thread.currentThread().getStackTrace();
        for (StackTraceElement elemento : elementos) {
            if (elemento.getClassName().startsWith("br.com.webpublico")) {
                try {
                    String nomeClasse = elemento.getClassName();
                    Class classe = Class.forName(nomeClasse);
                    if (classe != null && !classe.isAnnotationPresent(Entity.class) && (classe.getGenericSuperclass() != classe.getSuperclass())) {
                        Class classeTipoParametro = classe.getGenericSuperclass() != null ?
                            (Class<?>) ((ParameterizedType) classe.getGenericSuperclass()).getActualTypeArguments()[0] : null;
                        this.rotinaResponsavelAlteracao = (classeTipoParametro != null ? classeTipoParametro.getSimpleName() : "");
                    } else {
                        this.rotinaResponsavelAlteracao = nomeClasse;
                    }
                } catch (Exception e) {
                    logger.error("Erro ao recuperar nome da classe: ", e);
                }
            }
        }
    }
}
