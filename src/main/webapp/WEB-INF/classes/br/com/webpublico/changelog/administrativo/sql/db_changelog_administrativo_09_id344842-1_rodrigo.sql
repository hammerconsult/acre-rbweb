UPDATE movimentoaditivocontrato mac
SET mac.OPERACAO = 'REEQUILIBRIO_PRE_EXECUCAO'
where exists(
          select mac.OPERACAO
          from movimentoaditivocontrato mac
                   inner join aditivocontrato ac on mac.ADITIVOCONTRATO_ID = ac.ID
                   inner join contrato c on ac.CONTRATO_ID = c.ID
          where  c.NUMEROCONTRATO = '2143'
);
