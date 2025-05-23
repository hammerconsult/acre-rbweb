package br.com.webpublico.entidades.rh.administracaodepagamento;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.TipoFolhaDePagamento;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.FolhaCalculavel;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@GrupoDiagrama(nome = "RecursosHumanos")
@Entity

@Audited
@Etiqueta("Folha de Pagamento - Simulação")
public class FolhaDePagamentoSimulacao extends SuperEntidade implements Serializable, FolhaCalculavel {

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
    private Date efetivadaEm;
    @Etiqueta("Tipo de Folha")
    @Tabelavel
    @Pesquisavel
    @Enumerated(EnumType.STRING)
    private TipoFolhaDePagamento tipoFolhaDePagamento;
    @Invisivel
    @OneToMany(mappedBy = "folhaDePagamentoSimulacao")
    private List<FichaFinanceiraFPSimulacao> fichaFinanceiraFPs;
    @OneToOne
    @Etiqueta("Competência")
    private CompetenciaFP competenciaFP;
    @Etiqueta("Qtade de Meses Retroativos")
    private Integer qtdeMesesRetroacao;

    @ManyToOne
    @Etiqueta("Lote de Processamento")
    @Tabelavel
    private LoteProcessamento loteProcessamento;
    @Transient
    @Invisivel
    private Boolean selecionado;
    @Transient
    @Invisivel
    private DetalhesCalculoRH detalhesCalculoRHAtual;
    @Transient
    @Invisivel
    private List<DetalhesCalculoRH> detalhesCalculoRHList;
    @Invisivel
    @Transient
    private boolean imprimeLogEmArquivo;
    @Invisivel
    @Transient
    private boolean gravarMemoriaCalculo;
    @Invisivel
    @Transient
    private boolean processarCalculoTransient;

    @Override
    public boolean isGravarMemoriaCalculo() {
        return gravarMemoriaCalculo;
    }

    public void setGravarMemoriaCalculo(boolean gravarMemoriaCalculo) {
        this.gravarMemoriaCalculo = gravarMemoriaCalculo;
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

    @Override
    public Integer getVersao() {
        return null;
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
    public void setVersao(Integer calculadaEm) {

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
    public boolean isSimulacao() {
        return true;
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

    public List<FichaFinanceiraFPSimulacao> getFichaFinanceiraFPs() {
        return fichaFinanceiraFPs;
    }

    public void setFichaFinanceiraFPs(List<FichaFinanceiraFPSimulacao> fichaFinanceiraFPs) {
        this.fichaFinanceiraFPs = fichaFinanceiraFPs;
    }

    @Override
    public CompetenciaFP getCompetenciaFP() {
        return competenciaFP;
    }

    public void setCompetenciaFP(CompetenciaFP competenciaFP) {
        this.competenciaFP = competenciaFP;
    }

    public boolean folhaEfetivada() {
        return getEfetivadaEm() != null;
    }

    public FolhaDePagamentoSimulacao() {
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

    @Override
    public List<DetalhesCalculoRH> getDetalhesCalculoRHList() {
        if (detalhesCalculoRHList == null) {
            detalhesCalculoRHList = Lists.newLinkedList();
        }
        return detalhesCalculoRHList;
    }

    @Override
    public List<FiltroFolhaDePagamento> getFiltros() {
        return Lists.newArrayList();
    }

    public void setDetalhesCalculoRHList(List<DetalhesCalculoRH> detalhesCalculoRHList) {
        this.detalhesCalculoRHList = detalhesCalculoRHList;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final FolhaDePagamentoSimulacao other = (FolhaDePagamentoSimulacao) obj;
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
    public DetalhesCalculoRH getDetalhesCalculoRHAtual() {
        return detalhesCalculoRHAtual;
    }

    @Override
    public void setDetalhesCalculoRHAtual(DetalhesCalculoRH detalhesCalculoRHAtual) {
        this.detalhesCalculoRHAtual = detalhesCalculoRHAtual;
    }

    @Override
    public void setFiltros(List<FiltroFolhaDePagamento> filtros) {

    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        String strToString = "Folha De Pagamento: " + ano + " - " + mes + " - " + tipoFolhaDePagamento.getDescricao() + " - " + (unidadeOrganizacional != null ? unidadeOrganizacional.getDescricao() : "");
        return strToString;
    }
}
