package br.com.webpublico.controlerelatorio.administrativo;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.FiltroMateriais;
import br.com.webpublico.enums.ApresentacaoRelatorio;
import br.com.webpublico.enums.TipoEstoque;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.enums.TipoObjetoCompra;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;

import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Future;

/**
 * Created by Desenvolvimento on 07/02/2017.
 */
public class RelatorioMaterialSuperControlador extends AbstractReport {

    /**
     * recursos para o modelo de relatório de apresentação horizontal = br/com/webpublico/report/ModeloHorizontalApresentacao.jrxml
     * recursos para o modelo de relatório de apresentação vertical = br/com/webpublico/report/ModeloVerticalApresentacao.jrxml
     */

    @EJB
    private MaterialFacade materialFacade;
    @EJB
    private LocalEstoqueFacade localEstoqueFacade;
    @EJB
    private GrupoMaterialFacade grupoMaterialFacade;
    @EJB
    private GrupoObjetoCompraFacade grupoObjetoCompraFacade;
    @EJB
    private LoteMaterialFacade loteMaterialFacade;
    @EJB
    private ObjetoCompraFacade objetoCompraFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;

    private ApresentacaoRelatorio apresentacaoRelatorio;
    private TipoHierarquiaOrganizacional tipoHierarquiaOrganizacional;
    private TipoEstoque tipoEstoque;
    private LocalEstoque localEstoque;
    private Material material;
    private GrupoMaterial grupoMaterial;
    private GrupoObjetoCompra grupoObjetoCompra;
    private HierarquiaOrganizacional hierarquiaAdministrativa;
    private HierarquiaOrganizacional hierarquiaOrcamentaria;
    private Date dataInicial;
    private Date dataFinal;
    private Date dataReferencia;
    private StringBuffer filtros;
    private StringBuffer condicaoWhere;
    private FiltroMateriais filtroMateriais;
    private Future<ByteArrayOutputStream> futureBytesRelatorio;

    public RelatorioMaterialSuperControlador() {
        filtroMateriais = new FiltroMateriais();
        apresentacaoRelatorio = ApresentacaoRelatorio.CONSOLIDADO;
        dataInicial = new Date();
        dataFinal = new Date();
        dataReferencia = new Date();
        filtros = new StringBuffer();
        condicaoWhere = new StringBuffer();
        tipoHierarquiaOrganizacional = TipoHierarquiaOrganizacional.ADMINISTRATIVA;
    }

    public void verificarRelatorio() {
        if (futureBytesRelatorio != null && futureBytesRelatorio.isDone()) {
            FacesUtil.executaJavaScript("terminarRelatorio()");
        }
    }

