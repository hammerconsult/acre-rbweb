package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.OperacaoRelatorio;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.*;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Mateus on 15/09/2014.
 */
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-restos-a-pagar-funcao", pattern = "/relatorio/restos-a-pagar-por-funcao/", viewId = "/faces/financeiro/relatorio/relatoriorestospagarfuncao.xhtml")
})
@ManagedBean
public class RelatorioRestosPagarPorFuncaoControlador extends AbstractReport implements Serializable {

    @EJB
    private HierarquiaOrganizacionalFacade hoFacade;
    @EJB
    private ContaFacade contaFacade;
    @EJB
    private SubProjetoAtividadeFacade subProjetoAtividadeFacade;
    @EJB
    private ProjetoAtividadeFacade projetoAtividadeFacade;
    @EJB
    private SubFuncaoFacade subFuncaoFacade;
    @EJB
    private FuncaoFacade funcaoFacade;
    @EJB
    private ProgramaPPAFacade programaPPAFacade;
    private ConverterAutoComplete converteSubProjetoAtividade;
    private Date dataInicial;
    private Date dataFinal;
    private String filtro;
    private UnidadeGestora unidadeGestora;
    private Funcao funcao;
    private SubFuncao subFuncao;
    private ProgramaPPA programaPPA;
    private TipoAcaoPPA tipoAcaoPPA;
    private AcaoPPA projetoAtividade;
    private SubAcaoPPA subProjetoAtividade;
    private Conta contaDespesa;
    private FonteDeRecursos fonteDeRecursos;
    private List<HierarquiaOrganizacional> listaUnidades;

    public RelatorioRestosPagarPorFuncaoControlador() {
    }

