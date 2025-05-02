/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.GrupoObjetoCompra;
import br.com.webpublico.entidades.ObjetoCompra;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.enums.TipoCotacao;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.GrupoObjetoCompraFacade;
import br.com.webpublico.negocios.ObjetoCompraFacade;
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
import java.util.ArrayList;
import java.util.List;

/**
 * @author Romanini
 */
@ManagedBean(name = "grupoObjetoCompraControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-grupo-objeto-Compra", pattern = "/grupo-objeto-compra/novo/", viewId = "/faces/administrativo/licitacao/grupoobjetocompra/edita.xhtml"),
    @URLMapping(id = "editar-grupo-objeto-compra", pattern = "/grupo-objeto-compra/editar/#{grupoObjetoCompraControlador.id}/", viewId = "/faces/administrativo/licitacao/grupoobjetocompra/edita.xhtml"),
    @URLMapping(id = "ver-grupo-objeto-compra", pattern = "/grupo-objeto-compra/ver/#{grupoObjetoCompraControlador.id}/", viewId = "/faces/administrativo/licitacao/grupoobjetocompra/visualizar.xhtml"),
    @URLMapping(id = "adiconar-filho-grupo-objeto-compra", pattern = "/grupo-objeto-compra/adicionar-filho/#{grupoObjetoCompraControlador.id}/", viewId = "/faces/administrativo/licitacao/grupoobjetocompra/edita.xhtml"),
    @URLMapping(id = "listar-grupo-objeto-compra", pattern = "/grupo-objeto-compra/listar/", viewId = "/faces/administrativo/licitacao/grupoobjetocompra/lista.xhtml"),
    @URLMapping(id = "pesquisar-grupo-objeto-compra", pattern = "/grupo-objeto-compra/pesquisar/", viewId = "/faces/administrativo/licitacao/grupoobjetocompra/pesquisa.xhtml")
})
public class GrupoObjetoCompraControlador extends PrettyControlador<GrupoObjetoCompra> implements Serializable, CRUD {

    @EJB
    private GrupoObjetoCompraFacade grupoObjetoCompraFacade;
    @EJB
    private ObjetoCompraFacade objetoCompraFacade;
    private Pesquisa pesquisa;

    public GrupoObjetoCompraControlador() {
        super(GrupoObjetoCompra.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return grupoObjetoCompraFacade;
    }

    @URLAction(mappingId = "novo-grupo-objeto-Compra", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setCodigo(grupoObjetoCompraFacade.geraCodigoNovo());
    }

    @URLAction(mappingId = "ver-grupo-objeto-compra", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editar-grupo-objeto-compra", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/grupo-objeto-compra/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "adiconar-filho-grupo-objeto-compra", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoFilho() {
        GrupoObjetoCompra pai = grupoObjetoCompraFacade.recuperar(super.getId());
        super.novo();
        selecionado.setCodigo(grupoObjetoCompraFacade.geraCodigoFilho(pai));
        selecionado.setSuperior(pai);
    }

    @URLAction(mappingId = "pesquisar-grupo-objeto-compra", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void pesquisar() {
        pesquisa = new Pesquisa(grupoObjetoCompraFacade, objetoCompraFacade);
    }

    @Override
    public void excluir() {
        boolean podeExcluir = true;
        List<GrupoObjetoCompra> filhosDe = grupoObjetoCompraFacade.getFilhosDe(selecionado);
        List<ObjetoCompra> objetosDeCompa = grupoObjetoCompraFacade.getObjetosDeCompra(selecionado);

        if (filhosDe != null && !filhosDe.isEmpty()) {
            podeExcluir = false;
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), "O Grupo " + selecionado + " possui " + (filhosDe.size() == 1 ? " " + filhosDe.get(0) + " como filho." : filhosDe.size() + " filhos associados."));
        }

        if (objetosDeCompa != null && !objetosDeCompa.isEmpty()) {
            podeExcluir = false;
            FacesUtil.addError("Não foi possível remover!", "O Grupo " + selecionado + " possui " + objetosDeCompa.size() + " Objeto(s) de Compra associado(s)");
        }

        if (podeExcluir) {
            super.excluir();
        }
    }

    public void consultar() {
        try {
            pesquisa.pesquisar();
        } catch (ValidacaoException e) {
            FacesUtil.printAllFacesMessages(e.getMensagens());
        }
    }

    public Pesquisa getPesquisa() {
        return pesquisa;
    }

