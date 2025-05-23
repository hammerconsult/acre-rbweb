update PARCELAVALORDIVIDA set VENCIMENTO=to_date('31/12/2020', 'dd/MM/yyyy')
where ID in (select pvd.id from calculoiss iss
              inner join valordivida vd on vd.calculo_id = iss.id
              inner join parcelavalordivida pvd on pvd.valordivida_id = vd.id
              inner join situacaoparcelavalordivida spvd on spvd.id = pvd.situacaoatual_id
              INNER JOIN CADASTROECONOMICO CMC ON CMC.ID = iss.CADASTROECONOMICO_ID
              inner join PERMISSIONARIO on CMC.ID = PERMISSIONARIO.CADASTROECONOMICO_ID
              inner join PERMISSAOTRANSPORTE on PERMISSIONARIO.PERMISSAOTRANSPORTE_ID = PERMISSAOTRANSPORTE.ID
              WHERE vd.EXERCICIO_ID = 709504343
                AND spvd.SITUACAOPARCELA = 'EM_ABERTO'
                and vd.DIVIDA_ID=6194163);
