package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.ModuloTipoDoctoOficial;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ParametrosOttFacade;
import br.com.webpublico.negocios.TipoDoctoOficialFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author octavio
 */
@ManagedBean(name = "parametrosOttControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoParametrosOtt", pattern = "/parametros-ott/novo/",
        viewId = "/faces/tributario/rbtrans/parametros/parametrosott/edita.xhtml"),
    @URLMapping(id = "editarParametrosOtt", pattern = "/parametros-ott/editar/#{parametrosOttControlador.id}/",
        viewId = "/faces/tributario/rbtrans/parametros/parametrosott/edita.xhtml"),
    @URLMapping(id = "listarParametrosOtt", pattern = "/parametros-ott/listar/",
        viewId = "/faces/tributario/rbtrans/parametros/parametrosott/lista.xhtml"),
    @URLMapping(id = "verParametrosOtt", pattern = "/parametros-ott/ver/#{parametrosOttControlador.id}/",
        viewId = "/faces/tributario/rbtrans/parametros/parametrosott/visualizar.xhtml")
})
public class ParametrosOttControlador extends PrettyControlador<ParametrosOtt> implements Serializable, CRUD {

    @EJB
    private ParametrosOttFacade parametrosOttFacade;
    @EJB
    private TipoDoctoOficialFacade tipoDoctoOficialFacade;
    private DocumentoCredenciamentoOtt documentoCredenciamentoOtt;
    private DocumentoCondutorOtt documentoCondutorOtt;
    private DocumentoVeiculoOtt documentoVeiculoOtt;
    private CNAE cnae;

    public ParametrosOttControlador() {
        super(ParametrosOtt.class);
    }

    public DocumentoCredenciamentoOtt getDocumentoCredenciamentoOtt() {
        return documentoCredenciamentoOtt;
    }

    public void setDocumentoCredenciamentoOtt(DocumentoCredenciamentoOtt documentoCredenciamentoOtt) {
        this.documentoCredenciamentoOtt = documentoCredenciamentoOtt;
    }

    public DocumentoCondutorOtt getDocumentoCondutorOtt() {
        return documentoCondutorOtt;
    }

    public void setDocumentoCondutorOtt(DocumentoCondutorOtt documentoCondutorOtt) {
        this.documentoCondutorOtt = documentoCondutorOtt;
    }

    public DocumentoVeiculoOtt getDocumentoVeiculoOtt() {
        return documentoVeiculoOtt;
    }

    public void setDocumentoVeiculoOtt(DocumentoVeiculoOtt documentoVeiculoOtt) {
        this.documentoVeiculoOtt = documentoVeiculoOtt;
    }

    public CNAE getCnae() {
        return cnae;
    }

    public void setCnae(CNAE cnae) {
        this.cnae = cnae;
    }

