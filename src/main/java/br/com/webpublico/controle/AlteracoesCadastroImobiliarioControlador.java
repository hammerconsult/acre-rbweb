/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.UsuarioSistemaFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.JRException;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.servlet.ServletContext;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @author java
 */
@ManagedBean(name = "alteracoesCadastroImobiliarioControlador")
@ViewScoped
@URLMappings(mappings = {

    @URLMapping(id = "novoAlteracoesCadastroImobiliario", pattern = "/tributario/bci/relatorio-alteracao-bci/",
        viewId = "/faces/tributario/cadastromunicipal/relatorio/relatoriomovimentacaocadastral.xhtml"),
})
public class AlteracoesCadastroImobiliarioControlador extends AbstractReport {

    private Date inicial;
    private Date dataFinal;
    private String sql;
    private String filtroData;
    private UsuarioSistema usuario;
    @EJB
    public UsuarioSistemaFacade usuarioSistemaFacade;
    private ConverterAutoComplete converterUsuarioSistema;

    public AlteracoesCadastroImobiliarioControlador() {
    }

    public Converter getConverterUsuarioSistema() {
        if (converterUsuarioSistema == null) {
            converterUsuarioSistema = new ConverterAutoComplete(UsuarioSistema.class, usuarioSistemaFacade);
        }
        return converterUsuarioSistema;
    }

    @URLAction(mappingId = "novoMovimentacaoCadastral", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        usuario = new UsuarioSistema();
        limpaCampos();
    }

    public void gerarRelatorio() throws IOException, JRException {
        gerarFiltrosSql();
        String filtro = "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String subReport = ((ServletContext) (FacesContext.getCurrentInstance()).getExternalContext().getContext()).getRealPath("/WEB-INF/report") + "/";
        String arquivoJasper = "relatorioMovimentacoesCadastroImobiliario.jasper";
        String caminhoBrasao = getCaminhoImagem();
        HashMap parametros = new HashMap();
        parametros.put("NOMERELATORIO", "ALTERAÇÕES NO CADASTRO IMOBILIÁRIO");
        parametros.put("USUARIO", SistemaFacade.obtemLogin());
        parametros.put("DATAINICIAL", inicial);
        parametros.put("DATAFINAL", dataFinal);
        parametros.put("SQL", sql);
        parametros.put("FILTRODATA", filtroData);
        parametros.put("SECRETARIA", "Secretaria de Finanças");
        parametros.put("MODULO", "TRIBUTÁRIO");
        parametros.put("BRASAO", getCaminhoImagem());
        if (inicial != null && dataFinal != null) {
            if (usuario != null) {
                filtro += " por " + usuario.getPessoaFisica().getNome();
            }
            parametros.put("FILTROS", sdf.format(inicial) + " à " + sdf.format(dataFinal) + filtro);
        }
        gerarRelatorio(arquivoJasper, parametros);
    }

    public Date getInicial() {
        return inicial;
    }

    public void setInicial(Date inicial) {
        this.inicial = inicial;
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    public UsuarioSistema getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioSistema us) {
        this.usuario = us;
    }

    public void gerarFiltrosSql() {
        sql = "";
        filtroData = "";
        if (usuario != null) {
            sql = " where us.login = '" + usuario.getLogin() + "'";
        }
        if (dataFinal != null && inicial != null) {
            filtroData = " and trunc(ra.datahora) >= to_date('" + DataUtil.getDataFormatada(inicial) + "', 'dd/MM/yyyy') and trunc(ra.datahora) <= to_date('" + DataUtil.getDataFormatada(dataFinal) + "', 'dd/MM/yyyy') ";
        }
    }

    @URLAction(mappingId = "novoAlteracoesCadastroImobiliario", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limpaCamposImo() {
        inicial = null;
        dataFinal = null;
        usuario = null;

    }

    @URLAction(mappingId = "novoAlteracoesCadastroEconomico", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limpaCampos() {
        limpaCamposImo();
    }

    public List<UsuarioSistema> completaUsuarioSistema(String parte) {
        return usuarioSistemaFacade.listaFiltrando(parte.trim(), "login", "pessoaFisica.cpf");
    }
}
