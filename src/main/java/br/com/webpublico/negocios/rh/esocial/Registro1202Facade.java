package br.com.webpublico.negocios.rh.esocial;

import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.PessoaFisica;
import br.com.webpublico.entidades.rh.esocial.ConfiguracaoEmpregadorESocial;
import br.com.webpublico.entidades.rh.esocial.RegistroESocial;
import br.com.webpublico.entidades.rh.esocial.RegistroEventoEsocial;
import br.com.webpublico.entidades.rh.esocial.VinculoFPEventoEsocial;
import br.com.webpublico.enums.rh.esocial.TipoRegimePrevidenciario;
import br.com.webpublico.esocial.comunicacao.enums.TipoArquivoESocial;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.FacesUtil;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;
import org.jetbrains.annotations.NotNull;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class Registro1202Facade extends AbstractFacade<RegistroEventoEsocial> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @EJB
    private ConfiguracaoEmpregadorESocialFacade configuracaoEmpregadorESocialFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private FolhaDePagamentoFacade folhaDePagamentoFacade;
    @EJB
    private VinculoFPFacade vinculoFPFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private PrestadorServicosFacade prestadorServicosFacade;

    public Registro1202Facade() {
        super(RegistroEventoEsocial.class);
    }

    @Override
    public RegistroEventoEsocial recuperar(Object id) {
        RegistroEventoEsocial registro = em.find(RegistroEventoEsocial.class, id);
        Hibernate.initialize(registro.getItemVinculoFP());
        return registro;
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public ConfiguracaoEmpregadorESocialFacade getConfiguracaoEmpregadorESocialFacade() {
        return configuracaoEmpregadorESocialFacade;
    }

    public ContratoFPFacade getContratoFPFacade() {
        return contratoFPFacade;
    }

    public FolhaDePagamentoFacade getFolhaDePagamentoFacade() {
        return folhaDePagamentoFacade;
    }

    public VinculoFPFacade getVinculoFPFacade() {
        return vinculoFPFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public PrestadorServicosFacade getPrestadorServicosFacade() {
        return prestadorServicosFacade;
    }

    public List<VinculoFPEventoEsocial> getVinculoFPEventoEsocial(RegistroEventoEsocial registroEventoEsocial,
                                                                  PessoaFisica pessoaVinculo, boolean apenasNaoEnviados,
                                                                  HierarquiaOrganizacional hierarquia) {
        try {
            ConfiguracaoEmpregadorESocial configuracaoEmpregadorESocial = getConfiguracaoEmpregadorESocialFacade().recuperarPorEntidade(registroEventoEsocial.getEntidade());
            validarConfigEmpregadorESocial(configuracaoEmpregadorESocial, registroEventoEsocial);
            configuracaoEmpregadorESocial = getConfiguracaoEmpregadorESocialFacade().recuperar(configuracaoEmpregadorESocial.getId());

            List<Long> idsUnidade = hierarquia != null ? Lists.newArrayList(hierarquia.getSubordinada().getId()) : getContratoFPFacade().montarIdOrgaoEmpregador(configuracaoEmpregadorESocial);

            List<VinculoFPEventoEsocial> vinculoFPS = getFolhaDePagamentoFacade().buscarVinculosTipoPrevidenciaRppsSemRescisaoNoMesSelecionado(
                registroEventoEsocial, idsUnidade, pessoaVinculo, TipoArquivoESocial.S1202, configuracaoEmpregadorESocial);
            if (vinculoFPS != null && !vinculoFPS.isEmpty()) {
                for (VinculoFPEventoEsocial fp : vinculoFPS) {
                    fp.setIdsFichas(folhaDePagamentoFacade.buscarIdsFichasMensalOrDecimo(registroEventoEsocial, fp.getIdPessoa(), idsUnidade, fp.getVinculoFP(), TipoRegimePrevidenciario.RPPS));
                }

                if (apenasNaoEnviados) {
                    return buscarVinculosQueNaoForamEnviados(registroEventoEsocial, configuracaoEmpregadorESocial, vinculoFPS);
                }
                return vinculoFPS;
            }
            FacesUtil.addOperacaoNaoRealizada("Nenhum Servidor encontrado para os filtros informados.");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
        return null;
    }


    @NotNull
    private List<VinculoFPEventoEsocial> buscarVinculosQueNaoForamEnviados(RegistroEventoEsocial registroEventoEsocial,
                                                                           ConfiguracaoEmpregadorESocial configuracaoEmpregadorESocial,
                                                                           List<VinculoFPEventoEsocial> vinculoFPS) {
        List<VinculoFPEventoEsocial> vinculoPrimeiroEnvio = Lists.newArrayList();

        List<RegistroESocial> registrosEnviados = Lists.newArrayList();
        if (registroEventoEsocial.getMes() != null) {
            registrosEnviados = folhaDePagamentoFacade.
                recuperarRegistroEsocialAnoMesEmpregador(TipoArquivoESocial.S1202,
                    registroEventoEsocial.getMes().getNumeroMes(), registroEventoEsocial.getExercicio().getAno(),
                    configuracaoEmpregadorESocial);
        } else {
            registrosEnviados = folhaDePagamentoFacade.
                recuperarRegistroEsocialAnoEmpregador(TipoArquivoESocial.S1202, registroEventoEsocial.getExercicio().getAno(),
                    configuracaoEmpregadorESocial);
        }

        if (registrosEnviados == null || registrosEnviados.isEmpty()) {
            return Lists.newArrayList(vinculoFPS);
        }
        for (VinculoFPEventoEsocial vinculoEsocial : vinculoFPS) {
            boolean enviado = false;
            for (RegistroESocial registrosEnviado : registrosEnviados) {
                if (vinculoEsocial.getIdsFichas().get(0).toString().contains(registrosEnviado.getIdentificadorWP())) {
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

    private void validarConfigEmpregadorESocial(ConfiguracaoEmpregadorESocial configuracaoEmpregadorESocial, RegistroEventoEsocial registroEventoEsocial) {
        ValidacaoException ve = new ValidacaoException();
        if (ve.getMensagens().isEmpty()) {
            if (configuracaoEmpregadorESocial == null) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Não foi encontrado configuração para a Entidade " + registroEventoEsocial.getEntidade());
            }
        }
        ve.lancarException();
    }
}
