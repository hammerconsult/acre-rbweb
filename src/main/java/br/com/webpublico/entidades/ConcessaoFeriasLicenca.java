/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoFerias;
import br.com.webpublico.enums.rh.TipoEnvioDadosRBPonto;
import br.com.webpublico.geradores.CorEntidade;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.ValidadorVigencia;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.collect.ComparisonChain;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author peixe
 */
@Entity

@Audited
@GrupoDiagrama(nome = "RecursosHumanos")
@Etiqueta("Concessão de Férias")
@CorEntidade(value = "#9400D3")
public class ConcessaoFeriasLicenca extends SuperEntidade implements Serializable, ValidadorVigencia, Comparable<ConcessaoFeriasLicenca> {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Transient
    private ContratoFP contratoFP;
    @Etiqueta("Mês")
    @ColunaAuditoriaRH(posicao = 2)
    private Integer mes;
    @ColunaAuditoriaRH(posicao = 4)
    private Integer dias;
    @Etiqueta("Ano")
    @ColunaAuditoriaRH(posicao = 3)
    private Integer ano;
    @Pesquisavel
    @Etiqueta("Data Inicial")
    @Tabelavel
    @Temporal(javax.persistence.TemporalType.DATE)
    @ColunaAuditoriaRH(posicao = 5)
    private Date dataInicial;
    @Pesquisavel
    @Etiqueta("Data Final")
    @Tabelavel
    @Temporal(javax.persistence.TemporalType.DATE)
    @ColunaAuditoriaRH(posicao = 6)
    private Date dataFinal;
    @Pesquisavel
    @Etiqueta("Data da Comunicação")
    @Tabelavel
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataComunicacao;
    @Invisivel
    @Etiqueta("Dias de Abono Pecuniário")
    private Integer diasAbonoPecuniario;
    @Etiqueta("Data Inicial Abono Pecuniário")
    @Temporal(javax.persistence.TemporalType.DATE)
    @Invisivel
    private Date dataInicialAbonoPecuniario;
    @Etiqueta("Data Final Abono Pecuniário")
    @Temporal(javax.persistence.TemporalType.DATE)
    @Invisivel
    private Date dataFinalAbonoPecuniario;
    @Pesquisavel
    @Etiqueta("Período Aquisitivo Férias Licença")
    @Obrigatorio
    @ManyToOne
    @ColunaAuditoriaRH(posicao = 1)
    private PeriodoAquisitivoFL periodoAquisitivoFL;
    @Temporal(TemporalType.DATE)
    @Etiqueta("Data de Cadastro")
    private Date dataCadastro;
    @Temporal(javax.persistence.TemporalType.DATE)
    @Etiqueta("Data de Final de Vigência do Cadastro")
    private Date dataCadastroFinalVigencia;
    @Etiqueta("Total de Faltas Injustificadas")
    private Integer totalFaltasInjustificadas;
    @Etiqueta("Ato Legal")
    @ManyToOne
    private AtoLegal atoLegal;
    @Pesquisavel
    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo Férias")
    private TipoFerias tipoFerias;
    private String observacao;
    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @Invisivel
    private Arquivo arquivo;
    @Etiqueta("Programação de Licença Prêmio")
    @ManyToOne
    private ProgramacaoLicencaPremio programacaoLicencaPremio;
    @Enumerated(EnumType.STRING)
    private TipoEnvioDadosRBPonto tipoEnvioDadosRBPonto;

    private Boolean licencaPremioIndenizada;
    private Integer diasPecunia;

    public ConcessaoFeriasLicenca(Long id, ContratoFP contratoFP, Date dataInicial, Date dataFinal, Date dataComunicacao) {
        this.id = id;
        this.contratoFP = contratoFP;
        this.dataInicial = dataInicial;
        this.dataFinal = dataFinal;
        this.dataComunicacao = dataComunicacao;
    }

    public ConcessaoFeriasLicenca() {
        criadoEm = System.nanoTime();
        diasAbonoPecuniario = 0;
    }

