package br.com.webpublico.negocios.tributario.services;

import br.com.webpublico.entidades.Notificacao;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.tributario.regularizacaoconstrucao.ParametroRegularizacao;
import br.com.webpublico.enums.TipoNotificacao;
import br.com.webpublico.enums.tributario.TipoWebService;
import br.com.webpublico.negocios.ExercicioFacade;
import br.com.webpublico.negocios.ProcRegularizaConstrucaoFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.tributario.LeitorWsConfig;
import br.com.webpublico.negocios.tributario.regularizacaoconstrucao.ParametroRegularizacaoFacade;
import br.com.webpublico.negocios.tributario.sisobra.Comunicacao;
import br.com.webpublico.seguranca.NotificacaoService;
import br.com.webpublico.tributario.dto.sisobra.Alvara;
import br.com.webpublico.tributario.dto.sisobra.Habitese;
import br.com.webpublico.tributario.dto.sisobra.*;
import br.com.webpublico.tributario.dto.sisobra.enums.TipoAlvara;
import br.com.webpublico.tributario.dto.sisobra.enums.TipoHabitese;
import br.com.webpublico.tributario.dto.sisobra.enums.UnidadeMedida;
import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Lists;
import org.joda.time.MonthDay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import javax.annotation.PostConstruct;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class IntegracaoSisobraPrefService {

    private static final Logger logger = LoggerFactory.getLogger(IntegracaoSisobraPrefService.class);
    private ProcRegularizaConstrucaoFacade procRegularizaConstrucaoFacade;
    private ParametroRegularizacaoFacade parametroRegularizacaoFacade;
    private ExercicioFacade exercicioFacade;
    private LeitorWsConfig leitorWsConfig;
    private SistemaFacade sistemaFacade;

    @PostConstruct
    private void init() {
        try {
            procRegularizaConstrucaoFacade = (ProcRegularizaConstrucaoFacade) new InitialContext().lookup("java:module/ProcRegularizaConstrucaoFacade");
            parametroRegularizacaoFacade = (ParametroRegularizacaoFacade) new InitialContext().lookup("java:module/ParametroRegularizacaoFacade");
            exercicioFacade = (ExercicioFacade) new InitialContext().lookup("java:module/ExercicioFacade");
            leitorWsConfig = (LeitorWsConfig) new InitialContext().lookup("java:module/LeitorWsConfig");
            sistemaFacade = (SistemaFacade) new InitialContext().lookup("java:module/SistemaFacade");
        } catch (NamingException e) {
            logger.error("Erro ao iniciar service integração SisObra Pref {}", e);
        }
    }

    @Transactional(timeout = 10000, propagation = Propagation.REQUIRES_NEW)
    public void consultarAndProcessarIntegracaoSisObraPref() {
        try {
            ParametroRegularizacao parametroRegularizacao = parametroRegularizacaoFacade.buscarParametroRegularizacaoPorExercicio(exercicioFacade.getExercicioCorrente());
            if (parametroRegularizacao == null) {
                throw new RuntimeException("Parametro de Regularização não encontrado para o exercício corrente");
            } else if (MonthDay.now().getDayOfMonth() == parametroRegularizacao.getDiaDoMesWebService()) {
                ConfiguracaoWebService configuracaoPorTipoDaKeyCorrente = leitorWsConfig.getConfiguracaoPorTipoDaKeyCorrente(TipoWebService.SISOBRA, sistemaFacade.getUsuarioBancoDeDados());
                if (configuracaoPorTipoDaKeyCorrente == null) {
                    throw new RuntimeException("Configuração do WebService da Integração SisObra Pref não encontrada para a chave: " + sistemaFacade.getUsuarioBancoDeDados());
                }
                List<AlvaraConstrucao> alvaraConstrucaos = procRegularizaConstrucaoFacade.getAlvaraConstrucaoFacade().buscarAlvarasParaEnvioSisObra();
                List<Alvara> alvaras = Lists.newArrayList();
                List<br.com.webpublico.tributario.dto.sisobra.Habitese> habiteses = Lists.newArrayList();
                boolean jaAdicionouAreaPrincipal = false;
                for (AlvaraConstrucao alvaraConstrucao : alvaraConstrucaos) {
                    ServicosAlvaraConstrucao servicoPrincipal = null;
                    Alvara alvara = new Alvara();
                    for (ServicosAlvaraConstrucao servico : alvaraConstrucao.getServicos()) {
                        Area area;
                        if (!jaAdicionouAreaPrincipal && servico.getServicoConstrucao().getGeraHabitese()) {
                            servicoPrincipal = servico;
                            jaAdicionouAreaPrincipal = true;
                            area = new AreaPrincipal();
                            ((AreaPrincipal) area).setQtdTotalUnidadesBloco(alvaraConstrucao.getConstrucaoAlvara().getQuantidadePavimentos());
                            ((AreaPrincipal) area).setArea(servico.getArea());
                        } else {
                            area = new AreaComplementar();
                            /* TODO SERA PREENCHIDO DEPOIS, NÃO SABEMOS O QUE COLOCAR NOS CAMPOS AINDA
                                ((AreaComplementar)area).setAreaCoberta();
                                ((AreaComplementar)area).setAreaDescoberta();
                            */
                        }
                        /* TODO SERA PREENCHIDO DEPOIS, NÃO SABEMOS O QUE COLOCAR NOS CAMPOS AINDA
                            area.setTipoObra();
                            area.setDestinacao();
                            area.setCategoria();*/
                        alvara.getArea().add(area);
                    }
                    alvara.setDataAlvara(alvaraConstrucao.getDataExpedicao());
                    alvara.setDataInicioObra(alvaraConstrucao.getProcRegularizaConstrucao().getDataInicioObra());
                    alvara.setDataFinalObra(alvaraConstrucao.getProcRegularizaConstrucao().getDataFimObra());
                    alvara.setId(alvaraConstrucao.getCodigo().toString());
                    alvara.setNumeroAlvara(alvaraConstrucao.getCodigo().toString());
                    alvara.setTipoAlvara(TipoAlvara.INICIAL);
                    alvara.setUnidadeMedida(UnidadeMedida.M2); //TODO
                    /* TODO SERA PREENCHIDO DEPOIS, NÃO SABEMOS O QUE COLOCAR NOS CAMPOS AINDA
                        alvara.setNomeObra();
                     */

                    EnderecoObra enderecoObra = new EnderecoObra();
                    Lote lote = alvaraConstrucao.getProcRegularizaConstrucao().getCadastroImobiliario().getLote();
                    Face face = alvaraConstrucao.getProcRegularizaConstrucao().getCadastroImobiliario().getLote().getTestadaPrincipal().getFace();
                    enderecoObra.setBairro(face.getLogradouroBairro().getBairro().getDescricao());
                    enderecoObra.setLogradouro(face.getLogradouroBairro().getLogradouro().getNome());
                    enderecoObra.setNumero(alvaraConstrucao.getProcRegularizaConstrucao().getCadastroImobiliario().getNumero());
                    enderecoObra.setTipoLogradouro(face.getLogradouroBairro().getLogradouro().getTipoLogradouro().getDescricao());
                    enderecoObra.setCep(face.getLogradouroBairro().getCep());
                    enderecoObra.setComplemento(lote.getComplemento());
                    alvara.setEnderecoObra(enderecoObra);

                    ResponsavelExecucaoObra responsavelExecucaoObra = new ResponsavelExecucaoObra();
                    /* TODO SERA PREENCHIDO DEPOIS, NÃO SABEMOS O QUE COLOCAR NOS CAMPOS AINDA
                        //preencher apenas um dos campos
                        responsavelExecucaoObra.setConsorcio();
                        responsavelExecucaoObra.setConstrucaoNomeColetivo();
                        responsavelExecucaoObra.setDonoDaObra();//não pode ser o proprietario do imóvel
                        responsavelExecucaoObra.setEmpresaConstrutora();
                        responsavelExecucaoObra.setEmpresaLiderConsorcio();
                        responsavelExecucaoObra.setIncorporadorConstrucaoCivil();
                        responsavelExecucaoObra.setPropritarioDoImovel();*/
                    alvara.setResponsavelExecucaoObra(responsavelExecucaoObra);

                    InformacoesAdicionais informacoesAdicionais = new InformacoesAdicionais();
                    informacoesAdicionais.setNumeroProcesso(alvaraConstrucao.getProcRegularizaConstrucao().getCodigo().toString());
                    if (servicoPrincipal != null) {
                        informacoesAdicionais.setClasse(servicoPrincipal.getHabiteseClassesConstrucao().getDescricao());
                    }
                    ResponsavelTecnico responsavelTecnico = null;
                    switch (alvaraConstrucao.getResponsavelServico().getTipoEspecialidadeServico()) {
                        case ARQUITETO:
                        case ARQUITETO_E_URBANISTA:
                            responsavelTecnico = new Arquiteto();
                            ((Arquiteto) responsavelTecnico).setCau(alvaraConstrucao.getResponsavelServico().getCauCrea());
                            ((Arquiteto) responsavelTecnico).setRrt(alvaraConstrucao.getProcRegularizaConstrucao().getArtRrtResponsavelExecucao());
                            break;
                        case ENGENHEIRO_CIVIL:
                            responsavelTecnico = new Engenheiro();
                            ((Engenheiro) responsavelTecnico).setCrea(alvaraConstrucao.getResponsavelServico().getCauCrea());
                            ((Engenheiro) responsavelTecnico).setArt(alvaraConstrucao.getProcRegularizaConstrucao().getArtRrtResponsavelExecucao());
                            break;
                        case TECNOLOGO:
                            throw new RuntimeException("Tipo de responsável inválido, esperado ARQUITETO ou ENGENHEIRO");
                    }
                    responsavelTecnico.setNome(alvaraConstrucao.getResponsavelServico().getPessoa().getNome());
                    informacoesAdicionais.setResponsavelProjeto(responsavelTecnico);
                    /* TODO SERA PREENCHIDO DEPOIS, NÃO SABEMOS O QUE COLOCAR NOS CAMPOS AINDA
                        informacoesAdicionais.setResponsavelTecnico();
                        informacoesAdicionais.setEspecificacao();
                        informacoesAdicionais.setObs();
                    */
                    informacoesAdicionais.setSituacao(alvaraConstrucao.getSituacao().getDescricao());
                    alvara.setInfoAdicionais(informacoesAdicionais);

                    ProprietarioObra proprietarioObra = new ProprietarioObra();
                    if (!alvaraConstrucao.getProcRegularizaConstrucao().getCadastroImobiliario().getPropriedadeVigente().isEmpty()) {
                        Propriedade propriedade = alvaraConstrucao.getProcRegularizaConstrucao().getCadastroImobiliario().getPropriedadeVigente().get(0);
                        Pessoa pessoa = propriedade.getPessoa();
                        if (pessoa.isPessoaJuridica()) {
                            proprietarioObra.setCnpj(pessoa.getCpf_Cnpj());
                        } else {
                            proprietarioObra.setCpf(pessoa.getCpf_Cnpj());
                        }
                    } else {
                        throw new RuntimeException("Nenhuma propriedade vigente encontrada para o cadastro imobiliario");
                    }
                    alvara.setProprietarioObra(proprietarioObra);

                    for (br.com.webpublico.entidades.Habitese alvaraConstrucaoHabitese : alvaraConstrucao.getHabiteses()) {
                        Habitese habitese = new Habitese();
                        habitese.setDataFinalObra(alvaraConstrucao.getProcRegularizaConstrucao().getDataFimObra());
                        habitese.setDataAlvara(alvaraConstrucao.getDataExpedicao());
                        habitese.setDataHabitese(alvaraConstrucaoHabitese.getDataExpedicaoTermo());
                        habitese.setNumeroAlvara(alvaraConstrucao.getCodigo().toString());
                        habitese.setNumeroHabitese(alvaraConstrucaoHabitese.getCodigo().toString());
                        habitese.setId(alvaraConstrucaoHabitese.getCodigo().toString());
                        switch (alvaraConstrucaoHabitese.getCaracteristica().getTipoHabitese()){
                            case TOTAL:
                                habitese.setTipoHabitese(TipoHabitese.TOTAL);
                                break;
                            case PARCIAL:
                                habitese.setTipoHabitese(TipoHabitese.PARCIAL);
                                break;
                        }
                        habitese.setUnidadeMedida(UnidadeMedida.M2);
                        /* TODO SERA PREENCHIDO DEPOIS, NÃO SABEMOS O QUE COLOCAR NOS CAMPOS AINDA
                            habitese.setQtdTotalUnidadesBloco();
                            habitese.setObservacao();*/
                        AreaPrincipal area = new AreaPrincipal();
                        area.setArea(alvaraConstrucaoHabitese.getCaracteristica().getAreaConstruida());
                        area.setQtdTotalUnidadesBloco(alvaraConstrucaoHabitese.getCaracteristica().getQuantidadeDePavimentos());
                        /* TODO SERA PREENCHIDO DEPOIS, NÃO SABEMOS O QUE COLOCAR NOS CAMPOS AINDA
                            area.setTipoObra();
                            area.setDestinacao();
                            area.setCategoria();*/
                        habitese.setArea(area);
                        habiteses.add(habitese);
                    }
                    alvaras.add(alvara);
                }
                Comunicacao comunicacao = new Comunicacao(configuracaoPorTipoDaKeyCorrente.getUrl());
                try {
                    LoteAlvaraHabitese lote = new LoteAlvaraHabitese();
                    lote.setAlvara(alvaras);
                    lote.setHabitese(habiteses);
                    ResponseEntity<RetornoLoteAlvaraHabitese> retornoLoteAlvaraHabiteseResponseEntity = comunicacao.enviarLoteAlvaraHabitese(lote);
                    if (retornoLoteAlvaraHabiteseResponseEntity.getStatusCode().equals(HttpStatus.CREATED)) {
                        RetornoLoteAlvaraHabitese retorno = retornoLoteAlvaraHabiteseResponseEntity.getBody();
                    }
                } catch (HttpClientErrorException rest) {
                    throw new RuntimeException(rest.getResponseBodyAsString());
                }
            }
        } catch (Exception e) {
            criarNotificacaoFalhaIntegracao(e.getMessage());
        }
    }

    private void criarNotificacaoFalhaIntegracao(String msg) {
        Notificacao notificacao = new Notificacao();
        notificacao.setDescricao("Não foi possível realizar a integração com o SisObra Pref através do agendamento de tarefas. Erro: " + msg + ". Data: " + DataUtil.getDataFormatada(new Date()) + ".");
        notificacao.setGravidade(Notificacao.Gravidade.ERRO);
        notificacao.setTipoNotificacao(TipoNotificacao.INTEGRACAO_SISOBRA);
        notificacao.setTitulo(TipoNotificacao.INTEGRACAO_SISOBRA.getDescricao());
        notificacao.setLink("");
        NotificacaoService.getService().notificar(notificacao);
    }
}
