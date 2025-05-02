/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.VOAlvara;
import br.com.webpublico.entidadesauxiliares.VOAlvaraCnaes;
import br.com.webpublico.tributario.enumeration.GrauDeRiscoDTO;
import br.com.webpublico.enums.StatusVistoria;
import br.com.webpublico.enums.TipoAlvara;
import com.beust.jcommander.internal.Lists;
import org.primefaces.model.UploadedFile;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author claudio
 */
@Stateless
public class VistoriaFacade extends AbstractFacade<Vistoria> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private CadastroEconomicoFacade cadastroEconomicoFacade;
    @EJB
    private CNAEFacade cnaeFacade;
    @EJB
    private IrregularidadeFacade irregularidadeFacade;
    @EJB
    private TipoVistoriaFacade tipoVistoriaFacade;
    @EJB
    private AlvaraFacade alvaraFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ConfiguracaoTributarioFacade configuracaoTributarioFacade;
    @EJB
    private CalculoAlvaraFacade calculoAlvaraFacade;
    @EJB
    private ArquivoFacade arquivoFacade;

    public VistoriaFacade() {
        super(Vistoria.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CadastroEconomicoFacade getCadastroEconomicoFacade() {
        return cadastroEconomicoFacade;
    }

    public CNAEFacade getCnaeFacade() {
        return cnaeFacade;
    }

    public IrregularidadeFacade getIrregularidadeFacade() {
        return irregularidadeFacade;
    }

    public TipoVistoriaFacade getTipoVistoriaFacade() {
        return tipoVistoriaFacade;
    }

    public AlvaraFacade getAlvaraFacade() {
        return alvaraFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public ConfiguracaoTributarioFacade getConfiguracaoTributarioFacade() {
        return configuracaoTributarioFacade;
    }

    public CalculoAlvaraFacade getCalculoAlvaraFacade() {
        return calculoAlvaraFacade;
    }

    @Override
    public Vistoria recuperar(Object id) {
        Vistoria v = em.find(Vistoria.class, id);
        v.getListaIrregularidade().size();
        v.getCnaes().size();
        v.getArquivos().size();
        v.getPareceres().size();
        return v;
    }

    @Override
    public void salvar(Vistoria entity) {
        salvarArquivos(entity.getArquivos());
        em.merge(entity);
        verificaGeraSituacaoEconomicoCNAE(entity, SituacaoEconomicoCNAE.Situacao.EMBARGADO);
    }

    public void encerraVistoria(Vistoria entity) {
        em.merge(entity);
        entity = em.find(Vistoria.class, entity.getId());
        List<VistoriaCnae> cnaes = entity.getCnaes(); //pega os cnaes da vistoria
        for (VistoriaCnae vistoriaCnae : cnaes) {
            if (!verificaSituacaoEmAberto(entity)) {
                if (entity.getStatusVistoria().equals(StatusVistoria.FINALIZADA)) {
                    setaSituacao(vistoriaCnae, entity, SituacaoEconomicoCNAE.Situacao.LICENCIADO);
                } else if (entity.getStatusVistoria().equals(StatusVistoria.TRAMITANDO)) {
                    setaSituacao(vistoriaCnae, entity, SituacaoEconomicoCNAE.Situacao.NAO_LICENCIADO);
                }
            }
        }
        em.merge(entity);
    }

    private void setaSituacao(VistoriaCnae vistoriaCnae, Vistoria vistoria, SituacaoEconomicoCNAE.Situacao situacao) {
        CadastroEconomico ce = em.find(CadastroEconomico.class, vistoria.getAlvara().getCadastroEconomico().getId());
        for (EconomicoCNAE economicoCNAE : ce.getEconomicoCNAE()) {
            for (SituacaoEconomicoCNAE sit : economicoCNAE.getSituacoes()) {
                if (sit.getVistoria().equals(vistoria) && (sit.getSituacao().equals(SituacaoEconomicoCNAE.Situacao.LICENCIADO) || sit.getSituacao().equals(SituacaoEconomicoCNAE.Situacao.NAO_LICENCIADO))) {
                    return;
                }
            }
            economicoCNAE.getSituacoes().add(geraSituacaoCadastroEconomico(economicoCNAE, vistoria, situacao));
            em.merge(economicoCNAE);
        }
    }

    @Override
    public void salvarNovo(Vistoria entity) {
        salvarArquivos(entity.getArquivos());
        em.persist(entity);
        verificaGeraSituacaoEconomicoCNAE(entity, SituacaoEconomicoCNAE.Situacao.EMBARGADO);
    }

    public void salvarArquivos(List<ArquivoVistoria> arquivos) {
        for(ArquivoVistoria av : arquivos){
            if(av.getFile() != null){
                try {
                    UploadedFile arquivoRecebido = (UploadedFile) av.getFile();
                    av.getArquivo().setNome(arquivoRecebido.getFileName());
                    av.getArquivo().setMimeType(arquivoRecebido.getContentType());
                    av.getArquivo().setTamanho(arquivoRecebido.getSize());
                    arquivoFacade.novoArquivo(av.getArquivo(), arquivoRecebido.getInputstream());
                    arquivoRecebido.getInputstream().close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private void verificaGeraSituacaoEconomicoCNAE(Vistoria entity, SituacaoEconomicoCNAE.Situacao situacao) {
        CadastroEconomico ce = em.find(CadastroEconomico.class, entity.getAlvara().getCadastroEconomico().getId());
        SituacaoEconomicoCNAE situacaoEconomicoCNAE = null;

        Map<EconomicoCNAE, SituacaoEconomicoCNAE> mapa = new HashMap<EconomicoCNAE, SituacaoEconomicoCNAE>();
        for (EconomicoCNAE economicoCnae : ce.getEconomicoCNAE()) {

            List<SituacaoEconomicoCNAE> listaDeSituacoes = economicoCnae.getSituacoes();
            for (VistoriaCnae vistoriaCnae : entity.getCnaes()) {
                if (economicoCnae.getSituacoes().isEmpty() || situacao.equals(SituacaoEconomicoCNAE.Situacao.LICENCIADO)) {
                    if (economicoCnae.getCnae().equals(vistoriaCnae.getCnae())) {
                        mapa.put(economicoCnae, geraSituacaoCadastroEconomico(economicoCnae, entity, situacao));
                    }
                } else {
                    for (SituacaoEconomicoCNAE sec : listaDeSituacoes) {
                        Vistoria vistoriaRecuperada = em.find(Vistoria.class, sec.getVistoria().getId());
                        if (!vistoriaRecuperada.equals(entity)) {
                            if (economicoCnae.getCnae().equals(vistoriaCnae.getCnae())) {
                                mapa.put(economicoCnae, geraSituacaoCadastroEconomico(economicoCnae, entity, situacao));
                            }
                        }
                    }
                }
            }
        }
        for (EconomicoCNAE eco : mapa.keySet()) {
            persisteEconomicoCnae(eco, mapa.get(eco));
        }
    }

    public void persisteEconomicoCnae(EconomicoCNAE economicoCnae, SituacaoEconomicoCNAE situacao) {
        economicoCnae = em.find(EconomicoCNAE.class, economicoCnae.getId());
        economicoCnae.getSituacoes().size();
        economicoCnae.getSituacoes().add(situacao);
        em.merge(economicoCnae);
    }

    private SituacaoEconomicoCNAE geraSituacaoCadastroEconomico(EconomicoCNAE economicoCnae, Vistoria entity, SituacaoEconomicoCNAE.Situacao situacao) {
        SituacaoEconomicoCNAE sec = new SituacaoEconomicoCNAE();
        sec.setDataLancamento(new Date());
        sec.setEconomicoCNAE(economicoCnae);
        sec.setVistoria(entity);
        sec.setSituacao(situacao);
        return sec;
    }

    public void gerarVistoria(Alvara alvara, TipoAlvara tipo, String numeroProtocolo) {
        VOAlvara voAlvara = calculoAlvaraFacade.preencherVOAlvaraPorIdAlvara(alvara.getId());

        if (voAlvara != null && !temVistoriaNaoFinalizada(voAlvara.getId(), false) && !hasVistoriaNaoFinalizadaProcessoCalcAlvara(
            voAlvara.getId(), false)) {

            CadastroEconomico cadastroEconomico = em.find(CadastroEconomico.class, alvara.getCadastroEconomico().getId());

            List<EconomicoCNAE> listaEconomicoCnae = Lists.newArrayList();

            if(VOAlvara.TipoVoAlvara.PROCESSO_CALCULO.equals(voAlvara.getTipoVoAlvara())) {
                preencherCnaeNovoCalculo(voAlvara, cadastroEconomico, listaEconomicoCnae);
            } else {
                listaEconomicoCnae = cadastroEconomico.getEconomicoCNAE();
            }
            List<ConfiguracaoIrregularidadesDoAlvara> irregularidadesDoAlvaras = irregularidadesDaConfiguracao(tipo);

            Vistoria vistoria = new Vistoria();
            vistoria.setAlvara(alvara);
            vistoria.setStatusVistoria(StatusVistoria.ABERTA);
            vistoria.setData(new Date());
            vistoria.setNumeroProtocolo(numeroProtocolo);
            vistoria.setTipoAlvara(tipo);
            vistoria.setSequencia(getSequencia());

            for (EconomicoCNAE ec : listaEconomicoCnae) {
                if (ec.getCnae().getFiscalizacaoSanitaria()) {
                    VistoriaCnae vistoriaCnae = new VistoriaCnae();
                    vistoriaCnae.setVistoria(vistoria);
                    vistoriaCnae.setCnae(ec.getCnae());
                    vistoriaCnae.setEmbargado(GrauDeRiscoDTO.ALTO.equals(ec.getCnae().getGrauDeRisco()));
                    vistoria.getCnaes().add(vistoriaCnae);
                }
            }

            if (irregularidadesDoAlvaras != null) {
                if (!irregularidadesDoAlvaras.isEmpty()) {
                    for (ConfiguracaoIrregularidadesDoAlvara c : irregularidadesDoAlvaras) {
                        IrregularidadeDaVistoria irregularidadeDaVistoria = new IrregularidadeDaVistoria();
                        irregularidadeDaVistoria.setVistoria(vistoria);
                        irregularidadeDaVistoria.setIrregularidade(c.getIrregularidade());
                        irregularidadeDaVistoria.setObservacao("");
                        vistoria.getListaIrregularidade().add(irregularidadeDaVistoria);
                    }
                }
            }
            em.persist(vistoria);
        }
    }

    private void preencherCnaeNovoCalculo(VOAlvara voAlvara, CadastroEconomico cadastroEconomico, List<EconomicoCNAE> listaEconomicoCnae) {
        if(!voAlvara.getCnaes().isEmpty()) {
            for (VOAlvaraCnaes cnae : voAlvara.getCnaes()) {
                for (EconomicoCNAE economicoCNAE : cadastroEconomico.getEconomicoCNAE()) {
                    if(cnae.getIdCnae().equals(economicoCNAE.getCnae().getId())) {
                        listaEconomicoCnae.add(economicoCNAE);
                        break;
                    }
                }
            }
        }
    }

    public boolean hasVistoriaNaoFinalizadaProcessoCalcAlvara(Long idProcesso, boolean validarNivel) {
        String sql = " select vistoria.* from vistoria " +
            " inner join alvara alv on vistoria.alvara_id = alv.id " +
            " inner join processocalculoalvara processo on alv.id = processo.alvara_id " +
            " inner join cnaealvara ca on alv.id = ca.alvara_id " +
            " inner join cnae on ca.cnae_id = cnae.id " +
            " where (vistoria.statusvistoria = :statusAberta or vistoria.statusvistoria = :statusTramitando) " +
            " and processo.id = :idProcesso " + (validarNivel ? " and cnae.grauderisco = :grauDeRisco " : "");

        Query q = em.createNativeQuery(sql);
        q.setParameter("statusAberta", StatusVistoria.ABERTA.name());
        q.setParameter("statusTramitando", StatusVistoria.TRAMITANDO.name());
        q.setParameter("idProcesso", idProcesso);

        if (validarNivel) {
            q.setParameter("grauDeRisco", GrauDeRiscoDTO.ALTO.name());
        }

        return !q.getResultList().isEmpty();
    }

    public boolean temVistoriaNaoFinalizada(Long idCalculo, boolean validarNivel) {
        String sql = "select vis.id from vistoria vis " +
            "inner join alvara al on al.id = vis.alvara_id " +
            "inner join calculoalvarasanitario cal on cal.alvara_id = al.id " +
            "inner join cnaealvara ca on ca.alvara_id = al.id " +
            "inner join cnae cnae on cnae.id = ca.cnae_id " +
            "where (vis.statusVistoria = :aberta or vis.statusVistoria = :tramitando) " +
            "  and cal.id = :idCalculo ";
        if (validarNivel) {
            sql += "  and cnae.grauDeRisco = :grauDeRiscoAlto ";
        }
        Query q = em.createNativeQuery(sql);
        q.setParameter("idCalculo", idCalculo);
        q.setParameter("aberta", StatusVistoria.ABERTA.name());
        q.setParameter("tramitando", StatusVistoria.TRAMITANDO.name());
        if (validarNivel) {
            q.setParameter("grauDeRiscoAlto", GrauDeRiscoDTO.ALTO.name());
        }
        return !q.getResultList().isEmpty();
    }

    public boolean verificaSituacaoEmAberto(Vistoria vistoria) {
        String hql = "select v from Vistoria v where v != :vistoria and v.statusVistoria = :status";
        Query q = em.createQuery(hql);
        q.setParameter("vistoria", vistoria);
        q.setParameter("status", StatusVistoria.ABERTA);
        if (q.getResultList().size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public Long getSequencia() {
        Query q = em.createNativeQuery("SELECT coalesce(max(sequencia), 0) + 1 AS sequencia FROM Vistoria");
        BigDecimal resultado = (BigDecimal) q.getSingleResult();
        return resultado.longValue();
    }

    public Boolean verificaSequenciaExistente(Vistoria vistoria) {
        Query q = em.createNativeQuery("SELECT * FROM Vistoria v WHERE v.sequencia = :sequencia");
        q.setParameter("sequencia", vistoria.getSequencia());
        if (q.getResultList() == null || q.getResultList().size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public List<LotacaoTribUsuario> getUsuariosPorLotacaoVigente(LotacaoVistoriadora lotacao) {
        String sql = "SELECT * FROM LotacaoTribUsuario WHERE lotacao_id = :lotacao";
        Query q = em.createNativeQuery(sql, LotacaoTribUsuario.class);
        q.setParameter("lotacao", lotacao.getId());
        return q.getResultList();
    }

    @Override
    public List<Vistoria> listaFiltrandoX(String s, int inicio, int max, Field... atributos) {
        StringBuilder hql = new StringBuilder("select v from Vistoria v where to_char(v.exercicioInicial.ano) like :filtro ")
                .append(" or to_char(v.exercicioFinal.ano) like :filtro ")
                .append(" or lower(v.lotacaoVistoriadora.descricao) like :filtro ")
                .append(" or lower(v.tipoVistoria) like :filtro ")
                .append(" or lower(v.tipoAlvara) like :filtro ")
                .append(" or v.cadastroEconomico.inscricaoCadastral like :filtro ");
        Query q = em.createQuery(hql.toString());
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        q.setMaxResults(max + 1);
        q.setFirstResult(inicio);
        return q.getResultList();
    }

    public List<Alvara> getAlvarasPorCMC(CadastroEconomico cadastroEconomico) {
        StringBuilder sql = new StringBuilder("SELECT ALVARA.* FROM ALVARA ");
        sql.append(" INNER JOIN CADASTROECONOMICO CE ON CE.ID = ALVARA.CADASTROECONOMICO_ID ")
            .append(" WHERE (ALVARA.ID = (SELECT MAX(A.ID) FROM ALVARA A WHERE A.CADASTROECONOMICO_ID = CE.ID AND A.TIPOALVARA = '")
            .append(TipoAlvara.FUNCIONAMENTO.name()).append("') ")
            .append("  OR ALVARA.ID = (SELECT MAX(A.ID) FROM ALVARA A WHERE A.CADASTROECONOMICO_ID = CE.ID AND A.TIPOALVARA = '")
            .append(TipoAlvara.LOCALIZACAO.name()).append("') ")
            .append("  OR ALVARA.ID = (SELECT MAX(A.ID) FROM ALVARA A WHERE A.CADASTROECONOMICO_ID = CE.ID AND A.TIPOALVARA = '")
            .append(TipoAlvara.SANITARIO.name()).append("') ")
            .append("  OR ALVARA.ID = (SELECT MAX(A.ID) FROM ALVARA A WHERE A.CADASTROECONOMICO_ID = CE.ID AND A.TIPOALVARA = '")
            .append(TipoAlvara.LOCALIZACAO_FUNCIONAMENTO.name()).append("')) ")
            .append(" AND CE.ID = :cadastro ").append("  ORDER BY ALVARA.ID DESC");
        Query q = em.createNativeQuery(sql.toString(), Alvara.class);
        q.setParameter("cadastro", cadastroEconomico);
        return q.getResultList();
    }

    public List<CNAEAlvara> getCnaePorAlvara(Alvara alvara) {
        String hql = "select ca from CNAEAlvara ca where ca.alvara = :alvara";
        Query q = em.createQuery(hql);
        q.setParameter("alvara", alvara);
        return q.getResultList();
    }

    public List<ConfiguracaoIrregularidadesDoAlvara> irregularidadesDaConfiguracao(TipoAlvara tipoAlvara) {
        String hql = "select c from ConfiguracaoIrregularidadesDoAlvara c where c.tipoAlvara = :tipo and c.configuracaoTributario = :config";
        Query q = em.createQuery(hql);
        q.setParameter("tipo", tipoAlvara);
        q.setParameter("config", configuracaoTributarioFacade.retornaUltimo());
        return q.getResultList();
    }

    public List<ParecerVistoria> recuperarPareceres(Vistoria vistoria) {
        return em.createQuery("from ParecerVistoria where vistoria = :vistoria").setParameter("vistoria", vistoria).getResultList();
    }
}
