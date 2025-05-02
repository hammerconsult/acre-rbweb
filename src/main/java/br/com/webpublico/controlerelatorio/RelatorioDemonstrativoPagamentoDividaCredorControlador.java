package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.CategoriaDividaPublica;
import br.com.webpublico.entidades.DividaPublica;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.NaturezaDividaPublica;
import br.com.webpublico.enums.OperacaoRelatorio;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.CategoriaDividaPublicaFacade;
import br.com.webpublico.negocios.DividaPublicaFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
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
 * Criado por Mateus
 * Data: 30/11/2016.
 */
@ViewScoped
@ManagedBean
@URLMappings(mappings = {
    @URLMapping(id = "demonstrativo-pagamento-divida-credor", pattern = "/relatorio/demonstrativo-pagamento-divida-credor", viewId = "/faces/financeiro/relatorio/relatorio-demonstrativo-pagamento-divida-credor.xhtml")
})
public class RelatorioDemonstrativoPagamentoDividaCredorControlador extends RelatorioContabilSuperControlador implements Serializable {

    @EJB
    private DividaPublicaFacade dividaPublicaFacade;
    @EJB
    private CategoriaDividaPublicaFacade categoriaDividaPublicaFacade;
    private DividaPublica dividaPublica;
    private CategoriaDividaPublica categoriaDividaPublica;
    private NaturezaDividaPublica naturezaDividaPublica;

    public RelatorioDemonstrativoPagamentoDividaCredorControlador() {
    }

    @URLAction(mappingId = "demonstrativo-pagamento-divida-credor", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        limparCamposGeral();
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarDatasSemVerificarExercicioLogado();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("MUNICIPIO", getDescricaoSegundoEsferaDoPoder());
            dto.adicionarParametro("ANO_EXERCICIO", getSistemaControlador().getExercicioCorrente().getAno().toString());
            dto.adicionarParametro("EXERCICIO", getSistemaControlador().getExercicioCorrente().getId());
            dto.adicionarParametro("MODULO", "Contábil");
            dto.adicionarParametro("APRESENTACAO", apresentacao.name());
            dto.adicionarParametro("pesquisouUg", unidadeGestora != null);
            dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(montarParametrosEFiltros()));
            dto.adicionarParametro("FILTRO", filtros);
            dto.setNomeRelatorio(getNomeRelatorio());
            dto.setApi("contabil/demonstrativo-pagamento-divida-credor/");
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

    public Exercicio buscarExercicioPelaDataFinal() {
        return getExercicioFacade().recuperarExercicioPeloAno(DataUtil.getAno(dataFinal));
    }

    private List<ParametrosRelatorios> montarParametrosEFiltros() {
        List<ParametrosRelatorios> parametrosRelatorios = new ArrayList<>();
        filtros = "";
        parametrosRelatorios.add(new ParametrosRelatorios(null, ":dataInicial", ":dataFinal", OperacaoRelatorio.BETWEEN, getDataInicialFormatada(), getDataFinalFormatada(), 0, true));
        parametrosRelatorios.add(new ParametrosRelatorios(null, ":anoExercicio", null, OperacaoRelatorio.IGUAL, buscarExercicioPelaDataFinal().getAno(), null, 0, false));
        filtrosParametrosUnidade(parametrosRelatorios);
        if (dividaPublica != null) {
            filtros += " Dívida Pública: " + dividaPublica.toStringAutoComplete() + " -";
            parametrosRelatorios.add(new ParametrosRelatorios(" dv.id ", ":divida", null, OperacaoRelatorio.IGUAL, dividaPublica.getId(), null, 1, false));
        }
        if (categoriaDividaPublica != null) {
            filtros += " Categoria da Dívida Pública: " + categoriaDividaPublica.toString() + " -";
            parametrosRelatorios.add(new ParametrosRelatorios(" cat.id ", ":categoria", null, OperacaoRelatorio.IGUAL, categoriaDividaPublica.getId(), null, 1, false));
        }
        if (naturezaDividaPublica != null) {
            filtros += " Natureza da Dívida Pública: " + naturezaDividaPublica.toString() + " -";
            parametrosRelatorios.add(new ParametrosRelatorios(" cat.naturezadividapublica ", ":natureza", null, OperacaoRelatorio.IGUAL, naturezaDividaPublica.name(), null, 1, false));
        }
        adicionarExercicio(parametrosRelatorios);
        filtros = getFiltrosPeriodo() + filtros;
        atualizaFiltrosGerais();
        return parametrosRelatorios;
    }

    @Override
    public void adicionarExercicio(List<ParametrosRelatorios> parametros) {
        parametros.add(new ParametrosRelatorios(null, ":exercicio", null, null, buscarExercicioPelaDataFinal().getId(), null, 0, false));
    }

    public List<DividaPublica> completarDividaPublica(String parte) {
        return dividaPublicaFacade.listaFiltrandoDividaPublica(parte);
    }

    public List<CategoriaDividaPublica> completarCategoriaDividaPublica(String parte) {
        return categoriaDividaPublicaFacade.buscarFiltrandoCategoriaDividaPublica(parte);
    }

    public List<SelectItem> getNaturezas() {
        return Util.getListSelectItem(NaturezaDividaPublica.values());
    }

    @Override
    public String getNomeRelatorio() {
        return "DEMONSTRATIVO-PAGAMENTO-DIVIDAS-CREDOR";
    }

    public DividaPublica getDividaPublica() {
        return dividaPublica;
    }

    public void setDividaPublica(DividaPublica dividaPublica) {
        this.dividaPublica = dividaPublica;
    }

    public CategoriaDividaPublica getCategoriaDividaPublica() {
        return categoriaDividaPublica;
    }

    public void setCategoriaDividaPublica(CategoriaDividaPublica categoriaDividaPublica) {
        this.categoriaDividaPublica = categoriaDividaPublica;
    }

    public NaturezaDividaPublica getNaturezaDividaPublica() {
        return naturezaDividaPublica;
    }

    public void setNaturezaDividaPublica(NaturezaDividaPublica naturezaDividaPublica) {
        this.naturezaDividaPublica = naturezaDividaPublica;
    }
}
