package br.com.webpublico.controle;

import br.com.webpublico.entidades.InfracaoFiscalizacaoSecretaria;
import br.com.webpublico.entidades.SecretariaFiscalizacao;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.InfracaoFiscalizacaoSecretariaFacade;
import br.com.webpublico.negocios.SecretariaFiscalizacaoFacade;
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
@ManagedBean(name = "infracaoFiscalizacaoSecretariaControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaInfracaoFiscalizacaoSecretaria", pattern = "/infracaofiscalizacaosecretaria/novo/", viewId = "/faces/tributario/fiscalizacaosecretaria/infracao/edita.xhtml"),
    @URLMapping(id = "editarInfracaoFiscalizacaoSecretaria", pattern = "/infracaofiscalizacaosecretaria/editar/#{infracaoFiscalizacaoSecretariaControlador.id}/", viewId = "/faces/tributario/fiscalizacaosecretaria/infracao/edita.xhtml"),
    @URLMapping(id = "listarInfracaoFiscalizacaoSecretaria", pattern = "/infracaofiscalizacaosecretaria/listar/", viewId = "/faces/tributario/fiscalizacaosecretaria/infracao/lista.xhtml"),
    @URLMapping(id = "verInfracaoFiscalizacaoSecretaria", pattern = "/infracaofiscalizacaosecretaria/ver/#{infracaoFiscalizacaoSecretariaControlador.id}/", viewId = "/faces/tributario/fiscalizacaosecretaria/infracao/visualizar.xhtml")
})
public class InfracaoFiscalizacaoSecretariaControlador extends PrettyControlador<InfracaoFiscalizacaoSecretaria> implements Serializable, CRUD {

    @EJB
    private InfracaoFiscalizacaoSecretariaFacade infracaoFiscalizacaoSecretariaFacade;
    @EJB
    private SecretariaFiscalizacaoFacade secretariaFiscalizacaoFacade;
    private ConverterAutoComplete converterSecretariaFiscalizacao;

    public InfracaoFiscalizacaoSecretariaControlador() {
        super(InfracaoFiscalizacaoSecretaria.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/infracaofiscalizacaosecretaria/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return infracaoFiscalizacaoSecretariaFacade;
    }

    @URLAction(mappingId = "novaInfracaoFiscalizacaoSecretaria", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setSituacao(InfracaoFiscalizacaoSecretaria.Situacao.ATIVO);
    }

    @URLAction(mappingId = "verInfracaoFiscalizacaoSecretaria", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarInfracaoFiscalizacaoSecretaria", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
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
        return Util.getListSelectItem(Arrays.asList(InfracaoFiscalizacaoSecretaria.Situacao.values()));
    }

    @Override
    public void salvar() {
        if (Util.validaCampos(selecionado)) {
            try {
                if (operacao == Operacoes.NOVO) {
                    selecionado.setCodigo(infracaoFiscalizacaoSecretariaFacade.getProximoCodigoPorSecretaria(selecionado.getSecretariaFiscalizacao()));
                    getFacede().salvarNovo(selecionado);
                    FacesUtil.addOperacaoRealizada("A infração de fiscalização de secretaria foi registrada com o código: " + selecionado.getCodigo() + ".");
                } else {
                    getFacede().salvar(selecionado);
                    FacesUtil.addOperacaoRealizada("A infração de fiscalização de secretaria com o código: " + selecionado.getCodigo() + " foi alterada com sucesso.");
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
