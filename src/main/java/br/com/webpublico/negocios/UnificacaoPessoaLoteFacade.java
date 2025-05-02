package br.com.webpublico.negocios;

import br.com.webpublico.DateUtils;
import br.com.webpublico.StringUtils;
import br.com.webpublico.entidadesauxiliares.comum.FiltroUnificacaoPessoaLote;
import br.com.webpublico.enums.PerfilEnum;
import br.com.webpublico.enums.SituacaoCadastralPessoa;
import br.com.webpublico.enums.TipoPessoa;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.viewobjects.PessoaDuplicadaDTO;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Stateless
public class UnificacaoPessoaLoteFacade implements Serializable {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;


    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 1)
    public Future<List<PessoaDuplicadaDTO>> buscarPessoasDuplicadas(AssistenteBarraProgresso assistenteBarraProgresso,
                                                                    FiltroUnificacaoPessoaLote filtro) {
        List<PessoaDuplicadaDTO> pessoasDuplicadas = Lists.newArrayList();
        assistenteBarraProgresso.zerarContadoresProcesso();
        assistenteBarraProgresso.setDescricaoProcesso("Buscando CPF/CNPJ duplicados, essa busca pode ser demorada.");

        String sql = "select " +
            "    p.id as id, " +
            "    p.dataregistro, " +
            "    case when pf.id is not null then 'FISICA' else 'JURIDICA' end as tipo, " +
            "    coalesce(pf.cpf, pj.cnpj) as cpf_cnpj, " +
            "    coalesce(pf.nome, pj.razaosocial) as nome_razaosocial, " +
            "    p.situacaocadastralpessoa as situacao, " +
            "    (select listagg(pp.perfil, ',') within group (order by pp.perfil) from pessoa_perfil pp where pp.id_pessoa = p.id) as perfis, " +
            "    funcexistemovtributario(p.id) as movimento_tributario, " +
            "    funcexistemovrh(p.id) as movimento_rh, " +
            "    funcexistemovcontabil(p.id) as movimento_contabil, " +
            "    funcexistemovadm(p.id) as movimento_adm " +
            "   from pessoa p " +
            "  left join pessoafisica pf on pf.id = p.id " +
            "  left join pessoajuridica pj on pj.id = p.id " +
            " where coalesce(pf.cpf, pj.cnpj) in ( select coalesce(d_pf.cpf, d_pj.cnpj) " +
            "                                         from pessoa d_p " +
            "                                        left join pessoafisica d_pf on d_pf.id = d_p.id " +
            "                                        left join pessoajuridica d_pj on d_pj.id = d_p.id " +
            "                                      where valida_cpf_cnpj(coalesce(d_pf.cpf, d_pj.cnpj)) = 'S'" +
            "                                      group by coalesce(d_pf.cpf, d_pj.cnpj)" +
            "                                      having count(1) > 1) ";
        if (filtro.getTipoPessoa() != null) {
            sql += " and case when pf.id is not null then 'FISICA' else 'JURIDICA' end = :tipo ";
        }
        if (!Strings.isNullOrEmpty(filtro.getCpfCnpj())) {
            sql += " and coalesce(pf.cpf, pj.cnpj) = :cpf_cnpj ";
        }
        if (filtro.getDataCadastro() != null) {
            sql += " and exists (select 1 " +
                "                   from pessoa s_p " +
                "                  left join pessoafisica s_pf on s_pf.id = s_p.id " +
                "                  left join pessoajuridica s_pj on s_pj.id = s_p.id " +
                "               where coalesce(s_pf.cpf, s_pj.cnpj) = coalesce(pf.cpf, pj.cnpj) " +
                "                 and s_p.dataregistro >= :data_cadastro ) ";
        }
        sql += " order by coalesce(pf.cpf, pj.cnpj) ";
        Query q = em.createNativeQuery(sql);
        if (filtro.getTipoPessoa() != null) {
            q.setParameter("tipo", filtro.getTipoPessoa().name());
        }
        if (!Strings.isNullOrEmpty(filtro.getCpfCnpj())) {
            q.setParameter("cpf_cnpj", StringUtils.removerMascaraCpfCnpj(filtro.getCpfCnpj()));
        }
        if (filtro.getDataCadastro() != null) {
            q.setParameter("data_cadastro", DateUtils.dataSemHorario(filtro.getDataCadastro()));
        }
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            for (Object[] o : (List<Object[]>) resultList) {
                AtomicInteger index = new AtomicInteger(0);
                PessoaDuplicadaDTO pessoa = new PessoaDuplicadaDTO();
                pessoa.setId(((Number) o[index.getAndIncrement()]).longValue());
                pessoa.setDataCadastro(((Date) o[index.getAndIncrement()]));
                pessoa.setTipoPessoa(TipoPessoa.valueOf((String) o[index.getAndIncrement()]));
                pessoa.setCpfCnpj((String) o[index.getAndIncrement()]);
                pessoa.setNomeRazaoSocial((String) o[index.getAndIncrement()]);
                if (o[index.get()] != null && !Strings.isNullOrEmpty((String) o[index.get()])) {
                    pessoa.setSituacao(SituacaoCadastralPessoa.valueOf((String) o[index.get()]));
                }
                index.incrementAndGet();
                String perfis = (String) o[index.getAndIncrement()];
                if (!Strings.isNullOrEmpty(perfis)) {
                    String[] perfisSeparados = perfis.split(",");
                    List<PerfilEnum> perfisEnum = Lists.newArrayList();
                    for (String perfil : perfisSeparados) {
                        perfisEnum.add(PerfilEnum.valueOf(perfil));
                    }
                    pessoa.setPerfis(org.apache.commons.lang.StringUtils.join(perfisEnum, ","));
                }
                pessoa.setPossuiMovimentoTributario(((Number) o[index.getAndIncrement()]).intValue() == 1);
                pessoa.setPossuiMovimentoRh(((Number) o[index.getAndIncrement()]).intValue() == 1);
                pessoa.setPossuiMovimentoContabil(((Number) o[index.getAndIncrement()]).intValue() == 1);
                pessoa.setPossuiMovimentoAdministrativo(((Number) o[index.getAndIncrement()]).intValue() == 1);
                pessoasDuplicadas.add(pessoa);
            }
        }
        return new AsyncResult<>(pessoasDuplicadas);
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 8)
    public Future unificarPessoasDuplicadas(AssistenteBarraProgresso assistenteBarraProgresso,
                                            List<PessoaDuplicadaDTO> pessoasDuplicadas) {
        assistenteBarraProgresso.zerarContadoresProcesso();
        assistenteBarraProgresso.setDescricaoProcesso("Preparando unificação das pessoas duplicadas...");
        Map<PessoaDuplicadaDTO, List<PessoaDuplicadaDTO>> mapaPessoaDuplicada = montarMapaUnificacao(pessoasDuplicadas);
        List<String> updates = buscarUpdatesTransferenciaPessoa();

        assistenteBarraProgresso.setDescricaoProcesso("Unificando pessoas duplicadas...");
        Set<PessoaDuplicadaDTO> pessoasCorretas = mapaPessoaDuplicada.keySet();
        assistenteBarraProgresso.setTotal(pessoasCorretas.size());
        for (PessoaDuplicadaDTO pessoaCorreta : pessoasCorretas) {
            transferirMovimentos(updates, pessoaCorreta, mapaPessoaDuplicada.get(pessoaCorreta));
            assistenteBarraProgresso.conta();
        }
        return new AsyncResult(null);
    }

    private void transferirMovimentos(List<String> updates,
                                      PessoaDuplicadaDTO pessoaCorreta,
                                      List<PessoaDuplicadaDTO> pessoasIncorretas) {
        for (String update : updates) {
            for (PessoaDuplicadaDTO pessoaIncorreta : pessoasIncorretas) {
                transferirMovimentos(update, pessoaCorreta, pessoaIncorreta);
                inativarPessoaIncorreta(pessoaIncorreta);
            }
        }
    }

    private void inativarPessoaIncorreta(PessoaDuplicadaDTO pessoaIncorreta) {
        em.createNativeQuery(" UPDATE PESSOA SET SITUACAOCADASTRALPESSOA = :SITUACAO WHERE ID = :ID ")
            .setParameter("SITUACAO", SituacaoCadastralPessoa.INATIVO.name())
            .setParameter("ID", pessoaIncorreta.getId())
            .executeUpdate();

        em.createNativeQuery(" UPDATE PESSOAFISICA SET CPF = '00000000000', SEQUENCIAL = ID, NOME = 'PESSOA INATIVADA' WHERE ID = :ID ")
            .setParameter("ID", pessoaIncorreta.getId())
            .executeUpdate();

        em.createNativeQuery(" UPDATE PESSOAJURIDICA SET CNPJ = '00000000000000', SEQUENCIAL = ID, RAZAOSOCIAL = 'PESSOA INATIVADA' WHERE ID = :ID ")
            .setParameter("ID", pessoaIncorreta.getId())
            .executeUpdate();
    }

    private void transferirMovimentos(String update,
                                      PessoaDuplicadaDTO pessoaCorreta,
                                      PessoaDuplicadaDTO pessoaIncorreta) {
        em.createNativeQuery(update)
            .setParameter("PESSOA_CORRETA", pessoaCorreta.getId())
            .setParameter("PESSOA_INCORRETA", pessoaIncorreta.getId())
            .executeUpdate();
    }

    private Map<PessoaDuplicadaDTO, List<PessoaDuplicadaDTO>> montarMapaUnificacao(List<PessoaDuplicadaDTO> pessoasDuplicadas) {
        Map<PessoaDuplicadaDTO, List<PessoaDuplicadaDTO>> mapa = Maps.newHashMap();
        for (PessoaDuplicadaDTO pessoaDuplicada : pessoasDuplicadas) {
            if (pessoaDuplicada.getCorreta()) {
                mapa.put(pessoaDuplicada, new ArrayList<PessoaDuplicadaDTO>());
            }
        }
        for (PessoaDuplicadaDTO pessoaDuplicada : pessoasDuplicadas) {
            if (!pessoaDuplicada.getCorreta()) {
                PessoaDuplicadaDTO pessoaCorreta = getPessoaDuplicadaCorreta(mapa, pessoaDuplicada.getCpfCnpj());
                if (pessoaCorreta != null) {
                    mapa.get(pessoaCorreta).add(pessoaDuplicada);
                }
            }
        }
        return mapa;
    }

    private PessoaDuplicadaDTO getPessoaDuplicadaCorreta(Map<PessoaDuplicadaDTO, List<PessoaDuplicadaDTO>> mapa, String cpfCnpj) {
        for (PessoaDuplicadaDTO pessoaDuplicada : mapa.keySet()) {
            if (pessoaDuplicada.getCpfCnpj().equals(cpfCnpj)) {
                return pessoaDuplicada;
            }
        }
        return null;
    }

    public List<String> buscarUpdatesTransferenciaPessoa() {
        List<String> updates = Lists.newArrayList();
        String sql = " select cc.TABLE_NAME, cc.COLUMN_NAME " +
            "    from user_constraints c" +
            "   inner join user_cons_columns cc on cc.CONSTRAINT_NAME = c.CONSTRAINT_NAME" +
            "   inner join user_constraints cr on cr.CONSTRAINT_NAME = c.r_CONSTRAINT_NAME" +
            " where cr.TABLE_NAME IN ('PESSOA', 'PESSOAFISICA', 'PESSOAJURIDICA')" +
            "   and cc.TABLE_NAME not in ('PESSOA', 'PESSOAFISICA', 'PESSOAJURIDICA', 'PESSOA_ENDERECOCORREIO'," +
            " 'TELEFONE', 'CONTACORRENTEBANCPESSOA', 'PESSOA_PERFIL', 'CLASSECREDORPESSOA', 'PESSOACLASSIFICACAOCREDOR'," +
            " 'REPRESENTANTELEGALPESSOA','HISTORICOSITUACAOPESSOA','PESSOACNAE','PESSOAHORARIOFUNCIONAMENTO'," +
            " 'DOCUMENTOPESSOAL','PESSOAFISICACID','CONCELHOCLASSECONTRATOFP','MATRICULAFORMACAO','PESSOAHABILIDADE'," +
            " 'DEPENDENTE','TEMPOCONTRATOFPPESSOA','SOCIEDADEPESSOAJURIDICA','JUNTACOMERCIALPJ','FILIALPJ'," +
            " 'HISTORICOREDESIM', 'ALTERACAOCADASTRALPESSOA', 'ALTERACAOSTATUSPESSOA', 'TRANSFERENCIAMOVPESSOA'," +
            " 'STATUSCADASTROPESSOA') ";
        Query q = em.createNativeQuery(sql);
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            for (Object[] obj : (List<Object[]>) resultList) {
                updates.add(" UPDATE " + obj[0] + " SET " + obj[1] + " = :PESSOA_CORRETA WHERE " + obj[1] + " = :PESSOA_INCORRETA ");
            }
        }
        return updates;
    }
}
