/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.Divida;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.negocios.ExtratoFacade;
import br.com.webpublico.negocios.MovimentacoesTributariasFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.ConverterExercicio;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Future;


@ManagedBean(name = "movimentacoesTributariasControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "movimentacoesTributariasControlador", pattern = "/tributario/movimentacoes-tributarias/",
        viewId = "/faces/tributario/contacorrente/movimentacoestributarias/movimentacoestributarias.xhtml"),
})
public class MovimentacoesTributariasControlador extends AbstractReport implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(MovimentacoesTributariasControlador.class);
    @EJB
    private MovimentacoesTributariasFacade facade;
    private Future<GeraRelatorio> future;
    private Date inicio, fim;

    public Date getInicio() {
        return inicio;
    }

    public void setInicio(Date inicio) {
        this.inicio = inicio;
    }

    public Date getFim() {
        return fim;
    }

    public void setFim(Date fim) {
        this.fim = fim;
    }

    public void geraRelatorio() {
        String realPath = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/WEB-INF/report/MovimentacoesTributario.jasper");
        GeraRelatorio geraRelatorio = getGeraRelatorio(realPath);
        future = facade.geraRelatorio(geraRelatorio);
    }

    public void processarIntegracao() {

    }

    private GeraRelatorio getGeraRelatorio(String realPath) {
        GeraRelatorio geraRelatorio = new GeraRelatorio(realPath);
        HashMap param = new HashMap();
        param.put("INICIO", inicio);
        param.put("FIM", fim);
        String realPath1 = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/img");
        param.put("BRASAO", realPath1 + "/");
        param.put("USUARIO", getSistemaFacade().getLogin());
        param.put("MODULO", "Tributário");
        param.put("SUBREPORT_DIR", getCaminhoSubReport());
        param.put("SECRETARIA", "Secretaria de Finanças");
        param.put("NOMERELATORIO", "Movimentações Tributárias");
        geraRelatorio.setParameters(param);
        geraRelatorio.setCon(recuperaConexaoJDBC());
        return geraRelatorio;
    }

    public void consultaGeracao() {
        if (future != null && future.isDone()) {
            FacesUtil.executaJavaScript("termina();");
        }
    }

    public void imprime() {
        geraNoDialog = true;
        try {
            escreveNoResponse("MovimentacoesTributarias ", future.get().getDados().toByteArray());
        } catch (Exception e) {
            FacesUtil.addError("Operação não realizada", "Ocorreu um problema de comunicação com o servidor");
            logger.error(e.getMessage());
        }
    }

    public static class GeraRelatorio {
        private ByteArrayOutputStream dados;
        private HashMap parameters;
        private Connection con;
        private String report;

        public GeraRelatorio(String report) {
            this.report = report;
        }

        public ByteArrayOutputStream getDados() {
            return dados;
        }

        public void setDados(ByteArrayOutputStream dados) {
            this.dados = dados;
        }

        public HashMap getParameters() {
            return parameters;
        }

        public void setParameters(HashMap parameters) {
            this.parameters = parameters;
        }

        public Connection getCon() {
            return con;
        }

        public void setCon(Connection con) {
            this.con = con;
        }

        public String getReport() {
            return report;
        }
    }


}
