package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.ApresentacaoRelatorio;
import br.com.webpublico.enums.OperacaoRelatorio;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.ExercicioFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.relatoriofacade.RelatorioQDDSuperFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-despesa-por-acaoppa", pattern = "/relatorio/despesa-por-acaoppa/", viewId = "/faces/financeiro/relatorio/relatoriodespesaporacaoppa.xhtml")
})
public class RelatorioDespesaPorAcaoPPAControlador implements Serializable {

    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private RelatorioQDDSuperFacade relatorioQDDSuperFacade;
    private ApresentacaoRelatorio apresentacao;
    private String filtro;
    private Date dataInicial;
    private Date dataFinal;
    private Exercicio exercicio;
    private DespesaORC despesaORC;
    private FonteDeRecursos fonteDeRecursos;
    private ContaDeDestinacao contaDeDestinacao;
    private SubAcaoPPA subAcaoPPA;
    private Funcao funcao;
    private SubFuncao subFuncao;
    private List<HierarquiaOrganizacional> hierarquias;

    @URLAction(mappingId = "relatorio-despesa-por-acaoppa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        dataInicial = sistemaFacade.getDataOperacao();
        dataFinal = sistemaFacade.getDataOperacao();
        exercicio = sistemaFacade.getExercicioCorrente();
        fonteDeRecursos = null;
        hierarquias = Lists.newArrayList();
        apresentacao = ApresentacaoRelatorio.ORGAO;
    }

    public void gerarRelatorio() {
        try {
            validarDatas();
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("USER", sistemaFacade.getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("MUNICIPIO", "Município de Rio Branco - AC ");
            dto.adicionarParametro("NOMERELATORIO", "Relatório de Despesa por Projeto/Atividade");
            dto.adicionarParametro("APRESENTACAO", apresentacao.name());
            dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(montarParametrosEFiltros()));
            dto.adicionarParametro("FILTRO", filtro);
            dto.setNomeRelatorio("RELATÓRIO-DESPESA-POR-PROJETO/ATIVIDADE");
            dto.setApi("contabil/despesa-por-acaoppa/");
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
        }
    }

