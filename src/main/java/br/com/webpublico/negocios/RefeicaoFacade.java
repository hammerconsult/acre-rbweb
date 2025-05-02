package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.RefeicaoMaterialVO;
import br.com.webpublico.enums.PublicoAlvoPreferencial;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class RefeicaoFacade extends AbstractFacade<Refeicao> {

    @EJB
    private ContratoFacade contratoFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private LicitacaoFacade licitacaoFacade;
    @EJB
    private DispensaDeLicitacaoFacade dispensaDeLicitacaoFacade;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;
    @EJB
    private MaterialFacade materialFacade;

    @EJB
    private ArquivoFacade arquivoFacade;

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;


    public RefeicaoFacade() {
        super(Refeicao.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public void salvarNovo(Refeicao entity) {
        if (entity.getNumero() == null) {
            entity.setNumero(singletonGeradorCodigo.getProximoCodigo(Refeicao.class, "numero"));
        }
        super.salvarNovo(entity);
    }

    @Override
    public Refeicao recuperar(Object id) {
        Refeicao refeicao = super.recuperar(id);
        Hibernate.initialize(refeicao.getMateriais());
        Hibernate.initialize(refeicao.getRefeicoesEspeciais());
        return refeicao;
    }

    public ContratoFacade getContratoFacade() {
        return contratoFacade;
    }

    public void setContratoFacade(ContratoFacade contratoFacade) {
        this.contratoFacade = contratoFacade;
    }

    public PessoaFacade getPessoaFacade() {
        return pessoaFacade;
    }

    public void setPessoaFacade(PessoaFacade pessoaFacade) {
        this.pessoaFacade = pessoaFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public void setSistemaFacade(SistemaFacade sistemaFacade) {
        this.sistemaFacade = sistemaFacade;
    }

    public DispensaDeLicitacaoFacade getDispensaDeLicitacaoFacade() {
        return dispensaDeLicitacaoFacade;
    }

    public void setDispensaDeLicitacaoFacade(DispensaDeLicitacaoFacade dispensaDeLicitacaoFacade) {
        this.dispensaDeLicitacaoFacade = dispensaDeLicitacaoFacade;
    }

    public LicitacaoFacade getLicitacaoFacade() {
        return licitacaoFacade;
    }

    public void setLicitacaoFacade(LicitacaoFacade licitacaoFacade) {
        this.licitacaoFacade = licitacaoFacade;
    }

    public MaterialFacade getMaterialFacade() {
        return materialFacade;
    }

    public List<Refeicao> buscarRefeicao(String parte) {
        String sql = " SELECT R.* FROM REFEICAO R " +
            "      WHERE R.NUMERO LIKE :param " +
            "      or to_char(R.numero) LIKE :param " +
            "      or LOWER(R.TIPOREFEICAO) LIKE :param " +
            "      or LOWER(R.PUBLICOALVOPREFERENCIAL) LIKE :param " +
            "      or LOWER(R.DESCRICAOPRINCIPAL) LIKE :param " +
            "      and R.ATIVO = 1 ";
        Query q = em.createNativeQuery(sql, Refeicao.class);
        q.setParameter("param", "%" + parte.toLowerCase().trim() + "%");
        q.setMaxResults(10);
        return q.getResultList();
    }

    public List<Refeicao> buscarRefeicaoPorPublicoAlvo(String parte, PublicoAlvoPreferencial publicoAlvo) {
        String sql = " SELECT R.* FROM REFEICAO R " +
            " WHERE " +
            "    (R.ATIVO = 1 and (R.PUBLICOALVOPREFERENCIAL = :publicoAlvo OR R.PUBLICOALVOPREFERENCIAL = 'TODOS')) " +
            "    AND (R.NUMERO LIKE :param " +
            "    or to_char(R.numero) LIKE :param " +
            "    or LOWER(R.TIPOREFEICAO) LIKE :param " +
            "    or LOWER(R.PUBLICOALVOPREFERENCIAL) LIKE :param " +
            "    or LOWER(R.DESCRICAOPRINCIPAL) LIKE :param) ";
        Query q = em.createNativeQuery(sql, Refeicao.class);
        q.setParameter("param", "%" + parte.toLowerCase().trim() + "%");
        q.setParameter("publicoAlvo", publicoAlvo.name());
        q.setMaxResults(10);
        return q.getResultList();
    }

    public List<Pessoa> buscarFornecedores(String parte) {
        String bql = " select distinct  'PJ' as tipo, pj.id as id, pj.RAZAOSOCIAL as nomeOuRazaoSocial,   " +
            "                 pj.CNPJ        as cpfOuCnpj   " +
            " from CONTRATO c   " +
            "         inner join pessoa p on c.CONTRATADO_ID = p.ID   " +
            "         left join pessoajuridica pj on p.ID = pj.ID   " +
            " where pj.razaoSocial like :param   " +
            " union all   " +
            " select distinct 'PF' as tipo, " +
            "        pf.id as id, " +
            "        pf.nome as nomeOuRazaoSocial,   " +
            "        pf.cpf  as cpfOuCnpj   " +
            " from contrato c   " +
            "         inner join pessoa p on c.CONTRATADO_ID = p.ID   " +
            "         left join pessoafisica pf on p.ID = pf.ID   " +
            " where pf.nome like :param ";

        Query q = em.createNativeQuery(bql);

        q.setParameter("param", "%" + parte.toLowerCase().trim() + "%");
        q.setMaxResults(10);
        List<Pessoa> pessoas = Lists.newArrayList();
        List<Object[]> retorno  = q.getResultList();
        for (Object[] obj : retorno) {
            if(obj[0].equals("PJ")){
                PessoaJuridica pj = new PessoaJuridica();
                pj.setId(((Number) obj[1]).longValue());
                pj.setRazaoSocial((String) obj[2]);
                pj.setCnpj((String) obj[3]);
                pessoas.add(pj);
            }else{
                PessoaFisica pf = new PessoaFisica();
                pf.setId(((Number) obj[1]).longValue());
                pf.setNome((String) obj[2]);
                pf.setCpf((String) obj[3]);
                pessoas.add(pf);
            }
        }
        return pessoas;
    }

    public List<RefeicaoMaterialVO> buscarMateriais(String filtro) {
        String sql = "select distinct mat.* " +
            " from contrato cont " +
            "         inner join itemcontrato ic on ic.contrato_id = cont.id " +
            "         inner join pessoa p on p.id = cont.contratado_id" +
            "         left join conlicitacao contlic ON contlic.contrato_id = cont.id " +
            "         left join licitacao lic on lic.id = contlic.licitacao_id " +
            "         left join condispensalicitacao condisp on condisp.contrato_id = cont.id " +
            "         left join dispensadelicitacao disp on condisp.dispensadelicitacao_id = disp.id " +
            "         left join itemcontratoitempropfornec icpf on icpf.itemcontrato_id = ic.id " +
            "         left join itemcontratoitemirp iirp on iirp.itemcontrato_id = ic.id " +
            "         left join itemcontratoadesaoataint iata on iata.itemcontrato_id = ic.id " +
            "         left join itemcontratoitemsolext icise on icise.itemcontrato_id = ic.id " +
            "         left join itemcontratoitempropdisp icipfd on icipfd.itemcontrato_id = ic.id " +
            "         left join itempropostafornedisp ipfd on icipfd.itempropfornecdispensa_id = ipfd.id " +
            "         left join itempropfornec ipf on coalesce(icpf.itempropostafornecedor_id, iirp.itempropostafornecedor_id, iata.itempropostafornecedor_id) = ipf.id " +
            "         left join itemprocessodecompra ipc on ipc.id = coalesce(ipf.itemprocessodecompra_id, ipfd.itemprocessodecompra_id) " +
            "         left join loteprocessodecompra lote on lote.id = ipc.loteprocessodecompra_id " +
            "         left join itemsolicitacao itemsol on itemsol.id = ipc.itemsolicitacaomaterial_id " +
            "         left join itemsolicitacaoexterno ise on ise.id = icise.itemsolicitacaoexterno_id " +
            "         inner join objetocompra oc on oc.id = coalesce(itemsol.objetocompra_id, ise.objetocompra_id) " +
            "         inner join material mat on mat.objetocompra_id = oc.id " + filtro +
            " order by mat.codigo ";

        Query q = em.createNativeQuery(sql, Material.class);
        List<Material> objetos = q.getResultList();
        List<RefeicaoMaterialVO> materiais = Lists.newArrayList();
        for (Material objeto : objetos) {
            RefeicaoMaterialVO mat = new RefeicaoMaterialVO();
            mat.setSelecionado(false);
            mat.setMaterial(objeto);
            materiais.add(mat);
        }
        return materiais;
    }

    public ArquivoFacade getArquivoFacade() {
        return arquivoFacade;
    }
}
