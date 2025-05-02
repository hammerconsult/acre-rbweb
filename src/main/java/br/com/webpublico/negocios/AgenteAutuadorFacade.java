/*
 * Codigo gerado automaticamente em Fri Feb 11 08:09:57 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.AgenteAutuador;
import br.com.webpublico.entidades.ContratoFP;
import br.com.webpublico.entidades.ModalidadeContratoFP;
import br.com.webpublico.entidades.PessoaFisica;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Arrays;
import java.util.List;

@Stateless
public class AgenteAutuadorFacade extends AbstractFacade<AgenteAutuador> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private SistemaFacade sistemaFacade;

    public AgenteAutuadorFacade() {
        super(AgenteAutuador.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<PessoaFisica> completarPessoasComContratoFP(String parte) {
        List<Long> modalidades = Arrays.asList(ModalidadeContratoFP.CONCURSADOS, ModalidadeContratoFP.CARGO_COMISSAO, ModalidadeContratoFP.AGENTE_POLITICO);
        List<ContratoFP> contratoFPs = contratoFPFacade.recuperaContratoVigentePorModalidades(parte.trim(), modalidades);
        List<PessoaFisica> pessoas = Lists.newArrayList();
        for (ContratoFP contratoFP : contratoFPs) {
            pessoas.add(contratoFP.getMatriculaFP().getPessoa());
        }
        return pessoas;
    }

    public List<AgenteAutuador> buscarAgentesAutuadoresAtivos(String parte) {
        String sql = "select distinct aa.* from AgenteAutuador aa " +
            " inner join PessoaFisica pes on pes.id = aa.pessoafisica_id " +
            " left join DocumentoPessoal docs on docs.pessoafisica_id = pes.id " +
            " left join RG rg on rg.id = docs.id " +
            " inner join MatriculaFP matri on matri.pessoa_id = pes.id " +
            " inner join VinculoFP vinculo on vinculo.matriculafp_id = matri.id " +
            " inner join ContratoFP contrato on contrato.id = vinculo.id " +
            " where ((lower(pes.nome) like :filtro) OR (lower(pes.cpf) like :filtro) or (rg.numero like :filtro) or (matri.matricula like :filtro)) " +
            " and coalesce(aa.ativo,0) = 1  " +
            " and :dataVigencia between vinculo.inicioVigencia and coalesce(vinculo.finalVigencia, :dataVigencia) ";
        Query q = em.createNativeQuery(sql, AgenteAutuador.class);
        q.setParameter("filtro", "%" + parte.trim().toLowerCase() + "%");
        q.setParameter("dataVigencia", Util.getDataHoraMinutoSegundoZerado(sistemaFacade.getDataOperacao()));
        q.setMaxResults(10);
        return q.getResultList();
    }
}
