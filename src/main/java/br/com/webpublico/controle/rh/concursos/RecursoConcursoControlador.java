package br.com.webpublico.controle.rh.concursos;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.Arquivo;
import br.com.webpublico.entidades.ArquivoParte;
import br.com.webpublico.entidades.rh.concursos.*;
import br.com.webpublico.enums.SituacaoRecurso;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.rh.concursos.RecursoConcursoFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Buzatto
 * on 31/08/2015.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoRecursoConcurso", pattern = "/concursos/recurso/novo/", viewId = "/faces/rh/concursos/recurso/edita.xhtml"),
    @URLMapping(id = "editarRecursoConcurso", pattern = "/concursos/recurso/editar/#{recursoConcursoControlador.id}/", viewId = "/faces/rh/concursos/recurso/edita.xhtml"),
    @URLMapping(id = "verRecursoConcurso", pattern = "/concursos/recurso/ver/#{recursoConcursoControlador.id}/", viewId = "/faces/rh/concursos/recurso/edita.xhtml"),
    @URLMapping(id = "listarRecursoConcurso", pattern = "/concursos/recurso/listar/", viewId = "/faces/rh/concursos/recurso/lista.xhtml")
})
public class RecursoConcursoControlador extends PrettyControlador<RecursoConcurso> implements Serializable, CRUD {

    private static final Logger logger = LoggerFactory.getLogger(RecursoConcursoControlador.class);

    @EJB
    private RecursoConcursoFacade recursoConcursoFacade;
    private Concurso concursoSelecionado;
    private ConverterAutoComplete converterCandidato;
    private ConverterAutoComplete converterCargo;
    private ConverterAutoComplete converterFase;
    private ConverterAutoComplete converterProva;

