package br.com.webpublico.controle.rh.concursos;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.rh.concursos.*;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.MatriculaFPFacade;
import br.com.webpublico.negocios.PessoaFisicaFacade;
import br.com.webpublico.negocios.rh.concursos.ApresentacaoConcursoFacade;
import br.com.webpublico.negocios.rh.concursos.CargoConcursoFacade;
import br.com.webpublico.negocios.rh.concursos.ClassificacaoConcursoFacade;
import br.com.webpublico.negocios.rh.concursos.ConcursoFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.UtilRH;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@ManagedBean(name = "apresentacaoConcursoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-apresentacao-concurso", pattern = "/concursos/apresentacao/novo/", viewId = "/faces/rh/concursos/apresentacao/edita.xhtml"),
    @URLMapping(id = "editar-apresentacao-concurso", pattern = "/concursos/apresentacao/editar/#{apresentacaoConcursoControlador.id}/", viewId = "/faces/rh/concursos/apresentacao/edita.xhtml"),
    @URLMapping(id = "ver-apresentacao-concurso", pattern = "/concursos/apresentacao/ver/#{apresentacaoConcursoControlador.id}/", viewId = "/faces/rh/concursos/apresentacao/edita.xhtml"),
    @URLMapping(id = "listar-apresentacao-concurso", pattern = "/concursos/apresentacao/listar/", viewId = "/faces/rh/concursos/apresentacao/lista.xhtml")
})
public class ApresentacaoConcursoControlador extends PrettyControlador<ApresentacaoConcurso> implements Serializable, CRUD {

    @EJB
    private ClassificacaoConcursoFacade classificacaoConcursoFacade;
    @EJB
    private ApresentacaoConcursoFacade apresentacaoConcursoFacade;
    @EJB
    private ConcursoFacade concursoFacade;
    @EJB
    private CargoConcursoFacade cargoConcursoFacade;
    @EJB
    private PessoaFisicaFacade pessoaFisicaFacade;
    @EJB
    private MatriculaFPFacade matriculaFPFacade;
    private CargoConcurso cargoSelecionado;
    private Concurso concursoSelecionado;
    private ClassificacaoConcurso classificacaoConcursoSelecionada;
    private ClassificacaoInscricao classificacaoInscricaoSelecionada;

    public Concurso getConcursoSelecionado() {
        return concursoSelecionado;
    }

    public void setConcursoSelecionado(Concurso concursoSelecionado) {
        this.concursoSelecionado = concursoSelecionado;
    }

