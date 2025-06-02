package br.com.webpublico.reinf.service;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.contabil.reinf.FiltroReinf;
import br.com.webpublico.entidades.contabil.reinf.NotaReinf;
import br.com.webpublico.entidades.contabil.reinf.ReceitaExtraReinf;
import br.com.webpublico.entidades.contabil.reinf.RegistroEventoRetencaoReinf;
import br.com.webpublico.entidades.rh.esocial.ConfiguracaoEmpregadorESocial;
import br.com.webpublico.entidadesauxiliares.AssistenteSincronizacaoReinf;
import br.com.webpublico.esocial.domain.EmpregadorESocial;
import br.com.webpublico.esocial.service.ESocialService;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.ConfiguracaoContabilFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.contabil.reinf.ReinfFacade;
import br.com.webpublico.reinf.eventos.EventosReinfDTO;
import br.com.webpublico.reinf.eventos.domain.EventoR2010;
import br.com.webpublico.reinf.eventos.TipoArquivoReinf;
import br.com.webpublico.reinf.eventos.domain.EventoR2020;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.StringUtil;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import static br.com.webpublico.util.StringUtil.retornaApenasNumeros;

@Service(value = "r2020Service")
public class R2020Service {
    protected static final Logger logger = LoggerFactory.getLogger(R2020Service.class);
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
        List<ConfiguracaoContabilContaReinf> contas = configuracaoContabilFacade.buscarContasReinfVigentesPorTipoArquivo(TipoArquivoReinf.R2020);
        if (contas.isEmpty()) {
            throw new ExcecaoNegocioGenerica("Nenhum conta configurada para as retenções do REINF foi encontrada na configuração contábil para o Tipo de Arquivo REINF " + TipoArquivoReinf.R2020.getDescricao() + ".");
        }
        List<RegistroEventoRetencaoReinf> receitas = reinfFacade.buscarReceitasExtrasR2020(config, filtroReinf, contas);
        return receitas != null ? receitas : Lists.newArrayList();
    }

    public void enviar(AssistenteSincronizacaoReinf assistente, EventoR2020 r2020) {
        if (assistente.getSelecionado().getUtilizarVersao2_1()) {
            reinfService.enviarEventoR2020V2(r2020);
        } else {
            reinfService.enviarEventoR2020(r2020);
        }
    }

    public List<EventoR2020> criarEventosR2020(AssistenteSincronizacaoReinf assistente, List<RegistroEventoRetencaoReinf> objs) {
        List<EventoR2020> retorno = Lists.newArrayList();
        ValidacaoException val = new ValidacaoException();
        if (objs != null) {
            this.empregadorESocial = null;
            for (RegistroEventoRetencaoReinf obj : objs) {
                if (obj.isValido() && obj.getMarcarPraEnviar()) {
                    EventoR2020 r1070 = criarEventoR2020(assistente, obj, val);
                    if (r1070 != null) {
                        logger.info("XML " + r1070.getXml());
                        //val.lancarException();
                        // reinfService.enviarEventoR1000(r1000);
                        reinfFacade.salvarRegistroEvento(obj);
                        retorno.add(r1070);
                    }
                }
            }
        }
        return retorno;
    }

    private EventoR2020 criarEventoR2020(AssistenteSincronizacaoReinf assistente, RegistroEventoRetencaoReinf reg, ValidacaoException val) {
        ConfiguracaoEmpregadorESocial config = assistente.getConfiguracaoEmpregadorESocial();
        if (empregadorESocial == null) {
            empregadorESocial = reinfService.getEmpregadorPorCnpj(retornaApenasNumeros(config.getEntidade().getPessoaJuridica().getCnpj()));
        }
        if (assistente.getSelecionado().getUtilizarVersao2_1()) {
            return criarEvento2020V2(reg, config);
        } else {
            return criarEvento2020(reg, config);
        }
    }

    private EventosReinfDTO.R2020 criarEvento2020(RegistroEventoRetencaoReinf reg, ConfiguracaoEmpregadorESocial config) {
        EventosReinfDTO.R2020 evento = (EventosReinfDTO.R2020) reinfService.getEventoR2020(empregadorESocial);

        if (reinfService.isPerfilDev()) {
            evento.setTpAmb(2);
        }
        Integer mes = DataUtil.getMes(reg.getData());
        Integer ano = DataUtil.getAno(reg.getData());
        evento.setIdESocial(reg.getPessoa().getId().toString().concat(ano.toString()).concat(mes.toString()));
        if (reg.getRetificacao()) {
            evento.setIndRetif(2);
            evento.setNrRecibo(reg.getNumeroReciboOrigem());
        } else {
            evento.setIndRetif(1);
        }

        evento.setTpInsc(1);
        evento.setNrInsc(empregadorESocial.getNrInscID());
        evento.setPerApur(ano, mes);

        evento.setTpInscEstabPrest(1);
        evento.setNrInscrEstabPrest(config.getEntidade().getPessoaJuridica().getCnpjSemFormatacao());

        if (reg.getPessoa() instanceof PessoaJuridica) {
            evento.setTpInscTomador(1);
            evento.setNrInscrTomador(((PessoaJuridica) reg.getPessoa()).getCnpjSemFormatacao());
            evento.setIndObra(reg.getPessoa().getReceitaBrutaCPRB() ? 1 : 0);
        }
        BigDecimal totalBaseCalculo = BigDecimal.ZERO;
        for (NotaReinf notaReinf : reg.getNotas()) {
            LiquidacaoDoctoFiscal nota = notaReinf.getNota();
            EventoR2020.Nfs nfs = evento.addNfs();
            nfs.setSerie(nota.getDoctoFiscalLiquidacao().getSerie());
            nfs.setNumDoc(StringUtil.removeCaracteresEspeciaisSemEspaco(nota.getDoctoFiscalLiquidacao().getNumero()));
            nfs.setDtEmissaoNF(nota.getDoctoFiscalLiquidacao().getDataDocto());
            nfs.setVlrBruto(notaReinf.getValorLiquido());
            if (nota.getTipoServicoReinf() != null) {

                EventoR2020.Nfs.InfoTpServ infoTpServ = nfs.addServico();
                infoTpServ.setTpService(new BigInteger(nota.getTipoServicoReinf().getCodigo()));
                infoTpServ.setVlrBaseRet(nota.getValorLiquidado());
                infoTpServ.setVlrRetencao(notaReinf.getValorRetido());
            }
            totalBaseCalculo = totalBaseCalculo.add(nota.getValorBaseCalculo());
        }

        evento.setVlrTotalBruto(reg.getValorTotalBruto());
        evento.setVlrTotalBaseRet(totalBaseCalculo);
        evento.setVlrTotalRetPrinc(reg.getValorTotalRetencao());
        return evento;
    }

    private EventosReinfDTO.R2020V2 criarEvento2020V2(RegistroEventoRetencaoReinf reg, ConfiguracaoEmpregadorESocial config) {
        EventosReinfDTO.R2020V2 evento = (EventosReinfDTO.R2020V2) reinfService.getEventoR2020V2(empregadorESocial);

        Integer mes = DataUtil.getMes(reg.getData());
        Integer ano = DataUtil.getAno(reg.getData());
        evento.setIdESocial(reg.getPessoa().getId().toString().concat(ano.toString()).concat(mes.toString()));
        if (reg.getRetificacao()) {
            evento.setIndRetif(2);
            evento.setNrRecibo(reg.getNumeroReciboOrigem());
        } else {
            evento.setIndRetif(1);
        }

        evento.setTpInsc(1);
        evento.setNrInsc(empregadorESocial.getNrInscID());
        evento.setPerApur(ano, mes);

        if (reinfService.isPerfilDev()) {
            evento.setTpAmb(2);
        }
        evento.setTpInscEstabPrest(1);
        evento.setNrInscrEstabPrest(config.getEntidade().getPessoaJuridica().getCnpjSemFormatacao());

        if (reg.getPessoa() instanceof PessoaJuridica) {
            evento.setTpInscTomador(1);
            evento.setNrInscrTomador(((PessoaJuridica) reg.getPessoa()).getCnpjSemFormatacao());
            evento.setIndObra(reg.getPessoa().getReceitaBrutaCPRB() ? 1 : 0);
        }
        BigDecimal totalBaseCalculo = BigDecimal.ZERO;
        for (NotaReinf notaReinf : reg.getNotas()) {
            LiquidacaoDoctoFiscal nota = notaReinf.getNota();
            EventoR2020.Nfs nfs = evento.addNfs();
            nfs.setSerie(nota.getDoctoFiscalLiquidacao().getSerie());
            nfs.setNumDoc(StringUtil.removeCaracteresEspeciaisSemEspaco(nota.getDoctoFiscalLiquidacao().getNumero()));
            nfs.setDtEmissaoNF(nota.getDoctoFiscalLiquidacao().getDataDocto());
            nfs.setVlrBruto(notaReinf.getValorLiquido());
            if (nota.getTipoServicoReinf() != null) {

                EventoR2020.Nfs.InfoTpServ infoTpServ = nfs.addServico();
                infoTpServ.setTpService(new BigInteger(nota.getTipoServicoReinf().getCodigo()));
                infoTpServ.setVlrBaseRet(nota.getValorLiquidado());
                infoTpServ.setVlrRetencao(notaReinf.getValorRetido());
            }
            totalBaseCalculo = totalBaseCalculo.add(nota.getValorBaseCalculo());
        }

        evento.setVlrTotalBruto(reg.getValorTotalBruto());
        evento.setVlrTotalBaseRet(totalBaseCalculo);
        evento.setVlrTotalRetPrinc(reg.getValorTotalRetencao());
        return evento;
    }
}
