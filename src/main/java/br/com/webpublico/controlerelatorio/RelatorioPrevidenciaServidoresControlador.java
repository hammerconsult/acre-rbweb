/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.PPA;
import br.com.webpublico.entidades.RelatoriosItemDemonst;
import br.com.webpublico.entidadesauxiliares.ItemDemonstrativoComponente;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.OperacaoRelatorio;
import br.com.webpublico.enums.TipoRelatorioItemDemonstrativo;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.PPAFacade;
import br.com.webpublico.negocios.RelatoriosItemDemonstFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

/**
 * @author wiplash
 */
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-rdprps", pattern = "/relatorio/rdprps/", viewId = "/faces/financeiro/relatorio/relatorioprevidenciaservidores.xhtml")
})
@ManagedBean
public class RelatorioPrevidenciaServidoresControlador extends AbstractReport implements Serializable {

    @EJB
    private PPAFacade pPAFacade;
    @EJB
    private RelatoriosItemDemonstFacade relatoriosItemDemonstFacade;
    private List<ItemDemonstrativoComponente> itens;
    private PPA ppa;

    public RelatorioPrevidenciaServidoresControlador() {
        itens = Lists.newArrayList();
    }

    @URLAction(mappingId = "relatorio-rdprps", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        ppa = null;
    }

    public void gerarRelatorio() {
        try {
            RelatoriosItemDemonst relatoriosItemDemonst = relatoriosItemDemonstFacade.recuperaRelatorioPorTipoEDescricao("Previdencia Servidores", getSistemaFacade().getExercicioCorrente(), TipoRelatorioItemDemonstrativo.LDO);
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getNome());
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC");
            dto.adicionarParametro("ANO_EXERCICIO", getSistemaFacade().getExercicioCorrente().getAno());
            dto.adicionarParametro("ANO_EXERCICIO-2", (getSistemaFacade().getExercicioCorrente().getAno() - 2));
            dto.adicionarParametro("ANO_EXERCICIO-3", (getSistemaFacade().getExercicioCorrente().getAno() - 3));
            dto.adicionarParametro("ANO_EXERCICIO-4", (getSistemaFacade().getExercicioCorrente().getAno() - 4));
            dto.adicionarParametro("relatoriosItemDemonst", relatoriosItemDemonst.toDto());
            dto.adicionarParametro("idExercicioCorrente", getSistemaFacade().getExercicioCorrente().getId());
            dto.adicionarParametro("itens", ItemDemonstrativoComponente.itensDemonstrativosComponentesToDto(itens));
            dto.adicionarParametro("parametros", ParametrosRelatorios.parametrosToDto(buscarParametros()));
            dto.setNomeRelatorio("receita e despesas previdenciárias do regime de previdência dos servidores");
            dto.setApi("contabil/previdencia-servidores/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private List<ParametrosRelatorios> buscarParametros() {
        List<ParametrosRelatorios> parametros = Lists.newArrayList();
        if (ppa != null) {
            parametros.add(new ParametrosRelatorios(" ppa.id ", ":ppaId", null, OperacaoRelatorio.IGUAL, ppa.getId(), null, 1, false));
        }
        return parametros;
    }

    public List<PPA> completarPPAs(String parte) {
        return pPAFacade.listaTodosPpaExercicio(getSistemaFacade().getExercicioCorrente(), parte);
    }

    public PPA getPpa() {
        return ppa;
    }

    public void setPpa(PPA ppa) {
        this.ppa = ppa;
    }

    public List<ItemDemonstrativoComponente> getItens() {
        return itens;
    }

    public void setItens(List<ItemDemonstrativoComponente> itens) {
        this.itens = itens;
    }
}
