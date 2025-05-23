/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.singletons;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.UnidadeOrganizacional;
import br.com.webpublico.entidadesauxiliares.IdentificadorMovimentoContabil;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.util.DataUtil;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import javax.ejb.*;
import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * @author reidocrime
 */
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@AccessTimeout(value = 50000)
public class SingletonGeradorCodigoContabil implements Serializable {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    private Map<Exercicio, Long> proximoCodigoReceitaRealizada;
    private Map<Exercicio, Long> proximoCodigoBensMoveis;
    private Map<Exercicio, Long> proximoCodigoBensImoveis;
    private Map<IdentificadorMovimentoContabil, String> proximoCodigoLiquidacao;
    private Map<IdentificadorMovimentoContabil, String> proximoCodigoAtoPotencial;
    private Map<IdentificadorMovimentoContabil, Long> proximoNumeroOrdemBancaria;
    private Map<IdentificadorMovimentoContabil, String> proximoNumeroObrigacaoPagar;
    private Map<IdentificadorMovimentoContabil, String> proximoNumeroInvestimento;
    private Map<IdentificadorMovimentoContabil, String> proximoNumeroReceitaExtra;
    private Map<IdentificadorMovimentoContabil, String> proximoNumeroResponsabilidade;
    private Map<IdentificadorMovimentoContabil, String> proximoNumeroReconhecimentoDivida;
    private Map<Exercicio, Long> proximoNumeroSolicitacaoReconhecimentoDivida;
    private Map<Exercicio, Long> proximoNumeroEmenda;
    private Map<Exercicio, Long> proximoNumeroTransferenciaBensMoveis;
    private Map<Exercicio, Long> proximoNumeroTransferenciaBensEstoque;

    private HierarquiaOrganizacional recuperaHierarquiaOrcamentaria(Date data, UnidadeOrganizacional und) {
        try {
            Query q = em.createQuery("SELECT obj FROM HierarquiaOrganizacional obj where obj.subordinada=:und and obj.tipoHierarquiaOrganizacional=:tipo  and :data BETWEEN obj.inicioVigencia AND COALESCE(OBJ.fimVigencia,:data) ");
            q.setParameter("und", und);
            q.setParameter("data", data);
            q.setParameter("tipo", TipoHierarquiaOrganizacional.ORCAMENTARIA);

            return (HierarquiaOrganizacional) q.getSingleResult();
        } catch (NonUniqueResultException e) {
            throw new ExcecaoNegocioGenerica("Existe mais de uma hierarquia orçamentária correspondente a unidade " + und.getDescricao() + " na data " + DataUtil.getDataFormatada(data) + ". Entre em contato com o suporte!");
        } catch (NoResultException e) {
            throw new ExcecaoNegocioGenerica("Não existe uma hierarquia orçamentaria correspondente a unidade " + und.getDescricao() + " na data " + DataUtil.getDataFormatada(data) + ". Entre em contato com o suporte!");
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica("Erro ao validar a hierarquia vigente para unidade " + und.getDescricao() + " na data " + DataUtil.getDataFormatada(data) + ". Entre em contato com o suporte! " + e.getMessage());
        }
    }

    private String geraPrimeiroCoigo(Date data, UnidadeOrganizacional und) {
        String toReturn;

        HierarquiaOrganizacional h = recuperaHierarquiaOrcamentaria(data, und);
        String cd[] = h.getCodigo().split("\\.");
        toReturn = cd[1].substring(1, 3) + cd[2] + "0001";

        if (toReturn.length() != 9) {
            throw new ExcecaoNegocioGenerica("Erro ao gerar o primeiro código para unidade " + und.getDescricao() + ". Código: " + toReturn);
        }
        return toReturn;
    }

    private void validaMaximoDeMovimentos(String s) {
        if (s.endsWith("9999")) {
            throw new ExcecaoNegocioGenerica("Erro a o gerar o número para o movimento. O número máximo de movimento foi atingido N° 9999");
        }
    }

    private String acrecentaFormataCodigo(String codigo) {
        int ultimoCod = Integer.parseInt(codigo);
        String cd = (ultimoCod + 1) + "";
        return cd.length() == 9 ? cd : "0" + cd;
    }

    private void validaParametros(Exercicio ex, UnidadeOrganizacional und, Date data, String movimento) {
        Preconditions.checkNotNull(ex, "Exercício esta vazio.");
        Preconditions.checkNotNull(und, "A unidade orçamentária esta vazia.");
        Preconditions.checkNotNull(data, "A data para verificar a vigencia da unidade esta vazia.");
    }

    @Lock(LockType.READ)
    public String getNumeroEmpenho(Exercicio ex, UnidadeOrganizacional und, Date date) {
        try {
            validaParametros(ex, und, date, "Empenho");
            String toReturn;
            Query q = em.createNativeQuery(" select max(numero) from (" +
                " SELECT max(cast(OBJ.numero as number)) as numero FROM Empenho obj " +
                " where OBJ.exercicio_id=:ex " +
                " and OBJ.unidadeOrganizacional_id=:und " +
                " having max(cast(OBJ.numero as number)) is not null" +
                " union all" +
                " SELECT max(cast(OBJ.numero as number)) as numero FROM Empenhoestorno obj " +
                " inner join empenho emp on obj.empenho_id = emp.id " +
                " where emp.exercicio_id=:ex " +
                " and obj.unidadeOrganizacional_id=:und " +
                " having max(cast(OBJ.numero as number)) is not null )");
            q.setParameter("ex", ex.getId());
            q.setParameter("und", und.getId());

            Object singleResult = q.getSingleResult();
            if (singleResult == null) {
                throw new NoResultException("Nenhum registro encontrado.");
            }
            toReturn = acrecentaFormataCodigo(singleResult.toString());
            validaMaximoDeMovimentos(toReturn);

            return toReturn;
        } catch (NonUniqueResultException e) {
            throw new ExcecaoNegocioGenerica(" Erro ao gerar o número do empenho para o exercício " + ex.getAno() + ", unidade " + und.getDescricao() + ". Entre em contato com o suporte!");
        } catch (NoResultException e) {
            return geraPrimeiroCoigo(date, und);
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica(" Erro ao gerar o número do empenho para o exercício " + ex.getAno() + ", unidade " + und.getDescricao() + "  " + e.getMessage() + ". Entre em contato com o suporte!");
        }
    }

    @Lock(LockType.READ)
    public String getNumeroLiquidacao(Exercicio ex, UnidadeOrganizacional und, Date date) {
        IdentificadorMovimentoContabil identificadorMovimentoContabil = criarIdentificadorMovimentoContabil(ex, und, date);
        try {
            if (proximoCodigoLiquidacao == null) {
                proximoCodigoLiquidacao = Maps.newHashMap();
            }
            if (proximoCodigoLiquidacao.get(identificadorMovimentoContabil) == null) {

                validaParametros(ex, und, date, "Liquidação");
                String toReturn;
                Query q = em.createNativeQuery(" SELECT max(numero) FROM ( " +
                    " SELECT max(cast(OBJ.numero as number)) as numero FROM liquidacao obj  " +
                    " where OBJ.exercicio_id=:ex " +
                    " and OBJ.unidadeOrganizacional_id=:und " +
                    " having max(cast(OBJ.numero as number)) is not null " +
                    " union all  " +
                    " SELECT max(cast(OBJ.numero as number)) as numero FROM liquidacaoestorno obj  " +
                    " inner join liquidacao liq on obj.liquidacao_id = liq.id " +
                    " where liq.exercicio_id=:ex " +
                    " and OBJ.unidadeOrganizacional_id=:und " +
                    " having max(cast(OBJ.numero as number)) is not null )");
                q.setParameter("ex", ex.getId());
                q.setParameter("und", und.getId());

                Object singleResult = q.getSingleResult();
                if (singleResult == null) {
                    throw new NoResultException("Nenhum registro encontrado.");
                }
                toReturn = acrecentaFormataCodigo(singleResult.toString());
                validaMaximoDeMovimentos(toReturn);
                proximoCodigoLiquidacao.put(identificadorMovimentoContabil, toReturn);
            } else {
                String codigo = acrecentaFormataCodigo(proximoCodigoLiquidacao.get(identificadorMovimentoContabil));
                proximoCodigoLiquidacao.put(identificadorMovimentoContabil, codigo);
            }
            return proximoCodigoLiquidacao.get(identificadorMovimentoContabil);
        } catch (NonUniqueResultException e) {
            throw new ExcecaoNegocioGenerica(" Erro ao gerar o número da liquidacao para o exercício " + ex.getAno() + ", unidade " + und.getDescricao() + ". Entre em contato com o suporte!");
        } catch (NoResultException e) {
            String codigo = geraPrimeiroCoigo(date, und);
            proximoCodigoLiquidacao.put(identificadorMovimentoContabil, codigo);
            return codigo;
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica(" Erro ao gerar o número da liquidacao para o exercício " + ex.getAno() + ", unidade " + und.getDescricao() + "  " + e.getMessage() + ". Entre em contato com o suporte!");
        }
    }

