package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.OperacaoRelatorio;
import br.com.webpublico.enums.SubTipoDespesa;
import br.com.webpublico.enums.TipoContaDespesa;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-relatorio-obrigacao-pagar", pattern = "/relatorio/obrigacao-a-pagar/", viewId = "/faces/financeiro/relatorio/relatorioobrigacaopagar.xhtml")})
@ManagedBean
public class RelatorioObrigacaoAPagarControlador extends RelatorioContabilSuperControlador {

    private TipoLancamento tipoLancamentoObrigacao;
    private ContaDeDestinacao contaDeDestinacao;
    private TipoContaDespesa tipoContaDespesa;
    private SubTipoDespesa subTipoDespesa;
    private String numeroEmpenhoResto;
    private Date dataEmpenhoResto;
    private String numeroObrigacaoPagar;
    private BigDecimal valor;

    @URLAction(mappingId = "novo-relatorio-obrigacao-pagar", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        limparCamposGeral();
        valor = null;
        tipoLancamentoObrigacao = null;
        contaDeDestinacao = null;
        tipoContaDespesa = null;
        subTipoDespesa = null;
        numeroEmpenhoResto = null;
        dataEmpenhoResto = null;
        numeroObrigacaoPagar = null;
    }

    public List<SelectItem> getTiposLancamento() {
        return Util.getListSelectItem(TipoLancamento.values(), false);
    }

    public List<SelectItem> getTiposDeContaDeDespesa() {
        if (conta != null) {

            List<SelectItem> toReturn = Lists.newArrayList();
            toReturn.add(new SelectItem(null, ""));
            TipoContaDespesa tipo = ((ContaDespesa) conta).getTipoContaDespesa();
            if (!tipo.isNaoAplicavel()) {
                tipoContaDespesa = tipo;
                toReturn.add(new SelectItem(tipo, tipo.getDescricao()));
            } else {
                for (TipoContaDespesa tp : buscarTipoContaDespesa()) {
                    if (!tp.isNaoAplicavel()) {
                        toReturn.add(new SelectItem(tp, tp.getDescricao()));
                    }
                }
            }
            return toReturn;
        } else {
            return Util.getListSelectItem(TipoContaDespesa.values());
        }
    }

    private List<TipoContaDespesa> buscarTipoContaDespesa() {
        if (conta != null) {
            TipoContaDespesa tipo = ((ContaDespesa) conta).getTipoContaDespesa();
            if (tipo != null && tipo.isNaoAplicavel()) {
                return relatorioContabilSuperFacade.getContaFacade().buscarTiposContasDespesaNosFilhosDaConta((ContaDespesa) conta);
            }
        }
        return Lists.newArrayList();
    }

    public List<SubTipoDespesa> getSubTiposDeContas() {
        List<SubTipoDespesa> toReturn = Lists.newArrayList();
        if (tipoContaDespesa != null) {
            if (tipoContaDespesa.isPessoaEncargos()) {
                toReturn.add(SubTipoDespesa.RGPS);
                toReturn.add(SubTipoDespesa.RPPS);
            } else if (tipoContaDespesa.isDividaPublica() || tipoContaDespesa.isPrecatorio()) {
                toReturn.add(SubTipoDespesa.JUROS);
                toReturn.add(SubTipoDespesa.OUTROS_ENCARGOS);
                toReturn.add(SubTipoDespesa.VALOR_PRINCIPAL);
            } else {
                for (SubTipoDespesa std : SubTipoDespesa.values()) {
                    toReturn.add(std);
                }
            }
        }
        return toReturn;
    }

