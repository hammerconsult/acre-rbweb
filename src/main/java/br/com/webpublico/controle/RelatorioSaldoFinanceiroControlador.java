/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.RelatorioContabilSuperControlador;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.ApresentacaoRelatorio;
import br.com.webpublico.enums.OperacaoRelatorio;
import br.com.webpublico.enums.TipoExibicao;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.ContaBancariaEntidadeFacade;
import br.com.webpublico.negocios.ContaFacade;
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
import java.util.ArrayList;
import java.util.List;

@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-relacao-disponibilidade-caixa", pattern = "/relatorios/relacao-disponibilidade-caixa/", viewId = "/faces/financeiro/relatorio/relatoriosaldofinanceiro.xhtml")
})
@ManagedBean
public class RelatorioSaldoFinanceiroControlador extends RelatorioContabilSuperControlador implements Serializable {

    @EJB
    private ContaBancariaEntidadeFacade contaBancariaEntidadeFacade;
    @EJB
    private ContaFacade contaFacade;
    private TipoExibicao tipoExibicao;
    private Conta contaDeDestinacao;

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarCampos();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("MUNICIPIO", getDescricaoSegundoEsferaDoPoder());
            dto.adicionarParametro("ANO_EXERCICIO", getSistemaFacade().getExercicioCorrente().getAno().toString());
            dto.adicionarParametro("EXERCICIO", getSistemaFacade().getExercicioCorrente().getId());
            dto.adicionarParametro("APRESENTACAO", apresentacao.name());
            dto.adicionarParametro("NOMERELATORIO", getNomeRelatorio());
            dto.adicionarParametro("DATAREFERENCIA", DataUtil.getDataFormatada(dataReferencia));
            dto.adicionarParametro("MODULO", "Financeiro e Contábil");
            dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(montarParametrosEFiltros()));
            dto.adicionarParametro("pesquisouUg", unidadeGestora != null);
            dto.adicionarParametro("apresentacao", apresentacao.getToDto());
            dto.adicionarParametro("TIPOEXIBICAO", tipoExibicao.getToDto());
            dto.adicionarParametro("FILTRO", filtros);
            dto.setNomeRelatorio(getNomeRelatorioDownload());
            dto.setApi("contabil/relacao-disponibilidade-caixa/");
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

    private List<ParametrosRelatorios> montarParametrosEFiltros() {
        filtros = "";
        List<ParametrosRelatorios> parametros = new ArrayList<>();
        filtros += " Data de Referência: " + DataUtil.getDataFormatada(dataReferencia) + " -";
        parametros.add(new ParametrosRelatorios(null, ":dataReferencia", null, OperacaoRelatorio.MENOR_IGUAL, DataUtil.getDataFormatada(dataReferencia), null, 0, true));
        adicionarExercicio(parametros);
        filtros += " Exibir: " + tipoExibicao.getDescricao() + " -";
        if (contaBancariaEntidade != null) {
            parametros.add(new ParametrosRelatorios(" cbe.id ", ":cbe", null, OperacaoRelatorio.IGUAL, contaBancariaEntidade.getId(), null, 1, false));
            filtros += " Conta Bancária: " + contaBancariaEntidade.getNumeroConta() + " - " + contaBancariaEntidade.getDigitoVerificador() + " -";
        }
        if (contaFinanceira != null) {
            parametros.add(new ParametrosRelatorios(" sb.id ", ":sb", null, OperacaoRelatorio.IGUAL, contaFinanceira.getId(), null, 1, false));
            filtros += " Conta Financeira: " + contaFinanceira.getCodigo() + " -";
        }
        if (fonteDeRecursos != null) {
            parametros.add(new ParametrosRelatorios(" font.id ", ":font", null, OperacaoRelatorio.IGUAL, fonteDeRecursos.getId(), null, 1, false));
            filtros += " Fonte de Recursos: " + fonteDeRecursos.getCodigo() + " -";
        }
        if (contaDeDestinacao != null) {
            parametros.add(new ParametrosRelatorios(" cd.ID ", ":idContaDeDestinacao", null, OperacaoRelatorio.IGUAL, contaDeDestinacao.getId(), null, 1, false));
            filtros += " Conta de Destinação de Recurso: " + contaDeDestinacao.getCodigo() + " -";
        }
        filtrosParametrosUnidade(parametros);
        if (ApresentacaoRelatorio.UNIDADE_GESTORA.equals(apresentacao)) {
            adicionarExercicio(parametros);
        }
        filtros = atualizaFiltrosGerais();
        return parametros;
    }

    @Override
    public void adicionarExercicio(List<ParametrosRelatorios> parametros) {
        parametros.add(new ParametrosRelatorios(null, ":exercicio", null, null, buscarExercicioPelaDataReferencia().getId(), null, 0, false));
    }

    private String getNomeRelatorioDownload() {
        String nomeArquivo = "DCB-" + DataUtil.getDataFormatada(dataReferencia, "yyyy-MM");
        switch (apresentacao) {
            case UNIDADE_GESTORA:
                return nomeArquivo + "-UNG";

            case UNIDADE:
                return nomeArquivo + "-UND";

            case ORGAO:
                return nomeArquivo + "-ORG";
        }
        return nomeArquivo + "-CSL";
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (dataReferencia == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Favor informar uma Data Referência.");
        }
        ve.lancarException();
    }

    @URLAction(mappingId = "relatorio-relacao-disponibilidade-caixa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        limparCamposGeral();
        apresentacao = ApresentacaoRelatorio.CONSOLIDADO;
        tipoExibicao = TipoExibicao.CONTA_DE_DESTINACAO;
    }

    public void limparCamposSemData() {
        contaBancariaEntidade = null;
        contaFinanceira = null;
        fonteDeRecursos = null;
        listaUnidades = Lists.newArrayList();
    }

    public List<SelectItem> getTiposDeExibicao() {
        return Util.getListSelectItemSemCampoVazio(TipoExibicao.values(), false);
    }

    public List<ContaBancariaEntidade> completarContaBancariaEntidade(String parte) {
        try {
            return contaBancariaEntidadeFacade.listaFiltrandoPorUnidadeDoUsuario(parte.trim(),
                getSistemaFacade().getUnidadeOrganizacionalOrcamentoCorrente(), buscarExercicioPelaDataReferencia(), getSistemaFacade().getUsuarioCorrente(), dataReferencia);
        } catch (Exception ex) {
            return new ArrayList<>();
        }
    }

    public List<SubConta> completarSubContas(String parte) {
        if (contaBancariaEntidade != null) {
            if (contaBancariaEntidade.getId() != null) {
                return contaBancariaEntidadeFacade.getSubContaFacade().listaPorContaBancariaEntidadeDoUsuarioLogadoOuTodas(parte.trim(),
                    contaBancariaEntidade, getSistemaFacade().getUnidadeOrganizacionalOrcamentoCorrente(), buscarExercicioPelaDataReferencia(), getSistemaFacade().getUsuarioCorrente(), dataReferencia);
            }
        }
        return contaBancariaEntidadeFacade.getSubContaFacade().listaTodas(parte.trim());
    }

    public List<FonteDeRecursos> completarFontesDeRecursos(String parte) {
        if (contaFinanceira != null) {
            return contaBancariaEntidadeFacade.getFonteDeRecursosFacade().listaFiltrandoPorContaFinanceiraExercicio(parte.trim(), contaFinanceira, buscarExercicioPelaDataReferencia());
        }
        return contaBancariaEntidadeFacade.getFonteDeRecursosFacade().listaFiltrandoPorExercicio(parte.trim(), buscarExercicioPelaDataReferencia());
    }

    public List<Conta> completarContasDeDestinacao(String parte) {
        List<Conta> retorno = Lists.newArrayList();
        if (contaFinanceira != null) {
            retorno.addAll(contaFacade.buscarContasDeDestinacaoPorContaFinanceirAndExercicio(parte.trim(), contaFinanceira, buscarExercicioPelaDataReferencia()));
        } else {
            retorno.addAll(contaFacade.buscarContasDeDestinacaoDeRecursos(parte.trim(), buscarExercicioPelaDataReferencia()));
        }
        return retorno;
    }

    public void recuperaContaBancariaApartirDaContaFinanceira() {
        fonteDeRecursos = null;
        contaDeDestinacao = null;
        contaBancariaEntidade = contaFinanceira != null ? contaFinanceira.getContaBancariaEntidade() : null;
    }

    public void atribuirIdNullContaBancaria() {
        contaBancariaEntidade = null;
    }

    public void atribuirNullContaFinanceira() {
        contaFinanceira = null;
        fonteDeRecursos = null;
    }

    public Exercicio buscarExercicioPelaDataReferencia() {
        return getExercicioFacade().recuperarExercicioPeloAno(DataUtil.getAno(dataReferencia));
    }

    @Override
    public String getNomeRelatorio() {
        return "Relação de Disponibilidade de Caixa Bruta";
    }

    public void limparContaFonte() {
        contaFinanceira = null;
        fonteDeRecursos = null;
        contaDeDestinacao = null;
    }

    public TipoExibicao getTipoExibicao() {
        return tipoExibicao;
    }

    public void setTipoExibicao(TipoExibicao tipoExibicao) {
        this.tipoExibicao = tipoExibicao;
    }

    public Conta getContaDeDestinacao() {
        return contaDeDestinacao;
    }

    public void setContaDeDestinacao(Conta contaDeDestinacao) {
        this.contaDeDestinacao = contaDeDestinacao;
    }
}
