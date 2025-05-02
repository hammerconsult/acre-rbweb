package br.com.webpublico.controle.comum;

import br.com.webpublico.entidades.comum.dashboard.ConfiguracaoDeDashboard;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.comum.dashboard.ConfiguracaoDeDashboardFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.Date;

/**
 * Created by renato on 14/08/19.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "configuracao-de-dashboard", pattern = "/configuracao-de-dashboard/", viewId = "/faces/configuracao-dashboard/edita.xhtml")
})
public class ConfiguracaoDeDashboardControlador  {

    @EJB
    private ConfiguracaoDeDashboardFacade facade;

    private ConfiguracaoDeDashboard selecionado;

    @URLAction(mappingId = "configuracao-de-dashboard", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        selecionado= facade.getConfiguracaoPorChave();
        if(selecionado == null) {
            selecionado = new ConfiguracaoDeDashboard();
            selecionado.setData(new Date());
            selecionado.setChave(facade.getUsuarioBanco().toUpperCase());
            selecionado.setUrl("http://localhost:8484/api/");
        }
    }

    public void salvar() {
        try {
            ValidacaoException ve = new ValidacaoException();
            Util.validarCamposObrigatorios(selecionado, ve);
            ve.lancarException();
            if(selecionado.getId() == null){
                facade.salvarNovo(selecionado);
            }else{
                facade.salvar(selecionado);
            }
            FacesUtil.addOperacaoRealizada("Configuração salva com sucesso.");
            FacesUtil.redirecionamentoInterno("/configuracao-de-dashboard/");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    public ConfiguracaoDeDashboard getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(ConfiguracaoDeDashboard selecionado) {
        this.selecionado = selecionado;
    }
}
