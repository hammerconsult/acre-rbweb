package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.AgrupadorVORevisaoCalculoLocalizacao;
import br.com.webpublico.entidadesauxiliares.VOCnaeAlteracoesCMC;
import br.com.webpublico.entidadesauxiliares.VOEndereco;
import br.com.webpublico.entidadesauxiliares.VORevisaoCalculoLocalizacao;
import br.com.webpublico.enums.Operador;
import br.com.webpublico.enums.SituacaoCalculoAlvara;
import br.com.webpublico.enums.TipoLogradouroEnderecoCorreio;
import br.com.webpublico.tributario.enumeration.GrauDeRiscoDTO;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.Util;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.*;

@Stateless
public class AlteracaoCmcFacade {
    private static final Logger logger = LoggerFactory.getLogger(AlteracaoCmcFacade.class);

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private CadastroEconomicoFacade cadastroEconomicoFacade;

    public boolean hasAlteracaoCadastro(Long idCmc) {
        Alvara alvara = recuperarUltimoAlvaraDoCmc(idCmc);
        return alvara == null || (hasAlteracaoCnae(idCmc) || hasAlteracaoEndereco(idCmc, alvara));
    }

    public boolean hasAlteracaoEndereco(Long idCmc, Alvara alvara) {
        EnderecoAlvara enderecoAlvara = buscarEnderecoAlvara(alvara);
        EnderecoCadastroEconomico enderecoCmc = buscarEnderecoCmc(idCmc);

        if (enderecoAlvara == null && enderecoCmc == null) {
            return false;
        } else if (enderecoAlvara == null || enderecoCmc == null) {
            return true;
        }
        if ((!StringUtils.isBlank(enderecoAlvara.getNumero()) && StringUtils.isBlank(enderecoCmc.getNumero())) || (!StringUtils.isBlank(enderecoCmc.getNumero()) && StringUtils.isBlank(enderecoAlvara.getNumero()))) {
            return true;
        } else if (enderecoAlvara.getNumero() != null && enderecoCmc.getNumero() != null && !removerAcentosCaracteresEspeciaisEspacos(
            enderecoAlvara.getNumero()).equalsIgnoreCase(removerAcentosCaracteresEspeciaisEspacos(enderecoCmc.getNumero()))) {
            return true;
        }
        if ((!StringUtils.isBlank(enderecoAlvara.getComplemento()) && StringUtils.isBlank(enderecoCmc.getComplemento())) || (!StringUtils.isBlank(enderecoCmc.getComplemento()) && StringUtils.isBlank(enderecoAlvara.getComplemento()))) {
            return true;
        } else if (enderecoAlvara.getComplemento() != null && enderecoCmc.getComplemento() != null && !removerAcentosCaracteresEspeciaisEspacos(
            enderecoAlvara.getComplemento()).equalsIgnoreCase(removerAcentosCaracteresEspeciaisEspacos(enderecoCmc.getComplemento()))) {
            return true;
        }
        String logradouro = enderecoAlvara.getLogradouro();

        String bairro = enderecoAlvara.getBairro();

        if (enderecoCmc.getLogradouro() != null && !removerAcentosCaracteresEspeciaisEspacos(logradouro).equalsIgnoreCase(
            removerAcentosCaracteresEspeciaisEspacos(enderecoCmc.getLogradouro()))) {
            return true;
        }
        if (enderecoCmc.getBairro() != null && !removerAcentosCaracteresEspeciaisEspacos(bairro).equalsIgnoreCase(removerAcentosCaracteresEspeciaisEspacos(
            enderecoCmc.getBairro()))) {
            return true;
        }
        if ((!StringUtils.isBlank(enderecoAlvara.getCep()) && StringUtils.isBlank(enderecoCmc.getCep())) || (!StringUtils.isBlank(enderecoCmc.getCep()) && StringUtils.isBlank(enderecoAlvara.getCep()))) {
            return true;
        }
        return enderecoAlvara.getCep() != null && enderecoCmc.getCep() != null && !removerAcentosCaracteresEspeciaisEspacos(
            enderecoAlvara.getCep()).equalsIgnoreCase(removerAcentosCaracteresEspeciaisEspacos(enderecoCmc.getCep()));
    }

