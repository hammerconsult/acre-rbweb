package br.com.webpublico.negocios.jdbc;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.contabil.ContaAuxiliarDetalhada;
import br.com.webpublico.enums.OperacaoClasseCredor;
import br.com.webpublico.enums.TagValor;
import br.com.webpublico.interfaces.IGeraContaAuxiliar;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by venom on 23/02/15.
 */
public class ParametroEventoJDBC extends ClassPatternJDBC {

    private static ParametroEventoJDBC instancia;
    private OrcamentoJDBC orcamentoJDBC = OrcamentoJDBC.createInstance(conexao);
    private BancarioJDBC bancarioJDBC = BancarioJDBC.createInstance(conexao);
    private OrigemContaContabilJDBC origemContaContabilJDBC = OrigemContaContabilJDBC.createInstance(conexao);
    private IdGenerator idGenerator = IdGenerator.createInstance(conexao);
    private PreparedStatement psInsertParametroEvento;
    private PreparedStatement psInsertItemParametroEvento;


    private ParametroEventoJDBC(Connection conn) {
        this.conexao = conn;
    }

    public static ParametroEventoJDBC createInstance(Connection conn) {
        if (instancia == null) {
            instancia = new ParametroEventoJDBC(conn);
        }
        showConnection(instancia.getClass());
        return instancia;
    }

    public void contabilizarParametroEvento(ParametroEvento param) throws SQLException {
        CLP clp = null;
        TagValor tagValorTemp = null;
        for (ItemParametroEvento item : param.getItensParametrosEvento()) {
            if (item.getTagValor() != tagValorTemp) {
                tagValorTemp = item.getTagValor();
                clp = origemContaContabilJDBC.selectCLP(param.getEventoContabil(), item.getTagValor(), param.getDataEvento());
            }
            if (clp.getLcps().isEmpty()) {
                throw new ExcecaoNegocioGenerica("Não foi encontrada nenhuma LCP para a CLP " + clp.toString() + ".");
            }
            for (LCP lcp : clp.getLcps()) {
                ContaContabil contaCredito = recuperarContaCredito(param, item, lcp);
                ContaContabil contaDebito = recuperarContaDebito(param, item, lcp);

                ContaAuxiliar auxiliarCredito = selectContaAuxiliar(lcp.getTipoContaAuxiliarCredito(), item.getObjetoParametros(), contaCredito, param.getExercicio(), true);
                ContaAuxiliar contaAuxiliarCredSiconfi = selectContaAuxiliar(lcp.getTipoContaAuxCredSiconfi(), item.getObjetoParametros(), contaCredito, param.getExercicio(), false);
                ContaAuxiliarDetalhada contaAuxiliarCredSiconfiDetalhada = null;

                ContaAuxiliar auxiliarDebito = selectContaAuxiliar(lcp.getTipoContaAuxiliarDebito(), item.getObjetoParametros(), contaDebito, param.getExercicio(), true);
                ContaAuxiliar contaAuxiliarDebSiconfi = selectContaAuxiliar(lcp.getTipoContaAuxDebSiconfi(), item.getObjetoParametros(), contaCredito, param.getExercicio(), false);
                ContaAuxiliarDetalhada contaAuxiliarDebSiconfiDetalada = null;
                LancamentoContabil lc = new LancamentoContabil(
                    param.getDataEvento(),
                    item.getValor(),
                    contaCredito,
                    contaDebito,
                    param.getEventoContabil().getClpHistoricoContabil(),
                    param.getComplementoHistorico(),
                    param.getUnidadeOrganizacional(),
                    lcp,
                    item,
                    auxiliarCredito,
                    auxiliarDebito,
                    contaAuxiliarCredSiconfi,
                    contaAuxiliarDebSiconfi,
                    contaAuxiliarCredSiconfiDetalhada,
                    contaAuxiliarDebSiconfiDetalada);
                orcamentoJDBC.insertLancamentoContabil(lc);
            }
        }
        insertParametroEvento(param);
    }

