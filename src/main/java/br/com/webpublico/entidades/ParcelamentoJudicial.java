package br.com.webpublico.entidades;

import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
@Audited
public class ParcelamentoJudicial extends SuperEntidade {

    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    private Exercicio exercicio;
    private Long codigo;
    @Temporal(TemporalType.DATE)
    private Date dataSolicitacao;
    private String numeroProtocolo;
    @ManyToOne
    private UsuarioSistema usuarioSolicitacao;
    @Enumerated(EnumType.STRING)
    private Situacao situacao;
    private String observacao;
    @Etiqueta("Contato")
    @Obrigatorio
    private String contato;
    private String numeroProcessoForum;
    @ManyToOne
    private Pessoa pessoa;
    @ManyToOne(cascade = CascadeType.ALL)
    private DocumentoOficial termoUnificado;
    @OneToMany(mappedBy = "parcelamentoJudicial", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemParcelamentoJudicial> itens;
    @ManyToOne
    private Pessoa procurador;

    public ParcelamentoJudicial() {
        super();
        this.itens = Lists.newArrayList();
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

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

    public Date getDataSolicitacao() {
        return dataSolicitacao;
    }

    public void setDataSolicitacao(Date dataSolicitacao) {
        this.dataSolicitacao = dataSolicitacao;
    }

    public String getNumeroProtocolo() {
        return numeroProtocolo;
    }

    public void setNumeroProtocolo(String numeroProtocolo) {
        this.numeroProtocolo = numeroProtocolo;
    }

    public UsuarioSistema getUsuarioSolicitacao() {
        return usuarioSolicitacao;
    }

    public void setUsuarioSolicitacao(UsuarioSistema usuarioSolicitacao) {
        this.usuarioSolicitacao = usuarioSolicitacao;
    }

    public Situacao getSituacao() {
        return situacao;
    }

    public void setSituacao(Situacao situacao) {
        this.situacao = situacao;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public String getContato() {
        return contato;
    }

    public void setContato(String contato) {
        this.contato = contato;
    }

    public String getNumeroProcessoForum() {
        return numeroProcessoForum;
    }

    public void setNumeroProcessoForum(String numeroProcessoForum) {
        this.numeroProcessoForum = numeroProcessoForum;
    }

    public List<ItemParcelamentoJudicial> getItens() {
        return itens;
    }

    public void setItens(List<ItemParcelamentoJudicial> itens) {
        this.itens = itens;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public DocumentoOficial getTermoUnificado() {
        return termoUnificado;
    }

    public void setTermoUnificado(DocumentoOficial termoUnificado) {
        this.termoUnificado = termoUnificado;
    }

    public Pessoa getProcurador() {
        return procurador;
    }

    public void setProcurador(Pessoa procurador) {
        this.procurador = procurador;
    }

    public enum Situacao implements EnumComDescricao {
        ABERTO("Aberto"), PROCESSADO("Efetivado");

        private final String descricao;

        Situacao(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    public BigDecimal getValorImpostoParcelamento() {
        BigDecimal valor =  BigDecimal.ZERO;
        for (ItemParcelamentoJudicial item : this.itens) {
            for (DebitoParcelamentoJudicial debito : item.getDebitos()) {
                valor = valor.add(debito.getResultadoParcela().getValorImposto());
            }
        }
        return valor;
    }

    public BigDecimal getValorTaxaParcelamento() {
        BigDecimal valor =  BigDecimal.ZERO;
        for (ItemParcelamentoJudicial item : this.itens) {
            for (DebitoParcelamentoJudicial debito : item.getDebitos()) {
                valor = valor.add(debito.getResultadoParcela().getValorTaxa());
            }
        }
        return valor;
    }

    public BigDecimal getValorDescontoParcelamento() {
        BigDecimal valor =  BigDecimal.ZERO;
        for (ItemParcelamentoJudicial item : this.itens) {
            for (DebitoParcelamentoJudicial debito : item.getDebitos()) {
                valor = valor.add(debito.getResultadoParcela().getValorDesconto());
            }
        }
        return valor;
    }

    public BigDecimal getValorMultaParcelamento() {
        BigDecimal valor =  BigDecimal.ZERO;
        for (ItemParcelamentoJudicial item : this.itens) {
            for (DebitoParcelamentoJudicial debito : item.getDebitos()) {
                valor = valor.add(debito.getResultadoParcela().getValorMulta());
            }
        }
        return valor;
    }

    public BigDecimal getValorJurosParcelamento() {
        BigDecimal valor =  BigDecimal.ZERO;
        for (ItemParcelamentoJudicial item : this.itens) {
            for (DebitoParcelamentoJudicial debito : item.getDebitos()) {
                valor = valor.add(debito.getResultadoParcela().getValorJuros());
            }
        }
        return valor;
    }

    public BigDecimal getValorCorrecaoParcelamento() {
        BigDecimal valor =  BigDecimal.ZERO;
        for (ItemParcelamentoJudicial item : this.itens) {
            for (DebitoParcelamentoJudicial debito : item.getDebitos()) {
                valor = valor.add(debito.getResultadoParcela().getValorCorrecao());
            }
        }
        return valor;
    }

    public BigDecimal getValorHonorariosParcelamento() {
        BigDecimal valor =  BigDecimal.ZERO;
        for (ItemParcelamentoJudicial item : this.itens) {
            for (DebitoParcelamentoJudicial debito : item.getDebitos()) {
                valor = valor.add(debito.getResultadoParcela().getValorHonorarios());
            }
        }
        return valor;
    }

    public BigDecimal getValorTotalParcelamento() {
        BigDecimal valor =  BigDecimal.ZERO;
        for (ItemParcelamentoJudicial item : this.itens) {
            for (DebitoParcelamentoJudicial debito : item.getDebitos()) {
                valor = valor.add(debito.getResultadoParcela().getValorTotal());
            }
        }
       return valor;
    }

    @Override
    public void realizarValidacoes() {
        super.realizarValidacoes();
        ValidacaoException ve = new ValidacaoException();
        if (Strings.isNullOrEmpty(numeroProcessoForum)) {
            ve.adicionarMensagemDeCampoObrigatorio("O N° Processo Judicial deve ser informado.");
        } else {
            if (itens == null || itens.isEmpty()) {
                ve.adicionarMensagemDeCampoObrigatorio("Nenhum débito Em Aberto vinculado ao processo judicial.");
            }
        }
        ve.lancarException();
    }
}
