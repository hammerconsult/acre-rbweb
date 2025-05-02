/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.RelatoriosItemDemonst;
import br.com.webpublico.entidadesauxiliares.ItemDemonstrativoComponente;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.OperacaoRelatorio;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.enums.TipoRelatorioItemDemonstrativo;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.RelatoriosItemDemonstFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

/**
 * @author juggernaut
 */
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-demonstrativo-divida-ativa", pattern = "/relatorio/demonstrativo-divida-ativa/", viewId = "/faces/financeiro/relatorio/relatoriodemonstrativodividaativa.xhtml")
})
@ManagedBean
public class RelatorioDemonstrativoDividaAtivaContControlador extends RelatorioContabilSuperControlador implements Serializable {

    @EJB
    private RelatoriosItemDemonstFacade relatoriosItemDemonstFacade;
    private List<ItemDemonstrativoComponente> itens;
    private RelatoriosItemDemonst relatoriosItemDemonst;

    public RelatorioDemonstrativoDividaAtivaContControlador() {
        limparItens();
    }

    @URLAction(mappingId = "relatorio-demonstrativo-divida-ativa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        super.limparCamposGeral();
        dataReferencia = null;
    }

    public void limparItens() {
        itens = Lists.newArrayList();
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        verificarCampoNull(ve, dataInicial, "O campo Data Inicial deve ser informado.");
        verificarCampoNull(ve, dataFinal, "O campo Data Final deve ser informado.");
        ve.lancarException();
        if (dataFinal.before(dataInicial)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Data Final deve ser maior que a Data Inicial.");
        }
        verificarDataEmExercicioDiferente(ve, dataInicial, dataFinal, " As datas estão com exercícios diferentes.");
        ve.lancarException();
    }

    public void gerarRelatorio() {
        try {
            validarCampos();
            RelatorioDTO dto = montarRelatorioDto();
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

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarCampos();
            RelatorioDTO dto = montarRelatorioDto();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
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

    private RelatorioDTO montarRelatorioDto() {
        relatoriosItemDemonst = relatoriosItemDemonstFacade.recuperaRelatorioPorTipoEDescricao("Demonstrativo de Dívida Ativa", buscarExercicioPelaDataFinal(), TipoRelatorioItemDemonstrativo.OUTROS);
        RelatorioDTO dto = new RelatorioDTO();
        dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getNome());
        dto.setNomeParametroBrasao("IMAGEM");
        dto.adicionarParametro("MUNICIPIO", "Município de Rio Branco - AC");
        dto.adicionarParametro("itens", ItemDemonstrativoComponente.itensDemonstrativosComponentesToDto(itens));
        dto.adicionarParametro("usaConta", relatoriosItemDemonst.getUsaConta());
        dto.adicionarParametro("pesquisouUg", unidadeGestora != null);
        dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(montarParametrosEFiltros()));
        dto.adicionarParametro("dataInicial", DataUtil.getDataFormatada(dataInicial));
        dto.adicionarParametro("FILTRO", filtros);
        dto.setNomeRelatorio(getNomeRelatorio());
        dto.setApi("contabil/demonstrativo-divida-ativa/");
        return dto;
    }

    private List<ParametrosRelatorios> montarParametrosEFiltros() {
        filtros = " Período: " + DataUtil.getDataFormatada(dataInicial) + " a " + DataUtil.getDataFormatada(dataFinal) + " -";
        List<ParametrosRelatorios> parametros = Lists.newArrayList();
        parametros.add(new ParametrosRelatorios(null, ":DATAINICIAL", null, null, DataUtil.getDataFormatada(dataInicial), null, 4, true));
        parametros.add(new ParametrosRelatorios(null, ":DATAFINAL", null, null, DataUtil.getDataFormatada(dataFinal), null, 5, true));
        List<Long> idsUnidades = Lists.newArrayList();
        if (this.listaUnidades.size() > 0) {
            String unidades = "";
            for (HierarquiaOrganizacional lista : listaUnidades) {
                idsUnidades.add(lista.getSubordinada().getId());
                unidades += " " + lista.getCodigo().substring(3, 10) + " -";
            }
            filtros += " Unidade(s): " + unidades;
            parametros.add(new ParametrosRelatorios(" vw.subordinada_id", ":undId", null, OperacaoRelatorio.IN, idsUnidades, null, 1, false));
        } else if (this.unidadeGestora == null) {
            List<HierarquiaOrganizacional> unidadesDoUsuario = relatoriosItemDemonstFacade.getHierarquiaOrganizacionalFacade().listaHierarquiaUsuarioCorrentePorNivel("", getSistemaControlador().getUsuarioCorrente(), buscarExercicioPelaDataFinal(), dataFinal, TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), 3);
            for (HierarquiaOrganizacional hiearquias : unidadesDoUsuario) {
                idsUnidades.add(hiearquias.getSubordinada().getId());
            }
            if (idsUnidades.size() != 0) {
                parametros.add(new ParametrosRelatorios(" vw.subordinada_id ", ":undId", null, OperacaoRelatorio.IN, idsUnidades, null, 1, false));
            }
        }
        if (this.unidadeGestora != null) {
            parametros.add(new ParametrosRelatorios(" ug.id ", ":ugId", null, OperacaoRelatorio.IGUAL, this.unidadeGestora.getId(), null, 2, false));
            filtros += " Unidade Gestora: " + this.unidadeGestora.getCodigo() + " -";
        }
        parametros.add(new ParametrosRelatorios(null, ":exercicio", null, null, buscarExercicioPelaDataFinal().getId(), null, 0, false));
        atualizaFiltrosGerais();
        return parametros;
    }

    @Override
    public Exercicio buscarExercicioPelaDataFinal() {
        if (dataFinal == null) {
            return getSistemaControlador().getExercicioCorrente();
        }
        return getExercicioFacade().getExercicioPorAno(DataUtil.getAno(dataFinal));
    }

    @Override
    public String getNomeRelatorio() {
        return "demonstrativo-divida-ativa";
    }

    public List<ItemDemonstrativoComponente> getItens() {
        return itens;
    }

    public void setItens(List<ItemDemonstrativoComponente> itens) {
        this.itens = itens;
    }

}