    @Lock(LockType.READ)
    public String getNumeroAtoPotencial(Exercicio ex, UnidadeOrganizacional und, Date date) {
        IdentificadorMovimentoContabil identificadorMovimentoContabil = criarIdentificadorMovimentoContabil(ex, und, date);
        try {
            if (proximoCodigoAtoPotencial == null) {
                proximoCodigoAtoPotencial = Maps.newHashMap();
            }
            if (proximoCodigoAtoPotencial.get(identificadorMovimentoContabil) == null) {

                validaParametros(ex, und, date, "Liquidação");
                String toReturn;
                Query q = em.createNativeQuery(" SELECT max(cast(OBJ.numero as number)) as numero FROM atoPotencial obj  " +
                    " where OBJ.exercicio_id = :ex " +
                    " and OBJ.unidadeOrganizacional_id = :und " +
                    " having max(cast(OBJ.numero as number)) is not null ");
                q.setParameter("ex", ex.getId());
                q.setParameter("und", und.getId());

                Object singleResult = q.getSingleResult();
                if (singleResult == null) {
                    throw new NoResultException("Nenhum registro encontrado.");
                }
                toReturn = acrecentaFormataCodigo(singleResult.toString());
                validaMaximoDeMovimentos(toReturn);
                proximoCodigoAtoPotencial.put(identificadorMovimentoContabil, toReturn);
            } else {
                String codigo = acrecentaFormataCodigo(proximoCodigoAtoPotencial.get(identificadorMovimentoContabil));
                proximoCodigoAtoPotencial.put(identificadorMovimentoContabil, codigo);
            }
            return proximoCodigoAtoPotencial.get(identificadorMovimentoContabil);
        } catch (NonUniqueResultException e) {
            throw new ExcecaoNegocioGenerica(" Erro ao gerar o número do ato potencial para o exercício " + ex.getAno() + ", unidade " + und.getDescricao() + ". Entre em contato com o suporte!");
        } catch (NoResultException e) {
            String codigo = geraPrimeiroCoigo(date, und);
            proximoCodigoAtoPotencial.put(identificadorMovimentoContabil, codigo);
            return codigo;
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica(" Erro ao gerar o número do ato potencial para o exercício " + ex.getAno() + ", unidade " + und.getDescricao() + "  " + e.getMessage() + ". Entre em contato com o suporte!");
        }
    }

    @Lock(LockType.READ)
    public String getNumeroPagamento(Exercicio ex, UnidadeOrganizacional und, Date date) {
        try {
            validaParametros(ex, und, date, "Pagamento");
            String toReturn;
            Query q = em.createNativeQuery("SELECT max(numero) FROM ( " +
                " SELECT max(cast(obj.numeropagamento as number)) as numero FROM Pagamento obj " +
                " where OBJ.exercicio_id=:ex" +
                " and OBJ.unidadeOrganizacional_id=:und" +
                " having max(cast(OBJ.numeropagamento as number)) is not null" +
                " union all " +
                " SELECT max(cast(obj.NUMERO as number)) as numero FROM Pagamentoestorno obj " +
                " inner join pagamento pag on obj.pagamento_id = pag.id " +
                " where pag.exercicio_id=:ex" +
                " and OBJ.unidadeOrganizacional_id=:und" +
                " having max(cast(OBJ.NUMERO as number)) is not null )");
            q.setParameter("ex", ex.getId());
            q.setParameter("und", und.getId());

            Object singleResult = q.getSingleResult();
            if (singleResult == null) {
                throw new NoResultException("Nenhum registro encontrado.");
            }
            toReturn = acrecentaFormataCodigo(singleResult.toString());
            validaMaximoDeMovimentos(toReturn);

            return toReturn;
        } catch (NonUniqueResultException e) {
            throw new ExcecaoNegocioGenerica(" Erro ao gerar o número do Pagamento para o exercício " + ex.getAno() + ", unidade " + und.getDescricao() + ". Entre em contato com o suporte!");
        } catch (NoResultException e) {
            return geraPrimeiroCoigo(date, und);
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica(" Erro ao gerar o número do Pagamento para o exercício " + ex.getAno() + ", unidade " + und.getDescricao() + "  " + e.getMessage() + ". Entre em contato com o suporte!");
        }
    }

    @Lock(LockType.READ)
    public String getNumeroPagamentoExtra(Exercicio ex, UnidadeOrganizacional und, Date date) {
        try {
            validaParametros(ex, und, date, "Despesa Extraorçamentária");
            String toReturn;
            String s = " select max(numero) from (" +
                " SELECT max(cast(OBJ.numero as number)) as numero FROM pagamentoextra obj " +
                " where OBJ.exercicio_id=:ex and OBJ.unidadeOrganizacional_id=:und " +
                " having max(cast(OBJ.numero as number)) is not null " +
                " union all " +
                " SELECT max(cast(OBJ.numero as number)) as numero FROM pagamentoextraestorno obj " +
                " where obj.exercicio_id=:ex and OBJ.unidadeOrganizacional_id=:und " +
                " having max(cast(OBJ.numero as number)) is not null )";
            Query q = em.createNativeQuery(s);
            q.setParameter("ex", ex.getId());
            q.setParameter("und", und.getId());

            Object singleResult = q.getSingleResult();
            if (singleResult == null) {
                throw new NoResultException("Nenhum registro encontrado.");
            }
            toReturn = acrecentaFormataCodigo(singleResult.toString());
            validaMaximoDeMovimentos(toReturn);

            return toReturn;
        } catch (NonUniqueResultException e) {
            throw new ExcecaoNegocioGenerica(" Erro ao gerar o número da Despesa Extraorçamentária  para o exercício " + ex.getAno() + ", unidade " + und.getDescricao() + ". Entre em contato com o suporte!");
        } catch (NoResultException e) {
            return geraPrimeiroCoigo(date, und);
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica(" Erro ao gerar o número da Despesa Extraorçamentária para o exercício " + ex.getAno() + ", unidade " + und.getDescricao() + "  " + e.getMessage() + ". Entre em contato com o suporte!");
        }
    }

    @Lock(LockType.READ)
    public String getNumeroTranferenciaContaFinanceira(Exercicio ex, UnidadeOrganizacional und, Date date) {

        try {
            validaParametros(ex, und, date, "Tranferência Financeira");
            String toReturn;
            String s = " SELECT max(numero) FROM ( " +
                " SELECT max(cast(OBJ.numero as number)) as numero FROM transferenciacontafinanc obj " +
                " where OBJ.exercicio_id=:ex and OBJ.UNIDADEORGCONCEDIDA_ID=:und " +
                " having max(cast(OBJ.numero as number)) is not null " +
                " union all " +
                " SELECT MAX(CAST(OBJ.numero AS NUMBER)) as numero " +
                " FROM estornotransferencia obj " +
                " inner join transferenciacontafinanc transf on obj.transferencia_id = transf.id " +
                " WHERE OBJ.exercicio_id                  =:ex " +
                " AND transf.UNIDADEORGCONCEDIDA_ID        =:und " +
                " HAVING MAX(CAST(OBJ.numero AS NUMBER)) IS NOT NULL ) ";
            Query q = em.createNativeQuery(s);
            q.setParameter("ex", ex.getId());
            q.setParameter("und", und.getId());

            Object singleResult = q.getSingleResult();
            if (singleResult == null) {
                throw new NoResultException("Nenhum registro encontrado.");
            }
            toReturn = acrecentaFormataCodigo(singleResult.toString());
            validaMaximoDeMovimentos(toReturn);

            return toReturn;
        } catch (NonUniqueResultException e) {
            throw new ExcecaoNegocioGenerica(" Erro ao gerar o número da Tranferência Financeira para o exercício " + ex.getAno() + ", unidade " + und.getDescricao() + ". Entre em contato com o suporte!");
        } catch (NoResultException e) {
            return geraPrimeiroCoigo(date, und);
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica(" Erro ao gerar o número da Tranferência Financeira para o exercício " + ex.getAno() + ", unidade " + und.getDescricao() + "  " + e.getMessage() + ". Entre em contato com o suporte!");
        }
    }

