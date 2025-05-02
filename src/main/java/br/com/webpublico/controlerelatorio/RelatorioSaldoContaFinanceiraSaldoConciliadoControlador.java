package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.UnidadeOrganizacional;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.OperacaoRelatorio;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: usuario
 * Date: 22/07/14
 * Time: 08:03
 * To change this template use File | Settings | File Templates.
 */
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-saldo-conta-financeira-saldo-conciliado", pattern = "/relatorio/saldo-conta-financeira-x-saldo-bancario/", viewId = "/faces/financeiro/relatorio/relatoriosaldocontafinanceirasaldoconciliado.xhtml")
})
@ManagedBean
public class RelatorioSaldoContaFinanceiraSaldoConciliadoControlador extends RelatorioContabilSuperControlador implements Serializable {

    private ConciliadoAConciliar conciliadoAConciliar;

    @URLAction(mappingId = "relatorio-saldo-conta-financeira-saldo-conciliado", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        limparCamposGeral();
        this.conciliadoAConciliar = ConciliadoAConciliar.CONCILIADO;
    }

    public List<SelectItem> getConciliadosAConciliar() {
        List<SelectItem> toReturn = Lists.newArrayList();
        for (ConciliadoAConciliar cac : ConciliadoAConciliar.values()) {
            toReturn.add(new SelectItem(cac, cac.getDescricao()));
        }
        return toReturn;
    }

    public void gerarRelatorio() {
        try {
            validarDataDeReferencia();
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO");
            dto.adicionarParametro("pesquisouUg", unidadeGestora != null);
            dto.adicionarParametro("aConciliar", ConciliadoAConciliar.A_CONCILIAR.equals(conciliadoAConciliar));
            dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(montarParametros()));
            dto.adicionarParametro("mesRefencia", DataUtil.getDataFormatada(dataReferencia).substring(3, 10));
            dto.adicionarParametro("FILTRO", filtros);
            dto.adicionarParametro("NOMERELATORIO", gerarNomeRelatorio());
            dto.adicionarParametro("MODULO", "Financeiro e Contábil");
            dto.adicionarParametro("CONCILIADOACONCILIAR", conciliadoAConciliar.name());
            dto.adicionarParametro("MOSTRAR_SALDOBANCARIO", mostrarSaldoBancario());
            filtros = atualizaFiltrosGerais();
            dto.adicionarParametro("FILTRO_GERAL", filtros.trim());
            dto.setNomeRelatorio(getNomeRelatorio());
            dto.setApi("contabil/saldo-conta-financeira-x-saldo-conciliado/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    @Override
    public String getNomeArquivo() {
        return getNomeRelatorio() + "-" + DataUtil.getAno(dataReferencia) + "-" + StringUtil.preencheString(String.valueOf(DataUtil.getMes(dataReferencia)), 2, '0') + "-" + StringUtil.preencheString(String.valueOf(DataUtil.getDia(dataReferencia)), 2, '0');
    }

    private List<ParametrosRelatorios> montarParametros() {
        List<ParametrosRelatorios> parametros = Lists.newArrayList();
        filtros = "";
        filtrosParametrosGerais(parametros);
        filtrosParametrosUnidade(parametros);
        return parametros;
    }

    public List<ParametrosRelatorios> filtrosParametrosGerais(List<ParametrosRelatorios> parametros) {
        parametros.add(new ParametrosRelatorios(null, ":DATAREFERENCIA", null, null, getDataReferenciaFormatada(), null, 0, true));
        filtros += " Data Referência: " + DataUtil.getDataFormatada(dataReferencia) + " -";

        parametros.add(new ParametrosRelatorios(" subunid.exercicio_id ", ":ID_EXERCICIO", null, OperacaoRelatorio.IGUAL, getExercicioDaDataReferencia().getId(), null, 3, false));

        if (contaBancariaEntidade != null) {
            parametros.add(new ParametrosRelatorios(" cbe.id ", ":IDCONTABANCARIA", null, OperacaoRelatorio.IGUAL, contaBancariaEntidade.getId(), null, 1, false));
            parametros.add(new ParametrosRelatorios(" sd.contabancariaentidade_id ", ":IDCONTABANCARIA", null, OperacaoRelatorio.IGUAL, contaBancariaEntidade.getId(), null, 2, false));
            filtros += " Conta Bancária: " + contaBancariaEntidade.getNumeroContaComDigitoVerificador() + " -";
        }
        if (contaFinanceira != null) {
            parametros.add(new ParametrosRelatorios(" sub.id ", ":IDCONTAFINANCEIRA", null, OperacaoRelatorio.IGUAL, contaFinanceira.getId(), null, 1, false));
            filtros += " Conta Financeira: " + contaFinanceira.getCodigo() + " -";
        }
        filtros = atualizaFiltrosGerais();
        return parametros;
    }

    private String gerarNomeRelatorio() {
        if (ConciliadoAConciliar.A_CONCILIAR.equals(conciliadoAConciliar)) {
            return "Relatório Saldo de Conta Financeira X Saldo Bancário - A Conciliar";
        } else {
            return "Relatório Saldo de Conta Financeira X Saldo Bancário - Conciliado";
        }

    }

    public Exercicio getExercicioDaDataReferencia() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy");
        return getExercicioFacade().getExercicioPorAno(new Integer(format.format(dataReferencia)));
    }

    private Boolean mostrarSaldoBancario() {
        List<UnidadeOrganizacional> unidadesUsuario = recuperarUnidadesUsuario();
        List<UnidadeOrganizacional> unidadesContaBancaria = buscarUnidadesDaContaBancaria();
        return unidadesUsuario.containsAll(unidadesContaBancaria);
    }

    @Override
    public String getNomeRelatorio() {
        if (conciliadoAConciliar.equals(ConciliadoAConciliar.A_CONCILIAR)) {
            return "RELATORIO-SALDO-CONTAFINANCEIRA-X-SALDOBANCARIO-CONCILIAR";
        } else {
            return "RELATORIO-SALDO-CONTAFINANCEIRA-X-SALDOBANCARIO-CONCILIADO";
        }
    }

    public void atualizarCampos() {
        contaFinanceira = null;
        contaBancariaEntidade = null;
        listaUnidades = new ArrayList<>();
        unidadeGestora = null;
        this.conciliadoAConciliar = ConciliadoAConciliar.CONCILIADO;
    }

    private enum ConciliadoAConciliar {

        CONCILIADO("Conciliado"),
        A_CONCILIAR("A Conciliar");
        private String descricao;

        ConciliadoAConciliar(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    public ConciliadoAConciliar getConciliadoAConciliar() {
        return conciliadoAConciliar;
    }

    public void setConciliadoAConciliar(ConciliadoAConciliar conciliadoAConciliar) {
        this.conciliadoAConciliar = conciliadoAConciliar;
    }
}
