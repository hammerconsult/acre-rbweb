package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.Funeraria;
import br.com.webpublico.entidadesauxiliares.DemonstrativoAuxilioFuneral;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.relatoriofacade.DemonstrativoAuxilioFuneralFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import com.google.common.collect.Maps;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Criado por Mateus
 * Data: 15/05/2017.
 */
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "demonstrativo-auxilio-funeral", pattern = "/relatorio/demonstrativo-auxilio-funeral/", viewId = "/faces/funeral/relatorio/demonstrativoauxiliofuneral.xhtml")
})
@ManagedBean
public class DemonstrativoAuxilioFuneralControlador extends AbstractReport {

    @EJB
    private DemonstrativoAuxilioFuneralFacade demonstrativoAuxilioFuneralFacade;
    private Date dataInicial;
    private Date dataFinal;
    private String nomeFalecido;
    private Funeraria funeraria;
    private String cemiterio;
    private String filtros;

    public DemonstrativoAuxilioFuneralControlador() {
    }

    @URLAction(mappingId = "demonstrativo-auxilio-funeral", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        dataInicial = new Date();
        dataFinal = new Date();
        nomeFalecido = "";
        funeraria = null;
        cemiterio = "";
    }

    public List<Funeraria> completarFunerarias(String filtro) {
        return demonstrativoAuxilioFuneralFacade.getFunerariaFacade().listaFiltrando(filtro.trim(), "descricao");
    }

    public void gerarRelatorio() {
        try {
            validarCampos();
            String arquivoJasper = "RelatorioDemonstrativoAuxilioFuneral.jasper";
            HashMap parametros = Maps.newHashMap();
            JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(buscarDados());
            parametros.put("FILTROS", filtros.trim());
            parametros.put("MODULO", "Funeral");
            parametros.put("BRASAO", getCaminhoImagem());
            parametros.put("USUARIO", demonstrativoAuxilioFuneralFacade.getSistemaFacade().getUsuarioCorrente().getNome());
            gerarRelatorioComDadosEmCollection(arquivoJasper, parametros, ds);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (dataInicial == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data Inicial deve ser informado!");
        }
        if (dataFinal == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data Final deve ser informado!");
        }
        ve.lancarException();
        if (dataFinal.before(dataInicial)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A data inicial deve ser anterior ou igual a data final!");
        }
        ve.lancarException();
    }

    private List<DemonstrativoAuxilioFuneral> buscarDados() {
        filtros = "";
        String sql = "";
        String clausula = " where ";
        sql += clausula + " trunc(aux.datadoatendimento) between to_date('" + DataUtil.getDataFormatada(dataInicial) +
            "', 'dd/MM/yyyy') and to_date('" + DataUtil.getDataFormatada(dataFinal) + "', 'dd/MM/yyyy') ";
        clausula = " and ";
        filtros = "Data Inicial: " + DataUtil.getDataFormatada(dataInicial) + "; Data Final: " + DataUtil.getDataFormatada(dataFinal) + "; ";
        if (!nomeFalecido.trim().isEmpty()) {
            sql += clausula + " upper(aux.nomefalecido) like '%" + nomeFalecido.trim().toUpperCase() + "%' ";
            filtros += "Nome do Falecido: " + nomeFalecido.trim() + "; ";
        }
        if (funeraria != null) {
            sql += clausula + " fun.id = " + funeraria.getId();
            filtros += "Funerária: " + funeraria.getDescricao() + "; ";
        }
        if (!cemiterio.trim().isEmpty()) {
            sql += clausula + " upper(aux.cemiterio) like '%" + cemiterio.trim().toUpperCase() + "%' ";
            filtros += "Cemitério: " + cemiterio.trim() + "; ";
        }
        return demonstrativoAuxilioFuneralFacade.buscarDados(sql);
    }

    public String getFiltros() {
        return filtros;
    }

    public void setFiltros(String filtros) {
        this.filtros = filtros;
    }

    public String getCemiterio() {
        return cemiterio;
    }

    public void setCemiterio(String cemiterio) {
        this.cemiterio = cemiterio;
    }

    public Funeraria getFuneraria() {
        return funeraria;
    }

    public void setFuneraria(Funeraria funeraria) {
        this.funeraria = funeraria;
    }

    public String getNomeFalecido() {
        return nomeFalecido;
    }

    public void setNomeFalecido(String nomeFalecido) {
        this.nomeFalecido = nomeFalecido;
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
}
