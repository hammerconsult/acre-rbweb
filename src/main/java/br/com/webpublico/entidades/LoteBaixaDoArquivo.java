package br.com.webpublico.entidades;

import br.com.webpublico.entidadesauxiliares.ArquivoBancarioTributario;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Cacheable
@Audited
public class LoteBaixaDoArquivo implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Invisivel
    private Long id;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Valor Total do Arquivo")
    private BigDecimal valorTotalArquivo;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Código do Banco")
    private String codigoBanco;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Descrição do Banco")
    private String banco;
    @Temporal(javax.persistence.TemporalType.DATE)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Data de Pagamento")
    private Date dataPagamento;
    @Temporal(javax.persistence.TemporalType.DATE)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Data da Movimentação")
    private Date dataMovimentacao;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Número do Arquivo")
    private Integer numero;
    @ManyToOne(cascade = CascadeType.ALL)
    private LoteBaixa loteBaixa;
    @ManyToOne
    private ArquivoLoteBaixa arquivoLoteBaixa;
    @Transient
    private Long criadoEm;
    @Transient
    private ArquivoBancarioTributario arquivoBancarioTributario;

    public LoteBaixaDoArquivo() {
        this.criadoEm = System.currentTimeMillis();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LoteBaixa getLoteBaixa() {
        return loteBaixa;
    }

    public void setLoteBaixa(LoteBaixa loteBaixa) {
        this.loteBaixa = loteBaixa;
    }

    public ArquivoLoteBaixa getArquivoLoteBaixa() {
        return arquivoLoteBaixa;
    }

    public void setArquivoLoteBaixa(ArquivoLoteBaixa arquivoLoteBaixa) {
        this.arquivoLoteBaixa = arquivoLoteBaixa;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public BigDecimal getValorTotalArquivo() {
        return valorTotalArquivo;
    }

    public void setValorTotalArquivo(BigDecimal valorTotalArquivo) {
        this.valorTotalArquivo = valorTotalArquivo;
    }

    public String getCodigoBanco() {
        return codigoBanco;
    }

    public void setCodigoBanco(String codigoBanco) {
        this.codigoBanco = codigoBanco;
    }

    public String getBanco() {
        return banco;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

    public Date getDataPagamento() {
        return dataPagamento;
    }

    public void setDataPagamento(Date dataPagamento) {
        this.dataPagamento = dataPagamento;
    }

    public Date getDataMovimentacao() {
        return dataMovimentacao;
    }

    public void setDataMovimentacao(Date dataMovimentacao) {
        this.dataMovimentacao = dataMovimentacao;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public ArquivoBancarioTributario getArquivoBancarioTributario() {
        return arquivoBancarioTributario;
    }

    public void setArquivoBancarioTributario(ArquivoBancarioTributario arquivoBancarioTributario) {
        this.arquivoBancarioTributario = arquivoBancarioTributario;
    }

    @Override
    public boolean equals(Object obj) {
        return IdentidadeDaEntidade.calcularEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }
}
