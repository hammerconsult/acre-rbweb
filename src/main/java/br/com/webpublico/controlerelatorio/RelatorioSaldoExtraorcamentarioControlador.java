package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.OperacaoRelatorio;
import br.com.webpublico.enums.TipoExibicao;
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
 * Created by Mateus on 08/04/2015.
 */
@ManagedBean(name = "relatorioSaldoExtraorcamentarioControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-saldo-conta-extra", pattern = "/relatorio/saldo-conta-extra/", viewId = "/faces/financeiro/relatorio/relatoriosaldoextra.xhtml")
})
public class RelatorioSaldoExtraorcamentarioControlador extends RelatorioContabilSuperControlador implements Serializable {

    private Conta contaDeDestinacao;
    private TipoExibicao tipoExibicao;

    @URLAction(mappingId = "relatorio-saldo-conta-extra", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        super.limparCamposGeral();
        contaDeDestinacao = null;
        tipoExibicao = TipoExibicao.CONTA_DE_DESTINACAO;
    }

    public List<SelectItem> getTiposDeExibicao() {
        return Util.getListSelectItemSemCampoVazio(TipoExibicao.values(), false);
    }

    public void limparFonteAndConta() {
        fonteDeRecursos = null;
        conta = null;
        contaDeDestinacao = null;
    }

    public List<FonteDeRecursos> completarFontesDeRecursos(String parte) {
        return relatorioContabilSuperFacade.getFonteDeRecursosFacade().listaFiltrandoPorExercicio(parte, buscarExercicioPelaDataDeReferencia());
    }

    public List<Conta> completarContasExtras(String parte) {
        return relatorioContabilSuperFacade.getContaFacade().listaFiltrandoTodasContaExtraorcamentaria(parte, buscarExercicioPelaDataDeReferencia());
    }

    public List<Conta> completarContasDeDestinacao(String parte) {
        return relatorioContabilSuperFacade.getContaFacade().buscarContasDeDestinacaoDeRecursos(parte.trim(), buscarExercicioPelaDataDeReferencia());
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarDataDeReferencia();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO");
            dto.adicionarParametro("pesquisouUg", unidadeGestora != null);
            dto.adicionarParametro("apresentacaoRelatorioDTO", apresentacao.getToDto());
            dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(montarParametros()));
            dto.adicionarParametro("FILTRO", filtros);
            dto.adicionarParametro("APRESENTACAO", apresentacao.name());
            dto.adicionarParametro("TIPOEXIBICAO", tipoExibicao.getToDto());
            dto.adicionarParametro("DESCRICAO_FONTE", tipoExibicao.getDescricao());
            dto.setNomeRelatorio(getNomeRelatorio());
            dto.setApi("contabil/saldo-conta-extra/");
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

    private List<ParametrosRelatorios> montarParametros() {
        List<ParametrosRelatorios> parametros = new ArrayList<>();
        parametros.add(new ParametrosRelatorios(null, ":DATAREFERENCIA", null, null, DataUtil.getDataFormatada(dataReferencia), null, 0, true));
        filtros = " Data de Referência: " + DataUtil.getDataFormatada(dataReferencia) + " -";
        filtros += " Exibir: " + tipoExibicao.getDescricao() + " -";
        if (conta != null && conta.getId() != null) {
            parametros.add(new ParametrosRelatorios(" c.codigo ", ":conta", null, OperacaoRelatorio.LIKE, conta.getCodigo(), null, 1, false));
            filtros += " Conta Extra-Orçamentária: " + conta.getCodigo() + " -";
        }

        if (fonteDeRecursos != null && fonteDeRecursos.getId() != null) {
            parametros.add(new ParametrosRelatorios(" fonte.codigo ", ":fonte", null, OperacaoRelatorio.LIKE, fonteDeRecursos.getCodigo(), null, 1, false));
            filtros += " Fonte de Recurso: " + fonteDeRecursos.getCodigo() + " -";
        }
        if (contaDeDestinacao != null) {
            parametros.add(new ParametrosRelatorios(" cdest.ID ", ":cdestId", null, OperacaoRelatorio.IGUAL, contaDeDestinacao.getId(), null, 1, false));
            filtros += " Conta de Destinação de Recurso: " + contaDeDestinacao.getCodigo() + " -";
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
        }

        if (unidadeGestora != null) {
            parametros.add(new ParametrosRelatorios(" ug.codigo ", ":ugId", null, OperacaoRelatorio.LIKE, this.unidadeGestora.getCodigo(), null, 1, false));
            filtros += " Unidade Gestora: " + this.unidadeGestora.getCodigo() + " -";
        }
        Exercicio exercicio = getExercicioFacade().recuperarExercicioPeloAno(DataUtil.getAno(dataReferencia));
        parametros.add(new ParametrosRelatorios(" c.exercicio_id ", ":exercicio", null, OperacaoRelatorio.IGUAL, exercicio.getId(), null, 1, false));
        atualizaFiltrosGerais();
        return parametros;
    }

    @Override
    public String getNomeRelatorio() {
        if (apresentacao.isApresentacaoConsolidado()) {
            return "DPO-" + DataUtil.getAno(getDataReferencia()) + "-" + DataUtil.getMes(getDataReferencia()) + "-" + "CSL";
        } else if (apresentacao.isApresentacaoUnidade()) {
            return "DPO-" + DataUtil.getAno(getDataReferencia()) + "-" + DataUtil.getMes(getDataReferencia()) + "-" + "UND";
        } else if (apresentacao.isApresentacaoOrgao()) {
            return "DPO-" + DataUtil.getAno(getDataReferencia()) + "-" + DataUtil.getMes(getDataReferencia()) + "-" + "ORG";
        } else {
            return "DPO-" + DataUtil.getAno(getDataReferencia()) + "-" + DataUtil.getMes(getDataReferencia()) + "-" + "UNG";
        }
    }

    public Conta getContaDeDestinacao() {
        return contaDeDestinacao;
    }

    public void setContaDeDestinacao(Conta contaDeDestinacao) {
        this.contaDeDestinacao = contaDeDestinacao;
    }

    public TipoExibicao getTipoExibicao() {
        return tipoExibicao;
    }

    public void setTipoExibicao(TipoExibicao tipoExibicao) {
        this.tipoExibicao = tipoExibicao;
    }
}
