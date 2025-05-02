package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoProcessoDebito;
import br.com.webpublico.enums.TipoDiferencaContaCorrente;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@GrupoDiagrama(nome = "Tributario")
@Audited
@Etiqueta("Crédito em Conta Corrente")
public class CreditoContaCorrente extends SuperEntidade implements Serializable {

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
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "creditoContaCorrente")
    private List<ItemCreditoContaCorrente> itens = new ArrayList<>();
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
    @OneToMany(mappedBy = "creditoContaCorrente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CreditoContaCorrenteArquivo> arquivos;
    @ManyToOne
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Contribuinte")
    private Pessoa pessoa;
    @Enumerated(EnumType.STRING)
    private TipoDiferencaContaCorrente tipoDiferencaContaCorrente;

    public CreditoContaCorrente() {
        itens = new ArrayList<ItemCreditoContaCorrente>();
        arquivos = Lists.newArrayList();
        tipoDiferencaContaCorrente = null;
    }

    public CreditoContaCorrente(Long id,
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

    public CreditoContaCorrente(Long id,
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

    public List<ItemCreditoContaCorrente> getItens() {
        return itens;
    }

    public void setItens(List<ItemCreditoContaCorrente> itens) {
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

    public List<CreditoContaCorrenteArquivo> getArquivos() {
        return arquivos;
    }

    public void setArquivos(List<CreditoContaCorrenteArquivo> arquivos) {
        this.arquivos = arquivos;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public TipoDiferencaContaCorrente getTipoDiferencaContaCorrente() {
        return tipoDiferencaContaCorrente;
    }

    public void setTipoDiferencaContaCorrente(TipoDiferencaContaCorrente tipoDiferencaContaCorrente) {
        this.tipoDiferencaContaCorrente = tipoDiferencaContaCorrente;
    }
}
