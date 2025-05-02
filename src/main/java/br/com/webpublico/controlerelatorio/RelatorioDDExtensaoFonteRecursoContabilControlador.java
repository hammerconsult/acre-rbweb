package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.DDExtensaoFonteRecurso;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.ApresentacaoRelatorio;
import br.com.webpublico.enums.OperacaoRelatorio;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.relatoriofacade.RelatorioDDExtensaoFonteRecursoContabilFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Criado por Mateus
 * Data: 09/03/2017.
 */
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-dd-extensao-fonte-recursos-contabil", pattern = "/relatorio/dd-extensao-fonte-recursos-contabil/", viewId = "/faces/financeiro/relatorio/relatorioddextensaofonterecursoscontabil.xhtml")
})
@ManagedBean
public class RelatorioDDExtensaoFonteRecursoContabilControlador extends RelatorioQDDSuperControlador {

    @EJB
    private RelatorioDDExtensaoFonteRecursoContabilFacade relatorioDDExtensaoFonteRecursoContabilFacade;
    private ConverterAutoComplete converterSubProjetoAtividade;

    public RelatorioDDExtensaoFonteRecursoContabilControlador() {
    }

    @URLAction(mappingId = "relatorio-dd-extensao-fonte-recursos-contabil", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        super.limparCampos();
        dataInicial = null;
        dataFinal = null;
    }

    public ConverterAutoComplete getConverterSubProjetoAtividade() {
        if (converterSubProjetoAtividade == null) {
            converterSubProjetoAtividade = new ConverterAutoComplete(SubAcaoPPA.class, relatorioDDExtensaoFonteRecursoContabilFacade.getSubProjetoAtividadeFacade());
        }
        return converterSubProjetoAtividade;
    }

    public void gerarRelatorio() {
        try {
            filtros = "";
            validarDataDeReferencia();
            String arquivoJasper = "RelatorioDemonstrativoDespesaExtensaoFonteContabil.jasper";
            JRBeanCollectionDataSource jrbc = new JRBeanCollectionDataSource(gerarSql());
            HashMap parametros = getParametrosPadrao();
            parametros.putAll(getFiltrosPadrao());
            parametros.put("APRESENTACAO", apresentacao.name());
            gerarRelatorioComDadosEmCollectionAlterandoNomeArquivo(getNomeRelatorio(), arquivoJasper, parametros, jrbc);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
        }
    }

    public void limparSubAcaoPPA() {
        subAcaoPPA = null;
    }

    @Override
    public String getNomeRelatorio() {
        return "DD-EXTENSAO-FONTE-RECURSOS-CONTABIL " + DataUtil.getDataFormatada(dataReferencia, "ddMMyyyy");
    }

    private List<DDExtensaoFonteRecurso> gerarSql() {
        List<ParametrosRelatorios> parametros = new ArrayList<>();
        adicionarParametros(parametros);
        filtrosParametrosUnidade(parametros);
        if (ApresentacaoRelatorio.UNIDADE_GESTORA.equals(apresentacao)) {
            adicionarExercicio(parametros);
        }
        return relatorioDDExtensaoFonteRecursoContabilFacade.montarConsutaSQL(parametros, apresentacao.name(), unidadeGestora != null);
    }

