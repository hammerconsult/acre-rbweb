package br.com.webpublico.controle;

import br.com.webpublico.entidades.CadastroImobiliario;
import br.com.webpublico.entidades.DocumentoOficial;
import br.com.webpublico.entidades.IsencaoCadastroImobiliario;
import br.com.webpublico.negocios.DocumentoOficialFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.ProcessoIsencaoIPTUFacade;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@ManagedBean(name = "consultaIsencaoControle")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoConsultaIsencaoIPTU", pattern = "/tributario/isencao-iptu/consulta/", viewId = "/faces/tributario/iptu/isencaoimpostos/consulta/edita.xhtml")
})
public class ConsultaIsencaoControle implements Serializable {

    private List<IsencaoCadastroImobiliario> isencoes;
    private CadastroImobiliario cadastro;
    @EJB
    private ProcessoIsencaoIPTUFacade isencaoFacade;
    @EJB
    private DocumentoOficialFacade documentoOficialFacade;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;

    public List<IsencaoCadastroImobiliario> getIsencoes() {
        return isencoes;
    }

    public CadastroImobiliario getCadastro() {
        return cadastro;
    }

    public void setCadastro(CadastroImobiliario cadastro) {
        this.cadastro = cadastro;
    }


    @URLAction(mappingId = "novoConsultaIsencaoIPTU", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        cadastro = new CadastroImobiliario();
        isencoes = new ArrayList<>();
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public void listar() {
        if (valida()) {
            try {
                isencoes = isencaoFacade.listaIsencoesPorCadastro(cadastro);
            } catch (ExcecaoNegocioGenerica e) {
                FacesUtil.addWarn("Atenção", e.getMessage());
            } catch (Exception e) {
                FacesUtil.addError("Ação inesperada", e.getMessage());
            }
        }
    }

    public boolean valida() {
        boolean valida = true;
        if (cadastro == null) {
            valida = false;
            FacesUtil.addError("Atenção! ", "Antes de continuar você deve informar um cadastro");
        }
        return valida;
    }

    public void imprimirDocumentoOficial(IsencaoCadastroImobiliario isencao) {
        try {
            DocumentoOficial docto = documentoOficialFacade.geraDocumentoIsencaoIPTU(isencao, isencao.getDocumentoOficial(), isencao.getCadastroImobiliario(), isencao.getProcessoIsencaoIPTU().getCategoriaIsencaoIPTU().getTipoDoctoOficial(), sistemaControlador.getSistemaFacade().getUsuarioCorrente(), sistemaControlador.getExercicioCorrente(), sistemaControlador.getIpCorrente());
            if (docto != null) {
                isencao.setDocumentoOficial(docto);
                isencaoFacade.salvar(isencao);
                documentoOficialFacade.emiteDocumentoOficial(docto);
            }
        } catch (Exception e) {
            FacesUtil.addError("Erro", "Erro ao imprimir o Documento Oficial!");
        }
    }
}
