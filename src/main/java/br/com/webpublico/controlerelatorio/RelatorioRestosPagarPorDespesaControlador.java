package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.OperacaoRelatorio;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.relatoriofacade.RelatorioContabilSuperFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.DataUtil;
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
import java.io.Serializable;
import java.util.List;

/**
 * Created by Mateus on 15/09/2014.
 */
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-restos-a-pagar-despesa", pattern = "/relatorio/restos-a-pagar-por-despesa/", viewId = "/faces/financeiro/relatorio/relatoriorestospagardespesa.xhtml")
})
@ManagedBean
public class RelatorioRestosPagarPorDespesaControlador extends RelatorioContabilSuperControlador implements Serializable {

    @EJB
    private RelatorioContabilSuperFacade facade;
    private Funcao funcao;
    private SubFuncao subFuncao;
    private ProgramaPPA programaPPA;
    private TipoAcaoPPA tipoAcaoPPA;
    private AcaoPPA acaoPPA;
    private SubAcaoPPA subAcaoPPA;
    private ConverterAutoComplete converterSubProjetoAtividade;

    public RelatorioRestosPagarPorDespesaControlador() {
    }

    @URLAction(mappingId = "relatorio-restos-a-pagar-despesa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        limparCamposGeral();
        funcao = null;
        subFuncao = null;
        programaPPA = null;
        tipoAcaoPPA = null;
        acaoPPA = null;
        subAcaoPPA = null;
    }

    public void limparCamposSemData() {
        unidadeGestora = null;
        listaUnidades = Lists.newArrayList();
        fonteDeRecursos = null;
        conta = null;
        funcao = null;
        subFuncao = null;
        programaPPA = null;
        tipoAcaoPPA = null;
        acaoPPA = null;
        subAcaoPPA = null;
    }

    public List<Funcao> completarFuncoes(String filtro) {
        return facade.getFuncaoFacade().listaFiltrandoFuncao(filtro.trim());
    }

    public List<SubFuncao> completarSubFuncoes(String filtro) {
        return facade.getSubFuncaoFacade().listaFiltrandoSubFuncao(filtro);
    }

    public List<ProgramaPPA> completarProgramas(String filtro) {
        return facade.getProgramaPPAFacade().listaFiltrandoProgramasPorExercicio(filtro, buscarExercicioPelaDataFinal());
    }

    public List<TipoAcaoPPA> completarTiposAcoesPPA(String filtro) {
        return facade.getTipoAcaoPPAFacade().listaFiltrando(filtro.trim(), "descricao");
    }

    public List<AcaoPPA> completarAcoesPPA(String filtro) {
        return facade.getProjetoAtividadeFacade().buscarAcoesPPAPorExercicio(filtro.trim(), buscarExercicioPelaDataFinal());
    }

    public List<SubAcaoPPA> completarSubAcoesPPA(String filtro) {
        if (acaoPPA != null) {
            return facade.getSubProjetoAtividadeFacade().buscarSubPorProjetoAtividades(filtro.trim(), acaoPPA);
        } else {
            return facade.getSubProjetoAtividadeFacade().buscarSubProjetoAtividadePorExercicio(filtro.trim(), buscarExercicioPelaDataFinal());
        }
    }

    public List<Conta> completarContasDespesa(String filtro) {
        return facade.getContaFacade().listaFiltrandoContaDespesa(filtro.trim(), buscarExercicioPelaDataFinal());
    }

    public List<FonteDeRecursos> completarFontesDeRecursos(String filtro) {
        return facade.getFonteDeRecursosFacade().listaFiltrandoPorExercicio(filtro.trim(), buscarExercicioPelaDataFinal());
    }

    public ConverterAutoComplete getConverterSubProjetoAtividade() {
        if (converterSubProjetoAtividade == null) {
            converterSubProjetoAtividade = new ConverterAutoComplete(SubAcaoPPA.class, facade.getSubProjetoAtividadeFacade());
        }
        return converterSubProjetoAtividade;
    }

