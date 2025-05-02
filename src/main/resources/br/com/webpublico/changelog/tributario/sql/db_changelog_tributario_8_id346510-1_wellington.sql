
delete from itemlotebaixaparcela
where itemlotebaixa_id in (
    select ilb.id
    from ARQUIVODAF607 a
             inner join lotebaixadoarquivo lba on lba.ARQUIVODAF607_ID = a.id
             inner join lotebaixa lb on lb.id = lba.LOTEBAIXA_ID
             inner join itemlotebaixa ilb on ilb.lotebaixa_id = lb.id
    where ilb.dam_id is not null);

delete from itemlotebaixa
where id in (
    select ilb.id
    from ARQUIVODAF607 a
             inner join lotebaixadoarquivo lba on lba.ARQUIVODAF607_ID = a.id
             inner join lotebaixa lb on lb.id = lba.LOTEBAIXA_ID
             inner join itemlotebaixa ilb on ilb.lotebaixa_id = lb.id
    where ilb.dam_id is not null);

update lotebaixadoarquivo set lotebaixa_id = null
where id in (
    select lba.id
    from ARQUIVODAF607 a
             inner join lotebaixadoarquivo lba on lba.ARQUIVODAF607_ID = a.id
             inner join lotebaixa lb on lb.id = lba.LOTEBAIXA_ID
    where not exists (select 1 from itemlotebaixa ilb where ilb.lotebaixa_id = lb.id));


insert into SITUACAOPARCELAVALORDIVIDA (id, DATALANCAMENTO, SITUACAOPARCELA, PARCELA_ID, SALDO,
                                        INCONSISTENTE, REFERENCIA)
select hibernate_sequence.nextval, CURRENT_DATE, 'CANCELAMENTO', spvd.PARCELA_ID, spvd.SALDO,
       spvd.INCONSISTENTE, spvd.REFERENCIA
from parcelavalordivida pvd
         inner join valordivida vd on vd.id = pvd.VALORDIVIDA_ID
         inner join situacaoparcelavalordivida spvd on spvd.id = pvd.SITUACAOATUAL_ID
         left join itemdam id on id.PARCELA_ID  = pvd.id
where vd.DIVIDA_ID = 10711171287
  and id.id is null
  and spvd.SITUACAOPARCELA = 'PAGO';
