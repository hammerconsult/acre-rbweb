package br.com.webpublico.reinf.service;

import br.com.webpublico.entidades.ConfiguracaoContabilContaReinf;
import br.com.webpublico.entidades.Pagamento;
import br.com.webpublico.entidades.PessoaJuridica;
import br.com.webpublico.entidades.contabil.reinf.FiltroReinf;
import br.com.webpublico.entidades.contabil.reinf.NotaReinf;
import br.com.webpublico.entidades.contabil.reinf.RegistroEventoRetencaoReinf;
import br.com.webpublico.entidades.rh.esocial.ConfiguracaoEmpregadorESocial;
import br.com.webpublico.entidadesauxiliares.AssistenteSincronizacaoReinf;
import br.com.webpublico.esocial.domain.EmpregadorESocial;
import br.com.webpublico.esocial.service.ESocialService;
import br.com.webpublico.negocios.ConfiguracaoContabilFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.contabil.reinf.ReinfFacade;
import br.com.webpublico.reinf.eventos.EventosReinfDTO;
import br.com.webpublico.reinf.eventos.TipoArquivoReinf;
import br.com.webpublico.reinf.eventos.domain.EventoR4020;
import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static br.com.webpublico.util.StringUtil.retornaApenasNumeros;

@Service(value = "r4020Service")
public class R4020Service {

    protected static final Logger logger = LoggerFactory.getLogger(R4020Service.class);
    @Autowired
    private ReinfService reinfService;
    private ReinfFacade reinfFacade;
    private ConfiguracaoContabilFacade configuracaoContabilFacade;
    private EmpregadorESocial empregadorESocial;

    @PostConstruct
    public void init() {
        try {
            reinfFacade = (ReinfFacade) new InitialContext().lookup("java:module/ReinfFacade");
            configuracaoContabilFacade = (ConfiguracaoContabilFacade) new InitialContext().lookup("java:module/ConfiguracaoContabilFacade");
        } catch (NamingException e) {
            logger.error("Erro ao injetar Facades : ", e);
        }
    }


    public List<RegistroEventoRetencaoReinf> buscarEvento(ConfiguracaoEmpregadorESocial config, FiltroReinf filtroReinf) {
        List<ConfiguracaoContabilContaReinf> contas = configuracaoContabilFacade.buscarContasReinfVigentesPorTipoArquivo(TipoArquivoReinf.R4020);
        if (contas.isEmpty()) {
            throw new ExcecaoNegocioGenerica("Nenhum conta configurada para as retenções do REINF foi encontrada na configuração contábil para o Tipo de Arquivo REINF " + TipoArquivoReinf.R4020.getCodigo() + ".");
        }
        List<RegistroEventoRetencaoReinf> registros = reinfFacade.buscarRegistrosEventoR4020(config, filtroReinf, contas);
        return registros != null ? registros : Lists.newArrayList();
    }

    public void enviar(EventoR4020 r4020) {
        reinfService.enviarEventoR4020(r4020);
    }

    public List<EventoR4020> criarEventosR4020(AssistenteSincronizacaoReinf assistente, List<RegistroEventoRetencaoReinf> objs) {
        List<EventoR4020> retorno = Lists.newArrayList();
        if (objs != null) {
            empregadorESocial = null;
            for (RegistroEventoRetencaoReinf obj : objs) {
                if (obj.isValido() && obj.getMarcarPraEnviar()) {
                    EventoR4020 r4020 = criarEventoR4020(assistente, obj);
                    if (r4020 != null) {
                        logger.info("XML " + r4020.getXml());
                        reinfFacade.salvarRegistroEvento(obj);
                        retorno.add(r4020);
                    }
                }
            }
        }
        return retorno;
    }

    public EventoR4020 criarEventoR4020(AssistenteSincronizacaoReinf assistente, RegistroEventoRetencaoReinf reg) {
        ConfiguracaoEmpregadorESocial config = assistente.getConfiguracaoEmpregadorESocial();
        if (empregadorESocial == null) {
            empregadorESocial = reinfService.getEmpregadorPorCnpj(retornaApenasNumeros(config.getEntidade().getPessoaJuridica().getCnpj()));
        }
        return criarEvento4020(config, reg);
    }

