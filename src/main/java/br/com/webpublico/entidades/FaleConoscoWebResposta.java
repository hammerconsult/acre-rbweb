package br.com.webpublico.entidades;

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
@Etiqueta("Fale Conosco - Resposta")
public class FaleConoscoWebResposta extends SuperEntidade implements Comparable<FaleConoscoWebResposta>, PossuidorArquivo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Etiqueta("Fale Conosco")
    @ManyToOne
    private FaleConoscoWeb faleConoscoWeb;

    @Obrigatorio
    @Etiqueta("Conteúdo")
    private String conteudo;

    @ManyToOne
    @Etiqueta("Usuário")
    private UsuarioSistema usuario;

    @Temporal(TemporalType.TIMESTAMP)
    @Etiqueta("Enviada Em")
    private Date enviadaEm;

    @Temporal(TemporalType.TIMESTAMP)
    @Etiqueta("Visualizada Em")
    private Date visualizadaEm;

    @Invisivel
    @OneToOne(cascade = CascadeType.ALL)
    private DetentorArquivoComposicao detentorArquivoComposicao;

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

    public UsuarioSistema getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioSistema usuario) {
        this.usuario = usuario;
    }

    public Date getEnviadaEm() {
        return enviadaEm;
    }

    public void setEnviadaEm(Date enviadaEm) {
        this.enviadaEm = enviadaEm;
    }

    public Date getVisualizadaEm() {
        return visualizadaEm;
    }

    public void setVisualizadaEm(Date visualizadaEm) {
        this.visualizadaEm = visualizadaEm;
    }

    @Override
    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    @Override
    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }

    public Boolean isVisualizada(){
        return visualizadaEm !=null;
    }

    public String getConteudoReduzido() {
        int tamanho = 67;
        return conteudo.length() > tamanho ? conteudo.substring(0, tamanho) + "..." : conteudo;
    }

    @Override
    public int compareTo(FaleConoscoWebResposta o) {
        if (o.getEnviadaEm() != null && this.enviadaEm != null) {
            return this.enviadaEm.compareTo(o.getEnviadaEm());
        }
        return 0;
    }
}
