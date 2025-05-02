package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoIRP;
import br.com.webpublico.enums.TipoApuracaoLicitacao;
import br.com.webpublico.enums.TipoSolicitacao;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by Desenvolvimento on 12/01/2016.
 */
@Entity
@Audited
@Etiqueta("Intenção de Registro de Preço")
public class IntencaoRegistroPreco extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Temporal(javax.persistence.TemporalType.DATE)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Data de Lançamento")
    private Date dataLancamento;

    @Tabelavel
    @Pesquisavel
    @Etiqueta("Número")
    private Long numero;

    @Obrigatorio
    @Etiqueta("Descrição")
    @Length(maximo = 255)
    @Tabelavel
    @Pesquisavel
    private String descricao;

    @Obrigatorio
    @Temporal(javax.persistence.TemporalType.DATE)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Início da Intenção")
    private Date inicioVigencia;

    @Obrigatorio
    @Temporal(javax.persistence.TemporalType.DATE)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Témino da Intenção")
    private Date fimVigencia;

    @Obrigatorio
    @Etiqueta("Notificação")
    @Length(maximo = 255)
    @Tabelavel
    @Pesquisavel
    private String notificacao;

    @Tabelavel
    @Pesquisavel
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Responsável")
    private UsuarioSistema responsavel;

    @Transient
    @Obrigatorio
    @Etiqueta("Unidade Gerenciadora")
    private HierarquiaOrganizacional unidadeGerenciadora;

    private Boolean habilitarNumeroParticipante;

    @Enumerated(EnumType.STRING)
    @Pesquisavel
    @Etiqueta("Situação")
    private SituacaoIRP situacaoIRP;

    @Pesquisavel
    @Etiqueta("Quantidade de Participantes")
    private Integer quantidadeParticipante;

    @Pesquisavel
    @Etiqueta("Tipo de Apuração")
    @Obrigatorio
    @Enumerated(EnumType.STRING)
    private TipoApuracaoLicitacao tipoApuracaoLicitacao;

    @Etiqueta("Tipo de Objeto")
    @Obrigatorio
    @Enumerated(EnumType.STRING)
    private TipoSolicitacao tipoObjeto;

    @Invisivel
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "intencaoRegistroDePreco", orphanRemoval = true)
    private List<ParticipanteIRP> participantes;

    @Invisivel
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "intencaoRegistroPreco", orphanRemoval = true)
    private List<LoteIntencaoRegistroPreco> lotes;

    @Invisivel
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "intencaoRegistroPreco", orphanRemoval = true)
    private List<UnidadeIRP> unidades;

    public IntencaoRegistroPreco() {
        this.situacaoIRP = SituacaoIRP.EM_ELABORACAO;
        this.participantes = Lists.newArrayList();
        this.lotes = Lists.newArrayList();
        this.unidades = Lists.newArrayList();
        this.habilitarNumeroParticipante = Boolean.FALSE;
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    public Date getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(Date dataLancamento) {
        this.dataLancamento = dataLancamento;
    }

    public Boolean getHabilitarNumeroParticipante() {
        return habilitarNumeroParticipante;
    }

    public void setHabilitarNumeroParticipante(Boolean habilitarNumeroParticipante) {
        this.habilitarNumeroParticipante = habilitarNumeroParticipante;
    }

    public SituacaoIRP getSituacaoIRP() {
        return situacaoIRP;
    }

    public void setSituacaoIRP(SituacaoIRP situacaoIRP) {
        this.situacaoIRP = situacaoIRP;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Date getFimVigencia() {
        return fimVigencia;
    }

    public void setFimVigencia(Date fimVigencia) {
        this.fimVigencia = fimVigencia;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public String getNotificacao() {
        return notificacao;
    }

    public void setNotificacao(String notificacao) {
        this.notificacao = notificacao;
    }

    public UsuarioSistema getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(UsuarioSistema responsavel) {
        this.responsavel = responsavel;
    }

    public List<ParticipanteIRP> getParticipantes() {
        return participantes;
    }

    public void setParticipantes(List<ParticipanteIRP> participantes) {
        this.participantes = participantes;
    }

    public List<LoteIntencaoRegistroPreco> getLotes() {
        return lotes;
    }

    public void setLotes(List<LoteIntencaoRegistroPreco> lotes) {
        this.lotes = lotes;
    }

    public HierarquiaOrganizacional getUnidadeGerenciadora() {
        return unidadeGerenciadora;
    }

    public void setUnidadeGerenciadora(HierarquiaOrganizacional unidadeGerenciadora) {
        this.unidadeGerenciadora = unidadeGerenciadora;
    }

    public List<UnidadeIRP> getUnidades() {
        return unidades;
    }

    public void setUnidades(List<UnidadeIRP> unidades) {
        this.unidades = unidades;
    }

    public Integer getQuantidadeParticipante() {
        return quantidadeParticipante;
    }

    public void setQuantidadeParticipante(Integer quantidadeParticipante) {
        this.quantidadeParticipante = quantidadeParticipante;
    }

    public ParticipanteIRP getParticipanteGerenciador() {
        if (hasParticipantes()) {
            for (ParticipanteIRP part : participantes) {
                if (part.getGerenciador()) {
                    return part;
                }
            }
        }
        return null;
    }

    public boolean hasMaisDeUmParticipante() {
        return hasParticipantes() && participantes.size() > 1;
    }

    @Override
    public String toString() {
        return numero + " - " + DataUtil.getDataFormatada(dataLancamento) + " - " + descricao;
    }

    public Integer getQuantidadeParticipantesCadastrados() {
        return getParticipantes().size();
    }

    public TipoApuracaoLicitacao getTipoApuracaoLicitacao() {
        return tipoApuracaoLicitacao;
    }

    public void setTipoApuracaoLicitacao(TipoApuracaoLicitacao tipoApuracaoLicitacao) {
        this.tipoApuracaoLicitacao = tipoApuracaoLicitacao;
    }

    public TipoSolicitacao getTipoObjeto() {
        return tipoObjeto;
    }

    public void setTipoObjeto(TipoSolicitacao tipoObjeto) {
        this.tipoObjeto = tipoObjeto;
    }

    public boolean isTipoApuracaoPorItem() {
        return TipoApuracaoLicitacao.ITEM.equals(tipoApuracaoLicitacao);
    }

    public boolean isTipoApuracaoPorLote() {
        return TipoApuracaoLicitacao.LOTE.equals(tipoApuracaoLicitacao);
    }


    public boolean hasParticipantes() {
        return participantes != null && !participantes.isEmpty();
    }
}
