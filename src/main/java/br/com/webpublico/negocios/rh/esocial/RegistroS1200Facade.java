package br.com.webpublico.negocios.rh.esocial;


import br.com.webpublico.entidades.Entidade;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.PessoaFisica;
import br.com.webpublico.entidades.rh.esocial.ConfiguracaoEmpregadorESocial;
import br.com.webpublico.entidades.rh.esocial.RegistroESocial;
import br.com.webpublico.entidades.rh.esocial.RegistroEventoEsocial;
import br.com.webpublico.entidades.rh.esocial.VinculoFPEventoEsocial;
import br.com.webpublico.enums.rh.esocial.TipoApuracaoFolha;
import br.com.webpublico.enums.rh.esocial.TipoRegimePrevidenciario;
import br.com.webpublico.esocial.comunicacao.enums.TipoArquivoESocial;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ContratoFPFacade;
import br.com.webpublico.negocios.FichaRPAFacade;
import br.com.webpublico.negocios.FolhaDePagamentoFacade;
import br.com.webpublico.util.FacesUtil;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;
import org.jetbrains.annotations.NotNull;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Stateless
public class RegistroS1200Facade extends AbstractFacade<RegistroEventoEsocial> {

    @EJB
    private ConfiguracaoEmpregadorESocialFacade configuracaoEmpregadorESocialFacade;

    @EJB
    private ContratoFPFacade contratoFPFacade;

    @EJB
    private FolhaDePagamentoFacade folhaDePagamentoFacade;

    @EJB
    private FichaRPAFacade fichaRPAFacade;

    public ConfiguracaoEmpregadorESocialFacade getConfiguracaoEmpregadorESocialFacade() {
        return configuracaoEmpregadorESocialFacade;
    }

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public RegistroS1200Facade() {
        super(RegistroEventoEsocial.class);
    }

