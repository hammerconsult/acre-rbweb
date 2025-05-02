package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoConcessaoLicencaPremio;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.ValidadorVigencia;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@GrupoDiagrama(nome = "Recursos Humanos")
@Entity
@Audited
@Etiqueta("Programação Licença Prêmio")
public class ProgramacaoLicencaPremio implements Serializable, ValidadorVigencia {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Etiqueta("Código")
    @Invisivel
    private Long id;
    @Temporal(TemporalType.DATE)
    @Etiqueta("Data de Cadastro")
    private Date dataCadastro;
    @Transient
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Contrato FP")
    private ContratoFP contratoFP;
    @Transient
    @Tabelavel
    @Etiqueta("Período Aquisitivo")
    private String periodoAquisitivo;
    @ManyToOne
    @Invisivel
    @Etiqueta("Período Aquisitivo")
    @Obrigatorio
    private PeriodoAquisitivoFL periodoAquisitivoFL;
    @ManyToOne
    @Etiqueta("Parâmetro da Licença Prêmio")
    private ParametroLicencaPremio parametroLicencaPremio;
    @Temporal(TemporalType.DATE)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Início de Vigência")
    private Date inicioVigencia;
    @Pesquisavel
    @Temporal(TemporalType.DATE)
    @Tabelavel
    @Etiqueta("Final de Vigência")
    private Date finalVigencia;
    @Tabelavel
    @Transient
    @Etiqueta("Dias de Gozo")
    private Integer diasGozo;
    @Transient
    @Invisivel
    private Integer diasRetardo;
    @ManyToOne
    @Tabelavel
    @Etiqueta("Ato Legal")
    @Obrigatorio
    private AtoLegal atoLegal;
    @Transient
    @Invisivel
    private Long criadoEm;
    @Transient
    @Enumerated(EnumType.STRING)
    private TipoConcessaoLicencaPremio tipoConcessaoLicencaPremio;
    private Boolean licencaPremioIndenizada;
    private Integer diasPecunia;
    private String observacao;

    public ProgramacaoLicencaPremio() {
        criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(Date dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public PeriodoAquisitivoFL getPeriodoAquisitivoFL() {
        return periodoAquisitivoFL;
    }

    public void setPeriodoAquisitivoFL(PeriodoAquisitivoFL periodoAquisitivoFL) {
        this.periodoAquisitivoFL = periodoAquisitivoFL;
    }

    @Override
    public Date getInicioVigencia() {
        return inicioVigencia;
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

    public Date getFinalVigencia() {
        return finalVigencia;
    }

    public void setFinalVigencia(Date finalVigencia) {
        this.finalVigencia = finalVigencia;
    }

    public Integer getDiasRetardo() {
        return diasRetardo;
    }

    public void setDiasRetardo(Integer diasRetardo) {
        this.diasRetardo = diasRetardo;
    }

    public ParametroLicencaPremio getParametroLicencaPremio() {
        return parametroLicencaPremio;
    }

    public void setParametroLicencaPremio(ParametroLicencaPremio parametroLicencaPremio) {
        this.parametroLicencaPremio = parametroLicencaPremio;
    }

    public AtoLegal getAtoLegal() {
        return atoLegal;
    }

    public void setAtoLegal(AtoLegal atoLegal) {
        this.atoLegal = atoLegal;
    }

    public ContratoFP getContratoFP() {
        return contratoFP;
    }

    public void setContratoFP(ContratoFP contratoFP) {
        this.contratoFP = contratoFP;
    }

    public String getPeriodoAquisitivo() {
        return periodoAquisitivo;
    }

    public void setPeriodoAquisitivo(String periodoAquisitivo) {
        this.periodoAquisitivo = periodoAquisitivo;
    }

    public Integer getDiasGozo() {
        return diasGozo;
    }

    public void setDiasGozo(Integer diasGozo) {
        this.diasGozo = diasGozo;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public TipoConcessaoLicencaPremio getTipoConcessaoLicencaPremio() {
        return tipoConcessaoLicencaPremio;
    }

    public void setTipoConcessaoLicencaPremio(TipoConcessaoLicencaPremio tipoConcessaoLicencaPremio) {
        this.tipoConcessaoLicencaPremio = tipoConcessaoLicencaPremio;
    }

    @Override
    public boolean equals(Object o) {
        return IdentidadeDaEntidade.calcularEquals(this, o);
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public String toString() {
        return this.getPeriodoAquisitivoFL().getContratoFP() + " - " + this.getPeriodoAquisitivoFL().toStringAlternativo();
    }

    public Boolean getLicencaPremioIndenizada() {
        return licencaPremioIndenizada != null ? licencaPremioIndenizada : false;
    }

    public void setLicencaPremioIndenizada(Boolean licencaPremioIndenizada) {
        this.licencaPremioIndenizada = licencaPremioIndenizada;
    }

    public Integer getDiasPecunia() {
        return diasPecunia;
    }

    public void setDiasPecunia(Integer diasPecunia) {
        this.diasPecunia = diasPecunia;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public static enum ProgramacaoDesejada {
        ANTERIOR,
        PROXIMA;
    }
}
