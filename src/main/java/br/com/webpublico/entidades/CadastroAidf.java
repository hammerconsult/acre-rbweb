/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.StatusAidf;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author leonardo
 */
@GrupoDiagrama(nome = "CadastroEconomico")
@Entity

@Audited
@Etiqueta("AIDF")
public class CadastroAidf implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Pesquisavel
    @Etiqueta("Número do CMC")
    @Tabelavel
    private CadastroEconomico cadastroEconomico;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Número")
    private String numeroAidf;
    @Temporal(javax.persistence.TemporalType.DATE)
    @Tabelavel
    @Etiqueta("Data do Cadastro")
    @Pesquisavel
    private Date data;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("NF Inicial")
    private String notaFiscalInicial;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("NF Final")
    private String notaFiscalFinal;
    @ManyToOne
    @Etiqueta("N° Série")
    private NumeroSerie numeroSerie;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("NF Inicial Autorizado")
    private String notaFiscalInicialAutorizado;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("NF Final Autorizado")
    private String notaFiscalFinalAutorizado;
    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Situacao")
    private StatusAidf statusAidf = StatusAidf.ABERTO;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Gráfica")
    @ManyToOne
    private Grafica grafica;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataCancelamento;
    private String motivoCancelamento;
    private String numeroProcesso;
    private Integer quantidadeBlocos;
    @ManyToOne
    private UsuarioSistema usuario;
    @Transient
    private Long criadoEm;
    private String numeroProtocolo;
    private Integer anoProtocolo;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataProtocolo;
    private String observacao;
    private String formato;
    @OneToMany(mappedBy = "cadastroAidf", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ArquivoAidf> arquivos;
    @Temporal(TemporalType.DATE)
    private Date validadeNotaFiscal;

    public CadastroAidf() {
        this.criadoEm = System.nanoTime();
        arquivos = new ArrayList<>();
    }

    public List<ArquivoAidf> getArquivos() {
        return arquivos;
    }

    public void setArquivos(List<ArquivoAidf> arquivos) {
        this.arquivos = arquivos;
    }

    public String getNumeroProtocolo() {
        return numeroProtocolo;
    }

    public void setNumeroProtocolo(String numeroProtocolo) {
        this.numeroProtocolo = numeroProtocolo;
    }

    public Date getValidadeNotaFiscal() {
        return validadeNotaFiscal;
    }

    public void setValidadeNotaFiscal(Date validadeNotaFiscal) {
        this.validadeNotaFiscal = validadeNotaFiscal;
    }

    public Integer getAnoProtocolo() {
        return anoProtocolo;
    }

    public void setAnoProtocolo(Integer anoProtocolo) {
        this.anoProtocolo = anoProtocolo;
    }

    public Date getDataProtocolo() {
        return dataProtocolo;
    }

    public void setDataProtocolo(Date dataProtocolo) {
        this.dataProtocolo = dataProtocolo;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public String getFormato() {
        return formato;
    }

    public void setFormato(String formato) {
        this.formato = formato;
    }

    public Date getDataCancelamento() {
        return dataCancelamento = new Date();
    }

    public void setDataCancelamento(Date dataCancelamento) {
        this.dataCancelamento = dataCancelamento;
    }

    public String getMotivoCancelamento() {
        return motivoCancelamento;
    }

    public void setMotivoCancelamento(String motivoCancelamento) {
        this.motivoCancelamento = motivoCancelamento;
    }

    public String getNumeroProcesso() {
        return numeroProcesso;
    }

    public void setNumeroProcesso(String numeroProcesso) {
        this.numeroProcesso = numeroProcesso;
    }

    public StatusAidf getStatusAidf() {
        return statusAidf;
    }

    public void setStatusAidf(StatusAidf statusAidf) {
        this.statusAidf = statusAidf;
    }

    public UsuarioSistema getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioSistema usuario) {
        this.usuario = usuario;
    }

    public String getNumeroAidf() {
        return numeroAidf;
    }

    public Long getNumeroAidfToNumber() {
        if(numeroAidf == null || numeroAidf.isEmpty()){
            return 0l;
        }
        return Long.valueOf(numeroAidf);
    }

    public void setNumeroAidf(String numeroAidf) {
        this.numeroAidf = numeroAidf;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CadastroEconomico getCadastroEconomico() {
        return cadastroEconomico;
    }

    public void setCadastroEconomico(CadastroEconomico cadastroEconomico) {
        this.cadastroEconomico = cadastroEconomico;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public Grafica getGrafica() {
        return grafica;
    }

    public void setGrafica(Grafica grafica) {
        this.grafica = grafica;
    }

    public String getNotaFiscalFinal() {
        return notaFiscalFinal;
    }

    public void setNotaFiscalFinal(String notaFiscalFinal) {
        this.notaFiscalFinal = notaFiscalFinal;
    }

    public String getNotaFiscalFinalAutorizado() {
        return notaFiscalFinalAutorizado;
    }

    public void setNotaFiscalFinalAutorizado(String notaFiscalFinalAutorizado) {
        this.notaFiscalFinalAutorizado = notaFiscalFinalAutorizado;
    }

    public String getNotaFiscalInicial() {
        return notaFiscalInicial;
    }

    public void setNotaFiscalInicial(String notaFiscalInicial) {
        this.notaFiscalInicial = notaFiscalInicial;
    }

    public String getNotaFiscalInicialAutorizado() {
        return notaFiscalInicialAutorizado;
    }

    public void setNotaFiscalInicialAutorizado(String notaFiscalInicialAutorizado) {
        this.notaFiscalInicialAutorizado = notaFiscalInicialAutorizado;
    }

    public NumeroSerie getNumeroSerie() {
        return numeroSerie;
    }

    public void setNumeroSerie(NumeroSerie numeroSerie) {
        this.numeroSerie = numeroSerie;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Integer getQuantidadeBlocos() {
        return quantidadeBlocos;
    }

    public void setQuantidadeBlocos(Integer quantidadeBlocos) {
        this.quantidadeBlocos = quantidadeBlocos;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {

        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    public String getNotasLiberadas(){
        return notaFiscalInicialAutorizado + " à " + notaFiscalFinalAutorizado;
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.CadastroAidf[ id=" + id + " ]";
    }
}
