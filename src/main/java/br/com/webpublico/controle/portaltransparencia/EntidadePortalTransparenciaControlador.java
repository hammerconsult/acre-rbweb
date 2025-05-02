package br.com.webpublico.controle.portaltransparencia;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.controle.portaltransparencia.entidades.ModuloPrefeituraPortal;
import br.com.webpublico.controle.portaltransparencia.entidades.PrefeituraPortal;
import br.com.webpublico.controle.portaltransparencia.entidades.UnidadeAdmPrefeituraPortal;
import br.com.webpublico.controle.portaltransparencia.entidades.UnidadePrefeituraPortal;
import br.com.webpublico.controle.portaltransparencia.enums.ModuloSistema;
import br.com.webpublico.entidades.Arquivo;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.UnidadeOrganizacional;
import br.com.webpublico.enums.EsferaDoPoder;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.UtilRH;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.Date;
import java.util.List;


@ManagedBean(name = "entidadePortalTransparenciaControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-entidade-portal-transparencia", pattern = "/portal/transparencia/entidade/novo/", viewId = "/faces/portaltransparencia/entidade/edita.xhtml"),
    @URLMapping(id = "editar-entidade-portal-transparencia", pattern = "/portal/transparencia/entidade/editar/#{entidadePortalTransparenciaControlador.id}/", viewId = "/faces/portaltransparencia/entidade/edita.xhtml"),
    @URLMapping(id = "ver-entidade-portal-transparencia", pattern = "/portal/transparencia/entidade/ver/#{entidadePortalTransparenciaControlador.id}/", viewId = "/faces/portaltransparencia/entidade/visualizar.xhtml"),
    @URLMapping(id = "listar-entidade-portal-transparencia", pattern = "/portal/transparencia/entidade/listar/", viewId = "/faces/portaltransparencia/entidade/lista.xhtml"),
})
public class EntidadePortalTransparenciaControlador extends PrettyControlador<PrefeituraPortal> implements Serializable, CRUD {


    @EJB
    private PortalTransparenciaNovoFacade facade;
    private HierarquiaOrganizacional unidade;
    private HierarquiaOrganizacional unidadeAdm;
    private ModuloPrefeituraPortal modulo;

