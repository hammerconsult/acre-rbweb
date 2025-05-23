package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.*;
import br.com.webpublico.enums.TipoEstoque;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.enums.TipoMascaraUnidadeMedida;
import br.com.webpublico.negocios.comum.ConfiguracaoDeRelatorioFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Stateless
public class ConsultaLocalEstoqueFacade implements Serializable {

    @EJB
    private LocalEstoqueFacade localEstoqueFacade;
    @EJB
    private MaterialFacade materialFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private ConfiguracaoDeRelatorioFacade configuracaoDeRelatorioFacade;
    @EJB
    private LoteMaterialFacade loteMaterialFacade;

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;


    public List<ConsultaLocalEstoque> buscarLocaisEstoque(ConsultaLocalEstoqueFiltro filtro) {
        List<ConsultaLocalEstoque> toReturn = Lists.newArrayList();
        if (filtro.getTipoConsulta().isConsultaAnalitica()) {
            String sql = "" +
                "  with pai (id, codigo, descricao, fechadoem, superior_id, unidadeorganizacional_id) as        " +
                "    (select le_pai.id, le_pai.codigo, le_pai.descricao, le_pai.fechadoem, le_pai.superior_id, le_pai.unidadeorganizacional_id       " +
                "      from localestoque le_pai        " +
                "      where le_pai.id = :idLocalEstoquePai    " +
                "  union all        " +
                "     select le_filho.id, le_filho.codigo, le_filho.descricao, le_filho.fechadoem, le_filho.superior_id, le_filho.unidadeorganizacional_id        " +
                "       from localestoque le_filho        " +
                "     inner join pai p on p.id = le_filho.superior_id)    " +
                "   select distinct le.* from estoque e    " +
                "       inner join material m on m.id = e.material_id " +
                "       inner join grupomaterial gm on m.grupo_id = gm.id " +
                "       inner join localestoqueorcamentario leo on leo.id = e.localestoqueorcamentario_id    " +
                "       inner join localestoque le on le.id = leo.localestoque_id    " +
                "       inner join pai p on p.id = le.id    " +
                "     where le.fechadoem is null    " +
                "       and e.dataestoque = (select max(e2.dataestoque)    " +
                "                               from estoque e2    " +
                "                              where e2.localestoqueorcamentario_id = e.localestoqueorcamentario_id    " +
                "                                and e2.material_id = e.material_id   " +
                "                                and trunc(e2.dataestoque) <= trunc(:dataReferencia)) ";
            sql += !Util.isListNullOrEmpty(filtro.getGruposMateriais()) ? " and m.grupo_id in (:idsGrupos) " : "";
            sql += filtro.getMaterial() != null ? " and e.material_id = :idMaterial " : "";
            sql += !Util.isListNullOrEmpty(filtro.getUnidadesOrcamentarias()) ? " and leo.unidadeorcamentaria_id in (:idsOrcamentarias) " : "";
            sql += filtro.getStatusMaterial() != null ? " and m.statusmaterial = :statusMaterial" : "";
            sql += " order by le.codigo ";
            Query q = em.createNativeQuery(sql, LocalEstoque.class);
            q.setParameter("dataReferencia", DataUtil.dataSemHorario(filtro.getDataReferencia()));
            q.setParameter("idLocalEstoquePai", filtro.getLocalEstoque().getId());
            if (filtro.getMaterial() != null) {
                q.setParameter("idMaterial", filtro.getMaterial().getId());
            }
            if (!Util.isListNullOrEmpty(filtro.getIdsGruposMateriais())) {
                q.setParameter("idsGrupos", filtro.getIdsGruposMateriais());
            }
            if (!Util.isListNullOrEmpty(filtro.getIdsUnidadesOrcamentarias())) {
                q.setParameter("idsOrcamentarias", filtro.getIdsUnidadesOrcamentarias());
            }
            if (filtro.getStatusMaterial() != null) {
                q.setParameter("statusMaterial", filtro.getStatusMaterial().name());
            }
            List<LocalEstoque> list = q.getResultList();
            for (LocalEstoque local : list) {
                ConsultaLocalEstoque localEstoque = new ConsultaLocalEstoque(local);
                for (UnidadeOrganizacional uo : filtro.getUnidadesOrcamentarias()) {

                    LocalEstoqueOrcamentario leo = localEstoqueFacade.buscarLocalEstoqueOrcamentario(local, uo);
                    if (leo != null) {
                        HierarquiaOrganizacional hoOrc = hierarquiaOrganizacionalFacade.getHierarquiaDaUnidade(TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), uo, sistemaFacade.getDataOperacao());
                        ConsultaLocalEstoqueOrcamentario localEstoqueOrcamentario = new ConsultaLocalEstoqueOrcamentario(hoOrc);
                        localEstoque.getOrcamentarias().add(localEstoqueOrcamentario);
                        filtro.setOrcamentaria(uo);
                        localEstoqueOrcamentario.setMateriais(buscarMateriais(filtro, local));
                    }
                }
                toReturn.add(localEstoque);
            }
        } else {
            ConsultaLocalEstoque localEstoque = new ConsultaLocalEstoque(filtro.getLocalEstoque());
            List<ConsultaLocalEstoqueMaterial> materiais = buscarMateriais(filtro, filtro.getLocalEstoque());
            localEstoque.getMateriais().addAll(materiais);

            toReturn.add(localEstoque);
        }
        return toReturn;
    }

    public List<ConsultaLocalEstoqueMaterial> buscarMateriais(ConsultaLocalEstoqueFiltro filtro, LocalEstoque local) {
        String sql = "" +
                "     select m.id as id_mat," +
                "            m.codigo as codigo_mat," +
                "            m.descricao as descricao_mat," +
                "            um.sigla as unid_med," +
                "            gm.codigo || ' - ' || gm.descricao as grupo," +
                "            m.medicohospitalar as medico_hospitalar," +
                "            m.controledeLote as controle_lote," +
                "            coalesce(sum(e.fisico),0) as qtde_est, " +
                "            coalesce(sum(e.financeiro),0) as valor_est, " +
                "           um.mascaraquantidade as mascara_qtde," +
                "           um.mascaravalorunitario as mascara_valor" +
                "      from estoque e " +
                "       inner join material m on m.id = e.material_id " +
                "       inner join unidademedida um on um.id = m.unidademedida_id " +
                "       inner join grupomaterial gm on m.grupo_id = gm.id " +
                "       inner join localestoqueorcamentario leo on leo.id = e.localestoqueorcamentario_id    " +
                "       inner join localestoque le on le.id = leo.localestoque_id    " +
                "       where e.bloqueado = 0   " +
                "       and le.id = :idLocalEstoque " +
                "       and e.dataestoque = (select max(e2.dataestoque) " +
                "                               from estoque e2 " +
                "                              where e2.localestoqueorcamentario_id = e.localestoqueorcamentario_id " +
                "                                and e2.material_id = e.material_id " +
                "                                and trunc(e2.dataestoque) <= trunc(:dataReferencia)) ";
        sql += filtro.getTipoComparacao() != null && filtro.getQuantidade() != null ? " and e.fisico " + filtro.getTipoComparacao().getOperador() + " " + filtro.getQuantidade() : "";
        sql += !Util.isListNullOrEmpty(filtro.getGruposMateriais()) ? " and m.grupo_id in (:idsGrupos) " : "";
        sql += filtro.getMaterial() != null ? " and e.material_id = :idMaterial " : "";
        sql += filtro.getOrcamentaria() != null ? " and leo.unidadeorcamentaria_id = :idOrcamentario " : "";
        sql += filtro.getPerecibilidade() != null ? " and m.perecibilidade = :perecibilidade " : "";
        sql += filtro.getStatusMaterial() != null ? " and m.statusmaterial = :statusMaterial " : "";
        sql += " group by m.id, m.codigo, m.descricao, um.sigla, gm.codigo, gm.descricao, m.medicohospitalar, " +
                "             m.controledeLote, um.mascaraquantidade, um.mascaravalorunitario ";
        sql += " order by m.codigo ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("dataReferencia", DataUtil.dataSemHorario(filtro.getDataReferencia()));
        q.setParameter("idLocalEstoque", local.getId());
        if (filtro.getMaterial() != null) {
            q.setParameter("idMaterial", filtro.getMaterial().getId());
        }
        if (!Util.isListNullOrEmpty(filtro.getIdsGruposMateriais())) {
            q.setParameter("idsGrupos", filtro.getIdsGruposMateriais());
        }
        if (filtro.getOrcamentaria() != null) {
            q.setParameter("idOrcamentario", filtro.getOrcamentaria().getId());
        }
        if (filtro.getPerecibilidade() != null) {
            q.setParameter("perecibilidade", filtro.getPerecibilidade().name());
        }
        if (filtro.getStatusMaterial() != null) {
            q.setParameter("statusMaterial", filtro.getStatusMaterial().name());
        }
        List<Object[]> lista = q.getResultList();
        List<ConsultaLocalEstoqueMaterial> materiais = new ArrayList<>();
        for (Object[] obj : lista) {
            ConsultaLocalEstoqueMaterial mat = new ConsultaLocalEstoqueMaterial();
            mat.setIdMaterial(((BigDecimal) obj[0]).longValue());
            mat.setCodigo(((BigDecimal) obj[1]).longValue());
            mat.setDescricao((String) obj[2]);
            mat.setUnidadeMedida((String) obj[3]);
            mat.setGrupoMaterial((String) obj[4]);
            mat.setMedicoHospitalar(((BigDecimal) obj[5]).compareTo(BigDecimal.ONE) == 0);
            mat.setControleLote(((BigDecimal) obj[6]).compareTo(BigDecimal.ONE) == 0);
            mat.setQuantidadeEstoque((BigDecimal) obj[7]);
            mat.setValorEstoque((BigDecimal) obj[8]);
            mat.setMascaraQuantidade(obj[9] != null ? TipoMascaraUnidadeMedida.valueOf((String) obj[9]) : TipoMascaraUnidadeMedida.DUAS_CASAS_DECIMAL);
            mat.setMascaraValorUnitario(obj[10] != null ? TipoMascaraUnidadeMedida.valueOf((String) obj[10]) : TipoMascaraUnidadeMedida.DUAS_CASAS_DECIMAL);
            mat.setValorUnitario(mat.calcularValorUnitario());
            mat.setReservasEstoque(localEstoqueFacade.recuperarReservasAguardandoDoLocalDeEstoque(local, mat.getIdMaterial()));
            mat.setLotes(buscarLotesMateriais(local.getId(), mat.getIdMaterial(), filtro.getOrcamentaria() != null ? filtro.getOrcamentaria().getId() : null));

            materiais.add(mat);
        }
        return materiais;
    }

    private List<ConsultaLocalEstoqueLoteMaterial> buscarLotesMateriais(Long idLocalEstoque, Long idMaterial, Long idOrcamentaria) {
        List<FormularioLoteMaterial> formLotes = loteMaterialFacade.buscarLoteMaterialPorMaterialAndLocalEstoqueAndOrcamentariaAndFiltro(
            idLocalEstoque, idMaterial, idOrcamentaria, "");

        List<ConsultaLocalEstoqueLoteMaterial> toReturn = new ArrayList<>();
        for (FormularioLoteMaterial formLote : formLotes) {
            ConsultaLocalEstoqueLoteMaterial loteMaterial = new ConsultaLocalEstoqueLoteMaterial();
            loteMaterial.setIdentificacao(formLote.getIdentificacao());
            loteMaterial.setValidade(formLote.getValidade());
            loteMaterial.setQuantidade(formLote.getQuantidadeLote());
            loteMaterial.setMascaraQuantidade(formLote.getMascaraQuantidade());
            loteMaterial.setMascaraValorUnitario(formLote.getMascaraValorUnitario());
            toReturn.add(loteMaterial);
        }
        Collections.sort(toReturn);
        return toReturn;
    }

    public List<Material> buscarMateriaisHierarquiaLocalEstoque(ConsultaLocalEstoqueFiltro filtro, LocalEstoque local, String parte) {
        String sql = " " +
            " with pai (id, codigo, descricao, fechadoem, superior_id, unidadeorganizacional_id) " +
            "         as ( " +
            "        select " +
            "            le_pai.id, " +
            "            le_pai.codigo, " +
            "            le_pai.descricao, " +
            "            le_pai.fechadoem, " +
            "            le_pai.superior_id, " +
            "            le_pai.unidadeorganizacional_id " +
            "        from localestoque le_pai " +
            "        where le_pai.id = :local_pai_id " +
            "        union all " +
            "        select " +
            "            le_filho.id, " +
            "            le_filho.codigo, " +
            "            le_filho.descricao, " +
            "            le_filho.fechadoem, " +
            "            le_filho.superior_id, " +
            "            le_filho.unidadeorganizacional_id " +
            "        from localestoque le_filho " +
            "           inner join pai p on p.id = le_filho.superior_id) " +
            " select distinct m.* from material m " +
            "    inner join localestoquematerial lem on lem.material_id = m.id " +
            "    inner join pai on pai.id = lem.localestoque_id " +
            " where (m.codigo like :parte or lower(m.descricao) like :parte ) ";
        sql += filtro.getStatusMaterial() != null ? " and m.statusmaterial = :statusMaterial " : "";
        sql += !Util.isListNullOrEmpty(filtro.getGruposMateriais()) ? " and m.grupo_id in (:idsGrupos) " : "";
        sql += filtro.getPerecibilidade() != null ? " and m.perecibilidade = :perecibilidade " : "";
        sql += " order by (case when to_char(m.codigo) = :parte then 1 " +
            "              when to_char(m.codigo) like :parte then 2 " +
            "              else 3 end), m.codigo ";
        Query q = em.createNativeQuery(sql, Material.class);
        q.setParameter("local_pai_id", local.getId());
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        if (filtro.getStatusMaterial() != null) {
            q.setParameter("statusMaterial", filtro.getStatusMaterial().name());
        }
        if (!Util.isListNullOrEmpty(filtro.getIdsGruposMateriais())) {
            q.setParameter("idsGrupos", filtro.getIdsGruposMateriais());
        }
        if (filtro.getPerecibilidade() != null) {
            q.setParameter("perecibilidade", filtro.getPerecibilidade().name());
        }
        return q.getResultList();
    }

    public MovimentacaoGrupoMaterial getMovimentoGrupoMaterial(ConsultaLocalEstoqueFiltro filtro) {
        MovimentacaoGrupoMaterial movGrupo = new MovimentacaoGrupoMaterial();
        movGrupo.setMovimentosEstoque(buscarMovimentosEstoque(filtro));
        return movGrupo;
    }

    public List<MovimentoEstoqueContabil> buscarMovimentosEstoque(ConsultaLocalEstoqueFiltro filtro) {
        String sql = " " +
            " select " +
            "  mov.datamovimento       as data, " +
            "  mov.tipooperacao        as tipo_operacao, " +
            "  mov.descricaodaoperacao as movimento, " +
            "  case mov.tipooperacao when 'CREDITO' then sum(trunc(mov.fisico, 0)) else 0 end  as credito," +
            "  case mov.tipooperacao when 'DEBITO' then sum(trunc(mov.fisico, 0))  else 0 end  as debito " +
            " from movimentoestoque mov " +
            "   inner join estoque est on est.id = mov.estoque_id " +
            "   inner join localestoqueorcamentario leo on leo.id = mov.localestoqueorcamentario_id " +
            "   inner join localestoque le on leo.localestoque_id = le.id " +
            "   inner join material mat on mat.id = mov.material_id " +
            " where trunc(mov.datamovimento) <= to_date(:dataReferencia, 'dd/MM/yyyy') " +
            "    and est.tipoestoque = :tipoEstoque " +
            "    and le.id = :idLocalEstoque " +
            "    and mat.id = :idMaterial ";
        sql += filtro.getOrcamentaria() != null ? " and leo.unidadeorcamentaria_id = :unidade " : " ";

        sql += " group by mov.datamovimento, mov.descricaodaoperacao, mov.tipooperacao " +
            " order by mov.datamovimento ";

        Query q = em.createNativeQuery(sql);
        if (filtro.getOrcamentaria() != null) {
            q.setParameter("unidade", filtro.getOrcamentaria().getId());
        }
        q.setParameter("idMaterial", filtro.getIdMaterial());
        q.setParameter("idLocalEstoque", filtro.getLocalEstoque().getId());
        q.setParameter("tipoEstoque", TipoEstoque.MATERIAL_CONSUMO_PRINCIPAL_ALMOXARIFADO.name());
        q.setParameter("dataReferencia", DataUtil.getDataFormatada(filtro.getDataReferencia()));

        List<MovimentoEstoqueContabil> movimentos = Lists.newArrayList();
        List<Object[]> resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            for (Object[] obj : resultList) {
                MovimentoEstoqueContabil mov = new MovimentoEstoqueContabil();
                mov.setData((Date) obj[0]);
                mov.setDescricao((String) obj[2]);
                mov.setCredito((BigDecimal) obj[3]);
                mov.setDebito((BigDecimal) obj[4]);
                movimentos.add(mov);
            }
        }
        return movimentos;
    }

    public LocalEstoqueFacade getLocalEstoqueFacade() {
        return localEstoqueFacade;
    }

    public MaterialFacade getMaterialFacade() {
        return materialFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public ConfiguracaoDeRelatorioFacade getConfiguracaoDeRelatorioFacade() {
        return configuracaoDeRelatorioFacade;
    }
}
