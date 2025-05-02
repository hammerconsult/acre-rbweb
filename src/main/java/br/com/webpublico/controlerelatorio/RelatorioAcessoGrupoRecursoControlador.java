package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.GrupoRecurso;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.GrupoRecursoFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

/**
 * Created by zaca on 27/08/16.
 */
@ManagedBean(name = "relatorioAcessoGrupoRecursoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(
            id = "relatorioGrupoRecurso",
            pattern = "/relatorio/acessos/grupo-recurso/",
            viewId = "/faces/admin/controleusuario/relatorio/relatorioacessogruporecurso.xhtml")
})
public class RelatorioAcessoGrupoRecursoControlador implements Serializable {

    @EJB
    private GrupoRecursoFacade grupoRecursoFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    private GrupoRecurso grupoRecurso;

    public RelatorioAcessoGrupoRecursoControlador() {
    }

    @URLAction(mappingId = "relatorioGrupoRecurso", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo(){
        grupoRecurso = null;
    }

    public List<GrupoRecurso> buscarGrupos(String filter){
        return grupoRecursoFacade.buscarGrupoRecursoPorNome(filter);
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarCampos();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USER", sistemaFacade.getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("MUNICIPIO", "Município de Rio Branco - AC");
            dto.adicionarParametro("GRUPORECURSO_ID", grupoRecurso.getId());
            dto.adicionarParametro("FILTROS", "Grupo de Recurso: " + grupoRecurso.getNome());
            dto.setNomeRelatorio("Relatório de Acesso por Grupo de Recurso");
            dto.setApi("contabil/acesso-grupo-recurso/");
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

    public void validarCampos(){
        ValidacaoException ve = new ValidacaoException();
        if (grupoRecurso == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo grupo de recurso é obrigatório");
        }
        ve.lancarException();
    }

    public GrupoRecurso getGrupoRecurso() {
        return grupoRecurso;
    }

    public void setGrupoRecurso(GrupoRecurso grupoRecurso) {
        this.grupoRecurso = grupoRecurso;
    }
}
