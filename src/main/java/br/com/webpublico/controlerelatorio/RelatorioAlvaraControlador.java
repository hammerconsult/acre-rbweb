/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.CadastroEconomico;
import br.com.webpublico.enums.TipoAlvara;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author gustavo
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoListagemalvara", pattern = "/tributario/alvara/relatorios/listagemalvara/", viewId = "/faces/tributario/alvara/relatorios/listagemalvara.xhtml")})
public class RelatorioAlvaraControlador extends AbstractReport implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(RelatorioAlvaraControlador.class);

    private TipoAlvara filtroTipoAlvara;
    private CadastroEconomico filtroCmcInicial;
    private CadastroEconomico filtroCmcFinal;
    private Date dataInicialAbertura;
    private Date dataFinalAbertura;
    private Date dataInicialEmissao;
    private Date dataFinalEmissao;

    @URLAction(mappingId = "novoListagemalvara", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        filtroCmcInicial = null;
        filtroCmcFinal = null;
        filtroTipoAlvara = null;
        dataFinalEmissao = null;
        dataInicialEmissao = null;
        dataInicialAbertura = null;
        dataFinalAbertura = null;
    }

    public Date getDataFinalAbertura() {
        return dataFinalAbertura;
    }

    public void setDataFinalAbertura(Date dataFinalAbertura) {
        this.dataFinalAbertura = dataFinalAbertura;
    }

    public Date getDataFinalEmissao() {
        return dataFinalEmissao;
    }

    public void setDataFinalEmissao(Date dataFinalEmissao) {
        this.dataFinalEmissao = dataFinalEmissao;
    }

    public Date getDataInicialAbertura() {
        return dataInicialAbertura;
    }

    public void setDataInicialAbertura(Date dataInicialAbertura) {
        this.dataInicialAbertura = dataInicialAbertura;
    }

    public Date getDataInicialEmissao() {
        return dataInicialEmissao;
    }

    public void setDataInicialEmissao(Date dataInicialEmissao) {
        this.dataInicialEmissao = dataInicialEmissao;
    }

    public CadastroEconomico getFiltroCmcInicial() {
        return filtroCmcInicial;
    }

    public void setFiltroCmcInicial(CadastroEconomico filtroCmcInicial) {
        this.filtroCmcInicial = filtroCmcInicial;
    }

    public CadastroEconomico getFiltroCmcFinal() {
        return filtroCmcFinal;
    }

    public void setFiltroCmcFinal(CadastroEconomico filtroCmcFinal) {
        this.filtroCmcFinal = filtroCmcFinal;
    }


    public TipoAlvara getFiltroTipoAlvara() {
        return filtroTipoAlvara;
    }

    public void setFiltroTipoAlvara(TipoAlvara filtroTipoAlvara) {
        this.filtroTipoAlvara = filtroTipoAlvara;
    }

    public void imprime() {


        if (filtroCmcInicial == null
            && filtroCmcFinal == null
            && filtroTipoAlvara == null
            && dataInicialEmissao == null
            && dataFinalEmissao == null
            && dataInicialAbertura == null
            && dataFinalAbertura == null) {
            FacesUtil.addError("Impossível continuar", "Informe ao menos um filtro");
        } else {
            String juncao = " where ";
            StringBuilder where = new StringBuilder();
            StringBuilder filtroUtilizado = new StringBuilder();
            String caminhoBrasao = getCaminhoImagem();
            if (filtroCmcInicial != null) {
                where.append(juncao).append(" ce.inscricaocadastral >= ").append(filtroCmcInicial.getInscricaoCadastral());
                juncao = " and ";
                filtroUtilizado.append("<b>CMC INICIAL: </b>").append(filtroCmcInicial).append("; ");
            }
            if (filtroCmcFinal != null) {
                where.append(juncao).append(" ce.inscricaocadastral <= ").append(filtroCmcFinal.getInscricaoCadastral());
                juncao = " and ";
                filtroUtilizado.append("<b>CMC FINAL: </b>").append(filtroCmcFinal).append("; ");

            }
            if (filtroCmcFinal == null) {
                where.append(juncao).append(" ce.inscricaocadastral <= 999999999");
                juncao = " and ";
            }
            if (filtroTipoAlvara != null) {
                where.append(juncao).append(" alvara.tipoalvara = '").append(filtroTipoAlvara).append("'");
                juncao = " and ";
                filtroUtilizado.append("<b>TIPO DO ALVARÁ: </b>").append(filtroTipoAlvara).append("; ");
            }
            if (dataInicialEmissao != null) {
                where.append(juncao).append(" alvara.emissao >=").append(formataData(dataInicialEmissao));
                juncao = " and ";
                filtroUtilizado.append("<b>DATA EMISSÃO (INICIAL): </b>").append(formataData(dataInicialEmissao).replace("'", "")).append("; ");
            }
            if (dataFinalEmissao != null) {
                where.append(juncao).append(" alvara.emissao  <= ").append(formataData(dataFinalEmissao));
                juncao = " and ";
                filtroUtilizado.append("<b>DATA EMISSÃO (FINAL): </b>").append(formataData(dataFinalEmissao).replace("'", "")).append("; >");
            }
            if (dataInicialAbertura != null) {
                where.append(juncao).append(" ce.abertura >= ").append(formataData(dataInicialAbertura));
                juncao = " and ";
                filtroUtilizado.append("<b>DATA ABERTURA (INICIAL): </b>").append(formataData(dataInicialAbertura).replace("'", "")).append("; ");
            }
            if (dataFinalAbertura != null) {
                where.append(juncao).append(" ce.abertura <= ").append(formataData(dataFinalAbertura));
                juncao = " and ";
                filtroUtilizado.append("<b>DATA ABERTURA (FINAL): </b>").append(formataData(dataFinalAbertura).replace("'", "")).append("; ");
            }
          /*  HashMap parameters = new HashMap();
            parameters.put("USUARIO", SistemaFacade.obtemLogin());
            parameters.put("BRASAO", caminhoBrasao);
            parameters.put("WHERE", where.toString());
            parameters.put("FILTROUTILIZADO", filtroUtilizado.toString());
            try {
                gerarRelatorio("ListagemAlvara.jasper", parameters);
            } catch (JRException ex) {
                logger.error("Erro: ", ex);
            } catch (IOException ex) {
                logger.error("Erro: ", ex);
            }*/

            try {
                RelatorioDTO dto = new RelatorioDTO();
                dto.setApi("tributario/alvara/");
                dto.setNomeRelatorio("Listagem de Alvarás");
                dto.adicionarParametro("USUARIO", SistemaFacade.obtemLogin(), Boolean.FALSE);
                /*dto.adicionarParametro("BRASAO", caminhoBrasao);*/
                dto.adicionarParametro("WHERE", where.toString());
                dto.adicionarParametro("FILTROUTILIZADO", filtroUtilizado.toString());
                ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
                FacesUtil.addMensagemRelatorioSegundoPlano();
            } catch (WebReportRelatorioExistenteException e) {
                ReportService.getInstance().abrirDialogConfirmar(e);
            } catch (ValidacaoException ve) {
                FacesUtil.printAllFacesMessages(ve.getMensagens());
            } catch (Exception e) {
                FacesUtil.addOperacaoNaoRealizada(e.getMessage());
            }
        }
    }

    public String formataData(Date data) {
        SimpleDateFormat formatador = new SimpleDateFormat("dd/MM/yyyy");
        return "'" + formatador.format(data) + "'";
    }

    public List<SelectItem> getTipos() {
        List<SelectItem> retorno = new ArrayList<SelectItem>();
        retorno.add(new SelectItem(null, " "));
        for (TipoAlvara tipo : TipoAlvara.values()) {
            retorno.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return retorno;
    }

}
