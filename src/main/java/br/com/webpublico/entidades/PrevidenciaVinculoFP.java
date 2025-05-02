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
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author gustavo
 */
@GrupoDiagrama(nome = "RecursosHumanos")
@Audited
@Entity

public class PrevidenciaVinculoFP extends SuperEntidade implements Serializable, ValidadorEntidade, ValidadorVigencia, Comparable, PossuidorHistorico {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Inicio da Vigência")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date inicioVigencia;
    @Tabelavel
    @Etiqueta("Final da Vigência")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date finalVigencia;
    @ManyToOne
    private ContratoFP contratoFP;
    @Pesquisavel
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Tipo de Previdência")
    private TipoPrevidenciaFP tipoPrevidenciaFP;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @NotAudited
    private PrevidenciaVinculoFPHist previdenciaVinculoFPHist;
    @ManyToOne
    @Etiqueta("Ato Legal")
    private AtoLegal atoLegal;
    @Invisivel
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "previdenciaVinculoFP", orphanRemoval = true)
    private List<PrevidenciaArquivo> previdenciaArquivos;

    public PrevidenciaVinculoFP() {
        previdenciaArquivos = new ArrayList<>();
    }

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

    public Date getFinalVigencia() {
        return finalVigencia;
    }

    @Override
    public void criarOrAtualizarAndAssociarHistorico(PossuidorHistorico original) {
        PrevidenciaVinculoFPHist historico = getHistorico();
        historico.setDataCadastro(new Date());
        historico.setInicioVigencia(original.getInicioVigencia());
        if ((this.temFinalVigencia() && !this.getFinalVigencia().equals(original.getFinalVigencia()) || historico.getId() == null)
            || (!this.temFinalVigencia() && original.temFinalVigencia())) {
            historico.setFinalVigencia(original.getFinalVigencia());
        }
        this.previdenciaVinculoFPHist = historico;
    }

    @Override
    public void voltarHistorico() {
        if (temHistorico()) {
            setFinalVigencia(previdenciaVinculoFPHist.getFinalVigencia());
        } else {
            setFinalVigencia(null);
        }
    }

    @Override
    public boolean temFinalVigencia() {
        return finalVigencia != null;
    }

    public PrevidenciaVinculoFPHist getHistorico() {
        return this.temHistorico() ? this.previdenciaVinculoFPHist : new PrevidenciaVinculoFPHist();
    }

    public void setFinalVigencia(Date finalVigencia) {
        this.finalVigencia = finalVigencia;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    @Override
    public Date getFimVigencia() {
        return this.finalVigencia;
    }

    @Override
    public void setFimVigencia(Date data) {
        this.finalVigencia = data;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public TipoPrevidenciaFP getTipoPrevidenciaFP() {
        return tipoPrevidenciaFP;
    }

    public void setTipoPrevidenciaFP(TipoPrevidenciaFP tipoPrevidenciaFP) {
        this.tipoPrevidenciaFP = tipoPrevidenciaFP;
    }

    public PrevidenciaVinculoFPHist getPrevidenciaVinculoFPHist() {
        return previdenciaVinculoFPHist;
    }

    public void setPrevidenciaVinculoFPHist(PrevidenciaVinculoFPHist previdenciaVinculoFPHist) {
        this.previdenciaVinculoFPHist = previdenciaVinculoFPHist;
    }

    public AtoLegal getAtoLegal() {
        return atoLegal;
    }

    public void setAtoLegal(AtoLegal atoLegal) {
        this.atoLegal = atoLegal;
    }

    public List<PrevidenciaArquivo> getPrevidenciaArquivos() {
        return previdenciaArquivos;
    }

    public void setPrevidenciaArquivos(List<PrevidenciaArquivo> previdenciaArquivos) {
        this.previdenciaArquivos = previdenciaArquivos;
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
    public String toString() {
        return tipoPrevidenciaFP + "";
    }

    @Override
    public int compareTo(Object o) {
        return this.getInicioVigencia().compareTo(((PrevidenciaVinculoFP) o).getInicioVigencia());
    }

    public boolean temHistorico() {
        return this.previdenciaVinculoFPHist != null;
    }
}
