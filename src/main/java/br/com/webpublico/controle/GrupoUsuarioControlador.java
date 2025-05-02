/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.PeriodoGrupoUsuario;
import br.com.webpublico.enums.DiaSemana;
import br.com.webpublico.enums.TipoGrupoUsuario;
import br.com.webpublico.enums.TipoNotificacao;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.GrupoUsuarioFacade;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author fabio
 */
@ManagedBean(name = "grupoUsuarioControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoGrupoUsuario", pattern = "/admin/grupousuario/novo/", viewId = "/faces/admin/controleusuario/grupousuario/edita.xhtml"),
    @URLMapping(id = "editarGrupoUsuario", pattern = "/admin/grupousuario/editar/#{grupoUsuarioControlador.id}/", viewId = "/faces/admin/controleusuario/grupousuario/edita.xhtml"),
    @URLMapping(id = "duplicarGrupoUsuario", pattern = "/admin/grupousuario/duplicar/#{grupoUsuarioControlador.id}/", viewId = "/faces/admin/controleusuario/grupousuario/edita.xhtml"),
    @URLMapping(id = "listarGrupoUsuario", pattern = "/admin/grupousuario/listar/", viewId = "/faces/admin/controleusuario/grupousuario/lista.xhtml"),
    @URLMapping(id = "verGrupoUsuario", pattern = "/admin/grupousuario/ver/#{grupoUsuarioControlador.id}/", viewId = "/faces/admin/controleusuario/grupousuario/visualizar.xhtml")
})
public class GrupoUsuarioControlador extends PrettyControlador<GrupoUsuario> implements Serializable, CRUD {

    @EJB
    private GrupoUsuarioFacade grupoUsuarioFacade;
    private UsuarioSistema usuarioSistema;
    private ItemGrupoUsuario itemGrupoUsuario;
    private Boolean leitura;
    private Boolean escrita;
    private Boolean exclusao;
    private Date horarioInicio;
    private Date horarioTermino;
    private Boolean domingo;
    private Boolean segunda;
    private Boolean terca;
    private Boolean quarta;
    private Boolean quinta;
    private Boolean sexta;
    private Boolean sabado;
    private List<PeriodoGrupoUsuario> periodosGrupoUsuario;
    private GrupoUsuarioNotificacao grupoUsuarioNotificacao;

    public GrupoUsuarioControlador() {
        super(GrupoUsuario.class);
    }

    @URLAction(mappingId = "novoGrupoUsuario", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        this.selecionado.setTipo(TipoGrupoUsuario.AUTORIZACAO);
        novoUsuario();
        novoGrupoRecurso();
        novaNotificacao();
        inicializarAtributosControlador();
        periodosGrupoUsuario = new ArrayList<>();
    }

    private void inicializarAtributosControlador() {
        this.leitura = false;
        this.escrita = false;
        this.exclusao = false;
        this.domingo = false;
        this.segunda = false;
        this.terca = false;
        this.quarta = false;
        this.quinta = false;
        this.sexta = false;
        this.sabado = false;
        horarioInicio = null;
        horarioTermino = null;
    }

