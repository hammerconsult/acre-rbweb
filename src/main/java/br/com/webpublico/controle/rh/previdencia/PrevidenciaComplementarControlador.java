package br.com.webpublico.controle.rh.previdencia;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.previdencia.ItemPrevidenciaComplementar;
import br.com.webpublico.entidades.rh.previdencia.PrevidenciaComplementar;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.enums.rh.previdencia.TipoRegimeTributacaoBBPrev;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.rh.previdencia.PrevidenciaComplementarFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.UtilRH;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@ManagedBean(name = "previdenciaComplementarControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoPrevidenciaComplementar", pattern = "/previdencia-complementar/novo/", viewId = "/faces/rh/previdencia/previdenciacomplementar/edita.xhtml"),
    @URLMapping(id = "editarPrevidenciaComplementar", pattern = "/previdencia-complementar/editar/#{previdenciaComplementarControlador.id}/", viewId = "/faces/rh/previdencia/previdenciacomplementar/edita.xhtml"),
    @URLMapping(id = "listarPrevidenciaComplementar", pattern = "/previdencia-complementar/listar/", viewId = "/faces/rh/previdencia/previdenciacomplementar/lista.xhtml"),
    @URLMapping(id = "verPrevidenciaComplementar", pattern = "/previdencia-complementar/ver/#{previdenciaComplementarControlador.id}/", viewId = "/faces/rh/previdencia/previdenciacomplementar/visualizar.xhtml")
})
public class PrevidenciaComplementarControlador extends PrettyControlador<PrevidenciaComplementar> implements Serializable, CRUD {

    @EJB
    private PrevidenciaComplementarFacade facade;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    private ItemPrevidenciaComplementar itemPrevidenciaComplementarSelecionado;
    Map<UnidadeOrganizacional, HierarquiaOrganizacional> mapHierarquias;

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    public PrevidenciaComplementarControlador() {
        super(PrevidenciaComplementar.class);
    }


    @Override
    public String getCaminhoPadrao() {
        return "/previdencia-complementar/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novoPrevidenciaComplementar", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        mapHierarquias = new HashMap<>();
    }

    @Override
    @URLAction(mappingId = "editarPrevidenciaComplementar", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
        ordenarItensPrevidenciaComplementar(selecionado.getItens());
        mapHierarquias = new HashMap<>();
    }

    @Override
    @URLAction(mappingId = "verPrevidenciaComplementar", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
        ordenarItensPrevidenciaComplementar(selecionado.getItens());
        mapHierarquias = new HashMap<>();
    }

    @Override
    public void salvar() {
        try {
            validarCampos();
            super.salvar();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            logger.error("Erro ", e);
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        }
    }

    public void validarCampos() throws ValidacaoException {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getContratoFP() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Servidor deve ser informado.");
        }
        if (selecionado.getItens() == null || selecionado.getItens().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Deve ser adicionado ao menos uma previdência complementar..");
        }

