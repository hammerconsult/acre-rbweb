package br.com.webpublico.controle;

import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.entidades.comum.relatorio.ConfiguracaoDeRelatorio;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.comum.ConfiguracaoDeRelatorioFacade;
import br.com.webpublico.negocios.comum.DiagnosticoFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.report.WebReportDTO;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by renato on 17/07/19.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "configuracao-de-relatorio", pattern = "/configuracao-de-relatorio/", viewId = "/faces/configuracao-relatorio/edita.xhtml")
})
public class ConfiguracaoDeRelatorioControlador {

    @EJB
    private ConfiguracaoDeRelatorioFacade facade;

    private ConfiguracaoDeRelatorio selecionado;

    @URLAction(mappingId = "configuracao-de-relatorio", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        selecionado= facade.getConfiguracaoPorChave();
        if(selecionado == null) {
            selecionado = new ConfiguracaoDeRelatorio();
            selecionado.setData(new Date());
            selecionado.setChave(facade.getUsuarioBanco().toUpperCase());
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
            FacesUtil.redirecionamentoInterno("/configuracao-de-relatorio/");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    public ConfiguracaoDeRelatorio getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(ConfiguracaoDeRelatorio selecionado) {
        this.selecionado = selecionado;
    }


    public List<UsuarioSistema> getUsuarios() {
        return ReportService.getInstance().getRelatoriosUsuarios();
    }

    public List<WebReportDTO> getRelatoriosUsuario(UsuarioSistema usuarioSistema) {
        List<WebReportDTO> retorno = Lists.newArrayList();
        Map<String, WebReportDTO> relatorios = ReportService.getInstance().getRelatorios(usuarioSistema);
        for (String key : relatorios.keySet()) {
            retorno.add(relatorios.get(key));
        }
        return retorno;
    }

    public void remover(UsuarioSistema usuarioSistema, String uuid) {
        ReportService.getInstance().removerRelatorio(usuarioSistema, uuid);
    }
}