    @URLAction(mappingId = "duplicarGrupoUsuario", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void duplicar() {
        editar();
        GrupoUsuario clone = new GrupoUsuario();
        clone.setNome("Cópia de " + selecionado.getNome());
        clone.setTipo(selecionado.getTipo());

        for (UsuarioSistema u : selecionado.getUsuarios()) {
            clone.getUsuarios().add(u);
        }
        for (Periodo p : selecionado.getPeriodos()) {
            Periodo novoP = new Periodo();
            novoP.setDiaDaSemana(p.getDiaDaSemana());
            novoP.setInicio(p.getInicio());
            novoP.setTermino(p.getTermino());
            clone.getPeriodos().add(novoP);
        }
        for (ItemGrupoUsuario i : selecionado.getItens()) {
            ItemGrupoUsuario novoI = new ItemGrupoUsuario();
            novoI.setLeitura(i.getLeitura());
            novoI.setEscrita(i.getEscrita());
            novoI.setExclusao(i.getExclusao());
            novoI.setGrupoRecurso(i.getGrupoRecurso());
            novoI.setGrupoUsuario(clone);
            clone.getItens().add(novoI);
        }
        for (GrupoUsuarioNotificacao notificacao : selecionado.getNotificacoes()) {
            GrupoUsuarioNotificacao novaNotificacao = new GrupoUsuarioNotificacao();
            novaNotificacao.setTipoNotificacao(notificacao.getTipoNotificacao());
            novaNotificacao.setGrupoUsuario(clone);
            clone.getNotificacoes().add(novaNotificacao);
        }
        this.selecionado = clone;
    }

    @URLAction(mappingId = "editarGrupoUsuario", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        novoGrupoRecurso();
        novoUsuario();
        inicializarAtributosControlador();
        novaNotificacao();
        periodosGrupoUsuario = listaPeriodoGrupoUsuario();
    }

    @URLAction(mappingId = "verGrupoUsuario", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    public List<GrupoUsuario> getLista() {
        return this.grupoUsuarioFacade.lista();
    }

    public GrupoUsuarioFacade getFacade() {
        return grupoUsuarioFacade;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (this.selecionado.getNome() == null || "".equals(this.selecionado.getNome())) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Nome deve ser informado.");
        } else if (grupoUsuarioFacade.jaExisteNomeGrupoUsuario(selecionado)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe um Grupo de Usuário com esse nome!");
        }
        ve.lancarException();
    }

    @Override
    public void salvar() {
        try {
            validarCampos();
            selecionado = grupoUsuarioFacade.salvarRetornando(selecionado);

            for (UsuarioSistema usuario : selecionado.getUsuarios()) {
                grupoUsuarioFacade.getUsuarioSistemaFacade().executarDependenciasSalvarUsuario(usuario);
            }

            FacesUtil.addInfo("Salvo com sucesso!", "Grupo de Usuário salvo com sucesso!");
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addError("Não foi possível continuar!", "Erro ao salvar " + e.getMessage());
        }
    }

    public void removeUsuarioGrupo(UsuarioSistema usuario) {
        selecionado.getUsuarios().remove(usuario);
    }

    public void removePeriodo(PeriodoGrupoUsuario periodoGrupoUsuario) {
        periodosGrupoUsuario.remove(periodoGrupoUsuario);
        List<Periodo> periodosExcluidos = new ArrayList<Periodo>();
        for (Periodo per : selecionado.getPeriodos()) {
            if (per.getInicio().equals(periodoGrupoUsuario.getInicio()) && per.getTermino().equals(periodoGrupoUsuario.getTermino())) {
                periodosExcluidos.add(per);
            }
        }

        if (!periodosExcluidos.isEmpty()) {
            for (Periodo per : periodosExcluidos) {
                selecionado.getPeriodos().remove(per);
            }
        }
    }

    public void removeItens(ItemGrupoUsuario item) {
        selecionado.getItens().remove(item);
    }

    public List<UsuarioSistema> completarUsuarioSistema(String parte) {
        return grupoUsuarioFacade.getUsuarioSistemaFacade().buscarTodosUsuariosPorLoginOuNomeOuCpf(parte.trim());
    }

    private void validarUsuario() {
        ValidacaoException ve = new ValidacaoException();
        if (usuarioSistema == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Usuário deve ser informado.");
        }
        ve.lancarException();
        if (!selecionado.getUsuarios().isEmpty() && selecionado.getUsuarios().contains(usuarioSistema)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Usuário já adicionado na lista.");
        }
        ve.lancarException();
    }

    public void addUsuario() {
        try {
            validarUsuario();
            selecionado.getUsuarios().add(usuarioSistema);
            novoUsuario();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private void novoUsuario() {
        usuarioSistema = new UsuarioSistema();
    }

    public void adicionarPeriodo() {
        try {
            validarPeriodo();
            PeriodoGrupoUsuario per = new PeriodoGrupoUsuario();
            per.setDomingo("0");
            per.setSegunda("0");
            per.setTerca("0");
            per.setQuarta("0");
            per.setQuinta("0");
            per.setSexta("0");
            per.setSabado("0");
            per.setInicio(horarioInicio);
            per.setTermino(horarioTermino);
            if (domingo) {
                adicionaPeriodo(DiaSemana.DOMINGO);
                per.setDomingo("1");
            }
            if (segunda) {
                adicionaPeriodo(DiaSemana.SEGUNDA);
                per.setSegunda("1");
            }
            if (terca) {
                adicionaPeriodo(DiaSemana.TERCA);
                per.setTerca("1");
            }
            if (quarta) {
                adicionaPeriodo(DiaSemana.QUARTA);
                per.setQuarta("1");
            }
            if (quinta) {
                adicionaPeriodo(DiaSemana.QUINTA);
                per.setQuinta("1");
            }
            if (sexta) {
                adicionaPeriodo(DiaSemana.SEXTA);
                per.setSexta("1");
            }
            if (sabado) {
                adicionaPeriodo(DiaSemana.SABADO);
                per.setSabado("1");
            }
            periodosGrupoUsuario.add(per);

            inicializarAtributosControlador();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    private void validarPeriodo() {
        ValidacaoException ve = new ValidacaoException();
        if (!domingo && !segunda && !terca && !quarta && !quinta && !sexta && !sabado) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Dia da Semana deve ser informado.");
        }
        if (horarioInicio == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Hora de Início deve ser informado.");
        }
        if (horarioTermino == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Hora de Término deve ser informado.");
        }
        ve.lancarException();
        if (horarioInicio.after(horarioTermino)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O Horário de Inicío deve ser anterior ao Horário de Término.");
        }
        ve.lancarException();
    }

    private void adicionaPeriodo(DiaSemana dia) {
        Periodo per = new Periodo();
        per.setDiaDaSemana(dia);
        per.setInicio(horarioInicio);
        per.setTermino(horarioTermino);
        selecionado.getPeriodos().add(per);
    }

    private void validarItemGrupoRecurso() {
        ValidacaoException ve = new ValidacaoException();
        if (itemGrupoUsuario.getGrupoRecurso() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Grupo de Recurso deve ser informado.");
        }
        for (ItemGrupoUsuario i : selecionado.getItens()) {
            if (i.getGrupoRecurso().equals(itemGrupoUsuario.getGrupoRecurso())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Grupo de Recurso já adicionado na lista.");
            }
        }
        ve.lancarException();
    }

    public void addItem() {
        try {
            validarItemGrupoRecurso();
            itemGrupoUsuario.setGrupoUsuario(selecionado);
            selecionado.getItens().add(itemGrupoUsuario);
            novoGrupoRecurso();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private void novoGrupoRecurso() {
        itemGrupoUsuario = new ItemGrupoUsuario();
    }

    public Boolean getDomingo() {
        return domingo;
    }

    public void setDomingo(Boolean domingo) {
        this.domingo = domingo;
    }

    public Boolean getQuarta() {
        return quarta;
    }

    public void setQuarta(Boolean quarta) {
        this.quarta = quarta;
    }

    public Boolean getQuinta() {
        return quinta;
    }

    public void setQuinta(Boolean quinta) {
        this.quinta = quinta;
    }

    public Boolean getSabado() {
        return sabado;
    }

    public void setSabado(Boolean sabado) {
        this.sabado = sabado;
    }

    public Boolean getSegunda() {
        return segunda;
    }

    public void setSegunda(Boolean segunda) {
        this.segunda = segunda;
    }

    public Boolean getSexta() {
        return sexta;
    }

    public void setSexta(Boolean sexta) {
        this.sexta = sexta;
    }

    public Boolean getTerca() {
        return terca;
    }

    public void setTerca(Boolean terca) {
        this.terca = terca;
    }

    public List<PeriodoGrupoUsuario> getPeriodosGrupoUsuario() {
        return periodosGrupoUsuario;
    }

    public void setPeriodosGrupoUsuario(List<PeriodoGrupoUsuario> periodosGrupoUsuario) {
        this.periodosGrupoUsuario = periodosGrupoUsuario;
    }

    public List<PeriodoGrupoUsuario> listaPeriodoGrupoUsuario() {
        List<PeriodoGrupoUsuario> listPeriodoGrupoUsuario = new ArrayList<PeriodoGrupoUsuario>();

        List lista = grupoUsuarioFacade.listaPeriodoGrupoUsuario(selecionado);
        for (int i = 0; i < lista.size(); i++) {
            Object registro[] = (Object[]) lista.get(i);

            PeriodoGrupoUsuario per = new PeriodoGrupoUsuario();
            per.setDomingo(registro[0].toString());
            per.setSegunda(registro[1].toString());
            per.setTerca(registro[2].toString());
            per.setQuarta(registro[3].toString());
            per.setQuinta(registro[4].toString());
            per.setSexta(registro[5].toString());
            per.setSabado(registro[6].toString());
            per.setInicio((Date) registro[7]);
            per.setTermino((Date) registro[8]);

            listPeriodoGrupoUsuario.add(per);
        }

        return listPeriodoGrupoUsuario;
    }

    public List<GrupoRecurso> completarGrupoRecurso(String parte) {
        return grupoUsuarioFacade.getGrupoRecursoFacade().buscarGrupoRecursoPorNome(parte.trim());
    }

    @Override
    public AbstractFacade getFacede() {
        return grupoUsuarioFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/admin/grupousuario/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public GrupoUsuarioNotificacao getGrupoUsuarioNotificacao() {
        return grupoUsuarioNotificacao;
    }

    public List<SelectItem> getNotificacoes() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoNotificacao object : TipoNotificacao.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    private void validarNotificacao() {
        ValidacaoException ve = new ValidacaoException();
        if (grupoUsuarioNotificacao.getTipoNotificacao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Notificação deve ser informado.");
        }
        ve.lancarException();
        for (GrupoUsuarioNotificacao notificacao : selecionado.getNotificacoes()) {
            if (notificacao.getTipoNotificacao().equals(grupoUsuarioNotificacao.getTipoNotificacao())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O Tipo de Notificação já foi adicionado na lista.");
            }
        }
        ve.lancarException();
    }

    public void addNotificacao() {
        try {
            validarNotificacao();
            grupoUsuarioNotificacao.setGrupoUsuario(selecionado);
            selecionado.getNotificacoes().add(grupoUsuarioNotificacao);
            novaNotificacao();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private void novaNotificacao() {
        grupoUsuarioNotificacao = new GrupoUsuarioNotificacao();
    }

    public void removerNotificcao(GrupoUsuarioNotificacao grupoUsuarioNotificacao) {
        selecionado.getNotificacoes().remove(grupoUsuarioNotificacao);
    }

    public Date getHorarioInicio() {
        return horarioInicio;
    }

    public void setHorarioInicio(Date horarioInicio) {
        this.horarioInicio = horarioInicio;
    }

    public Date getHorarioTermino() {
        return horarioTermino;
    }

    public void setHorarioTermino(Date horarioTermino) {
        this.horarioTermino = horarioTermino;
    }

    public Boolean getEscrita() {
        return escrita;
    }

    public void setEscrita(Boolean escrita) {
        this.escrita = escrita;
    }

    public Boolean getExclusao() {
        return exclusao;
    }

    public void setExclusao(Boolean exclusao) {
        this.exclusao = exclusao;
    }

    public Boolean getLeitura() {
        return leitura;
    }

    public void setLeitura(Boolean leitura) {
        this.leitura = leitura;
    }

    public ItemGrupoUsuario getItemGrupoUsuario() {
        return itemGrupoUsuario;
    }

    public void setItemGrupoUsuario(ItemGrupoUsuario itemGrupoUsuario) {
        this.itemGrupoUsuario = itemGrupoUsuario;
    }
}
