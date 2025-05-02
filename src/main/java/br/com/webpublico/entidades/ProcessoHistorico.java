package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoProcesso;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

@GrupoDiagrama(nome = "Protocolo")
@Etiqueta("Histórico Protocolo")
@Entity
@Audited
public class ProcessoHistorico extends SuperEntidade implements Comparable<ProcessoHistorico> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    @Etiqueta("Data do Histórico")
    private Date dataHora;

    @ManyToOne
    @Etiqueta("Usuário")
    private UsuarioSistema usuarioSistema;

    @ManyToOne
    @Etiqueta("Unidade Organizacional")
    private UnidadeOrganizacional unidadeOrganizacional;

    @Transient
    @Etiqueta("Unidade Organizacional")
    private HierarquiaOrganizacional hierarquiaOrganizacional;

    @Etiqueta("Situação Processo")
    @Enumerated(EnumType.STRING)
    private SituacaoProcesso situacaoProcesso;

    @Etiqueta("Descrição")
    private String descricao;

    @ManyToOne
    @Etiqueta("Processo")
    private Processo processo;

    @Etiqueta("Unidade de Encaminhamento Externa")
    private String unidadeExterna;

    public ProcessoHistorico() {
    }

    public ProcessoHistorico(UsuarioSistema usuarioSistema, SituacaoProcesso situacaoProcesso, String descricao, Processo processo, UnidadeOrganizacional uo) {
        this.dataHora = new Date();
        this.usuarioSistema = usuarioSistema;
        this.situacaoProcesso = situacaoProcesso;
        this.descricao = descricao;
        this.processo = processo;
        this.unidadeOrganizacional = uo;
    }

    public ProcessoHistorico(UsuarioSistema usuarioSistema, SituacaoProcesso situacaoProcesso, String descricao, Processo processo, String unidadeExterna) {
        this.dataHora = new Date();
        this.usuarioSistema = usuarioSistema;
        this.situacaoProcesso = situacaoProcesso;
        this.descricao = descricao;
        this.processo = processo;
        this.unidadeExterna = unidadeExterna;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataHora() {
        return dataHora;
    }

    public void setDataHora(Date dataHora) {
        this.dataHora = dataHora;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public SituacaoProcesso getSituacaoProcesso() {
        return situacaoProcesso;
    }

    public void setSituacaoProcesso(SituacaoProcesso situacaoProcesso) {
        this.situacaoProcesso = situacaoProcesso;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Processo getProcesso() {
        return processo;
    }

    public void setProcesso(Processo processo) {
        this.processo = processo;
    }

    public String getUnidadeExterna() {
        return unidadeExterna;
    }

    public void setUnidadeExterna(String unidadeExterna) {
        this.unidadeExterna = unidadeExterna;
    }

    @Override
    public int compareTo(ProcessoHistorico o) {
        return this.dataHora.compareTo(o.getDataHora());
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }
}
