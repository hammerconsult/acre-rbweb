package br.com.webpublico.controle.rh.concursos;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.Arquivo;
import br.com.webpublico.entidades.CEPLogradouro;
import br.com.webpublico.entidades.EnderecoCorreio;
import br.com.webpublico.entidades.rh.concursos.*;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.rh.concursos.MetodoAvaliacao;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.interfaces.ValidadorEntidade;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ArquivoFacade;
import br.com.webpublico.negocios.rh.concursos.CargoConcursoFacade;
import br.com.webpublico.negocios.rh.concursos.ConcursoFacade;
import br.com.webpublico.negocios.rh.concursos.FaseConcursoFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.StreamedContent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "faseConcursoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-fase-concurso", pattern = "/concursos/fases/novo/", viewId = "/faces/rh/concursos/fases/edita.xhtml"),
    @URLMapping(id = "editar-fase-concurso", pattern = "/concursos/fases/editar/#{faseConcursoControlador.id}/", viewId = "/faces/rh/concursos/fases/edita.xhtml"),
    @URLMapping(id = "ver-fase-concurso", pattern = "/concursos/fases/ver/#{faseConcursoControlador.id}/", viewId = "/faces/rh/concursos/fases/edita.xhtml"),
    @URLMapping(id = "listar-fase-concurso", pattern = "/concursos/fases/listar/", viewId = "/faces/rh/concursos/fases/lista.xhtml")
})
public class FaseConcursoControlador extends PrettyControlador<Concurso> implements Serializable, CRUD {

    @EJB
    private FaseConcursoFacade faseConcursoFacade;
    @EJB
    private ConcursoFacade concursoFacade;
    @EJB
    private CargoConcursoFacade cargoConcursoFacade;
    private FaseConcurso faseSelecionada;
    private ProvaConcurso provaSelecionada;
    @EJB
    private ArquivoFacade arquivoFacade;
    private List<AnexoFase> anexosFase;

    public FaseConcursoControlador() {
        super(Concurso.class);
    }

    public ProvaConcurso getProvaSelecionada() {
        return provaSelecionada;
    }

    public void setProvaSelecionada(ProvaConcurso provaSelecionada) {
        this.provaSelecionada = provaSelecionada;
    }

    @Override
    public AbstractFacade getFacede() {
        return faseConcursoFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/concursos/fases/";
    }

    public List<AnexoFase> getAnexosFase() {
        return anexosFase;
    }

    public void setAnexosFase(List<AnexoFase> anexosFase) {
        this.anexosFase = anexosFase;
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novo-fase-concurso", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado = null;
        anexosFase = Lists.newLinkedList();
    }

    @URLAction(mappingId = "ver-fase-concurso", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        setOperacao(Operacoes.VER);
        carregarParametrosIniciais();
    }

    @URLAction(mappingId = "editar-fase-concurso", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        setOperacao(Operacoes.EDITAR);
        carregarParametrosIniciais();
    }


    private void carregarParametrosIniciais() {
        FaseConcurso fase = faseConcursoFacade.recuperar(getId());
        selecionado = concursoFacade.recuperarConcursosParaTelaDeFases(fase.getConcurso().getId());
    }

    public ConverterAutoComplete getConverterCargoConcurso() {
        return new ConverterAutoComplete(CargoConcurso.class, cargoConcursoFacade);
    }

