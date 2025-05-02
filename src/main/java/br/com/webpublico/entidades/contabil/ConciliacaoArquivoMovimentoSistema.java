package br.com.webpublico.entidades.contabil;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.entidadesauxiliares.contabil.MovimentoConciliacaoBancaria;
import br.com.webpublico.enums.TipoOperacaoConcilicaoBancaria;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Audited
@Entity
@Table(name = "CONCILIACAOARQUIVOMOVSIS")
@Etiqueta("Movimento do Arquivo")
public class ConciliacaoArquivoMovimentoSistema extends SuperEntidade implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Obrigatorio
    private ConciliacaoArquivoMovimentoArquivo movimentoArquivo;
    @Temporal(TemporalType.DATE)
    private Date dataMovimento;
    @Temporal(TemporalType.DATE)
    private Date dataConciliacao;
    private String historico;
    private BigDecimal valor;
    @Enumerated(EnumType.STRING)
    private TipoOperacaoConcilicaoBancaria tipoOperacaoConciliacao;
    private Long movimentoId;
    @Enumerated(EnumType.STRING)
    private MovimentoConciliacaoBancaria.TipoMovimento tipoMovimento;
    private String subConta;
    private String numero;
    private String numeroOBN600;
    @Enumerated(EnumType.STRING)
    private MovimentoConciliacaoBancaria.Situacao situacao;
    private Boolean correspondente;
    @Transient
    private String styleText;
    @Transient
    private String styleBackGround;
    @Transient
    private Boolean bloquearSelecionar;

    public ConciliacaoArquivoMovimentoSistema() {
        super();
        this.correspondente = Boolean.FALSE;
        this.bloquearSelecionar = Boolean.FALSE;
    }

    public ConciliacaoArquivoMovimentoSistema(ConciliacaoArquivoMovimentoArquivo movimentoArquivo, MovimentoConciliacaoBancaria movimentoConciliacaoBancaria) {
        super();
        this.correspondente = Boolean.FALSE;
        this.movimentoArquivo = movimentoArquivo;
        this.dataMovimento = movimentoConciliacaoBancaria.getDataMovimento();
        this.dataConciliacao = movimentoConciliacaoBancaria.getDataConciliacao();
        this.historico = movimentoConciliacaoBancaria.getHistorico();
        this.valor = movimentoConciliacaoBancaria.getCredito();
        this.tipoOperacaoConciliacao = movimentoConciliacaoBancaria.getTipoOperacaoConciliacao();
        this.movimentoId = movimentoConciliacaoBancaria.getMovimentoId();
        this.tipoMovimento = movimentoConciliacaoBancaria.getTipoMovimento();
        this.subConta = movimentoConciliacaoBancaria.getSubConta();
        this.numero = movimentoConciliacaoBancaria.getNumero();
        this.situacao = movimentoConciliacaoBancaria.getSituacao();
        this.numeroOBN600 = movimentoConciliacaoBancaria.getNumeroOBN600();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ConciliacaoArquivoMovimentoArquivo getMovimentoArquivo() {
        return movimentoArquivo;
    }

    public void setMovimentoArquivo(ConciliacaoArquivoMovimentoArquivo movimentoArquivo) {
        this.movimentoArquivo = movimentoArquivo;
    }

    public Date getDataMovimento() {
        return dataMovimento;
    }

    public void setDataMovimento(Date dataMovimento) {
        this.dataMovimento = dataMovimento;
    }

    public Date getDataConciliacao() {
        return dataConciliacao;
    }

    public void setDataConciliacao(Date dataConciliacao) {
        this.dataConciliacao = dataConciliacao;
    }

    public String getHistorico() {
        return historico;
    }

    public void setHistorico(String historico) {
        this.historico = historico;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public TipoOperacaoConcilicaoBancaria getTipoOperacaoConciliacao() {
        return tipoOperacaoConciliacao;
    }

    public void setTipoOperacaoConciliacao(TipoOperacaoConcilicaoBancaria tipoOperacaoConciliacao) {
        this.tipoOperacaoConciliacao = tipoOperacaoConciliacao;
    }

    public Long getMovimentoId() {
        return movimentoId;
    }

    public void setMovimentoId(Long movimentoId) {
        this.movimentoId = movimentoId;
    }

    public MovimentoConciliacaoBancaria.TipoMovimento getTipoMovimento() {
        return tipoMovimento;
    }

    public void setTipoMovimento(MovimentoConciliacaoBancaria.TipoMovimento tipoMovimento) {
        this.tipoMovimento = tipoMovimento;
    }

    public String getSubConta() {
        return subConta;
    }

    public void setSubConta(String subConta) {
        this.subConta = subConta;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public MovimentoConciliacaoBancaria.Situacao getSituacao() {
        return situacao;
    }

    public void setSituacao(MovimentoConciliacaoBancaria.Situacao situacao) {
        this.situacao = situacao;
    }

    public Boolean getCorrespondente() {
        return correspondente;
    }

    public void setCorrespondente(Boolean correspondente) {
        this.correspondente = correspondente;
    }

    public Boolean getBloquearSelecionar() {
        return bloquearSelecionar == null ? Boolean.FALSE : bloquearSelecionar;
    }

    public void setBloquearSelecionar(Boolean bloquearSelecionar) {
        this.bloquearSelecionar = bloquearSelecionar;
    }

    public String getNumeroOBN600() {
        return numeroOBN600;
    }

    public void setNumeroOBN600(String numeroOBN600) {
        this.numeroOBN600 = numeroOBN600;
    }

    public String getNumeroENumeroOBN600() {
        return numero + (numeroOBN600 != null ? "<br/>(" + numeroOBN600 + ")" : "");
    }

    public String getStyleText() {
        if (styleText == null) {
            atualizarStyles();
        }
        return styleText;
    }

    public void setStyleText(String styleText) {
        this.styleText = styleText;
    }

    public String getStyleBackGround() {
        if (styleBackGround == null) {
            atualizarStyles();
        }
        return styleBackGround;
    }

    public void setStyleBackGround(String styleBackGround) {
        this.styleBackGround = styleBackGround;
    }

    public void atualizarStyles() {
        if (correspondente) {
            styleBackGround = "alert alert-success mbot02";
            styleText = "verdenegrito";
        } else {
            styleBackGround = "alert alert-warning mbot02";
            styleText = "amarelonegrito";
        }
    }

    @Override
    public String toString() {
        return DataUtil.getDataFormatada(dataMovimento) +
            (tipoMovimento != null ? " - " + tipoMovimento.getDescricao() : "") +
            " - " + numero;
    }
}
