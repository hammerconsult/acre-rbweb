package br.com.webpublico.controle.portaltransparencia;

import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.controle.portaltransparencia.entidades.*;
import br.com.webpublico.controle.portaltransparencia.enums.ModuloSistema;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.configuracao.ConfiguracaoRH;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.FolhaDePagamentoFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.rh.configuracao.ConfiguracaoRHFacade;
import br.com.webpublico.util.*;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import org.apache.commons.lang.StringUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.UploadedFile;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.io.*;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

/**
 * Created by romanini on 18/08/15.
 */
@ManagedBean(name = "portalTransparenciaNovoControlador")
@ViewScoped
@URLMapping(id = "novo-portal-transparencia", pattern = "/portal/transparencia/", viewId = "/faces/portaltransparencia/edita.xhtml")
public class PortalTransparenciaNovoControlador implements Serializable {

    private PortalTransparenciaNovo selecionado;
    private List<Arquivo> arquivos;
    @EJB
    private PortalTransparenciaNovoFacade facade;
    @EJB
    private ConfiguracaoRHFacade configuracaoRHFacade;
    @EJB
    private FolhaDePagamentoFacade folhaDePagamentoFacade;

    private ConverterAutoComplete converterHierarquiaOrganizacional;
    private Future<PortalTransparenciaNovo> future;
    private ConverterGenerico converterGenerico;
    private Boolean selecionarTodos;

    @URLAction(mappingId = "novo-portal-transparencia", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        recuperarCriarSelecionado();
        recuperarPrefeituras();
        inicializarModulo();
        definirUrl();
        selecionarTodos = Boolean.FALSE;
    }


    public ConverterGenerico getConverterGenerico() {
        if (converterGenerico == null) {
            converterGenerico = new ConverterGenerico(PrefeituraPortal.class, facade);
        }
        return converterGenerico;
    }

    private void recuperarCriarSelecionado() {
        if (facade.getSingleton().getSelecionado() != null) {
            selecionado = facade.getSingleton().getSelecionado();
        } else {
            selecionado = new PortalTransparenciaNovo(getSistemaControlador().getDataOperacao(), getSistemaControlador().getExercicioCorrente());
        }
    }

    private void recuperarPrefeituras() {
        List<PrefeituraPortal> prefeituras = facade.getSingleton().recuperarTodasPrefeitura();
        if (prefeituras == null || prefeituras.isEmpty()) {
            novaPrefeitura();
        } else {
            selecionado.setPrefeituras(prefeituras);
        }
    }

    private void definirUrl() {
        SistemaFacade sistemaFacade = facade.getSistemaFacade();
        facade.getSingleton().definirUrl(sistemaFacade.getPerfilAplicacao(), selecionado);
    }

    public void inicializarModulo() {
        selecionado.setModulo(new ModuloPrefeituraPortal());
    }

    public void novaPrefeitura() {
        PrefeituraPortal prefeitura = new PrefeituraPortal();
        prefeitura.setNome("Prefeitura de Rio Branco");
        prefeitura.setIp("http://172.16.0.75:8080");
        prefeitura.setUltimaAtualizacao(new Date());
        selecionado.setPrefeitura(prefeitura);
        if (selecionado.getPrefeituras() == null) {
            selecionado.setPrefeituras(Lists.<PrefeituraPortal>newArrayList());
        }
    }

    public void atribuirNull() {
        selecionado.setPrefeitura(null);
    }

