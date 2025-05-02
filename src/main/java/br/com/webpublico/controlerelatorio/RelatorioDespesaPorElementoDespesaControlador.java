package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.UnidadeGestora;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.ExercicioFacade;
import br.com.webpublico.negocios.TransparenciaFacade;
import br.com.webpublico.negocios.UnidadeGestoraFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.FacesUtil;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mateus on 11/12/2014.
 */
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "relatorio-despesa-programa-elemento-despesa", pattern = "/relatorio/despesas-programa-elemento-despesa", viewId = "/faces/financeiro/relatorio/relatoriodespesaporelementodespesa.xhtml")
})
@ManagedBean
public class RelatorioDespesaPorElementoDespesaControlador implements Serializable {

    @EJB
    private TransparenciaFacade transparenciaFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private UnidadeGestoraFacade unidadeGestoraFacade;
    private Exercicio exercicio;
    private UnidadeGestora unidadeGestora;
    private Mes mes;

    public RelatorioDespesaPorElementoDespesaControlador() {
    }

    public List<UnidadeGestora> completarUnidadesGestoras(String parte) {
        if (exercicio != null) {
            return unidadeGestoraFacade.listaFiltrandoPorExercicio(exercicio, parte);
        }
        return Lists.newArrayList();
    }

    public List<SelectItem> getMeses() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (Mes m : Mes.values()) {
            toReturn.add(new SelectItem(m, m.getDescricao()));
        }
        return toReturn;
    }

    private void validarFiltros() {
        ValidacaoException ve = new ValidacaoException();
        if (exercicio == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Exercício deve ser informado.");
        }
        ve.lancarException();
    }

    @URLAction(mappingId = "relatorio-despesa-programa-elemento-despesa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        mes = Mes.JANEIRO;
        exercicio = null;
        unidadeGestora = null;
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarFiltros();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("FILTRO_RELATORIO", mes.getDescricao().toUpperCase());
            dto.adicionarParametro("ANO_EXERCICIO", exercicio.getAno().toString());
            dto.adicionarParametro("EXERCICIO_ID", exercicio.getId());
            dto.adicionarParametro("pesquisouUg", unidadeGestora != null);
            dto.adicionarParametro("MUNICIPIO", "Município de Rio Branco - AC");
            dto.adicionarParametro("FILTRO_VALORES", getCondicaoValores());
            if (unidadeGestora != null) {
                dto.adicionarParametro("UNIDADEGESTORA", unidadeGestora.getId());
                if (unidadeGestora.getCodigo().equals("001")) {
                    dto.adicionarParametro("UG", unidadeGestora.getCodigo() + " - " + unidadeGestora.getDescricao());
                }
            }
            dto.setNomeRelatorio("Relatório de Despesas");
            dto.setApi("contabil/despesa-por-elemento-despesa/");
            ReportService.getInstance().gerarRelatorio(transparenciaFacade.getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private String getCondicaoValores() {
        String sql = "  (valorJaneiro > 0 ";
        switch (mes.name()) {
            case "FEVEREIRO":
                sql += " or valorFevereiro > 0 ";
                break;
            case "MARCO":
                sql += " or valorFevereiro > 0 or valorMarco > 0 ";
                break;
            case "ABRIL":
                sql += " or valorFevereiro > 0 or valorMarco > 0 or valorAbril > 0 ";
                break;
            case "MAIO":
                sql += " or valorFevereiro > 0 or valorMarco > 0 or valorAbril > 0 or valorMaio > 0 ";
                break;
            case "JUNHO":
                sql += " or valorFevereiro > 0 or valorMarco > 0 or valorAbril > 0 or valorMaio > 0 or valorJunho > 0 ";
                break;
            case "JULHO":
                sql += " or valorFevereiro > 0 or valorMarco > 0 or valorAbril > 0 or valorMaio > 0 or valorJunho > 0 or valorJulho > 0 ";
                break;
            case "AGOSTO":
                sql += " or valorFevereiro > 0 or valorMarco > 0 or valorAbril > 0 or valorMaio > 0 or valorJunho > 0 or valorJulho > 0 or valorAgosto > 0 ";
                break;
            case "SETEMBRO":
                sql += " or valorFevereiro > 0 or valorMarco > 0 or valorAbril > 0 or valorMaio > 0 or valorJunho > 0 or valorJulho > 0 or valorAgosto > 0 or valorSetembro > 0 ";
                break;
            case "OUTUBRO":
                sql += " or valorFevereiro > 0 or valorMarco > 0 or valorAbril > 0 or valorMaio > 0 or valorJunho > 0 or valorJulho > 0 or valorAgosto > 0 or valorSetembro > 0 or valorOutubro > 0 ";
                break;
            case "NOVEMBRO":
                sql += " or valorFevereiro > 0 or valorMarco > 0 or valorAbril > 0 or valorMaio > 0 or valorJunho > 0 or valorJulho > 0 or valorAgosto > 0 or valorSetembro > 0 or valorOutubro > 0 or valorNovembro > 0 ";
                break;
            case "DEZEMBRO":
                sql += " or valorFevereiro > 0 or valorMarco > 0 or valorAbril > 0 or valorMaio > 0 or valorJunho > 0 or valorJulho > 0 or valorAgosto > 0 or valorSetembro > 0 or valorOutubro > 0 or valorNovembro > 0 or valorDezembro > 0 ";
                break;
        }
        sql += ")";

        return sql;
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