    private List<ParametrosRelatorios> montarParametrosEFiltros() {
        List<ParametrosRelatorios> parametros = Lists.newArrayList();
        parametros.add(new ParametrosRelatorios(null, ":idExercicio", null, OperacaoRelatorio.IGUAL, exercicio.getId(), null, 1, false));
        parametros.add(new ParametrosRelatorios(" trunc(saldo.datasaldo) ", ":dataInicial", ":dataFinal", OperacaoRelatorio.BETWEEN, DataUtil.getDataFormatada(dataInicial), DataUtil.getDataFormatada(dataFinal), 1, true));
        filtro = " Período: " + DataUtil.getDataFormatada(dataInicial) + " à " + DataUtil.getDataFormatada(dataFinal) + " -";
        if (!hierarquias.isEmpty()) {
            List<Long> idsUnidades = Lists.newArrayList();
            String unidades = "";
            for (HierarquiaOrganizacional hierarquiaOrganizacional : hierarquias) {
                idsUnidades.add(hierarquiaOrganizacional.getSubordinada().getId());
                unidades += " " + hierarquiaOrganizacional.getCodigo().substring(3, 10) + " -";
            }
            filtro += " Unidade(s): " + unidades + " -";
            parametros.add(new ParametrosRelatorios(" vw.subordinada_id", ":undId", null, OperacaoRelatorio.IN, idsUnidades, null, 1, false));
        } else {
            List<Long> idsUnidades = Lists.newArrayList();
            List<HierarquiaOrganizacional> unidadesDoUsuario = relatorioQDDSuperFacade.getHierarquiaOrganizacionalFacade().listaHierarquiaUsuarioCorrentePorNivel("", sistemaFacade.getUsuarioCorrente(), exercicio, dataFinal, TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), 3);
            for (HierarquiaOrganizacional hierarquia : unidadesDoUsuario) {
                idsUnidades.add(hierarquia.getSubordinada().getId());
            }
            if (!idsUnidades.isEmpty()) {
                parametros.add(new ParametrosRelatorios(" vw.subordinada_id ", ":undId", null, OperacaoRelatorio.IN, idsUnidades, null, 1, false));
            }
        }
        if (subAcaoPPA != null) {
            parametros.add(new ParametrosRelatorios(" sb.id ", ":idSubAcao", null, OperacaoRelatorio.IGUAL, subAcaoPPA.getId(), null, 1, false));
            filtro += " Sub-Projeto/Atividade: " + subAcaoPPA.toString() + " -";
        }
        if (funcao != null) {
            parametros.add(new ParametrosRelatorios(" func.codigo ", ":codigoFuncao", null, OperacaoRelatorio.IGUAL, funcao.getCodigo(), null, 1, false));
            filtro += " Função: " + funcao.toString() + " -";
        }
        if (subFuncao != null) {
            parametros.add(new ParametrosRelatorios(" sub.codigo ", ":codigoSubfuncao", null, OperacaoRelatorio.IGUAL, subFuncao.getCodigo(), null, 1, false));
            filtro += " SubFunção: " + subFuncao.toString() + " -";
        }
        if (despesaORC != null) {
            parametros.add(new ParametrosRelatorios(" desp.id ", ":idDespesaOrc", null, OperacaoRelatorio.IGUAL, despesaORC.getId(), null, 1, false));
            filtro += " Elemento de Despesa: " + despesaORC.getProvisaoPPADespesa().getContaDeDespesa().toString() + " -";
        }
        if (fonteDeRecursos != null) {
            parametros.add(new ParametrosRelatorios(" fte.id ", ":idFonte", null, OperacaoRelatorio.IGUAL, fonteDeRecursos.getId(), null, 1, false));
            filtro += " Fonte de Recurso: " + fonteDeRecursos.toString() + " -";
        }
        if (contaDeDestinacao != null) {
            parametros.add(new ParametrosRelatorios(" cd.id ", ":idCDestinacao", null, OperacaoRelatorio.IGUAL, contaDeDestinacao.getId(), null, 1, false));
            filtro += " Conta de Destinação de Recurso: " + contaDeDestinacao.toString() + " -";
        }
        filtro =  filtro.substring(0, filtro.length() - 1);
        return parametros;
    }

    private void validarDatas() {
        ValidacaoException ve = new ValidacaoException();
        if (dataInicial == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data Inicial deve ser informado.");
        }
        if (dataFinal == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data Final deve ser informado.");
        }
        ve.lancarException();
        if (dataInicial.after(dataFinal)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Data Inicial não pode ser maior que a Data Final.");
        }
        if (DataUtil.getAno(dataInicial).compareTo(DataUtil.getAno(dataFinal)) != 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("As datas estão com exercícios diferentes");
        }
        ve.lancarException();
    }

    public void atualizarExercicioPelaDataFinal() {
        atualizarCamposQueUtilizamExercicio();
        if (dataFinal == null) {
            exercicio = sistemaFacade.getExercicioCorrente();
        }
        exercicio = exercicioFacade.getExercicioPorAno(DataUtil.getAno(dataFinal));
    }

    private void atualizarCamposQueUtilizamExercicio() {
        if (dataFinal != null && exercicio != null &&  DataUtil.getAno(dataFinal).compareTo(exercicio.getAno()) != 0) {
            contaDeDestinacao = null;
            fonteDeRecursos = null;
            subAcaoPPA = null;
            hierarquias = Lists.newArrayList();
        }
    }

    public List<SelectItem> getApresentacoes() {
        List<SelectItem> retorno = Lists.newArrayList();
        retorno.add(new SelectItem(ApresentacaoRelatorio.CONSOLIDADO, ApresentacaoRelatorio.CONSOLIDADO.getDescricao()));
        retorno.add(new SelectItem(ApresentacaoRelatorio.ORGAO, ApresentacaoRelatorio.ORGAO.getDescricao()));
        return retorno;
    }

    public List<Conta> completarContasDeDestinacao(String filtro) {
        return relatorioQDDSuperFacade.getContaFacade().buscarContasDeDestinacaoDeRecursos(filtro, exercicio);
    }

    public List<FonteDeRecursos> completarFonteDeRecursos(String parte) {
        return relatorioQDDSuperFacade.getFonteDeRecursosFacade().listaFiltrandoPorExercicio(parte.trim(), exercicio);
    }

    public List<SubAcaoPPA> completarSubProjetoAtividade(String parte) {
        return relatorioQDDSuperFacade.getSubProjetoAtividadeFacade().buscarSubProjetoAtividadePorExercicio(parte.trim(), exercicio);
    }

    public List<Funcao> completarFuncao(String parte) {
        return relatorioQDDSuperFacade.getFuncaoFacade().listaFiltrandoFuncao(parte.trim());
    }

    public List<SubFuncao> completarSubFuncao(String parte) {
        return relatorioQDDSuperFacade.getSubFuncaoFacade().listaFiltrandoSubFuncao(parte.trim());
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    public Date getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(Date dataInicial) {
        this.dataInicial = dataInicial;
    }

    public String getFiltro() {
        return filtro;
    }

    public void setFiltro(String filtro) {
        this.filtro = filtro;
    }

    public List<HierarquiaOrganizacional> getHierarquias() {
        return hierarquias;
    }

    public void setHierarquias(List<HierarquiaOrganizacional> hierarquias) {
        this.hierarquias = hierarquias;
    }

    public FonteDeRecursos getFonteDeRecursos() {
        return fonteDeRecursos;
    }

    public void setFonteDeRecursos(FonteDeRecursos fonteDeRecursos) {
        this.fonteDeRecursos = fonteDeRecursos;
    }

    public DespesaORC getDespesaORC() {
        return despesaORC;
    }

    public void setDespesaORC(DespesaORC despesaORC) {
        this.despesaORC = despesaORC;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public ContaDeDestinacao getContaDeDestinacao() {
        return contaDeDestinacao;
    }

    public void setContaDeDestinacao(ContaDeDestinacao contaDeDestinacao) {
        this.contaDeDestinacao = contaDeDestinacao;
    }

    public SubAcaoPPA getSubAcaoPPA() {
        return subAcaoPPA;
    }

    public void setSubAcaoPPA(SubAcaoPPA subAcaoPPA) {
        this.subAcaoPPA = subAcaoPPA;
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

    public ApresentacaoRelatorio getApresentacao() {
        return apresentacao;
    }

    public void setApresentacao(ApresentacaoRelatorio apresentacao) {
        this.apresentacao = apresentacao;
    }
}
