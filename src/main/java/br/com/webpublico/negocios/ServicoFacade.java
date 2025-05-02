/*
 * Codigo gerado automaticamente em Tue Feb 22 10:41:19 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.CadastroEconomico;
import br.com.webpublico.entidades.Servico;
import br.com.webpublico.nfse.domain.dtos.ServicoNfseDTO;
import br.com.webpublico.util.StringUtil;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.Hibernate;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Stateless
public class ServicoFacade extends AbstractFacade<Servico> {

    private static final BigDecimal CEM = new BigDecimal("100");
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ServicoFacade() {
        super(Servico.class);
    }

    public boolean jaExisteServicoComCodigo(Servico servico) {
        String hql = "from Servico s where s.codigo = :codigo";
        if (servico.getId() != null) {
            hql += " and s != :servico";
        }
        Query q = em.createQuery(hql);
        q.setParameter("codigo", servico.getCodigo());
        if (servico.getId() != null) {
            q.setParameter("servico", servico);
        }
        return !q.getResultList().isEmpty();

    }

    @Override
    public Servico recuperar(Object id) {
        Servico servico = super.recuperar(id);
        Hibernate.initialize(servico.getServicos());
        return servico;
    }

    public Servico buscarPorCodigo(String codigo) {
        return buscarPorCodigo(codigo, null);
    }

    public Servico buscarPorCodigo(String codigo, Boolean liberadoNfse) {
        String hql = "from Servico s where s.codigo = :codigo and ativo =:ativo ";
        if (liberadoNfse != null) {
            hql += " and s.liberadoNfse = :liberadoNfse ";
        }
        Query q = em.createQuery(hql);
        q.setParameter("codigo", StringUtil.removeCaracteresEspeciaisSemEspaco(codigo));
        q.setParameter("ativo", Boolean.TRUE);
        if (liberadoNfse != null) {
            q.setParameter("liberadoNfse", liberadoNfse);
        }
        if (!q.getResultList().isEmpty()) {
            return (Servico) q.getResultList().get(0);
        }
        return null;
    }

    public List<Servico> listaNaoVinculadosAoCadastro(CadastroEconomico selecionado) {
        String hql = "select s from Servico s, CadastroEconomico ce where s not in elements(ce.servico) and ce = :ce";
        Query q = em.createQuery(hql);
        q.setParameter("ce", selecionado);
        return q.getResultList();
    }

    public List<Servico> completaServico(String parte) {
        String hql = "SELECT s.* " +
            "   FROM Servico s " +
            "  WHERE s.ativo = :ativo " +
            "     and (lower(s.nome) LIKE :parte OR lower(s.codigo) = :numero) ";
        Query q = em.createNativeQuery(hql, Servico.class);
        q.setParameter("ativo", Boolean.TRUE);
        q.setParameter("parte", "%" + parte.toLowerCase() + "%");
        q.setParameter("numero", parte.toLowerCase());
        q.setMaxResults(10);
        return q.getResultList();
    }

    public List<Servico> completaServicoInstituicaoFinanceira(String parte) {
        String hql = "SELECT s.* " +
            "   FROM Servico s " +
            "  WHERE s.ativo = :ativo and s.instituicaofinanceira =:instituicaofinanceira " +
            "     and (lower(s.nome) LIKE :parte OR lower(s.codigo) = :numero) ";
        Query q = em.createNativeQuery(hql, Servico.class);
        q.setParameter("instituicaofinanceira", Boolean.TRUE);
        q.setParameter("ativo", Boolean.TRUE);
        q.setParameter("parte", "%" + parte.toLowerCase() + "%");
        q.setParameter("numero", parte.toLowerCase());
        q.setMaxResults(10);
        return q.getResultList();
    }

    public List<Servico> listaPorCadastroEconomico(CadastroEconomico cadastroEconomico) {
        String sql = "SELECT * FROM servico s "
            + "JOIN cadastroeconomico_servico ces ON s.id = ces.servico_id "
            + "AND ces.cadastroeconomico_id = :cadastroeconomico "
            + "ORDER BY s.aliquotaisshomologado";

        Query q = em.createNativeQuery(sql, Servico.class);
        q.setParameter("cadastroeconomico", cadastroEconomico.getId());
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return new ArrayList<Servico>();
    }

    public List<Servico> listaComWhere(String where) {
        String sql = " SELECT * FROM servico s "
            + where;

        Query q = em.createNativeQuery(sql, Servico.class);
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return new ArrayList<Servico>();
    }

    public List<Servico> listarPorOrdemAlfabetica() {
        String sql = " SELECT * FROM servico s where s.ativo = :ativo order by s.nome ";
        Query q = em.createNativeQuery(sql, Servico.class);
        q.setParameter("ativo", Boolean.TRUE);
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return new ArrayList<>();
    }

    public Servico createAndSave(ServicoNfseDTO dto) {
        Servico servico = em.find(Servico.class, dto.getId());
        if (servico == null) {
            servico = new Servico(dto.getCodigo(), dto.getDescricao(), dto.getAliquota());
            servico = em.merge(servico);
        }
        return servico;
    }

    public List<Servico> buscarServicoPorParteAndCnaes(String parte, List<Long> cnaes) {
        String sql = "select obj.* from Servico obj " +
            " inner join ServicoCNAE scnae on scnae.servico_id = obj.id" +
            " where obj.ativo = :ativo and (lower(to_char(obj.nome)) like :filtro or  lower(to_char(obj.codigo)) like :filtro )";
        sql += " and scnae.cnae_id in (:idsCnaes)";

        Query q = getEntityManager().createNativeQuery(sql, Servico.class);
        q.setParameter("ativo", Boolean.TRUE);
        q.setParameter("filtro", "%" + parte.toLowerCase() + "%");
        q.setParameter("idsCnaes", cnaes);
        q.setMaxResults(10);
        return q.getResultList();
    }

    public List<Servico> buscarServicoPorCodigoOrDescricao(String parte) {
        StringBuilder sql = new StringBuilder();
        sql.append("select obj.* from Servico obj ")
            .append(" where obj.ativo = :ativo and (lower(to_char(obj.nome)) like :filtro or lower(to_char(obj.codigo)) like :filtro) ")
            .append(" order by obj.codigo ");
        Query q = getEntityManager().createNativeQuery(sql.toString(), Servico.class);
        q.setParameter("ativo", Boolean.TRUE);
        q.setParameter("filtro", "%" + parte.trim().toLowerCase() + "%");
        return q.getResultList();
    }

    public List<Servico> buscarServicoPorCnaeAndCadastroEconomico(Long cadastroId, Long cnaeId) {
        String sql = "select distinct eco.* from cadastroeconomico_servico eco " +
            "    inner join servico serv on  serv.id = eco.servico_id " +
            "    inner join servicocnae scane on serv.id = scane.servico_id " +
            "    inner join cnae on cnae.id = scane.cnae_id " +
            "    where cnae.id = :cnaeId and eco.cadastroeconomico_id = :cadastroId ";
        Query q = getEntityManager().createNativeQuery(sql, Servico.class);
        q.setParameter("cnaeId", cnaeId);
        q.setParameter("cadastroId", cadastroId);
        q.setMaxResults(10);
        return q.getResultList();
    }

    public void importarPlanilhaServicos(InputStream inputStream) throws IOException, InvalidFormatException {
        OPCPackage pkg = OPCPackage.open(inputStream);
        XSSFWorkbook wb = new XSSFWorkbook(pkg);
        XSSFSheet sheet = wb.getSheetAt(0);
        Iterator<Row> iterator = sheet.iterator();
        //desconsidera o cabeçalho
        if (iterator.hasNext()) {
            iterator.next();
        }
        while (iterator.hasNext()) {
            Row row = iterator.next();
            String codigo = String.valueOf(new Double(row.getCell(0).getNumericCellValue()).intValue());
            String descricao = row.getCell(1).getStringCellValue();
            BigDecimal aliquotaIss = row.getCell(2) != null ? new BigDecimal(row.getCell(2).getNumericCellValue()).multiply(CEM) : BigDecimal.ZERO;
            Boolean permiteDeducao = row.getCell(3).getStringCellValue() != null && row.getCell(3).getStringCellValue().equalsIgnoreCase("Sim");
            BigDecimal percentualDeducao = row.getCell(4) != null ? new BigDecimal(row.getCell(4).getNumericCellValue()).multiply(CEM) : BigDecimal.ZERO;
            Boolean construcaoCivil = row.getCell(5).getStringCellValue() != null && row.getCell(5).getStringCellValue().equalsIgnoreCase("Sim");
            Boolean recolhimentoFora = row.getCell(6).getStringCellValue() != null && row.getCell(6).getStringCellValue().equalsIgnoreCase("Sim");
            Boolean ativo = row.getCell(7).getStringCellValue() != null && row.getCell(7).getStringCellValue().equalsIgnoreCase("Sim");

            Servico servico = buscarPorCodigo(codigo);
            if (servico != null) {
                servico.setNome(descricao);
                servico.setAliquotaISSHomologado(aliquotaIss);
                servico.setPermiteDeducao(permiteDeducao);
                servico.setPercentualDeducao(percentualDeducao);
                servico.setConstrucaoCivil(construcaoCivil);
                servico.setPermiteRecolhimentoFora(recolhimentoFora);
                servico.setAtivo(ativo);
                salvar(servico);
            }
        }
    }
}
