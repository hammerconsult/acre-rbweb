package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TipoBancoArquivoObn;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.UtilRH;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
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

/**
 * Created by Edi on 05/04/2016.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "nova-config-arquivo-obn", pattern = "/configuracao-arquivo-obn/novo/", viewId = "/faces/financeiro/orcamentario/configuracaocontabil/config-arquivo-obn/edita.xhtml"),
    @URLMapping(id = "editar-config-arquivo-obn", pattern = "/configuracao-arquivo-obn/editar/#{configuracaoArquivoObnControlador.id}/", viewId = "/faces/financeiro/orcamentario/configuracaocontabil/config-arquivo-obn/edita.xhtml"),
    @URLMapping(id = "ver-config-arquivo-obn", pattern = "/configuracao-arquivo-obn/ver/#{configuracaoArquivoObnControlador.id}/", viewId = "/faces/financeiro/orcamentario/configuracaocontabil/config-arquivo-obn/visualizar.xhtml"),
    @URLMapping(id = "listar-config-arquivo-obn", pattern = "/configuracao-arquivo-obn/listar/", viewId = "/faces/financeiro/orcamentario/configuracaocontabil/config-arquivo-obn/lista.xhtml")
})
public class ConfiguracaoArquivoObnControlador extends PrettyControlador<ConfiguracaoArquivoObn> implements Serializable, CRUD {

    @EJB
    private ConfiguracaoArquivoObnFacade configuracaoArquivoObnFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;

    private BancoObn bancoObn;
    private List<UnidadeObn> unidadesObn;
    private ContratoObn contratoObn;
    private BancoObn bancoSelecionado;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private Boolean mostrarAdicionarHierarquia;

    public ConfiguracaoArquivoObnControlador() {
        super(ConfiguracaoArquivoObn.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/configuracao-arquivo-obn/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return configuracaoArquivoObnFacade;
    }

    @Override
    @URLAction(mappingId = "nova-config-arquivo-obn", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        bancoObn = new BancoObn();
        unidadesObn = Lists.newArrayList();
        contratoObn = new ContratoObn();

    }

    @Override
    @URLAction(mappingId = "ver-config-arquivo-obn", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
        bancoObn = new BancoObn();
        unidadesObn = Lists.newArrayList();
        contratoObn = new ContratoObn();
        recuperarDescricaoUnidades();
    }

    @Override
    @URLAction(mappingId = "editar-config-arquivo-obn", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
        bancoObn = new BancoObn();
        unidadesObn = Lists.newArrayList();
        contratoObn = new ContratoObn();
        recuperarDescricaoUnidades();

    }

    private void recuperarDescricaoUnidades() {
        List<HierarquiaOrganizacional> arvores = getHierarquiaOrganizacionalFacade().filtraPorNivelTrazendoTodas("", "3", TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), UtilRH.getDataOperacao());
        if (!selecionado.getListaBancosObn().isEmpty()) {
            percorrerHierarquias(arvores);
        }
    }

    private void percorrerHierarquias(List<HierarquiaOrganizacional> arvores) {
        for (BancoObn obn : selecionado.getListaBancosObn()) {
            if (!obn.getContratos().isEmpty()) {
                percorrerContratos(arvores, obn);
            }
        }
    }

    private void percorrerContratos(List<HierarquiaOrganizacional> arvores, BancoObn obn) {
        for (ContratoObn contrato : obn.getContratos()) {
            if (!contrato.getUnidades().isEmpty()) {
                percorrerUnidadesDoContrato(arvores, contrato);
            }
        }
    }

    private void percorrerUnidadesDoContrato(List<HierarquiaOrganizacional> arvores, ContratoObn contrato) {
        for (UnidadeObn unidadeObn : contrato.getUnidades()) {
            for (HierarquiaOrganizacional hierarquiaOrganizacional : arvores) {
                if (unidadeObn.getUnidadeOrganizacional().getId().equals(hierarquiaOrganizacional.getSubordinada().getId())) {
                    unidadeObn.setDescricaoUnidade(hierarquiaOrganizacional.getCodigo() + " - " + hierarquiaOrganizacional.getSubordinada().getDescricao());
                }
            }
        }
    }

    public List<ContaBancariaEntidade> listarContasBancariasEntidade(String parte) {
        return configuracaoArquivoObnFacade.getContaBancariaEntidadeFacade().listaPorCodigo(parte.trim().toLowerCase());
    }

    @Override
    public boolean validaRegrasEspecificas() {
        boolean retorno = true;
        if (selecionado.getListaBancosObn().isEmpty()) {
            FacesUtil.addOperacaoNaoPermitida("É obrigatório adicionar ao menos uma configuração para salvar.");
            retorno = false;
        }
        return retorno;
    }

    @Override
    public void salvar() {
        super.salvar();
    }

    public void adicionar() {
        try {
            validarAdicionar();
            bancoObn.setConfiguracaoArquivoObn(selecionado);
            selecionado.setListaBancosObn(Util.adicionarObjetoEmLista(selecionado.getListaBancosObn(), bancoObn));
            bancoObn = new BancoObn();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void remover(BancoObn b) {
        selecionado.getListaBancosObn().remove(b);
    }

    public void editar(BancoObn b) {
        bancoObn = b;
        selecionado.getListaBancosObn().remove(b);
    }

    private void validarAdicionar() {
        ValidacaoException ve = new ValidacaoException();
        if (bancoObn.getNumeroDoBanco() == null
            || bancoObn.getNumeroDoBanco().trim().equals("")) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Número do Banco deve ser informado.");
        }
        if (bancoObn.getTipoBancoArquivoObn() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Banco deve ser informado.");
        }
        if (bancoObn.getContaBancariaEntidade() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Conta Única deve ser informado.");
        }
        if (ve.temMensagens()) {
            throw ve;
        }
        for (BancoObn banco : selecionado.getListaBancosObn()) {
            if (bancoObn.getNumeroDoBanco().equals(banco.getNumeroDoBanco())
                || bancoObn.getTipoBancoArquivoObn().equals(banco.getTipoBancoArquivoObn())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Existe uma configuração para o Tipo de Banco <b> " + banco.getTipoBancoArquivoObn().getDescricao() + "</b>.");
            }
        }
        if (ve.temMensagens()) {
            throw ve;
        }
    }

    public List<SelectItem> getTipoBancosObn() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, " "));
        for (TipoBancoArquivoObn tipo : TipoBancoArquivoObn.values()) {
            toReturn.add(new SelectItem(tipo, tipo.toString()));
        }
        return toReturn;
    }

    public Boolean isConfiguracaoExistente() {
        ConfiguracaoArquivoObn configuracaoArquivoObn = configuracaoArquivoObnFacade.recuperarConfiguracaoArquivoObn();
        return configuracaoArquivoObn == null;
    }

    public void buscarOrgaosOrcamentaria() {
        List<HierarquiaOrganizacional> hierarquiaOrganizacionals = getHierarquiaOrganizacionalFacade().filtraPorNivelTrazendoTodas("", "3", TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), UtilRH.getDataOperacao());
        for (HierarquiaOrganizacional organizacional : hierarquiaOrganizacionals) {
            UnidadeObn unidade = new UnidadeObn();
            unidade.setContratoObn(contratoObn);
            unidade.setUnidadeOrganizacional(organizacional.getSubordinada());
            unidade.setDescricaoUnidade(organizacional.getCodigo() + " - " + organizacional.getSubordinada().getDescricao());
            boolean encontrouUnidadeIgual = false;
            for (UnidadeObn unidadeObn : getTodasAsUnidadesDoSelecionado()) {
                if (unidadeObn.getUnidadeOrganizacional().equals(unidade.getUnidadeOrganizacional())) {
                    encontrouUnidadeIgual = true;
                }
            }
            if (!encontrouUnidadeIgual) {
                unidadesObn = (Util.adicionarObjetoEmLista(unidadesObn, unidade));
            }
        }
    }

    public List<UnidadeObn> getUnidadesSelecionadas() {
        try {
            return unidadesObn;
        } catch (Exception e) {
            return Lists.newArrayList();
        }

    }

    public boolean containsUnidade(UnidadeObn uni) {
        return contratoObn.getUnidades().contains(uni);
    }

    public boolean containsTodasUnidades() {
        boolean todos = true;
        for (UnidadeObn unidadeObn : getUnidadesObn()) {
            if (!contratoObn.getUnidades().contains(unidadeObn)) {
                todos = false;
            }
        }
        return todos;
    }

    public void removerTodasUnidades() {
        contratoObn.getUnidades().clear();
    }

    public void adicionarTodasUnidades() {
        for (UnidadeObn unidadeObn : getUnidadesObn()) {
            adicionarUnidade(unidadeObn);
        }
    }

    public void removerUnidade(UnidadeObn uni) {
        contratoObn.getUnidades().remove(uni);
    }


    public void adicionarUnidade(UnidadeObn uni) {
        if (!contratoObn.getUnidades().contains(uni)) {
            contratoObn.getUnidades().add(uni);
        }

    }

    public void confirmarContrato() {
        try {
            validarLancamentoContratos();
            contratoObn.setBancoObn(bancoSelecionado);
            bancoSelecionado.setContratos(Util.adicionarObjetoEmLista(bancoSelecionado.getContratos(), contratoObn));
            contratoObn = null;
            unidadesObn = null;
            atualizarDialog();
        } catch (ValidacaoException e) {
            FacesUtil.printAllFacesMessages(e.getMensagens());
            logger.error(" validacao exception " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void atribuirContratoToBanco() {
        contratoObn = null;
    }

    public void atualizarDialog() {
        FacesUtil.executaJavaScript("dlgUnidades.hide();");
        FacesUtil.executaJavaScript("dlgContrato.show();");
    }

    public void validarLancamentoContratos() {
        ValidacaoException ex = new ValidacaoException();
        if (contratoObn.getNumeroContrato() == null || contratoObn.getNumeroContrato().trim().isEmpty()) {
            ex.adicionarMensagemDeCampoObrigatorio("O Número do contrato deve ser informado.");
        }
        if (contratoObn.getNumeroHeaderObn600() == null || contratoObn.getNumeroHeaderObn600().trim().isEmpty()) {
            ex.adicionarMensagemDeCampoObrigatorio("O Número Header Obn600 do contrato deve ser informado.");
        }
        if (!selecionado.getListaBancosObn().isEmpty()) {
            for (BancoObn obn : selecionado.getListaBancosObn()) {
                if (!obn.getContratos().isEmpty()) {
                    for (ContratoObn contratoDaVez : obn.getContratos()) {
                        if (!contratoDaVez.equals(contratoObn)) {
                            for (UnidadeObn uni : contratoObn.getUnidades()) {
                                if (contratoDaVez.getUnidades().contains(uni)) {
                                    ex.adicionarMensagemDeOperacaoNaoPermitida(" A unidade  <b>" + uni.getDescricaoUnidade() + "</b> já possui um contrato com Nº <b>" + contratoDaVez.getNumeroContrato() + "</b> para esse banco.");

                                }
                            }
                        }
                    }
                }
            }
        }
        if (contratoObn.getUnidades().isEmpty()) {
            ex.adicionarMensagemDeOperacaoNaoPermitida("È necessário informar pelo menos uma Unidade.");
        }


        if (ex.temMensagens()) {
            throw ex;
        }
    }

    public List<UnidadeObn> getTodasAsUnidadesDoSelecionado() {
        List<UnidadeObn> retorno = Lists.newArrayList();
        if (!selecionado.getListaBancosObn().isEmpty()) {
            for (BancoObn obn : selecionado.getListaBancosObn()) {
                if (!bancoSelecionado.getContratos().isEmpty()) {
//                if (!obn.getContratos().isEmpty()) {
                    for (ContratoObn contratoDaVez : bancoSelecionado.getContratos()) {
                        if (!contratoDaVez.equals(contratoObn)) {
                            retorno.addAll(contratoDaVez.getUnidades());
                        }
                    }
                }
            }
        }
        return retorno;
    }

    public void novoContrato() {
        mostrarAdicionarHierarquia = false;
        contratoObn = new ContratoObn();
        unidadesObn = Lists.newArrayList();
        buscarOrgaosOrcamentaria();
    }

    public void removerContrato(ContratoObn contrato) {
        bancoSelecionado.getContratos().remove(contrato);
    }

    public void editarContrato(ContratoObn contrato) {
        mostrarAdicionarHierarquia = true;
        hierarquiaOrganizacional = null;
        contratoObn = contrato;
        setUnidadesObn(new ArrayList<UnidadeObn>());
        setUnidadesObn(contrato.getUnidades());
        atualizarDescricaoUnidades();
    }

    public List<HierarquiaOrganizacional> completarHierarquias(String parte) {
        return hierarquiaOrganizacionalFacade.filtraPorNivelTrazendoTodas(parte, "3", TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), UtilRH.getDataOperacao());
    }

    public void adicionarHierarquia() {
        try {
            validarAdicionarHierarquia();
            UnidadeObn unidadeObn = new UnidadeObn();
            unidadeObn.setContratoObn(contratoObn);
            unidadeObn.setUnidadeOrganizacional(hierarquiaOrganizacional.getSubordinada());
            unidadeObn.setDescricaoUnidade(hierarquiaOrganizacional.getCodigo() + " - " + hierarquiaOrganizacional.getSubordinada().getDescricao());
            Util.adicionarObjetoEmLista(unidadesObn, unidadeObn);
            hierarquiaOrganizacional = null;
        } catch (ValidacaoException ve){
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarAdicionarHierarquia() {
        ValidacaoException ve = new ValidacaoException();
        if (hierarquiaOrganizacional == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Unidade Orçamentária deve ser informado.");
        }
        ve.lancarException();
        for (UnidadeObn unidade : unidadesObn) {
            if (unidade.getUnidadeOrganizacional().equals(hierarquiaOrganizacional.getSubordinada())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A Unidade Orçamentária selecionada ja foi adicionada na lista de secretarias.");
                break;
            }
        }
        for (ContratoObn obn : bancoSelecionado.getContratos()) {
            for (UnidadeObn unidadeObn : obn.getUnidades()) {
                if (unidadeObn.getUnidadeOrganizacional().equals(hierarquiaOrganizacional.getSubordinada()) && !unidadeObn.getContratoObn().equals(contratoObn)) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("A Unidade Orçamentária selecionada ja foi adicionada no contrato: " + unidadeObn.getContratoObn());
                    break;
                }
            }
        }
        ve.lancarException();
    }

    private void atualizarDescricaoUnidades() {
        for (UnidadeObn unidade : unidadesObn) {
            if (Strings.isNullOrEmpty(unidade.getDescricaoUnidade())) {
                unidade.setDescricaoUnidade(unidade.getUnidadeOrganizacional().getCodigoDescricao());
            }
        }
    }

    public void cancelarContrato() {
        contratoObn = null;
        unidadesObn = null;
    }


    public void atribuirBancoSelecionado(BancoObn selecionado) {
        bancoSelecionado = selecionado;
    }

    public void visualizarContratos(ContratoObn contrato) {
        contratoObn = contrato;
        setUnidadesObn(new ArrayList<UnidadeObn>());
        setUnidadesObn(contrato.getUnidades());
        atualizarDescricaoUnidades();
    }


    public BancoObn getBancoObn() {
        return bancoObn;
    }

    public void setBancoObn(BancoObn bancoObn) {
        this.bancoObn = bancoObn;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public List<UnidadeObn> getUnidadesObn() {
        return unidadesObn;
    }

    public void setUnidadesObn(List<UnidadeObn> unidadesObn) {
        this.unidadesObn = unidadesObn;
    }


    public ContratoObn getContratoObn() {
        return contratoObn;
    }

    public void setContratoObn(ContratoObn contratoObn) {
        this.contratoObn = contratoObn;
    }


    public BancoObn getBancoSelecionado() {
        return bancoSelecionado;
    }

    public void setBancoSelecionado(BancoObn bancoSelecionado) {
        this.bancoSelecionado = bancoSelecionado;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public Boolean getMostrarAdicionarHierarquia() {
        return mostrarAdicionarHierarquia;
    }

    public void setMostrarAdicionarHierarquia(Boolean mostrarAdicionarHierarquia) {
        this.mostrarAdicionarHierarquia = mostrarAdicionarHierarquia;
    }
}