    public EnderecoCorreio buscarEnderecoPessoaPeloIdCmc(Long idCmc) {
        CadastroEconomico cmc = em.find(CadastroEconomico.class, idCmc);
        if (cmc != null) {
            return buscarEnderecoPessoa(cmc.getPessoa().getId());
        }
        return null;
    }

    private EnderecoCorreio buscarEnderecoPessoa(Long idPessoa) {
        Pessoa pessoa = em.find(Pessoa.class, idPessoa);
        return pessoa != null ? pessoa.getEnderecoPrincipal() : null;
    }

    public String removerAcentosCaracteresEspeciaisEspacos(String str) {
        if (str != null) {
            return StringUtil.removeCaracteresEspeciais(StringUtil.removeAcentuacao(str)).replaceAll("\\s+", "");
        }
        return "";
    }

    public EnderecoAlvara buscarEnderecoAlvara(Alvara alvara) {
        return alvara.getEnderecoPrincipal();
    }

    public <T> List<CNAE> montarListaCnaes(List<T> cnaes) {
        try {
            return Lists.newArrayList(Lists.transform(cnaes, new Function<T, CNAE>() {
                @Override
                public CNAE apply(T cnae) {
                    if (cnae != null) {
                        return cnae instanceof EconomicoCNAE ? ((EconomicoCNAE) cnae).getCnae() : ((CNAEAlvara) cnae).getCnae();
                    }
                    return null;
                }
            }));
        } catch (Exception e) {
            logger.error("Erro ao converter lista de cnaes. ", e);
        }
        return Collections.emptyList();
    }

    public String montarSqlPadraoUltimoCalculo() {
        return montarSqlPadraoUltimoCalculo("");
    }

    public String montarSqlPadraoUltimoCalculo(String complementoWhere) {
        return " from alvara al " +
            " left join processocalculoalvara pca on al.id = pca.alvara_id " +
            " left join calculoalvaralocalizacao cal on al.id = cal.alvara_id " +
            " left join calculoalvarafunc caf on al.id = caf.alvara_id " +
            " left join calculoalvarasanitario cas on al.id = cas.alvara_id " +
            " left join processocalculo proc on proc.id = coalesce(cal.processocalculoalvaraloc_id, caf.processocalculo_id, cas.processocalculoalvarasan_id) " +
            " where coalesce(pca.situacaocalculoalvara, cal.situacaocalculoalvara, caf.situacaocalculoalvara, cas.situacaocalculoalvara) <> :estornado " +
            " and al.cadastroeconomico_id = :idCmc " +
            complementoWhere +
            " order by proc.datalancamento desc fetch first 1 rows only ";
    }

    public Alvara recuperarUltimoAlvaraDoCmc(Long idCmc) {
        return recuperarUltimoAlvaraDoCmc(idCmc, "");
    }

    public Alvara recuperarUltimoAlvaraDoCmc(Long idCmc, String complementoWhere) {
        String sql = " select al.* " + montarSqlPadraoUltimoCalculo(complementoWhere);

        Query q = em.createNativeQuery(sql, Alvara.class);
        q.setParameter("idCmc", idCmc);
        q.setParameter("estornado", SituacaoCalculoAlvara.ESTORNADO.name());

        List<Alvara> alvaras = q.getResultList();
        if (alvaras != null && !alvaras.isEmpty()) {
            Alvara alvara = alvaras.get(0);
            Hibernate.initialize(alvara.getCnaeAlvaras());
            return alvara;
        }
        return null;
    }

    public EnderecoCadastroEconomico buscarEnderecoCmc(Long idCmc) {
        return cadastroEconomicoFacade.getLocalizacao(em.find(CadastroEconomico.class, idCmc));
    }

    public boolean hasAlteracaoEndereco(Long idCmc) {
        Alvara alvara = recuperarUltimoAlvaraDoCmc(idCmc);
        return alvara == null || hasAlteracaoEndereco(idCmc, alvara);
    }

    public boolean hasAlteracaoCnae(Long idCmc) {
        return buscarNumeroDeAlteracoesCnae(idCmc) > 0;
    }