    public void adicionarParametros(List<ParametrosRelatorios> parametros) {
        parametros.add(new ParametrosRelatorios(null, ":DATAREFERENCIA", null, OperacaoRelatorio.IGUAL, getDataReferenciaFormatada(), null, 0, true));
        parametros.add(new ParametrosRelatorios(null, ":ID_EXERCICIO", null, OperacaoRelatorio.IGUAL, getSistemaControlador().getExercicioCorrente().getId(), null, 0, false));
        if (fonteDeRecursos != null) {
            parametros.add(new ParametrosRelatorios(" fte.id ", ":idFonte", null, OperacaoRelatorio.IGUAL, fonteDeRecursos.getId(), null, 1, false));
            filtros += " Fonte de Recurso: " + fonteDeRecursos.getCodigo() + " -";
        }
        if (funcao != null) {
            parametros.add(new ParametrosRelatorios(" func.codigo ", ":codigoFuncao", null, OperacaoRelatorio.IGUAL, funcao.getCodigo(), null, 1, false));
            filtros += " Função: " + funcao.getCodigo() + " -";
        }
        if (subFuncao != null) {
            parametros.add(new ParametrosRelatorios(" sub.codigo ", ":codigoSubfuncao", null, OperacaoRelatorio.IGUAL, subFuncao.getCodigo(), null, 1, false));
            filtros += " SubFunção: " + subFuncao.getCodigo() + " -";
        }
        if (programaPPA != null) {
            parametros.add(new ParametrosRelatorios(" prog.codigo ", ":programaCodigo", null, OperacaoRelatorio.IGUAL, programaPPA.getCodigo(), null, 1, false));
            filtros += " Programa: " + programaPPA.getCodigo() + " -";
        }
        if (tipoAcaoPPA != null) {
            parametros.add(new ParametrosRelatorios(" tpa.codigo ", ":tipoAcaoCodigo", null, OperacaoRelatorio.IGUAL, tipoAcaoPPA.getCodigo(), null, 1, false));
            filtros += " Tipo de Ação: " + tipoAcaoPPA.getCodigo() + " -";
        }
        if (acaoPPA != null) {
            parametros.add(new ParametrosRelatorios(" ac.codigo ", ":acaoCodigo", null, OperacaoRelatorio.IGUAL, acaoPPA.getCodigo(), null, 1, false));
            filtros += " Projeto/Atividade: " + acaoPPA.getCodigo() + " -";
        }

        if (subAcaoPPA != null) {
            parametros.add(new ParametrosRelatorios(" sb.id ", ":idSubAcao", null, OperacaoRelatorio.IGUAL, subAcaoPPA.getId(), null, 1, false));
            String codigoSubAcao = "";
            if (subAcaoPPA.getAcaoPPA().getTipoAcaoPPA() != null) {
                codigoSubAcao += subAcaoPPA.getAcaoPPA().getTipoAcaoPPA().getCodigo();
            }
            if (subAcaoPPA.getAcaoPPA().getCodigo() != null) {
                codigoSubAcao += subAcaoPPA.getAcaoPPA().getCodigo() + ".";
            }
            codigoSubAcao += subAcaoPPA.getCodigo();
            filtros += " Sub-Projeto/Atividade: " + codigoSubAcao + " -";
        }
        if (conta != null) {
            parametros.add(new ParametrosRelatorios(" c.codigo ", ":codigo", null, OperacaoRelatorio.LIKE, conta.getCodigoSemZerosAoFinal() + "%", null, 1, false));
            filtros += " Conta de Despesa: " + conta.getCodigo()+ " -";
        }
    }

    public List<FonteDeRecursos> completarFonteDeRecursos(String parte) {
        return relatorioDDExtensaoFonteRecursoContabilFacade.getFonteDeRecursosFacade().listaFiltrandoPorExercicio(parte.trim(), getSistemaControlador().getExercicioCorrente());
    }

    public List<SubAcaoPPA> completarSubAcoesPPA(String filtro) {
        if (acaoPPA != null) {
            return relatorioDDExtensaoFonteRecursoContabilFacade.getSubProjetoAtividadeFacade().buscarSubPorProjetoAtividades(filtro.trim(), acaoPPA);
        } else {
            return relatorioDDExtensaoFonteRecursoContabilFacade.getSubProjetoAtividadeFacade().buscarSubProjetoAtividadePorExercicio(filtro.trim(), getExercicioCorrente());
        }
    }

    public List<AcaoPPA> completarAcoesPPA(String filtro) {
        return relatorioDDExtensaoFonteRecursoContabilFacade.getProjetoAtividadeFacade().buscarAcoesPPAPorExercicio(filtro.trim(), getExercicioCorrente());
    }

    public List<TipoAcaoPPA> completarTiposAcoesPPA(String filtro) {
        return relatorioDDExtensaoFonteRecursoContabilFacade.getTipoAcaoPPAFacade().listaFiltrando(filtro.trim(), "descricao");
    }

    public List<Funcao> completarFuncao(String parte) {
        return relatorioDDExtensaoFonteRecursoContabilFacade.getFuncaoFacade().listaFiltrandoFuncao(parte.trim());
    }

    public List<SubFuncao> completarSubFuncao(String parte) {
        return relatorioDDExtensaoFonteRecursoContabilFacade.getSubFuncaoFacade().listaFiltrandoSubFuncao(parte.trim());
    }

    public List<Conta> completarContaDespesa(String parte) {
        return relatorioDDExtensaoFonteRecursoContabilFacade.getContaFacade().listaFiltrandoContaDespesa(parte, getSistemaControlador().getExercicioCorrente());
    }
}