    @Override
    public void salvar() {
        try {
            validarCampos();
            super.salvar();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    @URLAction(mappingId = "novoParametrosOtt", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        if (!isDesabilitarBotaoNovo()) {
            redireciona();
            FacesUtil.addOperacaoNaoRealizada("Já existe um parâmetro da OTT cadastrado!");
        }
        super.novo();
        this.documentoCredenciamentoOtt = new DocumentoCredenciamentoOtt();
        this.documentoCondutorOtt = new DocumentoCondutorOtt();
        this.documentoVeiculoOtt = new DocumentoVeiculoOtt();
    }

    @URLAction(mappingId = "editarParametrosOtt", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        this.documentoCredenciamentoOtt = new DocumentoCredenciamentoOtt();
        this.documentoCondutorOtt = new DocumentoCondutorOtt();
        this.documentoVeiculoOtt = new DocumentoVeiculoOtt();
    }

    @URLAction(mappingId = "verParametrosOtt", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/parametros-ott/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return parametrosOttFacade;
    }

    public List<DigitoVencimento> vencimentosVistoria() {
        return selecionado.getVencimentos().stream().filter(dv -> DigitoVencimento.TipoDigitoVencimento.VISTORIA_VEICULO_OTT.equals(dv.getTipoDigitoVencimento())).collect(Collectors.toList());
    }

    public List<DigitoVencimento> vencimentosCertificado() {
        return selecionado.getVencimentos().stream().filter(dv -> DigitoVencimento.TipoDigitoVencimento.CERTIFICADO_VEICULO_OTT.equals(dv.getTipoDigitoVencimento())).collect(Collectors.toList());
    }

    public List<Divida> completarDivida(String parte) {
        return parametrosOttFacade.getDividaFacade().listaFiltrandoDividas(parte.trim(), "descricao");
    }

    public List<Tributo> completarTributo(String parte) {
        return parametrosOttFacade.getTributoFacade().listaFiltrando(parte.trim(), "descricao");
    }

    public boolean isDesabilitarBotaoNovo() {
        return parametrosOttFacade.isTemParametro();
    }

    public List<TipoDoctoOficial> completarTipoDocOficialOtt(String parte) {
        return tipoDoctoOficialFacade.tipoDoctoPorModulo(parte, ModuloTipoDoctoOficial.RBTRANS_CERTIFICADO_OTT);
    }

    public List<TipoDoctoOficial> completarTipoDocOficialCondutor(String parte) {
        return tipoDoctoOficialFacade.tipoDoctoPorModulo(parte, ModuloTipoDoctoOficial.RBTRANS_CERTIFICADO_CONDUTOR_OTT);
    }

    public List<TipoDoctoOficial> completarTipoDocOficialRenovacao(String parte) {
        return tipoDoctoOficialFacade.tipoDoctoPorModulo(parte, ModuloTipoDoctoOficial.RBTRANS_CERTIFICADO_RENOVACAO_OTT);
    }

    public void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getDividaVistoriaVeiculoOtt() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Selecione a taxa de vistoria do veículo OTT!");
        }
        if (selecionado.getTributoVistoriaVeiculoOtt() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Selecione o tributo de vistoria do veículo OTT!");
        }
        if (selecionado.getCertificadoCredenciamento() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Selecione o certificado anual de credenciamento da OTT!");
        }
        if (selecionado.getCertificadoAutorizacao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Selecione o certificado de autorização do condutor!");
        }
        for (DigitoVencimento vencimento : selecionado.getVencimentos()) {
            boolean isVencimentoVistoria = DigitoVencimento.TipoDigitoVencimento.VISTORIA_VEICULO_OTT.equals(vencimento.getTipoDigitoVencimento());
            String restanteMensagemValidacao = (isVencimentoVistoria ? "da " : "do ") + vencimento.getTipoDigitoVencimento().getDescricao() + " do digito " + vencimento.getDigito();
            if (vencimento.getDia() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("Informe o dia do vencimento " + restanteMensagemValidacao + ".");
            } else if (vencimento.getDia() > 31 || vencimento.getDia() <= 0) {
                ve.adicionarMensagemDeCampoObrigatorio("O dia do vencimento " + restanteMensagemValidacao + " deve estar entre 1 e 31.");
            }
            if (vencimento.getMes() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("Informe o mês do vencimento " + restanteMensagemValidacao + ".");
            } else if (vencimento.getMes() > 12 || vencimento.getMes() <= 0) {
                ve.adicionarMensagemDeCampoObrigatorio("O mês do vencimento " + restanteMensagemValidacao + " deve estar entre 1 e 12.");
            }
        }
        ve.lancarException();
    }

    public void editarDocumentoCredenciamentoOtt(DocumentoCredenciamentoOtt documentoCredenciamentoOtt) {
        this.documentoCredenciamentoOtt = documentoCredenciamentoOtt;
        removerDocumentoCredenciamentoOtt(this.documentoCredenciamentoOtt);
    }

    public void removerDocumentoCredenciamentoOtt(DocumentoCredenciamentoOtt documentoCredenciamentoOtt) {
        selecionado.getDocumentosCredenciamento().remove(documentoCredenciamentoOtt);
    }

