package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.SubConta;
import br.com.webpublico.entidades.SubContaUniOrg;
import br.com.webpublico.entidades.UnidadeOrganizacional;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.OperacaoRelatorio;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.relatoriofacade.AbstractRelatorioConciliacaoBancariaFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by mateus on 04/08/17.
 */
public abstract class AbstractRelatorioConciliacaoBancariaControlador extends RelatorioContabilSuperControlador {

    protected Boolean diferenteZero;

    public abstract AbstractRelatorioConciliacaoBancariaFacade getRelatorioFacade();

    public abstract TipoRelatorio getTipoRelatorio();

    public void gerarRelatorio() {
        try {
            validarDataDeReferencia();
            validarCampos();
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("USER", getNomeUsuarioLogado(), false);
            dto.adicionarParametro("MUNICIPIO", getDescricaoSegundoEsferaDoPoder());
            dto.setNomeParametroSubreportDir("SUBREPORT_DIR");
            dto.adicionarParametro("ANO_EXERCICIO", getSistemaControlador().getExercicioCorrente().getAno().toString());
            dto.adicionarParametro("EXERCICIO", getSistemaControlador().getExercicioCorrente().getId());
            dto.adicionarParametro("pesquisouUg", unidadeGestora != null);
            dto.adicionarParametro("diferenteZero", diferenteZero);
            dto.adicionarParametro("parametroSaldoAnterior", getParametroDataSaldoAnterior());
            dto.adicionarParametro("dataReferencia", dataReferencia);
            dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(montarParametros()));
            dto.adicionarParametro("FILTRO_DATAREFERENCIA", getDataReferenciaFormatada());
            dto.adicionarParametro("unidadesUsuario", buscarIdsDeUnidades(recuperarUnidadesUsuario()));
            dto.adicionarParametro("unidadesContaBancaria", buscarIdsDeUnidades(buscarUnidadesDaContaBancaria()));
            dto.adicionarParametro("contaBancariaEntidade", contaBancariaEntidade.getId());
            dto.adicionarParametro("mapSubContas", buscarContasFinanceirasEUnidadesDaContaBancaria());
            dto.setNomeRelatorio(getNomeRelatorio());
            dto.setApi(getTipoRelatorio().getApi());
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    private List<Long> buscarIdsDeUnidades(List<UnidadeOrganizacional> unidadeOrganizacionais) {
        List<Long> idsUnidades = Lists.newArrayList();
        for (UnidadeOrganizacional unidadeOrganizacional : unidadeOrganizacionais) {
            idsUnidades.add(unidadeOrganizacional.getId());
        }
        return idsUnidades;
    }

    public void limparCamposRelatorio() {
        contaBancariaEntidade = null;
        contaFinanceira = null;
        listaUnidades = new ArrayList<>();
        unidadeGestora = null;
        diferenteZero = false;
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (contaBancariaEntidade == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Conta Bancária deve ser informado.");
        }
        ve.lancarException();
    }

    private HashMap buscarContasFinanceirasEUnidadesDaContaBancaria() {
        contaBancariaEntidade = getRelatorioFacade().getContaBancariaEntidadeFacade().recuperar(contaBancariaEntidade.getId());
        HashMap<Long, Long> idsSubContaAndUnidade = new HashMap<>();
        for (SubConta subConta : contaBancariaEntidade.getSubContas()) {
            UnidadeOrganizacional unidadeOrganizacional = getUnidadeDoExercicioSubConta(subConta);
            if (unidadeOrganizacional != null) {
                idsSubContaAndUnidade.put(subConta.getId(), unidadeOrganizacional.getId());
            }
        }
        return idsSubContaAndUnidade;
    }

    private UnidadeOrganizacional getUnidadeDoExercicioSubConta(SubConta subConta) {
        for (SubContaUniOrg subContaUniOrg : subConta.getUnidadesOrganizacionais()) {
            if (getSistemaControlador().getExercicioCorrente().equals(subContaUniOrg.getExercicio())) {
                return subContaUniOrg.getUnidadeOrganizacional();
            }
        }
        return null;
    }

    private String getParametroDataSaldoAnterior() {
        return " where trunc(datasaldo) <= TO_DATE('" + DataUtil.getDataFormatada(dataReferencia) + "', 'dd/MM/yyyy') ";
    }

    public List<ParametrosRelatorios> montarParametros() {
        List<ParametrosRelatorios> parametros = new ArrayList<>();
        filtros = "";
        parametrosParaConsulta(parametros);
        filtrosParametrosUnidade(parametros);
        montarFiltroSql(parametros);
        return parametros;
    }

    protected void parametrosParaConsulta(List<ParametrosRelatorios> parametros) {
        parametros.add(new ParametrosRelatorios(null, ":DATAREFERENCIA", null, OperacaoRelatorio.IGUAL, getDataReferenciaFormatada(), null, 0, false));
        parametros.add(new ParametrosRelatorios(null, ":EXERCICIO_ID", null, null, getExercicioDaDataReferencia().getId(), null, 0, false));
        parametros.add(new ParametrosRelatorios(null, ":CBE_ID", null, OperacaoRelatorio.IGUAL, contaBancariaEntidade.getId(), null, 0, false));
    }

    protected void montarFiltroSql(List<ParametrosRelatorios> parametros) {
        if (!diferenteZero) {
            parametros.add(new ParametrosRelatorios(" ssb.totaldebito - ssb.totalcredito ", ":soma", null, OperacaoRelatorio.DIFERENTE, "0", null, 3, false));
            parametros.add(new ParametrosRelatorios(" DEBITO - CREDITO ", ":soma", null, OperacaoRelatorio.DIFERENTE, "0", null, 4, false));
        }
    }

    public Exercicio getExercicioDaDataReferencia() {
        return getExercicioFacade().recuperarExercicioPeloAno(DataUtil.getAno(dataReferencia));
    }

    public String toStringConciliacaoBancaria() {
        return contaBancariaEntidade.getAgencia().getBanco().getNumeroBanco() + "-" + contaBancariaEntidade.getAgencia().getNumeroAgencia() + "-" + contaBancariaEntidade.getAgencia().getDigitoVerificador() + "-" + Util.zerosAEsquerda(contaBancariaEntidade.getNumeroConta(), 9) + "-" + contaBancariaEntidade.getDigitoVerificador();
    }

    @Override
    public void adicionarExercicio(List<ParametrosRelatorios> parametros) {

    }

    public Boolean getDiferenteZero() {
        return diferenteZero;
    }

    public void setDiferenteZero(Boolean diferenteZero) {
        this.diferenteZero = diferenteZero;
    }

    public enum TipoRelatorio {
        CONCILIACAO_BANCARIA("contabil/conciliacao-bancaria/"),
        CONCILIACAO_BANCARIA_POR_IDENTIFICADOR("contabil/conciliacao-bancaria/identificador/");

        private String api;

        TipoRelatorio(String api) {
            this.api = api;
        }

        public String getApi() {
            return api;
        }
    }
}
