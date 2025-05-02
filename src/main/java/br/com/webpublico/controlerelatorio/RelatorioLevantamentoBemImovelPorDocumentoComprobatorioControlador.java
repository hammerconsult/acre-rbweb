package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.GrupoBem;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.LevantamentoBemImovel;
import br.com.webpublico.enums.TipoAquisicaoBem;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.report.ReportService;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by carlos on 30/09/16.
 */
@ManagedBean(name = "relatorioLevantamentoBemImovelPorDocumentoComprobatorioControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoRelatorioLevBemImovelPorDocComprobatorio",
        pattern = "/relatorio-levantamento-bem-imovel-por-documento-comprobatorio/",
        viewId = "/faces/administrativo/patrimonio/relatorios/imovel/relatoriolevantamentobemimovelpordocumentocomprobatorio.xhtml"),
})
public class RelatorioLevantamentoBemImovelPorDocumentoComprobatorioControlador extends AbstractReport {

    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private HierarquiaOrganizacional hierarquiaOrganizacionalOrcamentaria;
    private GrupoBem grupoBem;
    private TipoAquisicaoBem tipoAquisicaoBem;
    private Date dtinicial;
    private Date dtFinal;
    private List<Object[]> ordenacaoDisponivel;
    private Object[][] ordenacaoSelecionada;
    private Exercicio exercicioInicial;
    private Exercicio exercicioFinal;
    private String localizacao;
    private String observacao;
    private String numeroNota;
    private String numeroEmpenho;
    private Exercicio exercicioEmpenho;

    public RelatorioLevantamentoBemImovelPorDocumentoComprobatorioControlador() {
        geraNoDialog = Boolean.FALSE;
    }

    @URLAction(mappingId = "novoRelatorioLevBemImovelPorDocComprobatorio", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        ordenacaoDisponivel = Lists.newArrayList();
        ordenacaoDisponivel.add(new String[]{"Data de Aquisição", "lev.DATAAQUISICAO"});
        ordenacaoDisponivel.add(new String[]{"Registro Patrimonial", "TO_NUMBER(TRIM(lev.CODIGOPATRIMONIO))"});
        ordenacaoDisponivel.add(new String[]{"Descrição do Bem", "lev.DESCRICAOIMOVEL"});
        ordenacaoDisponivel.add(new String[]{"Tipo de Aquisição", "lev.TIPOAQUISICAOBEM"});
        ordenacaoDisponivel.add(new String[]{"Valor do Bem", "lev.VALORBEM"});
    }

    public void gerarRelatorio(String tipoExtensaoRelatorio) {
        try {
            validarFiltrosRelatorio();
            StringBuilder filtros = new StringBuilder();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoExtensaoRelatorio));
            dto.adicionarParametro("MUNICIPIO", montarNomeDoMunicipio());
            if (hierarquiaOrganizacional != null) {
                dto.adicionarParametro("SECRETARIA", hierarquiaOrganizacional.getDescricao().toUpperCase());
            } else if (hierarquiaOrganizacionalOrcamentaria != null) {
                dto.adicionarParametro("SECRETARIA", hierarquiaOrganizacionalOrcamentaria.getDescricao().toUpperCase());
            } else {
                dto.adicionarParametro("SECRETARIA", "");
            }
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("MODULO", "PATRIMÔNIO");
            dto.adicionarParametro("condicao", montarCondicao(filtros));
            dto.adicionarParametro("dataOperacao", getSistemaFacade().getDataOperacao().getTime());
            dto.adicionarParametro("FILTROS", filtros.toString());
            dto.adicionarParametro("USUARIO", "Emitido por: " + getSistemaFacade().getUsuarioCorrente().getNome(), true);
            dto.setNomeRelatorio("RELATÓRIO DE LEVANTAMENTO DE BENS IMÓVEIS POR DOCUMENTOS COMPROBATÓRIOS");
            dto.setApi("administrativo/levantamento-bem-imovel-documento-comprobatorio/");

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

