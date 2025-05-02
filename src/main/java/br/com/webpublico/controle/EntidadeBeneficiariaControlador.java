/*
 * Codigo gerado automaticamente em Mon Apr 09 15:09:47 BRT 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.EntidadeBeneficiariaFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.FileUploadEvent;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.validator.ValidatorException;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@ManagedBean(name = "entidadeBeneficiariaControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-entidade-beneficiaria", pattern = "/entidade-beneficiaria/novo/", viewId = "/faces/financeiro/convenios/despesa/entidadebeneficiario/edita.xhtml"),
    @URLMapping(id = "editar-entidade-beneficiaria", pattern = "/entidade-beneficiaria/editar/#{entidadeBeneficiariaControlador.id}/", viewId = "/faces/financeiro/convenios/despesa/entidadebeneficiario/edita.xhtml"),
    @URLMapping(id = "ver-entidade-beneficiaria", pattern = "/entidade-beneficiaria/ver/#{entidadeBeneficiariaControlador.id}/", viewId = "/faces/financeiro/convenios/despesa/entidadebeneficiario/visualizar.xhtml"),
    @URLMapping(id = "listar-entidade-beneficiaria", pattern = "/entidade-beneficiaria/listar/", viewId = "/faces/financeiro/convenios/despesa/entidadebeneficiario/lista.xhtml")
})
public class EntidadeBeneficiariaControlador extends PrettyControlador<EntidadeBeneficiaria> implements Serializable, CRUD {

    @EJB
    private EntidadeBeneficiariaFacade entidadeBeneficiariaFacade;
    private DoctoHabilitacao doctoHabilitacaoSelecionado;
    private Arquivo arquivo;
    private ConverterAutoComplete converterPessoa;
    private ConverterAutoComplete converteDoctos;
    private EntidadeBeneficCertidoes entidadeBeneficCertidoes;
    private String conteudoDaDeclaracao;

    public EntidadeBeneficiariaControlador() {
        super(EntidadeBeneficiaria.class);
    }

    public EntidadeBeneficiariaFacade getFacade() {
        return entidadeBeneficiariaFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return entidadeBeneficiariaFacade;
    }

    private SistemaControlador getSistemaControlador() {
        return (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }

    @URLAction(mappingId = "novo-entidade-beneficiaria", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setExercicio(getSistemaControlador().getExercicioCorrente());
        selecionado.setDataCadastro(getSistemaControlador().getDataOperacao());
        selecionado.setInicioVigencia(getSistemaControlador().getDataOperacao());
        entidadeBeneficCertidoes = new EntidadeBeneficCertidoes();
    }

    @URLAction(mappingId = "ver-entidade-beneficiaria", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editar-entidade-beneficiaria", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperarEditarVer();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/entidade-beneficiaria/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public void redirecionarParaLista() {
        redireciona();
        FacesUtil.addInfo(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), " Registro salvo com sucesso.");
    }

    @Override
    public void salvar() {
        if (Util.validaCampos(selecionado)) {
            try {
                if (operacao.equals(Operacoes.NOVO)) {
                    entidadeBeneficiariaFacade.salvarNovo(selecionado);
                    FacesUtil.addInfo(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), " Registro salvo com sucesso.");
                    FacesUtil.executaJavaScript("dialogDeclaracaoCadastro.show()");
                } else {
                    entidadeBeneficiariaFacade.salvar(selecionado);
                    FacesUtil.addInfo(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), " Registro alterado com sucesso.");
                    redireciona();
                }
            } catch (Exception e) {
                FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), e.getMessage());
            }
        }
    }

    public void apagaArquivo() {
        arquivo = null;
    }

    public void recuperarEditarVer() {
        entidadeBeneficCertidoes = new EntidadeBeneficCertidoes();
        selecionado = entidadeBeneficiariaFacade.recuperar(super.getId());
    }

    public void validaInicioVigencia(FacesContext context, UIComponent component, Object value) {
        FacesMessage message = new FacesMessage();
        Date data = (Date) value;
        if (validaAnoExercicio(data)) {
            message.setDetail("Ano diferente do exercício corrente.");
            message.setSummary("Operação não Permitida!");
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(message);
        }
    }

    public void validaFimVigencia(FacesContext context, UIComponent component, Object value) {
        FacesMessage message = new FacesMessage();
        Date data = (Date) value;
        if (validaAnoExercicio(data)) {
            message.setDetail("Ano diferente do exercício corrente.");
            message.setSummary("Operação não Permitida!");
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(message);
        }
        Date inicio = ((EntidadeBeneficiaria) selecionado).getInicioVigencia();
        if (inicio != null) {
            if (data.before(inicio)) {
                message.setDetail("Final de Vigência deve ser maior que o Início de Vigência.");
                message.setSummary("Operação não Permitida!");
                message.setSeverity(FacesMessage.SEVERITY_ERROR);
                throw new ValidatorException(message);
            }
        }
    }

    private Boolean validaAnoExercicio(Date data) {
        Calendar dt = Calendar.getInstance();
        dt.setTime(data);
        Integer ano = getSistemaControlador().getExercicioCorrente().getAno();
        if (dt.get(Calendar.YEAR) != ano) {
            return true;
        } else {
            return false;
        }
    }

    public void imprimirDeclaracao() {
        Util.geraPDF("Declaração de Cadastro para Entidade Beneficiária", conteudoDaDeclaracao, FacesContext.getCurrentInstance());
    }

    public void prepararDeclaracaoParaImprimir() {
        try {
            conteudoDaDeclaracao = entidadeBeneficiariaFacade.montarConteudoDeclaracaoCadastro(selecionado);
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada("Erro ao gerar a declaração de cadastrado. Detalhes do erro: " + ex.getMessage());
        }
    }

    public String converterBytesParaMegaBytes(Long tamanhoEmBytes) {
        return String.format("%1$,.2f", ((double) (tamanhoEmBytes / 1024) / 1024));
    }

    public void redirecionarVerArquivo(EntidadeBeneficCertidoes arquivo) {
        FacesUtil.redirecionamentoInterno("/arquivos/" + arquivo.getArquivo().getNome() + "?id=" + arquivo.getArquivo().getId());
    }

    public void uploadArquivo(FileUploadEvent evento) {
        try {
            Arquivo arq = new Arquivo();
            arq.setNome(evento.getFile().getFileName());
            arq.setMimeType(entidadeBeneficiariaFacade.getArquivoFacade().getMimeType(evento.getFile().getFileName()));
            arq.setDescricao(getSistemaControlador().getDataOperacao().toString());
            arq.setTamanho(evento.getFile().getSize());

            entidadeBeneficCertidoes.setArquivo(entidadeBeneficiariaFacade.getArquivoFacade().novoArquivoMemoria(arq, evento.getFile().getInputstream()));
            arquivo = arq;
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada("Erro ao adicionar arquivo. Detalhes do erro: " + e.getMessage());
        }
    }

    public void adicionarDocumentos() {
        try {
            if (doctoHabilitacaoSelecionado == null) {
                FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " O campo Documento deve ser informado para adicionar.");
            } else if (selecionado.getEntidadeBeneficCertidoess().contains(entidadeBeneficCertidoes)) {
                FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " O documento " + doctoHabilitacaoSelecionado + " já foi adicionado na lista.");
            } else {
                if (podeAdicionarDocumento(doctoHabilitacaoSelecionado)) {
                    entidadeBeneficCertidoes.setDoctoHabilitacao(doctoHabilitacaoSelecionado);
                    entidadeBeneficCertidoes.setEntidadeBeneficiaria(selecionado);
                    entidadeBeneficCertidoes.setArquivo(arquivo);
                    selecionado.getEntidadeBeneficCertidoess().add(entidadeBeneficCertidoes);
                    entidadeBeneficCertidoes = new EntidadeBeneficCertidoes();
                    doctoHabilitacaoSelecionado = null;
                    arquivo = null;
                } else {
                    FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "O documento " + doctoHabilitacaoSelecionado + " já foi adicionado na lista");
                }
            }
        } catch (Exception e) {
            FacesUtil.addOperacaoRealizada("Documento não adicionado, detalhes do erro: " + e.getMessage());
        }
    }

    private boolean podeAdicionarDocumento(DoctoHabilitacao doctoHabilitacaoSelecionado) {
        for (EntidadeBeneficCertidoes entidadeBeneficCertidoes : selecionado.getEntidadeBeneficCertidoess()) {
            if (entidadeBeneficCertidoes.getDoctoHabilitacao().equals(doctoHabilitacaoSelecionado)) {
                return false;
            }
        }
        return true;
    }


    public void removeDoumentos(ActionEvent evento) {
        EntidadeBeneficCertidoes d = (EntidadeBeneficCertidoes) evento.getComponent().getAttributes().get("obj");
        selecionado.getEntidadeBeneficCertidoess().remove(d);

    }

    public List<Pessoa> completaPessoaJuridica(String parte) {
        return entidadeBeneficiariaFacade.getPessoaFacade().listaPessoasJuridicas(parte.trim());
    }

    public List<Pessoa> completaPessoaFisica(String parte) {
        return entidadeBeneficiariaFacade.getPessoaFacade().listaPessoasFisicas(parte.trim());
    }

    public List<DoctoHabilitacao> completaDocumentos(String parte) {
        return entidadeBeneficiariaFacade.getDoctoHabilitacaoFacade().listaFiltrando(parte.trim(), "descricao");
    }

    public ConverterAutoComplete getConverterPessoa() {
        if (converterPessoa == null) {
            converterPessoa = new ConverterAutoComplete(Pessoa.class, entidadeBeneficiariaFacade.getPessoaFacade());
        }
        return converterPessoa;
    }

    public ConverterAutoComplete getConverteDoctos() {
        if (converteDoctos == null) {
            converteDoctos = new ConverterAutoComplete(DoctoHabilitacao.class, entidadeBeneficiariaFacade.getDoctoHabilitacaoFacade());
        }
        return converteDoctos;
    }

    public DoctoHabilitacao getDoctoHabilitacaoSelecionado() {
        return doctoHabilitacaoSelecionado;
    }

    public void setDoctoHabilitacaoSelecionado(DoctoHabilitacao doctoHabilitacaoSelecionado) {
        this.doctoHabilitacaoSelecionado = doctoHabilitacaoSelecionado;
    }

    public Arquivo getArquivo() {
        return arquivo;
    }

    public void setArquivo(Arquivo arquivo) {
        this.arquivo = arquivo;
    }

    public String getConteudoDaDeclaracao() {
        return conteudoDaDeclaracao;
    }

    public void setConteudoDaDeclaracao(String conteudoDaDeclaracao) {
        this.conteudoDaDeclaracao = conteudoDaDeclaracao;
    }

    public EntidadeBeneficCertidoes getEntidadeBeneficCertidoes() {
        return entidadeBeneficCertidoes;
    }

    public void setEntidadeBeneficCertidoes(EntidadeBeneficCertidoes entidadeBeneficCertidoes) {
        this.entidadeBeneficCertidoes = entidadeBeneficCertidoes;
    }
}
