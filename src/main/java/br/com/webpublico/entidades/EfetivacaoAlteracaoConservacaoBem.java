package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoBem;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Length;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;
import java.util.List;


/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 29/10/14
 * Time: 16:42
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
@Etiqueta(value = "Efetivação de Alteração de Conservação do Bem")
@Table(name = "EFETIVACAOALTERACAOCONSBEM")
public class EfetivacaoAlteracaoConservacaoBem extends SuperEntidade implements PossuidorArquivo {

    @Id
    @GeneratedValue
    private Long id;

    @Etiqueta("Código")
    private Long codigo;

    @Temporal(TemporalType.DATE)
    @Etiqueta("Data da Efetivação")
    private Date dataEfetivacao;

    @Obrigatorio
    @ManyToOne
    @Etiqueta("Solicitação de Alteração Conservação")
    private SolicitacaoAlteracaoConservacaoBem solicitacaoAlteracaoConsBem;

    @Etiqueta("Descrição")
    @Length(maximo = 3000)
    private String descricao;

    @Etiqueta("Tipo do Bem")
    @Enumerated(EnumType.STRING)
    private TipoBem tipoBem;

    @ManyToOne
    @Etiqueta("Responsável")
    private UsuarioSistema responsavel;

    @OneToOne(cascade = CascadeType.ALL)
    @Invisivel
    private DetentorArquivoComposicao detentorArquivoComposicao;

    @Invisivel
    @OneToMany(mappedBy = "efetivacaoAlteracaoConsBem")
    private List<AlteracaoConservacaoBem> itens;

    public EfetivacaoAlteracaoConservacaoBem() {
        itens = Lists.newArrayList();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public UsuarioSistema getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(UsuarioSistema responsavel) {
        this.responsavel = responsavel;
    }

    public Date getDataEfetivacao() {
        return dataEfetivacao;
    }

    public void setDataEfetivacao(Date dataLote) {
        this.dataEfetivacao = dataLote;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String motivo) {
        this.descricao = motivo;
    }

    public SolicitacaoAlteracaoConservacaoBem getSolicitacaoAlteracaoConsBem() {
        return solicitacaoAlteracaoConsBem;
    }

    public void setSolicitacaoAlteracaoConsBem(SolicitacaoAlteracaoConservacaoBem solicitacaoAlteracaoConsBem) {
        this.solicitacaoAlteracaoConsBem = solicitacaoAlteracaoConsBem;
    }

    @Override
    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    @Override
    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }

    public TipoBem getTipoBem() {
        return tipoBem;
    }

    public void setTipoBem(TipoBem tipoBem) {
        this.tipoBem = tipoBem;
    }


    public List<AlteracaoConservacaoBem> getItens() {
        return itens;
    }

    public void setItens(List<AlteracaoConservacaoBem> alteracaoConservacaoBens) {
        this.itens = alteracaoConservacaoBens;
    }
}