    public void adicionarDocumentoCredenciamentoOtt() {
        try {
            validarDocumentoCredenciamentoOtt();
            documentoCredenciamentoOtt.setParametrosOtt(selecionado);
            selecionado.getDocumentosCredenciamento().add(documentoCredenciamentoOtt);
            documentoCredenciamentoOtt = new DocumentoCredenciamentoOtt();
        } catch (ValidacaoException va) {
            FacesUtil.printAllFacesMessages(va.getMensagens());
        }
    }

    public void validarDocumentoCredenciamentoOtt() {
        Util.validarCampos(documentoCredenciamentoOtt);
        if (selecionado.hasDocumentoCredenciamentoOtt(documentoCredenciamentoOtt)) {
            throw new ValidacaoException("Já existe um documento adicionado com essa mesma descrição.");
        }
    }

    public void editarDocumentoCondutorOtt(DocumentoCondutorOtt documentoCondutorOtt) {
        this.documentoCondutorOtt = documentoCondutorOtt;
        removerDocumentoCondutorOtt(this.documentoCondutorOtt);
    }

    public void removerDocumentoCondutorOtt(DocumentoCondutorOtt documentoCondutorOtt) {
        selecionado.getDocumentosCondutor().remove(documentoCondutorOtt);
    }

    public void adicionarDocumentoCondutorOtt() {
        try {
            validarDocumentoCondutorOtt();
            documentoCondutorOtt.setParametrosOtt(selecionado);
            selecionado.getDocumentosCondutor().add(documentoCondutorOtt);
            documentoCondutorOtt = new DocumentoCondutorOtt();
        } catch (ValidacaoException va) {
            FacesUtil.printAllFacesMessages(va.getMensagens());
        }
    }

    public void validarDocumentoCondutorOtt() {
        Util.validarCampos(documentoCondutorOtt);
        if (selecionado.hasDocumentoCondutorOtt(documentoCondutorOtt)) {
            throw new ValidacaoException("Já existe um documento adicionado com essa mesma descrição.");
        }
    }

    public void editarDocumentoVeiculoOtt(DocumentoVeiculoOtt documentoVeiculoOtt) {
        this.documentoVeiculoOtt = documentoVeiculoOtt;
        removerDocumentoVeiculoOtt(this.documentoVeiculoOtt);
    }

    public void removerDocumentoVeiculoOtt(DocumentoVeiculoOtt documentoVeiculoOtt) {
        selecionado.getDocumentosVeiculo().remove(documentoVeiculoOtt);
    }

    public void adicionarDocumentoVeiculoOtt() {
        try {
            validarDocumentoVeiculoOtt();
            documentoVeiculoOtt.setParametrosOtt(selecionado);
            selecionado.getDocumentosVeiculo().add(documentoVeiculoOtt);
            documentoVeiculoOtt = new DocumentoVeiculoOtt();
        } catch (ValidacaoException va) {
            FacesUtil.printAllFacesMessages(va.getMensagens());
        }
    }

    public void validarDocumentoVeiculoOtt() {
        Util.validarCampos(documentoVeiculoOtt);
        if (selecionado.hasDocumentoVeiculoOtt(documentoVeiculoOtt)) {
            throw new ValidacaoException("Já existe um documento adicionado com essa mesma descrição.");
        }
    }

    public void adicionarCnae() {
        try {
            validarCnae();
            ParametrosOttCNAE parametrosOttCNAE = new ParametrosOttCNAE();
            parametrosOttCNAE.setParametrosOtt(selecionado);
            parametrosOttCNAE.setCnae(cnae);
            selecionado.getCnaes().add(parametrosOttCNAE);
            cnae = null;
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        }
    }

    private void validarCnae() {
        if (cnae == null) {
            throw new ValidacaoException("O campo CNAE deve ser informado.");
        }
        if (selecionado.getCnaes().stream()
            .anyMatch(parametrosOttCNAE -> parametrosOttCNAE.getCnae().getId().equals(cnae.getId()))) {
            throw new ValidacaoException("O CNAE informado já está adicionado.");
        }
    }

    public void removerCnae(ParametrosOttCNAE parametrosOttCNAE) {
        selecionado.getCnaes().remove(parametrosOttCNAE);
    }
}


