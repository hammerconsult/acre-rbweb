package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.TipoProvimento;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.negocios.RelatorioServidoresPorProvimentoFacade;
import br.com.webpublico.negocios.TipoProvimentoFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.UtilRH;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

/**
 * Created by carlos on 25/01/16.
 */
@ManagedBean(name = "relatorioServidoresPorProvimentoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorioServidoresPorProvimento", pattern = "/rh/servidores-por-provimento", viewId = "/faces/rh/relatorios/relatorioservidoresporprovimento.xhtml")
})
public class RelatorioServidoresPorProvimento extends AbstractReport implements Serializable {

    private TipoProvimento tipoProvimento;
    private String unidade;
    private Date dataInicial;
    private Date dataFinal;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private Boolean todosOrgaos;
    private String filtros;
    @EJB
    private TipoProvimentoFacade tipoProvimentoFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private RelatorioServidoresPorProvimentoFacade relatorioServidoresPorProvimentoFacade;
    public static final String ARQUIVO_JASPER = "RelatorioServidoresPorProvimento.jasper";

    @URLAction(mappingId = "relatorioServidoresPorProvimento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        geraNoDialog = true;
        tipoProvimento = null;
        dataInicial = null;
        dataFinal = null;
        setTodosOrgaos(true);
        hierarquiaOrganizacional = null;
    }

    public List<HierarquiaOrganizacional> completarHierarquiaOrganizacional(String parte) {
        return getHierarquiaOrganizacionalFacade().filtraPorNivel(parte.trim(), "2", TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), UtilRH.getDataOperacao());
    }

    public void gerarRelatorioServidoresPorProvimento() throws JRException, IOException {
        if (validarCampos()) {
            JRBeanCollectionDataSource jrbcDadosServidor = recuperarDadosServidorJRBeanCollections();
            HashMap parameter = new HashMap();
            parameter.put("IMAGEM", super.getCaminhoImagem());
            parameter.put("FILTROS", tipoProvimento.getDescricao().toUpperCase());

            try {
                gerarRelatorioComDadosEmCollection(ARQUIVO_JASPER, parameter, jrbcDadosServidor);
            } catch (Exception e) {
                FacesUtil.addErroAoGerarRelatorio(e.getMessage());
            }

        }
    }

    private JRBeanCollectionDataSource recuperarDadosServidorJRBeanCollections() {
        return recuperarDadosServidorPorProvimento();
    }

    private JRBeanCollectionDataSource recuperarDadosServidorPorProvimento() {
        return new JRBeanCollectionDataSource(relatorioServidoresPorProvimentoFacade.buscarDadosServidoresPorProvimento(hierarquiaOrganizacional, dataInicial, dataFinal, tipoProvimento));
    }

    public Boolean validarCampos() {
        Boolean retorno = true;
        if (tipoProvimento == null) {
            retorno = false;
            FacesUtil.addCampoObrigatorio("Campo Tipo de Provimento deve ser selecionado.");
        }
        if (dataInicial == null) {
            retorno = false;
            FacesUtil.addCampoObrigatorio("Campo Data Inicial deve ser selecionado.");
        }
        if (dataFinal == null) {
            retorno = false;
            FacesUtil.addCampoObrigatorio("Campo Data Final deve ser selecionado.");
        }
        if (!getTodosOrgaos() && hierarquiaOrganizacional == null) {
            retorno = false;
            FacesUtil.addCampoObrigatorio("Selecione um órgão ou clique em todos para gerar de todos os órgãos.");
        }
        if (dataInicial != null && dataFinal != null && dataFinal.before(dataInicial)) {
            retorno = false;
            FacesUtil.addOperacaoNaoPermitida("A Data Inicial deve ser inferior ou igual a Data Final.");
        }
        return retorno;
    }

    public List<SelectItem> buscarItensTipoProvimento() {
        List<SelectItem> retorno = new ArrayList<SelectItem>();
        retorno.add(new SelectItem(null, ""));

        List<TipoProvimento> lista = getTipoProvimentoFacade().lista();
        Collections.sort(lista, new Comparator<TipoProvimento>() {
            @Override
            public int compare(TipoProvimento o1, TipoProvimento o2) {
                return o1.getCodigo().compareTo(o2.getCodigo());
            }
        });
        for (TipoProvimento tipo : lista) {
            retorno.add(new SelectItem(tipo, tipo.getCodigo().toString() + " - " + tipo.getDescricao()));
        }
        return retorno;
    }

    public void verificarOrgaoSelecionado() {
        if (getTodosOrgaos()) {
            hierarquiaOrganizacional = null;
        } else {
            hierarquiaOrganizacional = new HierarquiaOrganizacional();
        }
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    public Date getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(Date dataInicial) {
        this.dataInicial = dataInicial;
    }

    public TipoProvimento getTipoProvimento() {
        return tipoProvimento;
    }

    public void setTipoProvimento(TipoProvimento tipoProvimento) {
        this.tipoProvimento = tipoProvimento;
    }

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public Boolean getTodosOrgaos() {
        return todosOrgaos;
    }

    public void setTodosOrgaos(Boolean todosOrgaos) {
        this.todosOrgaos = todosOrgaos;
    }

    public TipoProvimentoFacade getTipoProvimentoFacade() {
        return tipoProvimentoFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public String getFiltros() {
        return filtros;
    }

    public void setFiltros(String filtros) {
        this.filtros = filtros;
    }
}
