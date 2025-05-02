package br.com.webpublico.relatoriofacade;

import br.com.webpublico.entidadesauxiliares.AuxRelatorioSaldoGrupoMaterial;
import br.com.webpublico.enums.NaturezaTipoGrupoMaterial;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.BemFacade;
import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Criado por Alex
 * Data: 20/06/2017.
 */
@Stateless
public class RelatorioSaldoGrupoMaterialFacade implements Serializable {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private BemFacade bemFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;

    public RelatorioSaldoGrupoMaterialFacade() {

    }

    public List<AuxRelatorioSaldoGrupoMaterial> buscarDados(List<Long> idsUnidades, Date dataReferencia) {
        try {
            StringBuilder sql = new StringBuilder();
            sql.append(" SELECT gp.CODIGO,gp.DESCRICAO, (saldo.DEBITO - saldo.CREDITO) as saldoOriginal, ");
            sql.append(" to_date(to_char(saldo.DATASALDO,'dd/MM/yyyy'),'dd/MM/yyyy'), vwORC.CODIGO ||' - '|| vwORC.DESCRICAO as unidade, ");
            sql.append("  case ");
            sql.append("  saldo.tipoestoque ");
            sql.append("  when 'PRODUTOS_ACABADOS_PRINCIPAL' then 'Produtos Acabados Principal' ");
            sql.append("  when 'SERVIÇOS_ACABADOS_PRINCIPAL' then 'Serviços Acabados Principal' ");
            sql.append("  when 'BEM_ESTOQUE_INTEGRACAO' then 'Bem Estoque Integração' ");
            sql.append("  when 'PRODUTOS_ELABORACAO_PRINCIPAL' then 'Produtos em Elaboração Principal' ");
            sql.append("  when 'SERVICOS_ELABORACAO_PRINCIPAL' then 'Serviços em Elaboração Principal' ");
            sql.append("  when 'MATERIA_PRIMA_PRINCIPAL' then 'Matéria-prima Principal' ");
            sql.append("  when 'MATERIAL_CONSUMO_PRINCIPAL_ALMOXARIFADO' then 'Material de Consumo Principal - Almoxarifado' ");
            sql.append("  when 'ESTOQUE_DIVERSOS_OUTROS_ESTOQUE' then 'Estoques Diversos - Outros Estoques' ");
            sql.append("  end as tipoEstoque");
            sql.append(" FROM saldoGrupoMaterial saldo ");
            sql.append(" INNER JOIN grupoMaterial gp ON gp.id = saldo.GRUPOMATERIAL_ID ");
            sql.append(" INNER JOIN VWHIERARQUIAORCAMENTARIA vwORC on saldo.UNIDADEORGANIZACIONAL_ID = vwORC.SUBORDINADA_ID");
            sql.append(" WHERE saldo.datasaldo =( ");
            sql.append("                        select max(sld.datasaldo) from saldogrupomaterial sld ");
            sql.append("                        where sld.datasaldo <= to_date(:data, 'dd/mm/yyyy') ");
            sql.append("                        and saldo.GRUPOMATERIAL_ID = sld.GRUPOMATERIAL_ID ");
            sql.append("                        and saldo.UNIDADEORGANIZACIONAL_ID = sld.UNIDADEORGANIZACIONAL_ID ");
            sql.append("                        and saldo.tipoestoque = sld.tipoestoque ");
            sql.append("                        and saldo.NATUREZATIPOGRUPOMATERIAL = sld.NATUREZATIPOGRUPOMATERIAL ");
            sql.append("                        ) ");
            if (!idsUnidades.isEmpty()) {
                sql.append(" AND saldo.unidadeorganizacional_id in :unidade ");
            }
            sql.append(" AND saldo.naturezatipogrupomaterial = :natureza ");
            sql.append(" AND saldo.DATASALDO between vwORC.INICIOVIGENCIA and coalesce(vwORC.FIMVIGENCIA, saldo.DATASALDO)");
            sql.append(" ORDER BY gp.codigo, vwORC.CODIGO ");
            Query q = em.createNativeQuery(sql.toString());
            if (!idsUnidades.isEmpty()) {
                q.setParameter("unidade", idsUnidades);
            }
            q.setParameter("natureza", NaturezaTipoGrupoMaterial.ORIGINAL.name());
            q.setParameter("data", DataUtil.getDataFormatada(dataReferencia));
            List<Object[]> resultado = q.getResultList();
            List<AuxRelatorioSaldoGrupoMaterial> retorno = Lists.newArrayList();
            if (!resultado.isEmpty()) {
                for (Object[] obj : resultado) {
                    AuxRelatorioSaldoGrupoMaterial saldo = new AuxRelatorioSaldoGrupoMaterial();
                    saldo.setCodigo((String) obj[0]);
                    saldo.setDescricaoGrupo((String) obj[1]);
                    saldo.setSaldo((BigDecimal) obj[2]);
                    saldo.setDataSaldo((Date) obj[3]);
                    saldo.setUnidadeOrganizacional((String) obj[4]);
                    saldo.setTipoEstoque((String) obj[5]);
                    retorno.add(saldo);
                }
            }
            return retorno;
        } catch (IllegalArgumentException e) {
            throw new ExcecaoNegocioGenerica(e.getMessage());
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        }
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public BemFacade getBemFacade() {
        return bemFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }
}
