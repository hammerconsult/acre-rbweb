package br.com.webpublico.controlerelatorio.rh;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.VinculoFP;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.TipoFolhaDePagamento;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.negocios.ContratoFPFacade;
import br.com.webpublico.negocios.FolhaDePagamentoFacade;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.negocios.VinculoFPFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.UtilRH;
import com.google.common.collect.Maps;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @Author peixe on 09/02/2017  13:23.
 */
public class RelatorioPagamentoRH extends AbstractReport {

    protected static final Logger logger = LoggerFactory.getLogger(RelatorioPagamentoRH.class);

    private TipoFolhaDePagamento tipoFolhaDePagamento;
    private Integer mes;
    private Integer ano;
    private Integer versao;

    protected JRBeanCollectionDataSource jrBeanCollection;
    protected HashMap parameters;
    protected HierarquiaOrganizacional hierarquiaOrganizacional;
    protected List<ParametrosRelatorios> filters;

    @EJB
    private FolhaDePagamentoFacade folhaDePagamentoFacade;
    @EJB
    private VinculoFPFacade vinculoFPFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;

    public RelatorioPagamentoRH() {
        this.parameters = Maps.newHashMap();
    }

    public TipoFolhaDePagamento getTipoFolhaDePagamento() {
        return tipoFolhaDePagamento;
    }

    public void setTipoFolhaDePagamento(TipoFolhaDePagamento tipoFolhaDePagamento) {
        this.tipoFolhaDePagamento = tipoFolhaDePagamento;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public void setHierarquiaOrganizacionalFacade(HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade) {
        this.hierarquiaOrganizacionalFacade = hierarquiaOrganizacionalFacade;
    }

    public List<HierarquiaOrganizacional> buscarHierarquiaOrganizacionalSegundoNivelAdministrativa(String filter) {
        return getHierarquiaOrganizacionalFacade().filtraNivelDoisEComRaiz(filter, TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), UtilRH.getDataOperacao());
    }

    public JRBeanCollectionDataSource getJrBeanCollection() {
        return jrBeanCollection;
    }

    public void setJrBeanCollection(JRBeanCollectionDataSource jrBeanCollection) {
        this.jrBeanCollection = jrBeanCollection;
    }

    public HashMap getParameters() {
        return parameters;
    }

    public void setParameters(HashMap parameters) {
        this.parameters = parameters;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public List<ParametrosRelatorios> getFilters() {
        return filters;
    }

    public void setFilters(List<ParametrosRelatorios> filters) {
        this.filters = filters;
    }

    public FolhaDePagamentoFacade getFolhaDePagamentoFacade() {
        return folhaDePagamentoFacade;
    }

    public void setFolhaDePagamentoFacade(FolhaDePagamentoFacade folhaDePagamentoFacade) {
        this.folhaDePagamentoFacade = folhaDePagamentoFacade;
    }

    public VinculoFPFacade getVinculoFPFacade() {
        return vinculoFPFacade;
    }

    public void setVinculoFPFacade(VinculoFPFacade vinculoFPFacade) {
        this.vinculoFPFacade = vinculoFPFacade;
    }

    public ContratoFPFacade getContratoFPFacade() {
        return contratoFPFacade;
    }

    public void setContratoFPFacade(ContratoFPFacade contratoFPFacade) {
        this.contratoFPFacade = contratoFPFacade;
    }

    public List<SelectItem> getMeses() {
        return Util.getListSelectItem(Mes.values(), false);
    }

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public Integer getVersao() {
        return versao;
    }

    public void setVersao(Integer versao) {
        this.versao = versao;
    }

    public String montarDescricaoVersao() {
        return versao != null ? " Versão " + versao : " ";
    }

    public void limparCampos() {
        this.versao = null;
        this.ano = getExercicioCorrente().getAno();
        this.mes = buscarMesCorrente();

    }

    public Integer buscarMesCorrente() {
        return DataUtil.getMes(getSistemaFacade().getDataOperacao());
    }

    public List<SelectItem> getTiposFolha() {
        return Util.getListSelectItem(TipoFolhaDePagamento.values(), false);
    }

    public List<VinculoFP> completaContrato(String s) {
        return contratoFPFacade.buscaContratoFiltrandoAtributosVinculoMatriculaFP(s.trim());
    }


    public String montarCampoVersao() {
        if (versao != null) {
            return " and folha.versao = " + versao;
        }
        return " ";
    }

    public List<SelectItem> getVersoes() {
        List<SelectItem> retorno = new ArrayList<>();
        if (mes != null && ano != null && tipoFolhaDePagamento != null) {
            retorno.add(new SelectItem(null, ""));
            for (Integer versao : folhaDePagamentoFacade.recuperarVersao(Mes.getMesToInt(mes), ano, tipoFolhaDePagamento)) {
                retorno.add(new SelectItem(versao, String.valueOf(versao)));
            }
        }
        return retorno;
    }
}