    @Lock(LockType.READ)
    public String getNumeroTranferenciaMesmaUnidade(Exercicio ex, UnidadeOrganizacional und, Date date) {
        try {
            validaParametros(ex, und, date, "Transferência Financeira Mesma Unidade");
            String toReturn;
            Query q = em.createNativeQuery(" select max(numero) from (" +
                " SELECT max(cast(OBJ.numero as number)) as numero FROM transferenciamesmaunidade obj " +
                " where OBJ.exercicio_id=:ex and OBJ.unidadeOrganizacional_id=:und " +
                " having max(cast(OBJ.numero as number)) is not null " +
                " union all " +
                " SELECT max(cast(OBJ.numero as number)) as numero FROM estornotransfmesmaunidade obj " +
                " inner join transferenciamesmaunidade t on t.id= obj.transferenciamesmaunidade_id " +
                " where t.exercicio_id=:ex and OBJ.unidadeOrganizacional_id=:und " +
                " having max(cast(OBJ.numero as number)) is not null )");
            q.setParameter("ex", ex.getId());
            q.setParameter("und", und.getId());
            Object singleResult = q.getSingleResult();
            if (singleResult == null) {
                throw new NoResultException("Nenhum registro encontrado.");
            }
            toReturn = acrecentaFormataCodigo(singleResult.toString());
            validaMaximoDeMovimentos(toReturn);
            return toReturn;
        } catch (NonUniqueResultException e) {
            throw new ExcecaoNegocioGenerica(" Erro ao gerar da Transferência Financeira Mesma Unidade para o exercício " + ex.getAno() + ", unidade " + und.getDescricao() + ". Entre em contato com o suporte!");
        } catch (NoResultException e) {
            return geraPrimeiroCoigo(date, und);
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica(" Erro ao gerar o número da Transferência Financeira Mesma Unidade para o exercício " + ex.getAno() + ", unidade " + und.getDescricao() + "  " + e.getMessage() + ". Entre em contato com o suporte!");
        }
    }

    @Lock(LockType.READ)
    public String getNumeroSolicitacaoFinanciera(Exercicio ex, UnidadeOrganizacional und, Date date) {
        try {
            validaParametros(ex, und, date, "Solicitação Financeira");
            String toReturn;
            Query q = em.createNativeQuery("SELECT max(cast(OBJ.numero as number)) FROM SolicitacaoCotaFinanceira obj where OBJ.exercicio_id=:ex and OBJ.unidadeOrganizacional_id=:und having max(cast(OBJ.numero as number)) is not null");
            q.setParameter("ex", ex.getId());
            q.setParameter("und", und.getId());
            Object singleResult = q.getSingleResult();
            if (singleResult == null) {
                throw new NoResultException("Nenhum registro encontrado.");
            }
            toReturn = acrecentaFormataCodigo(singleResult.toString());
            validaMaximoDeMovimentos(toReturn);
            return toReturn;
        } catch (NonUniqueResultException e) {
            throw new ExcecaoNegocioGenerica(" Erro ao gerar o número da Solicitação Financeira para o exercício " + ex.getAno() + ", unidade " + und.getDescricao() + ". Entre em contato com o suporte!");
        } catch (NoResultException e) {
            return geraPrimeiroCoigo(date, und);
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica(" Erro ao gerar o número da Solicitação Financeira para o exercício " + ex.getAno() + ", unidade " + und.getDescricao() + "  " + e.getMessage() + ". Entre em contato com o suporte!");
        }
    }

    @Lock(LockType.READ)
    public String getNumeroLiberacaoFinanciera(Exercicio ex, UnidadeOrganizacional und, Date date) {
        try {
            validaParametros(ex, und, date, "Liberação Financeira");
            String toReturn;
            Query q = em.createNativeQuery(" select max(numero) from (" +
                " SELECT max(cast(OBJ.numero as number)) as numero FROM liberacaoCotaFinanceira obj " +
                " where OBJ.exercicio_id=:ex and OBJ.unidadeOrganizacional_id=:und " +
                " having max(cast(OBJ.numero as number)) is not null " +
                " union all " +
                " SELECT max(cast(OBJ.numero as number)) as numero FROM estornoLibCotaFinanceira obj " +
                " INNER JOIN liberacaoCotaFinanceira lib on OBJ.liberacao_id = lib.id " +
                " WHERE LIB.exercicio_id=:ex and OBJ.unidadeOrganizacional_id=:und " +
                " having max(cast(OBJ.numero as number)) is not null )");
            q.setParameter("ex", ex.getId());
            q.setParameter("und", und.getId());
            Object singleResult = q.getSingleResult();
            if (singleResult == null) {
                throw new NoResultException("Nenhum registro encontrado.");
            }
            toReturn = acrecentaFormataCodigo(singleResult.toString());
            validaMaximoDeMovimentos(toReturn);
            return toReturn;
        } catch (NonUniqueResultException e) {
            throw new ExcecaoNegocioGenerica(" Erro ao gerar o número da Liberação Financeira para o exercício " + ex.getAno() + ", unidade " + und.getDescricao() + ". Entre em contato com o suporte!");
        } catch (NoResultException e) {
            return geraPrimeiroCoigo(date, und);
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica(" Erro ao gerar o número da Liberação Financeira para o exercício " + ex.getAno() + ", unidade " + und.getDescricao() + "  " + e.getMessage() + ". Entre em contato com o suporte!");
        }
    }

    @Lock(LockType.READ)
    public String getNumeroReceitaExtra(Exercicio exercicio, UnidadeOrganizacional und, Date date) {
        IdentificadorMovimentoContabil identificadorMovimentoContabil = criarIdentificadorMovimentoContabil(exercicio, und, date);
        try {
            if (proximoNumeroReceitaExtra == null) {
                proximoNumeroReceitaExtra = Maps.newHashMap();
            }

            if (proximoNumeroReceitaExtra.get(identificadorMovimentoContabil) == null) {
                validaParametros(exercicio, und, date, "Receita Extraorçamentária");
                String toReturn;
                String sql = " select max(numero) from ( " +
                    " SELECT max(cast(OBJ.numero as number)) as numero FROM receitaExtra obj " +
                    " WHERE OBJ.exercicio_id=:ex and OBJ.unidadeOrganizacional_id=:und " +
                    " having max(cast(OBJ.numero as number)) is not null " +
                    " union all " +
                    " SELECT max(cast(OBJ.numero as number)) as numero FROM  receitaExtraEstorno obj " +
                    " WHERE OBJ.exercicio_id=:ex and OBJ.unidadeOrganizacional_id=:und " +
                    " having max(cast(OBJ.numero as number)) is not null )";
                Query q = em.createNativeQuery(sql);
                q.setParameter("und", und.getId());
                q.setParameter("ex", exercicio.getId());

                Object singleResult = q.getSingleResult();
                if (singleResult == null) {
                    throw new NoResultException("Nenhum registro encontrado.");
                }
                toReturn = acrecentaFormataCodigo(singleResult.toString());
                validaMaximoDeMovimentos(toReturn);
                proximoNumeroReceitaExtra.put(identificadorMovimentoContabil, toReturn);
            } else {
                String codigo = acrecentaFormataCodigo(proximoNumeroReceitaExtra.get(identificadorMovimentoContabil));
                proximoNumeroReceitaExtra.put(identificadorMovimentoContabil, codigo);
            }
            return proximoNumeroReceitaExtra.get(identificadorMovimentoContabil);
        } catch (NonUniqueResultException e) {
            throw new ExcecaoNegocioGenerica(" Erro ao gerar o número da Receita Extraorçamentária para o exercício " + exercicio.getAno() + ", unidade " + und.getDescricao() + ". Entre em contato com o suporte!");
        } catch (NoResultException e) {
            String codigo = geraPrimeiroCoigo(date, und);
            proximoNumeroReceitaExtra.put(identificadorMovimentoContabil, codigo);
            return codigo;
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica(" Erro ao gerar o número da Receita Extraorçamentária para o exercício " + exercicio.getAno() + ", unidade " + und.getDescricao() + "  " + e.getMessage() + ". Entre em contato com o suporte!");
        }
    }

