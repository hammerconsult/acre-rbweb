package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TipoCadastroDoctoOficial;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.CorrespondenciaFacade;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Renato
 */


///tributario/dividaativa/notificacao/controledear.xhtml


@ManagedBean(name = "correspondenciaControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoControleAR", pattern = "/tributario/controledear/", viewId = "/faces/tributario/dividaativa/notificacao/controledear.xhtml"),
})

public class CorrespondenciaControlador extends SuperControladorCRUD implements Serializable {

    @EJB
    private CorrespondenciaFacade correspondenciaFacade;
    private List<Correspondencia> listaDeCorrespondenciaPendentes;
    private List<Correspondencia> listaDeCorrespondenciaAguardandoRecebimento;
    private List<Correspondencia> listaDeCorrespondenciaRecebidos;
    private List<Correspondencia> listaDeCorrespondenciaNaoRecebidos;
    private List<Correspondencia> listaDeTodasCorrespondencias;
    private Correspondencia correspondenciaNaoEntregue;

    //INICIO MÉTODOS GETTERS E SETTERS
    @Override
    public AbstractFacade getFacede() {
        return correspondenciaFacade;
    }

    public List<Correspondencia> getListaDeCorrespondenciaPendentes() {
        return listaDeCorrespondenciaPendentes;
    }

    public void setListaDeCorrespondenciaPendentes(List<Correspondencia> listaDeCorrespondenciaPendentes) {
        this.listaDeCorrespondenciaPendentes = listaDeCorrespondenciaPendentes;
    }

    public List<Correspondencia> getListaDeCorrespondenciaAguardandoRecebimento() {
        return listaDeCorrespondenciaAguardandoRecebimento;
    }

    public void setListaDeCorrespondenciaAguardandoRecebimento(List<Correspondencia> listaDeCorrespondenciaAguardandoRecebimento) {
        this.listaDeCorrespondenciaAguardandoRecebimento = listaDeCorrespondenciaAguardandoRecebimento;
    }

    public List<Correspondencia> getListaDeCorrespondenciaRecebidos() {
        return listaDeCorrespondenciaRecebidos;
    }

    public void setListaDeCorrespondenciaRecebidos(List<Correspondencia> listaDeCorrespondenciaRecebidos) {
        this.listaDeCorrespondenciaRecebidos = listaDeCorrespondenciaRecebidos;
    }

    public List<Correspondencia> getListaDeTodasCorrespondencias() {
        return listaDeTodasCorrespondencias;
    }

    public void setListaDeTodasCorrespondencias(List<Correspondencia> listaDeTodasCorrespondencias) {
        this.listaDeTodasCorrespondencias = listaDeTodasCorrespondencias;
    }

    public List<Correspondencia> getListaDeCorrespondenciaNaoRecebidos() {
        return listaDeCorrespondenciaNaoRecebidos;
    }

    public void setListaDeCorrespondenciaNaoRecebidos(List<Correspondencia> listaDeCorrespondenciaNaoRecebidos) {
        this.listaDeCorrespondenciaNaoRecebidos = listaDeCorrespondenciaNaoRecebidos;
    }

    public Correspondencia getCorrespondenciaNaoEntregue() {
        return correspondenciaNaoEntregue;
    }

    public void setCorrespondenciaNaoEntregue(Correspondencia correspondenciaNaoEntregue) {
        this.correspondenciaNaoEntregue = correspondenciaNaoEntregue;
    }

    //FIM MÉTODOS GETTERS E SETTERS
    //INICIO MÉTODOS SOBRESCRITOS

    //    novoControleAR
    @URLAction(mappingId = "novoControleAR", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        inicializaListasDeCorrespondencia();
        recuperaTodasCorrespondencia();

    }

    @Override
    public String salvar() {
//        return super.salvarListaDeCorrespondenciaPendentes();
        correspondenciaFacade.salvarListaDeCorrespondenciaPendentes(listaDeCorrespondenciaPendentes);
        novo();
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correspondência(s) salva(s) com sucesso!", "A(s) correspondência(s) que possui(em) data de emissão foram salvas."));
        return "controledear.xhtml";
    }

