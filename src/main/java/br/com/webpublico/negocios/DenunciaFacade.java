package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SituacaoDenuncia;
import br.com.webpublico.enums.StatusProcessoFiscalizacao;
import br.com.webpublico.enums.TipoLevantamentoFiscalizacao;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;

/**
 * @author Fabio
 */
@Stateless
public class DenunciaFacade extends AbstractFacade<Denuncia> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ProcessoFiscalizacaoFacade processoFiscalizacaoFacade;
    @EJB
    private TipoDenunciaFacade tipoDenunciaFacade;
    @EJB
    private UsuarioSistemaFacade usuarioSistemaFacade;
    @EJB
    private BairroFacade bairroFacade;
    @EJB
    private SecretariaFiscalizacaoFacade secretariaFiscalizacaoFacade;

    public DenunciaFacade() {
        super(Denuncia.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.em;
    }

    @Override
    public Denuncia recuperar(Object id) {
        Denuncia d = em.find(Denuncia.class, id);
        d.getDenunciaFisciasDesignados().size();
        d.getDenunciaOcorrencias().size();
        if (d.getDetentorArquivoComposicao() != null) {
            d.getDetentorArquivoComposicao().getArquivosComposicao().size();
        }
        return d;
    }

    @Override
    public void salvar(Denuncia entity) {
        entity = em.merge(entity);
//        if (entity.getSituacao().equals(SituacaoDenuncia.FISCALIZACAO)) {
//            // criar o processo de fiscalização
//            criadoProcessoFiscalizacao(entity);
//        }
    }

    public ProcessoFiscalizacao criadoProcessoFiscalizacao(Denuncia selecionado) {
        ProcessoFiscalizacao pf = new ProcessoFiscalizacao();
        pf.setSecretariaFiscalizacao(selecionado.getSecretariaFiscalizacao());
        pf.setCodigo(processoFiscalizacaoFacade.codigoPorSecretaria());
        pf.setDenuncia(selecionado);
        pf.setTipoLevantamentoFiscalizacao(TipoLevantamentoFiscalizacao.DENUNCIA);
        pf.setDataCadastro(new Date());
        SituacaoProcessoFiscal spf = new SituacaoProcessoFiscal();
        spf.setProcessoFiscalizacao(pf);
        spf.setData(new Date());
        spf.setStatusProcessoFiscalizacao(StatusProcessoFiscalizacao.DIGITADO);
        pf.getSituacoesProcessoFiscalizacao().add(spf);
        return em.merge(pf);
    }

    public ProcessoFiscalizacaoFacade getProcessoFiscalizacaoFacade() {
        return processoFiscalizacaoFacade;
    }

    public TipoDenunciaFacade getTipoDenunciaFacade() {
        return tipoDenunciaFacade;
    }

    public UsuarioSistemaFacade getUsuarioSistemaFacade() {
        return usuarioSistemaFacade;
    }

    public BairroFacade getBairroFacade() {
        return bairroFacade;
    }

    public SecretariaFiscalizacaoFacade getSecretariaFiscalizacaoFacade() {
        return secretariaFiscalizacaoFacade;
    }

    public List<Denuncia> listaFiltrando(String s, SecretariaFiscalizacao secretaria) {
        String hql = "from Denuncia obj where " +
                " (" +
                " to_char(obj.numero) like :filtro " +
                " or to_char(obj.numero) || '/' || to_char(obj.exercicio.ano) like :filtro " +
                " or to_char(obj.exercicio.ano) like :filtro " +
                " or to_char(obj.dataDenuncia, 'dd/MM/yyyy') like :filtro" +
                " or lower(to_char(obj.tipoOrigemDenuncia)) like :filtro" +
                " or lower(to_char(obj.tipoDenuncia)) like :filtro" +
                " or lower(obj.referenciaOrigem) like :filtro" +
                " )" +
                " and obj.secretariaFiscalizacao = :secretaria" +
                " order by obj.numero, obj.exercicio.ano ";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        q.setParameter("secretaria", secretaria);
        q.setMaxResults(MAX_RESULTADOS_NO_AUTOCOMPLATE);
        return q.getResultList();
    }
}
