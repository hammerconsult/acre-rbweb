package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoFaleConosco;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Length;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

@Entity
@Audited
@Etiqueta("Fale Conosco - Ocorrência")
public class FaleConoscoWebOcorrencia extends SuperEntidade implements Comparable<FaleConoscoWebOcorrencia>, PossuidorArquivo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Etiqueta("Fale Conosco")
    @ManyToOne
    private FaleConoscoWeb faleConoscoWeb;

    @Obrigatorio
    @Etiqueta("Conteúdo")
    private String conteudo;

    @Enumerated(EnumType.STRING)
    private SituacaoFaleConosco situacao;

    @Temporal(TemporalType.TIMESTAMP)
    @Etiqueta("Data da Ocorrência")
    private Date dataOcorrencia;

    @ManyToOne
    @Etiqueta("Usuário")
    private UsuarioSistema usuario;

    @Invisivel
    @OneToOne(cascade = CascadeType.ALL)
    private DetentorArquivoComposicao detentorArquivoComposicao;

    public UsuarioSistema getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioSistema usuario) {
        this.usuario = usuario;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public FaleConoscoWeb getFaleConoscoWeb() {
        return faleConoscoWeb;
    }

    public void setFaleConoscoWeb(FaleConoscoWeb faleConoscoWeb) {
        this.faleConoscoWeb = faleConoscoWeb;
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }

    public SituacaoFaleConosco getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoFaleConosco situacao) {
        this.situacao = situacao;
    }

    public Date getDataOcorrencia() {
        return dataOcorrencia;
    }

    public void setDataOcorrencia(Date dataOcorencia) {
        this.dataOcorrencia = dataOcorencia;
    }

    @Override
    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    @Override
    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }

    public String getConteudoReduzido() {
        int tamanho = 67;
        return conteudo.length() > tamanho ? conteudo.substring(0, tamanho) + "..." : conteudo;
    }

    @Override
    public int compareTo(FaleConoscoWebOcorrencia o) {
        if (o.getDataOcorrencia() != null && this.dataOcorrencia != null) {
            return this.dataOcorrencia.compareTo(o.getDataOcorrencia());
        }
        return 0;
    }
}
