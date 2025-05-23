/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * @author Wellington
 */
@Entity

@Audited
@Etiqueta("Parâmetros de Dívida Ativa")
@GrupoDiagrama(nome = "Divida Ativa")
public class ParametrosDividaAtiva extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @ManyToOne
    @Pesquisavel
    @Etiqueta("Exercício")
    @Tabelavel(ordemApresentacao = 1)
    private Exercicio exercicio;
    @Obrigatorio
    @Etiqueta(value = "Tipo Docto Oficial Cadastro Imobiliário")
    @ManyToOne
    private TipoDoctoOficial tipoDoctoOficialImobiliario;
    @Obrigatorio
    @Etiqueta(value = "Tipo Docto Oficial Cadastro Econômico")
    @ManyToOne
    private TipoDoctoOficial tipoDoctoOficialMobiliario;
    @Obrigatorio
    @Etiqueta(value = "Tipo Docto Oficial Cadastro Rural")
    @ManyToOne
    private TipoDoctoOficial tipoDoctoOficialRural;
    @Obrigatorio
    @Etiqueta(value = "Tipo Docto Oficial Contribuinte Geral (PF)")
    @ManyToOne
    private TipoDoctoOficial tipoDoctoOficialContribPF;
    @Obrigatorio
    @Etiqueta(value = "Tipo Docto Oficial Contribuinte Geral (PJ)")
    @ManyToOne
    private TipoDoctoOficial tipoDoctoOficialContribPJ;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "parametrosDividaAtiva", orphanRemoval = true)
    private List<ParametroDividaAtivaLeisDivida> parametroDividaAtivaLeisDivida;
    @Invisivel
    @Transient
    private Long criadoEm;
    @Obrigatorio
    @ManyToOne
    @Etiqueta(value = "Tipo de Documento Oficial para Certidão de Cadastro Imobiliário")
    private TipoDoctoOficial tipoDoctoCertidaoImob;
    @Obrigatorio
    @ManyToOne
    @Etiqueta(value = "Tipo de Documento Oficial para Certidão de Cadastro Mobiliário")
    private TipoDoctoOficial tipoDoctoCertidaoMob;
    @Obrigatorio
    @ManyToOne
    @Etiqueta(value = "Tipo de Documento Oficial para Certidão de Cadastro Rural")
    private TipoDoctoOficial tipoDoctoCertidaoRural;
    @Obrigatorio
    @ManyToOne
    @Etiqueta(value = "Tipo de Documento Oficial para Certidão de Pessoa Fisica")
    private TipoDoctoOficial tipoDoctoCertidaoContribPF;
    @Obrigatorio
    @ManyToOne
    @Etiqueta(value = "Tipo de Documento Oficial para Certidão de Pessoa Jurídica")
    private TipoDoctoOficial tipoDoctoCertidaoContribPJ;
    @ManyToOne
    private AtoLegal atoLegal;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "parametrosDividaAtiva", orphanRemoval = true)
    private List<ParamDividaAtivaDivida> dividasEnvioCDA;
    @Lob
    private String sqlCargaCDA;
    @Lob
    private String sqlCargaParcelamento;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "parametrosDividaAtiva", orphanRemoval = true)
    private List<AgrupadorCDA> agrupadoresCDA;

    public ParametrosDividaAtiva() {
        this.criadoEm = System.nanoTime();
        this.parametroDividaAtivaLeisDivida = Lists.newArrayList();
        this.dividasEnvioCDA = Lists.newArrayList();
        this.agrupadoresCDA = Lists.newArrayList();
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public AtoLegal getAtoLegal() {
        return atoLegal;
    }

    public void setAtoLegal(AtoLegal atoLegal) {
        this.atoLegal = atoLegal;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public TipoDoctoOficial getTipoDoctoOficialImobiliario() {
        return tipoDoctoOficialImobiliario;
    }

    public void setTipoDoctoOficialImobiliario(TipoDoctoOficial tipoDoctoOficialImobiliario) {
        this.tipoDoctoOficialImobiliario = tipoDoctoOficialImobiliario;
    }

    public TipoDoctoOficial getTipoDoctoOficialMobiliario() {
        return tipoDoctoOficialMobiliario;
    }

    public void setTipoDoctoOficialMobiliario(TipoDoctoOficial tipoDoctoOficialMobiliario) {
        this.tipoDoctoOficialMobiliario = tipoDoctoOficialMobiliario;
    }

    public TipoDoctoOficial getTipoDoctoOficialContribPF() {
        return tipoDoctoOficialContribPF;
    }

    public void setTipoDoctoOficialContribPF(TipoDoctoOficial tipoDoctoOficialContribPF) {
        this.tipoDoctoOficialContribPF = tipoDoctoOficialContribPF;
    }

    public TipoDoctoOficial getTipoDoctoOficialContribPJ() {
        return tipoDoctoOficialContribPJ;
    }

    public void setTipoDoctoOficialContribPJ(TipoDoctoOficial tipoDoctoOficialContribPJ) {
        this.tipoDoctoOficialContribPJ = tipoDoctoOficialContribPJ;
    }

    public TipoDoctoOficial getTipoDoctoOficialRural() {
        return tipoDoctoOficialRural;
    }

    public void setTipoDoctoOficialRural(TipoDoctoOficial tipoDoctoOficialRural) {
        this.tipoDoctoOficialRural = tipoDoctoOficialRural;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<ParametroDividaAtivaLeisDivida> getParametroDividaAtivaLeisDivida() {
        return parametroDividaAtivaLeisDivida;
    }

    public void setParametroDividaAtivaLeisDivida(List<ParametroDividaAtivaLeisDivida> parametroDividaAtivaLeisDivida) {
        this.parametroDividaAtivaLeisDivida = parametroDividaAtivaLeisDivida;
    }

    public TipoDoctoOficial getTipoDoctoCertidaoImob() {
        return tipoDoctoCertidaoImob;
    }

    public void setTipoDoctoCertidaoImob(TipoDoctoOficial tipoDoctoCertidaoImobiliario) {
        this.tipoDoctoCertidaoImob = tipoDoctoCertidaoImobiliario;
    }

    public TipoDoctoOficial getTipoDoctoCertidaoMob() {
        return tipoDoctoCertidaoMob;
    }

    public void setTipoDoctoCertidaoMob(TipoDoctoOficial tipoDoctoCertidaoMobiliario) {
        this.tipoDoctoCertidaoMob = tipoDoctoCertidaoMobiliario;
    }

    public TipoDoctoOficial getTipoDoctoCertidaoRural() {
        return tipoDoctoCertidaoRural;
    }

    public void setTipoDoctoCertidaoRural(TipoDoctoOficial tipoDoctoCertidaoRural) {
        this.tipoDoctoCertidaoRural = tipoDoctoCertidaoRural;
    }

    public TipoDoctoOficial getTipoDoctoCertidaoContribPF() {
        return tipoDoctoCertidaoContribPF;
    }

    public void setTipoDoctoCertidaoContribPF(TipoDoctoOficial tipoDoctoCertidaoContribPF) {
        this.tipoDoctoCertidaoContribPF = tipoDoctoCertidaoContribPF;
    }

    public TipoDoctoOficial getTipoDoctoCertidaoContribPJ() {
        return tipoDoctoCertidaoContribPJ;
    }

    public void setTipoDoctoCertidaoContribPJ(TipoDoctoOficial tipoDoctoCertidaoContribPJ) {
        this.tipoDoctoCertidaoContribPJ = tipoDoctoCertidaoContribPJ;
    }

    public List<ParamDividaAtivaDivida> getDividasEnvioCDA() {
        return dividasEnvioCDA;
    }

    public void setDividasEnvioCDA(List<ParamDividaAtivaDivida> dividasEnvioCDA) {
        this.dividasEnvioCDA = dividasEnvioCDA;
    }

    public List<Long> getIdDividas() {
        List<Long> ids = Lists.newArrayList();
        for (ParamDividaAtivaDivida paramDividaAtivaDivida : dividasEnvioCDA) {
            ids.add(paramDividaAtivaDivida.getDivida().getId());
        }
        return ids;
    }

    public String getSqlCargaCDA() {
        return sqlCargaCDA;
    }

    public void setSqlCargaCDA(String sqlCargaCDA) {
        this.sqlCargaCDA = sqlCargaCDA;
    }

    public String getSqlCargaParcelamento() {
        return sqlCargaParcelamento;
    }

    public void setSqlCargaParcelamento(String sqlCargaParcelamento) {
        this.sqlCargaParcelamento = sqlCargaParcelamento;
    }

    public List<AgrupadorCDA> getAgrupadoresCDA() {
        return agrupadoresCDA;
    }

    public void setAgrupadoresCDA(List<AgrupadorCDA> agrupadoresCDA) {
        this.agrupadoresCDA = agrupadoresCDA;
    }

    @Override
    public String toString() {
        return "Parâmetros";
    }
}