    public Converter getConverterConcurso() {
        return new Converter() {
            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {

                try {
                    return concursoFacade.recuperarConcursosParaTelaDeFases(Long.parseLong(value));
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
        toReturn.add(new SelectItem(null, ""));
        for (Concurso c : concursoFacade.getUltimosConcursosComPublicacoes()) {
            toReturn.add(new SelectItem(c, "" + c));
        }
        return toReturn;
    }

    public FaseConcurso getFaseSelecionada() {
        return faseSelecionada;
    }

    public void setFaseSelecionada(FaseConcurso faseSelecionada) {
        this.faseSelecionada = faseSelecionada;
    }

    public void novaFase() {
        faseSelecionada = new FaseConcurso();
        faseSelecionada.setConcurso(selecionado);
        faseSelecionada.setOrdem(selecionado.getProximaOrdemDasFases());
    }

    public void confirmarFase() {
        if (!Util.validaCampos(faseSelecionada)) {
            return;
        }

        if (selecionado.jaExisteFaseComOrdem(faseSelecionada)) {
            FacesUtil.addOperacaoNaoPermitida("Já existe uma fase com a ordem informada (" + faseSelecionada.getOrdem() + "), por favor informe uma ordem diferente.");
            return;
        }

        selecionado.setFases(Util.adicionarObjetoEmLista(selecionado.getFases(), faseSelecionada));
        cancelarFase();
    }

    public void cancelarFase() {
        faseSelecionada = null;
        anexosFase = Lists.newLinkedList();
    }

    public void removerFase(FaseConcurso fase) {
        selecionado.getFases().remove(fase);
    }

    public void selecionarFase(FaseConcurso fase) {
        faseSelecionada = fase;
        if(fase.getAnexos() != null && !fase.getAnexos().isEmpty()){
            anexosFase = Lists.newLinkedList(fase.getAnexos());
        }
    }

    public void novaProva(FaseConcurso faseConcurso) {
        provaSelecionada = new ProvaConcurso();
        provaSelecionada.setFaseConcurso(faseConcurso);
        selecionarFase(faseConcurso);
        provaSelecionada.setEnderecoCorreio(new EnderecoCorreio());
    }


    @Override
    public void salvar() {
        try {
            faseConcursoFacade.salvarConcurso(selecionado);
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
            return;
        } catch (Exception e) {
            descobrirETratarException(e);

        }
        redireciona();
    }

    public List<SelectItem> getMetodosDeAvaliacao() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (MetodoAvaliacao ma : MetodoAvaliacao.values()) {
            toReturn.add(new SelectItem(ma, ma.getDescricao()));
        }
        return toReturn;
    }

    private boolean validarConfirmacao(ValidadorEntidade obj) {
        if (!Util.validaCampos(obj)) {
            return false;
        }

        try {
            obj.validarConfirmacao();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
            return false;
        }
        return true;
    }

    public List<String> completaCep(String parte) {
        return concursoFacade.getConsultaCepFacade().consultaLogradouroPorParteCEPByString(parte.trim());
    }

    public void atualizaLogradouros() {
        if (provaSelecionada.getEnderecoCorreio().getCep() == null || provaSelecionada.getEnderecoCorreio().getCep().isEmpty()) {
            return;
        }

        provaSelecionada.getEnderecoCorreio().setCep(provaSelecionada.getEnderecoCorreio().getCep().replace("-", ""));

        List<CEPLogradouro> logradouros = concursoFacade.getConsultaCepFacade().consultaLogradouroPorCEP(provaSelecionada.getEnderecoCorreio().getCep());
        if (logradouros == null || logradouros.isEmpty()) {
            String cepAntigo = provaSelecionada.getEnderecoCorreio().getCep();
            provaSelecionada.setEnderecoCorreio(new EnderecoCorreio());
            provaSelecionada.getEnderecoCorreio().setCep(cepAntigo);
            return;
        }

        CEPLogradouro logradouro = logradouros.get(0);
        provaSelecionada.getEnderecoCorreio().setBairro(logradouro.getBairro().getNome());
        provaSelecionada.getEnderecoCorreio().setCep(logradouro.getCep());
        provaSelecionada.getEnderecoCorreio().setLogradouro(logradouro.getNome());
        provaSelecionada.getEnderecoCorreio().setUf(logradouro.getLocalidade().getCepuf().getSigla());
        provaSelecionada.getEnderecoCorreio().setLocalidade(logradouro.getLocalidade().getNome());
    }

    public boolean validarEnderecoProva() {
        boolean retorno = true;
        if (provaSelecionada.getEnderecoCorreio().getCep() == null || provaSelecionada.getEnderecoCorreio().getCep().isEmpty()) {
            FacesUtil.addCampoObrigatorio("O campo CEP deve ser informado.");
            retorno = false;
        }

        if (provaSelecionada.getEnderecoCorreio().getLocalidade() == null || provaSelecionada.getEnderecoCorreio().getLocalidade().isEmpty()) {
            FacesUtil.addCampoObrigatorio("O campo Cidade deve ser informado.");
            retorno = false;
        }

        if (!validarConfirmacao(provaSelecionada.getEnderecoCorreio())) {
            retorno = false;
        }

        return retorno;
    }

    public boolean validarConfirmacaoProva() {
        boolean retorno = true;

        if (!validarConfirmacao(provaSelecionada)) {
            retorno = false;
        }

        if (!validarEnderecoProva()) {
            retorno = false;
        }

        if (!isNotaMinimaValida()) {
            retorno = false;
        }

        return retorno;
    }

    private boolean isNotaMinimaValida() {
        if (provaSelecionada.getNotaMinima() == null || provaSelecionada.getNotaMinima().toString().trim().isEmpty()) {
            FacesUtil.addOperacaoNaoPermitida("O campo nota mínima deve ser informado.");
            return false;
        }

        if (provaSelecionada.getNotaMinima().compareTo(BigDecimal.ZERO) < 0 || provaSelecionada.getNotaMinima().compareTo(BigDecimal.TEN) > 0) {
            FacesUtil.addOperacaoNaoPermitida("A nota mínima da prova está fora dos parâmetros permitidos. Por favor, informe um valor entre 0(zero) e 10(dez).");
            return false;
        }

        return true;
    }

    public void confirmarProva() {
        if (!validarConfirmacaoProva()) {
            return;
        }

        faseSelecionada.setProvas(Util.adicionarObjetoEmLista(faseSelecionada.getProvas(), provaSelecionada));
        selecionado.setFases(Util.adicionarObjetoEmLista(selecionado.getFases(), faseSelecionada));
        FacesUtil.executaJavaScript("dialogNovaProva.hide()");
    }

    public void confirmarAnexos(){
        faseSelecionada.setAnexos(anexosFase);
    }

    public void selecionarProva(ProvaConcurso p) {
        provaSelecionada = p;
    }

    public void cancelarProva() {
        provaSelecionada = null;
    }

    public void removerProva(ProvaConcurso prova) {
        faseSelecionada.getProvas().remove(prova);
    }

    public void atribuirNotaMinimaPeloTipoDeAvaliacaoDaProva(){
        if (provaSelecionada.getMetodoAvaliacao().equals(MetodoAvaliacao.POR_OBJETIVO)){
            provaSelecionada.setNotaMinima(BigDecimal.TEN);
        }

        if (provaSelecionada.getMetodoAvaliacao().equals(MetodoAvaliacao.POR_NOTA)){
            provaSelecionada.setNotaMinima(BigDecimal.ZERO);
        }
    }

    public String getValueColunaAvaliada(ProvaConcurso prova) {
        if (prova.temAvaliacao()) {
            return "Sim";
        }
        return "Não";
    }


    public void enviarArquivo(FileUploadEvent event) {
        try {
            Arquivo a = new Arquivo();
            a.setMimeType(event.getFile().getContentType());
            a.setNome(event.getFile().getFileName());
            a.setDescricao(event.getFile().getFileName());
            a.setTamanho(event.getFile().getSize());
            a.setInputStream(event.getFile().getInputstream());
            logger.debug("nome do file {} {}", event.getFile().getFileName(), event.getFile().getContentType());
            a = arquivoFacade.novoArquivoMemoria(a);
            AnexoFase anexoFase = new AnexoFase(faseSelecionada, a);
            adicionarAnexoNaFase(faseSelecionada, anexoFase);

            //selecionado.setArquivo(a);
        } catch (Exception ex) {
            logger.error("Erro enviarArquivo: ", ex);
        }
    }

    public AnexoFase adicionarAnexoNaFase(FaseConcurso faseSelecionada, AnexoFase anexo) {
        anexosFase.add(anexo);
        return anexo;
    }

    public AnexoFase removerAnexoFase(AnexoFase anexo) {
        if(anexosFase.contains(anexo)) {
            anexosFase.remove(anexo);
        }
        return anexo;
    }

    public StreamedContent baixarAnexo(Arquivo arquivo) {
        logger.debug("arquivo {}", arquivo);
        try {
            if(arquivo != null) {
                return arquivoFacade.montarArquivoParaDownloadPorArquivo(arquivo);
            }
        } catch (Exception e) {
            logger.error("Erro ao recuperar arquivo ", e);
            FacesUtil.addOperacaoNaoRealizada("Não foi possível baixar o Edital em anexo. Detlhes: "+ e.getMessage());
        }
        return null;
    }
}
