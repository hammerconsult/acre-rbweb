/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.OperacaoRelatorio;
import br.com.webpublico.enums.TipoContaExtraorcamentaria;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
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

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author juggernaut
 */
@ManagedBean(name = "relatorioPagamentoExtra")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-despesa-extra", pattern = "/relatorio/despesa-extra/", viewId = "/faces/financeiro/relatorio/relatoriopagamentoextra.xhtml")
})
public class RelatorioPagamentoExtra extends RelatorioContabilSuperControlador implements Serializable {

    private TipoContaExtraorcamentaria tipoContaExtra;
    private Conta contaDeDestinacao;

    @URLAction(mappingId = "relatorio-despesa-extra", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        super.limparCamposGeral();
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            RelatorioDTO dto = new RelatorioDTO();
            dto.setApi("contabil/despesa-extra/");
            dto.setNomeRelatorio(getNomeRelatorio());
            dto.setNomeParametroBrasao("IMAGEM");
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.setNomeRelatorio("Relatório de Despesa Extra");
            dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO");
            dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(montarParametros()));
            dto.adicionarParametro("APRESENTACAO", apresentacao.name());
            dto.adicionarParametro("apresentacao", apresentacao.getToDto());
            dto.adicionarParametro("pesquisouUg", unidadeGestora != null);
            dto.adicionarParametro("ANO_EXERCICIO", getSistemaControlador().getExercicioCorrente().getAno().toString());
            dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getUsername(), Boolean.FALSE);
            dto.adicionarParametro("FILTRO", filtros);
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

    private List<ParametrosRelatorios> montarParametros() {
        List<ParametrosRelatorios> parametros = new ArrayList<>();
        parametros.add(new ParametrosRelatorios(" trunc(PE.DATAPAGTO) ", ":DataInicial", ":DataFinal", OperacaoRelatorio.BETWEEN, DataUtil.getDataFormatada(dataInicial), DataUtil.getDataFormatada(dataFinal), 1, true));
        filtros = " Período: " + DataUtil.getDataFormatada(dataInicial) + " a " + DataUtil.getDataFormatada(dataFinal) + " -";

        if (numero != null && !numero.isEmpty()) {
            parametros.add(new ParametrosRelatorios(" pe.numero ", ":numero", null, OperacaoRelatorio.LIKE, numero.toString(), null, 1, false));
            filtros += " Número: " + numero + " -";
        }
        if (pessoa != null && pessoa.getId() != null) {
            parametros.add(new ParametrosRelatorios(" pe.fornecedor_id ", ":fornecedor", null, OperacaoRelatorio.IGUAL, pessoa.getId(), null, 1, false));
            filtros += " Pessoa: " + pessoa.getNome() + " -";
        }
        if (fonteDeRecursos != null && fonteDeRecursos.getId() != null) {
            parametros.add(new ParametrosRelatorios(" FR.ID ", ":fonteId", null, OperacaoRelatorio.IGUAL, fonteDeRecursos.getId(), null, 1, false));
            filtros += " Fonte de Recurso: " + fonteDeRecursos.getCodigo() + " -";
        }
        if (contaDeDestinacao != null) {
            parametros.add(new ParametrosRelatorios(" cdest.ID ", ":cdestId", null, OperacaoRelatorio.IGUAL, contaDeDestinacao.getId(), null, 1, false));
            filtros += " Conta de Destinação de Recurso: " + contaDeDestinacao.getCodigo() + " -";
        }
        if (conta != null) {
            parametros.add(new ParametrosRelatorios(" C.CODIGO ", ":contaCodigo", null, OperacaoRelatorio.LIKE, conta.getCodigo(), null, 1, false));
            filtros += " Conta Extraorçamentária: " + conta.getCodigo() + " -";
        }
        if (contasFinanceiras != null && !contasFinanceiras.isEmpty()) {
            List<Long> idsSubContas = Lists.newArrayList();
            String juncao = "";
            filtros += contasFinanceiras.size() > 1 ? " Conta Financeira: " : " Contas Financeiras: ";
            for (SubConta subConta : contasFinanceiras) {
                idsSubContas.add(subConta.getId());
                filtros += juncao + subConta.getCodigo();
                juncao = ", ";
            }
            filtros += " -";
            parametros.add(new ParametrosRelatorios(" sub.id ", ":idSubContas", null, OperacaoRelatorio.IN, idsSubContas, null, 1, false));
        }

        if (!listaUnidades.isEmpty()) {
            List<Long> idsUnidades = Lists.newArrayList();
            String unidades = "";
            for (HierarquiaOrganizacional hierarquiaOrganizacional : listaUnidades) {
                idsUnidades.add(hierarquiaOrganizacional.getSubordinada().getId());
                unidades += " " + hierarquiaOrganizacional.getCodigo().substring(3, 10) + " -";
            }
            filtros += " Unidade(s): " + unidades;
            parametros.add(new ParametrosRelatorios(" vw.subordinada_id ", ":undId", null, OperacaoRelatorio.IN, idsUnidades, null, 1, false));
        } else if (unidadeGestora == null) {
            List<HierarquiaOrganizacional> unidadesDoUsuario = relatorioContabilSuperFacade.getHierarquiaOrganizacionalFacade().listaHierarquiaUsuarioCorrentePorNivel("", getSistemaFacade().getUsuarioCorrente(), getSistemaFacade().getExercicioCorrente(), getSistemaFacade().getDataOperacao(), TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), 3);
            List<Long> idsUnidades = Lists.newArrayList();
            for (HierarquiaOrganizacional hierarquiaOrganizacional : unidadesDoUsuario) {
                idsUnidades.add(hierarquiaOrganizacional.getSubordinada().getId());
            }
            if (!idsUnidades.isEmpty()) {
                parametros.add(new ParametrosRelatorios(" vw.subordinada_id ", ":undId", null, OperacaoRelatorio.IN, idsUnidades, null, 1, false));
            }
        }

        if (unidadeGestora != null) {
            parametros.add(new ParametrosRelatorios(" ug.id ", ":ugId", null, OperacaoRelatorio.IGUAL, this.unidadeGestora.getId(), null, 1, false));
            filtros += " Unidade Gestora: " + this.unidadeGestora.getCodigo() + " -";
        }
        if (unidadeGestora != null || apresentacao.isApresentacaoUnidadeGestora()) {
            Exercicio exercicio = getExercicioFacade().recuperarExercicioPeloAno(new Integer(DataUtil.getDataFormatada(dataInicial, "yyyy")));
            parametros.add(new ParametrosRelatorios(null, ":exercicio", null, null, exercicio.getId(), null, 0, false));
        }
        atualizaFiltrosGerais();
        return parametros;
    }

    public List<Pessoa> completarPessoas(String parte) {
        return relatorioContabilSuperFacade.getPessoaFacade().listaTodasPessoasPorId(parte);
    }

    public List<FonteDeRecursos> completarFontesDeRecursos(String parte) {
        return relatorioContabilSuperFacade.getFonteDeRecursosFacade().listaFiltrandoPorExercicio(parte, getSistemaFacade().getExercicioCorrente());
    }

    public List<Conta> completarContas(String parte) {
        return relatorioContabilSuperFacade.getContaFacade().listaFiltrandoExtraorcamentario(parte.trim(), getSistemaFacade().getExercicioCorrente());
    }

    public List<Conta> completarContasDeDestinacao(String parte) {
        return relatorioContabilSuperFacade.getContaFacade().buscarContasDeDestinacaoDeRecursos(parte.trim(), getSistemaControlador().getExercicioCorrente());
    }

    public List<SelectItem> getTiposDeContaExtra() {
        return Util.getListSelectItem(TipoContaExtraorcamentaria.values(), false);
    }

    public TipoContaExtraorcamentaria getTipoContaExtra() {
        return tipoContaExtra;
    }

    public void setTipoContaExtra(TipoContaExtraorcamentaria tipoContaExtra) {
        this.tipoContaExtra = tipoContaExtra;
    }

    public Conta getContaDeDestinacao() {
        return contaDeDestinacao;
    }

    public void setContaDeDestinacao(Conta contaDeDestinacao) {
        this.contaDeDestinacao = contaDeDestinacao;
    }

    @Override
    public String getNomeRelatorio() {
        return null;
    }
}
