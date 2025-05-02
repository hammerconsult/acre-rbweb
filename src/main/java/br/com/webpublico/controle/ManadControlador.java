package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.manad.Manad;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.negocios.manad.ManadFacade;
import br.com.webpublico.util.*;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import java.io.*;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Roma
 * Date: 09/06/14
 * Time: 18:52
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
@URLMapping(id = "novo-manad", pattern = "/manad/", viewId = "/faces/financeiro/manad/edita.xhtml")
public class ManadControlador extends AbstractReport implements Serializable {

    @EJB
    private ManadFacade manadFacade;
    private Manad manad;
    private StreamedContent fileDownload;
    private File arquivo;
    private ConverterAutoComplete pessoaConverter, desenvolvedorConverter, entidadeConverter;
    private ConverterExercicio exercicioConverter;
    //Variaveis auxiliares
    private Pessoa contador;
    private Pessoa desenvolvedor;


    public String caminhoPadrao() {
        return "/manad/";
    }

    @URLAction(mappingId = "novo-manad", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        SistemaControlador sistemaControlador = getSistemaControlador();
        manad = new Manad();
        manad.setExercicioFinal(sistemaControlador.getExercicioCorrente());
        manad.setExercicioInicial(sistemaControlador.getExercicioCorrente());
    }

    private SistemaControlador getSistemaControlador() {
        return (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }


    //GETTER E SETTERS

    public Manad getManad() {
        return manad;
    }

    public void setManad(Manad manad) {
        this.manad = manad;
    }

    public StreamedContent getFileDownload() {
        return fileDownload;
    }

    public void setFileDownload(StreamedContent fileDownload) {
        this.fileDownload = fileDownload;
    }

    public File getArquivo() {
        return arquivo;
    }

    public void setArquivo(File arquivo) {
        this.arquivo = arquivo;
    }

    public Pessoa getContador() {
        return contador;
    }

    public void setContador(Pessoa contador) {
        this.contador = contador;
    }

    public Pessoa getDesenvolvedor() {
        return desenvolvedor;
    }

    public void setDesenvolvedor(Pessoa desenvolvedor) {
        this.desenvolvedor = desenvolvedor;
    }

    //CONVERTES
    public Converter getPessoaConverter() {
        if (pessoaConverter == null) {
            pessoaConverter = new ConverterAutoComplete(Pessoa.class, manadFacade.getPessoaFacade());
        }
        return pessoaConverter;
    }

    public Converter getDesenvolvedorConverter() {
        if (desenvolvedorConverter == null) {
            desenvolvedorConverter = new ConverterAutoComplete(Pessoa.class, manadFacade.getPessoaFacade());
        }
        return desenvolvedorConverter;
    }

    public Converter getEntidadeConverter() {
        if (entidadeConverter == null) {
            entidadeConverter = new ConverterAutoComplete(Entidade.class, manadFacade.getEntidadeFacade());
        }
        return entidadeConverter;
    }

    public ConverterExercicio getExercicioConverter() {
        if (exercicioConverter == null) {
            exercicioConverter = new ConverterExercicio(manadFacade.getExercicioFacade());
        }
        return exercicioConverter;
    }

    //AUTO COMPLETES
    public List<Pessoa> completaPessoa(String parte) {
        return manadFacade.getPessoaFacade().listaTodasPessoasPorId(parte.trim());
    }

    public List<Entidade> completaEntidade(String parte) {
        return manadFacade.getEntidadeFacade().listaEntidades(parte.trim());
    }

    //METODOS UTILIZADOS PELA VIEW
    public void adicionarContador() {
        if (contador == null) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "Selecione um Contador para adicionar na lista.");
            return;
        }
        manad.setContadores(Util.adicionarObjetoEmLista(manad.getContadores(), contador));
        contador = null;
    }

    public void adicionarDesenvolvedor() {
        if (desenvolvedor == null) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "Selecione um Desenvolvedor para adicionar na lista.");
            return;
        }
        manad.setDesenvolvedores(Util.adicionarObjetoEmLista(manad.getDesenvolvedores(), desenvolvedor));
        desenvolvedor = null;
    }

    public void removerContador(Pessoa contador) {
        manad.getContadores().remove(contador);
    }

    public void removerDesenvolvedor(Pessoa desenvolvedor) {
        manad.getDesenvolvedores().remove(desenvolvedor);
    }

    public void gerarArquivo() {
        try {
            manad = manadFacade.gerarArquivo(manad);
        } catch (Exception e) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), e.getMessage());
        }
    }

    public StreamedContent fazerDownload() throws FileNotFoundException, IOException {
        arquivo = new File("MANAD.txt");
        FileOutputStream fos = new FileOutputStream(arquivo);
        fos.write(manad.getConteudo().getBytes("UTF8"));
        fos.close();

        InputStream stream = new FileInputStream(arquivo);
        fileDownload = new DefaultStreamedContent(stream, "text/plain", "MANAD.txt");
        arquivo = null;
        return fileDownload;
    }

}
