/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.SubTipoObjetoCompra;
import br.com.webpublico.interfaces.ObjetoLicitatorioContrato;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;

/**
 * @author Felipe Marinzeck
 */
@Entity
@Audited
@Table(name = "CONLICITACAO")
@Etiqueta("Contrato de Licitação")
public class ContratoLicitacao extends SuperEntidade implements ObjetoLicitatorioContrato {

    private static final Logger logger = LoggerFactory.getLogger(ContratoLicitacao.class);
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToOne
    private Contrato contrato;
    @ManyToOne
    private Licitacao licitacao;
    @ManyToOne
    private SolicitacaoMaterialExterno solicitacaoMaterialExterno;
    @ManyToOne
    private ParticipanteIRP participanteIRP;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Contrato getContrato() {
        return contrato;
    }

    public void setContrato(Contrato contrato) {
        this.contrato = contrato;
    }

    public Licitacao getLicitacao() {
        return licitacao;
    }

    public void setLicitacao(Licitacao licitacao) {
        this.licitacao = licitacao;
    }

    public SolicitacaoMaterialExterno getSolicitacaoMaterialExterno() {
        return solicitacaoMaterialExterno;
    }

    public void setSolicitacaoMaterialExterno(SolicitacaoMaterialExterno solicitacaoMaterialExterno) {
        this.solicitacaoMaterialExterno = solicitacaoMaterialExterno;
    }

    public ParticipanteIRP getParticipanteIRP() {
        return participanteIRP;
    }

    public void setParticipanteIRP(ParticipanteIRP participanteIRP) {
        this.participanteIRP = participanteIRP;
    }

    @Override
    public String toString() {
        if (getContrato().isDeAdesaoAtaRegistroPrecoInterna()) {
            return getSolicitacaoMaterialExterno().toString();
        }
        if (getContrato().isDeIrp()) {
            return getParticipanteIRP().toString();
        }
        return getLicitacao().toString();
    }

    @Override
    public boolean isRegistroDePrecos() {
        return getLicitacao().isRegistroDePrecos();
    }

    @Override
    public void transferirObjetoLicitatorioParaContrato() {
        try {
            getContrato().setObjeto(getLicitacao().getResumoDoObjeto());
            getContrato().setRegimeExecucao(getLicitacao().getRegimeDeExecucao() != null ? getLicitacao().getRegimeDeExecucao().getDescricao() : "");
            getContrato().setNumeroProcesso(getLicitacao().getProcessoDeCompra().getNumero());
            getContrato().setAnoProcesso(getLicitacao().getProcessoDeCompra().getExercicio().getAno());
        } catch (NullPointerException npe) {
            logger.error("Dados não encontrado ao transferir objeto licitatório. ", npe);
        }
    }

    @Override
    public String getLocalDeEntrega() {
        return getLicitacao().getLocalDeEntrega();
    }

    @Override
    public SolicitacaoMaterial getSolicitacaoMaterial() {
        return getProcessoDeCompra().getSolicitacaoMaterial();
    }

    @Override
    public ProcessoDeCompra getProcessoDeCompra() {
        return getLicitacao().getProcessoDeCompra();
    }

    @Override
    public boolean isObras() {
        return getProcessoDeCompra().getTipoSolicitacao().isObraServicoEngenharia();
    }

    @Override
    public SubTipoObjetoCompra getSubTipoObjetoCompra() {
        return getSolicitacaoMaterial().getSubTipoObjetoCompra();
    }
}
