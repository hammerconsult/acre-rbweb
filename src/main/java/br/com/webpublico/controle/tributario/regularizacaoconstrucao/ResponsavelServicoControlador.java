package br.com.webpublico.controle.tributario.regularizacaoconstrucao;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.tributario.regularizacaoconstrucao.ResponsavelServico;
import br.com.webpublico.enums.tributario.regularizacaoconstrucao.TipoEspecialidadeServico;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.PessoaFisicaFacade;
import br.com.webpublico.negocios.tributario.regularizacaoconstrucao.ResponsavelServicoFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import com.google.common.base.Strings;
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


@ManagedBean(name = "responsavelServicoControlador")
@ViewScoped
@URLMappings(mappings = {@URLMapping(id = "novoResponsavelServico", pattern = "/regularizacao-construcao/responsavel-tecnico-servico/novo/", viewId = "/faces/tributario/regularizacaoconstrucao/responsavelservico/edita.xhtml"), @URLMapping(id = "editarResponsavelServico", pattern = "/regularizacao-construcao/responsavel-tecnico-servico/editar/#{responsavelServicoControlador.id}/", viewId = "/faces/tributario/regularizacaoconstrucao/responsavelservico/edita.xhtml"), @URLMapping(id = "listarResponsavelServico", pattern = "/regularizacao-construcao/responsavel-tecnico-servico/listar/", viewId = "/faces/tributario/regularizacaoconstrucao/responsavelservico/lista.xhtml"), @URLMapping(id = "verResponsavelServico", pattern = "/regularizacao-construcao/responsavel-tecnico-servico/ver/#{responsavelServicoControlador.id}/", viewId = "/faces/tributario/regularizacaoconstrucao/responsavelservico/visualizar.xhtml")})

public class ResponsavelServicoControlador extends PrettyControlador<ResponsavelServico> implements Serializable, CRUD {

    @EJB
    private ResponsavelServicoFacade responsavelServicoFacade;
    @EJB
    private PessoaFisicaFacade pessoaFisicaFacade;
    private ConverterAutoComplete converterPessoaFisica;
    private String artRrt;

    public ResponsavelServicoControlador() {
        super(ResponsavelServico.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return responsavelServicoFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/regularizacao-construcao/responsavel-tecnico-servico/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public String getArtRrt() {
        return artRrt;
    }

    public void setArtRrt(String artRrt) {
        this.artRrt = artRrt;
    }

    @URLAction(mappingId = "novoResponsavelServico", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "verResponsavelServico", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarResponsavelServico", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    public List<ResponsavelServico> completarResponsavelServico(String parte) {
        return responsavelServicoFacade.buscarResponsavelServicoPorPessoaAndArt(parte);
    }

    public List<SelectItem> getTipoEspecialidadeServico() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, " "));
        for (TipoEspecialidadeServico tipo : TipoEspecialidadeServico.values()) {
            retorno.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return retorno;
    }

    private void validarArtRrt() {
        if (Strings.isNullOrEmpty(artRrt)) {
            throw new ValidacaoException("O campo ART/RRT deve ser informado.");
        }
    }
}
