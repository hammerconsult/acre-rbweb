package br.com.webpublico.controle.administrativo.patrimonio;

import br.com.webpublico.entidades.GrupoBem;
import br.com.webpublico.entidadesauxiliares.administrativo.relatorio.FiltroPatrimonio;
import br.com.webpublico.enums.TipoAquisicaoBem;
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
import org.apache.commons.lang.StringUtils;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Desenvolvimento on 13/03/2017.
 */

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoRelatorioLevantamentoImovel",
                pattern = "/relatorio-levantamento-bens-imoveis/",
                viewId = "/faces/administrativo/patrimonio/relatorios/imovel/relatorioLevantamentoBemImovel.xhtml")})
public class RelatorioLevantamentoImovelControlador extends RelatorioPatrimonioSuperControlador {

    private List<Object[]> ordenacaoDisponivel;
    private Object[][] ordenacaoSelecionada;
    private Boolean detalhar;
    private GrupoBem grupoBem;
    private Date dtinicial;
    private Date dtFinal;
    private TipoAquisicaoBem tipoAquisicaoBem;
    private String numeroNotaFiscal;
    private String numeroEmpenho;
    private Integer anoEmpenho;
    private String observacao;
    private String localizacao;

    @URLAction(mappingId = "novoRelatorioLevantamentoImovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoRelatorioLevantamentoImovel() {
        limparCampos();
        montarOrdenacaoDisponivel();
    }

    private void montarOrdenacaoDisponivel() {
        setOrdenacaoDisponivel(new ArrayList<Object[]>());
        getOrdenacaoDisponivel().add(new String[]{"Data de Aquisição", "lev.DATAAQUISICAO"});
        getOrdenacaoDisponivel().add(new String[]{"Registro Patrimonial", "TO_NUMBER(TRIM(lev.CODIGOPATRIMONIO))"});
        getOrdenacaoDisponivel().add(new String[]{"Descrição do Bem", "lev.DESCRICAOIMOVEL"});
        getOrdenacaoDisponivel().add(new String[]{"Tipo de Aquisição", "lev.TIPOAQUISICAOBEM"});
        getOrdenacaoDisponivel().add(new String[]{"Valor do Bem", "lev.VALORBEM"});
    }

    public void limparCampos() {
        filtro = new FiltroPatrimonio();
        ordenacaoSelecionada = null;
        detalhar = Boolean.FALSE;
        grupoBem = null;
        dtinicial = null;
        dtFinal = null;
        tipoAquisicaoBem = null;
        numeroNotaFiscal = null;
        numeroEmpenho = null;
        anoEmpenho = null;
        observacao = null;
        localizacao = null;
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarFiltrosRelatorioLevantamentoImovel();
            StringBuffer filtros = new StringBuffer();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USUARIO", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE " + getSistemaFacade().getMunicipio().toUpperCase());
            dto.adicionarParametro("MODULO", "PATRIMÔNIO");
            dto.adicionarParametro("SECRETARIA", montaNomeSecretaria());
            dto.adicionarParametro("DETALHADO", detalhar);
            dto.adicionarParametro("NOMERELATORIO", "RELATÓRIO DE LEVANTAMENTO DE BENS IMÓVEIS");
            dto.adicionarParametro("dataOperacao", DataUtil.getDataFormatada(getSistemaFacade().getDataOperacao()));
            dto.adicionarParametro("CONDICAO", montarCondicaoEFiltros(filtros, dto));
            dto.adicionarParametro("FILTROS", filtros.toString());
            dto.setNomeRelatorio("RelatorioDeLevantamentoDeBensImoveis");
            dto.setApi("administrativo/levantamento-imovel/");
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

    private void validarFiltrosRelatorioLevantamentoImovel() {
        ValidacaoException ve = new ValidacaoException();
        if (filtro.getHierarquiaOrc() == null && filtro.getHierarquiaAdm() == null)
            ve.adicionarMensagemDeCampoObrigatorio("Selecione ao menos uma unidade organizacional ou unidade orçamentária.");
        if (StringUtils.isNotEmpty(getNumeroEmpenho()) && getAnoEmpenho() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Para filtrar por empenho, por favor informar o ano do empenho Ex.: 110502/2016  ");
        } else if (getAnoEmpenho() != null && StringUtils.isEmpty(getNumeroEmpenho())) {
            ve.adicionarMensagemDeCampoObrigatorio("Para filtrar por empenho, por favor informar o número do empenho Ex.: 110502/2016  ");
        }
        if (getDtinicial() != null && getDtFinal() != null) {
            if (getDtinicial().after(getDtFinal())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Data inicial deve ser anterior a data final.");
            }
        }
        ve.lancarException();
    }

