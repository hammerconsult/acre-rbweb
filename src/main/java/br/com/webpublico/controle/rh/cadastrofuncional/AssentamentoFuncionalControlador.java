package br.com.webpublico.controle.rh.cadastrofuncional;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.ContratoFP;
import br.com.webpublico.entidades.rh.cadastrofuncional.AssentamentoFuncional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ContratoFPFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.rh.cadastrofuncional.AssentamentoFuncionalFacade;
import br.com.webpublico.util.ConverterGenerico;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

@ManagedBean(name = "assentamentoFuncionalControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-assentamento", pattern = "/assentamento-funcional/novo/", viewId = "/faces/rh/cadastrofuncional/assentamento-funcional/edita.xhtml"),
    @URLMapping(id = "editar-assentamento", pattern = "/assentamento-funcional/editar/#{assentamentoFuncionalControlador.id}/", viewId = "/faces/rh/cadastrofuncional/assentamento-funcional/edita.xhtml"),
    @URLMapping(id = "listar-assentamento", pattern = "/assentamento-funcional/listar/", viewId = "/faces/rh/cadastrofuncional/assentamento-funcional/lista.xhtml"),
    @URLMapping(id = "ver-assentamento", pattern = "/assentamento-funcional/ver/#{assentamentoFuncionalControlador.id}/", viewId = "/faces/rh/cadastrofuncional/assentamento-funcional/visualizar.xhtml")
})
public class AssentamentoFuncionalControlador extends PrettyControlador<AssentamentoFuncional> implements CRUD, Serializable {

    @EJB
    private AssentamentoFuncionalFacade assentamentoFuncionalFacade;

    private ConverterGenerico converterContratoFP;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;

    public AssentamentoFuncionalControlador() {
        super(AssentamentoFuncional.class);
    }

    private void validar() {
        ValidacaoException validacaoException = new ValidacaoException();
        if (selecionado.getContratoFP() == null) {
            validacaoException.adicionarMensagemDeCampoObrigatorio("Favor informar o servidor");
        }
        if (selecionado.getAtoLegal() == null) {
            validacaoException.adicionarMensagemDeCampoObrigatorio("Favor informar o ato legal");
        }
        if (selecionado.getHistorico() == null || selecionado.getHistorico().isEmpty()) {
            validacaoException.adicionarMensagemDeCampoObrigatorio("Favor informar o historico");
        } else if (selecionado.getHistorico().length() > 3000) {
            validacaoException.adicionarMensagemDeOperacaoNaoPermitida("O histórico não pode ultrapassar 3000 caracteres");
        }
        validacaoException.lancarException();
    }

    @Override
    public void salvar() {
        try {
            validar();
            if (selecionado.getSequencial() == null || selecionado.getSequencial() == 0) {
                selecionado.setSequencial(assentamentoFuncionalFacade.ultimoNumeroMaisUm(selecionado.getContratoFP()));
            }
            super.salvar();
        } catch (ValidacaoException ve) {
            selecionado.setSequencial(0L);
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }


    @URLAction(mappingId = "novo-assentamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setDataCadastro(sistemaFacade.getDataOperacao());
    }

    @URLAction(mappingId = "ver-assentamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editar-assentamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }


    public ConverterGenerico getConverterContratoFP() {
        if (converterContratoFP == null) {
            converterContratoFP = new ConverterGenerico(ContratoFP.class, assentamentoFuncionalFacade);
        }
        return converterContratoFP;
    }

    public List<ContratoFP> completaContratoFPs(String parte) {
        return contratoFPFacade.buscarContratos(parte.trim());
    }

    public void setConverterContratoFP(ConverterGenerico converterContratoFP) {
        this.converterContratoFP = converterContratoFP;
    }

    @Override
    public AbstractFacade getFacede() {
        return assentamentoFuncionalFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/assentamento-funcional/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }
}
