package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.Conta;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.FonteDeRecursos;
import br.com.webpublico.entidadesauxiliares.DemonstrativoReceitaItem;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.OperacaoRelatorio;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.relatoriofacade.RelatorioDemonstrativoReceitaSiopeFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Criado por Mateus
 * Data: 21/03/2017.
 */
@ManagedBean(name = "relatorioDemonstrativoReceitaSiopeControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "demonstrativo-receita-siope", pattern = "/relatorio/demonstrativo-receita-siope", viewId = "/faces/financeiro/relatorio/relatoriodemonstrativoreceitasiope.xhtml")
})
public class RelatorioDemonstrativoReceitaSiopeControlador extends RelatorioContabilSuperControlador {

    @EJB
    private RelatorioDemonstrativoReceitaSiopeFacade relatorioDemonstrativoReceitaSiopeFacade;

    @Override
    public String getNomeRelatorio() {
        return "DEMONSTRATIVO-RECEITA-SIOPE";
    }

    @URLAction(mappingId = "demonstrativo-receita-siope", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        super.limparCamposGeral();
    }

    public void gerarRelatorio() {
        try {
            validarDataDeReferencia();
            JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(buscarDados());
            HashMap parametros = getParametrosPadrao();
            parametros.putAll(gerarTodosFiltros());
            parametros.put("MODULO", "Contábil");
            String arquivoJasper = "RelatorioDemonstrativoReceitaSiope.jasper";
            gerarRelatorioComDadosEmCollectionAlterandoNomeArquivo(getNomeArquivo(), arquivoJasper, parametros, ds);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private List<DemonstrativoReceitaItem> buscarDados() {
        List<ParametrosRelatorios> parametros = new ArrayList<>();
        filtros = "";
        filtrosParametrosUnidade(parametros);
        adicionarExercicio(parametros);
        parametros.add(new ParametrosRelatorios(null, ":DATAINICIAL", null, null, "01/01/" + new SimpleDateFormat("yyyy").format(dataReferencia), null, 0, false));
        parametros.add(new ParametrosRelatorios(null, ":DATAREFERENCIA", null, null, DataUtil.getDataFormatada(dataReferencia), null, 0, false));
        if (conta != null) {
            parametros.add(new ParametrosRelatorios(" c.codigo ", ":contaCodigo", null, OperacaoRelatorio.LIKE, conta.getCodigoSemZerosAoFinal() + "%", null, 1, false));
            filtros += " Conta de Receita: " + conta.getCodigo() + " -";
        }
        if (fonteDeRecursos != null) {
            parametros.add(new ParametrosRelatorios(" fr.codigo ", ":fonteCodigo", null, OperacaoRelatorio.IGUAL, fonteDeRecursos.getCodigo(), null, 1, false));
            filtros += " Fonte de Recurso: " + fonteDeRecursos.getCodigo() + " -";
        }
        return relatorioDemonstrativoReceitaSiopeFacade.buscarDados(parametros, unidadeGestora != null);
    }

    @Override
    public void adicionarExercicio(List<ParametrosRelatorios> parametros) {
        Exercicio exercicio = getExercicioFacade().recuperarExercicioPeloAno(new Integer(new SimpleDateFormat("yyyy").format(dataReferencia)));
        parametros.add(new ParametrosRelatorios(null, ":exercicio", null, null, exercicio.getId(), null, 0, false));
    }

    public List<FonteDeRecursos> completarFonteDeRecursos(String filtro) {
        return relatorioDemonstrativoReceitaSiopeFacade.getFonteDeRecursosFacade().listaFiltrandoPorExercicio(filtro.trim(), relatorioDemonstrativoReceitaSiopeFacade.getSistemaFacade().getExercicioCorrente());
    }

    public List<Conta> completarContasDeReceita(String filtro) {
        return relatorioDemonstrativoReceitaSiopeFacade.getContaFacade().listaFiltrandoContaReceitaPorExercicio(filtro.trim(), relatorioDemonstrativoReceitaSiopeFacade.getSistemaFacade().getExercicioCorrente());
    }
}