    public void imprimir(String nomeRelatorio) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        facesContext.responseComplete();
        try {
            byte[] bytes = futureBytesRelatorio.get().toByteArray();
            if (bytes != null && bytes.length > 0) {
                HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
                response.setContentType("application/pdf");
                response.setHeader("Content-disposition", "inline; filename=\"'" + nomeRelatorio + "'.pdf\"");
                response.setContentLength(bytes.length);
                ServletOutputStream outputStream = response.getOutputStream();
                outputStream.write(bytes, 0, bytes.length);
                outputStream.flush();
                outputStream.close();
            }
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
        }
    }

    public void getUsuarioSistema(HashMap parameters) {
        parameters.put("USUARIO", getSistemaFacade().getUsuarioCorrente().getNome());
    }

    public MaterialFacade getMaterialFacade() {
        return materialFacade;
    }

    public LocalEstoqueFacade getLocalEstoqueFacade() {
        return localEstoqueFacade;
    }

    public GrupoMaterialFacade getGrupoMaterialFacade() {
        return grupoMaterialFacade;
    }

    public GrupoObjetoCompraFacade getGrupoObjetoCompraFacade() {
        return grupoObjetoCompraFacade;
    }

    public LoteMaterialFacade getLoteMaterialFacade() {
        return loteMaterialFacade;
    }

    public ObjetoCompraFacade getObjetoCompraFacade() {
        return objetoCompraFacade;
    }

    public ApresentacaoRelatorio getApresentacaoRelatorio() {
        return apresentacaoRelatorio;
    }

    public void setApresentacaoRelatorio(ApresentacaoRelatorio apresentacaoRelatorio) {
        this.apresentacaoRelatorio = apresentacaoRelatorio;
    }

    public TipoEstoque getTipoEstoque() {
        return tipoEstoque;
    }

    public void setTipoEstoque(TipoEstoque tipoEstoque) {
        this.tipoEstoque = tipoEstoque;
    }

    public LocalEstoque getLocalEstoque() {
        return localEstoque;
    }

    public void setLocalEstoque(LocalEstoque localEstoque) {
        this.localEstoque = localEstoque;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public GrupoMaterial getGrupoMaterial() {
        return grupoMaterial;
    }

    public void setGrupoMaterial(GrupoMaterial grupoMaterial) {
        this.grupoMaterial = grupoMaterial;
    }

    public GrupoObjetoCompra getGrupoObjetoCompra() {
        return grupoObjetoCompra;
    }

    public void setGrupoObjetoCompra(GrupoObjetoCompra grupoObjetoCompra) {
        this.grupoObjetoCompra = grupoObjetoCompra;
    }

    public HierarquiaOrganizacional getHierarquiaAdministrativa() {
        return hierarquiaAdministrativa;
    }

    public void setHierarquiaAdministrativa(HierarquiaOrganizacional hierarquiaAdministrativa) {
        this.hierarquiaAdministrativa = hierarquiaAdministrativa;
    }

    public HierarquiaOrganizacional getHierarquiaOrcamentaria() {
        return hierarquiaOrcamentaria;
    }

    public void setHierarquiaOrcamentaria(HierarquiaOrganizacional hierarquiaOrcamentaria) {
        this.hierarquiaOrcamentaria = hierarquiaOrcamentaria;
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

    public StringBuffer getFiltros() {
        return filtros;
    }

    public void setFiltros(StringBuffer filtros) {
        this.filtros = filtros;
    }

    public StringBuffer getCondicaoWhere() {
        return condicaoWhere;
    }

    public void setCondicaoWhere(StringBuffer condicaoWhere) {
        this.condicaoWhere = condicaoWhere;
    }

    public Date getDataReferencia() {
        return dataReferencia;
    }

    public void setDataReferencia(Date dataReferencia) {
        this.dataReferencia = dataReferencia;
    }

    public TipoHierarquiaOrganizacional getTipoHierarquiaOrganizacional() {
        return tipoHierarquiaOrganizacional;
    }

    public void setTipoHierarquiaOrganizacional(TipoHierarquiaOrganizacional tipoHierarquiaOrganizacional) {
        this.tipoHierarquiaOrganizacional = tipoHierarquiaOrganizacional;
    }

    public List<SelectItem> buscarApresentacoesDeRelatorio() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (ApresentacaoRelatorio ap : ApresentacaoRelatorio.values()) {
            if (!ap.equals(ApresentacaoRelatorio.UNIDADE_GESTORA)) {
                toReturn.add(new SelectItem(ap, ap.getDescricao()));
            }
        }
        return toReturn;
    }

    public List<SelectItem> getBuscarTipoDeEstoques() {
        return Util.getListSelectItem(TipoEstoque.values());
    }

    public List<SelectItem> getBuscarTipoHierarquiaOrganizacional() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (TipoHierarquiaOrganizacional tp : TipoHierarquiaOrganizacional.values()) {
            toReturn.add(new SelectItem(tp, tp.getDescricao()));
        }
        return toReturn;
    }

    public List<HierarquiaOrganizacional> completarHierarquiaOrcamentaria(String parte) {
        if (dataFinal != null) {
            return completarHierarquiaOrcamentaria(parte, dataFinal);
        }
        return completarHierarquiaOrcamentaria(parte, getSistemaFacade().getDataOperacao());
    }

    private List<HierarquiaOrganizacional> completarHierarquiaOrcamentaria(String parte, Date data) {
        if (hierarquiaAdministrativa != null) {
            return hierarquiaOrganizacionalFacade.retornaHierarquiaOrcamentariaPorUnidadeAdministrativa(hierarquiaAdministrativa.getSubordinada(), data);
        }
        return hierarquiaOrganizacionalFacade.filtraPorNivel(parte.trim(), "3", TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), data);
    }

    public List<LocalEstoque> buscarLocalEstoque(String filtro) {
        if (hierarquiaAdministrativa != null) {
            return localEstoqueFacade.completarLocalEstoqueAbertos(filtro, hierarquiaAdministrativa.getSubordinada());
        }
        return localEstoqueFacade.completarLocalEstoqueAbertos(filtro);
    }

    public List<GrupoMaterial> buscarGrupoMaterial(String parte) {
        return grupoMaterialFacade.listaFiltrando(parte.trim());
    }

    public List<LoteMaterial> buscarLoteMaterial(String filtro) {
        return loteMaterialFacade.listaFiltrando(filtro.trim(), "identificacao");
    }

    public List<ObjetoCompra> buscarObjetoCompra(String codigoOrDescricao) {
        return objetoCompraFacade.buscarObjetoCompraPorCodigoOrDescricaoAndTipoObjetoCompra(codigoOrDescricao, TipoObjetoCompra.CONSUMO);
    }

    public List<GrupoObjetoCompra> buscarGrupoObjetoCompraConsumo(String codigoOrDescricao) {
        return objetoCompraFacade.getGrupoObjetoCompraFacade().buscarGrupoObjetoCompraConsumoPorCodigoOrDescricao(codigoOrDescricao);
    }

    public void limparCampos() {
        tipoEstoque = null;
        localEstoque = null;
        material = null;
        grupoMaterial = null;
        grupoObjetoCompra = null;
        hierarquiaAdministrativa = null;
        hierarquiaOrcamentaria = null;
        dataInicial = new Date();
        dataFinal = new Date();
        dataReferencia = new Date();
        filtros.setLength(0);
        condicaoWhere.setLength(0);
        apresentacaoRelatorio = ApresentacaoRelatorio.CONSOLIDADO;
        tipoHierarquiaOrganizacional = TipoHierarquiaOrganizacional.ADMINISTRATIVA;
    }

    public void limparCamposFiltros() {
        /**
         * não adicionar os itens tipo hierarquia e apresentação pois sobreescreve o filtro informado pelo usuario
         */
        filtros.setLength(0);
        condicaoWhere.setLength(0);
    }

    public void definirUnidadeOrcamentariaComoNull() {
        setHierarquiaOrcamentaria(null);
        setHierarquiaAdministrativa(null);
    }

    public Boolean isApresentacaoConsolidado() {
        return ApresentacaoRelatorio.CONSOLIDADO.equals(apresentacaoRelatorio);
    }

    public Future<ByteArrayOutputStream> getFutureBytesRelatorio() {
        return futureBytesRelatorio;
    }

    public void setFutureBytesRelatorio(Future<ByteArrayOutputStream> futureBytesRelatorio) {
        this.futureBytesRelatorio = futureBytesRelatorio;
    }

    public FiltroMateriais getFiltroMateriais() {
        return filtroMateriais;
    }

    public void setFiltroMateriais(FiltroMateriais filtroMateriais) {
        this.filtroMateriais = filtroMateriais;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }
}
