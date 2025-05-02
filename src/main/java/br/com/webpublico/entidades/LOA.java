/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Lei do Orçamento Anual (LOA): gerada a partir da LDO previamente aprovada,
 * representa o orçamento efetivo para o exercício.
 *
 * @author arthur
 */
@GrupoDiagrama(nome = "PPA")
@Audited
@Entity

@Etiqueta("LOA")
public class LOA extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Tabelavel
    @Etiqueta(value = "LDO")
    private LDO ldo;
    /**
     * Lei aprovada ("a" LOA).
     */
    @ManyToOne
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Ato Legal")
    private AtoLegal atoLegal;
    /**
     * Data na qual esta versão da LOA foi gerada.
     */
    @Tabelavel
    @Etiqueta("Data de Registro")
    @Pesquisavel
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataRegistro;
    /**
     * Data na qual esta versão da LOA foi aprovada.
     */
    @Etiqueta("Aprovação")
    @Tabelavel
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date aprovacao;
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Versão")
    private String versao;
    @Monetario
    @Obrigatorio
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.DIREITA)
    @Etiqueta("Valor da Despesa(R$)")
    @Pesquisavel
    private BigDecimal valorDaDespesa;
    @Monetario
    @Obrigatorio
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.DIREITA)
    @Pesquisavel
    @Etiqueta("Valor da Receita(R$)")
    private BigDecimal valorDaReceita;
    @Invisivel
    @OneToMany(mappedBy = "loa", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReceitaLOA> receitaLOAs;
    @Column
    private Boolean efetivada;
    @Etiqueta("Data da Efetivação")
    @Temporal(TemporalType.DATE)
    private Date dataEfetivacao;
    @Tabelavel
    @Etiqueta(value = "Contabilizada")
    private Boolean contabilizado;
    @Etiqueta("Data da Contabilizaçao")
    @Temporal(TemporalType.DATE)
    private Date dataContabilizacao;

    public LOA() {
        dataRegistro = new Date();
        valorDaReceita = new BigDecimal(BigInteger.ZERO);
        valorDaDespesa = new BigDecimal(BigInteger.ZERO);
        receitaLOAs = new ArrayList<ReceitaLOA>();
        efetivada = false;
        contabilizado = false;
    }

    public LOA(LDO ldo, AtoLegal atoLegal, Date dataRegistro, Date aprovacao, String versao) {
        this.ldo = ldo;
        this.atoLegal = atoLegal;
        this.dataRegistro = dataRegistro;
        this.aprovacao = aprovacao;
        this.versao = versao;
    }

    public List<ReceitaLOA> getReceitaLOAs() {
        return receitaLOAs;
    }

    public void setReceitaLOAs(List<ReceitaLOA> receitaLOAs) {
        this.receitaLOAs = receitaLOAs;
    }

    public AtoLegal getAtoLegal() {
        return atoLegal;
    }

    public void setAtoLegal(AtoLegal atoLegal) {
        this.atoLegal = atoLegal;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LDO getLdo() {
        return ldo;
    }

    public void setLdo(LDO ldo) {
        this.ldo = ldo;
    }

    public Date getAprovacao() {
        return aprovacao;
    }

    public void setAprovacao(Date aprovacao) {
        this.aprovacao = aprovacao;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = new Date();
    }

    public String getVersao() {
        return versao;
    }

    public void setVersao(String versao) {
        this.versao = versao;
    }

    public BigDecimal getValorDaDespesa() {
        return valorDaDespesa;
    }

    public void setValorDaDespesa(BigDecimal valorDaDespesa) {
        this.valorDaDespesa = valorDaDespesa;
    }

    public BigDecimal getValorDaReceita() {
        return valorDaReceita;
    }

    public void setValorDaReceita(BigDecimal valorDaReceita) {
        this.valorDaReceita = valorDaReceita;
    }

    public Boolean getEfetivada() {
        return efetivada;
    }

    public void setEfetivada(Boolean efetivada) {
        this.efetivada = efetivada;
    }

    public Boolean getContabilizado() {
        return contabilizado;
    }

    public void setContabilizado(Boolean contabilizado) {
        this.contabilizado = contabilizado;
    }

    public Date getDataContabilizacao() {
        return dataContabilizacao;
    }

    public void setDataContabilizacao(Date dataContabilizacao) {
        this.dataContabilizacao = dataContabilizacao;
    }

    public Date getDataEfetivacao() {
        return dataEfetivacao;
    }

    public void setDataEfetivacao(Date dataEfetivacao) {
        this.dataEfetivacao = dataEfetivacao;
    }

    @Override
    public boolean equals(Object obj) {
        return IdentidadeDaEntidade.calcularEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public String toString() {
        return versao;
    }


    public String getComplementoContabil() {
        String toReturn = "";
        if (this.atoLegal != null) {
            toReturn = this.atoLegal.getNome();
        }
        if (this.dataRegistro != null) {
            toReturn = toReturn + " - " + this.dataRegistro;
        }
        return toReturn;
    }
}
