package br.com.webpublico.controle;

import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.Memorando;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.entidadesauxiliares.AssistenteDetentorArquivoComposicao;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.MemorandoFacade;
import br.com.webpush.push.Push;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Criado por Mateus
 * Data: 15/03/2017.
 */
@ManagedBean(name = "memorandoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-memorando", pattern = "/memorando/novo/", viewId = "/faces/admin/memorando/edita.xhtml"),
    @URLMapping(id = "resposta-memorando", pattern = "/memorando/resposta/#{memorandoControlador.id}/", viewId = "/faces/admin/memorando/edita.xhtml"),
    @URLMapping(id = "visualizar-memorando", pattern = "/memorando/ver/#{memorandoControlador.id}/", viewId = "/faces/admin/memorando/visualiza.xhtml")
})
public class MemorandoControlador extends PrettyControlador<Memorando> implements Serializable, CRUD {

    public static final Double ITENS_PAGINA = 10.0;
    @EJB
    private MemorandoFacade memorandoFacade;
    private List<UsuarioSistema> usuarios;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private boolean resposta;
    private AssistenteDetentorArquivoComposicao assistenteDetentorArquivoComposicao;
    private String filtro = "";
    private List<Memorando> memorandos;
    private Integer total;
    private Integer quantidadeMemorandosNaoLidos;
    private List<Pagina> paginas;
    private Pagina paginaAtual;
    private TipoMemorando tipoMemorando;

    public MemorandoControlador() {
        super(Memorando.class);
    }

    public TipoMemorando getTipoMemorando() {
        return tipoMemorando;
    }

    public TipoMemorando getTipoEnviado() {
        return TipoMemorando.ENVIADO;
    }

    public TipoMemorando getTipoRecebido() {
        return TipoMemorando.RECEBIDO;
    }

    @URLAction(mappingId = "visualizar-memorando", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        if (selecionado.getUsuarioDestino().equals(memorandoFacade.getSistemaFacade().getUsuarioCorrente())) {
            selecionado.setVisualizado(true);
        }
        inicializarAtributos();
    }

    private void inicializarAtributos() {
        usuarios = new ArrayList<>();
        hierarquiaOrganizacional = null;
        resposta = false;
        assistenteDetentorArquivoComposicao = new AssistenteDetentorArquivoComposicao(selecionado, memorandoFacade.getSistemaFacade().getDataOperacao());
    }

    @URLAction(mappingId = "novo-memorando", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setUsuarioOrigem(memorandoFacade.getSistemaFacade().getUsuarioCorrente());
        inicializarAtributos();
    }

    @URLAction(mappingId = "resposta-memorando", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void resposta() {
        novo();
        Memorando memorandoOrigem = memorandoFacade.recuperar(getId());
        selecionado.setTitulo("RE: " + memorandoOrigem.getTitulo());
        selecionado.setMemorandoOrigem(memorandoOrigem);
        selecionado.setUsuarioDestino(memorandoOrigem.getUsuarioOrigem());
        memorandoOrigem.setVisualizado(true);
        usuarios.add(selecionado.getUsuarioDestino());
        resposta = true;
    }

    public void voltar() {
        memorandoFacade.salvar(selecionado);
        FacesUtil.redirecionamentoInterno("/");
    }

    @Override
    public void cancelar() {
        FacesUtil.redirecionamentoInterno("/");
    }

    public void responderMemorando(Memorando memorandoOrigem) {
        FacesUtil.redirecionamentoInterno(this.getCaminhoPadrao() + "resposta/" + memorandoOrigem.getId() + "/");
    }

    public void removerMemorando(Memorando memorando) {
        memorando.setRemovido(Boolean.TRUE);
        memorandoFacade.salvar(memorando);
        montarMemorandos();
    }

    public void criarNovo() {
        FacesUtil.redirecionamentoInterno(this.getCaminhoPadrao() + "novo/");
    }

    public void verMemorando(Memorando memorando) {
        FacesUtil.redirecionamentoInterno(this.getCaminhoPadrao() + "ver/" + memorando.getId() + "/");
    }

    public void notificarMemorando(UsuarioSistema usuarioDestino) {
        Push.sendTo(  memorandoFacade.getSistemaFacade().getUsuarioCorrente(), "memorando");
    }

    public String getCanal() {
        return "/" + memorandoFacade.getSistemaFacade().getUsuarioCorrente().getId() + "memorando";
    }

    public List<UsuarioSistema> buscarUsuariosSistema(String filtro) {
        return memorandoFacade.getUsuarioSistemaFacade().buscarTodosUsuariosPorLoginOuNome(filtro.trim());
    }

    public List<HierarquiaOrganizacional> buscarHierarquiasOrganizacionaisAdministrativas(String filtro) {
        return memorandoFacade.getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalTipo(filtro, TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), memorandoFacade.getSistemaFacade().getDataOperacao());
    }