    private ContaAuxiliar selectContaAuxiliar(TipoContaAuxiliar tipoContaAuxiliar, List<ObjetoParametro> objetoParametros, ContaContabil contaContabil, Exercicio exercicio, boolean isContaAuxiliarSistema) throws SQLException {
        ObjetoParametro objP = getObjetoParametroByTipoContaAuxiliar(tipoContaAuxiliar, objetoParametros);
        Object obj = getEntityByObjetoParametro(objP);
        IGeraContaAuxiliar iObjeto = (IGeraContaAuxiliar) obj;
        TreeMap contasAuxiliares = isContaAuxiliarSistema ? iObjeto.getMapContaAuxiliarSistema(tipoContaAuxiliar) : iObjeto.getMapContaAuxiliarSiconfi(tipoContaAuxiliar, contaContabil);
        PlanoDeContas pdc = orcamentoJDBC.getPlanoDeContasAuxiliarByAno(exercicio.getAno());

        MapContaAuxiliar mapTipo = new MapContaAuxiliar(tipoContaAuxiliar.getCodigo(), contaContabil.getId(), pdc.getId());
        orcamentoJDBC.loadContasAuxiliaresByCodigoTipoContaAuxiliar(mapTipo, contaContabil, tipoContaAuxiliar.getDescricao(), exercicio);

        MapContaAuxiliar lastMap = mapTipo;

        Set set = contasAuxiliares.entrySet();
        Iterator i = set.iterator();
        while (i.hasNext()) {
            Map.Entry me = (Map.Entry) i.next();
            MapContaAuxiliar map = new MapContaAuxiliar(tipoContaAuxiliar.getCodigo() + "." + me.getKey().toString(), contaContabil.getId(), pdc.getId());
//            if (!contasAuxiliares.containsKey(map)) {
//                insereContaAuxiliar(map, getContaAuxiliarByMap(lastMap), me.getValue().toString(), ano);
//            } else {
//                lastMap = map;
//            }
            orcamentoJDBC.loadContasAuxiliaresByCodigoTipoContaAuxiliar(map, contaContabil, tipoContaAuxiliar.getDescricao(), exercicio);
            lastMap = map;
        }

        MapContaAuxiliar mapChave = new MapContaAuxiliar(tipoContaAuxiliar.getCodigo() + "." + contasAuxiliares.lastKey().toString(), contaContabil.getId(), pdc.getId());
        if (orcamentoJDBC.getContaAuxiliarByMap(mapChave) != null) {
            return orcamentoJDBC.getContaAuxiliarByMap(mapChave);
        }
        return orcamentoJDBC.getContaAuxiliarByMap(lastMap);
    }

    private Object getEntityByObjetoParametro(ObjetoParametro obj) throws SQLException {
        switch (obj.getClasseObjeto()) {
            case "ContaDespesa":
                return orcamentoJDBC.getContaDespesaById(Long.parseLong(obj.getIdObjeto()));
            case "SubConta":
                return bancarioJDBC.getSubContaById(Long.parseLong(obj.getIdObjeto()));
            default:
                throw new ExcecaoNegocioGenerica("Classe do objeto parâmetro não tratada: " + obj.getClasseObjeto());
        }
    }

    private ObjetoParametro getObjetoParametroByTipoContaAuxiliar(TipoContaAuxiliar tipoContaAuxiliar, List<ObjetoParametro> objetoParametros) {
        List<ObjetoParametro> lista = new ArrayList<>();
        for (ObjetoParametro obj : objetoParametros) {
            if (obj.getClasseObjeto().equals(tipoContaAuxiliar.getChave())) {
                lista.add(obj);
            }
        }
        if (lista.size() > 1) {
            throw new ExcecaoNegocioGenerica("Mais de um objeto parâmetro com o mesmo tipoo de conta auxiliar: " + tipoContaAuxiliar.getChave());
        }
        if (lista.isEmpty()) {
            throw new ExcecaoNegocioGenerica(" O tipo de Conta Auxiliar " + tipoContaAuxiliar.getCodigo() + " - " + tipoContaAuxiliar.getDescricao() + " contem uma chave(\"" + tipoContaAuxiliar.getChave() + "\") que o sistema não reconhece.");
        }
        return lista.get(0);
    }

