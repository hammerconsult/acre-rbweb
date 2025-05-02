package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SituacaoCapacitacao;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.InscricaoEventoCapacitacaoFacade;
import br.com.webpublico.util.FacesUtil;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.SelectEvent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by AndreGustavo on 30/10/2014.
 */
@ManagedBean(name = "inscricaoEventoCapacitacaoControlador")
@ViewScoped
@URLMappings(
        mappings = {
                @URLMapping(id = "novaInscricao",
                        pattern = "/evento-de-capacitacao/inscricao/",
                        viewId = "/faces/rh/administracaodepagamento/capacitacaoservidor/inscricaoeventocapacitacao/inscricao.xhtml")
        }
)
public class InscricaoEventoCapacitacaoControlador extends PrettyControlador<InscricaoCapacitacao> implements Serializable, CRUD {
    @EJB
    private InscricaoEventoCapacitacaoFacade inscricaoEventoCapacitacaoFacade;


    private InscricaoCapacitacao inscricaoCapacitacao;
    private Capacitacao capacitacaoSelecionado;
    private MatriculaFP matriculaSelecionada;
    private List<CapacitacaoHabilidade> habilidadesCapacitacao;
    private List<ModuloCapacitacao> modulosCapacitacao;
    private List<InscricaoCapacitacao> inscricoesCapacitacao;
    private List<InscricaoCapacitacao> inscricoesCapacitacaoExcluidas;

