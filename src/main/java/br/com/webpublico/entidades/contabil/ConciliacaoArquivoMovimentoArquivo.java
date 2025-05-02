package br.com.webpublico.entidades.contabil;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TipoOperacaoConcilicaoBancaria;
import br.com.webpublico.enums.contabil.TipoMovimentoConciliacaoArquivo;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Audited
@Entity
@Table(name = "CONCILIACAOARQUIVOMOVARQ")
@Etiqueta("Movimento do Arquivo")
public class ConciliacaoArquivoMovimentoArquivo extends SuperEntidade implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Obrigatorio
    private ConciliacaoArquivo conciliacaoArquivo;
    @Temporal(TemporalType.DATE)
    private Date dataMovimento;
    @Enumerated(EnumType.STRING)
    private TipoMovimentoConciliacaoArquivo tipo;
    private BigDecimal valor;
    private String identificador;
    private String numeroDocumento;
    private String historico;
    @ManyToOne
    private UnidadeOrganizacional unidadeOrganizacional;
    @ManyToOne
    private SubConta subConta;
    @Enumerated(EnumType.STRING)
    private TipoOperacaoConcilicaoBancaria tipoOperacaoConcilicaoBancaria;
    @ManyToOne
    private TipoConciliacao tipoConciliacao;
    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "movimentoArquivo")
    private List<ConciliacaoArquivoMovimentoSistema> movimentosSistemas;
    @Transient
    private Boolean selecionado;
    @Transient
    private String styleText;
    @Transient
    private String styleBackGround;

    public ConciliacaoArquivoMovimentoArquivo() {
        super();
        movimentosSistemas = Lists.newArrayList();
        selecionado = Boolean.FALSE;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ConciliacaoArquivo getConciliacaoArquivo() {
        return conciliacaoArquivo;
    }

    public void setConciliacaoArquivo(ConciliacaoArquivo conciliacaoArquivo) {
        this.conciliacaoArquivo = conciliacaoArquivo;
    }

    public Date getDataMovimento() {
        return dataMovimento;
    }

    public void setDataMovimento(Date dataMovimento) {
        this.dataMovimento = dataMovimento;
    }

    public TipoMovimentoConciliacaoArquivo getTipo() {
        return tipo;
    }

    public void setTipo(TipoMovimentoConciliacaoArquivo tipo) {
        this.tipo = tipo;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public String getHistorico() {
        return historico;
    }

    public void setHistorico(String historico) {
        this.historico = historico;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public SubConta getSubConta() {
        return subConta;
    }

    public void setSubConta(SubConta subConta) {
        this.subConta = subConta;
    }

    public TipoOperacaoConcilicaoBancaria getTipoOperacaoConcilicaoBancaria() {
        return tipoOperacaoConcilicaoBancaria;
    }

    public void setTipoOperacaoConcilicaoBancaria(TipoOperacaoConcilicaoBancaria tipoOperacaoConcilicaoBancaria) {
        this.tipoOperacaoConcilicaoBancaria = tipoOperacaoConcilicaoBancaria;
    }

    public TipoConciliacao getTipoConciliacao() {
        return tipoConciliacao;
    }

    public void setTipoConciliacao(TipoConciliacao tipoConciliacao) {
        this.tipoConciliacao = tipoConciliacao;
    }

    public List<ConciliacaoArquivoMovimentoSistema> getMovimentosSistemas() {
        return movimentosSistemas;
    }

    public void setMovimentosSistemas(List<ConciliacaoArquivoMovimentoSistema> movimentosSistemas) {
        this.movimentosSistemas = movimentosSistemas;
    }

    public Boolean getSelecionado() {
        return selecionado == null ? Boolean.FALSE : selecionado;
    }

    public void setSelecionado(Boolean selecionado) {
        this.selecionado = selecionado;
    }

    public List<ConciliacaoArquivoMovimentoSistema> getMovimentosSistemasNaoConciliados() {
        if (movimentosSistemas != null && !movimentosSistemas.isEmpty()) {
            return movimentosSistemas.stream().filter(movSistema -> movSistema.getDataConciliacao() == null).collect(Collectors.toList());
        }
        return Lists.newArrayList();
    }

    public String getDataMovimentoFormatada() {
        return DataUtil.getDataFormatada(dataMovimento);
    }

    public String getValorFormatado() {
        return Util.formatarValor(valor, null, false);
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
        if (movimentosSistemas != null && !movimentosSistemas.isEmpty()) {
            if (movimentosSistemas.stream().anyMatch(ConciliacaoArquivoMovimentoSistema::getCorrespondente) ||
                movimentosSistemas.size() == 1) {
                styleBackGround = "alert alert-success mbot02";
                styleText = "verdenegrito";
            } else if (movimentosSistemas.size() > 1) {
                styleBackGround = "alert alert-warning mbot02";
                styleText = "amarelonegrito";
            }
        } else {
            styleBackGround = "alert alert-danger mbot02";
            styleText = "vermelhonegrito";
        }
    }

    @Override
    public String toString() {
        return tipo.getDescricao() + " - " + DataUtil.getDataFormatada(dataMovimento) + " - " + numeroDocumento;
    }
}