    @Override
    public RegistroEventoEsocial recuperar(Object id) {
        RegistroEventoEsocial registro = super.recuperar(id);
        Hibernate.initialize(registro.getItemVinculoFP());
        return registro;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<VinculoFPEventoEsocial> getVinculoComFolhaEfetivadaNaCompetencia(RegistroEventoEsocial registroEventoEsocial, Entidade empregador, PessoaFisica pessoa,
                                                                                 boolean somenteNaoEnviados, HierarquiaOrganizacional hierarquia) {
        ValidacaoException ve = new ValidacaoException();
        ConfiguracaoEmpregadorESocial configuracaoEmpregadorESocial = getConfiguracaoEmpregadorESocialFacade().recuperarPorEntidade(empregador);
        configuracaoEmpregadorESocial = getConfiguracaoEmpregadorESocialFacade().recuperar(configuracaoEmpregadorESocial.getId());
        List<Long> idsUnidade = hierarquia != null ? Lists.newArrayList(hierarquia.getSubordinada().getId()) : contratoFPFacade.montarIdOrgaoEmpregador(configuracaoEmpregadorESocial);

        if (configuracaoEmpregadorESocial == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não foi encontrado configuração para a Entidade " + empregador);
            ve.lancarException();
        } else {
            boolean semRescisao = true;
            List<VinculoFPEventoEsocial> prestadoresRPA = Lists.newArrayList();
            if (TipoApuracaoFolha.SALARIO_13.equals(registroEventoEsocial.getTipoApuracaoFolha())) {
                semRescisao = false;
            } else {
                prestadoresRPA = fichaRPAFacade.buscarPrestadorServicoPorMesAndAnoAndUnidades(registroEventoEsocial, pessoa, idsUnidade, TipoArquivoESocial.S1200, configuracaoEmpregadorESocial.getId());
            }
            Map<String, VinculoFPEventoEsocial> mapVinculosEsocial = new HashMap<>();

            List<VinculoFPEventoEsocial> vinculoFPS = folhaDePagamentoFacade.buscarVinculosPorTipoFolhaMesEAnoAndFolhaEfetivadaAndUnidadesS1200(registroEventoEsocial, idsUnidade,
                semRescisao, TipoRegimePrevidenciario.RGPS, pessoa, configuracaoEmpregadorESocial);

            if (vinculoFPS != null && !vinculoFPS.isEmpty()) {
                vinculoFPS.forEach(vinculo -> {
                    mapVinculosEsocial.put(vinculo.getCpf(), vinculo);
                });
            }

            if (prestadoresRPA != null && !prestadoresRPA.isEmpty()) {
                prestadoresRPA.forEach(prestador -> {
                    if (mapVinculosEsocial.containsKey(prestador.getCpf())) {
                        Long idVinculo = mapVinculosEsocial.get(prestador.getCpf()).getIdVinculo();
                        prestador.setIdVinculo(idVinculo);
                    }
                    mapVinculosEsocial.put(prestador.getCpf(), prestador);
                });
            }

            List<String> VinculosSemFichaCPF = Lists.newArrayList();
            for (VinculoFPEventoEsocial vinculoEsocial : mapVinculosEsocial.values()) {
                List<Long> idFichas = folhaDePagamentoFacade.buscarIdsFichasMensalOrDecimoS1200(registroEventoEsocial,
                    vinculoEsocial.getIdPessoa(), idsUnidade, null, TipoRegimePrevidenciario.RGPS);
                if (TipoApuracaoFolha.MENSAL.equals(registroEventoEsocial.getTipoApuracaoFolha())) {
                    idFichas.addAll(fichaRPAFacade.buscarIdsFichas(registroEventoEsocial, vinculoEsocial.getIdPessoa()));
                }
                if (idFichas.isEmpty()) {
                    logger.error("Pessoa sem ficha financeira ou ficha RPA: " + vinculoEsocial);
                    VinculosSemFichaCPF.add(vinculoEsocial.getCpf());
                }
                vinculoEsocial.setIdsFichas(idFichas);
            }

            for (String cpf : VinculosSemFichaCPF) {
                mapVinculosEsocial.remove(cpf);
            }

            if (!mapVinculosEsocial.isEmpty()) {
                if (!somenteNaoEnviados) {
                    return Lists.newArrayList(mapVinculosEsocial.values());
                } else {
                    return buscarVinculosQueNaoForamEnviados(registroEventoEsocial, configuracaoEmpregadorESocial,
                        mapVinculosEsocial);
                }
            }
        }
        FacesUtil.addOperacaoNaoRealizada("Não foi encontrado servidor(res) com folha de pagamento calculada na competência e CNPJ/Entidade informados!");
        pessoa = null;
        return null;
    }

    @NotNull
    private List<VinculoFPEventoEsocial> buscarVinculosQueNaoForamEnviados(@NotNull RegistroEventoEsocial registroEventoEsocial,
                                                                           ConfiguracaoEmpregadorESocial configuracaoEmpregadorESocial,
                                                                           Map<String, VinculoFPEventoEsocial> mapVinculosEsocial) {
        List<VinculoFPEventoEsocial> vinculoPrimeiroEnvio = Lists.newArrayList();
        List<RegistroESocial> registrosEnviados = Lists.newArrayList();
        if (registroEventoEsocial.getMes() != null) {
            registrosEnviados = folhaDePagamentoFacade.
                recuperarRegistroEsocialAnoMesEmpregador(TipoArquivoESocial.S1200,
                    registroEventoEsocial.getMes().getNumeroMes(), registroEventoEsocial.getExercicio().getAno(),
                    configuracaoEmpregadorESocial);
        } else {
            registrosEnviados = folhaDePagamentoFacade.
                recuperarRegistroEsocialAnoEmpregador(TipoArquivoESocial.S1200, registroEventoEsocial.getExercicio().getAno(),
                    configuracaoEmpregadorESocial);
        }

        if (registrosEnviados == null || registrosEnviados.isEmpty()) {
            return Lists.newArrayList(mapVinculosEsocial.values());
        }
        for (VinculoFPEventoEsocial vinculoEsocial : mapVinculosEsocial.values()) {
            boolean enviado = false;
            for (RegistroESocial registrosEnviado : registrosEnviados) {
                if (!vinculoEsocial.getIdsFichas().isEmpty() && vinculoEsocial.getIdsFichas().get(0).toString().contains(registrosEnviado.getIdentificadorWP())) {
                    enviado = true;
                    break;
                }
            }
            if (!enviado) {
                vinculoPrimeiroEnvio.add(vinculoEsocial);
            }
        }
        return vinculoPrimeiroEnvio;
    }
}