    @Lock(LockType.READ)
    public String getNumeroDiariaCivil(Exercicio ex, UnidadeOrganizacional und, Date date) {
        try {
            validaParametros(ex, und, date, "Diária Civil");
            String toReturn;
            String s = "SELECT max(cast(OBJ.codigo as number)) FROM PropostaConcessaoDiaria obj where OBJ.exercicio_id=:ex and OBJ.unidadeOrganizacional_id=:und and OBJ.tipoProposta = 'CONCESSAO_DIARIA' and obj.codigo > 0 having max(cast(OBJ.codigo as number)) is not null";
            Query q = em.createNativeQuery(s);
            q.setParameter("ex", ex.getId());
            q.setParameter("und", und.getId());

            Object singleResult = q.getSingleResult();
            if (singleResult == null) {
                throw new NoResultException("Nenhum registro encontrado.");
            }
            toReturn = acrecentaFormataCodigo(singleResult.toString());
            validaMaximoDeMovimentos(toReturn);

            return toReturn;
        } catch (NonUniqueResultException e) {
            throw new ExcecaoNegocioGenerica(" Erro ao gerar o número da Diária Civil para o exercício " + ex.getAno() + ", unidade " + und.getDescricao() + ". Entre em contato com o suporte!");
        } catch (NoResultException e) {
            return geraPrimeiroCoigo(date, und);
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica(" Erro ao gerar o número da Diária Civil para o exercício " + ex.getAno() + ", unidade " + und.getDescricao() + "  " + e.getMessage() + ". Entre em contato com o suporte!");
        }
    }

    @Lock(LockType.READ)
    public String getNumeroDiariaColaboradorEventual(Exercicio ex, UnidadeOrganizacional und, Date date) {
        try {
            validaParametros(ex, und, date, "Diária Colaborador Eventual");
            String toReturn;
            String s = "SELECT max(cast(OBJ.codigo as number)) FROM PropostaConcessaoDiaria obj where OBJ.exercicio_id=:ex and OBJ.unidadeOrganizacional_id=:und and OBJ.tipoProposta = 'COLABORADOR_EVENTUAL' and obj.codigo > 0 having max(cast(OBJ.codigo as number)) is not null";
            Query q = em.createNativeQuery(s);
            q.setParameter("ex", ex.getId());
            q.setParameter("und", und.getId());

            Object singleResult = q.getSingleResult();
            if (singleResult == null) {
                throw new NoResultException("Nenhum registro encontrado.");
            }
            toReturn = acrecentaFormataCodigo(singleResult.toString());
            validaMaximoDeMovimentos(toReturn);

            return toReturn;
        } catch (NonUniqueResultException e) {
            throw new ExcecaoNegocioGenerica(" Erro ao gerar o número da Diária Colaborador Eventual para o exercício " + ex.getAno() + ", unidade " + und.getDescricao() + ". Entre em contato com o suporte!");
        } catch (NoResultException e) {
            return geraPrimeiroCoigo(date, und);
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica(" Erro ao gerar o número da Diária Colaborador Eventual para o exercício " + ex.getAno() + ", unidade " + und.getDescricao() + "  " + e.getMessage() + ". Entre em contato com o suporte!");
        }
    }

    @Lock(LockType.READ)
    public String getNumeroDiariaDeCampo(Exercicio ex, UnidadeOrganizacional und, Date date) {
        try {
            validaParametros(ex, und, date, "Diária de Campo");
            String toReturn;
            String s = "SELECT max(cast(OBJ.codigo as number)) FROM PropostaConcessaoDiaria obj where OBJ.exercicio_id=:ex and OBJ.unidadeOrganizacional_id=:und and OBJ.tipoProposta = 'CONCESSAO_DIARIACAMPO' and obj.codigo > 0 having max(cast(OBJ.codigo as number)) is not null";
            Query q = em.createNativeQuery(s);
            q.setParameter("ex", ex.getId());
            q.setParameter("und", und.getId());

            Object singleResult = q.getSingleResult();
            if (singleResult == null) {
                throw new NoResultException("Nenhum registro encontrado.");
            }
            toReturn = acrecentaFormataCodigo(singleResult.toString());
            validaMaximoDeMovimentos(toReturn);

            return toReturn;
        } catch (NonUniqueResultException e) {
            throw new ExcecaoNegocioGenerica(" Erro ao gerar o número da Diária de Campo para o exercício " + ex.getAno() + ", unidade " + und.getDescricao() + ". Entre em contato com o suporte!");
        } catch (NoResultException e) {
            return geraPrimeiroCoigo(date, und);
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica(" Erro ao gerar o número da Diária de Campo para o exercício " + ex.getAno() + ", unidade " + und.getDescricao() + "  " + e.getMessage() + ". Entre em contato com o suporte!");
        }
    }

    @Lock(LockType.READ)
    public String getNumeroSuprimentoDeFundo(Exercicio ex, UnidadeOrganizacional und, Date date) {
        try {
            validaParametros(ex, und, date, "Suprimento de Fundo");
            String toReturn;
            Query q = em.createNativeQuery("SELECT max(cast(OBJ.codigo as number)) FROM PropostaConcessaoDiaria obj where OBJ.exercicio_id=:ex and OBJ.unidadeOrganizacional_id=:und and OBJ.tipoProposta = 'SUPRIMENTO_FUNDO' and obj.codigo > 0 having max(cast(OBJ.codigo as number)) is not null");
            q.setParameter("ex", ex.getId());
            q.setParameter("und", und.getId());

            Object singleResult = q.getSingleResult();
            if (singleResult == null) {
                throw new NoResultException("Nenhum registro encontrado.");
            }
            toReturn = acrecentaFormataCodigo(singleResult.toString());
            validaMaximoDeMovimentos(toReturn);

            return toReturn;
        } catch (NonUniqueResultException e) {
            throw new ExcecaoNegocioGenerica(" Erro ao gerar o número do Suprimento de Fundo para o exercício " + ex.getAno() + ", unidade " + und.getDescricao() + ". Entre em contato com o suporte!");
        } catch (NoResultException e) {
            return geraPrimeiroCoigo(date, und);
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica(" Erro ao gerar o número do Suprimento de Fundo para o exercício " + ex.getAno() + ", unidade " + und.getDescricao() + "  " + e.getMessage() + ". Entre em contato com o suporte!");
        }
    }

    @Lock(LockType.READ)
    public String getNumeroReceitaRealizada(Exercicio exercicio, Date data) {
        try {
            Preconditions.checkNotNull(exercicio, "Exercício está vazio.");
            Preconditions.checkNotNull(data, "A data para verificar a vigencia da unidade esta vazia.");
            if (proximoCodigoReceitaRealizada == null) {
                proximoCodigoReceitaRealizada = Maps.newHashMap();
            }
            if (proximoCodigoReceitaRealizada.get(exercicio) == null) {

                Query q = em.createNativeQuery(" select max(numero) from (" +
                    " SELECT MAX(CAST(OBJ.numero AS NUMBER)) as numero FROM lancamentoreceitaorc obj " +
                    " WHERE OBJ.exercicio_id = :ex " +
                    " HAVING MAX(CAST(OBJ.numero AS NUMBER)) IS NOT NULL " +
                    " union all " +
                    " SELECT MAX(CAST(OBJ.numero AS NUMBER)) as numero FROM receitaorcestorno obj " +
                    " WHERE OBJ.exercicio_id = :ex " +
                    " HAVING MAX(CAST(OBJ.numero AS NUMBER)) IS NOT NULL )");
                q.setParameter("ex", exercicio.getId());

                if (q.getResultList().isEmpty()
                    || q.getSingleResult() == null) {
                    throw new NoResultException("Nenhum registro encontrado.");
                }
                proximoCodigoReceitaRealizada.put(exercicio, ((BigDecimal) q.getResultList().get(0)).longValue());
            }
            proximoCodigoReceitaRealizada.put(exercicio, proximoCodigoReceitaRealizada.get(exercicio) + 1);
            return acrecentaFormataCodigo(proximoCodigoReceitaRealizada.get(exercicio).toString());
        } catch (NonUniqueResultException e) {
            throw new ExcecaoNegocioGenerica(" Erro ao gerar o número da Receita Realizada o para o exercício " + exercicio.getAno() + ". Entre em contato com o suporte!");
        } catch (NoResultException e) {
            return "01";
        } catch (Exception e) {
            e.printStackTrace();
            throw new ExcecaoNegocioGenerica(" Erro ao gerar o número da Receita Realizada para o exercício " + exercicio.getAno() + "  " + e.getMessage() + ". Entre em contato com o suporte!");
        }
    }

