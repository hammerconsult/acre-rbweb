package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoAquisicao;
import br.com.webpublico.enums.TipoBem;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
@Audited
@Etiqueta("Solicitação de Aquisição")
public class SolicitacaoAquisicao extends SuperEntidade implements PossuidorArquivo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Etiqueta("Número")
    @Tabelavel
    @Pesquisavel
    @CodigoGeradoAoConcluir
    private Long numero;

    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Temporal(TemporalType.DATE)
    @Etiqueta("Data da Solicitação")
    private Date dataSolicitacao;

    @Obrigatorio
    @ManyToOne
    @Tabelavel
    @Etiqueta("Usuário")
    private UsuarioSistema usuario;

    @Obrigatorio
    @OneToOne
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Requisição de Compra")
    private RequisicaoDeCompra requisicaoDeCompra;

    @OneToOne(cascade = CascadeType.ALL)
    @Invisivel
    private DetentorArquivoComposicao detentorArquivoComposicao;

    @Invisivel
    @OneToMany(mappedBy = "solicitacaoAquisicao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemSolicitacaoAquisicao> itensSolicitacao;

    @Invisivel
    @OneToMany(mappedBy = "solicitacaoAquisicao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DoctoFiscalSolicitacaoAquisicao> documentosFiscais;

    @Tabelavel
    @Enumerated(EnumType.STRING)
    @Etiqueta("Situação")
    private SituacaoAquisicao situacao;

    @Etiqueta("Tipo de Bem")
    @Enumerated(EnumType.STRING)
    private TipoBem tipoBem;

    @Etiqueta("Motivo")
    @Length(maximo = 255)
    private String motivo;

    //Utilizado na tela de rquisição de compra para gerar o termo
    @Transient
    private Long idAquisicao;

    public SolicitacaoAquisicao() {
        super();
        this.itensSolicitacao = Lists.newArrayList();
        this.documentosFiscais = Lists.newArrayList();
        this.situacao = SituacaoAquisicao.EM_ELABORACAO;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    public List<ItemSolicitacaoAquisicao> getItensSolicitacao() {
        return itensSolicitacao;
    }

    public void setItensSolicitacao(List<ItemSolicitacaoAquisicao> itensSolicitacao) {
        this.itensSolicitacao = itensSolicitacao;
    }

    public Date getDataSolicitacao() {
        return dataSolicitacao;
    }

    public void setDataSolicitacao(Date dataSolicitacao) {
        this.dataSolicitacao = dataSolicitacao;
    }

    public UsuarioSistema getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioSistema usuario) {
        this.usuario = usuario;
    }

    public RequisicaoDeCompra getRequisicaoDeCompra() {
        return requisicaoDeCompra;
    }

    public void setRequisicaoDeCompra(RequisicaoDeCompra requisicaoDeCompra) {
        this.requisicaoDeCompra = requisicaoDeCompra;
    }

    @Override
    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    @Override
    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }

    public SituacaoAquisicao getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoAquisicao situacao) {
        this.situacao = situacao;
    }

    public TipoBem getTipoBem() {
        return tipoBem;
    }

    public void setTipoBem(TipoBem tipoBem) {
        this.tipoBem = tipoBem;
    }

    public List<DoctoFiscalSolicitacaoAquisicao> getDocumentosFiscais() {
        return documentosFiscais;
    }

    public void setDocumentosFiscais(List<DoctoFiscalSolicitacaoAquisicao> documentosFiscais) {
        this.documentosFiscais = documentosFiscais;
    }

    public BigDecimal getValorTotalDocumento() {
        BigDecimal valorTotal = BigDecimal.ZERO;
        if (!getDocumentosFiscais().isEmpty()) {
            for (DoctoFiscalSolicitacaoAquisicao docto : getDocumentosFiscais()) {
                valorTotal = valorTotal.add(docto.getDocumentoFiscal().getTotal());
            }
        }
        return valorTotal;
    }

    public BigDecimal getQuantidadeTotalItensDocumento() {
        BigDecimal qtde = BigDecimal.ZERO;
        for (DoctoFiscalSolicitacaoAquisicao docto : getDocumentosFiscais()) {
            for (ItemDoctoItemAquisicao itemDocto : docto.getItens()) {
                qtde = qtde.add(itemDocto.getItemDoctoFiscalLiquidacao().getQuantidade());
            }
        }
        return qtde;
    }

    public BigDecimal getValorTotalItensDocumento() {
        BigDecimal qtde = BigDecimal.ZERO;
        for (DoctoFiscalSolicitacaoAquisicao docto : getDocumentosFiscais()) {
            for (ItemDoctoItemAquisicao itemDocto : docto.getItens()) {
                qtde = qtde.add(itemDocto.getItemDoctoFiscalLiquidacao().getQuantidade());
            }
        }
        return qtde;
    }

    public BigDecimal getValorTotalItensSolicitacao() {
        BigDecimal total = BigDecimal.ZERO;
        for (ItemSolicitacaoAquisicao item : getItensSolicitacao()) {
            total = total.add(item.getValorDoLancamento());
        }
        return total;
    }

    @Override
    public String toString() {
        try {
            return " Nº " + numero + " - " + DataUtil.getDataFormatada(dataSolicitacao) + " - " + usuario.getPessoaFisica();
        } catch (NullPointerException ne) {
            return "";
        }
    }

    public Boolean isEmElaboracao() {
        return SituacaoAquisicao.EM_ELABORACAO.equals(this.situacao);
    }

    public Boolean isRecusada() {
        return SituacaoAquisicao.RECUSADO.equals(this.situacao);
    }

    public Long getIdAquisicao() {
        return idAquisicao;
    }

    public void setIdAquisicao(Long idAquisicao) {
        this.idAquisicao = idAquisicao;
    }
}
