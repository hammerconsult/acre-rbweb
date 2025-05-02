/*
 * Codigo gerado automaticamente em Mon Jan 09 17:42:41 BRST 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.ClasseDoAtributo;
import br.com.webpublico.enums.TipoCondominio;
import br.com.webpublico.enums.TipoOcupacaoCondominio;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.CondominioFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.ConverterGenerico;
import br.com.webpublico.util.FacesUtil;
import com.google.common.base.Strings;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.SelectEvent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "condominioControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoCondominio", pattern = "/condominio/novo/",
                viewId = "/faces/tributario/cadastromunicipal/condominio/edita.xhtml"),
        @URLMapping(id = "editarCondominio", pattern = "/condominio/editar/#{condominioControlador.id}/",
                viewId = "/faces/tributario/cadastromunicipal/condominio/edita.xhtml"),
        @URLMapping(id = "listarCondominio", pattern = "/condominio/listar/",
                viewId = "/faces/tributario/cadastromunicipal/condominio/lista.xhtml"),
        @URLMapping(id = "verCondominio", pattern = "/condominio/ver/#{condominioControlador.id}/",
                viewId = "/faces/tributario/cadastromunicipal/condominio/visualizar.xhtml")
})
public class CondominioControlador extends PrettyControlador<Condominio> implements Serializable, CRUD {

    @EJB
    private CondominioFacade condominioFacade;

    private ConverterAutoComplete converterLote;
    private ConverterAutoComplete converterCep;
    private ConverterAutoComplete converterBairro;
    private ConverterAutoComplete converterLogradouro;
    private Converter converterSetor;
    private String cep;
    private String caminho;
    private Bairro bairro;
    private Logradouro logradouro;
    private ConverterAutoComplete converterQuadra;
    private Long codigoAux;

    public CondominioControlador() {
        super(Condominio.class);
    }

    public String getCaminho() {
        return caminho;
    }

    public void setCaminho(String caminho) {
        this.caminho = caminho;
    }

    public String caminho() {
        return caminho;
    }

    public Converter getConverterLote() {
        if (converterLote == null) {
            converterLote = new ConverterAutoComplete(Lote.class, condominioFacade.getLoteFacade());
        }
        return converterLote;
    }

    public Converter getConverterCep() {
        if (converterCep == null) {
            this.converterCep = new ConverterAutoComplete(EnderecoCorreio.class, condominioFacade.getEnderecoCorreioFacade());
        }
        return converterCep;
    }

    public CondominioFacade getFacade() {
        return condominioFacade;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public AbstractFacade getFacede() {
        return condominioFacade;
    }

    public List<SelectItem> getTiposDeLote() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(0, " "));
        toReturn.add(new SelectItem(1, "Loteamento"));
        toReturn.add(new SelectItem(2, "Município"));
        toReturn.add(new SelectItem(3, "Loteamento/Município"));
        return toReturn;
    }

    public List<EnderecoCorreio> completaCep(String parte) {
        return condominioFacade.getEnderecoCorreioFacade().retornaEnderecoCorreioPorCep(parte.trim());
    }

    public void adicionaLoteCondominio() {
        if (validaLote()) {
            selecionado.getLoteCondominio().setCondominio(selecionado);
            selecionado.getLotes().add(selecionado.getLoteCondominio());

            limpaDadosLote();
        }
    }

    private void limpaDadosLote() {
        selecionado.setLoteCondominio(new LoteCondominio());
        selecionado.setSetor(null);
        selecionado.setQuadra(null);
        selecionado.setBairro(null);
        selecionado.setLogradouro(null);
        selecionado.setCep("");
    }

    public void removeLoteCondominio(ActionEvent e) {
        selecionado.setLoteCondominio((LoteCondominio) e.getComponent().getAttributes().get("objeto"));
        selecionado.getLotes().remove(selecionado.getLoteCondominio());
        limpaDadosLote();
    }

    public List<SelectItem> getTipoCondominio() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, " "));
        for (TipoCondominio tipoCondominio : TipoCondominio.values()) {
            toReturn.add(new SelectItem(tipoCondominio, tipoCondominio.getDescricao()));
        }
        return toReturn;
    }

    public void excluirSelecionado() {
        this.condominioFacade.remover(selecionado);
    }

    public List<SelectItem> getTipoOcupacao() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, " "));
        for (TipoOcupacaoCondominio tipoOcupacaoCondominio : TipoOcupacaoCondominio.values()) {
            toReturn.add(new SelectItem(tipoOcupacaoCondominio, tipoOcupacaoCondominio.getDescricao()));
        }
        return toReturn;
    }

    public boolean validaLote() {
        boolean valida = true;
        if (selecionado.getLoteCondominio() == null || (selecionado.getLoteCondominio().getLote() == null || selecionado.getLoteCondominio().getLote().getId() == null)) {
            FacesUtil.addWarn("Atenção!", "O Lote é um campo obrigatório.");
            valida = false;
        } else {
            for (LoteCondominio lote : selecionado.getLotes()) {
                if (selecionado.getLoteCondominio().getLote() != null && selecionado.getLoteCondominio().getLote().equals(lote.getLote())) {
                    FacesUtil.addWarn("Atenção!", "O Lote selecionado já foi adicionado a este condomínio.");
                    valida = false;
                    break;
                }
            }
        }
        return valida;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/condominio/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public void salvar() {
        try {
            if(isOperacaoEditar()) {
                if(selecionado.getCodigoCondominio() != null && selecionado.getCodigoCondominio().compareTo(codigoAux) != 0) {
                    selecionado.setCodigo(null);
                    selecionado.setCodigoManual(selecionado.getCodigoCondominio());
                }
            }
            if(isOperacaoNovo()) {
                if(codigoAux != null && selecionado.getCodigo() != null && codigoAux.compareTo(selecionado.getCodigo()) != 0L) {
                    selecionado.setCodigo(null);
                    selecionado.setCodigoManual(codigoAux);
                }
            }
            validarCondominio();
            super.salvar();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada("Erro ao salvar Condomínio. Detalhes: " + e.getMessage());
            logger.error("Erro ao salvar Condominio. ", e);
        }
    }

    public void validarCondominio() {
        ValidacaoException ve = new ValidacaoException();

        if(Strings.isNullOrEmpty(selecionado.getDescricao())) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Descrição deve ser informado.");
        }
        if(selecionado.getTipoCondominio() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Condomínio deve ser informado.");
        }
        if(selecionado.getTipoOcupacaoCondominio() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Ocupação deve ser informado.");
        }
        if(selecionado.getLogradouroBairro().getBairro() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Bairro deve ser informado.");
        }
        if(selecionado.getLogradouroBairro().getLogradouro() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Logradouro deve ser informado.");
        }
        if(selecionado.getLotes().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A lista de Lotes deve possuir ao menos um Lote.");
        }
        if(selecionado.getCodigo() == null && selecionado.getCodigoManual() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Código deve ser informado.");
        }
        if(selecionado.getCodigo() != null && condominioFacade.isCodigoAdicionado(selecionado.getCodigo(), selecionado.getId())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O Código: " + selecionado.getCodigo() + " pertence a outro Condomínio.");
        }
        if(selecionado.getCodigoManual() != null && condominioFacade.isCodigoAdicionado(selecionado.getCodigoManual(), selecionado.getId())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O Código: " + selecionado.getCodigoManual() + " pertence a outro Condomínio.");
        }

        ve.lancarException();
    }

    @URLAction(mappingId = "novoCondominio", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setLoteCondominio(new LoteCondominio());
        selecionado.setCodigo(condominioFacade.buscarProximoCodigo());
        codigoAux = condominioFacade.buscarProximoCodigo();
        bairro = new Bairro();
        logradouro = new Logradouro();
    }

    @URLAction(mappingId = "verCondominio", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperarEditarVer();
    }

    @URLAction(mappingId = "editarCondominio", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        codigoAux = selecionado.getCodigoCondominio();
        selecionado.setLoteCondominio(new LoteCondominio());
        recuperarEditarVer();
    }

    private void recuperarEditarVer() {
        if (selecionado.getLogradouroBairro() != null) {
            bairro = selecionado.getLogradouroBairro().getBairro();
            logradouro = selecionado.getLogradouroBairro().getLogradouro();
            cep = selecionado.getLogradouroBairro().getCep();
        } else {
            bairro = new Bairro();
            logradouro = new Logradouro();
            cep = "";
        }
    }

    public void marcaEsseComoPrincipal(LoteCondominio lote) {
        for (LoteCondominio l : selecionado.getLotes()) {
            if (!lote.getLote().equals(l.getLote())) {
                l.setPrincipal(false);
            }
        }
    }

    public List<Logradouro> completaLogradouro(String parte) {
        return condominioFacade.getLogradouroFacade().listarPorBairro(bairro, parte.trim());
    }

    public void carregaCEP(SelectEvent evt) {
        logradouro = (Logradouro) evt.getObject();
        selecionado.setLogradouroBairro(condominioFacade.getLogradouroFacade().recuperaLogradourBairro(bairro, logradouro));
        //cep = selecionado.getLogradouroBairro().getCep();
    }

    public void limpaCep() {
        if (logradouro == null) {
            cep = null;
        }
    }

    public List<Cidade> completaCidade(String parte) {
        return condominioFacade.getCidadeFacade().listaFiltrando(parte.trim(), "nome");
    }

    public List<Bairro> completaBairro(String parte) {
        return condominioFacade.getBairroFacade().listaFiltrando(parte.trim(), "descricao");
    }

    public void limpaLogradouro() {
        logradouro = null;
        cep = "";
    }

    public Converter getConverterBairro() {
        if (converterBairro == null) {
            converterBairro = new ConverterAutoComplete(Bairro.class, condominioFacade.getBairroFacade());
        }
        return converterBairro;
    }

    public Converter getConverterLogradouro() {
        if (converterLogradouro == null) {
            converterLogradouro = new ConverterAutoComplete(Logradouro.class, condominioFacade.getLogradouroFacade());
        }
        return converterLogradouro;
    }

    public Bairro getBairro() {
        return bairro;
    }

    public void setBairro(Bairro bairro) {
        this.bairro = bairro;
    }

    public Logradouro getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(Logradouro logradouro) {
        this.logradouro = logradouro;
    }

    public Converter getConverterSetor() {
        if (converterSetor == null) {
            converterSetor = new ConverterGenerico(Setor.class, condominioFacade.getSetorFacade());
        }
        return converterSetor;
    }

    public List<SelectItem> getSetores() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        List<Setor> setores = condominioFacade.getSetorFacade().listaSetoresOrdenadosPorCodigo();
        for (Setor obj : setores) {
            toReturn.add(new SelectItem(obj, obj.toString()));
        }
        return toReturn;
    }

    public Converter getConverterQuadra() {
        if (converterQuadra == null) {
            converterQuadra = new ConverterAutoComplete(Quadra.class, condominioFacade.getQuadraFacade());
        }
        return converterQuadra;
    }

    public List<Quadra> completaQuadra(String parte) {
        if (selecionado.getSetor() != null && selecionado.getSetor().getId() != null) {
            return condominioFacade.getQuadraFacade().listaFiltrandoPorSetor(parte.trim(), selecionado.getSetor(), 2);
        }
        return new ArrayList<>();
    }

    public List<Lote> completaLote(String parte) {
        if (selecionado.getQuadra() != null && selecionado.getQuadra().getId() != null) {
            return condominioFacade.getLoteFacade().listaFiltrandoFk("quadra", selecionado.getQuadra().getId(), parte.trim(), "codigoLote", "codigoAnterior");
        }
        return new ArrayList<>();
    }

    public void selecionaLote(SelectEvent evento) {
        if (getSelecionado().getLoteCondominio() != null && getSelecionado().getLoteCondominio().getLote() != null && getSelecionado().getLoteCondominio().getLote().getId() != null) {
            Lote l = condominioFacade.getLoteFacade().recuperar(getSelecionado().getLoteCondominio().getLote().getId());
            selecionado.setQuadra(getSelecionado().getLoteCondominio().getLote().getQuadra());
            selecionado.setSetor(getSelecionado().getLoteCondominio().getLote().getSetor());
            selecionado.setListaTestadas(l.getTestadas());
            Testada testadaPrincipal = l.getTestadaPrincipal();
            if (testadaPrincipal != null) {
                Face faceTestadaPrincipal = testadaPrincipal.getFace();
                if (faceTestadaPrincipal != null) {
                    if (faceTestadaPrincipal.getLogradouroBairro() != null) {
                        getSelecionado().setLogradouro(l.getTestadaPrincipal().getFace().getLogradouroBairro().getLogradouro());
                        getSelecionado().setBairro(l.getTestadaPrincipal().getFace().getLogradouroBairro().getBairro());
                        getSelecionado().setCep(l.getTestadaPrincipal().getFace().getCep());
                    }
                }
            }
            condominioFacade.getAtributoFacade().completaAtributos(getSelecionado().getLoteCondominio().getLote().getAtributos(), ClasseDoAtributo.LOTE);
        }
    }

    public List<FaceServico> servicosUrbanos(Face face) {
        return condominioFacade.getServicosFace(face);
    }
}