    @Lock(LockType.READ)
    public String getNumeroCreditoReceber(Exercicio ex, Date date) {
        try {
            Preconditions.checkNotNull(ex, "Exercício está vazio.");
            Preconditions.checkNotNull(date, "A data para verificar a vigencia da unidade esta vazia.");
            String toReturn;
            String s = "SELECT max(cast(OBJ.numero as number)) FROM CREDITORECEBER obj where OBJ.exercicio_id=:ex having max(cast(OBJ.numero as number)) is not null";
            Query q = em.createNativeQuery(s);
            q.setParameter("ex", ex.getId());
            Object singleResult = q.getSingleResult();
            if (singleResult == null) {
                throw new NoResultException("Nenhum registro encontrado.");
            }
            toReturn = acrecentaFormataCodigo(singleResult.toString());
            return toReturn;

        } catch (NonUniqueResultException e) {
            throw new ExcecaoNegocioGenerica(" Erro ao gerar o número de Crédito a Receber para o exercício " + ex.getAno() + ". Entre em contato com o suporte!");
        } catch (NoResultException e) {
            return "01";
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica(" Erro ao gerar o número de Crédito a Receber para o exercício " + ex.getAno() + "  " + e.getMessage() + ". Entre em contato com o suporte!");
        }
    }

    @Lock(LockType.READ)
    public String getNumeroDividaAtivaContabil(Exercicio ex, Date date) {
        try {
            Preconditions.checkNotNull(ex, "Exercício está vazio.");
            Preconditions.checkNotNull(date, "A data para verificar a vigencia da unidade esta vazia.");
            String toReturn;
            String s = "SELECT max(cast(OBJ.numero as number)) FROM DIVIDAATIVACONTABIL obj where OBJ.exercicio_id=:ex having max(cast(OBJ.numero as number)) is not null";
            Query q = em.createNativeQuery(s);
            q.setParameter("ex", ex.getId());
            Object singleResult = q.getSingleResult();
            if (singleResult == null) {
                throw new NoResultException("Nenhum registro encontrado.");
            }
            toReturn = acrecentaFormataCodigo(singleResult.toString());
            return toReturn;

        } catch (NonUniqueResultException e) {
            throw new ExcecaoNegocioGenerica(" Erro ao gerar o número da Dívida Ativa Contábil para o exercício " + ex.getAno() + ". Entre em contato com o suporte!");
        } catch (NoResultException e) {
            return "01";
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica(" Erro ao gerar o número da Dívida Ativa Contábil para o exercício " + ex.getAno() + "  " + e.getMessage() + ". Entre em contato com o suporte!");
        }
    }

    @Lock(LockType.READ)
    public String getNumeroBordero(Exercicio ex, UnidadeOrganizacional und, Date date) {
        try {
            validaParametros(ex, und, date, "Ordem Bancária");
            String toReturn;
            String s = "SELECT max(cast(OBJ.sequenciaArquivo as number)) FROM BORDERO obj " +
                " where OBJ.exercicio_id = :ex " +
                " and OBJ.unidadeOrganizacional_id = :und " +
                " having max(cast(OBJ.sequenciaArquivo as number)) is not null ";
            Query q = em.createNativeQuery(s);
            q.setParameter("ex", ex.getId());
            q.setParameter("und", und.getId());

            Object singleResult = q.getSingleResult();
            if (singleResult == null) {
                throw new NoResultException("Nenhum registro encontrado.");
            }
            toReturn = acrecentaFormataCodigo(singleResult.toString());
            validaMaximoDeMovimentos(toReturn);
            return toReturn;

        } catch (NonUniqueResultException e) {
            throw new ExcecaoNegocioGenerica(" Erro ao gerar o número da Ordem Bancária para o exercício " + ex.getAno() + ", unidade " + und.getDescricao() + ". Entre em contato com o suporte!");
        } catch (NoResultException e) {
            return geraPrimeiroCoigo(date, und);
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica(" Erro ao gerar o número da Ordem Bancária para o exercício " + ex.getAno() + ", unidade " + und.getDescricao() + "  " + e.getMessage() + ". Entre em contato com o suporte!");
        }
    }

    @Lock(LockType.READ)
    public String getNumeroOrdemBancaria(Exercicio ex, UnidadeOrganizacional und, Date data) {

        IdentificadorMovimentoContabil identificadorMovimentoContabil = criarIdentificadorMovimentoContabil(ex, und, data);

        try {
            validaParametros(ex, und, data, "Ordem Bancária");
            if (proximoNumeroOrdemBancaria == null) {
                proximoNumeroOrdemBancaria = Maps.newHashMap();
            }
            if (proximoNumeroOrdemBancaria.get(identificadorMovimentoContabil) == null) {

                String sql = " select max(numero) from ( " +
                    " SELECT max(cast(OBJ.sequenciaArquivo as number)) as numero " +
                    " FROM BORDERO obj " +
                    "  where OBJ.exercicio_id = :ex " +
                    "  and OBJ.unidadeOrganizacional_id = :und " +
                    "  having max(cast(OBJ.sequenciaArquivo as number)) is not null " +
                    "  " +
                    " union all " +
                    " SELECT coalesce(max(cast(obj.numerore as number)),0) as numero " +
                    "  FROM pagamento obj " +
                    "  where obj.exercicio_id = :ex" +
                    "  and OBJ.unidadeOrganizacional_id = :und" +
                    "  having max(cast(OBJ.numerore as number)) is not null " +
                    " " +
                    " union all " +
                    " SELECT coalesce(max(cast(OBJ.numerore as number)),0) as numero " +
                    "  FROM pagamentoextra obj " +
                    "  where OBJ.exercicio_id = :ex " +
                    "  and OBJ.unidadeOrganizacional_id = :und " +
                    "  having max(cast(OBJ.numerore as number)) is not null ) ";

                Query q = em.createNativeQuery(sql);
                q.setParameter("und", und.getId());
                q.setParameter("ex", ex.getId());
                if (q.getResultList().isEmpty()
                    || q.getSingleResult() == null) {
                    throw new NoResultException("Nenhum registro encontrado.");
                }
                proximoNumeroOrdemBancaria.put(identificadorMovimentoContabil, ((BigDecimal) q.getResultList().get(0)).longValue());
            } else {
                proximoNumeroOrdemBancaria.put(identificadorMovimentoContabil, proximoNumeroOrdemBancaria.get(identificadorMovimentoContabil) + 1);
            }
            return acrecentaFormataCodigo(proximoNumeroOrdemBancaria.get(identificadorMovimentoContabil).toString());
        } catch (NonUniqueResultException e) {
            throw new ExcecaoNegocioGenerica(" Erro ao gerar o número da Ordem Bancária para o exercício " + ex.getAno() + ", unidade " + und.getDescricao() + ". Entre em contato com o suporte!");
        } catch (NoResultException e) {
            return geraPrimeiroCoigo(data, und);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ExcecaoNegocioGenerica(" Erro ao gerar o número da Ordem Bancária para o exercício " + ex.getAno() + ", unidade " + und.getDescricao() + "  " + e.getMessage() + ". Entre em contato com o suporte!");
        }
    }

    @Lock(LockType.READ)
    public String getNumeroBensMoveis(Exercicio exercicio) {
        try {
            if (proximoCodigoBensMoveis == null) {
                proximoCodigoBensMoveis = Maps.newHashMap();
            }
            if (proximoCodigoBensMoveis.get(exercicio) == null) {
                String s = "SELECT max(cast(obj.numero as number)) FROM bensmoveis obj " +
                    " where obj.exercicio_id = :ex " +
                    " having max(cast(obj.numero as number)) is not null ";
                Query q = em.createNativeQuery(s);
                q.setParameter("ex", exercicio.getId());

                Object singleResult = q.getSingleResult();
                if (singleResult == null) {
                    throw new NoResultException("Nenhum registro encontrado.");
                }
                proximoCodigoBensMoveis.put(exercicio, ((BigDecimal) q.getResultList().get(0)).longValue());
            }
            String retorno = acrecentaFormataCodigo(proximoCodigoBensMoveis.get(exercicio).toString());
            proximoCodigoBensMoveis.put(exercicio, proximoCodigoBensMoveis.get(exercicio) + 1);
            return retorno;
        } catch (NonUniqueResultException e) {
            throw new ExcecaoNegocioGenerica(" Erro ao gerar o número de Bens Móveis para o exercício " + exercicio.getAno() + ". Entre em contato com o suporte!");
        } catch (NoResultException e) {
            return "01";
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica(" Erro ao gerar o número de Bens Móveis para o exercício " + exercicio.getAno() + " " + e.getMessage() + ". Entre em contato com o suporte!");
        }
    }

