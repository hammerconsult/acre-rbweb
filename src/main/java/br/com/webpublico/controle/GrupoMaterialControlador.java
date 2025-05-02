/*
 * Codigo gerado automaticamente em Thu Jun 16 11:59:46 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.GrupoMaterial;
import br.com.webpublico.enums.SituacaoCadastralContabil;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.GrupoMaterialFacade;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@ManagedBean(name = "grupoMaterialControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novo-grupo-material", pattern = "/grupo-material/novo/", viewId = "/faces/administrativo/materiais/grupomaterial/edita.xhtml"),
        @URLMapping(id = "editar-grupo-material", pattern = "/grupo-material/editar/#{grupoMaterialControlador.id}/", viewId = "/faces/administrativo/materiais/grupomaterial/edita.xhtml"),
        @URLMapping(id = "ver-grupo-material", pattern = "/grupo-material/ver/#{grupoMaterialControlador.id}/", viewId = "/faces/administrativo/materiais/grupomaterial/visualizar.xhtml"),
        @URLMapping(id = "adiconar-filho-grupo-material", pattern = "/grupo-material/adicionar-filho/#{grupoMaterialControlador.id}/", viewId = "/faces/administrativo/materiais/grupomaterial/edita.xhtml"),
        @URLMapping(id = "listar-grupo-material", pattern = "/grupo-material/listar/", viewId = "/faces/administrativo/materiais/grupomaterial/lista.xhtml")
})
public class GrupoMaterialControlador extends PrettyControlador<GrupoMaterial> implements Serializable, CRUD {

    @EJB
    private GrupoMaterialFacade grupoMaterialFacade;
    private String codigoDoFilho;
    private String mascaraDoCodigo;


    public GrupoMaterialControlador() {
        super(GrupoMaterial.class);
    }

    public GrupoMaterialFacade getFacade() {
        return grupoMaterialFacade;
    }

    public String getCodigoCompleto() {
        if(selecionado.getSuperior() != null){
            return selecionado.getSuperior().getCodigo() + "." + codigoDoFilho;
        }
        return codigoDoFilho;
    }

    @Override
    public void salvar() {
        try {
            validarCodigo();
            selecionado.setCodigo(getCodigoCompleto());
            super.salvar();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addErrorGenerico(e);
        }
    }

    public void validarCodigo() {
        ValidacaoException ve = new ValidacaoException();
        String msg = "O Código " + getCodigoCompleto() + " deve ser diferente, pois já foi utilizado.";
        String codigo[];

        if (codigoDoFilho.trim().isEmpty()) {
            return;
        }

        if (selecionado.getSuperior() != null) {
            codigo = getCodigoCompleto().split(Pattern.quote("."));
            StringBuilder codigoFormatado = new StringBuilder();

            Map<String, BigDecimal> codigosComGrupoMaterialId = new HashMap<>();

            codigosComGrupoMaterialId = grupoMaterialFacade.recuperarCodigoFormatadoComGrupoMaterial();

            for (String s : codigo) {
                codigoFormatado.append(String.valueOf(Integer.valueOf(s)) + ".");
            }

            for (Map.Entry<String,BigDecimal> s : codigosComGrupoMaterialId.entrySet()) {
                if (codigoFormatado.toString().equals(s.getKey()) && !selecionado.getId().equals(s.getValue().longValue())) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida(msg);
                    break;
                }
            }
        } else {
            for (String s : grupoMaterialFacade.recuperarCodigoRaiz()) {
                if (Integer.valueOf(s).equals(Integer.valueOf(codigoDoFilho))) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida(msg);
                    break;
                }
            }

        }

        if (ve.temMensagens()) {
            throw ve;
        }
    }

    public AbstractFacade getFacede() {
        return grupoMaterialFacade;
    }

    public List<GrupoMaterial> completaGrupoMaterialAnalitico(String parte) {
        return grupoMaterialFacade.buscarGrupoMaterialAtivoNivelAnalitico(parte.trim());
    }

    public List<GrupoMaterial> completaGrupoMaterial(String parte) {
        return grupoMaterialFacade.listaFiltrandoGrupoDeMaterial(parte.trim());
    }

    @URLAction(mappingId = "novo-grupo-material", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        codigoDoFilho = grupoMaterialFacade.geraCodigoNovo();
        mascaraDoCodigo = "99";
    }

    @URLAction(mappingId = "adiconar-filho-grupo-material", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoFilho() {
        GrupoMaterial pai = grupoMaterialFacade.recuperar(super.getId());
        super.novo();
        selecionado.setSuperior(pai);
        codigoDoFilho = grupoMaterialFacade.geraCodigoFilho(pai);

        int nivelDaMascara = grupoMaterialFacade.recuperaNivel(pai.getCodigo());

        if (nivelDaMascara < 3) {
            mascaraDoCodigo = "99";
        } else {
            mascaraDoCodigo = "9999";
        }
    }

    @URLAction(mappingId = "ver-grupo-material", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editar-grupo-material", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();

        int posicaoDoCodigoQuePodeSerAlterado = selecionado.getCodigo().lastIndexOf(".");
        codigoDoFilho = selecionado.getCodigo().substring(posicaoDoCodigoQuePodeSerAlterado + 1, selecionado.getCodigo().length());

        int nivelDaMascara = grupoMaterialFacade.recuperaNivel(selecionado.getCodigo());

        if (nivelDaMascara > 1) {
           mascaraDoCodigo = "";
            for (int i = 0; i < codigoDoFilho.length(); i++) {
                mascaraDoCodigo += "9";
            }
        } else {
            mascaraDoCodigo = "99";
        }
    }

    @Override
    public String getCaminhoPadrao() {
        return "/grupo-material/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public String getMascaraDoCodigo() {
        return mascaraDoCodigo;
    }

    public void setMascaraDoCodigo(String mascaraDoCodigo) {
        this.mascaraDoCodigo = mascaraDoCodigo;
    }

    public boolean disableCodigo() {
        List<GrupoMaterial> filhosDe = grupoMaterialFacade.getFilhosDe(selecionado);
        if (filhosDe != null) {
            if (!filhosDe.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void excluir() {
        List<GrupoMaterial> filhosDe = grupoMaterialFacade.getFilhosDe(selecionado);
        if (filhosDe != null) {
            if (!filhosDe.isEmpty()) {
                FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "O Grupo Material, Cod: " + selecionado.getCodigo() + " - " + selecionado.getDescricao().toUpperCase() + " possui " + filhosDe.size() + " filho(s).");
            } else {
                super.excluir();
            }
        } else {
            super.excluir();
        }
    }

    public List<SelectItem> getListaSituacao() {
        return Util.getListSelectItem(Arrays.asList(SituacaoCadastralContabil.values()));
    }

    public String getCodigoDoFilho() {
        return codigoDoFilho;
    }

    public void setCodigoDoFilho(String codigoDoFilho) {
        this.codigoDoFilho = codigoDoFilho;
    }
}