    public int buscarNumeroDeAlteracoesCnae(Long idCmc) {
        boolean isAdicionouRevisaoAntesAlteracao = false;
        List<Object[]> objetos = Lists.newArrayList();

        List<Object[]> revisoesAnterioresAlteracao = buscarRevisoesCmc(idCmc, false);
        if (revisoesAnterioresAlteracao != null && !revisoesAnterioresAlteracao.isEmpty()) {
            isAdicionouRevisaoAntesAlteracao = true;
            objetos.addAll(revisoesAnterioresAlteracao);
        }
        List<Object[]> revisoesAposAlteracao = buscarRevisoesCmc(idCmc, true);
        objetos.addAll(revisoesAposAlteracao);

        Map<Long, List<VOCnaeAlteracoesCMC>> cnaesPorRevisao = Maps.newLinkedHashMap();

        String sql = " select c.codigocnae, c.grauderisco, ec.tipo " +
            " from economicocnae_aud ec " +
            " inner join cnae c on ec.cnae_id = c.id " +
            " where ec.cadastroeconomico_id = :idCmc " +
            " and ec.rev = (select ec2.rev " +
            "               from economicocnae_aud ec2 " +
            "               where ec2.rev <= :revisao " +
            "               and ec.id = ec2.id " +
            "               order by ec2.rev desc fetch first 1 rows only) " +
            " and ec.revtype <> :exclusao " +
            " order by ec.tipo, c.codigocnae ";

        for (Object[] obj : objetos) {
            if (obj[0] != null) {
                long revisao = ((BigDecimal) obj[0]).longValue();

                Query q = em.createNativeQuery(sql);
                q.setParameter("idCmc", idCmc);
                q.setParameter("revisao", revisao);
                q.setParameter("exclusao", 2);

                List<Object[]> objetosCnae = q.getResultList();

                if (objetosCnae != null && !objetosCnae.isEmpty()) {
                    for (Object[] dadosEconomicoCnae : objetosCnae) {
                        String codigoCnae = dadosEconomicoCnae[0] != null ? (String) dadosEconomicoCnae[0] : null;
                        GrauDeRiscoDTO grauDeRisco = dadosEconomicoCnae[1] != null ? Util.traduzirEnum(GrauDeRiscoDTO.class, (String) dadosEconomicoCnae[1]) : null;
                        EconomicoCNAE.TipoCnae tipoCnae = dadosEconomicoCnae[2] != null ? Util.traduzirEnum(EconomicoCNAE.TipoCnae.class,
                            (String) dadosEconomicoCnae[2]) : null;

                        VOCnaeAlteracoesCMC cnae = new VOCnaeAlteracoesCMC(codigoCnae, grauDeRisco, tipoCnae);
                        if (!cnaesPorRevisao.containsKey(revisao)) {
                            cnaesPorRevisao.put(revisao, Lists.newArrayList(cnae));
                        } else {
                            cnaesPorRevisao.get(revisao).add(cnae);
                        }
                    }
                }
            }
        }

        if (cnaesPorRevisao.size() > 1) {
            List<Long> revisoes = Lists.newLinkedList(cnaesPorRevisao.keySet());

            for (int i = 0; i < revisoes.size() - 1; i++) {
                for (int j = 0; j < revisoes.size(); j++) {
                    if (i == j - 1) {
                        List<VOCnaeAlteracoesCMC> atuais = cnaesPorRevisao.get(revisoes.get(i));
                        List<VOCnaeAlteracoesCMC> proximos = cnaesPorRevisao.get(revisoes.get(j));

                        if (atuais.size() == proximos.size() && atuais.containsAll(proximos)) {
                            cnaesPorRevisao.remove(revisoes.get(i));
                        }
                    }
                }
            }
        }

        return cnaesPorRevisao.size() - (isAdicionouRevisaoAntesAlteracao ? 1 : 0);
    }

    private List<Object[]> buscarRevisoesCmc(Long idCmc, boolean isAlteracao) {
        Operador operador = isAlteracao ? Operador.MAIOR : Operador.MENOR;

        String sql = " select distinct revisao.id, revisao.datahora " +
            " from cadastroeconomico_aud cmc " +
            " inner join revisaoauditoria revisao on cmc.rev = revisao.id " +
            " where cmc.id = :idCmc " +
            " and revisao.datahora " + operador.getOperador() + " (select al.dataalteracao " + montarSqlPadraoUltimoCalculo() + ") " +
            " order by revisao.datahora " + (!isAlteracao ? " desc fetch first 1 rows only " : "");

        Query q = em.createNativeQuery(sql);
        q.setParameter("idCmc", idCmc);
        q.setParameter("estornado", SituacaoCalculoAlvara.ESTORNADO.name());

        List<Object[]> objetos = q.getResultList();
        return objetos != null ? objetos : Lists.<Object[]>newArrayList();
    }