        PrevidenciaComplementar previdencia = facade.buscarPrevidenciaComplementarPorContrato(selecionado.getContratoFP());
        if (previdencia != null && !previdencia.equals(selecionado)) {
            String url = "<b><a href='" + FacesUtil.getRequestContextPath() + "/previdencia-complementar/ver/" + previdencia.getId() + "' target='_blank'>" + "existente." + "</a></b>";
            ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe registro de previdência complementar para o contrato selecionado, novas previdências devem ser adicionadas ao ." + url);
        }
        ve.lancarException();
    }

    public void novoItemPrevidenciaComplementar() {
        itemPrevidenciaComplementarSelecionado = new ItemPrevidenciaComplementar(selecionado);
    }

    public void adicionarItemPrevidenciaComplementar() {
        try {
            validarCamposPrevidenciaComplementar();
            Util.adicionarObjetoEmLista(selecionado.getItens(), itemPrevidenciaComplementarSelecionado);
            cancelarItemPrevidenciaComplementar();
            ordenarItensPrevidenciaComplementar(selecionado.getItens());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void validarCamposPrevidenciaComplementar() throws ValidacaoException {
        ValidacaoException ve = new ValidacaoException();
        if (itemPrevidenciaComplementarSelecionado.getInicioVigencia() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Início da Vigência deve ser informado.");
        } else if (itemPrevidenciaComplementarSelecionado.getFinalVigencia() != null && itemPrevidenciaComplementarSelecionado.getFinalVigencia().before(itemPrevidenciaComplementarSelecionado.getInicioVigencia())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O Final da Vigência não pode ser menor que o Início da Vigência.");
        } else if (isPeriodoConcomitante()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A vigência do novo registro está concomitante com outro já existente.");
        }
        if (itemPrevidenciaComplementarSelecionado.getAliquotaServidor() == null || BigDecimal.ZERO.equals(itemPrevidenciaComplementarSelecionado.getAliquotaServidor()) || new BigDecimal("0.00").equals(itemPrevidenciaComplementarSelecionado.getAliquotaServidor())) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Alíquota Servidor deve ser informado.");
        } else if (itemPrevidenciaComplementarSelecionado.getAliquotaServidor().compareTo(new BigDecimal(100)) > 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Alíquota Servidor não pode ser superior a 100%.");
        }
        if (itemPrevidenciaComplementarSelecionado.getAliquotaPatrocinador() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Alíquota Patrocinador deve ser informado.");
        }
        if (itemPrevidenciaComplementarSelecionado.getRegimeTributacaoBBPrev() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Regime de Tributação deve ser informado.");
        }
        ve.lancarException();
    }

    public boolean isPeriodoConcomitante() {
        LocalDate inicioVigenciaAdicionado = DataUtil.dateToLocalDate(itemPrevidenciaComplementarSelecionado.getInicioVigencia());
        LocalDate finalVigenciaAdicionado = itemPrevidenciaComplementarSelecionado.getFinalVigencia() != null ? DataUtil.dateToLocalDate(itemPrevidenciaComplementarSelecionado.getFinalVigencia()) : null;

        for (ItemPrevidenciaComplementar item : selecionado.getItens()) {
            LocalDate inicioVigencia = DataUtil.dateToLocalDate(item.getInicioVigencia());
            LocalDate finalVigencia = item.getFinalVigencia() != null ? DataUtil.dateToLocalDate(item.getFinalVigencia()) : null;

            if (!item.equals(itemPrevidenciaComplementarSelecionado)
                && ((inicioVigenciaAdicionado.compareTo(inicioVigencia) <= 0 && (finalVigenciaAdicionado == null || finalVigenciaAdicionado.compareTo(inicioVigencia) >= 0))
                || (inicioVigenciaAdicionado.isAfter(inicioVigencia) && (finalVigencia == null || inicioVigenciaAdicionado.compareTo(finalVigencia) <= 0))
                || (finalVigenciaAdicionado != null && finalVigenciaAdicionado.compareTo(inicioVigencia) >= 0 && (finalVigencia == null || (finalVigenciaAdicionado.isAfter(inicioVigencia) && finalVigenciaAdicionado.isBefore(finalVigencia)))))) {
                return true;
            }
        }
        return false;
    }

    public void cancelarItemPrevidenciaComplementar() {
        itemPrevidenciaComplementarSelecionado = null;
    }

    public void removerItemPrevidenciaComplementar(ItemPrevidenciaComplementar item) {
        selecionado.getItens().remove(item);
    }

    public void alterarItemPrevidenciaComplementar(ItemPrevidenciaComplementar item) {
        itemPrevidenciaComplementarSelecionado = (ItemPrevidenciaComplementar) Util.clonarObjeto(item);
    }

    public List<ContratoFP> completarContratoFP(String parte) {
        return contratoFPFacade.buscarContratosFiltrandoMatriculaOrNomeOrCPFPorTipoPrevidencia(parte.trim(), TipoPrevidenciaFP.RPPS);
    }

    public ItemPrevidenciaComplementar getItemPrevidenciaComplementarSelecionado() {
        return itemPrevidenciaComplementarSelecionado;
    }

    public void setItemPrevidenciaComplementarSelecionado(ItemPrevidenciaComplementar itemPrevidenciaComplementarSelecionado) {
        this.itemPrevidenciaComplementarSelecionado = itemPrevidenciaComplementarSelecionado;
    }

    public String buscarHierarquiaDaLotacao(ContratoFP contratoFP) {
        if (contratoFP != null) {
            contratoFP = contratoFPFacade.recuperarContratoComLotacaoFuncional(contratoFP.getId());
            LotacaoFuncional lotacaoFuncional = contratoFP.getLotacaoFuncionalVigente();
            if (lotacaoFuncional != null) {
                if (mapHierarquias.containsKey(lotacaoFuncional.getUnidadeOrganizacional())) {
                    return mapHierarquias.get(lotacaoFuncional.getUnidadeOrganizacional()).toString();
                }
                HierarquiaOrganizacional ho = hierarquiaOrganizacionalFacade.getHierarquiaDaUnidade(TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), lotacaoFuncional.getUnidadeOrganizacional(), UtilRH.getDataOperacao());
                mapHierarquias.put(lotacaoFuncional.getUnidadeOrganizacional(), ho);
                return ho.toString();
            }
            return "";
        }
        return "";
    }

    private void ordenarItensPrevidenciaComplementar(List<ItemPrevidenciaComplementar> itens) {
        Collections.sort(itens, new Comparator<ItemPrevidenciaComplementar>() {
            @Override
            public int compare(ItemPrevidenciaComplementar o1, ItemPrevidenciaComplementar o2) {
                return o1.getInicioVigencia().compareTo(o2.getInicioVigencia());
            }
        });
    }

    public void atribuirAliquotaPatrocinador() {
        if (itemPrevidenciaComplementarSelecionado.getAliquotaServidor() != null) {
            itemPrevidenciaComplementarSelecionado.setAliquotaPatrocinador(itemPrevidenciaComplementarSelecionado.getAliquotaServidor().compareTo(BigDecimal.valueOf(8.5)) <= 0 ? itemPrevidenciaComplementarSelecionado.getAliquotaServidor() : BigDecimal.valueOf(8.5));
        }
    }

    public List<SelectItem> getTiposRegimeTributacaoBBPrev() {
        return Util.getListSelectItem(TipoRegimeTributacaoBBPrev.values());
    }
}