    public List<SelectItem> retornarHierarquiaOrcamentaria() {
        List<SelectItem> toReturn = new ArrayList<>();
        if (hierarquiaOrganizacional != null && hierarquiaOrganizacional.getSubordinada() != null) {
            toReturn.add(new SelectItem(null, ""));
            for (HierarquiaOrganizacional obj : getUnidadeOrganizacionalFacade().getHierarquiaOrganizacionalFacade().retornaHierarquiaOrcamentariaPorUnidadeAdministrativa(hierarquiaOrganizacional.getSubordinada(), getSistemaFacade().getDataOperacao())) {
                toReturn.add(new SelectItem(obj, obj.toString()));
            }
            return toReturn;
        }

        toReturn.add(new SelectItem(null, ""));
        for (HierarquiaOrganizacional obj : getUnidadeOrganizacionalFacade().getHierarquiaOrganizacionalFacade().retornaHierarquiaOrganizacionalOrcamentariaOndeUsuarioEhGestorPatrimonio(getSistemaFacade().getUsuarioCorrente(), getSistemaFacade().getDataOperacao())) {
            toReturn.add(new SelectItem(obj, obj.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getListaSelectItemTipoDeAquisicaoBem() {
        return Util.getListSelectItem(Arrays.asList(LevantamentoBemImovel.tiposDeAquisicaoPermitidosNoCadastro));
    }

    public void limparCampos() {
        hierarquiaOrganizacional = null;
        hierarquiaOrganizacionalOrcamentaria = null;
        grupoBem = null;
        tipoAquisicaoBem = null;
        dtinicial = null;
        dtFinal = null;
    }

    public void validarFiltrosRelatorio() {
        ValidacaoException ve = new ValidacaoException();
        if (hierarquiaOrganizacionalOrcamentaria == null && hierarquiaOrganizacional == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Selecione ao menos uma Unidade Organizacional ou Unidade Orçamentária.");
        }

        if (ve.temMensagens()) {
            throw ve;
        }
    }


    public String montarCondicao(StringBuilder filtros) {
        StringBuilder sql = new StringBuilder();

        if (hierarquiaOrganizacional != null && hierarquiaOrganizacional.getCodigo() != null) {
            sql.append(" and vw.codigo like '").append(hierarquiaOrganizacional.getCodigoSemZerosFinais()).append("%'");
            filtros.append("Unidade Organizacional: ").append(hierarquiaOrganizacional.toString()).append("; ");
        }

        if (hierarquiaOrganizacionalOrcamentaria != null && hierarquiaOrganizacionalOrcamentaria.getCodigo() != null) {
            sql.append(" and vworc.codigo = '").append(hierarquiaOrganizacionalOrcamentaria.getCodigo()).append("'");
            filtros.append("Unidade Orçamentária: ").append(hierarquiaOrganizacionalOrcamentaria.toString()).append("; ");
        }

        if (grupoBem != null) {
            sql.append(" and grupo.id = ").append(grupoBem.getId());
            filtros.append("Grupo Patrimonial ").append(grupoBem.toString()).append("; ");
        }

        if (tipoAquisicaoBem != null) {
            sql.append(" and lev.TIPOAQUISICAOBEM = '").append(tipoAquisicaoBem.name()).append("'");
            filtros.append("Tipo de Aquisição do Bem ").append(tipoAquisicaoBem.getDescricao()).append("; ");
        }

        if (exercicioInicial != null) {
            sql.append(" and extract(year from lev.dataaquisicao) >= ").append(exercicioInicial.getAno());
            filtros.append("Exercício Inicial: ").append(exercicioInicial.getAno()).append("; ");
        }

        if (exercicioFinal != null) {
            sql.append(" and extract(year from lev.dataaquisicao) <= ").append(exercicioFinal.getAno());
            filtros.append("Exercício Final: ").append(exercicioFinal.getAno()).append("; ");
        }

        if (localizacao != null && !localizacao.trim().isEmpty()) {
            sql.append(" and lower(lev.localizacao) like ('%").append(localizacao.toLowerCase()).append("%')");
            filtros.append("Localização: ").append(localizacao).append("; ");
        }

        if (observacao != null && !observacao.trim().isEmpty()) {
            sql.append(" and lower(lev.observacao) like ('%").append(observacao.toLowerCase()).append("%')");
            filtros.append("Observação: ").append(observacao).append("; ");
        }

        if (numeroNota != null && !numeroNota.trim().isEmpty()) {
            sql.append(" and doc.numero = '").append(numeroNota).append("'");
            filtros.append("Número da Nota: ").append(numeroNota).append("; ");
        }

        if (numeroEmpenho != null && !numeroEmpenho.trim().isEmpty()) {
            sql.append(" and empenho.numeroempenho = '").append(numeroEmpenho).append("'");
            filtros.append("Número do Empenho: ").append(numeroEmpenho).append("; ");
        }

        if (exercicioEmpenho != null) {
            sql.append(" and extract(year from empenho.dataempenho) = ").append(exercicioEmpenho.getAno());
            filtros.append("Exercício Empenho: ").append(exercicioEmpenho.getAno()).append("; ");
        }

        if (ordenacaoSelecionada != null && ordenacaoSelecionada.length > 0) {
            sql.append(" order by ");
            filtros.append(" Ordenado por: ");
            for (Object[] item : ordenacaoSelecionada) {
                sql.append((String) item[1]).append(", ");
                filtros.append((String) item[0]).append(", ");
            }
            sql = new StringBuilder(sql.substring(0, sql.length() - 2));
        } else {
            sql.append(" order by lev.codigopatrimonio");
        }
        return sql.toString();
    }

    @Override
    public String montarNomeDoMunicipio() {
        return "MUNICÍPIO DE " + getSistemaFacade().getMunicipio().toUpperCase();
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacionalOrcamentaria() {
        return hierarquiaOrganizacionalOrcamentaria;
    }

    public void setHierarquiaOrganizacionalOrcamentaria(HierarquiaOrganizacional hierarquiaOrganizacionalOrcamentaria) {
        this.hierarquiaOrganizacionalOrcamentaria = hierarquiaOrganizacionalOrcamentaria;
    }

    public GrupoBem getGrupoBem() {
        return grupoBem;
    }

    public void setGrupoBem(GrupoBem grupoBem) {
        this.grupoBem = grupoBem;
    }

    public TipoAquisicaoBem getTipoAquisicaoBem() {
        return tipoAquisicaoBem;
    }

    public void setTipoAquisicaoBem(TipoAquisicaoBem tipoAquisicaoBem) {
        this.tipoAquisicaoBem = tipoAquisicaoBem;
    }

    public Date getDtinicial() {
        return dtinicial;
    }

    public void setDtinicial(Date dtinicial) {
        this.dtinicial = dtinicial;
    }

    public Date getDtFinal() {
        return dtFinal;
    }

    public void setDtFinal(Date dtFinal) {
        this.dtFinal = dtFinal;
    }

    public List<Object[]> getOrdenacaoDisponivel() {
        return ordenacaoDisponivel;
    }

    public void setOrdenacaoDisponivel(List<Object[]> ordenacaoDisponivel) {
        this.ordenacaoDisponivel = ordenacaoDisponivel;
    }

    public Object[][] getOrdenacaoSelecionada() {
        return ordenacaoSelecionada;
    }

    public void setOrdenacaoSelecionada(Object[][] ordenacaoSelecionada) {
        this.ordenacaoSelecionada = ordenacaoSelecionada;
    }

    public Exercicio getExercicioInicial() {
        return exercicioInicial;
    }

    public void setExercicioInicial(Exercicio exercicioInicial) {
        this.exercicioInicial = exercicioInicial;
    }

    public Exercicio getExercicioFinal() {
        return exercicioFinal;
    }

    public void setExercicioFinal(Exercicio exercicioFinal) {
        this.exercicioFinal = exercicioFinal;
    }

    public String getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(String localizacao) {
        this.localizacao = localizacao;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public String getNumeroNota() {
        return numeroNota;
    }

    public void setNumeroNota(String numeroNota) {
        this.numeroNota = numeroNota;
    }

    public String getNumeroEmpenho() {
        return numeroEmpenho;
    }

    public void setNumeroEmpenho(String numeroEmpenho) {
        this.numeroEmpenho = numeroEmpenho;
    }

    public Exercicio getExercicioEmpenho() {
        return exercicioEmpenho;
    }

    public void setExercicioEmpenho(Exercicio exercicioEmpenho) {
        this.exercicioEmpenho = exercicioEmpenho;
    }
}
