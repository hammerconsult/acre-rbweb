package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.ApresentacaoRelatorio;
import br.com.webpublico.enums.OperacaoRelatorio;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.*;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: usuario
 * Date: 18/06/14
 * Time: 10:20
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean(name = "relatorioExtratoContaExtraControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-extrato-conta-extra", pattern = "/relatorio/extrato-conta-extra/", viewId = "/faces/financeiro/relatorio/relatorioextratocontaextra.xhtml")
})
public class RelatorioExtratoContaExtraControlador implements Serializable {

    @EJB
    private FonteDeRecursosFacade fonteDeRecursosFacade;
    @EJB
    private ContaFacade contaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    private FonteDeRecursos fonteDeRecursos;
    private ContaExtraorcamentaria conta;
    private List<HierarquiaOrganizacional> hierarquias;
    private String filtro;
    private ApresentacaoRelatorio apresentacao;
    private UnidadeGestora unidadeGestora;
    private Date dataInicial;
    private Date dataFinal;
    private Conta contaDeDestinacao;

    public List<FonteDeRecursos> completarFontesDeRecursos(String parte) {
        return fonteDeRecursosFacade.listaFiltrandoPorExercicio(parte, buscarExercicioPelaDataFinal());
    }

    public List<ContaExtraorcamentaria> completarContas(String parte) {
        return contaFacade.listaFiltrandoTodasContaExtraorcamentaria(parte, buscarExercicioPelaDataFinal());
    }

    public List<Conta> completarContasDeDestinacao(String parte) {
        return contaFacade.buscarContasDeDestinacaoDeRecursos(parte.trim(), buscarExercicioPelaDataFinal());
    }

    public Exercicio buscarExercicioPelaDataFinal() {
        return exercicioFacade.recuperarExercicioPeloAno(DataUtil.getAno(dataFinal));
    }

