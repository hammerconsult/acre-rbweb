update CONTATRIBUTORECEITA conta set conta.FINALVIGENCIA =
(select to_date('31/12/' || ex.ano, 'dd/MM/yyyy') from CONTATRIBUTORECEITA ctc
inner join EnquadramentoTributoExerc enq on enq.id = ctc.ENQUADRAMENTO_ID
inner join Exercicio ex on ex.id = enq.EXERCICIOVIGENTE_ID
where conta.id = ctc.id)
