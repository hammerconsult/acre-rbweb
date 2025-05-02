package br.com.webpublico.entidades;

import br.com.webpublico.entidadesauxiliares.rh.VwFalta;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.ValidadorVigencia;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: mga
 * Date: 15/10/13
 * Time: 16:44
 * To change this template use File | Settings | File Templates.
 */
@Entity

@Etiqueta("Concessão de Direito à Sexta Parte")
@GrupoDiagrama(nome = "RecursosHumanos")
@Audited
public class SextaParte extends SuperEntidade implements Serializable, ValidadorVigencia {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Tabelavel
    @Etiqueta("Servidor")
    private VinculoFP vinculoFP;
    @Tabelavel
    @Etiqueta("Tem direito")
    @Pesquisavel
    private Boolean temDireito;
    @Enumerated(EnumType.STRING)
    @Etiqueta("Mês")
    private Mes mes;
    @Etiqueta("Ano")
    private Integer ano;
    @Pesquisavel
    @Tabelavel
    @Temporal(TemporalType.DATE)
    @Etiqueta("Início do Benefício")
    private Date inicioVigencia;
    @Temporal(TemporalType.DATE)
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Final do Benefício")
    private Date fimVigencia;
    @Transient
    @Invisivel
    private double totalAnos;
    @Transient
    @Etiqueta("Afastamentos")
    private List<Afastamento> afastamentoList;
    @Transient
    @Etiqueta("Faltas")
    private List<VwFalta> faltasList;
    @Transient
    @Etiqueta("Averbação")
    private List<AverbacaoTempoServico> averbacaoTempoServicoList;
    @Transient
    private List<PeriodoExcludenteDTO> periodoExcludenteList;
    @ManyToOne
    @Etiqueta("Ato Legal")
    private AtoLegal atoLegal;
    @Length(maximo = 255)
    private String observacao;
    @ManyToOne
    @Etiqueta("Tipo de Sexta Parte")
    @Obrigatorio
    private TipoSextaParte tipoSextaParte;
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private BaseFP baseFP;

    public SextaParte() {
        super();
        inicioVigencia = new Date();
        averbacaoTempoServicoList = new LinkedList<>();
        faltasList = new LinkedList<>();
        afastamentoList = new LinkedList<>();
        periodoExcludenteList = new LinkedList<>();
    }

    public SextaParte(VinculoFP vinculoFP, Boolean temDireito, Mes mes, Integer ano) {
        this.vinculoFP = vinculoFP;
        this.temDireito = temDireito;
        this.mes = mes;
        this.ano = ano;
    }

    public SextaParte(VinculoFP vinculoFP, Boolean temDireito) {
        this.vinculoFP = vinculoFP;
        this.temDireito = temDireito;
    }

    public SextaParte(VinculoFP vinculoFP, double totalAnos) {
        this.vinculoFP = vinculoFP;
        this.totalAnos = totalAnos;
        this.temDireito = false;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public List<Afastamento> getAfastamentoList() {
        return afastamentoList;
    }

    public void setAfastamentoList(List<Afastamento> afastamentoList) {
        this.afastamentoList = afastamentoList;
    }

    public List<VwFalta> getFaltasList() {
        return faltasList;
    }

    public void setFaltasList(List<VwFalta> faltasList) {
        this.faltasList = faltasList;
    }

    public List<AverbacaoTempoServico> getAverbacaoTempoServicoList() {
        return averbacaoTempoServicoList;
    }

    public void setAverbacaoTempoServicoList(List<AverbacaoTempoServico> averbacaoTempoServicoList) {
        this.averbacaoTempoServicoList = averbacaoTempoServicoList;
    }

    public List<PeriodoExcludenteDTO> getPeriodoExcludenteList() {
        return periodoExcludenteList;
    }

    public void setPeriodoExcludenteList(List<PeriodoExcludenteDTO> periodoExcludenteList) {
        this.periodoExcludenteList = periodoExcludenteList;
    }

    public double getTotalAnos() {
        return totalAnos;
    }

    public void setTotalAnos(double totalAnos) {
        this.totalAnos = totalAnos;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public VinculoFP getVinculoFP() {
        return vinculoFP;
    }

    public void setVinculoFP(VinculoFP vinculoFP) {
        this.vinculoFP = vinculoFP;
    }

    public Boolean getTemDireito() {
        return temDireito;
    }

    public void setTemDireito(Boolean temDireito) {
        this.temDireito = temDireito;
    }

    public Mes getMes() {
        return mes;
    }

    public void setMes(Mes mes) {
        this.mes = mes;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public BaseFP getBaseFP() {
        return baseFP;
    }

    public void setBaseFP(BaseFP baseFP) {
        this.baseFP = baseFP;
    }

    @Override
    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    @Override
    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public AtoLegal getAtoLegal() {
        return atoLegal;
    }

    public void setAtoLegal(AtoLegal atoLegal) {
        this.atoLegal = atoLegal;
    }

    @Override
    public Date getFimVigencia() {
        return fimVigencia;
    }

    @Override
    public void setFimVigencia(Date fimVigencia) {
        this.fimVigencia = fimVigencia;
    }

    public TipoSextaParte getTipoSextaParte() {
        return tipoSextaParte;
    }

    public void setTipoSextaParte(TipoSextaParte tipoSextaParte) {
        this.tipoSextaParte = tipoSextaParte;
    }

    @Override
    public String toString() {
        return vinculoFP + "";
    }
}
