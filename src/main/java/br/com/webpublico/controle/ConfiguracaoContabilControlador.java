/*
 * Codigo gerado automaticamente em Fri Aug 26 16:25:56 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ConfiguracaoContabilFacade;
import br.com.webpublico.util.ExcelUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.StreamedContent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@ManagedBean(name = "configuracaoContabilControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-configuracao-contabil", pattern = "/configuracao-contabil/novo/", viewId = "/faces/admin/configuracaocontabil/edita.xhtml"),
    @URLMapping(id = "editar-configuracao-contabil", pattern = "/configuracao-contabil/editar/#{configuracaoContabilControlador.id}/", viewId = "/faces/admin/configuracaocontabil/edita.xhtml"),
    @URLMapping(id = "ver-configuracao-contabil", pattern = "/configuracao-contabil/ver/#{configuracaoContabilControlador.id}/", viewId = "/faces/admin/configuracaocontabil/visualizar.xhtml"),
    @URLMapping(id = "listar-configuracao-contabil", pattern = "/configuracao-contabil/listar/", viewId = "/faces/admin/configuracaocontabil/lista.xhtml")
})
public class ConfiguracaoContabilControlador extends PrettyControlador<ConfiguracaoContabil> implements Serializable, CRUD {

    @EJB
    private ConfiguracaoContabilFacade configuracaoContabilFacade;
    private ConfiguracaoContabilContaDespesa configuracaoContabilContaDespesa;
    private ConfiguracaoContabilContaReceita configuracaoContabilContaReceita;
    private ConfiguracaoContabilContaContabil contaContabilAjuste;
    private ConfiguracaoContabilContaContabil contaContabilResultado;
    private ConfiguracaoContabilUsuario configuracaoContabilUsuario;
    private ConfiguracaoContabilContaReinf configuracaoContabilContaReinf;
    private ConfiguracaoContabilFonte configuracaoContabilFonte;
    private ConfiguracaoContabilUnidadeDocumentoBloqueado unidadeDocumentoBloqueado;
    private ConfiguracaoContabilArquivoLayout arquivoLayout;

    public ConfiguracaoContabilControlador() {
        super(ConfiguracaoContabil.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return configuracaoContabilFacade;
    }

    @URLAction(mappingId = "ver-configuracao-contabil", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperarHierarquiaDaUnidadeDocumentoBloqueado();
    }

    @URLAction(mappingId = "editar-configuracao-contabil", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        instanciarContasDespesa();
        instanciarContasReceita();
        instanciarContaContabilAjuste();
        instanciarContaContabilResultado();
        recuperarHierarquiaDaUnidadeDocumentoBloqueado();
    }

    @Override
    @URLAction(mappingId = "novo-configuracao-contabil", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        selecionado.setDesde(new Date());
        selecionado.setTipoDesdobramento(TipoDesdobramento.LIQUIDACAO);
        instanciarContasDespesa();
        instanciarContasReceita();
        instanciarContaContabilAjuste();
        instanciarContaContabilResultado();
    }

    public List<SelectItem> getTiposDeUsuarioGestor() {
        return Util.getListSelectItemSemCampoVazio(TipoUsuarioGestor.values());
    }

    public void cancelarUsuario() {
        configuracaoContabilUsuario = null;
    }

    public void instanciarUsuario() {
        configuracaoContabilUsuario = new ConfiguracaoContabilUsuario();
    }

    public List<UsuarioSistema> completarUsuarios(String filtro) {
        return configuracaoContabilFacade.getUsuarioSistemaFacade().buscarTodosUsuariosPorLoginOuNome(filtro.trim());
    }

    public void editarUsuario(ConfiguracaoContabilUsuario configuracaoContabilUsuario) {
        this.configuracaoContabilUsuario = (ConfiguracaoContabilUsuario) Util.clonarObjeto(configuracaoContabilUsuario);
    }

    public void removerUsuario(ConfiguracaoContabilUsuario configuracaoContabilUsuario) {
        selecionado.getUsuariosGestores().remove(configuracaoContabilUsuario);
    }

    public void adicionarUsuario() {
        try {
            validarUsuario();
            configuracaoContabilUsuario.setConfiguracaoContabil(selecionado);
            Util.adicionarObjetoEmLista(selecionado.getUsuariosGestores(), configuracaoContabilUsuario);
            cancelarUsuario();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarUsuario() {
        ValidacaoException ve = new ValidacaoException();
        if (configuracaoContabilUsuario.getUsuarioSistema() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Usuário deve ser informado.");
        } else {
            for (ConfiguracaoContabilUsuario usuario : selecionado.getUsuariosGestores()) {
                if (!usuario.equals(configuracaoContabilUsuario) &&
                    usuario.getUsuarioSistema().equals(configuracaoContabilUsuario.getUsuarioSistema()) &&
                    usuario.getTipoUsuarioGestor().equals(configuracaoContabilUsuario.getTipoUsuarioGestor())) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O usuário informado já está adicionado como " + configuracaoContabilUsuario.getTipoUsuarioGestor().getDescricao());
                }
            }
        }
        ve.lancarException();
    }

    @Override
    public void salvar() {
        try {
            Util.validarCampos(selecionado);
            selecionado.setDesde(new Date());
            selecionado.setTipoDesdobramento(TipoDesdobramento.LIQUIDACAO);
            if (isOperacaoEditar()) {
                configuracaoContabilFacade.salvar(selecionado);
            } else {
                configuracaoContabilFacade.salvarNovo(selecionado);
            }
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            configuracaoContabilFacade.alterarCodigosHierarquiaOrcamentaria(selecionado);
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public void adicionarContaDeDespesa() {
        try {
            validarContaDeDespesa();
            Util.adicionarObjetoEmLista(selecionado.getContasDespesa(), configuracaoContabilContaDespesa);
            instanciarContasDespesa();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private void validarContaDeDespesa() {
        ValidacaoException ve = new ValidacaoException();
        if (configuracaoContabilContaDespesa.getContaDespesa() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Conta de Despesa é obrigatório.");
        } else {
            for (ConfiguracaoContabilContaDespesa contasDespesa : selecionado.getContasDespesa()) {
                if (contasDespesa.getContaDespesa().equals(configuracaoContabilContaDespesa.getContaDespesa()) && !contasDespesa.equals(configuracaoContabilContaDespesa)) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("A conta selecionada já está adicionada.");
                }
            }
        }
        ve.lancarException();
    }

    public void adicionarContaContabilAjuste() {
        try {
            validarContaContabilAjuste();
            contaContabilAjuste.setAjusteResultado(ConfiguracaoContabilContaContabil.AjusteResultado.AJUSTE);
            Util.adicionarObjetoEmLista(selecionado.getContasContabeisTransferencia(), contaContabilAjuste);
            instanciarContaContabilAjuste();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private void validarContaContabilAjuste() {
        Util.validarCampos(contaContabilAjuste);
        ValidacaoException ve = new ValidacaoException();
        for (ConfiguracaoContabilContaContabil configuracaoContabilContaContabil : selecionado.getContasContabeisTransferenciaAjustes()) {
            if (configuracaoContabilContaContabil.getContaContabil().equals(contaContabilAjuste.getContaContabil())
                && !configuracaoContabilContaContabil.equals(contaContabilAjuste)
                && configuracaoContabilContaContabil.getPatrimonioLiquido().equals(contaContabilAjuste.getPatrimonioLiquido())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A conta selecionada já está adicionada.");
            }
        }
        ve.lancarException();
    }

    public List<br.com.webpublico.enums.PatrimonioLiquido> getTiposPatrimoLiquido() {
        return Arrays.asList(br.com.webpublico.enums.PatrimonioLiquido.values());
    }

    public void adicionarContaContabilResultado() {
        try {
            validarContaContabilResultado();
            contaContabilResultado.setAjusteResultado(ConfiguracaoContabilContaContabil.AjusteResultado.RESULTADO);
            Util.adicionarObjetoEmLista(selecionado.getContasContabeisTransferencia(), contaContabilResultado);
            instanciarContaContabilResultado();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private void validarContaContabilResultado() {
        Util.validarCampos(contaContabilResultado);
        ValidacaoException ve = new ValidacaoException();
        for (ConfiguracaoContabilContaContabil configuracaoContabilContaContabil : selecionado.getContasContabeisTransferenciaResultado()) {
            if (configuracaoContabilContaContabil.getContaContabil().equals(contaContabilResultado.getContaContabil())
                && !configuracaoContabilContaContabil.equals(contaContabilResultado)
                && configuracaoContabilContaContabil.getPatrimonioLiquido().equals(contaContabilResultado.getPatrimonioLiquido())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A conta selecionada já está adicionada.");
            }
        }
        ve.lancarException();
    }

    public void adicionarContaDeReceita() {
        try {
            validarContaDeReceita();
            Util.adicionarObjetoEmLista(selecionado.getContasReceita(), configuracaoContabilContaReceita);
            instanciarContasReceita();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private void validarContaDeReceita() {
        ValidacaoException ve = new ValidacaoException();
        if (configuracaoContabilContaReceita.getContaReceita() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Conta de Receita é obrigatório.");
        } else {
            for (ConfiguracaoContabilContaReceita contasReceita : selecionado.getContasReceita()) {
                if (contasReceita.getContaReceita().equals(configuracaoContabilContaReceita.getContaReceita()) && !contasReceita.equals(configuracaoContabilContaReceita)) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("A conta selecionada já está adicionada.");
                }
            }
        }
        ve.lancarException();
    }

    private void instanciarContasDespesa() {
        configuracaoContabilContaDespesa = new ConfiguracaoContabilContaDespesa();
        configuracaoContabilContaDespesa.setConfiguracaoContabil(selecionado);
    }

    private void instanciarContaContabilAjuste() {
        contaContabilAjuste = new ConfiguracaoContabilContaContabil();
        contaContabilAjuste.setConfiguracaoContabil(selecionado);
    }

    private void instanciarContaContabilResultado() {
        contaContabilResultado = new ConfiguracaoContabilContaContabil();
        contaContabilResultado.setConfiguracaoContabil(selecionado);
    }

    private void instanciarContasReceita() {
        configuracaoContabilContaReceita = new ConfiguracaoContabilContaReceita();
        configuracaoContabilContaReceita.setConfiguracaoContabil(selecionado);
    }

    public void removerContaDeDespesa(ConfiguracaoContabilContaDespesa contaDespesa) {
        selecionado.getContasDespesa().remove(contaDespesa);
    }

    public void removerContaDeReceita(ConfiguracaoContabilContaReceita contaReceita) {
        selecionado.getContasReceita().remove(contaReceita);
    }

    public void removerContaContabilAjuste(ConfiguracaoContabilContaContabil configuracaoContabilContaContabil) {
        selecionado.getContasContabeisTransferencia().remove(configuracaoContabilContaContabil);
    }

    public void removerContaContabilResultado(ConfiguracaoContabilContaContabil configuracaoContabilContaContabil) {
        selecionado.getContasContabeisTransferencia().remove(configuracaoContabilContaContabil);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/configuracao-contabil/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public List<ClasseCredor> completarClassesCredor(String parte) {
        return configuracaoContabilFacade.getClasseCredorFacade().listaFiltrando(parte.trim(), "codigo", "descricao");
    }

    public List<Conta> completarContasDeReceita(String filtro) {
        return configuracaoContabilFacade.getContaFacade().listaFiltrandoReceita(filtro.trim(), configuracaoContabilFacade.getSistemaFacade().getExercicioCorrente());
    }

    public List<Conta> completarContasDeDespesa(String filtro) {
        return configuracaoContabilFacade.getContaFacade().listaFiltrandoDespesaCriteria(filtro.trim(), configuracaoContabilFacade.getSistemaFacade().getExercicioCorrente());
    }

    public List<Conta> completarContasContabeis(String filtro) {
        return configuracaoContabilFacade.getContaFacade().listaContasContabeis(filtro.trim(), configuracaoContabilFacade.getSistemaFacade().getExercicioCorrente());
    }

    public ConfiguracaoContabilContaDespesa getConfiguracaoContabilContaDespesa() {
        return configuracaoContabilContaDespesa;
    }

    public void setConfiguracaoContabilContaDespesa(ConfiguracaoContabilContaDespesa configuracaoContabilContaDespesa) {
        this.configuracaoContabilContaDespesa = configuracaoContabilContaDespesa;
    }

    public ConfiguracaoContabilContaReceita getConfiguracaoContabilContaReceita() {
        return configuracaoContabilContaReceita;
    }

    public void setConfiguracaoContabilContaReceita(ConfiguracaoContabilContaReceita configuracaoContabilContaReceita) {
        this.configuracaoContabilContaReceita = configuracaoContabilContaReceita;
    }

    public ConfiguracaoContabilContaContabil getContaContabilAjuste() {
        return contaContabilAjuste;
    }

    public void setContaContabilAjuste(ConfiguracaoContabilContaContabil contaContabilAjuste) {
        this.contaContabilAjuste = contaContabilAjuste;
    }

    public ConfiguracaoContabilContaContabil getContaContabilResultado() {
        return contaContabilResultado;
    }

    public void setContaContabilResultado(ConfiguracaoContabilContaContabil contaContabilResultado) {
        this.contaContabilResultado = contaContabilResultado;
    }

    public ConfiguracaoContabilUsuario getConfiguracaoContabilUsuario() {
        return configuracaoContabilUsuario;
    }

    public void setConfiguracaoContabilUsuario(ConfiguracaoContabilUsuario configuracaoContabilUsuario) {
        this.configuracaoContabilUsuario = configuracaoContabilUsuario;
    }

    public ConfiguracaoContabilContaReinf getConfiguracaoContabilContaReinf() {
        return configuracaoContabilContaReinf;
    }

    public void setConfiguracaoContabilContaReinf(ConfiguracaoContabilContaReinf configuracaoContabilContaReinf) {
        this.configuracaoContabilContaReinf = configuracaoContabilContaReinf;
    }

    public void cancelarContaReinf() {
        configuracaoContabilContaReinf = null;
    }

    public void instanciarContaReinf() {
        configuracaoContabilContaReinf = new ConfiguracaoContabilContaReinf();
    }

    public List<ContaExtraorcamentaria> completarContaReinf(String filtro) {
        return configuracaoContabilFacade.getContaFacade().listaFiltrandoExtraorcamentario(filtro.trim(), configuracaoContabilFacade.getSistemaFacade().getExercicioCorrente());
    }

    public void editarContaReinf(ConfiguracaoContabilContaReinf config) {
        this.configuracaoContabilContaReinf = (ConfiguracaoContabilContaReinf) Util.clonarObjeto(config);
    }

    public void removerContaReinf(ConfiguracaoContabilContaReinf config) {
        selecionado.getContasReinf().remove(config);
    }

    public void adicionarContaReinf() {
        try {
            validarContaReinf();
            configuracaoContabilContaReinf.setConfiguracaoContabil(selecionado);
            Util.adicionarObjetoEmLista(selecionado.getContasReinf(), configuracaoContabilContaReinf);
            cancelarContaReinf();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarContaReinf() {
        ValidacaoException ve = new ValidacaoException();
        if (configuracaoContabilContaReinf.getContaExtra() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo conta deve ser informado.");
        } else {
            for (ConfiguracaoContabilContaReinf config : selecionado.getContasReinf()) {
                if (!config.equals(configuracaoContabilContaReinf) &&
                    config.getContaExtra().getCodigo().equals(configuracaoContabilContaReinf.getContaExtra().getCodigo())) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("A Conta informada já foi adicionado.");
                }
            }
        }
        ve.lancarException();
    }

    private void recuperarHierarquiaDaUnidadeDocumentoBloqueado() {
        if (selecionado.getUnidadesDocumentosBloqueados() != null && !selecionado.getUnidadesDocumentosBloqueados().isEmpty()) {
            for (ConfiguracaoContabilUnidadeDocumentoBloqueado ccudb : selecionado.getUnidadesDocumentosBloqueados()) {
                ccudb.setHierarquiaOrganizacional(configuracaoContabilFacade.getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalPorUnidade(
                    configuracaoContabilFacade.getSistemaFacade().getDataOperacao(),
                    ccudb.getUnidadeOrganizacional(),
                    TipoHierarquiaOrganizacional.ORCAMENTARIA
                ));
            }
        }
    }

    public List<HierarquiaOrganizacional> completarHierarquiasOrcamentarias(String parte) {
        return configuracaoContabilFacade.getHierarquiaOrganizacionalFacade().buscarHierarquiaUsuarioPorTipoData(
            parte,
            configuracaoContabilFacade.getSistemaFacade().getUsuarioCorrente(),
            configuracaoContabilFacade.getSistemaFacade().getDataOperacao(),
            TipoHierarquiaOrganizacional.ORCAMENTARIA
        );
    }

    public void novoUnidadeDocumentoBloqueado() {
        unidadeDocumentoBloqueado = new ConfiguracaoContabilUnidadeDocumentoBloqueado();
    }

    public void adicionarUnidadeDocumentoBloqueado() {
        try {
            atualizarUnidadeComHierarquia();
            validarUnidadeDocumentoBloqueado();
            unidadeDocumentoBloqueado.setConfiguracaoContabil(selecionado);
            Util.adicionarObjetoEmLista(selecionado.getUnidadesDocumentosBloqueados(), unidadeDocumentoBloqueado);
            cancelarUnidadeDocumentoBloqueado();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void atualizarUnidadeComHierarquia() {
        if (unidadeDocumentoBloqueado.getHierarquiaOrganizacional() != null) {
            unidadeDocumentoBloqueado.setUnidadeOrganizacional(unidadeDocumentoBloqueado.getHierarquiaOrganizacional().getSubordinada());
        }
    }

    private void validarUnidadeDocumentoBloqueado() {
        ValidacaoException ve = new ValidacaoException();
        if (unidadeDocumentoBloqueado.getUnidadeOrganizacional() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Unidade Orçamentária deve ser informado.");
        } else {
            for (ConfiguracaoContabilUnidadeDocumentoBloqueado ccudb : selecionado.getUnidadesDocumentosBloqueados()) {
                if (!ccudb.equals(unidadeDocumentoBloqueado) && ccudb.getUnidadeOrganizacional().equals(unidadeDocumentoBloqueado.getUnidadeOrganizacional())) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("A Unidade Orçamentária informada já foi adicionada.");
                }
            }
        }
        ve.lancarException();
    }

    public void cancelarUnidadeDocumentoBloqueado() {
        unidadeDocumentoBloqueado = null;
    }

    public void editarUnidadeDocumentoBloqueado(ConfiguracaoContabilUnidadeDocumentoBloqueado config) {
        this.unidadeDocumentoBloqueado = (ConfiguracaoContabilUnidadeDocumentoBloqueado) Util.clonarObjeto(config);
    }

    public void removerUnidadeDocumentoBloqueado(ConfiguracaoContabilUnidadeDocumentoBloqueado config) {
        selecionado.getUnidadesDocumentosBloqueados().remove(config);
    }

    public ConfiguracaoContabilUnidadeDocumentoBloqueado getUnidadeDocumentoBloqueado() {
        return unidadeDocumentoBloqueado;
    }

    public void setUnidadeDocumentoBloqueado(ConfiguracaoContabilUnidadeDocumentoBloqueado unidadeDocumentoBloqueado) {
        this.unidadeDocumentoBloqueado = unidadeDocumentoBloqueado;
    }

    public ConfiguracaoContabilFonte getConfiguracaoContabilFonte() {
        return configuracaoContabilFonte;
    }

    public void setConfiguracaoContabilFonte(ConfiguracaoContabilFonte configuracaoContabilFonte) {
        this.configuracaoContabilFonte = configuracaoContabilFonte;
    }

    public List<FonteDeRecursos> completarFonteDeRecurso(String parte) {
        return configuracaoContabilFacade.getFonteDeRecursosFacade().listaFiltrandoPorExercicio(
            parte,
            configuracaoContabilFacade.getSistemaFacade().getExercicioCorrente()
        );
    }

    public void novoFonte() {
        configuracaoContabilFonte = new ConfiguracaoContabilFonte();
    }

    public void adicionarFonte() {
        try {
            ValidacaoException ve = new ValidacaoException();
            if (configuracaoContabilFonte.getFonteDeRecursos() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Fonte de Recurso deve ser informado.");
            }
            ve.lancarException();
            configuracaoContabilFonte.setConfiguracaoContabil(selecionado);
            Util.adicionarObjetoEmLista(selecionado.getFontesTesouro(), configuracaoContabilFonte);
            cancelarUnidadeDocumentoBloqueado();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void removerFonte(ConfiguracaoContabilFonte configuracaoContabilFonte) {
        selecionado.getFontesTesouro().remove(configuracaoContabilFonte);
    }

    public ConfiguracaoContabilArquivoLayout getArquivoLayout() {
        return arquivoLayout;
    }

    public void setArquivoLayout(ConfiguracaoContabilArquivoLayout arquivoLayout) {
        this.arquivoLayout = arquivoLayout;
    }

    public void novoArquivoLayout() {
        arquivoLayout = new ConfiguracaoContabilArquivoLayout();
    }

    public void adicionarArquivoLayout() {
        try {
            validarAdicionarArquivoLayout();
            arquivoLayout.setConfiguracaoContabil(selecionado);
            Util.adicionarObjetoEmLista(selecionado.getArquivosLayouts(), arquivoLayout);
            cancelarArquivoLayout();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarAdicionarArquivoLayout() {
        ValidacaoException ve = new ValidacaoException();
        if (arquivoLayout.getArquivo() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Arquivo deve ser informado.");
        }
        if (arquivoLayout.getTipo() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo deve ser informado.");
        }
        if (arquivoLayout.getNumeroAba() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Número Aba deve ser informado.");
        }
        if (arquivoLayout.getNumeroLinhaInicial() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Número Linha Inicial deve ser informado.");
        }
        ve.lancarException();
        if (arquivoLayout.getNumeroAba() <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Número Aba deve ser maior que 0.");
        }
        if (arquivoLayout.getNumeroLinhaInicial() <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Número Linha Inicial deve ser maior que 0.");
        }
        if (!selecionado.getArquivosLayouts().isEmpty()) {
            for (ConfiguracaoContabilArquivoLayout al : selecionado.getArquivosLayouts()) {
                if (!al.equals(arquivoLayout) && al.getTipo().equals(arquivoLayout.getTipo())) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O Tipo selecionado já foi adicionado.");
                }
            }
        }
        ve.lancarException();
    }

    public void removerArquivoLayout(ConfiguracaoContabilArquivoLayout arquivoLayout) {
        selecionado.getArquivosLayouts().remove(arquivoLayout);
    }

    public void cancelarArquivoLayout() {
        arquivoLayout = null;
    }

    public void uploadArquivo(FileUploadEvent evento) {
        try {
            Arquivo arquivo = new Arquivo();
            arquivo.setNome(evento.getFile().getFileName());
            arquivo.setDescricao(evento.getFile().getFileName());
            arquivo.setTamanho(evento.getFile().getSize());
            arquivo.setDescricao(evento.getFile().getFileName());
            arquivo.setInputStream(evento.getFile().getInputstream());
            arquivo.setMimeType(arquivo.getNome().trim().toLowerCase().contains(ExcelUtil.XLSX_EXTENCAO) ? ExcelUtil.XLSX_CONTENTTYPE : ExcelUtil.XLS_CONTENTTYPE);
            arquivoLayout.setArquivo(configuracaoContabilFacade.getArquivoFacade().novoArquivoMemoria(arquivo));
        } catch (Exception ex) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), "Erro ao anexar arquivo: " + ex.getMessage());
        }
    }

    public void limparArquivoDoArquivoLayout() {
        arquivoLayout.setArquivo(null);
    }

    public void editarArquivoLayout(ConfiguracaoContabilArquivoLayout arquivoLayout) {
        this.arquivoLayout = (ConfiguracaoContabilArquivoLayout) Util.clonarObjeto(arquivoLayout);
    }

    public StreamedContent downloadArquivo(Arquivo arquivo) {
        try {
            return configuracaoContabilFacade.downloadArquivo(arquivo);
        } catch (Exception ex) {
            logger.error("Erro ao fazer o download do arquivo ", ex);
            FacesUtil.addOperacaoNaoRealizada("Não foi possível fazer o download do arquivo. Detlhes: " + ex.getMessage());
        }
        return null;
    }

    public List<SelectItem> getTiposArquivosLayouts() {
        return Util.getListSelectItem(TipoConvenioArquivoMensal.values());
    }
}
