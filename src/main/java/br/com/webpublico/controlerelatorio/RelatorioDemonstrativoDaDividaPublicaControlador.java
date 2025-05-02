package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.Intervalo;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by carlos on 19/10/16.
 */
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-divida-publica", pattern = "/relatorio/demonstrativo-divida-publica/", viewId = "/faces/financeiro/relatorio/relatorio-demonstrativo-divida-publica.xhtml")
})
@ManagedBean
public class RelatorioDemonstrativoDaDividaPublicaControlador extends RelatorioContabilSuperControlador implements Serializable {

    @EJB
    private DividaPublicaFacade dividaPublicaFacade;
    @EJB
    private CategoriaDividaPublicaFacade categoriaDividaPublicaFacade;
    private DividaPublica dividaPublica;
    private CategoriaDividaPublica categoriaDividaPublica;
    private NaturezaDividaPublica naturezaDividaPublica;
    private Intervalo intervalo;
    private ContaDeDestinacao contaDeDestinacao;

    @URLAction(mappingId = "relatorio-divida-publica", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        limparCamposGeral();
        dividaPublica = null;
        categoriaDividaPublica = null;
        naturezaDividaPublica = null;
        intervalo = null;
    }

    public List<DividaPublica> completarDividasPublicas(String parte) {
        return dividaPublicaFacade.listaFiltrandoDividaPublica(parte);
    }

    public List<FonteDeRecursos> completarFontesDeRecursos(String parte) {
        return relatorioContabilSuperFacade.getFonteDeRecursosFacade().listaFiltrandoPorExercicio(parte, getExercicioDaDataFinal());
    }

    private Exercicio getExercicioDaDataFinal() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy");
        return getExercicioFacade().getExercicioPorAno(Integer.valueOf(format.format(dataFinal)));
    }

    public List<CategoriaDividaPublica> completarCategoriasDividasPublicas(String parte) {
        return categoriaDividaPublicaFacade.buscarFiltrandoCategoriaDividaPublica(parte);
    }

    public List<ContaDeDestinacao> completarContasDeDestinacao(String parte) {
        return relatorioContabilSuperFacade.getContaFacade().buscarContasDeDestinacaoPorCodigoOrDescricao(parte, getSistemaFacade().getExercicioCorrente());
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
            dto.adicionarParametro("NOMERELATORIO", getNomeRelatorio());
            dto.adicionarParametro("APRESENTACAO", getApresentacao().name());
            dto.adicionarParametro("pesquisouUg", unidadeGestora != null);
            dto.adicionarParametro("dataInicial", DataUtil.getDataFormatada(dataInicial));
            dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(montarParametrosEFiltros()));
            dto.adicionarParametro("FILTRO", filtros);
            dto.setNomeRelatorio("DemonstrativoDaDividaPublica");
            dto.setApi("contabil/demonstrativo-divida-publica/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            logger.error("Erro ao gerar relatório: " + ex);
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private List<ParametrosRelatorios> montarParametrosEFiltros() {
        List<ParametrosRelatorios> parametros = new ArrayList<>();
        parametros.add(new ParametrosRelatorios(null, ":data_inicial", null, OperacaoRelatorio.IGUAL, DataUtil.getDataFormatada(dataInicial), null, 0, true));
        parametros.add(new ParametrosRelatorios(null, ":data_final", null, OperacaoRelatorio.IGUAL, DataUtil.getDataFormatada(dataFinal), null, 0, true));
        filtros = "";
        if (dividaPublica != null) {
            filtros += " Dívida Pública: " + dividaPublica.toStringAutoComplete() + " -";
            parametros.add(new ParametrosRelatorios(" divida.id ", ":divida", null, OperacaoRelatorio.IGUAL, dividaPublica.getId(), null, 1, false));
        }
        if (categoriaDividaPublica != null) {
            filtros += " Categoria da Dívida Pública: " + categoriaDividaPublica.toString() + " -";
            parametros.add(new ParametrosRelatorios(" cat.id ", ":categoria", null, OperacaoRelatorio.IGUAL, categoriaDividaPublica.getId(), null, 1, false));
        }
        if (naturezaDividaPublica != null) {
            filtros += " Natureza da Dívida Pública: " + naturezaDividaPublica.toString() + " -";
            parametros.add(new ParametrosRelatorios(" cat.naturezadividapublica ", ":natureza", null, OperacaoRelatorio.IGUAL, naturezaDividaPublica.name(), null, 1, false));
        }
        if (fonteDeRecursos != null) {
            filtros += " Fonte de Recurso: " + fonteDeRecursos.toString() + " -";
            parametros.add(new ParametrosRelatorios(" fonte.id ", ":fonte", null, OperacaoRelatorio.IGUAL, fonteDeRecursos.getId(), null, 1, false));
        }
        if (contaDeDestinacao != null) {
            filtros += " Conta de Destinação de Recurso: " + contaDeDestinacao.toString() + " -";
            parametros.add(new ParametrosRelatorios(" cd.ID ", ":idContaDeDestinacao", null, OperacaoRelatorio.IGUAL, contaDeDestinacao.getId(), null, 1, false));
        }
        if (intervalo != null) {
            filtros += " Intervalo: " + intervalo.getDescricao() + " -";
            parametros.add(new ParametrosRelatorios(" saldo.intervalo ", ":intervalo", null, OperacaoRelatorio.IGUAL, intervalo.name(), null, 1, false));
        }
        filtrosParametrosUnidade(parametros);
        adicionarExercicio(parametros);
        filtros = getFiltrosPeriodo() + filtros;
        atualizaFiltrosGerais();
        return parametros;
    }

    @Override
    public void adicionarExercicio(List<ParametrosRelatorios> parametros) {
        parametros.add(new ParametrosRelatorios(null, ":exercicio", null, null, getExercicioDaDataFinal().getId(), null, 0, false));
    }

    public List<SelectItem> getNatureza() {
        return Util.getListSelectItem(NaturezaDividaPublica.values());
    }

    public List<SelectItem> getIntervalos() {
        return Util.getListSelectItem(Intervalo.values());
    }

    @Override
    public String getNomeRelatorio() {
        return "Demonstrativo da Dívida Pública";
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

    public Intervalo getIntervalo() {
        return intervalo;
    }

    public void setIntervalo(Intervalo intervalo) {
        this.intervalo = intervalo;
    }

    public ContaDeDestinacao getContaDeDestinacao() {
        return contaDeDestinacao;
    }

    public void setContaDeDestinacao(ContaDeDestinacao contaDeDestinacao) {
        this.contaDeDestinacao = contaDeDestinacao;
    }
}
