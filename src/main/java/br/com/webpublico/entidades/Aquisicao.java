package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoAquisicao;
import br.com.webpublico.enums.TipoBem;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Desenvolvimento
 * Date: 16/10/14
 * Time: 16:29
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
@Etiqueta("Efetivação de Aquisição")
public class Aquisicao extends SuperEntidade implements PossuidorArquivo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Etiqueta("Número")
    @Tabelavel
    @Pesquisavel
    private Long numero;

    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Temporal(TemporalType.DATE)
    @Etiqueta("Data da Aquisição")
    private Date dataDeAquisicao;

    @Obrigatorio
    @Etiqueta("Usuário")
    @ManyToOne
    @Tabelavel
    private UsuarioSistema usuario;

    @Obrigatorio
    @OneToOne
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Solicitação de Aquisição")
    private SolicitacaoAquisicao solicitacaoAquisicao;

    @Invisivel
    @OneToMany(mappedBy = "aquisicao", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ItemAquisicao> itensAquisicao;

    @Tabelavel
    @Enumerated(EnumType.STRING)
    @Etiqueta("Situação")
    private SituacaoAquisicao situacao;

    @Etiqueta("Tipo de Bem")
    @Enumerated(EnumType.STRING)
    private TipoBem tipoBem;

    @OneToOne(cascade = CascadeType.ALL)
    @Invisivel
    private DetentorArquivoComposicao detentorArquivoComposicao;

    @Obrigatorio
    @Enumerated(EnumType.STRING)
    @Etiqueta("Avaliar Solicitação")
    private SituacaoEfetivacao situacaoSolicitacao;

    public Aquisicao() {
        super();
        this.itensAquisicao = new ArrayList<>();
        this.situacao = SituacaoAquisicao.EM_ELABORACAO;
    }

    public SituacaoEfetivacao getSituacaoSolicitacao() {
        return situacaoSolicitacao;
    }

    public void setSituacaoSolicitacao(SituacaoEfetivacao situacaoSolicitacao) {
        this.situacaoSolicitacao = situacaoSolicitacao;
    }

    public SolicitacaoAquisicao getSolicitacaoAquisicao() {
        return solicitacaoAquisicao;
    }

    public void setSolicitacaoAquisicao(SolicitacaoAquisicao solicitacaoAquisicao) {
        this.solicitacaoAquisicao = solicitacaoAquisicao;
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

    public List<ItemAquisicao> getItensAquisicao() {
        return itensAquisicao;
    }

    public void setItensAquisicao(List<ItemAquisicao> itensAquisicao) {
        this.itensAquisicao = itensAquisicao;
    }

    public Date getDataDeAquisicao() {
        return dataDeAquisicao;
    }

    public void setDataDeAquisicao(Date dataDeAquisicao) {
        this.dataDeAquisicao = dataDeAquisicao;
    }

    public UsuarioSistema getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioSistema usuario) {
        this.usuario = usuario;
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

    @Override
    public String toString() {
        try {
            return " Nº " + numero + " - " + DataUtil.getDataFormatada(dataDeAquisicao) + " - " + usuario.getPessoaFisica();
        } catch (NullPointerException ne) {
            return "";
        }
    }

    public boolean isSolicitacaoAceita(){
        return SituacaoEfetivacao.ACEITA.equals(this.situacaoSolicitacao);
    }

    public boolean isSolicitacaoRecusada(){
        return SituacaoEfetivacao.REJEITADA.equals(this.situacaoSolicitacao);
    }


    public enum SituacaoEfetivacao{
        ACEITA("Aceita"),
        REJEITADA("Rejeitada");
        private String descricao;

        SituacaoEfetivacao(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }

        @Override
        public String toString() {
            return descricao;
        }
    }
}
