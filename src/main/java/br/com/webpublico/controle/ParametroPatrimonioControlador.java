package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.administrativo.patrimonio.SecretariaControleSetorial;
import br.com.webpublico.entidades.administrativo.patrimonio.UnidadeControleSetorial;
import br.com.webpublico.enums.TipoBem;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ParametroPatrimonioFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Desenvolvimento
 * Date: 23/05/14
 * Time: 14:10
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean(name = "parametroPatrimonioControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoParametroPatrimonio", pattern = "/parametro-patrimonio/novo/", viewId = "/faces/administrativo/patrimonio/parametropatrimonio/edita.xhtml"),
    @URLMapping(id = "editarParametroPatrimonio", pattern = "/parametro-patrimonio/editar/#{parametroPatrimonioControlador.id}/", viewId = "/faces/administrativo/patrimonio/parametropatrimonio/edita.xhtml"),
    @URLMapping(id = "listarParametroPatrimonio", pattern = "/parametro-patrimonio/listar/", viewId = "/faces/administrativo/patrimonio/parametropatrimonio/lista.xhtml"),
    @URLMapping(id = "verParametroPatrimonio", pattern = "/parametro-patrimonio/ver/#{parametroPatrimonioControlador.id}/", viewId = "/faces/administrativo/patrimonio/parametropatrimonio/visualizar.xhtml")
})
public class ParametroPatrimonioControlador extends PrettyControlador<ParametroPatrimonio> implements Serializable, CRUD {

    @EJB
    private ParametroPatrimonioFacade parametroPatrimonioFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    private ConverterAutoComplete converterResponsavelPatrimonio;
    private boolean mostrarBotaoNovo;
    private ControleDaSequenciaIdentificacaoPatrimonio controleDaSequenciaIdentificacaoPatrimonioMovel;
    private ControleDaSequenciaIdentificacaoPatrimonio controleDaSequenciaIdentificacaoPatrimonioImovel;
    private SecretariaControleSetorial secretariaControleSetorial;
    private UnidadeControleSetorial unidadeControleSetorial;

