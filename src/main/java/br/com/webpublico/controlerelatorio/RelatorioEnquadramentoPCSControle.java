/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.PlanoCargosSalarios;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.negocios.PlanoCargosSalariosFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author andregustavo
 */
@ManagedBean(name = "relatorioEnquadramentoPCSControle")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoRelatorioEnquadramentoPcs", pattern = "/relatorio/enquadramento-pcs/novo/", viewId = "/faces/rh/relatorios/relatorioenquadramentopcs.xhtml")
})
public class RelatorioEnquadramentoPCSControle implements Serializable {

    private Date dataDeReferencia;
    private PlanoCargosSalarios planoCargosSalariosSelecionado;
    private Converter converterPlanoDeCargosSalarios;
    @EJB
    private PlanoCargosSalariosFacade planoCargosSalariosFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public Converter getConverterPlanoDeCargosSalarios() {
        if (converterPlanoDeCargosSalarios == null) {
            converterPlanoDeCargosSalarios = new ConverterAutoComplete(PlanoCargosSalarios.class, planoCargosSalariosFacade);
        }
        return converterPlanoDeCargosSalarios;
    }

    public Date getDataDeReferencia() {
        return dataDeReferencia;
    }

    public void setDataDeReferencia(Date dataDeReferencia) {
        this.dataDeReferencia = dataDeReferencia;
    }

    public PlanoCargosSalarios getPlanoCargosSalariosSelecionado() {
        return planoCargosSalariosSelecionado;
    }

    public void setPlanoCargosSalariosSelecionado(PlanoCargosSalarios planoCargosSalariosSelecionado) {
        this.planoCargosSalariosSelecionado = planoCargosSalariosSelecionado;
    }

    @URLAction(mappingId = "novoRelatorioEnquadramentoPcs", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limpaCampos() {
        dataDeReferencia = null;
        planoCargosSalariosSelecionado = null;
    }

    public String formataData(Date data) {
        SimpleDateFormat formatador = new SimpleDateFormat("dd/MM/yyyy");
        return formatador.format(data);
    }

    public List<PlanoCargosSalarios> completaPlanos(String parte) {
        if (dataDeReferencia != null) {
            return planoCargosSalariosFacade.listaFiltrandoPorVigenciaAutoComplete(dataDeReferencia, parte.trim());
        } else {
            return null;
        }
    }

    public void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (dataDeReferencia == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O Campo data de referência é obrigatório!");
        }

        if (planoCargosSalariosSelecionado == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O Campo plano de cargos e salários é obrigatório!");
        }
        ve.lancarException();
    }

    public String geraUrlImagemDir() {
        FacesContext fc = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
        final StringBuffer url = request.getRequestURL();
        String test = url.toString();
        String[] quebrado = test.split("/");
        StringBuilder b = new StringBuilder();
        b.append(quebrado[0]);
        b.append("/").append(quebrado[1]);
        b.append("/").append(quebrado[2]);
        b.append("/").append(quebrado[3]).append("/");
        return b.toString();
    }

    public void gerarRelatorioEnquadramento() {
        try {
            validarCampos();
            HierarquiaOrganizacional hierarquia = hierarquiaOrganizacionalFacade.getRaizHierarquia(dataDeReferencia);
            RelatorioDTO dto = new RelatorioDTO();
            if (hierarquia.getSubordinada() != null) {
                dto.adicionarParametro("PLANO", planoCargosSalariosSelecionado.getId());
                dto.adicionarParametro("DESC_PLANO", planoCargosSalariosSelecionado.getDescricao());

                dto.adicionarParametro("UNIDADE_NOME", hierarquia.getDescricao());
                dto.adicionarParametro("UNIDADE_ORG", hierarquia.getId());
                dto.adicionarParametro("DATA", DataUtil.getDataFormatada(dataDeReferencia));
                dto.setNomeParametroBrasao("IMAGEM");

                dto.setNomeRelatorio("Relatório De Enquadramento De Plano De Cargos e Salários");
                dto.setApi("rh/enquadramento-plano-cargo-salario/");
                ReportService.getInstance().gerarRelatorio(sistemaControlador.getUsuarioCorrente(), dto);
                FacesUtil.addMensagemRelatorioSegundoPlano();
            } else {
                FacesUtil.addWarn("Atenção!", "A raíz da hierarquia organizacional não está vigente.");
            }
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }

    }

}
