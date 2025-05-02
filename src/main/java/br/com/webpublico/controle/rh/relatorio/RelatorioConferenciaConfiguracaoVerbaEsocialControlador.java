package br.com.webpublico.controle.rh.relatorio;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.Entidade;
import br.com.webpublico.entidades.EventoFP;
import br.com.webpublico.enums.rh.relatorios.TipoRelatorioConferenciaConfiguracaoVerbasEsocial;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.EventoFPFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.rh.esocial.ConfiguracaoEmpregadorESocialFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.*;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.util.*;

@ManagedBean(name = "relatorioConferenciaConfiguracaoVerbaEsocialControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "conferencia-configuracao-verbas-esocial", pattern = "/conferencia-configuracao-verbas-esocial/", viewId = "/faces/rh/relatorios/relatorioconferenciaconfiguracaoverbaesocial.xhtml")
})
public class RelatorioConferenciaConfiguracaoVerbaEsocialControlador extends AbstractReport {

    protected static final Logger logger = LoggerFactory.getLogger(RelatorioConferenciaConfiguracaoVerbaEsocialControlador.class);
    @EJB
    private EventoFPFacade eventoFPFacade;
    @EJB
    private ConfiguracaoEmpregadorESocialFacade configuracaoEmpregadorESocialFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    private EventoFP eventoFP;
    private List<EventoFP> eventos;
    private Entidade entidade;
    private List<Entidade> empregadores;
    private Boolean exibirDescricoesConfg;
    private TipoRelatorioConferenciaConfiguracaoVerbasEsocial tipoAgrupamento;

    @URLAction(mappingId = "conferencia-configuracao-verbas-esocial", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limpaCampos() {
        exibirDescricoesConfg = Boolean.FALSE;
        eventos = Lists.newArrayList();
        empregadores = Lists.newArrayList();
        eventoFP = null;
        entidade = null;
        tipoAgrupamento = TipoRelatorioConferenciaConfiguracaoVerbasEsocial.VERBA;
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarCampos();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.setNomeParametroBrasao("BRASAO");
            dto.setNomeRelatorio("Relatório de Conferência de Configuração de Verbas eSocial");
            dto.adicionarParametro("MODULO", "Recursos Humanos");
            dto.adicionarParametro("SECRETARIA", "SECRETARIA MUNICIPAL DE ADMINISTRAÇÃO");
            dto.adicionarParametro("NOMERELATORIO", "Relatório de Conferência de Configuração de Verbas eSocial");
            dto.adicionarParametro("CONDICAO", montarCondicoes());
            dto.adicionarParametro("TIPO_RELATORIO", tipoAgrupamento.name());
            dto.adicionarParametro("EXIBIR_DESCRICAO", exibirDescricoesConfg);
            dto.adicionarParametro("FILTROS_UTILIZADOS", buscarFiltrosUtilizados());
            dto.adicionarParametro("USUARIO", sistemaFacade.getUsuarioCorrente().getNome(), false);
            dto.setApi("rh/conferencia-configuracao-verbas-esocial/");
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
            logger.error("Erro ao gerar relatório: " + e);
        }
    }

    private String montarCondicoes() {
        StringBuilder sb = new StringBuilder();
        sb.append(" where ev.codigo in (");

        for (EventoFP evento : eventos) {
            sb.append(evento.getCodigo()).append(", ");
        }
        sb = new StringBuilder(sb.substring(0, sb.length() - 2));
        sb.append(") ");

        if (empregadores != null && !empregadores.isEmpty()) {
            sb.append(" and entempregador.id in (");
            for (Entidade empregador : empregadores) {
                sb.append(empregador.getId()).append(" ,");
            }
            sb = new StringBuilder(sb.substring(0, sb.length() - 2));
            sb.append(") ");
        }
        return sb.toString();
    }

    private String buscarFiltrosUtilizados() {
        ordenarVerbas(eventos);
        StringBuilder sb = new StringBuilder();
        sb.append("Agrupado por: ").append(tipoAgrupamento.toString());
        sb.append("; Verbas: ");
        for (EventoFP ev : eventos) {
            sb.append(ev.getCodigo()).append(", ");
        }
        sb = new StringBuilder(sb.substring(0, sb.length() - 2));
        sb.append(";");
        if (empregadores != null && !empregadores.isEmpty()) {
            ordenarEmpregadores(empregadores);
            sb.append(" Empregadores: ");
            for (Entidade emp : empregadores) {
                sb.append(StringUtil.removeCaracteresEspeciaisSemEspaco(emp.getPessoaJuridica().getCnpj())).append(", ");
            }
            sb = new StringBuilder(sb.substring(0, sb.length() - 2));
            sb.append(";");
        } else {
            sb.append(" Empregadores: Todos;");
        }
        sb.append(" Incluir Descrições das Configurações: ").append(exibirDescricoesConfg ? "Sim." : "Não.");
        return sb.toString();
    }