    private ContaContabil recuperarContaCredito(ParametroEvento parametroEvento, ItemParametroEvento itemParametro, LCP lcp) {
        if ((itemParametro.getOperacaoClasseCredor() == OperacaoClasseCredor.EXTRA
            || itemParametro.getOperacaoClasseCredor() == OperacaoClasseCredor.NENHUM
            || itemParametro.getOperacaoClasseCredor() == null)
            && lcp.getContaCredito() != null) {
            return (ContaContabil) lcp.getContaCredito();
        } else if (itemParametro.getOperacaoClasseCredor() == OperacaoClasseCredor.INTER && lcp.getContaCreditoInterMunicipal() != null) {
            return (ContaContabil) lcp.getContaCreditoInterMunicipal();
        } else if (itemParametro.getOperacaoClasseCredor() == OperacaoClasseCredor.INTER_ESTADO && lcp.getContaCreditoInterEstado() != null) {
            return (ContaContabil) lcp.getContaCreditoInterEstado();
        } else if (itemParametro.getOperacaoClasseCredor() == OperacaoClasseCredor.INTER_UNIAO && lcp.getContaCreditoInterUniao() != null) {
            return (ContaContabil) lcp.getContaCreditoInterUniao();
        } else if (itemParametro.getOperacaoClasseCredor() == OperacaoClasseCredor.INTRA && lcp.getContaCreditoIntra() != null) {
            return (ContaContabil) lcp.getContaCreditoIntra();
        } else if (lcp.getTagOCCCredito() != null) {
            return recuperaContaPorTagEObjeto(lcp.getTagOCCCredito(), itemParametro.getObjetoParametrosCredito(), itemParametro.getOperacaoClasseCredor(), parametroEvento.getDataEvento());
        } else {
            if (itemParametro.getOperacaoClasseCredor() != null) {
                if (lcp.getContaCredito() == null) {
                    throw new ExcecaoNegocioGenerica(" Não foi encontrada nenhuma conta contábil de crédito configurada para a operação classe credor " + itemParametro.getOperacaoClasseCredor().getDescricao());
                } else {
                    return (ContaContabil) lcp.getContaCredito();
                }
            } else {
                if (lcp.getContaCredito() == null) {
                    throw new ExcecaoNegocioGenerica("Não foi encontrada nenhuma conta contábil de crédito configurada para a operação classe credor.");
                } else {
                    return (ContaContabil) lcp.getContaCredito();
                }
            }
        }
    }

