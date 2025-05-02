package br.com.webpublico.controle.rh.concursos;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.controle.Web;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.concursos.*;
import br.com.webpublico.enums.CriterioDesempate;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.enums.TipoLocalConcurso;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ArquivoFacade;
import br.com.webpublico.negocios.AtoLegalFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.rh.concursos.ConcursoFacade;
import br.com.webpublico.util.*;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.model.StreamedContent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by venom on 22/10/14.
 */
@ManagedBean(name = "concursoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoConcurso", pattern = "/concurso/novo/", viewId = "/faces/rh/concursos/concurso/edita.xhtml"),
    @URLMapping(id = "editarConcurso", pattern = "/concurso/editar/#{concursoControlador.id}/", viewId = "/faces/rh/concursos/concurso/edita.xhtml"),
    @URLMapping(id = "verConcurso", pattern = "/concurso/ver/#{concursoControlador.id}/", viewId = "/faces/rh/concursos/concurso/edita.xhtml"),
    @URLMapping(id = "listarConcurso", pattern = "/concurso/listar/", viewId = "/faces/rh/concursos/concurso/lista.xhtml")
})
public class ConcursoControlador extends PrettyControlador<Concurso> implements Serializable, CRUD {

    @EJB
    private ConcursoFacade concursoFacade;
    private HierarquiaOrganizacional hierarquiaOrganizacionalSelecionada;
    private LocalConcurso localConcursoSelecionado;
    private LocalConcurso localConcursoSelecionadoBackup;
    private CargoConcurso cargoConcursoSelecionado;
    private CargoConcurso cargoConcursoSelecionadoBackup;
    private ConverterAutoComplete converterHierarquia;
    private DesempateConcurso desempateSelecionado;
    private PublicacaoConcurso publicacaoSelecionada;
    private List<MembroComissao> membrosComissao;
    @EJB
    private AtoLegalFacade atoLegalFacade;
    @EJB
    private ArquivoFacade arquivoFacade;

    public ConcursoControlador() {
        super(Concurso.class);
    }

    @URLAction(mappingId = "novoConcurso", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "verConcurso", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarConcurso", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperarEditaVer();
        popularMembrosComissao();
    }

    @Override
    public void salvar() {
        if (podeSalvar()) {
            setarCodigo();
            setarAno();
            super.salvar();
        }
    }