    private List<UsuarioSistema> buscarUsuariosPelaUnidade() {
        List<HierarquiaOrganizacional> hierarquiasAdministrativas = new ArrayList<>();
        hierarquiasAdministrativas.add(hierarquiaOrganizacional);
        return memorandoFacade.getUsuarioSistemaFacade().recuperarUsuariosPorHierarquiasAdministrativas(hierarquiasAdministrativas, memorandoFacade.getSistemaFacade().getDataOperacao(), "", true);
    }

    public void montarPaginas() {
        if (paginas == null || paginas.isEmpty()) {
            paginas = Lists.newArrayList();
            total = memorandoFacade.buscarQuantidadeMemorandosPorUsuario(memorandoFacade.getSistemaFacade().getUsuarioCorrente(), filtro, tipoMemorando);
            double totalPaginas = total / ITENS_PAGINA;
            for (int numeroPagina = 0; numeroPagina < totalPaginas; numeroPagina++) {
                Pagina pagina = new Pagina(numeroPagina + 1, numeroPagina > 0 ? (ITENS_PAGINA.intValue() * numeroPagina) : 0);
                paginas.add(pagina);
            }
            if (!paginas.isEmpty()) {
                paginaAtual = paginas.get(0);
            } else {
                paginaAtual = new Pagina(1, 0);
            }
        }
    }

    public Integer getTotal() {
        return total;
    }

    public void filtrar() {
        memorandos.clear();
        paginas.clear();
    }

    public void avancarPagina() {
        paginaAtual = paginas.get(paginaAtual.numero);
        memorandos.clear();
    }

    public void voltarPagina() {
        paginaAtual = paginas.get(paginaAtual.numero - 2);
        memorandos.clear();
    }

    public List<Pagina> getPaginas() {
        if (paginas == null) {
            paginas = Lists.newArrayList();
        }
        return paginas;
    }

    public String getNumeroPaginas() {
        if (paginaAtual != null) {
            return paginaAtual.numero + " / " + paginas.size();
        }
        return "1/1";
    }

    public String getTextoRodapeTabela() {
        if (paginaAtual != null) {
            int de = paginaAtual.index == 0 ? 1 : paginaAtual.index + 1;
            int ate = paginaAtual.index == 0 ? memorandos.size() :
                paginaAtual.index + (memorandos.size());
            return "Exibindo de " + de + " até " + ate + " em um total de " + total + " registros";
        }
        return "";
    }

    public Pagina getPaginaAtual() {
        return paginaAtual;
    }

    public List<Memorando> getMemorandos() {
        if (memorandos == null || memorandos.isEmpty()) {
            montarMemorandos();
        }
        return memorandos;
    }

    public void filtrarCaixaEntrada() {
        tipoMemorando = TipoMemorando.RECEBIDO;
        memorandos.clear();
        paginas.clear();
    }

    public void filtrarEnviados() {
        tipoMemorando = TipoMemorando.ENVIADO;
        memorandos.clear();
        paginas.clear();
    }

