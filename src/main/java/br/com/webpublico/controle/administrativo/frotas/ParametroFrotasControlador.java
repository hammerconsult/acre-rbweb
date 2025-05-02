package br.com.webpublico.controle.administrativo.frotas;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.GrupoBem;
import br.com.webpublico.entidades.GrupoMaterial;
import br.com.webpublico.entidades.TaxaVeiculo;
import br.com.webpublico.entidades.TipoObjetoFrotaGrupoBem;
import br.com.webpublico.entidades.administrativo.frotas.ParametroFrotas;
import br.com.webpublico.entidades.administrativo.frotas.ParametroFrotasHierarquia;
import br.com.webpublico.entidades.administrativo.frotas.ParametroFrotasTaxa;
import br.com.webpublico.enums.TipoObjetoFrota;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.administrativo.frotas.ParametroFrotasFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by Wellington on 04/02/2016.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-parametro-frotas", pattern = "/frotas/parametro/novo/",
        viewId = "/faces/administrativo/frota/parametro/edita.xhtml"),
    @URLMapping(id = "editar-parametro-frotas", pattern = "/frotas/parametro/editar/#{parametroFrotasControlador.id}/",
        viewId = "/faces/administrativo/frota/parametro/edita.xhtml"),
    @URLMapping(id = "ver-parametro-frotas", pattern = "/frotas/parametro/ver/#{parametroFrotasControlador.id}/",
        viewId = "/faces/administrativo/frota/parametro/visualizar.xhtml"),
    @URLMapping(id = "listar-parametro-frotas", pattern = "/frotas/parametro/listar/",
        viewId = "/faces/administrativo/frota/parametro/lista.xhtml")
})
public class ParametroFrotasControlador extends PrettyControlador<ParametroFrotas> implements CRUD, Serializable {

    @EJB
    private ParametroFrotasFacade facade;
    private TipoObjetoFrotaGrupoBem tipoObjetoFrotaGrupoBem;
    private ParametroFrotasHierarquia parametroFrotasHierarquia;
    private ParametroFrotasTaxa parametroFrotasTaxa;