    public void adicionarPrefeitura() {
        String nome = selecionado.getPrefeitura().getNome();
        try {
            Util.validarCampos(selecionado.getPrefeitura());
            validarPrefeitura(selecionado.getPrefeitura());
            facade.salvarPrefeitura(selecionado.getPrefeitura());
            selecionado.setPrefeituras(Util.adicionarObjetoEmLista(selecionado.getPrefeituras(), selecionado.getPrefeitura()));
            selecionado.setPrefeitura(null);
            FacesUtil.addOperacaoRealizada("A Entidade " + nome + " foi salva com sucesso!");
        } catch (ValidacaoException e) {
            FacesUtil.printAllFacesMessages(e.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada("Erro ao salvar a Entidade " + nome + ". Erro: " + e.getMessage());
        }
    }

    private void validarPrefeitura(PrefeituraPortal prefeitura) {
        ValidacaoException ve = new ValidacaoException();
        if (prefeitura.getPrincipal() != null && prefeitura.getPrincipal()) {
            for (PrefeituraPortal prefeituraPortal : selecionado.getPrefeituras()) {
                if (prefeituraPortal.getPrincipal() && !prefeituraPortal.getId().equals(prefeitura.getId())) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida(" Já foi adicionado uma prefeitura como principal!");
                }
            }
        }
        for (PrefeituraPortal prefeituraPortal : selecionado.getPrefeituras()) {
            if (prefeituraPortal.getIdentificador().trim().equals(prefeitura.getIdentificador().trim())
                && !prefeituraPortal.getId().equals(prefeitura.getId())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida(" Já possui uma prefeitura com esse identificador!");
            }
        }
        ve.lancarException();
    }

    public void editarPrefeitura(PrefeituraPortal prefeituraPortal) {
        selecionado.setPrefeitura(prefeituraPortal);
    }

    public void removerPrefeitura(PrefeituraPortal prefeituraPortal) {
        selecionado.getPrefeituras().remove(prefeituraPortal);
    }

