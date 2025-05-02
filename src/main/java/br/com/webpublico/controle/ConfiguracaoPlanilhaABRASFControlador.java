package br.com.webpublico.controle;

import br.com.webpublico.entidades.ConfiguracaoPlanilhaABRASF;
import br.com.webpublico.entidades.Conta;
import br.com.webpublico.enums.TipoContaReceitaAbrasf;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ConfiguracaoPlanilhaABRASFFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoConfiguracaoPlanilhaABRASF", pattern = "/configuracaoplanilhaabrasf/novo/", viewId = "/faces/tributario/configuracaoplanilhaabrasf/edita.xhtml"),
    @URLMapping(id = "editarConfiguracaoPlanilhaABRASF", pattern = "/configuracaoplanilhaabrasf/editar/#{configuracaoPlanilhaABRASFControlador.id}/", viewId = "/faces/tributario/configuracaoplanilhaabrasf/edita.xhtml"),
    @URLMapping(id = "listarConfiguracaoPlanilhaABRASF", pattern = "/configuracaoplanilhaabrasf/listar/", viewId = "/faces/tributario/configuracaoplanilhaabrasf/lista.xhtml"),
    @URLMapping(id = "verConfiguracaoPlanilhaABRASF", pattern = "/configuracaoplanilhaabrasf/ver/#{configuracaoPlanilhaABRASFControlador.id}/", viewId = "/faces/tributario/configuracaoplanilhaabrasf/visualizar.xhtml")
})
public class ConfiguracaoPlanilhaABRASFControlador extends PrettyControlador<ConfiguracaoPlanilhaABRASF> implements Serializable, CRUD {

    private static final Logger logger = LoggerFactory.getLogger(ConfiguracaoPlanilhaABRASFControlador.class);

    @EJB
    private ConfiguracaoPlanilhaABRASFFacade configuracaoPlanilhaABRASFFacade;


    public ConfiguracaoPlanilhaABRASFControlador() {
        super(ConfiguracaoPlanilhaABRASF.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return configuracaoPlanilhaABRASFFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/configuracaoplanilhaabrasf/";
    }

    @Override
    public Object getUrlKeyValue() {
        return getId();
    }

    @Override
    @URLAction(mappingId = "novoConfiguracaoPlanilhaABRASF", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "editarConfiguracaoPlanilhaABRASF", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "verConfiguracaoPlanilhaABRASF", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @Override
    public void salvar() {
        try {
            validarContaPorExercicio();
            super.salvar();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        }
    }

    public List<SelectItem> buscarTipoContaReceitaAbrasf() {
        return Util.getListSelectItem(TipoContaReceitaAbrasf.values());
    }

    public List<Conta> completarContaReceita(String parte) {
        if (selecionado.getExercicio() == null) {
            FacesUtil.addOperacaoNaoPermitida("Informe o exercício");
            return null;
        }
        return configuracaoPlanilhaABRASFFacade.completarContaReceita(parte, selecionado.getExercicio());
    }

    private void validarContaPorExercicio() {
        ValidacaoException ve = new ValidacaoException();
        if(selecionado.getTipoContaReceitaAbrasf() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo Conta Receita deve ser informado.");
        } else if (selecionado.getExercicio() != null && selecionado.getContaReceita() != null && selecionado.getTipoContaReceitaAbrasf() != null) {
            boolean hasConfiguracaoParaExercicio = configuracaoPlanilhaABRASFFacade.hasConfiguracaoPorExercicio(selecionado);
            if (hasConfiguracaoParaExercicio) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe uma configuração para a conta de " + selecionado.getTipoContaReceitaAbrasf().getDescricao() + " para o exercício de " + selecionado.getExercicio().getAno() + ".");
            }
        }
        ve.lancarException();
    }

}
