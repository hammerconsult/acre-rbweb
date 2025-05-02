/*
 * Codigo gerado automaticamente em Thu Jun 16 11:59:46 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.Bem;
import br.com.webpublico.entidades.GrupoBem;
import br.com.webpublico.enums.SituacaoCadastralContabil;
import br.com.webpublico.enums.TipoBem;
import br.com.webpublico.enums.TipoUtilizacaoBem;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.GrupoBemFacade;
import br.com.webpublico.util.ConverterGenerico;
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
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "grupoBemControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-grupo-bem", pattern = "/grupo-bem/novo/", viewId = "/faces/administrativo/patrimonio/grupobem/edita.xhtml"),
    @URLMapping(id = "editar-grupo-bem", pattern = "/grupo-bem/editar/#{grupoBemControlador.id}/", viewId = "/faces/administrativo/patrimonio/grupobem/edita.xhtml"),
    @URLMapping(id = "ver-grupo-bem", pattern = "/grupo-bem/ver/#{grupoBemControlador.id}/", viewId = "/faces/administrativo/patrimonio/grupobem/visualizar.xhtml"),
    @URLMapping(id = "adiconar-filho-grupo-bem", pattern = "/grupo-bem/adicionar-filho/#{grupoBemControlador.id}/", viewId = "/faces/administrativo/patrimonio/grupobem/edita.xhtml"),
    @URLMapping(id = "listar-grupo-bem", pattern = "/grupo-bem/listar/", viewId = "/faces/administrativo/patrimonio/grupobem/lista.xhtml")
})
public class GrupoBemControlador extends PrettyControlador<GrupoBem> implements Serializable, CRUD {

    @EJB
    private GrupoBemFacade grupoBemFacade;
    private ConverterGenerico converterBem;
    private String codigoDoFilho;
    private String mascaraDoCodigo;

    public GrupoBemControlador() {
        super(GrupoBem.class);
    }

    public String getCodigoCompleto() {
        if (selecionado.getSuperior() != null) {
            return selecionado.getSuperior().getCodigo() + "." + codigoDoFilho;
        }
        return codigoDoFilho;
    }

    @Override
    public void salvar() {
        try {
            validarCampos();
            validarCodigoRepedito();
            selecionado.setCodigo(getCodigoCompleto());
            super.salvar();
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        }
    }

    private void validarCodigoRepedito() {
        ValidacaoException ve = new ValidacaoException();
        String codigo = getCodigoCompleto();
        if (grupoBemFacade.verificarCodigoRepetido(selecionado, codigo)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O Código " + codigo + " deve ser diferente, pois já foi utilizado.");
        }
        ve.lancarException();
    }

    public AbstractFacade getFacede() {
        return grupoBemFacade;
    }

    @URLAction(mappingId = "novo-grupo-bem", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        codigoDoFilho = grupoBemFacade.geraCodigoNovo();
        mascaraDoCodigo = "9999";
    }

    @URLAction(mappingId = "adiconar-filho-grupo-bem", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoFilho() {
        GrupoBem pai = grupoBemFacade.recuperar(super.getId());
        super.novo();
        selecionado.setSuperior(pai);
        selecionado.setTipoBem(pai.getTipoBem());

        codigoDoFilho = grupoBemFacade.geraCodigoFilho(pai);

        int nivelDaMascara = grupoBemFacade.recuperaNivel(pai.getCodigo());

        if (nivelDaMascara < 3) {
            mascaraDoCodigo = "99";
        } else {
            mascaraDoCodigo = "9999";
        }
    }

    @URLAction(mappingId = "ver-grupo-bem", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editar-grupo-bem", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();

        int posicaoDoCodigoQuePodeSerAlterado = selecionado.getCodigo().lastIndexOf(".");
        codigoDoFilho = selecionado.getCodigo().substring(posicaoDoCodigoQuePodeSerAlterado + 1, selecionado.getCodigo().length());

        int nivelDaMascara = grupoBemFacade.recuperaNivel(selecionado.getCodigo());

        if (nivelDaMascara > 1) {
            mascaraDoCodigo = "";
            for (int i = 0; i < codigoDoFilho.length(); i++) {
                mascaraDoCodigo += "9";
            }
        } else {
            mascaraDoCodigo = "9999";
        }
    }

    @Override
    public String getCaminhoPadrao() {
        return "/grupo-bem/";
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
        List<GrupoBem> filhosDe = grupoBemFacade.getFilhosDe(selecionado);
        if (filhosDe != null) {
            if (!filhosDe.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void excluir() {
        try {
            validarExclusao();
            super.excluir();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarExclusao() {
        ValidacaoException ve = new ValidacaoException();
        List<GrupoBem> filhosDe = grupoBemFacade.getFilhosDe(selecionado);
        if (filhosDe != null && !filhosDe.isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O Grupo Patrimonial, código: " + selecionado.getCodigo() + " - " + selecionado.getDescricao().toUpperCase() + " possui " + filhosDe.size() + " filho(s).");
        }
        if (getVerificarSeGrupoBemPossuiVinculos()){
            ve.adicionarMensagemDeOperacaoNaoPermitida("O Grupo Patrimonial possui vínculos com outros processos, portanto não será permitido a exclusão!");
        }
        ve.lancarException();
    }

    public List<SelectItem> getListaSituacao() {
        List<SelectItem> listaSit = new ArrayList<SelectItem>();
        for (SituacaoCadastralContabil scc : SituacaoCadastralContabil.values()) {
            listaSit.add(new SelectItem(scc, scc.getDescricao()));
        }
        return listaSit;
    }

    public ConverterGenerico getConverterBem() {
        if (converterBem == null) {
            converterBem = new ConverterGenerico(Bem.class, grupoBemFacade);
        }
        return converterBem;
    }

    public List<Bem> completaBem(String parte) {
        return grupoBemFacade.listaBemFiltrandoDescricaoIdentificacao(parte.trim());
    }

    public List<GrupoBem> completaGrupoBemImovel(String parte) {
        return grupoBemFacade.buscarGrupoBemPorTipoBem(parte.trim(), TipoBem.IMOVEIS);
    }

    public List<GrupoBem> completaGrupoBemMovel(String parte) {
        return grupoBemFacade.buscarGrupoBemPorTipoBem(parte.trim(), TipoBem.MOVEIS);
    }

    public List<GrupoBem> completaGrupoBemIntangivel(String parte) {
        return grupoBemFacade.buscarGrupoBemPorTipoBem(parte.trim(), TipoBem.INTANGIVEIS);
    }

    public List<GrupoBem> completaGrupoBem(String parte) {
        return grupoBemFacade.buscarGrupoBemPorTipoBem(parte.trim(), TipoBem.MOVEIS);
    }

    public List<GrupoBem> completaGrupoBemSemTipoReducao(String parte) {
        return grupoBemFacade.recuperarGrupoBemSemTipoReducao(parte.trim());
    }

    public List<SelectItem> tiposBens() {
        List<SelectItem> lista = new ArrayList<SelectItem>();
        lista.add(new SelectItem(null, ""));
        for (TipoBem tipo : TipoBem.values()) {
            lista.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return lista;
    }

    public List<SelectItem> tiposUtilizacaoBem() {
        return Util.getListSelectItem(TipoUtilizacaoBem.getTiposUtilizacaoPorTipoBem(selecionado.getTipoBem()).toArray());
    }

    public boolean getVerificarSeGrupoBemPossuiVinculos() {
        return selecionado.getId() != null && !grupoBemFacade.verificarSeExisteVinculosComGrupoBem(selecionado);
    }

    public String getCodigoDoFilho() {
        return codigoDoFilho;
    }

    public void setCodigoDoFilho(String codigoDoFilho) {
        this.codigoDoFilho = codigoDoFilho;
    }

    public void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getTipoUtilizacaoBem() == null) {
            if (selecionado.getTipoBem().isImovel() || selecionado.getTipoBem().isMovel() || selecionado.getTipoBem().isIntangivel()) {
               ve.adicionarMensagemDeCampoObrigatorio("Selecione um Tipo de Utilização de Bens.");
            }
        }
        ve.lancarException();
    }

    public boolean isPatrimonial() {
        return selecionado.getTipoBem() != null && (selecionado.getTipoBem().isMovel() || selecionado.getTipoBem().isImovel() || selecionado.getTipoBem().isIntangivel());
    }

    public void setTipoUtilizacaoBens() {
        selecionado.setTipoUtilizacaoBem(null);
        if (selecionado.getTipoBem().isMovel() || selecionado.getTipoBem().isIntangivel()) {
            selecionado.setTipoUtilizacaoBem(TipoUtilizacaoBem.BENS_DOMINIAIS);
        }
    }
}
