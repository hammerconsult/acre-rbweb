package br.com.webpublico.negocios.contabil.execucao;


import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.contabil.ReformaAdministrativa;
import br.com.webpublico.entidades.contabil.ReformaAdministrativaUnidade;
import br.com.webpublico.entidades.contabil.ReformaAdministrativaUnidadeUsuario;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class ReformaAdministrativaFacade extends AbstractFacade<ReformaAdministrativa> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private DocumentoOficialFacade documentoOficialFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private UnidadeOrganizacionalFacade unidadeOrganizacionalFacade;
    @EJB
    private UsuarioSistemaFacade usuarioSistemaFacade;

    public ReformaAdministrativaFacade() {
        super(ReformaAdministrativa.class);
    }


    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public ReformaAdministrativa recuperar(Object id) {
        if (id != null) {
            ReformaAdministrativa ra = em.find(ReformaAdministrativa.class, id);
            ra.getUnidades().size();
            if (!ra.getUnidades().isEmpty()) {
                for (ReformaAdministrativaUnidade reformaUnidade : ra.getUnidades()) {
                    reformaUnidade.getUsuarios().size();
                }
            }
            return ra;
        }
        return null;
    }

    @Override
    public ReformaAdministrativa salvarRetornando(ReformaAdministrativa entity) {
        if (entity.getNumero() == null) {
            entity.setNumero(singletonGeradorCodigo.getProximoCodigo(ReformaAdministrativa.class, "numero"));
        }
        return super.salvarRetornando(entity);
    }


    public ReformaAdministrativa deferirReforma(ReformaAdministrativa entity) {
        for (ReformaAdministrativaUnidade unidade : entity.getUnidades()) {
            realizarMovimentacaoUnidade(unidade);
        }
        entity.setDeferidaEm(new Date());
        return super.salvarRetornando(entity);
    }

    private void realizarMovimentacaoUnidade(ReformaAdministrativaUnidade unidade) {

        if (unidade.getTipo().isCriarNovo()) {
            criarHierarquiaUnidade(unidade);
        }
        if (unidade.getTipo().isAlterarExistente()) {
            alterarHierarquiaExistente(unidade);
        }
        if (unidade.getTipo().isEncerrar()) {
            encerrarVigenciaHierarquia(unidade);
        }
    }

    public void recuperarHierarquiasDasUnidadesDestinos(ReformaAdministrativa entity) {
        entity.getUnidades().forEach(reformaUnidade -> {
            reformaUnidade.getUsuarios().forEach(reformaUsuario -> {
                if (reformaUsuario.getUnidadeDestino() != null) {
                    reformaUsuario.setHierarquiaOrganizacional(hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalPorUnidade(
                        entity.getData(), reformaUsuario.getUnidadeDestino(), reformaUnidade.getUnidade().getTipoHierarquiaOrganizacional()
                    ));
                }
            });
        });
    }

    private void encerrarVigenciaHierarquia(ReformaAdministrativaUnidade reformaUnidade) {
        HierarquiaOrganizacional ho = reformaUnidade.getUnidade();
        ho.setFimVigencia(reformaUnidade.getFimVigencia());
        hierarquiaOrganizacionalFacade.salvar(ho);
        transferirUsuarios(reformaUnidade);
    }

    private void transferirUsuarios(ReformaAdministrativaUnidade reformaUnidade) {
        if (reformaUnidade.getUsuarios() != null && !reformaUnidade.getUsuarios().isEmpty()) {
            TipoHierarquiaOrganizacional tipoHierarquia = reformaUnidade.getUnidade().getTipoHierarquiaOrganizacional();
            for (ReformaAdministrativaUnidadeUsuario reformaUsuario : reformaUnidade.getUsuarios()) {
                if (reformaUsuario.getUnidadeDestino() != null) {
                    reformaUsuario.setUsuarioSistema(usuarioSistemaFacade.recuperarDependenciasDasUnidadesPorTipo(reformaUsuario.getUsuarioSistema().getId(), tipoHierarquia));
                    transferirUsuarioUnidadeAdm(tipoHierarquia, reformaUsuario);
                    transferirUsuarioUnidadeOrc(tipoHierarquia, reformaUsuario);
                }
            }
        }
    }

    private void transferirUsuarioUnidadeAdm(TipoHierarquiaOrganizacional tipoHierarquia, ReformaAdministrativaUnidadeUsuario reformaUsuario) {
        if (TipoHierarquiaOrganizacional.ADMINISTRATIVA.equals(tipoHierarquia) &&
            reformaUsuario.getUsuarioSistema().getUsuarioUnidadeOrganizacional().stream().noneMatch(uuoo -> uuoo.getUnidadeOrganizacional().equals(reformaUsuario.getUnidadeDestino()))) {
            List<UsuarioUnidadeOrganizacional> privilegios = reformaUsuario.getUsuarioSistema().getUsuarioUnidadeOrganizacional().stream().filter(
                uuo -> uuo.getUnidadeOrganizacional().equals(reformaUsuario.getReformaUnidade().getUnidade().getSubordinada())
            ).collect(Collectors.toList());

            UsuarioUnidadeOrganizacional privilegio = privilegios.isEmpty() ? new UsuarioUnidadeOrganizacional() : privilegios.get(0);
            UsuarioUnidadeOrganizacional novoUuo = new UsuarioUnidadeOrganizacional(reformaUsuario.getUsuarioSistema(), reformaUsuario.getUnidadeDestino(), privilegio);
            em.persist(novoUuo);
        }
    }

    private void transferirUsuarioUnidadeOrc(TipoHierarquiaOrganizacional tipoHierarquia, ReformaAdministrativaUnidadeUsuario reformaUsuario) {
        if (TipoHierarquiaOrganizacional.ORCAMENTARIA.equals(tipoHierarquia) &&
            reformaUsuario.getUsuarioSistema().getUsuarioUnidadeOrganizacionalOrc().stream().noneMatch(uuoo -> uuoo.getUnidadeOrganizacional().equals(reformaUsuario.getUnidadeDestino()))) {
            UsuarioUnidadeOrganizacionalOrcamentaria novoUuoo = new UsuarioUnidadeOrganizacionalOrcamentaria(reformaUsuario.getUsuarioSistema(), reformaUsuario.getUnidadeDestino());
            em.persist(novoUuoo);
        }
    }


    public void criarReformaUnidadeUsuario(ReformaAdministrativaUnidade reformaUnidade) {
        List<ReformaAdministrativaUnidadeUsuario> retorno = Lists.newArrayList();
        if (reformaUnidade != null && reformaUnidade.getUnidade() != null) {
            TipoHierarquiaOrganizacional tipoHierarquia = reformaUnidade.getUnidade().getTipoHierarquiaOrganizacional();
            List<UsuarioSistema> usuarios = tipoHierarquia.isAdministrativa()
                ? usuarioSistemaFacade.recuperarUsuariosPorHierarquiasAdministrativas(Lists.newArrayList(reformaUnidade.getUnidade()), sistemaFacade.getDataOperacao(), "", true)
                : usuarioSistemaFacade.recuperarUsuariosPorHierarquiasOrcamentaris(Lists.newArrayList(reformaUnidade.getUnidade()), sistemaFacade.getDataOperacao(), true);
            usuarios.forEach(usuario -> retorno.add(new ReformaAdministrativaUnidadeUsuario(reformaUnidade, usuario, null)));
            reformaUnidade.setUsuarios(retorno);
        }
    }

    private void alterarHierarquiaExistente(ReformaAdministrativaUnidade unidade) {
        HierarquiaOrganizacional ho = hierarquiaOrganizacionalFacade.recuperar(unidade.getUnidade());
        Date inicioVigencia = unidade.getInicioVigencia();
        ho.setFimVigencia(DataUtil.adicionaDias(inicioVigencia, -1));
        ho = hierarquiaOrganizacionalFacade.salvarRetornando(ho);

        HierarquiaOrganizacional novo = new HierarquiaOrganizacional();
        novo.setSubordinada(ho.getSubordinada());
        novo.setCodigo(unidade.getCodigoNovo());
        novo.setDescricao(unidade.getDescricaoNovo());
        novo.setTipoHierarquiaOrganizacional(ho.getTipoHierarquiaOrganizacional());
        novo.setInicioVigencia(unidade.getInicioVigencia());
        novo.setFimVigencia(unidade.getFimVigencia());
        novo.setExercicio(exercicioFacade.getExercicioPorAno(DataUtil.getAno(unidade.getInicioVigencia())));
        novo.setTipoHierarquiaOrganizacional(unidade.getUnidade().getTipoHierarquiaOrganizacional());
        novo.setTipoHierarquia(unidade.getUnidade().getTipoHierarquia());
        novo.setCodigoNo(ho.getCodigoNo());
        novo.setIndiceDoNo(ho.getIndiceDoNo());
        novo.setNivelNaEntidade(ho.getNivelNaEntidade());
        atribuirSuperior(unidade, novo);
        adicionarResponsaveisCorrespondentes(unidade, ho, novo);
        unidade.setNovaHierarquia(novo);
        hierarquiaOrganizacionalFacade.salvarNovo(novo);
    }

    private void criarHierarquiaUnidade(ReformaAdministrativaUnidade unidade) {
        HierarquiaOrganizacional ho = hierarquiaOrganizacionalFacade.recuperar(unidade.getUnidade());

        UnidadeOrganizacional novaUnidade = (UnidadeOrganizacional) Util.clonarObjeto(ho.getSubordinada());
        novaUnidade.setId(null);
        novaUnidade.setDescricao(unidade.getDescricaoNovo());
        novaUnidade.setEntidade(null);
        novaUnidade.setPrevisaoHOContaDestinacao(Lists.newArrayList());
        novaUnidade.setHierarquiasOrganizacionais(Lists.newArrayList());
        novaUnidade.setResponsaveisUnidades(Lists.newArrayList());
        novaUnidade.setUnidadeGestoraUnidadesOrganizacionais(Lists.newArrayList());
        novaUnidade = unidadeOrganizacionalFacade.salvarRetornando(novaUnidade);

        HierarquiaOrganizacional novo = new HierarquiaOrganizacional();
        novo.setSubordinada(novaUnidade);
        novo.setCodigo(unidade.getCodigoNovo());
        novo.setDescricao(unidade.getDescricaoNovo());
        novo.setTipoHierarquiaOrganizacional(ho.getTipoHierarquiaOrganizacional());
        novo.setInicioVigencia(unidade.getInicioVigencia());
        novo.setFimVigencia(unidade.getFimVigencia());
        novo.setExercicio(exercicioFacade.getExercicioPorAno(DataUtil.getAno(unidade.getInicioVigencia())));
        novo.setTipoHierarquiaOrganizacional(unidade.getUnidade().getTipoHierarquiaOrganizacional());
        novo.setTipoHierarquia(unidade.getUnidade().getTipoHierarquia());
        novo.setCodigoNo(ho.getCodigoNo());
        novo.setIndiceDoNo(ho.getIndiceDoNo());
        novo.setNivelNaEntidade(ho.getNivelNaEntidade());

        atribuirSuperior(unidade, novo);
        adicionarResponsaveisCorrespondentes(unidade, ho, novo);
        unidade.setNovaHierarquia(novo);
        hierarquiaOrganizacionalFacade.salvarNovo(novo);
    }

    private static void atribuirSuperior(ReformaAdministrativaUnidade unidade, HierarquiaOrganizacional novo) {
        ReformaAdministrativaUnidade superior = unidade.getUnidadeSuperior();
        if (superior != null) {
            novo.setSuperior(superior.getNovaHierarquia() != null ? superior.getNovaHierarquia().getSubordinada() : superior.getUnidade().getSubordinada());
        } else {
            throw new ExcecaoNegocioGenerica("Não foi encontrado hierarquia superior para a unidade " + unidade.getCodigoNovo());
        }
    }

    private static void adicionarResponsaveisCorrespondentes(ReformaAdministrativaUnidade unidade, HierarquiaOrganizacional ho, HierarquiaOrganizacional novo) {
        if (unidade.getReformaAdministrativa().getTipo().isAdministrativa()) {
            novo.setHierarquiaOrganizacionalResponsavels(Lists.newArrayList());
            for (HierarquiaOrganizacionalResponsavel hierarquiaOrganizacionalResponsavel : ho.getHierarquiaOrganizacionalResponsavels()) {


                HierarquiaOrganizacionalResponsavel novoResponsavel = new HierarquiaOrganizacionalResponsavel();
                novoResponsavel.setDataInicio(unidade.getInicioVigencia());
                novoResponsavel.setDataFim(unidade.getFimVigencia());
                novoResponsavel.setHierarquiaOrganizacionalAdm(novo);
                novoResponsavel.setHierarquiaOrganizacionalOrc(hierarquiaOrganizacionalResponsavel.getHierarquiaOrganizacionalOrc());

                novo.getHierarquiaOrganizacionalResponsavels().add(novoResponsavel);
            }
        }
        if (unidade.getReformaAdministrativa().getTipo().isOrcamentaria()) {
            novo.setHierarquiaOrganizacionalCorrespondentes(Lists.newArrayList());
            for (HierarquiaOrganizacionalCorrespondente hierarquiaOrganizacionalCorrespondente : ho.getHierarquiaOrganizacionalCorrespondentes()) {
                HierarquiaOrganizacionalCorrespondente novoCorrespondente = new HierarquiaOrganizacionalCorrespondente();
                novoCorrespondente.setDataInicio(unidade.getInicioVigencia());
                novoCorrespondente.setDataFim(unidade.getFimVigencia());
                novoCorrespondente.setHierarquiaOrganizacionalOrc(hierarquiaOrganizacionalCorrespondente.getHierarquiaOrganizacionalAdm());
                novoCorrespondente.setHierarquiaOrganizacionalAdm(novo);

                novo.getHierarquiaOrganizacionalCorrespondentes().add(novoCorrespondente);
            }
        }
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public DocumentoOficialFacade getDocumentoOficialFacade() {
        return documentoOficialFacade;
    }
}
