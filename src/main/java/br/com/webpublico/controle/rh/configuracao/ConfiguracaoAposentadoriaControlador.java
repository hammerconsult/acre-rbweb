package br.com.webpublico.controle.rh.configuracao;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.rh.configuracao.ConfiguracaoAposentadoria;
import br.com.webpublico.entidades.rh.configuracao.TempoMinimoAposentadoria;
import br.com.webpublico.entidades.RegraAposentadoria;
import br.com.webpublico.enums.Sexo;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.interfaces.ValidadorEntidade;
import br.com.webpublico.negocios.RegraAposentadoriaFacade;
import br.com.webpublico.negocios.rh.configuracao.ConfiguracaoAposentadoriaFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
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
 * @Author peixe on 05/11/2015  17:56.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "nova-configuracao-aposentadoria", pattern = "/configuracao/aposentadoria/novo/", viewId = "/faces/rh/configuracao/aposentadoria/edita.xhtml"),
    @URLMapping(id = "editar-configuracao-aposentadoria", pattern = "/configuracao/aposentadoria/editar/#{configuracaoAposentadoriaControlador.id}/", viewId = "/faces/rh/configuracao/aposentadoria/edita.xhtml"),
    @URLMapping(id = "ver-configuracao-aposentadoria", pattern = "/configuracao/aposentadoria/ver/#{configuracaoAposentadoriaControlador.id}/", viewId = "/faces/rh/configuracao/aposentadoria/visualizar.xhtml"),
    @URLMapping(id = "listar-configuracao-aposentadoria", pattern = "/configuracao/aposentadoria/listar/", viewId = "/faces/rh/configuracao/aposentadoria/lista.xhtml")
})
public class ConfiguracaoAposentadoriaControlador extends PrettyControlador<ConfiguracaoAposentadoria> implements Serializable, CRUD, ValidadorEntidade {

    @EJB
    private ConfiguracaoAposentadoriaFacade configuracaoAposentadoriaFacade;
    private TempoMinimoAposentadoria tempoMinimoAposentadoria;
    @EJB
    private RegraAposentadoriaFacade regraAposentadoriaFacade;

    public ConfiguracaoAposentadoriaControlador() {
        super(ConfiguracaoAposentadoria.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/configuracao/aposentadoria/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public ConfiguracaoAposentadoriaFacade getFacede() {
        return configuracaoAposentadoriaFacade;
    }

    @Override
    public void salvar() {
        super.salvar();
    }

    @Override
    public void validarConfirmacao() throws ValidacaoException {
    }

    @URLAction(mappingId = "nova-configuracao-aposentadoria", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "ver-configuracao-aposentadoria", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editar-configuracao-aposentadoria", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    public TempoMinimoAposentadoria getTempoMinimoAposentadoria() {
        return tempoMinimoAposentadoria;
    }

    public void setTempoMinimoAposentadoria(TempoMinimoAposentadoria tempoMinimoAposentadoria) {
        this.tempoMinimoAposentadoria = tempoMinimoAposentadoria;
    }

    public List<SelectItem> getSexos() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (Sexo sexo : Sexo.values()) {
            toReturn.add(new SelectItem(sexo, sexo.getDescricao()));
        }
        return toReturn;
    }

    public void novoTempoMinimoAposentadoria() {
        this.tempoMinimoAposentadoria = new TempoMinimoAposentadoria();
        tempoMinimoAposentadoria.setConfiguracaoAposentadoria(selecionado);
    }

    public void confimarTempoMinimo() {
        if (!Util.validaCampos(this.tempoMinimoAposentadoria)) {
            return;
        }


        if (selecionado.getTempoMinimoAposentadorias() != null) {
            for (TempoMinimoAposentadoria tempoMinimo : selecionado.getTempoMinimoAposentadorias()) {
                if (!tempoMinimo.equals(tempoMinimoAposentadoria) && tempoMinimo.getSexo().equals(tempoMinimoAposentadoria.getSexo())) {
                    FacesUtil.addOperacaoNaoPermitida("Já existe uma configuração para " + tempoMinimo.getSexo());
                    return;
                }
            }
        }
        this.tempoMinimoAposentadoria.setConfiguracaoAposentadoria(selecionado);
        selecionado.setTempoMinimoAposentadorias(Util.adicionarObjetoEmLista(selecionado.getTempoMinimoAposentadorias(), this.tempoMinimoAposentadoria));
        cancelarTempoMinimo();
    }

    public void cancelarTempoMinimo() {
        this.tempoMinimoAposentadoria = null;
    }

    public void selecionarTempoMinimo(TempoMinimoAposentadoria tempoMinimo) {
        this.tempoMinimoAposentadoria = tempoMinimo;
    }

    public void removerTempoMinimo(TempoMinimoAposentadoria tempoMinimo) {
        selecionado.getTempoMinimoAposentadorias().remove(tempoMinimo);
    }

    public void buscarConfiguracoesExistentes() {
        RegraAposentadoria regra = selecionado.getRegraAposentadoria();
        if (selecionado.getRegraAposentadoria() != null) {
            ConfiguracaoAposentadoria config = configuracaoAposentadoriaFacade.buscarConfiguracaoPorRegraAposentadoria(selecionado.getRegraAposentadoria());
            if (config != null) {
                FacesUtil.addInfo("Ateção", "Já existe um registro lançado para " + regra.getDescricao());
                String url = "<b><a href='" + FacesUtil.getRequestContextPath() + getCaminhoPadrao() + "editar/" + config.getId() + "' >Aqui</a></b>";
                FacesUtil.addInfo("", "Para abrir o registro clique  " + url);

            }
        }
    }
}
