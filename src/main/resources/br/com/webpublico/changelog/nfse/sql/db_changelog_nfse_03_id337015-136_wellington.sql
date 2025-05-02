update enquadramentofiscal ef
set banco_id = (
    select ce.banco_id
    from cadastroeconomico ce
    where ce.id = ef.cadastroeconomico_id)
where ef.fimvigencia is null;

update enquadramentofiscal_aud ef
set banco_id = (
    select ce.banco_id
    from cadastroeconomico ce
    where ce.id = ef.cadastroeconomico_id)
where ef.fimvigencia is null;


update enquadramentofiscal
set versaodesif = 'VERSAO_1_0'
where banco_id is not null;

update enquadramentofiscal_aud
set versaodesif = 'VERSAO_1_0'
where banco_id is not null;
