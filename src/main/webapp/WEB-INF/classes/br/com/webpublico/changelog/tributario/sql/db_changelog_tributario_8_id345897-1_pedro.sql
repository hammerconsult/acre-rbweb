update parcelavalordivida
set vencimento = to_date('30/11/2024', 'dd/MM/YYYY')
where id in (select PVD.ID
             from parcelavalordivida pvd
                      inner join valordivida vd on vd.id = pvd.valordivida_id
                      inner join exercicio ex on vd.EXERCICIO_ID = ex.ID
                      inner join calculo on calculo.id = vd.calculo_id
                      inner join situacaoparcelavalordivida spvd on spvd.id = pvd.situacaoatual_id
             where calculo.cadastro_id in (select cmc.ID
                                           from permissionario perm
                                                    inner join CADASTROECONOMICO cmc on perm.CADASTROECONOMICO_ID = cmc.ID
                                                    inner join PERMISSAOTRANSPORTE permtrans
                                                               on perm.PERMISSAOTRANSPORTE_ID = permtrans.ID
                                           where (perm.FINALVIGENCIA is null or
                                                  trunc(perm.FINALVIGENCIA) > trunc(current_date))
                                             AND permtrans.TIPOPERMISSAORBTRANS = 'MOTO_TAXI')
               and spvd.SITUACAOPARCELA = 'EM_ABERTO'
               and calculo.TIPOCALCULO = 'RB_TRANS'
               and ex.ANO = 2024)