    public InscricaoEventoCapacitacaoControlador() {
        super(InscricaoCapacitacao.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return null;
    }

    @Override
    public Object getUrlKeyValue() {
        return null;
    }

    @Override
    public AbstractFacade getFacede() {
        return inscricaoEventoCapacitacaoFacade;
    }

    @Override
    @URLAction(mappingId = "novaInscricao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        inscricaoCapacitacao = new InscricaoCapacitacao();
        habilidadesCapacitacao = new ArrayList<>();
        modulosCapacitacao = new ArrayList<>();
        inscricoesCapacitacao = new ArrayList<>();
    }

    @Override
    public void cancelar() {
        FacesUtil.redirecionamentoInterno("/evento-de-capacitacao/listar/");
    }

    @Override
    public void salvar() {
        for (InscricaoCapacitacao obj : inscricoesCapacitacao) {
            if (obj.getId() == null) {
                obj.setNumero(inscricaoEventoCapacitacaoFacade.getSingletonGeradorCodigo().getProximoCodigo(InscricaoCapacitacao.class, "numero"));
            }
        }
        try {
            inscricaoEventoCapacitacaoFacade.salvar(inscricoesCapacitacao, inscricoesCapacitacaoExcluidas);
            for (InscricaoCapacitacao capacitacao : inscricoesCapacitacao) {
                if (pessoaTemEmail(capacitacao.getMatriculaFP().getPessoa())) {
                    inscricaoEventoCapacitacaoFacade.enviaEmail(capacitacao.getMatriculaFP().getPessoa().getEmail(), montaCorpoEMail(capacitacao), "Inscrição para Evento de Capacitação");
                } else {
                    FacesUtil.addAtencao("O Servidor " + capacitacao.getMatriculaFP() + " não possui E-Mail para contato.");
                }
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
        FacesUtil.addOperacaoRealizada("Operação realizada com sucesso.");
        FacesUtil.redirecionamentoInterno("/evento-de-capacitacao/listar/");
    }

    public boolean pessoaTemEmail(Pessoa p) {
        return p.getEmail() != null || !p.getEmail().trim().isEmpty();
    }

    public String montaCorpoEMail(InscricaoCapacitacao obj) {
        StringBuilder corpo = new StringBuilder();
        corpo.append("<p>Ol&aacute; " + obj.getMatriculaFP().getPessoa().getNome() + ".</p>");
        corpo.append("<p>Informamos que sua inscri&ccedil;&atilde;o para o Evento de Capacita&ccedil;&atilde;o foi realizada.</p>");
        corpo.append("<p>&nbsp;</p>");
        corpo.append("<p><strong>Evento :</strong> " + obj.getCapacitacao().getNome() + "</p>");
        corpo.append("<p><strong>Promotor do Evento:</strong> " + obj.getCapacitacao().getPromotorEvento().getNome() + "</p>");
        corpo.append("<p><strong>Carga Hor&aacute;ria:</strong> " + obj.getCapacitacao().getCargaHoraria() + "</p>");
        corpo.append("<p><strong>Data de In&iacute;cio:</strong> " + new SimpleDateFormat("dd/MM/yyyy").format(obj.getCapacitacao().getDataInicio()) + "</p>");
        corpo.append("<p><strong>M&oacute;dulos:</strong></p>");
        corpo.append("<ul>");
        for (ModuloCapacitacao mod : inscricaoEventoCapacitacaoFacade.getEventoCapacitacaoFacade().recuperar(obj.getCapacitacao().getId()).getModulos()) {
            corpo.append("<li>" + new SimpleDateFormat("dd/MM/yyyy").format(mod.getDataInicioModulo()) + " - " + mod.getNomeModulo() + " </li>");
        }
        corpo.append("</ul>");
        return corpo.toString();
    }

    public void removerInscricao(InscricaoCapacitacao insCapa) {
        if (insCapa != null) {
            inscricoesCapacitacao.remove(insCapa);
            inscricoesCapacitacaoExcluidas.add(insCapa);
        }
    }


    public List<Capacitacao> completaCapacitacao(String parte) {
        return inscricaoEventoCapacitacaoFacade.getEventoCapacitacaoFacade().buscarEventosCapacitacaoPorSituacaoEmAndamento(parte, SituacaoCapacitacao.EM_ANDAMENTO);
    }

    public List<MatriculaFP> completarMatriculaFP(String parte) {
        return inscricaoEventoCapacitacaoFacade.getMatriculaFPFacade().recuperarMatriculasComContratoVigente(parte);
    }

    public void carregaEventoCapacitacao(SelectEvent evt) {
        setCapacitacaoSelecionado(inscricaoEventoCapacitacaoFacade.getEventoCapacitacaoFacade().recuperar(((Capacitacao) evt.getObject()).getId()));
        habilidadesCapacitacao = capacitacaoSelecionado.getHabilidades();
        modulosCapacitacao = capacitacaoSelecionado.getModulos();
        inscricoesCapacitacao = capacitacaoSelecionado.getInscricoes();
        inscricoesCapacitacaoExcluidas = Lists.newArrayList();

    }

    public void teste() {
        if (capacitacaoSelecionado != null) {
            //System.out.println(capacitacaoSelecionado.getHabilidades().size());
        }
    }

    public void adicionarInscricao() {
        if (validaInscricao()) {
            inscricaoCapacitacao.setCapacitacao(capacitacaoSelecionado);
            inscricaoCapacitacao.setMatriculaFP(matriculaSelecionada);
            inscricoesCapacitacao.add(inscricaoCapacitacao);
            matriculaSelecionada = null;
            inscricaoCapacitacao = new InscricaoCapacitacao();
        }
    }

    public boolean validaInscricao() {
        boolean valida = true;

        if (matriculaSelecionada == null) {
            FacesUtil.addCampoObrigatorio("A MatrículaFP é um campo obrigatório.");
            valida = false;
        } else {
            for (InscricaoCapacitacao obj : inscricoesCapacitacao) {
                if (matriculaSelecionada.equals(obj.getMatriculaFP())) {
                    FacesUtil.addOperacaoNaoPermitida("A MatrículaFP " + matriculaSelecionada + " já foi inscrita no evento de capacitação.");
                    valida = false;
                }
            }
        }

        if (inscricoesCapacitacao.size() >= capacitacaoSelecionado.getQtdVagas()) {
            FacesUtil.addOperacaoNaoPermitida("O número de vagas está esgotado.");
            valida = false;
        }

        return valida;
    }

    //-----------------------------------GETTERS AND SETTERS---------------------------------------------------------------
    public Capacitacao getCapacitacaoSelecionado() {
        return capacitacaoSelecionado;
    }

    public void setCapacitacaoSelecionado(Capacitacao capacitacaoSelecionado) {
        this.capacitacaoSelecionado = capacitacaoSelecionado;
    }

    public MatriculaFP getMatriculaSelecionada() {
        return matriculaSelecionada;
    }

    public void setMatriculaSelecionada(MatriculaFP matriculaSelecionada) {
        this.matriculaSelecionada = matriculaSelecionada;
    }

    public InscricaoCapacitacao getInscricaoCapacitacao() {
        return inscricaoCapacitacao;
    }

    public void setInscricaoCapacitacao(InscricaoCapacitacao inscricaoCapacitacao) {
        this.inscricaoCapacitacao = inscricaoCapacitacao;
    }

    public List<CapacitacaoHabilidade> getHabilidadesCapacitacao() {
        return habilidadesCapacitacao;
    }

    public void setHabilidadesCapacitacao(List<CapacitacaoHabilidade> habilidadesCapacitacao) {
        this.habilidadesCapacitacao = habilidadesCapacitacao;
    }

    public List<ModuloCapacitacao> getModulosCapacitacao() {
        return modulosCapacitacao;
    }

    public void setModulosCapacitacao(List<ModuloCapacitacao> modulosCapacitacao) {
        this.modulosCapacitacao = modulosCapacitacao;
    }

    public List<InscricaoCapacitacao> getInscricoesCapacitacao() {
        return inscricoesCapacitacao;
    }

    public void setInscricoesCapacitacao(List<InscricaoCapacitacao> inscricoesCapacitacao) {
        this.inscricoesCapacitacao = inscricoesCapacitacao;
    }

    public List<InscricaoCapacitacao> getInscricoesCapacitacaoExcluidas() {
        return inscricoesCapacitacaoExcluidas;
    }

    public void setInscricoesCapacitacaoExcluidas(List<InscricaoCapacitacao> inscricoesCapacitacaoExcluidas) {
        this.inscricoesCapacitacaoExcluidas = inscricoesCapacitacaoExcluidas;
    }
}