    @Override
    public void excluir() {
        try {
            concursoFacade.remover(selecionado);
            redireciona();
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoExcluir());
        } catch (ExcecaoNegocioGenerica eng) {
            FacesUtil.addOperacaoNaoPermitida(eng.getMessage());
        }
    }

    private boolean possuiDoisCriteriosComAmesmaOrdem() {
        for (DesempateConcurso desempateDaVez : selecionado.getDesempates()) {
            for (DesempateConcurso desempateDaLista : selecionado.getDesempates()) {
                if (!desempateDaVez.equals(desempateDaLista) && desempateDaVez.getOrdem().compareTo(desempateDaLista.getOrdem()) == 0) {
                    FacesUtil.addOperacaoNaoPermitida("Existem dois critérios de desempate com a mesma ordem, verifique as informações e tente novamente.");
                    return true;
                }
            }
        }
        return false;
    }

    private boolean podeSalvar() {
        if (!Util.validaCampos(selecionado)) {
            return false;
        }
        if (!temLocalAdicionado()) {
            return false;
        }
        if (!temCargoAdicionado()) {
            return false;
        }
        if (possuiDoisCriteriosComAmesmaOrdem()) {
            return false;
        }

        return true;
    }

    private boolean temLocalAdicionado() {
        if (selecionado.getLocais() == null || selecionado.getLocais().isEmpty()) {
            FacesUtil.addCampoObrigatorio("É obrigatório adicionar um local para o concurso!");
            return false;
        }
        return true;
    }

    private boolean temCargoAdicionado() {
        if (selecionado.getCargos() == null || selecionado.getCargos().isEmpty()) {
            FacesUtil.addCampoObrigatorio("É obrigatório adicionar um cargo para o concurso!");
            return false;
        }
        return true;
    }

    private void setarAno() {
        if (isOperacaoNovo()) {
            selecionado.setAno(DataUtil.getAno(concursoFacade.getSistemaFacade().getDataOperacao()));
        }
    }

    public void setarCodigo() {
        if (isOperacaoNovo()) {
            selecionado.setCodigo(getProximoCodigo());
        }
    }

    public Integer getProximoCodigo() {
        return concursoFacade.lista().size() + 1;
    }

    public void recuperarEditaVer() {
        this.hierarquiaOrganizacionalSelecionada = concursoFacade.getHierarquiaOrganizacionalFacade().recuperaHierarquiaOrganizacionalPelaUnidade(selecionado.getUnidadeOrganizacional().getId());
    }

    // .:GETTER AND SETTER:.
    public HierarquiaOrganizacional getHierarquiaOrganizacionalSelecionada() {
        return hierarquiaOrganizacionalSelecionada;
    }

    public void setHierarquiaOrganizacionalSelecionada(HierarquiaOrganizacional hierarquiaOrganizacionalSelecionada) {
        this.hierarquiaOrganizacionalSelecionada = hierarquiaOrganizacionalSelecionada;
    }

    public LocalConcurso getLocalConcursoSelecionado() {
        return localConcursoSelecionado;
    }

    public void setLocalConcursoSelecionado(LocalConcurso localConcursoSelecionado) {
        this.localConcursoSelecionado = localConcursoSelecionado;
    }

    public LocalConcurso getLocalConcursoSelecionadoBackup() {
        return localConcursoSelecionadoBackup;
    }

    public void setLocalConcursoSelecionadoBackup(LocalConcurso localConcursoSelecionadoBackup) {
        this.localConcursoSelecionadoBackup = localConcursoSelecionadoBackup;
    }

    public CargoConcurso getCargoConcursoSelecionado() {
        return cargoConcursoSelecionado;
    }

    public void setCargoConcursoSelecionado(CargoConcurso cargoConcursoSelecionado) {
        this.cargoConcursoSelecionado = cargoConcursoSelecionado;
    }

    public CargoConcurso getCargoConcursoSelecionadoBackup() {
        return cargoConcursoSelecionadoBackup;
    }

    public void setCargoConcursoSelecionadoBackup(CargoConcurso cargoConcursoSelecionadoBackup) {
        this.cargoConcursoSelecionadoBackup = cargoConcursoSelecionadoBackup;
    }

    public List<MembroComissao> getMembrosComissao() {
        return membrosComissao;
    }

    public void setMembrosComissao(List<MembroComissao> membrosComissao) {
        this.membrosComissao = membrosComissao;
    }
    // ========== FIM GETTER AND SETTER

    public List<HierarquiaOrganizacional> completaHierarquias(String parte) {
        return concursoFacade.getHierarquiaOrganizacionalFacade().filtraNivelDoisEComRaiz(parte.trim(), TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), UtilRH.getDataOperacao());
    }

    public Converter getConverterHierarquia() {
        if (converterHierarquia == null) {
            converterHierarquia = new ConverterAutoComplete(HierarquiaOrganizacional.class, concursoFacade.getHierarquiaOrganizacionalFacade());
        }
        return converterHierarquia;
    }

    public void setarOrgao() {
        selecionado.setUnidadeOrganizacional(hierarquiaOrganizacionalSelecionada.getSubordinada());
    }

    // .:LOCAIS:.
    public void novoLocal() {
        this.localConcursoSelecionado = new LocalConcurso(selecionado);
    }

    public void cancelarLocal() {
        cancelarLocalSelecionado();
        cancelarLocalBackup();
    }

    public void cancelarLocalSelecionado() {
        this.localConcursoSelecionado = null;
    }

    public void cancelarLocalBackup() {
        if (localConcursoSelecionadoBackup != null) {
            adicionaLocal(localConcursoSelecionadoBackup);
        }
        this.localConcursoSelecionadoBackup = null;
    }

    public void confirmarLocal() {
        if (podeConfirmarLocal()) {
            adicionaLocal(this.localConcursoSelecionado);
            cancelarLocalSelecionado();
        }
    }

    public void adicionaLocal(LocalConcurso local) {
        selecionado.setLocais(Util.adicionarObjetoEmLista(selecionado.getLocais(), local));
    }

    private boolean podeConfirmarLocal() {
        return Util.validaCampos(localConcursoSelecionado.getEnderecoCorreio());
    }

    public void selecionarLocal(LocalConcurso local) {
        this.localConcursoSelecionadoBackup = (LocalConcurso) Util.clonarObjeto(local);
        this.localConcursoSelecionado = (LocalConcurso) Util.clonarObjeto(local);
        removerLocal(local);
    }

    public void removerLocal(LocalConcurso local) {
        selecionado.getLocais().remove(local);
    }

    public List<SelectItem> getTiposDeLocal() {
        List<SelectItem> retorno = new ArrayList<>();
        for (TipoLocalConcurso tipoLocalConcurso : TipoLocalConcurso.values()) {
            retorno.add(new SelectItem(tipoLocalConcurso, tipoLocalConcurso.getDescricao()));
        }
        return retorno;
    }

    public List<String> completaCep(String parte) {
        return concursoFacade.getConsultaCepFacade().consultaLogradouroPorParteCEPByString(parte.trim());
    }

    public void atualizaLogradouros() {
        concursoFacade.getConsultaCepFacade().atualizarLogradouros(localConcursoSelecionado.getEnderecoCorreio());
    }
    // ========== FIM LOCAIS

    // .:CARGO:.
    public void novoCargo() {
        this.cargoConcursoSelecionado = new CargoConcurso(selecionado);
    }

    public void somarTotalVagas() {
        cargoConcursoSelecionado.setVagasTotais(getVagasDisponiveis() + getVagasEspeciais());
    }

    private Integer getVagasEspeciais() {
        if (cargoConcursoSelecionado.getVagasEspeciais() != null) {
            return cargoConcursoSelecionado.getVagasEspeciais();
        }
        return 0;
    }

    private Integer getVagasDisponiveis() {
        if (cargoConcursoSelecionado.getVagasDisponiveis() != null) {
            return cargoConcursoSelecionado.getVagasDisponiveis();
        }
        return 0;
    }

    public BigDecimal remuneracaoInicialDoCargo() {
        if (cargoConcursoSelecionado == null || cargoConcursoSelecionado.getCargo() == null) {
            return null;
        }
        return concursoFacade.getCargoFacade().getRemuneracaoInicialDoCargo(cargoConcursoSelecionado.getCargo());
    }

    public void confirmarCargo() {
        if (podeConfirmarCargo()) {
            adicionarCargo(cargoConcursoSelecionado);
            cancelarCargoSelecionado();
        }
    }

    public void adicionarCargo(CargoConcurso cargo) {
        selecionado.setCargos(Util.adicionarObjetoEmLista(selecionado.getCargos(), cargo));
    }

    private boolean podeConfirmarCargo() {
        return Util.validaCampos(cargoConcursoSelecionado);
    }

    public void selecionarCargo(CargoConcurso cargo) {
        this.cargoConcursoSelecionado = (CargoConcurso) Util.clonarObjeto(cargo);
        this.cargoConcursoSelecionadoBackup = (CargoConcurso) Util.clonarObjeto(cargo);
        removerCargo(cargo);
    }

    public void removerCargo(CargoConcurso cargo) {
        selecionado.getCargos().remove(cargo);
    }

    public void cancelarCargo() {
        cancelarCargoSelecionado();
        cancelarCargoSelecionadoBackup();
    }

    public void cancelarCargoSelecionado() {
        this.cargoConcursoSelecionado = null;
    }

    public void cancelarCargoSelecionadoBackup() {
        if (cargoConcursoSelecionadoBackup != null) {
            adicionarCargo(cargoConcursoSelecionadoBackup);
        }
        this.cargoConcursoSelecionadoBackup = null;
    }

    public ConverterAutoComplete getConverterCargoConcurso() {
        return new ConverterAutoComplete(Cargo.class, concursoFacade.getCargoFacade());
    }

    public List<Cargo> completaCargo(String parte) {
        return concursoFacade.getCargoFacade().buscarCargosVigentesPorUnidadeOrganizacionalAndUsuario(parte.trim());
    }
    // ========= FIM CARGO

    @Override
    public String getCaminhoPadrao() {
        return "/concurso/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return concursoFacade;
    }

    public DesempateConcurso getDesempateSelecionado() {
        return desempateSelecionado;
    }

    public void setDesempateSelecionado(DesempateConcurso desempateSelecionado) {
        this.desempateSelecionado = desempateSelecionado;
    }

    public void novoDesempate() {
        this.desempateSelecionado = new DesempateConcurso();
        this.desempateSelecionado.setConcurso(selecionado);
        this.desempateSelecionado.setOrdem(getProximaOrdemDesempate());
    }

    public void cancelarDesempate() {
        this.desempateSelecionado = null;
    }

    public void selecionarDesempate(DesempateConcurso dc) {
        this.desempateSelecionado = dc;
    }

    public void removerDesempate(DesempateConcurso dc) {
        selecionado.getDesempates().remove(dc);
    }

    public void confirmarDesempate() {
        if (!Util.validaCampos(this.desempateSelecionado)) {
            return;
        }

        selecionado.setDesempates(Util.adicionarObjetoEmLista(selecionado.getDesempates(), desempateSelecionado));
        cancelarDesempate();
    }

    private Integer getProximaOrdemDesempate() {
        try {
            return selecionado.getDesempates().size() + 1;
        } catch (Exception e) {
            return 1;
        }
    }

    public List<SelectItem> getCriteriosDeDesempate() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (CriterioDesempate cd : CriterioDesempate.values()) {
            toReturn.add(new SelectItem(cd, cd.getDescricao()));
        }
        return toReturn;
    }

    public void navegarAtoLegal() {
        Web.navegacao(getUrlAtual(), "/atolegal/novo/", selecionado, 5);
    }

    public void selecionarObjetoPesquisaGenerico(ActionEvent ac) {
        AtoLegal a = (AtoLegal) ac.getComponent().getAttributes().get("objeto");
        publicacaoSelecionada.setAtoLegal(a);
    }

    public List<AtoLegal> completaAtoLegal(String parte) {
        return atoLegalFacade.buscarAtoLegalPorNomeNumero(parte.trim());
    }
    public ConverterAutoComplete getConverterAtoLegal() {
        return new ConverterAutoComplete(AtoLegal.class, atoLegalFacade);
    }

    public PublicacaoConcurso getPublicacaoSelecionada() {
        return publicacaoSelecionada;
    }

    public void setPublicacaoSelecionada(PublicacaoConcurso publicacaoSelecionada) {
        this.publicacaoSelecionada = publicacaoSelecionada;
    }

    public void novoPublicacao() {
        publicacaoSelecionada = new PublicacaoConcurso();
        publicacaoSelecionada.setConcurso(selecionado);
        publicacaoSelecionada.setCadastradaEm(UtilRH.getDataOperacao());
    }

    public void confirmarPublicacao() {
        logger.debug("confirmando...");
        if (!Util.validaCampos(publicacaoSelecionada)) {
            return;
        }
        logger.debug("lista {} {}",publicacaoSelecionada, selecionado.getPublicacoes() );
        selecionado.getPublicacoes().add(publicacaoSelecionada);
        logger.debug("lista {} {}",publicacaoSelecionada, selecionado.getPublicacoes() );
        popularMembrosComissao();
        cancelarPublicacao();
    }

    public void cancelarPublicacao() {
        publicacaoSelecionada = null;
    }

    public void removerPublicacao(PublicacaoConcurso pc) {
        selecionado.getPublicacoes().remove(pc);
    }

    public void popularMembrosComissao(){
        membrosComissao = Lists.newLinkedList();
        for (PublicacaoConcurso publicacaoConcurso : selecionado.getPublicacoes()) {
            AtoLegal ato = publicacaoConcurso.getAtoLegal();
            for (Comissao comissao : atoLegalFacade.getComissaoFacade().recuperaComissoesPorAtoLegal(ato.getAtoDeComissao())) {
                for (MembroComissao membroComissao : comissao.getMembroComissao()) {
                    if(!membrosComissao.contains(membroComissao)){
                        membrosComissao.add(membroComissao);
                    }
                }
            }
        }
    }

    public StreamedContent baixarEdital(Arquivo arquivo) {
        logger.debug("arquivo {}", arquivo);
        try {
            if(arquivo != null) {
                /*Arquivo file = arquivoFacade.recuperaDependencias(arquivo.getId());
                file.montarImputStream();
                return new DefaultStreamedContent(file.getInputStream(), file.getMimeType(), file.getNome());*/
                return arquivoFacade.montarArquivoParaDownloadPorArquivo(arquivo);
            }
        } catch (Exception e) {
            logger.error("Erro ao recuperar arquivo ", e);
            FacesUtil.addOperacaoNaoRealizada("Não foi possível baixar o Edital em anexo. Detlhes: "+ e.getMessage());
        }
        return null;
    }
}
