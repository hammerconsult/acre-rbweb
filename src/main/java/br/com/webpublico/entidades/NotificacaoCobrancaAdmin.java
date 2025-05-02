package br.com.webpublico.entidades;

import br.com.webpublico.enums.AgrupadorCobrancaAdm;
import br.com.webpublico.enums.TipoAcaoCobranca;
import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.enums.TipoSituacaoProgramacaoCobranca;
import br.com.webpublico.util.IdentidadeDaEntidade;
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

/**
 * Created with IntelliJ IDEA.
 * User: Wanderley
 * Date: 29/11/13
 * Time: 14:51
 * To change this template use File | Settings | File Templates.
 */

@Entity
@Cacheable
@Audited
@Etiqueta("Notificação de Cobrança Administrativa")
public class NotificacaoCobrancaAdmin implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Invisivel
    private Long id;
    @Etiqueta("Programação de Cobrança")
    @Tabelavel(ordemApresentacao = 1)
    @Pesquisavel
    @OneToOne
    private ProgramacaoCobranca programacaoCobranca;
    @Tabelavel(ordemApresentacao = 2)
    @Etiqueta("Descrição da Programação")
    @Transient
    private String descricaoProgramacao;
    @Etiqueta("Tipo de Ação de Cobrança")
    @Tabelavel(ordemApresentacao = 3)
    @Enumerated(EnumType.STRING)
    private TipoAcaoCobranca tipoAcaoCobranca;
    @Pesquisavel
    @Tabelavel(ordemApresentacao = 4)
    @Etiqueta("Cobrança com Aceite")
    private Boolean aceite;
    @Etiqueta("Cobrança com Guia")
    @Pesquisavel
    @Tabelavel(ordemApresentacao = 5)
    private Boolean emitirGuia;
    @Etiqueta("DAM Agrupado")
    @Tabelavel(ordemApresentacao = 6)
    @Pesquisavel
    private Boolean damAgrupado;
    @Enumerated(EnumType.STRING)
    private AgrupadorCobrancaAdm agrupado;
    @OneToMany(mappedBy = "notificacaoADM", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemNotificacao> itemNotificacaoLista;
    @Etiqueta("Vencimento do DAM")
    @Tabelavel(ordemApresentacao = 7, ALINHAMENTO = Tabelavel.ALINHAMENTO.CENTRO)
    @Pesquisavel
    @Temporal(TemporalType.DATE)
    private Date vencimentoDam;
    @Invisivel
    @Transient
    private Long criadoEm;
    @Etiqueta("Situação")
    @Enumerated
    @Transient
    @Tabelavel(ordemApresentacao = 8, ALINHAMENTO = Tabelavel.ALINHAMENTO.CENTRO)
    private TipoSituacaoProgramacaoCobranca situacao;
    @Etiqueta("Tipo de Cadastro")
    @Enumerated
    @Transient
    @Tabelavel(ordemApresentacao = 9, ALINHAMENTO = Tabelavel.ALINHAMENTO.CENTRO)
    private TipoCadastroTributario tipoCadastroTributario;
    @Etiqueta("Vencimento da Notificação")
    @Pesquisavel
    @Tabelavel(ordemApresentacao = 10, ALINHAMENTO = Tabelavel.ALINHAMENTO.CENTRO)
    @Temporal(TemporalType.DATE)
    private Date vencimentoNotificacao;


    public NotificacaoCobrancaAdmin() {
        this.criadoEm = System.currentTimeMillis();
        this.itemNotificacaoLista = Lists.newArrayList();
    }

    public NotificacaoCobrancaAdmin(Long id, TipoSituacaoProgramacaoCobranca situacao, NotificacaoCobrancaAdmin notificacao) {
        this.id = id;
        this.situacao = situacao;
        this.programacaoCobranca = notificacao.getProgramacaoCobranca();
        this.emitirGuia = notificacao.getEmitirGuia();
        this.agrupado = notificacao.getAgrupado();
        this.aceite = notificacao.getAceite();
        this.vencimentoDam = notificacao.getVencimentoDam();
        this.damAgrupado = notificacao.getDamAgrupado();
        this.tipoAcaoCobranca = notificacao.getTipoAcaoCobranca();
        this.tipoCadastroTributario = notificacao.programacaoCobranca.getTipoCadastro();
        this.vencimentoNotificacao = notificacao.getVencimentoNotificacao();
        this.descricaoProgramacao = programacaoCobranca.getDescricao();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getAceite() {
        return aceite != null ? aceite : Boolean.FALSE;
    }

    public void setAceite(Boolean aceite) {
        this.aceite = aceite;
    }

    public AgrupadorCobrancaAdm getAgrupado() {
        return agrupado;
    }

    public void setAgrupado(AgrupadorCobrancaAdm agrupado) {
        this.agrupado = agrupado;
    }

    public Boolean getEmitirGuia() {
        return emitirGuia != null ? emitirGuia : Boolean.FALSE;
    }

    public void setEmitirGuia(Boolean emitirGuia) {
        this.emitirGuia = emitirGuia;
    }

    public List<ItemNotificacao> getItemNotificacaoLista() {
        return itemNotificacaoLista;
    }

    public void setItemNotificacaoLista(List<ItemNotificacao> itemNotificacaoLista) {
        this.itemNotificacaoLista = itemNotificacaoLista;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public ProgramacaoCobranca getProgramacaoCobranca() {
        return programacaoCobranca;
    }

    public void setProgramacaoCobranca(ProgramacaoCobranca programacaoCobranca) {
        this.programacaoCobranca = programacaoCobranca;
    }

    public Boolean getDamAgrupado() {
        return damAgrupado != null ? damAgrupado : Boolean.FALSE;
    }

    public void setDamAgrupado(Boolean damAgrupado) {
        this.damAgrupado = damAgrupado;
    }

    public Date getVencimentoDam() {
        return vencimentoDam;
    }

    public void setVencimentoDam(Date vencimentoDam) {
        this.vencimentoDam = vencimentoDam;
    }

    public TipoSituacaoProgramacaoCobranca getSituacao() {
        return situacao;
    }

    public void setSituacao(TipoSituacaoProgramacaoCobranca situacao) {
        this.situacao = situacao;
    }

    public TipoAcaoCobranca getTipoAcaoCobranca() {
        return tipoAcaoCobranca;
    }

    public void setTipoAcaoCobranca(TipoAcaoCobranca tipoAcaoCobranca) {
        this.tipoAcaoCobranca = tipoAcaoCobranca;
    }

    @Override
    public boolean equals(Object obj) {
        return IdentidadeDaEntidade.calcularEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    public Date getVencimentoNotificacao() {
        return vencimentoNotificacao;
    }

    public void setVencimentoNotificacao(Date vencimentoNotificacao) {
        this.vencimentoNotificacao = vencimentoNotificacao;
    }
}