    @URLAction(mappingId = "relatorio-restos-a-pagar-funcao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limpaCampos() {
        this.listaUnidades = new ArrayList<>();
        this.dataInicial = new Date();
        this.dataFinal = new Date();
        this.funcao = null;
        this.subFuncao = null;
        this.programaPPA = null;
        this.tipoAcaoPPA = null;
        this.projetoAtividade = null;
        this.subProjetoAtividade = null;
        this.contaDespesa = null;
        this.fonteDeRecursos = null;
        this.filtro = "";
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarFiltros();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USUARIO", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("MODULO", "Contabilidade");
            dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC");
            dto.adicionarParametro("pesquisouUg", unidadeGestora != null);
            dto.adicionarParametro("parametrosRelatorioDTO", ParametrosRelatorios.parametrosToDto(montarParametros()));
            dto.adicionarParametro("FILTRO", filtro);
            dto.setNomeRelatorio("RELATÓRIO-RESTOS-A-PAGAR-POR-FUNÇÃO");
            dto.setApi("contabil/restos-a-pagar-funcao/");
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

    private void validarFiltros() {
        ValidacaoException ve = new ValidacaoException();
        if (this.dataInicial == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data Inicial deve ser informado.");
        }
        if (this.dataFinal == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data Final deve ser informado.");
        }
        ve.lancarException();
        if (this.dataFinal.before(this.dataInicial)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Data final deve ser superior ou igual a data inicial.");
        }
        SimpleDateFormat formato = new SimpleDateFormat("yyyy");
        if (formato.format(dataInicial).compareTo(formato.format(dataFinal)) != 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("As datas estão com exercícios diferentes.");
        }
        ve.lancarException();
    }

    private List<ParametrosRelatorios> montarParametros() {
        filtro = " Período: " + DataUtil.getDataFormatada(dataInicial) + " a " + DataUtil.getDataFormatada(dataFinal) + " -";
        List<ParametrosRelatorios> parametros = new ArrayList<>();
        parametros.add(new ParametrosRelatorios(null, ":DATAINICIAL", null, null, DataUtil.getDataFormatada(dataInicial), null, 0, true));
        parametros.add(new ParametrosRelatorios(null, ":DATAFINAL", null, null, DataUtil.getDataFormatada(dataFinal), null, 0, true));
        adicionarParametrosUnidadeAndUG(parametros);
        adicionarParametrosGerais(parametros);
        filtro = filtro.substring(0, filtro.length() - 1);
        return parametros;
    }

    private void adicionarParametrosGerais(List<ParametrosRelatorios> parametros) {
        if (this.funcao != null) {
            parametros.add(new ParametrosRelatorios(" f.id ", ":idFuncao", null, OperacaoRelatorio.IGUAL, this.funcao.getId(), null, 1, false));
            filtro += " Função: " + this.funcao.getCodigo() + " -";
        }
        if (this.subFuncao != null) {
            parametros.add(new ParametrosRelatorios(" sf.id ", ":idSubFuncao", null, OperacaoRelatorio.IGUAL, this.subFuncao.getId(), null, 1, false));
            filtro += " Subfunção: " + this.subFuncao.getCodigo() + " -";
        }
        if (this.programaPPA != null) {
            parametros.add(new ParametrosRelatorios(" prog.codigo ", ":codigoPrograma", null, OperacaoRelatorio.IGUAL, this.programaPPA.getCodigo(), null, 1, false));
            filtro += " Programa: " + this.programaPPA.getCodigo() + " -";
        }
        if (this.tipoAcaoPPA != null) {
            parametros.add(new ParametrosRelatorios(" tpa.id ", ":idTipoAcao", null, OperacaoRelatorio.IGUAL, this.tipoAcaoPPA.getId(), null, 1, false));
            filtro += " Tipo Ação: " + this.tipoAcaoPPA.getCodigo() + " -";
        }
        if (this.projetoAtividade != null) {
            parametros.add(new ParametrosRelatorios(" a.codigo ", ":idProjeto", null, OperacaoRelatorio.IGUAL, this.projetoAtividade.getCodigo(), null, 1, false));
            filtro += " Projeto/Atividade: " + this.projetoAtividade.getCodigo() + " -";
        }
        if (this.subProjetoAtividade != null) {
            parametros.add(new ParametrosRelatorios(" sub.codigo ", ":codigoSubProjeto", null, OperacaoRelatorio.IGUAL, this.subProjetoAtividade.getCodigo(), null, 1, false));
            filtro += " Subprojeto/Atividade: " + this.subProjetoAtividade.getCodigo() + " -";
        }
        if (this.contaDespesa != null) {
            parametros.add(new ParametrosRelatorios(" c.codigo ", ":contaDespesa", null, OperacaoRelatorio.IGUAL, this.contaDespesa.getCodigo(), null, 1, false));
            filtro += " Conta de Despesa: " + this.contaDespesa.getCodigo() + " -";
        }
        if (this.fonteDeRecursos != null) {
            parametros.add(new ParametrosRelatorios(" fonte.codigo ", ":fonteRecurso", null, OperacaoRelatorio.IGUAL, this.fonteDeRecursos.getCodigo(), null, 1, false));
            filtro += " Fonte de Recurso: " + this.fonteDeRecursos.getCodigo() + " -";
        }
        parametros.add(new ParametrosRelatorios(null, ":EXERCICIO", null, null, getSistemaFacade().getExercicioCorrente().getId(), null, 0, false));
    }

    private void adicionarParametrosUnidadeAndUG(List<ParametrosRelatorios> listaParametros) {
        if (this.listaUnidades.size() > 0) {
            List<Long> listaIdsUnidades = new ArrayList<>();
            String unidades = "";
            for (HierarquiaOrganizacional lista : listaUnidades) {
                listaIdsUnidades.add(lista.getSubordinada().getId());
                unidades += " " + lista.getCodigo().substring(3, 10) + " -";
            }
            filtro += " Unidade(s): " + unidades;
            listaParametros.add(new ParametrosRelatorios(" vw.subordinada_id ", ":undId", null, OperacaoRelatorio.IN, listaIdsUnidades, null, 1, false));
        } else if (this.unidadeGestora == null) {
            List<HierarquiaOrganizacional> listaUndsUsuarios = hoFacade.listaHierarquiaUsuarioCorrentePorNivel("", getSistemaFacade().getUsuarioCorrente(), getSistemaFacade().getExercicioCorrente(), getSistemaFacade().getDataOperacao(), TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), 3);
            List<Long> listaIdsUnidades = new ArrayList<>();
            for (HierarquiaOrganizacional lista : listaUndsUsuarios) {
                listaIdsUnidades.add(lista.getSubordinada().getId());
            }
            if (listaIdsUnidades.size() != 0) {
                listaParametros.add(new ParametrosRelatorios(" vw.subordinada_id ", ":undId", null, OperacaoRelatorio.IN, listaIdsUnidades, null, 1, false));
            }
        }
        if (this.unidadeGestora != null) {
            listaParametros.add(new ParametrosRelatorios(" ug.id ", ":ugId", null, OperacaoRelatorio.IGUAL, this.unidadeGestora.getId(), null, 1, false));
            filtro += " Unidade Gestora: " + this.unidadeGestora.getCodigo() + " -";
        }
    }

