/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.ApresentacaoRelatorio;
import br.com.webpublico.enums.OperacaoRelatorio;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author juggernaut
 */
@ManagedBean(name = "relatorioEstornoPagamentoExtra")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-estorno-pagamento-extra", pattern = "/relatorio/estorno-pagamento-extra/", viewId = "/faces/financeiro/relatorio/relatorioestornopagamentoextra.xhtml")
})
public class RelatorioEstornoPagamentoExtra extends RelatorioContabilSuperControlador implements Serializable {

    private ContaExtraorcamentaria contaExtraorcamentaria;

    public List<ContaExtraorcamentaria> completaConta(String parte) {
        return relatorioContabilSuperFacade.getContaFacade().listaFiltrandoExtraorcamentario(parte.trim(), getSistemaFacade().getExercicioCorrente());
    }

    public void gerarRelatorio(String tipoExtensaoRelatorio) {
        try {
            validarDatasSemVerificarExercicioLogado();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoExtensaoRelatorio));
            dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(montarParametros()));
            dto.adicionarParametro("FILTRO", filtros);
            dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC");
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("apresentacao", apresentacao.getToDto());
            dto.adicionarParametro("APRESENTACAO", apresentacao.name());
            dto.adicionarParametro("pesquisouUg", unidadeGestora != null);
            dto.setNomeRelatorio(getNomeRelatorio());
            dto.setApi("contabil/estorno-pagamento-extra/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, " ", "Ocorreu um erro ao gerar o relatório: " + ex.getMessage()));
        }
    }

    private List<ParametrosRelatorios> montarParametros() {
        List<ParametrosRelatorios> parametros = new ArrayList<>();
        parametros.add(new ParametrosRelatorios(" trunc(PEE.DATAESTORNO) ", ":DataInicial", ":DataFinal", OperacaoRelatorio.BETWEEN, DataUtil.getDataFormatada(dataInicial), DataUtil.getDataFormatada(dataFinal), 1, true));
        filtros = " Período: " + DataUtil.getDataFormatada(dataInicial) + " a " + DataUtil.getDataFormatada(dataFinal) + " -";

        if (numero != null && !numero.isEmpty()) {
            parametros.add(new ParametrosRelatorios(" pee.numero ", ":numero", null, OperacaoRelatorio.IGUAL, numero, null, 1, true));
            filtros += " Número: " + numero + " -";
        }
        if (pessoa != null && pessoa.getId() != null) {
            parametros.add(new ParametrosRelatorios(" pe.fornecedor_id ", ":fornecedor", null, OperacaoRelatorio.IGUAL, pessoa.getId(), null, 1, true));
            filtros += " Fornecedor: " + pessoa.getNome() + " -";
        }
        if (fonteDeRecursos != null && fonteDeRecursos.getId() != null) {
            parametros.add(new ParametrosRelatorios(" pe.FONTEDERECURSOS_ID ", ":fornecedor", null, OperacaoRelatorio.IGUAL, fonteDeRecursos.getId(), null, 1, true));
            filtros += " Fonte de Recurso: " + fonteDeRecursos.getCodigo() + " -";
        }
        if (this.contaExtraorcamentaria != null) {
            parametros.add(new ParametrosRelatorios(" C.CODIGO ", ":contaCodigo", null, OperacaoRelatorio.LIKE, contaExtraorcamentaria.getCodigo(), null, 1, false));
            filtros += " Conta Extraorçamentária " + contaExtraorcamentaria.getCodigo() + " -";
        }
        if (!listaUnidades.isEmpty()) {
            List<Long> listaIdsUnidades = new ArrayList<>();
            String unidades = "";
            for (HierarquiaOrganizacional lista : listaUnidades) {
                listaIdsUnidades.add(lista.getSubordinada().getId());
                unidades += " " + lista.getCodigo().substring(3, 10) + " -";
            }
            filtros += " Unidade(s): " + unidades;
            parametros.add(new ParametrosRelatorios(" vw.subordinada_id ", ":undId", null, OperacaoRelatorio.IN, listaIdsUnidades, null, 1, false));
        } else if (unidadeGestora == null) {
            List<HierarquiaOrganizacional> listaUndsUsuarios = new ArrayList<>();
            List<Long> listaIdsUnidades = new ArrayList<>();
            listaUndsUsuarios = relatorioContabilSuperFacade.getHierarquiaOrganizacionalFacade().listaHierarquiaUsuarioCorrentePorNivel("", getSistemaFacade().getUsuarioCorrente(), getSistemaFacade().getExercicioCorrente(), getSistemaFacade().getDataOperacao(), TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), 3);
            for (HierarquiaOrganizacional lista : listaUndsUsuarios) {
                listaIdsUnidades.add(lista.getSubordinada().getId());
            }
            if (listaIdsUnidades.size() != 0) {
                parametros.add(new ParametrosRelatorios(" vw.subordinada_id ", ":undId", null, OperacaoRelatorio.IN, listaIdsUnidades, null, 1, false));
            }
        }
        if (this.unidadeGestora != null) {
            parametros.add(new ParametrosRelatorios(" ug.id ", ":ugId", null, OperacaoRelatorio.IGUAL, this.unidadeGestora.getId(), null, 1, false));
            filtros += " Unidade Gestora: " + this.unidadeGestora.getCodigo() + " -";
        }

        if (this.unidadeGestora != null || apresentacao.equals(ApresentacaoRelatorio.UNIDADE_GESTORA)) {
            SimpleDateFormat formato = new SimpleDateFormat("yyyy");
            Exercicio exercicio = getExercicioFacade().recuperarExercicioPeloAno(new Integer(formato.format(dataInicial)));
            parametros.add(new ParametrosRelatorios(null, ":exercicio", null, null, exercicio.getId(), null, 0, false));
        }
        filtros = filtros.substring(0, filtros.length() - 1);
        return parametros;
    }

    @URLAction(mappingId = "relatorio-estorno-pagamento-extra", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        limparCamposGeral();
    }

    public List<Pessoa> completaPessoa(String parte) {
        return relatorioContabilSuperFacade.getPessoaFacade().listaTodasPessoasPorId(parte);
    }

    public List<FonteDeRecursos> completaFonteDeRecursos(String parte) {
        return relatorioContabilSuperFacade.getFonteDeRecursosFacade().listaFiltrando(parte, "descricao");
    }

    @Override
    public String getNomeRelatorio() {
        return "estorno-pagamento-extra";
    }

    public ContaExtraorcamentaria getContaExtraorcamentaria() {
        return contaExtraorcamentaria;
    }

    public void setContaExtraorcamentaria(ContaExtraorcamentaria contaExtraorcamentaria) {
        this.contaExtraorcamentaria = contaExtraorcamentaria;
    }
}
