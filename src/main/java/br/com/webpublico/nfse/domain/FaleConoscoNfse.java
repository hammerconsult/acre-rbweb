package br.com.webpublico.nfse.domain;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.enums.Sistema;
import br.com.webpublico.enums.tributario.SituacaoFaleConoscoNfse;
import br.com.webpublico.enums.tributario.TipoFaleConoscoNfse;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Audited
@Etiqueta("Fale Conosco (Nfse)")
public class FaleConoscoNfse extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Invisivel
    private Long id;
    @Enumerated(EnumType.STRING)
    private SituacaoFaleConoscoNfse situacao;
    @Enumerated(EnumType.STRING)
    private Sistema sistema;
    @Enumerated(EnumType.STRING)
    private TipoFaleConoscoNfse tipo;
    private String assunto;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataEnvio;

    @ManyToOne
    private DadosPessoaisNfse dadosReclamante;

    @ManyToOne
    private DadosPessoaisNfse dadosReclamado;

    @Temporal(TemporalType.DATE)
    private Date dataServico;
    private String descricaoServico;
    private Long numeroNotaServico;
    private BigDecimal valorServico;

    private String mensagem;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dataResposta;
    @ManyToOne
    private UsuarioSistema usuarioResposta;
    private String resposta;

    public FaleConoscoNfse() {
        situacao = SituacaoFaleConoscoNfse.ABERTO;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SituacaoFaleConoscoNfse getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoFaleConoscoNfse situacao) {
        this.situacao = situacao;
    }

    public Sistema getSistema() {
        return sistema;
    }

    public void setSistema(Sistema sistema) {
        this.sistema = sistema;
    }

    public TipoFaleConoscoNfse getTipo() {
        return tipo;
    }

    public void setTipo(TipoFaleConoscoNfse tipo) {
        this.tipo = tipo;
    }

    public String getAssunto() {
        return assunto;
    }

    public void setAssunto(String assunto) {
        this.assunto = assunto;
    }

    public Date getDataEnvio() {
        return dataEnvio;
    }

    public void setDataEnvio(Date dataEnvio) {
        this.dataEnvio = dataEnvio;
    }

    public DadosPessoaisNfse getDadosReclamante() {
        return dadosReclamante;
    }

    public void setDadosReclamante(DadosPessoaisNfse dadosReclamante) {
        this.dadosReclamante = dadosReclamante;
    }

    public DadosPessoaisNfse getDadosReclamado() {
        return dadosReclamado;
    }

    public void setDadosReclamado(DadosPessoaisNfse dadosReclamado) {
        this.dadosReclamado = dadosReclamado;
    }

    public Date getDataServico() {
        return dataServico;
    }

    public void setDataServico(Date dataServico) {
        this.dataServico = dataServico;
    }

    public String getDescricaoServico() {
        return descricaoServico;
    }

    public void setDescricaoServico(String descricaoServico) {
        this.descricaoServico = descricaoServico;
    }

    public Long getNumeroNotaServico() {
        return numeroNotaServico;
    }

    public void setNumeroNotaServico(Long numeroNotaServico) {
        this.numeroNotaServico = numeroNotaServico;
    }

    public BigDecimal getValorServico() {
        return valorServico;
    }

    public void setValorServico(BigDecimal valorServico) {
        this.valorServico = valorServico;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public Date getDataResposta() {
        return dataResposta;
    }

    public void setDataResposta(Date dataResposta) {
        this.dataResposta = dataResposta;
    }

    public UsuarioSistema getUsuarioResposta() {
        return usuarioResposta;
    }

    public void setUsuarioResposta(UsuarioSistema usuarioResposta) {
        this.usuarioResposta = usuarioResposta;
    }

    public String getResposta() {
        return resposta;
    }

    public void setResposta(String resposta) {
        this.resposta = resposta;
    }
}
