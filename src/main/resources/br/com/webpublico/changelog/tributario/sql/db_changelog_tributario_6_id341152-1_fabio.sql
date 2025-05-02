update REGISTROLANCAMENTOCONTABIL set NUMEROREGISTRO = 2 where situacao = 'AGUARDANDO' and acaofiscal_id in (
select ACAOFISCAL_ID from (
select reg.ACAOFISCAL_ID, count(reg.NUMEROREGISTRO) from REGISTROLANCAMENTOCONTABIL reg
where reg.NUMEROREGISTRO = 1
group by reg.ACAOFISCAL_ID
having count(reg.NUMEROREGISTRO) > 1))
