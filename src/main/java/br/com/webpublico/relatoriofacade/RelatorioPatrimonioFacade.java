package br.com.webpublico.relatoriofacade;

import br.com.webpublico.controle.RelatorioPatrimonioControlador;
import br.com.webpublico.entidadesauxiliares.ItemRelatorioPatrimonio;
import br.com.webpublico.enums.SituacaoReducaoValorBem;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Lists;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by mateus on 29/05/18.
 */
@Stateless
public class RelatorioPatrimonioFacade extends AbstractRelatorioAssincronoFacade {

    private static Logger logger = LoggerFactory.getLogger(RelatorioPatrimonioFacade.class);
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;

    @Override
    public JRBeanCollectionDataSource criarBean(Object clausulasConsulta) {
        return new JRBeanCollectionDataSource(buscarDados((RelatorioPatrimonioControlador.FiltrosRelatorioPatrimonio) clausulasConsulta));
    }

    private List<ItemRelatorioPatrimonio> buscarDados(RelatorioPatrimonioControlador.FiltrosRelatorioPatrimonio filtros) {
        String sql = " " +
            " select  "+
            "   lote.codigo,  "+
            "   lote.data,  "+
            "   lote.datalancamento,  "+
            "   coalesce(pf.nome, usuario.login) usuario,  "+
            "   vw.codigo || ' - '|| vw.descricao as administrativa,  "+
            "   vworc.codigo || ' - ' || vworc.descricao as orcamentaria,  "+
            "   grupo.codigo || ' - ' || grupo.descricao grupo,  "+
            "   tiporeducao.valorresidual,  "+
            "   tiporeducao.vidautilemanos,  "+
            "   bem.identificacao || ' - ' || bem.descricao bem,  "+
            "   coalesce(estado.valororiginal, 0) as valorbem,  "+
            "   evento.valordolancamento valorreducao,  "+
            "   lote.situacao  "+
            "  from lotereducaovalorbem lote  "+
            " inner join usuariosistema usuario on usuario.id = lote.usuariosistema_id  "+
            "  left join pessoafisica pf on pf.id = usuario.pessoafisica_id  "+
            " inner join reducaovalorbem reducao on reducao.lotereducaovalorbem_id = lote.id  "+
            " inner join eventobem evento on evento.id = reducao.id  "+
            " inner join estadobem estado on estado.id = evento.estadoresultante_id  "+
            " inner join grupobem grupo on grupo.id = estado.grupobem_id  "+
            " inner join tiporeducao tiporeducao on (tiporeducao.grupobem_id = grupo.id  "+
            "                          and tiporeducao.tiporeducaovalorbem = lote.tiporeducao  "+
            "                          and trunc(lote.data) between tiporeducao.iniciovigencia and coalesce(tiporeducao.fimvigencia, trunc(lote.data)))  "+
            " inner join bem bem on bem.id = evento.bem_id  "+
            " inner join hierarquiaorganizacional vw on vw.subordinada_id = estado.detentoraadministrativa_id  "+
            " inner join hierarquiaorganizacional vworc on vworc.subordinada_id = estado.detentoraorcamentaria_id  "+
            "where lote.id = :idLote "+
            "   and to_date(:dataOperacao, 'dd/mm/yyyy') between vw.iniciovigencia and coalesce(vw.fimvigencia, to_date(:dataOperacao, 'dd/mm/yyyy'))  "+
            "   and vw.tipohierarquiaorganizacional = :tipoAdm "+
            "   and to_date(:dataOperacao, 'dd/mm/yyyy') between vworc.iniciovigencia and coalesce(vworc.fimvigencia, to_date(:dataOperacao, 'dd/mm/yyyy'))  "+
            "   and vworc.tipohierarquiaorganizacional = :tipoOrc "+
            "order by lote.codigo, vworc.codigo, vw.codigo, grupo.codigo ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(filtros.getDataOperacao()));
        q.setParameter("idLote", filtros.getLoteReducaoValorBem().getId());
        q.setParameter("tipoAdm", TipoHierarquiaOrganizacional.ADMINISTRATIVA.name());
        q.setParameter("tipoOrc", TipoHierarquiaOrganizacional.ORCAMENTARIA.name());
        List<ItemRelatorioPatrimonio> retorno = Lists.newArrayList();
        List<Object[]> resultado = q.getResultList();
        if (!resultado.isEmpty()) {
            for (Object[] obj : resultado) {
                ItemRelatorioPatrimonio itemRelatorioPatrimonio = new ItemRelatorioPatrimonio();
                itemRelatorioPatrimonio.setCodigo((BigDecimal) obj[0]);
                itemRelatorioPatrimonio.setData((Date) obj[1]);
                itemRelatorioPatrimonio.setDataLancamento((Date) obj[2]);
                itemRelatorioPatrimonio.setUsuario((String) obj[3]);
                itemRelatorioPatrimonio.setAdministrativa((String) obj[4]);
                itemRelatorioPatrimonio.setOrcamentaria((String) obj[5]);
                itemRelatorioPatrimonio.setGrupo((String) obj[6]);
                itemRelatorioPatrimonio.setValorResidual((BigDecimal) obj[7]);
                itemRelatorioPatrimonio.setVidaUtilEmAnos((BigDecimal) obj[8]);
                itemRelatorioPatrimonio.setBem((String) obj[9]);
                itemRelatorioPatrimonio.setValorBem((BigDecimal) obj[10]);
                itemRelatorioPatrimonio.setValorReducao((BigDecimal) obj[11]);
                itemRelatorioPatrimonio.setSituacao(SituacaoReducaoValorBem.valueOf((String) obj[12]).getDescricao());
                retorno.add(itemRelatorioPatrimonio);
            }
        }
        return retorno;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }
}
