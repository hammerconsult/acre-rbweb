package br.com.webpublico.controle;

import br.com.webpublico.controle.contabil.reprocessamento.AbstractReprocessamentoHistoricoControlador;
import br.com.webpublico.entidades.GrupoBem;
import br.com.webpublico.entidades.GrupoMaterial;
import br.com.webpublico.entidadesauxiliares.AssistenteReprocessamento;
import br.com.webpublico.enums.TipoReprocessamentoHistorico;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.negocios.ReprocessamentoSaldoBensFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
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
 * Date: 24/04/15
 * Time: 07:56
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean(name = "reprocessamentoSaldoGrupoBemGrupoMaterialControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-reprocessamento-bens", pattern = "/reprocessamento-saldo-bens/novo/", viewId = "/faces/financeiro/orcamentario/reprocessamento-saldo-bens/edita.xhtml"),
    @URLMapping(id = "listar-reprocessamento-bens", pattern = "/reprocessamento-saldo-bens/listar/", viewId = "/faces/financeiro/orcamentario/reprocessamento-saldo-bens/lista.xhtml"),
    @URLMapping(id = "ver-reprocessamento-bens", pattern = "/reprocessamento-saldo-bens/ver/#{reprocessamentoSaldoGrupoBemGrupoMaterialControlador.id}/", viewId = "/faces/financeiro/orcamentario/reprocessamento-saldo-bens/visualizar.xhtml")
})
public class ReprocessamentoSaldoGrupoBemGrupoMaterialControlador extends AbstractReprocessamentoHistoricoControlador implements Serializable {
    @EJB
    private ReprocessamentoSaldoBensFacade reprocessamentoSaldoGrupoBemFacade;
    private GrupoBem grupoBem;
    private GrupoMaterial grupoMaterial;
    private TipoSaldo tipoSaldo;

    public ReprocessamentoSaldoGrupoBemGrupoMaterialControlador() {
    }

    @URLAction(mappingId = "novo-reprocessamento-bens", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        assistente = new AssistenteReprocessamento();
        tipoSaldo = TipoSaldo.BENS_MOVEIS;
    }

    @URLAction(mappingId = "ver-reprocessamento-bens", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.recuperarObjeto();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/reprocessamento-saldo-bens/";
    }

    @Override
    protected TipoReprocessamentoHistorico getTipo() {
        return TipoReprocessamentoHistorico.SALDO_GRUPO_BEM_GRUPO_MATERIAL;
    }

    private String getFiltros() {
        StringBuilder stb = new StringBuilder();
        String concat = " and ";
        if (grupoBem != null) {
            stb.append(concat).append(" g.id = ").append(grupoBem.getId());
            assistente.getHistorico().setFiltrosUtilizados("Grupo Patrimonial: " + grupoBem.toString());
        }
        if (grupoMaterial != null) {
            stb.append(concat).append(" g.id = ").append(grupoMaterial.getId());
            assistente.getHistorico().setFiltrosUtilizados("Grupo Material: " + grupoMaterial.toString());
        }
        return stb.toString();
    }

    public List<GrupoBem> completaGrupoBem(String parte) {
        try {
            return reprocessamentoSaldoGrupoBemFacade.getGrupoBemFacade().listaFiltrandoGrupoBemCodigoDescricao(parte.trim());
        } catch (Exception ex) {
            return new ArrayList<>();
        }
    }

    public List<GrupoMaterial> completaGrupoMaterial(String parte) {
        try {
            return reprocessamentoSaldoGrupoBemFacade.getGrupoMaterialFacade().listaFiltrandoGrupoDeMaterialAtivo(parte.trim());
        } catch (Exception ex) {
            return new ArrayList<>();
        }
    }

    public List<SelectItem> getTiposSaldos() {
        return Util.getListSelectItemSemCampoVazio(TipoSaldo.values());
    }

    public Boolean renderizarGrupoBem() {
        return tipoSaldo.isSaldoBensMoveis() || tipoSaldo.isSaldoBensImoveis() || tipoSaldo.isSaldoBensIntangiveis();
    }

    public Boolean renderizarGrupoMaterial() {
        return tipoSaldo.isSaldoBensEstoque();
    }

    public void reprocessar() {
        try {
            inicializarAssistente();
            assistente.setFiltro(getFiltros());
            future = reprocessamentoSaldoGrupoBemFacade.reprocessar(assistente, tipoSaldo);
            FacesUtil.executaJavaScript("dialogProgressBar.show()");
            FacesUtil.executaJavaScript("poll.start()");
            FacesUtil.atualizarComponente("formDialogProgressBar");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
            FacesUtil.executaJavaScript("aguarde.hide()");
        } catch (Exception ex) {
            FacesUtil.executaJavaScript("aguarde.hide()");
            FacesUtil.addErrorGenerico(ex);
        }
    }

    public enum TipoSaldo implements EnumComDescricao {
        BENS_MOVEIS("Bens Móveis"),
        BENS_IMOVEIS("Bens Imóveis"),
        BENS_INTANGIVEIS("Bens Intangíveis"),
        BENS_ESTOQUE("Bens Estoque");
        private String descricao;

        TipoSaldo(String descricao) {
            this.descricao = descricao;
        }

        @Override
        public String getDescricao() {
            return descricao;
        }

        public boolean isSaldoBensEstoque() {
            return TipoSaldo.BENS_ESTOQUE.equals(this);
        }

        public boolean isSaldoBensMoveis() {
            return TipoSaldo.BENS_MOVEIS.equals(this);
        }

        public boolean isSaldoBensImoveis() {
            return TipoSaldo.BENS_IMOVEIS.equals(this);
        }

        public boolean isSaldoBensIntangiveis() {
            return TipoSaldo.BENS_INTANGIVEIS.equals(this);
        }
    }

    public GrupoBem getGrupoBem() {
        return grupoBem;
    }

    public void setGrupoBem(GrupoBem grupoBem) {
        this.grupoBem = grupoBem;
    }

    public TipoSaldo getTipoSaldo() {
        return tipoSaldo;
    }

    public void setTipoSaldo(TipoSaldo tipoSaldo) {
        this.tipoSaldo = tipoSaldo;
    }

    public GrupoMaterial getGrupoMaterial() {
        return grupoMaterial;
    }

    public void setGrupoMaterial(GrupoMaterial grupoMaterial) {
        this.grupoMaterial = grupoMaterial;
    }
}