    public ContratoFP getContratoFP() {
        return contratoFP;
    }

    public void setContratoFP(ContratoFP contratoFP) {
        this.contratoFP = contratoFP;
    }

    public Date getDataCadastroFinalVigencia() {
        return dataCadastroFinalVigencia;
    }

    public void setDataCadastroFinalVigencia(Date dataCadastroFinalVigencia) {
        this.dataCadastroFinalVigencia = dataCadastroFinalVigencia;
    }

    public Date getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(Date dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public Date getDataComunicacao() {
        return dataComunicacao;
    }

    public void setDataComunicacao(Date dataComunicacao) {
        this.dataComunicacao = dataComunicacao;
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    public Date getDataFinalAbonoPecuniario() {
        return dataFinalAbonoPecuniario;
    }

    public void setDataFinalAbonoPecuniario(Date dataFinalAbonoPecuniario) {
        this.dataFinalAbonoPecuniario = dataFinalAbonoPecuniario;
    }

    public Date getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(Date dataInicial) {
        this.dataInicial = dataInicial;
    }

    public Date getDataInicialAbonoPecuniario() {
        return dataInicialAbonoPecuniario;
    }

    public void setDataInicialAbonoPecuniario(Date dataInicialAbonoPecuniario) {
        this.dataInicialAbonoPecuniario = dataInicialAbonoPecuniario;
    }

    public Integer getDias() {
        return dias;
    }

    public void setDias(Integer dias) {
        this.dias = dias;
    }

    public Integer getDiasAbonoPecuniario() {
        return diasAbonoPecuniario;
    }

    public void setDiasAbonoPecuniario(Integer diasAbonoPecuniario) {
        this.diasAbonoPecuniario = diasAbonoPecuniario;
    }

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    public PeriodoAquisitivoFL getPeriodoAquisitivoFL() {
        return periodoAquisitivoFL;
    }

    public void setPeriodoAquisitivoFL(PeriodoAquisitivoFL periodoAquisitivoFL) {
        this.periodoAquisitivoFL = periodoAquisitivoFL;
    }

    @Override
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

    public Arquivo getArquivo() {
        return arquivo;
    }

    public void setArquivo(Arquivo arquivo) {
        this.arquivo = arquivo;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public TipoFerias getTipoFerias() {
        return tipoFerias;
    }

    public void setTipoFerias(TipoFerias tipoFerias) {
        this.tipoFerias = tipoFerias;
    }

    public ProgramacaoLicencaPremio getProgramacaoLicencaPremio() {
        return programacaoLicencaPremio;
    }

    public void setProgramacaoLicencaPremio(ProgramacaoLicencaPremio programacaoLicencaPremio) {
        this.programacaoLicencaPremio = programacaoLicencaPremio;
    }

    public Integer getTotalFaltasInjustificadas() {
        return totalFaltasInjustificadas;
    }

    public void setTotalFaltasInjustificadas(Integer totalFaltasInjustificadas) {
        this.totalFaltasInjustificadas = totalFaltasInjustificadas;
    }

    @Override
    public String toString() {
        return periodoAquisitivoFL + "";
    }

    @Override
    public Date getInicioVigencia() {
        return this.dataInicial;
    }

    @Override
    public void setInicioVigencia(Date data) {

    }

    @Override
    public Date getFimVigencia() {
        return this.dataFinal;
    }

    @Override
    public void setFimVigencia(Date data) {

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

    public TipoEnvioDadosRBPonto getTipoEnvioDadosRBPonto() {
        return tipoEnvioDadosRBPonto;
    }

    public void setTipoEnvioDadosRBPonto(TipoEnvioDadosRBPonto tipoEnvioDadosRBPonto) {
        this.tipoEnvioDadosRBPonto = tipoEnvioDadosRBPonto;
    }

    @Override
    public int compareTo(ConcessaoFeriasLicenca o) {
        return ComparisonChain.start()
            .compare(this.getInicioVigencia(), o.getInicioVigencia())
            .result();
    }
}

