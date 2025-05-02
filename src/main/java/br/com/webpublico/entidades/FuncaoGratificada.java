/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.rh.TipoFuncaoGratificada;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.ValidadorVigencia;
import br.com.webpublico.interfaces.ValidadorVigenciaFolha;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author peixe
 */
@Entity

@Audited
@GrupoDiagrama(nome = "RecursosHumanos")
@Etiqueta("Função Gratificada")
public class FuncaoGratificada implements Serializable, ValidadorVigenciaFolha, ValidadorVigencia {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Servidor")
    @ManyToOne
    private ContratoFP contratoFP;
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Início Vigência")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date inicioVigencia;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Final Vigência")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date finalVigencia;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Data de Nomeação")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataNomeacao;
    @Tabelavel
    @Obrigatorio
    @Pesquisavel
    @Etiqueta("Descrição")
    private String descricao;
    @Obrigatorio
    private Double percentual;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Ato Legal")
    private AtoLegal atoDePessoal;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataRegistro;
    @OneToMany(mappedBy = "funcaoGratificada", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EnquadramentoFG> enquadramentoFGs;
    @ManyToOne
    private ProvimentoFP provimentoFP;
    @Invisivel
    @Transient
    private BigDecimal valor;
    @Invisivel
    @Transient
    private Long criadoEm;
    @Etiqueta("Observação")
    private String observacao;
    @Etiqueta("Tipo de Função Gratificada")
    @Tabelavel
    @Pesquisavel
    @Enumerated(EnumType.STRING)
    private TipoFuncaoGratificada tipoFuncaoGratificada;
    @Etiqueta("Contrato de Referência")
    @ManyToOne
    private ContratoFP contratoFPRef;

    public FuncaoGratificada() {
        criadoEm = System.nanoTime();
        enquadramentoFGs = new ArrayList<EnquadramentoFG>();
        dataRegistro = new Date();
    }

    public FuncaoGratificada(Date inicioVigencia, Date finalVigencia, BigDecimal valor) {
        this.inicioVigencia = inicioVigencia;
        this.finalVigencia = finalVigencia;
        this.valor = valor;
    }

    public List<EnquadramentoFG> getEnquadramentoFGs() {
        return enquadramentoFGs;
    }

    public void setEnquadramentoFGs(List<EnquadramentoFG> enquadramentoFGs) {
        this.enquadramentoFGs = enquadramentoFGs;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public AtoLegal getAtoDePessoal() {
        return atoDePessoal;
    }

    public void setAtoDePessoal(AtoLegal atoDePessoal) {
        this.atoDePessoal = atoDePessoal;
    }

    public ContratoFP getContratoFP() {
        return contratoFP;
    }

    public void setContratoFP(ContratoFP contratoFP) {
        this.contratoFP = contratoFP;
    }

    public Date getDataNomeacao() {
        return dataNomeacao;
    }

    public void setDataNomeacao(Date dataNomeacao) {
        this.dataNomeacao = dataNomeacao;
    }

    public String getDescricao() {
        return descricao;
    }

    public Double getPercentual() {
        return percentual;
    }

    public void setPercentual(Double percentual) {
        this.percentual = percentual;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Date getFinalVigencia() {
        return finalVigencia;
    }

    public void setFinalVigencia(Date finalVigencia) {
        this.finalVigencia = finalVigencia;
    }

    @Override
    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    @Override
    public Date getFimVigencia() {
        return this.getFinalVigencia();
    }

    @Override
    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    @Override
    public void setFimVigencia(Date data) {
        setFimVigencia(data);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public ContratoFP getContratoFPRef() {
        return contratoFPRef;
    }

    public void setContratoFPRef(ContratoFP contratoFPRef) {
        this.contratoFPRef = contratoFPRef;
    }

    public EnquadramentoFG getEnquadramentoCCVigente() {
        for (EnquadramentoFG ecc : enquadramentoFGs) {
            if (ecc.getFinalVigencia() == null) {
                return ecc;
            }
            if (ecc.getFinalVigencia().getTime() < new Date().getTime()) {
                return ecc;
            }
        }
        return null;
    }

    public TipoFuncaoGratificada getTipoFuncaoGratificada() {
        return tipoFuncaoGratificada;
    }

    public void setTipoFuncaoGratificada(TipoFuncaoGratificada tipoFuncaoGratificada) {
        this.tipoFuncaoGratificada = tipoFuncaoGratificada;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    public ProvimentoFP getProvimentoFP() {
        return provimentoFP;
    }

    public void setProvimentoFP(ProvimentoFP provimentoFP) {
        this.provimentoFP = provimentoFP;
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    @Override
    public String toString() {
        return descricao;
    }

    public boolean isVigente() {
        if (finalVigencia == null) {
            return true;
        }
        if (finalVigencia.getTime() > new Date().getTime()) {
            return true;
        }
        return false;
    }


}