    public void definirSubTipoDespesaPorTipoDespesa() {
        if (tipoContaDespesa != null) {
            if (!tipoContaDespesa.isPessoaEncargos() && !tipoContaDespesa.isDividaPublica() && !tipoContaDespesa.isPrecatorio()) {
                subTipoDespesa = SubTipoDespesa.NAO_APLICAVEL;
            } else {
                subTipoDespesa = null;
            }
        }
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarDatasSemVerificarExercicioLogado();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("IMAGEM");
            dto.setNomeParametroSubreportDir("SUBREPORT_DIR");
            dto.adicionarParametro("MUNICIPIO", getDescricaoSegundoEsferaDoPoder());
            dto.adicionarParametro("ANO_EXERCICIO", getSistemaControlador().getExercicioCorrente().getAno().toString());
            dto.adicionarParametro("EXERCICIO", getSistemaControlador().getExercicioCorrente().getId());
            dto.adicionarParametro("apresentacao", apresentacao.getToDto());
            dto.adicionarParametro("pesquisouUg", unidadeGestora != null);
            dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(montarParametros()));
            dto.adicionarParametro("FILTRO", getFiltrosAtualizados());
            dto.setNomeRelatorio(getNomeRelatorio());
            dto.setApi("contabil/obrigacao-a-pagar/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private String getFiltrosAtualizados() {
        filtros = getFiltrosPeriodo() + filtros;
        return atualizaFiltrosGerais();
    }

    private List<ParametrosRelatorios> montarParametros() {
        List<ParametrosRelatorios> parametros = Lists.newArrayList();
        filtros = "";
        parametros.add(new ParametrosRelatorios(null, ":DATAINICIAL", ":DATAFINAL", OperacaoRelatorio.BETWEEN, DataUtil.getDataFormatada(dataInicial), DataUtil.getDataFormatada(dataFinal), 0, true));
        if (!Strings.isNullOrEmpty(numero)) {
            parametros.add(new ParametrosRelatorios(" obr.numero ", ":numero", null, OperacaoRelatorio.LIKE, numero, null, 1, false));
            filtros += " Número: " + numero + " -";
        }
        if (tipoLancamentoObrigacao != null) {
            parametros.add(new ParametrosRelatorios(" tipoLancamento ", ":tipo", null, OperacaoRelatorio.IGUAL, tipoLancamentoObrigacao.getSigla(), null, 2, false));
            filtros += " Tipo de Lançamento: " + tipoLancamentoObrigacao.getDescricao() + " -";
        }
        if (!Strings.isNullOrEmpty(numeroEmpenhoResto)) {
            parametros.add(new ParametrosRelatorios(" emp.numero ", ":emp", null, OperacaoRelatorio.IGUAL, numeroEmpenhoResto, null, 1, false));
            filtros += " Número do Empenho/Restos a Pagar: " + numeroEmpenhoResto + " -";
        }
        if (dataEmpenhoResto != null) {
            parametros.add(new ParametrosRelatorios(" trunc(emp.DATAEMPENHO) ", ":dataEmp", null, OperacaoRelatorio.IGUAL, DataUtil.getDataFormatada(dataEmpenhoResto), null, 1, true));
            filtros += " Data do Empenho/Restos a Pagar: " + numeroEmpenhoResto + " -";
        }
        if (funcao != null) {
            parametros.add(new ParametrosRelatorios(" f.ID ", ":funcaoId", null, OperacaoRelatorio.IGUAL, funcao.getId(), null, 1, false));
            filtros += " Função: " + funcao.getCodigo() + " -";
        }
        if (subFuncao != null) {
            parametros.add(new ParametrosRelatorios(" sf.ID ", ":subfuncaoId", null, OperacaoRelatorio.IGUAL, subFuncao.getId(), null, 1, false));
            filtros += " Subfunção: " + subFuncao.getCodigo() + " -";
        }
        if (programaPPA != null) {
            parametros.add(new ParametrosRelatorios(" prog.ID ", ":programaId", null, OperacaoRelatorio.IGUAL, programaPPA.getId(), null, 1, false));
            filtros += " Programa: " + programaPPA.getCodigo() + " -";
        }
        if (tipoAcaoPPA != null) {
            parametros.add(new ParametrosRelatorios(" tpa.ID ", ":tipoAcaoId", null, OperacaoRelatorio.IGUAL, tipoAcaoPPA.getId(), null, 1, false));
            filtros += " Tipo de Ação: " + tipoAcaoPPA.getCodigo() + " -";
        }
        if (acaoPPA != null) {
            parametros.add(new ParametrosRelatorios(" a.ID ", ":acId", null, OperacaoRelatorio.IGUAL, acaoPPA.getId(), null, 1, false));
            filtros += " Projeto/Atividade: " + acaoPPA.getCodigo() + " -";
        }
        if (subAcaoPPA != null) {
            parametros.add(new ParametrosRelatorios(" sub.ID ", ":subId", null, OperacaoRelatorio.IGUAL, subAcaoPPA.getId(), null, 1, false));
            filtros += " Subprojeto/Atividade: " + subAcaoPPA.getCodigo() + " -";
        }
        if (conta != null) {
            parametros.add(new ParametrosRelatorios(" c.ID ", ":contaId", null, OperacaoRelatorio.IGUAL, conta.getId(), null, 1, false));
            filtros += " Conta de Despesa: " + conta.getCodigo() + " -";
        }
        if (tipoContaDespesa != null) {
            parametros.add(new ParametrosRelatorios(" obr.TIPOCONTADESPESA ", ":tipoContaDespesa", null, OperacaoRelatorio.IGUAL, tipoContaDespesa.name(), null, 1, false));
            filtros += " Tipo de Despesa: " + tipoContaDespesa.getDescricao() + " -";
        }
        if (subTipoDespesa != null) {
            parametros.add(new ParametrosRelatorios(" obr.SUBTIPODESPESA ", ":subtipo", null, OperacaoRelatorio.IGUAL, subTipoDespesa.name(), null, 1, false));
            filtros += " Subtipo de Despesa: " + subTipoDespesa.getDescricao() + " -";
        }
        if (fonteDeRecursos != null) {
            parametros.add(new ParametrosRelatorios(" fr.ID ", ":fonteId", null, OperacaoRelatorio.IGUAL, fonteDeRecursos.getId(), null, 1, false));
            filtros += " Fonte de Recurso: " + fonteDeRecursos.getCodigo() + " -";
        }
        if (contaDeDestinacao != null) {
            parametros.add(new ParametrosRelatorios(" cd.ID ", ":destId", null, OperacaoRelatorio.IGUAL, contaDeDestinacao.getId(), null, 1, false));
            filtros += " Conta de Destinação: " + contaDeDestinacao.getCodigo() + " -";
        }
        if (pessoa != null) {
            parametros.add(new ParametrosRelatorios(" obr.pessoa_id ", ":fornecedor", null, OperacaoRelatorio.IGUAL, pessoa.getId(), null, 1, false));
            filtros += " Pessoa: " + pessoa.getNome() + " -";
        }
        if (classeCredor != null) {
            parametros.add(new ParametrosRelatorios(" cc.id ", ":classe", null, OperacaoRelatorio.IGUAL, classeCredor.getId(), null, 1, false));
            filtros += " Classe: " + classeCredor.getCodigo() + " -";
        }
        if (!Strings.isNullOrEmpty(numeroObrigacaoPagar)) {
            parametros.add(new ParametrosRelatorios(" obrOriginal.numero ", ":obrOriginal", null, OperacaoRelatorio.IGUAL, numeroObrigacaoPagar, null, 1, false));
            filtros += " Número da Obrigação a Pagar de Referência: " + numeroObrigacaoPagar + " -";
        }
        if (valor != null && BigDecimal.ZERO.compareTo(valor) != 0) {
            parametros.add(new ParametrosRelatorios(" valor ", ":valor", null, OperacaoRelatorio.IGUAL, valor, null, 2, false));
            filtros += " Valor: " + Util.formataValor(valor) + " -";
        }
        filtrosParametrosUnidade(parametros);
        if (apresentacao.isApresentacaoUnidadeGestora()) {
            adicionarExercicio(parametros);
        }
        return parametros;
    }

