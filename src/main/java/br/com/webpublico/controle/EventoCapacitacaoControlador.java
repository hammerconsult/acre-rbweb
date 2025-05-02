package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SituacaoCadastralPessoa;
import br.com.webpublico.enums.SituacaoCapacitacao;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.EventoCapacitacaoFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
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
 * Created by AndreGustavo on 17/10/2014.
 */
@ManagedBean(name = "eventoCapacitacaoControlador")
@ViewScoped
@URLMappings(
        mappings = {
                @URLMapping(id = "listarEventoCapacitacao",
                        pattern = "/evento-de-capacitacao/listar/",
                        viewId = "/faces/rh/administracaodepagamento/capacitacaoservidor/eventocapacitacao/lista.xhtml"),
                @URLMapping(id = "criarEventoCapacitacao",
                        pattern = "/evento-de-capacitacao/novo/",
                        viewId = "/faces/rh/administracaodepagamento/capacitacaoservidor/eventocapacitacao/edita.xhtml"),
                @URLMapping(id = "editarEventoCapacitacao",
                        pattern = "/evento-de-capacitacao/editar/#{eventoCapacitacaoControlador.id}/",
                        viewId = "/faces/rh/administracaodepagamento/capacitacaoservidor/eventocapacitacao/edita.xhtml"),
                @URLMapping(id = "verEventoCapacitacao",
                        pattern = "/evento-de-capacitacao/ver/#{eventoCapacitacaoControlador.id}/",
                        viewId = "/faces/rh/administracaodepagamento/capacitacaoservidor/eventocapacitacao/visualizar.xhtml")
        }
)
public class EventoCapacitacaoControlador extends PrettyControlador<Capacitacao> implements Serializable, CRUD {
    @EJB
    EventoCapacitacaoFacade eventoCapacitacaoFacade;

    private ModuloCapacitacao moduloCapacitacao;
    private InstrutorModulo instrutor;
    private MetodologiaModulo metodologiaModulo;
    private CapacitacaoHabilidade capacitacaoHabilidade;
    private MetodoCientifCapacitacao metodoCientifCapacitacao;
    private boolean edicaoModulo;
    private List<PresencaModulo> presencaModulo;
    private SituacaoCapacitacao situacaoCapacitacao;
    private Integer indiceAba;
    private Long indiceAbaModulo;
    private Boolean status;
    private ConverterAutoComplete converterPessoaJuridica;

