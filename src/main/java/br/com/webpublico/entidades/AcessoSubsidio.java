/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
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
@Etiqueta("Acesso a Subsídio")
public class AcessoSubsidio implements Serializable, ValidadorVigenciaFolha {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @ManyToOne
    @Etiqueta("Contrato FP")
    @Obrigatorio
    private ContratoFP contratoFP;
    @Temporal(TemporalType.DATE)
    @Tabelavel
    @Etiqueta("Início Vigência")
    @Obrigatorio
    @Pesquisavel
    private Date inicioVigencia;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Final Vigência")
    @Temporal(TemporalType.DATE)
    private Date finalVigencia;
    @Tabelavel
    @Etiqueta("Data da Nomeação")
    @Obrigatorio
    @Pesquisavel
    @Temporal(TemporalType.DATE)
    private Date dataNomeacao;
    @Etiqueta("Descrição")
    @Obrigatorio
    private String descricao;
    @Tabelavel
    @Etiqueta("Opção Salarial")
    @Pesquisavel
    @Obrigatorio
    @ManyToOne
    private OpcaoSalarialCC opcaoSalarialCC;
    @Tabelavel
    @Obrigatorio
    private Double percentual;
    @Etiqueta("Ato Legal")
    @Obrigatorio
    @ManyToOne
    private AtoLegal atoDePessoal;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataRegistro;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "acessoSubsidio", orphanRemoval = true)
    private List<EnquadramentoCC> enquadramentoCCs;
    @Etiqueta("Provimento")
    @ManyToOne
    private ProvimentoFP provimentoFP;
    @Etiqueta("Ato Legal de Retorno")
    @Invisivel
    @ManyToOne
    private AtoLegal atoLegal;
    @Etiqueta("Ato Legal de Exoneração")
    @Invisivel
    @ManyToOne
    private AtoLegal atoLegalExoneracao;
    @Invisivel
    @Etiqueta("Provimento de Retorno")
    @ManyToOne
    private ProvimentoFP provimentoRetorno;
    @ManyToOne
    private Cargo cargo;
    @ManyToOne
    private BaseFP baseFP;
    @Invisivel
    @Transient
    private BigDecimal valor;
    @Invisivel
    @Transient
    private Long criadoEm;
    @Etiqueta("Observação")
    private String observacaoRCC;
    @Etiqueta("Observação")
    private String observacao;


    public String getObservacaoRCC() {
        return observacaoRCC;
    }

    public void setObservacaoRCC(String observacaoRCC) {
        this.observacaoRCC = observacaoRCC;
    }

    public AcessoSubsidio() {
        criadoEm = System.nanoTime();
        dataRegistro = new Date();
        enquadramentoCCs = new ArrayList<EnquadramentoCC>();
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public Double getPercentual() {
        return percentual;
    }

    public void setPercentual(Double percentual) {
        this.percentual = percentual;
    }

    public AtoLegal getAtoLegal() {
        return atoLegal;
    }

    public void setAtoLegal(AtoLegal atoLegal) {
        this.atoLegal = atoLegal;
    }

    public AtoLegal getAtoLegalExoneracao() {
        return atoLegalExoneracao;
    }

    public void setAtoLegalExoneracao(AtoLegal atoLegalExoneracao) {
        this.atoLegalExoneracao = atoLegalExoneracao;
    }

    public ProvimentoFP getProvimentoRetorno() {
        return provimentoRetorno;
    }

    public void setProvimentoRetorno(ProvimentoFP provimentoRetorno) {
        this.provimentoRetorno = provimentoRetorno;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public List<EnquadramentoCC> getEnquadramentoCCs() {
        return enquadramentoCCs;
    }

    public void setEnquadramentoCCs(List<EnquadramentoCC> enquadramentoCCs) {
        this.enquadramentoCCs = enquadramentoCCs;
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

    public BaseFP getBaseFP() {
        return baseFP;
    }

    public void setBaseFP(BaseFP baseFP) {
        this.baseFP = baseFP;
    }

    @Override
    public Date getFimVigencia() {
        return getFinalVigencia();
    }

    @Override
    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    @Override
    public void setFimVigencia(Date data) {
        setFinalVigencia(data);
    }

    public OpcaoSalarialCC getOpcaoSalarialCC() {
        return opcaoSalarialCC;
    }

    public void setOpcaoSalarialCC(OpcaoSalarialCC opcaoSalarialCC) {
        this.opcaoSalarialCC = opcaoSalarialCC;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ProvimentoFP getProvimentoFP() {
        return provimentoFP;
    }

    public void setProvimentoFP(ProvimentoFP provimentoFP) {
        this.provimentoFP = provimentoFP;
    }

    public Cargo getCargo() {
        return cargo;
    }

    public void setCargo(Cargo cargo) {
        this.cargo = cargo;
    }

    public EnquadramentoCC getEnquadramentoCCVigente() {
        for (EnquadramentoCC ecc : enquadramentoCCs) {
            if (ecc.getFinalVigencia() == null) {
                return ecc;
            }
            if (ecc.getFinalVigencia().getTime() < new Date().getTime()) {
                return ecc;
            }
        }
        return null;
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
        return descricao != null ? descricao : " ";
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

    public boolean temProvimentoRetorno() {
        return provimentoRetorno != null;
    }
}
