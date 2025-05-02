package br.com.webpublico.controle;

import br.com.webpublico.entidades.ExperienciaExtraCurricular;
import br.com.webpublico.entidades.VinculoFP;
import br.com.webpublico.enums.TipoExperienciaExtraCurricular;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExperienciaExtraCurricularFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mga on 01/06/2017.
 */
@ManagedBean(name = "experienciaExtraCurricularControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaExperienciaExtra", pattern = "/experiencia-extra-curricular/novo/", viewId = "/faces/rh/concursos/experienciaextracurricular/edita.xhtml"),
    @URLMapping(id = "editarExperienciaExtra", pattern = "/experiencia-extra-curricular/editar/#{experienciaExtraCurricularControlador.id}/", viewId = "/faces/rh/concursos/experienciaextracurricular/edita.xhtml"),
    @URLMapping(id = "verExperienciaExtra", pattern = "/experiencia-extra-curricular/ver/#{experienciaExtraCurricularControlador.id}/", viewId = "/faces/rh/concursos/experienciaextracurricular/visualizar.xhtml"),
    @URLMapping(id = "listarExperienciaExtra", pattern = "/experiencia-extra-curricular/listar/", viewId = "/faces/rh/concursos/experienciaextracurricular/lista.xhtml")
})
public class ExperienciaExtraCurricularControlador extends PrettyControlador<ExperienciaExtraCurricular> implements Serializable, CRUD {

    @EJB
    private ExperienciaExtraCurricularFacade facade;
    private ConverterAutoComplete converterVinculoFP;

    public ExperienciaExtraCurricularControlador() {
        super(ExperienciaExtraCurricular.class);
    }

    @URLAction(mappingId = "novaExperienciaExtra", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "verExperienciaExtra", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarExperienciaExtra", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/experiencia-extra-curricular/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public void salvar() {
        try {
            validarCampos();
            super.salvar();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        selecionado.realizarValidacoes();
        if (selecionado.isTipoCadastroComissao()) {
            if (selecionado.getTipoComissao() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Comissão deve ser informado.");
            }
            if (selecionado.getAtribuicaoComissao() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Atribuição de Comissão deve ser informado.");
            }
        } else {
            if (selecionado.getCurso() == null || selecionado.getCurso().isEmpty()) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Curso deve ser informado.");
            }
        }
        if (selecionado.getFimVigencia() != null && selecionado.getFimVigencia().before(selecionado.getInicioVigencia())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo fim de vigência deve ser superior ao campo início de vigência.");
        }
        ve.lancarException();


    }

    public List<VinculoFP> completaServidor(String s) {
        return facade.getVinculoFPFacade().buscaVinculoVigenteFiltrandoAtributosMatriculaFP(s.trim());
    }

    public ConverterAutoComplete getConverterContratoFP() {
        if (converterVinculoFP == null) {
            converterVinculoFP = new ConverterAutoComplete(VinculoFP.class, facade.getVinculoFPFacade());
        }
        return converterVinculoFP;
    }

    public List<SelectItem> getTiposExperiencia() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (TipoExperienciaExtraCurricular tipo : TipoExperienciaExtraCurricular.values()) {
            toReturn.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return toReturn;
    }

    public boolean renderizarPanelCurso() {
        return selecionado.isTipoCadastroCurso();
    }

    public boolean renderizarPanelComissao() {
        return selecionado.isTipoCadastroComissao();
    }

    public String getStyle() {
        if (selecionado.isTipoCadastroComissao()) {
            if (isOperacaoEditar() || isOperacaoNovo()) {
                return "margin-right: 50px";
            } else {
                return "margin-right: 40px";
            }
        }
        return "";
    }

    public void atribuirNullParaVariaveis() {
        if (selecionado.isTipoCadastroComissao()) {
            selecionado.setCurso(null);
            selecionado.setCargaHoraria(null);
            selecionado.setInstituicao(null);
        } else {
            selecionado.setTipoComissao(null);
            selecionado.setAtribuicaoComissao(null);
        }
    }
}