    public EventoCapacitacaoControlador() {
        super(Capacitacao.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/evento-de-capacitacao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return eventoCapacitacaoFacade;
    }

    @Override
    @URLAction(mappingId = "criarEventoCapacitacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();

        indiceAba = 0;
        indiceAbaModulo = 0L;
        Long indiceModulo = (Long) Web.pegaDaSessao(Long.class);
        if (indiceAbaModulo != null) {
            indiceAbaModulo = indiceModulo;
        }

        status = false;
        Integer indice = (Integer) Web.pegaDaSessao(Integer.class);
        if (indice != null) {
            indiceAba = indice;
        }

        PessoaJuridica le = (PessoaJuridica) Web.pegaDaSessao(PessoaJuridica.class);
        if (le != null) {
            selecionado.setPessoaJuridica(le);
        }

        moduloCapacitacao = (ModuloCapacitacao) Web.pegaDaSessao(ModuloCapacitacao.class);
        if (moduloCapacitacao == null) {
            moduloCapacitacao = new ModuloCapacitacao();
        }else{
            status = true;
        }

        instrutor = (InstrutorModulo) Web.pegaDaSessao(InstrutorModulo.class);
        if (instrutor == null) {
            instrutor = new InstrutorModulo();
        }

        metodologiaModulo = (MetodologiaModulo) Web.pegaDaSessao(MetodologiaModulo.class);
        if (metodologiaModulo == null) {
            metodologiaModulo = new MetodologiaModulo();
        }

        metodoCientifCapacitacao = (MetodoCientifCapacitacao) Web.pegaDaSessao(MetodoCientifCapacitacao.class);
        if (metodoCientifCapacitacao == null) {
            metodoCientifCapacitacao = new MetodoCientifCapacitacao();
        }

        capacitacaoHabilidade = (CapacitacaoHabilidade) Web.pegaDaSessao(CapacitacaoHabilidade.class);
        if (capacitacaoHabilidade == null) {
            capacitacaoHabilidade = new CapacitacaoHabilidade();
        }

        situacaoCapacitacao = selecionado.getSituacao();
    }

    @Override
    @URLAction(mappingId = "verEventoCapacitacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    @Override
    @URLAction(mappingId = "editarEventoCapacitacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        indiceAba = 0;
        indiceAbaModulo = 0L;
        moduloCapacitacao = new ModuloCapacitacao();
        instrutor = new InstrutorModulo();
        metodologiaModulo = new MetodologiaModulo();
        metodoCientifCapacitacao = new MetodoCientifCapacitacao();
        capacitacaoHabilidade = new CapacitacaoHabilidade();
        status = false;
        super.editar();
        situacaoCapacitacao = selecionado.getSituacao();
        instanciaDependencias();
    }

    @Override
    public void salvar() {
        if (validaEvento()) {
            super.salvar();
        }
    }

    public List<SelectItem> getSituacoes() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (SituacaoCapacitacao obj : SituacaoCapacitacao.values()) {
            toReturn.add(new SelectItem(obj, obj.toString()));
        }
        return toReturn;
    }

    public void instanciaDependencias() {
        presencaModulo = new ArrayList<>();
//        edicaoModulo = false;
    }

    public void verificaValorSituacao() {
        if (!situacaoCapacitacao.equals(selecionado.getSituacao())) {
            selecionado.setSituacao(situacaoCapacitacao);
            FacesUtil.executaJavaScript("aguarde.hide()");
        }
    }

    public void confirmaAlteracaoSituacao() {
        selecionado.setSituacao(situacaoCapacitacao);
    }

    public void negaAlteracaoSituacao() {
        situacaoCapacitacao = selecionado.getSituacao();
    }

    public String getDescricaoConfirmacao() {
        switch (situacaoCapacitacao) {
            case EM_ANDAMENTO:
                return "Deseja realmente reabrir o evento: " + selecionado.getNome() + "?";
            case CANCELADO:
                return "Deseja realmente cancelar o evento: " + selecionado.getNome() + "?";
            case CONCLUIDO:
                return "Deseja realmente concluir o evento: " + selecionado.getNome() + "?";
            default:
                return "";
        }
    }

    public List<SituacaoCadastralPessoa> getSituacoesPesquisaPessoa() {
        return Lists.newArrayList(SituacaoCadastralPessoa.ATIVO);
    }

    public void poeNaSessao() throws IllegalAccessException {
        Web.poeNaSessao(selecionado);
        Web.poeTodosFieldsNaSessao(this);
    }


    public void mudarStatus() {
        if (!status) {
            status = true;
        } else {
            status = false;
        }
    }

    public void cancelarModulo() {
        moduloCapacitacao = new ModuloCapacitacao();
        mudarStatus();
    }

//    ========================================= LOCAL EVENTO ===========================================================

    public void navegaLocalEvento() {
        Web.navegacao(getUrlAtual(), "/pessoa-juridica/novo/", selecionado, indiceAba);
    }

    public List<PessoaJuridica> completaLocalEvento(String parte) {
        return eventoCapacitacaoFacade.getPessoaJuridicaFacade().listaFiltrandoRazaoSocialCNPJ(parte);
    }

//    ========================================= HABILIDADE =============================================================

    public void navegaHabilidade() {
        Web.navegacao(getUrlAtual(), "/habilidade/novo/", selecionado, capacitacaoHabilidade, indiceAba);
    }

    public void removerHabilidade(CapacitacaoHabilidade capacitacaoHabilidade) {
        selecionado.getHabilidades().remove(capacitacaoHabilidade);
    }

    public void adicionaHabilidade() {
        if (validaHabilidade()) {
            capacitacaoHabilidade.setCapacitacao(selecionado);
            selecionado.getHabilidades().add(capacitacaoHabilidade);
            capacitacaoHabilidade = new CapacitacaoHabilidade();
        }
    }

    public List<Habilidade> completaHabilidade(String parte) {
        return eventoCapacitacaoFacade.getHabilidadeFacade().completaHabilidade(parte);
    }

    public List<AreaFormacao> completaAreaFormacao(String parte) {
        return eventoCapacitacaoFacade.getAreaFormacaoFacade().completaAreaFormacao(parte);
    }

    public void navegaAreaFormacao() {
        Web.navegacao(getUrlAtual(), "/area-de-formacao/novo/", selecionado, instrutor, moduloCapacitacao, metodologiaModulo, indiceAba, indiceAbaModulo);
    }

    public boolean validaHabilidade() {
        boolean valida = true;

        if (capacitacaoHabilidade.getHabilidade() == null) {
            FacesUtil.addCampoObrigatorio("A Habilidade é um campo obrigatório");
            valida = false;
        } else {
            for (CapacitacaoHabilidade obj : selecionado.getHabilidades()) {
                if (obj.getHabilidade().equals(capacitacaoHabilidade.getHabilidade())) {
                    FacesUtil.addOperacaoNaoPermitida("Esta Habilidade já foi inserida");
                    valida = false;
                }
            }
        }

        return valida;
    }

//    ============================================ METODOS =============================================================

    public void navegaMetodoCientifico() {
        Web.navegacao(getUrlAtual(), "/metodo-cientifico/novo/", selecionado, metodoCientifCapacitacao, indiceAba);
    }

    public void removerMetodoCientifico(MetodoCientifCapacitacao metodoCientifCapacitacao) {
        selecionado.getMetodoCientifCapacitacaos().remove(metodoCientifCapacitacao);
    }

    public void adicionaMetodoCientifico() {
        if (validaMetodoCientifico()) {
            metodoCientifCapacitacao.setCapacitacao(selecionado);
            selecionado.getMetodoCientifCapacitacaos().add(metodoCientifCapacitacao);
            metodoCientifCapacitacao = new MetodoCientifCapacitacao();
        }
    }

    public List<MetodoCientifico> completaMetodoCientifico(String parte) {
        return eventoCapacitacaoFacade.getMetodoCientificoFacade().completaMetodoCientifico(parte);
    }

    public boolean validaMetodoCientifico() {
        boolean valida = true;

        if (metodoCientifCapacitacao.getMetodoCientifico() == null) {
            FacesUtil.addCampoObrigatorio("O método científico é um campo obrigatório");
            valida = false;
        } else {
            for (MetodoCientifCapacitacao obj : selecionado.getMetodoCientifCapacitacaos()) {
                if (obj.getMetodoCientifico().equals(metodoCientifCapacitacao.getMetodoCientifico())) {
                    FacesUtil.addOperacaoNaoPermitida("Este método já foi inserido.");
                    valida = false;
                }
            }
        }

        return valida;
    }

//    ============================================= INSTRUTOR ==========================================================

    public void removerInstrutor(InstrutorModulo instrutor) {
        moduloCapacitacao.getInstrutores().remove(instrutor);
    }

    public void adicionarInstrutor() {
        if (validaInstrutor()) {
            instrutor.setModuloCapacitacao(moduloCapacitacao);
            moduloCapacitacao.getInstrutores().add(instrutor);
            instrutor = new InstrutorModulo();
        }
    }

    public boolean validaInstrutor() {
        boolean valida = true;
        if (instrutor.getPessoaFisica() == null) {
            FacesUtil.addCampoObrigatorio("O instrutor é um campo obrigatório.");
            valida = false;
        } else {
            for (InstrutorModulo obj : moduloCapacitacao.getInstrutores()) {
                if (obj.getPessoaFisica().equals(instrutor.getPessoaFisica())) {
                    FacesUtil.addOperacaoNaoPermitida("O Instrutor: " + instrutor.getPessoaFisica().getNome() + " já foi inserido neste módulo.");
                    valida = false;
                }
            }
        }
        return valida;
    }
//============================================== METODOLOGIA ===========================================================

    public void navegaMetodologia() {
        Web.navegacao(getUrlAtual(), "/metodologia/novo/", selecionado, moduloCapacitacao, instrutor, metodologiaModulo, indiceAba, indiceAbaModulo);
    }

    public void removerMetodologia(MetodologiaModulo metodologiaModulo) {
        moduloCapacitacao.getMetodologiaModulos().remove(metodologiaModulo);
    }

    public void adicionarMetodologia() {
        if (validaMetodologia()) {
            metodologiaModulo.setModuloCapacitacao(moduloCapacitacao);
            moduloCapacitacao.getMetodologiaModulos().add(metodologiaModulo);
            metodologiaModulo = new MetodologiaModulo();
        }
    }

    public List<Metodologia> completaMetodologia(String parte) {
        return eventoCapacitacaoFacade.getMetodologiaFacade().completaMetodologia(parte);
    }

    public boolean validaMetodologia() {
        boolean valida = true;

        if (metodologiaModulo.getMetodologia() == null) {
            FacesUtil.addCampoObrigatorio("A metodologia é um campo obrigatório");
            valida = false;
        } else {
            for (MetodologiaModulo obj : moduloCapacitacao.getMetodologiaModulos()) {
                if (obj.getMetodologia().equals(metodologiaModulo.getMetodologia())) {
                    FacesUtil.addOperacaoNaoPermitida("Esta metodologia já foi inserida.");
                    valida = false;
                }
            }
        }

        return valida;
    }

    public void removerModulo(ModuloCapacitacao moduloCapacitacao) {
        selecionado.getModulos().remove(moduloCapacitacao);
    }

    public void editarModulo(ModuloCapacitacao moduloCapacitacao) {
        mudarStatus();
        this.moduloCapacitacao = moduloCapacitacao;
        instrutor = new InstrutorModulo();
        metodologiaModulo = new MetodologiaModulo();
    }

    public void adicionaModulo() {
        if (validaModulo()) {
            moduloCapacitacao.setCapacitacao(selecionado);
            Util.adicionarObjetoEmLista(selecionado.getModulos(), moduloCapacitacao);
            moduloCapacitacao = new ModuloCapacitacao();
            instrutor = new InstrutorModulo();
            metodologiaModulo = new MetodologiaModulo();
            mudarStatus();
        }
    }

    public void encerrarVisualizacaoModulo() {
        mudarStatus();
    }

    public void habilitarVisualizacaoModulo(ModuloCapacitacao moduloCapacitacao) {
        mudarStatus();
        this.moduloCapacitacao = moduloCapacitacao;
    }

    public boolean validaEvento() {
        boolean valida = true;
        if (selecionado.getNome() == null || selecionado.getNome().trim().equals("")) {
            FacesUtil.addCampoObrigatorio("O Nome do Evento é um campo obrigatório.");
            valida = false;
        }

        if (selecionado.getPromotorEvento() == null) {
            FacesUtil.addCampoObrigatorio("O Promotor do Evento é um campo obrigatório.");
            valida = false;
        }

        if (selecionado.getPessoaJuridica() == null) {
            FacesUtil.addCampoObrigatorio("O Local do Evento é um campo obrigatório.");
            valida = false;
        }

        if (selecionado.getDataInicio() == null) {
            FacesUtil.addCampoObrigatorio("A Data de Início é um campo obrigatório.");
            valida = false;
        }

        if (selecionado.getQtdDias() == null) {
            FacesUtil.addCampoObrigatorio("A Quantidade de Dias é um campo obrigatório.");
            valida = false;
        }

        if (selecionado.getCargaHoraria() == null) {
            FacesUtil.addCampoObrigatorio("A Carga Horária é um campo obrigatório.");
            valida = false;
        }

        if (selecionado.getDataTermino() == null) {
            FacesUtil.addCampoObrigatorio("A Data de Término é um campo obrigatório.");
            valida = false;
        }

        if (selecionado.getQtdVagas() == null) {
            FacesUtil.addCampoObrigatorio("A Quantidade de Vagas é um campo obrigatório.");
            valida = false;
        }

        if (selecionado.getMetodoCientifCapacitacaos().isEmpty()) {
            FacesUtil.addCampoObrigatorio("É necessário inserir ao menos um Método.");
            valida = false;
        }

        if (selecionado.getHabilidades().isEmpty()) {
            FacesUtil.addCampoObrigatorio("É necessário inserir ao menos uma Habilidade.");
            valida = false;
        }

        if (selecionado.getModulos().isEmpty()) {
            FacesUtil.addCampoObrigatorio("É necessário inserir ao menos um Módulo");
            valida = false;
        }

        if (selecionado.getDataInicio() != null && selecionado.getDataTermino() != null && selecionado.getDataInicio().after(selecionado.getDataTermino())) {
            FacesUtil.addOperacaoNaoPermitida("A Data de Início da capacitação deve ser inferior ou igual a Data de Término.");
            valida = false;
        }

        if (selecionado.getCargaHoraria() != null && selecionado.getCargaHoraria() <= 0) {
            FacesUtil.addOperacaoNaoPermitida("Informe uma Carga Horária válida.");
            valida = false;
        }

        return valida;
    }

    public boolean validaModulo() {
        boolean valida = true;

        if (moduloCapacitacao.getNomeModulo() == null || moduloCapacitacao.getNomeModulo().trim().equals("")) {
            FacesUtil.addCampoObrigatorio("O Nome do Módulo é um campo obrigatório.");
            valida = false;
        }

        if (moduloCapacitacao.getDataInicioModulo() == null) {
            FacesUtil.addCampoObrigatorio("A Data de Início é um campo obrigatório.");
            valida = false;
        }

        if (moduloCapacitacao.getDataFinalModulo() == null) {
            FacesUtil.addCampoObrigatorio("A Data de Término é um campo obrigatório.");
            valida = false;
        }

        if (moduloCapacitacao.getCargaHoraria() == null) {
            FacesUtil.addCampoObrigatorio("A Carga Horária é um campo obrigatório.");
            valida = false;
        }

        if (moduloCapacitacao.getHoraInicio() == null) {
            FacesUtil.addCampoObrigatorio("A Hora de Início é um campo obrigatório.");
            valida = false;
        }

        if (moduloCapacitacao.getHoraFim() == null) {
            FacesUtil.addCampoObrigatorio("A Hora de Término é um campo obrigatório.");
            valida = false;
        }

        if (moduloCapacitacao.getInstrutores().isEmpty()) {
            FacesUtil.addCampoObrigatorio("É necessário inserir ao menos um Instrutor.");
            valida = false;
        }

        if (moduloCapacitacao.getMetodologiaModulos().isEmpty()) {
            FacesUtil.addCampoObrigatorio("É necessário inserir ao menos uma Metodologia.");
            valida = false;
        }

        if (moduloCapacitacao.getDataInicioModulo() != null && moduloCapacitacao.getDataFinalModulo() != null && moduloCapacitacao.getDataInicioModulo().after(moduloCapacitacao.getDataFinalModulo())) {
            FacesUtil.addOperacaoNaoPermitida("A Data de Início do módulo deve ser inferior ou igual a Data de Término.");
            valida = false;
        }

        if (moduloCapacitacao.getCargaHoraria() != null && moduloCapacitacao.getCargaHoraria() <= 0) {
            FacesUtil.addOperacaoNaoPermitida("Informe uma Carga Horária válida.");
            valida = false;
        }

        if (moduloCapacitacao.getCargaHoraria() != null && selecionado.getCargaHoraria() != null && moduloCapacitacao.getCargaHoraria() > selecionado.getCargaHoraria()) {
            FacesUtil.addOperacaoNaoPermitida("A Carga Horária do módulo deve ser inferior ou igual a Carga Horária da Capacitação.");
            valida = false;
        }

        return valida;
    }

    public boolean renderizaColunaPresencaModulo(ModuloCapacitacao mod) {
        return !selecionado.getInscricoes().isEmpty();
    }

    public List<PresencaModulo> clonaPresencas(List<PresencaModulo> presencas) {
        List<PresencaModulo> lista = new ArrayList<>();

        for (PresencaModulo obj : presencas) {
            PresencaModulo presenca = new PresencaModulo();
            presenca.setInscricaoCapacitacao(obj.getInscricaoCapacitacao());
            presenca.setModuloCapacitacao(obj.getModuloCapacitacao());
            presenca.setId(obj.getId());
            presenca.setPresente(obj.getPresente());
            lista.add(presenca);
        }
        return lista;

    }

    public void carregaListaPresenca(ModuloCapacitacao mod) {
        if (mod.getPresencas().isEmpty()) {
            for (InscricaoCapacitacao inscricao : selecionado.getInscricoes()) {
                PresencaModulo presenca = new PresencaModulo();
                presenca.setModuloCapacitacao(mod);
                presenca.setInscricaoCapacitacao(inscricao);
                presencaModulo.add(presenca);
            }
        } else {
            presencaModulo = clonaPresencas(mod.getPresencas());
        }
    }

    public void confirmaListaPresenca() {
        ModuloCapacitacao modulo = presencaModulo.get(0).getModuloCapacitacao();
        modulo.setPresencas(presencaModulo);
    }

    public void limpaListaPresenca() {
        //System.out.println("Chamou limpar");
        presencaModulo = null;
    }

//----------------------------------------Getters and Setters-----------------------------------------------------------


    public Long getIndiceAbaModulo() {
        return indiceAbaModulo;
    }

    public void setIndiceAbaModulo(Long indiceAbaModulo) {
        this.indiceAbaModulo = indiceAbaModulo;
    }

    public Boolean isStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Integer getIndiceAba() {
        return indiceAba;
    }

    public void setIndiceAba(Integer indiceAba) {
        this.indiceAba = indiceAba;
    }

    public ModuloCapacitacao getModuloCapacitacao() {
        return moduloCapacitacao;
    }

    public void setModuloCapacitacao(ModuloCapacitacao moduloCapacitacao) {
        this.moduloCapacitacao = moduloCapacitacao;
    }

    public InstrutorModulo getInstrutor() {
        return instrutor;
    }

    public void setInstrutor(InstrutorModulo instrutor) {
        this.instrutor = instrutor;
    }

    public MetodologiaModulo getMetodologiaModulo() {
        return metodologiaModulo;
    }

    public void setMetodologiaModulo(MetodologiaModulo metodologiaModulo) {
        this.metodologiaModulo = metodologiaModulo;
    }

    public boolean isEdicaoModulo() {
        return edicaoModulo;
    }

    public void setEdicaoModulo(boolean edicaoModulo) {
        this.edicaoModulo = edicaoModulo;
    }

    public CapacitacaoHabilidade getCapacitacaoHabilidade() {
        return capacitacaoHabilidade;
    }

    public void setCapacitacaoHabilidade(CapacitacaoHabilidade capacitacaoHabilidade) {
        this.capacitacaoHabilidade = capacitacaoHabilidade;
    }

    public MetodoCientifCapacitacao getMetodoCientifCapacitacao() {
        return metodoCientifCapacitacao;
    }

    public void setMetodoCientifCapacitacao(MetodoCientifCapacitacao metodoCientifCapacitacao) {
        this.metodoCientifCapacitacao = metodoCientifCapacitacao;
    }

    public List<PresencaModulo> getPresencaModulo() {
        return presencaModulo;
    }

    public void setPresencaModulo(List<PresencaModulo> presencaModulo) {
        this.presencaModulo = presencaModulo;
    }

    public SituacaoCapacitacao getSituacaoCapacitacao() {
        return situacaoCapacitacao;
    }

    public void setSituacaoCapacitacao(SituacaoCapacitacao situacaoCapacitacao) {
        this.situacaoCapacitacao = situacaoCapacitacao;
    }

    public ConverterAutoComplete getConverterPessoaJuridica() {
        if (converterPessoaJuridica == null) {
            converterPessoaJuridica = new ConverterAutoComplete(PessoaJuridica.class, eventoCapacitacaoFacade.getPessoaJuridicaFacade());
        }
        return converterPessoaJuridica;
    }

    //----------------------------------------FIM Getters and Setters-------------------------------------------------------
}
