package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: JoãoPaulo
 * Date: 07/08/14
 * Time: 15:20
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited

@Table(name = "UNIDADESOLICITACAOMAT")
@Etiqueta("Unidade Solicitacão de Compra")
public class UnidadeSolicitacaoMaterial extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private SolicitacaoMaterial solicitacaoMaterial;

    @ManyToOne
    private UnidadeGestora unidadeGestora;

    @ManyToOne
    @Etiqueta("Unidade Participante")
    private ParticipanteIRP participanteIRP;

    public UnidadeSolicitacaoMaterial() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SolicitacaoMaterial getSolicitacaoMaterial() {
        return solicitacaoMaterial;
    }

    public void setSolicitacaoMaterial(SolicitacaoMaterial solicitacaoMaterial) {
        this.solicitacaoMaterial = solicitacaoMaterial;
    }

    public UnidadeGestora getUnidadeGestora() {
        return unidadeGestora;
    }

    public void setUnidadeGestora(UnidadeGestora unidadeGestora) {
        this.unidadeGestora = unidadeGestora;
    }

    public ParticipanteIRP getParticipanteIRP() {
        return participanteIRP;
    }

    public void setParticipanteIRP(ParticipanteIRP participanteIRP) {
        this.participanteIRP = participanteIRP;
    }
}
