package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoProcessoDebito;
import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity

@Audited
@Etiqueta("Pagamento Judicial")
public class PagamentoJudicial  implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Transient
    private Long criadoEm;
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
    @Etiqueta("Data da Compensação")
    private Date dataCompensacao;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Número do Protocolo")
    private String numeroProtocolo;
    @ManyToOne
    private Pessoa pessoa;
    @ManyToOne
    private Cadastro cadastro;
    private String motivo;
    @ManyToOne
    private UsuarioSistema usuario;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "pagamentoJudicial")
    private List<PagamentoJudicialParcela> parcelas;
    @Enumerated(EnumType.STRING)
    @Etiqueta("Situação")
    @Pesquisavel
    @Tabelavel
    private SituacaoProcessoDebito situacao;
    private BigDecimal valorCompensado;
    private BigDecimal valorACompensar;
    @Enumerated(EnumType.STRING)
    private TipoCadastroTributario tipoCadastroTributario;
    @Temporal(TemporalType.DATE)
    private Date dataBloqueio;
    private BigDecimal saldo;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataEstorno;
    private String motivoEstorno;


    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public Date getDataCompensacao() {
        return dataCompensacao;
    }

    public void setDataCompensacao(Date dataCompensacao) {
        this.dataCompensacao = dataCompensacao;
    }

    public String getNumeroProtocolo() {
        return numeroProtocolo;
    }

    public TipoCadastroTributario getTipoCadastroTributario() {
        return tipoCadastroTributario;
    }

    public void setTipoCadastroTributario(TipoCadastroTributario tipoCadastroTributario) {
        this.tipoCadastroTributario = tipoCadastroTributario;
    }

    public void setNumeroProtocolo(String numeroProtocolo) {
        this.numeroProtocolo = numeroProtocolo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public UsuarioSistema getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioSistema usuario) {
        this.usuario = usuario;
    }

    public List<PagamentoJudicialParcela> getParcelas() {
        return parcelas;
    }

    public void setParcelas(List<PagamentoJudicialParcela> parcelas) {
        this.parcelas = parcelas;
    }

    public BigDecimal getValorCompensado() {
        return valorCompensado;
    }

    public void setValorCompensado(BigDecimal valorCompensado) {
        this.valorCompensado = valorCompensado;
    }

    public BigDecimal getValorACompensar() {
        return valorACompensar;
    }

    public void setValorACompensar(BigDecimal valorACompensar) {
        this.valorACompensar = valorACompensar;
    }

    public SituacaoProcessoDebito getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoProcessoDebito situacao) {
        this.situacao = situacao;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public Date getDataBloqueio() {
        return dataBloqueio;
    }

    public void setDataBloqueio(Date dataBloqueio) {
        this.dataBloqueio = dataBloqueio;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public Cadastro getCadastro() {
        return cadastro;
    }

    public void setCadastro(Cadastro cadastro) {
        this.cadastro = cadastro;
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

    public PagamentoJudicial() {
        criadoEm = System.nanoTime();
        parcelas = Lists.newArrayList();
        valorACompensar = BigDecimal.ZERO;
        valorCompensado = BigDecimal.ZERO;
        saldo = BigDecimal.ZERO;
    }


    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    public String getBeneficiario(){
        if (pessoa != null) {
         return pessoa.toString();
        } else {
           return cadastro.getCadastroResponsavel();
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(codigo);
        sb.append("/");
        sb.append(exercicio.getAno());
        sb.append(" - ");
        if (pessoa != null) {
            sb.append(pessoa.toString());
        } else {
            sb.append(cadastro.getCadastroResponsavel());
        }
        return sb.toString();
    }
}
