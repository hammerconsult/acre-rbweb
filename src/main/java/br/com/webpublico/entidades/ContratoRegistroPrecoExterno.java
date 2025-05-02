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
@Table(name = "CONREGPRECOEXTERNO")
@Etiqueta("Contrato de Registro de Preço Externo")
public class ContratoRegistroPrecoExterno extends SuperEntidade implements ObjetoLicitatorioContrato {

    private static final Logger logger = LoggerFactory.getLogger(ContratoRegistroPrecoExterno.class);
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne
    @Etiqueta("Contrato")
    private Contrato contrato;

    @ManyToOne
    @JoinColumn(name = "REGSOLMATEXT_ID")
    private RegistroSolicitacaoMaterialExterno registroSolicitacaoMaterialExterno;

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

    public RegistroSolicitacaoMaterialExterno getRegistroSolicitacaoMaterialExterno() {
        return registroSolicitacaoMaterialExterno;
    }

    public void setRegistroSolicitacaoMaterialExterno(RegistroSolicitacaoMaterialExterno registroSolicitacaoMaterialExterno) {
        this.registroSolicitacaoMaterialExterno = registroSolicitacaoMaterialExterno;
    }

    @Override
    public String toString() {
        return "" + getRegistroSolicitacaoMaterialExterno();
    }

    @Override
    public boolean isRegistroDePrecos() {
        return true;
    }

    @Override
    public void transferirObjetoLicitatorioParaContrato() {
        try {
            getContrato().setObjeto(getRegistroSolicitacaoMaterialExterno().getObjeto());
            getContrato().setNumeroProcesso(getRegistroSolicitacaoMaterialExterno().getNumeroRegistro());
            getContrato().setAnoProcesso(getRegistroSolicitacaoMaterialExterno().getExercicioRegistro().getAno());
        } catch (NullPointerException npe) {
            logger.error("Dados não encontrado ao transferir objeto licitatório. ", npe);
        }
    }

    @Override
    public String getLocalDeEntrega() {
        return getRegistroSolicitacaoMaterialExterno().getSolicitacaoMaterialExterno().getUnidadeOrganizacional() + "";
    }

    @Override
    public ProcessoDeCompra getProcessoDeCompra() {
        return null;
    }

    @Override
    public SolicitacaoMaterial getSolicitacaoMaterial() {
        return null;
    }

    @Override
    public boolean isObras() {
        return false;
    }

    @Override
    public SubTipoObjetoCompra getSubTipoObjetoCompra() {
        try {
            return getRegistroSolicitacaoMaterialExterno().getSolicitacaoMaterialExterno().getItensDaSolicitacao().get(0).getSubTipoObjetoCompra();
        } catch (NullPointerException e) {
            return SubTipoObjetoCompra.NAO_APLICAVEL;
        }
    }
}
