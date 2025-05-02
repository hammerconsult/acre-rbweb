package br.com.webpublico.seguranca.resources;

import br.com.webpublico.agendamentotarefas.SingletonAgendamentoTarefas;
import br.com.webpublico.agendamentotarefas.job.WPJob;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.entidadesauxiliares.FavoritoDTO;
import br.com.webpublico.entidadesauxiliares.HierarquiaOrganizacionalDTO;
import br.com.webpublico.entidadesauxiliares.JobDTO;
import br.com.webpublico.entidadesauxiliares.UsuarioSistemaDTO;
import br.com.webpublico.enums.TipoNotificacao;
import br.com.webpublico.seguranca.service.SistemaService;
import br.com.webpublico.singletons.SingletonFavoritos;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/usuario-sistema")
@Scope("session")
public class UsuarioSistemaResource {

    private UsuarioSistemaDTO usuarioLogado;
    @Autowired
    private SistemaService sistemaService;
    @Autowired
    private SingletonAgendamentoTarefas singletonAgendamentoTarefas;
    @Autowired
    private SingletonFavoritos singletonFavoritos;


    @RequestMapping(value = "/administrativa-por-usuario", method = RequestMethod.GET)
    @ResponseBody
    public List<HierarquiaOrganizacionalDTO> getAdministrativasUsuario() {
        return sistemaService.getAdministrativas();
    }

    @RequestMapping(value = "/trocar-administrativa-usuario", method = RequestMethod.GET)
    @ResponseBody
    public void trocarAdministrativa(@RequestParam(value = "id") Long id) {
        sistemaService.setAdministrativaCorrente(id);
        usuarioLogado = null;
    }

    @RequestMapping(value = "/orcamentaria-por-usuario", method = RequestMethod.GET)
    @ResponseBody
    public List<HierarquiaOrganizacionalDTO> getOrcamentariasUsuario() {
        return sistemaService.getOrcamentarias();
    }

    @RequestMapping(value = "/trocar-orcamentaria-usuario", method = RequestMethod.GET)
    @ResponseBody
    public void trocarOrcamentaria(@RequestParam(value = "id") Long id) {
        sistemaService.setOrcamentariaCorrente(id, true);
        usuarioLogado = null;
    }


    @RequestMapping(value = "/usuario-logado", method = RequestMethod.GET)
    @ResponseBody
    public UsuarioSistemaDTO getUsuarioLogado() {
        if (usuarioLogado == null) {
            criarUsuarioDTO();
        }
        return usuarioLogado;
    }

    @RequestMapping(value = "/jobs", method = RequestMethod.GET)
    @ResponseBody
    public List<JobDTO> getJobs() {
        List<JobDTO> dtos = Lists.newArrayList();
        for (WPJob job : singletonAgendamentoTarefas.getJobs()) {
            dtos.add(new JobDTO(job.toString()));
        }
        return dtos;
    }

    @RequestMapping(value = "/tipos-notificacao", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, String> getTiposNotificacao() {
        Map<String, String> hashedMap = Maps.newHashMap();
        for (TipoNotificacao value : TipoNotificacao.values()) {
            hashedMap.put(value.name(), value.getDescricao());
        }
        return hashedMap;
    }

    @RequestMapping(value = "/favoritos-por-usuario", method = RequestMethod.GET)
    @ResponseBody
    public List<FavoritoDTO> getFavoritos() {
        return singletonFavoritos.getFavoritos();
    }

    @RequestMapping(value = "/trocar-data-sistema", method = RequestMethod.GET)
    @ResponseBody
    public void trocarDataUsuario(@RequestParam(value = "data-operacao") String data) {
        usuarioLogado = null;
        sistemaService.trocarDataSistema(DataUtil.getDateParse(data));
    }

    private void criarUsuarioDTO() {
        usuarioLogado = new UsuarioSistemaDTO();
        UsuarioSistema usuarioCorrente = sistemaService.getUsuarioCorrente();
        usuarioLogado.setLogin(usuarioCorrente.getLogin());
        usuarioLogado.setId(usuarioCorrente.getId());
        usuarioLogado.setNome(usuarioCorrente.getNome());
        usuarioLogado.setUnidadeAdmLogada(usuarioCorrente.getAdministrativaCorrente().getDescricao());
        usuarioLogado.setIdUnidadeAdmLogada(usuarioCorrente.getAdministrativaCorrente().getId());
        HierarquiaOrganizacional hierarquiAdministrativaCorrente = sistemaService.getHierarquiAdministrativaCorrente();
        if (hierarquiAdministrativaCorrente != null) {
            usuarioLogado.setIdHierarquiaAdmLogada(hierarquiAdministrativaCorrente.getId());
            usuarioLogado.setHierarquiaAdmLogada(hierarquiAdministrativaCorrente.getDescricao());
        }
        usuarioLogado.setUnidadeOrcLogada(usuarioCorrente.getOrcamentariaCorrente().getDescricao());
        usuarioLogado.setIdUnidadeOrcLogada(usuarioCorrente.getOrcamentariaCorrente().getId());
        usuarioLogado.setHierarquiaOrcLogada(sistemaService.getHierarquiOrcamentariaCorrente().getDescricao());
        usuarioLogado.setIdHieraraquiaOrcLogada(sistemaService.getHierarquiOrcamentariaCorrente().getId());
        usuarioLogado.setDataOperacao(Util.formatterDiaMesAno.format(sistemaService.getDataOperacao()));
        usuarioLogado.setNomeResumido(usuarioCorrente.getPessoaFisica().getPrimeiroNome());
        usuarioLogado.setPodeAlterarData(usuarioCorrente.getPodeAlterarData());
    }

    public void atribuirNullUsuario() {
        this.usuarioLogado = null;
    }
}