    public void limparContasAndUnidades() {
        fonteDeRecursos = null;
        contaDeDestinacao = null;
        conta = null;
        listaUnidades = Lists.newArrayList();
        unidadeGestora = null;
        programaPPA = null;
        acaoPPA = null;
        subAcaoPPA = null;
    }

    public List<ProgramaPPA> completarProgramas(String filtro) {
        return relatorioContabilSuperFacade.getProgramaPPAFacade().listaFiltrandoProgramasPorExercicio(filtro, buscarExercicioPelaDataFinal());
    }

    public List<TipoAcaoPPA> completarTiposDeAcao(String filtro) {
        return relatorioContabilSuperFacade.getTipoAcaoPPAFacade().listaFiltrando(filtro.trim(), "codigo", "descricao");
    }

    public List<AcaoPPA> completarProjetosAtividade(String filtro) {
        return relatorioContabilSuperFacade.getProjetoAtividadeFacade().buscarAcoesPPAPorExercicio(filtro, buscarExercicioPelaDataFinal());
    }

    public List<SubAcaoPPA> completarSubprojetosAtividade(String filtro) {
        return relatorioContabilSuperFacade.getSubProjetoAtividadeFacade().buscarSubProjetoAtividadePorExercicio(filtro, buscarExercicioPelaDataFinal());
    }

