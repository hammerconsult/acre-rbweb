/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoClassificacaoFornecedor;
import br.com.webpublico.enums.TipoSituacaoFornecedorLicitacao;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Length;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lucas
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Licitacao")
@Etiqueta("Licitação Fornecedor")
public class LicitacaoFornecedor extends SuperEntidade implements Comparable<LicitacaoFornecedor> {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Obrigatorio
    @Etiqueta("Código")
    private Integer codigo;

    @ManyToOne
    @Etiqueta("Licitação")
    private Licitacao licitacao;

    @Obrigatorio
    @ManyToOne
    @Etiqueta("Fornecedor")
    private Pessoa empresa;

    @ManyToOne
    @Obrigatorio
    @Etiqueta("Representante")
    private RepresentanteLicitacao representante;

    @Enumerated(EnumType.STRING)
    @Etiqueta("Classificação")
    private TipoClassificacaoFornecedor tipoClassificacaoFornecedor;

    @Enumerated(EnumType.STRING)
    @Etiqueta("Classificação Técnica")
    private TipoClassificacaoFornecedor classificacaoTecnica;

    private String justificativaClassificacao;

    @Transient
    private String mensagemDeJustificativa;

//    @Etiqueta("Prazo de Entrega")
//    private Integer prazoDeEntrega;
//
//    @Enumerated(EnumType.STRING)
//    @Etiqueta("(Tipo) Prazo Entrega")
//    private TipoPrazo tipoPrazoEntrega;

//    @Etiqueta("Validade da Proposta")
//    private Integer validadeDaProposta;

//    @Enumerated(EnumType.STRING)
//    @Etiqueta("(Tipo) Prazo Validade")
//    private TipoPrazo tipoPrazoValidadeProposta;

