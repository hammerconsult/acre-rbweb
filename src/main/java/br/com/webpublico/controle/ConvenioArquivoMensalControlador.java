package br.com.webpublico.controle;

import br.com.webpublico.entidades.Arquivo;
import br.com.webpublico.entidades.ConvenioArquivoMensal;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.TipoConvenioArquivoMensal;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ConvenioArquivoMensalFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.model.StreamedContent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-convenio-arquivo-mensal", pattern = "/convenio/arquivo-mensal/novo/", viewId = "/faces/financeiro/convenios/arquivo/edita.xhtml"),
    @URLMapping(id = "ver-convenio-arquivo-mensal", pattern = "/convenio/arquivo-mensal/ver/#{convenioArquivoMensalControlador.id}/", viewId = "/faces/financeiro/convenios/arquivo/visualizar.xhtml"),
    @URLMapping(id = "listar-convenio-arquivo-mensal", pattern = "/convenio/arquivo-mensal/listar/", viewId = "/faces/financeiro/convenios/arquivo/lista.xhtml")
})
public class ConvenioArquivoMensalControlador extends PrettyControlador<ConvenioArquivoMensal> implements Serializable, CRUD {
    @EJB
    private ConvenioArquivoMensalFacade facade;

    public ConvenioArquivoMensalControlador() {
        super(ConvenioArquivoMensal.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/convenio/arquivo-mensal/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @URLAction(mappingId = "novo-convenio-arquivo-mensal", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setTipo(TipoConvenioArquivoMensal.CONVENIO_DESPESA);
        selecionado.setExercicio(facade.getExercicioCorrente());
        selecionado.setMes(Mes.getMesToInt(DataUtil.getMes(facade.getDataOperacao())));
    }

    @URLAction(mappingId = "ver-convenio-arquivo-mensal", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    public List<SelectItem> getTipos() {
        return Util.getListSelectItemSemCampoVazio(TipoConvenioArquivoMensal.values());
    }

    public List<SelectItem> getMeses() {
        return Util.getListSelectItemSemCampoVazio(Mes.values(), false);
    }

    public void gerarArquivo() {
        try {
            Arquivo arquivo = facade.gerarArquivo(selecionado);
            selecionado.setArquivo(arquivo);
        } catch (Exception ex) {
            logger.error("Erro ao gerar o arquivo ", ex);
            FacesUtil.addOperacaoNaoRealizada("Não foi possível gerar o arquivo. Detlhes: " + ex.getMessage());
        }
    }

    public StreamedContent downloadArquivo() {
        try {
            return facade.downloadArquivo(selecionado);
        } catch (Exception ex) {
            logger.error("Erro ao fazer o download do arquivo ", ex);
            FacesUtil.addOperacaoNaoRealizada("Não foi possível fazer o download do arquivo. Detlhes: " + ex.getMessage());
        }
        return null;
    }

    public void limparArquivo() {
        selecionado.setArquivo(null);
    }
}
