package br.com.webpublico.negocios.rh.relatorio;

import br.com.webpublico.entidades.Afastamento;
import br.com.webpublico.entidadesauxiliares.RelatorioServidoresQueEntraramOuSairamDaFPComMotivo;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.TipoFolhaDePagamento;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Lists;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;

/**
 * Created by William on 17/06/2019.
 */
@Stateless
public class RelatorioServidoresQueEntraramOuSairamDaFPComMotivoFacade extends AbstractFacade<RelatorioServidoresQueEntraramOuSairamDaFPComMotivo> {
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public RelatorioServidoresQueEntraramOuSairamDaFPComMotivoFacade() {
        super(RelatorioServidoresQueEntraramOuSairamDaFPComMotivo.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<RelatorioServidoresQueEntraramOuSairamDaFPComMotivo> buscarDadosRelatorio(Date dataRelatorio, Integer ano, Mes mes, TipoFolhaDePagamento tipoFolha, Integer versao,
                                                                                          Integer anoAnterior, Mes mesAnterior, Boolean entraramNaFolha, String orgao) {
        String sql = " SELECT UNIQUE v.id, m.matricula, " +
            "  v.numero, " +
            "  pf.nome, " +
            "  sf.descricao AS situacaofuncional " +
            " FROM pessoafisica pf " +
            " JOIN matriculafp m ON m.pessoa_id = pf.id " +
            " JOIN vinculofp v ON v.matriculafp_id = m.id " +
            " JOIN situacaocontratofp sc ON sc.contratofp_id   = v.id " +
            " JOIN lotacaofuncional lot ON lot.vinculofp_id = v.id " +
            " JOIN unidadeorganizacional uo ON uo.id = lot.unidadeorganizacional_id " +
            " JOIN vwhierarquiaadministrativa ho on ho.subordinada_id = uo.id " +
            " JOIN situacaofuncional sf ON sf.id    = sc.situacaofuncional_id " +
            " WHERE v.id IN " +
            "  (SELECT distinct ficha.vinculofp_id " +
            "  FROM fichafinanceirafp ficha " +
            "  JOIN folhadepagamento folha   ON ficha.folhadepagamento_id   = folha.id " +
            "  inner join itemfichafinanceirafp iff on iff.FICHAFINANCEIRAFP_ID = ficha.id " +
            "  WHERE folha.ano                = :ano " +
            "  AND folha.mes                  = :mes  " +
            "  AND folha.tipofolhadepagamento = :tipoFolha " +
            "  AND folha.versao               = :versao " +
            " AND sc.id = " +
            "  (SELECT MAX(situacaocontratofp.id) " +
            "  FROM situacaocontratofp " +
            "  WHERE situacaocontratofp.contratofp_id = sc.contratofp_id " +
            "  and to_date(to_char(:dataRelatorio, 'MM/yyyy'), 'MM/yyyy') between        to_date(to_char(situacaocontratofp.iniciovigencia, 'MM/yyyy'), 'MM/yyyy') " +
            "  and to_date(to_char(coalesce(situacaocontratofp.FINALVIGENCIA, sysdate), 'MM/yyyy'), 'MM/yyyy') " +
            "  ) " +
            "  AND ficha.vinculofp_id NOT    IN " +
            "    (SELECT distinct ficha.vinculofp_id " +
            "    FROM fichafinanceirafp ficha " +
            "    JOIN folhadepagamento folha    ON ficha.folhadepagamento_id   = folha.id " +
            "    inner join itemfichafinanceirafp iffold on iffold.FICHAFINANCEIRAFP_ID = ficha.id " +
            "    WHERE folha.ano                = :comparacaoAno " +
            "    AND folha.mes                  = :comparacaoMes " +
            "    AND folha.tipofolhadepagamento = :tipoFolha " +
            "    ) " +
            "  ) ";
        if (orgao != null) {
            sql += orgao;
        }
        sql += " ORDER BY pf.nome";

        Date inicioParametro = null;
        Date finalParametro = null;
        inicioParametro = DataUtil.montaData(1, mesAnterior.getNumeroMesIniciandoEmZero(), anoAnterior).getTime();
        finalParametro = DataUtil.montaData(30, mes.getNumeroMesIniciandoEmZero(), ano).getTime();
        Query q = em.createNativeQuery(sql);
        q.setParameter("ano", ano);
        q.setParameter("mes", mes.getNumeroMesIniciandoEmZero());
        q.setParameter("tipoFolha", tipoFolha.name());
        q.setParameter("versao", versao);
        q.setParameter("comparacaoAno", anoAnterior);
        q.setParameter("comparacaoMes", mesAnterior.getNumeroMesIniciandoEmZero());
        q.setParameter("dataRelatorio", dataRelatorio);
        List<RelatorioServidoresQueEntraramOuSairamDaFPComMotivo> itemRelatorio = Lists.newArrayList();

        if (!q.getResultList().isEmpty()) {
            for (Object[] obj : (List<Object[]>) q.getResultList()) {
                RelatorioServidoresQueEntraramOuSairamDaFPComMotivo relatorio = new RelatorioServidoresQueEntraramOuSairamDaFPComMotivo();
                relatorio.setIdVinculo(Long.parseLong(obj[0].toString()));
                relatorio.setMatricula((String) obj[1]);
                relatorio.setNumero((String) obj[2]);
                relatorio.setNome((String) obj[3]);
                relatorio.setSituacaoFuncional((String) obj[4]);
                relatorio.setMotivo(tratarMotivoEntrarOuSairFolhaPagamento(Long.parseLong(obj[0].toString()), inicioParametro, finalParametro, entraramNaFolha, relatorio.getSituacaoFuncional()));
                itemRelatorio.add(relatorio);
            }
        }
        return itemRelatorio;
    }

    private String tratarMotivoEntrarOuSairFolhaPagamento(Long idVinculo, Date dataInicial, Date dataFinal, Boolean entraramNaFolha, String situacaoFuncional) {
        if (isNomeado(idVinculo, dataInicial, dataFinal)) {
            return "NOMEADO";
        }
        Afastamento afastamento = recuperarAfastamento(idVinculo, dataInicial, dataFinal);
        if (afastamento != null) {
            return "AFASTAMENTO - " + afastamento.getTipoAfastamento().getDescricao();
        }
        if (!entraramNaFolha) {
            return situacaoFuncional;
        }
        return "MOTIVO NÃO ENCONTRADO";
    }

    private boolean isNomeado(Long idVinculo, Date dataInicial, Date dataFinal) {
        String sql = "select * from vinculofp " +
            "where id = :idVinculo and INICIOVIGENCIA between :inicio and :fim";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idVinculo", idVinculo);
        q.setParameter("inicio", DataUtil.dataSemHorario(dataInicial));
        q.setParameter("fim", DataUtil.dataSemHorario(dataFinal));
        return !q.getResultList().isEmpty();
    }

    private Afastamento recuperarAfastamento(Long idVinculo, Date dataInicial, Date dataFinal) {
        String sql = " select afastamento.* from vinculofp vinculo " +
            " inner join afastamento on afastamento.CONTRATOFP_ID = vinculo.id " +
            " inner join TIPOAFASTAMENTO tipo on afastamento.TIPOAFASTAMENTO_ID = tipo.id " +
            " where vinculo.id = :idVinculo " +
            " and (afastamento.DATARETORNOINFORMADO between :inicio and :fim or afastamento.DATARETORNOINFORMADO between :inicio -30 and :fim)" +
            " and tipo.DESCONTARDIASTRABALHADOS = :descontaDias";
        Query q = em.createNativeQuery(sql, Afastamento.class);
        q.setParameter("idVinculo", idVinculo);
        q.setParameter("inicio", DataUtil.dataSemHorario(dataInicial));
        q.setParameter("fim", DataUtil.dataSemHorario(dataFinal));
        q.setParameter("descontaDias", Boolean.TRUE);
        if (!q.getResultList().isEmpty()) {
            return (Afastamento) q.getResultList().get(0);
        }
        return null;
    }
}


