package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.ClasseCredor;
import br.com.webpublico.entidades.Conta;
import br.com.webpublico.entidades.FonteDeRecursos;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.OperacaoRelatorio;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-extrato-empenho", pattern = "/relatorio/extrato-empenho-despesa-resto-a-pagar/", viewId = "/faces/financeiro/relatorio/relatorioextratoempenho.xhtml")
})
@ManagedBean
public class RelatorioExtratoEmpenhoControlador extends RelatorioContabilSuperControlador implements Serializable {

    private Boolean quebrarPorEmpenho;
    private Conta contaDestinacao;

    @URLAction(mappingId = "relatorio-extrato-empenho", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        limparCamposGeral();
        pessoa = null;
        quebrarPorEmpenho = false;
    }

    public List<FonteDeRecursos> completarFontesDeRecursos(String parte) {
        return relatorioContabilSuperFacade.getFonteDeRecursosFacade().listaFiltrandoPorExercicio(parte, getSistemaControlador().getExercicioCorrente());
    }

    public void gerarRelatorio() {
        try {
            validarDatasComDataReferenciaSemVerificarExercicioLogado();
            validarFiltros();
            exercicio = relatorioContabilSuperFacade.getEmpenhoFacade().getExercicioFacade().listaFiltrandoEspecial(String.valueOf(DataUtil.getAno(dataInicial))).get(0);
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getNome());
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("MUNICIPIO", getDescricaoSegundoEsferaDoPoder());
            dto.adicionarParametro("ANO_EXERCICIO", getSistemaControlador().getExercicioCorrente().getAno().toString());
            dto.adicionarParametro("EXERCICIO", getSistemaControlador().getExercicioCorrente().getId());
            dto.adicionarParametro("QUEBRA_EMPENHO", quebrarPorEmpenho);
            dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(montarParametros()));
            dto.adicionarParametro("pesquisouUg", unidadeGestora != null);
            dto.adicionarParametro("apresentacaoRelatorioDTO", apresentacao.getToDto());
            dto.adicionarParametro("FILTRO", getFiltrosAtualizados());
            dto.setNomeRelatorio(getNomeRelatorio());
            dto.setApi("contabil/extrato-empenho/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
        }
    }

    private String getFiltrosAtualizados() {
        filtros = getFiltrosPeriodo() + filtros;
        filtros = filtros.length() == 0 ? " " : filtros.substring(0, filtros.length() - 1);
        return filtros;
    }

    private void validarFiltros() {
        ValidacaoException ve = new ValidacaoException();
        if (pessoa == null && classeCredor == null && conta == null && contaDestinacao == null && fonteDeRecursos == null && contaBancariaEntidade == null &&
            contaFinanceira == null && listaUnidades.isEmpty() && unidadeGestora == null && (numero == null || numero.isEmpty())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("É obrigatório selecionar ao menos um filtro para gerar o relatório.");
        }
        ve.lancarException();
    }

    public List<Conta> completarContasDespesa(String parte) {
        return relatorioContabilSuperFacade.getContaFacade().listaFiltrandoContaDespesa(parte, getSistemaControlador().getExercicioCorrente());
    }

    public List<ClasseCredor> completarClassesCredor(String parte) {
        return relatorioContabilSuperFacade.getClasseCredorFacade().listaFiltrandoDescricao(parte.trim());
    }

    public List<Conta> completarContasDestinacao(String parte) {
        return relatorioContabilSuperFacade.getContaFacade().listaFiltrandoContaDestinacaoPorTpoDestinacao(parte.trim(), getSistemaControlador().getExercicioCorrente());
    }

    public void buscarContaBancariaDaContaFinanceira() {
        try {
            fonteDeRecursos = null;
            contaBancariaEntidade = contaFinanceira.getContaBancariaEntidade();
        } catch (Exception e) {
            contaBancariaEntidade = null;
        }
    }

    private List<ParametrosRelatorios> montarParametros() {
        List<ParametrosRelatorios> parametros = Lists.newArrayList();
        filtros = "";

        parametros.add(new ParametrosRelatorios(null, ":DATAINICIAL", ":DATAFINAL", OperacaoRelatorio.BETWEEN, DataUtil.getDataFormatada(dataInicial), DataUtil.getDataFormatada(dataFinal), 2, true));
        parametros.add(new ParametrosRelatorios(null, ":DATAINICIAL", ":DATAREFERENCIA", OperacaoRelatorio.BETWEEN, DataUtil.getDataFormatada(dataInicial), DataUtil.getDataFormatada(dataReferencia), 3, true));

        if (pessoa != null) {
            parametros.add(new ParametrosRelatorios(" p.id ", ":fornecedorId", null, OperacaoRelatorio.IGUAL, pessoa.getId(), null, 1, false));
            filtros += " Pessoa: " + pessoa.getCpf_Cnpj() + " - " + pessoa.getNome() + " -";
        }
        if (classeCredor != null) {
            parametros.add(new ParametrosRelatorios(" cc.id ", ":classeId", null, OperacaoRelatorio.IGUAL, classeCredor.getId(), null, 1, false));
            filtros += " Classe de Pessoa: " + classeCredor + " -";
        }
        if (conta != null) {
            parametros.add(new ParametrosRelatorios(" c.codigo ", ":codigo", null, OperacaoRelatorio.LIKE, conta.getCodigoSemZerosAoFinal() + "%", null, 1, false));
            filtros += " Conta de Despesa: " + conta.getCodigoSemZerosAoFinal() + " -";
        }
        if (contaDestinacao != null) {
            parametros.add(new ParametrosRelatorios(" ccd.codigo ", ":contaDestinacao", null, OperacaoRelatorio.LIKE, contaDestinacao.getCodigo(), null, 1, false));
            filtros += " Conta de Destinação: " + contaDestinacao.getCodigo() + " -";
        }
        if (fonteDeRecursos != null) {
            parametros.add(new ParametrosRelatorios(" fonte.codigo ", ":fonteCodigo", null, OperacaoRelatorio.LIKE, fonteDeRecursos.getCodigo(), null, 1, false));
            filtros += " Fonte de Recurso: " + fonteDeRecursos.getCodigo() + " -";
        }
        if (contaBancariaEntidade != null) {
            parametros.add(new ParametrosRelatorios(" CBE.ID ", ":cbeId", null, OperacaoRelatorio.IGUAL, this.contaBancariaEntidade.getId(), null, 4, false));
            filtros += " Conta Bancária: " + contaBancariaEntidade.getNumeroConta() + " - " + contaBancariaEntidade.getDigitoVerificador() + " -";
        }
        if (contaFinanceira != null) {
            parametros.add(new ParametrosRelatorios(" SUB.id ", ":subId", null, OperacaoRelatorio.IGUAL, contaFinanceira.getId(), null, 4, false));
            filtros += " Conta Financeira: " + contaFinanceira.getCodigo() + " -";
        }
        if (numero != null && !numero.isEmpty()) {
            parametros.add(new ParametrosRelatorios(" emp.numero ", ":empNumero", null, OperacaoRelatorio.IGUAL, numero, null, 1, false));
            filtros += " Empenho: " + numero + " -";
        }
        parametros = filtrosParametrosUnidade(parametros);
        if (unidadeGestora != null || getApresentacao().isApresentacaoUnidadeGestora()) {
            parametros.add(new ParametrosRelatorios(null, ":exercicio", null, null, exercicio.getId(), null, 0, false));
        }
        return parametros;
    }

    public Boolean getQuebrarPorEmpenho() {
        return quebrarPorEmpenho;
    }

    public void setQuebrarPorEmpenho(Boolean quebrarPorEmpenho) {
        this.quebrarPorEmpenho = quebrarPorEmpenho;
    }

    public Conta getContaDestinacao() {
        return contaDestinacao;
    }

    public void setContaDestinacao(Conta contaDestinacao) {
        this.contaDestinacao = contaDestinacao;
    }

    @Override
    public String getNomeRelatorio() {
        return "EXTRATO-EMPENHO-DESPESA-RESTOS-A-PAGAR";
    }
}
