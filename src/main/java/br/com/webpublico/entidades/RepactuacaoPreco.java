package br.com.webpublico.entidades;

import br.com.webpublico.enums.ParecerRepactuacaoPreco;
import br.com.webpublico.enums.TipoMovimentoProcessoLicitatorio;
import br.com.webpublico.interfaces.EntidadeDetendorDocumentoLicitacao;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: JoãoPaulo
 * Date: 14/04/14
 * Time: 16:25
 * To change this template use File | Settings | File Templates.
 */
@Audited
@Entity
@Etiqueta("Repactuação de Preço")
public class RepactuacaoPreco extends SuperEntidade implements EntidadeDetendorDocumentoLicitacao {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Etiqueta("Número")
    private Long numero;

    @ManyToOne
    @Obrigatorio
    @Etiqueta("Ata de Registro de Preço")
    private AtaRegistroPreco ataRegistroPreco;

    @ManyToOne
    @Obrigatorio
    @Etiqueta("Fornecedor")
    private LicitacaoFornecedor fornecedor;

    @Obrigatorio
    @Etiqueta("Justificativa do Pedido")
    private String justificativaDoPedido;

    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Etiqueta("Tipo Parecer")
    private ParecerRepactuacaoPreco parecerRepactuacao;

    @Obrigatorio
    @Etiqueta("Justificativa do Parecer")
    private String justificativaDoParecer;

    @ManyToOne
    @Obrigatorio
    @Etiqueta("Responsável pelo Parecer")
    private PessoaFisica responsavelParecer;

    @Etiqueta("Itens da Licitação")
    @OneToMany(mappedBy = "repactuacaoPreco", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ObjetoCompraRepactuacao> itens;

    @Obrigatorio
    @Etiqueta("Número do Parecer")
    private String numeroParecer;

    @Obrigatorio
    @Etiqueta("Número OAB do Parecerista")
    private String oabParecerista;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private DetentorDocumentoLicitacao detentorDocumentoLicitacao;

    public RepactuacaoPreco() {
        itens = Lists.newArrayList();
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

    public AtaRegistroPreco getAtaRegistroPreco() {
        return ataRegistroPreco;
    }

    public void setAtaRegistroPreco(AtaRegistroPreco ataRegistroPreco) {
        this.ataRegistroPreco = ataRegistroPreco;
    }

    public LicitacaoFornecedor getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(LicitacaoFornecedor fornecedor) {
        this.fornecedor = fornecedor;
    }

    public String getJustificativaDoPedido() {
        return justificativaDoPedido;
    }

    public void setJustificativaDoPedido(String justificativaDoPedido) {
        this.justificativaDoPedido = justificativaDoPedido;
    }

    public ParecerRepactuacaoPreco getParecerRepactuacao() {
        return parecerRepactuacao;
    }

    public void setParecerRepactuacao(ParecerRepactuacaoPreco parecerRepactuacao) {
        this.parecerRepactuacao = parecerRepactuacao;
    }

    public String getJustificativaDoParecer() {
        return justificativaDoParecer;
    }

    public void setJustificativaDoParecer(String justificativaDoParecer) {
        this.justificativaDoParecer = justificativaDoParecer;
    }

    public PessoaFisica getResponsavelParecer() {
        return responsavelParecer;
    }

    public void setResponsavelParecer(PessoaFisica responsavelParecer) {
        this.responsavelParecer = responsavelParecer;
    }

    public List<ObjetoCompraRepactuacao> getItens() {
        return itens;
    }

    public void setItens(List<ObjetoCompraRepactuacao> listaDeObjetoDeCompra) {
        this.itens = listaDeObjetoDeCompra;
    }

    public String getNumeroParecer() {
        return numeroParecer;
    }

    public void setNumeroParecer(String numeroParecer) {
        this.numeroParecer = numeroParecer;
    }

    public String getOabParecerista() {
        return oabParecerista;
    }

    public void setOabParecerista(String oabParecerista) {
        this.oabParecerista = oabParecerista;
    }

    @Override
    public DetentorDocumentoLicitacao getDetentorDocumentoLicitacao() {
        return detentorDocumentoLicitacao;
    }

    @Override
    public void setDetentorDocumentoLicitacao(DetentorDocumentoLicitacao detentorDocumentoLicitacao) {
        this.detentorDocumentoLicitacao = detentorDocumentoLicitacao;
    }

    @Override
    public TipoMovimentoProcessoLicitatorio getTipoAnexo() {
        return TipoMovimentoProcessoLicitatorio.REPACTUACAO_PRECO;
    }
}
