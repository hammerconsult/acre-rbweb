/*
 * Codigo gerado automaticamente em Fri Feb 11 09:06:37 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SituacaoCertidaoDA;
import br.com.webpublico.enums.TipoCadastroDoctoOficial;
import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.exception.AtributosNulosException;
import br.com.webpublico.exception.UFMException;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Stateless
public class PeticaoFacade extends AbstractFacade<Peticao> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private DocumentoOficialFacade documentoOficialFacade;
    @EJB
    private CadastroImobiliarioFacade cadastroImobiliarioFacade;
    @EJB
    private CadastroEconomicoFacade cadastroEconomicoFacade;
    @EJB
    private CadastroRuralFacade cadastroRuralFacade;
    @EJB
    private ContratoRendasPatrimoniaisFacade contratoRendasPatrimoniaisFacade;
    @EJB
    private CertidaoDividaAtivaFacade certidaoDividaAtivaFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private MoedaFacade moedaFacade;
    @EJB
    private InscricaoDividaAtivaFacade inscricaoDividaAtivaFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PeticaoFacade() {
        super(Peticao.class);
    }


    public List<CertidaoDividaAtiva> buscaCertidoes(String cadastroInicial, String cadastroFinal, Long certidaoInicial, Long certidaoFinal, Pessoa pessoa, TipoCadastroTributario tipoCadastroTributario, Integer opcao) {
        String hql = "select certidao from "
                + " CertidaoDividaAtiva certidao " +
                " where 1=1";

//                + " where certidao.situacaoCertidaoDA = 'ABERTA' "
//                + " and certidao not in (select i.certidaoDividaAtiva from ItemPeticao i) ";

        if (opcao == 2) {
            hql += " and certidao.situacaoCertidaoDA = 'ABERTA'";
        }
        if (pessoa != null && pessoa.getId() != null) {
            hql += " and certidao.pessoa = :pessoa";
        }
        if (pessoa == null && tipoCadastroTributario != null && tipoCadastroTributario.equals(TipoCadastroTributario.PESSOA)) {
            hql += " and certidao.pessoa is not null and certidao.cadastro is null";
//
        }
        if (certidaoInicial != null) {
            hql += " and certidao.numero >= :certidaoInicial";
        }
        if (certidaoFinal != null) {
            hql += " and certidao.numero <= :certidaoFinal";
        }
        if (tipoCadastroTributario != null && !tipoCadastroTributario.equals(TipoCadastroTributario.PESSOA)) {
            if (cadastroInicial != null && cadastroInicial.trim().length() > 0) {
                hql += " and certidao.cadastro in (";
                if (tipoCadastroTributario.equals(TipoCadastroTributario.IMOBILIARIO)) {
                    hql += " select cadI from CadastroImobiliario cadI where cadI.inscricaoCadastral >= :cadastroInicial ";
                }
                if (tipoCadastroTributario.equals(TipoCadastroTributario.ECONOMICO)) {
                    //System.out.println("economico");
                    hql += " select cadE from CadastroEconomico cadE where cadE.inscricaoCadastral >= :cadastroInicial ";
                }
                if (tipoCadastroTributario.equals(TipoCadastroTributario.RURAL)) {
                    hql += " select cadR from CadastroRural cadR where to_char(cadR.codigo) >= :cadastroInicial ";
                }
                hql += " )";

            }
            if (cadastroFinal != null && cadastroFinal.trim().length() > 0) {
                hql += " and certidao.cadastro in (";
                if (tipoCadastroTributario.equals(TipoCadastroTributario.IMOBILIARIO)) {
                    hql += " select cadI from CadastroImobiliario cadI where cadI.inscricaoCadastral <= :cadastroFinal ";
                }
                if (tipoCadastroTributario.equals(TipoCadastroTributario.ECONOMICO)) {
                    hql += " select cadE from CadastroEconomico cadE where cadE.inscricaoCadastral <= :cadastroFinal ";
                }
                if (tipoCadastroTributario.equals(TipoCadastroTributario.RURAL)) {
                    hql += " select cadR from CadastroRural cadR where to_char(cadR.codigo) <= :cadastroFinal ";
                }
                hql += " )";

            }
        }
        Query q = em.createQuery(hql);
        if (pessoa != null && pessoa.getId() != null) {
            q.setParameter("pessoa", pessoa);
        }
        if (tipoCadastroTributario != null && !tipoCadastroTributario.equals(TipoCadastroTributario.PESSOA)) {
            if (cadastroInicial != null && cadastroInicial.trim().length() > 0) {
                q.setParameter("cadastroInicial", cadastroInicial);
            }
            if (cadastroFinal != null && cadastroFinal.trim().length() > 0) {
                q.setParameter("cadastroFinal", cadastroFinal);
            }
        }
        if (certidaoInicial != null) {
            q.setParameter("certidaoInicial", certidaoInicial);
        }
        if (certidaoFinal != null) {
            q.setParameter("certidaoFinal", certidaoFinal);
        }
//       new Util().imprimeSQL(hql, q);
        return q.getResultList();
    }

    public List<Peticao> geraPeticoes(CertidaoDividaAtiva[] certidoes, Date emissao, Exercicio exercicio, SistemaControlador sistemaControlador, Peticao selecionado) throws ExcecaoNegocioGenerica, UFMException, AtributosNulosException {
        ParametrosPeticao parametros = recuperaUltimoParametro();
        if (parametros == null) {
            throw new ExcecaoNegocioGenerica("Nenhum parametro foi encontrado");
        }
        List<Peticao> peticoes = new ArrayList<>();
        Long ultimoCodigo = super.retornaUltimoCodigoLong();
        try {
            for (CertidaoDividaAtiva certidao : certidoes) {

                certidao.setSituacaoCertidaoDA(SituacaoCertidaoDA.PETICIONADA);
                em.merge(certidao);
                Peticao peticao = new Peticao();
                peticao.setCodigo(ultimoCodigo++);
                peticao.setDataEmissao(emissao);
                peticao.setExercicio(exercicio);
                peticao.setCadastro(certidao.getCadastro());
                peticao.setSituacaoPeticao(Peticao.SituacaoPeticao.EM_ABERTO);
                peticao.setVaraCivel(selecionado.getVaraCivel());
                peticao.setPessoa(certidao.getPessoa());
                ItemPeticao item = new ItemPeticao();
                item.setCertidaoDividaAtiva(certidao);
                item.setAtualizado(certidaoDividaAtivaFacade.valorDaCertidao(certidao));
                item.setValorOriginal(item.getAtualizado());
                item.setPeticao(peticao);
                peticao.getItensPeticao().add(item);
                peticao = em.merge(peticao);
                peticoes.add(peticao);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return peticoes;
    }

    public void geraDocumentoOficialdaPeticao(Peticao peticao, SistemaControlador sistemaControlador) throws ExcecaoNegocioGenerica, UFMException, AtributosNulosException {
        ParametrosPeticao parametros = recuperaUltimoParametro();

        TipoDoctoOficial tipoDoctoOficial = null;
        if (peticao.getCadastro() == null) {
            if (peticao.getPessoa() instanceof PessoaFisica) {
                tipoDoctoOficial = parametros.getDoctoPessoaFisica();
            } else if (peticao.getPessoa() instanceof PessoaJuridica) {
                tipoDoctoOficial = parametros.getDoctoPessoaJuridica();
            }
        } else {
            if (peticao.getCadastro() instanceof CadastroEconomico) {
                tipoDoctoOficial = parametros.getDoctoEconomico();
            } else if (peticao.getCadastro() instanceof CadastroImobiliario) {
                tipoDoctoOficial = parametros.getDoctoImobiliario();
            } else if (peticao.getCadastro() instanceof CadastroRural) {
                tipoDoctoOficial = parametros.getDoctoRural();
            }
        }
        if (tipoDoctoOficial != null) {
            geraDocumentoOficialDaPeticao(peticao, tipoDoctoOficial, sistemaControlador);
        } else {
            throw new ExcecaoNegocioGenerica("O parametro de Documento Oficial não foi encontrado.");
        }
    }


    private List<Pessoa> recuperaPessoasDoCadastro(Cadastro cadastro) {
        String hql = "select p from Pessoa p where p in ";
        hql += " (select p.pessoa from Propriedade p where p.imovel = :cadastro)";
        hql += " or p in ";
        hql += " (select p.pessoa from PropriedadeRural p where p.imovel = :cadastro)";
        hql += " or p in ";
        hql += " (select c.pessoa from CadastroEconomico c where c = :cadastro)";
        hql += " or p in ";
        hql += " (select c.locatario from ContratoRendasPatrimoniais c where c = :cadastro)";
        Query q = em.createQuery(hql);
        q.setParameter("cadastro", cadastro);
        return q.getResultList();
    }

    public ParametrosPeticao recuperaUltimoParametro() {
        Query q = em.createQuery("from ParametrosPeticao p where p.id = (select max(id) from ParametrosPeticao p )");
        List<ParametrosPeticao> listaParam = q.getResultList();
        if (!listaParam.isEmpty()) {
            return listaParam.get(0);
        }
        return null;
    }

    private void geraDocumentoOficialDaPeticao(Peticao peticao, TipoDoctoOficial tipo, SistemaControlador sistemaControlador) throws UFMException, AtributosNulosException {
        DocumentoOficial documentoOficial = documentoOficialFacade.geraDocumentoPeticaoDividaAtiva(peticao, peticao.getDocumentoOficial(), peticao.getCadastro(), peticao.getPessoa(), tipo, sistemaControlador);
        if (documentoOficial != null) {
            peticao.setDocumentoOficial(documentoOficial);
            em.merge(peticao);
        }
    }

    public List<TipoDoctoOficial> completaTipoDoctoOficial(String parte) {
        String hql = " select tipo from TipoDoctoOficial tipo where tipo.moduloTipoDoctoOficial = 'PETICAODIVIDAATIVA'"
                + " and (lower(tipo.descricao) like :parte or to_char(tipo.codigo) like :parte)";
        Query consulta = em.createQuery(hql);
        consulta.setParameter("parte", "%" + parte.toLowerCase() + "%");
        try {
            return (List<TipoDoctoOficial>) consulta.getResultList();
        } catch (NoResultException e) {
            return new ArrayList<TipoDoctoOficial>();
        }
    }

    public List<TipoDoctoOficial> completaTipoDoctoOficialPorTipo(String parte, TipoCadastroDoctoOficial tipo) {
        String hql = " select tipo from TipoDoctoOficial tipo where tipo.moduloTipoDoctoOficial = 'PETICAODIVIDAATIVA'"
                + " and (lower(tipo.descricao) like :parte or to_char(tipo.codigo) like :parte)"
                + " and tipo.tipoCadastroDoctoOficial = :tipo";
        Query consulta = em.createQuery(hql);
        consulta.setParameter("parte", "%" + parte.toLowerCase() + "%");
        consulta.setParameter("tipo", tipo);
        try {
            return (List<TipoDoctoOficial>) consulta.getResultList();
        } catch (NoResultException e) {
            return new ArrayList<TipoDoctoOficial>();
        }
    }

    public List<Peticao> recuperaPeticoes() {
        Query consulta = em.createQuery("select p from Peticao p order by p.id desc");
        try {
            return (List<Peticao>) consulta.getResultList();
        } catch (NoResultException e) {
            return new ArrayList<Peticao>();
        }
    }

    public List<ItemInscricaoDividaAtiva> recuperaItemInscricaoPelaCertidao(CertidaoDividaAtiva certidao) {
        Query consulta = em.createQuery("select item from ItemCertidaoDividaAtiva itemcert" +
                " join itemcert.itemInscricaoDividaAtiva item " +
                " where itemcert.certidao = :certidao");
        consulta.setParameter("certidao", certidao);
        return consulta.getResultList();
    }

    public BigDecimal recuperaParcelaValorDividaPelaCertidao(ItemInscricaoDividaAtiva item) {
        Query consulta = em.createQuery("select sum(inscricao.parcelaValorDivida.valor) from InscricaoDividaParcela inscricao " +
                " where inscricao.itemInscricaoDividaAtiva = :item");
        consulta.setParameter("item", item);
        return (BigDecimal) consulta.getSingleResult();
    }


    public List<Peticao> recuperaPeticoesEmAberto() {
        Query consulta = em.createQuery("select p from Peticao p where p.situacaoPeticao = 'EM_ABERTO'");
        try {
            return (List<Peticao>) consulta.getResultList();
        } catch (NoResultException e) {
            return new ArrayList<Peticao>();
        }
    }

    public CadastroEconomicoFacade getCadastroEconomicoFacade() {
        return cadastroEconomicoFacade;
    }

    public void setCadastroEconomicoFacade(CadastroEconomicoFacade cadastroEconomicoFacade) {
        this.cadastroEconomicoFacade = cadastroEconomicoFacade;
    }

    public CadastroImobiliarioFacade getCadastroImobiliarioFacade() {
        return cadastroImobiliarioFacade;
    }

    public void setCadastroImobiliarioFacade(CadastroImobiliarioFacade cadastroImobiliarioFacade) {
        this.cadastroImobiliarioFacade = cadastroImobiliarioFacade;
    }

    public CadastroRuralFacade getCadastroRuralFacade() {
        return cadastroRuralFacade;
    }

    public void setCadastroRuralFacade(CadastroRuralFacade cadastroRuralFacade) {
        this.cadastroRuralFacade = cadastroRuralFacade;
    }

    public CertidaoDividaAtivaFacade getCertidaoDividaAtivaFacade() {
        return certidaoDividaAtivaFacade;
    }

    public void setCertidaoDividaAtivaFacade(CertidaoDividaAtivaFacade certidaoDividaAtivaFacade) {
        this.certidaoDividaAtivaFacade = certidaoDividaAtivaFacade;
    }

    public ContratoRendasPatrimoniaisFacade getContratoRendasPatrimoniaisFacade() {
        return contratoRendasPatrimoniaisFacade;
    }

    public void setContratoRendasPatrimoniaisFacade(ContratoRendasPatrimoniaisFacade contratoRendasPatrimoniaisFacade) {
        this.contratoRendasPatrimoniaisFacade = contratoRendasPatrimoniaisFacade;
    }

    public DocumentoOficialFacade getDocumentoOficialFacade() {
        return documentoOficialFacade;
    }

    public void setDocumentoOficialFacade(DocumentoOficialFacade documentoOficialFacade) {
        this.documentoOficialFacade = documentoOficialFacade;
    }

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }

    public void setExercicioFacade(ExercicioFacade exercicioFacade) {
        this.exercicioFacade = exercicioFacade;
    }

    public PessoaFacade getPessoaFacade() {
        return pessoaFacade;
    }

    public void setPessoaFacade(PessoaFacade pessoaFacade) {
        this.pessoaFacade = pessoaFacade;
    }

    public MoedaFacade getMoedaFacade() {
        return moedaFacade;
    }

    public void setMoedaFacade(MoedaFacade moedaFacade) {
        this.moedaFacade = moedaFacade;
    }

    public InscricaoDividaAtivaFacade getInscricaoDividaAtivaFacade() {
        return inscricaoDividaAtivaFacade;
    }

    public void imprimeNovo(Peticao peticao) {
        peticao.setSituacaoPeticao(Peticao.SituacaoPeticao.FINALIZADO);
        peticao = em.merge(peticao);
        documentoOficialFacade.emiteDocumentoOficial(peticao.getDocumentoOficial());
    }

    public List<ParametrosPeticao> listaParametros() {
        return em.createQuery("from ParametrosPeticao").getResultList();
    }

    public void salvar(ParametrosPeticao parametrosPeticao) {
        em.merge(parametrosPeticao);
    }

    public List<Peticao> listaFiltrandoEspecial(String parte) {
        String hql = "select p from " + Peticao.class.getSimpleName() + " as p "
                + " where to_char(p.codigo) like :parte";

        Query query = em.createQuery(hql);
        query.setMaxResults(50);
        query.setParameter("parte", "%" + parte.toLowerCase() + "%");
        try {
            return query.getResultList();
        } catch (NoResultException e) {
            return new ArrayList<Peticao>();
        }
    }

//    public List<Peticao> retornaPeticoesDisponiveisParaAjuizamento(Long filtroNumeroPeticaoInicial, Long filtroNumeroPeticaoFinal, Long filtroExercicioInicial, Long filtroExercicioFinal, Date filtroEmissaoInicial, Date filtroEmissaoFinal, Ajuizamento ajuizamento) {
//        String where = "";
//        if (ajuizamento.getId() != null) {
//            where = " where s.ajuizamento != :ajuizamento ";
//        }
//        String hql = "select p from Peticao as p "
//                + "where p not in (select s.peticao from SituacaoProcesAjuizamento s " + where + " )";
//
//        if (filtroNumeroPeticaoInicial != null) {
//            hql += " and p.codigo >= :filtroNumeroPeticaoInicial";
//        }
//        if (filtroNumeroPeticaoFinal != null) {
//            hql += " and p.codigo <= :filtroNumeroPeticaoFinal";
//        }
//
//        if (filtroEmissaoInicial != null) {
//            hql += " and p.dataEmissao >= :filtroEmissaoInicial";
//        }
//        if (filtroEmissaoFinal != null) {
//            hql += " and p.dataEmissao <= :filtroEmissaoFinal";
//        }
//        if (filtroExercicioInicial != null) {
//            hql += " and p.exercicio.ano >= :filtroExercicioInicial";
//        }
//        if (filtroExercicioFinal != null) {
//            hql += " and p.exercicio.ano <= :filtroExercicioFinal";
//        }
//
//        Query q = em.createQuery(hql);
//
//        if (filtroNumeroPeticaoInicial != null) {
//            q.setParameter("filtroNumeroPeticaoInicial", filtroNumeroPeticaoInicial);
//        }
//        if (filtroNumeroPeticaoFinal != null) {
//            q.setParameter("filtroNumeroPeticaoFinal", filtroNumeroPeticaoFinal);
//        }
//        if (filtroEmissaoInicial != null) {
//            q.setParameter("filtroEmissaoInicial", filtroEmissaoInicial);
//        }
//        if (filtroEmissaoFinal != null) {
//            q.setParameter("filtroEmissaoFinal", filtroEmissaoFinal);
//        }
//        if (filtroExercicioInicial != null) {
//            q.setParameter("filtroExercicioInicial", filtroExercicioInicial.intValue());
//        }
//        if (filtroExercicioFinal != null) {
//            q.setParameter("filtroExercicioFinal", filtroExercicioFinal.intValue());
//        }
//        if (ajuizamento.getId() != null) {
//            q.setParameter("ajuizamento", ajuizamento);
//        }
//        try {
//            return q.getResultList();
//        } catch (NoResultException e) {
//            return new ArrayList<Peticao>();
//        }
//    }


    public BigDecimal getValorTotalCertidao(CertidaoDividaAtiva certidao) {
        Query consulta = em.createQuery("select sum(inscricao.parcelaValorDivida.valor) from InscricaoDividaParcela inscricao " +
                " where inscricao.itemInscricaoDividaAtiva in  (select i.itemInscricaoDividaAtiva from ItemCertidaoDividaAtiva  i where i.certidao = :certidao)");
        consulta.setParameter("certidao", certidao);
        return (BigDecimal) consulta.getSingleResult();
    }

    @Override
    public Peticao recuperar(Object id) {
        Peticao peticao = em.find(Peticao.class, id);
        peticao.getItensPeticao().size();
        return peticao;
    }

    public ParametrosPeticao recuperarParametrosPeticao(Object id) {
        ParametrosPeticao parametro = em.find(ParametrosPeticao.class, id);
        return parametro;
    }

    public Peticao peticaoDaCertidao(CertidaoDividaAtiva certidao) {
        Query consulta = em.createQuery("select item.peticao from ItemPeticao item where item.certidaoDividaAtiva = :certidao");
        consulta.setParameter("certidao", certidao);
        List<Peticao> listaPeticao = consulta.getResultList();
        if (!listaPeticao.isEmpty()) {
            return listaPeticao.get(0);
        }
        return null;
    }

}