    @Lock(LockType.READ)
    public String getNumeroResponsabilidade(Exercicio ex, UnidadeOrganizacional und, Date date) {
        IdentificadorMovimentoContabil identificadorMovimentoContabil = criarIdentificadorMovimentoContabil(ex, und, date);
        try {
            validaParametros(ex, und, date, "Responsabilidade por Valores, Títulos e Bens");
            String toReturn;
            if (proximoNumeroResponsabilidade == null) {
                proximoNumeroResponsabilidade = Maps.newHashMap();
            }
            if (proximoNumeroResponsabilidade.get(identificadorMovimentoContabil) == null) {
                String s = "SELECT max(cast(obj.numero as number)) FROM responsabilidadevtb obj " +
                    " where obj.exercicio_id = :ex " +
                    " and obj.unidadeOrganizacional_id = :und " +
                    " having max(cast(obj.numero as number)) is not null ";
                Query q = em.createNativeQuery(s);
                q.setParameter("ex", ex.getId());
                q.setParameter("und", und.getId());

                Object singleResult = q.getSingleResult();
                if (singleResult == null) {
                    throw new NoResultException("Nenhum registro encontrado.");
                }
                toReturn = acrecentaFormataCodigo(singleResult.toString());
                validaMaximoDeMovimentos(toReturn);
                proximoNumeroResponsabilidade.put(identificadorMovimentoContabil, toReturn);
            } else {
                String codigo = acrecentaFormataCodigo(proximoNumeroResponsabilidade.get(identificadorMovimentoContabil));
                proximoNumeroResponsabilidade.put(identificadorMovimentoContabil, codigo);
            }
            return proximoNumeroResponsabilidade.get(identificadorMovimentoContabil);
        } catch (NonUniqueResultException e) {
            throw new ExcecaoNegocioGenerica(" Erro ao gerar o número de Responsabilidade por Valores, Títulos e Bens para o exercício " + ex.getAno() + ", unidade " + und.getDescricao() + ". Entre em contato com o suporte!");
        } catch (NoResultException e) {
            String codigo = geraPrimeiroCoigo(date, und);
            proximoNumeroResponsabilidade.put(identificadorMovimentoContabil, codigo);
            return codigo;
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica(" Erro ao gerar o número de Responsabilidade por Valores, Títulos e Bens para o exercício " + ex.getAno() + ", unidade " + und.getDescricao() + "  " + e.getMessage() + ". Entre em contato com o suporte!");
        }
    }

    @Lock(LockType.READ)
    public String getNumeroBensImoveis(Exercicio exercicio) {
        try {
            if (proximoCodigoBensImoveis == null) {
                proximoCodigoBensImoveis = Maps.newHashMap();
            }
            if (proximoCodigoBensImoveis.get(exercicio) == null) {
                String s = "SELECT max(cast(obj.numero as number)) FROM bensimoveis obj " +
                    " where obj.exercicio_id = :ex " +
                    " having max(cast(obj.numero as number)) is not null ";
                Query q = em.createNativeQuery(s);
                q.setParameter("ex", exercicio.getId());

                Object singleResult = q.getSingleResult();
                if (singleResult == null) {
                    throw new NoResultException("Nenhum registro encontrado.");
                }
                proximoCodigoBensImoveis.put(exercicio, ((BigDecimal) q.getResultList().get(0)).longValue());
            }
            String retorno = acrecentaFormataCodigo(proximoCodigoBensImoveis.get(exercicio).toString());
            proximoCodigoBensImoveis.put(exercicio, proximoCodigoBensImoveis.get(exercicio) + 1);
            return retorno;
        } catch (NonUniqueResultException e) {
            throw new ExcecaoNegocioGenerica(" Erro ao gerar o número de Bens Imóveis para o exercício " + exercicio.getAno() + ". Entre em contato com o suporte!");
        } catch (NoResultException e) {
            return "01";
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica(" Erro ao gerar o número de Bens Imóveis para o exercício " + exercicio.getAno() + " " + e.getMessage() + ". Entre em contato com o suporte!");
        }
    }

    @Lock(LockType.READ)
    public String getNumeroBensIntagiveis(Exercicio exercicio) {
        try {
            String s = "SELECT max(cast(obj.numero as number)) FROM bensintangiveis obj " +
                " where obj.exercicio_id = :ex " +
                " having max(cast(obj.numero as number)) is not null ";
            Query q = em.createNativeQuery(s);
            q.setParameter("ex", exercicio.getId());

            Object singleResult = q.getSingleResult();
            if (singleResult == null) {
                throw new NoResultException("Nenhum registro encontrado.");
            }
            return acrecentaFormataCodigo(singleResult.toString());
        } catch (NonUniqueResultException e) {
            throw new ExcecaoNegocioGenerica(" Erro ao gerar o número de Bens Intangíveis para o exercício " + exercicio.getAno() + ". Entre em contato com o suporte!");
        } catch (NoResultException e) {
            return "01";
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica(" Erro ao gerar o número de Bens Intangíveis para o exercício " + exercicio.getAno() + " " + e.getMessage() + ". Entre em contato com o suporte!");
        }
    }


    @Lock(LockType.READ)
    public String getNumeroBensEstoque(Exercicio exercicio) {
        try {
            if (proximoNumeroTransferenciaBensEstoque == null) {
                proximoNumeroTransferenciaBensEstoque = Maps.newHashMap();
            }
            if (proximoNumeroTransferenciaBensEstoque.get(exercicio) == null) {
                String s = "SELECT max(cast(obj.numero as number)) FROM bensestoque obj " +
                    " where obj.exercicio_id = :ex " +
                    " having max(cast(obj.numero as number)) is not null ";
                Query q = em.createNativeQuery(s);
                q.setParameter("ex", exercicio.getId());

                Object singleResult = q.getSingleResult();
                if (singleResult == null) {
                    throw new NoResultException("Nenhum registro encontrado.");
                }
                proximoNumeroTransferenciaBensEstoque.put(exercicio, ((BigDecimal) q.getResultList().get(0)).longValue());
            }
            String retorno = acrecentaFormataCodigo(proximoNumeroTransferenciaBensEstoque.get(exercicio).toString());
            proximoNumeroTransferenciaBensEstoque.put(exercicio, proximoNumeroTransferenciaBensEstoque.get(exercicio) + 1);
            return retorno;
        } catch (NonUniqueResultException e) {
            throw new ExcecaoNegocioGenerica(" Erro ao gerar o número de Bens de Estoque para o exercício " + exercicio.getAno() + ". Entre em contato com o suporte!");
        } catch (NoResultException e) {
            return "01";
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica(" Erro ao gerar o número de Bens de Estoque para o exercício " + exercicio.getAno() + " " + e.getMessage() + ". Entre em contato com o suporte!");
        }
    }

    @Lock(LockType.READ)
    public String getNumeroTransferenciaBensMoveis(Exercicio exercicio) {
        try {
            if (proximoNumeroTransferenciaBensMoveis == null) {
                proximoNumeroTransferenciaBensMoveis = Maps.newHashMap();
            }
            if (proximoNumeroTransferenciaBensMoveis.get(exercicio) == null) {
                String s = "SELECT max(cast(obj.numero as number)) FROM transfbensmoveis obj " +
                    " where obj.exercicio_id = :ex " +
                    " having max(cast(obj.numero as number)) is not null ";
                Query q = em.createNativeQuery(s);
                q.setParameter("ex", exercicio.getId());

                Object singleResult = q.getSingleResult();
                if (singleResult == null) {
                    throw new NoResultException("Nenhum registro encontrado.");
                }
                proximoNumeroTransferenciaBensMoveis.put(exercicio, ((BigDecimal) q.getResultList().get(0)).longValue());
            }
            String retorno = acrecentaFormataCodigo(proximoNumeroTransferenciaBensMoveis.get(exercicio).toString());
            proximoNumeroTransferenciaBensMoveis.put(exercicio, proximoNumeroTransferenciaBensMoveis.get(exercicio) + 1);
            return retorno;
        } catch (NonUniqueResultException e) {
            throw new ExcecaoNegocioGenerica(" Erro ao gerar o número da Transferência de Bens Móveis para o exercício " + exercicio.getAno() + ". Entre em contato com o suporte!");
        } catch (NoResultException e) {
            return "01";
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica(" Erro ao gerar o número da Transferência de Bens Móveis para o exercício " + exercicio.getAno() + " " + e.getMessage() + ". Entre em contato com o suporte!");
        }
    }

    @Lock(LockType.READ)
    public String getNumeroTransferenciaBensImoveis(Exercicio exercicio) {
        try {
            String s = "SELECT max(cast(obj.numero as number)) FROM transfbensimoveis obj " +
                " where obj.exercicio_id = :ex " +
                " having max(cast(obj.numero as number)) is not null ";
            Query q = em.createNativeQuery(s);
            q.setParameter("ex", exercicio.getId());

            Object singleResult = q.getSingleResult();
            if (singleResult == null) {
                throw new NoResultException("Nenhum registro encontrado.");
            }
            return acrecentaFormataCodigo(singleResult.toString());
        } catch (NonUniqueResultException e) {
            throw new ExcecaoNegocioGenerica(" Erro ao gerar o número da Transferência de Bens Imóveis para o exercício " + exercicio.getAno() + ". Entre em contato com o suporte!");
        } catch (NoResultException e) {
            return "01";
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica(" Erro ao gerar o número da Transferência de Bens Imóveis para o exercício " + exercicio.getAno() + " " + e.getMessage() + ". Entre em contato com o suporte!");
        }
    }

