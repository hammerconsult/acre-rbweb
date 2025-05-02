/*
 * Codigo gerado automaticamente em Mon Nov 14 16:00:21 BRST 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.SituacaoFuncional;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.SituacaoFuncionalFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.util.Util;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ManagedBean(name = "situacaoFuncionalControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaSituacaoFuncional", pattern = "/situacaofuncional/novo/", viewId = "/faces/rh/administracaodepagamento/situacaofuncional/edita.xhtml"),
    @URLMapping(id = "editarSituacaoFuncional", pattern = "/situacaofuncional/editar/#{situacaoFuncionalControlador.id}/", viewId = "/faces/rh/administracaodepagamento/situacaofuncional/edita.xhtml"),
    @URLMapping(id = "listarSituacaoFuncional", pattern = "/situacaofuncional/listar/", viewId = "/faces/rh/administracaodepagamento/situacaofuncional/lista.xhtml"),
    @URLMapping(id = "verSituacaoFuncional", pattern = "/situacaofuncional/ver/#{situacaoFuncionalControlador.id}/", viewId = "/faces/rh/administracaodepagamento/situacaofuncional/visualizar.xhtml")
})
public class SituacaoFuncionalControlador extends PrettyControlador<SituacaoFuncional> implements Serializable, CRUD {

    @EJB
    private SituacaoFuncionalFacade situacaoFuncionalFacade;
    public Map<Long, String> dadosIniciais = new HashMap<Long, String>();
    private ConverterAutoComplete converterSituacaoFuncional;

    public SituacaoFuncionalControlador() {
        super(SituacaoFuncional.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return situacaoFuncionalFacade;
    }

    @URLAction(mappingId = "novaSituacaoFuncional", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        ((SituacaoFuncional) selecionado).setCodigo(situacaoFuncionalFacade.retornaUltimoCodigoLong());
    }

    @URLAction(mappingId = "editarSituacaoFuncional", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "verSituacaoFuncional", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    public void cargaInicial() {
        dadosIniciais.put(1l, "EM EXERCÍCIO");
        dadosIniciais.put(2l, "EM FÉRIAS");
        dadosIniciais.put(3l, "AFASTADO/LICENCIADO");
        dadosIniciais.put(4l, "A DISPOSIÇÃO");
        dadosIniciais.put(5l, "A NOSSA DISPOSIÇÃO");
        dadosIniciais.put(6l, "EXONERADO/RESCISO");
        dadosIniciais.put(7l, "APOSENTADO");
        dadosIniciais.put(8l, "PENSIONISTA");
        dadosIniciais.put(9l, "SUBSTITUIDO");
        dadosIniciais.put(10l, "INSTITUIDOR (FALECIDO - GERA PENSÃO)");
        dadosIniciais.put(11l, "INATIVO PARA FOLHA");

        for (Map.Entry<Long, String> entry : dadosIniciais.entrySet()) {
            SituacaoFuncional s = situacaoFuncionalFacade.recuperaCodigo(entry.getKey());
            if (s == null) {
                situacaoFuncionalFacade.salvarNovo(new SituacaoFuncional(entry.getKey(), entry.getValue()));
            } else {
                s.setDescricao(entry.getValue());
                situacaoFuncionalFacade.salvar(s);
            }
        }

        //lista = null;

    }

    @Override
    public void salvar() {
        if (Util.validaCampos(selecionado)) {
            try {
                SituacaoFuncional situacao = ((SituacaoFuncional) selecionado);
                if (operacao == Operacoes.NOVO) {
                    Long novoCodigo = situacaoFuncionalFacade.retornaUltimoCodigoLong();

                    if (!novoCodigo.equals(situacao.getCodigo())) {
                        FacesUtil.addWarn("Atenção!", "O Código " + situacao.getCodigo() + " já está sendo usado e foi gerado o novo código " + novoCodigo + " !");
                        situacao.setCodigo(novoCodigo);
                    }

                }
                super.salvar();

            } catch (Exception e) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Exceção do sistema!", e.getMessage().toString()));
            }
        }
    }

    @Override
    public String getCaminhoPadrao() {
        return "/situacaofuncional/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public List<SituacaoFuncional> completaSituacaoFuncional(String parte) {
        return situacaoFuncionalFacade.listaFiltrandoCodigoDescricao(parte.trim());
    }

    public Converter getConverterSituacaoFuncional() {
        if (converterSituacaoFuncional == null) {
            converterSituacaoFuncional = new ConverterAutoComplete(SituacaoFuncional.class, situacaoFuncionalFacade);
        }
        return converterSituacaoFuncional;
    }
}
