package br.com.webpublico.controle;

import br.com.webpublico.entidades.Empenho;
import br.com.webpublico.entidades.SolicitacaoEmpenho;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.CorrecaoSolicitacaoEmpenhoFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

/**
 * Created by mateus on 02/10/17.
 */
@ManagedBean(name = "correcaoSolicitacaoEmpenhoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "correcao-solicitacao-empenho", pattern = "/correcao-solicitacao-empenho/", viewId = "/faces/financeiro/orcamentario/correcaosolicitacaoempenho/edita.xhtml")

})

public class CorrecaoSolicitacaoEmpenhoControlador implements Serializable {

    @EJB
    private CorrecaoSolicitacaoEmpenhoFacade correcaoSolicitacaoEmpenhoFacade;
    private Empenho empenho;
    private SolicitacaoEmpenho solicitacaoEmpenho;
    private ConverterAutoComplete converterSolicitacaoEmpenho;

    @URLAction(mappingId = "correcao-solicitacao-empenho", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        empenho = null;
        solicitacaoEmpenho = null;
    }

    public List<SolicitacaoEmpenho> completarSolicitacoesSemEmpenho(String filtro) {
        return correcaoSolicitacaoEmpenhoFacade.getSolicitacaoEmpenhoFacade().buscarSolicitacoesPendentesPorUnidade(correcaoSolicitacaoEmpenhoFacade.getSistemaFacade().getUnidadeOrganizacionalOrcamentoCorrente(), filtro);
    }

    public ConverterAutoComplete getConverterSolicitacaoEmpenho() {
        if (converterSolicitacaoEmpenho == null) {
            converterSolicitacaoEmpenho = new ConverterAutoComplete(SolicitacaoEmpenho.class, correcaoSolicitacaoEmpenhoFacade.getSolicitacaoEmpenhoFacade());
        }
        return converterSolicitacaoEmpenho;
    }

    public List<Empenho> completarEmpenhosSemSolicitacao(String filtro) {
        return correcaoSolicitacaoEmpenhoFacade.getEmpenhoFacade().buscarEmpenhosSemSolicitacaoPorUnidade(correcaoSolicitacaoEmpenhoFacade.getSistemaFacade().getUnidadeOrganizacionalOrcamentoCorrente(),
            filtro,
            correcaoSolicitacaoEmpenhoFacade.getSistemaFacade().getExercicioCorrente());
    }

    public void salvar() {
        try {
            validarCampos();
            empenho.setSolicitacaoEmpenho(solicitacaoEmpenho);
            correcaoSolicitacaoEmpenhoFacade.salvarSolicitacaoEmpenho(empenho, solicitacaoEmpenho);
            FacesUtil.addOperacaoRealizada("A solicitação de empenho foi atualizada com sucesso!");
            limparCampos();
            FacesUtil.atualizarComponente("Formulario");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (solicitacaoEmpenho == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Solicitação de Empenho deve ser informado!");
        }
        if (empenho == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Empenho deve ser informado!");
        }
        ve.lancarException();
    }

    public Empenho getEmpenho() {
        return empenho;
    }

    public void setEmpenho(Empenho empenho) {
        this.empenho = empenho;
    }

    public SolicitacaoEmpenho getSolicitacaoEmpenho() {
        return solicitacaoEmpenho;
    }

    public void setSolicitacaoEmpenho(SolicitacaoEmpenho solicitacaoEmpenho) {
        this.solicitacaoEmpenho = solicitacaoEmpenho;
    }
}
