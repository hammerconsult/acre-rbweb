package br.com.webpublico.esocial.service;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.esocial.ConfiguracaoEmpregadorESocial;
import br.com.webpublico.entidades.rh.esocial.EventoFPEmpregador;
import br.com.webpublico.entidades.rh.esocial.RegistroEventoEsocial;
import br.com.webpublico.enums.TipoEventoFP;
import br.com.webpublico.enums.rh.esocial.TipoApuracaoFolha;
import br.com.webpublico.esocial.comunicacao.eventos.periodicos.EventoS1207;
import br.com.webpublico.esocial.domain.EmpregadorESocial;
import br.com.webpublico.esocial.dto.EventosESocialDTO;
import br.com.webpublico.esocial.enums.ClasseWP;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.EventoFPFacade;
import br.com.webpublico.negocios.FichaFinanceiraFPFacade;
import br.com.webpublico.negocios.PessoaFisicaFacade;
import br.com.webpublico.negocios.rh.esocial.ConfiguracaoEmpregadorESocialFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Date;
import java.util.List;

import static br.com.webpublico.util.StringUtil.retornaApenasNumeros;

@Service(value = "s1207Service")
public class S1207Service {
    protected static final Logger logger = LoggerFactory.getLogger(S1207Service.class);

    private ConfiguracaoEmpregadorESocialFacade configuracaoEmpregadorESocialFacade;
    private PessoaFisicaFacade pessoaFisicaFacade;
    private FichaFinanceiraFPFacade fichaFinanceiraFPFacade;
    private EventoFPFacade eventoFPFacade;

    @Autowired
    private ESocialService eSocialService;

    @PostConstruct
    public void init() {
        try {
            configuracaoEmpregadorESocialFacade = (ConfiguracaoEmpregadorESocialFacade) new InitialContext().lookup("java:module/ConfiguracaoEmpregadorESocialFacade");
            pessoaFisicaFacade = (PessoaFisicaFacade) new InitialContext().lookup("java:module/PessoaFisicaFacade");
            fichaFinanceiraFPFacade = (FichaFinanceiraFPFacade) new InitialContext().lookup("java:module/FichaFinanceiraFPFacade");
            eventoFPFacade = (EventoFPFacade) new InitialContext().lookup("java:module/EventoFPFacade");
        } catch (NamingException e) {
            logger.error("Não foi possivel criar a instancia: " + e.getMessage());
        } catch (Exception ex) {
            logger.error("Não foi possivel criar a instancia: " + ex.getMessage());
        }
    }

    public void enviarS1207(RegistroEventoEsocial registroEventoEsocial, VinculoFP vinculoFP, Long idFicha) {
        ValidacaoException val = new ValidacaoException();
        ConfiguracaoEmpregadorESocial config = configuracaoEmpregadorESocialFacade.recuperarPorEntidade(registroEventoEsocial.getEntidade());
        EventoS1207 s1207 = criarEventoS1207(registroEventoEsocial, vinculoFP, val, idFicha, config);
        logger.debug("Antes de Enviar: " + s1207.getXml());
        val.lancarException();
        eSocialService.enviarEventoS1207(s1207);
    }

    private EventoS1207 criarEventoS1207(RegistroEventoEsocial registroEventoEsocial, VinculoFP vinculoFP, ValidacaoException val, Long idFicha, ConfiguracaoEmpregadorESocial config) {
        EmpregadorESocial empregador = eSocialService.getEmpregadorPorCnpj(retornaApenasNumeros(registroEventoEsocial.getEntidade().getPessoaJuridica().getCnpj()));
        EventosESocialDTO.S1207 eventoS1207 = (EventosESocialDTO.S1207) eSocialService.getEventoS1207(empregador);

        String mes = registroEventoEsocial.getMes().getNumeroMes().toString();
        String ano = registroEventoEsocial.getExercicio().getAno().toString();
        if (idFicha != null) {
            eventoS1207.setClasseWP(ClasseWP.FICHAFINANCEIRAFP_OU_RPA);
            eventoS1207.setIdentificadorWP(idFicha.toString());
        }

        if (TipoApuracaoFolha.MENSAL.equals(registroEventoEsocial.getTipoApuracaoFolha())) {
            eventoS1207.setIdESocial(TipoApuracaoFolha.MENSAL.name().concat(ano).concat(mes).concat(vinculoFP.getId().toString()));
        } else {
            eventoS1207.setIdESocial(TipoApuracaoFolha.SALARIO_13.name().concat(ano).concat(mes).concat(vinculoFP.getId().toString()));
        }

        eventoS1207.setIdESocial(registroEventoEsocial.getTipoApuracaoFolha().name().concat(ano).concat(mes).concat(vinculoFP.getId().toString()));
        eventoS1207.setIndApuracao(registroEventoEsocial.getTipoApuracaoFolha().equals(TipoApuracaoFolha.SALARIO_13) ? 2 : 1);
        if (TipoApuracaoFolha.SALARIO_13.equals(registroEventoEsocial.getTipoApuracaoFolha())) {
            eventoS1207.setPerApur(registroEventoEsocial.getExercicio().getAno());
        } else {
            eventoS1207.setPerApur(registroEventoEsocial.getExercicio().getAno(), registroEventoEsocial.getMes().getNumeroMes());
        }
        adicionarInformacoesBasicas(eventoS1207, vinculoFP, val);
        adicionarVerbas(eventoS1207, vinculoFP, idFicha, registroEventoEsocial, val, config);
        return eventoS1207;
    }