    @Lock(LockType.READ)
    public String getNumeroTransferenciaBensIntangiveis(Exercicio exercicio) {
        try {
            String s = "SELECT max(cast(obj.numero as number)) FROM transfbensintangiveis obj " +
                " where obj.exercicio_id = :ex " +
                " having max(cast(obj.numero as number)) is not null ";
            Query q = em.createNativeQuery(s);
            q.setParameter("ex", exercicio.getId());

            Object singleResult = q.getSingleResult();
            if (singleResult == null) {
                throw new NoResultException("Nenhum registro encontrado.");
            }
            return acrecentaFormataCodigo(singleResult.toString());
        } catch (NonUniqueResultException e) {
            throw new ExcecaoNegocioGenerica(" Erro ao gerar o número da Transferência de Bens Intangíveis para o exercício " + exercicio.getAno() + ". Entre em contato com o suporte!");
        } catch (NoResultException e) {
            return "01";
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica(" Erro ao gerar o número da Transferência de Bens Intangíveis para o exercício " + exercicio.getAno() + " " + e.getMessage() + ". Entre em contato com o suporte!");
        }
    }

    @Lock(LockType.READ)
    public String getNumeroTransferenciaBensEstoque(Exercicio exercicio) {
        try {
            String s = "SELECT max(cast(obj.numero as number)) FROM transfbensestoque obj " +
                " where obj.exercicio_id = :ex " +
                " having max(cast(obj.numero as number)) is not null ";
            Query q = em.createNativeQuery(s);
            q.setParameter("ex", exercicio.getId());

            Object singleResult = q.getSingleResult();
            if (singleResult == null) {
                throw new NoResultException("Nenhum registro encontrado.");
            }
            return acrecentaFormataCodigo(singleResult.toString());
        } catch (NonUniqueResultException e) {
            throw new ExcecaoNegocioGenerica(" Erro ao gerar o número da Transferência de Bens Estoque para o exercício " + exercicio.getAno() + ". Entre em contato com o suporte!");
        } catch (NoResultException e) {
            return "01";
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica(" Erro ao gerar o número da Transferência de Bens Estoque para o exercício " + exercicio.getAno() + " " + e.getMessage() + ". Entre em contato com o suporte!");
        }
    }

    @Lock(LockType.READ)
    public String getNumeroAjusteDeposito(UnidadeOrganizacional und, Date date) {
        try {
            String toReturn;
            Query q = em.createNativeQuery(" select max(numero) from (" +
                " SELECT max(cast(OBJ.numero as number)) as numero FROM AjusteDeposito obj " +
                " where OBJ.unidadeOrganizacional_id = :und " +
                " having max(cast(OBJ.numero as number)) is not null" +
                " union all" +
                " SELECT max(cast(OBJ.numero as number)) as numero FROM AjusteDepositoEstorno obj " +
                " inner join AjusteDeposito aj on obj.ajustedeposito_id = aj.id " +
                " and obj.unidadeOrganizacional_id = :und " +
                " having max(cast(OBJ.numero as number)) is not null )");
            q.setParameter("und", und.getId());
            Object singleResult = q.getSingleResult();
            if (singleResult == null) {
                throw new NoResultException("Nenhum registro encontrado.");
            }
            toReturn = acrecentaFormataCodigo(singleResult.toString());
            validaMaximoDeMovimentos(toReturn);

            return toReturn;
        } catch (NonUniqueResultException e) {
            throw new ExcecaoNegocioGenerica(" Erro ao gerar o número do ajuste em depósito para a unidade " + und.getDescricao() + ". Entre em contato com o suporte!");
        } catch (NoResultException e) {
            return geraPrimeiroCoigo(date, und);
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica(" Erro ao gerar o número do ajuste em depósito para a unidade " + und.getDescricao() + "  " + e.getMessage() + ". Entre em contato com o suporte!");
        }
    }

    @Lock(LockType.READ)
    public String getNumeroObrigacaoAPagar(Exercicio ex, UnidadeOrganizacional und, Date date) {
        IdentificadorMovimentoContabil identificadorMovimentoContabil = criarIdentificadorMovimentoContabil(ex, und, date);
        try {
            if (proximoNumeroObrigacaoPagar == null) {
                proximoNumeroObrigacaoPagar = Maps.newHashMap();
            }
            if (proximoNumeroObrigacaoPagar.get(identificadorMovimentoContabil) == null) {
                validaParametros(ex, und, date, "Obrigação a Pagar");
                String toReturn;
                String sql =
                    "  SELECT max(numero) FROM (  " +
                        "SELECT max(cast(OBJ.numero as number)) as numero FROM obrigacaoapagar obj  " +
                        "  where OBJ.exercicio_id = :ex " +
                        "  and OBJ.unidadeOrganizacional_id = :und " +
                        "  having max(cast(OBJ.numero as number)) is not null " +
                        "  union all " +
                        " SELECT max(cast(est.numero as number)) as numero FROM ObrigacaoAPagarEstorno est " +
                        "  inner join obrigacaoAPagar op on est.obrigacaoapagar_id = op.id " +
                        "  where est.exercicio_id=:ex " +
                        "  and est.unidadeOrganizacional_id=:und " +
                        "  having max(cast(est.numero as number)) is not null) ";
                Query q = em.createNativeQuery(sql);
                q.setParameter("ex", ex.getId());
                q.setParameter("und", und.getId());

                Object singleResult = q.getSingleResult();
                if (singleResult == null) {
                    throw new NoResultException("Nenhum registro encontrado.");
                }
                toReturn = acrecentaFormataCodigo(singleResult.toString());
                validaMaximoDeMovimentos(toReturn);
                proximoNumeroObrigacaoPagar.put(identificadorMovimentoContabil, toReturn);
            } else {
                String codigo = acrecentaFormataCodigo(proximoNumeroObrigacaoPagar.get(identificadorMovimentoContabil));
                proximoNumeroObrigacaoPagar.put(identificadorMovimentoContabil, codigo);
            }
            return proximoNumeroObrigacaoPagar.get(identificadorMovimentoContabil);
        } catch (NonUniqueResultException e) {
            throw new ExcecaoNegocioGenerica(" Erro ao gerar o número da obrigação a pagar para o exercício " + ex.getAno() + ", unidade " + und.getDescricao() + ". Entre em contato com o suporte!");
        } catch (NoResultException e) {
            String codigo = geraPrimeiroCoigo(date, und);
            proximoNumeroObrigacaoPagar.put(identificadorMovimentoContabil, codigo);
            return codigo;
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica(" Erro ao gerar o número da obrigação a pagar para o exercício " + ex.getAno() + ", unidade " + und.getDescricao() + "  " + e.getMessage() + ". Entre em contato com o suporte!");
        }
    }

    @Lock(LockType.READ)
    public String getNumeroPatrimonioLiquido(UnidadeOrganizacional und, Date date) {
        try {
            String toReturn;
            Query q = em.createNativeQuery(
                " SELECT max(cast(OBJ.numero as number)) as numero FROM patrimonioliquido obj " +
                    " WHERE OBJ.unidadeOrganizacional_id=:und " +
                    " having max(cast(OBJ.numero as number)) is not null ");
            q.setParameter("und", und.getId());

            Object singleResult = q.getSingleResult();
            if (singleResult == null) {
                throw new NoResultException("Nenhum registro encontrado.");
            }
            toReturn = acrecentaFormataCodigo(singleResult.toString());
            validaMaximoDeMovimentos(toReturn);

            return toReturn;
        } catch (NonUniqueResultException e) {
            throw new ExcecaoNegocioGenerica(" Erro ao gerar o número do patrimônio líquido para a unidade " + und.getDescricao() + ". Entre em contato com o suporte!");
        } catch (NoResultException e) {
            return geraPrimeiroCoigo(date, und);
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica(" Erro ao gerar o número do patrimônio líquido para a  unidade " + und.getDescricao() + "  " + e.getMessage() + ". Entre em contato com o suporte!");
        }
    }

    private IdentificadorMovimentoContabil criarIdentificadorMovimentoContabil(Exercicio ex, UnidadeOrganizacional und, Date date) {
        IdentificadorMovimentoContabil identificadorMovimentoContabil = new IdentificadorMovimentoContabil();
        identificadorMovimentoContabil.setExercicio(ex);
        identificadorMovimentoContabil.setUnidadeOrganizacional(und);
        identificadorMovimentoContabil.setDataMovimento(date);
        return identificadorMovimentoContabil;
    }

