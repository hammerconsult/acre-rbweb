/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.RelatoriosItemDemonst;
import br.com.webpublico.entidadesauxiliares.ItemDemonstrativoComponente;
import br.com.webpublico.enums.TipoRelatorioItemDemonstrativo;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.ItemDemonstrativoFacade;
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
import java.util.ArrayList;
import java.util.List;

/**
 * @author wiplash
 */
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-origem-aplicacao-recursos-obtidos-alienacao-ativos", pattern = "/relatorio/origem-aplicacao-recursos-obtidos-alienacao-ativos/", viewId = "/faces/financeiro/relatorio/relatorioorigemaplicacaoalienacao.xhtml")
})
@ManagedBean
public class RelatorioOrigemAplicacaoAlienacaoAtivoControlador extends AbstractReport implements Serializable {

    @EJB
    private ItemDemonstrativoFacade itemDemonstrativoFacade;
    private RelatoriosItemDemonst relatoriosItemDemonst;
    private List<ItemDemonstrativoComponente> itens;

    public RelatorioOrigemAplicacaoAlienacaoAtivoControlador() {
        itens = new ArrayList<ItemDemonstrativoComponente>();
    }

    @URLAction(mappingId = "relatorio-origem-aplicacao-recursos-obtidos-alienacao-ativos", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limpaCampos() {
        relatoriosItemDemonst = itemDemonstrativoFacade.getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Origem e Aplicação dos Recursos Obtidos com Alienação de Ativos", getSistemaFacade().getExercicioCorrente(), TipoRelatorioItemDemonstrativo.LDO);
    }

    public void gerarRelatorio() {
        try {
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC");
            dto.adicionarParametro("ANO_EXERCICIO", getSistemaFacade().getExercicioCorrente().getAno());
            dto.adicionarParametro("ANO_EXERCICIO-1", (getSistemaFacade().getExercicioCorrente().getAno() - 1));
            dto.adicionarParametro("ANO_EXERCICIO-2", (getSistemaFacade().getExercicioCorrente().getAno() - 2));
            dto.adicionarParametro("ANO_EXERCICIO-3", (getSistemaFacade().getExercicioCorrente().getAno() - 3));
            dto.adicionarParametro("relatoriosItemDemonstDTO", relatoriosItemDemonst.toDto());
            dto.adicionarParametro("itens", ItemDemonstrativoComponente.itensDemonstrativosComponentesToDto(itens));
            dto.setNomeRelatorio("ORIGEM E APLICAÇÃO DOS RECURSOS OBTIDOS COM ALIENAÇÃO DE ATIVOS");
            dto.setApi("contabil/origem-aplicacao-alienacao-ativo/");
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

    public List<ItemDemonstrativoComponente> getItens() {
        return itens;
    }

    public void setItens(List<ItemDemonstrativoComponente> itens) {
        this.itens = itens;
    }

    public RelatoriosItemDemonst getRelatoriosItemDemonst() {
        return relatoriosItemDemonst;
    }

    public void setRelatoriosItemDemonst(RelatoriosItemDemonst relatoriosItemDemonst) {
        this.relatoriosItemDemonst = relatoriosItemDemonst;
    }
}
