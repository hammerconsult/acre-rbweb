/*
 * Codigo gerado automaticamente em Mon Apr 09 15:09:47 BRT 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.DoctoHabilitacao;
import br.com.webpublico.entidades.EntidadeBeneficiaria;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.enums.TipoClasseCredor;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class EntidadeBeneficiariaFacade extends AbstractFacade<EntidadeBeneficiaria> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private DoctoHabilitacaoFacade doctoHabilitacaoFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private AcoesBeneficiarioFacade acoesBeneficiarioFacade;
    @EJB
    private ClausulaBenificiarioFacade clausulaBenificiarioFacade;
    @EJB
    private ContaCorrenteBancariaFacade contaCorrenteBancariaFacade;
    @EJB
    private ArquivoFacade arquivoFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ClasseBeneficiarioFacade classeBeneficiarioFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public EntidadeBeneficiariaFacade() {
        super(EntidadeBeneficiaria.class);
    }


    @Override
    public void salvarNovo(EntidadeBeneficiaria entity) {
        try {
            if (validarEntidadeBeneficiariaComMesmoCNPJ(entity)) {
                entity.setCodigo(getCodigoSequencial());
                em.persist(entity);
            }
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        }
    }

    public boolean validarEntidadeBeneficiariaComMesmoCNPJ(EntidadeBeneficiaria entidadeBeneficiaria) {
        String sql = " select b.* from entidadebeneficiaria b " +
            "          inner join pessoajuridica pj on pj.id = b.pessoajuridica_id " +
            "           where pj.cnpj = :cnpj ";
        if (entidadeBeneficiaria.getId() != null) {
            sql += " and b.id <> :id ";
        }
        Query q = em.createNativeQuery(sql);
        q.setParameter("cnpj", entidadeBeneficiaria.getPessoaJuridica().getCnpj().trim());
        if (entidadeBeneficiaria.getId() != null) {
            q.setParameter("id", entidadeBeneficiaria.getId());
        }
        if (q.getResultList().isEmpty()) {
            return true;
        }
        throw new ExcecaoNegocioGenerica(" Já existe uma pessoa jurífica cadastrada com o cnjp: " + entidadeBeneficiaria.getPessoaJuridica().getCnpj() + ".");
    }

    @Override
    public EntidadeBeneficiaria recarregar(EntidadeBeneficiaria entity) {
        Query q = getEntityManager().createQuery("from EntidadeBeneficiaria e where e = :param", EntidadeBeneficiaria.class);
        q.setParameter("param", entity);
        EntidadeBeneficiaria e = (EntidadeBeneficiaria) q.getSingleResult();
        e.getClausulasBenificiarios().size();
        e.getEntidadeBeneficCertidoess().size();
        e.getListaAcoesBeneficiarios().size();

        return e;
    }


    public List<EntidadeBeneficiaria> listaPorPessoa(String parte) {
        String sql = "SELECT ent.* FROM entidadebeneficiaria ent "
            + "INNER JOIN pessoajuridica pj ON ent.pessoajuridica_id = pj.id "
            + "AND ((lower(pj.nomefantasia) LIKE :parte) OR (lower(pj.nomereduzido) LIKE :parte) OR (pj.cnpj LIKE :parte))";
        Query q = em.createNativeQuery(sql, EntidadeBeneficiaria.class);
        q.setParameter("parte", "%" + parte.toLowerCase() + "%");
        List<EntidadeBeneficiaria> entidadeBeneficiarias = q.getResultList();
        if (entidadeBeneficiarias.isEmpty()) {
            return new ArrayList<EntidadeBeneficiaria>();
        } else {
            return entidadeBeneficiarias;
        }
    }

    public List<EntidadeBeneficiaria> listaPorPessoaEClasseCredor(String parte, Exercicio ex) {
        String sql = " SELECT DISTINCT ent.* FROM entidadebeneficiaria ent "
            + " INNER JOIN PESSOAJURIDICA PJ ON ENT.PESSOAJURIDICA_ID = PJ.ID "
            + " INNER JOIN CLASSECREDORPESSOA CRP ON CRP.PESSOA_ID = PJ.ID "
            + " INNER JOIN CLASSECREDOR CC ON CC.ID = CRP.CLASSECREDOR_ID "
            + " WHERE CC.TIPOCLASSECREDOR = :tipoClasseCredor "
            + " and (lower(translate(pj.razaoSocial,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')) like translate(:parte,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc') "
            + " or lower(translate(pj.nomeFantasia,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')) like translate(:parte,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc') "
            + " or replace(replace(replace(pj.cnpj,'.',''),'-',''),'/','') like :parte)";
        Query q = em.createNativeQuery(sql, EntidadeBeneficiaria.class);
        q.setParameter("parte", "%" + parte.toLowerCase() + "%");
        q.setParameter("tipoClasseCredor", TipoClasseCredor.CONVENIIO_DESPESA.name());
        List<EntidadeBeneficiaria> entidadeBeneficiarias = q.getResultList();
        if (entidadeBeneficiarias.isEmpty()) {
            return new ArrayList<EntidadeBeneficiaria>();
        } else {
            return entidadeBeneficiarias;
        }
    }

    public String getCodigoSequencial() {
        String sql = "SELECT MAX(TO_NUMBER(E.CODIGO))+1 AS ULTIMONUMERO FROM ENTIDADEBENEFICIARIA E";
        Query q = em.createNativeQuery(sql);
        if (q.getSingleResult() == null) {
            return "1";
        }
        return q.getSingleResult().toString();
    }

    public List<DoctoHabilitacao> listaDoctoHabilitacao() {
        return doctoHabilitacaoFacade.lista();
    }

    public DoctoHabilitacaoFacade getDoctoHabilitacaoFacade() {
        return doctoHabilitacaoFacade;
    }

    public AcoesBeneficiarioFacade getAcoesBeneficiarioFacade() {
        return acoesBeneficiarioFacade;
    }

    public ClausulaBenificiarioFacade getClausulaBenificiarioFacade() {
        return clausulaBenificiarioFacade;
    }

    public ContaCorrenteBancariaFacade getContaCorrenteBancariaFacade() {
        return contaCorrenteBancariaFacade;
    }

    public PessoaFacade getPessoaFacade() {
        return pessoaFacade;
    }

    public EntidadeBeneficiaria recuperar(Long id) {
        Query q = getEntityManager().createQuery("from EntidadeBeneficiaria e where e.id = :param", EntidadeBeneficiaria.class);
        q.setParameter("param", id);
        EntidadeBeneficiaria e = (EntidadeBeneficiaria) q.getSingleResult();
        e.getClausulasBenificiarios().size();
        e.getEntidadeBeneficCertidoess().size();
        e.getListaAcoesBeneficiarios().size();

        return e;
    }

    public ArquivoFacade getArquivoFacade() {
        return arquivoFacade;
    }

    public ClasseBeneficiarioFacade getClasseBeneficiarioFacade() {
        return classeBeneficiarioFacade;
    }

    public String montarConteudoDeclaracaoCadastro(EntidadeBeneficiaria selecionado) {
        String caminhoDaImagem = FacesUtil.geraUrlImagemDir() + "img/Brasao_de_Rio_Branco.gif";

        String conteudoFicha = "<?xml version='1.0' encoding='iso-8859-1'?>"
            + " <!DOCTYPE HTML PUBLIC \"HTML 4.01 Transitional//PT\" \"http://www.w3.org/TR/html4/loose.dtd\">"
            + " <html>" +
            "<html> " +
            "<style> " +
            "table,th,td " +
            "{ " +
            "border:1px solid black; " +
            "border-collapse:collapse " +
            "}" +
            "</style> " +
            "<div style='text-align:center'> " +
            "<img src=\"" + caminhoDaImagem + "\" alt=\"PREFEITURA DO MUNIC&Iacute;PIO DE RIO BRANCO\" /> </br> " +
            "</div> " +
            "</br>" +
            "<div style='text-align:center'> " +
            "<b> PREFEITURA MUNICIPAL DE RIO BRANCO </br> " +
            "Secretaria Municipal de Planejamento </b> " +
            "</div> " +
            " " +
            "</br> " +
            "</br> " +
            " " +
            "<div style='text-align:center'> " +
            "<b>DECLARAÇÃO DE CADASTRO</b> " +
            "</div> " +
            " " +
            "</br> " +
            "</br> " +
            "</br> " +
            "" +
            "<div style='text-align:justify'>" +
            " &nbsp;&nbsp;&nbsp;&nbsp; Declaro para os devidos fins de interesse que a <b>Associação</b> " + selecionado.getPessoaJuridica().getNome() + ", " +
            " inscrita no CNPJ n° " + selecionado.getPessoaJuridica().getCnpj() + ", " +
            " encontra-se devidamente cadastrada junto à Secretaria Municipal de Planejamento, estando apta a participar " +
            " enquanto Entidade interessada a firmar convênios junto ao Município de Rio Branco pelo período de 01 (um) ano.</br>" +
            "</div>" +
            " " +
            " </br>" +
            " </br>" +
            " </br>" +
            " </br>" +
            " </br>" +
            " " +
            "<div style='text-align:right'>" +
            " Rio Branco, AC " + DataUtil.getDataFormatada(selecionado.getDataCadastro()) + "." +
            "</div> " +
            " " +
            " </br>" +
            " </br>" +
            " </br>" +
            " </br>" +
            " </br>" +
            " " +
            "<div style='text-align:center'> " +
            " <b>Clicia Rodrigues da Silva</b>" +
            "</div> " +
            " " +
            "<div style='text-align:center'> " +
            " Diretora do Dep. de Ger. de projetos e convênio" +
            "</div> " +
            "  " +
            "</html>";
        return conteudoFicha;
    }
}
