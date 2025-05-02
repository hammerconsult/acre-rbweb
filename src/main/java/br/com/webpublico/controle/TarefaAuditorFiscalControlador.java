package br.com.webpublico.controle;

import br.com.webpublico.entidades.SecretariaFiscalizacao;
import br.com.webpublico.entidades.TarefaAuditorFiscal;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.SecretariaFiscalizacaoFacade;
import br.com.webpublico.negocios.TarefaAuditorFiscalFacade;
import br.com.webpublico.util.ConverterAutoComplete;
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
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 04/08/14
 * Time: 15:08
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean(name = "tarefaAuditorFiscalControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaTarefaAuditorFiscal", pattern = "/tarefaauditorfiscal/novo/", viewId = "/faces/tributario/fiscalizacaosecretaria/tarefaauditorfiscal/edita.xhtml"),
    @URLMapping(id = "editarTarefaAuditorFiscal", pattern = "/tarefaauditorfiscal/editar/#{tarefaAuditorFiscalControlador.id}/", viewId = "/faces/tributario/fiscalizacaosecretaria/tarefaauditorfiscal/edita.xhtml"),
    @URLMapping(id = "listarTarefaAuditorFiscal", pattern = "/tarefaauditorfiscal/listar/", viewId = "/faces/tributario/fiscalizacaosecretaria/tarefaauditorfiscal/lista.xhtml"),
    @URLMapping(id = "verTarefaAuditorFiscal", pattern = "/tarefaauditorfiscal/ver/#{tarefaAuditorFiscalControlador.id}/", viewId = "/faces/tributario/fiscalizacaosecretaria/tarefaauditorfiscal/visualizar.xhtml")
})

public class TarefaAuditorFiscalControlador extends PrettyControlador<TarefaAuditorFiscal> implements Serializable, CRUD {

    @EJB
    private TarefaAuditorFiscalFacade tarefaAuditorFiscalFacade;
    @EJB
    private SecretariaFiscalizacaoFacade secretariaFiscalizacaoFacade;
    private ConverterAutoComplete converterSecretariaFiscalizacao;

    public TarefaAuditorFiscalControlador() {
        super(TarefaAuditorFiscal.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tarefaauditorfiscal/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return tarefaAuditorFiscalFacade;
    }

    @URLAction(mappingId = "novaTarefaAuditorFiscal", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setSituacao(TarefaAuditorFiscal.Situacao.ATIVO);
    }

    @URLAction(mappingId = "verTarefaAuditorFiscal", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarTarefaAuditorFiscal", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    public ConverterAutoComplete getConverterSecretariaFiscalizacao() {
        if (converterSecretariaFiscalizacao == null) {
            converterSecretariaFiscalizacao = new ConverterAutoComplete(SecretariaFiscalizacao.class, secretariaFiscalizacaoFacade);
        }
        return converterSecretariaFiscalizacao;
    }

    public List<SecretariaFiscalizacao> completaSecretaria(String parte) {
        return secretariaFiscalizacaoFacade.completarSecretariaFiscalizacao(parte.trim());
    }

    public List<SelectItem> getSituacoes() {
        return Util.getListSelectItem(Arrays.asList(TarefaAuditorFiscal.Situacao.values()));
    }

    @Override
    public void salvar() {
        if (Util.validaCampos(selecionado)) {
            try {
                if (operacao == Operacoes.NOVO) {
                    selecionado.setCodigo(tarefaAuditorFiscalFacade.getProximoCodigoPorSecretaria(selecionado.getSecretariaFiscalizacao()));
                    getFacede().salvarNovo(selecionado);
                    FacesUtil.addOperacaoRealizada("A tarefa do auditor fiscal foi registrada com o código: " + selecionado.getCodigo() + ".");
                } else {
                    getFacede().salvar(selecionado);
                    FacesUtil.addOperacaoRealizada("A tarefa do auditor fiscal com o código: " + selecionado.getCodigo() + " foi alterada com sucesso.");
                }
            } catch (ValidacaoException ex) {
                FacesUtil.printAllFacesMessages(ex.getMensagens());
                return;
            } catch (Exception e) {
                descobrirETratarException(e);

            }
            redireciona();
        }
    }
}
