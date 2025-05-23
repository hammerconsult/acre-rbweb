update logcdaremessaprotesto set usuario = 'Atualização feita automaticamente'
where usuario is null or trim(usuario) = '' or trim(upper(usuario)) = 'AGENDAMENTO DE TAREFAS';

update cdaremessaprotesto set usuarioultimaatualizacao = (select logcdaremessaprotesto.usuario
                                                          from logcdaremessaprotesto
                                                          where logcdaremessaprotesto.id = (select max(s.id)
                                                                                            from logcdaremessaprotesto s
                                                                                            where s.CDAREMESSAPROTESTO_ID = cdaremessaprotesto.id));
