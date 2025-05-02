package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Fornecedor;
import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.enums.SituacaoCadastralPessoa;
import br.com.webpublico.singletons.SingletonAdministrativo;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created with IntelliJ IDEA.
 * User: JoãoPaulo
 * Date: 26/06/14
 * Time: 09:01
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class FornecedorFacade extends AbstractFacade<Fornecedor> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private DoctoHabilitacaoFacade doctoHabilitacaoFacade;
    @EJB
    private CNAEFacade cnaeFacade;
    @EJB
    private ArquivoFacade arquivoFacade;
    @EJB
    private DocumentoOficialFacade documentoOficialFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private SingletonAdministrativo singletonAdministrativo;
    @EJB
    private PessoaJuridicaFacade pessoaJuridicaFacade;


    public FornecedorFacade() {
        super(Fornecedor.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public Fornecedor recuperar(Object id) {
        Fornecedor f = super.recuperar(id);
        f.getDocumentosFornecedor().size();
        if (f.getDetentorArquivoComposicao() != null) {
            f.getDetentorArquivoComposicao().getArquivosComposicao().size();
        }
        return f;
    }

    public void salvar(Fornecedor selecionado) {
        if (selecionado.getId() == null) {
            selecionado.setInscricaoNoSicaRb(singletonAdministrativo.gerarNumeroInscricaoSicaRb());
            selecionado.setCertificado(singletonAdministrativo.gerarNumeroCertificado());
            super.salvarNovo(selecionado);
        }
        super.salvar(selecionado);
    }

    public Fornecedor buscarFornecedorPorPessoa(Pessoa pessoa) {
        if(pessoa == null){
            return null;
        }
        String sql = " Select f.* from fornecedor f where f.pessoa_id = :pessoa";
        Query q = em.createNativeQuery(sql, Fornecedor.class);
        q.setParameter("pessoa", pessoa.getId());
        try {
            return recuperar(((Fornecedor) q.getSingleResult()).getId());
        } catch (Exception no) {
            return null;
        }
    }

    @Override
    public void remover(Fornecedor selecionado) {
        super.remover(selecionado);
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public PessoaFacade getPessoaFacade() {
        return pessoaFacade;
    }

    public DoctoHabilitacaoFacade getDoctoHabilitacaoFacade() {
        return doctoHabilitacaoFacade;
    }

    public CNAEFacade getCnaeFacade() {
        return cnaeFacade;
    }

    public ArquivoFacade getArquivoFacade() {
        return arquivoFacade;
    }

    public DocumentoOficialFacade getDocumentoOficialFacade() {
        return documentoOficialFacade;
    }

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }

    public SingletonAdministrativo getSingletonAdministrativo() {
        return singletonAdministrativo;
    }

    public PessoaJuridicaFacade getPessoaJuridicaFacade() {
        return pessoaJuridicaFacade;
    }

    public void salvarMerge(Fornecedor entity) {
        super.salvar(entity);
    }

    public List<Pessoa> listaTodosFornecedoresExcetoUnidadeExterna(String filtro, SituacaoCadastralPessoa... situacoes) {
        ArrayList<Pessoa> retorno = Lists.newArrayList();
        retorno.addAll(pessoaFacade.listaPessoasFisicas(filtro, situacoes));
        retorno.addAll(listaPessoasJuridicas(filtro, situacoes));
        return retorno;
    }

    public List<Pessoa> listaPessoasJuridicas(String filtro, SituacaoCadastralPessoa... situacoes) {
        List<Pessoa> lista = Lists.newArrayList();
        if (situacoes == null) {
            situacoes = SituacaoCadastralPessoa.values();
        }
        List<String> filtros = Lists.newArrayList(filtro.split("\\s+"));
        String sql = "select obj from PessoaJuridica obj where ";
        sql += " ((lower(translate(obj.razaoSocial,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')) like translate(:filtro,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc') ";
        sql += " or lower(translate(obj.nomeFantasia,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')) like translate(:filtro,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc') ";
        sql += " or replace(replace(replace(obj.cnpj,'.',''),'-',''),'/','') like :cpfCnpj))";
        sql += " and (obj.id not in (select ue.pessoaJuridica.id from UnidadeExterna ue))";
        if (situacoes != null && situacoes.length > 0 && situacoes[0] != null) {
            sql += " and obj.situacaoCadastralPessoa in (:situacoes)";
        }
        Query q = em.createQuery(sql);
        q.setParameter("filtro", "%" + filtro.toLowerCase() + "%");
        q.setParameter("cpfCnpj", "%" + filtro.toLowerCase().replace(".", "").replace("-", "").replace("/", "") + "%");
        if (situacoes != null && situacoes.length > 0 && situacoes[0] != null) {
            q.setParameter("situacoes", Lists.newArrayList(situacoes));
        }
        lista.addAll(q.setMaxResults(10).getResultList());
        if (lista.isEmpty() || lista.size() < 10) {
            sql = "select obj from PessoaJuridica obj where ";
            sql += filtros.stream().map(f -> "  (lower(translate(obj.razaoSocial,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')) like translate(:filtro" + filtros.indexOf(f) + ",'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc') or " +
            "  lower(translate(obj.nomeFantasia,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')) like translate(:filtro" + filtros.indexOf(f) + ",'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')) ").collect(Collectors.joining(" and "));
            sql += " and (obj.id not in (select ue.pessoaJuridica.id from UnidadeExterna ue))";

            if (situacoes != null && situacoes.length > 0 && situacoes[0] != null) {
                sql += " and obj.situacaoCadastralPessoa in (:situacoes)";
            }
            if (!lista.isEmpty()) {
                sql += " and obj not in (:pessoas)";
            }
            q = em.createQuery(sql);
            for (String f : filtros) {
                q.setParameter("filtro" + filtros.indexOf(f), "%" + f.toLowerCase() + "%");
            }
            if (situacoes != null && situacoes.length > 0 && situacoes[0] != null) {
                q.setParameter("situacoes", Lists.newArrayList(situacoes));
            }
            if (!lista.isEmpty()) {
                q.setParameter("pessoas", lista);
            }
            lista.addAll(q.setMaxResults(10).getResultList());
        }
        return lista;
    }
}