    @Etiqueta("Documentos do Fornecedor")
    @OneToMany(mappedBy = "licitacaoFornecedor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LicitacaoDoctoFornecedor> documentosFornecedor;

    @Transient
    private List<LicitacaoDoctoFornecedor> documentosVencidosParaValidar;

    @OneToMany(mappedBy = "licitacaoFornecedor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StatusFornecedorLicitacao> listaDeStatus;

    @OneToOne(mappedBy = "licitacaoFornecedor", cascade = CascadeType.ALL)
    private PropostaFornecedor propostaFornecedor;

    @Length(maximo = 255)
    @Etiqueta("Instrumento de Representação")
    private String instrumentoRepresentacao;

    @Transient
    private List<ObjetoCompra> itensSelecionados;

    @Invisivel
    @Transient
    private Boolean selecionado;

    @Invisivel
    @Transient
    private BigDecimal valorDoLoteLancadoNaProposta; // Utilizado na tela do pregão por lote.

    public LicitacaoFornecedor() {
        super();
        this.listaDeStatus = new ArrayList<>();
        this.itensSelecionados = new ArrayList<>();
        this.documentosFornecedor = Lists.newArrayList();
        this.documentosVencidosParaValidar = new ArrayList<>();
    }

    public LicitacaoFornecedor(Licitacao licitacao, Integer codigo) {
        super();
        this.licitacao = licitacao;
        this.codigo = codigo;
    }

    public List<ObjetoCompra> getItensSelecionados() {
        return itensSelecionados;
    }

    public void setItensSelecionados(List<ObjetoCompra> itensSelecionados) {
        this.itensSelecionados = itensSelecionados;
    }

    public List<LicitacaoDoctoFornecedor> getDocumentosFornecedor() {
        return documentosFornecedor;
    }

    public void setDocumentosFornecedor(List<LicitacaoDoctoFornecedor> documentosFornecedor) {
        this.documentosFornecedor = documentosFornecedor;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Licitacao getLicitacao() {
        return licitacao;
    }

    public void setLicitacao(Licitacao licitacao) {
        this.licitacao = licitacao;
    }

    public Pessoa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Pessoa empresa) {
        this.empresa = empresa;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public String getJustificativaClassificacao() {
        return justificativaClassificacao;
    }

    public void setJustificativaClassificacao(String justificativaClassificacao) {
        this.justificativaClassificacao = justificativaClassificacao;
    }

    public RepresentanteLicitacao getRepresentante() {
        return representante;
    }

    public void setRepresentante(RepresentanteLicitacao representante) {
        this.representante = representante;
    }

    public TipoClassificacaoFornecedor getTipoClassificacaoFornecedor() {
        return tipoClassificacaoFornecedor;
    }

    public void setTipoClassificacaoFornecedor(TipoClassificacaoFornecedor tipoClassificacaoFornecedor) {
        this.tipoClassificacaoFornecedor = tipoClassificacaoFornecedor;
    }

    public String getMensagemDeJustificativa() {
        return mensagemDeJustificativa;
    }

    public void setMensagemDeJustificativa(String mensagemDeJustificativa) {
        this.mensagemDeJustificativa = mensagemDeJustificativa;
    }

    public TipoClassificacaoFornecedor getClassificacaoTecnica() {
        return classificacaoTecnica;
    }

    public void setClassificacaoTecnica(TipoClassificacaoFornecedor classificacaoTecnica) {
        this.classificacaoTecnica = classificacaoTecnica;
    }

    public List<StatusFornecedorLicitacao> getListaDeStatus() {
        return listaDeStatus;
    }

    public void setListaDeStatus(List<StatusFornecedorLicitacao> listaDeStatus) {
        this.listaDeStatus = listaDeStatus;
    }

    public String getInstrumentoRepresentacao() {
        return instrumentoRepresentacao;
    }

    public void setInstrumentoRepresentacao(String instrumentoRepresentacao) {
        this.instrumentoRepresentacao = instrumentoRepresentacao;
    }

    public Boolean getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(Boolean selecionado) {
        this.selecionado = selecionado;
    }

    public BigDecimal getValorDoLoteLancadoNaProposta() {
        if (valorDoLoteLancadoNaProposta == null) {
            valorDoLoteLancadoNaProposta = BigDecimal.ZERO;
        }
        return valorDoLoteLancadoNaProposta;
    }

    public void setValorDoLoteLancadoNaProposta(BigDecimal valorDoLoteLancadoNaProposta) {
        this.valorDoLoteLancadoNaProposta = valorDoLoteLancadoNaProposta;
    }

    public PropostaFornecedor getPropostaFornecedor() {
        return propostaFornecedor;
    }

    public void setPropostaFornecedor(PropostaFornecedor propostaFornecedor) {
        this.propostaFornecedor = propostaFornecedor;
    }

    @Override
    public String toString() {
        return (empresa != null ? empresa : "") + (representante != null ? " - " + representante : "");
    }

    private boolean existeDocumentoFaltando(List<DoctoHabilitacao> listaDeDoctoHabilitacaoDaLicitacao, List<DoctoHabilitacao> listaDeDoctoHabilitacao) {
        for (DoctoHabilitacao doctoHabilitacaoDaVez : listaDeDoctoHabilitacaoDaLicitacao) {
            if (!listaDeDoctoHabilitacao.contains(doctoHabilitacaoDaVez)) {
                return true;
            }
        }
        return false;
    }

    private boolean existeDocumentoVencido() {
        if (getDocumentosFornecedor() != null) {
            for (LicitacaoDoctoFornecedor ldf : getDocumentosFornecedor()) {
                if (ldf.getDocumentoVencido() != null && ldf.getDocumentoVencido().booleanValue()) {
                    return true;
                }
            }
        }

        return false;
    }

    private List<DoctoHabilitacao> obterListaDeDoctoHabilitacaoDoFornecedor() {
        ArrayList<DoctoHabilitacao> listaDeRetorno = new ArrayList<>();
        if (getDocumentosFornecedor() != null) {
            for (LicitacaoDoctoFornecedor ldf : getDocumentosFornecedor()) {
                listaDeRetorno.add(ldf.getDoctoHabilitacao());
            }
        }

        return listaDeRetorno;
    }

    public boolean habilitarFornecedor(List<DoctoHabilitacao> listaDeDoctoHabilitacaoDaLicitacao, Boolean fornecedorComDocumentoVencido) {
        if (listaDeDoctoHabilitacaoDaLicitacao == null || listaDeDoctoHabilitacaoDaLicitacao.isEmpty()) {
            habilitarValidandoDocumento(fornecedorComDocumentoVencido);
            return true;
        }

        if (existeDocumentoVencido()) {
            inabilitarFornecedor("Fornecedor inabilitado por apresentar documento(s) vencido(s).");
            return false;
        }

        if (existeDocumentoFaltando(listaDeDoctoHabilitacaoDaLicitacao, obterListaDeDoctoHabilitacaoDoFornecedor())) {
            inabilitarFornecedor("Fornecedor inabilitado por não apresentar todos os documentos requeridos.");
            return false;
        }

        habilitarValidandoDocumento(fornecedorComDocumentoVencido);
        return true;
    }

    public void habilitarValidandoDocumento(Boolean documentoVencido) {
        if (documentoVencido) {
            habilitarComRessalva();
        } else {
            habilitar();
        }
    }

    private void inabilitarFornecedor(String motivo) {
        this.setJustificativaClassificacao(motivo);
        this.setTipoClassificacaoFornecedor(TipoClassificacaoFornecedor.INABILITADO);
    }

    private void habilitar() {
        this.setJustificativaClassificacao("");
        this.setTipoClassificacaoFornecedor(TipoClassificacaoFornecedor.HABILITADO);
    }

    private void habilitarComRessalva() {
        this.setJustificativaClassificacao("");
        this.setTipoClassificacaoFornecedor(TipoClassificacaoFornecedor.HABILITADOCOMRESSALVA);
    }

    public boolean possuiEsteDocumento(DoctoHabilitacao documento) {
        for (LicitacaoDoctoFornecedor ldf : getDocumentosFornecedor()) {
            if (ldf.getDoctoHabilitacao().equals(documento)) {
                return true;
            }
        }
        return false;
    }

    public boolean isHabilitado() {
        return TipoClassificacaoFornecedor.HABILITADO.equals(getTipoClassificacaoFornecedor());
    }

    public boolean foiAdjudicado() {
        return passouPelaSituacao(TipoSituacaoFornecedorLicitacao.ADJUDICADA);
    }

    public boolean foiHomologado() {
        return passouPelaSituacao(TipoSituacaoFornecedorLicitacao.HOMOLOGADA);
    }

    private boolean passouPelaSituacao(TipoSituacaoFornecedorLicitacao situacao) {
        for (StatusFornecedorLicitacao sfl : listaDeStatus) {
            if (sfl.getTipoSituacao().equals(situacao)) {
                return true;
            }
        }

        return false;
    }

    public TipoClassificacaoFornecedor processarClassificacaoDesteFornecedor() {
        setJustificativaClassificacao("");
        setMensagemDeJustificativa("");
        TipoClassificacaoFornecedor tp = TipoClassificacaoFornecedor.INABILITADO;
        if (documentosFornecedor != null && documentosFornecedor.size() == this.getLicitacao().getListaDeDoctoHabilitacao().size()) {
            if (documentosValidos()) {
                tp = TipoClassificacaoFornecedor.HABILITADO;
            } else {
                List<TipoClassificacaoFornecedor> listaTipos = Lists.newArrayList();
                for (LicitacaoDoctoFornecedor doc : documentosVencidosParaValidar) {
                    listaTipos.add(doc.getSituacaoDeAcordoComEsteDocumento());
                }

                if (listaTipos.contains(TipoClassificacaoFornecedor.INABILITADO)) {
                    tp = TipoClassificacaoFornecedor.INABILITADO;
                } else if (listaTipos.contains(TipoClassificacaoFornecedor.HABILITADOCOMRESSALVA)) {
                    tp = TipoClassificacaoFornecedor.HABILITADOCOMRESSALVA;
                }
            }
        } else {
            setMensagemDeJustificativa("Não foram apresentados todos os documentos requisitados.");
        }
        setTipoClassificacaoFornecedor(tp);
        return tp;
    }

    private boolean documentosValidos() {
        documentosVencidosParaValidar.clear();
        boolean retorno = true;
        if (documentosFornecedor.isEmpty()) {
            return false;
        }
        for (LicitacaoDoctoFornecedor ldf : documentosFornecedor) {
            if (ldf.isDocumentoVencido()) {
                ldf.setDocumentoVencido(Boolean.TRUE);
                retorno = false;
                documentosVencidosParaValidar.add(ldf);
            }
        }
        return retorno;
    }

    public boolean isFornecedorHabilitado() {
        return TipoClassificacaoFornecedor.HABILITADO.equals(tipoClassificacaoFornecedor);
    }

    public boolean isFornecedorHabilitadoComRessalva() {
        return TipoClassificacaoFornecedor.HABILITADOCOMRESSALVA.equals(tipoClassificacaoFornecedor);
    }

    public boolean isFornecedorInabilitado() {
        return TipoClassificacaoFornecedor.INABILITADO.equals(tipoClassificacaoFornecedor);
    }

    public boolean isFornecedorHabilitadoOuRessalva() {
        return isFornecedorHabilitado() || isFornecedorHabilitadoComRessalva();
    }

    public boolean hasDocumentos() {
        return documentosFornecedor != null && !documentosFornecedor.isEmpty();
    }

    public boolean hasPropostas() {
        return propostaFornecedor != null;
    }

    @Override
    public int compareTo(LicitacaoFornecedor o) {
        if (o.getCodigo() != null && getCodigo() != null) {
            return ComparisonChain.start().compare(getCodigo(), o.getCodigo()).result();
        }
        return 0;
    }
}