    private void adicionarInformacoesBasicas(EventosESocialDTO.S1207 eventoS1207, VinculoFP vinculoFP, ValidacaoException val) {
        PessoaFisica pessoa = vinculoFP.getMatriculaFP().getPessoa();
        pessoa = pessoaFisicaFacade.recuperarComDocumentos(pessoa.getId());
        eventoS1207.setCpfTrab(StringUtil.retornaApenasNumeros(pessoa.getCpf()));
        eventoS1207.setCpfBenef(StringUtil.retornaApenasNumeros(pessoa.getCpf()));
        if (pessoa.getCarteiraDeTrabalho() == null) {
            val.adicionarMensagemDeOperacaoNaoRealizada("A pessoa " + pessoa + " não possui carteira de trabalho.");
        } else {
            eventoS1207.setNisTrab(pessoa.getCarteiraDeTrabalho().getPisPasep());
        }
    }

    private List<ItemFichaFinanceiraFP> getItemFichaFinanceiraFP(Long idFichaFinanceiraFP) {
        FichaFinanceiraFP ficha = fichaFinanceiraFPFacade.recuperar(idFichaFinanceiraFP);
        return ficha.getItemFichaFinanceiraFP();
    }

    private void adicionarVerbas(EventosESocialDTO.S1207 eventoS1207, VinculoFP vinculo, Long idFichaFinanceiraFP,
                                 RegistroEventoEsocial registroEventoEsocial, ValidacaoException val,
                                 ConfiguracaoEmpregadorESocial config) {
        Date dataReferencia = DataUtil.criarDataComMesEAno(registroEventoEsocial.getMes().getNumeroMes(), registroEventoEsocial.getExercicio().getAno()).toDate();

        EventoS1207.DmDev dmDev = eventoS1207.addDmDev();
        if (((Aposentadoria) vinculo).getNumeroBeneficioPrevidenciario() != null) {
            dmDev.setNrBeneficio(((Aposentadoria) vinculo).getNumeroBeneficioPrevidenciario());
        } else {
            val.adicionarMensagemDeOperacaoNaoPermitida(vinculo + " A Aposentadoria não possui Número do Benefício Previdenciário.");
            return;
        }
        dmDev.setIdeDmDev(idFichaFinanceiraFP.toString());

        List<ItemFichaFinanceiraFP> itemFIcha = getItemFichaFinanceiraFP(idFichaFinanceiraFP);

        if (itemFIcha != null && !itemFIcha.isEmpty()) {
            eventoS1207.setDescricao(itemFIcha.get(0).getFichaFinanceiraFP().getVinculoFP().toString());
        }

        EventoS1207.DmDev.IdeEstab ideEstab = dmDev.addIdeEstab();
        ideEstab.setTpInsc(config.getClassificacaoTributaria().getTpInsc());
        ideEstab.setNrInsc(retornaApenasNumeros(config.getEntidade().getPessoaJuridica().getCnpj()));

        for (ItemFichaFinanceiraFP itemFichaFinanceiraFP : itemFIcha) {
            EventoFPEmpregador eventoFPEmpregador = eventoFPFacade.getEventoFPEmpregador(itemFichaFinanceiraFP.getEventoFP(), dataReferencia, registroEventoEsocial.getEntidade());
            TipoEventoFP tipoEventoFP = itemFichaFinanceiraFP.getEventoFP().getTipoEventoFP();
            if (eventoFPEmpregador == null) {
                if (TipoEventoFP.INFORMATIVO.equals(tipoEventoFP) || TipoEventoFP.INFORMATIVO_DEDUTORA.equals(tipoEventoFP)) {
                    continue;
                }
                val.adicionarMensagemDeOperacaoNaoPermitida("O evento de código: " + itemFichaFinanceiraFP.getEventoFP().getCodigo() + ", não tem um Empregador vigente na aba e-social.");
                continue;
            }
            if (Util.isStringNulaOuVazia(eventoFPEmpregador.getIdentificacaoTabela())) {
                val.adicionarMensagemDeOperacaoNaoPermitida("O evento de código: " + itemFichaFinanceiraFP.getEventoFP().getCodigo() + ", não tem a idenficação da tabela informado.");
                continue;
            }

            EventoS1207.DmDev.IdeEstab.ItensRemun remunPerApur = ideEstab.addRemunPerApur();

            remunPerApur.setCodRubr(itemFichaFinanceiraFP.getEventoFP().getCodigo());
            remunPerApur.setIdeTabRubr(eventoFPEmpregador.getIdentificacaoTabela());
            remunPerApur.setVrRubr(itemFichaFinanceiraFP.getValor());
            remunPerApur.setIndApurIR(0);
        }
    }
}
