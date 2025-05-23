package br.com.webpublico.entidades;

import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.TipoFolhaDePagamento;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.FolhaCalculavel;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@GrupoDiagrama(nome = "RecursosHumanos")
@Entity

@Audited
@Etiqueta("Folha de Pagamento")
public class FolhaDePagamento implements Serializable, FolhaCalculavel {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Mês")
    @Enumerated(EnumType.ORDINAL)
    private Mes mes;
    @Tabelavel
    @Etiqueta("Ano")
    @Pesquisavel
    private Integer ano;
    @Etiqueta("Calculada Em")
    @Tabelavel
    @Pesquisavel
    @Temporal(TemporalType.DATE)
    private Date calculadaEm;
    @Etiqueta("Unidade Organziacional")
    @Tabelavel
    @Pesquisavel
    @ManyToOne
    private UnidadeOrganizacional unidadeOrganizacional;
    @Tabelavel
    @Pesquisavel
    @Temporal(TemporalType.DATE)
    @Etiqueta("Efetivada Em")
    /**
     * Determina se a folha de pagamento já foi efetivada, o que impedirá
     * cálculos posteriores que resultariam em duplicidades na folha.
     *
     */
    private Date efetivadaEm;
    @Etiqueta("Tipo de Folha")
    @Tabelavel
    @Pesquisavel
    @Enumerated(EnumType.STRING)
    private TipoFolhaDePagamento tipoFolhaDePagamento;
    @Invisivel
    @OneToMany(mappedBy = "folhaDePagamento")
    private List<FichaFinanceiraFP> fichaFinanceiraFPs;
    @OneToOne
    @Etiqueta("Competência")
    private CompetenciaFP competenciaFP;
    @Invisivel
    @OneToMany(mappedBy = "folhaDePagamento")
    private List<Empenho> empenhos;
    @Tabelavel
    @Etiqueta("Versão")
    private Integer versao;
    @Etiqueta("Qtade de Meses Retroativos")
    private Integer qtdeMesesRetroacao;

    @ManyToOne
    @Etiqueta("Lote de Processamento")
    @Tabelavel
    private LoteProcessamento loteProcessamento;
    @OneToMany(mappedBy = "folhaDePagamento", orphanRemoval = true, cascade = CascadeType.ALL)
    @OrderBy("dataCalculo asc")
    @Invisivel
    private List<DetalhesCalculoRH> detalhesCalculoRHList;
    @OneToMany(mappedBy = "folhaDePagamento", orphanRemoval = true, cascade = CascadeType.ALL)
    @OrderBy("calculadaEm asc")
    @Invisivel
    private List<FiltroFolhaDePagamento> filtros;
    @Transient
    @Invisivel
    private Boolean selecionado;
    @OneToMany(mappedBy = "folhaDePagamento")
    @Invisivel
    private List<SefipFolhaDePagamento> sefipsFolhaDePagamento;
    @Transient
    @Invisivel
    private DetalhesCalculoRH detalhesCalculoRHAtual;
    @Invisivel
    @Transient
    private boolean imprimeLogEmArquivo;
    @Invisivel
    @Transient
    private boolean gravarMemoriaCalculo;
    @Invisivel
    @Transient
    private boolean processarCalculoTransient;

    private Boolean exibirPortal;
    @Etiqueta("Disponibilizada no Portal de Servidor Em:")
    @Temporal(TemporalType.DATE)
    private Date dataPortal;

    public List<SefipFolhaDePagamento> getSefipsFolhaDePagamento() {
        return sefipsFolhaDePagamento;
    }

    public void setSefipsFolhaDePagamento(List<SefipFolhaDePagamento> sefipsFolhaDePagamento) {
        this.sefipsFolhaDePagamento = sefipsFolhaDePagamento;
    }

    @Override
    public boolean isImprimeLogEmArquivo() {
        return imprimeLogEmArquivo;
    }

    public boolean isProcessarCalculoTransient() {
        return processarCalculoTransient;
    }

    public void setProcessarCalculoTransient(boolean processarCalculoTransient) {
        this.processarCalculoTransient = processarCalculoTransient;
    }

    @Override
    public boolean isGravarMemoriaCalculo() {
        return gravarMemoriaCalculo;
    }

    public void setGravarMemoriaCalculo(boolean gravarMemoriaCalculo) {
        this.gravarMemoriaCalculo = gravarMemoriaCalculo;
    }


    @Override
    public void setImprimeLogEmArquivo(boolean imprimeLogEmArquivo) {
        this.imprimeLogEmArquivo = imprimeLogEmArquivo;
    }

