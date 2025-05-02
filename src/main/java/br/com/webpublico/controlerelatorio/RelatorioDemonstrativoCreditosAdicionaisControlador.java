package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.OperacaoRelatorio;
import br.com.webpublico.enums.TipoAdministracao;
import br.com.webpublico.enums.TipoEntidade;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
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

/**
 * Created with IntelliJ IDEA.
 * User: Edi
 * Date: 12/12/13
 * Time: 11:30
 * To change this template use File | Settings | File Templates.
 */

@ManagedBean(name = "relatorioDemonstrativoCreditosAdicionaisControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-demonstrativo-creditos-adicionais", pattern = "/relatorio/demonstrativo-creditos-adicionais/", viewId = "/faces/financeiro/relatorio/relatoriodemonstrativocreditosadicionais.xhtml")
})
public class RelatorioDemonstrativoCreditosAdicionaisControlador extends RelatorioContabilSuperControlador implements Serializable {

    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    private TipoAdministracao tipoAdministracao;
    private TipoEntidade natureza;
    private Exibicao exibicao;

    private enum Exibicao {
        ORGAO("Orgão"),
        UNIDADE("Orgão e Unidade");
        private String descricao;

        private Exibicao(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }

        public void setDescricao(String descricao) {
            this.descricao = descricao;
        }

        @Override
        public String toString() {
            return descricao;
        }
    }

    public RelatorioDemonstrativoCreditosAdicionaisControlador() {
    }

    @URLAction(mappingId = "relatorio-demonstrativo-creditos-adicionais", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        super.limparCamposGeral();
        dataInicial = null;
        dataFinal = null;
        tipoAdministracao = null;
        natureza = null;
    }

    public List<SelectItem> getExibicoes() {
        return Util.getListSelectItemSemCampoVazio(Exibicao.values());
    }

    public List<SelectItem> getTiposDeAdministracao() {
        return Util.getListSelectItem(TipoAdministracao.values());
    }

    public List<SelectItem> getTiposDeEntidade() {
        return Util.getListSelectItem(TipoEntidade.values());
    }

    public void gerarRelatorio(String tipoExtensaoRelatorio) {
        try {
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoExtensaoRelatorio));
            dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("MUNICIPIO", getDescricaoSegundoEsferaDoPoder());
            dto.adicionarParametro("ANO_EXERCICIO", getSistemaControlador().getExercicioCorrente().getAno().toString());
            dto.adicionarParametro("EXERCICIO", getSistemaControlador().getExercicioCorrente().getId());
            dto.adicionarParametro("FILTRO_DATA", getFiltrosDatas());
            dto.adicionarParametro("FILTRO_DATAREFERENCIA", getDataReferenciaFormatada());
            dto.adicionarParametro("FILTRO_UG", unidadeGestora != null ? filtroUG : "");
            dto.adicionarParametro("APRESENTACAO", apresentacao.name());
            dto.adicionarParametro("EXIBICAO", exibicao.name());
            dto.adicionarParametro("pesquisouUg", unidadeGestora != null);
            dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(montarParametrosEFiltros()));
            dto.adicionarParametro("FILTRO_GERAL", filtros.trim());
            dto.setNomeRelatorio(getNomeRelatorio());
            dto.setApi("contabil/demonstrativo-creditos-adicionais/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public Exercicio buscarExercicioPelaDataReferencia() {
        if (dataReferencia == null) {
            return getSistemaControlador().getExercicioCorrente();
        }
        return getExercicioFacade().getExercicioPorAno(DataUtil.getAno(dataReferencia));
    }

    private List<ParametrosRelatorios> montarParametrosEFiltros() {
        List<ParametrosRelatorios> parametros = new ArrayList<>();
        parametros.add(new ParametrosRelatorios(null, ":DATAINICIAL", null, OperacaoRelatorio.IGUAL, "01/01/" + buscarExercicioPelaDataReferencia().getAno(), null, 0, true));
        parametros.add(new ParametrosRelatorios(null, ":DATAFINAL", null, OperacaoRelatorio.IGUAL, DataUtil.getDataFormatada(dataReferencia), null, 0, true));
        filtros = "";
        List<Long> idsUnidades = new ArrayList<>();
        if (this.listaUnidades.size() > 0) {
            String unidades = "";
            for (HierarquiaOrganizacional hierarquia : listaUnidades) {
                idsUnidades.add(hierarquia.getSubordinada().getId());
                unidades += " " + hierarquia.getCodigo().substring(3, 10) + " -";
            }
            filtros += " Unidade(s): " + unidades;
            parametros.add(new ParametrosRelatorios(" vw.subordinada_id ", ":undId", null, OperacaoRelatorio.IN, idsUnidades, null, 1, false));
        } else if (this.unidadeGestora == null) {
            List<HierarquiaOrganizacional> hierarquias = hierarquiaOrganizacionalFacade.listaHierarquiaUsuarioCorrentePorNivel("", getSistemaControlador().getUsuarioCorrente(), buscarExercicioPelaDataReferencia(), getDataFinal(), TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), 3);
            for (HierarquiaOrganizacional hierarquia : hierarquias) {
                idsUnidades.add(hierarquia.getSubordinada().getId());
            }
            if (idsUnidades.size() != 0) {
                parametros.add(new ParametrosRelatorios(" vw.subordinada_id ", ":undId", null, OperacaoRelatorio.IN, idsUnidades, null, 1, false));
            }
        } else {
            parametros.add(new ParametrosRelatorios(" ug.id ", ":ugId", null, OperacaoRelatorio.IGUAL, this.unidadeGestora.getId(), null, 1, false));
            filtros += " Unidade Gestora: " + this.unidadeGestora.getCodigo() + " -";
        }
        if (esferaDoPoder != null) {
            parametros.add(new ParametrosRelatorios(" VW.ESFERADOPODER ", ":esfera", null, OperacaoRelatorio.IGUAL, esferaDoPoder.name(), null, 1, false));
            filtros += " Poder: " + this.esferaDoPoder.getDescricao() + " -";
        }
        if (tipoAdministracao != null) {
            parametros.add(new ParametrosRelatorios(" VW.TIPOADMINISTRACAO ", ":tipoAdm", null, OperacaoRelatorio.IGUAL, tipoAdministracao.name(), null, 1, false));
            filtros += " Administração: " + this.tipoAdministracao.getDescricao() + " -";
        }
        if (natureza != null) {
            parametros.add(new ParametrosRelatorios(" VW.TIPOUNIDADE ", ":natureza", null, OperacaoRelatorio.IGUAL, natureza.name(), null, 1, false));
            filtros += " Natureza: " + this.natureza.getTipo() + " -";
        }
        parametros.add(new ParametrosRelatorios(null, ":exercicio", null, null, buscarExercicioPelaDataReferencia().getId(), null, 0, false));
        filtros = atualizaFiltrosGerais();
        return parametros;
    }

    @Override
    public String getNomeRelatorio() {
        return "demonstrativo-creditos-adicionais";
    }

    public TipoAdministracao getTipoAdministracao() {
        return tipoAdministracao;
    }

    public void setTipoAdministracao(TipoAdministracao tipoAdministracao) {
        this.tipoAdministracao = tipoAdministracao;
    }

    public TipoEntidade getNatureza() {
        return natureza;
    }

    public void setNatureza(TipoEntidade natureza) {
        this.natureza = natureza;
    }

    public Exibicao getExibicao() {
        return exibicao;
    }

    public void setExibicao(Exibicao exibicao) {
        this.exibicao = exibicao;
    }
}
