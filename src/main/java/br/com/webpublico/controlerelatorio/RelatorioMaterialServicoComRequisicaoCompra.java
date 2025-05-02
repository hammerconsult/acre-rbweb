/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.ObjetoCompra;
import br.com.webpublico.entidades.RequisicaoDeCompra;
import br.com.webpublico.entidades.UnidadeOrganizacional;
import br.com.webpublico.enums.TipoObjetoCompra;
import br.com.webpublico.negocios.ObjetoCompraFacade;
import br.com.webpublico.negocios.RequisicaoDeCompraFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Limpavel;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.JRException;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * @author Sarruf
 */
@ViewScoped
@ManagedBean

@URLMappings(mappings = {
        @URLMapping(id = "novo-relatorio-material-servico-requisicao-compra", pattern = "/licitacao/relatorio-material-servico-requisicao-compra/", viewId = "/faces/administrativo/relatorios/relatoriomaterialservicoporrequisicaocompra.xhtml")
})
public class RelatorioMaterialServicoComRequisicaoCompra extends AbstractReport implements Serializable {

    @Limpavel(Limpavel.NULO)
    private RequisicaoDeCompra requisicaoDeCompra;
    @Limpavel(Limpavel.NULO)
    private ObjetoCompra objetoCompra;
    private String filtros;
    @Limpavel(Limpavel.NULO)
    private UnidadeOrganizacional unidadeOrganizacional;
    @EJB
    private RequisicaoDeCompraFacade requisicaoDeCompraFacade;
    @EJB
    private ObjetoCompraFacade objetoCompraFacade;

    public RequisicaoDeCompra getRequisicaoDeCompra() {
        return requisicaoDeCompra;
    }

    public void setRequisicaoDeCompra(RequisicaoDeCompra requisicaoDeCompra) {
        this.requisicaoDeCompra = requisicaoDeCompra;
    }

    public ObjetoCompra getObjetoCompra() {
        return objetoCompra;
    }

    public void setObjetoCompra(ObjetoCompra objetoCompra) {
        this.objetoCompra = objetoCompra;
    }

    public String getFiltros() {
        return filtros;
    }

    public void setFiltros(String filtros) {
        this.filtros = filtros;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public List<ObjetoCompra> buscarObjetoCompra(String codigoOrDescricao) {
        List<ObjetoCompra> retorno = objetoCompraFacade.buscarObjetoCompraPorDescricaoOrcodigoAndListTipoObjetoCompra(codigoOrDescricao, TipoObjetoCompra.getTiposObjetoCompraMaterial());
        if (retorno != null && retorno.isEmpty()) {
            FacesUtil.addAtencao("Nenhum objeto de compra encontrado.");
            return null;
        }
        return retorno;
    }

    @URLAction(mappingId = "novo-relatorio-material-servico-requisicao-compra", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        Util.limparCampos(this);
        limparFiltros();
    }

    public void geraRelatorio() throws JRException, IOException {
        String arquivoJasper = "Relatorio_material_servico_RequisicaoCompra.jasper";
        HashMap parametros = new HashMap();

        parametros.put("BRASAO", getCaminhoImagem());
        parametros.put("SECRETARIA", "SECRETARIA MUNICIPAL DE DESENVOLVIMENTO ECONÔMICO E FINANÇAS");
        parametros.put("NOMERELATORIO", "Relatório Material com Requisição de Compra");
        parametros.put("MODULO", "Administrativo");
        parametros.put("USUARIO", getSistemaFacade().getUsuarioCorrente().getNome());
        parametros.put("WHERE", montarCondicao());
        parametros.put("FILTROS", filtros);

        gerarRelatorio(arquivoJasper, parametros);
    }

    private String montarCondicao() {
        StringBuilder sb = new StringBuilder(" WHERE 1 = 1 ");
        String complemento = " and ";
        limparFiltros();

        if (requisicaoDeCompra != null) {
            sb.append(complemento).append(" rdc.id = ").append(requisicaoDeCompra.getId());
            filtros += "Requisição de Compra: " + requisicaoDeCompra.getDescricao() + " ";
        }
        if (objetoCompra != null) {
            sb.append(complemento).append(" obj.id = ").append(objetoCompra.getId());
            filtros += "Item: " + objetoCompra.getDescricao() + " ";
        }

        return sb.toString();
    }

    private void limparFiltros() {
        this.filtros = "";
    }

    public void cancelarLimparCampos() {
        Util.limparCampos(this);
    }

    public List<RequisicaoDeCompra> completaRequisicaoDeCompra(String parte) {
        return requisicaoDeCompraFacade.listaDecrescente();
    }
}
