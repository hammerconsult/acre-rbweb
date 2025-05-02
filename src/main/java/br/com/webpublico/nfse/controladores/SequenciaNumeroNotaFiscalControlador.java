package br.com.webpublico.nfse.controladores;

import br.com.webpublico.entidades.CadastroEconomico;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.nfse.facades.NotaFiscalFacade;
import br.com.webpublico.util.FacesUtil;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "sequenciaNfse", pattern = "/nfse/ajustes/sequencia-nfse/",
        viewId = "/faces/tributario/nfse/ajustes/sequencianfse.xhtml")
})
public class SequenciaNumeroNotaFiscalControlador implements Serializable {

    @EJB
    private NotaFiscalFacade notaFiscalFacade;

    private Date emissaoInicial;
    private List<CadastroEconomico> prestadores;
    private CadastroEconomico prestador;

    public Date getEmissaoInicial() {
        return emissaoInicial;
    }

    public void setEmissaoInicial(Date emissaoInicial) {
        this.emissaoInicial = emissaoInicial;
    }

    public List<CadastroEconomico> getPrestadores() {
        return prestadores;
    }

    public void setPrestadores(List<CadastroEconomico> prestadores) {
        this.prestadores = prestadores;
    }

    public CadastroEconomico getPrestador() {
        return prestador;
    }

    public void setPrestador(CadastroEconomico prestador) {
        this.prestador = prestador;
    }

    @URLAction(mappingId = "sequenciaNfse", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        prestadores = Lists.newArrayList();
    }

    public void validarCamposObrigatorios() {
        ValidacaoException ve = new ValidacaoException();
        if (prestadores == null || prestadores.isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Por favor adicione um prestador.");
        }
        if (emissaoInicial == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Emissão Inicial é obrigatório.");
        }
        ve.lancarException();
    }

    public void sequenciarNotasFiscais() {
        try {
            validarCamposObrigatorios();
            notaFiscalFacade.sequenciarNotasFiscais(prestadores, emissaoInicial);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    public void removerPrestador(CadastroEconomico prestador) {
        this.prestadores.remove(prestador);
    }

    public void adicionarPrestador() {
        this.prestadores.add(prestador);
        prestador = null;
    }
}
