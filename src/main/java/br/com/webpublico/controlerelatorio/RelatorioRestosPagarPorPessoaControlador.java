package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.OperacaoRelatorio;
import br.com.webpublico.enums.TipoMovimentoRelatorioRestoAPagar;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import br.com.webpublico.webreportdto.dto.contabil.TipoMovimentoRelatorioRestoAPagarDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Desenvolvimento on 08/03/2017.
 */
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-restos-a-pagar-pessoa", pattern = "/relatorio/restos-a-pagar-por-pessoa/", viewId = "/faces/financeiro/relatorio/demonstrativo-de-restos-a-pagar-por-pessoa.xhtml")
})
@ManagedBean
public class RelatorioRestosPagarPorPessoaControlador extends RelatorioContabilSuperControlador implements Serializable {

    private Funcao funcao;
    private SubFuncao subFuncao;
    private ProgramaPPA programaPPA;
    private TipoAcaoPPA tipoAcaoPPA;
    private AcaoPPA acaoPPA;
    private SubAcaoPPA subAcaoPPA;
    private ConverterAutoComplete converterSubProjetoAtividade;
    private TipoMovimentoRelatorioRestoAPagar[] tipoMovimentos;

    public RelatorioRestosPagarPorPessoaControlador() {
    }

    public List<Funcao> completarFuncoes(String filtro) {
        return relatorioContabilSuperFacade.getFuncaoFacade().listaFiltrandoFuncao(filtro.trim());
    }

    public List<SubFuncao> completarSubFuncoes(String filtro) {
        return relatorioContabilSuperFacade.getSubFuncaoFacade().listaFiltrandoSubFuncao(filtro);
    }

    public List<ProgramaPPA> completarProgramas(String filtro) {
        return relatorioContabilSuperFacade.getProgramaPPAFacade().listaFiltrandoProgramasPorExercicio(filtro, getExercicioCorrente());
    }

    public List<TipoAcaoPPA> completarTiposAcoesPPA(String filtro) {
        return relatorioContabilSuperFacade.getTipoAcaoPPAFacade().listaFiltrando(filtro.trim(), "descricao");
    }

    public List<AcaoPPA> completarAcoesPPA(String filtro) {
        return relatorioContabilSuperFacade.getProjetoAtividadeFacade().buscarAcoesPPAPorExercicio(filtro.trim(), getExercicioCorrente());
    }

    public List<SubAcaoPPA> completarSubAcoesPPA(String filtro) {
        if (acaoPPA != null) {
            return relatorioContabilSuperFacade.getSubProjetoAtividadeFacade().buscarSubPorProjetoAtividades(filtro.trim(), acaoPPA);
        } else {
            return relatorioContabilSuperFacade.getSubProjetoAtividadeFacade().buscarSubProjetoAtividadePorExercicio(filtro.trim(), getExercicioCorrente());
        }
    }

    public List<Conta> completarContasDespesa(String filtro) {
        return relatorioContabilSuperFacade.getContaFacade().listaFiltrandoContaDespesa(filtro.trim(), getExercicioCorrente());
    }

    public List<FonteDeRecursos> completarFontesDeRecursos(String filtro) {
        return relatorioContabilSuperFacade.getFonteDeRecursosFacade().listaFiltrandoPorExercicio(filtro.trim(), getExercicioCorrente());
    }

    public ConverterAutoComplete getConverterSubProjetoAtividade() {
        if (converterSubProjetoAtividade == null) {
            converterSubProjetoAtividade = new ConverterAutoComplete(SubAcaoPPA.class, relatorioContabilSuperFacade.getSubProjetoAtividadeFacade());
        }
        return converterSubProjetoAtividade;
    }

    public String getCaminhoRelatorio() {
        return "/relatorio/restos-a-pagar-por-pessoa/";
    }

    public List<ClasseCredor> completarClasseCredor(String parte) {
        if (pessoa != null) {
            return relatorioContabilSuperFacade.getClasseCredorFacade().buscarClassesPorPessoa(parte.trim(), pessoa);
        } else {
            return relatorioContabilSuperFacade.getClasseCredorFacade().listaFiltrandoDescricao(parte.trim());
        }
    }

