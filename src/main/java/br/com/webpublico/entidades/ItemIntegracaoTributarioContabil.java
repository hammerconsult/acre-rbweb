package br.com.webpublico.entidades;

import br.com.webpublico.enums.OperacaoIntegracaoTribCont;
import br.com.webpublico.enums.OperacaoReceita;
import br.com.webpublico.enums.TipoIntegracao;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@GrupoDiagrama(nome = "Arrecadacao")
@Audited
@Entity
@Etiqueta(value = "Item de Integracao para Crédito a Receber")
@Table(name = "ITEMINTEGRACAOTRIBCONT")
public class ItemIntegracaoTributarioContabil implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private UnidadeOrganizacional administrativa;
    @ManyToOne
    private UnidadeOrganizacional orcamentaria;
    @Temporal(TemporalType.DATE)
    private Date dataPagamento;
    @ManyToOne
    private ContaReceita contaReceita;
    @Enumerated(EnumType.STRING)
    private OperacaoReceita operacaoReceitaRealizada;
    @ManyToOne
    private FonteDeRecursos fonteDeRecursos;
    @Transient
    private Long criadoEm;
    private BigDecimal valor;
    @Enumerated(EnumType.STRING)
    private TipoIntegracao tipo;
    @ManyToOne
    private SubConta subConta;
    @Temporal(TemporalType.DATE)
    private Date dataCredito;
    @ManyToOne
    private LoteBaixa loteBaixa;
    @Enumerated(EnumType.STRING)
    private OperacaoIntegracaoTribCont operacaoIntegracaoTribCont;

    public ItemIntegracaoTributarioContabil() {
        criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UnidadeOrganizacional getAdministrativa() {
        return administrativa;
    }

    public void setAdministrativa(UnidadeOrganizacional administrativa) {
        this.administrativa = administrativa;
    }

    public UnidadeOrganizacional getOrcamentaria() {
        return orcamentaria;
    }

    public void setOrcamentaria(UnidadeOrganizacional orcamentaria) {
        this.orcamentaria = orcamentaria;
    }

    public Date getDataPagamento() {
        return dataPagamento;
    }

    public void setDataPagamento(Date dataPagamento) {
        this.dataPagamento = dataPagamento;
    }

    public ContaReceita getContaReceita() {
        return contaReceita;
    }

    public void setContaReceita(ContaReceita contaReceita) {
        this.contaReceita = contaReceita;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public TipoIntegracao getTipo() {
        return tipo;
    }

    public void setTipo(TipoIntegracao tipo) {
        this.tipo = tipo;
    }

    public SubConta getSubConta() {
        return subConta;
    }

    public void setSubConta(SubConta subConta) {
        this.subConta = subConta;
    }

    public Date getDataCredito() {
        return dataCredito;
    }

    public void setDataCredito(Date dataCredito) {
        this.dataCredito = dataCredito;
    }

    public LoteBaixa getLoteBaixa() {
        return loteBaixa;
    }

    public void setLoteBaixa(LoteBaixa loteBaixa) {
        this.loteBaixa = loteBaixa;
    }

    public OperacaoReceita getOperacaoReceitaRealizada() {
        return operacaoReceitaRealizada;
    }

    public void setOperacaoReceitaRealizada(OperacaoReceita operacaoReceitaRealizada) {
        this.operacaoReceitaRealizada = operacaoReceitaRealizada;
    }

    public FonteDeRecursos getFonteDeRecursos() {
        return fonteDeRecursos;
    }

    public void setFonteDeRecursos(FonteDeRecursos fonteDeRecursos) {
        this.fonteDeRecursos = fonteDeRecursos;
    }

    public OperacaoIntegracaoTribCont getOperacaoIntegracaoTribCont() {
        return operacaoIntegracaoTribCont;
    }

    public void setOperacaoIntegracaoTribCont(OperacaoIntegracaoTribCont operacaoIntegracaoTribCont) {
        this.operacaoIntegracaoTribCont = operacaoIntegracaoTribCont;
    }

    @Override
    public boolean equals(Object o) {
        return IdentidadeDaEntidade.calcularEquals(this, o);
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public String toString() {
        return contaReceita.getDescricao() + " - R$. " + valor;
    }


}
