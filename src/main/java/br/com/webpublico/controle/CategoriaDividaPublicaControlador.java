/*
 * Codigo gerado automaticamente em Fri Oct 19 13:43:11 BRT 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.CategoriaDividaPublica;
import br.com.webpublico.enums.NaturezaDividaPublica;
import br.com.webpublico.enums.SituacaoCadastralCadastroEconomico;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.CategoriaDividaPublicaFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "categoriaDividaPublicaControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-categoria-divida-publica", pattern = "/categoria-divida-publica/novo/", viewId = "/faces/financeiro/orcamentario/dividapublica/categoriadividapublica/edita.xhtml"),
    @URLMapping(id = "editar-categoria-divida-publica", pattern = "/categoria-divida-publica/editar/#{categoriaDividaPublicaControlador.id}/", viewId = "/faces/financeiro/orcamentario/dividapublica/categoriadividapublica/edita.xhtml"),
    @URLMapping(id = "ver-categoria-divida-publica", pattern = "/categoria-divida-publica/ver/#{categoriaDividaPublicaControlador.id}/", viewId = "/faces/financeiro/orcamentario/dividapublica/categoriadividapublica/visualizar.xhtml"),
    @URLMapping(id = "listar-categoria-divida-publica", pattern = "/categoria-divida-publica/listar/", viewId = "/faces/financeiro/orcamentario/dividapublica/categoriadividapublica/lista.xhtml")
})
public class CategoriaDividaPublicaControlador extends PrettyControlador<CategoriaDividaPublica> implements Serializable, CRUD {

    @EJB
    private CategoriaDividaPublicaFacade categoriaDividaPublicaFacade;
    private ConverterAutoComplete converterSuperior;
    private String mascaraDoCodigo;

    public CategoriaDividaPublicaControlador() {
        super(CategoriaDividaPublica.class);
    }

    public List<SelectItem> getSituacaoCadastral() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (SituacaoCadastralCadastroEconomico sit : SituacaoCadastralCadastroEconomico.values()) {
            if (sit.equals(SituacaoCadastralCadastroEconomico.ATIVA_REGULAR) || sit.equals(SituacaoCadastralCadastroEconomico.ATIVA_NAO_REGULAR) || sit.equals(SituacaoCadastralCadastroEconomico.INATIVO)) {
                toReturn.add(new SelectItem(sit, sit.getDescricao()));
            }
        }
        return toReturn;
    }

    public CategoriaDividaPublicaFacade getFacade() {
        return categoriaDividaPublicaFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return categoriaDividaPublicaFacade;
    }

    @URLAction(mappingId = "novo-categoria-divida-publica", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();

        String mascara = categoriaDividaPublicaFacade.recuperaMascaraDoCodigo();
        String mascaraAtePrimeiroPonto[] = mascara.split("\\.");
        mascaraDoCodigo = mascaraAtePrimeiroPonto[0].replaceAll("#", "9");
    }

    @URLAction(mappingId = "ver-categoria-divida-publica", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editar-categoria-divida-publica", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperaMascara();
    }

    public void recuperaMascara() {
        CategoriaDividaPublica cd = (CategoriaDividaPublica) selecionado;
        int nivelDaMascara = recuperaNivel(cd.getCodigo());

        if (nivelDaMascara > 1) {
            int posicaoDoCodigoQuePodeSerAlterado = cd.getCodigo().lastIndexOf(".");
            String codigoQuePodeSerAlterado = cd.getCodigo().substring(posicaoDoCodigoQuePodeSerAlterado + 1, cd.getCodigo().length());
            String codigo = "";
            for (int i = 0; i < codigoQuePodeSerAlterado.length(); i++) {
                codigo += "9";
            }
            selecionado.setCodigo(cd.getCodigo());
            mascaraDoCodigo = cd.getCodigo().substring(0, posicaoDoCodigoQuePodeSerAlterado) + "." + codigo;
        } else {
            String mascara = categoriaDividaPublicaFacade.recuperaMascaraDoCodigo();
            String mascaraAtePrimeiroPonto[] = mascara.split("\\.");
            mascaraDoCodigo = mascaraAtePrimeiroPonto[0].replaceAll("#", "9");
        }
    }

    private int recuperaNivel(String codigoDoPai) {
        int nivel = 1;
        for (int i = 0; i < codigoDoPai.length(); i++) {
            char c = codigoDoPai.charAt(i);
            if (c == '.') {
                nivel++;
            }
        }
        return nivel;
    }

    public List<CategoriaDividaPublica> completaSuperior(String parte) {
        return categoriaDividaPublicaFacade.listaFiltrando(parte.trim(), "codigo", "descricao");
    }

    public ConverterAutoComplete getConverterSuperior() {
        if (converterSuperior == null) {
            converterSuperior = new ConverterAutoComplete(CategoriaDividaPublica.class, categoriaDividaPublicaFacade);
        }
        return converterSuperior;
    }

    @Override
    public void salvar() {
        CategoriaDividaPublica cat = ((CategoriaDividaPublica) selecionado);
        boolean validaCodigo = categoriaDividaPublicaFacade.existeCodigoIgualAtivo(cat);
        if (Util.validaCampos(cat) && validaCodigo) {
            try {
                if (cat.getSuperior() != null) {
                    CategoriaDividaPublica superior = categoriaDividaPublicaFacade.recuperar(cat.getSuperior().getId());
                    cat.setCodigo(superior.getCodigo() + "." + cat.getCodigo().substring(cat.getCodigo().length() - 2, cat.getCodigo().length()));
                    if (superior.getFilhos() == null) {
                        superior.setFilhos(new ArrayList<CategoriaDividaPublica>());
                    }
                    superior.getFilhos().add(cat);
                    categoriaDividaPublicaFacade.salvar(superior);
                    alteraCodigoDosFilhos(cat);
                } else {
                    categoriaDividaPublicaFacade.salvar(cat);
                    alteraCodigoDosFilhos(cat);
                }
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Operação Realizada! ", " A Natureza da Dívida Pública: " + selecionado.getCodigo() + " " + selecionado.getDescricao() + " foi salvo com sucesso"));
                redireciona();
            } catch (Exception ex) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Operação não Realizada! ", ex.getMessage()));
            }

        } else {
            if (!validaCodigo) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ja existe um registro com o código " + cat.getCodigo() + "!", "Informe um valore diferente no campo Código!"));
            }
        }
    }

    @Override
    public void excluir() {
        try {
            if (validaExclusao()) {
                getFacede().remover(selecionado);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Operação Realizada! ", " A Natureza da Dívida Pública: " + selecionado.getCodigo() + " " + selecionado.getDescricao() + " foi excluida com sucesso."));
                redireciona();
            }
        } catch (Exception e) {
            FacesUtil.addError(e.getMessage(), e.getMessage());
        }
    }

    private boolean validaExclusao() {
        boolean deuCerto = true;
        if (categoriaDividaPublicaFacade.validaSeExisteCategoriasFilhas((CategoriaDividaPublica) selecionado)) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro ao Remover! ", " Não foi possível remover, pois a Natureza da Dívida: " + selecionado.getCodigo() + " " + selecionado.getDescricao() + " possui categoria(s) filhas associadas."));
            deuCerto = false;
        }
        if (categoriaDividaPublicaFacade.validaVinculoComDividaPublica((CategoriaDividaPublica) selecionado)) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro ao Remover! ", " Não foi possível remover, pois a Natureza da Dívida: " + selecionado.getCodigo() + " " + ((CategoriaDividaPublica) selecionado).getDescricao() + " possui vínculo com a Dívida Pública."));
            deuCerto = false;
        }
        return deuCerto;
    }

    public String retornaTipoCategoria(CategoriaDividaPublica categoria) {
        return categoriaDividaPublicaFacade.retornaTipoCategoria(categoria).name();
    }

    public List<SelectItem> getNaturezaDivida() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (NaturezaDividaPublica object : NaturezaDividaPublica.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/categoria-divida-publica/";
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

    private void alteraCodigoDosFilhos(CategoriaDividaPublica selecionado) {
        String novoCodigo = selecionado.getCodigo();
        List<CategoriaDividaPublica> l = categoriaDividaPublicaFacade.recuperaFilhos(selecionado);

        for (CategoriaDividaPublica c : l) {
            c.setCodigo(novoCodigo + c.getCodigo().substring(selecionado.getCodigo().length(), c.getCodigo().length()));
            categoriaDividaPublicaFacade.salvar(c);
        }
    }
}
