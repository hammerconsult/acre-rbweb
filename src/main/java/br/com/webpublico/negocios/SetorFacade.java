/*
 * Codigo gerado automaticamente em Wed Mar 23 09:14:21 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Cidade;
import br.com.webpublico.entidades.Distrito;
import br.com.webpublico.entidades.Setor;
import br.com.webpublico.util.ResultadoValidacao;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.List;

@Stateless
public class SetorFacade extends AbstractFacade<Setor> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ConfiguracaoTributarioFacade configuracaoTributarioFacade;
    @EJB
    private DistritoFacade distritoFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SetorFacade() {
        super(Setor.class);
    }

    public DistritoFacade getDistritoFacade() {
        return distritoFacade;
    }

    public ConfiguracaoTributarioFacade getConfiguracaoTributarioFacade() {
        return configuracaoTributarioFacade;
    }

    public List<Setor> listaFiltrandoPorCidade(String parte, Cidade cidade) {
        if (cidade == null) {
            return this.listaFiltrando(parte.trim(), "nome");
        } else {
            Query q = em.createQuery("from Setor s where s.cidade = :cidade and s.nome like :nome");
            q.setParameter("cidade", cidade);
            q.setParameter("nome", "%" + parte.trim() + "%");
            return q.getResultList();
        }
    }

    public Setor recuparaSetorPorCidade(String codigo, Cidade cidade) {
        Query q = em.createQuery("from Setor s where s.cidade = :cidade and s.codigo = :codigo ");
        q.setParameter("cidade", cidade);
        q.setParameter("codigo", codigo);
        if (q.getResultList().size() > 0) {
            return (Setor) q.getResultList().get(0);
        } else {
            return null;
        }
    }


    public Setor recuperarSetorPorCodigo(String codigo) {
        String hql = "from Setor s where s.codigo = :codigo or to_number(s.codigo) = :codigoNumero";
        Query q = em.createQuery(hql);
        q.setParameter("codigo", codigo);
        q.setParameter("codigoNumero", new BigDecimal(codigo));
        if (q.getResultList().size() > 0) {
            return (Setor) q.getResultList().get(0);
        } else {
            return null;
        }
    }

    public List<Setor> recuperarSetores() {
        Query q = this.em.createQuery("select s from Setor s order by s.codigo");
        return q.getResultList();
    }

    public ResultadoValidacao salvaNovo(Setor setor) {
        ResultadoValidacao resultado = validaRegrasDeNegocio(setor);

        if (resultado.isValidado()) {
            super.salvarNovo(setor);
        }

        return resultado;
    }

    public ResultadoValidacao salva(Setor setor) {
        ResultadoValidacao resultado = validaRegrasDeNegocio(setor);

        if (resultado.isValidado()) {
            super.salvar(setor);
        }

        return resultado;
    }

    private ResultadoValidacao validaRegrasDeNegocio(Setor setor) {
        ResultadoValidacao resultado = new ResultadoValidacao();
        final String summary = "Impossível Continuar!";
        int numeroDigitos = configuracaoTributarioFacade.retornaUltimo().getNumDigitosSetor();

        if (setor.getCodigo() == null) {
            resultado.addErro(null, summary, "O Código deve ser maior que zero!");
        } else {
            if (setor.getCodigo().toString().length() > numeroDigitos) {
                resultado.addErro(null, summary, "Nas Configurações do Tributário, o tamanho máximo para o Código do Setor é de " + numeroDigitos + " dígito(s).");
            }

            try {
                if (setor.getId() == null && recuperarSetorPorCodigo(setor.getCodigo()) != null) {
                    resultado.addErro(null, summary, "O Código informado já esta sendo utilizado por outro setor!");
                }
            } catch (NoResultException nre) {
            }
        }

        if (setor.getNome() == null || setor.getNome().trim().isEmpty()) {
            resultado.addErro(null, summary, "Informe o Nome.");
        }


        return resultado;
    }

    public Integer ultimoNumeroMaisUm() {
        Query q1 = em.createNativeQuery("select coalesce(max(to_number(codigo)), 0) + 1 as numero from Setor");
        BigDecimal retorno = (BigDecimal) q1.getSingleResult();
        return retorno.intValue();
    }

    public boolean existeSetorComMesmoCodigo(Setor setor) {
        StringBuilder sql = new StringBuilder(" select s.* from setor s ");
        sql.append(" inner join distrito d on d.id = s.distrito_id and d.id = :distrito");
        sql.append(" where s.codigo = :codigo ");
        if (setor.getId() != null) {
            sql.append(" and s.id <> :id ");
        }
        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("codigo", setor.getCodigo());
        q.setParameter("distrito", setor.getDistrito().getId());
        if (setor.getId() != null) {
            q.setParameter("id", setor.getId());
        }


        return !q.getResultList().isEmpty();
    }

    public List<Setor> listaSetoresOrdenadosPorCodigo() {
        String sql = "select * from setor order by codigo";
        Query q = em.createNativeQuery(sql, Setor.class);
        return q.getResultList();
    }

    public boolean existeSetorComMesmoNome(Long id, String nome) {
        StringBuilder sql = new StringBuilder("select * from setor s where upper(replace(s.nome, ' ', '')) = :nome");
        if (id != null) {
            sql.append(" and  id <> :id");
        }
        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("nome", nome.replaceAll(" ", "").toUpperCase());
        if (id != null) {
            q.setParameter("id", id);
        }
        if (q.getResultList().isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    public boolean existeEsteSetorNoDistrito(Setor setor) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select s.* from distrito d");
        sql.append(" inner join setor s on s.distrito_id = d.id and d.id = :distrito");
        sql.append(" where s.nome = :nomeSetor or s.codigo = :codigoSetor");

        Query q = em.createNativeQuery(sql.toString(), Distrito.class);
        q.setParameter("nomeSetor", setor.getNome());
        q.setParameter("codigoSetor", setor.getCodigo());
        q.setParameter("distrito", setor.getDistrito().getId());


        return !q.getResultList().isEmpty();
    }

    public List<Setor> buscarPorIntervaloDeSetor(String setorInicial, String setorFinal) {
        String sql = " select * from setor s ";
        String juncao = " where ";
        if (setorInicial != null && !setorInicial.isEmpty()) {
            sql += juncao;
            sql += " cast(s.codigo as int) >= :inicial ";
            juncao = " and ";
        }
        if (setorFinal != null && !setorFinal.isEmpty()) {
            sql += juncao;
            sql += " cast(s.codigo as int) <= :final ";
        }
        sql += " order by s.codigo ";
        Query q = em.createNativeQuery(sql, Setor.class);
        if (setorInicial != null && !setorInicial.isEmpty()) {
            q.setParameter("inicial", Integer.parseInt(setorInicial));
        }
        if (setorFinal != null && !setorFinal.isEmpty()) {
            q.setParameter("final", Integer.parseInt(setorFinal));
        }
        q.setMaxResults(1000);
        return q.getResultList();
    }

}
