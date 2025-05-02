package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.OperacaoRelatorio;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.relatoriofacade.RelatorioContabilSuperFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
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

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "consulta-despesa-funcao-subfuncao", pattern = "/consulta-despesa-funcao-subfuncao/", viewId = "/faces/financeiro/relatorio/consultadespesafuncao.xhtml")
})
public class ConsultaDespesaFuncaoSubfuncaoControlador extends RelatorioContabilSuperControlador implements Serializable {

    @EJB
    private RelatorioContabilSuperFacade consultaFacade;
    private ContaDeDestinacao contaDeDestinacao;
    private Tipo tipo;

    @URLAction(mappingId = "consulta-despesa-funcao-subfuncao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        limparCamposGeral();
    }

    public void limparCamposSemData() {
        if (!DataUtil.getDataFormatada(dataInicial, "yyyy").equals(DataUtil.getDataFormatada(dataFinal, "yyyy"))) {
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
    }

    public List<SelectItem> getTipos() {
        return Util.getListSelectItem(Tipo.values());
    }

    public List<Funcao> completarFuncoes(String filtro) {
        return consultaFacade.getFuncaoFacade().listaFiltrandoFuncao(filtro.trim());
    }

    public List<SubFuncao> completarSubFuncoes(String filtro) {
        return consultaFacade.getSubFuncaoFacade().listaFiltrandoSubFuncao(filtro);
    }

    public List<ProgramaPPA> completarProgramas(String filtro) {
        return consultaFacade.getProgramaPPAFacade().buscarProgramasPorExercicio(filtro, buscarExercicioPelaDataFinal());
    }

    public List<TipoAcaoPPA> completarTiposAcoesPPA(String filtro) {
        return consultaFacade.getTipoAcaoPPAFacade().listaFiltrando(filtro.trim(), "descricao");
    }

    public List<AcaoPPA> completarAcoesPPA(String filtro) {
        return consultaFacade.getProjetoAtividadeFacade().buscarAcoesPPAPorExercicio(filtro.trim(), buscarExercicioPelaDataFinal());
    }

    public List<SubAcaoPPA> completarSubAcoesPPA(String filtro) {
        if (acaoPPA != null) {
            return consultaFacade.getSubProjetoAtividadeFacade().buscarSubPorProjetoAtividades(filtro.trim(), acaoPPA);
        } else {
            return consultaFacade.getSubProjetoAtividadeFacade().buscarSubProjetoAtividadePorExercicio(filtro.trim(), buscarExercicioPelaDataFinal());
        }
    }

    public List<Conta> completarContasDespesa(String filtro) {
        return consultaFacade.getContaFacade().buscarContasDespesaAteNivelPorExercicio(filtro.trim(), buscarExercicioPelaDataFinal(), 4);
    }

    public List<FonteDeRecursos> completarFontesDeRecursos(String filtro) {
        return consultaFacade.getFonteDeRecursosFacade().listaFiltrandoPorExercicio(filtro.trim(), buscarExercicioPelaDataFinal());
    }

    public List<ContaDeDestinacao> completarContasDeDestinacao(String filtro) {
        return consultaFacade.getContaFacade().buscarContasDeDestinacaoPorCodigoOrDescricao(filtro.trim(), buscarExercicioPelaDataFinal());
    }

    public void limparSubAcaoPPA() {
        subAcaoPPA = null;
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USUARIO", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO");
            dto.adicionarParametro("NOME_RELATORIO", "Consulta de Despesa por Função e Subfunção");
            dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(montarParametros()));
            dto.adicionarParametro("pesquisouUg", unidadeGestora != null);
            dto.adicionarParametro("tipoCondicaoIntra", tipo != null ? tipo.getCondicao() : "");
            dto.adicionarParametro("filtrosUtilizados", filtros);
            dto.setNomeRelatorio(getNomeRelatorio());
            dto.setApi("contabil/consulta-despesa-funcao-subfuncao/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            logger.error("Erro ao gerar relatório: ", ex);
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private List<ParametrosRelatorios> montarParametros() {
        List<ParametrosRelatorios> parametros = Lists.newArrayList();
        parametros.add(new ParametrosRelatorios(null, ":DATAINICIAL", ":DATAFINAL", OperacaoRelatorio.BETWEEN, DataUtil.getDataFormatada(dataInicial), DataUtil.getDataFormatada(dataFinal), 0, true));
        filtros = "Período: " + DataUtil.getDataFormatada(dataInicial) + " a " + DataUtil.getDataFormatada(dataFinal) + " -";
        filtrosParametrosUnidade(parametros);
        if (funcao != null) {
            parametros.add(new ParametrosRelatorios(" f.id ", ":funcaoId", null, OperacaoRelatorio.IGUAL, funcao.getId(), null, 1, false));
            filtros += " Função: " + funcao.getCodigo() + " -";
        }
        if (subFuncao != null) {
            parametros.add(new ParametrosRelatorios(" sf.id ", ":subfuncaoId", null, OperacaoRelatorio.IGUAL, subFuncao.getId(), null, 1, false));
            filtros += " SubFunção: " + subFuncao.getCodigo() + " -";
        }
        if (programaPPA != null) {
            parametros.add(new ParametrosRelatorios(" prog.id ", ":progId", null, OperacaoRelatorio.IGUAL, programaPPA.getId(), null, 1, false));
            filtros += " Programa: " + programaPPA.getCodigo() + " -";
        }
        if (tipoAcaoPPA != null) {
            parametros.add(new ParametrosRelatorios(" tpa.id ", ":tipoAcaoId", null, OperacaoRelatorio.IGUAL, tipoAcaoPPA.getId(), null, 1, false));
            filtros += " Tipo de Ação: " + tipoAcaoPPA.getCodigo() + " -";
        }
        if (acaoPPA != null) {
            parametros.add(new ParametrosRelatorios(" a.id ", ":acaoId", null, OperacaoRelatorio.IGUAL, acaoPPA.getId(), null, 1, false));
            filtros += " Projeto/Atividade: " + acaoPPA.getCodigo() + " -";
        }
        if (subAcaoPPA != null) {
            parametros.add(new ParametrosRelatorios(" sub.id ", ":subAcaoId", null, OperacaoRelatorio.IGUAL, subAcaoPPA.getId(), null, 1, false));
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
            parametros.add(new ParametrosRelatorios(" c.codigo ", ":cCodigo", null, OperacaoRelatorio.LIKE, conta.getCodigoSemZerosAoFinal() + "%", null, 1, false));
            filtros += " Conta de Despesa: " + conta.getCodigo() + " -";
        }
        if (fonteDeRecursos != null) {
            parametros.add(new ParametrosRelatorios(" fr.id ", ":frId", null, OperacaoRelatorio.IGUAL, fonteDeRecursos.getId(), null, 1, false));
            filtros += " Fonte de Recurso: " + fonteDeRecursos.getCodigo() + " -";
        }
        if (contaDeDestinacao != null) {
            parametros.add(new ParametrosRelatorios(" cd.id ", ":cDest", null, OperacaoRelatorio.IGUAL, contaDeDestinacao.getId(), null, 1, false));
            filtros += " Conta de Destinação: " + contaDeDestinacao.getCodigo() + " -";
        }
        atualizaFiltrosGerais();
        adicionarExercicio(parametros);
        return parametros;
    }

    @Override
    public void adicionarExercicio(List<ParametrosRelatorios> parametros) {
        parametros.add(new ParametrosRelatorios(null, ":exercicio", null, null, buscarExercicioPelaDataFinal().getId(), null, 0, false));
    }

    @Override
    public String getNomeRelatorio() {
        return "CONSULTA-DESPESA-FUNCAO-SUBFUNCAO";
    }

    public ContaDeDestinacao getContaDeDestinacao() {
        return contaDeDestinacao;
    }

    public void setContaDeDestinacao(ContaDeDestinacao contaDeDestinacao) {
        this.contaDeDestinacao = contaDeDestinacao;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public void setTipo(Tipo tipo) {
        this.tipo = tipo;
    }

    public enum Tipo {
        EXCETO_INTRA("Exceto Intra", " AND SUBSTR(c.codigo, 5,2) <> '91' "),
        INTRA("Intra", " AND SUBSTR(c.codigo, 5,2) = '91' ");

        private String descricao;
        private String condicao;

        Tipo(String descricao, String condicao) {
            this.descricao = descricao;
            this.condicao = condicao;
        }

        public String getDescricao() {
            return descricao;
        }

        public String getCondicao() {
            return condicao;
        }

        @Override
        public String toString() {
            return descricao;
        }
    }
}
