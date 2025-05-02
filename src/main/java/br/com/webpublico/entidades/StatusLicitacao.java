/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoSituacaoLicitacao;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

/**
 * @author renato
 */
@Entity
@Audited
@GrupoDiagrama(nome = "Status da Licitação")
@Etiqueta("Status da Licitação")
public class StatusLicitacao extends SuperEntidade {


    public static final TipoSituacaoLicitacao[] tipoParaBloqueiosLicitacao = {
        TipoSituacaoLicitacao.ANULADA,
        TipoSituacaoLicitacao.DESERTA,
        TipoSituacaoLicitacao.REVOGADA,
        TipoSituacaoLicitacao.FRACASSADA,
    };
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Licitação")
    @ManyToOne
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    private Licitacao licitacao;
    @Etiqueta("Número")
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    private Long numero;
    @Etiqueta("Status")
    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    private TipoSituacaoLicitacao tipoSituacaoLicitacao;
    @Etiqueta("Data do Status")
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataStatus;
    @Etiqueta("Motivo do Status")
    @Obrigatorio
    private String motivoStatusLicitacao;
    @Etiqueta("Responsável")
    @OneToOne
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    private UsuarioSistema responsavel;
    @OneToOne(mappedBy = "statusLicitacao", cascade = CascadeType.ALL, orphanRemoval = true)
    private RecursoStatus recursoStatus;
    @Etiqueta("Data da Publicação")
    @Temporal(TemporalType.DATE)
    private Date dataPublicacao;
    @Etiqueta("Veículo de Publicação")
    @ManyToOne
    private VeiculoDePublicacao veiculoDePublicacao;
    @Etiqueta("Número da Edição")
    private String numeroEdicao;
    @Etiqueta("Página da Publicação")
    private String paginaPublicacao;

    public StatusLicitacao() {
        this.dataStatus = new Date();
    }

    public static String concatenarListaStatus() {
        String concat = "";
        for (TipoSituacaoLicitacao tipoSituacaoLicitacao : tipoParaBloqueiosLicitacao) {
            concat += "'" + tipoSituacaoLicitacao.name() + "',";
        }
        return concat.substring(0, concat.length() - 1);
    }

    public Licitacao getLicitacao() {
        return licitacao;
    }

    public void setLicitacao(Licitacao licitacao) {
        this.licitacao = licitacao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoSituacaoLicitacao getTipoSituacaoLicitacao() {
        return tipoSituacaoLicitacao;
    }

    public void setTipoSituacaoLicitacao(TipoSituacaoLicitacao tipoSituacaoLicitacao) {
        this.tipoSituacaoLicitacao = tipoSituacaoLicitacao;
    }

    public Date getDataStatus() {
        return dataStatus;
    }

    public void setDataStatus(Date dataStatus) {
        this.dataStatus = dataStatus;
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    public String getMotivoStatusLicitacao() {
        return motivoStatusLicitacao;
    }

    public void setMotivoStatusLicitacao(String motivoStatusLicitacao) {
        this.motivoStatusLicitacao = motivoStatusLicitacao;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public UsuarioSistema getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(UsuarioSistema responsavel) {
        this.responsavel = responsavel;
    }

    public RecursoStatus getRecursoStatus() {
        return recursoStatus;
    }

    public void setRecursoStatus(RecursoStatus recursoStatus) {
        this.recursoStatus = recursoStatus;
    }

    public Date getDataPublicacao() {
        return dataPublicacao;
    }

    public void setDataPublicacao(Date dataPublicacao) {
        this.dataPublicacao = dataPublicacao;
    }

    public VeiculoDePublicacao getVeiculoDePublicacao() {
        return veiculoDePublicacao;
    }

    public void setVeiculoDePublicacao(VeiculoDePublicacao veiculoDePublicacao) {
        this.veiculoDePublicacao = veiculoDePublicacao;
    }

    public String getNumeroEdicao() {
        return numeroEdicao;
    }

    public void setNumeroEdicao(String numeroEdicao) {
        this.numeroEdicao = numeroEdicao;
    }

    public String getPaginaPublicacao() {
        return paginaPublicacao;
    }

    public void setPaginaPublicacao(String paginaPublicacao) {
        this.paginaPublicacao = paginaPublicacao;
    }

    @Override
    public String toString() {
        return this.getNumero() + "  -  " + this.tipoSituacaoLicitacao.getDescricao();
    }

    public boolean isTipoAdjudicada() {
        return TipoSituacaoLicitacao.ADJUDICADA.equals(this.tipoSituacaoLicitacao);
    }

    public boolean isTipoAndamento() {
        return TipoSituacaoLicitacao.ANDAMENTO.equals(tipoSituacaoLicitacao);
    }

    public boolean isTipoAnulada() {
        return TipoSituacaoLicitacao.ANULADA.equals(tipoSituacaoLicitacao);
    }

    public boolean isTipoCancelada() {
        return TipoSituacaoLicitacao.CANCELADA.equals(tipoSituacaoLicitacao);
    }

    public boolean isTipoDeserta() {
        return TipoSituacaoLicitacao.DESERTA.equals(tipoSituacaoLicitacao);
    }

    public boolean isTipoEmRecurso() {
        return TipoSituacaoLicitacao.EM_RECURSO.equals(tipoSituacaoLicitacao);
    }

    public boolean isTipoFracassada() {
        return TipoSituacaoLicitacao.FRACASSADA.equals(tipoSituacaoLicitacao);
    }

    public boolean isTipoHomologada() {
        return TipoSituacaoLicitacao.HOMOLOGADA.equals(tipoSituacaoLicitacao);
    }

    public boolean isTipoProgramada() {
        return TipoSituacaoLicitacao.PRORROGADA.equals(tipoSituacaoLicitacao);
    }

    public boolean isTipoRevogada() {
        return TipoSituacaoLicitacao.REVOGADA.equals(tipoSituacaoLicitacao);
    }

    public boolean isTipoSuspensa() {
        return TipoSituacaoLicitacao.SUSPENSA.equals(tipoSituacaoLicitacao);
    }
}