    public void limparSubAcaoPPA() {
        subAcaoPPA = null;
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            filtros = "";
            validarDatasSemVerificarExercicioLogado();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getNome());
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("MUNICIPIO", getDescricaoSegundoEsferaDoPoder());
            dto.adicionarParametro("ANO_EXERCICIO", buscarExercicioPelaDataFinal().getAno().toString());
            dto.adicionarParametro("EXERCICIO", buscarExercicioPelaDataFinal().getId());
            dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(montarParametros()));
            dto.adicionarParametro("pesquisouUg", unidadeGestora != null);
            filtros = getFiltrosPeriodo() + filtros;
            dto.adicionarParametro("FILTRO", atualizaFiltrosGerais());
            dto.setNomeRelatorio(getNomeRelatorio());
            dto.setApi("contabil/restos-a-pagar-despesa/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private List<ParametrosRelatorios> montarParametros() {
        List<ParametrosRelatorios> parametros = Lists.newArrayList();
        parametros.add(new ParametrosRelatorios(null, ":DATAINICIAL", null, null, DataUtil.getDataFormatada(dataInicial), null, 0, true));
        parametros.add(new ParametrosRelatorios(null, ":DATAFINAL", null, null, DataUtil.getDataFormatada(dataFinal), null, 0, true));
        if (funcao != null) {
            parametros.add(new ParametrosRelatorios(" f.codigo ", ":funcaoCodigo", null, OperacaoRelatorio.IGUAL, funcao.getCodigo(), null, 1, false));
            filtros += " Função: " + funcao.getCodigo() + " -";
        }
        if (subFuncao != null) {
            parametros.add(new ParametrosRelatorios(" sf.codigo ", ":subfuncaoCodigo", null, OperacaoRelatorio.IGUAL, subFuncao.getCodigo(), null, 1, false));
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
            parametros.add(new ParametrosRelatorios(" a.codigo ", ":acaoCodigo", null, OperacaoRelatorio.IGUAL, acaoPPA.getCodigo(), null, 1, false));
            filtros += " Projeto/Atividade: " + acaoPPA.getCodigo() + " -";
        }
        if (subAcaoPPA != null) {
            parametros.add(new ParametrosRelatorios(" sub.codigo ", ":subAcaoCodigo", null, OperacaoRelatorio.IGUAL, subAcaoPPA.getCodigo(), null, 1, false));
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
            parametros.add(new ParametrosRelatorios(" c.codigo ", ":contaCodigo", null, OperacaoRelatorio.LIKE, conta.getCodigoSemZerosAoFinal() + "%", null, 1, false));
            filtros += " Conta de Despesa: " + conta.getCodigo() + " -";
        }
        if (fonteDeRecursos != null) {
            parametros.add(new ParametrosRelatorios(" fonte.codigo ", ":fonteCodigo", null, OperacaoRelatorio.IGUAL, fonteDeRecursos.getCodigo(), null, 1, false));
            filtros += " Fonte de Recursos: " + fonteDeRecursos.getCodigo() + " -";
        }
        filtrosParametrosUnidade(parametros);
        adicionarExercicio(parametros);
        return parametros;
    }

    @Override
    public void adicionarExercicio(List<ParametrosRelatorios> parametros) {
        parametros.add(new ParametrosRelatorios(null, ":exercicio", null, null, buscarExercicioPelaDataFinal().getId(), null, 0, false));
    }

    @Override
    public String getNomeRelatorio() {
        return "demonstrativo-resto-pagar-por-natureza-despesa";
    }

    public Funcao getFuncao() {
        return funcao;
    }

    public void setFuncao(Funcao funcao) {
        this.funcao = funcao;
    }

    public SubFuncao getSubFuncao() {
        return subFuncao;
    }

    public void setSubFuncao(SubFuncao subFuncao) {
        this.subFuncao = subFuncao;
    }

    public ProgramaPPA getProgramaPPA() {
        return programaPPA;
    }

    public void setProgramaPPA(ProgramaPPA programaPPA) {
        this.programaPPA = programaPPA;
    }

    public TipoAcaoPPA getTipoAcaoPPA() {
        return tipoAcaoPPA;
    }

    public void setTipoAcaoPPA(TipoAcaoPPA tipoAcaoPPA) {
        this.tipoAcaoPPA = tipoAcaoPPA;
    }

    public AcaoPPA getAcaoPPA() {
        return acaoPPA;
    }

    public void setAcaoPPA(AcaoPPA acaoPPA) {
        this.acaoPPA = acaoPPA;
    }

    public SubAcaoPPA getSubAcaoPPA() {
        return subAcaoPPA;
    }

    public void setSubAcaoPPA(SubAcaoPPA subAcaoPPA) {
        this.subAcaoPPA = subAcaoPPA;
    }
}
