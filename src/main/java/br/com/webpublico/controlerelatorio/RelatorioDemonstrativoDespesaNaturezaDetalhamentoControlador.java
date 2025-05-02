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
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Criado por Mateus
 * Data: 15/02/2017.
 */
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-demonstrativo-despesa-natureza-detalhamento", pattern = "/relatorio/demonstrativo-despesa-por-natureza-despesa-detalhamento/", viewId = "/faces/financeiro/relatorio/relatoriodemonstrativodespesadetalhamento.xhtml")
})
@ManagedBean
public class RelatorioDemonstrativoDespesaNaturezaDetalhamentoControlador extends RelatorioContabilSuperControlador implements Serializable {

    @EJB
    private RelatorioContabilSuperFacade facade;

    @URLAction(mappingId = "relatorio-demonstrativo-despesa-natureza-detalhamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
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
        return facade.getContaFacade().buscarContasDespesaAteNivelPorExercicio(filtro.trim(), buscarExercicioPelaDataFinal(), 4);
    }

    public List<FonteDeRecursos> completarFontesDeRecursos(String filtro) {
        return facade.getFonteDeRecursosFacade().listaFiltrandoPorExercicio(filtro.trim(), buscarExercicioPelaDataFinal());
    }

    public void limparSubAcaoPPA() {
        subAcaoPPA = null;
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarDatasSemVerificarExercicioLogado();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            HashMap mapParameters = Maps.newHashMap();
            List<ParametrosRelatorios> parametros = Lists.newArrayList();
            montarParametros(mapParameters, parametros);
            dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(parametros));
            dto.adicionarParametro("mapParametros", mapParameters);
            dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.adicionarParametro("MUNICIPIO", getDescricaoSegundoEsferaDoPoder());
            dto.adicionarParametro("APRESENTACAO", apresentacao.name());
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("pesquisouUg", unidadeGestora != null);
            filtros = getFiltrosPeriodo() + filtros;
            dto.adicionarParametro("FILTRO", atualizaFiltrosGerais());
            dto.setNomeRelatorio(getNomeRelatorio());
            dto.setApi("contabil/demonstrativo-despesa-natureza-detalhamento/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getAllMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public void montarParametros(HashMap mapParameters, List<ParametrosRelatorios> parametros) {
        filtros = "";
        montarFiltroUnidade(mapParameters, parametros);
        montarFiltrosGerais(mapParameters, parametros);
        mapParameters.put("DATAINICIAL", DataUtil.getDataFormatada(dataInicial));
        mapParameters.put("DATAFINAL", DataUtil.getDataFormatada(dataFinal));
        mapParameters.put("EXERCICIO_ID", buscarExercicioPelaDataFinal().getId());
        mapParameters.put("apresentacao", apresentacao.name());
    }

    private void montarFiltrosGerais(HashMap parameters, List<ParametrosRelatorios> parametros) {
        if (funcao != null) {
            parametros.add(new ParametrosRelatorios(" f.id ", ":funcaoId", null, OperacaoRelatorio.IGUAL, funcao.getId(), null, 1, false));
            parameters.put("funcaoId", funcao.getId());
            filtros += " Função: " + funcao.getCodigo() + " -";
        }
        if (subFuncao != null) {
            parametros.add(new ParametrosRelatorios(" sf.id ", ":subfuncaoId", null, OperacaoRelatorio.IGUAL, subFuncao.getId(), null, 1, false));
            parameters.put("subfuncaoId", subFuncao.getId());
            filtros += " SubFunção: " + subFuncao.getCodigo() + " -";
        }
        if (programaPPA != null) {
            parametros.add(new ParametrosRelatorios(" prog.id ", ":progId", null, OperacaoRelatorio.IGUAL, programaPPA.getId(), null, 1, false));
            parameters.put("progId", programaPPA.getId());
            filtros += " Programa: " + programaPPA.getCodigo() + " -";
        }
        if (tipoAcaoPPA != null) {
            parametros.add(new ParametrosRelatorios(" tpa.id ", ":tipoAcaoId", null, OperacaoRelatorio.IGUAL, tipoAcaoPPA.getId(), null, 1, false));
            parameters.put("tipoAcaoId", tipoAcaoPPA.getId());
            filtros += " Tipo de Ação: " + tipoAcaoPPA.getCodigo() + " -";
        }
        if (acaoPPA != null) {
            parametros.add(new ParametrosRelatorios(" a.id ", ":acaoId", null, OperacaoRelatorio.IGUAL, acaoPPA.getId(), null, 1, false));
            parameters.put("acaoId", acaoPPA.getId());
            filtros += " Projeto/Atividade: " + acaoPPA.getCodigo() + " -";
        }
        if (subAcaoPPA != null) {
            parametros.add(new ParametrosRelatorios(" sub.id ", ":subAcaoId", null, OperacaoRelatorio.IGUAL, subAcaoPPA.getId(), null, 1, false));
            parameters.put("subAcaoId", subAcaoPPA.getId());
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
            parameters.put("cCodigo", conta.getCodigoSemZerosAoFinal() + "%");
            filtros += " Conta de Despesa: " + conta.getCodigo() + " -";
        }
        if (fonteDeRecursos != null) {
            parametros.add(new ParametrosRelatorios(" fr.id ", ":frId", null, OperacaoRelatorio.IGUAL, fonteDeRecursos.getId(), null, 1, false));
            parameters.put("frId", fonteDeRecursos.getId());
            filtros += " Fonte de Recurso: " + fonteDeRecursos.getCodigo() + " -";
        }
        if (unidadeGestora != null) {
            parametros.add(new ParametrosRelatorios(" ug.id ", ":ugId", null, OperacaoRelatorio.IGUAL, unidadeGestora.getId(), null, 1, false));
            parameters.put("ugId", unidadeGestora.getId());
            filtros += " Unidade Gestora: " + unidadeGestora.getCodigo() + " -";
        }

        parameters.put("exercicioLogado_id", buscarExercicioPelaDataFinal().getId());
        parameters.put("exercicioLogadoAno", buscarExercicioPelaDataFinal().getAno());
    }

    private void montarFiltroUnidade(HashMap parameters, List<ParametrosRelatorios> parametros) {
        List<Long> idsUnidades = new ArrayList<>();
        if (listaUnidades.size() > 0) {
            String unidades = "";
            for (HierarquiaOrganizacional hierarquiaOrganizacional : listaUnidades) {
                idsUnidades.add(hierarquiaOrganizacional.getSubordinada().getId());
                unidades += " " + hierarquiaOrganizacional.getCodigo().substring(3, 10) + " -";
            }
            filtros += " Unidade(s): " + unidades;
        } else if (this.unidadeGestora == null) {
            List<UnidadeOrganizacional> unidadesUsuario = recuperarUnidadesUsuario();
            for (UnidadeOrganizacional unidadeOrganizacional : unidadesUsuario) {
                idsUnidades.add(unidadeOrganizacional.getId());
            }
        }
        if (!idsUnidades.isEmpty()) {
            parametros.add(new ParametrosRelatorios(" vw.subordinada_id ", ":undId", null, OperacaoRelatorio.IN, idsUnidades, null, 1, false));
            parameters.put("undId", idsUnidades);
        }
    }

    @Override
    public String getNomeRelatorio() {
        return "DDNDD";
    }
}
