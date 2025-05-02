package br.com.webpublico.controle;

import br.com.webpublico.entidadesauxiliares.AutenticaNFSAvulsa;
import br.com.webpublico.negocios.ConsultaAutenticidadeNFSAvulsaFacade;
import br.com.webpublico.negocios.NFSAvulsaFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.apache.commons.codec.binary.Base64;
import org.primefaces.event.SelectEvent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIOutput;
import javax.faces.event.AjaxBehaviorEvent;
import java.io.UnsupportedEncodingException;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: André Gustavo
 * Date: 18/03/14
 * Time: 15:45
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novaConsulta", pattern = "/tributario/notafiscal-avulsa/consulta-autenticidade/", viewId = "/faces/tributario/notafiscalavulsa/consultaautenticidade/edita.xhtml")
})
public class ConsultaAutenticidadeNFSAvulsaControlador {
    @EJB
    private ConsultaAutenticidadeNFSAvulsaFacade consultaAutenticidadeNFSAvulsaFacade;

    private Date dataEmissao;
    private String numeroNota;
    private String cpfCnpj;
    private String numeroAutenticidade;
    private String opcaoCpfCnpj;
    private Boolean validado;

    @URLAction(mappingId = "novaConsulta", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        opcaoCpfCnpj = "CPF";
        validado = null;
    }

    public boolean validaConsulta() {
        boolean valida = true;
        if (numeroNota == null || numeroNota.trim().equals("")) {
            FacesUtil.addWarn("Atenção!", "O Número da Nota é um campo obrigatório.");
            valida = false;
        }

        if (cpfCnpj == null || Util.removeMascaras(cpfCnpj).trim().equals("")) {
            FacesUtil.addWarn("Atenção!", "O " + opcaoCpfCnpj + " é um campo obrigatório.");
            valida = false;
        }

        if (dataEmissao == null) {
            FacesUtil.addWarn("Atenção!", "A Data de Emissão é um campo obrigatório.");
            valida = false;
        }

        if (numeroAutenticidade == null || numeroAutenticidade.trim().equals("")) {
            FacesUtil.addWarn("Atenção!", "O Número da Autenticidade é um campo obrigatório.");
            valida = false;
        }
        return valida;
    }


    public void validar() {
        if (validaConsulta()) {
            AutenticaNFSAvulsa autenticaNFSAvulsa = new AutenticaNFSAvulsa();
            autenticaNFSAvulsa.setDataLancamento(dataEmissao);
            autenticaNFSAvulsa.setNumeroNota(numeroNota);
            autenticaNFSAvulsa.setCpfCnpj(cpfCnpj);
            autenticaNFSAvulsa.setNumeroAutenticidade(numeroAutenticidade);
            validado = consultaAutenticidadeNFSAvulsaFacade.validouNotaFiscalAvulsa(autenticaNFSAvulsa);
        }
    }


    public Date getDataEmissao() {
        return dataEmissao;
    }

    public void setDataEmissao(Date dataEmissao) {
        this.dataEmissao = dataEmissao;
    }

    public String getNumeroNota() {
        return numeroNota;
    }

    public void setNumeroNota(String numeroNota) {
        this.numeroNota = numeroNota;
    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
    }

    public String getNumeroAutenticidade() {
        return numeroAutenticidade;
    }

    public void setNumeroAutenticidade(String numeroAutenticidade) {
        this.numeroAutenticidade = numeroAutenticidade;
    }

    public String getOpcaoCpfCnpj() {
        return opcaoCpfCnpj;
    }

    public void setOpcaoCpfCnpj(String opcaoCpfCnpj) {
        this.opcaoCpfCnpj = opcaoCpfCnpj;
    }

    public Boolean getValidado() {
        return validado;
    }

    public void setValidado(Boolean validado) {
        this.validado = validado;
    }

    public void limpaCpfCnpj() {
        cpfCnpj = "";
    }

    public String getMascara() {
        if (("CPF").equals(opcaoCpfCnpj)) {
            return Util.removeMascaras(cpfCnpj);
//            return "999.999.999-99";
        } else {
            return "99.999.999/9999-99";
        }
    }
}
