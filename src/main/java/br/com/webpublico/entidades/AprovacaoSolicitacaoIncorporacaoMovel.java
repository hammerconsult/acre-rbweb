package br.com.webpublico.entidades;

import br.com.webpublico.enums.administrativo.SituacaoAprovacao;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Desenvolvimento on 02/02/2016.
 */

@Entity
@Audited
@Table(name = "APROVACAOSOLINCORPMOVEL")
@Etiqueta("Aprovação de Solicitação de Incorporação Móvel ")
public class AprovacaoSolicitacaoIncorporacaoMovel extends SuperEntidade implements PossuidorArquivo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Solicitação de Incorporação")
    private SolicitacaoIncorporacaoMovel solicitacao;

    @Etiqueta("Observação")
    @Pesquisavel
    @Tabelavel
    private String observacao;

    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Responsável")
    @ManyToOne
    private UsuarioSistema responsavel;

    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Data da Aprovação")
    @Temporal(TemporalType.DATE)
    private Date dataAprovacao;

    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Situação")
    private SituacaoAprovacao situacaoAprovacao;

    @OneToOne(cascade = CascadeType.ALL)
    @Invisivel
    private DetentorArquivoComposicao detentorArquivoComposicao;

    private String motivo;

    public AprovacaoSolicitacaoIncorporacaoMovel() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SolicitacaoIncorporacaoMovel getSolicitacao() {
        return solicitacao;
    }

    public void setSolicitacao(SolicitacaoIncorporacaoMovel solicitacao) {
        this.solicitacao = solicitacao;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public UsuarioSistema getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(UsuarioSistema responsavel) {
        this.responsavel = responsavel;
    }

    public Date getDataAprovacao() {
        return dataAprovacao;
    }

    public void setDataAprovacao(Date dataAprovacao) {
        this.dataAprovacao = dataAprovacao;
    }

    public SituacaoAprovacao getSituacaoAprovacao() {
        return situacaoAprovacao;
    }

    public void setSituacaoAprovacao(SituacaoAprovacao situacaoAprovacao) {
        this.situacaoAprovacao = situacaoAprovacao;
    }

    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

}