    public EntidadePortalTransparenciaControlador() {
        super(PrefeituraPortal.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/portal/transparencia/entidade/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @URLAction(mappingId = "novo-entidade-portal-transparencia", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        inicializarModulo();
    }

    @URLAction(mappingId = "editar-entidade-portal-transparencia", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        inicializarModulo();
    }

    @URLAction(mappingId = "ver-entidade-portal-transparencia", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @Override
    public void salvar() {
        super.salvar();
    }

    public List<SelectItem> getModulos() {
        return Util.getListSelectItem(ModuloSistema.values());
    }

    public List<SelectItem> getEsferas() {
        return Util.getListSelectItem(EsferaDoPoder.values());
    }

    public void uploadLogoTopo(FileUploadEvent event) {
        try {
            UploadedFile file = event.getFile();
            Arquivo arquivo = new Arquivo();
            arquivo = facade.getArquivoFacade().novoArquivoMemoria(arquivo, file.getInputstream());
            arquivo.setNome(file.getFileName());
            arquivo.setMimeType(facade.getArquivoFacade().getMimeType(file.getFileName()));
            arquivo.setDescricao("Logo");
            arquivo.setTamanho(file.getSize());

            selecionado.setLogoTopo(arquivo);
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    public List<HierarquiaOrganizacional> completaHierarquiaOrganizacional(String parte) {
        return facade.getHierarquiaOrganizacionalFacade().filtraPorNivel(parte, "3", TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), UtilRH.getDataOperacao());
    }


    public List<HierarquiaOrganizacional> completaHierarquiaOrganizacionalAdministrativa(String parte) {
        return facade.getHierarquiaOrganizacionalFacade().filtraPorNivel(parte, "2", TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), UtilRH.getDataOperacao());
    }

    public void adicionarTodasUnidadePrefeitura() {
        try {
            List<HierarquiaOrganizacional> hierarquiaOrganizacionals = facade.getHierarquiaOrganizacionalFacade().filtraPorNivelTrazendoTodas("", "3", TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), UtilRH.getDataOperacao());
            for (HierarquiaOrganizacional hierarquiaOrganizacional : hierarquiaOrganizacionals) {
                UnidadePrefeituraPortal unidadePrefeituraPortal = criarUnidadePrefeitura(hierarquiaOrganizacional.getSubordinada());
                Util.validarCampos(unidadePrefeituraPortal);
                validarUnidadePrefeitura(unidadePrefeituraPortal);
                selecionado.getUnidades().add(unidadePrefeituraPortal);
            }
            FacesUtil.addOperacaoRealizada("Todas as " + hierarquiaOrganizacionals.size() + " unidades foram adicionadas com sucesso!");
        } catch (ValidacaoException e) {
            FacesUtil.printAllFacesMessages(e.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada("Erro ao adicionar todas as unidades. Erro: " + e.getMessage());
        }
    }

    public void adicionarUnidadePrefeitura() {
        try {
            UnidadePrefeituraPortal unidadePrefeituraPortal = criarUnidadePrefeitura(unidade.getSubordinada());
            Util.validarCampos(unidadePrefeituraPortal);
            validarUnidadePrefeitura(unidadePrefeituraPortal);
            selecionado.getUnidades().add(unidadePrefeituraPortal);
            FacesUtil.addOperacaoRealizada("A Unidade " + unidade.getSubordinada() + " foi adicionada com sucesso!");
        } catch (ValidacaoException e) {
            FacesUtil.printAllFacesMessages(e.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada("Erro ao adicionar a unidade " + unidade.getSubordinada() + ". Erro: " + e.getMessage());
        }
        unidade = null;
    }

    private void validarUnidadePrefeitura(UnidadePrefeituraPortal unidadePrefeituraPortal) {
        ValidacaoException ve = new ValidacaoException();
        for (PrefeituraPortal prefeituraPortal : facade.getSingleton().recuperarTodasPrefeitura()) {
            for (UnidadePrefeituraPortal uni : prefeituraPortal.getUnidades()) {
                if (uni.getUnidade().equals(unidadePrefeituraPortal.getUnidade())) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("A Unidade " + uni.getUnidade() + " já foi utilizada na entidade " + prefeituraPortal.getNome() + ".");
                }
            }
        }
        ve.lancarException();
    }

    public UnidadePrefeituraPortal criarUnidadePrefeitura(UnidadeOrganizacional unidadeOrganizacional) {
        UnidadePrefeituraPortal unidadePrefeituraPortal = new UnidadePrefeituraPortal();
        unidadePrefeituraPortal.setPrefeitura(selecionado);
        unidadePrefeituraPortal.setUnidade(unidadeOrganizacional);
        return unidadePrefeituraPortal;
    }

    public void removerUnidadePrefeitura(UnidadePrefeituraPortal unidadePrefeituraPortal) {
        selecionado.getUnidades().remove(unidadePrefeituraPortal);
    }

    public void inicializarModulo() {
        modulo = new ModuloPrefeituraPortal();
    }

    public void adicionarTodosModuloPrefeitura() {
        try {
            for (ModuloSistema moduloSistema : ModuloSistema.values()) {
                ModuloPrefeituraPortal moduloPrefeituraPortal = new ModuloPrefeituraPortal(moduloSistema, selecionado);
                Util.validarCampos(moduloPrefeituraPortal);
                validarModuloPrefeitura(selecionado, moduloPrefeituraPortal);
                selecionado.getModulos().add(moduloPrefeituraPortal);
            }
            FacesUtil.addOperacaoRealizada("Todos os " + ModuloSistema.values().length + " módulos foram adicionados com sucesso!");
            inicializarModulo();
        } catch (ValidacaoException e) {
            FacesUtil.printAllFacesMessages(e.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada("Erro ao adicionar o módulo " + modulo + ". Erro: " + e.getMessage());
        }
    }

    public void adicionarModuloPrefeitura() {
        try {
            modulo.setPrefeitura(selecionado);
            Util.validarCampos(modulo);
            Util.adicionarObjetoEmLista(selecionado.getModulos(), modulo);
            FacesUtil.addOperacaoRealizada("O módulo " + modulo + " foi adicionado com sucesso!");
            inicializarModulo();
        } catch (ValidacaoException e) {
            FacesUtil.printAllFacesMessages(e.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada("Erro ao adicionar o módulo " + modulo + ". Erro: " + e.getMessage());
        }
    }

    public void editarModulo(ModuloPrefeituraPortal modulo) {
        this.modulo = modulo;
    }

    private void validarModuloPrefeitura(PrefeituraPortal prefeitura, ModuloPrefeituraPortal modulo) {
        ValidacaoException ve = new ValidacaoException();
        for (ModuloPrefeituraPortal moduloPrefeituraPortal : prefeitura.getModulos()) {
            if (moduloPrefeituraPortal.getModulo().equals(modulo.getModulo())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O Módulo " + modulo.getModulo().getDescricao() + " já foi adicionado!");
            }
        }
        ve.lancarException();

    }

    public void removerModuloPrefeitura(ModuloPrefeituraPortal moduloPrefeituraPortal) {
        selecionado.getModulos().remove(moduloPrefeituraPortal);
    }


    public HierarquiaOrganizacional getUnidade() {
        return unidade;
    }

    public void setUnidade(HierarquiaOrganizacional unidade) {
        this.unidade = unidade;
    }

    public ModuloPrefeituraPortal getModulo() {
        return modulo;
    }

    public void setModulo(ModuloPrefeituraPortal modulo) {
        this.modulo = modulo;
    }

    public HierarquiaOrganizacional getUnidadeAdm() {
        return unidadeAdm;
    }

    public void setUnidadeAdm(HierarquiaOrganizacional unidadeAdm) {
        this.unidadeAdm = unidadeAdm;
    }

    public void adicionarUnidadePrefeituraAdm() {
        try {
            UnidadeAdmPrefeituraPortal unidadePrefeituraPortal = criarUnidadePrefeituraAdm(unidade.getSubordinada());
            Util.validarCampos(unidadePrefeituraPortal);
            validarUnidadePrefeituraAdm(unidadePrefeituraPortal);
            selecionado.getUnidadesAdm().add(unidadePrefeituraPortal);
            FacesUtil.addOperacaoRealizada("A Unidade " + unidade.getSubordinada() + " foi adicionada com sucesso!");
        } catch (ValidacaoException e) {
            FacesUtil.printAllFacesMessages(e.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada("Erro ao adicionar a unidade " + unidade.getSubordinada() + ". Erro: " + e.getMessage());
        }
        unidade = null;
    }

    private void validarUnidadePrefeituraAdm(UnidadeAdmPrefeituraPortal unidadePrefeituraPortal) {
        ValidacaoException ve = new ValidacaoException();
        for (PrefeituraPortal prefeituraPortal : facade.getSingleton().recuperarTodasPrefeitura()) {
            for (UnidadeAdmPrefeituraPortal uni : prefeituraPortal.getUnidadesAdm()) {
                if (uni.getUnidade().equals(unidadePrefeituraPortal.getUnidade())) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("A Unidade " + uni.getUnidade() + " já foi utilizada na entidade " + prefeituraPortal.getNome() + ".");
                }
            }
        }
        ve.lancarException();
    }

    public UnidadeAdmPrefeituraPortal criarUnidadePrefeituraAdm(UnidadeOrganizacional unidadeOrganizacional) {
        UnidadeAdmPrefeituraPortal unidadePrefeituraPortal = new UnidadeAdmPrefeituraPortal();
        unidadePrefeituraPortal.setPrefeitura(selecionado);
        unidadePrefeituraPortal.setUnidade(unidadeOrganizacional);
        return unidadePrefeituraPortal;
    }

    public void removerUnidadePrefeituraAdm(UnidadeAdmPrefeituraPortal unidadePrefeituraPortal) {
        selecionado.getUnidadesAdm().remove(unidadePrefeituraPortal);
    }

    public Date getDataOperacao(){
        return facade.getSistemaFacade().getDataOperacao();
    }
}
