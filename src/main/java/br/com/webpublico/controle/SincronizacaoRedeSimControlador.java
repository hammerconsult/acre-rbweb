package br.com.webpublico.controle;

import br.com.webpublico.entidades.PessoaJuridica;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.enums.AutorizacaoTributario;
import br.com.webpublico.enums.SituacaoCadastralCadastroEconomico;
import br.com.webpublico.negocios.SincronizacaoRedeSimFacade;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.FacesUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "sincronizacaoRedeSim", pattern = "/cadastro-municipal/sincronizacao-redesim/",
        viewId = "/faces/tributario/cadastromunicipal/sincronizacaoredesim/lista.xhtml")})
public class SincronizacaoRedeSimControlador {

    @EJB
    private SincronizacaoRedeSimFacade facade;
    private AssistenteBarraProgresso assistente;
    private String cmcInicial;
    private String cmcFinal;
    private Date dataInicial;
    private Date dataFinal;
    private List<PessoaJuridica> pessoasJuridicas;
    private Map<Long, SituacaoCadastralCadastroEconomico> mapSituacaoCmcDaPessoa;
    private List<Future> futuresSincronizacoes;
    private boolean atualizarEnquadramentoFiscal;
    private boolean isPermiteAlterarEnquadramentoFiscal;

    public AssistenteBarraProgresso getAssistente() {
        return assistente;
    }

    public void setAssistente(AssistenteBarraProgresso assistente) {
        this.assistente = assistente;
    }

    public String getCmcInicial() {
        return cmcInicial;
    }

    public void setCmcInicial(String cmcInicial) {
        this.cmcInicial = cmcInicial;
    }

    public String getCmcFinal() {
        return cmcFinal;
    }

    public void setCmcFinal(String cmcFinal) {
        this.cmcFinal = cmcFinal;
    }

    public Date getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(Date dataInicial) {
        this.dataInicial = dataInicial;
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    public boolean isAtualizarEnquadramentoFiscal() {
        return atualizarEnquadramentoFiscal;
    }

    public void setAtualizarEnquadramentoFiscal(boolean atualizarEnquadramentoFiscal) {
        this.atualizarEnquadramentoFiscal = atualizarEnquadramentoFiscal;
    }

    public boolean isPermiteAlterarEnquadramentoFiscal() {
        return isPermiteAlterarEnquadramentoFiscal;
    }

    public void setPermiteAlterarEnquadramentoFiscal(boolean permiteAlterarEnquadramentoFiscal) {
        isPermiteAlterarEnquadramentoFiscal = permiteAlterarEnquadramentoFiscal;
    }

    public List<PessoaJuridica> getPessoasJuridicas() {
        return pessoasJuridicas;
    }

    public void setPessoasJuridicas(List<PessoaJuridica> pessoasJuridicas) {
        this.pessoasJuridicas = pessoasJuridicas;
    }

    @URLAction(mappingId = "sincronizacaoRedeSim", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        cmcInicial = null;
        cmcFinal = null;
        dataInicial = null;
        dataFinal = null;
        pessoasJuridicas = Lists.newArrayList();
        futuresSincronizacoes = null;
        mapSituacaoCmcDaPessoa = Maps.newHashMap();
        atualizarEnquadramentoFiscal = false;
        UsuarioSistema usuarioSistema = facade.getUsuarioSistemaFacade().recuperar(facade.getSistemaFacade().getUsuarioCorrente().getId());
        if (usuarioSistema.isFiscalTributario()) {
            isPermiteAlterarEnquadramentoFiscal = facade.getUsuarioSistemaFacade().validaAutorizacaoUsuario(usuarioSistema, AutorizacaoTributario.PERMITIR_ALTERAR_ENQUADRAMENTO_FISCAL);
        }
    }

    public void consultar() {
        mapSituacaoCmcDaPessoa.clear();
        pessoasJuridicas = facade.buscarPessoasJuridicasParaSincronizacao(cmcInicial, cmcFinal, dataInicial, dataFinal);
        List<List<PessoaJuridica>> partition = Lists.partition(pessoasJuridicas, 500);
        for (List<PessoaJuridica> pjs : partition) {
            mapSituacaoCmcDaPessoa.putAll(facade.buscarSituacaoCmcPeloIdDaPessoa(pjs.stream().map(PessoaJuridica::getId).collect(Collectors.toList())));
        }
        if (pessoasJuridicas.isEmpty()) {
            FacesUtil.addWarn("Atenção!", "Nenhuma pessoa jurídica encontrada com os filtros selecionado.");
        }
    }

    public String buscarSituacaoCmcNoMapa(PessoaJuridica pj) {
        if (mapSituacaoCmcDaPessoa.containsKey(pj.getId())) {
            return mapSituacaoCmcDaPessoa.get(pj.getId()).getDescricao();
        }
        return pj.getSituacaoCadastralPessoa().getDescricao();
    }

    public void iniciarSincronizacao() {
        assistente = new AssistenteBarraProgresso();
        assistente.setUsuarioSistema(facade.getSistemaFacade().getUsuarioCorrente());
        assistente.setUsuarioBancoDados(facade.getSistemaFacade().getUsuarioDB());
        assistente.setDescricaoProcesso("Sincronizando empresas registradas no município com a REDESIM.");
        assistente.zerarContadoresProcesso();
        assistente.setTotal(pessoasJuridicas.size());
        int size = pessoasJuridicas.size() > 5 ? pessoasJuridicas.size() / 5 : 1;
        List<List<PessoaJuridica>> listOfListPessoasJuridicas = Lists.partition(pessoasJuridicas, size);
        futuresSincronizacoes = Lists.newArrayList();
        for (List<PessoaJuridica> listPessoasJuridicas : listOfListPessoasJuridicas) {
            futuresSincronizacoes.add(facade.sincronizar(assistente, listPessoasJuridicas, atualizarEnquadramentoFiscal));
        }
        FacesUtil.executaJavaScript("pollSincronizacao.start()");
        FacesUtil.executaJavaScript("openDialog(dlgAcompanhamento)");
    }

    public void acompanharSincronizacao() {
        if (futuresSincronizacoes != null) {
            boolean sincronizou = true;
            for (Future futureSincronizacao : futuresSincronizacoes) {
                if (!futureSincronizacao.isDone()) {
                    sincronizou = false;
                    break;
                }
            }
            if (sincronizou) {
                FacesUtil.executaJavaScript("pollSincronizacao.stop()");
                FacesUtil.executaJavaScript("closeDialog(dlgAcompanhamento)");
                FacesUtil.addOperacaoRealizada("Sincronização com a REDESIM realizada com sucesso.");
                novo();
                FacesUtil.atualizarComponente("formulario");
            }
        }
    }
}
