package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.Cargo;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.relatoriofacade.ExportacaoServidoresAtivosFacade;
import br.com.webpublico.util.ExcelUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.UtilRH;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import org.primefaces.model.StreamedContent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMapping(id = "exportacao-servidores-ativos", pattern = "/exportacao-servidores-ativos/", viewId = "/faces/rh/relatorios/exportacaoservidoresativos.xhtml")
public class ExportacaoServidoresAtivosControlador {

    @EJB
    private ExportacaoServidoresAtivosFacade exportacaoServidoresAtivosFacade;
    private Cargo cargo;
    private HierarquiaOrganizacional hierarquiaOrganizacional;

    @URLAction(mappingId = "exportacao-servidores-ativos", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        cargo = null;
        hierarquiaOrganizacional = null;
    }

    public StreamedContent fileDownload() {
        try {
            List<String> titulos = Lists.newArrayList();
            titulos.add("Nome");
            titulos.add("Cpf");
            titulos.add("Órgão");
            titulos.add("Cargo");
            titulos.add("Carga Horária");
            titulos.add("Afastamento");
            titulos.add("Possui Função Gratificada");
            ExcelUtil excel = new ExcelUtil();
            excel.gerarExcel("Servidores Ativos", "servidores-ativos", titulos, exportacaoServidoresAtivosFacade.buscarServidores(montarFiltros()), "", false);
            return excel.fileDownload();
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
        return null;
    }

    private String montarFiltros() {
        String retorno = "";
        if (cargo != null) {
            retorno += " and cargo.id = " + cargo.getId();
        }
        if (hierarquiaOrganizacional != null) {
            retorno += " and vw.codigo like '" + hierarquiaOrganizacional.getCodigoSemZerosFinais() + "%' ";
        }
        return retorno;
    }

    public List<HierarquiaOrganizacional> completarHierarquias(String parte) {
        return exportacaoServidoresAtivosFacade.getHierarquiaOrganizacionalFacade().filtraNivelDoisEComRaiz(parte.trim(), TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), UtilRH.getDataOperacao());
    }

    public List<Cargo> completarCargos(String filtro) {
        return exportacaoServidoresAtivosFacade.getCargoFacade().buscarCargosVigentesPorUnidadeOrganizacionalAndUsuario(filtro.trim());
    }

    public Cargo getCargo() {
        return cargo;
    }

    public void setCargo(Cargo cargo) {
        this.cargo = cargo;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }
}