    public ParametroFrotasControlador() {
        super(ParametroFrotas.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/frotas/parametro/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @URLAction(mappingId = "novo-parametro-frotas", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        ParametroFrotas parametroFrotas = facade.buscarParametroFrotas();
        if (parametroFrotas != null) {
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + parametroFrotas.getId() + "/");
        } else {
            super.novo();
        }
    }

    @URLAction(mappingId = "editar-parametro-frotas", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "ver-parametro-frotas", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @Override
    public void salvar() {
        try {
            validarDadosDoParametro();
            if (isOperacaoNovo()) {
                facade.salvarNovo(selecionado);
            } else {
                facade.salvar(selecionado);
            }
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            redireciona();
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (Exception e) {
            descobrirETratarException(e);
        }

    }

    public Boolean hasParametroExistente() {
        return (facade.lista() != null && facade.lista().size() > 0);
    }

    public List<GrupoBem> completarGrupBem(String parte) {
        return facade.getGrupoBemFacade().listaFiltrandoGrupoBemCodigoDescricao(parte.trim());
    }

    private void validarDadosDoParametro() {
        ValidacaoException ve = new ValidacaoException();
        Util.validarCamposObrigatorios(selecionado, ve);

        if (selecionado.getSegundosRevisaoAVencer().compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo 'Horas Revisão à Vencer' deve ser informado com um valor maior que 0(zero).");
        }
        if (selecionado.getDiasRetiradaVeiculoEquipamento() <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Dias para retirada de veículo/equipamento deve ser maior que 0(zero).");
        }
        ve.lancarException();
    }

    public void novoGrupoBem() {
        this.tipoObjetoFrotaGrupoBem = new TipoObjetoFrotaGrupoBem();
        tipoObjetoFrotaGrupoBem.setInicioVigencia(new Date());
        tipoObjetoFrotaGrupoBem.setTipoObjetoFrota(TipoObjetoFrota.VEICULO);
    }

    public void novoOrgaoTaxa() {
        instanciarParametroFrotasHierarquia();
        instanciarParametroFrotasTaxa();
    }

    private void instanciarParametroFrotasHierarquia() {
        parametroFrotasHierarquia = new ParametroFrotasHierarquia();
    }

    private void instanciarParametroFrotasTaxa() {
        parametroFrotasTaxa = new ParametroFrotasTaxa();
    }

    public void cancelarOrgaoTaxa() {
        parametroFrotasHierarquia = null;
        parametroFrotasTaxa = null;
    }

    public void alterarFrotasHierarquia(ParametroFrotasHierarquia parametroFrotasHierarquia) {
        ParametroFrotasHierarquia clone = new ParametroFrotasHierarquia();
        clone.setParametroFrotas(parametroFrotasHierarquia.getParametroFrotas());
        clone.setId(parametroFrotasHierarquia.getId());
        clone.setCriadoEm(parametroFrotasHierarquia.getCriadoEm());
        clone.setHierarquiaOrganizacional(parametroFrotasHierarquia.getHierarquiaOrganizacional());
        for (ParametroFrotasTaxa taxas : parametroFrotasHierarquia.getItemTaxaVeiculo()) {
            ParametroFrotasTaxa cloneTaxa = new ParametroFrotasTaxa();
            cloneTaxa.setId(taxas.getId());
            cloneTaxa.setParametroFrotasHierarquia(clone);
            cloneTaxa.setTaxaVeiculo(taxas.getTaxaVeiculo());
            cloneTaxa.setCriadoEm(taxas.getCriadoEm());
            Util.adicionarObjetoEmLista(clone.getItemTaxaVeiculo(), cloneTaxa);
        }
        this.parametroFrotasHierarquia = clone;
        instanciarParametroFrotasTaxa();
    }

    public void removerFrotasHierarquia(ParametroFrotasHierarquia parametroFrotasHierarquia) {
        selecionado.getItemParametroFrotasHierarquia().remove(parametroFrotasHierarquia);
    }

    public void removerFrotasTaxa(ParametroFrotasTaxa parametroFrotasTaxa) {
        parametroFrotasHierarquia.getItemTaxaVeiculo().remove(parametroFrotasTaxa);
    }

    public void adicionarSecretaria() {
        try {
            validarHierarquias();
            parametroFrotasHierarquia.setParametroFrotas(selecionado);
            Util.adicionarObjetoEmLista(selecionado.getItemParametroFrotasHierarquia(), parametroFrotasHierarquia);
            cancelarOrgaoTaxa();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarHierarquias() {
        ValidacaoException ve = new ValidacaoException();
        if (parametroFrotasHierarquia != null) {
            if (parametroFrotasHierarquia.getHierarquiaOrganizacional() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Secretaria é obrigatório");
            } else {
                if (parametroFrotasHierarquia.getItemTaxaVeiculo().isEmpty()) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("É necessário informar ao menos uma taxa.");
                }
                for (ParametroFrotasHierarquia hierarquias : selecionado.getItemParametroFrotasHierarquia()) {
                    if (hierarquias.getHierarquiaOrganizacional().equals(parametroFrotasHierarquia.getHierarquiaOrganizacional())
                        && !hierarquias.equals(parametroFrotasHierarquia)) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida("A Secretaria " + parametroFrotasHierarquia.getHierarquiaOrganizacional() + " já foi adicionada.");
                    }
                }
            }

        }
        ve.lancarException();
    }

    public void adicionarTaxas() {
        try {
            validarTaxas();
            parametroFrotasTaxa.setParametroFrotasHierarquia(parametroFrotasHierarquia);
            Util.adicionarObjetoEmLista(parametroFrotasHierarquia.getItemTaxaVeiculo(), parametroFrotasTaxa);
            instanciarParametroFrotasTaxa();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarTaxas() {
        ValidacaoException ve = new ValidacaoException();
        if (parametroFrotasTaxa != null) {
            if (parametroFrotasTaxa.getTaxaVeiculo() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Taxa isenta é obrigatório");
            } else {
                for (ParametroFrotasTaxa taxas : parametroFrotasHierarquia.getItemTaxaVeiculo()) {
                    if (taxas.getTaxaVeiculo().equals(parametroFrotasTaxa.getTaxaVeiculo())
                        && !parametroFrotasTaxa.equals(taxas)) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida("A Taxa isenta " + parametroFrotasTaxa.getTaxaVeiculo().getDescricao() + " já foi adicionada.");
                    }
                }
            }
        }
        ve.lancarException();
    }

    public void cancelarGrupoBem() {
        this.tipoObjetoFrotaGrupoBem = null;
    }

    public void editarGrupoBem(TipoObjetoFrotaGrupoBem tipo) {
        this.tipoObjetoFrotaGrupoBem = (TipoObjetoFrotaGrupoBem) Util.clonarObjeto(tipo);
    }

    public void removerGrupoBem(TipoObjetoFrotaGrupoBem tipo) {
        selecionado.getGruposPatrimoniais().remove(tipo);
    }

    public void adicionarGrupoBem() {
        try {
            tipoObjetoFrotaGrupoBem.setParametroFrotas(selecionado);
            ValidacaoException ve = new ValidacaoException();
            validarCamposObrigatorios(ve);
            if (ve.validou) {
                selecionado.setGruposPatrimoniais(Util.adicionarObjetoEmLista(selecionado.getGruposPatrimoniais(), tipoObjetoFrotaGrupoBem));
                cancelarGrupoBem();
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    private void validarCamposObrigatorios(ValidacaoException ve) {
        Util.validarCampos(tipoObjetoFrotaGrupoBem);

        if (tipoObjetoFrotaGrupoBem.getFimVigencia() != null
            && tipoObjetoFrotaGrupoBem.getFimVigencia().before(tipoObjetoFrotaGrupoBem.getInicioVigencia())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O fim de vigência deve ser posterior ao início de vigência.");
            ve.validou = false;
        }
        ve.lancarException();
        for (TipoObjetoFrotaGrupoBem obj : selecionado.getGruposPatrimoniais()) {
            if (obj.getGrupoPatrimonial().equals(tipoObjetoFrotaGrupoBem.getGrupoPatrimonial())
                && obj.getTipoObjetoFrota().equals(tipoObjetoFrotaGrupoBem.getTipoObjetoFrota())
                && !obj.equals(tipoObjetoFrotaGrupoBem)) {

                if (tipoObjetoFrotaGrupoBem.getInicioVigencia().before(obj.getInicioVigencia())) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe um registro vigente para o grupo e tipo informado, com início de vigência em " + DataUtil.getDataFormatada(obj.getInicioVigencia()));
                    ve.validou = false;

                } else if (tipoObjetoFrotaGrupoBem.getFimVigencia() == null && obj.getFimVigencia() == null) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe um registro vigente para o grupo e tipo informado.");
                    ve.validou = false;

                } else if (tipoObjetoFrotaGrupoBem.getFimVigencia() != null
                    && obj.getFimVigencia() != null
                    && tipoObjetoFrotaGrupoBem.getInicioVigencia().before(obj.getFimVigencia())) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O próximo registro vigente deve iniciar-se posterior à " + DataUtil.getDataFormatada(obj.getFimVigencia()) + ".");
                    ve.validou = false;

                } else {
                    List<TipoObjetoFrotaGrupoBem> list = new ArrayList<>();
                    list.add(obj);
                    if (!DataUtil.isVigenciaValida(tipoObjetoFrotaGrupoBem, list)) {
                        ve.validou = false;
                    }
                }
            }
        }
        ve.lancarException();
    }


    public List<TipoObjetoFrota> getTiposObjetoFrota() {
        List<TipoObjetoFrota> lista = new ArrayList<>();
        lista.addAll(Arrays.asList(TipoObjetoFrota.values()));
        return lista;
    }

    public TipoObjetoFrotaGrupoBem getTipoObjetoFrotaGrupoBem() {
        return tipoObjetoFrotaGrupoBem;
    }

    public void setTipoObjetoFrotaGrupoBem(TipoObjetoFrotaGrupoBem tipoObjetoFrotaGrupoBem) {
        this.tipoObjetoFrotaGrupoBem = tipoObjetoFrotaGrupoBem;
    }

    public List<TaxaVeiculo> completarTaxaObrigatoriaVeiculo(String parte) {
        return facade.getTaxaVeiculoFacade().listarTaxaVeiculo(parte.trim(), true, null);
    }

    public ParametroFrotasHierarquia getParametroFrotasHierarquia() {
        return parametroFrotasHierarquia;
    }

    public void setParametroFrotasHierarquia(ParametroFrotasHierarquia parametroFrotasHierarquia) {
        this.parametroFrotasHierarquia = parametroFrotasHierarquia;
    }

    public ParametroFrotasTaxa getParametroFrotasTaxa() {
        return parametroFrotasTaxa;
    }

    public void setParametroFrotasTaxa(ParametroFrotasTaxa parametroFrotasTaxa) {
        this.parametroFrotasTaxa = parametroFrotasTaxa;
    }
}
