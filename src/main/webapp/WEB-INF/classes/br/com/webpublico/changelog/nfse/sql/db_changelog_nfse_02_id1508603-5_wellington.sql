create or replace procedure corrige_declaracoes_itens_detalhe is
    cursor declaracoes is
        with item_duplicado as (
            select dec.id as dec_id, count(ids.ID)
            from notafiscal nf
                     inner join declaracaoprestacaoservico dec on dec.id = nf.DECLARACAOPRESTACAOSERVICO_ID
                     inner join ITEMDECLARACAOSERVICO ids on ids.DECLARACAOPRESTACAOSERVICO_ID = dec.ID
            where nf.DESCRIMINACAOSERVICO not like 'Nota fiscal eletronica não emitida por falha sistemica%'
            group by dec.id
            having count(1) > 1)
        select dec.id
        from declaracaoprestacaoservico dec
                 inner join notafiscal nf on nf.DECLARACAOPRESTACAOSERVICO_ID = dec.ID
                 inner join cadastroeconomico ce on ce.id = nf.PRESTADOR_ID
                 inner join item_duplicado i on i.dec_id = dec.id;
    declaracao    declaracoes%rowtype;
    cursor itens_agrupado(id_dec number) is
        select s.id                          as servico_id,
               s.nome                        as nomeservico,
               s.nome                        as descricao,
               s.ALIQUOTAISSHOMOLOGADO       as aliquota,
               SUM(ISS)                      as iss,
               SUM(BASECALCULO)              as BASECALCULO,
               SUM(DEDUCOES)                 as DEDUCOES,
               SUM(DESCONTOSINCONDICIONADOS) as DESCONTOSINCONDICIONADOS,
               SUM(DESCONTOSCONDICIONADOS)   as DESCONTOSCONDICIONADOS,
               SUM(VALORSERVICO)             as VALORSERVICO,
               MAX(COALESCE(PRESTADONOPAIS, 0)) PRESTADONOPAIS,
               MAX(MUNICIPIO_ID)             as MUNICIPIO_ID,
               MAX(PAIS_ID)                  as PAIS_ID,
               MAX(COALESCE(DEDUCAO, 0))     as DEDUCAO,
               MAX(TIPODEDUCAO)              as TIPODEDUCAO,
               MAX(TIPOOPERACAO)             as TIPOOPERACAO,
               MAX(NUMERODOCUMENTOFISCAL)    AS NUMERODOCUMENTOFISCAL,
               MAX(CPFCNPJDEDUCAO)           as CPFCNPJDEDUCAO,
               MAX(VALORDOCUMENTOFISCAL)     as VALORDOCUMENTOFISCAL
        from ITEMDECLARACAOSERVICO ids
                 inner join servico s on s.id = ids.SERVICO_ID
        where ids.DECLARACAOPRESTACAOSERVICO_ID = id_dec
        group by s.id, s.nome, s.ALIQUOTAISSHOMOLOGADO;
    item_agrupado itens_agrupado%rowtype;
    cursor itens(id_dec number, id_agrupado number) is
        select ids.id, ids.DESCRICAO, ids.quantidade, ids.VALORSERVICO
        from itemdeclaracaoservico ids
        where ids.DECLARACAOPRESTACAOSERVICO_ID = id_dec
          and ids.id != id_agrupado;
    item          itens%rowtype;
    id_item       number;
BEGIN
    dbms_output.put_line('Iniciou: ' || current_timestamp);
    open declaracoes;
    loop
        FETCH declaracoes INTO declaracao;
        EXIT WHEN declaracoes%NOTFOUND;


        select HIBERNATE_SEQUENCE.nextval into id_item from dual;

        open itens_agrupado(declaracao.id);
        loop
            fetch itens_agrupado into item_agrupado;
            exit when itens_agrupado%notfound;

            insert into ITEMDECLARACAOSERVICO (ID, ISS, BASECALCULO, DEDUCOES, DESCONTOSINCONDICIONADOS,
                                               DESCONTOSCONDICIONADOS,
                                               QUANTIDADE, VALORSERVICO, DESCRICAO, NOMESERVICO, ALIQUOTASERVICO,
                                               PRESTADONOPAIS,
                                               OBSERVACOES, SERVICO_ID, MUNICIPIO_ID, DECLARACAOPRESTACAOSERVICO_ID,
                                               PAIS_ID,
                                               CNAE_ID, DEDUCAO, TIPODEDUCAO, TIPOOPERACAO, NUMERODOCUMENTOFISCAL,
                                               CPFCNPJDEDUCAO,
                                               VALORDOCUMENTOFISCAL)
            values (id_item, item_agrupado.iss, item_agrupado.BASECALCULO, item_agrupado.DEDUCOES,
                    item_agrupado.DESCONTOSINCONDICIONADOS,
                    item_agrupado.DESCONTOSCONDICIONADOS, 1, item_agrupado.VALORSERVICO, item_agrupado.descricao,
                    item_agrupado.nomeservico, item_agrupado.aliquota, item_agrupado.PRESTADONOPAIS,
                    '', item_agrupado.servico_id, item_agrupado.MUNICIPIO_ID, declaracao.id,
                    item_agrupado.PAIS_ID,
                    null, item_agrupado.DEDUCAO, item_agrupado.TIPODEDUCAO, item_agrupado.TIPOOPERACAO,
                    item_agrupado.NUMERODOCUMENTOFISCAL, item_agrupado.CPFCNPJDEDUCAO,
                    item_agrupado.VALORDOCUMENTOFISCAL);
        end loop;
        close itens_agrupado;

        open itens(declaracao.id, id_item);
        loop
            fetch itens into item;
            exit when itens%notfound;

            insert into DETALHEITEMDECSERVICO (ID, QUANTIDADE, VALORSERVICO, DESCRICAO, ITEMDECLARACAOSERVICO_ID)
            values (HIBERNATE_SEQUENCE.nextval, item.QUANTIDADE, item.VALORSERVICO, item.DESCRICAO, id_item);

            delete from DETALHEITEMDECSERVICO where ITEMDECLARACAOSERVICO_ID = item.id;
            delete from ITEMDECLARACAOSERVICO where id = item.id;
        end loop;
        close itens;
    END LOOP;
    CLOSE declaracoes;

    commit;
    dbms_output.put_line('Finalizou: ' || current_timestamp);
END;
