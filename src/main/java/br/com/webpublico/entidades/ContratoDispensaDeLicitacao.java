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
@Table(name = "CONDISPENSALICITACAO")
@Etiqueta("Contrato de Dispensa de Licitação")
public class ContratoDispensaDeLicitacao extends SuperEntidade implements ObjetoLicitatorioContrato {

    private static final Logger logger = LoggerFactory.getLogger(ContratoLicitacao.class);
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne
    @Etiqueta("Contrato")
    private Contrato contrato;

    @ManyToOne
    @Etiqueta("Dispensa de Licitação")
    private DispensaDeLicitacao dispensaDeLicitacao;

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

    public DispensaDeLicitacao getDispensaDeLicitacao() {
        return dispensaDeLicitacao;
    }

    public void setDispensaDeLicitacao(DispensaDeLicitacao dispensaDeLicitacao) {
        this.dispensaDeLicitacao = dispensaDeLicitacao;
    }

    @Override
    public String toString() {
        return "" + getDispensaDeLicitacao();
    }

    @Override
    public boolean isRegistroDePrecos() {
        return false;
    }

    @Override
    public void transferirObjetoLicitatorioParaContrato() {
        try {
            getContrato().setObjeto(getDispensaDeLicitacao().getResumoDoObjeto());
            getContrato().setRegimeExecucao(getDispensaDeLicitacao().getRegimeDeExecucao().getDescricao());
            getContrato().setNumeroProcesso(getDispensaDeLicitacao().getProcessoDeCompra().getNumero());
            getContrato().setAnoProcesso(getDispensaDeLicitacao().getProcessoDeCompra().getExercicio().getAno());
        } catch (NullPointerException npe) {
            logger.error("Dados não encontrado ao transferir objeto licitatório. ", npe);
        }
    }

    @Override
    public String getLocalDeEntrega() {
        return getDispensaDeLicitacao().getLocalDeEntrega();
    }

    @Override
    public SolicitacaoMaterial getSolicitacaoMaterial() {
        return getDispensaDeLicitacao().getProcessoDeCompra().getSolicitacaoMaterial();
    }

    @Override
    public boolean isObras() {
        return dispensaDeLicitacao.getProcessoDeCompra().getSolicitacaoMaterial().getTipoSolicitacao().isObraServicoEngenharia();
    }

    @Override
    public ProcessoDeCompra getProcessoDeCompra() {
        return getDispensaDeLicitacao().getProcessoDeCompra();
    }

    @Override
    public SubTipoObjetoCompra getSubTipoObjetoCompra() {
        return getDispensaDeLicitacao().getProcessoDeCompra().getSolicitacaoMaterial().getSubTipoObjetoCompra();
    }
}
