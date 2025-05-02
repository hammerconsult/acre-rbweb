update(select cp.cnae_id cnae1, ec.cnae_id cnae2,
              cp.horariofuncionamento_id horario1, ec.horariofuncionamento_id horario2,
              cp.tipocnae tipo1, ec.tipo tipo2
       from cnaeprocessocalculoalvara cp, economicocnae ec
       where cp.economicocnae_id = ec.id)
set cnae1 = cnae2, horario1 = horario2, tipo1 = tipo2;