    public List<SelectItem> getApresentacoes() {
        return Util.getListSelectItemSemCampoVazio(ApresentacaoRelatorio.values(), false);
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (dataInicial == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data Inicial deve ser informado.");
        }
        if (dataFinal == null) {
            ve.adicionarMensagemDeCampoObrigatorio(" O campo Data Final deve ser informado.");
        }
        ve.lancarException();
        if (dataFinal.compareTo(dataInicial) < 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Informe um Intervalo de datas válido.");
        }
        SimpleDateFormat formato = new SimpleDateFormat("yyyy");
        if (formato.format(dataInicial).compareTo(formato.format(dataFinal)) != 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("As datas estão com exercícios diferentes.");
        }
        ve.lancarException();
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarCampos();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USER", sistemaFacade.getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO");
            dto.adicionarParametro("APRESENTACAO", apresentacao.name());
            dto.adicionarParametro("apresentacaoRelatorioDTO", apresentacao.getToDto());
            dto.adicionarParametro("pesquisouUg", unidadeGestora != null);
            dto.adicionarParametro("dataInicial", DataUtil.getDataFormatada(dataInicial));
            dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(montarParametros()));
            dto.adicionarParametro("FILTRO", filtro);
            dto.setNomeRelatorio("RELATÓRIO-EXTRATO-CONTA-EXTRA");
            dto.setApi("contabil/extrato-conta-extra/");
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private List<ParametrosRelatorios> montarParametros() {
        List<Long> idsUnidades = Lists.newArrayList();
        String unidades = "";
        List<ParametrosRelatorios> parametros = Lists.newArrayList();
        parametros.add(new ParametrosRelatorios(null, ":DATAINICIAL", null, null, DataUtil.getDataFormatada(dataInicial), null, 0, true));
        parametros.add(new ParametrosRelatorios(null, ":DATAFINAL", null, null, DataUtil.getDataFormatada(dataFinal), null, 2, true));
        filtro = " Período: " + DataUtil.getDataFormatada(dataInicial) + " a " + DataUtil.getDataFormatada(dataFinal) + " -";

        if (conta != null) {
            parametros.add(new ParametrosRelatorios(" c.ID ", ":conta", null, OperacaoRelatorio.IGUAL, conta.getId(), null, 1, false));
            filtro += " Conta Extraorçamentária: " + conta.getCodigo() + " -";
        }

        if (fonteDeRecursos != null) {
            parametros.add(new ParametrosRelatorios(" fonte.ID ", ":fornecedor", null, OperacaoRelatorio.IGUAL, fonteDeRecursos.getId(), null, 1, false));
            filtro += " Fonte de Recurso: " + fonteDeRecursos.getCodigo() + " -";
        }
        if (contaDeDestinacao != null) {
            parametros.add(new ParametrosRelatorios(" cdest.ID ", ":cdestId", null, OperacaoRelatorio.IGUAL, contaDeDestinacao.getId(), null, 1, false));
            filtro += " Conta de Destinação de Recurso: " + contaDeDestinacao.getCodigo() + " -";
        }
        if (!hierarquias.isEmpty()) {
            for (HierarquiaOrganizacional hierarquiaOrganizacional : hierarquias) {
                idsUnidades.add(hierarquiaOrganizacional.getSubordinada().getId());
                unidades += " " + hierarquiaOrganizacional.getCodigo().substring(3, 10) + " -";
            }
            filtro += " Unidade(s): " + unidades;
            parametros.add(new ParametrosRelatorios(" vw.subordinada_id ", ":undId", null, OperacaoRelatorio.IN, idsUnidades, null, 1, false));
        } else if (unidadeGestora == null) {
            for (HierarquiaOrganizacional hierarquiaOrganizacional : hierarquiaOrganizacionalFacade.listaHierarquiaUsuarioCorrentePorNivel("", sistemaFacade.getUsuarioCorrente(), buscarExercicioPelaDataFinal(), sistemaFacade.getDataOperacao(), TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), 3)) {
                idsUnidades.add(hierarquiaOrganizacional.getSubordinada().getId());
            }
            parametros.add(new ParametrosRelatorios(" vw.subordinada_id ", ":undId", null, OperacaoRelatorio.IN, idsUnidades, null, 1, false));
        }
        if (unidadeGestora != null) {
            parametros.add(new ParametrosRelatorios(" ug.id ", ":ugId", null, OperacaoRelatorio.IGUAL, unidadeGestora.getId(), null, 1, false));
            filtro += " Unidade Gestora: " + unidadeGestora.getCodigo() + " -";
        }
        parametros.add(new ParametrosRelatorios(" c.exercicio_id ", ":exercicio", null, OperacaoRelatorio.IGUAL, buscarExercicioPelaDataFinal().getId(), null, 1, false));
        filtro = filtro.substring(0, filtro.length() - 1);
        return parametros;
    }

    @URLAction(mappingId = "relatorio-extrato-conta-extra", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        dataInicial = new Date();
        dataFinal = new Date();
        conta = null;
        fonteDeRecursos = null;
        filtro = "";
        hierarquias = Lists.newArrayList();
    }

    public FonteDeRecursos getFonteDeRecursos() {
        return fonteDeRecursos;
    }

    public void setFonteDeRecursos(FonteDeRecursos fonteDeRecursos) {
        this.fonteDeRecursos = fonteDeRecursos;
    }

    public List<HierarquiaOrganizacional> getHierarquias() {
        return hierarquias;
    }

    public void setHierarquias(List<HierarquiaOrganizacional> hierarquias) {
        this.hierarquias = hierarquias;
    }

    public Conta getContaDeDestinacao() {
        return contaDeDestinacao;
    }

    public void setContaDeDestinacao(Conta contaDeDestinacao) {
        this.contaDeDestinacao = contaDeDestinacao;
    }

    public String getFiltro() {
        return filtro;
    }

    public void setFiltro(String filtro) {
        this.filtro = filtro;
    }

    public ApresentacaoRelatorio getApresentacao() {
        return apresentacao;
    }

    public void setApresentacao(ApresentacaoRelatorio apresentacao) {
        this.apresentacao = apresentacao;
    }

    public UnidadeGestora getUnidadeGestora() {
        return unidadeGestora;
    }

    public void setUnidadeGestora(UnidadeGestora unidadeGestora) {
        this.unidadeGestora = unidadeGestora;
    }

    public ContaExtraorcamentaria getConta() {
        return conta;
    }

    public void setConta(ContaExtraorcamentaria conta) {
        this.conta = conta;
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
}