    private EventosReinfDTO.R4020 criarEvento4020(ConfiguracaoEmpregadorESocial config, RegistroEventoRetencaoReinf reg) {
        EventosReinfDTO.R4020 evento = (EventosReinfDTO.R4020) reinfService.getEventoR4020(empregadorESocial);

        Integer mes = DataUtil.getMes(reg.getData());
        Integer ano = DataUtil.getAno(reg.getData());
        evento.setIdESocial(reg.getPessoa().getId().toString().concat(ano.toString()).concat(mes.toString()));


        if (reinfService.isPerfilDev()) {
            evento.setTpAmb(2);
        }
        evento.setTpInsc(1);
        evento.setNrInsc(empregadorESocial.getNrInscID());
        evento.setPerApur(ano, mes);
        if (reg.getRetificacao()) {
            evento.setIndRetif(2);
            evento.setNrRecibo(reg.getNumeroReciboOrigem());
        } else {
            evento.setIndRetif(1);
        }

        evento.setTpInscEstab(1);
        evento.setNrInscEstab(config.getEntidade().getPessoaJuridica().getCnpjSemFormatacao());

        if (reg.getPessoa() instanceof PessoaJuridica) {
            evento.setCnpjBenef(((PessoaJuridica) reg.getPessoa()).getCnpjSemFormatacao());
            evento.setIsenImun(getIsenImunValido((PessoaJuridica) reg.getPessoa()));
        }

        HashMap<String, List<NotaReinf>> pagamentosPorNaturezaDeRendimento = montarMapPagamentosPorNatureza(reg.getNotas());
        for (Map.Entry<String, List<NotaReinf>> map : pagamentosPorNaturezaDeRendimento.entrySet()) {
            EventoR4020.IdePgto idePgto = evento.addIdePgto();
            idePgto.setNatRend(map.getKey());

            map.getValue().forEach(notaReinf -> {
                EventoR4020.IdePgto.InfoPgto infoPgto = idePgto.addInfoPgto();
                infoPgto.setDtFG(notaReinf.getPagamento().getDataPagamento());
                infoPgto.setVlrBruto(new DecimalFormat("##0.00").format(notaReinf.getPagamento().getValor()));
                infoPgto.setObserv(notaReinf.getPagamento().getComplementoHistorico().length() > 200 ? notaReinf.getPagamento().getComplementoHistorico().substring(0, 200) : notaReinf.getPagamento().getComplementoHistorico());
                infoPgto.setVlrBaseIR(new DecimalFormat("##0.00").format(notaReinf.getNota().getValorBaseCalculoIrrf()));
                infoPgto.setVlrIR(new DecimalFormat("##0.00").format(notaReinf.getNota().getValorRetidoIrrf()));
            });
        }
        return evento;
    }

    private HashMap<String, List<NotaReinf>> montarMapPagamentosPorNatureza(List<NotaReinf> notasReinf) {
        HashMap<String, List<NotaReinf>> retorno = new HashMap<>();
        notasReinf.forEach(notaReinf -> {
            if (notaReinf.getPagamento() != null) {
                String natRend = getNaturezaRendimento(notaReinf.getPagamento());
                if (natRend != null && !natRend.isEmpty()) {
                    if (!retorno.containsKey(natRend)) {
                        retorno.put(natRend, Lists.newArrayList());
                    }
                    retorno.get(natRend).add(notaReinf);
                }
            }
        });
        return retorno;
    }

    private String getNaturezaRendimento(Pagamento pagamento) {
        if (pagamento != null && pagamento.getLiquidacao() != null && pagamento.getLiquidacao().getDoctoFiscais() != null && !pagamento.getLiquidacao().getDoctoFiscais().isEmpty()) {
            return pagamento.getLiquidacao().getDoctoFiscais().get(0).getNaturezaRendimentoReinf().getCodigo();
        }
        return "";
    }

    private Integer getIsenImunValido(PessoaJuridica pj) {
        if (pj.getCodigoIsencaoImunidade() != null && (pj.getCodigoIsencaoImunidade() == 2 || pj.getCodigoIsencaoImunidade() == 3)) {
            return pj.getCodigoIsencaoImunidade();
        }
        return null;
    }
}