    public AgrupadorVORevisaoCalculoLocalizacao buscarRevisoesDosCalculoDeLocalizacaoDoCmc(Long idCmc, Long idCalculo) {
        List<VORevisaoCalculoLocalizacao> alteracoes = Lists.newArrayList();

        buscarRevisoesPorCmcAndCalculo(idCmc, idCalculo, false, true, alteracoes);
        buscarRevisoesPorCmcAndCalculo(idCmc, idCalculo, true, true, alteracoes);
        buscarRevisoesPorCmcAndCalculo(idCmc, idCalculo, false, false, alteracoes);
        buscarRevisoesPorCmcAndCalculo(idCmc, idCalculo, true, false, alteracoes);

        return new AgrupadorVORevisaoCalculoLocalizacao(preencherCnaesAndEnderecoPelaRevisao(idCmc, alteracoes));
    }

    private void buscarRevisoesPorCmcAndCalculo(Long idCmc, Long idCalculo, boolean isAlteracao, boolean isCnae, List<VORevisaoCalculoLocalizacao> alteracoes) {
        List<Object[]> revisoes = isCnae ? buscarRevisoesCnae(idCmc, idCalculo, isAlteracao) : buscarRevisoesEndereco(idCmc, idCalculo, isAlteracao);

        for (Object[] revisao : revisoes) {
            VORevisaoCalculoLocalizacao alteracao = new VORevisaoCalculoLocalizacao();
            alteracao.setRevisao_id(((BigDecimal) revisao[0]).longValue());
            alteracao.setDataRevisao((Date) revisao[1]);
            alteracao.setAlteracao(isAlteracao);
            alteracao.setCnae(isCnae);

            alteracoes.add(alteracao);
        }
    }

    public List<Object[]> buscarRevisoesCnae(Long idCmc, Long idCalculo, boolean isAlteracao) {
        Operador operador = isAlteracao ? Operador.MAIOR : Operador.MENOR;
        String sql = " select distinct revisao.id, revisao.datahora " +
            " from cadastroeconomico_aud cmc " +
            " inner join revisaoauditoria revisao on cmc.rev = revisao.id " +
            " where cmc.id = :idCmc " +
            " and revisao.datahora " + operador.getOperador() +
            " (select coalesce(aud.dataalteracao, al.dataalteracao) from alvara al " +
            "  left join alvara_aud aud on al.id = aud.id " +
            "  left join processocalculoalvara pca on al.id = pca.alvara_id " +
            "  left join calculoalvaralocalizacao cal on al.id = cal.alvara_id " +
            "  left join calculoalvarafunc caf on al.id = caf.alvara_id " +
            "  left join calculoalvarasanitario cas on al.id = cas.alvara_id " +
            "  left join processocalculo proc on proc.id = coalesce(cal.processocalculoalvaraloc_id, caf.processocalculo_id, cas.processocalculoalvarasan_id) " +
            "  where coalesce(pca.situacaocalculoalvara, cal.situacaocalculoalvara, caf.situacaocalculoalvara, cas.situacaocalculoalvara) <> :estornado " +
            "  and al.cadastroeconomico_id = :idCmc " +
            "  and coalesce(aud.dataalteracao, al.dataalteracao) < (select revisao2.datahora " +
            "                          from calculoalvara_aud ca " +
            "                          inner join revisaoauditoria revisao2 on ca.rev = revisao2.id " +
            "                          where ca.id = :idCalculo order by revisao2.datahora fetch first 1 rows only) " +
            "  order by proc.datalancamento desc fetch first 1 rows only) " +
            " and revisao.datahora < (select revisao2.datahora " +
            "                         from calculoalvara_aud ca " +
            "                         inner join revisaoauditoria revisao2 on ca.rev = revisao2.id " +
            "                         where ca.id = :idCalculo order by revisao2.datahora fetch first 1 rows only) " +
            " order by revisao.datahora " + (!isAlteracao ? " desc fetch first 1 rows only " : "");

        Query q = em.createNativeQuery(sql);
        q.setParameter("idCmc", idCmc);
        q.setParameter("idCalculo", idCalculo);
        q.setParameter("estornado", SituacaoCalculoAlvara.ESTORNADO.name());

        List<Object[]> objetos = q.getResultList();

        return objetos != null ? objetos : Lists.<Object[]>newArrayList();
    }

