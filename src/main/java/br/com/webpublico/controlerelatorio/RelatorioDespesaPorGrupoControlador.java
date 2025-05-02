package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.UnidadeGestora;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.ExercicioFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.UnidadeGestoraFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.List;

/**
 * Created by Mateus on 11/12/2014.
 */
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "relatorio-despesa-grupo", pattern = "/relatorio/despesa-grupo", viewId = "/faces/financeiro/relatorio/relatoriodespesaporgrupo.xhtml")
})
@ManagedBean
public class RelatorioDespesaPorGrupoControlador implements Serializable {

    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private UnidadeGestoraFacade unidadeGestoraFacade;
    private Exercicio exercicio;
    private UnidadeGestora unidadeGestora;
    private Mes mes;

    public RelatorioDespesaPorGrupoControlador() {
    }

    public List<UnidadeGestora> completarUnidadesGestoras(String parte) {
        if (exercicio != null) {
            return unidadeGestoraFacade.listaFiltrandoPorExercicio(exercicio, parte);
        }
        return Lists.newArrayList();
    }

    public List<SelectItem> getMeses() {
        return Util.getListSelectItemSemCampoVazio(Mes.values());
    }

    private void validarFiltros() {
        ValidacaoException ve = new ValidacaoException();
        if (exercicio == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Exercício deve ser informado.");
        }
        ve.lancarException();
    }

    @URLAction(mappingId = "relatorio-despesa-grupo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        exercicio = null;
        unidadeGestora = null;
        mes = Mes.JANEIRO;
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarFiltros();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("FILTRO_RELATORIO", mes.name());
            dto.adicionarParametro("MES", mes.getDescricao().toUpperCase());
            dto.adicionarParametro("ANO_EXERCICIO", exercicio.getAno().toString());
            dto.adicionarParametro("EXERCICIO_ID", exercicio.getId());
            dto.adicionarParametro("MUNICIPIO", "Município de Rio Branco - AC");
            dto.adicionarParametro("pesquisouUg", unidadeGestora != null);
            if (unidadeGestora != null) {
                dto.adicionarParametro("UNIDADEGESTORA", unidadeGestora.getId());
                if (unidadeGestora.getCodigo().equals("001")) {
                    dto.adicionarParametro("UG", unidadeGestora.getCodigo() + " - " + unidadeGestora.getDescricao());
                }
            }
            dto.setNomeRelatorio("Relatório de Despesas por Grupo");
            dto.setApi("contabil/despesa-por-grupo/");
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

    public List<Exercicio> completarExercicios(String parte) {
        return exercicioFacade.listaFiltrandoEspecial(parte.trim());
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public Mes getMes() {
        return mes;
    }

    public void setMes(Mes mes) {
        this.mes = mes;
    }

    public UnidadeGestora getUnidadeGestora() {
        return unidadeGestora;
    }

    public void setUnidadeGestora(UnidadeGestora unidadeGestora) {
        this.unidadeGestora = unidadeGestora;
    }
}