    private ContaContabil recuperarContaDebito(ParametroEvento parametroEvento, ItemParametroEvento itemParametro, LCP lcp) {
        if ((itemParametro.getOperacaoClasseCredor() == OperacaoClasseCredor.EXTRA
            || itemParametro.getOperacaoClasseCredor() == OperacaoClasseCredor.NENHUM
            || itemParametro.getOperacaoClasseCredor() == null)
            && lcp.getContaDebito() != null) {
            return (ContaContabil) lcp.getContaDebito();
        } else if (itemParametro.getOperacaoClasseCredor() == OperacaoClasseCredor.INTER && lcp.getContaDebitoInterMunicipal() != null) {
            return (ContaContabil) lcp.getContaDebitoInterMunicipal();
        } else if (itemParametro.getOperacaoClasseCredor() == OperacaoClasseCredor.INTER_ESTADO && lcp.getContaDebitoInterEstado() != null) {
            return (ContaContabil) lcp.getContaDebitoInterEstado();
        } else if (itemParametro.getOperacaoClasseCredor() == OperacaoClasseCredor.INTER_UNIAO && lcp.getContaDebitoInterUniao() != null) {
            return (ContaContabil) lcp.getContaDebitoInterUniao();
        } else if (itemParametro.getOperacaoClasseCredor() == OperacaoClasseCredor.INTRA && lcp.getContaDebitoIntra() != null) {
            return (ContaContabil) lcp.getContaDebitoIntra();
        } else if (lcp.getTagOCCDebito() != null) {
            return recuperaContaPorTagEObjeto(lcp.getTagOCCDebito(), itemParametro.getObjetoParametrosDebito(), itemParametro.getOperacaoClasseCredor(), parametroEvento.getDataEvento());
        } else {
            if (itemParametro.getOperacaoClasseCredor() != null) {
                if (lcp.getContaDebito() == null) {
                    throw new ExcecaoNegocioGenerica(" Não foi encontrada nenhuma conta contábil de débito configurada para a operação classe credor " + itemParametro.getOperacaoClasseCredor().getDescricao());
                } else {
                    return (ContaContabil) lcp.getContaDebito();
                }
            } else {
                if (lcp.getContaDebito() == null) {
                    throw new ExcecaoNegocioGenerica("Não foi encontrada nenhuma conta contábil de débito configurada para a operação classe credor.");
                } else {
                    return (ContaContabil) lcp.getContaDebito();
                }
            }
        }
    }

    private ContaContabil recuperaContaPorTagEObjeto(TagOCC tagOCC, List<ObjetoParametro> objetoParametros, OperacaoClasseCredor op, Date data) throws ExcecaoNegocioGenerica {
        try {
            //Para cada origem conta contábil deverá ser implementada uma condição, incluindo a SQL de busca!
            switch (tagOCC.getEntidadeOCC()) {
                case CONTAFINANCEIRA:
                    return recuperaContaPorTagEObjetoContaFinanceira(tagOCC, objetoParametros, op, data);
                case CONTADESPESA:
                    return recuperaContaPorTagEObjetoContaDespesaReceitaExtraDestinacao(tagOCC, objetoParametros, op, data);
                case CONTARECEITA:
                    return recuperaContaPorTagEObjetoContaDespesaReceitaExtraDestinacao(tagOCC, objetoParametros, op, data);
                case CONTACONTABIL:
                    return recuperaContaContabilDireto(objetoParametros);
                case DESTINACAO:
                    return recuperaContaPorTagEObjetoContaDespesaReceitaExtraDestinacao(tagOCC, objetoParametros, op, data);
                case CONTAEXTRAORCAMENTARIA:
                    return recuperaContaPorTagEObjetoContaDespesaReceitaExtraDestinacao(tagOCC, objetoParametros, op, data);
                case NATUREZADIVIDAPUBLICA:
                    return recuperaContaPorTagEObjetoNaturezaDividaPublica(tagOCC, objetoParametros, op, data);
                case TIPOCLASSEPESSOA:
                    return recuperaContaPorTagEObjetoTipoClassePessoa(tagOCC, objetoParametros, op, data);
                case TIPOPASSIVOATUARIAL:
                    return recuperaContaPorTagEObjetoTipoPassivoAtuarial(tagOCC, objetoParametros, op, data);
                case GRUPOMATERIAL:
                    return recuperaContaPorTagEObjetoGrupoMaterial(tagOCC, objetoParametros, op, data);
                case GRUPOBEM:
                    return recuperaContaPorTagEObjetoGrupoBem(tagOCC, objetoParametros, op, data);
                case ORIGEM_RECURSO:
                    return recuperaContaPorTagEObjetoOrigemRecurso(tagOCC, objetoParametros, op, data);
                default:
                    throw new ExcecaoNegocioGenerica("Entidade da origem conta contábil não encontrada! " + tagOCC.getEntidadeOCC().name());
            }
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        }
    }

