/*
 * Codigo gerado automaticamente em Fri Sep 28 09:34:49 BRT 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.ProvisaoSalario;
import br.com.webpublico.enums.TipoLancamento;
import br.com.webpublico.enums.TipoOperacaoProvSalario;
import br.com.webpublico.enums.TipoProvisaoSalario;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ProvisaoSalarioFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-provisao-salario", pattern = "/provisao-salario/novo/", viewId = "/faces/financeiro/orcamentario/rh/provisaosalarial/edita.xhtml"),
    @URLMapping(id = "ver-provisao-salario", pattern = "/provisao-salario/ver/#{provisaoSalarioControlador.id}/", viewId = "/faces/financeiro/orcamentario/rh/provisaosalarial/visualizar.xhtml"),
    @URLMapping(id = "editar-provisao-salario", pattern = "/provisao-salario/editar/#{provisaoSalarioControlador.id}/", viewId = "/faces/financeiro/orcamentario/rh/provisaosalarial/edita.xhtml"),
    @URLMapping(id = "listar-provisao-salario", pattern = "/provisao-salario/listar/", viewId = "/faces/financeiro/orcamentario/rh/provisaosalarial/lista.xhtml")
})
public class ProvisaoSalarioControlador extends PrettyControlador<ProvisaoSalario> implements Serializable, CRUD {

    @EJB
    private ProvisaoSalarioFacade provisaoSalarioFacade;

    public ProvisaoSalarioControlador() {
        super(ProvisaoSalario.class);
    }

    @Override
    @URLAction(mappingId = "novo-provisao-salario", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        selecionado.setUnidadeOrganizacional(provisaoSalarioFacade.getSistemaFacade().getUnidadeOrganizacionalOrcamentoCorrente());
        selecionado.setExercicio(provisaoSalarioFacade.getSistemaFacade().getExercicioCorrente());
    }

    @Override
    @URLAction(mappingId = "ver-provisao-salario", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    @Override
    @URLAction(mappingId = "editar-provisao-salario", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
    }

    public Integer getNumeroMaiorProvisao() {
        BigDecimal maior = new BigDecimal(provisaoSalarioFacade.getUltimoNumero());
        maior = maior.add(BigDecimal.ONE);
        return maior.intValue();
    }

    @Override
    public void salvar() {
        try {
            validarCampos();
            if (isOperacaoNovo()) {
                selecionado.setNumero(getNumeroMaiorProvisao().toString());
            }
            provisaoSalarioFacade.salvar(selecionado);
            redireciona();
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            descobrirETratarException(ex);
        }
    }

    private void validarCampos() {
        Util.validarCampos(selecionado);
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getTipoLancamento().isEstorno() && !provisaoSalarioFacade.hasLancamentoNoExercicio(selecionado)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não é possível realizar um estorno de exercícios anteriores");
        }
        ve.lancarException();
    }

    public List<SelectItem> getTiposDeLancamento() {
        return Util.getListSelectItemSemCampoVazio(TipoLancamento.values());
    }

    public List<SelectItem> getTiposDeOperacao() {
        return Util.getListSelectItem(TipoOperacaoProvSalario.values());
    }

    public List<SelectItem> getTiposDeProvisao() {
        return Util.getListSelectItem(TipoProvisaoSalario.values());
    }

    @Override
    public String getCaminhoPadrao() {
        return "/provisao-salario/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return provisaoSalarioFacade;
    }
}
