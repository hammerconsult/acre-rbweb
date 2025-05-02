package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoProcessoDebito;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@GrupoDiagrama(nome = "Tributario")
@Audited
@Etiqueta("Processo de Compensação de Débitos")
public class Compensacao extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Exercício")
    private Exercicio exercicio;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Código")
    private Long codigo;
    @Temporal(TemporalType.TIMESTAMP)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Data de Lançamento")
    private Date lancamento;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Número do Protocolo")
    private String numeroProtocolo;
    private String motivo;
    @ManyToOne
    private UsuarioSistema usuarioIncluiu;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "compensacao")
    private List<ItemCompensacao> itens = new ArrayList<>();
    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Situação")
    private SituacaoProcessoDebito situacao;
    @Temporal(TemporalType.DATE)
    private Date dataEstorno;
    private String motivoEstorno;
    @ManyToOne
    @Etiqueta("Ato Legal")
    private AtoLegal atoLegal;
    @Etiqueta("Arquivos")
    @OneToMany(mappedBy = "compensacao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CompensacaoArquivo> arquivos;
    @ManyToOne
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Contribuinte")
    private Pessoa pessoa;
    @Temporal(TemporalType.DATE)
    private Date dataCompensacao;
    @Temporal(TemporalType.DATE)
    private Date dataBloqueio;
    private BigDecimal valorCreditoCompensar;
    private BigDecimal valorCompensar;
    private BigDecimal valorCompensado;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "compensacao")
    private List<CreditoCompensacao> creditos = new ArrayList<>();

    public Compensacao() {
        itens = new ArrayList<ItemCompensacao>();
        creditos = new ArrayList<CreditoCompensacao>();
        arquivos = Lists.newArrayList();
    }

    public Compensacao(Long id,
                       Long codigo,
                       Pessoa pessoa,
                       Date lancamento,
                       String numeroProtocolo,
                       SituacaoProcessoDebito situacao) {
        this.id = id;
        this.codigo = codigo;
        this.pessoa = pessoa;
        this.lancamento = lancamento;
        this.numeroProtocolo = numeroProtocolo;
        this.situacao = situacao;
    }

    public Compensacao(Long id,
                       Long codigo,
                       Pessoa pessoa,
                       Date lancamento,
                       String numeroProtocolo,
                       SituacaoProcessoDebito situacao,
                       Integer ano) {
        this.id = id;
        this.codigo = codigo;
        this.pessoa = pessoa;
        this.lancamento = lancamento;
        this.numeroProtocolo = numeroProtocolo;
        this.situacao = situacao;
    }

    public SituacaoProcessoDebito getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoProcessoDebito situacao) {
        this.situacao = situacao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public List<ItemCompensacao> getItens() {
        return itens;
    }

    public void setItens(List<ItemCompensacao> itens) {
        this.itens = itens;
    }

    public UsuarioSistema getUsuarioIncluiu() {
        return usuarioIncluiu;
    }

    public void setUsuarioIncluiu(UsuarioSistema usuarioIncluiu) {
        this.usuarioIncluiu = usuarioIncluiu;
    }

    public Date getLancamento() {
        return lancamento;
    }

    public void setLancamento(Date lancamento) {
        this.lancamento = lancamento;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getNumeroProtocolo() {
        return numeroProtocolo;
    }

    public void setNumeroProtocolo(String numeroProtocolo) {
        this.numeroProtocolo = numeroProtocolo;
    }

    public Date getDataEstorno() {
        return dataEstorno;
    }

    public void setDataEstorno(Date dataEstorno) {
        this.dataEstorno = dataEstorno;
    }

    public String getMotivoEstorno() {
        return motivoEstorno;
    }

    public void setMotivoEstorno(String motivoEstorno) {
        this.motivoEstorno = motivoEstorno;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public AtoLegal getAtoLegal() {
        return atoLegal;
    }

    public void setAtoLegal(AtoLegal atoLegal) {
        this.atoLegal = atoLegal;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public List<CompensacaoArquivo> getArquivos() {
        return arquivos;
    }

    public void setArquivos(List<CompensacaoArquivo> arquivos) {
        this.arquivos = arquivos;
    }

    public Date getDataCompensacao() {
        return dataCompensacao;
    }

    public void setDataCompensacao(Date dataCompensacao) {
        this.dataCompensacao = dataCompensacao;
    }

    public Date getDataBloqueio() {
        return dataBloqueio;
    }

    public void setDataBloqueio(Date dataBloqueio) {
        this.dataBloqueio = dataBloqueio;
    }

    public BigDecimal getValorCreditoCompensar() {
        return valorCreditoCompensar != null ? valorCreditoCompensar : BigDecimal.ZERO;
    }

    public void setValorCreditoCompensar(BigDecimal valorCreditoCompensar) {
        this.valorCreditoCompensar = valorCreditoCompensar;
    }

    public BigDecimal getValorCompensar() {
        return valorCompensar != null ? valorCompensar : BigDecimal.ZERO;
    }

    public void setValorCompensar(BigDecimal valorCompensar) {
        this.valorCompensar = valorCompensar;
    }

    public BigDecimal getValorCompensado() {
        return valorCompensado != null ? valorCompensado : BigDecimal.ZERO;
    }

    public void setValorCompensado(BigDecimal valorCompensado) {
        this.valorCompensado = valorCompensado;
    }

    public List<CreditoCompensacao> getCreditos() {
        return creditos;
    }

    public void setCreditos(List<CreditoCompensacao> creditos) {
        this.creditos = creditos;
    }
}