    //FIM MÉTODOS SOBRESCRITOS
    //INICIO MÉTODOS DE RECUPERAÇÃO E INICIALIZAÇÃO
    public void recuperaListaDeDocumentosPendentes() {
        inicializaListaCorrespondenciaPendentes();
        List<DocumentoOficial> listaDeDocumentosPendentes = correspondenciaFacade.recuperaDocumentosOficialPendentes();
        for (DocumentoOficial documentoOficial : listaDeDocumentosPendentes) {
            if (!correspondenciaFacade.documentoEstaEmOutraCorrespondencia(documentoOficial)) {
                Correspondencia correspondencia = criaNovaCorrespondencia(documentoOficial);
                listaDeCorrespondenciaPendentes.add(correspondencia);
            }
        }
    }

    public void recuperaTodasCorrespondencia() {
        listaDeTodasCorrespondencias = correspondenciaFacade.recuperaTodasCorrespondencia();
        for (Correspondencia correspondencia : listaDeTodasCorrespondencias) {
            if (correspondencia.getEmissao() != null && correspondencia.getRecebimento() == null && !correspondencia.isEntregue()) {
                listaDeCorrespondenciaAguardandoRecebimento.add(correspondencia);
            }
            if (correspondencia.isEntregue()) {
                listaDeCorrespondenciaRecebidos.add(correspondencia);
            }
            if (!correspondencia.isEntregue() && correspondencia.getMotivo() != null) {
                listaDeCorrespondenciaNaoRecebidos.add(correspondencia);
            }
        }
    }

    public Pessoa recuperaDestinatarioDoDocumento(DocumentoOficial documentoOficial) {
        SolicitacaoDoctoOficial solicitacaoDoctoOficial = correspondenciaFacade.recuperaSolicitacaoDoDocumentoOficial(documentoOficial);
        if (documentoOficial.getModeloDoctoOficial().getTipoDoctoOficial().getTipoCadastroDoctoOficial().equals(TipoCadastroDoctoOficial.PESSOAFISICA)) {
            return correspondenciaFacade.getSolicitacaoDoctoOficialFacade().recuperarPessoaFisica(solicitacaoDoctoOficial);
        }
        if (documentoOficial.getModeloDoctoOficial().getTipoDoctoOficial().getTipoCadastroDoctoOficial().equals(TipoCadastroDoctoOficial.PESSOAJURIDICA)) {
            return correspondenciaFacade.getSolicitacaoDoctoOficialFacade().recuperarPessoaJuridica(solicitacaoDoctoOficial);
        }
        if (documentoOficial.getModeloDoctoOficial().getTipoDoctoOficial().getTipoCadastroDoctoOficial().equals(TipoCadastroDoctoOficial.CADASTROECONOMICO)) {
            return ((CadastroEconomico) correspondenciaFacade.getSolicitacaoDoctoOficialFacade().recuperarCadastroEconomico(solicitacaoDoctoOficial)).getPessoa();
        }
        if (documentoOficial.getModeloDoctoOficial().getTipoDoctoOficial().getTipoCadastroDoctoOficial().equals(TipoCadastroDoctoOficial.CADASTROIMOBILIARIO)) {
            CadastroImobiliario cadImobiliario = correspondenciaFacade.recuperarCadastroImobiliario(solicitacaoDoctoOficial);
            if (cadImobiliario.getPropriedade() != null && cadImobiliario.getPropriedade().get(0) != null) {
                return (Pessoa) cadImobiliario.getPropriedade().get(0).getPessoa();
            }
        }
        if (documentoOficial.getModeloDoctoOficial().getTipoDoctoOficial().getTipoCadastroDoctoOficial().equals(TipoCadastroDoctoOficial.CADASTRORURAL)) {
            CadastroRural cadRural = correspondenciaFacade.recuperarCadastroRural(solicitacaoDoctoOficial);
            if (cadRural.getPropriedade() != null && cadRural.getPropriedade().get(0) != null) {
                return (Pessoa) cadRural.getPropriedade().get(0).getPessoa();
            }
        }
        return null;
    }

    public String recuperaNomeDoDestinatário(DocumentoOficial documentoOficial) {
        return recuperaDestinatarioDoDocumento(documentoOficial).getNome();
    }

    public String recuperaCpfCnpjDoDestinatário(DocumentoOficial documentoOficial) {
        return recuperaDestinatarioDoDocumento(documentoOficial).getCpf_Cnpj();
    }