    private List<Object[]> buscarRevisoesEndereco(Long idCmc, Long idCalculo, boolean isAlteracao) {
        Operador operador = isAlteracao ? Operador.MAIOR : Operador.MENOR;
        String sql = " select distinct revisao.id, revisao.datahora " +
            " from cadastroeconomico cmc " +
            " inner join vwenderecopessoa vw on cmc.pessoa_id = vw.pessoa_id " +
            " inner join enderecocorreio_aud aud on vw.endereco_id = aud.id " +
            " inner join revisaoauditoria revisao on aud.rev = revisao.id " +
            " where cmc.id = :idCmc " +
            " and revisao.datahora " + operador.getOperador() +
            " (select coalesce(aud.dataalteracao, al.dataalteracao) from alvara al " +
            "  left join alvara_aud aud on al.id = aud.id " +
            "  left join processocalculoalvara pca on al.id = pca.alvara_id " +
            "  left join calculoalvaralocalizacao cal on al.id = cal.alvara_id " +
            "  left join calculoalvarafunc caf on al.id = caf.alvara_id " +
            "  left join calculoalvarasanitario cas on al.id = cas.alvara_id " +
            "  left join processocalculo proc on proc.id = coalesce(cal.processocalculoalvaraloc_id, caf.processocalculo_id, cas.processocalculoalvarasan_id) " +
            "  where coalesce(pca.situacaocalculoalvara, cal.situacaocalculoalvara, caf.situacaocalculoalvara, cas.situacaocalculoalvara) <> :estornado " +
            "  and al.cadastroeconomico_id = :idCmc " +
            "  and coalesce(aud.dataalteracao, al.dataalteracao) < (select revisao2.datahora " +
            "                          from calculoalvara_aud ca " +
            "                          inner join revisaoauditoria revisao2 on ca.rev = revisao2.id " +
            "                          where ca.id = :idCalculo order by revisao2.datahora desc fetch first 1 rows only) " +
            "  order by proc.datalancamento desc fetch first 1 rows only) " +
            " and revisao.datahora < (select revisao2.datahora " +
            "                         from calculoalvara_aud ca " +
            "                         inner join revisaoauditoria revisao2 on ca.rev = revisao2.id " +
            "                         where ca.id = :idCalculo order by revisao2.datahora desc fetch first 1 rows only) " +
            " order by revisao.datahora " + (!isAlteracao ? " desc fetch first 1 rows only " : "");

        Query q = em.createNativeQuery(sql);
        q.setParameter("idCmc", idCmc);
        q.setParameter("idCalculo", idCalculo);
        q.setParameter("estornado", SituacaoCalculoAlvara.ESTORNADO.name());

        List<Object[]> objetos = q.getResultList();
        return objetos != null ? objetos : Lists.<Object[]>newArrayList();
    }