    @Lock(LockType.READ)
    public String getNumeroInvestimento(Exercicio exercicio, UnidadeOrganizacional und, Date date) {
        IdentificadorMovimentoContabil identificadorMovimentoContabil = criarIdentificadorMovimentoContabil(exercicio, und, date);
        try {
            if (proximoNumeroInvestimento == null) {
                proximoNumeroInvestimento = Maps.newHashMap();
            }
            if (proximoNumeroInvestimento.get(identificadorMovimentoContabil) == null) {
                Preconditions.checkNotNull(und, "A unidade orçamentária esta vazia.");
                Preconditions.checkNotNull(date, "A data para verificar a vigencia da unidade esta vazia.");
                String toReturn;
                String sql =
                    "  SELECT max(cast(OBJ.numero as number)) as numero FROM investimento obj  " +
                        "  where OBJ.unidadeOrganizacional_id = :und " +
                        "  having max(cast(OBJ.numero as number)) is not null ";
                Query q = em.createNativeQuery(sql);
                q.setParameter("und", und.getId());

                Object singleResult = q.getSingleResult();
                if (singleResult == null) {
                    throw new NoResultException("Nenhum registro encontrado.");
                }
                toReturn = acrecentaFormataCodigo(singleResult.toString());
                validaMaximoDeMovimentos(toReturn);
                proximoNumeroInvestimento.put(identificadorMovimentoContabil, toReturn);
            } else {
                String codigo = acrecentaFormataCodigo(proximoNumeroInvestimento.get(identificadorMovimentoContabil));
                proximoNumeroInvestimento.put(identificadorMovimentoContabil, codigo);
            }
            return proximoNumeroInvestimento.get(identificadorMovimentoContabil);
        } catch (NonUniqueResultException e) {
            throw new ExcecaoNegocioGenerica(" Erro ao gerar o número de investimento para a unidade " + und.getDescricao() + ". Entre em contato com o suporte!");
        } catch (NoResultException e) {
            String codigo = geraPrimeiroCoigo(date, und);
            proximoNumeroInvestimento.put(identificadorMovimentoContabil, codigo);
            return codigo;
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica(" Erro ao gerar o número de investimento para a unidade " + und.getDescricao() + "  " + e.getMessage() + ". Entre em contato com o suporte!");
        }
    }

    @Lock(LockType.READ)
    public String getNumeroReconhecimentoDivida(Exercicio exercicio, UnidadeOrganizacional und, Date date) {
        IdentificadorMovimentoContabil identificadorMovimentoContabil = criarIdentificadorMovimentoContabil(exercicio, und, date);
        try {
            if (proximoNumeroReconhecimentoDivida == null) {
                proximoNumeroReconhecimentoDivida = Maps.newHashMap();
            }
            if (proximoNumeroReconhecimentoDivida.get(identificadorMovimentoContabil) == null) {
                Preconditions.checkNotNull(und, "A unidade orçamentária esta vazia.");
                Preconditions.checkNotNull(date, "A data para verificar a vigencia da unidade esta vazia.");
                String toReturn;
                String sql =
                    "  SELECT max(cast(OBJ.numero as number)) as numero FROM reconhecimentodivida obj  " +
                        "  where OBJ.unidadeOrcamentaria_id = :und " +
                        "  having max(cast(OBJ.numero as number)) is not null ";
                Query q = em.createNativeQuery(sql);
                q.setParameter("und", und.getId());

                Object singleResult = q.getSingleResult();
                if (singleResult == null) {
                    throw new NoResultException("Nenhum registro encontrado.");
                }
                toReturn = acrecentaFormataCodigo(singleResult.toString());
                validaMaximoDeMovimentos(toReturn);
                proximoNumeroReconhecimentoDivida.put(identificadorMovimentoContabil, toReturn);
            } else {
                String codigo = acrecentaFormataCodigo(proximoNumeroReconhecimentoDivida.get(identificadorMovimentoContabil));
                proximoNumeroReconhecimentoDivida.put(identificadorMovimentoContabil, codigo);
            }
            return proximoNumeroReconhecimentoDivida.get(identificadorMovimentoContabil);
        } catch (NonUniqueResultException e) {
            throw new ExcecaoNegocioGenerica(" Erro ao gerar o número de reconhecimento de dívida do exercício para a unidade " + und.getDescricao() + ". Entre em contato com o suporte!");
        } catch (NoResultException e) {
            String codigo = geraPrimeiroCoigo(date, und);
            proximoNumeroReconhecimentoDivida.put(identificadorMovimentoContabil, codigo);
            return codigo;
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica(" Erro ao gerar o número de reconhecimento de dívida do exercício para a unidade " + und.getDescricao() + "  " + e.getMessage() + ". Entre em contato com o suporte!");
        }
    }

    @Lock(LockType.READ)
    public String getNumeroSolicitacaoReconhecimentoDivida(Exercicio exercicio) {
        try {
            if (proximoNumeroSolicitacaoReconhecimentoDivida == null) {
                proximoNumeroSolicitacaoReconhecimentoDivida = Maps.newHashMap();
            }
            if (proximoNumeroSolicitacaoReconhecimentoDivida.get(exercicio) == null) {
                String toReturn;
                String sql = "  SELECT max(cast(OBJ.numero as number)) as numero" +
                    "  FROM SOLRECONHECIMENTODIVIDA obj " +
                    " inner join reconhecimentoDivida rec on obj.reconhecimentoDivida_id = rec.id " +
                    "  where rec.exercicio_id = :exerc " +
                    "  having max(cast(OBJ.numero as number)) is not null ";
                Query q = em.createNativeQuery(sql);
                q.setParameter("exerc", exercicio.getId());
                Object singleResult = q.getSingleResult();
                if (singleResult == null) {
                    throw new NoResultException("Nenhum registro encontrado.");
                }
                toReturn = acrecentaFormataCodigo(singleResult.toString());
                validaMaximoDeMovimentos(toReturn);
                proximoNumeroSolicitacaoReconhecimentoDivida.put(exercicio, ((BigDecimal) q.getResultList().get(0)).longValue());
            }
            String retorno = acrecentaFormataCodigo(proximoNumeroSolicitacaoReconhecimentoDivida.get(exercicio).toString());
            proximoNumeroSolicitacaoReconhecimentoDivida.put(exercicio, proximoNumeroSolicitacaoReconhecimentoDivida.get(exercicio) + 1);
            return retorno;
        } catch (NonUniqueResultException e) {
            throw new ExcecaoNegocioGenerica(" Erro ao gerar o número da solicitação de empenho/reconhecimento de dívida do exercício para o exercício " + exercicio.getAno() + ". Entre em contato com o suporte!");
        } catch (NoResultException e) {
            return "01";
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica(" Erro ao gerar o número da solicitação de empenho/reconhecimento de dívida do exercício para o exercício " + exercicio.getAno() + "  " + e.getMessage() + ". Entre em contato com o suporte!");
        }
    }

    @Lock(LockType.READ)
    public String getNumeroEmenda(Exercicio exercicio) {
        try {
            if (proximoNumeroEmenda == null) {
                proximoNumeroEmenda = Maps.newHashMap();
            }
            if (proximoNumeroEmenda.get(exercicio) == null) {
                String sql = "  SELECT max(cast(OBJ.numero as number)) as numero" +
                    "  from emenda obj " +
                    "  where obj.exercicio_id = :exerc " +
                    "  having max(cast(obj.numero as number)) is not null ";
                Query q = em.createNativeQuery(sql);
                q.setParameter("exerc", exercicio.getId());
                Object singleResult = q.getSingleResult();
                if (singleResult == null) {
                    throw new NoResultException("Nenhum registro encontrado.");
                }
                proximoNumeroEmenda.put(exercicio, ((BigDecimal) q.getResultList().get(0)).longValue());
            }
            proximoNumeroEmenda.put(exercicio, proximoNumeroEmenda.get(exercicio) + 1);
            return proximoNumeroEmenda.get(exercicio).toString();
        } catch (NonUniqueResultException e) {
            throw new ExcecaoNegocioGenerica(" Erro ao gerar o número da emenda para o exercício " + exercicio.getAno() + ". Entre em contato com o suporte!");
        } catch (NoResultException e) {
            return "1";
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica(" Erro ao gerar o número da emenda para o exercício " + exercicio.getAno() + "  " + e.getMessage() + ". Entre em contato com o suporte!");
        }
    }
}
