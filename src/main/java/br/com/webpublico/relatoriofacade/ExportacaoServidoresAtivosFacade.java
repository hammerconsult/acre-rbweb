package br.com.webpublico.relatoriofacade;

import br.com.webpublico.negocios.CargoFacade;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.UtilRH;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.util.List;

@Stateless
public class ExportacaoServidoresAtivosFacade implements Serializable {
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private CargoFacade cargoFacade;

    public List<Object[]> buscarServidores(String filtros) {
        String sql = " select pf.nome, " +
            "        FORMATACPFCNPJ(pf.cpf), " +
            "        (select ho.codigo || ' - ' || ho.DESCRICAO " +
            "           from VWHIERARQUIAADMINISTRATIVA ho " +
            "          where SUBSTR(ho.CODIGO, 1, 6) = SUBSTR(vw.CODIGO, 1, 6) " +
            "           and ho.CODIGO like '%00.00000.000.00' " +
            "            and to_date(:dataOperacao, 'dd/MM/yyyy') between ho.INICIOVIGENCIA and coalesce(ho.FIMVIGENCIA, to_date(:dataOperacao, 'dd/MM/yyyy'))) as orgao, " +
            "        cargo.DESCRICAO, " +
            "        jornada.HORASSEMANAL, " +
            "        coalesce((select case WHEN fg.id is not null then 'SIM' else 'NÃO' END " +
            "                    from AFASTAMENTO fg " +
            "                   where fg.CONTRATOFP_ID = v.id " +
            "                     and to_date(:dataOperacao, 'dd/MM/yyyy') between fg.INICIO and coalesce(fg.TERMINO, to_date(:dataOperacao, 'dd/MM/yyyy'))),'NÃO') as afastamento, " +
            "        coalesce((select case WHEN fg.id is not null then 'SIM' else 'NÃO' END " +
            "                    from FUNCAOGRATIFICADA fg " +
            "                   where fg.CONTRATOFP_ID = v.id " +
            "                     and to_date(:dataOperacao, 'dd/MM/yyyy') between fg.INICIOVIGENCIA and coalesce(fg.FINALVIGENCIA, to_date(:dataOperacao, 'dd/MM/yyyy'))), 'NÃO') as funcaoGratificada " +
            "   from vinculofp v " +
            "  inner join matriculafp mat on mat.id = v.MATRICULAFP_ID " +
            "  inner join pessoafisica pf on pf.id = mat.PESSOA_ID " +
            "  inner join contratofp c on c.id = v.id " +
            "   left join JORNADADETRABALHO jornada on jornada.id = c.JORNADADETRABALHO_ID " +
            "   left join cargo cargo on c.CARGO_ID = cargo.ID " +
            "  inner join UNIDADEORGANIZACIONAL un on un.id = v.UNIDADEORGANIZACIONAL_ID " +
            "  inner join VWHIERARQUIAADMINISTRATIVA vw on vw.SUBORDINADA_ID = un.id " +
            "  where to_date(:dataOperacao, 'dd/MM/yyyy') between v.INICIOVIGENCIA and coalesce(v.FINALVIGENCIA, to_date(:dataOperacao, 'dd/MM/yyyy')) " +
            "    and to_date(:dataOperacao, 'dd/MM/yyyy') between vw.INICIOVIGENCIA and coalesce(vw.FIMVIGENCIA, to_date(:dataOperacao, 'dd/MM/yyyy')) " +
            filtros +
            "  order by pf.nome ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(UtilRH.getDataOperacao()));
        return q.getResultList();
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public CargoFacade getCargoFacade() {
        return cargoFacade;
    }
}