    public Boolean getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(Boolean selecionado) {
        this.selecionado = selecionado;
    }

    @Override
    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    @Override
    public Date getCalculadaEm() {
        return calculadaEm;
    }

    @Override
    public void setCalculadaEm(Date calculadaEm) {
        this.calculadaEm = calculadaEm;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Mes getMes() {
        return mes;
    }

    public void setMes(Mes mes) {
        this.mes = mes;
    }

    @Override
    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    @Override
    public Date getEfetivadaEm() {
        return efetivadaEm;
    }

    public void setEfetivadaEm(Date efetivadaEm) {
        this.efetivadaEm = efetivadaEm;
    }

    @Override
    public TipoFolhaDePagamento getTipoFolhaDePagamento() {
        return tipoFolhaDePagamento;
    }

    public void setTipoFolhaDePagamento(TipoFolhaDePagamento tipoFolhaDePagamento) {
        this.tipoFolhaDePagamento = tipoFolhaDePagamento;
    }

    public List<FichaFinanceiraFP> getFichaFinanceiraFPs() {
        return fichaFinanceiraFPs;
    }

    public void setFichaFinanceiraFPs(List<FichaFinanceiraFP> fichaFinanceiraFPs) {
        this.fichaFinanceiraFPs = fichaFinanceiraFPs;
    }

    @Override
    public CompetenciaFP getCompetenciaFP() {
        return competenciaFP;
    }

    public void setCompetenciaFP(CompetenciaFP competenciaFP) {
        this.competenciaFP = competenciaFP;
    }

    public List<Empenho> getEmpenhos() {
        return empenhos;
    }

    public void setEmpenhos(List<Empenho> empenhos) {
        this.empenhos = empenhos;
    }

    public Integer getVersao() {
        return versao;
    }

    public void setVersao(Integer versao) {
        this.versao = versao;
    }

    public boolean folhaEfetivada() {
        return getEfetivadaEm() != null;
    }

    public FolhaDePagamento() {
        qtdeMesesRetroacao = 0;
    }

    @Override
    public LoteProcessamento getLoteProcessamento() {
        return loteProcessamento;
    }

    @Override
    public void setLoteProcessamento(LoteProcessamento loteProcessamento) {
        this.loteProcessamento = loteProcessamento;
    }

    public Boolean getExibirPortal() {
        return exibirPortal == null ? Boolean.FALSE : exibirPortal;
    }

    public void setExibirPortal(Boolean exibirPortal) {
        this.exibirPortal = exibirPortal;
    }

    public Date getDataPortal() {
        return dataPortal;
    }

    public void setDataPortal(Date dataPortal) {
        this.dataPortal = dataPortal;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final FolhaDePagamento other = (FolhaDePagamento) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    public Integer getQtdeMesesRetroacao() {
        if (qtdeMesesRetroacao == null) return 0;
        return qtdeMesesRetroacao;
    }

    public void setQtdeMesesRetroacao(Integer qtdeMesesRetroacao) {
        this.qtdeMesesRetroacao = qtdeMesesRetroacao;
    }

    @Override
    public List<DetalhesCalculoRH> getDetalhesCalculoRHList() {
        return detalhesCalculoRHList;
    }

    public void setDetalhesCalculoRHList(List<DetalhesCalculoRH> detalhesCalculoRHList) {
        this.detalhesCalculoRHList = detalhesCalculoRHList;
    }

    @Override
    public DetalhesCalculoRH getDetalhesCalculoRHAtual() {
        return detalhesCalculoRHAtual;
    }

    @Override
    public void setDetalhesCalculoRHAtual(DetalhesCalculoRH detalhesCalculoRHAtual) {
        this.detalhesCalculoRHAtual = detalhesCalculoRHAtual;
    }

    @Override
    public List<FiltroFolhaDePagamento> getFiltros() {
        return filtros;
    }

    @Override
    public void setFiltros(List<FiltroFolhaDePagamento> filtros) {
        this.filtros = filtros;
    }

    @Override
    public boolean isSimulacao() {
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        try {
            String strToString = "Folha De Pagamento: " + ano + " - " + mes + " - " + tipoFolhaDePagamento.getDescricao() + " - " + (unidadeOrganizacional != null ? unidadeOrganizacional.getDescricao() : "");
            if (getVersao() != null && getVersao() > 0) {
                strToString = strToString + " - " + getVersao().toString();
            }
            return strToString;
        } catch (NullPointerException np) {
            return "Folha De Pagamento: " + ano + " - " + mes;
        }

    }
}
