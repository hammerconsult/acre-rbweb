package br.com.webpublico.negocios.rh.relatorio;

import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.entidadesauxiliares.rh.relatorio.RelatorioAposentadosPorCargo;
import br.com.webpublico.enums.TipoEventoFP;
import br.com.webpublico.negocios.FolhaDePagamentoFacade;
import br.com.webpublico.util.UtilRelatorioContabil;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by William on 03/04/2018.
 */
@Stateless
public class RelatorioAposentadosPorCargoFacade implements Serializable {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @EJB
    private FolhaDePagamentoFacade folhaDePagamentoFacade;

    public FolhaDePagamentoFacade getFolhaDePagamentoFacade() {
        return folhaDePagamentoFacade;
    }

    public List<RelatorioAposentadosPorCargo> buscarAposentadosPorCargo(List<ParametrosRelatorios> parametros) {
        String sql = "select distinct pf.nome," +
            "      matricula.matricula || '-' || vinculo.numero as matricula," +
            "      cargo.CODIGODOCARGO as codigocargo," +
            "      cargo.DESCRICAO as descricaocargo," +
            "      contrato.DATAADMISSAO as admissao," +
            "      vinculo.INICIOVIGENCIA as aposentadoria," +
            "      categoriapai.descricao as tabela," +
            "      categoria.codigo as nivel," +
            "      progressao.descricao as referencia," +
            "      recurso.DESCRICAO as lotacao, " +
            "      (select sum(item.VALOR) from ITEMFICHAFINANCEIRAFP item  " +
            "          inner join eventofp evento on evento.id = item.EVENTOFP_ID " +
            "          where item.FICHAFINANCEIRAFP_ID = ficha.id and item.TIPOEVENTOFP = :tipoEvento)" +
            " from folhadepagamento folha" +
            " inner join fichafinanceirafp ficha on ficha.FOLHADEPAGAMENTO_ID = folha.id" +
            " inner join aposentadoria on ficha.VINCULOFP_ID = aposentadoria.id" +
            " inner join vinculofp vinculo on vinculo.id = aposentadoria.id" +
            " inner join contratofp contrato on contrato.id = aposentadoria.contratofp_id" +
            " inner join matriculafp matricula on vinculo.MATRICULAFP_ID = matricula.id" +
            " inner join pessoafisica pf on matricula.PESSOA_ID = pf.id" +
            " inner join cargo on cargo.id = contrato.CARGO_ID" +
            " inner join enquadramentofuncional enquadramento on enquadramento.CONTRATOSERVIDOR_ID = contrato.id" +
            " INNER JOIN progressaopcs progressao ON progressao.id = enquadramento.progressaopcs_id" +
            " INNER JOIN categoriapcs categoria ON categoria.id = enquadramento.categoriapcs_id" +
            " INNER JOIN categoriapcs categoriapai ON categoriapai.id = categoria.superior_id" +
            " inner join recursofp recurso on recurso.id = ficha.RECURSOFP_ID " +
            " where enquadramento.iniciovigencia =  (SELECT MAX(e.iniciovigencia) FROM enquadramentofuncional e WHERE e.contratoServidor_id = contrato.id) ";
        sql += UtilRelatorioContabil.concatenarParametros(parametros, 0, " AND ");
        sql += " order by recurso.DESCRICAO, pf.nome ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("tipoEvento", TipoEventoFP.VANTAGEM.name());
        UtilRelatorioContabil.adicionarParametros(parametros, q);
        List<RelatorioAposentadosPorCargo> retorno = new ArrayList<>();
        List<Object[]> resultado = (List<Object[]>) q.getResultList();
        if (!resultado.isEmpty()) {
            for (Object[] obj : resultado) {
                RelatorioAposentadosPorCargo relatorio = new RelatorioAposentadosPorCargo();
                relatorio.setPessoa((String) obj[0]);
                relatorio.setMatricula((String) obj[1]);
                relatorio.setCodigoCargo((String) obj[2]);
                relatorio.setCargo((String) obj[3]);
                relatorio.setAdmissao((Date) obj[4]);
                relatorio.setAposentadoria((Date) obj[5]);
                relatorio.setTabela((String) obj[6]);
                relatorio.setNivel((BigDecimal) obj[7]);
                relatorio.setReferencia((String) obj[8]);
                relatorio.setLotacao((String) obj[9]);
                relatorio.setSalarioBruto((BigDecimal) obj[10]);
                retorno.add(relatorio);
            }
        }
        return retorno;
    }
}