    public RecursoConcursoControlador() {
        super(RecursoConcurso.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/concursos/recurso/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return recursoConcursoFacade;
    }

    @Override
    @URLAction(mappingId = "novoRecursoConcurso", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        selecionado.setData(recursoConcursoFacade.getSistemaFacade().getDataOperacao());
        selecionado.setSituacaoRecurso(SituacaoRecurso.EM_ANDAMENTO);
    }

    @Override
    @URLAction(mappingId = "verRecursoConcurso", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    @Override
    @URLAction(mappingId = "editarRecursoConcurso", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
    }

    @Override
    public void excluir() {
        super.excluir();
    }

    @Override
    public void salvar() {
        if (podeSalvar()) {
            try {
                setarCodigo();
                recursoConcursoFacade.salvar(selecionado);
                FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            } catch (ValidacaoException ex) {
                FacesUtil.printAllFacesMessages(ex.getMensagens());
                return;
            } catch (Exception e) {
                descobrirETratarException(e);

            }
            redireciona();
        }
    }

    private void setarCodigo() {
        if (isOperacaoNovo()) {
            selecionado.setCodigo(getProximoCodigo());
        }
    }

    public Integer getProximoCodigo() {
        return recursoConcursoFacade.lista().size() + 1;
    }

    private boolean podeSalvar() {
        return Util.validaCampos(selecionado);
    }

    public Concurso getConcursoSelecionado() {
        return concursoSelecionado;
    }

    public void setConcursoSelecionado(Concurso concursoSelecionado) {
        this.concursoSelecionado = concursoSelecionado;
    }

    public List<SelectItem> getSituacoes() {
        List<SelectItem> lista = getNewArrayListSelectItem();
        for (SituacaoRecurso sr : SituacaoRecurso.values()) {
            lista.add(new SelectItem(sr, sr.getDescricao()));
        }
        return lista;
    }

    public List<SelectItem> getConcursos() {
        List<SelectItem> toReturn = getNewArrayListSelectItem();
        toReturn.add(new SelectItem(null, "Selecione um Concurso..."));
        for (Concurso concurso : recursoConcursoFacade.getConcursoFacade().getUltimosConcursosComAvaliacaoDeProva()) {
            toReturn.add(new SelectItem(concurso, concurso.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getCandidatos() {
        List<SelectItem> toReturn = getNewArrayListSelectItem();
        toReturn.add(new SelectItem(null, "Selecione um Candidato..."));
        if (selecionado.temConcurso() && selecionado.temCargo()) {
            Collections.sort(selecionado.getConcurso().getInscricoes());
            for (InscricaoConcurso inscricao : selecionado.getConcurso().getInscricoes()) {
                if (inscricao.getCargoConcurso().equals(selecionado.getCargoConcurso())) {
                    toReturn.add(new SelectItem(inscricao, inscricao.toString()));
                }
            }
        }
        return toReturn;
    }

    public List<SelectItem> getCargos() {
        List<SelectItem> toReturn = getNewArrayListSelectItem();
        toReturn.add(new SelectItem(null, "Selecione um Cargo..."));
        if (selecionado.temConcurso()) {
            if (selecionado.getConcurso().getCargos() != null && !selecionado.getConcurso().getCargos().isEmpty()) {
                for (CargoConcurso cargo : selecionado.getConcurso().getCargos()) {
                    if (cargo.temClassificacao()) {
                        toReturn.add(new SelectItem(cargo, cargo.toString()));
                    }
                }
            }
            if (toReturn.size() == 1) {
                toReturn.get(0).setLabel("Faça a classificação dos cargos.");
            }
        }
        return toReturn;
    }

    public List<SelectItem> getFases() {
        List<SelectItem> toReturn = getNewArrayListSelectItem();
        toReturn.add(new SelectItem(null, "Selecione uma Fase..."));
        if (selecionado.temConcurso()) {
            if (selecionado.getConcurso().getFases() != null && !selecionado.getConcurso().getFases().isEmpty()) {
                Util.ordenarListas(selecionado.getConcurso().getFases());
                for (FaseConcurso fase : selecionado.getConcurso().getFases()) {
                    toReturn.add(new SelectItem(fase, fase.toString()));
                }
            }
        }
        return toReturn;
    }

    public List<SelectItem> getProvas() {
        List<SelectItem> toReturn = getNewArrayListSelectItem();
        toReturn.add(new SelectItem(null, "Selecione uma Prova..."));
        if (selecionado.temFase()) {
            setarFaseDoRecursoComFaseDoConcursoCarregadaAsListas();
            for (ProvaConcurso prova : selecionado.getFaseConcurso().getProvas()) {
                if (prova.getCargoConcurso().equals(selecionado.getCargoConcurso())) {
                    toReturn.add(new SelectItem(prova, prova.toString()));
                }
            }
        }
        return toReturn;
    }

    public void setarFaseDoRecursoComFaseDoConcursoCarregadaAsListas() {
        for (FaseConcurso fase : selecionado.getConcurso().getFases()) {
            if (fase.equals(selecionado.getFaseConcurso())) {
                selecionado.setFaseConcurso(fase);
            }
        }
    }

    public List<SelectItem> getNewArrayListSelectItem() {
        List<SelectItem> toReturn = new ArrayList<>();
        return toReturn;
    }

    public ConverterAutoComplete getConverterCandidato() {
        if (converterCandidato == null) {
            converterCandidato = new ConverterAutoComplete(InscricaoConcurso.class, recursoConcursoFacade);
        }
        return converterCandidato;
    }

    public ConverterAutoComplete getConverterCargo() {
        if (converterCargo == null) {
            converterCargo = new ConverterAutoComplete(CargoConcurso.class, recursoConcursoFacade);
        }
        return converterCargo;
    }

    public ConverterAutoComplete getConverterFase() {
        if (converterFase == null) {
            converterFase = new ConverterAutoComplete(FaseConcurso.class, recursoConcursoFacade);
        }
        return converterFase;
    }

    public ConverterAutoComplete getConverterProva() {
        if (converterProva == null) {
            converterProva = new ConverterAutoComplete(ProvaConcurso.class, recursoConcursoFacade);
        }
        return converterProva;
    }

    public void carregarListasConcurso() {
        if (concursoSelecionado != null) {
            selecionado.setConcurso(recursoConcursoFacade.getConcursoFacade().recuperarConcursoParaTelaDeRecurso(concursoSelecionado.getId()));
        } else {
            selecionado.cancelarConcurso();
            selecionado.cancelarCandidato();
            selecionado.cancelarCargo();
            selecionado.cancelarFase();
            selecionado.cancelarProva();
        }
    }

    public void verificarDisponibilidadeDoCargoParaRecurso() {
        if (selecionado.temCargo()) {
            try {
                recursoConcursoFacade.getClassificacaoConcursoFacade().validarProvasCadastradasAndAvaliadas(selecionado.getCargoConcurso());
            } catch (ExcecaoNegocioGenerica eng) {
                FacesUtil.addOperacaoNaoPermitida(eng.getMessage());
                cancelarCargoAndProva();
            }
        } else {
            cancelarProva();
        }
    }

    public void verificarDisponibilidadeDaProvaParaRecurso() {
        RecursoConcurso recurso = recursoConcursoFacade.buscarUltimoRecursoPorProva(selecionado.getProvaConcurso());
        if (recurso != null && recurso.isSituacaoEmAndamento()) {
            FacesUtil.addOperacaoNaoPermitida("Só é permitdo entrar com recurso para provas que não estejam em recurso com situação em andamento. Foi encontrado um recurso para está prova. Recurso: " + recurso.toString());
            cancelarProva();
        }
    }

    private void cancelarCargoAndProva() {
        cancelarCargo();
        cancelarProva();
    }

    private void cancelarProva() {
        selecionado.setProvaConcurso(null);
    }

    public void cancelarCargo() {
        selecionado.setCargoConcurso(null);
    }

    public void handleFileUpload(FileUploadEvent event) {
        try {
            Arquivo a = new Arquivo();
            a.setMimeType(event.getFile().getContentType());
            a.setNome(event.getFile().getFileName());
            a.setDescricao(event.getFile().getFileName());
            a.setTamanho(event.getFile().getSize());
            a.setInputStream(event.getFile().getInputstream());
            a = recursoConcursoFacade.getArquivoFacade().novoArquivoMemoria(a);
            selecionado.setArquivo(a);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public StreamedContent recuperarArquivoParaDownload(Arquivo arq) {
        if (arq.getId() != null) {
            arq = recursoConcursoFacade.getArquivoFacade().recuperaDependencias(arq.getId());
        }
        StreamedContent s = null;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        for (ArquivoParte a : arq.getPartes()) {
            try {
                buffer.write(a.getDados());
            } catch (IOException ex) {
                logger.error("Erro: ", ex);
            }
        }
        InputStream is = new ByteArrayInputStream(buffer.toByteArray());
        s = new DefaultStreamedContent(is, arq.getMimeType(), arq.getNome());
        return s;
    }
}
