package br.com.webpublico.negocios;

import br.com.webpublico.entidades.EfetivacaoMaterial;
import br.com.webpublico.entidades.EntradaCompraMaterial;
import br.com.webpublico.entidades.Material;
import br.com.webpublico.entidades.Notificacao;
import br.com.webpublico.entidadesauxiliares.EfetivacaoMaterialVO;
import br.com.webpublico.entidadesauxiliares.FiltroEfetivacaoMaterial;
import br.com.webpublico.enums.SituacaoEntradaMaterial;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.enums.TipoNotificacao;
import br.com.webpublico.seguranca.NotificacaoService;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.*;

/**
 * Criado por Mateus
 * Data: 27/01/2017.
 */
@Stateless
public class EfetivacaoMaterialFacade extends AbstractFacade<EfetivacaoMaterial> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private MaterialFacade materialFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private EntradaMaterialFacade entradaMaterialFacade;

    public EfetivacaoMaterialFacade() {
        super(EfetivacaoMaterial.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public EfetivacaoMaterial recuperar(Object id) {
        return super.recuperar(id);
    }


    public List<EfetivacaoMaterial> buscarMateriaisFiltrando(FiltroEfetivacaoMaterial filtro) {
        String sql = " select em.* from efetivacaomaterial em  " +
            "            inner join material mat on em.material_id = mat.id " +
            "          where em.dataregistro = (select max(efe.dataregistro) from efetivacaomaterial efe where efe.material_id = mat.id) ";
        if (filtro.getGrupoMaterial() != null) {
            sql += " and mat.grupo_id = :idGrupo ";
        }
        if (filtro.getStatusMaterial() != null) {
            sql += " and mat.statusMaterial = :status ";
        }
        if (filtro.getUsuarioSistema() != null) {
            sql += " and em.usuariosistema_id = :idUsuario ";
        }
        if (filtro.getUnidadeOrganizacional() != null) {
            sql += " and em.unidadeadministrativa_id = :idUnidade ";
        }
        if (filtro.getParteMaterial() != null) {
            sql += " and (to_char(mat.codigo) like :parteMaterial  " +
                "        or lower(translate(mat.descricao,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')) like translate(:parteMaterial,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')) ";
        }
        Query q = em.createNativeQuery(sql, EfetivacaoMaterial.class);
        if (filtro.getGrupoMaterial() != null) {
            q.setParameter("idGrupo", filtro.getGrupoMaterial().getId());
        }
        if (filtro.getStatusMaterial() != null) {
            q.setParameter("status", filtro.getStatusMaterial().name());
        }
        if (filtro.getUsuarioSistema() != null) {
            q.setParameter("idUsuario", filtro.getUsuarioSistema().getId());
        }
        if (filtro.getUnidadeOrganizacional() != null) {
            q.setParameter("idUnidade", filtro.getUnidadeOrganizacional().getId());
        }
        if (filtro.getParteMaterial() != null) {
            q.setParameter("parteMaterial", "%" + filtro.getParteMaterial().toLowerCase().trim() + "%");
        }
        List<EfetivacaoMaterial> resultado = q.getResultList();
        if (resultado == null) {
            return Lists.newArrayList();
        }
        return resultado;
    }

    public void salvar(List<EfetivacaoMaterialVO> efetivacoes) {
        for (EfetivacaoMaterialVO efetVO : efetivacoes) {
            if (efetVO.hasSituacao()) {
                EfetivacaoMaterial novaEfetivacao = new EfetivacaoMaterial();
                Material material = efetVO.getMaterial();
                material.setStatusMaterial(efetVO.getSituacao());
                material = em.merge(material);

                novaEfetivacao.setDataRegistro(new Date());
                novaEfetivacao.setMaterial(material);
                novaEfetivacao.setObservacao(efetVO.getObservacao());
                novaEfetivacao.setSituacao(efetVO.getSituacao());
                novaEfetivacao.setUnidadeAdministrativa(sistemaFacade.getUnidadeOrganizacionalAdministrativaCorrente());
                novaEfetivacao.setUsuarioSistema(sistemaFacade.getUsuarioCorrente());
                novaEfetivacao = em.merge(novaEfetivacao);

                criarNotificacaoEfetivacaoMaterial(novaEfetivacao);
            }
        }
    }

    public void desbloquearEntradaPorCompraComPendenciaEfetivacaoMaterial(List<EfetivacaoMaterialVO> efetivacoes) {
        Set<EntradaCompraMaterial> entradasComPendencias = new HashSet<>();
        for (EfetivacaoMaterialVO efetivacao : efetivacoes) {
            if (efetivacao.hasSituacao()) {
                List<EntradaCompraMaterial> entradas = entradaMaterialFacade.buscarEntradaCompraPorMaterial(efetivacao.getMaterial(), SituacaoEntradaMaterial.ATESTO_PROVISORIO_COM_PENDENCIA);
                entradasComPendencias.addAll(entradas);
            }
        }
        for (EntradaCompraMaterial entrada : entradasComPendencias) {
            List<Material> materiais = materialFacade.buscarMateriaisEntrada(entrada);
            Optional<Material> any = materiais
                .stream()
                .filter(mat -> mat.getStatusMaterial().isAguardando() || mat.getStatusMaterial().isIndeferido())
                .findAny();
            if (!any.isPresent()) {
                entrada.setSituacao(SituacaoEntradaMaterial.ATESTO_PROVISORIO_AGUARDANDO_LIQUIDACAO);
            }
        }
    }

    public void criarNotificacaoEfetivacaoMaterial(EfetivacaoMaterial efetivacao) {
        Material material = efetivacao.getMaterial();

        String obs = !Util.isStringNulaOuVazia(efetivacao.getObservacao()) ? efetivacao.getObservacao() + "." : "";
        String msg = "A solicitação de cadastro do material nº " + material.getCodigoDescricao() + ", por " + efetivacao.getUsuarioSistema().getNome() + ", "
            + hierarquiaOrganizacionalFacade.getHierarquiaDaUnidade(TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), efetivacao.getUnidadeAdministrativa(), sistemaFacade.getDataOperacao())
            + ", está " + material.getStatusMaterial().getDescricao() + ". " + obs;

        String link = "/material/ver/" + material.getId() + "/";

        Notificacao notificacao = new Notificacao();
        notificacao.setDescricao(msg);
        notificacao.setLink(link);
        notificacao.setGravidade(Notificacao.Gravidade.ATENCAO);
        notificacao.setTitulo(TipoNotificacao.OCORRENCIA_CADASTRO_MATERIAL.getDescricao());
        notificacao.setTipoNotificacao(TipoNotificacao.OCORRENCIA_CADASTRO_MATERIAL);
        if (!Util.isListNullOrEmpty(material.getEfetivacoes())) {
            EfetivacaoMaterial efetivacaoInicial = material.getEfetivacoes().get(0);
            notificacao.setUsuarioSistema(efetivacaoInicial.getUsuarioSistema());
            if (efetivacaoInicial.getUnidadeAdministrativa() != null) {
                notificacao.setUnidadeOrganizacional(efetivacaoInicial.getUnidadeAdministrativa());
            }
        }
        NotificacaoService.getService().notificar(notificacao);
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public MaterialFacade getMaterialFacade() {
        return materialFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

}
