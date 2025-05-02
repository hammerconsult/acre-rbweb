    create or replace function parcela_original_pgto_judicial(
    PARCELA_ID in number
    ) return number
    is retorno number;
    begin
    select original
    into retorno
    from (
    with cteparcelamento(originada, original, tipocalculo) as (
    select pvd.id as originada, pvdoriginal.id as original, calculo.tipocalculo
    from parcelavalordivida pvd
    inner join valordivida vd on vd.id = pvd.valordivida_id
    inner join calculopagamentojudicial calpag on calpag.id = vd.calculo_id
    inner join pagamentojudicialparcela itempag on itempag.pagamentojudicial_id = calpag.pagamentojudicial_id
    inner join parcelavalordivida pvdoriginal on pvdoriginal.id = itempag.PARCELAVALORDIVIDA_ID
    inner join calculo on calculo.id = calpag.id
    where pvd.id = PARCELA_ID and rownum = 1
    union all
    select pvd.id as originada, pvdoriginal.id as original,  calculo.tipocalculo
    from parcelavalordivida pvd
    inner join CTEPARCELAMENTO cte on cte.original = pvd.id
    left join valordivida vd on vd.id = pvd.valordivida_id
    left join processoparcelamento parcelamento on parcelamento.id = vd.CALCULO_ID
    left join ITEMPROCESSOPARCELAMENTO itemparcelamento on itemparcelamento.PROCESSOPARCELAMENTO_ID = parcelamento.id
    left join parcelavalordivida pvdoriginal on pvdoriginal.id = itemparcelamento.PARCELAVALORDIVIDA_ID
    left join valordivida vdoriginal on vdoriginal.id = pvdoriginal.valordivida_id
    inner join calculo on calculo.id = vdoriginal.calculo_id
    where rownum= 1
    )

    select  min(cte.original) as original
    from cteparcelamento cte
    where cte.tipocalculo <> 'PROCESSO_COMPENSACAO'
    );
    return retorno;
    end
