/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades.rh.creditodesalario;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.configuracao.ConfiguracaoCreditoSalario;
import br.com.webpublico.entidadesauxiliares.DependenciasDirf;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorioConferenciaCreditoSalario;
import br.com.webpublico.enums.TipoGeracaoCreditoSalario;
import br.com.webpublico.enums.rh.creditosalario.BancoCreditoSalario;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@GrupoDiagrama(nome = "Recursos Humanos")
@Entity
@Audited
@Etiqueta(value = "Crédito de Salário")
public class CreditoSalario extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Temporal(TemporalType.DATE)
    @Obrigatorio
    @Etiqueta("Data de Crédito")
    @Tabelavel
    @Pesquisavel
    private Date dataCredito;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Conta Bancária")
    @Tabelavel
    @Pesquisavel
    private ContaBancariaEntidade contaBancariaEntidade;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Competência")
    @Tabelavel
    @Pesquisavel
    private CompetenciaFP competenciaFP;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Folha de Pagamento")
    @Tabelavel
    @Pesquisavel
    private FolhaDePagamento folhaDePagamento;
    @OneToOne
    @Etiqueta("Arquivo")
    private Arquivo arquivo;
    @Etiqueta("Arquivo Relatório")
    @OneToOne
    private Arquivo arquivoRelatorio;
    @Transient
    private List<GrupoRecursoFP> grupos;
    @Transient
    private BeneficioPensaoAlimenticia[] beneficiarios;
    @Transient
    @Obrigatorio
    @Etiqueta("Orgão")
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    @Transient
    @Etiqueta("Log")
    private DependenciasDirf.TipoLog tipoLog;
    @Transient
    @Etiqueta("Conteúdo do Arquivo")
    private String conteudoArquivo;
    @Transient
    @Etiqueta("Matrículas")
    private List<MatriculaFP> matriculas;
    @Transient
    private ParametrosRelatorioConferenciaCreditoSalario parametrosRelatorioConferenciaCreditoSalario;
    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo de Arquivo")
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    private TipoGeracaoCreditoSalario tipoGeracaoCreditoSalario;
    @OrderBy(value = "sequencial")
    @OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = true, mappedBy = "creditoSalario")
    private List<ItemCreditoSalario> itensCreditoSalario;
    @Temporal(TemporalType.TIMESTAMP)
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Gerado Em")
    private Date geradoEm;
    @Transient
    @Invisivel
    private ConfiguracaoCreditoSalario configuracaoCreditoSalario;
    @Enumerated(EnumType.STRING)
    private BancoCreditoSalario bancoCreditoSalario;

    @Invisivel
    @OneToMany(mappedBy = "creditoSalario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LogCreditoSalario> itemLogCreditoSalario;

    @Transient
    private List<RecursoFP> recursos;
    @Transient
    private List<HierarquiaOrganizacional> hierarquiaOrganizacionals;
    @Transient
    private List<VinculoFP> vinculoFPS;

    @Obrigatorio
    @Etiqueta("Número Sequencial do Arquivo")
    private Integer numeroRemessa;

    @Etiqueta("Data Registro")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataRegistro;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "creditoSalario")
    private List<FiltroCreditoSalario> filtroCreditoSalarios;

    public CreditoSalario() {
        matriculas = new ArrayList<>();
        grupos = Lists.newArrayList();
        itensCreditoSalario = Lists.newLinkedList();
        geradoEm = new Date();
        itemLogCreditoSalario = Lists.newArrayList();
        filtroCreditoSalarios = Lists.newLinkedList();
        recursos = Lists.newArrayList();
        vinculoFPS = Lists.newArrayList();
        hierarquiaOrganizacionals = Lists.newArrayList();
    }

    public List<GrupoRecursoFP> getGrupos() {
        return grupos;
    }

    public void setGrupos(List<GrupoRecursoFP> grupos) {
        this.grupos = grupos;
    }

    public BeneficioPensaoAlimenticia[] getBeneficiarios() {
        return beneficiarios;
    }

    public void setBeneficiarios(BeneficioPensaoAlimenticia[] beneficiarios) {
        this.beneficiarios = beneficiarios;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ContaBancariaEntidade getContaBancariaEntidade() {
        return contaBancariaEntidade;
    }

    public void setContaBancariaEntidade(ContaBancariaEntidade contaBancariaEntidade) {
        this.contaBancariaEntidade = contaBancariaEntidade;
    }

    public CompetenciaFP getCompetenciaFP() {
        return competenciaFP;
    }

    public void setCompetenciaFP(CompetenciaFP competenciaFP) {
        this.competenciaFP = competenciaFP;
    }

    public FolhaDePagamento getFolhaDePagamento() {
        return folhaDePagamento;
    }

    public void setFolhaDePagamento(FolhaDePagamento folhaDePagamento) {
        this.folhaDePagamento = folhaDePagamento;
    }

    public Date getDataCredito() {
        return dataCredito;
    }

    public void setDataCredito(Date dataCredito) {
        this.dataCredito = dataCredito;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public Arquivo getArquivo() {
        return arquivo;
    }

    public void setArquivo(Arquivo arquivo) {
        this.arquivo = arquivo;
    }

    public Arquivo getArquivoRelatorio() {
        return arquivoRelatorio;
    }

    public void setArquivoRelatorio(Arquivo arquivoRelatorio) {
        this.arquivoRelatorio = arquivoRelatorio;
    }

    public DependenciasDirf.TipoLog getTipoLog() {
        return tipoLog;
    }

    public void setTipoLog(DependenciasDirf.TipoLog tipoLog) {
        this.tipoLog = tipoLog;
    }

    public String getConteudoArquivo() {
        return conteudoArquivo;
    }

    public void setConteudoArquivo(String conteudoArquivo) {
        this.conteudoArquivo = conteudoArquivo;
    }

    public List<MatriculaFP> getMatriculas() {
        return matriculas;
    }

    public void setMatriculas(List<MatriculaFP> matriculas) {
        this.matriculas = matriculas;
    }

    public List<LogCreditoSalario> getItemLogCreditoSalario() {
        return itemLogCreditoSalario;
    }

    public void setItemLogCreditoSalario(List<LogCreditoSalario> itemLogCreditoSalario) {
        this.itemLogCreditoSalario = itemLogCreditoSalario;
    }

    public ParametrosRelatorioConferenciaCreditoSalario getParametrosRelatorioConferenciaCreditoSalario() {
        return parametrosRelatorioConferenciaCreditoSalario;
    }

    public void setParametrosRelatorioConferenciaCreditoSalario(ParametrosRelatorioConferenciaCreditoSalario parametrosRelatorioConferenciaCreditoSalario) {
        this.parametrosRelatorioConferenciaCreditoSalario = parametrosRelatorioConferenciaCreditoSalario;
    }

    public List<ItemCreditoSalario> getItensCreditoSalario() {
        return itensCreditoSalario;
    }

    public void setItensCreditoSalario(List<ItemCreditoSalario> itensCreditoSalario) {
        this.itensCreditoSalario = itensCreditoSalario;
    }

    public ConfiguracaoCreditoSalario getConfiguracaoCreditoSalario() {
        return configuracaoCreditoSalario;
    }

    public void setConfiguracaoCreditoSalario(ConfiguracaoCreditoSalario configuracaoCreditoSalario) {
        this.configuracaoCreditoSalario = configuracaoCreditoSalario;
    }

    public Date getGeradoEm() {
        return geradoEm;
    }

    public void setGeradoEm(Date geradoEm) {
        this.geradoEm = geradoEm;
    }

    @Override
    public String toString() {
        return getFolhaDePagamento().getMes().getNumeroMes() + " / " + getFolhaDePagamento().getAno() + " - " + getHierarquiaOrganizacional() + " - " + getTipoGeracaoCreditoSalario().getDescricao();
    }

    public TipoGeracaoCreditoSalario getTipoGeracaoCreditoSalario() {
        return tipoGeracaoCreditoSalario;
    }

    public void setTipoGeracaoCreditoSalario(TipoGeracaoCreditoSalario tipoGeracaoCreditoSalario) {
        this.tipoGeracaoCreditoSalario = tipoGeracaoCreditoSalario;
    }

    public boolean isTipoArquivoServidor() {
        return TipoGeracaoCreditoSalario.SERVIDORES.name().equals(this.tipoGeracaoCreditoSalario.name());
    }

    public boolean isTipoArquivoPensionista() {
        return TipoGeracaoCreditoSalario.PENSIONISTAS.name().equals(this.tipoGeracaoCreditoSalario.name());
    }

    public enum TipoCreditoSalario {
        TODOS("Todos"),
        SERVIDORES("Servidores Ativos/Aposentados/Pensionistas/Beneficiários"),
        PENSAO_ALIMENTICIA("Pensão Alimentícia");

        private String descricao;

        private TipoCreditoSalario(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    public List<RecursoFP> getRecursos() {
        return recursos;
    }

    public void setRecursos(List<RecursoFP> recursos) {
        this.recursos = recursos;
    }

    public List<HierarquiaOrganizacional> getHierarquiaOrganizacionals() {
        return hierarquiaOrganizacionals;
    }

    public void setHierarquiaOrganizacionals(List<HierarquiaOrganizacional> hierarquiaOrganizacionals) {
        this.hierarquiaOrganizacionals = hierarquiaOrganizacionals;
    }

    public List<VinculoFP> getVinculoFPS() {
        return vinculoFPS;
    }

    public void setVinculoFPS(List<VinculoFP> vinculoFPS) {
        this.vinculoFPS = vinculoFPS;
    }

    public void instanciarListas() {
        grupos = Lists.newLinkedList();
        recursos = Lists.newLinkedList();
        hierarquiaOrganizacionals = Lists.newLinkedList();
        vinculoFPS = Lists.newLinkedList();
    }

    public BancoCreditoSalario getBancoCreditoSalario() {
        return bancoCreditoSalario;
    }

    public void setBancoCreditoSalario(BancoCreditoSalario bancoCreditoSalario) {
        this.bancoCreditoSalario = bancoCreditoSalario;
    }

    public Integer getNumeroRemessa() {
        return numeroRemessa;
    }

    public void setNumeroRemessa(Integer numeroRemessa) {
        this.numeroRemessa = numeroRemessa;
    }

    public List<FiltroCreditoSalario> getFiltroCreditoSalarios() {
        return filtroCreditoSalarios;
    }

    public void setFiltroCreditoSalarios(List<FiltroCreditoSalario> filtroCreditoSalarios) {
        this.filtroCreditoSalarios = filtroCreditoSalarios;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }
}

