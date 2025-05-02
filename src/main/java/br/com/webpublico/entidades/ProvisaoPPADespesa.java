/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoContaDespesa;
import br.com.webpublico.enums.TipoDespesaORC;
import br.com.webpublico.geradores.GrupoDiagrama;
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
 * Detalha o valor da AcaoPPA por conta de despesa, de modo a determinar como os
 * valores serão gastos.
 *
 * @author arthur
 */
@GrupoDiagrama(nome = "PPA")
@Audited
@Entity

@Etiqueta("Fixação da Despesa")
public class ProvisaoPPADespesa implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    /**
     * Código sequencial gerado por exercício contábil. Será utilizado nos
     * Lançamentos Contábeis para localizar a ProvisaoPPADespesa que classifica
     * a despesa que está sendo realizada.
     */
    @Pesquisavel
    @Obrigatorio
    @Tabelavel
    @Etiqueta(value = "Código")
    private String codigo;
    @Etiqueta("Sub Ação PPA")
    @ManyToOne
    @Obrigatorio
    @Tabelavel(campoSelecionado = false)
    private SubAcaoPPA subAcaoPPA;
    /**
     * Somente uma conta de despesa pode ser selecionada! Verificar qual é o
     * plano de contas de despesa vigente no exercício do PPA para filtrar as
     * contas que poderão ser vinculadas (ver PlanoDecontasExercicio)
     */
    @Tabelavel
    @Etiqueta("Conta de Despesa")
    @ManyToOne
    @Pesquisavel
    @Obrigatorio
    private Conta contaDeDespesa;
    @Tabelavel
    @Monetario
    @Pesquisavel
    private BigDecimal valor;

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataRegistro;
    @OneToMany(mappedBy = "provisaoPPADespesa", cascade = CascadeType.ALL, orphanRemoval = true)
    @Tabelavel
    @Etiqueta(value = "Fontes")
    private List<ProvisaoPPAFonte> provisaoPPAFontes;
    @OneToOne
    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    private ProvisaoPPADespesa origem;
    @Etiqueta("Somente Leitura")
    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    private Boolean somenteLeitura;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Tipo de Despesa")
    @Enumerated(EnumType.STRING)
    @Obrigatorio
    private TipoDespesaORC tipoDespesaORC;
    @ManyToOne
    @Obrigatorio
    @Etiqueta(value = "Unidade Organizacional")
    private UnidadeOrganizacional unidadeOrganizacional;
    @Invisivel
    @Transient
    private Long criadoEm;
    @Transient
    @Tabelavel
    @Etiqueta("Unidade Orçamentária")
    private HierarquiaOrganizacional hierarquiaOrganizacional;

    public ProvisaoPPADespesa() {
        somenteLeitura = false;
        valor = BigDecimal.ZERO;
        criadoEm = System.nanoTime();
        provisaoPPAFontes = new ArrayList<ProvisaoPPAFonte>();
    }

    public ProvisaoPPADespesa(ProvisaoPPADespesa provisaoPPADespesa, HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.setId(provisaoPPADespesa.getId());
        this.setUnidadeOrganizacional(provisaoPPADespesa.getUnidadeOrganizacional());
        this.setHierarquiaOrganizacional(hierarquiaOrganizacional);
        this.setCodigo(provisaoPPADespesa.getCodigo());
        this.setValor(provisaoPPADespesa.getValor());
        this.setTipoDespesaORC(provisaoPPADespesa.getTipoDespesaORC());
        this.setProvisaoPPAFontes(provisaoPPADespesa.getProvisaoPPAFontes());
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public ProvisaoPPADespesa(String codigo) {
        this.codigo = codigo;
    }

    public Boolean getSomenteLeitura() {
        return somenteLeitura;
    }

    public void setSomenteLeitura(Boolean somenteLeitura) {
        this.somenteLeitura = somenteLeitura;
    }

    public ProvisaoPPADespesa getOrigem() {
        return origem;
    }

    public void setOrigem(ProvisaoPPADespesa origem) {
        this.origem = origem;
    }

    public Conta getContaDeDespesa() {
        return contaDeDespesa;
    }

    public void setContaDeDespesa(Conta contaDeDespesa) {
        this.contaDeDespesa = contaDeDespesa;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public SubAcaoPPA getSubAcaoPPA() {
        return subAcaoPPA;
    }

    public void setSubAcaoPPA(SubAcaoPPA subAcaoPPA) {
        this.subAcaoPPA = subAcaoPPA;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public List<ProvisaoPPAFonte> getProvisaoPPAFontes() {
        return provisaoPPAFontes;
    }

    public void setProvisaoPPAFontes(List<ProvisaoPPAFonte> provisaoPPAFontes) {
        this.provisaoPPAFontes = provisaoPPAFontes;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public TipoDespesaORC getTipoDespesaORC() {
        return tipoDespesaORC;
    }

    public void setTipoDespesaORC(TipoDespesaORC tipoDespesaORC) {
        this.tipoDespesaORC = tipoDespesaORC;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
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
        return "Código: " + codigo + " - " + contaDeDespesa.toString();
    }

    public TipoContaDespesa getTipoContaDespesa() {
        return ((ContaDespesa) this.getContaDeDespesa()).getTipoContaDespesa();
    }
}