    private List<VORevisaoCalculoLocalizacao> preencherCnaesAndEnderecoPelaRevisao(Long idCmc, List<VORevisaoCalculoLocalizacao> alteracoes) {
        Set<VORevisaoCalculoLocalizacao> cnaesPorRevisao = Sets.newLinkedHashSet();
        for (VORevisaoCalculoLocalizacao alteracao : alteracoes) {
            if (alteracao.isCnae()) {
                List<Object[]> cnaes = buscarCnaesPorRevisaoCmc(idCmc, alteracao.getRevisao_id());
                for (Object[] cnae : cnaes) {
                    String codigoCnae = cnae[0] != null ? (String) cnae[0] : null;
                    GrauDeRiscoDTO grauDeRisco = cnae[1] != null ? Util.traduzirEnum(GrauDeRiscoDTO.class, (String) cnae[1]) : null;
                    EconomicoCNAE.TipoCnae tipoCnae = cnae[2] != null ? Util.traduzirEnum(EconomicoCNAE.TipoCnae.class, (String) cnae[2]) : null;

                    alteracao.getAlteracoesCnae().add(new VOCnaeAlteracoesCMC(codigoCnae, grauDeRisco, tipoCnae));
                }
                cnaesPorRevisao.add(alteracao);
            } else {
                VOEndereco endereco = buscarAlteracoesEndereco(idCmc, alteracao.getRevisao_id());
                alteracao.setEndereco(endereco);
            }
        }

        Set<VORevisaoCalculoLocalizacao> alteracoesRemover = Sets.newHashSet();

        for (int i = 0; i < cnaesPorRevisao.size() - 1; i++) {
            for (int j = 0; j < cnaesPorRevisao.size(); j++) {
                if (i == j - 1) {
                    List<VOCnaeAlteracoesCMC> atuais = Lists.newArrayList(cnaesPorRevisao).get(i).getAlteracoesCnae();
                    List<VOCnaeAlteracoesCMC> proximos = Lists.newArrayList(cnaesPorRevisao).get(j).getAlteracoesCnae();

                    if (atuais.size() == proximos.size() && atuais.containsAll(proximos)) {
                        alteracoesRemover.add(Lists.newArrayList(cnaesPorRevisao).get(i));
                    }
                }
            }
        }

        alteracoes.removeAll(Lists.newArrayList(alteracoesRemover));
        return alteracoes;
    }

    public List<Object[]> buscarCnaesPorRevisaoCmc(Long idCmc, Long idRevisao) {
        String sql = " select c.codigocnae, c.grauderisco, ec.tipo " +
            " from economicocnae_aud ec " +
            " inner join cnae c on ec.cnae_id = c.id " +
            " where ec.cadastroeconomico_id = :idCmc " +
            " and ec.rev = (select ec2.rev " +
            "               from economicocnae_aud ec2 " +
            "               where ec2.rev <= :revisao " +
            "               and ec.id = ec2.id " +
            "               order by ec2.rev desc fetch first 1 rows only) " +
            " and ec.revtype <> :exclusao " +
            " order by ec.tipo, c.codigocnae ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("idCmc", idCmc);
        q.setParameter("revisao", idRevisao);
        q.setParameter("exclusao", 2);

        List<Object[]> objetos = q.getResultList();
        return objetos != null ? objetos : Lists.<Object[]>newArrayList();
    }

    private VOEndereco buscarAlteracoesEndereco(Long idCmc, Long idRevisao) {
        String sql = " select distinct aud.tipologradouro, aud.logradouro, aud.numero, aud.bairro, aud.cep, aud.localidade, aud.uf " +
            " from cadastroeconomico cmc " +
            " inner join vwenderecopessoa vw on cmc.pessoa_id = vw.pessoa_id " +
            " inner join enderecocorreio_aud aud on vw.endereco_id = aud.id " +
            " inner join revisaoauditoria revisao on aud.rev = revisao.id " +
            " where cmc.id = :idCmc " +
            " and revisao.id = :idRevisao " +
            " order by revisao.id desc fetch first 1 rows only ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("idCmc", idCmc);
        q.setParameter("idRevisao", idRevisao);

        List<Object[]> revisoes = q.getResultList();

        if (revisoes != null && !revisoes.isEmpty()) {
            for (Object[] obj : revisoes) {
                VOEndereco endereco = new VOEndereco();
                endereco.setTipoLogradouro(Util.traduzirEnum(TipoLogradouroEnderecoCorreio.class, (obj[0] != null ? (String) obj[0] : "")));
                endereco.setLogradouro(obj[1] != null ? (String) obj[1] : "");
                endereco.setNumero(obj[2] != null ? (String) obj[2] : "");
                endereco.setBairro(obj[3] != null ? (String) obj[3] : "");
                endereco.setCep(obj[4] != null ? (String) obj[4] : "");
                endereco.setLocalidade(obj[5] != null ? (String) obj[5] : "");
                endereco.setUf(obj[6] != null ? (String) obj[6] : "");

                return endereco;
            }
        }
        return null;
    }
}