    public void definirSubProjetoComoNull() {
        projetoAtividade = null;
        subProjetoAtividade = null;
    }

    public void definirSubFuncaoComoNull() {
        funcao = null;
        subFuncao = null;
    }

    public void definirProjetoAtividadeComoNull() {
        programaPPA = null;
        projetoAtividade = null;
        subProjetoAtividade = null;
    }

    public List<Conta> completarContas(String parte) {
        List<Conta> contas = contaFacade.listaFiltrandoDespesaCriteria(parte.trim(), getExercicioCorrente());
        if (contas.isEmpty()) {
            return new ArrayList<Conta>();
        } else {
            return contas;
        }
    }

    public List<Funcao> completarFuncao(String parte) {
        return funcaoFacade.listaFiltrandoFuncao(parte.trim());
    }

    public List<ProgramaPPA> completarPrograma(String parte) {
        return programaPPAFacade.listaFiltrandoProgramasPorExercicio(parte.trim(), getExercicioCorrente());
    }

    public List<SubFuncao> completarSubFuncao(String parte) {
        if (funcao != null) {
            return subFuncaoFacade.buscarSubFuncaoPorFuncao(parte.trim(), funcao);
        } else {
            return subFuncaoFacade.listaFiltrandoSubFuncao(parte.trim());
        }
    }

    public List<AcaoPPA> completarProjetoAtividade(String parte) {
        if (programaPPA != null) {
            return projetoAtividadeFacade.buscarProjetoAtividadePorPrograma(programaPPA, parte.trim());
        } else {
            return projetoAtividadeFacade.buscarAcoesPPAPorExercicio(parte.trim(), getExercicioCorrente());
        }
    }

    public List<SubAcaoPPA> completarSubProjetoAtividade(String parte) {
        if (projetoAtividade != null) {
            return subProjetoAtividadeFacade.buscarSubPorProjetoAtividades(parte.trim(), projetoAtividade);
        } else {
            return subProjetoAtividadeFacade.buscarSubProjetoAtividadePorExercicio(parte.trim(), getExercicioCorrente());
        }
    }

    public ConverterAutoComplete getConverteSubProjetoAtividade() {
        if (converteSubProjetoAtividade == null) {
            converteSubProjetoAtividade = new ConverterAutoComplete(SubAcaoPPA.class, subProjetoAtividadeFacade);
        }
        return converteSubProjetoAtividade;
    }

    public Date getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(Date dataInicial) {
        this.dataInicial = dataInicial;
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    public String getFiltro() {
        return filtro;
    }

    public void setFiltro(String filtro) {
        this.filtro = filtro;
    }

    public UnidadeGestora getUnidadeGestora() {
        return unidadeGestora;
    }

    public void setUnidadeGestora(UnidadeGestora unidadeGestora) {
        this.unidadeGestora = unidadeGestora;
    }

    public List<HierarquiaOrganizacional> getListaUnidades() {
        return listaUnidades;
    }

    public void setListaUnidades(List<HierarquiaOrganizacional> listaUnidades) {
        this.listaUnidades = listaUnidades;
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

    public AcaoPPA getProjetoAtividade() {
        return projetoAtividade;
    }

    public void setProjetoAtividade(AcaoPPA projetoAtividade) {
        this.projetoAtividade = projetoAtividade;
    }

    public SubAcaoPPA getSubProjetoAtividade() {
        return subProjetoAtividade;
    }

    public void setSubProjetoAtividade(SubAcaoPPA subProjetoAtividade) {
        this.subProjetoAtividade = subProjetoAtividade;
    }

    public Conta getContaDespesa() {
        return contaDespesa;
    }

    public void setContaDespesa(Conta contaDespesa) {
        this.contaDespesa = contaDespesa;
    }

    public FonteDeRecursos getFonteDeRecursos() {
        return fonteDeRecursos;
    }

    public void setFonteDeRecursos(FonteDeRecursos fonteDeRecursos) {
        this.fonteDeRecursos = fonteDeRecursos;
    }
}
