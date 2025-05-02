package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoArquivoTransferenciaMovBCI;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
@Etiqueta("Arquivo de Transferencia dos Movimentos do Cadastro Imobiliário")
public class ArquivoTransferenciaMovBCI extends SuperEntidade implements PossuidorArquivo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Enumerated(EnumType.STRING)
    private TipoArquivoTransferenciaMovBCI tipoArquivo;
    @ManyToOne
    private SolicitacaoTransfMovBCI solicitacao;
    @ManyToOne
    private ParecerTransferenciaMovBCI parecer;
    @OneToOne(cascade = CascadeType.ALL)
    @Invisivel
    private DetentorArquivoComposicao detentorArquivoComposicao;

    public ArquivoTransferenciaMovBCI() {
        this.detentorArquivoComposicao = new DetentorArquivoComposicao();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SolicitacaoTransfMovBCI getSolicitacao() {
        return solicitacao;
    }

    public void setSolicitacao(SolicitacaoTransfMovBCI solicitacao) {
        this.solicitacao = solicitacao;
    }

    public ParecerTransferenciaMovBCI getParecer() {
        return parecer;
    }

    public void setParecer(ParecerTransferenciaMovBCI parecer) {
        this.parecer = parecer;
    }

    @Override
    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    @Override
    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }

    public TipoArquivoTransferenciaMovBCI getTipoArquivo() {
        return tipoArquivo;
    }

    public void setTipoArquivo(TipoArquivoTransferenciaMovBCI tipoArquivo) {
        this.tipoArquivo = tipoArquivo;
    }
}
