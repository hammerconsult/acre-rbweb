package br.com.webpublico.entidades;

import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

@Entity
@Audited
@Etiqueta("Parecer do Processo de Isenção de IPTU")
public class ParecerProcIsencaoIPTU extends SuperEntidade implements PossuidorArquivo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Data do Parecer")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataParecer;
    @Etiqueta("Justificativa")
    private String justificativa;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Responsável pelo Parecer")
    @ManyToOne
    private UsuarioSistema usuarioParecer;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Solicitação de Isenção de IPTU")
    @ManyToOne
    private ProcessoIsencaoIPTU solicitacaoIsencao;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @Invisivel
    private DetentorArquivoComposicao detentorArquivo;

    public ParecerProcIsencaoIPTU() {
        this.detentorArquivo = new DetentorArquivoComposicao();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataParecer() {
        return dataParecer;
    }

    public void setDataParecer(Date dataParecer) {
        this.dataParecer = dataParecer;
    }

    public String getJustificativa() {
        return justificativa;
    }

    public void setJustificativa(String justificativa) {
        this.justificativa = justificativa;
    }

    public UsuarioSistema getUsuarioParecer() {
        return usuarioParecer;
    }

    public void setUsuarioParecer(UsuarioSistema usuarioParecer) {
        this.usuarioParecer = usuarioParecer;
    }

    public ProcessoIsencaoIPTU getSolicitacaoIsencao() {
        return solicitacaoIsencao;
    }

    public void setSolicitacaoIsencao(ProcessoIsencaoIPTU solicitacaoIsencao) {
        this.solicitacaoIsencao = solicitacaoIsencao;
    }

    @Override
    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivo;
    }

    @Override
    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivo) {
        this.detentorArquivo = detentorArquivo;
    }
}
