package br.com.webpublico.entidades;

import br.com.webpublico.entidades.rh.arquivos.TipoSicapArquivo;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.TipoFolhaDePagamento;
import br.com.webpublico.enums.TipoFolhaDePagamentoSicap;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Criado por Mateus
 * Data: 24/05/2016.
 */
@GrupoDiagrama(nome = "Recursos Humanos")
@Entity
@Audited
@Etiqueta(value = "Sicap")
public class Sicap extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToOne
    @Etiqueta("Arquivo")
    private Arquivo arquivo;
    @Etiqueta("Tipo de Arquivo")
    @Tabelavel
    @Pesquisavel
    @NotAudited
    @OneToMany(mappedBy = "sicap", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TipoSicapArquivo> sicapTipoArquivo;
    @Temporal(TemporalType.TIMESTAMP)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Data de Geração")
    private Date dataGeracao;
    @Temporal(TemporalType.TIMESTAMP)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Data de Referência")
    private Date dataReferencia;
    @Etiqueta("Mês")
    @Pesquisavel
    @Tabelavel
    @Enumerated(EnumType.STRING)
    @Obrigatorio
    private Mes mes;
    @Etiqueta("Exercício")
    @Pesquisavel
    @Tabelavel
    @ManyToOne
    @Obrigatorio
    private Exercicio exercicio;
    @Obrigatorio
    @Etiqueta("Estabelecimento")
    @Tabelavel
    @Pesquisavel
    @ManyToOne
    private Entidade entidade;
    @Transient
    private List<HierarquiaOrganizacional> hierarquiasDaEntidade;
    @Transient
    private List<HierarquiaOrganizacional> hierarquiasBloqueadas;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "sicap", orphanRemoval = true)
    private List<SicapGrupoRecursoFP> grupos;
    @Transient
    private List<Long> folhasPagamentosRescisao;
    @Etiqueta("Sicap Folha de Pagamento")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "sicap")
    private List<SicapFolhaPagamento> sicapFolhasPagamentos;
    @Transient
    @Tabelavel
    @Etiqueta("Tipo de Folha")
    private TipoFolhaDePagamento tipoFolhaDePagamento;
    @Transient
    @Tabelavel
    @Etiqueta("Vesões da Folha")
    private String versoes;
    @Etiqueta("Tipo de Folha")
    @Enumerated(EnumType.STRING)
    private TipoFolhaDePagamentoSicap tipoFolhaDePagamentoSicap;

    public Sicap() {
        folhasPagamentosRescisao = Lists.newArrayList();
        sicapFolhasPagamentos = Lists.newArrayList();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Arquivo getArquivo() {
        return arquivo;
    }

    public void setArquivo(Arquivo arquivo) {
        this.arquivo = arquivo;
    }


    public List<TipoSicapArquivo> getSicapTipoArquivo() {
        return sicapTipoArquivo;
    }

    public void setSicapTipoArquivo(List<TipoSicapArquivo> sicapTipoArquivo) {
        this.sicapTipoArquivo = sicapTipoArquivo;
    }

    public Date getDataGeracao() {
        return dataGeracao;
    }

    public void setDataGeracao(Date dataGeracao) {
        this.dataGeracao = dataGeracao;
    }

    public Mes getMes() {
        return mes;
    }

    public void setMes(Mes mes) {
        this.mes = mes;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public Entidade getEntidade() {
        return entidade;
    }

    public void setEntidade(Entidade entidade) {
        this.entidade = entidade;
    }

    public Date getDataReferencia() {
        return dataReferencia;
    }

    public void setDataReferencia(Date dataReferencia) {
        this.dataReferencia = dataReferencia;
    }

    public List<HierarquiaOrganizacional> getHierarquiasDaEntidade() {
        return hierarquiasDaEntidade;
    }

    public void setHierarquiasDaEntidade(List<HierarquiaOrganizacional> hierarquiasDaEntidade) {
        this.hierarquiasDaEntidade = hierarquiasDaEntidade;
    }

    public List<HierarquiaOrganizacional> getHierarquiasBloqueadas() {
        return hierarquiasBloqueadas;
    }

    public void setHierarquiasBloqueadas(List<HierarquiaOrganizacional> hierarquiasBloqueadas) {
        this.hierarquiasBloqueadas = hierarquiasBloqueadas;
    }

    public List<GrupoRecursoFP> getGruposRecursos() {
        List<GrupoRecursoFP> gruposRecurso = Lists.newArrayList();
        if (grupos != null && !grupos.isEmpty()) {
            for (SicapGrupoRecursoFP grupo : grupos) {
                gruposRecurso.add(grupo.getGrupoRecursoFP());
            }
        }
        return gruposRecurso;
    }


    public List<SicapGrupoRecursoFP> getGrupos() {
        return grupos;
    }

    public void setGrupos(List<SicapGrupoRecursoFP> grupos) {
        this.grupos = grupos;
    }

    public Date getPrimeiroDiaDoMes() {
        if (getMes() != null && getExercicio() != null) {
            Calendar pd = Calendar.getInstance();
            pd.set(Calendar.DAY_OF_MONTH, 1);
            pd.set(Calendar.MONTH, this.getMes().getNumeroMes() - 1);
            pd.set(Calendar.YEAR, this.getExercicio().getAno());
            pd.setTime(Util.getDataHoraMinutoSegundoZerado(pd.getTime()));
            return pd.getTime();
        }
        return null;
    }

    public Date getUltimoDiaDoMes() {
        if (getMes() != null && getExercicio() != null) {
            Calendar ud = Calendar.getInstance();
            ud.set(Calendar.MONTH, this.getMes().getNumeroMes() - 1);
            ud.set(Calendar.DAY_OF_MONTH, ud.getActualMaximum(Calendar.DAY_OF_MONTH));
            ud.set(Calendar.YEAR, this.getExercicio().getAno());
            ud.setTime(Util.getDataHoraMinutoSegundoZerado(ud.getTime()));
            return ud.getTime();
        }
        return null;
    }

    public List<Long> getFolhasPagamentosRescisao() {
        return folhasPagamentosRescisao;
    }

    public void setFolhasPagamentosRescisao(List<Long> folhasPagamentosRescisao) {
        this.folhasPagamentosRescisao = folhasPagamentosRescisao;
    }

    public List<SicapFolhaPagamento> getSicapFolhasPagamentos() {
        return sicapFolhasPagamentos;
    }

    public void setSicapFolhasPagamentos(List<SicapFolhaPagamento> sicapFolhasPagamentos) {
        this.sicapFolhasPagamentos = sicapFolhasPagamentos;
    }

    public TipoFolhaDePagamento getTipoFolhaDePagamento() {
        return tipoFolhaDePagamento;
    }

    public void setTipoFolhaDePagamento(TipoFolhaDePagamento tipoFolhaDePagamento) {
        this.tipoFolhaDePagamento = tipoFolhaDePagamento;
    }

    public String getVersoes() {
        return versoes;
    }

    public void setVersoes(String versoes) {
        this.versoes = versoes;
    }

    public TipoFolhaDePagamentoSicap getTipoFolhaDePagamentoSicap() {
        return tipoFolhaDePagamentoSicap;
    }

    public void setTipoFolhaDePagamentoSicap(TipoFolhaDePagamentoSicap tipoFolhaDePagamentoSicap) {
        this.tipoFolhaDePagamentoSicap = tipoFolhaDePagamentoSicap;
    }

    @Override
    public String toString() {
        return mes + "/" + exercicio + " - " + sicapTipoArquivo;
    }
}
