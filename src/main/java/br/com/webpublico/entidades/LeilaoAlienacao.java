package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoAlienacao;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 12/11/14
 * Time: 14:27
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
@Etiqueta("Efetivação da Alienação")
public class LeilaoAlienacao extends SuperEntidade implements PossuidorArquivo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Pesquisavel
    @Tabelavel
    @Etiqueta("Código")
    private Long codigo;

    @Pesquisavel
    @Tabelavel
    @Etiqueta("Data Efetivação")
    @Obrigatorio
    @Temporal(TemporalType.DATE)
    private Date dataEfetivacao;

    @Pesquisavel
    @Tabelavel
    @Etiqueta("Responsável")
    @Obrigatorio
    @ManyToOne
    private UsuarioSistema responsavel;

    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Avaliação da Alienação")
    @ManyToOne
    private AvaliacaoAlienacao avaliacaoAlienacao;

    @Pesquisavel
    @Tabelavel
    @Etiqueta("Descrição")
    @Obrigatorio
    private String descricao;

    @OneToMany(mappedBy = "leilaoAlienacao")
    @Invisivel
    private List<LeilaoAlienacaoLote> lotesLeilaoAlienacao;

    @OneToOne(cascade = CascadeType.ALL)
    @Invisivel
    private DetentorArquivoComposicao detentorArquivoComposicao;

    @Enumerated(EnumType.STRING)
    @Etiqueta("Situação")
    private SituacaoAlienacao situacaoAlienacao;

    public LeilaoAlienacao() {
        super();
        situacaoAlienacao = SituacaoAlienacao.EM_ELABORACAO;
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

    public AvaliacaoAlienacao getAvaliacaoAlienacao() {
        return avaliacaoAlienacao;
    }

    public void setAvaliacaoAlienacao(AvaliacaoAlienacao avaliacaoAlienacao) {
        this.avaliacaoAlienacao = avaliacaoAlienacao;
    }

    public List<LeilaoAlienacaoLote> getLotesLeilaoAlienacao() {
        return lotesLeilaoAlienacao;
    }

    public void setLotesLeilaoAlienacao(List<LeilaoAlienacaoLote> lotesLeilaoAlienacao) {
        this.lotesLeilaoAlienacao = lotesLeilaoAlienacao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Date getDataEfetivacao() {
        return dataEfetivacao;
    }

    public void setDataEfetivacao(Date dataEfetivacao) {
        this.dataEfetivacao = dataEfetivacao;
    }

    public UsuarioSistema getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(UsuarioSistema responsavel) {
        this.responsavel = responsavel;
    }

    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }

    public SituacaoAlienacao getSituacaoAlienacao() {
        return situacaoAlienacao;
    }

    public void setSituacaoAlienacao(SituacaoAlienacao situacaoAlienacao) {
        this.situacaoAlienacao = situacaoAlienacao;
    }

    @Override
    public String toString() {
        String retorno = "";
        if (codigo != null) {
            retorno = codigo.toString() + " - ";
        }
        if (descricao != null) {
            retorno = retorno + descricao + " - ";
        }
        if (dataEfetivacao != null) {
            retorno = retorno + DataUtil.getDataFormatada(dataEfetivacao);
        }
        return retorno;
    }

    public boolean isEmElaboracao(){
        return SituacaoAlienacao.EM_ELABORACAO.equals(this.situacaoAlienacao);
    }

    public boolean isFinalizada(){
        return SituacaoAlienacao.FINALIZADA.equals(this.situacaoAlienacao);
    }
}