    public void limparClasseCredor() {
        classeCredor = null;
    }

    public List<Conta> completarContasDeDespesa(String filtro) {
        return relatorioContabilSuperFacade.getContaFacade().listaFiltrandoContaDespesa(filtro, buscarExercicioPelaDataFinal());
    }

    public List<ContaDeDestinacao> completarContasDeDestinacao(String filtro) {
        return relatorioContabilSuperFacade.getContaFacade().buscarContasDeDestinacaoPorCodigoOrDescricao(filtro, buscarExercicioPelaDataFinal());
    }

    @Override
    public List<FonteDeRecursos> completarFonteRecurso(String parte) {
        return relatorioContabilSuperFacade.getFonteDeRecursosFacade().listaFiltrandoPorExercicio(parte.trim(), buscarExercicioPelaDataFinal());
    }

    public List<Pessoa> completarFornecedores(String parte) {
        return relatorioContabilSuperFacade.getPessoaFacade().listaTodasPessoas(parte.trim());
    }

    public List<ClasseCredor> completarClassesCredor(String parte) {
        if (pessoa != null) {
            return relatorioContabilSuperFacade.getClasseCredorFacade().buscarClassesPorPessoa(parte.trim(), pessoa);
        } else {
            return relatorioContabilSuperFacade.getClasseCredorFacade().listaFiltrandoDescricao(parte.trim());
        }
    }

    @Override
    public String getNomeRelatorio() {
        return "RELATORIO-OBRIGACAO-A-PAGAR";
    }

    public TipoLancamento getTipoLancamentoObrigacao() {
        return tipoLancamentoObrigacao;
    }

    public void setTipoLancamentoObrigacao(TipoLancamento tipoLancamentoObrigacao) {
        this.tipoLancamentoObrigacao = tipoLancamentoObrigacao;
    }

    public ContaDeDestinacao getContaDeDestinacao() {
        return contaDeDestinacao;
    }

    public void setContaDeDestinacao(ContaDeDestinacao contaDeDestinacao) {
        this.contaDeDestinacao = contaDeDestinacao;
    }

    public TipoContaDespesa getTipoContaDespesa() {
        return tipoContaDespesa;
    }

    public void setTipoContaDespesa(TipoContaDespesa tipoContaDespesa) {
        this.tipoContaDespesa = tipoContaDespesa;
    }

    public SubTipoDespesa getSubTipoDespesa() {
        return subTipoDespesa;
    }

    public void setSubTipoDespesa(SubTipoDespesa subTipoDespesa) {
        this.subTipoDespesa = subTipoDespesa;
    }

    public String getNumeroEmpenhoResto() {
        return numeroEmpenhoResto;
    }

    public void setNumeroEmpenhoResto(String numeroEmpenhoResto) {
        this.numeroEmpenhoResto = numeroEmpenhoResto;
    }

    public Date getDataEmpenhoResto() {
        return dataEmpenhoResto;
    }

    public void setDataEmpenhoResto(Date dataEmpenhoResto) {
        this.dataEmpenhoResto = dataEmpenhoResto;
    }

    public String getNumeroObrigacaoPagar() {
        return numeroObrigacaoPagar;
    }

    public void setNumeroObrigacaoPagar(String numeroObrigacaoPagar) {
        this.numeroObrigacaoPagar = numeroObrigacaoPagar;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public enum TipoLancamento {
        NORMAL("Normal", "N"),
        ESTORNO("Estorno", "E");

        private String descricao;
        private String sigla;

        TipoLancamento(String descricao, String sigla) {
            this.descricao = descricao;
            this.sigla = sigla;
        }

        public String getDescricao() {
            return descricao;
        }

        public String getSigla() {
            return sigla;
        }

        @Override
        public String toString() {
            return descricao;
        }
    }
}