    public String recuperaRgDoDestinatário(DocumentoOficial documentoOficial) {
        Pessoa p = recuperaDestinatarioDoDocumento(documentoOficial);
        if (p instanceof PessoaFisica) {
            return correspondenciaFacade.recuperarRg(((PessoaFisica) p));
        }
        if (p instanceof PessoaJuridica) {
            return ((PessoaJuridica) p).getInscricaoEstadual();
        }
        return " - ";
    }

    private Correspondencia criaNovaCorrespondencia(DocumentoOficial documentoOficial) {
        Correspondencia toReturn = new Correspondencia();
        toReturn.setDocumentoOficial(documentoOficial);
        toReturn.setEmissao(new Date());
        return toReturn;
    }

    private void inicializaListasDeCorrespondencia() {
        inicializaListaCorrespondenciaAguardandoRecebimento();
        inicializaListaCorrespondenciaPendentes();
        inicializaListaCorrespondenciaRecebidos();
        inicializaListaCorrespondenciaNaoRecebidos();
        inicializaListaTodasCorrespondencia();
    }

    private void inicializaListaCorrespondenciaPendentes() {
        listaDeCorrespondenciaPendentes = new ArrayList<Correspondencia>();
    }

    private void inicializaListaCorrespondenciaAguardandoRecebimento() {
        listaDeCorrespondenciaAguardandoRecebimento = new ArrayList<Correspondencia>();
    }

    private void inicializaListaCorrespondenciaRecebidos() {
        listaDeCorrespondenciaRecebidos = new ArrayList<Correspondencia>();
    }

    private void inicializaListaCorrespondenciaNaoRecebidos() {
        listaDeCorrespondenciaNaoRecebidos = new ArrayList<Correspondencia>();
    }

    private void inicializaListaTodasCorrespondencia() {
        listaDeTodasCorrespondencias = new ArrayList<Correspondencia>();
    }

    public Date retornaDateAtual() {
        return new Date();
    }
    //FIM MÉTODOS DE RECUPERAÇÃO

    //INICIO MÉTODOS DE VALIDAÇÃO
    public boolean dataVencimentoMenorQueDataAtual(Correspondencia correspondencia) {
        if (correspondencia.getEmissao() == null) {
            return true;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date emissao = new Date(sdf.format(correspondencia.getEmissao()));
        Date hoje = new Date(sdf.format(new Date()));

        if (hoje.compareTo(emissao) < 0) {
            return true;
        }

        return false;
    }

    public void alteraDataDeHojeParaTodos() {
        for (Correspondencia correspondencia : listaDeCorrespondenciaPendentes) {
            correspondencia.setEmissao(new Date());
        }
    }

    public void receberCorrespondencia(Correspondencia correspondencia) {
        listaDeCorrespondenciaAguardandoRecebimento.remove(correspondencia);
        correspondencia.setEntregue(true);
        correspondencia.setRecebimento(new Date());
        correspondencia = correspondenciaFacade.salvarCorrespondencia(correspondencia);
        listaDeCorrespondenciaRecebidos.add(correspondencia);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correspondência(s) salva(s) com sucesso!", "A Correspondência entregue ao destinatário foi salva com sucesso."));
    }

    public void receberCorrespondenciaNaoEntregue() {
        listaDeCorrespondenciaAguardandoRecebimento.remove(this.correspondenciaNaoEntregue);
        this.correspondenciaNaoEntregue.setEntregue(false);
        this.correspondenciaNaoEntregue.setRecebimento(new Date());
        this.correspondenciaNaoEntregue = correspondenciaFacade.salvarCorrespondencia(this.correspondenciaNaoEntregue);
        listaDeCorrespondenciaNaoRecebidos.add(this.correspondenciaNaoEntregue);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correspondência(s) salva(s) com sucesso!", "A Correspondência que não foi entregue ao destinátario foi salva com sucesso."));
    }

    public void inicializaCorrespondenciaNaoEntregue(Correspondencia correspondencia) {
        correspondenciaNaoEntregue = correspondencia;
    }

    public String retornaData(Date data) {
        if (data == null) {
            return " - ";
        }
        return new SimpleDateFormat("dd/MM/yyyy").format(data);
    }

    public boolean habilitaBotaoReceberCorrespondencia(Correspondencia correspondencia) {
        if (correspondencia.getCodigo() == null || correspondencia.getCodigo().trim().isEmpty()) {
            return false;
        }
        return true;
    }
    //FIM MÉTODOS DE VALIDAÇÃO
}