    public void limparSubAcaoPPA() {
        subAcaoPPA = null;
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            filtros = "";
            validarDatas();
            validarDataDeReferencia();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("MUNICIPIO", getDescricaoSegundoEsferaDoPoder());
            dto.adicionarParametro("ANO_EXERCICIO", getSistemaFacade().getExercicioCorrente().getAno().toString());
            dto.adicionarParametro("EXERCICIO", getSistemaFacade().getExercicioCorrente().getId());
            dto.adicionarParametro("FILTRO_DATA", getFiltrosDatas());
            dto.adicionarParametro("FILTRO_DATAREFERENCIA", getDataReferenciaFormatada());
            if (unidadeGestora != null) {
                dto.adicionarParametro("FILTRO_UG", filtroUG);
            }
            dto.adicionarParametro("pesquisouUg", unidadeGestora != null);
            dto.adicionarParametro("parametrosRelatorioDTO", ParametrosRelatorios.parametrosToDto(montarParametros()));
            dto.adicionarParametro("tiposMovimentosDTO", tiposToDto(Arrays.asList(tipoMovimentos)));
            dto.adicionarParametro("FILTRO_GERAL", filtros.trim());
            dto.setNomeRelatorio(getNomeRelatorio());
            dto.setApi("contabil/restos-a-pagar-pessoa/");
            ReportService.getInstance().gerarRelatorio(getSistemaControlador().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private List<TipoMovimentoRelatorioRestoAPagarDTO> tiposToDto(List<TipoMovimentoRelatorioRestoAPagar> tiposMovimentos) {
        List<TipoMovimentoRelatorioRestoAPagarDTO> retorno = Lists.newArrayList();
        for (TipoMovimentoRelatorioRestoAPagar tipo : tiposMovimentos) {
            retorno.add(tipo.getToDto());
        }
        return retorno;
    }

    @Override
    public void validarDataDeReferencia() {
        super.validarDataDeReferencia();
        ValidacaoException ve = new ValidacaoException();
        if (dataReferencia.before(dataFinal)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" Data de Referência deve ser maior ou igual a Data Final.");
        }
        ve.lancarException();
    }

    private List<ParametrosRelatorios> montarParametros() {
        List<ParametrosRelatorios> parametros = new ArrayList<>();
        parametros.add(new ParametrosRelatorios(null, ":DATAINICIAL", null, null, getDataInicialFormatada(), null, 0, true));
        parametros.add(new ParametrosRelatorios(null, ":DATAFINAL", null, null, getDataFinalFormatada(), null, 0, true));
        parametros.add(new ParametrosRelatorios(null, ":DATAREFERENCIA", null, null, getDataReferenciaFormatada(), null, 0, true));
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

        if (classeCredor != null) {
            parametros.add(new ParametrosRelatorios(" e.classecredor_id ", ":classecredor", null, OperacaoRelatorio.IGUAL, classeCredor.getId(), null, 1, false));
            filtros += " Classe: " + classeCredor.getDescricao() + " -";
        }

        if (pessoa != null) {
            parametros.add(new ParametrosRelatorios(" pes.id ", ":pessoa", null, OperacaoRelatorio.IGUAL, pessoa.getId(), null, 1, false));
            filtros += " Pessoa: " + pessoa.getNome() + " -";
        }
        filtrosParametrosUnidade(parametros);
        adicionarExercicio(parametros);
        atualizaFiltrosGerais();
        return parametros;
    }

    @URLAction(mappingId = "relatorio-restos-a-pagar-pessoa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        limparCamposGeral();
        funcao = null;
        subFuncao = null;
        programaPPA = null;
        tipoAcaoPPA = null;
        acaoPPA = null;
        subAcaoPPA = null;
    }

    public List<TipoMovimentoRelatorioRestoAPagar> getTiposDeMovimento() {
        return Arrays.asList(TipoMovimentoRelatorioRestoAPagar.values());
    }

    @Override
    public String getNomeRelatorio() {
        return "DEMONSTRATIVO-RESTO-PAGAR-POR-NATUREZA-PESSOA";
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

    public TipoMovimentoRelatorioRestoAPagar[] getTipoMovimentos() {
        return tipoMovimentos;
    }

    public void setTipoMovimentos(TipoMovimentoRelatorioRestoAPagar[] tipoMovimentos) {
        this.tipoMovimentos = tipoMovimentos;
    }
}
