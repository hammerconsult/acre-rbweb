package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.FiltroRelatorioAdministrativo;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.ContratoFacade;
import br.com.webpublico.negocios.EmpenhoFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: usuario
 * Date: 25/07/14
 * Time: 16:38
 * To change this template use File | Settings | File Templates.
 */
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-acompanhamento-contrato", pattern = "/relatorio/acompanhamento-execucao-contrato/", viewId = "/faces/financeiro/relatorio/relatorioacompanhamentocontrato.xhtml")
})
@ManagedBean(name = "relatorioAcompanhamentoContratoControlador")
public class RelatorioAcompanhamentoContratoControlador implements Serializable {

    @EJB
    private EmpenhoFacade empenhoFacade;
    @EJB
    private ContratoFacade contratoFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    private FiltroRelatorioAdministrativo filtroRelatorio;

    public List<Empenho> completarEmpenho(String parte) {
        return empenhoFacade.buscarEmpenhoPorUnidades(parte, filtroRelatorio.getUnidades());
    }

    public List<Pessoa> completarFornecedor(String parte) {
        return empenhoFacade.getPessoaFacade().listaTodasPessoasPorId(parte.trim());
    }

    public List<Contrato> completarContrato(String parte) {
        return contratoFacade.buscarContratoPorNumeroOrExercicio(parte.trim());
    }

    @URLAction(mappingId = "relatorio-acompanhamento-contrato", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        filtroRelatorio = new FiltroRelatorioAdministrativo();
    }

    private void validarFiltros() {
        ValidacaoException ve = new ValidacaoException();
        if (filtroRelatorio.getDataInicial() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo data inicial deve ser informado.");
        }
        if (filtroRelatorio.getDataFinal() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo data final deve ser informado.");
        }
        if (filtroRelatorio.getDataReferencia() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo data de referência deve ser informado.");
        }
        ve.lancarException();
        if (filtroRelatorio.getDataInicial().after(filtroRelatorio.getDataFinal())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo data inicial não pode ser maior que a data final.");
        }
        SimpleDateFormat formato = new SimpleDateFormat("yyyy");
        if (formato.format(filtroRelatorio.getDataInicial()).compareTo(formato.format(filtroRelatorio.getDataFinal())) != 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("As datas estão com exercícios diferentes.");
        }
        ve.lancarException();
    }

    public void gerarRelatorio() {
        try {
            validarFiltros();
            Exercicio e = empenhoFacade.getExercicioFacade().listaFiltrandoEspecial(DataUtil.getDataFormatada(filtroRelatorio.getDataInicial(), "yyyy")).get(0);
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("MUNICIPIO", "Município de Rio Branco - AC ");
            dto.adicionarParametro("USER", sistemaFacade.getUsuarioCorrente().getNome(), false);
            dto.adicionarParametro("ANO_EXERCICIO", e.getAno().toString());
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("QUEBRA_EMPENHO", filtroRelatorio.getQuebraPagina());
            dto.adicionarParametro("condicao", montarCondicao());
            dto.adicionarParametro("FILTRO", filtroRelatorio.getFiltros());
            dto.adicionarParametro("dataInicial", filtroRelatorio.getDataInicial().getTime());
            dto.adicionarParametro("dataFinal", filtroRelatorio.getDataFinal().getTime());
            dto.adicionarParametro("dataReferencia", filtroRelatorio.getDataReferencia().getTime());
            dto.setNomeRelatorio("ACOMPANHAMENTO-DA-EXECUCAO-DE-CONTRATO");
            dto.setApi("administrativo/acompanhamento-execucao-contrato/");
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private String montarCondicao() {
        String filtro = "";
        filtro += " Período: " + DataUtil.getDataFormatada(filtroRelatorio.getDataInicial()) + " a " + DataUtil.getDataFormatada(filtroRelatorio.getDataFinal()) + " -";
        filtro += " Data de Referência: " + DataUtil.getDataFormatada(filtroRelatorio.getDataReferencia()) + " -";
        StringBuilder stb = new StringBuilder();
        if (filtroRelatorio.getContrato() != null) {
            filtro += " Contrato: " + filtroRelatorio.getContrato();
            stb.append(" and cont.id = ").append(filtroRelatorio.getContrato().getId());
        }
        if (filtroRelatorio.getPessoa() != null) {
            filtro += " Pessoa: " + filtroRelatorio.getPessoa();
            stb.append(" and p.id = ").append(filtroRelatorio.getPessoa().getId());
        }
        if (!filtroRelatorio.getUnidades().isEmpty()) {
            StringBuilder idUnidades = new StringBuilder();
            String concat = "";
            StringBuilder unds = new StringBuilder();
            for (HierarquiaOrganizacional hierarquiaOrganizacional : filtroRelatorio.getUnidades()) {
                idUnidades.append(concat).append(hierarquiaOrganizacional.getSubordinada().getId());
                unds.append(concat).append(hierarquiaOrganizacional.getCodigo());
                concat = ", ";
            }
            filtro += " Unidade(s): " + unds.toString() + " -";
            stb.append(" and VW.SUBORDINADA_ID IN (").append(idUnidades).append(")");
        } else {
            List<HierarquiaOrganizacional> unidadesDoUsuario = empenhoFacade.getHierarquiaOrganizacionalFacade().listaHierarquiaUsuarioCorrentePorNivel("",
                sistemaFacade.getUsuarioCorrente(),
                sistemaFacade.getExercicioCorrente(),
                sistemaFacade.getDataOperacao(),
                TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), 3);

            StringBuilder idUnidades = new StringBuilder();
            String concat = "";
            for (HierarquiaOrganizacional hierarquiaOrganizacional : unidadesDoUsuario) {
                idUnidades.append(concat).append(hierarquiaOrganizacional.getSubordinada().getId());
                concat = ", ";
            }
            stb.append(" and VW.SUBORDINADA_ID IN (").append(idUnidades).append(")");
        }
        if (filtroRelatorio.getEmpenho() != null) {
            stb.append(" AND exists (select 1 from empenho emp where EMP.ID = ").append(filtroRelatorio.getEmpenho().getId()).append(" and emp.contrato_id = cont.id) ");
            filtro += " Empenho: " + filtroRelatorio.getEmpenho().getNumero() + " -";
        }
        filtro = filtro.substring(0, filtro.length() - 1);
        filtroRelatorio.setFiltros(filtro);
        return stb.toString();
    }

    public FiltroRelatorioAdministrativo getFiltroRelatorio() {
        return filtroRelatorio;
    }

    public void setFiltroRelatorio(FiltroRelatorioAdministrativo filtroRelatorio) {
        this.filtroRelatorio = filtroRelatorio;
    }
}
