package br.com.webpublico.controlerelatorio.rh;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.FiltroBaseFP;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.TipoFolhaDePagamento;
import br.com.webpublico.enums.rh.relatorios.TipoIncidenciaVerba;
import br.com.webpublico.negocios.FolhaDePagamentoFacade;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import javax.naming.NamingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

@ManagedBean
@ViewScoped
public class SuperRelatorioRH extends AbstractReport {
    protected static final Logger logger = LoggerFactory.getLogger(SuperRelatorioRH.class);
    private HashMap parameters;
    private FiltroBaseFP filtroBaseFP;
    private TipoIncidenciaVerba tipoIncidenciaVerba;
    private JRBeanCollectionDataSource dataSource;
    private Collection<?> objetos;
    private List<ParametrosRelatorios> filtros;
    private String criterioUtilizado;
    private TipoFolhaDePagamento tipoFolhaDePagamento;
    private Integer ano;
    private Mes mes;
    private Integer versao;
    @EJB
    private FolhaDePagamentoFacade folhaDePagamentoFacade;


    public SuperRelatorioRH() {
        this.setParameters(Maps.newHashMap());
    }

    public void limpar() {
        this.setParameters(Maps.newHashMap());
    }

    public void definirDataSource(Collection objetos) {
        this.setDataSource(new JRBeanCollectionDataSource((Collection<?>) objetos));
    }


    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public Mes getMes() {
        return mes;
    }

    public void setMes(Mes mes) {
        this.mes = mes;
    }

    public HashMap getParameters() {
        return parameters;
    }

    public void setParameters(HashMap parameters) {
        this.parameters = parameters;
    }

    public FiltroBaseFP getFiltroBaseFP() {
        return filtroBaseFP;
    }

    public void setFiltroBaseFP(FiltroBaseFP filtroBaseFP) {
        this.filtroBaseFP = filtroBaseFP;
    }

    public TipoIncidenciaVerba getTipoIncidenciaVerba() {
        return tipoIncidenciaVerba;
    }

    public void setTipoIncidenciaVerba(TipoIncidenciaVerba tipoIncidenciaVerba) {
        this.tipoIncidenciaVerba = tipoIncidenciaVerba;
    }

    public JRBeanCollectionDataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(JRBeanCollectionDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Collection<?> getObjetos() {
        return objetos;
    }

    public void setObjetos(Collection<?> objetos) {
        this.objetos = objetos;
    }

    public List<ParametrosRelatorios> getFiltros() {
        return filtros;
    }

    public void setFiltros(List<ParametrosRelatorios> filtros) {
        this.filtros = filtros;
    }

    public String getCriterioUtilizado() {
        return criterioUtilizado;
    }

    public void setCriterioUtilizado(String criterioUtilizado) {
        this.criterioUtilizado = criterioUtilizado;
    }

    public TipoFolhaDePagamento getTipoFolhaDePagamento() {
        return tipoFolhaDePagamento;
    }

    public void setTipoFolhaDePagamento(TipoFolhaDePagamento tipoFolhaDePagamento) {
        this.tipoFolhaDePagamento = tipoFolhaDePagamento;
    }

    public List<SelectItem> getTiposFolha() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, " "));
        for (TipoFolhaDePagamento tipo : TipoFolhaDePagamento.values()) {
            retorno.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return retorno;
    }

    public List<SelectItem> getVersoes() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, " "));
        if (mes != null && ano != null && tipoFolhaDePagamento != null) {
            for (Integer versaoFolha : folhaDePagamentoFacade.recuperarVersao(mes, ano, tipoFolhaDePagamento)) {
                retorno.add(new SelectItem(versaoFolha, String.valueOf(versaoFolha)));
            }
        }
        return retorno;
    }

    public List<SelectItem> getMeses() {
        List<SelectItem> retorno = Lists.newArrayList();
        retorno.add(new SelectItem(null, ""));
        for (Mes meses : Mes.values()) {
            retorno.add(new SelectItem(meses, meses.getDescricao()));
        }
        return retorno;
    }

    public Integer getVersao() {
        return versao;
    }

    public void setVersao(Integer versao) {
        this.versao = versao;
    }

    public void limparCampos() {
        try {
            tipoFolhaDePagamento = null;
            ano = getSistemaFacadeSemInjetar().getExercicioCorrente().getAno();
            mes = null;
            versao = null;
        } catch (NamingException e) {
            logger.error(e.getMessage());
        }
    }
}
