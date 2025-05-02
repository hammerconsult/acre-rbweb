/*
 * Codigo gerado automaticamente em Thu Mar 31 17:21:35 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ConsultaTermoInscricaoDividaAtiva;
import br.com.webpublico.enums.TipoCadastroTributario;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Stateless
public class TermoInscricaoDividaAtivaFacade extends AbstractFacade<TermoInscricaoDividaAtiva> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private DocumentoOficialFacade documentoOficialFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private CadastroImobiliarioFacade cadastroImobiliarioFacade;
    @EJB
    private CadastroEconomicoFacade cadastroEconomicoFacade;
    @EJB
    private CadastroRuralFacade cadastroRuralFacade;
    @EJB
    private ContratoRendasPatrimoniaisFacade contratoRendasPatrimoniaisFacade;
    @EJB
    private DividaFacade dividaFacade;
    @EJB
    private LivroDividaAtivaFacade livroDividaAtivaFacade;
    @EJB
    private PessoaFacade pessoa;
    @EJB
    private ParametrosDividaAtivaFacade parametrosDividaAtivaFacade;
    @EJB
    private ConsultaDebitoFacade consultaDebitoFacade;

    public TermoInscricaoDividaAtivaFacade() {
        super(TermoInscricaoDividaAtiva.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CadastroEconomicoFacade getCadastroEconomicoFacade() {
        return cadastroEconomicoFacade;
    }

    public CadastroImobiliarioFacade getCadastroImobiliarioFacade() {
        return cadastroImobiliarioFacade;
    }

    public CadastroRuralFacade getCadastroRuralFacade() {
        return cadastroRuralFacade;
    }

    public ContratoRendasPatrimoniaisFacade getContratoRendasPatrimoniaisFacade() {
        return contratoRendasPatrimoniaisFacade;
    }

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }

    public PessoaFacade getPessoa() {
        return pessoa;
    }

    public List<CadastroImobiliario> completaCadastroImobiliario(String parte) {
        return cadastroImobiliarioFacade.listaFiltrando(parte.trim(), "inscricaoCadastral");
    }

    public List<CadastroEconomico> completaCadastroEconomico(String parte) {
        return cadastroEconomicoFacade.buscarCadastrosPorInscricaoOrCpfCnpjOrNome(parte.trim());
    }

    public List<CadastroRural> completaCadastroRural(String parte) {
        return cadastroRuralFacade.listaFiltrandoPorCodigo(parte.trim());
    }

    public List<ContratoRendasPatrimoniais> completaContratolRendasPatrimonial(String parte) {
        return contratoRendasPatrimoniaisFacade.listaFiltrando(parte.trim(), "numeroContrato");
    }

    public DividaFacade getDividaFacade() {
        return dividaFacade;
    }

    public DocumentoOficialFacade getDocumentoOficialFacade() {
        return documentoOficialFacade;
    }

    public LivroDividaAtivaFacade getLivroDividaAtivaFacade() {
        return livroDividaAtivaFacade;
    }

    public ParametrosDividaAtivaFacade getParametrosDividaAtivaFacade() {
        return parametrosDividaAtivaFacade;
    }

    public ConsultaDebitoFacade getConsultaDebitoFacade() {
        return consultaDebitoFacade;
    }

    @Override
    public TermoInscricaoDividaAtiva recuperar(Object id) {
        TermoInscricaoDividaAtiva termo = em.find(TermoInscricaoDividaAtiva.class, id);
        return termo;
    }

    public Long recuperaProximoCodigoTermoInscricaoDividaAtiva() {
        Query consulta = em.createQuery("select max(termo.numero) from TermoInscricaoDividaAtiva termo");
        if (consulta.getSingleResult() != null) {
            String retorno = (String) consulta.getSingleResult();
            return (Long.parseLong(retorno)) + 1l;
        }
        return (long) 1;
    }

    public LinhaDoLivroDividaAtiva recuperaLinhaDoTermoDividaAtiva(TermoInscricaoDividaAtiva termo) {
        Query consulta = em.createQuery("select linha from LinhaDoLivroDividaAtiva linha where linha.termoInscricaoDividaAtiva = :termo");
        consulta.setParameter("termo", termo);
        return (LinhaDoLivroDividaAtiva) consulta.getSingleResult();
    }

    public List<ConsultaTermoInscricaoDividaAtiva> filtrar(TipoCadastroTributario tipoCadastroTributario, String cadastroInicial, String cadastroFinal, String exercicio, Integer livroDividaAtiva, String termoDividaAtivaInicial, String termoDividaAtivaFinal, Date dataInscricaoInicial, Date dataInscricaoFinal, Pessoa contribuinte, List<Divida> dividas, int max, int inicio) {

        String hql = "select distinct new br.com.webpublico.entidadesauxiliares.ConsultaTermoInscricaoDividaAtiva(cad, pessoa, "
                + " divida,"
                + " inscricao,"
                + " termo,"
                + " linha)"
                + " from LinhaDoLivroDividaAtiva linha"
                + " inner join linha.itemInscricaoDividaAtiva item"
                + " inner join linha.itemLivroDividaAtiva itemlivro"
                + " inner join linha.termoInscricaoDividaAtiva termo"
                + " inner join item.inscricaoDividaAtiva inscricao"
                + " inner join item.divida divida"
                + " left join item.cadastro cad"
                + " left join item.pessoa pessoa"
                + " where 1=1 ";
        if (tipoCadastroTributario != null) {
            hql += "and inscricao.tipoCadastroTributario = :tipo ";
            if (cadastroInicial != null && cadastroInicial.trim().length() > 0 && tipoCadastroTributario.equals(TipoCadastroTributario.ECONOMICO)) {
                hql += "and cad.id in (select cadE.id from CadastroEconomico cadE where cadE.inscricaoCadastral >= :cadastroInicial)";
            }
            if (cadastroFinal != null && cadastroFinal.trim().length() > 0 && tipoCadastroTributario.equals(TipoCadastroTributario.ECONOMICO)) {
                hql += "and cad.id in (select cadE.id from CadastroEconomico cadE where cadE.inscricaoCadastral <= :cadastroFinal)";
            }
            if (cadastroInicial != null && cadastroInicial.trim().length() > 0 && tipoCadastroTributario.equals(TipoCadastroTributario.IMOBILIARIO)) {
                hql += "and cad.id in (select cadI.id from CadastroImobiliario cadI where cadI.inscricaoCadastral >= :cadastroInicial)";
            }
            if (cadastroFinal != null && cadastroFinal.trim().length() > 0 && tipoCadastroTributario.equals(TipoCadastroTributario.IMOBILIARIO)) {
                hql += "and cad.id in (select cadI.id from CadastroImobiliario cadI where cadI.inscricaoCadastral <= :cadastroFinal)";
            }
            if (cadastroInicial != null && cadastroInicial.trim().length() > 0 && tipoCadastroTributario.equals(TipoCadastroTributario.RURAL)) {
                hql += "and cad.id in (select cadR.id from CadastroRural cadR where to_char(cadR.codigo) >= :cadastroInicial)";
            }
            if (cadastroFinal != null && cadastroFinal.trim().length() > 0 && tipoCadastroTributario.equals(TipoCadastroTributario.RURAL)) {
                hql += "and cad.id in (select cadR.id from CadastroRural cadR where to_char(cadR.codigo) <= :cadastroFinal)";
            }
        }
        if (livroDividaAtiva != null) {
            hql += " and itemLivro.livroDividaAtiva.numero = :livro";
        }
        if (termoDividaAtivaInicial != null && termoDividaAtivaInicial.trim().length() > 0) {
            hql += " and termo.numero >= :termoInicial";
        }
        if (termoDividaAtivaFinal != null && termoDividaAtivaFinal.trim().length() > 0) {
            hql += " and termo.numero <= :termoFinal";
        }
        if (dataInscricaoInicial != null) {
            hql += " and inscricao.dataInscricao >= :dataInicial";
        }
        if (dataInscricaoFinal != null) {
            hql += " and inscricao.dataInscricao <= :dataFinal";
        }
        if (contribuinte != null) {
            hql += " and pessoa = :pessoa";
        }
        if (exercicio != null && exercicio.trim().length() > 0) {
            hql += " and inscricao.exercicio.ano = :exercicio";
        }
        if (dividas != null && dividas.size() > 0) {
            hql += " and divida in :divida";
        }
        Query consulta = em.createQuery(hql);
        if (tipoCadastroTributario != null) {
            consulta.setParameter("tipo", tipoCadastroTributario);
            if (!tipoCadastroTributario.equals(TipoCadastroTributario.PESSOA)) {
                if (cadastroInicial != null && cadastroInicial.trim().length() > 0) {
                    consulta.setParameter("cadastroInicial", cadastroInicial);
                }
                if (cadastroFinal != null && cadastroFinal.trim().length() > 0) {
                    consulta.setParameter("cadastroFinal", cadastroFinal);
                }
            }
        }
        if (livroDividaAtiva != null) {
            consulta.setParameter("livro", livroDividaAtiva.longValue());
        }
        if (termoDividaAtivaInicial != null && termoDividaAtivaInicial.trim().length() > 0) {
            consulta.setParameter("termoInicial", termoDividaAtivaInicial);
        }
        if (termoDividaAtivaFinal != null && termoDividaAtivaFinal.trim().length() > 0) {
            consulta.setParameter("termoFinal", termoDividaAtivaFinal);
        }
        if (dataInscricaoInicial != null) {
            consulta.setParameter("dataInicial", dataInscricaoInicial);
        }
        if (dataInscricaoFinal != null) {
            consulta.setParameter("dataFinal", dataInscricaoFinal);
        }
        if (exercicio != null && exercicio.trim().length() > 0) {
            consulta.setParameter("exercicio", Integer.parseInt(exercicio));
        }
        if (contribuinte != null) {
            consulta.setParameter("pessoa", contribuinte);
        }
        if (dividas != null && dividas.size() > 0) {
            consulta.setParameter("divida", montaStringDivida(dividas));
        }
        consulta.setMaxResults(max);
        consulta.setFirstResult(inicio);
        return consulta.getResultList();
    }

    public List<TermoInscricaoDividaAtiva> completaTermoDividaAtiva(String parte) {
        String hql = "select termo from TermoInscricaoDividaAtiva termo where to_char(termo.numero) like :parte";
        Query q = em.createQuery(hql);
        q.setParameter("parte", "%" + parte + "%");
        return q.getResultList();
    }

    public String recuperaNumeroCadastroItemInscrica(ItemInscricaoDividaAtiva item) {
        String sql = "SELECT coalesce(bci.inscricaocadastral, cmc.inscricaocadastral, to_char(bcr.codigo), '-') AS inscricao "
                + " FROM iteminscricaodividaativa item "
                + " inner join Calculo cal on cal.id = item.id "
                + " INNER JOIN cadastro cad ON cal.cadastro_id = cad.id"
                + " LEFT JOIN cadastroimobiliario bci ON bci.id = cad.id "
                + " LEFT JOIN cadastroeconomico cmc ON cmc.id = cad.id"
                + " LEFT JOIN cadastrorural bcr ON bcr.id = cad.id"
                + " LEFT JOIN contratorendaspatrimoniais contrato ON contrato.id = cad.id"
                + " WHERE item.id = :item";
        Query q = em.createNativeQuery(sql);
        q.setParameter("item", item.getId());
        q.setMaxResults(1);
        return (String) q.getResultList().get(0);
    }

    private List<Divida> montaStringDivida(List<Divida> dividas) {
        List<Divida> toReturn = new ArrayList<>();
        for (Divida divida : dividas) {
            toReturn.add(divida);
        }
        return toReturn;
    }


}