    private ContaContabil recuperaContaContabilDireto(List<ObjetoParametro> objetoParametros) {
        for (ObjetoParametro obj : objetoParametros) {
            if (obj.getClasseObjeto().equals("ContaContabil")) {
                return (ContaContabil) orcamentoJDBC.getContaById(Long.parseLong(obj.getIdObjeto()));
            }
        }
        return null;
    }

    private ContaContabil recuperaContaPorTagEObjetoGrupoMaterial(TagOCC tagOCC, List<ObjetoParametro> objetoParametros, OperacaoClasseCredor op, Date data) {
        String idObjeto = null;
        for (ObjetoParametro obj : objetoParametros) {
            if (obj.getClasseObjeto().equals("SubConta")) {
                idObjeto = obj.getIdObjeto();
            }
        }
        if (idObjeto != null) {
            MapTagOcc mapTag = new MapTagOcc(tagOCC, idObjeto);
            return retornaContaOCCporOperacaoClasseCredor(op, origemContaContabilJDBC.getOccGrupoMaterialByMap(mapTag, data));
        } else {
            throw new ExcecaoNegocioGenerica(" Não foi encontrada o parametro de busca para a tag " + tagOCC);
        }
    }

    private ContaContabil recuperaContaPorTagEObjetoGrupoBem(TagOCC tagOCC, List<ObjetoParametro> objetoParametros, OperacaoClasseCredor op, Date data) {
        String idObjeto = null;
        for (ObjetoParametro obj : objetoParametros) {
            if (obj.getClasseObjeto().equals("SubConta")) {
                idObjeto = obj.getIdObjeto();
            }
        }
        if (idObjeto != null) {
            MapTagOcc mapTag = new MapTagOcc(tagOCC, idObjeto);
            return retornaContaOCCporOperacaoClasseCredor(op, origemContaContabilJDBC.getOccGrupoBemByMap(mapTag, data));
        } else {
            throw new ExcecaoNegocioGenerica(" Não foi encontrada o parametro de busca para a tag " + tagOCC);
        }
    }

    private ContaContabil recuperaContaPorTagEObjetoOrigemRecurso(TagOCC tagOCC, List<ObjetoParametro> objetoParametros, OperacaoClasseCredor op, Date data) {
        String idObjeto = null;
        for (ObjetoParametro obj : objetoParametros) {
            if (obj.getClasseObjeto().equals("SubConta")) {
                idObjeto = obj.getIdObjeto();
            }
        }
        if (idObjeto != null) {
            MapTagOcc mapTag = new MapTagOcc(tagOCC, idObjeto);
            return retornaContaOCCporOperacaoClasseCredor(op, origemContaContabilJDBC.getOccOrigemRecursoByMap(mapTag, data));
        } else {
            throw new ExcecaoNegocioGenerica(" Não foi encontrada o parametro de busca para a tag " + tagOCC);
        }
    }

    private ContaContabil recuperaContaPorTagEObjetoTipoPassivoAtuarial(TagOCC tagOCC, List<ObjetoParametro> objetoParametros, OperacaoClasseCredor op, Date data) {
        String idObjeto = null;
        for (ObjetoParametro obj : objetoParametros) {
            if (obj.getClasseObjeto().equals("SubConta")) {
                idObjeto = obj.getIdObjeto();
            }
        }
        if (idObjeto != null) {
            MapTagOcc mapTag = new MapTagOcc(tagOCC, idObjeto);
            return retornaContaOCCporOperacaoClasseCredor(op, origemContaContabilJDBC.getOccTipoPassivoAtuarialByMap(mapTag, data));
        } else {
            throw new ExcecaoNegocioGenerica(" Não foi encontrada o parametro de busca para a tag " + tagOCC);
        }
    }

