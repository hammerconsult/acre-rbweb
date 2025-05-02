package br.com.webpublico.entidades;

import br.com.webpublico.entidadesauxiliares.VOSituacaoParcela;
import br.com.webpublico.enums.SituacaoProcessoRevisaoDividaAtiva;
import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity
@GrupoDiagrama(nome = "Tributario")
@Audited
@Etiqueta("Processo de Revisão de Dívida Ativa")
public class ProcessoRevisaoDividaAtiva extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Código")
    private Long codigo;
    @ManyToOne
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Exercício")
    private Exercicio exercicio;
    @Temporal(TemporalType.TIMESTAMP)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Data de Lançamento")
    private Date lancamento;
    @Etiqueta("Número do Protocolo")
    private String numeroProtocolo;
    @Etiqueta("Ano do Protocolo")
    private String anoProtocolo;
    @ManyToOne
    private UsuarioSistema usuarioSistema;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "processoRevisao")
    private List<ItemProcessoRevisaoDividaAtiva> itens;
    @Enumerated(EnumType.STRING)
    private TipoCadastroTributario tipoCadastro;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Situação")
    @Enumerated(EnumType.STRING)
    private SituacaoProcessoRevisaoDividaAtiva situacaoProcesso;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Cadastro")
    @ManyToOne
    private Cadastro cadastro;
    @ManyToOne
    private Pessoa pessoa;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Dívida")
    @ManyToOne
    private Divida divida;
    @OneToMany(mappedBy = "processoRevisaoDividaAtiva", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProcessoRevisaoDividaAtivaExercicio> exerciciosDebitos;
    private String motivo;
    @Transient
    private Set<VOSituacaoParcela> situacoesDAEmRevisao;

    public ProcessoRevisaoDividaAtiva() {
        super();
        itens = Lists.newArrayList();
        exerciciosDebitos = Lists.newArrayList();
        situacoesDAEmRevisao = Sets.newHashSet();
    }

    public ProcessoRevisaoDividaAtiva(Long id, Exercicio exercicio, Long codigo, Date lancamento, SituacaoProcessoRevisaoDividaAtiva situacaoProcesso, Cadastro cadastro, Pessoa pessoa, Divida divida) {
        this();
        this.id = id;
        this.exercicio = exercicio;
        this.codigo = codigo;
        this.lancamento = lancamento;
        this.situacaoProcesso = situacaoProcesso;
        this.cadastro = cadastro;
        this.pessoa = pessoa;
        this.divida = divida;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCriadoEm() {
        return this.criadoEm;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public List<ItemProcessoRevisaoDividaAtiva> getItens() {
        return itens;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setItens(List<ItemProcessoRevisaoDividaAtiva> itens) {
        this.itens = itens;
    }

    public Date getLancamento() {
        return lancamento;
    }

    public void setLancamento(Date lancamento) {
        this.lancamento = lancamento;
    }

    public String getNumeroProtocolo() {
        return numeroProtocolo;
    }

    public void setNumeroProtocolo(String numeroProtocolo) {
        this.numeroProtocolo = numeroProtocolo;
    }

    public String getAnoProtocolo() {
        return anoProtocolo;
    }

    public void setAnoProtocolo(String anoProtocolo) {
        this.anoProtocolo = anoProtocolo;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public TipoCadastroTributario getTipoCadastro() {
        return tipoCadastro;
    }

    public void setTipoCadastro(TipoCadastroTributario tipoCadastro) {
        this.tipoCadastro = tipoCadastro;
    }

    public Cadastro getCadastro() {
        return cadastro;
    }

    public void setCadastro(Cadastro cadastro) {
        this.cadastro = cadastro;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public Divida getDivida() {
        return divida;
    }

    public void setDivida(Divida divida) {
        this.divida = divida;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public SituacaoProcessoRevisaoDividaAtiva getSituacaoProcesso() {
        return situacaoProcesso;
    }

    public void setSituacaoProcesso(SituacaoProcessoRevisaoDividaAtiva situacaoProcesso) {
        this.situacaoProcesso = situacaoProcesso;
    }

    public List<ProcessoRevisaoDividaAtivaExercicio> getExerciciosDebitos() {
        return exerciciosDebitos;
    }

    public void setExerciciosDebitos(List<ProcessoRevisaoDividaAtivaExercicio> exerciciosDebitos) {
        this.exerciciosDebitos = exerciciosDebitos;
    }

    public Set<VOSituacaoParcela> getSituacoesDAEmRevisao() {
        return situacoesDAEmRevisao;
    }

    public void setSituacoesDAEmRevisao(Set<VOSituacaoParcela> situacoesDAEmRevisao) {
        this.situacoesDAEmRevisao = situacoesDAEmRevisao;
    }
}