    public ParametroPatrimonioControlador() {
        super(ParametroPatrimonio.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return parametroPatrimonioFacade;
    }

    @URLAction(mappingId = "novoParametroPatrimonio", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setDataCriacao(sistemaFacade.getDataOperacao());
        selecionado.setUsuarioSistema(sistemaFacade.getUsuarioCorrente());
        controleDaSequenciaIdentificacaoPatrimonioMovel = new ControleDaSequenciaIdentificacaoPatrimonio();
        controleDaSequenciaIdentificacaoPatrimonioImovel = new ControleDaSequenciaIdentificacaoPatrimonio();
    }

    public void novaSecretaria() {
        instanciarSecretariaControleSetorial();
        instanciarUnidadeControleSetorial();
    }

    public void atribuirNullSecretaria() {
        this.secretariaControleSetorial = null;
    }

    private void instanciarSecretariaControleSetorial() {
        secretariaControleSetorial = new SecretariaControleSetorial();
        secretariaControleSetorial.setParametroPatrimonio(selecionado);
    }

    private void instanciarUnidadeControleSetorial() {
        unidadeControleSetorial = new UnidadeControleSetorial();
        unidadeControleSetorial.setSecretariaControleSetorial(this.secretariaControleSetorial);
    }

    @URLAction(mappingId = "verParametroPatrimonio", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarParametroPatrimonio", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        controleDaSequenciaIdentificacaoPatrimonioMovel = new ControleDaSequenciaIdentificacaoPatrimonio();
        controleDaSequenciaIdentificacaoPatrimonioMovel.recarregarEntidadesGeradorasSequenciaPropriaMovel(selecionado.getEntidadeGeradoras());
        controleDaSequenciaIdentificacaoPatrimonioImovel = new ControleDaSequenciaIdentificacaoPatrimonio();
        controleDaSequenciaIdentificacaoPatrimonioImovel.recarregarEntidadesGeradorasSequenciaPropriaImovel(selecionado.getEntidadeGeradoras());
    }

    @URLAction(mappingId = "listarParametroPatrimonio", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void lista() {
        mostrarBotaoNovo = parametroPatrimonioFacade.naoExisteParametro();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/parametro-patrimonio/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public void salvar() {
        try {
            Web.limpaNavegacao();
            Util.validarCampos(selecionado);
            if (isOperacaoNovo()) {
                getFacede().salvarNovo(selecionado);
            } else {
                parametroPatrimonioFacade.salvar(selecionado);
            }
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            redireciona();
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    public ControleDaSequenciaIdentificacaoPatrimonio getControleDaSequenciaIdentificacaoPatrimonioMovel() {
        return controleDaSequenciaIdentificacaoPatrimonioMovel;
    }

    public void setControleDaSequenciaIdentificacaoPatrimonioMovel(ControleDaSequenciaIdentificacaoPatrimonio controleDaSequenciaIdentificacaoPatrimonioMovel) {
        this.controleDaSequenciaIdentificacaoPatrimonioMovel = controleDaSequenciaIdentificacaoPatrimonioMovel;
    }

    public ControleDaSequenciaIdentificacaoPatrimonio getControleDaSequenciaIdentificacaoPatrimonioImovel() {
        return controleDaSequenciaIdentificacaoPatrimonioImovel;
    }

    public void setControleDaSequenciaIdentificacaoPatrimonioImovel(ControleDaSequenciaIdentificacaoPatrimonio controleDaSequenciaIdentificacaoPatrimonioImovel) {
        this.controleDaSequenciaIdentificacaoPatrimonioImovel = controleDaSequenciaIdentificacaoPatrimonioImovel;
    }

    public boolean isMostrarBotaoNovo() {
        return mostrarBotaoNovo;
    }

    public void setMostrarBotaoNovo(boolean mostrarBotaoNovo) {
        this.mostrarBotaoNovo = mostrarBotaoNovo;
    }


    public SecretariaControleSetorial getSecretariaControleSetorial() {
        return secretariaControleSetorial;
    }

    public void setSecretariaControleSetorial(SecretariaControleSetorial secretariaControleSetorial) {
        this.secretariaControleSetorial = secretariaControleSetorial;
    }

    public UnidadeControleSetorial getUnidadeControleSetorial() {
        return unidadeControleSetorial;
    }

    public void setUnidadeControleSetorial(UnidadeControleSetorial unidadeControleSetorial) {
        this.unidadeControleSetorial = unidadeControleSetorial;
    }

    public List<HierarquiaOrganizacional> completarHierarquiaFilhasAdministrativa(String parte) {
        if (secretariaControleSetorial.getHierarquiaOrganizacional() != null) {
            return parametroPatrimonioFacade.getHierarquiaOrganizacionalFacade().listaHierarquiasFilhasDeUmaHierarquiaOrganizacionalAdministrativa(
                secretariaControleSetorial.getHierarquiaOrganizacional(),
                selecionado.getUsuarioSistema(),
                parametroPatrimonioFacade.getSistemaFacade().getDataOperacao(),
                parte.trim());
        }
        return Lists.newArrayList();
    }

    public void alterarSecretaria(SecretariaControleSetorial secretaria) {
        this.secretariaControleSetorial = (SecretariaControleSetorial) Util.clonarEmNiveis(secretaria, 1);
        instanciarUnidadeControleSetorial();
    }

    public void removerSecretaria(SecretariaControleSetorial secretaria) {
        this.selecionado.getSecretarias().remove(secretaria);
    }

    public void adicionarSecretaria() {
        try {
            if (secretariaControleSetorial.getHierarquiaOrganizacional() != null) {
                secretariaControleSetorial.setUnidadeOrganizacional(secretariaControleSetorial.getHierarquiaOrganizacional().getSubordinada());
            }
            validarSecretariaControleSetorial();
            this.selecionado.setSecretarias(Util.adicionarObjetoEmLista(this.selecionado.getSecretarias(), this.secretariaControleSetorial));
            atribuirNullSecretaria();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void removerUnidade(UnidadeControleSetorial unidade) {
        this.secretariaControleSetorial.getUnidades().remove(unidade);
    }

    public void adicionarUnidade() {
        try {
            if (this.unidadeControleSetorial.getHierarquiaOrganizacional() != null) {
                unidadeControleSetorial.setUnidadeOrganizacional(unidadeControleSetorial.getHierarquiaOrganizacional().getSubordinada());
            }
            validarUnidadeControleSetorial();
            this.secretariaControleSetorial.setUnidades(Util.adicionarObjetoEmLista(this.secretariaControleSetorial.getUnidades(), this.unidadeControleSetorial));
            instanciarUnidadeControleSetorial();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }

    }

    private void validarSecretariaControleSetorial() {
        ValidacaoException ve = new ValidacaoException();
        if (this.secretariaControleSetorial != null) {
            if (secretariaControleSetorial.getHierarquiaOrganizacional() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O Campo Secretária é obrigatório");
            } else {
                if (secretariaControleSetorial.getUnidades().isEmpty()) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("É necessário informar ao menos uma unidade organizacional.");
                }
                for (SecretariaControleSetorial controleSetorial : this.selecionado.getSecretarias()) {
                    if (controleSetorial.getUnidadeOrganizacional().equals(this.secretariaControleSetorial.getUnidadeOrganizacional())
                        && !controleSetorial.equals(this.secretariaControleSetorial)) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida("A Secretária " + this.secretariaControleSetorial.getUnidadeOrganizacional() + " já foi adicionada.");
                    }
                }
            }

        }
        ve.lancarException();
    }

    private void validarUnidadeControleSetorial() {
        ValidacaoException ve = new ValidacaoException();
        if (this.unidadeControleSetorial != null) {
            if (unidadeControleSetorial.getHierarquiaOrganizacional() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O Campo Unidade Controle setorial é obrigatório");
            } else {
                for (UnidadeControleSetorial controleSetorial : this.secretariaControleSetorial.getUnidades()) {
                    if (controleSetorial.getUnidadeOrganizacional().equals(this.unidadeControleSetorial.getUnidadeOrganizacional())
                        && !this.unidadeControleSetorial.equals(controleSetorial)) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida("A Unidade " + this.unidadeControleSetorial.getUnidadeOrganizacional() + " já foi adicionada.");
                    }
                }
            }
        }
        ve.lancarException();
    }

    public class ControleDaSequenciaIdentificacaoPatrimonio {
        private Entidade entidadeSelecionada;
        private EntidadeGeradoraIdentificacaoPatrimonio entidadeGeradora;
        private Boolean sequenciaPropria;
        private List<EntidadeSequenciaPropria> entidadesGeradorasSequenciaPropria;
        private ConverterEntidadeSequenciaPropria converterEntidadeSequenciaPropria;
        private Boolean edicaoDaEntidadeGeradora = false;

        public ControleDaSequenciaIdentificacaoPatrimonio() {
            instanciarGeradoraSequenciaPropria();
            sequenciaPropria = Boolean.TRUE;
            entidadesGeradorasSequenciaPropria = new ArrayList<>();
        }

        public EntidadeGeradoraIdentificacaoPatrimonio getEntidadeGeradora() {
            return entidadeGeradora;
        }

        public void setEntidadeGeradora(EntidadeGeradoraIdentificacaoPatrimonio entidadeGeradora) {
            this.entidadeGeradora = entidadeGeradora;
        }

        public Boolean getSequenciaPropria() {
            return sequenciaPropria;
        }

        public void setSequenciaPropria(Boolean sequenciaPropria) {
            this.sequenciaPropria = sequenciaPropria;
        }

        public ConverterEntidadeSequenciaPropria getConverterEntidadeSequenciaPropria() {
            if (converterEntidadeSequenciaPropria == null) {
                converterEntidadeSequenciaPropria = new ConverterEntidadeSequenciaPropria();
            }

            return converterEntidadeSequenciaPropria;
        }

        public Boolean getEdicaoDaEntidadeGeradora() {
            return edicaoDaEntidadeGeradora;
        }

        public void setEdicaoDaEntidadeGeradora(Boolean edicaoDaEntidadeGeradora) {
            this.edicaoDaEntidadeGeradora = edicaoDaEntidadeGeradora;
        }

        public List<EntidadeSequenciaPropria> getEntidadesGeradorasSequenciaPropria() {
            return entidadesGeradorasSequenciaPropria;
        }

        public List<EntidadeSequenciaPropria> getEntidadesGeradorasSequenciaPropriaMoveis() {
            List<EntidadeSequenciaPropria> retorno = Lists.newArrayList();
            for (EntidadeSequenciaPropria entidadeSequenciaPropria : entidadesGeradorasSequenciaPropria) {
                if (entidadeSequenciaPropria.isTipoGeracaoBemMovel()) {
                    Util.adicionarObjetoEmLista(retorno, entidadeSequenciaPropria);
                }
            }
            return retorno;
        }

        public List<EntidadeSequenciaPropria> getEntidadesGeradorasSequenciaPropriaImoveis() {
            List<EntidadeSequenciaPropria> retorno = Lists.newArrayList();
            for (EntidadeSequenciaPropria entidadeSequenciaPropria : entidadesGeradorasSequenciaPropria) {
                if (entidadeSequenciaPropria.isTipoGeracaoBemImovel()) {
                    Util.adicionarObjetoEmLista(retorno, entidadeSequenciaPropria);
                }
            }
            return retorno;
        }

        public void setEntidadesGeradorasSequenciaPropria(List<EntidadeSequenciaPropria> entidadesGeradorasSequenciaPropria) {
            this.entidadesGeradorasSequenciaPropria = entidadesGeradorasSequenciaPropria;
        }

        public Entidade getEntidadeSelecionada() {
            return entidadeSelecionada;
        }

        public void setEntidadeSelecionada(Entidade entidadeSelecionada) {
            this.entidadeSelecionada = entidadeSelecionada;
        }

        public void setConverterEntidadeSequenciaPropria(ConverterEntidadeSequenciaPropria converterEntidadeSequenciaPropria) {
            this.converterEntidadeSequenciaPropria = converterEntidadeSequenciaPropria;
        }

        public void instanciarGeradoraSequenciaPropria() {
            entidadeGeradora = new EntidadeSequenciaPropria();
        }

        public void instanciarGeradoraSequenciaAgregada() {
            entidadeGeradora = new EntidadeSequenciaAgregada();
        }

        public void definirEntidadeGeradora() {
            if (!this.sequenciaPropria) {
                instanciarGeradoraSequenciaAgregada();
                return;
            }

            instanciarGeradoraSequenciaPropria();
        }

        public void adicionarEntidadeGeradoraSequenciaPropria() {
            if (entidadeGeradora.getEntidadeSequenciaPropria() != null &&
                !entidadesGeradorasSequenciaPropria.contains(entidadeGeradora.getEntidadeSequenciaPropria())) {
                Util.adicionarObjetoEmLista(entidadesGeradorasSequenciaPropria, entidadeGeradora.getEntidadeSequenciaPropria());
            }
        }

        public void recarregarEntidadesGeradorasSequenciaPropriaMovel(List<EntidadeGeradoraIdentificacaoPatrimonio> entidades) {
            for (EntidadeGeradoraIdentificacaoPatrimonio e : entidades) {
                if (e instanceof EntidadeSequenciaPropria && e.isTipoGeracaoBemMovel()) {
                    Util.adicionarObjetoEmLista(entidadesGeradorasSequenciaPropria, (EntidadeSequenciaPropria) e);

                }
            }
        }

        public void recarregarEntidadesGeradorasSequenciaPropriaImovel(List<EntidadeGeradoraIdentificacaoPatrimonio> entidades) {
            for (EntidadeGeradoraIdentificacaoPatrimonio e : entidades) {
                if (e instanceof EntidadeSequenciaPropria && e.isTipoGeracaoBemImovel()) {
                    Util.adicionarObjetoEmLista(entidadesGeradorasSequenciaPropria, (EntidadeSequenciaPropria) e);

                }
            }
        }

        public void editarEntidadeGeradora(EntidadeGeradoraIdentificacaoPatrimonio entidade) {
            entidadeSelecionada = entidade.getEntidade();
            sequenciaPropria = entidade.ehSequenciaPropria();
            edicaoDaEntidadeGeradora = Boolean.TRUE;

            if (sequenciaPropria) {
                entidadeGeradora = (EntidadeSequenciaPropria) Util.clonarObjeto(entidade);
            } else {
                entidadeGeradora = (EntidadeSequenciaAgregada) Util.clonarObjeto(entidade);
            }
        }

        private void validarExcluir(EntidadeGeradoraIdentificacaoPatrimonio entidade) {
            ValidacaoException ve = new ValidacaoException();
            if (entidade.ehSequenciaPropria() && !((EntidadeSequenciaPropria) entidade).getAgregadas().isEmpty()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A sequência da entidade " + entidade.getEntidade().getNome() + " é utilizada por outras entidades.");
            }
            ve.lancarException();
        }

        public void excluirEntidadeGeradora(EntidadeGeradoraIdentificacaoPatrimonio entidade) {
            try {
                validarExcluir(entidade);
                excluirEntidade(entidade);
            } catch (ValidacaoException ve) {
                FacesUtil.printAllFacesMessages(ve.getAllMensagens());
            }
        }

        private void excluirEntidade(EntidadeGeradoraIdentificacaoPatrimonio entidade) {
            selecionado.excluirEntidadeGeradora(entidade);
            if (entidade.ehSequenciaPropria()) {
                entidadesGeradorasSequenciaPropria.remove(entidade);
            } else {
                entidade.getEntidadeSequenciaPropria().getAgregadas().remove(entidade);
            }
        }

        public void adicionarNovaEntidadeSequenciaGeradoraMovel() {
            entidadeGeradora.setTipoBem(TipoBem.MOVEIS);
            adicionarNovaEntidade();
        }

        private void adicionarNovaEntidade() {
            try {
                entidadeGeradora.setEntidade(entidadeSelecionada);
                selecionado.adicionarEntidadeGeradora(entidadeGeradora);
                adicionarEntidadeGeradoraSequenciaPropria();
                instanciarGeradoraSequenciaPropria();
                entidadeSelecionada = new Entidade();
                edicaoDaEntidadeGeradora = Boolean.FALSE;
                sequenciaPropria = Boolean.TRUE;
            } catch (ValidacaoException ve) {
                FacesUtil.printAllFacesMessages(ve.getMensagens());
            }
        }

        public void adicionarNovaEntidadeSequenciaGeradoraImovel() {
            entidadeGeradora.setTipoBem(TipoBem.IMOVEIS);
            adicionarNovaEntidade();
        }

        public void cancelarEdicaoDaEntidadeGeradora() {
            try {
                instanciarGeradoraSequenciaPropria();
                edicaoDaEntidadeGeradora = Boolean.FALSE;
                entidadeSelecionada = new Entidade();
            } catch (ValidacaoException ve) {
                FacesUtil.printAllFacesMessages(ve.getMensagens());
            }
        }
    }

    public ConverterAutoComplete getConverterResponsavelPatrimonio() {
        if (converterResponsavelPatrimonio == null) {
            converterResponsavelPatrimonio = new ConverterAutoComplete(ResponsavelPatrimonio.class, getFacede());
        }

        return converterResponsavelPatrimonio;
    }

    public class ConverterEntidadeSequenciaPropria implements Converter, Serializable {

        @Override
        public Object getAsObject(FacesContext context, UIComponent component, String value) {
            try {
                for (EntidadeSequenciaPropria esp : controleDaSequenciaIdentificacaoPatrimonioMovel.entidadesGeradorasSequenciaPropria) {
                    if (esp.getCriadoEm().equals(Long.valueOf(value))) {
                        return esp;
                    }
                }
                for (EntidadeSequenciaPropria esp : controleDaSequenciaIdentificacaoPatrimonioImovel.entidadesGeradorasSequenciaPropria) {
                    if (esp.getCriadoEm().equals(Long.valueOf(value))) {
                        return esp;
                    }
                }
            } catch (Exception ex) {
                logger.error(ex.getMessage());
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Impossível continuar", "Erro ao converter: " + value));
            }
            return value.toString();
        }

        @Override
        public String getAsString(FacesContext context, UIComponent component, Object value) {
            if (value != null) {
                return String.valueOf(((EntidadeSequenciaPropria) value).getCriadoEm());
            } else {
                return null;
            }
        }
    }
}