    public void setPesquisa(Pesquisa pesquisa) {
        this.pesquisa = pesquisa;
    }

    public List<GrupoObjetoCompra> buscarGrupoObjetoCompraPermanente(String codigoOrDescricao) {
        return grupoObjetoCompraFacade.buscarGrupoObjetoCompraPermanentePorCodigoOrDescricao(codigoOrDescricao);
    }

    public List<SelectItem> buscarTipoCotacao() {
        return Util.getListSelectItem(TipoCotacao.values());
    }

    public class Pesquisa {
        private String grupoObjetoCompra;
        private Integer nivelGrupoObjetoCompra;
        private String objetoCompra;
        private List<Object[]> grupos;
        private GrupoObjetoCompraFacade grupoFacade;
        private ObjetoCompraFacade objetoFacade;
        private ObjetoCompra objeto;

        public Pesquisa(GrupoObjetoCompraFacade grupoFacade, ObjetoCompraFacade objetoFacade) {
            this.grupoFacade = grupoFacade;
            this.objetoFacade = objetoFacade;
        }

        public List<SelectItem> getNiveis() {
            List<SelectItem> retorno = new ArrayList<>();

            retorno.add(new SelectItem(new Integer(1), "1"));
            retorno.add(new SelectItem(new Integer(2), "2"));
            retorno.add(new SelectItem(new Integer(3), "3"));
            retorno.add(new SelectItem(new Integer(4), "4"));

            return retorno;
        }

        public void pesquisar() throws ValidacaoException {
            ValidacaoException ve = new ValidacaoException();
            if ((grupoObjetoCompra == null || grupoObjetoCompra.isEmpty())
                && (objetoCompra == null || objetoCompra.isEmpty())) {
                ve.adicionarMensagemDeOperacaoNaoRealizada("Informe o grupo de objeto de compra ou o objeto de compra.");
                return;
            }

            objeto = null;

            if (objetoCompra != null && !objetoCompra.isEmpty()) {
                objeto = objetoFacade.recuperarPorCodigoOuDescricao(objetoCompra);

                if (objeto == null) {
                    ve.adicionarMensagemDeOperacaoNaoRealizada("Nenhum grupo foi encontrado com os filtros informados.");
                }

                grupos = grupoFacade.recuperarGrupos(objeto.getGrupoObjetoCompra().getCodigo(), nivelGrupoObjetoCompra);
            } else {
                grupos = grupoFacade.recuperarGrupos(grupoObjetoCompra, nivelGrupoObjetoCompra);

                if (grupos == null) {
                    ve.adicionarMensagemDeOperacaoNaoRealizada("Nenhum grupo foi encontrado com os filtros informados.");
                }
            }
            if (ve.temMensagens()) {
                throw ve;
            }
        }

        public Boolean grupoTemNivelParaExibirObjetos(BigDecimal nivel) {
            if (nivel != null && nivelGrupoObjetoCompra != null) {
                return nivel.compareTo(new BigDecimal(nivelGrupoObjetoCompra.toString())) == 0;
            }

            return false;
        }

        public List<Object[]> recuperarObjetosDoGrupo(BigDecimal idDoGrupo) {
            if (objeto != null) {
                List<Object[]> retorno = new ArrayList<>();

                Object[] obj = new Object[2];
                obj[0] = objeto.getCodigo();
                obj[1] = objeto.getDescricao();

                retorno.add(obj);

                return retorno;
            }

            return grupoFacade.recuperarObjetos(idDoGrupo.longValue());
        }

        public String getGrupoObjetoCompra() {
            return grupoObjetoCompra;
        }

        public void setGrupoObjetoCompra(String grupoObjetoCompra) {
            this.grupoObjetoCompra = grupoObjetoCompra;
        }

        public Integer getNivelGrupoObjetoCompra() {
            return nivelGrupoObjetoCompra;
        }

        public void setNivelGrupoObjetoCompra(Integer nivelGrupoObjetoCompra) {
            this.nivelGrupoObjetoCompra = nivelGrupoObjetoCompra;
        }

        public String getObjetoCompra() {
            return objetoCompra;
        }

        public void setObjetoCompra(String objetoCompra) {
            this.objetoCompra = objetoCompra;
        }

        public List<Object[]> getGrupos() {
            return grupos;
        }

        public void setGrupos(List<Object[]> grupos) {
            this.grupos = grupos;
        }
    }
}