    private void montarMemorandos() {
        if (filtro == null) {
            filtro = "";
        }
        if (tipoMemorando == null) {
            tipoMemorando = TipoMemorando.RECEBIDO;
        }
        montarPaginas();
        memorandos = memorandoFacade.buscarMemorandosPorUsuarioAndTituloOrUsuarioOrigem(memorandoFacade.getSistemaFacade().getUsuarioCorrente(), tipoMemorando, filtro, paginaAtual.index, ITENS_PAGINA.intValue());
        quantidadeMemorandosNaoLidos = memorandoFacade.buscarQuantidadeMemorandosPorUsuarioNaoLidos(memorandoFacade.getSistemaFacade().getUsuarioCorrente());
    }

    public Integer getQuantidadeMemorandosNaoLidos() {
        if (quantidadeMemorandosNaoLidos == null) {
            montarMemorandos();
        }
        return quantidadeMemorandosNaoLidos;
    }

    @Override
    public void salvar() {
        try {
            realizarValidacoes();
            if (isOperacaoNovo() || resposta) {
                selecionado.setDataEnvio(new Date());
                enviarMemorandos();
            } else {
                memorandoFacade.salvar(selecionado);
            }
            FacesUtil.addOperacaoRealizada("Memorando enviado com sucesso!");
            FacesUtil.redirecionamentoInterno("/");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            descobrirETratarException(ex);
        }
    }

    private void realizarValidacoes() {
        selecionado.realizarValidacoes();
        ValidacaoException ve = new ValidacaoException();
        if ((usuarios == null || usuarios.isEmpty()) && hierarquiaOrganizacional == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Para enviar um memorando, é necessário informar os Usuários ou uma Unidade Administrativa!");
        }
        ve.lancarException();
    }

    private void enviarMemorandos() {
        if (usuarios == null) {
            usuarios = Lists.newArrayList();
        }
        if (hierarquiaOrganizacional != null) {
            List<UsuarioSistema> usuariosHierarquia = buscarUsuariosPelaUnidade();
            for (UsuarioSistema usuarioSistema : usuariosHierarquia) {
                Util.adicionarObjetoEmLista(usuarios, usuarioSistema);
            }
        }
        for (UsuarioSistema usuarioSistema : usuarios) {
            Memorando memorando = (Memorando) Util.clonarObjeto(selecionado);
            memorando.setUsuarioDestino(usuarioSistema);
            memorandoFacade.salvar(memorando);
            notificarMemorando(memorando.getUsuarioDestino());
        }
    }

    @Override
    public String getCaminhoPadrao() {
        return "/memorando/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return memorandoFacade;
    }

    public List<UsuarioSistema> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(List<UsuarioSistema> usuarios) {
        this.usuarios = usuarios;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public boolean isResposta() {
        return resposta;
    }

    public void setResposta(boolean resposta) {
        this.resposta = resposta;
    }

    public AssistenteDetentorArquivoComposicao getAssistenteDetentorArquivoComposicao() {
        return assistenteDetentorArquivoComposicao;
    }

    public void setAssistenteDetentorArquivoComposicao(AssistenteDetentorArquivoComposicao assistenteDetentorArquivoComposicao) {
        this.assistenteDetentorArquivoComposicao = assistenteDetentorArquivoComposicao;
    }

    public String getFiltro() {
        return filtro;
    }

    public void setFiltro(String filtro) {
        this.filtro = filtro;
    }

    public class Pagina {
        protected int numero, index;

        public Pagina(int numero, int index) {
            this.numero = numero;
            this.index = index;
        }

        public int getNumero() {
            return numero;
        }

        public void setNumero(int numero) {
            this.numero = numero;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }
    }

    public static enum TipoMemorando {
        ENVIADO("Enviados"), RECEBIDO("Caixa de entrada");
        private String descricao;

        TipoMemorando(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }
}
