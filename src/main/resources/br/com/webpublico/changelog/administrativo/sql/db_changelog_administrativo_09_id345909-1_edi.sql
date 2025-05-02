update MOVIMENTOALTERACAOCONT m
set m.VALORVARIACAOCONTRATO = (select 1
                               from MOVIMENTOALTERACAOCONT mov
                               where mov.TIPO = 'VALOR_VARIACAO'
                                 and mov.ALTERACAOCONTRATUAL_ID = m.ALTERACAOCONTRATUAL_ID
                                 and mov.id = m.id)
where m.VALORVARIACAOCONTRATO is null;


update MOVIMENTOALTERACAOCONT m
set m.VALORVARIACAOCONTRATO = 0
where m.VALORVARIACAOCONTRATO is null;
