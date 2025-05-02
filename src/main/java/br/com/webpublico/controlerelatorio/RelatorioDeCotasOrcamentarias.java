/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.GrupoOrcamentario;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.GrupoOrcamentarioFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
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
 * @author major
 */
@ManagedBean(name = "relatorioDeCotasOrcamentarias")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-relatorio-cotas-orcamentarias", pattern = "/relatorio/cotasorcamentarias/", viewId = "/faces/financeiro/relatorio/relatoriocotaorcamentaria.xhtml")})
public class RelatorioDeCotasOrcamentarias extends AbstractReport implements Serializable {

    @EJB
    private GrupoOrcamentarioFacade grupoOrcamentarioFacade;
    private GrupoOrcamentario grupoOrcamentario;

    @URLAction(mappingId = "novo-relatorio-cotas-orcamentarias", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        grupoOrcamentario = null;
    }

    public GrupoOrcamentario getGrupoOrcamentario() {
        return grupoOrcamentario;
    }

    public void setGrupoOrcamentario(GrupoOrcamentario grupoOrcamentario) {
        this.grupoOrcamentario = grupoOrcamentario;
    }

    public List<GrupoOrcamentario> completarGruposOrcamentarios(String parte) {
        return grupoOrcamentarioFacade.listaFiltrandoFk("exercicio", getSistemaFacade().getExercicioCorrente().getId(), parte.trim(), "codigo", "descricao");
    }

    public void gerarRelatorioDeCotas() {
        try {
            validarCampos();
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("DESCRICAOGRUPO", grupoOrcamentario.getCodigo() + " - " + grupoOrcamentario.getDescricao());
            dto.adicionarParametro("MUNICIPIO", "Municipio de Rio Branco - AC");
            dto.adicionarParametro("DATA", DataUtil.getDataFormatada(getSistemaFacade().getDataOperacao()));
            dto.adicionarParametro("idGrupo", grupoOrcamentario.getId());
            dto.setNomeRelatorio("Relatório de Cotas Orçamentárias");
            dto.setApi("contabil/cotas-orcamentarias/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            logger.error("Erro ao gerar relatório: ", ex);
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (grupoOrcamentario == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Grupo Orçamentário deve ser informado.");
        }
        ve.lancarException();
    }
}
