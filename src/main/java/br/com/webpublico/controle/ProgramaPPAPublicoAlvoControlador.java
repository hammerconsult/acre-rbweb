package br.com.webpublico.controle;

import br.com.webpublico.entidades.PPA;
import br.com.webpublico.entidades.ProgramaPPA;
import br.com.webpublico.entidades.PublicoAlvo;
import br.com.webpublico.entidades.PublicoAlvoProgramaPPA;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.ProgramaPPAFacade;
import br.com.webpublico.util.ConverterGenerico;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "programaPPAPublicoAlvoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "editarProgramaPPAPublicoAlvoControlador", pattern = "/programa-ppa-publico-alvo/editar/#{programaPPAPublicoAlvoControlador.id}/", viewId = "/faces/financeiro/ppa/programappapublicoalvo/edita.xhtml"),
    @URLMapping(id = "listarProgramaPPAPublicoAlvoControlador", pattern = "/programa-ppa-publico-alvo/listar/", viewId = "/faces/financeiro/ppa/programappapublicoalvo/lista.xhtml"),
    @URLMapping(id = "verProgramaPPAPublicoAlvoControlador", pattern = "/programa-ppa-publico-alvo/ver/#{programaPPAPublicoAlvoControlador.id}/", viewId = "/faces/financeiro/ppa/programappapublicoalvo/visualizar.xhtml")
})
public class ProgramaPPAPublicoAlvoControlador extends PrettyControlador<ProgramaPPA> implements Serializable, CRUD {

    @EJB
    private ProgramaPPAFacade programaPPAFacade;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private PublicoAlvoProgramaPPA publicoAlvoProgramaPPA;
    private ConverterGenerico converterPublicoAlvo;

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public PublicoAlvoProgramaPPA getPublicoAlvoProgramaPPA() {
        return publicoAlvoProgramaPPA;
    }

    public void setPublicoAlvoProgramaPPA(PublicoAlvoProgramaPPA publicoAlvoProgramaPPA) {
        this.publicoAlvoProgramaPPA = publicoAlvoProgramaPPA;
    }

    public ProgramaPPAFacade getProgramaPPAFacade() {
        return programaPPAFacade;
    }

    public void setProgramaPPAFacade(ProgramaPPAFacade programaPPAFacade) {
        this.programaPPAFacade = programaPPAFacade;
    }

    public void setConverterPublicoAlvo(ConverterGenerico converterPublicoAlvo) {
        this.converterPublicoAlvo = converterPublicoAlvo;
    }

    public ProgramaPPAPublicoAlvoControlador() {
        super(ProgramaPPA.class);
    }

    @URLAction(mappingId = "verProgramaPPAPublicoAlvoControlador", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarProgramaPPAPublicoAlvoControlador", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        publicoAlvoProgramaPPA = new PublicoAlvoProgramaPPA();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/programa-ppa-publico-alvo/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return programaPPAFacade;
    }

    @Override
    protected String getMensagemSucessoAoSalvar() {
        return "O público alvo " + selecionado.getPublicoAlvo() + " foi salvo com sucesso.";
    }

    @Override
    protected String getMensagemSucessoAoExcluir() {
        return "A público alvo " + selecionado.getPublicoAlvo() + " foi excluído com sucesso.";
    }

    public ConverterGenerico getConverterPublicoAlvo() {
        if (converterPublicoAlvo == null) {
            converterPublicoAlvo = new ConverterGenerico(PublicoAlvo.class, programaPPAFacade);
        }
        return converterPublicoAlvo;
    }

    public boolean getPpaAtivo() {
        try {
            PPA ppa = programaPPAFacade.getPpaFacade().ultimoPpaDoExercicio(sistemaControlador.getExercicioCorrente());
            return ppa != null;
        } catch (NullPointerException ex) {
            FacesUtil.addError("Não existe PPA para o exercicio Logado", ex.getMessage());
            return false;
        } catch (ExcecaoNegocioGenerica ex) {
            return false;
        } catch (Exception ex) {
            FacesUtil.addError("Ocorreu um erro entre em contato com o Suporte!", ex.getMessage());
            return false;
        }

    }

    public List<SelectItem> getPublicosAlvosProgramas() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (PublicoAlvo object : programaPPAFacade.getPublicoAlvoFacade().lista()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public void btnCadastroNovoPublicoAlvo() {
        Web.navegacao("/programa-ppa-publico-alvo/editar/" + selecionado.getId() + "/",
            "/publico-alvo/novo/",
            selecionado);
    }

    private boolean validanovoPublicoAlvo(PublicoAlvoProgramaPPA p) {
        if (p.getPublicoAlvo() == null) {
            FacesUtil.addOperacaoNaoPermitida("O campo Público Alvo dever ser informado.");
            return false;
        }
        for (PublicoAlvoProgramaPPA pap : selecionado.getPublicoAlvo()) {
            if (p.getPublicoAlvo().equals(pap.getPublicoAlvo())) {
                FacesUtil.addOperacaoNaoPermitida("O Público Alvo: " + p.getPublicoAlvo() + " já foi adicionado na lista.");
                return false;
            }
        }
        return true;
    }

    public void adicionarPublicoAlvo() {
        publicoAlvoProgramaPPA.setProgramaPPA(selecionado);
        if (validanovoPublicoAlvo(publicoAlvoProgramaPPA)) {
            selecionado.getPublicoAlvo().add(publicoAlvoProgramaPPA);
            publicoAlvoProgramaPPA = new PublicoAlvoProgramaPPA();
        }
    }

    public List<PublicoAlvoProgramaPPA> getPublicoAlvos() {
        try {
            return selecionado.getPublicoAlvo();
        } catch (NullPointerException e) {
            FacesUtil.addError("Problema ao recuperar Publicos Alvos ", e.getMessage());
        } catch (Exception e) {
            FacesUtil.addError("Problema ao recuperar Publicos Alvos, entre em contato com o Suporte! ", e.getMessage());
        }
        return new ArrayList<>();
    }

    public void removePublicoAlvo(ActionEvent evento) {
        selecionado.getPublicoAlvo().remove((PublicoAlvoProgramaPPA) evento.getComponent().getAttributes().get("removePublicoAlvo"));
    }
}
