package br.com.webpublico.entidades;

import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.interfaces.ValidadorVigencia;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Audited
public class ContratoFPCargo extends SuperEntidade implements Serializable, Comparable<ContratoFPCargo>, ValidadorVigencia {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ContratoFP contratoFP;
    @ManyToOne
    @Etiqueta("Cargo")
    @Obrigatorio
    private Cargo cargo;
    @ManyToOne
    @Etiqueta("CBO")
    private CBO cbo;
    @Etiqueta("Base Período Aquisitivo")
    @ManyToOne
    private BasePeriodoAquisitivo basePeriodoAquisitivo;
    @Etiqueta("Inicio de Vigência")
    @Temporal(TemporalType.DATE)
    @Obrigatorio
    private Date inicioVigencia;
    @Etiqueta("Fim de Vigência")
    @Temporal(TemporalType.DATE)
    private Date fimVigencia;
    @Transient
    private Operacoes operacao;

    public ContratoFPCargo() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CBO getCbo() {
        return cbo;
    }

    public void setCbo(CBO cbo) {
        this.cbo = cbo;
    }

    public ContratoFP getContratoFP() {
        return contratoFP;
    }

    public void setContratoFP(ContratoFP contratoFP) {
        this.contratoFP = contratoFP;
    }

    public Cargo getCargo() {
        return cargo;
    }

    public void setCargo(Cargo cargo) {
        this.cargo = cargo;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public Date getFimVigencia() {
        return fimVigencia;
    }

    public void setFimVigencia(Date fimVigencia) {
        this.fimVigencia = fimVigencia;
    }

    public Operacoes getOperacao() {
        return operacao;
    }

    public void setOperacao(Operacoes operacao) {
        this.operacao = operacao;
    }

    public BasePeriodoAquisitivo getBasePeriodoAquisitivo() {
        return basePeriodoAquisitivo;
    }

    public void setBasePeriodoAquisitivo(BasePeriodoAquisitivo basePeriodoAquisitivo) {
        this.basePeriodoAquisitivo = basePeriodoAquisitivo;
    }

    @Override
    public int compareTo(ContratoFPCargo o) {
        return o.getInicioVigencia().compareTo(this.inicioVigencia);
    }

    public boolean isFimVigenciaNull() {
        return this.getFimVigencia() == null;
    }

    public boolean isNovo() {
        return Operacoes.NOVO.equals(operacao);
    }

    public boolean isEditando() {
        return Operacoes.EDITAR.equals(operacao);
    }

    public boolean isOrigemAlteracaoCargo(AlteracaoCargo alteracaoCargo) {
        try {
            if (this.equals(alteracaoCargo.getContratoFPCargo())) {
                return true;
            }
            return false;
        } catch (NullPointerException npe) {
            return false;
        }
    }

    public boolean isVigente() {
        return getFimVigencia() == null || getFimVigencia().after(new Date());
    }

    @Override
    public String toString() {
        String retorno = DataUtil.getDataFormatada(inicioVigencia) + " - ";
        if (fimVigencia != null) {
            retorno += DataUtil.getDataFormatada(fimVigencia) + " - ";
        } else {
            retorno += " (Atual Vigente) - ";
        }
        retorno += cargo;
        return retorno;
    }
}
