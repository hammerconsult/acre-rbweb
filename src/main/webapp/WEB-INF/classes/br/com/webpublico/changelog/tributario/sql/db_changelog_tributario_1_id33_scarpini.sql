update situacaoparcelavalordivida spvd set saldo =
(select s.saldo from situacaoparcelavalordivida s where s.parcela_id = spvd.parcela_id and s.situacaoparcela = 'EM_ABERTO' and rownum =1)
where situacaoparcela = 'PARCELAMENTO_CANCELADO'
and datalancamento > to_date('14/01/2015', 'dd/MM/yyyy')