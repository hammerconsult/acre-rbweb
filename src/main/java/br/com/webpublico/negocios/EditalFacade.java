package br.com.webpublico.negocios;

import br.com.webpublico.entidades.AtoLegal;
import br.com.webpublico.entidades.Edital;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.ws.model.WSArquivo;
import br.com.webpublico.ws.model.WSAtoLegal;
import br.com.webpublico.ws.model.WSEdital;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 20/08/15
 * Time: 14:40
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class EditalFacade extends AbstractFacade<Edital> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ArquivoFacade arquivoFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public EditalFacade() {
        super(Edital.class);
    }


    public Integer getProximoNumeroDeEdita(Exercicio exercicio) {
        String sql = " select max(coalesce(numero, 0)) + 1 maior " +
                "  from edital " +
                " where exercicio_id = :id_exercicio ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("id_exercicio", exercicio.getId());
        if (!q.getResultList().isEmpty() && q.getResultList().size() > 0) {
            return q.getResultList().get(0) != null ? ((BigDecimal) q.getResultList().get(0)).intValue() : 1;
        }
        return 1;

    }

    public Edital salvarRetornando(Edital edital) {
        if(edital.getId() == null){
            edital.setNumero(getProximoNumeroDeEdita(edital.getExercicio()));
        }
        return em.merge(edital);
    }

    public WSEdital transformarEditalEmWSEdital(Edital edital) {
        WSEdital wsEdital = new WSEdital();
        wsEdital.setId(edital.getId());
        wsEdital.setExercicio(edital.getExercicio().getAno());
        wsEdital.setNumero(edital.getNumero());
        wsEdital.setDataPublicacao(edital.getDataPublicacao());
        wsEdital.setDataValidade(edital.getDataValidade());
        wsEdital.setTitulo(edital.getTitulo());
        wsEdital.setDescricao(edital.getDescricao());
        return wsEdital;
    }

    public List<WSEdital> consultarEditais(WSEdital filtros, int inicio, int quantidade) {
        List<WSEdital> toReturn = Lists.newArrayList();
        StringBuilder sql = new StringBuilder();
        sql.append(" select edi.* from edital edi ");
        sql.append(" inner join exercicio e on e.id = edi.exercicio_id ");
        String juncao = " where ";
        Map<String, Object> parameters = Maps.newHashMap();
        if (filtros.getExercicio() != null && filtros.getExercicio() > 0) {
            sql.append(juncao).append(" e.ano = :exercicio ");
            juncao = " and ";
            parameters.put("exercicio", filtros.getExercicio());
        }
        if (filtros.getNumero() != null && filtros.getNumero() > 0) {
            sql.append(juncao).append(" edi.numero = :numero ");
            juncao = " and ";
            parameters.put("numero", filtros.getNumero());
        }
        if (filtros.getTitulo() != null && !filtros.getTitulo().isEmpty()) {
            sql.append(juncao).append(" lower(edi.titulo) LIKE CONCAT(lower(:titulo),'%') ");
            juncao = " and ";
            parameters.put("titulo", filtros.getTitulo());
        }
        if (filtros.getDataPublicacao() != null) {
            sql.append(juncao).append(" edi.dataPublicacao = :dataPublicacao ");
            juncao = " and ";
            parameters.put("dataPublicacao", filtros.getDataPublicacao());
        }
        if (filtros.getDataValidade() != null) {
            sql.append(juncao).append(" edi.dataValidade = :dataValidade");
            juncao = " and ";
            parameters.put("dataValidade", filtros.getDataValidade());
        }
        sql.append(" order by edi.id desc ");
        Query q = em.createNativeQuery(sql.toString(), Edital.class);
        for (String key : parameters.keySet()) {
            q.setParameter(key, parameters.get(key));
        }
        q.setFirstResult(inicio);
        q.setMaxResults(quantidade);
        if (!q.getResultList().isEmpty()) {
            for (Edital edital : ((List<Edital>) q.getResultList())) {
                toReturn.add(transformarEditalEmWSEdital(edital));
            }
        }
        return toReturn;
    }

    public WSArquivo recuperaArquivo(Long idEdital) {
        WSArquivo toReturn = null;
        Edital edital = recuperar(idEdital);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            arquivoFacade.recupera(edital.getArquivo().getId(), byteArrayOutputStream);
            toReturn = new WSArquivo();
            toReturn.setNome(edital.getArquivo().getNome());
            toReturn.setMimeType(edital.getArquivo().getMimeType());
            toReturn.setDados(byteArrayOutputStream.toByteArray());
        } catch (Exception e) {
        }
        return toReturn;
    }
}