    public void adicionarTodasUnidadePrefeitura() {
        try {
            List<HierarquiaOrganizacional> hierarquiaOrganizacionals = facade.getHierarquiaOrganizacionalFacade().filtraPorNivelTrazendoTodas("", "3", TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), UtilRH.getDataOperacao());
            for (HierarquiaOrganizacional hierarquiaOrganizacional : hierarquiaOrganizacionals) {
                UnidadePrefeituraPortal unidadePrefeituraPortal = criarUnidadePrefeitura(hierarquiaOrganizacional.getSubordinada());
                Util.validarCampos(unidadePrefeituraPortal);
                validarUnidadePrefeitura(unidadePrefeituraPortal);
                selecionado.getPrefeitura().getUnidades().add(unidadePrefeituraPortal);
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
            UnidadePrefeituraPortal unidadePrefeituraPortal = criarUnidadePrefeitura(selecionado.getUnidade().getSubordinada());
            Util.validarCampos(unidadePrefeituraPortal);
            validarUnidadePrefeitura(unidadePrefeituraPortal);
            selecionado.getPrefeitura().getUnidades().add(unidadePrefeituraPortal);
            FacesUtil.addOperacaoRealizada("A Unidade " + selecionado.getUnidade().getSubordinada() + " foi adicionada com sucesso!");
        } catch (ValidacaoException e) {
            FacesUtil.printAllFacesMessages(e.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada("Erro ao adicionar a unidade " + selecionado.getUnidade().getSubordinada() + ". Erro: " + e.getMessage());
        }
        selecionado.setUnidade(null);
    }

    private void validarUnidadePrefeitura(UnidadePrefeituraPortal unidadePrefeituraPortal) {
        ValidacaoException ve = new ValidacaoException();
        for (PrefeituraPortal prefeituraPortal : selecionado.getPrefeituras()) {
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
        unidadePrefeituraPortal.setPrefeitura(selecionado.getPrefeitura());
        unidadePrefeituraPortal.setUnidade(unidadeOrganizacional);
        return unidadePrefeituraPortal;
    }

    public void removerUnidadePrefeitura(UnidadePrefeituraPortal unidadePrefeituraPortal) {
        selecionado.getPrefeitura().getUnidades().remove(unidadePrefeituraPortal);
    }

    public void adicionarTodosModuloPrefeitura() {
        try {
            for (ModuloSistema moduloSistema : ModuloSistema.values()) {
                ModuloPrefeituraPortal moduloPrefeituraPortal = new ModuloPrefeituraPortal(moduloSistema, selecionado.getPrefeitura());
                Util.validarCampos(moduloPrefeituraPortal);
                validarModuloPrefeitura(selecionado.getPrefeitura(), moduloPrefeituraPortal);
                selecionado.getPrefeitura().getModulos().add(moduloPrefeituraPortal);
            }
            FacesUtil.addOperacaoRealizada("Todos os " + ModuloSistema.values().length + " módulos foram adicionados com sucesso!");
            inicializarModulo();
        } catch (ValidacaoException e) {
            FacesUtil.printAllFacesMessages(e.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada("Erro ao adicionar o módulo " + selecionado.getUnidade().getSubordinada() + ". Erro: " + e.getMessage());
        }
    }

    public void adicionarModuloPrefeitura() {
        try {
            selecionado.getModulo().setPrefeitura(selecionado.getPrefeitura());
            Util.validarCampos(selecionado.getModulo());
            Util.adicionarObjetoEmLista(selecionado.getPrefeitura().getModulos(), selecionado.getModulo());
            FacesUtil.addOperacaoRealizada("O módulo " + selecionado.getModulo() + " foi adicionado com sucesso!");
            inicializarModulo();
        } catch (ValidacaoException e) {
            FacesUtil.printAllFacesMessages(e.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada("Erro ao adicionar o módulo " + selecionado.getUnidade().getSubordinada() + ". Erro: " + e.getMessage());
        }
    }

    public void editarModulo(ModuloPrefeituraPortal modulo) {
        selecionado.setModulo(modulo);
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
        selecionado.getPrefeitura().getModulos().remove(moduloPrefeituraPortal);
    }

    public void atualizarFinal() {
        if (!facade.isCalculando()) {
            FacesUtil.atualizarComponente("Formulario");
        }
    }

    public void enviar() {
        try {
            validarMovimentos();
            validarAtosLegais();
            validarDadosPrefeitura();

            if (selecionado.getEnviarDadosRh()) {
                Integer ano = selecionado.getExercicio().getAno();
                int numeroMesIniciandoEmZero = Mes.getMesToInt(DataUtil.getMes(selecionado.getDataOperacao())).getNumeroMesIniciandoEmZero();
                ConfiguracaoRH configuracaoRH = configuracaoRHFacade.recuperarConfiguracaoRHVigente(selecionado.getDataOperacao());
                if (configuracaoRH.getVencimentoBasePortal() != null && folhaDePagamentoFacade.folhasNaoExibidasPortalServidor(ano, numeroMesIniciandoEmZero)) {
                    FacesUtil.addAtencao("Foram encontradas folhas de pagamento não enviadas ao Portal do Servidor na competência. Os dados dessas folhas não serão enviados ao Portal de Transparência.");
                }
            }
            selecionado.getAssistente().setExecutando(true);
            selecionado.getAssistente().setSelecionado(selecionado);
            selecionado.getAssistente().setUsuarioSistema(getSistemaControlador().getUsuarioCorrente());
            selecionado.getAssistente().setDataAtual(getSistemaControlador().getDataOperacao());
            selecionado.getAssistente().setTotal(calcularTotalAssistente());
            selecionado.getAssistente().setDescricaoProcesso("Sincronização do Portal da Transparência.");
            future = facade.enviar(selecionado);
        } catch (ValidacaoException e) {
            selecionado.getAssistente().zerarContadoresProcesso();
            FacesUtil.printAllFacesMessages(e.getMensagens());
        } catch (Exception e) {
            selecionado.getAssistente().zerarContadoresProcesso();
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        }
    }

    private void validarDadosPrefeitura() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getUrl() == null || selecionado.getUrl().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio(" O Campo URL deve ser informado.");
        }
        if (selecionado.getDataOperacao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio(" O Campo Data deve ser informado.");
        }
        ve.lancarException();
    }

    private void validarMovimentos() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getEnviarPlanejamentoDespesa()
            || selecionado.getEnviarPlanejamentoReceita()
            || selecionado.getEnviarAlteracaoOrcamentaria()
            || selecionado.getEnviarDiaria()
            || selecionado.getEnviarExecucaoOrcamentaria()
            || selecionado.getEnviarFinanceiro()
            || selecionado.getEnviarReceitaRealizada()
            || selecionado.getEnviarRestoAPagar()
            || selecionado.getEnviarUnidades()) {
            if (selecionado.getUnidades().isEmpty()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("É necessário selecionar pelo menos 1(UMA) unidade para recuperar os objetos.");
            }
        }
        if (ve.temMensagens()) {
            throw ve;
        }
    }

