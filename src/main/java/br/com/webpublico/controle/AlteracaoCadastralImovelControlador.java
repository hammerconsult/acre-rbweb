package br.com.webpublico.controle;

import br.com.webpublico.entidades.AlteracaoCadastralImovel;
import br.com.webpublico.entidades.CadastroImobiliario;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.AlteracaoCadastralImovelFacade;
import br.com.webpublico.negocios.CadastroImobiliarioFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 05/06/14
 * Time: 09:09
 * To change this template use File | Settings | File Templates.
 */

@ManagedBean(name = "alteracaoCadastralImovelControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novaAlteracaoCadastralImovel", pattern = "/alteracaocadastralimovel/novo/", viewId = "/faces/tributario/cadastromunicipal/alteracaocadastralimovel/edita.xhtml"),
        @URLMapping(id = "editarAlteracaoCadastralImovel", pattern = "/alteracaocadastralimovel/editar/#{alteracaoCadastralImovelControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/alteracaocadastralimovel/edita.xhtml"),
        @URLMapping(id = "listarAlteracaoCadastralImovel", pattern = "/alteracaocadastralimovel/listar/", viewId = "/faces/tributario/cadastromunicipal/alteracaocadastralimovel/lista.xhtml"),
        @URLMapping(id = "verAlteracaoCadastralImovel", pattern = "/alteracaocadastralimovel/ver/#{alteracaoCadastralImovelControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/alteracaocadastralimovel/visualizar.xhtml")
})
public class AlteracaoCadastralImovelControlador extends PrettyControlador<AlteracaoCadastralImovel> implements Serializable, CRUD {

    @EJB
    private AlteracaoCadastralImovelFacade alteracaoCadastralImovelFacade;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    @EJB
    private CadastroImobiliarioFacade cadastroImobiliarioFacade;


    @Override
    public String getCaminhoPadrao() {
        return "/alteracaocadastralimovel/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return alteracaoCadastralImovelFacade;
    }

    public AlteracaoCadastralImovelControlador() {
        super(AlteracaoCadastralImovel.class);
    }

    @URLAction(mappingId = "novaAlteracaoCadastralImovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTime(sistemaControlador.getDataOperacao());
        selecionado.setDataRegistro(gregorianCalendar.getTime());
        selecionado.setHoraRegistro(gregorianCalendar.getTime());
        selecionado.setUsuarioResponsavel(sistemaControlador.getUsuarioCorrente());
    }

    @URLAction(mappingId = "verAlteracaoCadastralImovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        selecionado = alteracaoCadastralImovelFacade.recuperar(getId());

    }

    public List<SelectItem> getTiposDeProcesso() {
        List<SelectItem> lista = new ArrayList<SelectItem>();
        lista.add(new SelectItem(null, ""));
        for (AlteracaoCadastralImovel.TipoProcesso tipo : AlteracaoCadastralImovel.TipoProcesso.values()) {
            lista.add(new SelectItem(tipo, tipo.toString()));
        }
        return lista;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    private boolean existeDebitosEmAberto() {
        ConsultaParcela consulta = new ConsultaParcela();
        consulta.addParameter(ConsultaParcela.Campo.BCI_ID, ConsultaParcela.Operador.IGUAL, selecionado.getCadastroImobiliario().getId());
        consulta.addParameter(ConsultaParcela.Campo.PARCELA_SITUACAO, ConsultaParcela.Operador.IGUAL, SituacaoParcela.EM_ABERTO);
        consulta.executaConsulta();
        return consulta.getResultados() != null && consulta.getResultados().size() > 0;
    }

    private boolean validarDados() {
        if (!Util.validaCampos(selecionado)) {
            return false;
        }
        if (selecionado.getTipoProcesso().equals(AlteracaoCadastralImovel.TipoProcesso.ATIVO) && selecionado.getCadastroImobiliario().getAtivo()) {
            FacesUtil.addError("Não foi possível continuar!", "O imóvel selecionado já encontra-se Ativo.");
            return false;
        } else if (selecionado.getTipoProcesso().equals(AlteracaoCadastralImovel.TipoProcesso.INATIVO) && !selecionado.getCadastroImobiliario().getAtivo()) {
            FacesUtil.addError("Não foi possível continuar!", "O imóvel selecionado já encontra-se Inativo.");
            return false;
        }
        if (selecionado.getCadastroImobiliario().getAtivo() && existeDebitosEmAberto()) {
            FacesUtil.addError("Não foi possível continuar!", "O imóvel selecionado possui débitos em aberto.");
            return false;
        }
        return true;
    }

    public Integer retornaAno() {
        Date dataAtual = sistemaControlador.getDataOperacao();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dataAtual);
        return new Integer(calendar.get(Calendar.YEAR));
    }

   private void alterarSituacaoCadastroImobiliario() {
       CadastroImobiliario cadastroImobiliario = selecionado.getCadastroImobiliario();
       cadastroImobiliario.setAtivo(selecionado.getTipoProcesso().equals(AlteracaoCadastralImovel.TipoProcesso.ATIVO));
       cadastroImobiliarioFacade.alterar(cadastroImobiliario);
   }

    @Override
    public void salvar() {
        if (!validarDados()) {
            return;
        }
        try {
            Integer ano = retornaAno();
            Long ultimoNumeroDoAno = alteracaoCadastralImovelFacade.retornaUltimoNumeroDoAno(ano);
            Long numeroRegistro = new Long(ano.toString() + StringUtil.cortaOuCompletaEsquerda(ultimoNumeroDoAno.toString(), 4, "0"));
            selecionado.setRegistro(numeroRegistro);
            alterarSituacaoCadastroImobiliario();
            if (operacao == Operacoes.NOVO) {
                getFacede().salvarNovo(selecionado);
            }
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), "Processo registrado com o Nº " + numeroRegistro + "."));
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
            return;
        } catch (Exception e) {
            descobrirETratarException(e);

        }
        redireciona();
    }
}