    private String montarCondicaoEFiltros(StringBuffer filtros, RelatorioDTO dto) {
        StringBuffer sql = new StringBuffer();

        if (filtro.getHierarquiaAdm() != null && filtro.getHierarquiaAdm().getCodigo() != null) {
            sql.append(" and VW.CODIGO LIKE '").append(filtro.getHierarquiaAdm().getCodigoSemZerosFinais()).append("%' ");
            dto.adicionarParametro("HIERARQUIA", filtro.getHierarquiaAdm().toString());
            filtros.append("Unidade Administrativa: ").append(filtro.getHierarquiaAdm().toString()).append(". ");
        }

        if (filtro.getHierarquiaOrc() != null && filtro.getHierarquiaOrc().getCodigo() != null) {
            sql.append(" and lev.UNIDADEORCAMENTARIA_ID = ").append(filtro.getHierarquiaOrc().getSubordinada().getId());
            dto.adicionarParametro("HIERARQUIA_ORC", filtro.getHierarquiaOrc().toString());
            filtros.append("Unidade Orçametária: ").append(filtro.getHierarquiaOrc().toString()).append(". ");
        }

        if (grupoBem != null) {
            sql.append(" and lev.grupobem_id = ").append(grupoBem.getId());
            filtros.append("Grupo Patrimonial: ").append(grupoBem.toString()).append(". ");
        }

        if (dtinicial != null) {
            sql.append(" and lev.DATAAQUISICAO >= to_date('").append(DataUtil.getDataFormatada(dtinicial)).append("','dd/MM/yyyy') ");
            filtros.append("Data de Aquisição Inicial: ").append(DataUtil.getDataFormatada(dtinicial)).append(". ");
        }

        if (dtFinal != null) {
            sql.append(" and lev.DATAAQUISICAO <= to_date('").append(DataUtil.getDataFormatada(dtFinal)).append("','dd/MM/yyyy') ");
            filtros.append("Data de Aquisição Final: ").append(DataUtil.getDataFormatada(dtFinal)).append(". ");
        }

        if (tipoAquisicaoBem != null) {
            sql.append(" and lev.tipoAquisicaoBem =  '").append(tipoAquisicaoBem.name()).append("' ");
            filtros.append("Tipo de Aquisição: ").append(tipoAquisicaoBem.getDescricao()).append(". ");
        }

        if (StringUtils.isNotEmpty(getNumeroNotaFiscal())) {
            sql.append(" and exists ( ");
            sql.append(" select doc.id from DOCTOCOMPROBLEVBEMIMOVEL doc where lev.id = doc.LEVANTAMENTOBEMIMOVEL_ID ");
            sql.append(" and doc.NUMERO  = '").append(getNumeroNotaFiscal()).append("' ) ");
        }

        if (StringUtils.isNotEmpty(getNumeroEmpenho()) && getAnoEmpenho() != null) {
            sql.append(" and exists ( ");
            sql.append(" select doc.id ");
            sql.append(" from DOCTOCOMPROBLEVBEMIMOVEL doc ");
            sql.append(" INNER JOIN EMPENHOLEVANTAMENTOIMOVEL emp ON doc.ID = emp.DOCUMENTOCOMPROBATORIO_ID ");
            sql.append(" WHERE doc.LEVANTAMENTOBEMIMOVEL_ID = lev.id AND emp.NUMEROEMPENHO = ").append(getNumeroEmpenho());
            sql.append(" AND  ");
            sql.append(" extract(YEAR FROM emp.DATAEMPENHO) =  ").append(getAnoEmpenho()).append(" ) ");

        }

        if (StringUtils.isNotEmpty(getObservacao())) {
            sql.append(" and lev.observacao like  '%").append(getObservacao()).append("%'");
        }

        if (StringUtils.isNotEmpty(getLocalizacao())) {
            sql.append(" and lev.localizacao like '%").append(getLocalizacao()).append("%'");
        }

        if (ordenacaoSelecionada != null && ordenacaoSelecionada.length > 0) {
            sql.append(" order by ");
            filtros.append(" Ordenado por: ");
            for (Object[] item : ordenacaoSelecionada) {
                sql.append((String) item[1]).append(", ");
                filtros.append((String) item[0]).append(", ");
            }
            sql = new StringBuffer(sql.substring(0, sql.length() - 2));
            filtros = new StringBuffer(sql.substring(0, sql.length() - 2)).append(".");
        } else {
            sql.append(" order by vw.CODIGO || ' ' || vw.DESCRICAO ");
        }
        return sql.toString();
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

    public Boolean getDetalhar() {
        return detalhar;
    }

    public void setDetalhar(Boolean detalhar) {
        this.detalhar = detalhar;
    }

    public GrupoBem getGrupoBem() {
        return grupoBem;
    }

    public void setGrupoBem(GrupoBem grupoBem) {
        this.grupoBem = grupoBem;
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

    public TipoAquisicaoBem getTipoAquisicaoBem() {
        return tipoAquisicaoBem;
    }

    public void setTipoAquisicaoBem(TipoAquisicaoBem tipoAquisicaoBem) {
        this.tipoAquisicaoBem = tipoAquisicaoBem;
    }

    public String getNumeroNotaFiscal() {
        return numeroNotaFiscal;
    }

    public void setNumeroNotaFiscal(String numeroNotaFiscal) {
        this.numeroNotaFiscal = numeroNotaFiscal;
    }

    public String getNumeroEmpenho() {
        return numeroEmpenho;
    }

    public void setNumeroEmpenho(String numeroEmpenho) {
        this.numeroEmpenho = numeroEmpenho;
    }

    public Integer getAnoEmpenho() {
        return anoEmpenho;
    }

    public void setAnoEmpenho(Integer anoEmpenho) {
        this.anoEmpenho = anoEmpenho;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public String getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(String localizacao) {
        this.localizacao = localizacao;
    }
}