    private void validarAtosLegais() {
        if (selecionado.getEnviarAtoLegal()) {
            if (selecionado.getAtos().isEmpty()) {
                for (AtoLegal atoLegal : selecionado.getAtos()) {
                    facade.validarAtoLegal(atoLegal);
                }
            }
        }
    }

    public void abortar() {
        facade.abortar();
        if (future != null) {
            future.cancel(true);
        }
    }

    public void limpar() {
        facade.limpar();
    }

    //    RECUPERADORES
    public void recuperarUnidades() {
        selecionado.setUnidades(facade.getHierarquiaOrganizacionalFacade().listaTodasHierarquiaHorganizacionalTipo("", TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), selecionado.getDataOperacao()));
    }

    //    GETTER E SETTER
    public SistemaControlador getSistemaControlador() {
        return (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }

    public PortalTransparenciaNovo getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(PortalTransparenciaNovo selecionado) {
        this.selecionado = selecionado;
    }

    public PortalTransparenciaNovoFacade getFacade() {
        return facade;
    }

    public void uploadLogoLink(FileUploadEvent event) {
        try {
            selecionado.getLinkPortal().setLogo(criarNovoArquivo(event, "Logo Link"));
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    public void uploadLogoTopo(FileUploadEvent event) {
        try {
            selecionado.getPrefeitura().setLogoTopo(criarNovoArquivo(event, "Logo"));
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    public void uploadOrganograma(FileUploadEvent event) {
        try {
            selecionado.getPrefeitura().setOrganograma(criarNovoArquivo(event, "Organograma"));
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    private Arquivo criarNovoArquivo(FileUploadEvent event, String descricaoArquivo) throws Exception {
        UploadedFile file = event.getFile();
        Arquivo arquivo = new Arquivo();
        arquivo = facade.getArquivoFacade().novoArquivoMemoria(arquivo, file.getInputstream());
        arquivo.setNome(file.getFileName());
        arquivo.setMimeType(facade.getArquivoFacade().getMimeType(file.getFileName()));
        arquivo.setDescricao(descricaoArquivo);
        arquivo.setTamanho(file.getSize());
        return arquivo;
    }

    public void limparArquivoLogoTopo() {
        selecionado.getPrefeitura().setLogoTopo(null);
    }

    public void limparArquivoOrganograma() {
        selecionado.getPrefeitura().setOrganograma(null);
    }

    public DefaultStreamedContent gerarTxt() {
        String log = facade.getSingleton().getResultado();
        SistemaControlador sistemaControlador = (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
        String secretaria = sistemaControlador.getUnidadeOrganizacionalOrcamentoCorrente().getDescricao();

        String conteudo = secretaria + "</br>"
            + " LOG SINCRONIZAÇÃO DO PORTAL DA TRANSPARÊNCIA </br></br>"
            + log
            + "\"</br></br>" +
            " USUÁRIO RESPONSÁVEL:" + sistemaControlador.getUsuarioCorrente().toString()
            + "</br>"
            + "               DATA:" + sistemaControlador.getDataOperacaoFormatada();

        conteudo = conteudo.replace("</br>", System.getProperty("line.separator"));
        conteudo = conteudo.replace("<b>", " ");
        conteudo = conteudo.replace("</b>", " ");
        conteudo = conteudo.replace("<font color='red'>", " ");
        conteudo = conteudo.replace("<font color='blue'>", " ");
        conteudo = conteudo.replace("</font>", " ");

        String nomeArquivo = "Log de Sincronização Portal da Transparência.txt";

        File arquivo = new File(nomeArquivo);

        try {
            FileOutputStream fos = new FileOutputStream(arquivo);
            fos.write(conteudo.getBytes());
            InputStream stream = new FileInputStream(arquivo);
            return new DefaultStreamedContent(stream, "text/plain", nomeArquivo);
        } catch (Exception e) {
            return null;
        }
    }

    public void novoGlossario() {
        selecionado.inicializarGlossario();
    }

    public void adicionarGlossario() {
        try {
            ValidacaoException validacaoException = new ValidacaoException();
            if (selecionado.getGlossarioPortal().getNome().trim().isEmpty()) {
                validacaoException.adicionarMensagemDeCampoObrigatorio("O campo Nome deve ser informado.");
            }
            if (selecionado.getGlossarioPortal().getDescricao().trim().isEmpty()) {
                validacaoException.adicionarMensagemDeCampoObrigatorio("O campo Descrição deve ser informado.");
            }

            if (validacaoException.temMensagens()) {
                throw validacaoException;
            }
            selecionado.getGlossarios().add(selecionado.getGlossarioPortal());
            selecionado.setGlossarioPortal(new GlossarioPortal());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErrorGenerico(ex);
        }
    }

    public void novoLink() {
        selecionado.inicializarLink();
    }

    public void adicionarLink() {
        try {
            ValidacaoException validacaoException = new ValidacaoException();
            if (selecionado.getLinkPortal().getSiteLink().trim().isEmpty()) {
                validacaoException.adicionarMensagemDeCampoObrigatorio("O campo Site deve ser informado.");
            }
            if (selecionado.getLinkPortal().getNomeLink().trim().isEmpty()) {
                validacaoException.adicionarMensagemDeCampoObrigatorio("O campo Nome deve ser informado.");
            }
            if (selecionado.getLinkPortal().getLogo() == null) {
                validacaoException.adicionarMensagemDeCampoObrigatorio("O campo Arquivo deve ser informado.");
            }
            if (validacaoException.temMensagens()) {
                throw validacaoException;
            }
            selecionado.getLinks().add(selecionado.getLinkPortal());
            selecionado.setLinkPortal(new LinkPortal());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErrorGenerico(ex);
        }
    }

    public List<Exercicio> completaExercicios(String parte) {
        return facade.getExercicioFacade().listaFiltrandoExerciciosAtualPassados(parte.trim());
    }

    public List<PrefeituraPortal> buscar(String parte) {
        return facade.buscarPorNomeLike(parte);
    }


    public void removerGlossario(GlossarioPortal glossarioPortal) {
        selecionado.getGlossarios().remove(glossarioPortal);
    }

    public void removerLink(LinkPortal linkPortal) {
        selecionado.getLinks().remove(linkPortal);
    }

    public void removerAtoLegal(AtoLegal atoLegal) {
        selecionado.getAtos().remove(atoLegal);
    }

    public void novaImportacaoAtosLegais() {
        arquivos = Lists.newArrayList();
    }

    public void uploadLAtosLegais(FileUploadEvent event) {
        try {
            arquivos.add(criarNovoArquivo(event, "Arquivo Ato Legal"));
        } catch (Exception ex) {
            ex.printStackTrace();
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }


    public void removerArquivos() {
        selecionado.getAtos().clear();
        arquivos.clear();
    }

    public void adicionarArquivos() {
        try {
            if (arquivos.isEmpty()) {
                FacesUtil.addOperacaoNaoPermitida("É necessário selecionar pelo menos 1 (UM) arquivo.");
                return;
            }
            for (Arquivo arquivo : arquivos) {
                String nomeArquivo = arquivo.getNome().toUpperCase();

                if (nomeArquivo.contains("Nº")) {
                    int indice = nomeArquivo.indexOf("Nº");
                    selecionado.getAtoLegal().setNumero(StringUtil.retornaApenasNumeros(nomeArquivo.substring(indice, indice + 8)));
                }

                facade.validarAtoLegal(selecionado.getAtoLegal());

                AtoLegal atoLegal = atribuirValores(arquivo, nomeArquivo);

                validarJaFoiAdicionado(atoLegal);

                selecionado.getAtos().add(atoLegal);
            }
            arquivos.clear();
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());

        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }

    }

    private void validarJaFoiAdicionado(AtoLegal atoLegal) {
        for (AtoLegal legal : selecionado.getAtos()) {
            if (legal.getNumero().equals(atoLegal.getNumero()) &&
                legal.getExercicio().getId().equals(atoLegal.getExercicio().getId())) {
                arquivos.remove(atoLegal.getArquivo());
                throw new ExcecaoNegocioGenerica("O Ato legal " + atoLegal.toString() + " já foi adicionado.");
            }
        }
    }

    private AtoLegal atribuirValores(Arquivo arquivo, String nomeArquivo) {
        AtoLegal atoLegal = new AtoLegal();
        atoLegal.setExercicio(selecionado.getAtoLegal().getExercicio());
        atoLegal.setPropositoAtoLegal(selecionado.getAtoLegal().getPropositoAtoLegal());
        atoLegal.setTipoAtoLegal(selecionado.getAtoLegal().getTipoAtoLegal());
        atoLegal.setEsferaGoverno(selecionado.getAtoLegal().getEsferaGoverno());
        atoLegal.setVeiculoDePublicacao(selecionado.getAtoLegal().getVeiculoDePublicacao());
        atoLegal.setDataEmissao(selecionado.getAtoLegal().getDataEmissao());
        atoLegal.setDataEnvio(selecionado.getAtoLegal().getDataEmissao());
        atoLegal.setDataPromulgacao(selecionado.getAtoLegal().getDataEmissao());
        atoLegal.setDataPublicacao(selecionado.getAtoLegal().getDataEmissao());
        atoLegal.setDataValidade(selecionado.getAtoLegal().getDataEmissao());

        if (nomeArquivo.contains("LEI")) {
            atoLegal.setTipoAtoLegal(TipoAtoLegal.LEI_ORDINARIA);
        }
        if (nomeArquivo.contains("LEI-COMPLEMENTAR")) {
            atoLegal.setTipoAtoLegal(TipoAtoLegal.LEI_COMPLEMENTAR);
        }
        if (nomeArquivo.contains("PORTARIA")) {
            atoLegal.setTipoAtoLegal(TipoAtoLegal.PORTARIA);
        }
        if (nomeArquivo.contains("DECRETO")) {
            atoLegal.setTipoAtoLegal(TipoAtoLegal.DECRETO);
        }
        if (nomeArquivo.contains("RREO")
            || nomeArquivo.contains("RGF")
            || nomeArquivo.contains("RLF")
            || nomeArquivo.contains("RELATORIO")
            || nomeArquivo.contains("DEMONSTRATIVO")
            || nomeArquivo.contains("ANEXO")) {
            atoLegal.setPropositoAtoLegal(PropositoAtoLegal.OUTROS);
        }
        atoLegal.setNumero(selecionado.getAtoLegal().getNumero());
        atoLegal.setNome(UtilPortalTransparencia.getNomeRemovendoExtensao(nomeArquivo));
        atoLegal.setArquivo(arquivo);
        return atoLegal;
    }


    public List<Arquivo> getArquivos() {
        return arquivos;
    }

    public void setArquivos(List<Arquivo> arquivos) {
        this.arquivos = arquivos;
    }


    public List<HierarquiaOrganizacional> completaHierarquiaOrganizacional(String parte) {
        return facade.getHierarquiaOrganizacionalFacade().filtraPorNivel(parte, "3", TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), UtilRH.getDataOperacao());
    }

    public List<SelectItem> getModulos() {
        return Util.getListSelectItem(ModuloSistema.values());
    }

    public List<SelectItem> getEsferas() {
        return Util.getListSelectItem(EsferaDoPoder.values());
    }

    public Converter getConverterHierarquiaOrganizacional() {
        if (converterHierarquiaOrganizacional == null) {
            converterHierarquiaOrganizacional = new ConverterAutoComplete(HierarquiaOrganizacional.class, facade.getHierarquiaOrganizacionalFacade());
        }
        return converterHierarquiaOrganizacional;
    }

    private Integer calcularTotalAssistente() {
        Integer retorno = 1;
        if (selecionado.getEnviarUnidades()) {
            retorno++;
        }
        if (selecionado.getEnviarAtoLegal()) {
            retorno++;
        }
        if (selecionado.getEnviarPlanejamentoDespesa()) {
            retorno++;
        }
        if (selecionado.getEnviarPlanejamentoReceita()) {
            retorno++;
        }
        if (selecionado.getEnviarAlteracaoOrcamentaria()) {
            retorno++;
        }
        if (selecionado.getEnviarExecucaoOrcamentaria()) {
            retorno++;
        }
        if (selecionado.getEnviarRestoAPagar()) {
            retorno++;
        }
        if (selecionado.getEnviarReceitaRealizada()) {
            retorno++;
        }
        if (selecionado.getEnviarFinanceiro()) {
            retorno++;
        }
        if (selecionado.getEnviarDiaria()) {
            retorno++;
        }
        if (selecionado.getEnviarLicitacao()) {
            retorno++;
        }
        if (selecionado.getEnviarContrato()) {
            retorno++;
        }
        if (selecionado.getEnviarLink()) {
            retorno++;
        }
        if (selecionado.getEnviarGlossario()) {
            retorno++;
        }
        if (selecionado.getEnviarAnexos()) {
            retorno++;
        }
        if (selecionado.getEnviarConvenios()) {
            retorno++;
        }
        if (selecionado.getEnviarDadosRh()) {
            retorno++;
        }
        if (selecionado.getEnviarObra()) {
            retorno++;
        }
        if (selecionado.getEnviarCalamidadePublica()) {
            retorno++;
        }
        if (selecionado.getEnviarDicionarioDeDados()) {
            retorno++;
        }
        if (selecionado.getEnviarBem()) {
            retorno++;
        }
        return retorno;
    }

    public List<SelectItem> getTipos() {
        return Util.getListSelectItem(TipoObjetoPortalTransparencia.values());
    }

    public void buscarDados() {
        try {
            if (StringUtils.isEmpty(selecionado.getHql())) {
                throw new ExcecaoNegocioGenerica("O Campo HQL é obrigatório.");
            }
            if (selecionado.getTipo() == null) {
                throw new ExcecaoNegocioGenerica("O Campo Tipo é obrigatório.");
            }
            facade.buscarDadosHql(selecionado);
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        }
    }

    public void enviarDados() {
        try {
            future = facade.enviarObjetos(selecionado);
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        }
    }

    public Boolean getSelecionarTodos() {
        return selecionarTodos == null ? Boolean.FALSE : selecionarTodos;
    }

    public void setSelecionarTodos(Boolean selecionarTodos) {
        this.selecionarTodos = selecionarTodos;
    }

    public void marcarDesmarcarTodos() {
        selecionado.marcarDesmarcarTodos(selecionarTodos);
    }
}
