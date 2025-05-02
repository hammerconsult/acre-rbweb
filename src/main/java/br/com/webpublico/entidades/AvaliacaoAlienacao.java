package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoEventoBem;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Desenvolvimento on 14/09/2017.
 */
@Entity
@Audited
@Etiqueta("Avaliação Alienação")
public class AvaliacaoAlienacao extends SuperEntidade implements PossuidorArquivo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Tabelavel
    @Pesquisavel
    @Etiqueta("Código")
    @CodigoGeradoAoConcluir
    private Long codigo;

    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Data da Avaliação")
    @Temporal(TemporalType.DATE)
    private Date dataAvaliacao;

    @Pesquisavel
    @Tabelavel
    @Etiqueta("Descrição")
    @Obrigatorio
    private String descricao;

    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Responsável")
    @ManyToOne
    private UsuarioSistema responsavel;

    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Situação")
    @Enumerated(EnumType.STRING)
    private SituacaoEventoBem situacao;

    @OneToOne(cascade = CascadeType.ALL)
    @Invisivel
    private DetentorArquivoComposicao detentorArquivoComposicao;

    @Invisivel
    @OneToMany(mappedBy = "avaliacaoAlienacao")
    private List<LoteAvaliacaoAlienacao> lotes;

    @Version
    private Long versao;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataVersao;

    public AvaliacaoAlienacao() {
        super();
        dataVersao = new Date();
        lotes = new ArrayList<>();
        situacao = SituacaoEventoBem.EM_ELABORACAO;
        detentorArquivoComposicao = new DetentorArquivoComposicao();
    }

    public Date getDataVersao() {
        return dataVersao;
    }

    public void setDataVersao(Date dataVersao) {
        this.dataVersao = dataVersao;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataAvaliacao() {
        return dataAvaliacao;
    }

    public void setDataAvaliacao(Date dataAvaliacao) {
        this.dataAvaliacao = dataAvaliacao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public UsuarioSistema getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(UsuarioSistema responsavel) {
        this.responsavel = responsavel;
    }

    public List<LoteAvaliacaoAlienacao> getLotes() {
        return lotes;
    }

    public void setLotes(List<LoteAvaliacaoAlienacao> lotes) {
        this.lotes = lotes;
    }

    public SituacaoEventoBem getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoEventoBem situacao) {
        this.situacao = situacao;
    }

    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }

    public Boolean emElaboracao() {
        return SituacaoEventoBem.EM_ELABORACAO.equals(situacao);
    }

    public Boolean isConcluida() {
        return SituacaoEventoBem.CONCLUIDO.equals(situacao);
    }

    @Override
    public void realizarValidacoes() {
        super.realizarValidacoes();
        ValidacaoException ve = new ValidacaoException();
        ve.lancarException();
    }

    @Override
    public String toString() {
        String retorno = "";
        if(codigo != null){
            retorno = codigo.toString();
        }
        if(descricao != null){
            retorno = retorno + " " +descricao;
        }
        return retorno;
    }
}
