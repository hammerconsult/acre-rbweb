package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.GrupoUsuario;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.GrupoUsuarioFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

/**
 * Created by zaca on 29/08/16.
 */
@ManagedBean(name = "relatorioAcessoGrupoUsuarioControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(
        id = "relatorioAcessoGrupoUsuario",
        pattern = "/relatorio/acessos/grupo-usuario/",
        viewId = "/faces/admin/controleusuario/relatorio/relatorioacessogrupousuario.xhtml")
})
public class RelatorioAcessoGrupoUsuarioControlador implements Serializable {

    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private GrupoUsuarioFacade grupoUsuarioFacade;
    private GrupoUsuario grupoUsuario;

    @URLAction(mappingId = "relatorioAcessoGrupoUsuario", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        grupoUsuario = null;
    }

    public void gerarRelatorio() {
        try {
            validarCampos();
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("USER", sistemaFacade.getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("MUNICIPIO", "Município de Rio Branco - AC");
            dto.adicionarParametro("idGrupo", grupoUsuario.getId());
            dto.setNomeRelatorio("Relatório de Acesso por Grupo Usuário");
            dto.setApi("contabil/acesso_grupo_usuario/");
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private void validarCampos() {
        ValidacaoException ex = new ValidacaoException();
        if (grupoUsuario == null)
            ex.adicionarMensagemDeCampoObrigatorio("O campo grupo de usuário deve ser informado.");

        ex.lancarException();
    }

    public List<GrupoUsuario> buscarGruposDeUsuario(String filter) {
        return grupoUsuarioFacade.buscarGrupoUsuarioPorNome(filter);
    }

    public GrupoUsuario getGrupoUsuario() {
        return grupoUsuario;
    }

    public void setGrupoUsuario(GrupoUsuario grupoUsuario) {
        this.grupoUsuario = grupoUsuario;
    }
}
