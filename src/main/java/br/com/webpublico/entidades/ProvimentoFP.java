/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.ValidadorEntidade;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import org.hibernate.envers.Audited;

/**
 * @author everton
 */
@Entity

@GrupoDiagrama(nome = "RecursosHumanos")
@Audited
@Etiqueta("Provimento")
public class ProvimentoFP implements Serializable, ValidadorEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Servidor")
    private VinculoFP vinculoFP;
    @ManyToOne
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Tipo de Provimento")
    private TipoProvimento tipoProvimento;
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Temporal(javax.persistence.TemporalType.DATE)
    @Etiqueta("Data do Provimento")
    private Date dataProvimento;
    @ManyToOne
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Ato Legal")
    private AtoLegal atoLegal;
    @Etiqueta("Observação")
    private String observacao;
    @Etiqueta("Sequência")
    private Integer sequencia;
    @ManyToOne
    private Cargo cargo;
    @Transient
    private ContratoFP contratoFP;
    @Transient
    private Long criadoEm;

    public ProvimentoFP() {
        this.criadoEm = System.nanoTime();
    }

    public ProvimentoFP(VinculoFP vinculoFP, TipoProvimento tipoProvimento, Date dataProvimento, AtoLegal atoLegal, String observacao, Integer sequencia) {
        this.vinculoFP = vinculoFP;
        this.tipoProvimento = tipoProvimento;
        this.dataProvimento = dataProvimento;
        this.atoLegal = atoLegal;
        this.observacao = observacao;
        this.sequencia = sequencia;
        this.criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AtoLegal getAtoLegal() {
        return atoLegal;
    }

    public void setAtoLegal(AtoLegal atoLegal) {
        this.atoLegal = atoLegal;
    }

    public Date getDataProvimento() {
        return dataProvimento;
    }

    public void setDataProvimento(Date dataProvimento) {
        this.dataProvimento = dataProvimento;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public TipoProvimento getTipoProvimento() {
        return tipoProvimento;
    }

    public void setTipoProvimento(TipoProvimento tipoProvimento) {
        this.tipoProvimento = tipoProvimento;
    }

    public VinculoFP getVinculoFP() {
        return vinculoFP;
    }

    public void setVinculoFP(VinculoFP vinculoFP) {
        this.vinculoFP = vinculoFP;
    }

    public Integer getSequencia() {
        return sequencia;
    }

    public void setSequencia(Integer sequencia) {
        this.sequencia = sequencia;
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
        super.toString();
        StringBuilder toString = new StringBuilder("");

        if (getVinculoFP() != null) {
            toString.append(getVinculoFP().getDescricao());
        }
        if (getTipoProvimento() != null) {
            toString.append(getTipoProvimento().getDescricao());
        }
        return toString.toString();
    }

    @Override
    public void validarConfirmacao() throws ValidacaoException {
        return;
    }
}