    private ContaContabil recuperaContaPorTagEObjetoNaturezaDividaPublica(TagOCC tagOCC, List<ObjetoParametro> objetoParametros, OperacaoClasseCredor op, Date data) {
        String idObjeto = null;
        for (ObjetoParametro obj : objetoParametros) {
            if (obj.getClasseObjeto().equals("SubConta")) {
                idObjeto = obj.getIdObjeto();
            }
        }
        if (idObjeto != null) {
            MapTagOcc mapTag = new MapTagOcc(tagOCC, idObjeto);
            return retornaContaOCCporOperacaoClasseCredor(op, origemContaContabilJDBC.getOccNaturezaDividaPublicaByMap(mapTag, data));
        } else {
            throw new ExcecaoNegocioGenerica(" Não foi encontrada o parametro de busca para a tag " + tagOCC);
        }
    }

    private ContaContabil recuperaContaPorTagEObjetoTipoClassePessoa(TagOCC tagOCC, List<ObjetoParametro> objetoParametros, OperacaoClasseCredor op, Date data) {
        String idObjeto = null;
        for (ObjetoParametro obj : objetoParametros) {
            if (obj.getClasseObjeto().equals("SubConta")) {
                idObjeto = obj.getIdObjeto();
            }
        }
        if (idObjeto != null) {
            MapTagOcc mapTag = new MapTagOcc(tagOCC, idObjeto);
            return retornaContaOCCporOperacaoClasseCredor(op, origemContaContabilJDBC.getOccClassePessoaByMap(mapTag, data));
        } else {
            throw new ExcecaoNegocioGenerica(" Não foi encontrada o parametro de busca para a tag " + tagOCC);
        }
    }

    private ContaContabil recuperaContaPorTagEObjetoContaFinanceira(TagOCC tagOCC, List<ObjetoParametro> objetoParametros, OperacaoClasseCredor op, Date data) {
        String idObjeto = null;
        for (ObjetoParametro obj : objetoParametros) {
            if (obj.getClasseObjeto().equals("SubConta")) {
                idObjeto = obj.getIdObjeto();
            }
        }
        if (idObjeto != null) {
            MapTagOcc mapTag = new MapTagOcc(tagOCC, idObjeto);
            return retornaContaOCCporOperacaoClasseCredor(op, origemContaContabilJDBC.getOccBancoByMap(mapTag, data));
        } else {
            throw new ExcecaoNegocioGenerica(" Não foi encontrada o parametro de busca para a tag " + tagOCC);
        }
    }

    private ContaContabil recuperaContaPorTagEObjetoContaDespesaReceitaExtraDestinacao(TagOCC tagOCC, List<ObjetoParametro> objetoParametros, OperacaoClasseCredor op, Date data) {
        String idObjeto = null;
        //for muito questionável!!
        for (ObjetoParametro obj : objetoParametros) {
            if (obj.getClasseObjeto().equals("ContaDespesa") || obj.getClasseObjeto().equals("ContaDeDestinacao") || obj.getClasseObjeto().equals("ContaReceita") || obj.getClasseObjeto().equals("ContaExtraorcamentaria")) {
                idObjeto = obj.getIdObjeto();
            }
        }
        if (idObjeto != null) {
            MapTagOcc mapTag = new MapTagOcc(tagOCC, idObjeto);
            return retornaContaOCCporOperacaoClasseCredor(op, origemContaContabilJDBC.getOccContaByMap(mapTag, data));
        } else {
            throw new ExcecaoNegocioGenerica(" Não foi encontrada o parametro de busca para a tag " + tagOCC);
        }
    }