    public void ordenarVerbas(List<EventoFP> verbas) {
        Collections.sort(verbas, new Comparator<EventoFP>() {
            @Override
            public int compare(EventoFP o1, EventoFP o2) {
                return Integer.valueOf(o1.getCodigo()).compareTo(Integer.valueOf(o2.getCodigo()));
            }
        });
    }

    public void ordenarEmpregadores(List<Entidade> verbas) {
        Collections.sort(verbas, new Comparator<Entidade>() {
            @Override
            public int compare(Entidade o1, Entidade o2) {
                return o1.getPessoaJuridica().getCnpj().compareTo(o2.getPessoaJuridica().getCnpj());
            }
        });
    }

    public void removerVerba(EventoFP eventoFP) {
        eventos.remove(eventoFP);
    }

    public void removerEmpregador(Entidade entidade) {
        empregadores.remove(entidade);
    }

    public void adicionarVerba() {
        try {
            validarAdicionarVerba();
            eventos.add(eventoFP);
            eventoFP = null;
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public void adicionarEmpregador() {
        try {
            validarAdicionarEmpregador();
            empregadores.add(entidade);
            entidade = null;
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (eventos == null || eventos.isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo verbas deve ser infomado.");
        }
        ve.lancarException();
    }

    private void validarAdicionarVerba() {
        ValidacaoException ve = new ValidacaoException();
        if (eventoFP == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo verbas deve ser infomado.");
        } else {
            for (EventoFP verba : eventos) {
                if (verba.equals(eventoFP)) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("A verba " + eventoFP + " já foi adicionada na lista.");
                }
            }
        }
        ve.lancarException();
    }

    private void validarAdicionarEmpregador() {
        ValidacaoException ve = new ValidacaoException();
        if (entidade == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Empregadores deve ser infomado.");
        } else {
            for (Entidade ent : empregadores) {
                if (ent.getId().equals(entidade.getId())) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O empregador " + entidade + " já foi adicionado na lista.");
                }
            }
        }
        ve.lancarException();
    }

    public List<SelectItem> getTiposAgrupamento() {
        return Util.getListSelectItemSemCampoVazio(TipoRelatorioConferenciaConfiguracaoVerbasEsocial.values(), false);
    }

    public List<EventoFP> completaEventoFP(String parte) {
        return eventoFPFacade.listaFiltrandoEventosAtivos(parte.trim());
    }

    public List<Entidade> completaEmpregador(String parte) {
        return configuracaoEmpregadorESocialFacade.buscarEntidadesDasConfiguracoesDoEmpregadorFiltrando(parte.trim());
    }

    public EventoFP getEventoFP() {
        return eventoFP;
    }

    public void setEventoFP(EventoFP eventoFP) {
        this.eventoFP = eventoFP;
    }

    public List<EventoFP> getEventos() {
        return eventos;
    }

    public void setEventos(List<EventoFP> eventos) {
        this.eventos = eventos;
    }

    public Entidade getEntidade() {
        return entidade;
    }

    public void setEntidade(Entidade entidade) {
        this.entidade = entidade;
    }

    public List<Entidade> getEmpregadores() {
        return empregadores;
    }

    public void setEmpregadores(List<Entidade> empregadores) {
        this.empregadores = empregadores;
    }

    public Boolean getExibirDescricoesConfg() {
        return exibirDescricoesConfg;
    }

    public void setExibirDescricoesConfg(Boolean exibirDescricoesConfg) {
        this.exibirDescricoesConfg = exibirDescricoesConfg;
    }

    public TipoRelatorioConferenciaConfiguracaoVerbasEsocial getTipoAgrupamento() {
        return tipoAgrupamento;
    }

    public void setTipoAgrupamento(TipoRelatorioConferenciaConfiguracaoVerbasEsocial tipoAgrupamento) {
        this.tipoAgrupamento = tipoAgrupamento;
    }
}
