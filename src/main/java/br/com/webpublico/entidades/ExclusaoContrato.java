package br.com.webpublico.entidades;


import br.com.webpublico.enums.SituacaoMovimentoAdministrativo;
import br.com.webpublico.enums.TipoAquisicaoContrato;
import br.com.webpublico.enums.TipoExclusaoContrato;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;

import javax.persistence.*;
import java.util.Date;

@Entity
@Etiqueta("Exclusão de Contrato")
public class ExclusaoContrato extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Etiqueta("Id Movimento")
    private Long idMovimento;

    @Etiqueta("Número")
    private Long numero;

    @Temporal(TemporalType.DATE)
    @Etiqueta("Data de Exclusão ")
    private Date dataExclusao;

    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo de Contrato")
    private TipoAquisicaoContrato tipoContrato;

    @Enumerated(EnumType.STRING)
    @Etiqueta("Situação")
    private SituacaoMovimentoAdministrativo situacao;

    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo de Exclusão")
    private TipoExclusaoContrato tipoExclusao;

    @Obrigatorio
    @Etiqueta("Histórico")
    private String historico;

    @ManyToOne
    @Etiqueta("Usuário")
    private UsuarioSistema usuarioSistema;

    private Boolean somenteRemoverPortal;

    public SituacaoMovimentoAdministrativo getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoMovimentoAdministrativo situacao) {
        this.situacao = situacao;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdMovimento() {
        return idMovimento;
    }

    public void setIdMovimento(Long idContrato) {
        this.idMovimento = idContrato;
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    public Date getDataExclusao() {
        return dataExclusao;
    }

    public void setDataExclusao(Date dataExclusao) {
        this.dataExclusao = dataExclusao;
    }

    public String getHistorico() {
        return historico;
    }

    public void setHistorico(String historico) {
        this.historico = historico;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public TipoAquisicaoContrato getTipoContrato() {
        return tipoContrato;
    }

    public void setTipoContrato(TipoAquisicaoContrato tipoContrato) {
        this.tipoContrato = tipoContrato;
    }

    public TipoExclusaoContrato getTipoExclusao() {
        return tipoExclusao;
    }

    public void setTipoExclusao(TipoExclusaoContrato tipoExclusao) {
        this.tipoExclusao = tipoExclusao;
    }

    public Boolean getSomenteRemoverPortal() {
        if (somenteRemoverPortal == null) {
            somenteRemoverPortal = Boolean.FALSE;
        }
        return somenteRemoverPortal;
    }

    public void setSomenteRemoverPortal(Boolean somenteRemoverPortal) {
        this.somenteRemoverPortal = somenteRemoverPortal;
    }

    public boolean isEmElaboracao() {
        return SituacaoMovimentoAdministrativo.EM_ELABORACAO.equals(situacao);
    }

    public boolean isTipoExclusaoContrato() {
        return TipoExclusaoContrato.CONTRATO.equals(tipoExclusao);
    }

    public boolean isTipoExclusaoExecucaoContrato() {
        return TipoExclusaoContrato.EXECUCAO_CONTRATO.equals(tipoExclusao);
    }

    public boolean isTipoExclusaoAlteracaoContratual() {
        return isTipoExclusaoAditivo() || isTipoExclusaoApostilamento();
    }

    public boolean isTipoExclusaoAditivo() {
        return TipoExclusaoContrato.ADITIVO.equals(tipoExclusao);
    }

    public boolean isTipoExclusaoApostilamento() {
        return TipoExclusaoContrato.APOSTILAMENTO.equals(tipoExclusao);
    }

}
