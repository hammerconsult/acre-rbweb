/*
 * Codigo gerado automaticamente em Mon Aug 08 13:59:17 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.EventoFP;
import br.com.webpublico.entidades.ModalidadeBeneficioFP;
import br.com.webpublico.entidades.ModalidadeBeneficioFPFacade;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.EventoFPFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.util.Util;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import java.io.Serializable;
import java.util.List;
import javax.faces.bean.ViewScoped;

@ManagedBean(name = "modalidadeBeneficioFPControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoModalidadeBeneficio", pattern = "/modalidadebeneficio/novo/", viewId = "/faces/rh/administracaodepagamento/modalidadebeneficiofp/edita.xhtml"),
    @URLMapping(id = "editarModalidadeBeneficio", pattern = "/modalidadebeneficio/editar/#{modalidadeBeneficioFPControlador.id}/", viewId = "/faces/rh/administracaodepagamento/modalidadebeneficiofp/edita.xhtml"),
    @URLMapping(id = "listarModalidadeBeneficio", pattern = "/modalidadebeneficio/listar/", viewId = "/faces/rh/administracaodepagamento/modalidadebeneficiofp/lista.xhtml"),
    @URLMapping(id = "verModalidadeBeneficio", pattern = "/modalidadebeneficio/ver/#{modalidadeBeneficioFPControlador.id}/", viewId = "/faces/rh/administracaodepagamento/modalidadebeneficiofp/visualizar.xhtml")
})
public class ModalidadeBeneficioFPControlador extends PrettyControlador<ModalidadeBeneficioFP> implements Serializable, CRUD {

    @EJB
    private ModalidadeBeneficioFPFacade modalidadeBeneficioFPFacade;
    @EJB
    private EventoFPFacade vantagemVencimentoBaseFacade;
    private ConverterAutoComplete converterVantagemVencimentoBase;

    public ModalidadeBeneficioFPControlador() {
        super(ModalidadeBeneficioFP.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return modalidadeBeneficioFPFacade;
    }

    public Converter getConverterVantagemVencimentoBase() {
        if (converterVantagemVencimentoBase == null) {
            converterVantagemVencimentoBase = new ConverterAutoComplete(EventoFP.class, vantagemVencimentoBaseFacade);
        }
        return converterVantagemVencimentoBase;
    }

    public List<EventoFP> completaVantagemVencimentoBase(String parte) {
        return vantagemVencimentoBaseFacade.listaFiltrando(parte.trim(), "descricao");
    }

    @Override
    public ModalidadeBeneficioFP getSelecionado() {
        return ((ModalidadeBeneficioFP) selecionado);
    }

    @URLAction(mappingId="novoModalidadeBeneficio", phaseId=URLAction.PhaseId.RENDER_RESPONSE, onPostback=false )
    @Override
    public void novo() {
        super.novo();
        getSelecionado().setCodigo(modalidadeBeneficioFPFacade.retornaUltimoCodigoLong());
    }

    @URLAction(mappingId="verModalidadeBeneficio", phaseId=URLAction.PhaseId.RENDER_RESPONSE, onPostback=false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId="editarModalidadeBeneficio", phaseId=URLAction.PhaseId.RENDER_RESPONSE, onPostback=false)
    @Override
    public void editar() {
        super.editar();
    }

//    @Override
//    public void selecionar(ActionEvent evento) {
//        operacao = Operacoes.EDITAR;
//        ModalidadeBeneficioFP mbfp = (ModalidadeBeneficioFP) evento.getComponent().getAttributes().get("objeto");
//        selecionado = modalidadeBeneficioFPFacade.recuperar(mbfp.getId());
//    }

    @Override
    public void salvar() {
        if (Util.validaCampos(selecionado)) {
            try {

                if (operacao == Operacoes.NOVO) {
                    Long novoCodigo = modalidadeBeneficioFPFacade.retornaUltimoCodigoLong();

                    if (!novoCodigo.equals(getSelecionado().getCodigo())) {
                        FacesUtil.addWarn("Atenção!","O Código " + getSelecionado().getCodigo() + " já está sendo usado e foi gerado o novo código " + novoCodigo + " !");
                        getSelecionado().setCodigo(novoCodigo);
                    }
                }

                super.salvar();

            } catch (Exception e) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Exceção do sistema!", e.getMessage().toString()));
            }
        }
    }

    @Override
    public String getCaminhoPadrao() {
        return "/modalidadebeneficio/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }
}