    public ContaContabil retornaContaOCCporOperacaoClasseCredor(OperacaoClasseCredor op, OrigemContaContabil origemContaContabil) throws ExcecaoNegocioGenerica {
        if ((op == OperacaoClasseCredor.EXTRA
            || op == OperacaoClasseCredor.NENHUM
            || op == null)
            && origemContaContabil.getContaContabil() != null) {
            return origemContaContabil.getContaContabil();
        } else if (op == OperacaoClasseCredor.INTER && origemContaContabil.getContaInterMunicipal() != null) {
            return origemContaContabil.getContaInterMunicipal();
        } else if (op == OperacaoClasseCredor.INTER_ESTADO && origemContaContabil.getContaInterEstado() != null) {
            return origemContaContabil.getContaInterEstado();
        } else if (op == OperacaoClasseCredor.INTER_UNIAO && origemContaContabil.getContaInterUniao() != null) {
            return origemContaContabil.getContaInterUniao();
        } else if (op == OperacaoClasseCredor.INTRA && origemContaContabil.getContaIntra() != null) {
            return origemContaContabil.getContaIntra();
        } else if ((op == OperacaoClasseCredor.EXTRA
            || op == OperacaoClasseCredor.NENHUM
            || op == null)
            && origemContaContabil.getContaContabil() == null) {
            throw new ExcecaoNegocioGenerica(" A Conta Contábil Extra da Origem Conta Contábil " + origemContaContabil + " não foi preenchida.");
        } else {
            throw new ExcecaoNegocioGenerica(" Não foi encontrada nenhuma conta contábil configurada para a operação classe credor " + op.getDescricao() + " na Origem Conta Contabil " + origemContaContabil + ".");
        }
    }

    private void insertParametroEvento(ParametroEvento param) {
        param.setId(idGenerator.createID());
        try {
            preparaInsertParametroEvento();
            psInsertParametroEvento.clearParameters();
            psInsertParametroEvento.setLong(1, param.getId());
            psInsertParametroEvento.setDate(2, (java.sql.Date) param.getDataEvento());
            psInsertParametroEvento.setString(3, param.getComplementoHistorico());
            psInsertParametroEvento.setString(4, param.getClasseOrigem());
            psInsertParametroEvento.setString(5, param.getIdOrigem());
            psInsertParametroEvento.setString(6, param.getComplementoId().name());
            psInsertParametroEvento.setString(7, param.getTipoBalancete().name());
            psInsertParametroEvento.setLong(8, param.getEventoContabil().getId());
            psInsertParametroEvento.setLong(9, param.getUnidadeOrganizacional().getId());
            psInsertParametroEvento.executeUpdate();
            persistItensParametroEvento(param.getItensParametrosEvento());
        } catch (SQLException ex) {
            throw new ExcecaoNegocioGenerica("Erro ao inserir parâmetro evento: " + ex.getMessage(), ex);
        }
    }

    private void preparaInsertParametroEvento() throws SQLException {
        if (psInsertParametroEvento == null) {
            psInsertParametroEvento = this.conexao.prepareStatement(Queries.insertParametroEvento());
        }
    }

    private void persistItensParametroEvento(List<ItemParametroEvento> lista) {
        for (ItemParametroEvento item : lista) {
            insertItemParametroEvento(item);
        }
    }

    private void insertItemParametroEvento(ItemParametroEvento item) {
        item.setId(idGenerator.createID());
        try {
            preparaInsertItemParametroEvento();
            psInsertItemParametroEvento.clearParameters();
            psInsertItemParametroEvento.setLong(1, item.getId());
            psInsertItemParametroEvento.setBigDecimal(2, item.getValor());
            psInsertItemParametroEvento.setString(3, item.getTagValor().name());
            psInsertItemParametroEvento.setString(4, item.getOperacaoClasseCredor().name());
            psInsertItemParametroEvento.setLong(5, item.getParametroEvento().getId());
            psInsertItemParametroEvento.executeUpdate();
        } catch (SQLException ex) {
            throw new ExcecaoNegocioGenerica("Erro ao inserir item parâmetro evento: " + ex.getMessage(), ex);
        }
    }

    private void preparaInsertItemParametroEvento() throws SQLException {
        if (psInsertItemParametroEvento == null) {
            psInsertItemParametroEvento = this.conexao.prepareStatement(Queries.insertItemParametroEvento());
        }
    }
}