    public ApresentacaoConcursoControlador() {
        super(ApresentacaoConcurso.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return apresentacaoConcursoFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/concursos/apresentacao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novo-apresentacao-concurso", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "ver-apresentacao-concurso", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        carregarParametrosIniciais();
    }

    @URLAction(mappingId = "editar-apresentacao-concurso", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        carregarParametrosIniciais();
    }

    private void carregarParametrosIniciais() {
        selecionado = apresentacaoConcursoFacade.recuperar(getId());
        classificacaoConcursoSelecionada = classificacaoConcursoFacade.buscarClassificacaoComClassificados(selecionado.getCargo().getClassificacaoConcurso().getId());
        concursoSelecionado = concursoFacade.recuperarConcursoComCriteriosDeDesempate(selecionado.getCargo().getConcurso().getId());
        cargoSelecionado = selecionado.getCargo();
        Collections.sort(classificacaoConcursoSelecionada.getInscricoes());
    }

    public ConverterAutoComplete getConverterCargoConcurso() {
        return new ConverterAutoComplete(CargoConcurso.class, cargoConcursoFacade);
    }

    public Converter getConverterConcurso() {
        return new Converter() {
            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {

                try {
                    return concursoFacade.buscarConcursosComCargosAndInscricoesAndCriteriosDesempate(Long.parseLong(value));
                } catch (Exception ex) {
                }
                return null;
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                String resultado = null;
                if (value == null) {
                    return null;
                } else {
                    if (value instanceof Long) {
                        resultado = String.valueOf(value);
                    } else {
                        try {
                            return "" + ((Concurso) value).getId();
                        } catch (Exception e) {
                            resultado = String.valueOf(value);
                        }
                    }
                }
                return resultado;
            }
        };
    }

    public List<SelectItem> getConcursos() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, "Selecione um Concurso..."));
        for (Concurso c : concursoFacade.buscarConcursosParaTelaDeApresentacao()) {
            toReturn.add(new SelectItem(c, "" + c));
        }
        return toReturn;
    }

    public List<SelectItem> getCargos() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        if (concursoSelecionado == null) {
            toReturn.add(new SelectItem(null, ""));
            return toReturn;
        }
        toReturn.add(new SelectItem(null, "Selecione um Cargo..."));
        for (CargoConcurso c : concursoSelecionado.getCargos()) {
            toReturn.add(new SelectItem(c, "" + c));
        }
        return toReturn;
    }

    public CargoConcurso getCargoSelecionado() {
        return cargoSelecionado;
    }

    public void setCargoSelecionado(CargoConcurso cargoSelecionado) {
        this.cargoSelecionado = cargoSelecionado;
    }

    public void limparSelecionadoAndCargo() {
        cargoSelecionado = null;
        classificacaoConcursoSelecionada = null;
    }

    public String converterBooleanParaSimOuNao(Boolean valor) {
        return valor == null ? "Não" : valor ? "Sim" : "Não";
    }

    public String recuperarCorDoTexto(Boolean valor) {
        return valor == null ? "Não" : valor ? "verde" : "vermelho-escuro";
    }

    private void validarCamposObrigatorios() {
        ValidacaoException ve = new ValidacaoException();

        if (concursoSelecionado == null) {
            ve.adicionarMensagemError(SummaryMessages.CAMPO_OBRIGATORIO, "O campo Concurso deve ser informado.");
        }

        if (cargoSelecionado == null) {
            ve.adicionarMensagemError(SummaryMessages.CAMPO_OBRIGATORIO, "O campo Cargo deve ser informado.");
        }

        if (ve.temMensagens()) {
            throw ve;
        }
    }

    public void buscarClassificacaoJaExistente() {
        try {
            validarCamposObrigatorios();
            classificacaoConcursoSelecionada = classificacaoConcursoFacade.buscarClassificacaoComClassificados(cargoSelecionado.getClassificacaoConcurso().getId());
            Collections.sort(classificacaoConcursoSelecionada.getInscricoes());
            if(classificacaoConcursoSelecionada != null){
                salvarSelecionado();
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void salvarSelecionado() {
        selecionado.setCargo(cargoSelecionado);
        selecionado.setConcurso(concursoSelecionado);
        selecionado.setRealizadaEm(new Date());
        selecionado = apresentacaoConcursoFacade.salvar(selecionado, classificacaoConcursoSelecionada);
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "editar/" + selecionado.getId() + "/");
    }

    @Override
    public void salvar() {
        if (classificacaoConcursoSelecionada == null) {
            FacesUtil.addOperacaoNaoPermitida("É necessário recuperar uma classificação para poder salvar. Verifique as informações e tente novamente.");
            return;
        }

        selecionado.setCargo(cargoSelecionado);
        selecionado.setConcurso(concursoSelecionado);
        selecionado.setRealizadaEm(UtilRH.getDataOperacao());

        try {
            apresentacaoConcursoFacade.salvar(selecionado, classificacaoConcursoSelecionada);
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
            return;
        } catch (Exception e) {
            descobrirETratarException(e);
        }
        redireciona();
    }

    public ClassificacaoConcurso getClassificacaoConcursoSelecionada() {
        return classificacaoConcursoSelecionada;
    }

    public void setClassificacaoConcursoSelecionada(ClassificacaoConcurso classificacaoConcursoSelecionada) {
        this.classificacaoConcursoSelecionada = classificacaoConcursoSelecionada;
    }

    public void salvarInscricaoClassificacao(ClassificacaoInscricao ci) {
        ci = apresentacaoConcursoFacade.salvarClassificacaoInscricao(ci);
        classificacaoConcursoSelecionada.setInscricoes(Util.adicionarObjetoEmLista(classificacaoConcursoSelecionada.getInscricoes(), ci));
    }

    public ClassificacaoInscricao getClassificacaoInscricaoSelecionada() {
        return classificacaoInscricaoSelecionada;
    }

    public void setClassificacaoInscricaoSelecionada(ClassificacaoInscricao classificacaoInscricaoSelecionada) {
        this.classificacaoInscricaoSelecionada = classificacaoInscricaoSelecionada;
    }

    public void incluirCandidatoNoSistema(ClassificacaoInscricao classificacaoInscricao) {
        classificacaoInscricaoSelecionada = classificacaoInscricao;
        Long idPessoa = pessoaFisicaFacade.buscarIdDePessoaPorCpf(classificacaoInscricao.getInscricaoConcurso().getCpf());
        Long idMatricula = null;

        if (idPessoa != null) {
            idMatricula = matriculaFPFacade.buscarMatriculaDaPessoa(idPessoa);
        }

        classificacaoInscricaoSelecionada.setIdPessoa(idPessoa);
        classificacaoInscricaoSelecionada.setIdMatricula(idMatricula);

        FacesUtil.executaJavaScript("dialogClassificacaoPessoa.show()");
        FacesUtil.atualizarComponente("form-dialog-classificacao-pessoa");
    }
}
